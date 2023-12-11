package io.redit.samples.kafka14303;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.KafkaHelper;
import io.redit.helpers.ZookeeperHelper;
import org.apache.kafka.clients.producer.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;
import java.io.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ZookeeperHelper zookeeperHelper;
    private static KafkaHelper kafkaHelper;
    private static final String TOPIC_NAME = "test";
    Producer<String, String> producer;
    static AdminClient adminClient;

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

    @Test(timeout = 15000)
    public void testBatchSizeZeroNoPartitionNoRecordKey() throws RuntimeEngineException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                // 创建主题
                createTopic(TOPIC_NAME, 1, 1);

                // 创建生产者
                producer = createProducer();

                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, null, "hello");

                Future<RecordMetadata> future = producer.send(record);
                future.get(1000, java.util.concurrent.TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        runner.runtime().waitForRunSequenceCompletion(20);
        logger.info("completed !!!");
    }

    private static void createTopic(String topicName, int partitions, int replicationFactor) {
        NewTopic newTopic = new NewTopic(topicName, partitions, (short) replicationFactor);
        adminClient.createTopics(Collections.singletonList(newTopic));
    }

    private static Producer<String, String> createProducer() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHelper.BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(producerProps);
    }

}
