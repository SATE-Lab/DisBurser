package io.redit.samples.zookeeper1366;

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
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertNull;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ZookeeperHelper helper;
    private static ZooKeeper[] zooKeeper = {null, null, null};
    private static final String ZNODE_PATH = "/example";


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
    public void testZkClockDrift() throws RuntimeEngineException {
        runner.runtime().enforceOrder("E1", () -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            try {
                zooKeeper[0] = new ZooKeeper(runner.runtime().ip("server1") + ":2181", 10000, watchedEvent -> countDownLatch.countDown());
                countDownLatch.await();
                zooKeeper[0].create(ZNODE_PATH, "Hello, ZooKeeper!".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                Thread.sleep(1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("X1", () -> {
            runner.runtime().clockDrift("server1", 50000);
            runner.runtime().clockDrift("server2", 50000);
            runner.runtime().clockDrift("server3", 50000);
        });

        runner.runtime().enforceOrder("E2", () ->{
            try {
                Thread.sleep(3000);
                zooKeeper[0].create(ZNODE_PATH + "/test", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (InterruptedException | KeeperException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("completed !!!");
    }
}
