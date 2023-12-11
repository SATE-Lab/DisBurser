# Redit-HBASE-24189

### Details

Title: ***WALSplit recreates region dirs for deleted table with recovered edits data***

JIRA link：[https://issues.apache.org/jira/browse/HBASE-24189](https://issues.apache.org/jira/browse/HBASE-24189)

|         Label         |               Value                |      Label      |       Value       |
|:---------------------:|:----------------------------------:|:---------------:|:-----------------:|
|       **Type**        |                Bug                 |  **Priority**   |       Major       |
|      **Status**       |              RESOLVED              | **Resolution**  |       Fixed       |
| **Affects Version/s** |               2.2.4                | **Component/s** | regionserver, wal |
|  **Fix Version/s:**   | 3.0.0-alpha-1, 2.3.0, 1.7.0, 2.2.6 |

### Description

Under the following scenario region directories in HDFS can be recreated with only recovered.edits in them:

1. Create table "test"
2. Put into "test"
3. Delete table "test"
4. Create table "test" again
5. Crash the regionserver to which the put has went to force the WAL replay
6. Region directory in old table is recreated in new table
7. hbase hbck returns inconsistency

This appears to happen due to the fact that WALs are not cleaned up once a table is deleted and they still contain the edits from old table. I've tried wal_roll command on the regionserver before crashing it, but it doesn't seem to help as under some circumstances there are still WAL files around. The only solution that works consistently is to restart regionserver before creating the table at step 4 because that triggers log cleanup on startup: https://github.com/apache/hbase/blob/f3ee9b8aa37dd30d34ff54cd39fb9b4b6d22e683/hbase-procedure/src/main/java/org/apache/hadoop/hbase/procedure2/store/wal/WALProcedureStore.java#L508

Truncating a table also would be a workaround by in our case it's a no-go as we create and delete tables in our tests which run back to back (create table in the beginning of the test and delete in the end of the test).

A nice option in our case would be to provide hbase shell utility to force clean up of log files manually as I realize that it's not really viable to clean all of those up every time some table is removed.
### Testcase

Reproduced version：2.2.2

Prerequisite:

change /etc/hosts, add the following line:

```
10.2.0.7    server1
10.2.0.6    server2
10.2.0.13   server3
```

Steps to reproduce：

1. Set zookeeper.session.timeout to 20000 in hbase-site.xml so that hbase master could detect the rs crash in time.
2. Start hbase cluster with 3 hdfs nn, 3 hdfs jn, 3 hdfs dn, 3 zk nodes, a hbase master and 2 hbase rs.
3. Create a table named "test" and write some data.
4. Delete table "test".
5. Kill the rs which has the region of "test" table.
6. Wait until hbase master detect the rs crash.
7. Wait until WalSplit procedure finished.
8. Check the region dir of "test" table, it should be empty.