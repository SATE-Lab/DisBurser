package io.redit.samples.kafka13488;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.KafkaHelper;
import io.redit.helpers.Utils;
import io.redit.helpers.ZookeeperHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.io.*;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ZookeeperHelper zookeeperHelper;
    private static KafkaHelper kafkaHelper;
    private static final String TOPIC_NAME = "test";
    Producer<String, String> producer;
    Consumer<String, String> consumer;
    static AdminClient adminClient;
    static String cdCmd = "cd " + ReditHelper.getKafkaHomeDir() + "/bin";

    @BeforeClass
    public static void before() throws RuntimeEngineException, IOException, InterruptedException {
        runner = ReditRunner.run(ReditHelper.getDeployment());
        ReditHelper.startNodes(runner);
        zookeeperHelper = new ZookeeperHelper(runner, ReditHelper.getZookeeperHomeDir(), logger, ReditHelper.getZookeeperFileRW(), ReditHelper.numOfServers);
        zookeeperHelper.addConfFile();
        kafkaHelper = new KafkaHelper(runner, ReditHelper.getKafkaHomeDir(), logger, ReditHelper.getKafkaFileRW(), ReditHelper.numOfServers, "");
        kafkaHelper.addKafkaPropFile();

        zookeeperHelper.startServers();
        Thread.sleep(5000);
        zookeeperHelper.checkServersStatus();
        kafkaHelper.startKafkas();
        Thread.sleep(5000);
        kafkaHelper.checkJps();

        Properties adminProperties = new Properties();
        adminProperties.put("bootstrap.servers", kafkaHelper.BOOTSTRAP_SERVERS);

        adminClient = AdminClient.create(adminProperties);
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test(timeout = 20000)
    public void testSendWithTopicDeletionMidWay() throws RuntimeEngineException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                // 创建主题
                createTopic();
                describeTopic();

                // 重分配分区
                reassignPartitions();
                describeTopic();

                // 创建生产者
                producer = createProducer();

                // 写入数据
                produceMessages(producer, TOPIC_NAME, 0, 10);
                produceMessages(producer, TOPIC_NAME, 1, 10);
                produceMessages(producer, TOPIC_NAME, 2, 10);

                deleteTopic();

                produceMessages(producer, TOPIC_NAME, 0, 10);
                produceMessages(producer, TOPIC_NAME, 1, 10);
                produceMessages(producer, TOPIC_NAME, 2, 10);

                producer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        runner.runtime().waitForRunSequenceCompletion(20);
        logger.info("completed !!!");
    }

    private static Producer<String, String> createProducer() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHelper.BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());


        return new KafkaProducer<>(producerProps);
    }

    private static void produceMessages(Producer<String, String> producer, String topic, int partition, int numMessages) {
        for (int i = 0; i < numMessages; i++) {
            String key = "key-" + i;
            String value = "value-" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, partition, key, value);
            producer.send(record);
            System.out.printf("Produced record with key %s and value %s%n", key, value);
        }
        producer.flush();
    }
    private static void createTopic() throws RuntimeEngineException {
        String createTopicCmd = cdCmd + " && ./kafka-topics.sh --create --topic " + TOPIC_NAME + " --bootstrap-server " + kafkaHelper.BOOTSTRAP_SERVERS + " --partitions 3 --replication-factor 3";
        Utils.printResult(runner.runtime().runCommandInNode("server1", createTopicCmd), logger);
    }

    private static void describeTopic() throws RuntimeEngineException {
        String describeTopicCmd = cdCmd + " && ./kafka-topics.sh --describe --topic " + TOPIC_NAME + " --bootstrap-server " + kafkaHelper.BOOTSTRAP_SERVERS;
        Utils.printResult(runner.runtime().runCommandInNode("server1", describeTopicCmd), logger);
    }

    private static void reassignPartitions() throws RuntimeEngineException {
        String reassignPartitionsCmd = cdCmd + " && ./kafka-reassign-partitions.sh --bootstrap-server " + kafkaHelper.BOOTSTRAP_SERVERS + " --reassignment-json-file ./reassignment-file.json --execute";
        Utils.printResult(runner.runtime().runCommandInNode("server1", reassignPartitionsCmd), logger);
    }

    private static void deleteTopic() throws RuntimeEngineException {
        String deleteTopicCmd = cdCmd + " && ./kafka-topics.sh --delete --topic " + TOPIC_NAME + " --bootstrap-server " + kafkaHelper.BOOTSTRAP_SERVERS;
        Utils.printResult(runner.runtime().runCommandInNode("server1", deleteTopicCmd), logger);
    }

}
