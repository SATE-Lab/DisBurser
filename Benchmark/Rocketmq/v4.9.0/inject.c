#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef RMQ_3175
	system("cp ./buggy/PlainPermissionManager.java ./rocketmq-4.9.0/acl/src/main/java/org/apache/rocketmq/acl/plain/PlainPermissionManager.java");
	printf("inject RMQ_3175...\n");
#else
	system("cp ./fixed/PlainPermissionManager.java ./rocketmq-4.9.0/acl/src/main/java/org/apache/rocketmq/acl/plain/PlainPermissionManager.java");
	printf("don't inject RMQ_3175...\n");
#endif


#ifdef RMQ_3281
	system("cp ./buggy/PlainPermissionManager.java ./rocketmq-4.9.0/acl/src/main/java/org/apache/rocketmq/acl/plain/PlainPermissionManager.java");
	system("cp ./buggy/AdminBrokerProcessor.java ./rocketmq-4.9.0/broker/src/main/java/org/apache/rocketmq/broker/processor/AdminBrokerProcessor.java");
	system("cp ./buggy/MQClientAPIImpl.java ./rocketmq-4.9.0/client/src/main/java/org/apache/rocketmq/client/impl/MQClientAPIImpl.java");
	system("cp ./buggy/UtilAll.java ./rocketmq-4.9.0/common/src/main/java/org/apache/rocketmq/common/UtilAll.java");
	system("cp ./buggy/DeleteAccessConfigSubCommand.java ./rocketmq-4.9.0/tools/src/main/java/org/apache/rocketmq/tools/command/acl/DeleteAccessConfigSubCommand.java");
	system("cp ./buggy/UpdateGlobalWhiteAddrSubCommand.java ./rocketmq-4.9.0/tools/src/main/java/org/apache/rocketmq/tools/command/acl/UpdateGlobalWhiteAddrSubCommand.java");
	printf("inject RMQ_3281...\n");
#else
	system("cp ./fixed/PlainPermissionManager.java ./rocketmq-4.9.0/acl/src/main/java/org/apache/rocketmq/acl/plain/PlainPermissionManager.java");
	system("cp ./fixed/AdminBrokerProcessor.java ./rocketmq-4.9.0/broker/src/main/java/org/apache/rocketmq/broker/processor/AdminBrokerProcessor.java");
	system("cp ./fixed/MQClientAPIImpl.java ./rocketmq-4.9.0/client/src/main/java/org/apache/rocketmq/client/impl/MQClientAPIImpl.java");
	system("cp ./fixed/UtilAll.java ./rocketmq-4.9.0/common/src/main/java/org/apache/rocketmq/common/UtilAll.java");
	system("cp ./fixed/DeleteAccessConfigSubCommand.java ./rocketmq-4.9.0/tools/src/main/java/org/apache/rocketmq/tools/command/acl/DeleteAccessConfigSubCommand.java");
	system("cp ./fixed/UpdateGlobalWhiteAddrSubCommand.java ./rocketmq-4.9.0/tools/src/main/java/org/apache/rocketmq/tools/command/acl/UpdateGlobalWhiteAddrSubCommand.java");
	printf("don't inject RMQ_3281...\n");
#endif


#ifdef RMQ_3556
	system("cp ./buggy/MQClientAPIImpl.java ./rocketmq-4.9.0/client/src/main/java/org/apache/rocketmq/client/impl/MQClientAPIImpl.java");
	printf("inject RMQ_3556...\n");
#else
	system("cp ./fixed/MQClientAPIImpl.java ./rocketmq-4.9.0/client/src/main/java/org/apache/rocketmq/client/impl/MQClientAPIImpl.java");
	printf("don't inject RMQ_3556...\n");
#endif


}
