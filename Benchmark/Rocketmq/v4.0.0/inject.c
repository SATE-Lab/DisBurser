#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef RMQ_281
	system("cp ./buggy/BrokerStartup.java ./rocketmq-4.0.0/broker/src/main/java/org/apache/rocketmq/broker/BrokerStartup.java");
	system("cp ./buggy/DefaultMessageStore.java ./rocketmq-4.0.0/store/src/main/java/org/apache/rocketmq/store/DefaultMessageStore.java");
	system("cp ./buggy/StorePathConfigHelper.java ./rocketmq-4.0.0/store/src/main/java/org/apache/rocketmq/store/config/StorePathConfigHelper.java");
	printf("inject RMQ_281...\n");
#else
	system("cp ./fixed/BrokerStartup.java ./rocketmq-4.0.0/broker/src/main/java/org/apache/rocketmq/broker/BrokerStartup.java");
	system("cp ./fixed/DefaultMessageStore.java ./rocketmq-4.0.0/store/src/main/java/org/apache/rocketmq/store/DefaultMessageStore.java");
	system("cp ./fixed/StorePathConfigHelper.java ./rocketmq-4.0.0/store/src/main/java/org/apache/rocketmq/store/config/StorePathConfigHelper.java");
	printf("don't inject RMQ_281...\n");
#endif


}
