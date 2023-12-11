# Redit-Rocket-281

### Details

Title: ***add check for preventing repeat start mq***

JIRA link：[https://issues.apache.org/jira/browse/ROCKETMQ-281](https://issues.apache.org/jira/browse/ROCKETMQ-281)

|         Label         |               Value                |       Label       | Value |
|:---------------------:|:----------------------------------:|:-----------------:|:-----:|
|       **Type**        |            Improvement             |   **Priority**    | Major |
|      **Status**       |               CLOSED               |  **Resolution**   | Fixed |
| **Affects Version/s** | 4.0.0-incubating, 4.1.0-incubating | **Fix Version/s** | 4.2.0 |

### Description

add check for preventing repeat start mq

### Testcase

Reproduced version：4.1.0-incubating

Steps to reproduce：

1. Create a RocketMQ cluster with a master server, a master broker and a slave broker.
2. Restart the master broker.
3. The master starts trying to again with the following error message:

```
java.net.BindException: Address already in use
	at sun.nio.ch.Net.bind0(Native Method)
	at sun.nio.ch.Net.bind(Net.java:433)
	at sun.nio.ch.Net.bind(Net.java:425)
	at sun.nio.ch.ServerSocketChannelImpl.bind(ServerSocketChannelImpl.java:220)
	at sun.nio.ch.ServerSocketAdaptor.bind(ServerSocketAdaptor.java:85)
	at sun.nio.ch.ServerSocketAdaptor.bind(ServerSocketAdaptor.java:78)
	at org.apache.rocketmq.store.ha.HAService$AcceptSocketService.beginAccept(HAService.java:180)
	at org.apache.rocketmq.store.ha.HAService.start(HAService.java:112)
	at org.apache.rocketmq.store.DefaultMessageStore.start(DefaultMessageStore.java:208)
	at org.apache.rocketmq.broker.BrokerController.start(BrokerController.java:609)
	at org.apache.rocketmq.broker.BrokerStartup.start(BrokerStartup.java:57)
	at org.apache.rocketmq.broker.BrokerStartup.main(BrokerStartup.java:52)
```

after the patch, following exception will be thrown to prevent repeat start of mq:

```
java.lang.RuntimeException: Lock failed,MQ already started
	at org.apache.rocketmq.store.DefaultMessageStore.start(DefaultMessageStore.java:214)
	at org.apache.rocketmq.broker.BrokerController.start(BrokerController.java:654)
	at org.apache.rocketmq.broker.BrokerStartup.start(BrokerStartup.java:62)
	at org.apache.rocketmq.broker.BrokerStartup.main(BrokerStartup.java:56)
```