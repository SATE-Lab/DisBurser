package io.redit.samples.rocketmq231;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.RocketmqHelper;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.PullStatus;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static RocketmqHelper helper;
    private DefaultMQPullConsumer consumer;
    private DefaultMQProducer producer;

    private static final int EXPECTED_LEN = 3;
    private static final String TOPIC_NAME = "testTopic";

    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        helper = new RocketmqHelper(runner, ReditHelper.getRocketmqHomeDir(), logger, ReditHelper.getFileRW(), ReditHelper.numOfServers);
        helper.addRocketPropFile();
        helper.givePermission();

        helper.startServers();
        Thread.sleep(3000);
        helper.startBroker(1, "a");
        Thread.sleep(500);
        helper.startBroker(2, "a-s");
        Thread.sleep(500);
        helper.startBroker(2, "b");
        Thread.sleep(500);
        helper.startBroker(1, "b-s");
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
    public void testPullSize() throws RuntimeEngineException, TimeoutException {

        runner.runtime().enforceOrder("E1", () -> {
            try {
                // start a producer and send some messages
                producer = new DefaultMQProducer("producer_group");
                producer.setNamesrvAddr(helper.namesrvAddr);
                // 启动Producer实例
                producer.start();

                for (int i = 0; i < 40; i++) {
                    // 创建消息，并指定Topic，Tag和消息体
                    Message msg = new Message(TOPIC_NAME, "TagA", ("Hello RocketMQ " + i).getBytes("UTF-8"));
                    // 发送消息到一个Broker
                    producer.send(msg);

                }
                // 如果不再发送消息，关闭Producer实例。
                producer.shutdown();
            } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException |
                     UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });


        runner.runtime().enforceOrder("E2", () -> {
            consumer = new DefaultMQPullConsumer("consumer_group");
            consumer.setNamesrvAddr(helper.namesrvAddr);
            try {
                consumer.start();
                MessageQueue mq = new MessageQueue();

                mq.setQueueId(0);
                mq.setTopic(TOPIC_NAME);
                mq.setBrokerName("broker-a");
                PullResult pullResult = consumer.pull(mq, "*", 0, EXPECTED_LEN);
                if (pullResult.getPullStatus().equals(PullStatus.FOUND)) {
                    logger.info("expected result length: " + EXPECTED_LEN);
                    logger.info("pull result length: " + pullResult.getMsgFoundList().size());
                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
                }
                consumer.shutdown();
            } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("completed !!!");
    }
}
