# Redit-HDFS-14499

### Details

Title: ***Misleading REM_QUOTA value with snapshot and trash feature enabled for a directory***

JIRA link：[https://issues.apache.org/jira/browse/HDFS-14499](https://issues.apache.org/jira/browse/HDFS-14499)

|         Label         |  Value   |       Label       |        Value        |
|:---------------------:|:--------:|:-----------------:|:-------------------:|
|       **Type**        |   Bug    |   **Priority**    |        Major        |
|      **Status**       | RESOLVED |  **Resolution**   |        Fixed        |
| **Affects Version/s** |   None   | **Fix Version/s** | 3.3.0, 3.1.4, 3.2.2 |

### Description

This is the flow of steps where we see a discrepancy between REM_QUOTA and new file operation failure. REM_QUOTA shows a value of 1 but file creation operation does not succeed.

```
hdfs@c3265-node3 root$ hdfs dfs -mkdir /dir1
hdfs@c3265-node3 root$ hdfs dfsadmin -setQuota 2 /dir1
hdfs@c3265-node3 root$ hdfs dfsadmin -allowSnapshot /dir1
Allowing snaphot on /dir1 succeeded
hdfs@c3265-node3 root$ hdfs dfs -touchz /dir1/file1
hdfs@c3265-node3 root$ hdfs dfs -createSnapshot /dir1 snap1
Created snapshot /dir1/.snapshot/snap1
hdfs@c3265-node3 root$ hdfs dfs -count -v -q /dir1
QUOTA REM_QUOTA SPACE_QUOTA REM_SPACE_QUOTA DIR_COUNT FILE_COUNT CONTENT_SIZE PATHNAME
2 0 none inf 1 1 0 /dir1
hdfs@c3265-node3 root$ hdfs dfs -rm /dir1/file1
19/03/26 11:20:25 INFO fs.TrashPolicyDefault: Moved: 'hdfs://smajetinn/dir1/file1' to trash at: hdfs://smajetinn/user/hdfs/.Trash/Current/dir1/file11553599225772
hdfs@c3265-node3 root$ hdfs dfs -count -v -q /dir1
QUOTA REM_QUOTA SPACE_QUOTA REM_SPACE_QUOTA DIR_COUNT FILE_COUNT CONTENT_SIZE PATHNAME
2 1 none inf 1 0 0 /dir1
hdfs@c3265-node3 root$ hdfs dfs -touchz /dir1/file1
touchz: The NameSpace quota (directories and files) of directory /dir1 is exceeded: quota=2 file count=3
```

The issue here, is that the count command takes only files and directories into account not the inode references. When trash is enabled, the deletion of files inside a directory actually does a rename operation as a result of which an inode reference is maintained in the deleted list of the snapshot diff which is taken into account while computing the namespace quota, but count command (getContentSummary()) ,just takes into account just the files and directories, not the referenced entity for calculating the REM_QUOTA. The referenced entity is taken into account for space quota only.

InodeReference.java:
\-------------------

```
 @Override
    public final ContentSummaryComputationContext computeContentSummary(
        int snapshotId, ContentSummaryComputationContext summary) {
      final int s = snapshotId < lastSnapshotId ? snapshotId : lastSnapshotId;
      // only count storagespace for WithName
      final QuotaCounts q = computeQuotaUsage(
          summary.getBlockStoragePolicySuite(), getStoragePolicyID(), false, s);
      summary.getCounts().addContent(Content.DISKSPACE, q.getStorageSpace());
      summary.getCounts().addTypeSpaces(q.getTypeSpaces());
      return summary;
    }
```

### Testcase

Reproduced version：3.2.0

Steps to reproduce：

1. Enable trash in core-site.xml
2. Create a hdfs cluster with 3 nn, 3 jn and 3 dn.
3. Create a directory and set quota to 2
4. Allow snapshot on the directory
5. Create a file inside the directory
6. Create a snapshot
7. Check the quota using -count command
8. Delete the file
9. Check the quota again
10. Create a file inside the directory
11. Create file failed while the REM_QUOTA in step 9 is 1