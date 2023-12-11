package io.redit.samples.activemq6823;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.ActivemqHelper;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ActivemqHelper helper;
    private static final String QUEUE_NAME = "test-queue";
    private static ActiveMQConnectionFactory connectionFactory;

    String brokerURL = "http://10.2.0.3:8080";
//    String brokerURL = "tcp://10.2.0.3:61616";

    @BeforeClass
    public static void before() throws RuntimeEngineException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        String homeDir = ReditHelper.getActiveMQHomeDir();
        helper = new ActivemqHelper(runner, homeDir, logger, ReditHelper.numOfServers);

        ReditHelper.authenticateFiles(runner);
        helper.startServers();
        Thread.sleep(5000);
        helper.checkServers();
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test
    public void testHttpPullConsumer() throws Exception {
        runner.runtime().enforceOrder("E1", () -> {
            logger.info("create ActiveMQConnectionFactory ...");
            connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(brokerURL);
            connectionFactory.setClientID("test-client");

            ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
            prefetchPolicy.setQueuePrefetch(0);
            connectionFactory.setPrefetchPolicy(prefetchPolicy);

            try {
                sendAndReceiveMessage();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }

        });

        // runner.runtime().waitForRunSequenceCompletion(20);
        logger.info("completed !!!");
    }

    private void sendAndReceiveMessage() throws JMSException {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue q = session.createQueue(QUEUE_NAME);
        MessageProducer producer = session.createProducer(q);
        MessageConsumer consumer = session.createConsumer(q);

        TextMessage message = session.createTextMessage("Hello");
        producer.send(message);
        Message message1 = consumer.receive(1000);
        logger.info("message1: {}", message1);

        producer.close();
        consumer.close();
        connection.stop();
    }


}
