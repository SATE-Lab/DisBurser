package io.redit.samples.activemq7129;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.ActivemqHelper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ActivemqHelper helper;

    private static final String CLIENT_ID = "test-client";
    private static final String QUEUE_NAME = "test-queue";
    private static final String TOPIC_NAME = "test-topic";

    private static Topic topic;
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
    public void testActiveMQNoLocal() throws Exception {
        runner.runtime().enforceOrder("E1", () -> {
            logger.info("create ActiveMQConnectionFactory ...");
            connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(helper.ACTIVEMQ_URL1);
            connectionFactory.setClientID(CLIENT_ID);
        });

        runner.runtime().enforceOrder("E2", () -> {
            logger.info("send message ...");
            sendMessage();
        });

        runner.runtime().enforceOrder("X1", () -> {
            logger.info("restart broker");

            try {
                runner.runtime().restartNode("server1", 5);
                runner.runtime().restartNode("server2", 5);

                Thread.sleep(5000);

                helper.startServers();

                Thread.sleep(5000);
                helper.checkServers();

                getDestinationAttr("Topic", "EnqueueCount", TOPIC_NAME, "brokerA");
                getDestinationAttr("Topic", "DequeueCount", TOPIC_NAME, "brokerA");
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        runner.runtime().enforceOrder("E3", () -> {
            logger.info("receive message after restart ...");
            try {
                receiveMessageAfterRestart();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        logger.info("completed !!!");
    }

    private void sendMessage() {
        try {
            Session session = getSession(ActiveMQSession.INDIVIDUAL_ACKNOWLEDGE);
            topic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(topic);
            TopicSubscriber subscriber = session.createDurableSubscriber(topic, "sub1");

            for (int i = 1; i <= 10; i++) {
                producer.send(session.createTextMessage("msg: " + i));
            }

            producer.close();

            Thread.sleep(2000);
            getDestinationAttr("Topic", "EnqueueCount", TOPIC_NAME, "brokerA");
            getDestinationAttr("Topic", "DequeueCount", TOPIC_NAME, "brokerA");

            // receive message and acknowledge message 5
            for (int i = 1; i <= 10; i++) {
                TextMessage received = (TextMessage) subscriber.receive(1000);
                logger.info("received message: {}", received.getText());
                if (i == 5) {
                    received.acknowledge();
                }
            }

            getDestinationAttr("Topic", "EnqueueCount", TOPIC_NAME, "brokerA");
            getDestinationAttr("Topic", "DequeueCount", TOPIC_NAME, "brokerA");

            subscriber.close();
            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveMessageAfterRestart() throws Exception {
        Session session = getSession(ActiveMQSession.AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = session.createDurableSubscriber(topic, "sub1");

        for (int i = 1; i <= 9; i++) {
            TextMessage received = (TextMessage) subscriber.receive(1000);
            logger.info("received message: {}", received);
        }
        session.close();
    }


    private Session getSession(int ackMode) throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(CLIENT_ID);
        connection.start();
        return connection.createSession(false, ackMode);
    }

    /**
     * @param type 队列类型，Queue 或 Topic
     * @param attr 要查询的属性
     */
    private void getDestinationAttr(String type, String attr, String topicName, String brokerName) throws Exception {
        // JMX 连接参数
        String jmxServiceURL = String.format("service:jmx:rmi:///jndi/rmi://%s:1099/jmxrmi", runner.runtime().ip("server1"));
        ;
        JMXServiceURL url = new JMXServiceURL(jmxServiceURL);
        JMXConnector connector = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbeanServerConnection = connector.getMBeanServerConnection();

        // 获取队列的 MBean
        ObjectName queueMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",destinationType=" + type + ",destinationName=" + topicName);
        long attrVal = (Long) mbeanServerConnection.getAttribute(queueMBeanName, attr);

        System.out.println("Attribute [" + attr + "] for " + topicName + ": " + attrVal);

        // 关闭 JMX 连接
        connector.close();
    }

}
