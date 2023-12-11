package io.redit.samples.kafka9254;

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
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;
import java.io.*;

public class SampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);
    protected static ReditRunner runner;
    private static ZookeeperHelper zookeeperHelper;
    private static KafkaHelper kafkaHelper;
    private static final String TOPIC_NAME = "test";
    Producer<String, String> producer;

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

    @Test
    public void testConsecutiveConfigChange() throws RuntimeEngineException {
        runner.runtime().enforceOrder("E1", () -> {
            try {
                createTopic(TOPIC_NAME, 1, 3);
                producer = createProducer();
                produceMessages(producer, TOPIC_NAME, 20);
                Thread.sleep(3000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        logger.info("completed !!!");
    }

    private static void createTopic(String topicName, int partitions, int replicationFactor) {
        Properties adminProperties = new Properties();
        adminProperties.put("bootstrap.servers", kafkaHelper.BOOTSTRAP_SERVERS);

        try (AdminClient adminClient = AdminClient.create(adminProperties)) {
            NewTopic newTopic = new NewTopic(topicName, partitions, (short) replicationFactor)
                    .configs(Collections.singletonMap("min.insync.replicas", "2"));
            adminClient.createTopics(Collections.singletonList(newTopic));
        }
    }

    private static void alterConfig() throws RuntimeEngineException {
        String cdCmd = "cd " + ReditHelper.getKafkaHomeDir() + " && ";
        String cmd1 = cdCmd + "./bin/kafka-configs.sh --bootstrap-server " + runner.runtime().ip("server1") + ":9092" + " --entity-type brokers --entity-default --alter --add-config log.message.timestamp.type=LogAppendTime";
        String cmd2 = cdCmd + "./bin/kafka-configs.sh --bootstrap-server " + runner.runtime().ip("server1") + ":9092" + " --entity-type brokers --entity-default --alter --add-config log.retention.ms=604800000";
        Utils.printResult(runner.runtime().runCommandInNode("server1", cmd1), logger);
        Utils.printResult(runner.runtime().runCommandInNode("server1", cmd2), logger);
    }

    private static Producer<String, String> createProducer() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHelper.BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");

        return new KafkaProducer<>(producerProps);
    }

    private static void produceMessages(Producer<String, String> producer, String topic, int numMessages) throws InterruptedException, RuntimeEngineException {
        for (int i = 0; i < numMessages; i++) {
            String key = "key-" + i;
            String value = "value-" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            producer.send(record);
            producer.flush();
            Thread.sleep(800);
            System.out.printf("Produced record with key %s and value %s%n", key, value);
            if (i == 5) {
                alterConfig();
            }
            if (i == 10) {
                kafkaHelper.shutdownBroker(1);
            }
        }
        producer.flush();
        producer.close();
    }


}
