# Redit-Kafka-5098

### Details

Title: ***KafkaProducer.send() blocks and generates TimeoutException if topic name has illegal char***

JIRA link：[https://issues.apache.org/jira/browse/KAFKA-5098](https://issues.apache.org/jira/browse/KAFKA-5098)

|         Label         |  Value   |       Label       | Value |
|:---------------------:|:--------:|:-----------------:|:-----:|
|       **Type**        |   Bug    |   **Priority**    | Major |
|      **Status**       | RESOLVED |  **Resolution**   | Fixed |
| **Affects Version/s** | 0.10.2.0 | **Fix Version/s** | 2.1.0 |

### Description

The server is running with auto create enabled. If we try to publish to a topic with a forward slash in the name, the call blocks and we get a TimeoutException in the Callback. I would expect it to return immediately with an InvalidTopicException.

There are other blocking issues that have been reported which may be related to some degree, but this particular cause seems unrelated.

Sample code:

```
import org.apache.kafka.clients.producer.*;
import java.util.*;

public class KafkaProducerUnexpectedBlockingAndTimeoutException {

  public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "kafka.example.com:9092");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("max.block.ms", 10000); // 10 seconds should illustrate our point

    String separator = "/";
    //String separator = "_";

    try (Producer<String, String> producer = new KafkaProducer<>(props)) {

      System.out.println("Calling KafkaProducer.send() at " + new Date());
      producer.send(
          new ProducerRecord<String, String>("abc" + separator + "someStreamName",
              "Not expecting a TimeoutException here"),
          new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception e) {
              if (e != null) {
                System.out.println(e.toString());
              }
            }
          });
      System.out.println("KafkaProducer.send() completed at " + new Date());
    }


  }

}
```

Switching to the underscore separator in the above example works as expected.

Mea culpa: We neglected to research allowed chars in a topic name, but the TimeoutException we encountered did not help point us in the right direction.

### Testcase

Reproduced version：2.12-2.0.0

Steps to reproduce：

1. Start kafka in a three-node cluster.
2. Create a producer and send some message to an invalid topic name.
3. Exception is not thrown until max.block.ms passed.
