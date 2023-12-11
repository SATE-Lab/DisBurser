# Redit-ActiveMQ-6059

### Details

Title: ***DLQ message lost after broker restarts
***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-6059](https://issues.apache.org/jira/browse/AMQ-6059)

|         Label         |  Value   |       Label       |     Value      |
|:---------------------:|:--------:|:-----------------:|:--------------:|
|       **Type**        |   Bug    |   **Priority**    |     Major      |
|      **Status**       | RESOLVED |  **Resolution**   |     Fixed      |
| **Affects Version/s** |  5.12.1  | **Fix Version/s** | 5.13.1, 5.14.0 |

### Description

**How to Reproduce**

1. Default ActiveMQ 5.12.1 package with the attached activemq.xml.
2. Send a message to a queue with expiration time one second.
3. After the expiry time, the message will be moved to DLQ. This can be monitored by the AMQ web console.
4. Restart AMQ. You will find that the message disappear from the AMQ web console. Try to consume the message from the DLQ, nothing is received.

**Cause Analysis**

1. KahaDB works well. It means the cause should be related to the leveldb mechanism.
2. In my understanding, the JMS message is persistent to a log file and leveldb will maintain to a reference to the position where the JMS message is stored in the log. After a message expires, AMQ will copy the expired JMS message and the reference to the posistion is also copied, then send to DLQ. When AMQ restarts, DLQ will recover the message from the persistent storage. Since the DLQ message shares the same reference to the JMS message data, the message in DLQ also has the expiration time that is the same as the original message. The expiry scanner will detect the DLQ message expires and remove it. That's why the message is lost after restart.
3. Actually, the message to DLQ is not completely the same as the origial message. Because the expiration time will be reset to 0 and some more message properties will be added. The DLQ message should not reuse the same reference to message data. For more details, please refer to org.apache.activemq.broker.region.RegionBroker.sendToDeadLetterQueue(ConnectionContext context, MessageReference node, Subscription subscription, Throwable poisonCause) method.

**Fixed Proposal**
In org.apache.activemq.broker.region.RegionBroker.sendToDeadLetterQueue(ConnectionContext context, MessageReference node, Subscription subscription, Throwable poisonCause) method, after the message is copied, set the dataLocator to null (message.getMessageId().setDataLocator(null) to force leveldb to save the new JMS message data and refer to a new position.

### Testcase

Reproduced version：5.12.0

Steps to reproduce：

1. Modify server config, 

```
2023-11-01 12:35:57,321 | ERROR | Could not accept connection : org.apache.activemq.transport.tcp.ExceededMaximumConnectionsException: Exceeded the maximum number of allowed client connections. See the 'maximumConnections' property on the TCP transport configuration URI in the ActiveMQ configuration file (e.g., activemq.xml) | org.apache.activemq.broker.TransportConnector | ActiveMQ Transport Server Thread Handler: amqp+ssl://0.0.0.0:5671?needClientAuth=true&maximumConnections=3
2023-11-01 12:35:57,927 | ERROR | Could not accept connection : org.apache.activemq.transport.tcp.ExceededMaximumConnectionsException: Exceeded the maximum number of allowed client connections. See the 'maximumConnections' property on the TCP transport configuration URI in the ActiveMQ configuration file (e.g., activemq.xml) | org.apache.activemq.broker.TransportConnector | ActiveMQ Transport Server Thread Handler: amqp+ssl://0.0.0.0:5671?needClientAuth=true&maximumConnections=3
```