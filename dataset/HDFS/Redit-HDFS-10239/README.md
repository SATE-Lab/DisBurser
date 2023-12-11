# Redit-HDFS-10239

### Details

Title: ***Fsshell mv fails if port usage doesn't match in src and destination paths***

JIRA link：[https://issues.apache.org/jira/browse/HDFS-10239](https://issues.apache.org/jira/browse/HDFS-10239)

|         Label         |  Value   |       Label       |           Value            |
|:---------------------:|:--------:|:-----------------:|:--------------------------:|
|       **Type**        |   Bug    |   **Priority**    |           Major            |
|      **Status**       | RESOLVED |  **Resolution**   |           Fixed            |
| **Affects Version/s** |  2.7.2   | **Fix Version/s** | 2.8.0, 2.7.3, 3.0.0-alpha1 |

### Description

If one of the src or destination fs URIs does not contain the port while the other one does, the MoveCommands#processPath preemptively throws PathIOException "Does not match target filesystem".

eg.

```
-bash-4.1$ hadoop fs -mv hdfs://localhost:8020/tmp/foo3 hdfs://localhost/tmp/foo4
mv: `hdfs://localhost:8020:8020/tmp/foo3': Does not match target filesystem
```

This is due to strict string check in `processPath`

### Testcase

Reproduced version：2.7.0

Steps to reproduce：

1. Create a hdfs cluster with 2 namenodes, 3 journalnodes and 3 datanodes.
2. Create a file and write some data into it.
3. Move the file to another path with different port.
4. Move operation failed with error message "Does not match target filesystem".
