# Redit-Kafka-7496

### Details

Title: ***KafkaAdminClient#describeAcls should handle invalid filters gracefully***

JIRA link：[https://issues.apache.org/jira/browse/KAFKA-7496](https://issues.apache.org/jira/browse/KAFKA-7496)

|         Label         |  Value   |       Label       | Value |
|:---------------------:|:--------:|:-----------------:|:-----:|
|       **Type**        |   Bug    |   **Priority**    | Minor |
|      **Status**       | RESOLVED |  **Resolution**   | Fixed |
| **Affects Version/s** |   None   | **Fix Version/s** | 2.1.0 |

### Description

KafkaAdminClient#describeAcls should handle invalid filters gracefully. Specifically, it should return a future which
yields an exception.

The following code results in an uncaught IllegalArgumentException in the admin client thread, resulting in a zombie
admin client.

```
AclBindingFilter aclFilter = new AclBindingFilter(
    new ResourcePatternFilter(ResourceType.UNKNOWN, null, PatternType.ANY),
    AccessControlEntryFilter.ANY
);
kafkaAdminClient.describeAcls(aclFilter).values().get();
```

See the resulting stacktrace below

```
ERROR [kafka-admin-client-thread | adminclient-3] Uncaught exception in thread 'kafka-admin-client-thread | adminclient-3': (org.apache.kafka.common.utils.KafkaThread)
java.lang.IllegalArgumentException: Filter contain UNKNOWN elements
    at org.apache.kafka.common.requests.DescribeAclsRequest.validate(DescribeAclsRequest.java:140)
    at org.apache.kafka.common.requests.DescribeAclsRequest.<init>(DescribeAclsRequest.java:92)
    at org.apache.kafka.common.requests.DescribeAclsRequest$Builder.build(DescribeAclsRequest.java:77)
    at org.apache.kafka.common.requests.DescribeAclsRequest$Builder.build(DescribeAclsRequest.java:67)
    at org.apache.kafka.clients.NetworkClient.doSend(NetworkClient.java:450)
    at org.apache.kafka.clients.NetworkClient.send(NetworkClient.java:411)
    at org.apache.kafka.clients.admin.KafkaAdminClient$AdminClientRunnable.sendEligibleCalls(KafkaAdminClient.java:910)
    at org.apache.kafka.clients.admin.KafkaAdminClient$AdminClientRunnable.run(KafkaAdminClient.java:1107)
    at java.base/java.lang.Thread.run(Thread.java:844)
```

### Testcase

Reproduced version：2.12-2.0.0

Steps to reproduce：

1. Start zookeeper and kafka in a three-node cluster.
2. Create a kafkaAdminClient.
3. Create an invalid filter and call KafkaAdminClient#describeAcls.
4. KafkaAdminClient will hang forever.

Notice: change kafka-clients version to 2.0.0 in pom.xml to get the injected result, and change it back to 2.4.1 to get the fixed result.