package io.redit.samples.hdfs10239;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.CommandResults;
import io.redit.helpers.HdfsHelper;
import org.apache.hadoop.fs.FSDataOutputStream;
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

        helper.waitActiveForNNs();
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
    public void testMoveWithTargetPortEmpty() throws RuntimeEngineException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                Path path = new Path("/test.txt"); //指定文件的路径，这里是/user/hadoop/test.txt，其中/user/hadoop是HDFS的用户目录
                FSDataOutputStream out = dfs.create(path); //调用FileSystem对象的create方法，创建一个文件，并返回一个FSDataOutputStream对象，用于写入数据
                out.writeUTF("Hello, HDFS!"); //调用FSDataOutputStream对象的writeUTF方法，写入一些文本数据，这里是"Hello, HDFS!"
                out.close(); //关闭FSDataOutputStream对象，释放资源
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        runner.runtime().enforceOrder("E2", ()->{
            String cmd = "cd " + ReditHelper.getHadoopHomeDir() + " &&  ./bin/hadoop fs -mv hdfs://10.2.0.2:8020/test.txt hdfs://10.2.0.2/test1.txt";
            CommandResults res = runner.runtime().runCommandInNode("nn1", cmd);
            logger.info("Cmd :"+ cmd);
            logger.info("StdOut :"+ res.stdOut());
            logger.info("StdErr :"+ res.stdErr());
        });


        logger.info("completed !!!");
    }
}