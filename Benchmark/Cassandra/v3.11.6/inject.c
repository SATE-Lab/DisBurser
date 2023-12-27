#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef CAS_13464
	system("cp ./buggy/WhereClause.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/WhereClause.java");
	system("cp ./buggy/CreateViewStatement.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/statements/CreateViewStatement.java");
	printf("inject CAS_13464...\n");
#else
	system("cp ./fixed/WhereClause.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/WhereClause.java");
	system("cp ./fixed/CreateViewStatement.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/statements/CreateViewStatement.java");
	printf("don't inject CAS_13464...\n");
#endif


#ifdef CAS_15297
	system("cp ./buggy/Snapshot.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/tools/nodetool/Snapshot.java");
	printf("inject CAS_15297...\n");
#else
	system("cp ./fixed/Snapshot.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/tools/nodetool/Snapshot.java");
	printf("don't inject CAS_15297...\n");
#endif


#ifdef CAS_16836
	system("cp ./buggy/FunctionName.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/functions/FunctionName.java");
	system("cp ./buggy/FunctionCall.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/functions/FunctionCall.java");
	printf("inject CAS_16836...\n");
#else
	system("cp ./fixed/FunctionName.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/functions/FunctionName.java");
	system("cp ./fixed/FunctionCall.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/functions/FunctionCall.java");
	printf("don't inject CAS_16836...\n");
#endif


#ifdef CAS_17628
	system("cp ./buggy/CQL.textile ./apache-cassandra-3.11.6-src/doc/cql3/CQL.textile");
	system("cp ./buggy/Selectable.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/selection/Selectable.java");
	printf("inject CAS_17628...\n");
#else
	system("cp ./fixed/CQL.textile ./apache-cassandra-3.11.6-src/doc/cql3/CQL.textile");
	system("cp ./fixed/Selectable.java ./apache-cassandra-3.11.6-src/src/java/org/apache/cassandra/cql3/selection/Selectable.java");
	printf("don't inject CAS_17628...\n");
#endif


}
