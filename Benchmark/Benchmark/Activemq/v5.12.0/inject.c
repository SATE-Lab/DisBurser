#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef AMQ_6000
	system("cp ./buggy/Queue.java ./activemq-parent-5.12.0/activemq-broker/src/main/java/org/apache/activemq/broker/region/Queue.java");
	printf("inject AMQ_6000...\n");
#else
	system("cp ./fixed/Queue.java ./activemq-parent-5.12.0/activemq-broker/src/main/java/org/apache/activemq/broker/region/Queue.java");
	printf("don't inject AMQ_6000...\n");
#endif


#ifdef AMQ_6010
	system("cp ./buggy/AmqpProtocolDiscriminator.java ./activemq-parent-5.12.0/activemq-amqp/src/main/java/org/apache/activemq/transport/amqp/AmqpProtocolDiscriminator.java");
	printf("inject AMQ_6010...\n");
#else
	system("cp ./fixed/AmqpProtocolDiscriminator.java ./activemq-parent-5.12.0/activemq-amqp/src/main/java/org/apache/activemq/transport/amqp/AmqpProtocolDiscriminator.java");
	printf("don't inject AMQ_6010...\n");
#endif


#ifdef AMQ_6059
	system("cp ./buggy/BrokerSupport.java ./activemq-parent-5.12.0/activemq-broker/src/main/java/org/apache/activemq/util/BrokerSupport.java");
	printf("inject AMQ_6059...\n");
#else
	system("cp ./fixed/BrokerSupport.java ./activemq-parent-5.12.0/activemq-broker/src/main/java/org/apache/activemq/util/BrokerSupport.java");
	printf("don't inject AMQ_6059...\n");
#endif


}
