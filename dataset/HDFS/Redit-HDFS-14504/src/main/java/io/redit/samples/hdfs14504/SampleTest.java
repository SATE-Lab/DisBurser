package io.redit.samples.hdfs14504;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.HdfsHelper;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static HdfsHelper helper;
    private static DistributedFileSystem dfs;
    private static List<String> cmds;


    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        helper = new HdfsHelper(runner, ReditHelper.getHadoopHomeDir(), logger, ReditHelper.numOfNNs);

        helper.waitActive();
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
    public void testQuotaAndRenameWithSnapshot() throws RuntimeEngineException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                cmds = readLinesFromFile("cmds.txt");
                String prefix = "cd " + ReditHelper.getHadoopHomeDir() + " && ./bin/";
                String postfix = " >> " + ReditHelper.getHadoopHomeDir() + "/logs/console.log &";
                for (String cmd : cmds) {
                    String finCmd = prefix + cmd + postfix;
                    String echoCmd = "printf '" + finCmd + "\n' >> " + ReditHelper.getHadoopHomeDir() + "/logs/console.log";
                    runner.runtime().runCommandInNode("nn1", echoCmd);
                    runner.runtime().runCommandInNode("nn1", prefix + cmd + postfix);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static List<String> readLinesFromFile(String fileName) {
        List<String> lines = new ArrayList<>();

        java.nio.file.Path path = Paths.get("src/main/resources", fileName);

        // 读取文件内容
        try (BufferedReader reader = Files.newBufferedReader((java.nio.file.Path) path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lines;
    }
}