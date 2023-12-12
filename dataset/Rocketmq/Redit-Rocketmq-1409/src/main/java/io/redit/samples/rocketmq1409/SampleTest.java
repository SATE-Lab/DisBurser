package io.redit.samples.rocketmq1409;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.NetOp;
import io.redit.helpers.RocketmqHelper;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static RocketmqHelper helper;
    private DefaultMQProducer producer;

    private static final String TOPIC_NAME = "testTopic";
    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        helper = new RocketmqHelper(runner, ReditHelper.getRocketmqHomeDir(), logger, ReditHelper.getFileRW(), ReditHelper.numOfServers);
        // TODO change dledger conf files dynamically
        helper.givePermission();

        helper.startServer(1);
        Thread.sleep(1000);
        helper.startDledgerBroker(1, "n0");
        helper.startDledgerBroker(2, "n1");
        helper.startDledgerBroker(3, "n2");
        Thread.sleep(3000);
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test
    public void testQueryMsgByKeyWithAcl() throws RuntimeEngineException, TimeoutException {

        runner.runtime().enforceOrder("E1", () -> {
            try {
                // start a producer and send some messages
                producer = new DefaultMQProducer("producer_group", new AclClientRPCHook(new SessionCredentials("rocketmq2", "12345678")));
//                producer = new DefaultMQProducer("producer_group");
                producer.setNamesrvAddr(helper.namesrvAddr);
                // 启动Producer实例
                producer.start();

                // simulate network delay in real-world so that messages won't go too fast
                runner.runtime().networkOperation("server1", NetOp.delay(new Random().nextInt(101) + 50));
                runner.runtime().networkOperation("server2", NetOp.delay(new Random().nextInt(101) + 50));
                runner.runtime().networkOperation("server3", NetOp.delay(new Random().nextInt(101) + 50));

                for (int i = 0; i < 10; i++) {
                    // 创建消息，并指定Topic，Tag和消息体
                    Message msg = new Message(TOPIC_NAME, "TagA", ("Hello RocketMQ " + i).getBytes("UTF-8"));
                    msg.setKeys("key" + i);
                    // 发送消息到一个Broker
                    logger.info("send message: " + msg.toString());

                    producer.send(msg);

                }
            } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException |
                     UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });


        runner.runtime().enforceOrder("E2", () -> {
            // queryMsg
            try {
                QueryResult queryResult = producer.queryMessage(TOPIC_NAME, "key3", 10, 0, System.currentTimeMillis());
                List<MessageExt> messageExtList = queryResult.getMessageList();
                for (MessageExt messageExt : messageExtList) {
                    logger.info("query message: " + messageExt.toString());
                }

                runner.runtime().networkOperation("server1", NetOp.removeDelay());
                runner.runtime().networkOperation("server2", NetOp.removeDelay());
                runner.runtime().networkOperation("server3", NetOp.removeDelay());

            } catch (MQClientException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("completed !!!");
    }

}
