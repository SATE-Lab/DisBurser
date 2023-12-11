# Redit-ActiveMQ-6697

### Details

Title: ***Aborting a STOMP 1.1 transaction after ACK/NACK leads to invalid state***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-6697](https://issues.apache.org/jira/browse/AMQ-6697)

|         Label         |  Value   |       Label        |     Value      |
|:---------------------:|:--------:|:------------------:|:--------------:|
|       **Type**        |   Bug    |    **Priority**    |     Major      |
|      **Status**       | RESOLVED |   **Resolution**   |     Fixed      |
| **Affects Version/s** |  5.14.5  | **Fixe Version/s** | 5.15.0, 5.14.6 |

### Description

Reproducing the problem:

- Receive a message via STOMP (EDIT: on a subscription with ack:client-individual)
- Start a transaction
- ACK ~~(or NACK)~~ the message within the transaction
- Abort the transaction
- ACK (or NACK) the message

Expected behaviour:

- The message is according to step #5 either ACKed or NACKed.

Observed behaviour:

- The message is neither ACKed nor NACKed, but stays in unacknowledged state
- An exception is raised:
  org.apache.activemq.transport.stomp.ProtocolException: Unexpected ACK received for message-id [ID:(...)]
  at org.apache.activemq.transport.stomp.ProtocolConverter.onStompAck(ProtocolConverter.java:475)
  at org.apache.activemq.transport.stomp.ProtocolConverter.onStompCommand(ProtocolConverter.java:250)
  at org.apache.activemq.transport.stomp.StompTransportFilter.onCommand(StompTransportFilter.java:85)
  at org.apache.activemq.transport.TransportSupport.doConsume(TransportSupport.java:83)
  at org.apache.activemq.transport.tcp.TcpTransport.doRun(TcpTransport.java:233)
  at org.apache.activemq.transport.tcp.TcpTransport.run(TcpTransport.java:215)
  at java.lang.Thread.run(Thread.java:745)
- an ERROR message is sent to the client

As far as I can tell this is caused by code in both onStompAck() and onStompNack():
https://git-wip-us.apache.org/repos/asf?p=activemq.git;a=blob;f=activemq-stomp/src/main/java/org/apache/activemq/transport/stomp/ProtocolConverter.java;h=b25860bf6895240c33a8643b6fcc731af126d32e;hb=refs/heads/master#l440

With a STOMP 1.1 ACK/NACK, ackId == null, so the message entry is taken out of this.pedingAcks (sic!).
When the transaction is aborted the message entry is then not put back into this.pedingAcks, so any subsequent ACK/NACK will find pendingAck==null, therefore acked==false, raising an exception.

### Testcase

Reproduced version：5.14.0

Steps to reproduce：

1. Create an ConnectionFactory object and set parameters.
2. Establish a connection with the cluster, create a producer, send a message to a queue, and close the producer.
3. Create a STOMP connection
4. Receive a message via STOMP on a subscription with ack:client-individual
5. Start a transaction
6. ACK the message within the transaction
7. Abort the transaction
8. ACK the message
9. The result is that:
    - message failed to get asked
    - The following exception is raised
    - an ERROR message is sent to the client

```
org.apache.activemq.transport.stomp.ProtocolException: Unexpected ACK received for message-id [ID:zdc-pc-linux-38907-1698836475975-1:1:1:1:1]
	at org.apache.activemq.transport.stomp.ProtocolConverter.onStompAck(ProtocolConverter.java:475)
	at org.apache.activemq.transport.stomp.ProtocolConverter.onStompCommand(ProtocolConverter.java:250)
	at org.apache.activemq.transport.stomp.StompTransportFilter.onCommand(StompTransportFilter.java:85)
	at org.apache.activemq.transport.TransportSupport.doConsume(TransportSupport.java:83)
	at org.apache.activemq.transport.tcp.TcpTransport.doRun(TcpTransport.java:233)
	at org.apache.activemq.transport.tcp.TcpTransport.run(TcpTransport.java:215)
	at java.lang.Thread.run(Thread.java:748)
```