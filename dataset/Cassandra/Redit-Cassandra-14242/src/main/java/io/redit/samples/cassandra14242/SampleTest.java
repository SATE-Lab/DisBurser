package io.redit.samples.cassandra14242;

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
    public void testMakeUUIDFromSASIIndex() throws RuntimeEngineException, InterruptedException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            logger.info("---------create ks and table---------");
            createTable();
        });
        logger.info("completed !!!");
    }


    private static void createTable() {
        Cluster cluster = null;
        Session session = null;
        try {
            // 定义一个cluster类
            cluster = Cluster.builder().addContactPoint(runner.runtime().ip("server1")).build();
            // 获取session对象
            session = cluster.connect();

            String createKeySpaceCQL = "CREATE KEYSPACE ks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};";
            String createTableCQL = "CREATE TABLE t (k int, c int, s int static, PRIMARY KEY (k, c));";
            String createIndexCQL = "CREATE INDEX ON ks.t (s)";

            session.execute(createKeySpaceCQL);
            session.execute("use ks");
            session.execute(createTableCQL);
            session.execute(createIndexCQL);

            int numRows = 100;

            for (int id = 0; id < numRows; id++){
                logger.info("-------insert row " + id + "--------");
                String insertCQL = String.format("INSERT INTO ks.t (k, c, s) VALUES (%d, 1, 2);", id);
                session.execute(insertCQL);
                if(id % 10 == 0){
                    Thread.sleep(200);
                }
            }

            queryByPageSize(10, session);
            Thread.sleep(1000);
            queryByPageSize(100, session);
            Thread.sleep(1000);
            queryByPageSize(1000, session);
            Thread.sleep(1000);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            session.close();
            cluster.close();
        }
    }

    private static void queryByPageSize(int pageSize, Session session){
        logger.info("-------query by page size " + pageSize + "--------");

        int count = 0;
        String queryCQL = "SELECT * FROM ks.t WHERE s = 2;";

        SimpleStatement statement = new SimpleStatement(queryCQL);
        statement.setFetchSize(pageSize);

        ResultSet rs = session.execute(statement);

        while (!rs.isExhausted()) {
            for (Row row : rs) {
                count++;
            }
            if (!rs.isFullyFetched()) {
                rs.fetchMoreResults();
            }
        }

        logger.info(String.format("PageSize: %d, count: %d", pageSize, count));
    }

}
