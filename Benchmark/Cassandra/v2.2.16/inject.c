#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef CAS_10968
	system("cp ./buggy/ColumnFamilyStore.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/db/ColumnFamilyStore.java");
	printf("inject CAS_10968...\n");
#else
	system("cp ./fixed/ColumnFamilyStore.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/db/ColumnFamilyStore.java");
	printf("don't inject CAS_10968...\n");
#endif


#ifdef CAS_14365
	system("cp ./buggy/MapSerializer.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/serializers/MapSerializer.java");
	system("cp ./buggy/SetSerializer.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/serializers/SetSerializer.java");
	printf("inject CAS_14365...\n");
#else
	system("cp ./fixed/MapSerializer.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/serializers/MapSerializer.java");
	system("cp ./fixed/SetSerializer.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/serializers/SetSerializer.java");
	printf("don't inject CAS_14365...\n");
#endif


#ifdef CAS_15814
	system("cp ./buggy/Lists.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/cql3/Lists.java");
	system("cp ./buggy/Maps.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/cql3/Maps.java");
	system("cp ./buggy/Sets.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/cql3/Sets.java");
	printf("inject CAS_15814...\n");
#else
	system("cp ./fixed/Lists.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/cql3/Lists.java");
	system("cp ./fixed/Maps.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/cql3/Maps.java");
	system("cp ./fixed/Sets.java ./apache-cassandra-2.2.16-src/src/java/org/apache/cassandra/cql3/Sets.java");
	printf("don't inject CAS_15814...\n");
#endif


}
