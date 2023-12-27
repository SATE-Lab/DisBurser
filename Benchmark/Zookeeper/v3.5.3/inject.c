#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef ZK_2355
	system("cp ./buggy/Learner.java ./apache-zookeeper-3.5.3/src/java/main/org/apache/zookeeper/server/quorum/Learner.java");
	system("cp ./buggy/QuorumPeer.java ./apache-zookeeper-3.5.3/src/java/main/org/apache/zookeeper/server/quorum/QuorumPeer.java");
	system("cp ./buggy/QuorumPeerMain.java ./apache-zookeeper-3.5.3/src/java/main/org/apache/zookeeper/server/quorum/QuorumPeerMain.java");
	printf("inject ZK_2355...\n");
#else
	system("cp ./fixed/Learner.java ./apache-zookeeper-3.5.3/src/java/main/org/apache/zookeeper/server/quorum/Learner.java");
	system("cp ./fixed/QuorumPeer.java ./apache-zookeeper-3.5.3/src/java/main/org/apache/zookeeper/server/quorum/QuorumPeer.java");
	system("cp ./fixed/QuorumPeerMain.java ./apache-zookeeper-3.5.3/src/java/main/org/apache/zookeeper/server/quorum/QuorumPeerMain.java");
	printf("don't inject ZK_2355...\n");
#endif


}
