package io.redit.samples.cassandra10968;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.CommandResults;
import io.redit.helpers.CassandraHelper;
import io.redit.helpers.Utils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    private static ReditRunner runner;
    private static CassandraHelper helper;

    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        helper = new CassandraHelper(runner, ReditHelper.getCassandraHomeDir(), logger, ReditHelper.getFileRW(), ReditHelper.numOfServers);
        String seedsIp = runner.runtime().ip("server1");
        helper.addCassandraYamlFile(seedsIp);
        helper.makeCassandraDirs();

        logger.info("wait for Cassandra ...");
        helper.startServer(1);
        Thread.sleep(5000);
        helper.startServer(2);
        Thread.sleep(10000);
        helper.checkNetStatus(2);
        helper.checkStatus(1);
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test
    public void TestSnapshotWithoutFlushWithSecondaryIndexes() throws RuntimeEngineException, InterruptedException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            createTableAndInsert();
        });

        runner.runtime().enforceOrder("E2", () -> {
            createSnapshot();
        });

        logger.info("completed !!!");
    }

    private static void createTableAndInsert() {
        Cluster cluster = null;
        Session session = null;
        try {
            // 定义一个cluster类
            cluster = Cluster.builder().addContactPoint(runner.runtime().ip("server1")).build();
            // 获取session对象
            session = cluster.connect();
            // 创建键空间
            String createKeySpaceCQL = "CREATE KEYSPACE X\n" +
                    "  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };";
            session.execute(createKeySpaceCQL);
            session.execute("USE X");
            // 创建列族
            String createTableCQL = "CREATE TABLE table1 (\n" +
                    "  col1 varchar,\n" +
                    "  col2 varchar,\n" +
                    "  PRIMARY KEY (col1, col2)\n" +
                    ");";
            session.execute(createTableCQL);
            // 创建索引
            String createIndexCQL = "CREATE INDEX col2_idx ON X.table1 (col2);";
            session.execute(createIndexCQL);

            String insertCQL1 = "INSERT INTO X.table1 (col1, col2) VALUES ('a1', 'a2');";
            String insertCQL2 = "INSERT INTO X.table1 (col1, col2) VALUES ('b1', 'b2');";
            session.execute(insertCQL1);
            session.execute(insertCQL2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            session.close();
            cluster.close();
        }
    }

    private static void createSnapshot() throws RuntimeEngineException {
        String createSnapshotCmd = ReditHelper.getCassandraHomeDir() + "/bin/nodetool snapshot x";
        CommandResults commandResults = runner.runtime().runCommandInNode("server1", createSnapshotCmd);
        Utils.printResult(commandResults, logger);

        String catCmd = "cat /opt/cassandra/data_file_directories/x/*/snapshots/*/manifest.json";
        commandResults = runner.runtime().runCommandInNode("server1", catCmd);
        String catRes = getCommandResult(commandResults);
        logger.info("manifest.json content: ");
        logger.info(catRes);
    }

    private static String getCommandResult(CommandResults commandResults) {
        if (commandResults.stdOut() != null && commandResults.stdOut().length() != 0) {
            return commandResults.stdOut();
        } else {
            return commandResults.stdErr();
        }
    }
}
