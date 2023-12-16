#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef AMQ_6500
	system("cp ./buggy/TopicSubscription.java ./activemq-parent-5.14.1/activemq-broker/src/main/java/org/apache/activemq/broker/region/TopicSubscription.java");
	printf("inject AMQ_6500...\n");
#else
	system("cp ./fixed/TopicSubscription.java ./activemq-parent-5.14.1/activemq-broker/src/main/java/org/apache/activemq/broker/region/TopicSubscription.java");
	printf("don't inject AMQ_6500...\n");
#endif

}
