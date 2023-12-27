#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef HDFS_8950
	system("cp ./buggy/DatanodeManager.java ./hadoop-2.7.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/blockmanagement/DatanodeManager.java");
	system("cp ./buggy/HostFileManager.java ./hadoop-2.7.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/blockmanagement/HostFileManager.java");
	printf("inject HDFS_8950...\n");
#else
	system("cp ./fixed/DatanodeManager.java ./hadoop-2.7.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/blockmanagement/DatanodeManager.java");
	system("cp ./fixed/HostFileManager.java ./hadoop-2.7.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/blockmanagement/HostFileManager.java");
	printf("don't inject HDFS_8950...\n");
#endif


#ifdef HDFS_10239
	system("cp ./buggy/MoveCommands.java ./hadoop-2.7.0-src/hadoop-common-project/hadoop-common/src/main/java/org/apache/hadoop/fs/shell/MoveCommands.java");
	printf("inject HDFS_10239...\n");
#else
	system("cp ./fixed/MoveCommands.java ./hadoop-2.7.0-src/hadoop-common-project/hadoop-common/src/main/java/org/apache/hadoop/fs/shell/MoveCommands.java");
	printf("don't inject HDFS_10239...\n");
#endif


#ifdef HDFS_11379
	system("cp ./buggy/DFSInputStream.java ./hadoop-2.7.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/DFSInputStream.java");
	printf("inject HDFS_11379...\n");
#else
	system("cp ./fixed/DFSInputStream.java ./hadoop-2.7.0-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/DFSInputStream.java");
	printf("don't inject HDFS_11379...\n");
#endif


}
