package io.redit.samples.rocketmq3281;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.CommandResults;
import io.redit.helpers.RocketmqHelper;
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

    String mqAdminPath;

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
    public void testDeleteGroupPerm() throws RuntimeEngineException, TimeoutException {

        runner.runtime().enforceOrder("E1", () -> {
            // show acl conf file and update groupa's perm
            mqAdminPath = ReditHelper.getRocketmqHomeDir() + "/bin/mqadmin ";
            String showAclCommand = mqAdminPath + "getAccessConfigSubCommand -n localhost:9876 -c RaftCluster";
            runCmdAndPrintRes("server1", showAclCommand);

            String updateAclCommand = mqAdminPath + " updateAclConfig -n localhost:9876 -a RocketMQ -s 12345678 -c RaftCluster -g groupa=SUB";
            runCmdAndPrintRes("server1", updateAclCommand);
        });


        runner.runtime().enforceOrder("E2", () -> {
            try {
                Thread.sleep(1000);
                String updateAclCommand = mqAdminPath + " updateAclConfig -n localhost:9876 -a RocketMQ -s 12345678 -c RaftCluster -g \"\" && echo done";
                runCmdAndPrintRes("server1", updateAclCommand);
                Thread.sleep(3000);
                String showAclCommand = mqAdminPath + "getAccessConfigSubCommand -n localhost:9876 -c RaftCluster";
                runCmdAndPrintRes("server1", showAclCommand);

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
