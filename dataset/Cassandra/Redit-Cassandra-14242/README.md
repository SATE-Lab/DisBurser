# Redit-Cassandra-14242

### Details

Title: ***Secondary index query on partition key columns might not return partitions with only static data***

JIRA link：[https://issues.apache.org/jira/browse/CASSANDRA-14242](https://issues.apache.org/jira/browse/CASSANDRA-14242)

|       Label       |  Value   |       Label       |             Value              |
|:-----------------:|:--------:|:-----------------:|:------------------------------:|
|     **Type**      |   Bug    |   **Priority**    |             Normal             |
|    **Status**     | RESOLVED |  **Resolution**   |             Fixed              |
| **Since Version** |  3.0.0   | **Fix Version/s** | 3.0.21, 3.11.7, 4.0-beta1, 4.0 |

### Description

I am using Cassandra 3.11.2, and the Java driver 3.4.0

I have a table that has a static column, where the static column has a secondary index.
When querying the table I get incomplete or duplicated results, depending on the fetch size.

e.g.

```
CREATE KEYSPACE hack WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
CREATE TABLE hack.stuff (id int, kind text, chunk int static, val1 int, PRIMARY KEY (id, kind));
CREATE INDEX stuff_chunk_index ON hack.stuff (chunk);
```

– repeat with thousands of values for id =>

```
  INSERT INTO hack.stuff (id, chunk, kind, val1 ) VALUES (${id}, 777, 'A', 123);
```

Querying from Java:

```
    final SimpleStatement statement = new SimpleStatement("SELECT id, kind, val1 FROM hack.stuff WHERE chunk = " + chunk); 
    statement.setFetchSize(fetchSize);
    statement.setConsistencyLevel(ConsistencyLevel.ALL);
    final ResultSet resultSet = connection.getSession().execute(statement);
    for (Row row : resultSet) {
        final int id = row.getInt("id");
    }
```

**The number of results returned depends on the fetch-size.**

e.g. For 30k values inserted, I get the following:

| fetch-size | result-size |
| :--------: | :---------: |
|   40000    |    30000    |
|   20000    |    30001    |
|    5000    |    30006    |
|    100     |    30303    |

In production, I have a much larger table where the correct result size for a specific chunk is 20019, but some fetch sizes will return *significantly fewer* results.

| fetch-size | result-size |                                           |
| :--------: | :---------: | :---------------------------------------: |
|   25000    |    20019    |                                           |
|    5000    |    9999     | **<== this one is has far fewer results** |
|    5001    |    20026    |                                           |

(so far been unable to reproduce this with the simpler test table)

Thanks,
Ross

### Testcase

Reproduced version：3.11.3

Steps to reproduce：

1. Create a client connection cluster, create a key space and a table.
2. Create an index on static row.
3. Insert 100 rows into the table.
4. Execute the following query statement with page sizes of 10, 100, and 1000, respectively.
   `SELECT * FROM ks.t WHERE s = 2;`
5. Check the number of rows returned by the query statement, some row count is incorrect.

```
18:32:13.065 [main] INFO  i.r.s.cassandra14242.SampleTest - -------query by page size 10--------
18:32:13.438 [main] INFO  i.r.s.cassandra14242.SampleTest - PageSize: 10, count: 111
18:32:13.439 [main] INFO  i.r.s.cassandra14242.SampleTest - -------query by page size 100--------
18:32:13.500 [main] INFO  i.r.s.cassandra14242.SampleTest - PageSize: 100, count: 101
18:32:13.500 [main] INFO  i.r.s.cassandra14242.SampleTest - -------query by page size 1000--------
18:32:13.531 [main] INFO  i.r.s.cassandra14242.SampleTest - PageSize: 1000, count: 100
```
