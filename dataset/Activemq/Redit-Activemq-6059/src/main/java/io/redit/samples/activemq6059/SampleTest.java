package io.redit.samples.activemq6059;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.ActivemqHelper;
import org.apache.activemq.ActiveMQConnectionFactory;
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
    private static ActiveMQConnectionFactory connectionFactory;

    @BeforeClass
    public static void before() throws RuntimeEngineException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        ReditHelper.authenticateFiles(runner);
        String homeDir = ReditHelper.getActiveMQHomeDir();
        helper = new ActivemqHelper(runner, homeDir, logger, ReditHelper.numOfServers);

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
    public void testDLQRecovery() throws Exception {
        runner.runtime().enforceOrder("E1", () -> {
            logger.info("create ActiveMQConnectionFactory ...");
            connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(helper.ACTIVEMQ_URL1);
            connectionFactory.setClientID("test-client");
        });

        runner.runtime().enforceOrder("E2", () -> {
            sendOneMessage();
        });

        runner.runtime().enforceOrder("X1", () ->{
            try {
                // restart broker
                runner.runtime().restartNode("server1", 5);
                Thread.sleep(5000);
                helper.startServer(1);
                Thread.sleep(5000);
                helper.checkServer(1);
            } catch (RuntimeEngineException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E3", () -> {
            consumeFromDLQAfterRestart();
        });

        logger.info("completed !!!");
    }

    private void sendOneMessage()  {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue dest = session.createQueue("TestTopic");


            MessageProducer producer = session.createProducer(dest);
            TextMessage message = session.createTextMessage("Hello!");
            producer.send(message, DeliveryMode.PERSISTENT, 4, 1000);

            // expire message
            Thread.sleep(2000);

            MessageConsumer consumer = session.createConsumer(dest);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    logger.info("received message: " + message);
                }
            });

            getDLQQueueSize();

            producer.close();
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException | RuntimeEngineException e) {
            throw new RuntimeException(e);
        }
    }

    private void consumeFromDLQAfterRestart(){
        try{
            Connection connection;
            connection = connectionFactory.createConnection();
            connection.start();
            Session session2 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue dlq = session2.createQueue("ActiveMQ.DLQ");
            MessageConsumer consumer2 = session2.createConsumer(dlq);
            consumer2.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    // expected message consumed after restart
                    logger.info("DLQ received message: " + message);
                }
            });

            getDLQQueueSize();
            Thread.sleep(1000);

            consumer2.close();
            session2.close();
            connection.close();
        } catch (RuntimeEngineException | JMSException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void getDLQQueueSize() throws RuntimeEngineException {
        String dlqCmd = "curl -XGET --user admin:admin --header \"Origin: http://localhost\" http://localhost:8161/api/jolokia/read/org.apache.activemq:brokerName=brokerA,destinationName=ActiveMQ.DLQ,destinationType=Queue,type=Broker/QueueSize";
        String res = runner.runtime().runCommandInNode("server1", dlqCmd).stdOut();
        res = res.split("\"value\":")[1];
        res = res.split(",")[0];
        logger.info("DLQ QueueSize: " + res);
    }

}
