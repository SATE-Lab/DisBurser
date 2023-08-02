/*
 * MIT License
 *
 * Copyright (c) 2021 SATE-Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.redit.rt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

// TODO should some methods be synchronized ?

/**
 * This class acts as a client for the event server and contains the necessary methods for run sequence related instrumentation
 * 该类包含了运行时方法
 */
public class Redit {
    private static Redit instance;

    private final String hostname;
    private final String port;
    private final StackMatcher stackMatcher;
    // this is needed because each pass of a method can only be blocked once per thread
    private ThreadLocal<Boolean> allowBlocking;

    /**
     * This method returns an instance of Redit class initialized with ip and port from the env
     * 获取Redit带ip和端口的实例，ip和端口有什么用还有待查看
     *
     */
    public static Redit getInstance() {
        if (instance == null) {
            // the event server ip an port should come from the env vars if not given as args
            instance = new Redit(System.getenv("REDIT_EVENT_SERVER_IP_ADDRESS"),
                    System.getenv("REDIT_EVENT_SERVER_PORT_NUMBER"));
        }

        return instance;
    }

    /**
     * Private Constructor
     * update: Private? Public!
     * 可以通过该方法直接构造一个redit实例，但我们不会这么用
     * @param hostname the hostname or ip address of the event server
     * @param port the port number for the event server
     */
    public Redit(String hostname, String port) {
        this.hostname = hostname;
        this.port = port;
        this.stackMatcher = new StackMatcher();
        this.allowBlocking = ThreadLocal.withInitial(() -> true);
    }

    /**
     * Sets allow blocking to true and should be called in the beginning of each instrumented method
     * 设置锁，锁会在事件调度前被执行
     */
    public void allowBlocking() {
        allowBlocking.set(true);
    }

    /**
     * This method enforces the order for an internal event. It first checks if the event is already satisfied or not.
     * then, if the stack matches, the current thread is allowed to be blocked and the blocking condition is satisfied,
     * it blocks the thread until the event dependencies are satisfied. Then, it will mark the event as satisfied in the
     * event server and disallows blocking for the current thread
     * 该方法强制一个内部事件的执行流程，首先检查事件是否满足，如果栈符合我们的要求，且当前线程被阻塞，那么在前置条件被满足前会继续阻塞
     * 如果满足了条件，那么会给事件服务器发送信号，解除线程的阻塞
     * @param eventName that needs to be enforced
     * @param stack the stack trace to match in order to allow blocking
     */
    public void enforceOrder(String eventName, String stack) {
        // check if event is not already sent - useful when resetting a node
        if (!isEventAlreadySent(eventName)) {
            if (stack == null || stackMatcher.match(stack)) {
                // check if blocking is allowed in the current pass
                if (allowBlocking.get()) {
                    // check if blocking condition is satisfied
                    if (isBlockingConditionSatisfied(eventName)) {
                        blockAndPoll(eventName);
                        sendEvent(eventName);//todo check if the sendEvent works well，会不会是因为blockandpoll卡死了循环
                        allowBlocking.set(false);
                    }
                }
            }
        }
    }

    /**
     * This method enforces a garbage collection event. It should be called in the beginning of the main method. Then,
     * it will create a thread that checks that event is not sent yet, then, is the blocking condition is satisfied, it
     * will block the created thread until the event dependencies are satisfied. After getting unblocked, it will run the
     * gc and mark the event as satisfied in the event server.
     * 和上面的方法一样，只是不发送事件，直接调用垃圾回收，但貌似对象并不会被直接回收，所以没有被使用
     * @param eventName that needs to be enforced
     */
    public void garbageCollection(String eventName) {
        Thread gcThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // check if event is not already sent - useful when resetting a node
                if (!isEventAlreadySent(eventName)) {
                    // check if blocking condition is satisfied
                    if (isBlockingConditionSatisfied(eventName)) {
                        blockAndPoll(eventName);
                        System.gc();
                        sendEvent(eventName);
                    }
                }
            }
        });

        gcThread.start();
    }

    /**
     * Sends a message to event server to check if the event has been marked as satisfied or not.
     * 通过和事件服务器的连接，判断事件是否被发送成功
     * @param eventName that needs to be checked
     * @return true if the event is marked as satisfied, otherwise false
     */
    private boolean isEventAlreadySent(String eventName) {
        try {
            URL url = new URL("http://" + hostname + ":" + port + "/events/" + eventName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                return true;
            }
            // System.out.println("response code:"+connection.getResponseCode());
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends a message to event server to check if the blocking condition for the given event is satisfied or not.
     * 通过和事件服务器的连接，判断阻塞条件是否满足
     * @param eventName that needs to be checked
     * @return true if the blocking condition is marked as satisfied, otherwise false
     */
    private boolean isBlockingConditionSatisfied(String eventName) {
        try {
            URL url = new URL("http://" + hostname + ":" + port + "/blockDependencies/" + eventName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                return true;
            }
            System.out.println("response code:"+connection.getResponseCode());
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method, in an infinite loop, sends a message to event server and checks that the dependency of the given event
     * are satisfied. When the dependencies finally get satisfied, the method will return
     * 循环查看某个事件条件是否被满足
     * @param eventName that needs to be checked
     */
    public void blockAndPoll(String eventName) {
        try {
            blockAndPoll(eventName, false, null);
        } catch (TimeoutException e) {
            // This never happens
        }
    }

    /**
     * This method, in an infinite loop, sends a message to event server and checks that the dependency of the given event
     * (and the event itself if include event flag is true) are satisfied. When the dependencies finally get satisfied, the method will return
     * 允许提供一个外部的flag作为条件的检查
     * @param eventName that needs to be checked
     * @param includeEvent the flag to check if the event itself is satisfied or not
     */
    public void blockAndPoll(String eventName, Boolean includeEvent) {
        try {
            blockAndPoll(eventName, includeEvent, null);
        } catch (TimeoutException e) {
            // this never happens
        }
    }

    /**
     * This method, until gets timeout, sends a message to event server and checks that the dependency of the given event
     * are satisfied. When the dependencies finally get satisfied, the method will return
     * 超时前都会检查是否满足条件
     * @param eventName that needs to be checked
     * @param includeEvent the flag to check if the event itself is satisfied or not
     * @param timeout amount in seconds
     */
    public void blockAndPoll(String eventName, Boolean includeEvent, Integer timeout) throws TimeoutException {

        if (timeout != null) {
            timeout = timeout * 1000;
        }

        while (timeout == null || timeout > 0) {
            try {
                Integer eventInclusion = includeEvent? 1:0;
                URL url = new URL("http://" + hostname + ":" + port + "/dependencies/" + eventName
                        + "?includeEvent=" + eventInclusion);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    break;
                }
                Thread.sleep(10);

                if (timeout != null) {
                    timeout -= 10;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (timeout != null && timeout <= 0) {
            throw new TimeoutException("The timeout for event " + eventName + " is passed");
        }
    }

    /**
     * Sends a message to event server and marks the event as satisfied.
     * @param eventName the event to be marked as satisfied
     */
    public void sendEvent(String eventName) {
        try {
            URL url = new URL("http://" + hostname + ":" + port + "/events");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String input = "{\"name\":\"" + eventName + "\"}";

            OutputStream os = connection.getOutputStream();
            os.write(input.getBytes());

            connection.getResponseCode();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
