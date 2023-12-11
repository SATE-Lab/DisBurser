# Redit-ActiveMQ-6796

### Details

Title: ***Acknowledging messages out of order in a STOMP 1.1 transaction raises exception***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-6796](https://issues.apache.org/jira/browse/AMQ-6796)

|         Label         |  Value   |       Label       |     Value      |
|:---------------------:|:--------:|:-----------------:|:--------------:|
|       **Type**        |   Bug    |   **Priority**    |     Major      |
|      **Status**       | RESOLVED |  **Resolution**   |     Fixed      |
| **Affects Version/s** |  5.15.0  | **Fix Version/s** | 5.15.1, 5.16.0 |

### Description

Reproducing the problem:

- Receive two messages via STOMP on a subscription with ack:client-individual
- Start a transaction
- Acknowledge message #2
- Acknowledge message #1
- Commit the transaction

Expected behaviour:

- Both messages are acknowledged, life goes on

Observed behaviour:

- Exception is raised

{{javax.jms.JMSException: Unmatched acknowledge: MessageAck

{commandId = 0, responseRequired = false, ackType = 2, consumerId = ID:(...)-6:555:-1:1, firstMessageId = null, lastMessageId = ID:(...)-6:555:-1:1:1, destination = queue://(...), transactionId = null, messageCount = 2, poisonCause = null}

; Expected message count (2) differs from count in dispatched-list (1)
at org.apache.activemq.broker.region.PrefetchSubscription.assertAckMatchesDispatched(PrefetchSubscription.java:519)
at org.apache.activemq.broker.region.PrefetchSubscription.acknowledge(PrefetchSubscription.java:211)
at org.apache.activemq.broker.region.AbstractRegion.acknowledge(AbstractRegion.java:528)
at org.apache.activemq.broker.region.RegionBroker.acknowledge(RegionBroker.java:475)
(..)}}

I have prepared a [unit test](https://github.com/Anthchirp/activemq/commit/7df4f25975c6500e65b1688ca81e761c1825a32a) for this ([patch file](https://github.com/Anthchirp/activemq/commit/7df4f25975c6500e65b1688ca81e761c1825a32a.patch)).

The exception is not raised if the messages are acknowledged outside of a transaction

### Testcase

Reproduced version：5.15.0

Steps to reproduce：

1. Modify activemq.xml, add http support in transportConnectors.
2. Start an AMQ cluster.
3. Create connectionFactory, set queuePrefetch policy with prefetchSize 0.
4. Create a http connection, send a message to a queue.
5. Try to receive that message, but no message received.
