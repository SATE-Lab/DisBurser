.. Redit documentation master file, created by
   sphinx-quickstart on Mon Nov 8 11:07:10 2021.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

.. Replacements Definition


=======
Redit
=======

Introduction
============

|projectName| is a test framework for end-to-end testing of distributed systems. It can be used to deterministically inject
failures during a normal test case execution. Currently, node failure, network partition, network delay, network packet loss, and clock drift is supported. For a few supported languages, it is possible to enforce a specific order between
nodes in order to reproduce a specific time-sensitive scenario and inject failures before or after a specific method is
called when a specific stack trace is present. You can find full documentation in `here <https://www.javadoc.io/doc/io.github.martylinzy/redit/latest/index.html>`_ 

Bugs Reproduced by Redit
====================================
We applied Redit to 8 widely-used cloud systems. The following table shows the bugs reproduced by Redit in these systems.  

============================================================================== ===================================================================================== ==========================================================================================================================================================================
Bug ID                                                                         Bug Description                                                                       Redit Code
============================================================================== ===================================================================================== ==========================================================================================================================================================================
`AMQ-7337 <https://issues.apache.org/jira/browse/AMQ-7337>`_                   Fail to re-establish message pull state                                               `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Activemq/Redit-Activemq-7337>`_
`AMQ-8050 <https://issues.apache.org/jira/browse/AMQ-8050>`_                   XAException when failing over in a transaction                                        `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Activemq/Redit-Activemq-8050>`_
`AMQ-8252 <https://issues.apache.org/jira/browse/AMQ-8252>`_                   Unnecessary stack trace in case of invalid credentials                                `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Activemq/Redit-Activemq-8252>`_
`CASSANDRA-12424 <https://issues.apache.org/jira/browse/CASSANDRA-12247>`_     Assertion failure in ViewUpdateGenerator                                              `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Cassandra/Redit-Cassandra-12424>`_
`CASSANDRA-13464 <https://issues.apache.org/jira/browse/CASSANDRA-13464>`_     Failed to create Materialized view with a specific token rangee                       `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Cassandra/Redit-Cassandra-13464>`_
`CASSANDRA-13669 <https://issues.apache.org/jira/browse/CASSANDRA-13669>`_     Error when starting cassandra                                                         `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Cassandra/Redit-Cassandra-13669>`_
`CASSANDRA-14365 <https://issues.apache.org/jira/browse/CASSANDRA-14365>`_     Commit log replay failure                                                             `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Cassandra/Redit-Cassandra-14365>`_
`CASSANDRA-15297 <https://issues.apache.org/jira/browse/CASSANDRA-15297>`_     nodetool can not create snapshot with snapshot name that have special character       `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Cassandra/Redit-Cassandra-15297>`_
`CASSANDRA-16836 <https://issues.apache.org/jira/browse/CASSANDRA-16836>`_     Materialized views incorrect quoting of UDF                                           `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Cassandra/Redit-Cassandra-16836>`_
`CASSANDRA-17628 <https://issues.apache.org/jira/browse/CASSANDRA-17628>`_     CQL writetime and ttl functions should be forbidden for multicell columns             `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Cassandra/Redit-Cassandra-17628>`_
`Elasticsearch-8321 <https://github.com/elastic/elasticsearch/pull/8321>`_     Cluster refuse to vote leader                                                         `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Elasticsearch/Redit-Elasticsearch-minimum-master-nodes>`_
`Elasticsearch-9051 <https://github.com/elastic/elasticsearch/pull/9051>`_     Cluster refuse to vote leader                                                         `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Elasticsearch/Redit-Elasticsearch-minimum-master-nodes>`_
`Elasticsearch-2488 <https://github.com/elastic/elasticsearch/pull/2488>`_     split-brain                                                                           `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Elasticsearch/Redit-Elasticsearch-2488>`_
`Elasticsearch-14671 <https://github.com/elastic/elasticsearch/issues/14671>`_ Network partition for stale replicas                                                  `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Elasticsearch/Redit-Elasticsearch-14671>`_
`Elasticsearch-19269 <https://github.com/elastic/elasticsearch/issues/19269>`_ version info conflict due to network partition                                        `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Elasticsearch/Redit-Elasticsearch-19269>`_
`HDFS-11379 <https://issues.apache.org/jira/browse/HDFS-11379>`_               DFSInputStream may infinite loop requesting block locations                           `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/HDFS/Redit-HDFS-11379>`_
`HDFS-13998 <https://issues.apache.org/jira/browse/HDFS-13998>`_               EC policy get failure                                                                 `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/HDFS/Redit-HDFS-13998>`_
`HDFS-14528 <https://issues.apache.org/jira/browse/HDFS-14528>`_               Failover from Active to Standby Failed                                                `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/HDFS/Redit-HDFS-14528>`_
`HDFS-14987 <https://issues.apache.org/jira/browse/HDFS-14987>`_               EC policy store state get error                                                       `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/HDFS/Redit-HDFS-14987>`_
`HDFS-15398 <https://issues.apache.org/jira/browse/HDFS-15398>`_               EC: hdfs client hangs due to exception during addBlock                                `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/HDFS/Redit-HDFS-15398>`_
`HDFS-15443 <https://issues.apache.org/jira/browse/HDFS-15443>`_               Set dfs.datanode.max.transfer.threads to a very small value can cause strange failure `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/HDFS/Redit-HDFS-15443>`_
`HDFS-16381 <https://issues.apache.org/jira/browse/HDFS-16381>`_               Lease related errors                                                                  `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/HDFS/Redit-HDFS-16381>`_
`HBASE-23682 <https://issues.apache.org/jira/browse/HBASE-23682>`_             HMaster cannot become active master                                                   `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Hbase/Redit-Hbase-23682>`_
`HBASE-26114 <https://issues.apache.org/jira/browse/HBASE-26114>`_             HMaster cannot start normally                                                         `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Hbase/Redit-Hbase-26114>`_
`HBASE-26742 <https://issues.apache.org/jira/browse/HBASE-26742>`_             delete with null columnQualifier occurs NullPointerException                          `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Hbase/Redit-Hbase-26742>`_
`HBASE-26901 <https://issues.apache.org/jira/browse/HBASE-26901>`_             delete with null columnQualifier occurs NullPointerException                          `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Hbase/Redit-Hbase-26901>`_
`KAFKA-12702 <https://issues.apache.org/jira/browse/KAFKA-12702>`_             NPE caused by host address lost in configure                                          `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Kafka/Redit-Kafka-12702>`_
`KAFKA-12866 <https://issues.apache.org/jira/browse/KAFKA-12866>`_             Root access Error                                                                     `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Kafka/Redit-Kafka-12866>`_
`KAFKA-13718 <https://issues.apache.org/jira/browse/KAFKA-13718>`_             show segment.bytes overridden config                                                  `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Kafka/Redit-Kafka-13718>`_
`KAFKA-13852 <https://issues.apache.org/jira/browse/KAFKA-13852>`_             Kafka Acl documentation bug for wildcard '*'                                          `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Kafka/Redit-Kafka-13852>`_
`KAFKA-13964 <https://issues.apache.org/jira/browse/KAFKA-13964>`_             kafka-configs.sh end with UnsupportedVersionException                                 `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Kafka/Redit-Kafka-13964>`_
`KAFKA-7192 <https://issues.apache.org/jira/browse/KAFKA-7192>`_               State-store can desynchronise with changelog                                          `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Kafka/Redit-Kafka-7192>`_
`ROCKETMQ-189 <https://issues.apache.org/jira/browse/ROCKETMQ-189>`_           Misleading message for timestamp exception                                            `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Rocketmq/Redit-Rocketmq-189>`_
`ROCKETMQ-255 <https://issues.apache.org/jira/browse/ROCKETMQ-255>`_           Consumer service crash                                                                `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Rocketmq/Redit-Rocketmq-255>`_
`ROCKETMQ-257 <https://issues.apache.org/jira/browse/ROCKETMQ-257>`_           Producer communication error                                                          `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Rocketmq/Redit-Rocketmq-257>`_
`ROCKETMQ-266 <https://issues.apache.org/jira/browse/ROCKETMQ-266>`_           Can not start consumer with a small “consumerThreadMax” number                        `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Rocketmq/Redit-Rocketmq-266>`_
`ZOOKEEPER-1367 <https://issues.apache.org/jira/browse/ZOOKEEPER-1367>`_       Data inconsistencies and unexpired ephemeral nodes after cluster restart              `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Zookeeper/Redit-Zookeeper-1367>`_
`ZOOKEEPER-2355 <https://issues.apache.org/jira/browse/ZOOKEEPER-2355>`_       Ephemeral node is never deleted if follower fails                                     `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Zookeeper/Redit-Zookeeper-2355>`_
`ZOOKEEPER-3479 <https://issues.apache.org/jira/browse/ZOOKEEPER-3479>`_       Logging false leader election times                                                   `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Zookeeper/Redit-Zookeeper-3479>`_
`ZOOKEEPER-4466 <https://issues.apache.org/jira/browse/ZOOKEEPER-4466>`_       Watchers of different modes interfere on overlapping pathes                           `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Zookeeper/Redit-Zookeeper-4466>`_
`ZOOKEEPER-4473 <https://issues.apache.org/jira/browse/ZOOKEEPER-4473>`_       zooInspector create root node fail with path validate                                 `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Zookeeper/Redit-Zookeeper-4473>`_
`ZOOKEEPER-4508 <https://issues.apache.org/jira/browse/ZOOKEEPER-4508>`_       Client endless loop                                                                   `Code <https://github.com/SATE-Lab/Redit_Bugs_Benchmark/tree/master/Redit-Bug-Testcase/Zookeeper/Redit-Zookeeper-4508>`_
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


