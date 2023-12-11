# Redit-HDFS-14504

### Details

Title: ***Rename with Snapshots does not honor quota limit***

JIRA link：[https://issues.apache.org/jira/browse/HDFS-14504](https://issues.apache.org/jira/browse/HDFS-14504)

|         Label         |  Value   |       Label       |    Value     |
|:---------------------:|:--------:|:-----------------:|:------------:|
|       **Type**        |   Bug    |   **Priority**    |    Major     |
|      **Status**       | RESOLVED |  **Resolution**   |    Fixed     |
| **Affects Version/s** |   None   | **Fix Version/s** | 3.3.1, 3.4.0 |

### Description

Steps to Reproduce:

\----------------------------

```
HW15685:bin sbanerjee$ ./hdfs dfs -mkdir /dir2

2019-05-21 15:08:41,615 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

HW15685:bin sbanerjee$ ./hdfs dfsadmin -setQuota 3 /dir2
2019-05-21 15:08:57,326 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

HW15685:bin sbanerjee$ ./hdfs dfsadmin -allowSnapshot /dir2
2019-05-21 15:09:47,239 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

Allowing snapshot on /dir2 succeeded

HW15685:bin sbanerjee$ ./hdfs dfs -touchz /dir2/file1
2019-05-21 15:10:01,573 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

HW15685:bin sbanerjee$ ./hdfs dfs -createSnapshot /dir2 snap1

2019-05-21 15:10:16,332 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

Created snapshot /dir2/.snapshot/snap1
HW15685:bin sbanerjee$ ./hdfs dfs -mv /dir2/file1 /dir2/file2

2019-05-21 15:10:49,292 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

HW15685:bin sbanerjee$ ./hdfs dfs -ls /dir2

2019-05-21 15:11:05,207 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

Found 1 items

-rw-r--r--   1 sbanerjee hadoop          0 2019-05-21 15:10 /dir2/file2

HW15685:bin sbanerjee$ ./hdfs dfs -touchz /dir2/filex

2019-05-21 15:11:43,765 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

touchz: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=4

HW15685:bin sbanerjee$ ./hdfs dfs -createSnapshot /dir2 snap2

2019-05-21 15:12:05,464 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

Created snapshot /dir2/.snapshot/snap2


HW15685:bin sbanerjee$ ./hdfs dfs -ls /dir2

2019-05-21 15:12:25,072 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

Found 1 items

-rw-r--r--   1 sbanerjee hadoop          0 2019-05-21 15:10 /dir2/file2

HW15685:bin sbanerjee$ ./hdfs dfs -mv /dir2/file2 /dir2/file3

2019-05-21 15:12:35,908 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

HW15685:bin sbanerjee$ ./hdfs dfs -touchz /dir2/filey

2019-05-21 15:12:49,998 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

touchz: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=5
```

// create operation fails here as it has already exceeded the quota limit

```
HW15685:bin sbanerjee$ ./hdfs dfs -createSnapshot /dir2 snap3

2019-05-21 15:13:07,656 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

Created snapshot /dir2/.snapshot/snap3

HW15685:bin sbanerjee$ ./hdfs dfs -mv /dir2/file3 /dir2/file4

2019-05-21 15:13:20,715 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
```

// Rename operation succeeds here adding on to the namespace quota

```
HW15685:bin sbanerjee$ ./hdfs dfs -touchz /dir2/filez

2019-05-21 15:13:30,486 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable

touchz: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=6
```

// File creation fails here but file count has been increased to 6, bcoz of the previous rename operation

The quota being set here is 3. Each successive rename adds an entry to the deleted list of the snapshot diff which gets
accounted in the namespace quota, but the rename operation is allowed even when it exceeds the quota limit with
snapshots. Once, an attempt is made to create a file, it fails.

### Testcase

Reproduced version：3.1.2

Steps to reproduce：

1. Create a hdfs cluster with 3 nn, 3 jn and 3 dn.
2. Create a directory and set quota limit to 3.
3. Allow snapshot on the directory.
4. Create a file in the directory.
5. Create a snapshot.
6. Rename the file.
7. Create another file in the directory.
8. Loop step 5-7 for 3 times.

The snapshot and rename operation successes, while the file creation fails, and the file count is increased to 6.

before patch:
```
2023-11-18 14:26:11,320 INFO org.apache.hadoop.ipc.Server: IPC Server handler 3 on 8020, call Call#2 Retry#0 org.apache.hadoop.hdfs.protocol.ClientProtocol.create from 10.2.0.2:52814: org.apache.hadoop.hdfs.protocol.NSQuotaExceededException: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=4
2023-11-18 14:26:18,715 INFO org.apache.hadoop.ipc.Server: IPC Server handler 28 on 8020, call Call#2 Retry#0 org.apache.hadoop.hdfs.protocol.ClientProtocol.create from 10.2.0.2:43944: org.apache.hadoop.hdfs.protocol.NSQuotaExceededException: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=5
2023-11-18 14:26:24,608 INFO org.apache.hadoop.ipc.Server: IPC Server handler 28 on 8020, call Call#2 Retry#0 org.apache.hadoop.hdfs.protocol.ClientProtocol.create from 10.2.0.2:55166: org.apache.hadoop.hdfs.protocol.NSQuotaExceededException: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=6
```

after patch:
```
2023-11-18 14:33:28,558 INFO org.apache.hadoop.ipc.Server: IPC Server handler 4 on default port 8020, call Call#2 Retry#0 org.apache.hadoop.hdfs.protocol.ClientProtocol.create from 10.2.0.2:55748: org.apache.hadoop.hdfs.protocol.NSQuotaExceededException: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=4
2023-11-18 14:33:34,136 INFO org.apache.hadoop.ipc.Server: IPC Server handler 38 on default port 8020, call Call#3 Retry#0 org.apache.hadoop.hdfs.protocol.ClientProtocol.rename from 10.2.0.2:41294: org.apache.hadoop.hdfs.protocol.NSQuotaExceededException: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=4
2023-11-18 14:33:35,992 INFO org.apache.hadoop.ipc.Server: IPC Server handler 26 on default port 8020, call Call#2 Retry#0 org.apache.hadoop.hdfs.protocol.ClientProtocol.create from 10.2.0.2:41304: org.apache.hadoop.hdfs.protocol.NSQuotaExceededException: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=4
2023-11-18 14:33:41,475 INFO org.apache.hadoop.ipc.Server: IPC Server handler 9 on default port 8020, call Call#2 Retry#0 org.apache.hadoop.hdfs.protocol.ClientProtocol.create from 10.2.0.2:41336: org.apache.hadoop.hdfs.protocol.NSQuotaExceededException: The NameSpace quota (directories and files) of directory /dir2 is exceeded: quota=3 file count=4
```