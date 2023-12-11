package io.redit.samples.activemq6796;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.ActivemqHelper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.stomp.Stomp;
import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.activemq.transport.stomp.StompFrame;
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
    public void testAckMessagesInTransactionOutOfOrderWithTXClientIndividualAck() throws Exception {
        runner.runtime().enforceOrder("E1", () -> {
            logger.info("create ActiveMQConnectionFactory ...");
            connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(helper.ACTIVEMQ_URL1);
            connectionFactory.setClientID("test-client");
        });

        runner.runtime().enforceOrder("E2", ()->{
            try {
                createAProducerAndSendMessage();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E3", ()->{
            receiveMessageVisStomp();
        });

        logger.info("completed !!!");
    }

    private void createAProducerAndSendMessage() {
        try {
            logger.info("create a producer and send a message ...");
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue dest = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(dest);
            producer.send(session.createTextMessage("Message 1"));
            producer.send(session.createTextMessage("Message 2"));
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessageVisStomp() {
        try {
            logger.info("receive message via stomp ...");
            StompConnection stompConnection = new StompConnection();
            stompConnection.open(runner.runtime().ip("server1"), 61613);

            String frame = "STOMP\n" + "login:admin\n" + "passcode:admin\n" +
                    "accept-version:1.1\n" + "host:localhost\n" + "client-id:test-client\n" + "\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            String f = stompConnection.receiveFrame();
            printFrame(f);

            printQueueSize();

            // begin transaction
            frame = "BEGIN\n" + "transaction: tx1\n" + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            String ackMode = "client-individual";
            frame = "SUBSCRIBE\n" + "destination:/queue/" + QUEUE_NAME + "\n" +
                    "id:12345\n" + "ack:" + ackMode + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            StompFrame receivedFirst = stompConnection.receive();
            printFrame(receivedFirst.format());

            StompFrame receivedSecond = stompConnection.receive();
            printFrame(receivedSecond.format());

            // ack second, then first message
            frame = "ACK\n" + "transaction: tx1\n" + "subscription:12345\n" + "message-id:" +
                    receivedSecond.getHeaders().get("message-id") + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);
            Thread.sleep(300);

            frame = "ACK\n" + "transaction: tx1\n" + "subscription:12345\n" + "message-id:" +
                    receivedFirst.getHeaders().get("message-id") + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);
            Thread.sleep(300);

            // commit transaction
            frame = "COMMIT\n" + "receipt:1\n" + "transaction: tx1\n" + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            String receipt = stompConnection.receiveFrame();
            printFrame(receipt);
            Thread.sleep(1000);

            printQueueSize();

            String unsub = "UNSUBSCRIBE\n" + "destination:/queue/" + QUEUE_NAME + "\n" +
                    "receipt:1\n" + "id:12345\n\n" + Stomp.NULL;
            stompConnection.sendFrame(unsub);

            receipt = stompConnection.receiveFrame();
            printFrame(receipt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printQueueSize() throws RuntimeEngineException {
        String cmd = "curl -XGET --user admin:admin --header \"Origin: http://localhost\" http://localhost:8161/api/jolokia/read/org.apache.activemq:brokerName=brokerA,destinationName=" + QUEUE_NAME + ",destinationType=Queue,type=Broker/QueueSize";
        String res = runner.runtime().runCommandInNode("server1", cmd).stdOut();
        res = res.split("\"value\":")[1];
        res = res.split(",")[0];
        logger.info("queue " + QUEUE_NAME + " size: " + res);
    }

    private void printFrame(String f) {
        logger.info("received frame: \n" + f + "\n");
    }

}
