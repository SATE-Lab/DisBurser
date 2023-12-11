# Redit-ActiveMQ-6500

### Details

Title: ***Consuming problem with topics in ActiveMQ 5.14.1 with AMQP Qpid client***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-6500](https://issues.apache.org/jira/browse/AMQ-6500)

|         Label         |  Value   |       Label        |     Value      |
|:---------------------:|:--------:|:------------------:|:--------------:|
|       **Type**        |   Bug    |    **Priority**    |     Major      |
|      **Status**       | RESOLVED |   **Resolution**   |     Fixed      |
| **Affects Version/s** |  5.14.1  | **Fixe Version/s** | 5.15.0, 5.14.2 |

### Description

There could only be 500 topic messages be consumed with the AMQP Qpid client with ActiveMQ 5.14.1.

Specific settings :

- No prefetch sizes are set in a policyEntry of the activemq.xml.
- No prefetch sizes are set in the brokerURL in the client code.

We get the following situation (subscriber info) at the moment a subscriber
connects with the Qpid client.
The Inflight value becomes -1000 what is very strange.

Pending Queue Size | Inflight | Enqueued | Dequeued | Prefetch
0 | -1000 | 0 | 0 | 0

We get the following situation when one topic message is published.
The Inflight value increased with 2.

Pending Queue Size | Inflight | Enqueued | Dequeued | Prefetch
0 | -998 | 1 | 1 | 0

We get the following situation when 500 topic messages are published.
The Inflight value is now 0 and the 500 messages are consumed.

Pending Queue Size | Inflight | Enqueued | Dequeued | Prefetch
0 | 0 | 500 | 500 | 0

At the moment message 501 is published increases the "Pending Queue Size" and the messages "Enqueued" as well. The "Dequeued" messages remains 500 and message 501 can't be consumed by the subscriber !

Pending Queue Size | Inflight | Enqueued | Dequeued | Prefetch
1 | 0 | 501 | 500 | 0

Do somebody know what happens ? Do I something wrong or is it a real bug ?

Patrick

### Testcase

Reproduced version：5.14.1

Steps to reproduce：

1. Create an ConnectionFactory object using `org.apache.qpid.jms.JmsConnectionFactory` and set parameters.
2. Establish an amqp connection with the cluster.
3. Create a producer and a consumer
4. Send 2000 messages, compare the number of messages sent and received.