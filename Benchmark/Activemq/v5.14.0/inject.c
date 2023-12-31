#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef AMQ_6430
	system("cp ./buggy/Topic.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/broker/region/Topic.java");
	system("cp ./buggy/TopicRegion.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/broker/region/TopicRegion.java");
	system("cp ./buggy/NoLocalSubscriptionAware.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/store/NoLocalSubscriptionAware.java");
	system("cp ./buggy/MemoryPersistenceAdapter.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/store/memory/MemoryPersistenceAdapter.java");
	system("cp ./buggy/KahaDBPersistenceAdapter.java ./activemq-parent-5.14.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/KahaDBPersistenceAdapter.java");
	system("cp ./buggy/KahaDBStore.java ./activemq-parent-5.14.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/KahaDBStore.java");
	system("cp ./buggy/MultiKahaDBPersistenceAdapter.java ./activemq-parent-5.14.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/MultiKahaDBPersistenceAdapter.java");
	printf("inject AMQ_6430...\n");
#else
	system("cp ./fixed/Topic.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/broker/region/Topic.java");
	system("cp ./fixed/TopicRegion.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/broker/region/TopicRegion.java");
	system("cp ./fixed/NoLocalSubscriptionAware.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/store/NoLocalSubscriptionAware.java");
	system("cp ./fixed/MemoryPersistenceAdapter.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/store/memory/MemoryPersistenceAdapter.java");
	system("cp ./fixed/KahaDBPersistenceAdapter.java ./activemq-parent-5.14.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/KahaDBPersistenceAdapter.java");
	system("cp ./fixed/KahaDBStore.java ./activemq-parent-5.14.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/KahaDBStore.java");
	system("cp ./fixed/MultiKahaDBPersistenceAdapter.java ./activemq-parent-5.14.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/MultiKahaDBPersistenceAdapter.java");
	printf("don't inject AMQ_6430...\n");
#endif


#ifdef AMQ_6697
	system("cp ./buggy/StompSubscription.java ./activemq-parent-5.14.0-src/activemq-stomp/src/main/java/org/apache/activemq/transport/stomp/StompSubscription.java");
	printf("inject AMQ_6697...\n");
#else
	system("cp ./fixed/StompSubscription.java ./activemq-parent-5.14.0-src/activemq-stomp/src/main/java/org/apache/activemq/transport/stomp/StompSubscription.java");
	printf("don't inject AMQ_6697...\n");
#endif


#ifdef AMQ_6847
	system("cp ./buggy/Queue.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/broker/region/Queue.java");
	printf("inject AMQ_6847...\n");
#else
	system("cp ./fixed/Queue.java ./activemq-parent-5.14.0-src/activemq-broker/src/main/java/org/apache/activemq/broker/region/Queue.java");
	printf("don't inject AMQ_6847...\n");
#endif


}
