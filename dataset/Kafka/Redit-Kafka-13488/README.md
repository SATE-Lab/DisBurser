# Redit-Kafka-13488

### Details

Title:***Producer fails to recover if topic gets deleted (and gets auto-created)***

JIRA link：[https://issues.apache.org/jira/browse/KAFKA-13488](https://issues.apache.org/jira/browse/KAFKA-13488)

|         Label         |                      Value                      |       Label       |        Value        |  
|:---------------------:|:-----------------------------------------------:|:-----------------:|:-------------------:|  
|       **Type**        |                       Bug                       |   **Priority**    |       Blocker       |  
|      **Status**       |                    RESOLVED                     |  **Resolution**   |        Fixed        |  
| **Affects Version/s** | 2.2.2, 2.3.1, 2.4.1, 2.5.1, 2.6.3, 2.7.2, 2.8.1 | **Fix Version/s** | 3.1.0, 3.0.1, 2.8.2 |  

### Description
Producer currently fails to produce messages to a topic if the topic is deleted and gets auto-created OR is created manually during the lifetime of the producer (and certain other conditions are met - leaderEpochs of deleted topic > 0).



To reproduce, these are the steps which can be carried out:

0) A cluster with 2 brokers 0 and 1 with auto.topic.create=true.

1) Create a topic T with 2 partitions P0-> (0,1), P1-> (0,1)

2) Reassign the partitions such that P0-> (1,0), P1-> (1,0).

2) Create a producer P and send few messages which land on all the TPs of topic T.

3) Delete the topic T

4) Immediately, send a new message from producer P, this message will be failed to send and eventually timed out.

A test-case which fails with the above steps is added at the end as well as a patch file.



This happens after leaderEpoch (KIP-320) was introduced in the MetadataResponse KAFKA-7738. There is a solution attempted to fix this issue in KAFKA-12257, but the solution has a bug due to which the above use-case still fails.



Issue in the solution of KAFKA-12257:

// org.apache.kafka.clients.Metadata.handleMetadataResponse():
...
Map<String, Uuid> topicIds = new HashMap<>();
Map<String, Uuid> oldTopicIds = cache.topicIds();
for (MetadataResponse.TopicMetadata metadata : metadataResponse.topicMetadata()) {
String topicName = metadata.topic();
Uuid topicId = metadata.topicId();
topics.add(topicName);
// We can only reason about topic ID changes when both IDs are valid, so keep oldId null unless the new metadata contains a topic ID
Uuid oldTopicId = null;
if (!Uuid.ZERO_UUID.equals(topicId)) {
topicIds.put(topicName, topicId);
oldTopicId = oldTopicIds.get(topicName);
} else {
topicId = null;
}
...
}
With every new call to handleMetadataResponse(), cache.topicIds() gets created afresh. When a topic is deleted and created immediately soon afterwards (because of auto.create being true), producer's call to MetadataRequest for the deleted topic T will result in a UNKNOWN_TOPIC_OR_PARTITION or LEADER_NOT_AVAILABLE error MetadataResponse depending on which point of topic recreation metadata is being asked at. In the case of errors, TopicId returned back in the response is Uuid.ZERO_UUID. As seen in the above logic, if the topicId received is ZERO, the method removes the earlier topicId entry from the cache.

Now, when a non-Error Metadata Response does come back for the newly created topic T, it will have a non-ZERO topicId now but the leaderEpoch for the partitions will mostly be ZERO. This situation will lead to rejection of the new MetadataResponse if the older LeaderEpoch was >0 (for more details, refer to KAFKA-12257). Because of the rejection of the metadata, producer will never get to know the new Leader of the TPs of the newly created topic.



{} 1. Solution / Fix (Preferred):
Client's metadata should keep on remembering the old topicId till:
1) response for the TP has ERRORs
2) topicId entry was already present in the cache earlier
3) retain time is not expired

--- a/clients/src/main/java/org/apache/kafka/clients/Metadata.java
+++ b/clients/src/main/java/org/apache/kafka/clients/Metadata.java
@@ -336,6 +336,10 @@ public class Metadata implements Closeable {
topicIds.put(topicName, topicId);
oldTopicId = oldTopicIds.get(topicName);
} else {
+                // Retain the old topicId for comparison with newer TopicId created later. This is only needed till retainMs
+                if (metadata.error() != Errors.NONE && oldTopicIds.get(topicName) != null && retainTopic(topicName, false, nowMs))
+                    topicIds.put(topicName, oldTopicIds.get(topicName));
+                else
                     topicId = null;
             }

{} 2. Alternative Solution / Fix {}:
To allow updates to LeaderEpoch when originalTopicId was null. This is less desirable as when cluster moves from no topic IDs to using topic IDs, we will count this topic as new and update LeaderEpoch irrespective of whether newEpoch was greater than current or not.

@@ -394,7 +398,7 @@ public class Metadata implements Closeable {
if (hasReliableLeaderEpoch && partitionMetadata.leaderEpoch.isPresent()) {
int newEpoch = partitionMetadata.leaderEpoch.get();
Integer currentEpoch = lastSeenLeaderEpochs.get(tp);
-            if (topicId != null && oldTopicId != null && !topicId.equals(oldTopicId)) {
+            if (topicId != null && !topicId.equals(oldTopicId)) {
                 // If both topic IDs were valid and the topic ID changed, update the metadata
                 log.info("Resetting the last seen epoch of partition {} to {} since the associated topicId changed from {} to {}",
                          tp, newEpoch, oldTopicId, topicId);
From the above discussion, i think Solution 1 would be a better solution.

–
Testcase to repro the issue:

@Test
def testSendWithTopicDeletionMidWay(): Unit = {
val numRecords = 10

    // create topic with leader as 0 for the 2 partitions.
    createTopic(topic, Map(0 -> Seq(0, 1), 1 -> Seq(0, 1)))

    val reassignment = Map(
      new TopicPartition(topic, 0) -> Seq(1, 0),
      new TopicPartition(topic, 1) -> Seq(1, 0)
    )

    // Change leader to 1 for both the partitions to increase leader Epoch from 0 -> 1
    zkClient.createPartitionReassignment(reassignment)
    TestUtils.waitUntilTrue(() => !zkClient.reassignPartitionsInProgress,
      "failed to remove reassign partitions path after completion")

    val producer = createProducer(brokerList, maxBlockMs = 5 * 1000L, deliveryTimeoutMs = 20 * 1000)

    (1 to numRecords).map { i =>
      val resp = producer.send(new ProducerRecord(topic, null, ("value" + i).getBytes(StandardCharsets.UTF_8))).get
      assertEquals(topic, resp.topic())
    }

    // start topic deletion
    adminZkClient.deleteTopic(topic)

    // Verify that the topic is deleted when no metadata request comes in
    TestUtils.verifyTopicDeletion(zkClient, topic, 2, servers)
    
    // Producer would timeout and not self-recover after topic deletion.
    val e = assertThrows(classOf[ExecutionException], () => producer.send(new ProducerRecord(topic, null, ("value").getBytes(StandardCharsets.UTF_8))).get)
    assertEquals(classOf[TimeoutException], e.getCause.getClass)
}
Attaching the solution proposal and test repro as a patch file.



### Testcase

Reproduced version：2.13_3.0.0

Steps to reproduce：

1. Start zookeeper and kafka in a three-node cluster.
2. Create a topic with 3 partitions and replicate factor = 3.
3. Reassign partition leaders of that topic.
4. Create a producer and write some message to that topic.
5. Delete the topic.
6. Try to write message with the same producer again.
7. Producer with hang forever or until timeout.