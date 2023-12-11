package io.redit.samples.hbaseexample;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.HbaseHelper;
import io.redit.helpers.HdfsHelper;
import io.redit.helpers.ZookeeperHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static HdfsHelper hdfsHelper;
    private static ZookeeperHelper zookeeperHelper;
    private static HbaseHelper hbaseHelper;
    private static Connection connection;
    private static Admin admin;
    private static final TableName tableName = TableName.valueOf("test");
    private static byte[] emptySnapshot;


    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startHdfsNodes(runner);
        hdfsHelper = new HdfsHelper(runner, ReditHelper.getHadoopHomeDir(), logger, ReditHelper.numOfNNs);

        hdfsHelper.waitActive();
        logger.info("The Hdfs cluster is UP!");
        hdfsHelper.transitionToActive(1, runner);
        hdfsHelper.checkNNs(runner);

        ReditHelper.startServerNodes(runner);
        zookeeperHelper = new ZookeeperHelper(runner, ReditHelper.getZookeeperHomeDir(), logger, ReditHelper.getZookeeperFileRW(), ReditHelper.numOfServers);
        hbaseHelper = new HbaseHelper(runner, ReditHelper.getHbaseHomeDir(), logger, ReditHelper.getHbaseFileRW(), ReditHelper.numOfServers);
        zookeeperHelper.addConfFile();
        hbaseHelper.addRegionConf();

        zookeeperHelper.startServers();
        Thread.sleep(5000);
        zookeeperHelper.checkServersStatus();

        hbaseHelper.startSsh();
        hbaseHelper.startHbases();
        Thread.sleep(15000);

        hbaseHelper.checkJps();

    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test
    public void testOfflineRegionsShouldBeZeroAfterRestoreSnapshot() throws RuntimeEngineException {

        runner.runtime().enforceOrder("E1", () -> {
            try {
                emptySnapshot = Bytes.toBytes("emptySnap");

                getConnection();
                Table table = createTable();

                // take an empty snapshot
                admin.disableTable(tableName);
                admin.snapshot(emptySnapshot, tableName);

                // Load more data to split regions
                admin.enableTable(tableName);
                for (int i = 0; i < 3000; i++) {
                    Put put = new Put(Bytes.toBytes("row" + i));
                    put.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes("name"), Bytes.toBytes("name-" + i));
                    put.addColumn(Bytes.toBytes("cf2"), Bytes.toBytes("age"), Bytes.toBytes("age-" + i));
                    table.put(put);
                }

                table.close();

                // get table rows
                Table table2 = connection.getTable(tableName);
                for (int i = 0; i < 3000; i++) {
                    Get get = new Get(Bytes.toBytes("row" + i));
                    Result result = table2.get(get);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        runner.runtime().enforceOrder("E2", () -> {
            try {
                // Split regions
                admin.split(tableName, Bytes.toBytes("row1000"));

                // Restore the snapshot
                admin.disableTable(tableName);
                admin.restoreSnapshot(emptySnapshot);
                admin.enableTable(tableName);
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private static void getConnection() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        logger.info("hbase.zookeeper.quorum: " + hbaseHelper.HbaseSiteConf);
        conf.set("hbase.zookeeper.quorum", hbaseHelper.HbaseSiteConf);
        conf.set("hbase.master", "localhost:16000");
        try {
            connection = ConnectionFactory.createConnection(conf);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("connection: " + connection);
        System.out.println("admin: " + admin);
    }

    private static Table createTable() throws IOException {
        HTableDescriptor table = new HTableDescriptor(tableName);
        HColumnDescriptor family1 = new HColumnDescriptor("cf1"); //创建一个名为cf1的列族
        HColumnDescriptor family2 = new HColumnDescriptor("cf2"); //创建一个名为cf2的列族
        table.addFamily(family1);
        table.addFamily(family2);
        admin.createTable(table);

        return connection.getTable(tableName);
    }

}