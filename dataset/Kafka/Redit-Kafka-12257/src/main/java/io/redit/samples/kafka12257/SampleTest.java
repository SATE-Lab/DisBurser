package io.redit.samples.kafka12257;

import io.redit.ReditRunner;
import io.redit.exceptions.RuntimeEngineException;
import io.redit.execution.NetOp;
import io.redit.helpers.KafkaHelper;
import io.redit.helpers.ZookeeperHelper;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
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
import java.util.Random;
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
    int leaderId = -1;


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

    @Test
    public void testCreateTopicAndDeleteTopic() throws RuntimeEngineException, TimeoutException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                // 创建主题
                createTopic(TOPIC_NAME, 1, 1);

                // simulate network delay in real-world so that producer won't go too fast
                runner.runtime().networkOperation("server1", NetOp.delay(new Random().nextInt(101) + 50));
                runner.runtime().networkOperation("server2", NetOp.delay(new Random().nextInt(101) + 50));
                runner.runtime().networkOperation("server3", NetOp.delay(new Random().nextInt(101) + 50));

                // 创建生产者
                producer = createProducer();
                produceMessages(producer, TOPIC_NAME, 10);

                // 创建消费者
                consumer = createConsumer();

                // 获取分区的领导者
                leaderId = getPartitionLeader(TOPIC_NAME, 0);

                // 读取数据
                consumeMessages(consumer, TOPIC_NAME, 10);

                runner.runtime().networkOperation("server1", NetOp.removeDelay());
                runner.runtime().networkOperation("server2", NetOp.removeDelay());
                runner.runtime().networkOperation("server3", NetOp.removeDelay());


            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        runner.runtime().enforceOrder("X1", () -> {
            // 重启分区领导者
            try {
                kafkaHelper.shutdownBroker(leaderId);
                Thread.sleep(3000);
                runner.runtime().restartNode("server" + leaderId, 5);
                zookeeperHelper.startServer(leaderId);
                Thread.sleep(5000);
                kafkaHelper.startKafka(leaderId);
                Thread.sleep(5000);
                kafkaHelper.checkJps();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        runner.runtime().enforceOrder("E2", () -> {
            // 读取数据
            try {
                // 删除主题
                logger.info("delete topic: " + TOPIC_NAME);
                deleteTopic(TOPIC_NAME);

                // Create another topic with the same name
                logger.info("create topic again");
                createTopic(TOPIC_NAME, 1, 1);

                produceMessages(producer, TOPIC_NAME, 20);
                consumeMessages(consumer, TOPIC_NAME, 20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        logger.info("completed !!!");
    }


    private static void createTopic(String topicName, int partitions, int replicationFactor) {
        NewTopic newTopic = new NewTopic(topicName, partitions, (short) replicationFactor);
        adminClient.createTopics(Collections.singletonList(newTopic));
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
            System.out.printf("Produced record with key %s and value %s%n", key, value);
        }
        producer.flush();
    }

    private static Consumer<String, String> createConsumer() {
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHelper.BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "example-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(consumerProps);
    }

    private static void consumeMessages(Consumer<String, String> consumer, String topic,int expectedMsgCnt) throws InterruptedException {
        consumer.subscribe(Collections.singletonList(topic));
        int msgCnt = 0;
        int timeout = 15;

        for (int i = 0; i < timeout; i++){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            records.forEach(record -> {
                System.out.printf("Consumed record with key %s and value %s%n", record.key(), record.value());
            });
            if (records.count() > 0) {
                msgCnt += records.count();
                if (msgCnt >= expectedMsgCnt) {
                    return;
                }
            }
            Thread.sleep(500);
        }
        Assert.fail("Timeout without consuming enough messages");
    }

    private int getPartitionLeader(String topicName, int partitionNumber) {
        PartitionInfo partitionInfo = consumer.partitionsFor(topicName).stream()
                .filter(p -> p.partition() == partitionNumber) // 根据分区ID过滤
                .findFirst() // 找到第一个匹配的元素
                .orElse(null); // 如果没有匹配的元素，返回null
        // 如果找到了分区的元数据，就获取领导者的信息
        if (partitionInfo != null) {
            Node leader = partitionInfo.leader(); // 获取领导者节点
            int leaderId = leader.id(); // 获取领导者ID
            return leaderId + 1;
        } else {
            System.out.println("No partition info found for topic " + topicName + " partition " + partitionNumber);
            return -1;
        }
    }

    private void deleteTopic(String topicName) {
        adminClient.deleteTopics(Collections.singletonList(topicName));
    }


}
