===========
Quick Start
===========

|projectName| is a Java-based end-to-end testing framework. So, you will need to write your test cases In Java, or languages that
can use Java libraries like the ones that can run on JVM, e.g. Scala. |projectName| can be used alongside the popular testing
frameworks in your programming language of choice e.g. JUnit in Java. Here, we use Java and JUnit . We also use Maven as
the build system.

Adding dependencies
===================

First, create a simple Maven application and add |projectName|'s dependency to your pom file.

.. ifconfig:: version.endswith("SNAPSHOT")

    .. code-block:: xml

        <repositories>
            <repository>
                <id>oss.sonatype</id>
                <url>http://oss.sonatype.org/content/repositories/snapshots</url>
                <releases>
                    <enabled>false</enabled>
                </releases>
                <snapshots>
                    <enabled>true</enabled>
                </snapshots>
            </repository>
        </repositories>

.. parsed-literal::

    <dependency>
        <groupId>io.redit</groupId>
        <artifactId>redit</artifactId>
        <version>\ |release|\ </version>
    </dependency>

Also add failsafe plugin to your pom file to be able to run integration tests.

.. code-block:: xml

    <project>
      [...]
      <build>
        <plugins>
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>3.0.0-M3</version>
            <executions>
              <execution>
                <goals>
                  <goal>integration-test</goal>
                  <goal>verify</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
      [...]
    </project>

Creating a Dockerfile
=====================

Next, you need to create a Dockerfile for your application and that Dockerfile should add any dependency that may be
needed by your application. In case you want to use the network partition capability
of |projectName|, you need to install ``iptables`` package as well. Network delay and loss will also need the ``iproute``
package to be installed. Here, we assume the application under test is written in Java.
So, we create a Dockerfile in the ``docker/Dockerfile`` address with the following content:

.. code-block:: docker

    FROM openjdk:8-stretch

    RUN apt update && apt install -y iptables iproute

.. important::

    In case you are using Docker Toolbox (and consequently boot2docker) on Windows or Mac, be aware that your currently
    installed boot2docker image may be missing ``sched_netem`` kernel module which is included in most of the
    linux distributions and is needed for ``tc`` command in the ``iproute`` package to work. So, unless you upgrade your
    boot2docker image (normally through running ``docker-machine upgrade [machine_name]``, you won't be able to use the
    network operation capabilities of |projectName|.

Adding a Test Case
==================


Now, create a JUnit integration test case  (ending with IT so failsafe picks it up) in the project's test directory. Here,
we provide an example for testing the situation of multithread. You can find the full code in the Redit project.

.. code-block:: java
    :linenos:

     public class MultithreadTest {
        public static final Logger logger = LoggerFactory.getLogger(MultithreadTest.class);

        @Test
        public void simpleDefinition() throws DeploymentVerificationException, RuntimeEngineException, TimeoutException, WorkspaceException {
            Deployment deployment = Deployment.builder("sample-multithread")
                    // Service Definitions
                    .withServiceFromJvmClasspath("s1", "target/classes", "**commons-io*.jar")
                        .startCommand("java -cp ${REDIT_JVM_CLASSPATH} io.redit.samples.multithread.Main")
                        .dockerImageName("redit/sample-multithread")
                        .dockerFileAddress("../sample-multithread/docker/Dockerfile", true)
                        .logFile("/var/log/sample1")
                        .logDirectory("/var/log/samples")
                        .serviceType(ServiceType.JAVA).and()
                    // Node Definitions
                    .withNode("n1", "s1")
                        .stackTrace("e1", "io.redit.samples.multithread.Main.helloWorld1," +
                                "io.redit.samples.multithread.Main.hello")
                        .stackTrace("e2", "io.redit.samples.multithread.Main.helloWorld2," +
                                "io.redit.samples.multithread.Main.helloWorld")
                        .stackTrace("e3", "io.redit.samples.multithread.Main.helloWorld3," +
                                "io.redit.samples.multithread.Main.hello")
                        .stackTrace("e4", "org.apache.commons.io.FilenameUtils.normalize")
                        .blockBefore("bbe2", "e2")
                        .unblockBefore("ubbe2", "e2")
                        .garbageCollection("g1")
                        .and()
                    .withNode("n2", "s1").offOnStartup().and()
                    .withNode("n3", "s1").and()
                    .withNode("n4", "s1").and()
                    // Test Case Events
                    .testCaseEvents("x1", "x2")
                    // Run Sequence Definition
                    .runSequence("bbe2 * e1 * ubbe2 * x1 *  e2  * e3 * x2 * e4")
                    .sharedDirectory("/redit")
                    .build();

            ReditRunner runner = ReditRunner.run(deployment);
            // Starting node n2
            runner.runtime().enforceOrder("x1",10, () -> runner.runtime().startNode("n2"));
            // Adding new nodes to the deployed environment
            runner.addNode(Node.limitedBuilder("n5", "s1"));
            // Imposing overlapping network partitions
            NetPart netPart1 = NetPart.partitions("n1","n2").connect(1, NetPart.REST, false).build();
            NetPart netPart2 = NetPart.partitions("n1","n2,n3").connect(1, NetPart.REST).build();
            runner.runtime().networkPartition(netPart1);
            runner.runtime().networkPartition(netPart2);
            // Imposing 10 secs of clock drift in node n1
            runner.runtime().clockDrift("n1", -10000);
            // Applying network delay and loss on node n2 before restarting it
            runner.runtime().networkOperation("n2", NetOp.delay(50).jitter(10), NetOp.loss(30));
            // removing the first network partition and restarting node n2
            runner.runtime().enforceOrder("x2", 10, () -> {
                runner.runtime().removeNetworkPartition(netPart1);
                runner.runtime().restartNode("n2", 10);
            });
            // removing the second network partition
            runner.runtime().removeNetworkPartition(netPart2);
            // Applying different kinds of network operations in different orders
            runner.runtime().networkOperation("n1", NetOp.delay(100).jitter(10), NetOp.loss(30),
                    NetOp.removeDelay(), NetOp.delay(10).jitter(4), NetOp.removeLoss(),
                    NetOp.removeDelay(), NetOp.loss(20), NetOp.removeLoss());
            // Waiting for the run sequence to be completed
            runner.runtime().waitForRunSequenceCompletion(60, 20);
        }
    }

Each |projectName| test case should start with defining a new ``Deployment`` object. A deployment definition consists of a a set
of service and node definitions. A Service is a node template and defines the docker image for the node, the start bash
command, required environment variables, common paths, etc. for a specific type of node.

Line 8-14 defines ``service1`` service. Line 9 defines the start command for the node, and in this case, it is using the ``start.sh`` bash file and it feeding it with ``-conf /config.cfg`` argument. This
config file will be provided separately through node definitions later. Line 14 concludes the service definition by marking it as a Java application.
If the programming language in use is listed in ``ServiceType`` enum, make sure to mark your application with the right
``ServiceType``.

.. important:: If your program runs on JVM and your programming language in use is not listed in  the ``ServiceType``
          enum, just choose ``ServiceType.Java`` as the service type.

Lines 16-30 defines four nodes named ``n1``, ``n2``, ``n3`` and ``n4`` from ``service1`` service and is adding a separate local config file
to each of them which will be located at the same target address ``/config.cfg``. Most of the service configuration can be
overriden by nodes.

Line 38 starts the defined deployment and line 64 stops the deployment after all tests are executed.

Line 42 shows how to start node while running. In
this case, a clock dirft of 100ms will be applied to node ``n1``. Line 44-47 shows how a network partition can be defined
and imposed. Here, each of the nodes will be in a separate partition. Line 45 shows an example of imposing network delay and loss on all the interfaces of a specific node.
Here, a network delay from a uniform distribution with mean=100 and variance=10 will be applied on ``n1`` and 30% of the
packets will be lost.

Logger Configuration
====================

|projectName| uses SLF4J for logging. As such, you can configure your logging tool of choice. A sample configuration with
Logback can be like this:

.. code-block:: xml

    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
            <layout class="ch.qos.logback.classic.PatternLayout">
                <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>
            </layout>
        </appender>

        <logger name="io.redit" level="DEBUG"/>

        <root level="ERROR">
            <appender-ref ref="Console" />
        </root>
    </configuration>

Running the Test Case
=====================

Finally, to run the test cases, run the following bash command:

.. code-block:: bash

    $  mvn clean verify

