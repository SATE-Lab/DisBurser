#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef ZK_1367
	system("cp ./buggy/LeaderZooKeeperServer.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/quorum/LeaderZooKeeperServer.java");
	system("cp ./buggy/Learner.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/quorum/Learner.java");
	system("cp ./buggy/LearnerZooKeeperServer.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/quorum/LearnerZooKeeperServer.java");
	system("cp ./buggy/ZooKeeperServer.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/ZooKeeperServer.java");
	system("cp ./buggy/FinalRequestProcessor.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/FinalRequestProcessor.java");
	printf("inject ZK_1367...\n");
#else
	system("cp ./fixed/LeaderZooKeeperServer.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/quorum/LeaderZooKeeperServer.java");
	system("cp ./fixed/Learner.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/quorum/Learner.java");
	system("cp ./fixed/LearnerZooKeeperServer.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/quorum/LearnerZooKeeperServer.java");
	system("cp ./fixed/ZooKeeperServer.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/ZooKeeperServer.java");
	system("cp ./fixed/FinalRequestProcessor.java ./apache-zookeeper-3.4.3/src/java/main/org/apache/zookeeper/server/FinalRequestProcessor.java");
	printf("don't inject ZK_1367...\n");
#endif


}
