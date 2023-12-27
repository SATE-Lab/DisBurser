#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef KA_14303
	system("cp ./buggy/KafkaProducer.java ./kafka-3.3.1-src/clients/src/main/java/org/apache/kafka/clients/producer/KafkaProducer.java");
	system("cp ./buggy/BuiltInPartitioner.java ./kafka-3.3.1-src/clients/src/main/java/org/apache/kafka/clients/producer/internals/BuiltInPartitioner.java");
	printf("inject KA_14303...\n");
#else
	system("cp ./fixed/KafkaProducer.java ./kafka-3.3.1-src/clients/src/main/java/org/apache/kafka/clients/producer/KafkaProducer.java");
	system("cp ./fixed/BuiltInPartitioner.java ./kafka-3.3.1-src/clients/src/main/java/org/apache/kafka/clients/producer/internals/BuiltInPartitioner.java");
	printf("don't inject KA_14303...\n");
#endif


