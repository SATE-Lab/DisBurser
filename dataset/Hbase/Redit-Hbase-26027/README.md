# Redit-HBASE-26027

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

The batch api of HTable contains a param named results to store result or exception, its type is Object[].

If user pass an array with other type, eg: org.apache.hadoop.hbase.client.Result, and if we need to put an exception into it by some reason, then the ArrayStoreException will occur in AsyncRequestFutureImpl.updateResult, then the AsyncRequestFutureImpl.decActionCounter will be skipped, then in the AsyncRequestFutureImpl.waitUntilDone we will stuck at here checking the actionsInProgress again and again, forever.

It is better to add an cutoff calculated by operationTimeout, instead of only depend on the value of actionsInProgress.

BTW, this issue only for 2.x, since 3.x the implement has refactored.

How to reproduce:

1: add sleep in RSRpcServices.multi to mock slow response

```
try {
 Thread.sleep(2000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
```

2: set time out in config

```
conf.set("hbase.rpc.timeout","2000");
conf.set("hbase.client.operation.timeout","6000");
```

3: call batch api

```
Table table = HbaseUtil.getTable("test");
 byte[] cf = Bytes.toBytes("f");
 byte[] c = Bytes.toBytes("c1");
 List<Get> gets = new ArrayList<>();
 for (int i = 0; i < 10; i++) {
 byte[] rk = Bytes.toBytes("rk-" + i);
 Get get = new Get(rk);
 get.addColumn(cf, c);
 gets.add(get);
 }
 Result[] results = new Result[gets.size()];
 table.batch(gets, results);
```

The log will looks like below:

```
[ERROR] [2021/06/22 23:23:00,676] hconnection-0x6b927fb-shared-pool3-t1 - id=1 error for test processing localhost,16020,1624343786295
java.lang.ArrayStoreException: org.apache.hadoop.hbase.DoNotRetryIOException
	at org.apache.hadoop.hbase.client.AsyncRequestFutureImpl.updateResult(AsyncRequestFutureImpl.java:1242)
	at org.apache.hadoop.hbase.client.AsyncRequestFutureImpl.trySetResultSimple(AsyncRequestFutureImpl.java:1087)
	at org.apache.hadoop.hbase.client.AsyncRequestFutureImpl.setError(AsyncRequestFutureImpl.java:1021)
	at org.apache.hadoop.hbase.client.AsyncRequestFutureImpl.manageError(AsyncRequestFutureImpl.java:683)
	at org.apache.hadoop.hbase.client.AsyncRequestFutureImpl.receiveGlobalFailure(AsyncRequestFutureImpl.java:716)
	at org.apache.hadoop.hbase.client.AsyncRequestFutureImpl.access$1500(AsyncRequestFutureImpl.java:69)
	at org.apache.hadoop.hbase.client.AsyncRequestFutureImpl$SingleServerRequestRunnable.run(AsyncRequestFutureImpl.java:219)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:266)
	at java.util.concurrent.FutureTask.run(FutureTask.java)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
[INFO ] [2021/06/22 23:23:10,375] main - #1, waiting for 10  actions to finish on table: test
[INFO ] [2021/06/22 23:23:20,378] main - #1, waiting for 10  actions to finish on table: test
[INFO ] [2021/06/22 23:23:30,384] main - #1, waiting for 10  actions to finish on table: 
[INFO ] [2021/06/22 23:23:40,387] main - #1, waiting for 10  actions to finish on table: test
[INFO ] [2021/06/22 23:23:50,397] main - #1, waiting for 10  actions to finish on table: test
[INFO ] [2021/06/22 23:24:00,400] main - #1, waiting for 10  actions to finish on table: test
[INFO ] [2021/06/22 23:24:10,408] main - #1, waiting for 10  actions to finish on table: test
[INFO ] [2021/06/22 23:24:20,413] main - #1, waiting for 10  actions to finish on table: test
```

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

1. start hbase cluster with 3 hdfs nn, 3 hdfs jn, 3 hdfs dn, 3 zk nodes, a hbase master and 2 hbase rs.
2. Create a connection and add following config:
   - `hbase.client.operation.timeout = 6000`
   - `hbase.rpc.timeout = 2000`
3. Create table and insert some data
4. Apply network delay to both of the region servers.
5. Call batch api to read data.
6. Check the log of the client (shown in console), it will stuck at `AsyncRequestFutureImpl.waitUntilDone` forever rather than timeout.