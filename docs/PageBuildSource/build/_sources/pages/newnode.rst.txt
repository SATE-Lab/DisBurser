============================
Adding New Nodes Dynamically
============================


It is possible to add new nodes dynamically after a defined deployment is started. New nodes can only be created out of
pre-defined services and they can't include any internal events. In the following code, ``service1`` service is first created
similar to the one in :doc:`quickstart`. Then, at line 19, a new node named ``n2`` is being created out of ``service1`` service.
``Node.limitedBuilder`` method returns an instance of ``Node.LimitedBuilder`` which then can be further customized by chaining the proper
method calls. This builder wouldn't allow the definition of internal events for the node. However, all the other node configurations
are available.

.. code-block:: java
    :linenos:

    public class SampleTest {
        protected static ReditRunner runner;
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
                    .build();
            ReditRunner runner = ReditRunner.run(deployment);
            // Adding new nodes to the deployed environment
            runner.addNode(Node.limitedBuilder("n52", "s1"));
        }
    }

The current limitation of this capability is that if there is a network partition applied to the current deployment, the
new node wouldn't be included in that network partition. Introduction of new network partitions will include the new node
in generating blocking rules for iptables. This limitation will be removed in future releases.