#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef ZK_706
	system("cp ./buggy/ClientCnxn.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/ClientCnxn.java");
	printf("inject ZK_706...\n");
#else
	system("cp ./fixed/ClientCnxn.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/ClientCnxn.java");
	printf("don't inject ZK_706...\n");
#endif


#ifdef ZK_1366
	system("cp ./buggy/ClientCnxn.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/ClientCnxn.java");
	system("cp ./buggy/ClientCnxnSocket.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/ClientCnxnSocket.java");
	system("cp ./buggy/Login.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/Login.java");
	system("cp ./buggy/Shell.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/Shell.java");
	system("cp ./buggy/ZKUtil.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/ZKUtil.java");
	system("cp ./buggy/Time.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/common/Time.java");
	system("cp ./buggy/ConnectionBean.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ConnectionBean.java");
	system("cp ./buggy/ExpiryQueue.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ExpiryQueue.java");
	system("cp ./buggy/FinalRequestProcessor.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/FinalRequestProcessor.java");
	system("cp ./buggy/PrepRequestProcessor.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/PrepRequestProcessor.java");
	system("cp ./buggy/RateLogger.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/RateLogger.java");
	system("cp ./buggy/Request.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/Request.java");
	system("cp ./buggy/ServerStats.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ServerStats.java");
	system("cp ./buggy/SessionTrackerImpl.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/SessionTrackerImpl.java");
	system("cp ./buggy/WorkerService.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/WorkerService.java");
	system("cp ./buggy/ZKDatabase.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ZKDatabase.java");
	system("cp ./buggy/ZooKeeperServer.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ZooKeeperServer.java");
	system("cp ./buggy/AuthFastLeaderElection.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/AuthFastLeaderElection.java");
	system("cp ./buggy/FastLeaderElection.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/FastLeaderElection.java");
	system("cp ./buggy/Follower.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/Follower.java");
	system("cp ./buggy/Leader.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/Leader.java");
	system("cp ./buggy/LearnerSnapshotThrottler.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/LearnerSnapshotThrottler.java");
	system("cp ./buggy/QuorumPeer.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/QuorumPeer.java");
	printf("inject ZK_1366...\n");
#else
	system("cp ./fixed/ClientCnxn.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/ClientCnxn.java");
	system("cp ./fixed/ClientCnxnSocket.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/ClientCnxnSocket.java");
	system("cp ./fixed/Login.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/Login.java");
	system("cp ./fixed/Shell.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/Shell.java");
	system("cp ./fixed/ZKUtil.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/ZKUtil.java");
	system("cp ./fixed/Time.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/common/Time.java");
	system("cp ./fixed/ConnectionBean.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ConnectionBean.java");
	system("cp ./fixed/ExpiryQueue.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ExpiryQueue.java");
	system("cp ./fixed/FinalRequestProcessor.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/FinalRequestProcessor.java");
	system("cp ./fixed/PrepRequestProcessor.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/PrepRequestProcessor.java");
	system("cp ./fixed/RateLogger.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/RateLogger.java");
	system("cp ./fixed/Request.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/Request.java");
	system("cp ./fixed/ServerStats.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ServerStats.java");
	system("cp ./fixed/SessionTrackerImpl.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/SessionTrackerImpl.java");
	system("cp ./fixed/WorkerService.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/WorkerService.java");
	system("cp ./fixed/ZKDatabase.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ZKDatabase.java");
	system("cp ./fixed/ZooKeeperServer.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/ZooKeeperServer.java");
	system("cp ./fixed/AuthFastLeaderElection.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/AuthFastLeaderElection.java");
	system("cp ./fixed/FastLeaderElection.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/FastLeaderElection.java");
	system("cp ./fixed/Follower.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/Follower.java");
	system("cp ./fixed/Leader.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/Leader.java");
	system("cp ./fixed/LearnerSnapshotThrottler.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/LearnerSnapshotThrottler.java");
	system("cp ./fixed/QuorumPeer.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/quorum/QuorumPeer.java");
	printf("don't inject ZK_1366...\n");
#endif


#ifdef ZK_2052
	system("cp ./buggy/PrepRequestProcessor.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/PrepRequestProcessor.java");
	printf("inject ZK_2052...\n");
#else
	system("cp ./fixed/PrepRequestProcessor.java ./apache-zookeeper-3.5.0/src/java/main/org/apache/zookeeper/server/PrepRequestProcessor.java");
	printf("don't inject ZK_2052...\n");
#endif


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
