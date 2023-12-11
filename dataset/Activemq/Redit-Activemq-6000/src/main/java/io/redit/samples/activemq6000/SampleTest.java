package io.redit.samples.activemq6000;

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
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ActivemqHelper helper;
    private static final String QUEUE_NAME = "test-queue";
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
    public void testResumeNotDispatching() throws Exception {
        runner.runtime().enforceOrder("E1", () -> {
            logger.info("create ActiveMQConnectionFactory ...");
            connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(helper.ACTIVEMQ_URL1);
            connectionFactory.setClientID("test-client");
        });

        runner.runtime().enforceOrder("E2", () -> {
            try {
                sendMessageAndPauseQueue();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("completed !!!");
    }

    private void sendMessageAndPauseQueue() throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue q = session.createQueue(QUEUE_NAME);

        MessageProducer producer = session.createProducer(q);
        producer.send(session.createTextMessage("Hello"));

        // pause queue
        queueOperation(QUEUE_NAME, "brokerA", true);

        Thread.sleep(1500);
        MessageConsumer consumer = session.createConsumer(q);
        consumer.setMessageListener(message -> {
            try {
                logger.info("received message: {}", ((TextMessage) message).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
        Thread.sleep(1500);

        // resume queue
        queueOperation(QUEUE_NAME, "brokerA", false);

        // producer.send(session.createTextMessage("Hello again"));

        Thread.sleep(1000);

        producer.close();
        consumer.close();
        connection.close();
        session.close();
    }

    private void queueOperation(String queueName, String brokerName, boolean pause) throws Exception {
        // 连接到 ActiveMQ 的 JMX MBean 服务器
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://10.2.0.3:1099/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        // 构建 MBean 名称
        ObjectName queueMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",destinationType=Queue,destinationName=" + queueName);

        if (pause) {
            logger.info("Pausing queue: {}", queueName);
            mbsc.invoke(queueMBeanName, "pause", null, null);
        } else {
            logger.info("Resuming queue: {}", queueName);
            mbsc.invoke(queueMBeanName, "resume", null, null);
        }

        // 关闭 JMX 连接
        jmxc.close();
    }


}
