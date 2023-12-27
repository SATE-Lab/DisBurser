#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef HDFS_14504
	system("cp ./buggy/FSDirectory.java ./hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/FSDirectory.java");
	printf("inject HDFS_14504...\n");
#else
	system("cp ./fixed/FSDirectory.java ./hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/FSDirectory.java");
	printf("don't inject HDFS_14504...\n");
#endif


#ifdef HDFS_14987
	system("cp ./buggy/NamenodeFsck.java ./hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/NamenodeFsck.java");
	printf("inject HDFS_14987...\n");
#else
	system("cp ./fixed/NamenodeFsck.java ./hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs/src/main/java/org/apache/hadoop/hdfs/server/namenode/NamenodeFsck.java");
	printf("don't inject HDFS_14987...\n");
#endif


#ifdef HDFS_14869
	system("cp ./buggy/DistCp.java ./hadoop-3.1.2-src/hadoop-tools/hadoop-distcp/src/main/java/org/apache/hadoop/tools/DistCp.java");
	system("cp ./buggy/DistCpSync.java ./hadoop-3.1.2-src/hadoop-tools/hadoop-distcp/src/main/java/org/apache/hadoop/tools/DistCpSync.java");
	printf("inject HDFS_14869...\n");
#else
	system("cp ./fixed/DistCp.java ./hadoop-3.1.2-src/hadoop-tools/hadoop-distcp/src/main/java/org/apache/hadoop/tools/DistCp.java");
	system("cp ./fixed/DistCpSync.java ./hadoop-3.1.2-src/hadoop-tools/hadoop-distcp/src/main/java/org/apache/hadoop/tools/DistCpSync.java");
	printf("don't inject HDFS_14869...\n");
#endif


}
