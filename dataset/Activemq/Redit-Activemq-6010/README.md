# Redit-ActiveMQ-6010

### Details

Title: ***AMQP SSL Transport "leaking" currentTransportCounts***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-6010](https://issues.apache.org/jira/browse/AMQ-6010)

|         Label         |     Value      |       Label       |     Value      |
|:---------------------:|:--------------:|:-----------------:|:--------------:|
|       **Type**        |      Bug       |   **Priority**    |     Major      |
|      **Status**       |    RESOLVED    |  **Resolution**   |     Fixed      |
| **Affects Version/s** | 5.11.1, 5.12.0 | **Fix Version/s** | 5.13.0, 5.12.2 |

### Description

When using the AMQP SSL transport the currentTransportCount (variable that tracks connection count in TcpTransportServer.java) can "leak" when the SSL connection is aborted during handshake. In this case the TcpTransportServer class the currentTransportCount is incremented in handleSocket but never decremented in stopped. This eventually leads to ExceededMaximumConnectionsException being thrown from handleSocket. The SSL connection is aborted during handshake if needClientAuth is configured on the transport and a client with an invalid certificate tries to connect.

**Reproduction**

1. Enable the AMQP SSL transport: `<transportConnector name="amqp+ssl" uri="amqp+ssl://0.0.0.0:5671?needClientAuth=true&maximumConnections=10"/>`
2. Try to connect with no/invalid client certificate: `openssl s_client -connect localhost:5671`
3. After 10 attempts ActiveMQ logs will start showing ExceededMaximumConnectionsException exceptions.

**Bug**
During the SSL handshake phase the protocol converter in the AMQP transport is set to the AMQPProtocolDiscriminator which silently swallows exceptions:

```
public void onAMQPException(IOException error) {
}
```

Which in turn cause the normal stop sequence (via asyncStop) to be skipped.

**Fix**
Change the AMQPProtocolDiscriminator to handle the error instead of swallow it:

```
public void onAMQPException(IOException error) {
    transport.sendToActiveMQ(error);
}
```

### Testcase

Reproduced version：5.12.0

Steps to reproduce：

1. Modify activemq.xml, set deadLetterStrategy and persistenceAdapter as described in the JIRA issue
2. Start an AMQ cluster
3. Create a connection, send a message to a queue with expiration time one second.
4. Wait for the message to expire and be moved to DLQ.
5. Restart AMQ. Try to consume the message from the DLQ, nothing is received.
