package io.redit.samples.activemq6847;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.ActivemqHelper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
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

    private Connection connection;

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
    public void testMoveFromDLQImmediateDLQ() throws Exception {
        runner.runtime().enforceOrder("E1", () -> {
            createConnection();
        });


        runner.runtime().enforceOrder("E2", () -> {
            moveMessageToDLQ();
        });

        runner.runtime().enforceOrder("E3", () -> {
            retryDLQ();
        });


        logger.info("completed !!!");
    }

    private void createConnection() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(helper.ACTIVEMQ_URL1);
            RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
            redeliveryPolicy.setMaximumRedeliveries(0);
            connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
            connection = connectionFactory.createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    private void moveMessageToDLQ() {
        try {

            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue dest = session.createQueue("TestQueue");
            MessageConsumer consumer = session.createConsumer(dest);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println("TestQueue Received: " + message + " on " + message.getJMSDestination());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }

//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    logger.info("Consumer throw exception, message moved to DLQ");
                    throw new RuntimeException("Horrible exception");

                }
            });


            Thread.sleep(1000);

            MessageProducer producer = session.createProducer(dest);
            TextMessage textMessage = session.createTextMessage("test-message");
            producer.send(textMessage);
            System.out.println("Producer Sent message: " + textMessage.getJMSMessageID());

            Thread.sleep(1000);

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void retryDLQ(){
        try {
            for (int i = 0; i < 5; i++) {
                logger.info("retry DLQ message");
                // retry DLQ message
                String retryCmd = "curl -XGET --user admin:admin --header \"Origin: http://localhost\" http://localhost:8161/api/jolokia/exec/org.apache.activemq:brokerName=brokerA,destinationName=ActiveMQ.DLQ,destinationType=Queue,type=Broker/retryMessages";
                runner.runtime().runCommandInNode("server1", retryCmd);
                Thread.sleep(500);

                logger.info("check DLQ EnqueueCount");
                // 统计DLQ中的消息数量
                String dlqCmd = "curl -XGET --user admin:admin --header \"Origin: http://localhost\" http://localhost:8161/api/jolokia/read/org.apache.activemq:brokerName=brokerA,destinationName=ActiveMQ.DLQ,destinationType=Queue,type=Broker/EnqueueCount";
                String res = runner.runtime().runCommandInNode("server1", dlqCmd).stdOut();
                res = res.split("\"value\":")[1];
                res = res.split(",")[0];
                logger.info("DLQ EnqueueCount: " + res);
                Thread.sleep(500);
            }
        } catch (RuntimeEngineException | InterruptedException e){
            e.printStackTrace();
        }
    }


}
