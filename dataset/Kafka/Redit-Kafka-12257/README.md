# Redit-Kafka-12257

### Details

Title: ***Consumer mishandles topics deleted and recreated with the same name***

JIRA link：[https://issues.apache.org/jira/browse/KAFKA-12257](https://issues.apache.org/jira/browse/KAFKA-12257)

|         Label         |                      Value                      |       Label       |        Value        |
|:---------------------:|:-----------------------------------------------:|:-----------------:|:-------------------:|
|       **Type**        |                       Bug                       |   **Priority**    |      Critical       |
|      **Status**       |                    RESOLVED                     |  **Resolution**   |        Fixed        |
| **Affects Version/s** | 2.2.2, 2.3.1, 2.4.1, 2.5.1, 2.6.1, 2.7.1, 2.8.1 | **Fix Version/s** | 3.1.0, 3.0.0, 2.8.2 |

### Description

In [~~KAFKA-7738~~](https://issues.apache.org/jira/browse/KAFKA-7738), caching of leader epochs (KIP-320) was added to o.a.k.c.Metadata to ignore metadata responses with epochs smaller than the last seen epoch.

The current implementation can cause problems in cases where a consumer is subscribed to a topic that has been deleted and then recreated with the same name. This is something seen more often in consumers that subscribe to a multitude of topics using a wildcard.

Currently, when a topic is deleted and the Fetcher receives UNKNOWN_TOPIC_OR_PARTITION, the leader epoch is not cleared. If at a later time while the consumer is still running a topic is created with the same name, the leader epochs are set to 0 for the new topics partitions, and are likely smaller than those for the previous topic. For example, if a broker had restarted during the lifespan of the previous topic, the leader epoch would be at least 1 or 2. In this case the metadata will be ignored since it is incorrectly considered stale. Of course, the user will sometimes get lucky, and if a topic was only recently created so that the epoch is still 0, no problem will occur on recreation. The issue is also not seen when consumers happen to have been restarted in between deletion and recreation.

The most common side effect of the new metadata being disregarded is that the new partitions end up assigned but the Fetcher is unable to fetch data because it does not know the leaders. When recreating a topic with the same name it is likely that the partition leaders are not the same as for the previous topic, and the number of partitions may even be different. Besides not being able to retrieve data for the new topic, there is a more sinister side effect of the Fetcher triggering a metadata update after the fetch fails. The subsequent update will again ignore the topic's metadata if the leader epoch is still smaller than the cached value. This metadata refresh loop can continue indefinitely and with a sufficient number of consumers may even put a strain on a cluster since the requests are occurring in a tight loop. This can also be hard for clients to identify since there is nothing logged by default that would indicate what's happening. Both the Metadata class's logging of "*Not replacing existing epoch*", and the Fetcher's logging of "*Leader for partition <T-P> is unknown*" are at DEBUG level.

A second possible side effect was observed where if the consumer is acting as leader of the group and happens to not have any current data for the previous topic, e.g. it was cleared due to a metadata error from a broker failure, then the new topic's partitions may simply end up unassigned within the group. This is because while the subscription list contains the recreated topic the metadata for it was previously ignored due to the leader epochs. In this case the user would see logs such as:

```
WARN o.a.k.c.c.i.ConsumerCoordinator [Consumer clientId=myClientId, groupId=myGroup] The following subscribed topics are not assigned to any members: [myTopic]
```

Interestingly, I believe the Producer is less affected by this problem since o.a.k.c.p.i.ProducerMetadata explicitly clears knowledge of its topics in retainTopics() after each metadata expiration. ConsumerMetadata does no such thing.

To reproduce this issue:

1. Turn on DEBUG logging, e.g. for org.apache.kafka.clients.consumer and org.apache.kafka.clients.Metadata
2. Begin a consumer for a topic (or multiple topics)
3. Restart a broker that happens to be a leader for one of the topic's partitions
4. Delete the topic
5. Create another topic with the same name
6. Publish data for the new topic
7. The consumer will not receive data for the new topic, and there will be a high rate of metadata requests.
8. The issue can be corrected by restarting the consumer or restarting brokers until leader epochs are large enough

I believe KIP-516 (unique topic ids) will likely fix this problem, since after those changes the leader epoch map should be keyed off of the topic id, rather than the name.

One possible workaround with the current version of Kafka is to add code to onPartitionsRevoked() to manually clear leader epochs before each rebalance, e.g.

```
Map<TopicPartition, Integer> emptyLeaderEpochs = new HashMap<>();
ConsumerMetadata metadata = (ConsumerMetadata)FieldUtils.readField(consumer, "metadata", 
true);
FieldUtils.writeField(metadata, "lastSeenLeaderEpochs", emptyLeaderEpochs, true);
```

This is not really recommended of course, since besides modifying private consumer state, it defeats the purpose of epochs! It does in a sense revert the consumer to pre-2.2 behavior before leader epochs existed.

### Testcase

Reproduced version：2.13-2.8.0

Steps to reproduce：
1. Start kafka in a three-node cluster
2. Create a topic and produce some messages
3. Start a consumer to consume the messages
4. Restart the broker which is the leader of the partition
5. Delete the topic
6. Create another topic with the same name
7. Publish data for the new topic
8. The consumer will not receive data for the new topic, and there will be a high rate of metadata requests.
