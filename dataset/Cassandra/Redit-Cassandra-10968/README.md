# Redit-Cassandra-10968

### Details

Title: ***order by descending on frozen list not working***

JIRA link：[https://issues.apache.org/jira/browse/CASSANDRA-10968](https://issues.apache.org/jira/browse/CASSANDRA-10968

|       Label       |   Value    |       Label       |                     Value                     |
|:-----------------:|:----------:|:-----------------:|:---------------------------------------------:|
|     **Type**      |    Bug     |   **Priority**    |                    Normal                     |
|    **Status**     |  RESOLVED  |  **Resolution**   |                     Fixed                     |
| **Since Version** | 0.7 beta 1 | **Fix Version/s** | 2.1.x, 2.2.17, 3.0.21, 3.11.7, 4.0-beta1, 4.0 |

### Description

xNoticed indeterminate behaviour when taking snapshot on column families that has secondary indexes setup. The created manifest.json created when doing snapshot, sometimes contains no file names at all and sometimes some file names.
I don't know if this post is related but that was the only thing I could find:
http://www.mail-archive.com/user%40cassandra.apache.org/msg42019.html

### Testcase

Reproduced version：2.2.16

Steps to reproduce：

1. Create a cassandra cluster of 2 nodes
2. Create a client connection and create a key space
3. Create table `x` and insert some data
4. Take a snapshot of table `x`
5. Check manifest.json file content in snapshot directory using `cat`, file content is shown below ,some content is
   missing.

```
{"files":["lb-1-big-Data.db"]}
```
