package io.redit.samples.rocketmq3556;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.NetOp;
import io.redit.helpers.RocketmqHelper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static RocketmqHelper helper;
    private DefaultMQPushConsumer consumer;
    private DefaultMQProducer producer;

    private static final String TOPIC_NAME = "testTopic";

    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        helper = new RocketmqHelper(runner, ReditHelper.getRocketmqHomeDir(), logger, ReditHelper.getFileRW(), ReditHelper.numOfServers);
        helper.addRocketPropFile();
        helper.givePermission();

        helper.startServer(1);
        Thread.sleep(3000);
        helper.startBroker(2, "a");
        Thread.sleep(3000);
        helper.checkStatus(1);
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test
    public void testRetryAsyncMessageWhenBrokerDown() throws RuntimeEngineException, TimeoutException {

        runner.runtime().enforceOrder("E1", () -> {
            try {
                // simulate network delay in real-world so that messages won't go too fast
                runner.runtime().networkOperation("server1", NetOp.delay(new Random().nextInt(101) + 50));
                runner.runtime().networkOperation("server2", NetOp.delay(new Random().nextInt(101) + 50));

                // start a producer and send some messages
                producer = new DefaultMQProducer("producer_group");
                producer.setNamesrvAddr(helper.namesrvAddr);
                producer.setRetryTimesWhenSendFailed(3); // 设置重试次数为3次

                // 启动Producer实例
                producer.start();

                for (int i = 0; i < 10; i++) {
                    // 创建消息，并指定Topic，Tag和消息体
                    Message msg = new Message(TOPIC_NAME, "TagA", ("Hello RocketMQ " + i).getBytes("UTF-8"));
                    // 发送消息到一个Broker
                    logger.info("sending message: " + msg.toString());

                    if (i == 8) {
                        logger.info("stop server2");
                        runner.runtime().stopNode("server2", 3);
                        Thread.sleep(2000);
                    }

                    producer.send(msg, new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            System.out.println("Send success: " + sendResult);
                        }

                        @Override
                        public void onException(Throwable e) {
                            System.err.println("Send message FAILED!, msg " + msg.toString() + " failed with following exception:");
                            System.err.println(e.toString());
                        }
                    });
                }
                runner.runtime().networkOperation("server1", NetOp.removeDelay());

                // 如果不再发送消息，关闭Producer实例。
                producer.shutdown();
            } catch (MQClientException | RemotingException | InterruptedException |
                     UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });



        logger.info("completed !!!");
    }
}
