.. Redit documentation master file, created by
   sphinx-quickstart on Mon Nov 8 11:07:10 2021.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree`directive.

.. Replacements Definition


=======
RediB
=======

Introduction
============
RediB(Regression framework for Distributed system Benchmark) is an infrastructure or the deterministic reproduction of distributed system failures. RediB provides both a dataset of known distributed-systems bugs called RediD and a toolset called RediT.

Currently, node failure, network partition, network delay, network packet loss, and clock drift is supported. For a few supported languages, it is possible to enforce a specific order between
nodes in order to reproduce a specific time-sensitive scenario and inject failures before or after a specific method is
called when a specific stack trace is present. You can find full documentation in `here <https://www.javadoc.io/doc/io.github.martylinzy/redit/latest/index.html>`_ 

RediD
====================================
We applied RediT to 7 widely-used cloud systems. The following table shows the bugs reproduced by Redit in these systems.  

============================================================================== ===================================================================================== ==========================================================================================================================================================================
Bug ID                                                                         Bug Description                                                                       Testcase in RediD
============================================================================== ===================================================================================== ==========================================================================================================================================================================
`AMQ-6000  <https://issues.apache.org/jira/browse/AMQ-6000>`_                  Pause/resume feature of ActiveMQ not resuming properly                                `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6000>`_
`AMQ-6010  <https://issues.apache.org/jira/browse/AMQ-6010>`_                  AMQP SSL Transport "leaking" currentTransportCounts                                   `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6010>`_
`AMQ-6059  <https://issues.apache.org/jira/browse/AMQ-6059>`_                  DLQ message lost after broker restarts                                                `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6059>`_
`AMQ-6430  <https://issues.apache.org/jira/browse/AMQ-6430>`_                  noLocal=true in durable subscriptions is ignored after reconnect                      `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6430>`_
`AMQ-6697  <https://issues.apache.org/jira/browse/AMQ-6697>`_                  Aborting a STOMP 1.1 transaction after ACK/NACK leads to invalid state                `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6697>`_
`AMQ-6847  <https://issues.apache.org/jira/browse/AMQ-6847>`_                  Immediate poison ACK after move from DLQ leads to message loss                        `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6847>`_
`AMQ-6500  <https://issues.apache.org/jira/browse/AMQ-6500>`_                  Consuming problem with topics in ActiveMQ 5.14.1 with AMQP Qpid client                `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6500>`_
`AMQ-6796  <https://issues.apache.org/jira/browse/AMQ-6796>`_                  Acknowledging messages out of order in a STOMP 1.1 transaction raises exception       `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6796>`_
`AMQ-6823  <https://issues.apache.org/jira/browse/AMQ-6823>`_                  No message received with prefetch 0 over http                                         `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-6823>`_
`AMQ-7129  <https://issues.apache.org/jira/browse/AMQ-7129>`_                  Durable subscription messages can be orphaned when using individual ack mode          `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Activemq/Redit-Activemq-7129>`_
`CASSANDRA-10968  <https://issues.apache.org/jira/browse/CASSANDRA-10968>`_    When taking snapshot, manifest.json contains incorrect or no files                    `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-10968>`_
`CASSANDRA-15814  <https://issues.apache.org/jira/browse/CASSANDRA-15814>`_     order by descending on frozen list not working                                       `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-15814>`_
`CASSANDRA-14365  <https://issues.apache.org/jira/browse/CASSANDRA-14365>`_     Commit log replay failure for static columns with collections in clustering keys     `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-14365>`_
`CASSANDRA-12424  <https://issues.apache.org/jira/browse/CASSANDRA-12424>`_    Assertion failure in ViewUpdateGenerator                                              `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-12424>`_
`CASSANDRA-13666  <https://issues.apache.org/jira/browse/CASSANDRA-13666>`_    Secondary idx query on partition key cols not return partitions with only static data `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-13666>`_
`CASSANDRA-14242  <https://issues.apache.org/jira/browse/CASSANDRA-14242>`_    Indexed static column returns inconsistent results                                    `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-14242>`_
`CASSANDRA-13669  <https://issues.apache.org/jira/browse/CASSANDRA-13669>`_    Error when starting cassandra: Unable to make UUID from 'aa' (SASI index)             `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-13669>`_
`CASSANDRA-13464  <https://issues.apache.org/jira/browse/CASSANDRA-13464>`_    Failed to create Materialized view with a specific token range                        `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-13464>`_
`CASSANDRA-15297  <https://issues.apache.org/jira/browse/CASSANDRA-15297>`_    nodetool can not create snapshot with snapshot name that have special character       `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-15297>`_
`CASSANDRA-16836  <https://issues.apache.org/jira/browse/CASSANDRA-16836>`_    Materialized views incorrect quoting of UDF                                           `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-16836>`_
`CASSANDRA-17628  <https://issues.apache.org/jira/browse/CASSANDRA-17628>`_    CQL writetime and ttl functions should be forbidden for multicell columns             `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Cassandra/Redit-Cassandra-17628>`_
`HDFS-8950  <https://issues.apache.org/jira/browse/HDFS-8950>`_                NameNode refresh doesn't remove DataNodes that are no longer in the allowed list      `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-8950>`_
`HDFS-10239  <https://issues.apache.org/jira/browse/HDFS-10239>`_              Fsshell mv fails if port usage doesn't match in src and destination paths             `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-10239>`_
`HDFS-11379  <https://issues.apache.org/jira/browse/HDFS-11379>`_              DFSInputStream may infinite loop requesting block locations                           `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-11379>`_
`HDFS-14504  <https://issues.apache.org/jira/browse/HDFS-14504>`_              Rename with Snapshots does not honor quota limit                                      `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-14504>`_
`HDFS-14987  <https://issues.apache.org/jira/browse/HDFS-14987>`_              EC: EC file blockId location info displaying as "null" with hdfs fsck-blockId command `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-14987>`_
`HDFS-14869  <https://issues.apache.org/jira/browse/HDFS-14869>`_              Data loss in case of distcp using snapshot diff.                                      `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-14869>`_
`HDFS-14499  <https://issues.apache.org/jira/browse/HDFS-14499>`_              Misleading REM_QUOTA value with snapshot and trash feature enabled for a directory    `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-14499>`_
`HDFS-15446  <https://issues.apache.org/jira/browse/HDFS-15446>`_              CreateSnapshotOp fails during edit log loading                                        `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-15446>`_
`HDFS-15398  <https://issues.apache.org/jira/browse/HDFS-15398>`_              EC: hdfs client hangs due to exception during addBlock                                `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/HDFS/Redit-HDFS-15398>`_
`HBASE-19850  <https://issues.apache.org/jira/browse/HBASE-19850>`_            The number of Offline Regions is wrong after restoring a snapshot                     `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Hbase/Redit-Hbase-19850>`_
`HBASE-23682  <https://issues.apache.org/jira/browse/HBASE-23682>`_            Fix NPE when disable DeadServerMetricRegionChore                                      `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Hbase/Redit-Hbase-23682>`_
`HBASE-24189  <https://issues.apache.org/jira/browse/HBASE-24189>`_            WALSplit recreates region dirs for deleted table with recovered edits data            `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Hbase/Redit-Hbase-24189>`_
`HBASE-24135  <https://issues.apache.org/jira/browse/HBASE-24135>`_            TableStateNotFoundException happends when table creation if rsgroup is enable         `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Hbase/Redit-Hbase-24135>`_
`HBASE-26742  <https://issues.apache.org/jira/browse/HBASE-26742>`_            Comparator of NOT_EQUAL NULL is invalid for checkAndMutate                            `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Hbase/Redit-Hbase-26742>`_
`HBASE-26901  <https://issues.apache.org/jira/browse/HBASE-26901>`_            delete with null columnQualifier occurs NPE when NewVersionBehavior is on             `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Hbase/Redit-Hbase-26901>`_
`HBASE-26027  <https://issues.apache.org/jira/browse/HBASE-26027>`_            The calling of HTable.batch blocked caused by ArrayStoreException                     `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Hbase/Redit-Hbase-26027>`_
`KAFKA-9254  <https://issues.apache.org/jira/browse/KAFKA-9254>`_              Updating Broker configuration dynamically twice reverts log configuration to default  `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-9254>`_
`KAFKA-5098  <https://issues.apache.org/jira/browse/KAFKA-5098>`_              Producer.send() blocks and generates TimeoutException if topic name has illegal char  `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-5098>`_
`KAFKA-7496  <https://issues.apache.org/jira/browse/KAFKA-7496>`_              KafkaAdminClient#describeAcls should handle invalid filters gracefully                `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-7496>`_
`KAFKA-12257  <https://issues.apache.org/jira/browse/KAFKA-12257>`_            Consumer mishandles topics deleted and recreated with the same name                   `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-12257>`_
`KAFKA-12866  <https://issues.apache.org/jira/browse/KAFKA-12866>`_            Kafka requires ZK root access even when using a chroot                                `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-12866>`_
`KAFKA-13310  <https://issues.apache.org/jira/browse/KAFKA-13310>`_            KafkaConsumer cannot jump out of the poll method, and the consumer is blocked         `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-13310>`_
`KAFKA-13964  <https://issues.apache.org/jira/browse/KAFKA-13964>`_            kafka-configs.sh end with UVE when describing TLS user with quotas                    `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-13964>`_
`KAFKA-13488  <https://issues.apache.org/jira/browse/KAFKA-13488>`_            Producer fails to recover if topic gets deleted (and gets auto-created)               `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-13488>`_
`KAFKA-14303  <https://issues.apache.org/jira/browse/KAFKA-14303>`_            Producer.send without record key and batch.size=0 goes into infinite loop             `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/kafka/Redit-kafka-14303>`_
`ROCKETMQ-281  <https://issues.apache.org/jira/browse/ROCKETMQ-281>`_          add check for preventing repeat start mq                                              `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Rocketmq/Redit-Rocketmq-281>`_
`ROCKETMQ-231  <https://issues.apache.org/jira/browse/ROCKETMQ-231>`_          Pull result size is always less than given size in PullConsumer                       `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Rocketmq/Redit-Rocketmq-231>`_
`ROCKETMQ-255  <https://issues.apache.org/jira/browse/ROCKETMQ-255>`_          Offset store is null after consumer clients start()                                   `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Rocketmq/Redit-Rocketmq-255>`_
`ROCKETMQ-266  <https://issues.apache.org/jira/browse/ROCKETMQ-266>`_          Can't start consumer with a small “consumerThreadMax” number                          `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Rocketmq/Redit-Rocketmq-266>`_
`ROCKETMQ-1409  <https://github.com/apache/rocketmq/issues/1409>`_             rocketmq tools queryMsgByKey may have bug!                                            `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Rocketmq/Redit-Rocketmq-1409>`_
`ROCKETMQ-3175  <https://github.com/apache/rocketmq/issues/3175>`_             updateAclConfig cause broker fail to start                                            `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Rocketmq/Redit-Rocketmq-3175>`_
`ROCKETMQ-3281  <https://github.com/apache/rocketmq/issues/3281>`_             cannot delete topic/group perms in acl config                                         `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Rocketmq/Redit-Rocketmq-3281>`_
`ROCKETMQ-3556  <https://github.com/apache/rocketmq/issues/3556>`_             When broker is down, rocketmq client can not retry under Async send model             `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Rocketmq/Redit-Rocketmq-3556>`_
`ZOOKEEPER-706  <https://issues.apache.org/jira/browse/ZOOKEEPER-706>`_        large numbers of watches can cause session re-establishment to fail                   `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-706>`_
`ZOOKEEPER-1366  <https://issues.apache.org/jira/browse/ZOOKEEPER-1366>`_      Zookeeper should be tolerant of clock adjustments                                     `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-1366>`_
`ZOOKEEPER-2052  <https://issues.apache.org/jira/browse/ZOOKEEPER-2052>`_      Unable to delete a node when the node has no children                                 `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-2052>`_
`ZOOKEEPER-4466  <https://issues.apache.org/jira/browse/ZOOKEEPER-4466>`_      Support different watch modes on same path                                            `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-4466>`_ 
`ZOOKEEPER-4508  <https://issues.apache.org/jira/browse/ZOOKEEPER-4508>`_      ZooKeeper client run to endless loop in ClientCnxn.SendThread.run if all server down  `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-4508>`_
`ZOOKEEPER-4473  <https://issues.apache.org/jira/browse/ZOOKEEPER-4473>`_      zooInspector create root node fail with path validate                                 `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-4473>`_
`ZOOKEEPER-1367  <https://issues.apache.org/jira/browse/ZOOKEEPER-1367>`_      Data inconsistencies and unexpired ephemeral nodes after cluster restart              `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-1367>`_
`ZOOKEEPER-2355  <https://issues.apache.org/jira/browse/ZOOKEEPER-2355>`_      Ephemeral node is never deleted if follower fails while reading the proposal packet   `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-2355>`_
`ZOOKEEPER-3895  <https://issues.apache.org/jira/browse/ZOOKEEPER-3895>`_      Client side NullPointerException in case of empty Multi operation                     `Code <https://github.com/SSCT-Lab/RediB/tree/main/dataset/Zookeeper/Redit-Zookeeper-3895>`_
============================================================================== ===================================================================================== ==========================================================================================================================================================================




.. toctree::
    :caption: Table of Contents
    :maxdepth: 1
    :glob:

    pages/prereq
    pages/quickstart
    pages/deterministic
    pages/runseq
    pages/newnode
    pages/jvmservice
    pages/docker
    pages/changelog


