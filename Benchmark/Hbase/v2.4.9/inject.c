#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef HBASE_26742
	system("cp ./buggy/HRegion.java ./hbase-2.4.9/hbase-server/src/main/java/org/apache/hadoop/hbase/regionserver/HRegion.java");
	printf("inject HBASE_26742...\n");
#else
	system("cp ./fixed/HRegion.java ./hbase-2.4.9/hbase-server/src/main/java/org/apache/hadoop/hbase/regionserver/HRegion.java");
	printf("don't inject HBASE_26742...\n");
#endif


#ifdef HBASE_26901
	system("cp ./buggy/NewVersionBehaviorTracker.java ./hbase-2.4.9/hbase-server/src/main/java/org/apache/hadoop/hbase/regionserver/querymatcher/NewVersionBehaviorTracker.java");
	printf("inject HBASE_26901...\n");
#else
	system("cp ./fixed/NewVersionBehaviorTracker.java ./hbase-2.4.9/hbase-server/src/main/java/org/apache/hadoop/hbase/regionserver/querymatcher/NewVersionBehaviorTracker.java");
	printf("don't inject HBASE_26901...\n");
#endif


#ifdef HBASE_26027
	system("cp ./buggy/AsyncRequestFutureImpl.java ./hbase-2.4.9/hbase-client/src/main/java/org/apache/hadoop/hbase/client/AsyncRequestFutureImpl.java");
	printf("inject HBASE_26027...\n");
#else
	system("cp ./fixed/AsyncRequestFutureImpl.java ./hbase-2.4.9/hbase-client/src/main/java/org/apache/hadoop/hbase/client/AsyncRequestFutureImpl.java");
	printf("don't inject HBASE_26027...\n");
#endif


}
