package io.redit.samples.rocketmq1409;

import io.redit.ReditRunner;
import io.redit.dsl.entities.Deployment;
import io.redit.dsl.entities.PathAttr;
import io.redit.dsl.entities.ServiceType;
import io.redit.exceptions.RuntimeEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ReditHelper {
    public static int numOfServers = 3;
    private static final int HTTP_PORT = 9876;
    private static final String dir = "rocketmq-all-4.5.0";

    public static String getRocketmqHomeDir() {
        return "/rocketmq/" + dir;
    }

    public static Deployment getDeployment() {
        String workDir = System.getProperty("user.dir");
        String compressedPath = workDir + "/../../../Benchmark/Rocketmq/v4.5.0/" + dir + ".tar.gz";

        Deployment.Builder builder = Deployment.builder("sample-rocketmq")
                .withService("rocketmq")
                .applicationPath(compressedPath, "/rocketmq", PathAttr.COMPRESSED)
                .applicationPath("conf/runserver.sh", getRocketmqHomeDir() + "/bin/runserver.sh")
                .applicationPath("conf/runbroker.sh", getRocketmqHomeDir() + "/bin/runbroker.sh")
                .applicationPath("conf/logback_broker.xml", getRocketmqHomeDir() + "/conf/logback_broker.xml")
                .applicationPath("conf/logback_namesrv.xml", getRocketmqHomeDir() + "/conf/logback_namesrv.xml")
                .applicationPath("conf/broker.conf", getRocketmqHomeDir() + "/conf/broker.conf")
                .applicationPath("conf/plain_acl.yml", getRocketmqHomeDir() + "conf/plain_acl.yml")
                .dockerImageName("mengpo1106/redit").dockerFileAddress("docker/Dockerfile", true)
                .libraryPath(getRocketmqHomeDir() + "/lib/*.jar")
                .logDirectory(getRocketmqHomeDir() + "/logs")
                .serviceType(ServiceType.JAVA).and();

        builder.withService("server", "rocketmq").tcpPort(HTTP_PORT).and()
                .nodeInstances(numOfServers, "server", "server", true)
                .node("server1").applicationPath("conf/dledger/broker-n0.conf", getRocketmqHomeDir() + "/conf/dledger/broker-n0.conf").and()
                .node("server2").applicationPath("conf/dledger/broker-n1.conf", getRocketmqHomeDir() + "/conf/dledger/broker-n1.conf").and()
                .node("server3").applicationPath("conf/dledger/broker-n2.conf", getRocketmqHomeDir() + "/conf/dledger/broker-n2.conf").and();

        builder.node("server1").and().testCaseEvents("E1", "E2").runSequence("E1 * E2");
        return builder.build();
    }

    public static void startNodes(ReditRunner runner) throws RuntimeEngineException, InterruptedException {
        for (int index = 1; index <= numOfServers; index++) {
            runner.runtime().startNode("server" + index);
            Thread.sleep(1000);
        }
    }

    public static ArrayList<Object> getFileRW() throws IOException {
        ArrayList<Object> RWs = new ArrayList<>();
        return RWs;
    }
}
