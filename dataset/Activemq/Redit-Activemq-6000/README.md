# Redit-ActiveMQ-6000

### Details

Title: ***Pause/resume feature of ActiveMQ not resuming properly***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-6000](https://issues.apache.org/jira/browse/AMQ-6000)

|         Label         |     Value     |       Label       |     Value      |
|:---------------------:|:-------------:|:-----------------:|:--------------:|
|       **Type**        |      Bug      |   **Priority**    |     Major      |
|      **Status**       |   RESOLVED    |  **Resolution**   |     Fixed      |
| **Affects Version/s** |    5.12.0     | **Fix Version/s** | 5.13.0, 5.12.2 |

### Description

The problem is that, when you **resume** the message delivery,

1. If there is a message entering the queue: the broker will immediately send the pending messages to the consumer which is totally OK.
2. But if no message *enters* the queue: the pending messages in the queue will not be sent to the consumers until the expiration checking is performed on the queue (which by default is 30 seconds and can be controlled by the *expireMessagesPeriod* attribute) and non-expired messages will be sent to the consumers afterwards.

Obviously we can change the *expireMessagesPeriod* to limit this delay, but when you need a milisec precision, performing the expiration check every milisec will not make sense.
How is it possible to force the queue to start sending messages immediately after resumption?

### Testcase

Reproduced version：5.12.0

Steps to reproduce：

1. Modify activemq.xml, setuseCache="false" expireMessagesPeriod="0" in policeEntry
2. Start an AMQ cluster
3. Create a connection, send a message to a queue.
4. Pause the queue, create a consumer and resume the queue.
5. The pending messages in the queue will not be sent to the consumer.
6. If there is another message entering the queue after resume, the consumer can receive both of the messages.
