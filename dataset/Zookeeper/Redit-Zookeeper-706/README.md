# Redit-Zookeeper-706

### Details

Title: ***large numbers of watches can cause session re-establishment to fail***

JIRA link：[https://issues.apache.org/jira/browse/ZOOKEEPER-706](https://issues.apache.org/jira/browse/ZOOKEEPER-706)

|         Label         |        Value        |       Label       |        Value        |
|:---------------------:|:-------------------:|:-----------------:|:-------------------:|
|       **Type**        |         Bug         |   **Priority**    |      Critical       |
|      **Status**       |       CLOSED        |  **Resolution**   |        Fixed        |
| **Affects Version/s** | 3.1.2, 3.2.2, 3.3.0 | **Fix Version/s** | 3.4.7, 3.5.2, 3.6.0 |

### Description

If a client sets a large number of watches the "set watches" operation during session re-establishment can fail.

for example:
WARN [NIOServerCxn.Factory:22801:NIOServerCnxn@417] - Exception causing close of session 0xe727001201a4ee7c due to java.io.IOException: Len error 4348380

in this case the client was a web monitoring app and had set both data and child watches on > 32k znodes.

there are two issues I see here we need to fix:

1) handle this case properly (split up the set watches into multiple calls I guess...)
2) the session should have expired after the "timeout". however we seem to consider any message from the client as re-setting the expiration on the server side. Probably we should only consider messages from the client that are sent during an established session, otherwise we can see this situation where the session is not established however the session is not expired either. Perhaps we should create another JIRA for this particular issue.

### Testcase

Reproduced version：3.5.0-alpha

Steps to reproduce：

1. Start a three-node zookeeper cluster and elect a leader.
2. Create two zk clients with a session timeout of 10 seconds.
3. Create a zk node with 10,000 children the first client.
4. Set a large number of watches on the node with the second client.
5. Kill the cluster and wait some time.
6. Start the cluster and wait for the session to reconnect.
7. Trigger the watches and ensure they properly propagate to the client.
