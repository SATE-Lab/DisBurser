# Redit-Zookeeper-1366

### Details

Title: ***Unable to delete a node when the node has no children***

JIRA link：[https://issues.apache.org/jira/browse/ZOOKEEPER-1366](https://issues.apache.org/jira/browse/ZOOKEEPER-1366)

|         Label         |  Value   |       Label       |    Value     |
|:---------------------:|:--------:|:-----------------:|:------------:|
|       **Type**        |   Bug    |   **Priority**    |   Critical   |
|      **Status**       | RESOLVED |  **Resolution**   |    Fixed     |
| **Affects Version/s** |   None   | **Fix Version/s** | 3.5.1, 3.6.0 |

### Description

If you want to wreak havoc on a ZK based system just do [date -s "+1hour"] and watch the mayhem as all sessions expire at once.

This shouldn't happen. Zookeeper could easily know handle elapsed times as elapsed times rather than as differences between absolute times. The absolute times are subject to adjustment when the clock is set while a timer is not subject to this problem. In Java, System.currentTimeMillis() gives you absolute time while System.nanoTime() gives you time based on a timer from an arbitrary epoch.

I have done this and have been running tests now for some tens of minutes with no failures. I will set up a test machine to redo the build again on Ubuntu and post a patch here for discussion.

### Testcase

Reproduced version：3.5.0-alpha

Steps to reproduce：

1. Start a three-node zookeeper cluster and elect a leader.
2. Create a zk client with a session timeout of 10 seconds.
3. Create a zk node with the client.
4. Apply clock drift on all zk servers using Redit, the clock drift is larger than the session timeout.
5. Try to create a zk node again using the same client.
6. Creation failed with SessionExpiredException.
