# Redit-Rocket-1409


### Details

Title: ***rocketmq tools queryMsgByKey may have bug！ #1409***

Github link：[https://github.com/apache/rocketmq/issues/1409](https://github.com/apache/rocketmq/issues/1409)

Fixed version: 4.7.0

### Description

when I close acl(aclEnable=false), execute the command:sh mqadmin queryMsgByKey -n 127.0.0.1:9876 -t topicH -k 12345678 ,I am able to get the correct result！
but I open the acl(aclEnable=true),execute the command:sh mqadmin queryMsgByKey -n 127.0.0.1:9876 -t topicH -k 12345678,the error:(Caused by: org.apache.rocketmq.client.exception.MQClientException: CODE: 208 DESC: query message by key finished, but no message.)

The same situation，when use the DefaultMQProducer queryMessage,the java code eg：
producer.queryMessage("topicH","12345678",60,System.currentTimeMillis()-10006010,System.currentTimeMillis())
close acl ，the result is ok!
but open acl,the error:（CODE: 208 DESC: query message by key finished, but no message.）
I don't know if I am using the error or the program has a bug.

thanks!

### Testcase

Reproduced version：4.5.0

Steps to reproduce：

1. Create a RocketMQ cluster with a master server, and a raft cluster with 3 dledger nodes.
2. Set AclEnable=true in brokers' configuration files.
3. Establish a producer connection with the cluster using acl authentication.
4. Send some messages to the cluster.
5. Try to query a message with a specific messageKeys.
6. Query failed with the following exception:

```
java.lang.RuntimeException: org.apache.rocketmq.client.exception.MQClientException: CODE: 208  DESC: query message by key finished, but no message.
For more information, please visit the url, http://rocketmq.apache.org/docs/faq/

	at io.redit.samples.rocketmqexample.SampleTest.lambda$testQueryMsgByKeyWithAcl$1(SampleTest.java:99)
	at io.redit.execution.RuntimeEngine.enforceOrder(RuntimeEngine.java:317)
	at io.redit.execution.RuntimeEngine.enforceOrder(RuntimeEngine.java:300)
	at io.redit.samples.rocketmqexample.SampleTest.testQueryMsgByKeyWithAcl(SampleTest.java:90)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
	at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:27)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:69)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater$1.execute(IdeaTestRunner.java:38)
	at com.intellij.rt.execution.junit.TestsRepeater.repeat(TestsRepeater.java:11)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:35)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:232)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:55)
Caused by: org.apache.rocketmq.client.exception.MQClientException: CODE: 208  DESC: query message by key finished, but no message.
For more information, please visit the url, http://rocketmq.apache.org/docs/faq/
	at org.apache.rocketmq.client.impl.MQAdminImpl.queryMessage(MQAdminImpl.java:413)
	at org.apache.rocketmq.client.impl.MQAdminImpl.queryMessage(MQAdminImpl.java:259)
	at org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl.queryMessage(DefaultMQProducerImpl.java:453)
	at org.apache.rocketmq.client.producer.DefaultMQProducer.queryMessage(DefaultMQProducer.java:668)
	at io.redit.samples.rocketmqexample.SampleTest.lambda$testQueryMsgByKeyWithAcl$1(SampleTest.java:93)
	... 29 more

```