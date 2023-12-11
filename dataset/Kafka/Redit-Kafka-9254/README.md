# Redit-Kafka-9254

### Details

Title: ***Updating Kafka Broker configuration dynamically twice reverts log configuration to default***

JIRA link：[https://issues.apache.org/jira/browse/KAFKA-9254](https://issues.apache.org/jira/browse/KAFKA-9254)

|         Label         |                  Value                   |       Label       |                  Value                   |
|:---------------------:|:----------------------------------------:|:-----------------:|:----------------------------------------:|
|       **Type**        |                   Bug                    |   **Priority**    |                 Critical                 |
|      **Status**       |                 RESOLVED                 |  **Resolution**   |                  Fixed                   |
| **Affects Version/s** | 1.1.0, 2.0.1, 2.1.1, 2.2.2, 2.4.0, 2.3.1 | **Fix Version/s** | 1.1.2, 2.0.2, 2.1.2, 2.2.3, 2.3.2, 2.4.1 |

### Description

We are engineers at Huobi and now encounter Kafka BUG 

Modifying DynamicBrokerConfig more than 2 times will invalidate the topic level unrelated configuration

The bug reproduction method as follows：

1. Set Kafka Broker config server.properties min.insync.replicas=3
2. Create topic test-1 and set topic‘s level config min.insync.replicas=2
3. Dynamically modify the configuration twice as shown below

```
bin/kafka-configs.sh --bootstrap-server xxx:9092 --entity-type brokers --entity-default --alter --add-config log.message.timestamp.type=LogAppendTime

bin/kafka-configs.sh --bootstrap-server xxx:9092 --entity-type brokers --entity-default --alter --add-config log.retention.ms=604800000
```

1. stop a Kafka Server and found the Exception as shown below
   org.apache.kafka.common.errors.NotEnoughReplicasException: Number of insync replicas for partition test-1-0 is [2], below required minimum [3]

### Testcase

Reproduced version：2.12-2.0.0

Steps to reproduce：

1. Set Kafka Broker config  server.properties min.insync.replicas=3.
2. Start kafka in a three-node cluster.
3. Create a topic with min.insync.replicas 2.
4. Create a producer and continuously send messages to the topic during the whole process.
5. Dynamically modify the configuration twice.
6. Stop a Kafka Server and found the NotEnoughReplicasException is shown in another broker's log.

```

