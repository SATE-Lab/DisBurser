# Redit-HDFS-14869

### Details

Title: ***Data loss in case of distcp using snapshot diff. Replication should include rename records if file was skipped in the previous iteration***

JIRA link：[https://issues.apache.org/jira/browse/HDFS-14869](https://issues.apache.org/jira/browse/HDFS-14869)

|         Label         |  Value   |       Label       | Value |
|:---------------------:|:--------:|:-----------------:|:-----:|
|       **Type**        |   Bug    |   **Priority**    | Major |
|      **Status**       | RESOLVED |  **Resolution**   | Fixed |
| **Affects Version/s** |   None   | **Fix Version/s** | 3.1.4 |

### Description

This issue arises when a directory or file is excluded by exclusion filter during distcp replication. Later on if the directory is renamed later to a name which is not excluded by the filter, the snapshot diff reports only a rename operation. The directory is never copied to target even though its not excluded now. This also doesn't throw any error so there is no way to find the issue. 

Steps to reproduce

- Create a directory in hdfs to copy using distcp.
- Include a staging folder in the directory.

```
[hdfs@ctr-e141-1563959304486-33995-01-000003 hadoop-mapreduce]$ hadoop fs -ls /tmp/tocopy
Found 4 items
-rw-r--r--   3 hdfs hdfs         16 2019-09-12 10:32 /tmp/tocopy/.b.txt
drwxr-xr-x   - hdfs hdfs          0 2019-09-23 09:18 /tmp/tocopy/.staging
-rw-r--r--   3 hdfs hdfs         12 2019-09-12 10:32 /tmp/tocopy/a.txt
-rw-r--r--   3 hdfs hdfs          4 2019-09-20 08:23 /tmp/tocopy/foo.txt
```

- The exclusion filter is set to exclude any staging directory

```
[hdfs@ctr-e141-1563959304486-33995-01-000003 hadoop-mapreduce]$ cat /tmp/filter
.*\.Trash.*
.*\.staging.*
```

- Do a copy using distcp snapshots, the staging directory is not replicated.

```
hadoop jar hadoop-distcp-3.3.0-SNAPSHOT.jar -Dmapreduce.job.user.classpath.first=true -filters /tmp/filter /tmp/tocopy/.snapshot/s1 /tmp/target

[hdfs@ctr-e141-1563959304486-33995-01-000003 root]$ hadoop fs -ls /tmp/target
Found 3 items
-rw-r--r--   3 hdfs hdfs         16 2019-09-24 06:56 /tmp/target/.b.txt
-rw-r--r--   3 hdfs hdfs         12 2019-09-24 06:56 /tmp/target/a.txt
-rw-r--r--   3 hdfs hdfs          4 2019-09-24 06:56 /tmp/target/foo.txt
```

- Rename the staging directory to final

```
[hdfs@ctr-e141-1563959304486-33995-01-000003 hadoop-mapreduce]$ hadoop fs -mv /tmp/tocopy/.staging /tmp/tocopy/final
```

- Do a copy using snapshot diff.

```
[hdfs@ctr-e141-1563959304486-33995-01-000003 hadoop-mapreduce]$ hdfs snapshotDiff /tmp/tocopy s1 s2[hdfs@ctr-e141-1563959304486-33995-01-000003 hadoop-mapreduce]$ hdfs snapshotDiff /tmp/tocopy s1 s2Difference between snapshot s1 and snapshot s2 under directory /tmp/tocopy:M .R ./.staging -> ./final
```

- The diff report just has a rename record and the new final directory is never copied.

```
[hdfs@ctr-e141-1563959304486-33995-01-000003 hadoop-mapreduce]$ hadoop jar hadoop-distcp-3.3.0-SNAPSHOT.jar -Dmapreduce.job.user.classpath.first=true -filters /tmp/filter -diff s1 s2 -update /tmp/tocopy /tmp/target
19/09/24 07:05:32 INFO tools.DistCp: Input Options: DistCpOptions{atomicCommit=false, syncFolder=true, deleteMissing=false, ignoreFailures=false, overwrite=false, append=false, useDiff=true, useRdiff=false, fromSnapshot=s1, toSnapshot=s2, skipCRC=false, blocking=true, numListstatusThreads=0, maxMaps=20, mapBandwidth=0.0, copyStrategy='uniformsize', preserveStatus=[BLOCKSIZE], atomicWorkPath=null, logPath=null, sourceFileListing=null, sourcePaths=[/tmp/tocopy], targetPath=/tmp/target, filtersFile='/tmp/filter', blocksPerChunk=0, copyBufferSize=8192, verboseLog=false, directWrite=false}, sourcePaths=[/tmp/tocopy], targetPathExists=true, preserveRawXattrsfalse
19/09/24 07:05:32 INFO client.RMProxy: Connecting to ResourceManager at ctr-e141-1563959304486-33995-01-000003.hwx.site/172.27.68.128:8050
19/09/24 07:05:33 INFO client.AHSProxy: Connecting to Application History server at ctr-e141-1563959304486-33995-01-000003.hwx.site/172.27.68.128:10200
19/09/24 07:05:33 INFO tools.DistCp: Number of paths in the copy list: 0
19/09/24 07:05:33 INFO client.RMProxy: Connecting to ResourceManager at ctr-e141-1563959304486-33995-01-000003.hwx.site/172.27.68.128:8050
19/09/24 07:05:33 INFO client.AHSProxy: Connecting to Application History server at ctr-e141-1563959304486-33995-01-000003.hwx.site/172.27.68.128:10200
19/09/24 07:05:33 INFO mapreduce.JobResourceUploader: Disabling Erasure Coding for path: /user/hdfs/.staging/job_1568647978682_0010
19/09/24 07:05:34 INFO mapreduce.JobSubmitter: number of splits:0
19/09/24 07:05:34 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1568647978682_0010
19/09/24 07:05:34 INFO mapreduce.JobSubmitter: Executing with tokens: []
19/09/24 07:05:34 INFO conf.Configuration: found resource resource-types.xml at file:/etc/hadoop/3.1.4.0-272/0/resource-types.xml
19/09/24 07:05:34 INFO impl.YarnClientImpl: Submitted application application_1568647978682_0010
19/09/24 07:05:34 INFO mapreduce.Job: The url to track the job: http://ctr-e141-1563959304486-33995-01-000003.hwx.site:8088/proxy/application_1568647978682_0010/
19/09/24 07:05:34 INFO tools.DistCp: DistCp job-id: job_1568647978682_0010
19/09/24 07:05:34 INFO mapreduce.Job: Running job: job_1568647978682_0010
19/09/24 07:05:40 INFO mapreduce.Job: Job job_1568647978682_0010 running in uber mode : false
19/09/24 07:05:40 INFO mapreduce.Job:  map 0% reduce 0%
19/09/24 07:09:43 INFO mapreduce.Job: Job job_1568647978682_0010 completed successfully19/09/24 07:09:43 INFO mapreduce.Job: Job job_1568647978682_0010 completed successfully19/09/24 07:09:43 INFO mapreduce.Job: Counters: 2 Job Counters Total time spent by all maps in occupied slots (ms)=0 Total time spent by all reduces in occupied slots (ms)=0 

[hdfs@ctr-e141-1563959304486-33995-01-000003 root]$ hadoop fs -ls /tmp/target
Found 3 items
-rw-r--r--   3 hdfs hdfs         16 2019-09-24 06:56 /tmp/target/.b.txt
-rw-r--r--   3 hdfs hdfs         12 2019-09-24 06:56 /tmp/target/a.txt
-rw-r--r--   3 hdfs hdfs          4 2019-09-24 06:56 /tmp/target/foo.txt
```

### Testcase

Reproduced version：3.1.2

Steps to reproduce：

1. Set up a cluster with 3 nn, 3 jn and 3 dn.
2. Create /source and /target directories in hdfs.
3. Create /source/.staging directory in hdfs.
4. Create a file to exclude /source/.staging when distcp.
5. Distcp /source to /target with filter
6. Snapshot /source
7. Change /source/.staging to /source/prod
8. Snapshot /source again
9. Distcp /source to /target with snapshot diff
10. Check distcp result, /source/prod should be copied to /target
