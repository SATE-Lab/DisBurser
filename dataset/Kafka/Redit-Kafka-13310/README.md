# Redit-Kafka-13310

### Details

Title:***KafkaConsumer cannot jump out of the poll method, and the consumer is blocked in the ConsumerCoordinator method
maybeAutoCommitOffsetsSync(Timer timer). Cpu and traffic of Broker's side increase sharply***

JIRA link：[https://issues.apache.org/jira/browse/KAFKA-13310](https://issues.apache.org/jira/browse/KAFKA-13310)

|         Label         |  Value   |       Label       | Value |
|:---------------------:|:--------:|:-----------------:|:-----:|
|       **Type**        |   Bug    |   **Priority**    | Major |
|      **Status**       | RESOLVED |  **Resolution**   | Fixed |
| **Affects Version/s** |  2.8.1   | **Fix Version/s** | 3.2.0 |

### Description

## Foreword

   Because our consumers' consumption logic is sometimes heavier, we refer to the configuration of Kafka stream https://kafka.apache.org/documentation/#upgrade_10201_notable
Set max.poll.interval.ms to Integer.MAX_VALUE
Our consumers have adopted method : consumer.subscribe(Pattern.compile(".**riven.**"));

 

## Recurrence of the problem scene

operate steps are
(1) Test environment Kafka cluster: three brokers
(2) Topics conforming to regular expressions include rivenTest1, rivenTest2, and rivenTest88
(3) Only one consumer is needed, group.id is "rivenReassign", consumer.subscribe(Pattern.compile(".**riven.**"));
(4) At the beginning, the group status is stable, and everything is normal for consumers, then I delete topic: rivenTest88

 

## Phenomenon

   Problem phenomenon
 (1) The consumer is blocked in the poll method, no longer consume any messages, and the consumer log is always printing
[main] WARN org.apache.kafka.clients.consumer.internals.ConsumerCoordinator-[Consumer clientId=consumer-rivenReassign-1, groupId=rivenReassign] Offset commit failed on partition rivenTest88-1 at offset 0: This server does not host this topic-partition.
(2) The describe consumerGroup interface of Adminclient has always timed out, and the group status is no longer stable
(3) The cpu and traffic of the broker are **significantly increased**

 

 

## Problem tracking

  By analyzing the kafkaConsumer code, the version is 2.8.1.
I found that you introduced the waitForJoinGroup variable in the updateAssignmentMetadataIfNeeded method. For the reason, I attached the comment on the method: "try to update assignment metadata BUT do not need to block on the timer for join group". See as below:

 

```
 if (includeMetadataInTimeout) {
    // try to update assignment metadata BUT do not need to block on the timer for join group
    updateAssignmentMetadataIfNeeded(timer, false);
} else {
    while (!updateAssignmentMetadataIfNeeded(time.timer(Long.MAX_VALUE), true)) {
        log.warn("Still waiting for metadata");
    }
}
```

 

 

By tracing the code back layer by layer, it is found that the function of this variable is to construct a time.timer(0L) and pass it back to the method joinGroupIfNeeded (final Timer timer) in AbstractCoordinator. See as below:

```
// if not wait for join group, we would just use a timer of 0
      if (!ensureActiveGroup(waitForJoinGroup ? timer : time.timer(0L))) {
// since we may use a different timer in the callee, we'd still need 
// to update the original timer's current time after the call 
      timer.update(time.milliseconds()); 
      return false; 
}
```

 But you will find that there is a submethod onJoinPrepare in the method stack of joinGroupIfNeeded, and then there is a line of code in the onJoinPrepare method
maybeAutoCommitOffsetsSync(time.timer(rebalanceConfig.rebalanceTimeoutMs)), the value of rebalanceConfig.rebalanceTimeoutMs is actually max.poll.interval.ms.
Finally, I tracked down ConsumerCoordinator's method commitOffsetsSync(Map<TopicPartition, OffsetAndMetadata> offsets, Timer timer)
The input parameter offsets is subscriptions.allConsumed(), when I delete the topic: rivenTest88, commitOffsetsSync(Map<TopicPartition, OffsetAndMetadata> offsets, Timer timer) method will **fall into an infinite loop! !**

```
public boolean commitOffsetsSync(Map<TopicPartition, OffsetAndMetadata> offsets, Timer timer) {
 invokeCompletedOffsetCommitCallbacks();

 if (offsets.isEmpty())
 return true;

 do {
 if (coordinatorUnknown() && !ensureCoordinatorReady(timer)) {
 return false;
 }

 RequestFuture<Void> future = sendOffsetCommitRequest(offsets);
 client.poll(future, timer);

 // We may have had in-flight offset commits when the synchronous commit began. If so, ensure that
 // the corresponding callbacks are invoked prior to returning in order to preserve the order that
 // the offset commits were applied.
 invokeCompletedOffsetCommitCallbacks();

 if (future.succeeded()) {
 if (interceptors != null)
 interceptors.onCommit(offsets);
 return true;
 }

 if (future.failed() && !future.isRetriable())
 throw future.exception();

 timer.sleep(rebalanceConfig.retryBackoffMs);
 } while (timer.notExpired());

 return false;
}
```

 

 

**The reason for the endless loop is:**
(1) The expiration time of the timer is too long, which is max.poll.interval.ms
(2) The offsets to be submitted contain dirty data and TopicPartition that no longer exists
(3) The response future of sendOffsetCommitRequest(final Map<TopicPartition, OffsetAndMetadata> offsets) has always failed, and the exception in the future is UnknownTopicOrPartitionException. This exception is allowed to be retried.

Then since the infinite loop interval above is 100ms by default, timer.sleep(rebalanceConfig.retryBackoffMs);
If a large number of consumers have this problem at the same time, a large number of network requests will be generated to the Kafka broker, **resulting in a sharp increase in the cpu and traffic of the broker machine!**

 

 

## Suggest

1.maybeAutoCommitOffsetsSync(time.timer(rebalanceConfig.rebalanceTimeoutMs)), the time of this method is recommended not to use max.poll.interval.ms,
This parameter is open to users to configure. Through the explanation of this parameter on the official website, I would never think that this parameter will be used in this place. At the same time, it will block KafkaConsumer's poll (final Duration timeout), even if I set consumer.poll (Duration.ofMillis(1000)).

2. In fact, in the poll (Timer timer, boolean waitForJoinGroup) method of ConsumerCoordinatord, before calling the ensureActiveGroup method, the consumer ensures that the local metadata is up to date, see the code

 

```
if (rejoinNeededOrPending()) {
    // due to a race condition between the initial metadata fetch and the initial rebalance,
    // we need to ensure that the metadata is fresh before joining initially. This ensures
    // that we have matched the pattern against the cluster's topics at least once before joining.
    if (subscriptions.hasPatternSubscription()) {
        // For consumer group that uses pattern-based subscription, after a topic is created,
        // any consumer that discovers the topic after metadata refresh can trigger rebalance
        // across the entire consumer group. Multiple rebalances can be triggered after one topic
        // creation if consumers refresh metadata at vastly different times. We can significantly
        // reduce the number of rebalances caused by single topic creation by asking consumer to
        // refresh metadata before re-joining the group as long as the refresh backoff time has
        // passed.
        if (this.metadata.timeToAllowUpdate(time.milliseconds()) == 0) {
            this.metadata.requestUpdate();
        }

        if (!client.ensureFreshMetadata(timer)) {
            return false;
        }
    }

    if (!ensureActiveGroup(timer)) {
        return false;
    }
}
```

 

That is to say, the consumer knows which topic/topicPartition is legal before onJoinPrepare. In this case, why didn't you find the UnknownTopicOrPartitionException in the commitOffsetsSync method mentioned above,do not put the submitted offsets and the latest local metadata together for analysis, remove the non-existent topicpartitions, and then try to submit the offsets again. I think I can break out of the infinite loop by doing this

3. Why must the offset be submitted synchronously in the onJoinPrepare method? Can't the offset be submitted asynchronously? Or provide a parameter for the user to choose whether to submit synchronously or asynchronously. Or provide a new parameter to control the maximum number of retries for synchronous submission here, instead of using the Timer constructed by max.poll.interval.ms.

And if you don’t really submit the offset here, it will not have much impact. It may cause repeated consumption of some messages. I still suggest to provide a new parameter to control whether you need to submit the offset.

### Testcase

Reproduced version：2.13_2.8.0

Steps to reproduce：

1. Start zookeeper and kafka in a three-node cluster.
2. Create 3 topics "rivenTest1", "rivenTest2", "rivenTest88" with 1 partitions and 1 replicas.
3. Start a consumer with group.id "rivenReassign" and subscribe to the topic with a regular expression "riven.\*".
4. Produce some messages to all of the 3 topics, and consumer works fine.
5. Delete topic "rivenTest88".
6. Produce some message to "riveTest1" again.
7. Consumer will be blocked in the poll method, and the consumer log is always printing the following message:

```
[main] WARN  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-rivenReassign-1, groupId=rivenReassign] Offset commit failed on partition rivenTest88-0 at offset 10: This server does not host this topic-partition.
```
Notice: change kafka-clients version to 2.8.0 in pom.xml to get the injected test result, and change it to 3.2.0 to get the fixed test result.