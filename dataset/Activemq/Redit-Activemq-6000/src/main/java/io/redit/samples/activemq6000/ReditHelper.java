package io.redit.samples.activemq6000;

import io.redit.ReditRunner;
import io.redit.dsl.entities.Deployment;
import io.redit.dsl.entities.PathAttr;
import io.redit.dsl.entities.ServiceType;
import io.redit.exceptions.RuntimeEngineException;

public class ReditHelper {
    public static int numOfServers = 2;
    public static final String dir = "activemq-5.12.0";

    public static String getActiveMQHomeDir(){
        return "/activemq/" + dir;
    }

    public static Deployment getDeployment() {
        String workDir = System.getProperty("user.dir");
        String compressedPath = workDir + "/../../../Benchmark/Activemq/v5.12.0/" + dir + ".tar.gz";

        Deployment.Builder builder = Deployment.builder("sample-activemq")
                .withService("activemq")
                .applicationPath(compressedPath, "/activemq",  PathAttr.COMPRESSED)
                .applicationPath("conf/jetty.xml", getActiveMQHomeDir() + "/conf/jetty.xml")
                .dockerImageName("mengpo1106/redit").dockerFileAddress("docker/Dockerfile", true)
                .libraryPath(getActiveMQHomeDir() + "/lib/*.jar")
                .logDirectory(getActiveMQHomeDir() + "/data")
                .serviceType(ServiceType.JAVA).and();

        builder.withService("server", "activemq").and()
                .nodeInstances(numOfServers, "server", "server", true)
                .node("server1").applicationPath("conf/server1/activemq.xml", getActiveMQHomeDir() + "/conf/activemq.xml").and()
                .node("server2").applicationPath("conf/server2/activemq.xml", getActiveMQHomeDir() + "/conf/activemq.xml").and();

        builder.node("server1").and().testCaseEvents("E1", "E2").runSequence("E1 * E2");
        return builder.build();
    }

    public static void startNodes(ReditRunner runner) throws RuntimeEngineException, InterruptedException {
        for (int index = 1; index <= numOfServers; index++) {
            runner.runtime().startNode("server" + index);
            Thread.sleep(1000);
        }
    }

    public static void authenticateFiles(ReditRunner runner) throws RuntimeEngineException {
        String cmd = "chmod -R 777 /activemq/";
        for (int index = 1; index <= numOfServers; index++) {
            runner.runtime().runCommandInNode("server" + index, cmd);
        }
    }
}
