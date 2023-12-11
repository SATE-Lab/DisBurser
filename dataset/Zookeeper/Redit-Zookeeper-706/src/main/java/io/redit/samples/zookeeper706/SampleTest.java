package io.redit.samples.zookeeper706;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import org.apache.zookeeper.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.redit.helpers.ZookeeperHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ZookeeperHelper helper;
    private static final long TIMEOUT = 500L;
    ZooKeeper zk1;
    ZooKeeper zk2;
    MyWatcher childWatcher;
    List<String> paths = new ArrayList<String>();


    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {

        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        helper = new ZookeeperHelper(runner, ReditHelper.getZookeeperHomeDir(), logger, ReditHelper.getFileRW(), ReditHelper.numOfServers);
        helper.addConfFile();
        logger.info("wait for zookeeper...");
        helper.startServers();
        Thread.sleep(5000);
        helper.checkServersStatus();
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    private class MyWatcher implements Watcher {
        LinkedBlockingQueue<WatchedEvent> events =
                new LinkedBlockingQueue<WatchedEvent>();

        public void process(WatchedEvent event) {
            if (event.getType() != Event.EventType.None) {
                try {
                    events.put(event);
                } catch (InterruptedException e) {
                    logger.warn("ignoring interrupt during event.put");
                }
            }
        }
    }


    @Test
    public void testManyChildWatchers() throws RuntimeEngineException, TimeoutException, InterruptedException {
        runner.runtime().enforceOrder("E1", () -> {
            CountDownLatch countDownLatch1 = new CountDownLatch(1);
            CountDownLatch countDownLatch2 = new CountDownLatch(1);

            try {
                zk1 = new ZooKeeper(helper.connectionStr, 10000, (event) -> {
                    if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                        countDownLatch1.countDown();
                    }
                });

                zk2 = new ZooKeeper(helper.connectionStr, 10000, (event) -> {
                    if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                        countDownLatch2.countDown();
                    }
                });
                countDownLatch1.await();
                countDownLatch2.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E2", () -> {
            try {

                // 110 character base path
                String pathBase = "/long-path-000000000-111111111-222222222-333333333-444444444-"
                        + "555555555-666666666-777777777-888888888-999999999";

                zk1.create(pathBase, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                // Create 10,000 nodes. This should ensure the length of our
                // watches set below exceeds 1MB.
                paths = new ArrayList<String>();
                for (int i = 0; i < 10000; i++) {
                    String path = zk1.create(pathBase + "/ch-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT_SEQUENTIAL);
                    paths.add(path);
                }

                childWatcher = new MyWatcher();

                // Set a combination of child/exists/data watches
                int i = 0;
                for (String path : paths) {
                    if (i % 3 == 0) {
                        zk2.getChildren(path, childWatcher);
                    } else if (i % 3 == 1) {
                        zk2.exists(path + "/foo", childWatcher);
                    } else if (i % 3 == 2) {
                        zk2.getData(path, childWatcher, null);
                    }

                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        runner.runtime().enforceOrder("X1", () -> {
            try {
                runner.runtime().killNode("server1");
                runner.runtime().killNode("server2");
                runner.runtime().killNode("server3");
                // wait for disconnected
                Thread.sleep(13000);
                runner.runtime().startNode("server1");
                runner.runtime().startNode("server2");
                runner.runtime().startNode("server3");
                helper.startServers();
                Thread.sleep(5000);
                helper.checkServersStatus();
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E3", () -> {
            try {
                // Trigger the watches and ensure they properly propagate to the client
                int i = 0;
                for (String path : paths) {
                    if (i % 3 == 0) {
                        zk1.create(path + "/ch", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        WatchedEvent e = childWatcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                        Assert.assertNotNull(e);
                        Assert.assertEquals(Watcher.Event.EventType.NodeChildrenChanged, e.getType());
                        Assert.assertEquals(path, e.getPath());
                    } else if (i % 3 == 1) {
                        zk1.create(path + "/foo", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                        WatchedEvent e = childWatcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                        Assert.assertNotNull(e);
                        Assert.assertEquals(Watcher.Event.EventType.NodeCreated, e.getType());
                        Assert.assertEquals(path + "/foo", e.getPath());
                    } else if (i % 3 == 2) {
                        zk1.setData(path, new byte[]{1, 2, 3}, -1);

                        WatchedEvent e = childWatcher.events.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                        Assert.assertNotNull(e);
                        Assert.assertEquals(Watcher.Event.EventType.NodeDataChanged, e.getType());
                        Assert.assertEquals(path, e.getPath());
                    }
                    i++;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        logger.info("completed !!!");
    }
}
