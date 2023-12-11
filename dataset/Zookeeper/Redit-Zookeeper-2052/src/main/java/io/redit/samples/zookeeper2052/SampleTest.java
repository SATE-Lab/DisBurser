package io.redit.samples.zookeeper2052;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import org.apache.zookeeper.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.redit.helpers.ZookeeperHelper;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ZookeeperHelper helper;
    private static final String ZNODE_PATH = "/metadata/resources";
    private ZooKeeper zk1;
    private ZooKeeper zk2;
    private List<Op> multiOpList;


    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        helper = new ZookeeperHelper(runner, ReditHelper.getZookeeperHomeDir(), logger, ReditHelper.getFileRW(), ReditHelper.numOfServers);
        helper.addConfFile();
        logger.info("wait for zookeeper...");
        helper.startServers();
        Thread.sleep(5000);
        helper.checkServersStatus();
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test
    public void testMultiRollback() throws RuntimeEngineException, TimeoutException, InterruptedException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                CountDownLatch countDownLatchA = new CountDownLatch(1);
                CountDownLatch countDownLatchB = new CountDownLatch(1);
                multiOpList = Arrays.asList(Op.delete(ZNODE_PATH, -1), Op.delete("/metadata", -1));

                zk1 = new ZooKeeper(runner.runtime().ip("server1"), 4000, watchedEvent -> {countDownLatchA.countDown();});
                zk2 = new ZooKeeper(runner.runtime().ip("server2"), 4000, watchedEvent -> {countDownLatchB.countDown();});
                countDownLatchA.await();
                countDownLatchB.await();
                zk1.create("/metadata", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zk1.create("/metadata/resources", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zk2.create(ZNODE_PATH + "/clild1", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                zk2.create(ZNODE_PATH + "/clild2", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        runner.runtime().enforceOrder("E2", ()->{
            logger.info("try to recursively remove node tree, should fail because of ephemeral nodes");
            try {
                zk1.multi(multiOpList);
            } catch (KeeperException e) {
                logger.info("multi failed as expected");
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E3", ()->{
            try {
                logger.info("Close zk2 to remove ephemeral node");
                zk2.close();
                Thread.sleep(500);
                logger.info("try to recursively remove node tree again, should succeed");
                zk1.multi(multiOpList);
                zk1.close();
            } catch (InterruptedException | KeeperException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("completed !!!");
    }
}
