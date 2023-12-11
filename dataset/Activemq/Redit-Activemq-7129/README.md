# Redit-ActiveMQ-7129

### Details

Title: ***Durable subscription messages can be orphaned when using individual ack mode with KahaDB***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-7129](https://issues.apache.org/jira/browse/AMQ-7129)

|         Label         |  Value   |       Label       |     Value      |
|:---------------------:|:--------:|:-----------------:|:--------------:|
|       **Type**        |   Bug    |   **Priority**    |     Major      |
|      **Status**       | RESOLVED |  **Resolution**   |     Fixed      |
| **Affects Version/s** |  5.15.8  | **Fix Version/s** | 5.15.9, 5.16.0 |

### Description

While not a common use case it is possible for a client to use individual acknowledge mode on a durable subscription. This allows the following scenario:

1. durable subscription is created on a topic
2. 10 messages are published to the topic
3. durable subscription consumes the messages but only acks the 5th message
4. Broker is restarted

What should happen is there should be 9 messages left that are recovered when the subscription comes back online. What actually happens though is that messages 1-4 are stuck forever because the KahaDB index will not load them on activation. The problem is that the lackAck value that is stored in the index is for message 5 so it starts on message 5 for recovery and ignores messages 1-4 even though those values are still tracked as part of the ackPositions sequence set.

The solution is that when the subscription is loaded to check if there are any ackPositions for that subscription that are earlier than the lastAck value and if they are we need to reset the cursor to the first ackPosition. Then we need to verify on recovery that any message iterated over actually exists as part of the sequence set so we don't load duplicates (this scenario happens if multiple subscriptions exist on the topic)

### Testcase

Reproduced version：5.15.0

Steps to reproduce：

1. Modify activemq.xml, set forceRecoverIndex="true" in kahaDB.
2. Start an AMQ cluster.
3. Create a topic named "testTopic" and a durable subscription named "sub1" on the topic.
4. Send 10 messages to the topic.
5. Consume the first 5 messages and ack the 5th message.
6. Restart the broker.
7. Consume the remaining messages.
8. No messages received after restart.
