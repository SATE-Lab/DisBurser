#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef RMQ_1409
	system("cp ./buggy/PlainAccessValidator.java ./rocketmq-all-4.5.0/acl/src/main/java/org/apache/rocketmq/acl/plain/PlainAccessValidator.java");
	printf("inject RMQ_1409...\n");
#else
	system("cp ./fixed/PlainAccessValidator.java ./rocketmq-all-4.5.0/acl/src/main/java/org/apache/rocketmq/acl/plain/PlainAccessValidator.java");
	printf("don't inject RMQ_1409...\n");
#endif


}
