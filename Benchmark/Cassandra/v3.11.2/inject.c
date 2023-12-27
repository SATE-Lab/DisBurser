#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef CAS_13669
	system("cp ./buggy/SASIIndex.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/SASIIndex.java");
	system("cp ./buggy/AbstractAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/AbstractAnalyzer.java");
	system("cp ./buggy/NoOpAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/NoOpAnalyzer.java");
	system("cp ./buggy/NonTokenizingAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/NonTokenizingAnalyzer.java");
	system("cp ./buggy/StandardAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/StandardAnalyzer.java");
	system("cp ./buggy/IndexMode.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/conf/IndexMode.java");
	printf("inject CAS_13669...\n");
#else
	system("cp ./fixed/SASIIndex.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/SASIIndex.java");
	system("cp ./fixed/AbstractAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/AbstractAnalyzer.java");
	system("cp ./fixed/DelimiterAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/DelimiterAnalyzer.java");
	system("cp ./fixed/NoOpAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/NoOpAnalyzer.java");
	system("cp ./fixed/NonTokenizingAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/NonTokenizingAnalyzer.java");
	system("cp ./fixed/StandardAnalyzer.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/analyzer/StandardAnalyzer.java");
	system("cp ./fixed/IndexMode.java ./apache-cassandra-3.11.2-src/src/java/org/apache/cassandra/index/sasi/conf/IndexMode.java");
	printf("don't inject CAS_13669...\n");
#endif


}
