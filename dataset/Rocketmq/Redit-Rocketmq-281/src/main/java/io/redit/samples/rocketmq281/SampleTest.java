package io.redit.samples.rocketmq281;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.RocketmqHelper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
        helper.startBroker(1, "a");
        Thread.sleep(500);
        helper.startBroker(2, "a-s");
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
    public void testRepeatStartOfBroker() throws RuntimeEngineException, TimeoutException {

        runner.runtime().enforceOrder("E1", () -> {
            try {
                helper.startBroker(1, "a");
                Thread.sleep(3000);
                helper.checkStatus(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("completed !!!");
    }
}
