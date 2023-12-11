# Redit-ActiveMQ-6823

### Details

Title: ***No message received with prefetch 0 over http***

JIRA link：[https://issues.apache.org/jira/browse/AMQ-6823](https://issues.apache.org/jira/browse/AMQ-6823)

|         Label         |  Value   |       Label       |     Value      |
|:---------------------:|:--------:|:-----------------:|:--------------:|
|       **Type**        |   Bug    |   **Priority**    |     Major      |
|      **Status**       | RESOLVED |  **Resolution**   |     Fixed      |
| **Affects Version/s** |  5.14.5  | **Fix Version/s** | 5.15.1, 5.16.0 |

### Description

The http connector doesn't seem to work with prefetchSize 0.
It works with tcp connector.

AMQ configuration:

```
        <transportConnectors>
            <transportConnector name="openwire" uri="tcp://0.0.0.0:61616"/>
            <transportConnector name="http" uri="http://0.0.0.0:8080"/>
        </transportConnectors>
```

Code to reproduce:

```
	String brokerURL = "http://localhost:8080";
	//String brokerURL = "tcp://localhost:61616";
	ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
	ActiveMQPrefetchPolicy pp = new ActiveMQPrefetchPolicy();
	pp.setQueuePrefetch(0);
	cf.setPrefetchPolicy(pp);
	Connection con = cf.createConnection();
	con.start();
	Session s = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
	Destination d = s.createQueue("test");
	MessageProducer p = s.createProducer(d);
	MessageConsumer c = s.createConsumer(d);
	Message m = new ActiveMQTextMessage();
	m.setStringProperty("test", "test");
	p.send(m);
	c.receive();
	p.close();
	s.close();
	con.stop();
```

### Testcase

Reproduced version：5.15.0

Steps to reproduce：

1. Modify activemq.xml, add http support in transportConnectors.
2. Start an AMQ cluster.
3. Create connectionFactory, set queuePrefetch policy with prefetchSize 0.
4. Create a http connection, send a message to a queue.
5. Try to receive that message, but no message received.
