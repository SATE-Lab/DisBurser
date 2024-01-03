#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef KA_9254
	system("cp ./buggy/DynamicBrokerConfig.scala ./kafka-2.0.0-src/core/src/main/scala/kafka/server/DynamicBrokerConfig.scala");
	printf("inject KA_9254...\n");
#else
	system("cp ./fixed/DynamicBrokerConfig.scala ./kafka-2.0.0-src/core/src/main/scala/kafka/server/DynamicBrokerConfig.scala");
	printf("don't inject KA_9254...\n");
#endif


#ifdef KA_5098
	system("cp ./buggy/ProducerRecord.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/producer/ProducerRecord.java");
	system("cp ./buggy/Metadata.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/Metadata.java");
	system("cp ./buggy/KafkaConsumer.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/consumer/KafkaConsumer.java");
	system("cp ./buggy/ConsumerCoordinator.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/consumer/internals/ConsumerCoordinator.java");
	system("cp ./buggy/KafkaProducer.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/producer/KafkaProducer.java");
	system("cp ./buggy/Cluster.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/common/Cluster.java");
	system("cp ./buggy/InvalidTopicException.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/common/errors/InvalidTopicException.java");
	system("cp ./buggy/MetadataResponse.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/common/requests/MetadataResponse.java");
	printf("inject KA_5098...\n");
#else
	system("cp ./fixed/ProducerRecord.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/producer/ProducerRecord.java");
	system("cp ./fixed/Metadata.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/Metadata.java");
	system("cp ./fixed/KafkaConsumer.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/consumer/KafkaConsumer.java");
	system("cp ./fixed/ConsumerCoordinator.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/consumer/internals/ConsumerCoordinator.java");
	system("cp ./fixed/KafkaProducer.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/producer/KafkaProducer.java");
	system("cp ./fixed/Cluster.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/common/Cluster.java");
	system("cp ./fixed/InvalidTopicException.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/common/errors/InvalidTopicException.java");
	system("cp ./fixed/MetadataResponse.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/common/requests/MetadataResponse.java");
	printf("don't inject KA_5098...\n");
#endif


#ifdef KA_7496
	system("cp ./buggy/KafkaAdminClient.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/admin/KafkaAdminClient.java");
	printf("inject KA_7496...\n");
#else
	system("cp ./fixed/KafkaAdminClient.java ./kafka-2.0.0-src/clients/src/main/java/org/apache/kafka/clients/admin/KafkaAdminClient.java");
	printf("don't inject KA_7496...\n");
#endif

}
