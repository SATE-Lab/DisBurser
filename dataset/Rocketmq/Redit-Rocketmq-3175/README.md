# Redit-Rocket-3175

### Details

Title: ***updateAclConfig cause broker fail to start #3175***

Github link：[https://github.com/apache/rocketmq/issues/3175](https://github.com/apache/rocketmq/issues/3175)

Fixed version: 4.9.1

### Description

**BUG REPORT**

1. Please describe the issue you observed:

- What did you do (The steps to reproduce)?
  `mqadmin  updateAclConfig -c RaftCluster -a aaaaaaaaaak `

and then restart broker

- What did you expect to see?
  restart success
- What did you see instead?
  broker start failed

```
java.lang.ClassCastException: java.lang.Class cannot be cast to org.apache.rocketmq.acl.AccessValidator
	at org.apache.rocketmq.broker.BrokerController.initialAcl(BrokerController.java:516)
	at org.apache.rocketmq.broker.BrokerController.initialize(BrokerController.java:483)
	at org.apache.rocketmq.broker.BrokerStartup.createBrokerController(BrokerStartup.java:222)
	at org.apache.rocketmq.broker.BrokerStartup.main(BrokerStartup.java:58)
```



1. Please tell us about your environment:
2. Other information (e.g. detailed explanation, logs, related issues, suggestions how to fix, etc):

thanks!

### Testcase

Reproduced version：4.9.0

Steps to reproduce：

1. Create a RocketMQ cluster with a master server, and a raft cluster with 3 dledger nodes.
2. Set AclEnable=true in brokers' configuration files.
3. Update acl config using `mqadmin  updateAclConfig`
4. Restart broker
5. Broker restart failed with the following exception:

```
java.lang.ClassCastException: java.lang.Class cannot be cast to org.apache.rocketmq.acl.AccessValidator
	at org.apache.rocketmq.broker.BrokerController.initialAcl(BrokerController.java:516)
	at org.apache.rocketmq.broker.BrokerController.initialize(BrokerController.java:483)
	at org.apache.rocketmq.broker.BrokerStartup.createBrokerController(BrokerStartup.java:222)
	at org.apache.rocketmq.broker.BrokerStartup.main(BrokerStartup.java:58)
```
