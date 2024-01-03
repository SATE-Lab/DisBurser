#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef KA_12257
	system("cp ./buggy/Metadata.java ./kafka-2.8.0-src/clients/src/main/java/org/apache/kafka/clients/Metadata.java");
	system("cp ./buggy/MetadataCache.java ./kafka-2.8.0-src/clients/src/main/java/org/apache/kafka/clients/MetadataCache.java");
	printf("inject KA_12257...\n");
#else
	system("cp ./fixed/Metadata.java ./kafka-2.8.0-src/clients/src/main/java/org/apache/kafka/clients/Metadata.java");
	system("cp ./fixed/MetadataCache.java ./kafka-2.8.0-src/clients/src/main/java/org/apache/kafka/clients/MetadataCache.java");
	printf("don't inject KA_12257...\n");
#endif


#ifdef KA_12866
	system("cp ./buggy/KafkaZkClient.scala ./kafka-2.8.0-src/core/src/main/scala/kafka/zk/KafkaZkClient.scala");
	printf("inject KA_12866...\n");
#else
	system("cp ./fixed/KafkaZkClient.scala ./kafka-2.8.0-src/core/src/main/scala/kafka/zk/KafkaZkClient.scala");
	printf("don't inject KA_12866...\n");
#endif


#ifdef KA_13310
	system("cp ./buggy/AbstractCoordinator.java ./kafka-2.8.0-src/clients/src/main/java/org/apache/kafka/clients/consumer/internals/AbstractCoordinator.java");
	system("cp ./buggy/ConsumerCoordinator.java ./kafka-2.8.0-src/clients/src/main/java/org/apache/kafka/clients/consumer/internals/ConsumerCoordinator.java");
	system("cp ./buggy/WorkerCoordinator.java ./kafka-2.8.0-src/connect/runtime/src/main/java/org/apache/kafka/connect/runtime/distributed/WorkerCoordinator.java");
	printf("inject KA_13310...\n");
#else
	system("cp ./fixed/AbstractCoordinator.java ./kafka-2.8.0-src/clients/src/main/java/org/apache/kafka/clients/consumer/internals/AbstractCoordinator.java");
	system("cp ./fixed/ConsumerCoordinator.java ./kafka-2.8.0-src/clients/src/main/java/org/apache/kafka/clients/consumer/internals/ConsumerCoordinator.java");
	system("cp ./fixed/WorkerCoordinator.java ./kafka-2.8.0-src/connect/runtime/src/main/java/org/apache/kafka/connect/runtime/distributed/WorkerCoordinator.java");
	printf("don't inject KA_13310...\n");
#endif

}
