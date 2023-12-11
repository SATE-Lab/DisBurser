# Redit-Kafka-14303

### Details

Title: ***Producer.send without record key and batch.size=0 goes into infinite loop***

JIRA link：[https://issues.apache.org/jira/browse/KAFKA-13964](https://issues.apache.org/jira/browse/KAFKA-13964)

|         Label         |    Value     |       Label       |    Value     |
|:---------------------:|:------------:|:-----------------:|:------------:|
|       **Type**        |     Bug      |   **Priority**    |    Major     |
|      **Status**       |     OPEN     |  **Resolution**   |    Fixed     |
| **Affects Version/s** | 3.3.0, 3.3.1 | **Fix Version/s** | 3.4.0, 3.3.2 |

### Description

3.3 has broken previous producer behavior.

A call to `producer.send(record)` with a record without a key and configured with `batch.size=0` never returns.

Reproducer:

```
class ProducerIssueTest extends IntegrationTestHarness {
  override protected def brokerCount = 1
  @Test
  def testProduceWithBatchSizeZeroAndNoRecordKey(): Unit = {
    val topicName = "foo"
    createTopic(topicName)
    val overrides = new Properties
    overrides.put(ProducerConfig.BATCH_SIZE_CONFIG, 0)
    val producer = createProducer(keySerializer = new StringSerializer, valueSerializer = new StringSerializer, overrides)
    val record = new ProducerRecord[String, String](topicName, null, "hello")
    val future = producer.send(record) // goes into infinite loop here
    future.get(10, TimeUnit.SECONDS)
  }
} 
```

 

[Documentation for producer configuration](https://kafka.apache.org/documentation/#producerconfigs_batch.size) states `batch.size=0` as a valid value:

> Valid Values: [0,...]

and recommends its use directly:

> A small batch size will make batching less common and may reduce throughput (a batch size of zero will disable batching entirely).

### Testcase

Reproduced version：3.3.1

Steps to reproduce：

1. Start kafka in a three-node cluster using KRaft.
2. Create a topic.
3. Create a producer with `batch.size=0` and send a message without a key.
4. The producer will go into an infinite loop in Producer.send.