# Redit-Cassandra-15814

### Details

Title: ***order by descending on frozen list not working***

JIRA link：[https://issues.apache.org/jira/browse/CASSANDRA-15814](https://issues.apache.org/jira/browse/CASSANDRA-15814)

|         Label         |                  Value                   |      Label      |     Value      |
|:---------------------:|:----------------------------------------:|:---------------:|:--------------:|
|       **Type**        |                   Bug                    |  **Priority**   |     Normal     |
|      **Status**       |                 RESOLVED                 | **Resolution**  |     Fixed      |
|   **Since Version**   |                  2.1.3                  | **Fix Version/s** | 2.2.18, 3.0.22, 3.11.8, 4.0-beta2, 4.0 |

### Description

By creating a table like the following:

```
CREATE TABLE IF NOT EXISTS software (
 name ascii,
 version frozen<list<int>>,
 data ascii,
 PRIMARY KEY(name,version)
)
```

It works and version is ordered in an ascending order. But when trying to order in descending order:

```
CREATE TABLE IF NOT EXISTS software (
    name ascii,
    version frozen<list<int>>,
    data ascii,
    PRIMARY KEY(name,version)
) WITH CLUSTERING ORDER BY (version DESC);
```

The table is created normally, but when trying to insert a row:

```
insert into software(name, version) values ('t1', [2,10,30,40,50]); 
```

Cassandra throws an error:

```
InvalidRequest: Error from server: code=2200 [Invalid query] message="Invalid list literal for version of type frozen<list<int>>"
```

The goal here is that I would like to get the last version of a software.

### Testcase

Reproduced version：2.2.16

Steps to reproduce：
1. Create a cassandra cluster of 2 nodes
2. Create a client connection and create a key space
3. Create a table with a column of type frozen<list<int>> and WITH CLUSTERING ORDER BY (version DESC)
4. Insert a row with a list of integers
5. Check the console, following exceptions are thrown:

```
com.datastax.driver.core.exceptions.InvalidQueryException: Invalid list literal for version of type frozen<list<int>>
...
```
