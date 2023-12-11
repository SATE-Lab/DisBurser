# Redit-ActiveMQ-6847

### Details

Title: ***Immediate poison ACK after move from DLQ leads to message loss***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-6847](https://issues.apache.org/jira/browse/AMQ-6847)

|         Label         |     Value      |       Label        |         Value          |
|:---------------------:|:--------------:|:------------------:|:----------------------:|
|       **Type**        |      Bug       |    **Priority**    |         Major          |
|      **Status**       |    RESOLVED    |   **Resolution**   |         Fixed          |
| **Affects Version/s** | 5.14.5, 5.15.2 | **Fixe Version/s** | 5.14.6, 5.15.3, 5.16.0 |

### Description

**Setup**

- setMaximumRedeliveries(0)
- Throw RuntimeException (Poison ACK) in consumer directly after receiving message

**Scenario**

- Move message from DLQ to original input queue (either web console or JMX retryMessages)
- Processing of message fails again directly
- The message is considered to be still on the DLQ on the rejection and ActiveMQ logs "Not adding duplicate to DLQ"

Introducing a delay before throwing the exception in the client will get around the issue.

I initially noticed the issue when using an AMQP reject (Apache Qpid Proton) after which I reproduced similar conditions (instant poison ACK) it with the JMS client. The attached Java app will reproduce the issue on 5.14.5 and 5.15.2.

Might be related to [~~AMQ-5752~~](https://issues.apache.org/jira/browse/AMQ-5752).

2017-10-24 13:38:11,275 | DEBUG | Not adding duplicate to DLQ: ID:xxx-32848-1508845049112-6:1:1:1:1, dest: queue://TEST | org.apache.activemq.broker.region.policy.AbstractDeadLetterStrategy | ActiveMQ Transport: tcp:///127.0.0.1:36360@61616 

### Testcase

Reproduced version：5.14.0

Steps to reproduce：

1. Create an ConnectionFactory object and set parameters.
2. Establish a connection with the cluster, and set the maximum number of retries to 0.
3. Create a queue, attach a consumer to the queue.
4. In consumer's message callback, throw a RuntimeException whenever a message is received.
5. The producer sends a message, an exception is raised in the consumer, causing this message to enter the DLQ.
6. Retry the message in DLQ, and log EnqueueCount.
7. The retried message is expected to re-enter the DLQ (Dead Letter Queue), but it doesn't.


```
14:07:58.186 [main] INFO  i.r.s.activemqexample.SampleTest - check DLQ EnqueueCount
14:07:58.290 [main] INFO  i.r.s.activemqexample.SampleTest - DLQ EnqueueCount: 1
14:07:58.790 [main] INFO  i.r.s.activemqexample.SampleTest - retry DLQ message
14:07:59.402 [main] INFO  i.r.s.activemqexample.SampleTest - check DLQ EnqueueCount
14:07:59.494 [main] INFO  i.r.s.activemqexample.SampleTest - DLQ EnqueueCount: 1
14:07:59.994 [main] INFO  i.r.s.activemqexample.SampleTest - retry DLQ message
14:08:00.587 [main] INFO  i.r.s.activemqexample.SampleTest - check DLQ EnqueueCount
14:08:00.685 [main] INFO  i.r.s.activemqexample.SampleTest - DLQ EnqueueCount: 1
14:08:01.185 [main] INFO  i.r.s.activemqexample.SampleTest - retry DLQ message
14:08:01.778 [main] INFO  i.r.s.activemqexample.SampleTest - check DLQ EnqueueCount
14:08:01.873 [main] INFO  i.r.s.activemqexample.SampleTest - DLQ EnqueueCount: 1
14:08:02.373 [main] INFO  i.r.s.activemqexample.SampleTest - retry DLQ message
14:08:02.954 [main] INFO  i.r.s.activemqexample.SampleTest - check DLQ EnqueueCount
14:08:03.045 [main] INFO  i.r.s.activemqexample.SampleTest - DLQ EnqueueCount: 1
```