# Redit-Rocket-231

### Details

Title: ***Pull result size is always less than given size in PullConsumer***

JIRA link：[https://issues.apache.org/jira/browse/ROCKETMQ-231](https://issues.apache.org/jira/browse/ROCKETMQ-231)

|         Label         |               Value                |       Label       |  Value   |
|:---------------------:|:----------------------------------:|:-----------------:|:--------:|
|       **Type**        |                Bug                 |   **Priority**    | Critical |
|      **Status**       |              RESOLVED              |  **Resolution**   |  Fixed   |
| **Affects Version/s** | 4.0.0-incubating, 4.1.0-incubating | **Fix Version/s** |  4.2.0   |

### Description

When using PullConsumer pull message by default result size is 32,and messages is more than 32 in a queue,but broker always returns 31.

### Testcase

Reproduced version：4.1.0-incubating

Steps to reproduce：

1. Create a RocketMQ cluster, create a producer and send some messages to a topic.
2. Create a DefaultMQPullConsumer object, try to pull a certain number of messages from the topic.
3. Pull result size is smaller than expected, despite the fact that there are more messages in the topic.

