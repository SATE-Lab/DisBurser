#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef HBASE_19850
	system("cp ./buggy/RestoreSnapshotHandler.java ./hbase-1.4.0/hbase-server/src/main/java/org/apache/hadoop/hbase/master/snapshot/RestoreSnapshotHandler.java");
	printf("inject HBASE_19850...\n");
#else
	system("cp ./fixed/RestoreSnapshotHandler.java ./hbase-1.4.0/hbase-server/src/main/java/org/apache/hadoop/hbase/master/snapshot/RestoreSnapshotHandler.java");
	printf("don't inject HBASE_19850...\n");
#endif


}
