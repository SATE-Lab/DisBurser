# Redit-HBASE-19850

### Details

Title: ***The calling of HTable.batch blocked at AsyncRequestFutureImpl.waitUntilDone caused by ArrayStoreException***

JIRA link：[https://issues.apache.org/jira/browse/HBASE-26027](https://issues.apache.org/jira/browse/HBASE-26027)

|         Label         |           Value            |      Label      | Value  |
|:---------------------:|:--------------------------:|:---------------:|:------:|
|       **Type**        |            Bug             |  **Priority**   | Major  |
|      **Status**       |          RESOLVED          | **Resolution**  | Fixed  |
| **Affects Version/s** | 2.2.7, 2.5.0, 2.3.5, 2.4.4 | **Component/s** | Client |
|  **Fix Version/s:**   |           2.5.0            |

### Description

Steps to reproduce are as follows:

1. Create a table

```
create "test", "cf"
```

2. Take a snapshot for the table

```
snapshot "test", "snap"
```

3. Load data to the table

```
(0...2000).each{|i| put "test", "row#{i}", "cf:col", "val"}
```

4. Split regions of the table

```
split "test"
```

5. Restore the table from the snapshot

```
disable "test"
restore_snapshot "snap"
enable "test"
```

 

The number of Offline Regions is as follows:

![img](https://issues.apache.org/jira/secure/attachment/12907331/12907331_The+number+of+Offline+Regions.png)

The number of Offline Regions should be zero.

It seems like when regions are removed by restoring a snapshot, the number of Offline Regions becomes wrong. And as far as I reviewed the code, it seems like the Offline Regions will not be cleaned up. After restarting Master, the offline regions disappear.

### Testcase

Reproduced version：2.2.2, 2.4.9

Prerequisite:

change /etc/hosts, add the following line:
```
10.2.0.7    server1
10.2.0.6    server2
10.2.0.13   server3
```

Steps to reproduce：

1. Start hbase cluster with 3 hdfs nn, 3 hdfs jn, 3 hdfs dn, 3 zk nodes, 3 hbase master and 3 hbase region server.
2. Create a table.
3. Take a snapshot for the table.
4. Insert some data to the table.
5. Split regions of the table.
6. Restore the table from the snapshot.
7. Open web console, check the number of offline regions, it should be zero.