#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef CAS_12424
	system("cp ./buggy/ViewUpdateGenerator.java ./apache-cassandra-3.7-src/src/java/org/apache/cassandra/db/view/ViewUpdateGenerator.java");
	printf("inject CAS_12424...\n");
#else
	system("cp ./fixed/ViewUpdateGenerator.java ./apache-cassandra-3.7-src/src/java/org/apache/cassandra/db/view/ViewUpdateGenerator.java");
	printf("don't inject CAS_12424...\n");
#endif


}
