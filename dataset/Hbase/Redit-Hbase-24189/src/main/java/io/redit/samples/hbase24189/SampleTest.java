package io.redit.samples.hbase24189;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.HbaseHelper;
import io.redit.helpers.HdfsHelper;
import io.redit.helpers.ZookeeperHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.master.RegionState;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.junit.AfterClass;
import org.junit.Assert;
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
    private static final TableName TABLE_NAME = TableName.valueOf("test");
    private static final byte[] CF_1 = Bytes.toBytes("cf1");
    private static final byte[] CF_1_Q_1 = Bytes.toBytes("name");
    private static final byte[] CF_1_Q_2 = Bytes.toBytes("age");
    private static String serverToKill; // rs that holds the table 'test'


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
        hbaseHelper.startHbaseMaster(1);
        hbaseHelper.startHbaseRegionServer(2);
        hbaseHelper.startHbaseRegionServer(3);
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
    public void testWALSplitWithDeletedTableData() throws RuntimeEngineException {

        runner.runtime().enforceOrder("E1", () -> {
            try {

                getConnection();

                Thread.sleep(2000);

                Table table = createTable();

                Put put1 = new Put(Bytes.toBytes("row1"));
                Put put2 = new Put(Bytes.toBytes("row2"));

                put1.addColumn(CF_1, CF_1_Q_1, Bytes.toBytes("Alice"));
                put1.addColumn(CF_1, CF_1_Q_2, Bytes.toBytes(20));
                put2.addColumn(CF_1, CF_1_Q_1, Bytes.toBytes("Bob"));
                put2.addColumn(CF_1, CF_1_Q_2, Bytes.toBytes(24));

                table.put(put1);
                table.put(put2);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        runner.runtime().enforceOrder("E2", () -> {
            try {
                Thread.sleep(1000);
                serverToKill = connection.getRegionLocator(TABLE_NAME).getAllRegionLocations().get(0).getServerName().getHostname();
                System.out.println("serverToKill: " + serverToKill);

                // delete table
                admin.disableTable(TABLE_NAME);
                admin.deleteTable(TABLE_NAME);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        runner.runtime().enforceOrder("X1", () -> {
            try {
                runner.runtime().killNode(serverToKill);
                waitUntilRsDead();
                waitUntilRITDone();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E3", () -> {
            try {
                DistributedFileSystem dfs = hdfsHelper.getDFS(runner);
                //创建要查找的目录路径对象
                Path path = new Path("/hbase/data/default");
                //创建文件状态数组，存储目录下的文件状态
                FileStatus[] fileStatuses = dfs.listStatus(path);
                if (fileStatuses == null || fileStatuses.length == 0) {
                    logger.info("No files in directory /hbase/data/default !");
                } else {
                    for (FileStatus fileStatus : fileStatuses) {
                        logger.info("File under /hbase/data/default:  File name: " +  fileStatus.getPath().getName() + ", File size: " + fileStatus.getLen());
                        // assert 'test' not exists
                        Assert.assertNotEquals("test", fileStatus.getPath().getName());
                    }
                }
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
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
        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(TABLE_NAME).setColumnFamily(ColumnFamilyDescriptorBuilder
                .newBuilder(CF_1).build()).build();
        admin.createTable(tableDescriptor);
        return connection.getTable(TABLE_NAME);
    }

    private static void waitUntilRsDead() throws IOException, InterruptedException {
        while (true) {
            // 获取集群状态对象
            ClusterStatus clusterStatus = admin.getClusterStatus();
            // 获取宕机的region server的名称集合
            List<ServerName> deadServerNames = clusterStatus.getDeadServerNames();
            // 判断是否有宕机的region server
            if (!deadServerNames.isEmpty()) {
                logger.info("Dead region server(s)：");
                // 遍历并打印宕机的region server的名称
                for (ServerName name : deadServerNames) {
                    logger.info(String.valueOf(name));
                }
                break;
            }
            Thread.sleep(1000);
        }
    }

    private static void waitUntilRITDone() throws IOException, InterruptedException {
        for (int i = 0; i < 300; i++) {
            Thread.sleep(200);
            // Create HBaseAdmin instance
            ClusterStatus clusterStatus = admin.getClusterStatus();

            // Get RegionStates
            List<RegionState> regionStates = clusterStatus.getRegionStatesInTransition();

            // Check if there are regions in transition
            if (!regionStates.isEmpty()) {
                logger.info("Regions in transition：");
                // Iterate over the regions in transition
                for (RegionState regionState : regionStates) {
                    logger.info(String.valueOf(regionState));
                }
            } else {
                logger.info("No regions in transition");
            }
        }

    }
}