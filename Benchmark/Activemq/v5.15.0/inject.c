#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef AMQ_6796
	system("cp ./buggy/StompSubscription.java ./activemq-parent-5.15.0-src/activemq-stomp/src/main/java/org/apache/activemq/transport/stomp/StompSubscription.java");
	printf("inject AMQ_6796...\n");
#else
	system("cp ./fixed/StompSubscription.java ./activemq-parent-5.15.0-src/activemq-stomp/src/main/java/org/apache/activemq/transport/stomp/StompSubscription.java");
	printf("don't inject AMQ_6796...\n");
#endif


#ifdef AMQ_6823
	system("cp ./buggy/MessagePull.java ./activemq-parent-5.15.0-src/activemq-client/src/main/java/org/apache/activemq/command/MessagePull.java");
	printf("inject AMQ_6823...\n");
#else
	system("cp ./fixed/MessagePull.java ./activemq-parent-5.15.0-src/activemq-client/src/main/java/org/apache/activemq/command/MessagePull.java");
	printf("don't inject AMQ_6823...\n");
#endif


#ifdef AMQ_7129
	system("cp ./buggy/KahaDBStore.java ./activemq-parent-5.15.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/KahaDBStore.java");
	system("cp ./buggy/MessageDatabase.java ./activemq-parent-5.15.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/MessageDatabase.java");
	printf("inject AMQ_7129...\n");
#else
	system("cp ./fixed/KahaDBStore.java ./activemq-parent-5.15.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/KahaDBStore.java");
	system("cp ./fixed/MessageDatabase.java ./activemq-parent-5.15.0-src/activemq-kahadb-store/src/main/java/org/apache/activemq/store/kahadb/MessageDatabase.java");
	printf("don't inject AMQ_7129...\n");
#endif


}
