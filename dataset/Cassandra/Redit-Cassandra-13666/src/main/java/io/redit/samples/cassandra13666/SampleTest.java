package io.redit.samples.cassandra13666;

import com.datastax.driver.core.*;
import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.CassandraHelper;
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
    public void TestIndexOnPartitionKeyWithPartitionWithoutRows() throws RuntimeEngineException, InterruptedException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            createTableAndQuery();
        });

        // runner.runtime().waitForRunSequenceCompletion(20);
        logger.info("completed !!!");
    }


    private static void createTableAndQuery() {
        Cluster cluster = null;
        Session session = null;
        try {
            // 定义一个cluster类
            cluster = Cluster.builder().addContactPoint(runner.runtime().ip("server1")).build();
            // 获取session对象
            session = cluster.connect();

            String createKeySpaceCQL = "CREATE KEYSPACE ks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;";
            String createTableCQL = "CREATE TABLE ks.t (pk1 int, pk2 int, c int, s int static, v int, PRIMARY KEY((pk1, pk2), c));";
            String createIndexCQL = "CREATE INDEX ON ks.t (pk2)";

            String insertCQL1 = "INSERT INTO ks.t (pk1, pk2, c, s, v) VALUES (1, 1, 1, 9, 1);";
            String insertCQL2 = "INSERT INTO ks.t (pk1, pk2, c, s, v) VALUES (1, 1, 2, 9, 2);";
            String insertCQL3 = "INSERT INTO ks.t (pk1, pk2, c, s, v) VALUES (3, 1, 1, 9, 1);";
            String insertCQL4 = "INSERT INTO ks.t (pk1, pk2, c, s, v) VALUES (4, 1, 1, 9, 1);";

            session.execute(createKeySpaceCQL);
            session.execute("use ks;");
            session.execute(createTableCQL);
            session.execute(createIndexCQL);
            session.execute(insertCQL1);
            session.execute(insertCQL2);
            session.execute(insertCQL3);
            session.execute(insertCQL4);

            logger.info("----- first select: -----");
            String selectCQL1 = "SELECT * FROM ks.t WHERE pk2 = 1;";
            ResultSet selectCQL1Res1 = session.execute(selectCQL1);
            for (Row row : selectCQL1Res1) {
                System.out.println(row);
            }

            String delCQL1 = "DELETE FROM ks.t WHERE pk1 = 3 AND pk2 = 1 AND c = 1";
            session.execute(delCQL1);

            logger.info("----- second select after deletion: ------");
            ResultSet selectCQL1Res2 = session.execute(selectCQL1);
            for (Row row : selectCQL1Res2) {
                System.out.println(row);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            session.close();
            cluster.close();
        }
    }

}
