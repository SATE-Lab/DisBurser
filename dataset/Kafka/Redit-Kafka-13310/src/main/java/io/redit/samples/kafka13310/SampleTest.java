package io.redit.samples.kafka13310;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.helpers.KafkaHelper;
import io.redit.helpers.ZookeeperHelper;
import org.apache.kafka.clients.admin.DeleteTopicsOptions;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ZookeeperHelper zookeeperHelper;
    private static KafkaHelper kafkaHelper;
    private static final String TOPIC_NAME1 = "rivenTest1";
    private static final String TOPIC_NAME2 = "rivenTest2";
    private static final String TOPIC_NAME3 = "rivenTest88";
    private Producer<String, String> producer;
    private Consumer<String, String> consumer;

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
    }

    @AfterClass
    public static void after() {
        if (runner != null) {
            runner.stop();
        }
    }

    @Test(timeout = 60000)
    public void testTopicDeletionWithConsumerUsingPatternSubscribe() throws RuntimeEngineException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                createTopic(TOPIC_NAME1, 1, 1);
                producer = createProducer();
                produceMessages(producer, TOPIC_NAME1, 10);
                produceMessages(producer, TOPIC_NAME2, 10);
                produceMessages(producer, TOPIC_NAME3, 10);
                consumer = createConsumer();
                consumeMessages(consumer, 30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E2", () -> {
            logger.info("deleting topic rivenTest88...");
            // 删除rivenTest88
            deleteTopic(TOPIC_NAME3);
        });

        runner.runtime().enforceOrder("E3", ()->{
            try {
                logger.info("produce message again");
                produceMessages(producer, TOPIC_NAME1, 10);
                // 再次尝试读取数据
                consumeMessages(consumer, 10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().waitForRunSequenceCompletion(15);
        logger.info("completed !!!");
    }


    private static void createTopic(String topicName, int partitions, int replicationFactor) {
        Properties adminProperties = new Properties();
        adminProperties.put("bootstrap.servers", kafkaHelper.BOOTSTRAP_SERVERS);

        try (AdminClient adminClient = AdminClient.create(adminProperties)) {
            NewTopic newTopic = new NewTopic(topicName, partitions, (short) replicationFactor);
            adminClient.createTopics(Collections.singletonList(newTopic));
        }
    }

    private static Producer<String, String> createProducer() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHelper.BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(producerProps);
    }

    private static void produceMessages(Producer<String, String> producer, String topic, int numMessages) {
        for (int i = 0; i < numMessages; i++) {
            String key = "key-" + i;
            String value = "value-" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            producer.send(record);
        }
        producer.flush();
    }

    private static Consumer<String, String> createConsumer() {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHelper.BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "rivenReassign");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        return new KafkaConsumer<>(consumerProps);
    }

    private static void consumeMessages(Consumer<String, String> consumer, int expectedMsgCnt) throws InterruptedException {
        consumer.subscribe(Pattern.compile("riven.*"));
        int msgCnt = 0;
        int timeout = 10;
        boolean success = false;

        for (int i = 0; i < timeout; i++) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                System.out.printf("Consumed record with key %s and value %s%n", record.key(), record.value());
            });
            msgCnt += records.count();
            if (msgCnt >= expectedMsgCnt) {
                success = true;
                break;
            }
            Thread.sleep(1000);
        }
        if (!success) {
            Assert.fail("Timeout without receiving enough messages");
        }
    }

    private static void deleteTopic(String topic) {
        Properties adminProperties = new Properties();
        adminProperties.put("bootstrap.servers", kafkaHelper.BOOTSTRAP_SERVERS);
        try (AdminClient adminClient = AdminClient.create(adminProperties)) {
            DeleteTopicsOptions options = new DeleteTopicsOptions().timeoutMs(5000); // 设置删除主题的超时时间
            DeleteTopicsResult result = adminClient.deleteTopics(Collections.singletonList(topic), options);
            result.values().get(topic).get();
            System.out.println("Topic deletion successful: " + topic);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error deleting topic: " + topic);
            e.printStackTrace();
        }
    }

}
