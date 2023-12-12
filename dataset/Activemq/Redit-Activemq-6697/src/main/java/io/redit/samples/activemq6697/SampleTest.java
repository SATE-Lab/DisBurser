package io.redit.samples.activemq6697;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.NetOp;
import io.redit.helpers.ActivemqHelper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.stomp.StompFrame;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.activemq.transport.stomp.Stomp;
import org.apache.activemq.transport.stomp.StompConnection;

import javax.jms.*;
import java.util.Random;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ActivemqHelper helper;
    private static final String TOPIC_NAME = "test-topic";
    private static ActiveMQConnectionFactory connectionFactory;

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
    public void testTransactionRollbackAllowsSecondAckOutsideTXClientIndividualAck() throws Exception {
        runner.runtime().enforceOrder("E1", () -> {
            logger.info("create ActiveMQConnectionFactory ...");
            connectionFactory = new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(helper.ACTIVEMQ_URL1);
            connectionFactory.setClientID("test-client");
        });

        runner.runtime().enforceOrder("E2", () -> {
            createTopic();
        });
    }

    private void createTopic() {
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Queue queue = session.createQueue(TOPIC_NAME);

            MessageProducer producer = session.createProducer(queue);
            producer.send(session.createTextMessage("Hello"));
            producer.close();

            StompConnection stompConnection = new StompConnection();
            stompConnection.open(runner.runtime().ip("server1"), 61613);

            // simulate network delay in real-world so that message exchanges won't go too fast
            runner.runtime().networkOperation("server1", NetOp.delay(new Random().nextInt(201) + 100));
            runner.runtime().networkOperation("server2", NetOp.delay(new Random().nextInt(201) + 100));

            String frame = "STOMP\n" + "login:system\n" + "passcode:manager\n" +
                    "accept-version:1.1\n" + "host:localhost\n" + "client-id:test\n" + "\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            String f = stompConnection.receiveFrame();
            logger.info("received CONNECTED frame: \n" + f + "\n");

            // start transaction
            logger.info("start transaction");
            frame = "BEGIN\n" + "transaction: tx1\n" + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            // subscribe hello message
            String ackMode = "client-individual";
            frame = "SUBSCRIBE\n" + "destination:/queue/" + TOPIC_NAME + "\n" +
                    "id:12345\n" + "ack:" + ackMode + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            StompFrame received = stompConnection.receive();
            logger.info("received MESSAGE frame: \n" + received.format() + "\n");

            // ack it in the TX then abort
            frame = "ACK\n" + "transaction: tx1\n" + "subscription:12345\n" + "message-id:" +
                    received.getHeaders().get("message-id") + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            // rollback first message
            frame = "ABORT\n" + "transaction: tx1\n" + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            // assertEquals(1, queueView.getQueueSize());
            logger.info("queue size after 1st attempt to ask: " + getQueueSize());

            // ack it outside the TX and it should be really ack'd (expect)
            frame = "ACK\n" + "subscription:12345\n" + "message-id:" +
                    received.getHeaders().get("message-id") + "\n\n" + Stomp.NULL;
            stompConnection.sendFrame(frame);

            // expect queue size is 0
            logger.info("queue size after 2nd attempt to ask: " + getQueueSize());

            String unsub = "UNSUBSCRIBE\n" + "destination:/queue/" + TOPIC_NAME + "\n" +
                    "receipt:1\n" + "id:12345\n\n" + Stomp.NULL;
            stompConnection.sendFrame(unsub);

            String receipt = stompConnection.receiveFrame();
            logger.info("Expect to receive RECEIPT frame: \n" + receipt + "\n");

            runner.runtime().networkOperation("server1", NetOp.removeDelay());
            runner.runtime().networkOperation("server2", NetOp.removeDelay());

            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getQueueSize() throws RuntimeEngineException {
        String cmd = "curl -XGET --user admin:admin --header \"Origin: http://localhost\" http://localhost:8161/api/jolokia/read/org.apache.activemq:brokerName=brokerA,destinationName=" + TOPIC_NAME + ",destinationType=Queue,type=Broker/QueueSize";
        String res = runner.runtime().runCommandInNode("server1", cmd).stdOut();
        res = res.split("\"value\":")[1];
        res = res.split(",")[0];
        return Integer.parseInt(res);
    }


}
