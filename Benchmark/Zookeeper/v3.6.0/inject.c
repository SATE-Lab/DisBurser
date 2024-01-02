#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef ZK_3895
	system("cp ./buggy/ClientCnxn.java ./zookeeper-3.6.0-src/zookeeper-server/src/main/java/org/apache/zookeeper/ClientCnxn.java");
	system("cp ./buggy/ZooKeeper.java ./zookeeper-3.6.0-src/zookeeper-server/src/main/java/org/apache/zookeeper/ZooKeeper.java");
	printf("inject ZK_3895...\n");
#else
	system("cp ./fixed/ClientCnxn.java ./zookeeper-3.6.0-src/zookeeper-server/src/main/java/org/apache/zookeeper/ClientCnxn.java");
	system("cp ./fixed/ZooKeeper.java ./zookeeper-3.6.0-src/zookeeper-server/src/main/java/org/apache/zookeeper/ZooKeeper.java");
	printf("don't inject ZK_3895...\n");
#endif


}
