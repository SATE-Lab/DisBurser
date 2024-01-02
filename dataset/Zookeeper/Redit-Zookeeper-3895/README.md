# Redit-Zookeeper-3895

### Details

Title: ***Client side NullPointerException in case of empty Multi operation***

JIRA link：[https://issues.apache.org/jira/browse/ZOOKEEPER-3895](https://issues.apache.org/jira/browse/ZOOKEEPER-3895)

|         Label         | Value  |       Label       |    Value     |
|:---------------------:|:------:|:-----------------:|:------------:|
|       **Type**        |  Bug   |   **Priority**    |   Blocker    |
|      **Status**       | CLOSED |  **Resolution**   |    Fixed     |
| **Affects Version/s** | 3.6.1  | **Fix Version/s** | 3.7.0, 3.6.2 |

### Description

I saw this error in an application that uses Apache BookKeeper DistributedLog library.

This is a regression on 3.6.x release series

The bug is in ZooKeeper Java Client.

```
Caused by: java.lang.NullPointerExceptionCaused by: java.lang.NullPointerException at 
org.apache.zookeeper.ZooKeeper.multiInternal(ZooKeeper.java:2105) at 
org.apache.zookeeper.ZooKeeper.multi(ZooKeeper.java:2050) at 
org.apache.bookkeeper.zookeeper.ZooKeeperClient.access$1101(ZooKeeperClient.java:70) at 
org.apache.bookkeeper.zookeeper.ZooKeeperClient$3.zkRun(ZooKeeperClient.java:498) at 
org.apache.bookkeeper.zookeeper.ZooKeeperClient$ZkRetryRunnable.run(ZooKeeperClient.java:389) at 
org.apache.bookkeeper.zookeeper.ZooKeeperClient.multi(ZooKeeperClient.java:510) at 
org.apache.distributedlog.zk.ZKTransaction.execute(ZKTransaction.java:67) at 
org.apache.distributedlog.BKLogWriteHandler.setLogSegmentTruncationStatus(BKLogWriteHandler.java:1223) at 
org.apache.distributedlog.BKLogWriteHandler.setLogSegmentsOlderThanDLSNTruncated(BKLogWriteHandler.java:1117) at 
org.apache.distributedlog.BKLogWriteHandler.lambda$setLogSegmentsOlderThanDLSNTruncated$0(BKLogWriteHandler.java:1083) at 
java.base/java.util.concurrent.CompletableFuture.uniComposeStage(CompletableFuture.java:1183) at 
java.base/java.util.concurrent.CompletableFuture.thenCompose(CompletableFuture.java:2299) at 
org.apache.distributedlog.BKLogWriteHandler.setLogSegmentsOlderThanDLSNTruncated(BKLogWriteHandler.java:1082) at 
org.apache.distributedlog.BKAsyncLogWriter.truncate(BKAsyncLogWriter.java:449)
```

### Testcase

Reproduced version：3.6.0

Steps to reproduce：

1. Start a three-node zookeeper cluster and elect a leader.
2. Create client zk1 to connect to the zookeeper cluster.
3. Operate multi operation on zk1 with empty operations list.

Notice: you may need to change zookeeper version in pom.xml to get the injected/ fixed result