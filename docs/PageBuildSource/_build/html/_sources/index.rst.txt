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
called when a specific stack trace is present.

Bugs Reproduced by Redit
====================================
We applied Redit to 19 versions of 8 widely-used cloud systems including Cassandra-3.11.6, HDFS-3.3.0, HDFS-3.1.2, ZK-3.7.0,
and HBASE-2.4.12. The following table shows the bugs reproduced by Redit in these systems.

============================================================================== ======================================================== ==========================================================================================================================
Bug ID                                                                         Bug Description                                          Redit Code
============================================================================== ======================================================== ==========================================================================================================================
`HBASE-26114 <https://issues.apache.org/jira/browse/HBASE-26114>`_             HMaster cannot start normally                            `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Hbase-26114>`_
`HBASE-23682 <https://issues.apache.org/jira/browse/HBASE-23682>`_             HMaster cannot become active master                      `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Hbase-23682>`_
`HBASE-21487 <https://issues.apache.org/jira/browse/HBASE-21487>`_             Concurrent modify table ops lead to unexpected results   `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Hbase-21487>`_
`CASSANDRA-16836 <https://issues.apache.org/jira/browse/CASSANDRA-16836>`_     Materialized views incorrect quoting of UDF              `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Cassandra-16836>`_
`CASSANDRA-14365 <https://issues.apache.org/jira/browse/CASSANDRA-14365>`_     Commit log replay failure                                `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Cassandra-14365>`_
`CASSANDRA-13669 <https://issues.apache.org/jira/browse/CASSANDRA-13669>`_     Error when starting cassandra                            `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Cassandra-13669>`_
`KAFKA-13563 <https://issues.apache.org/jira/browse/KAFKA-13563>`_             Coordinator never get found                              `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Kafka-13563>`_
`KAFKA-12866 <https://issues.apache.org/jira/browse/KAFKA-12866>`_             Root access Error                                        `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Kafka-12866>`_
`KAFKA-7192 <https://issues.apache.org/jira/browse/KAFKA-7192>`_               State-store can desynchronise with changelog             `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Kafka-7192>`_
`KAFKA-12702 <https://issues.apache.org/jira/browse/KAFKA-12702>`_             NPE caused by host address lost in configure             `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Kafka-12702>`_
`HDFS-13998 <https://issues.apache.org/jira/browse/HDFS-13998>`_               EC policy get failure                                    `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-HDFS-13998>`_
`HDFS-14987 <https://issues.apache.org/jira/browse/HDFS-14987>`_               EC policy store state get error                          `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-HDFS-14987>`_
`HDFS-16381 <https://issues.apache.org/jira/browse/HDFS-16381>`_               Lease related errors                                     `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-HDFS-16381>`_
`ROCKETMQ-255 <https://issues.apache.org/jira/browse/ROCKETMQ-255>`_           Consumer service crash                                   `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Rocketmq-255>`_
`ROCKETMQ-257 <https://issues.apache.org/jira/browse/ROCKETMQ-257>`_           Producer communication error                             `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Rocketmq-257>`_
`ROCKETMQ-189 <https://issues.apache.org/jira/browse/ROCKETMQ-189>`_           Misleading message for timestamp exception               `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Rocketmq-189>`_
`ZOOKEEPER-4508 <https://issues.apache.org/jira/browse/ZOOKEEPER-4508>`_       Client endless loop                                      `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Zookeeper-4508>`_
`ZOOKEEPER-2355 <https://issues.apache.org/jira/browse/ZOOKEEPER-2355>`_       Ephemeral node is never deleted if follower fails        `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Zookeeper-2355>`_
`ZOOKEEPER-3479 <https://issues.apache.org/jira/browse/ZOOKEEPER-3479>`_       Logging false leader election times                      `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Zookeeper-3479>`_
`AMQ-8050 <https://issues.apache.org/jira/browse/AMQ-8050>`_                   XAException when failing over in a transaction           `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Activemq-8050>`_
`AMQ-7337 <https://issues.apache.org/jira/browse/AMQ-7337>`_                   Fail to re-establish message pull state                  `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Activemq-7337>`_
`AMQ-6430 <https://issues.apache.org/jira/browse/AMQ-6430>`_                   Failure after client reconnection                        `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Activemq-6430>`_
`Elasticsearch-8321 <https://github.com/elastic/elasticsearch/pull/8321>`_     Cluster refuse to vote leader                            `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Elasticsearch-minimum-master-nodes>`_
`Elasticsearch-9051 <https://github.com/elastic/elasticsearch/pull/9051>`_     Cluster refuse to vote leader                            `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Elasticsearch-minimum-master-nodes>`_
`Elasticsearch-2488 <https://github.com/elastic/elasticsearch/pull/2488>`_     split-brain                                              `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Elasticsearch-2488>`_
`Elasticsearch-19269 <https://github.com/elastic/elasticsearch/issues/19269>`_ version info conflict due to network partition           `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Elasticsearch-19269>`_
`Elasticsearch-14671 <https://github.com/elastic/elasticsearch/issues/14671>`_ Network partition for stale replicas                     `Code <https://github.com/SATE-Lab/Redit-Benchmark/tree/main/Special-bug/Redit-Elasticsearch-14671>`_
============================================================================== ======================================================== ==========================================================================================================================




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


