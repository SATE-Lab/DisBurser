# DisBurser_Dataset

## Benchmark

*Benchmarks of distributed systems*

| Systems | Versions | Reference | Download URL |
| :----: | :----: | :----: | :----: |
|       Activemq      |       5.12.0, 5.14.0, 5.15.9        |          https://github.com/apache/activemq         | https://activemq.apache.org/download-archives |
|       Cassandra     |       2.2.16, 3.7, 3.11.3, 3.11.6        |          https://github.com/apache/cassandra        | https://archive.apache.org/dist/cassandra/        |
|       Elasticsearch |       1.2.1, 1.4.0, 1.4.5        |          https://github.com/elastic/elasticsearch   | https://github.com/elastic/elasticsearch/tags |
|       Hadoop        |       2.7.0, 3.1.2, 3.2.0, 3.3.0        |          https://github.com/apache/hadoop           | https://archive.apache.org/dist/hadoop/common/ |
|       Hbase         |       1.4.8, 2.2.0, 2.4.9, 2.4.11        |          https://github.com/apache/hbase            | https://archive.apache.org/dist/hbase/ |
|       Kafka         |       2.0.0, 2.8.0, 3.0.0, 3.2.0        |          https://github.com/apache/kafka            | https://kafka.apache.org/downloads |
|       Rocketmq      |       4.0.0, 4.1.0        |          https://github.com/apache/rocketmq         | https://rocketmq.apache.org/download |
|       Zookeeper     |       3.4.3, 3.5.0, 3.5.3, 3.5.5, 3.7.1, 3.8.0        |          https://github.com/apache/zookeeper        |https://github.com/apache/zookeeper/tags|



## Explanation

- Due to the large storage space occupied by the source code packages and binary packages of the distributed system, these packages in this anonymous project will be omitted and download URLs will be provided.

- We provide two implementation methods for simulating patch repairs in the benchmark test suite file inject.c in this anonymous project:

  1. Use the io stream to dynamically repair the buggy version file to obtain the fixed version file.

  2. Provide the fixed version file directly.

  In short, the first method is more in line with the patch repair process, and the second method is more convenient and simple.

- We involved some other system versions when building test cases, so some additional versions are included in the benchmark for use by test cases, which are temporarily out of the scope of benchmark and do not include test suites. I think these versions will be involved in the subsequent expansion of the benchmark.
