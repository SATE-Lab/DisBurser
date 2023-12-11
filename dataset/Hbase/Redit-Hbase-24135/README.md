# Redit-HBASE-24135

### Details

Title: ***TableStateNotFoundException happends when table creation if rsgroup is enable***

JIRA link：[https://issues.apache.org/jira/browse/HBASE-26027](https://issues.apache.org/jira/browse/HBASE-26027)

|         Label         |    Value     |      Label      | Value  |
|:---------------------:|:------------:|:---------------:|:------:|
|       **Type**        |     Bug      |  **Priority**   | Minor  |
|      **Status**       |   RESOLVED   | **Resolution**  | Fixed  |
| **Affects Version/s** |    2.2.3     | **Component/s** | Client |
|  **Fix Version/s:**   | 2.3.0, 2.2.5 |

### Description

IF RS group is enabled then Unable to get table state error is thrown while creating a new table

Stpes:

1: Make sure RS group feature is enabled

2: Create a table say usertable2000

3: Check master log and observe below exception is thrown
org.apache.hadoop.hbase.master.TableStateManager$TableStateNotFoundException: usertable2000

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

1. Enable RSGroup feature in hbase-site.xml
2. Start hbase cluster with 3 hdfs nn, 3 hdfs jn, 3 hdfs dn, 3 zk nodes, a hbase master and 2 hbase rs.
3. Create a table and put some data.
4. Check hbase master log, it will throw TableStateManager$TableStateNotFoundException.