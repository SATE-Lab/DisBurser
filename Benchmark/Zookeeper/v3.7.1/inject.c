#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef ZK_4466
	system("cp ./buggy/DataTree.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/DataTree.java");
	system("cp ./buggy/IWatchManager.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/IWatchManager.java");
	system("cp ./buggy/WatchManager.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatchManager.java");
	system("cp ./buggy/WatchStats.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatchStats.java");
	system("cp ./buggy/WatcherMode.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatcherMode.java");
	system("cp ./buggy/WatcherModeManager.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatcherModeManager.java");
	printf("inject ZK_4466...\n");
#else
	system("cp ./fixed/DataTree.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/DataTree.java");
	system("cp ./fixed/IWatchManager.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/IWatchManager.java");
	system("cp ./fixed/WatchManager.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatchManager.java");
	system("cp ./fixed/WatchStats.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatchStats.java");
	system("cp ./fixed/WatcherMode.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatcherMode.java");
	system("cp ./fixed/WatcherModeManager.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatcherModeManager.java");
	printf("don't inject ZK_4466...\n");
#endif


#ifdef ZK_4508
	system("cp ./buggy/ClientCnxn.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/ClientCnxn.java");
	printf("inject ZK_4508...\n");
#else
	system("cp ./fixed/ClientCnxn.java ./apache-zookeeper-3.7.1/zookeeper-server/src/main/java/org/apache/zookeeper/ClientCnxn.java");
	printf("don't inject ZK_4508...\n");
#endif


#ifdef ZK_4473
	system("cp ./buggy/pom.xml ./apache-zookeeper-3.7.1/zookeeper-contrib/zookeeper-contrib-zooinspector/pom.xml");
	system("cp ./buggy/ZooInspectorTreeView.java ./apache-zookeeper-3.7.1/zookeeper-contrib/zookeeper-contrib-zooinspector/src/main/java/org/apache/zookeeper/inspector/gui/ZooInspectorTreeView.java");
	system("cp ./buggy/ZooInspectorManagerImpl.java ./apache-zookeeper-3.7.1/zookeeper-contrib/zookeeper-contrib-zooinspector/src/main/java/org/apache/zookeeper/inspector/manager/ZooInspectorManagerImpl.java");
	printf("inject ZK_4473...\n");
#else
	system("cp ./fixed/pom.xml ./apache-zookeeper-3.7.1/zookeeper-contrib/zookeeper-contrib-zooinspector/pom.xml");
	system("cp ./fixed/ZooInspectorTreeView.java ./apache-zookeeper-3.7.1/zookeeper-contrib/zookeeper-contrib-zooinspector/src/main/java/org/apache/zookeeper/inspector/gui/ZooInspectorTreeView.java");
	system("cp ./fixed/ZooInspectorManagerImpl.java ./apache-zookeeper-3.7.1/zookeeper-contrib/zookeeper-contrib-zooinspector/src/main/java/org/apache/zookeeper/inspector/manager/ZooInspectorManagerImpl.java");
	printf("don't inject ZK_4473...\n");
#endif


}
