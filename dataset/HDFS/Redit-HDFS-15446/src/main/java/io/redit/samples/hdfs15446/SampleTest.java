package io.redit.samples.hdfs15446;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.HdfsHelper;
import org.apache.hadoop.fs.Path;
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
    public void testSnapshotOpsOnReservedPath() throws RuntimeEngineException {
        runner.runtime().enforceOrder("E1", () -> {
            try {

                final Path path = new Path("/test");
                final Path reservedRawPath = new Path("/.reserved/raw/test");
                // 在/test路径下创建一个文件
                dfs.mkdirs(path);
                dfs.create(new Path(path, "file1"));
                // 允许在/test路径上创建快照
                dfs.allowSnapshot(path);
                // 在/.reserved/raw/test路径上创建一个名为s1的快照
                dfs.createSnapshot(reservedRawPath, "s1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        runner.runtime().enforceOrder("X1", ()->{
            try {
                Thread.sleep(10000);
                runner.runtime().restartNode("nn2", 10);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }
}