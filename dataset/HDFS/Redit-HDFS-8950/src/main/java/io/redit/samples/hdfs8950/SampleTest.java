package io.redit.samples.hdfs8950;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.HdfsHelper;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static HdfsHelper helper;
    private static DistributedFileSystem dfs;
    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        helper = new HdfsHelper(runner, ReditHelper.getHadoopHomeDir(), logger, ReditHelper.numOfNNs);

        helper.waitActiveForNNs();
        logger.info("The cluster is UP!");
        helper.transitionToActive(1, runner);
        helper.checkNNs(runner);
        dfs = helper.getDFS(runner);
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test
    public void testRemoveIncludedNode() throws RuntimeEngineException {
        runner.runtime().enforceOrder("E1", () -> {
            addDnToDfsHostsAllow();
        });
        runner.runtime().enforceOrder("E2", () -> {
            try {
                dfs.refreshNodes();
                reportStatus("------------dfs.hosts=dn1, dn2, dn3-----------");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        runner.runtime().enforceOrder("X1", () -> {
            runner.runtime().stopNode("dn3", 3);
        });

        runner.runtime().enforceOrder("E3", ()->{
            removeDnFromHostsAllow();
        });


        runner.runtime().enforceOrder("E4", () -> {
            try {
                dfs.refreshNodes();
                reportStatus("------------dfs.hosts=dn1, dn2-----------");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void addDnToDfsHostsAllow() {
        try {
            runner.runtime().runCommandInNode("nn1", "touch " + ReditHelper.getHadoopHomeDir() + "/etc/hadoop/dfs.hosts");
            runner.runtime().runCommandInNode("nn1", "printf 'dn1\ndn2\ndn3' > " + ReditHelper.getHadoopHomeDir() + "/etc/hadoop/dfs.hosts");
            Thread.sleep(2000);
        } catch (InterruptedException | RuntimeEngineException e) {
            throw new RuntimeException(e);
        }
    }

    private static void removeDnFromHostsAllow(){
        try {
            runner.runtime().runCommandInNode("nn1", "rm -f " + ReditHelper.getHadoopHomeDir() + "/etc/hadoop/dfs.hosts");
            runner.runtime().runCommandInNode("nn1", "touch " + ReditHelper.getHadoopHomeDir() + "/etc/hadoop/dfs.hosts");
            runner.runtime().runCommandInNode("nn1", "printf 'dn1\ndn2' > " + ReditHelper.getHadoopHomeDir() + "/etc/hadoop/dfs.hosts");
            Thread.sleep(2000);
        } catch (InterruptedException | RuntimeEngineException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reportStatus(String msg) throws RuntimeEngineException {
        String writeMsgCmd = "printf '" + msg + "\n' >> " + ReditHelper.getHadoopHomeDir() + "/logs/dnStatus.log";
        runner.runtime().runCommandInNode("nn1", writeMsgCmd);
        String cmd = "cd  " + ReditHelper.getHadoopHomeDir() + " && bash ./bin/hdfs dfsadmin -report >> " + ReditHelper.getHadoopHomeDir() + "/logs/dnStatus.log &";
        runner.runtime().runCommandInNode("nn1", cmd);
    }


}