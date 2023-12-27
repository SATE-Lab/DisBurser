#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef RMQ_231
	system("cp ./buggy/DefaultMessageStore.java ./rocketmq-all-4.1.0/store/src/main/java/org/apache/rocketmq/store/DefaultMessageStore.java");
	printf("inject RMQ_231...\n");
#else
	system("cp ./fixed/DefaultMessageStore.java ./rocketmq-all-4.1.0/store/src/main/java/org/apache/rocketmq/store/DefaultMessageStore.java");
	printf("don't inject RMQ_231...\n");
#endif


#ifdef RMQ_255
	system("cp ./buggy/DefaultMQPullConsumerImpl.java ./rocketmq-all-4.1.0/client/src/main/java/org/apache/rocketmq/client/impl/consumer/DefaultMQPullConsumerImpl.java");
	system("cp ./buggy/DefaultMQPushConsumerImpl.java ./rocketmq-all-4.1.0/client/src/main/java/org/apache/rocketmq/client/impl/consumer/DefaultMQPushConsumerImpl.java");
	printf("inject RMQ_255...\n");
#else
	system("cp ./fixed/DefaultMQPullConsumerImpl.java ./rocketmq-all-4.1.0/client/src/main/java/org/apache/rocketmq/client/impl/consumer/DefaultMQPullConsumerImpl.java");
	system("cp ./fixed/DefaultMQPushConsumerImpl.java ./rocketmq-all-4.1.0/client/src/main/java/org/apache/rocketmq/client/impl/consumer/DefaultMQPushConsumerImpl.java");
	printf("don't inject RMQ_255...\n");
#endif


#ifdef RMQ_266
	system("cp ./buggy/DefaultMQPushConsumerImpl.java ./rocketmq-all-4.1.0/client/src/main/java/org/apache/rocketmq/client/impl/consumer/DefaultMQPushConsumerImpl.java");
	printf("inject RMQ_266...\n");
#else
	system("cp ./fixed/DefaultMQPushConsumerImpl.java ./rocketmq-all-4.1.0/client/src/main/java/org/apache/rocketmq/client/impl/consumer/DefaultMQPushConsumerImpl.java");
	printf("don't inject RMQ_266...\n");
#endif


}
