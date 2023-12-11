package io.redit.samples.rocketmq3175;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.CommandResults;
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
        // TODO change dledger conf files dynamically
        helper.givePermission();

        helper.startServer(1);
        Thread.sleep(1000);
        helper.startDledgerBroker(1, "n0");
        helper.startDledgerBroker(2, "n1");
        helper.startDledgerBroker(3, "n2");
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
    public void testBrokerRestartAfterUpdateAclConfig() throws RuntimeEngineException, TimeoutException {

        runner.runtime().enforceOrder("E1", () -> {
            // update acl config
            try {
                String updateAclCmd = ReditHelper.getRocketmqHomeDir() +"/bin/mqadmin  updateAclConfig -c RaftCluster -a aaaaaaaaaak -n 127.0.0.1:9876 & ";
                new Thread(() -> {
                    try {
                        runCmdAndPrintRes("server1", updateAclCmd);
                    } catch (RuntimeEngineException e) {
                        e.printStackTrace();
                    }
                }).start();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        runner.runtime().enforceOrder("E2", () -> {
            // restart broker
            try {
                String cdCmd = "cd " + ReditHelper.getRocketmqHomeDir() + " && ";
                String stopBrokerCmd = cdCmd + "./bin/mqshutdown broker";
                runCmdAndPrintRes("server1", stopBrokerCmd);
                Thread.sleep(1000);
                String startBrokerCmd = cdCmd + "./bin/mqbroker -c conf/dledger/broker-n0.conf && echo done";
                runCmdAndPrintRes("server1", startBrokerCmd);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("completed !!!");
    }

    private void runCmdAndPrintRes(String nodeName, String cmd) throws RuntimeEngineException {
        CommandResults commandResults = runner.runtime().runCommandInNode(nodeName, cmd);
        logger.info("orignal command = " + cmd);
        logger.info("commandResults.stdout() = " + commandResults.stdOut());
        logger.info("commandResults.stderr() = " + commandResults.stdErr());
    }

}
