#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef ZK_4466
	system("cp ./buggy/DataTree.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/DataTree.java");
	system("cp ./buggy/IWatchManager.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/IWatchManager.java");
	system("cp ./buggy/WatchManager.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatchManager.java");
	system("cp ./buggy/WatchStats.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatchStats.java");
	system("cp ./buggy/WatcherMode.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatcherMode.java");
	system("cp ./buggy/WatcherModeManager.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatcherModeManager.java");
	printf("inject ZK_4466...\n");
#else
	system("cp ./fixed/DataTree.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/DataTree.java");
	system("cp ./fixed/IWatchManager.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/IWatchManager.java");
	system("cp ./fixed/WatchManager.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatchManager.java");
	system("cp ./fixed/WatchStats.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatchStats.java");
	system("cp ./fixed/WatcherMode.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatcherMode.java");
	system("cp ./fixed/WatcherModeManager.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/server/watch/WatcherModeManager.java");
	printf("don't inject ZK_4466...\n");
#endif


#ifdef ZK_4508
	system("cp ./buggy/ClientCnxn.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/ClientCnxn.java");
	printf("inject ZK_4508...\n");
#else
	system("cp ./fixed/ClientCnxn.java ./zookeeper-3.7.1-src/zookeeper-server/src/main/java/org/apache/zookeeper/ClientCnxn.java");
	printf("don't inject ZK_4508...\n");
#endif


#ifdef ZK_4473
	system("cp ./buggy/pom.xml ./zookeeper-3.7.1-src/zookeeper-contrib/zookeeper-contrib-zooinspector/pom.xml");
	system("cp ./buggy/ZooInspectorTreeView.java ./zookeeper-3.7.1-src/zookeeper-contrib/zookeeper-contrib-zooinspector/src/main/java/org/apache/zookeeper/inspector/gui/ZooInspectorTreeView.java");
	system("cp ./buggy/ZooInspectorManagerImpl.java ./zookeeper-3.7.1-src/zookeeper-contrib/zookeeper-contrib-zooinspector/src/main/java/org/apache/zookeeper/inspector/manager/ZooInspectorManagerImpl.java");
	printf("inject ZK_4473...\n");
#else
	system("cp ./fixed/pom.xml ./zookeeper-3.7.1-src/zookeeper-contrib/zookeeper-contrib-zooinspector/pom.xml");
	system("cp ./fixed/ZooInspectorTreeView.java ./zookeeper-3.7.1-src/zookeeper-contrib/zookeeper-contrib-zooinspector/src/main/java/org/apache/zookeeper/inspector/gui/ZooInspectorTreeView.java");
	system("cp ./fixed/ZooInspectorManagerImpl.java ./zookeeper-3.7.1-src/zookeeper-contrib/zookeeper-contrib-zooinspector/src/main/java/org/apache/zookeeper/inspector/manager/ZooInspectorManagerImpl.java");
	printf("don't inject ZK_4473...\n");
#endif


}
