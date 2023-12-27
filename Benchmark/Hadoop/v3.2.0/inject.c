#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef HDFS_14499
	system("cp ./buggy/INodeReference.java ./hadoop-3.2.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/INodeReference.java");
	printf("inject HDFS_14499...\n");
#else
	system("cp ./fixed/INodeReference.java ./hadoop-3.2.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/INodeReference.java");
	printf("don't inject HDFS_14499...\n");
#endif


#ifdef HDFS_15446
	system("cp ./buggy/FSDirectory.java ./hadoop-3.2.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/FSDirectory.java");
	system("cp ./buggy/FSEditLogLoader.java ./hadoop-3.2.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/FSEditLogLoader.java");
	printf("inject HDFS_15446...\n");
#else
	system("cp ./fixed/FSDirectory.java ./hadoop-3.2.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/FSDirectory.java");
	system("cp ./fixed/FSEditLogLoader.java ./hadoop-3.2.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/FSEditLogLoader.java");
	printf("don't inject HDFS_15446...\n");
#endif


#ifdef HDFS_15398
	system("cp ./buggy/DFSStripedOutputStream.java ./hadoop-3.2.0-src/hadoop-hdfs-project/hadoop-hdfs-client/src/main/java/org/apache/hadoop/hdfs/DFSStripedOutputStream.java");
	printf("inject HDFS_15398...\n");
#else
	system("cp ./fixed/DFSStripedOutputStream.java ./hadoop-3.2.0-src/hadoop-hdfs-project/hadoop-hdfs-client/src/main/java/org/apache/hadoop/hdfs/DFSStripedOutputStream.java");
	printf("don't inject HDFS_15398...\n");
#endif


}
