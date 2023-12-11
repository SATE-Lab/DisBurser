# Redit-Zookeeper-2052

### Details

Title: ***Unable to delete a node when the node has no children***

JIRA link：[https://issues.apache.org/jira/browse/ZOOKEEPER-2052](https://issues.apache.org/jira/browse/ZOOKEEPER-2052)

|         Label         |    Value     |       Label       |        Value        |
|:---------------------:|:------------:|:-----------------:|:-------------------:|
|       **Type**        |     Bug      |   **Priority**    |        Major        |
|      **Status**       |   RESOLVED   |  **Resolution**   |        Fixed        |
| **Affects Version/s** | 3.4.6, 3.5.0 | **Fix Version/s** | 3.4.7, 3.5.1, 3.6.0 |

### Description

We stumbled upon a ZooKeeper bug where a node with no children cannot be removed on our 3 node ZooKeeper ensemble or standalone ZooKeeper on Red Hat Enterprise Linux x86_64 environment. Here is an example scenario/setup:

o Standalone ZooKeeper or 3 node ensemble (v3.4.6)
o 2 Java clients (v3.4.6)

- Client A creates a persistent node (e.g.: /metadata/resources)
- Client B creates ephemeral nodes under this persistent node

o Client A attempts to remove the /metadata/resources node via multi op
delete but fails since there are children
o Client B's session expired, all the ephemeral nodes are removed
o Client A attempts to recursively remove /metadata/resources node via
multi op, this is expected to succeed but got the following exception:
org.apache.zookeeper.KeeperException$NotEmptyException:
KeeperErrorCode = Directory not empty

(Note that Client B is the only client that creates these ephemeral nodes)

o After this, we use zkCli.sh to inspect the problematic node but the zkCli.sh shows the /metadata/resources node indeed have no children but it will not allow /metadata/resources node to get deleted. (shown below)

[zk: localhost:2181(CONNECTED) 0] ls /
[zookeeper, metadata]
[zk: localhost:2181(CONNECTED) 1] ls /metadata
[resources]
[zk: localhost:2181(CONNECTED) 2] get /metadata/resources
null
cZxid = 0x3
ctime = Wed Oct 01 22:04:11 PDT 2014
mZxid = 0x3
mtime = Wed Oct 01 22:04:11 PDT 2014
pZxid = 0x9
cversion = 2
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 0
[zk: localhost:2181(CONNECTED) 3] delete /metadata/resources
Node not empty: /metadata/resources
[zk: localhost:2181(CONNECTED) 4] get /metadata/resources
null
cZxid = 0x3
ctime = Wed Oct 01 22:04:11 PDT 2014
mZxid = 0x3
mtime = Wed Oct 01 22:04:11 PDT 2014
pZxid = 0x9
cversion = 2
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 0
numChildren = 0

o The only ways to remove this node is to either:
a) Restart the ZooKeeper server
b) set data to /metadata/resources then followed by a subsequent delete.

### Testcase

Reproduced version：3.5.0-alpha

Steps to reproduce：

1. Start a three-node zookeeper cluster and elect a leader.
2. Create client zk1 to connect to server1, and client zk2 to connect to server2.
3. Create a persistent node /metadata/resources on zk1.
4. Create some ephemeral nodes under /metadata/resources via zk2.
5. Delete /metadata/resources recursively via zk1 using multiOp, it will fail with NotEmptyException.
6. Close zk2 to delete ephemeral nodes under /metadata/resources.
7. Delete /metadata/resources recursively again using multiOp, it is expected to success, but it fails with NotEmptyException again.
