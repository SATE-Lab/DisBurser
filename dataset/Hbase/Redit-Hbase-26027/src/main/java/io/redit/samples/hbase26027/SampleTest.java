package io.redit.samples.hbase26027;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.NetOp;
import io.redit.helpers.HbaseHelper;
import io.redit.helpers.HdfsHelper;
import io.redit.helpers.ZookeeperHelper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
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
    private static final byte[] cf = Bytes.toBytes("cf1");
    private static final byte[] qualifier = Bytes.toBytes("c1");;
    private static Table table;


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

    @Test(timeout = 90000)
    public void testMultiPutsTimeout() throws RuntimeEngineException {

        runner.runtime().enforceOrder("E1", () -> {
            try {
                getConnection();
                Thread.sleep(2000);
                table = createTable();
                for(int i = 0; i < 10; i++){
                    byte[] rk = Bytes.toBytes("rk-" + i);
                    Put put = new Put(rk);
                    put.addColumn(cf, qualifier, Bytes.toBytes("value-" + i));
                    table.put(put);
                }

                connection.close();
                table.close();

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });


        runner.runtime().enforceOrder("X1", ()->{
            runner.runtime().networkOperation("server2", NetOp.delay(2000));
            runner.runtime().networkOperation("server3", NetOp.delay(2000));
        });

        runner.runtime().enforceOrder("E2", ()->{
            try {
                Thread.sleep(2000);
                getConnection();
                table = connection.getTable(tableName);

                List<Get> gets = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    byte[] rk = Bytes.toBytes("rk-" + i);
                    Get get = new Get(rk);
                    get.addColumn(cf, qualifier);
                    gets.add(get);
                }
                Result[] results = new Result[gets.size()];
                table.batch(gets, results);
            } catch (InterruptedException | IOException e) {
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
        conf.set("hbase.rpc.timeout","2000");
        conf.set("hbase.client.operation.timeout","6000");
        try {
            connection = ConnectionFactory.createConnection(conf);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Table createTable() throws IOException {
        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder
                .newBuilder(cf).build()).build();
        admin.createTable(tableDescriptor);
        return connection.getTable(tableName);
    }
}