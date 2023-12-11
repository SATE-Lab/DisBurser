# Redit-HDFS-8950

### Details

Title: ***NameNode refresh doesn't remove DataNodes that are no longer in the allowed list***

JIRA link：[https://issues.apache.org/jira/browse/HDFS-8950](https://issues.apache.org/jira/browse/HDFS-8950)

|         Label         |  Value   |       Label       |           Value            |
|:---------------------:|:--------:|:-----------------:|:--------------------------:|
|       **Type**        |   Bug    |   **Priority**    |           Major            |
|      **Status**       | RESOLVED |  **Resolution**   |           Fixed            |
| **Affects Version/s** |  2.6.0   | **Fix Version/s** | 2.8.0, 2.7.2, 3.0.0-alpha1 |

### Description

If you remove a DN from NN's allowed host list (HDFS was HA) and then do NN refresh, it doesn't remove it actually and the NN UI keeps showing that node. It may try to allocate some blocks to that DN as well during an MR job. This issue is independent from DN decommission.

To reproduce:
1. Add a DN to dfs_hosts_allow
2. Refresh NN
3. Start DN. Now NN starts seeing DN.
4. Stop DN
5. Remove DN from dfs_hosts_allow
6. Refresh NN -> NN is still reporting DN as being used by HDFS.

This is different from decom because there DN is added to exclude list in addition to being removed from allowed list, and in that case everything works correctly.

### Testcase

Reproduced version：2.7.0

Steps to reproduce：

1. Create a hdfs cluster with an active namenode, a standby namenode, three journalnodes, and three datanodes.
2. Add all datanodes to dfs_hosts_allow, and refresh the namenode.
3. Run hdfs dfsadmin -report, log status of all datanodes
4. Stop one datanode, and remove it from dfs_hosts_allow, and refresh the namenode.
5. Run hdfs dfsadmin -report again, log status of all datanodes

The outputs of step 3 and step 5 are identical, which means the datanode is still in the cluster.

```
