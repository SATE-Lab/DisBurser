# Redit-Cassandra-13666

### Details

Title: ***Secondary index query on partition key columns might not return partitions with only static data***

JIRA link：[https://issues.apache.org/jira/browse/CASSANDRA-13666](https://issues.apache.org/jira/browse/CASSANDRA-13666)

|       Label       |  Value   |       Label       |             Value              |
|:-----------------:|:--------:|:-----------------:|:------------------------------:|
|     **Type**      |   Bug    |   **Priority**    |             Normal             |
|    **Status**     | RESOLVED |  **Resolution**   |             Fixed              |
| **Since Version** |  3.0.0   | **Fix Version/s** | 3.0.21, 3.11.7, 4.0-beta1, 4.0 |

### Description

The problem can be reproduced with the following test in `3.0`:

```
   @Test
    public void testIndexOnPartitionKeyWithPartitionWithoutRows() throws Throwable
    {
        createTable("CREATE TABLE %s (pk1 int, pk2 int, c int, s int static, v int, PRIMARY KEY((pk1, pk2), c))");
        createIndex("CREATE INDEX ON %s (pk2)");

        execute("INSERT INTO %s (pk1, pk2, c, s, v) VALUES (?, ?, ?, ?, ?)", 1, 1, 1, 9, 1);
        execute("INSERT INTO %s (pk1, pk2, c, s, v) VALUES (?, ?, ?, ?, ?)", 1, 1, 2, 9, 2);
        execute("INSERT INTO %s (pk1, pk2, c, s, v) VALUES (?, ?, ?, ?, ?)", 3, 1, 1, 9, 1);
        execute("INSERT INTO %s (pk1, pk2, c, s, v) VALUES (?, ?, ?, ?, ?)", 4, 1, 1, 9, 1);
        flush();

        assertRows(execute("SELECT * FROM %s WHERE pk2 = ?", 1),
                   row(1, 1, 1, 9, 1),
                   row(1, 1, 2, 9, 2),
                   row(3, 1, 1, 9, 1),
                   row(4, 1, 1, 9, 1));

        execute("DELETE FROM %s WHERE pk1 = ? AND pk2 = ? AND c = ?", 3, 1, 1);

        assertRows(execute("SELECT * FROM %s WHERE pk2 = ?", 1),
                   row(1, 1, 1, 9, 1),
                   row(1, 1, 2, 9, 2),
                   row(3, 1, null, 9, null),  // This row will not be returned
                   row(4, 1, 1, 9, 1));
    }
```

The problem seems to be that the index entries for the static data are inserted with an empty clustering key. When the first `SELECT` is executed those entries are removed by `CompositesSearcher::filterStaleEntries` which consider that those entries are stales. When the second `SELECT` is executed the index ignore the (3, 1) partition as there is not entry for it anymore.

### Testcase

Reproduced version：3.11.3

Steps to reproduce：

1. Create a client connection cluster, create a key space and a table.
2. Create an index and insert data.
3. Execute the first `SELECT` statement.
4. Delete a row and execute the second `SELECT` statement.
5. `row(3, 1, null, 9, null)` is not returned

```
----- first select: -----
Row[4, 1, 1, 9, 1]
Row[1, 1, 1, 9, 1]
Row[1, 1, 2, 9, 2]
Row[3, 1, 1, 9, 1]
----- second select after deletion: ------
Row[4, 1, 1, 9, 1]
Row[1, 1, 1, 9, 1]
Row[1, 1, 2, 9, 2]
```

