package io.redit.samples.hdfs14869;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.HdfsHelper;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.tools.DistCp;
import org.apache.hadoop.tools.DistCpOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static HdfsHelper helper;
    private static DistributedFileSystem dfs;
    java.nio.file.Path filterFile = null;

    final Path sourcePath = new Path("/source");
    final Path target = new Path("/target");

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
    public void testDistcpWithFilterAndSnapshotDiff() throws RuntimeEngineException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                // init data
                initData(sourcePath);
                dfs.allowSnapshot(sourcePath);
                dfs.createSnapshot(sourcePath, "s1");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E2", ()->{
            try {
                // distcp /source to /target with filter
                filterFile = generateFilterFile("filters");
                final DistCpOptions.Builder builder = new DistCpOptions.Builder(
                        new ArrayList<>(Arrays.asList(sourcePath)),
                        target)
                        .withFiltersFile(filterFile.toString())
                        .withSyncFolder(true);
                new DistCp(dfs.getConf(), builder.build()).execute();
                Thread.sleep(1000);
                dfs.allowSnapshot(target);
                dfs.createSnapshot(target, "s1");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E3", ()->{
            // rename .staging dir and distcp again using snapshot diff
            try {
                changeData(sourcePath); // change .staging to prod, which is not included in the filter
                Thread.sleep(1000);
                dfs.createSnapshot(sourcePath, "s2");
                final DistCpOptions.Builder diffBuilder = new DistCpOptions.Builder(
                        new ArrayList<>(Arrays.asList(sourcePath)),
                        target)
                        .withUseDiff("s1", "s2")
                        .withFiltersFile(filterFile.toString())
                        .withSyncFolder(true);
                new DistCp(dfs.getConf(), diffBuilder.build()).execute();
                Thread.sleep(2000);
                // list files in /target
                logger.info("Files in /target:");
                FileStatus[] fileStatuses = dfs.listStatus(target);
                for (FileStatus fileStatus : fileStatuses) {
                    logger.info(fileStatus.getPath().toString());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

    }

    private void initData(Path dir) throws IOException {
        final Path staging = new Path(dir, ".staging");
        final Path stagingF1 = new Path(staging, "f1");
        final Path data = new Path(dir, "data");
        final Path dataF1 = new Path(data, "f1");

        dfs.create(stagingF1).close();
        dfs.create(dataF1).close();
    }

    private void changeData(Path dir) throws IOException {
        final Path staging = new Path(dir, ".staging");
        final Path prod = new Path(dir, "prod");
        dfs.rename(staging, prod);
    }

    private java.nio.file.Path generateFilterFile(String fileName)
            throws IOException {
        java.nio.file.Path tmpFile = Files.createTempFile(fileName, "txt");
        String str = ".*\\.staging.*";
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(tmpFile.toString()))) {
            writer.write(str);
        }
        return tmpFile;
    }
}