package io.redit.samples.activemq6500;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.ActivemqHelper;

import javax.xml.ws.Holder;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.lang.reflect.InvocationTargetException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ActivemqHelper helper;
    private static final String TOPIC_NAME = "commandTopic";

    private Connection connection;

    private Session session;


    @BeforeClass
    public static void before() throws RuntimeEngineException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
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
    public void testProduceAndConsumeLargeNumbersOfMessages() throws Exception {
        runner.runtime().enforceOrder("E1", () -> {

            try {
                String brokerURL = "amqp://10.2.0.3:5672?amqp.idleTimeout=240000";
                ConnectionFactory factory
                        = (ConnectionFactory) Class.forName("org.apache.qpid.jms.JmsConnectionFactory").getConstructor(String.class).newInstance(
                        brokerURL);

                connection = factory.createConnection();
                connection.start();

                session = connection.createSession(false, QueueSession.CLIENT_ACKNOWLEDGE);
            } catch (NoSuchMethodException | ClassNotFoundException | JMSException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });


        runner.runtime().enforceOrder("E2", () -> {
            MessageProducer producer = null;
            MessageConsumer consumer = null;
            final int nbOfMessagesSent = 2000;

            final Holder<Integer> nbOfMessagesReceived = new Holder(0);


            try {
                Topic topic = session.createTopic(TOPIC_NAME);
                consumer = session.createConsumer(topic);
                consumer.setMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        try {
                            nbOfMessagesReceived.value++;
                            message.acknowledge();
                        } catch (JMSException ex) {
                            logger.info("Caught exception.", ex);
                        }
                    }
                });

                producer = session.createProducer(topic);

                TextMessage textMessage = session.createTextMessage();
                textMessage.setText("messageText");

                for (int i = 0; i < nbOfMessagesSent; i++) {
                    producer.send(textMessage, javax.jms.DeliveryMode.PERSISTENT,
                            javax.jms.Message.DEFAULT_PRIORITY,
                            60000);
                }

                producer.close();
                consumer.close();
                session.close();
                connection.close();

            } catch (JMSException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {

                logger.info("Num OfMessagesSent: " + nbOfMessagesSent);
                logger.info("Num OfMessagesReceived: " + nbOfMessagesReceived.value.intValue());
            }
        });
    }

}
