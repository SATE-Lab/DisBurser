#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef CAS_13666
	system("cp ./buggy/CompositesSearcher.java ./apache-cassandra-3.11.3-src/src/java/org/apache/cassandra/index/internal/composites/CompositesSearcher.java");
	printf("inject CAS_13666...\n");
#else
	system("cp ./fixed/CompositesSearcher.java ./apache-cassandra-3.11.3-src/src/java/org/apache/cassandra/index/internal/composites/CompositesSearcher.java");
	printf("don't inject CAS_13666...\n");
#endif


#ifdef CAS_14242
	system("cp ./buggy/AbstractQueryPager.java ./apache-cassandra-3.11.3-src/src/java/org/apache/cassandra/service/pager/AbstractQueryPager.java");
	system("cp ./buggy/MultiPartitionPager.java ./apache-cassandra-3.11.3-src/src/java/org/apache/cassandra/service/pager/MultiPartitionPager.java");
	system("cp ./buggy/PartitionRangeQueryPager.java ./apache-cassandra-3.11.3-src/src/java/org/apache/cassandra/service/pager/PartitionRangeQueryPager.java");
	printf("inject CAS_14242...\n");
#else
	system("cp ./fixed/AbstractQueryPager.java ./apache-cassandra-3.11.3-src/src/java/org/apache/cassandra/service/pager/AbstractQueryPager.java");
	system("cp ./fixed/MultiPartitionPager.java ./apache-cassandra-3.11.3-src/src/java/org/apache/cassandra/service/pager/MultiPartitionPager.java");
	system("cp ./fixed/PartitionRangeQueryPager.java ./apache-cassandra-3.11.3-src/src/java/org/apache/cassandra/service/pager/PartitionRangeQueryPager.java");
	printf("don't inject CAS_14242...\n");
#endif


}
