# Redit-HDFS-15446

### Details

Title:
***CreateSnapshotOp fails during edit log loading for /.reserved/raw/path with error java.io.FileNotFoundException:
Directory does not exist: /.reserved/raw/path***

JIRA link：[https://issues.apache.org/jira/browse/HDFS-15446](https://issues.apache.org/jira/browse/HDFS-15446)

|         Label         |    Value     |       Label       |        Value        |
|:---------------------:|:------------:|:-----------------:|:-------------------:|
|       **Type**        |     Bug      |   **Priority**    |        Major        |
|      **Status**       |   RESOLVED   |  **Resolution**   |        Fixed        |
| **Affects Version/s** | 3.2.0, 3.3.0 | **Fix Version/s** | 3.2.2, 3.3.1, 3.4.0 |

### Description

After allowing snapshot creation for a path say /app-logs , when we try to create snapshot on
/.reserved/raw/app-logs , its successful with snapshot creation but later when Standby Namenode is restarted and tries to load the edit record OP_CREATE_SNAPSHOT , we see it failing and Standby Namenode shuts down with an exception "ava.io.FileNotFoundException: Directory does not exist: /.reserved/raw/app-logs" .

Here are the steps to reproduce :

```
# hdfs dfs -ls /.reserved/raw/
Found 15 items
drwxrwxrwt   - yarn   hadoop          0 2020-06-29 10:27 /.reserved/raw/app-logs
drwxr-xr-x   - hive   hadoop          0 2020-06-29 10:29 /.reserved/raw/prod
++++++++++++++
[root@c3230-node2 ~]# hdfs dfsadmin -allowSnapshot /app-logs
Allowing snapshot on /app-logs succeeded
[root@c3230-node2 ~]# hdfs dfsadmin -allowSnapshot /prod
Allowing snapshot on /prod succeeded
++++++++++++++
# hdfs lsSnapshottableDir
drwxrwxrwt 0 yarn hadoop 0 2020-06-29 10:27 1 65536 /app-logs
drwxr-xr-x 0 hive hadoop 0 2020-06-29 10:29 1 65536 /prod
++++++++++++++
[root@c3230-node2 ~]# hdfs dfs -createSnapshot /.reserved/raw/app-logs testSS
Created snapshot /.reserved/raw/app-logs/.snapshot/testSS
```

Exception we see in Standby namenode while loading the snapshot creation edit record.

```
2020-06-29 10:33:25,488 ERROR namenode.NameNode (NameNode.java:main(1715)) - Failed to start namenode.
java.io.FileNotFoundException: Directory does not exist: /.reserved/raw/app-logs
        at org.apache.hadoop.hdfs.server.namenode.INodeDirectory.valueOf(INodeDirectory.java:60)
        at org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotManager.getSnapshottableRoot(SnapshotManager.java:259)
        at org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotManager.createSnapshot(SnapshotManager.java:307)
        at org.apache.hadoop.hdfs.server.namenode.FSEditLogLoader.applyEditLogOp(FSEditLogLoader.java:772)
        at org.apache.hadoop.hdfs.server.namenode.FSEditLogLoader.loadEditRecords(FSEditLogLoader.java:257)
```

### Testcase

Reproduced version：3.2.0

Steps to reproduce：

1. Set dfs.ha.log-roll.period and dfs.ha.tail-edits.period to a smaller value so that we can trigger log roll and tail edit log in reasonable time.
2. Set up a cluster with 3 nn, 3 jn and 3 dn
3. Create a directory /test and allow snapshot on it.
4. Create a snapshot on /.reserved/raw/test.
5. Restart nn2.
6. Nn2 restart failed with following logs:

```
2023-11-20 14:40:29,025 ERROR org.apache.hadoop.hdfs.server.namenode.NameNode: Failed to start namenode.
java.io.FileNotFoundException: Directory does not exist: /.reserved/raw/test
    ...
    ...    
2023-11-20 14:40:29,026 INFO org.apache.hadoop.util.ExitUtil: Exiting with status 1: java.io.FileNotFoundException: Directory does not exist: /.reserved/raw/test
2023-11-20 14:40:29,027 INFO org.apache.hadoop.hdfs.server.namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at nn2/10.2.0.4
************************************************************/
```