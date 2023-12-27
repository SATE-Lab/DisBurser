#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef HBASE_23682
	system("cp ./buggy/ProcedureExecutor.java ./hbase-2.2.2/hbase-procedure/src/main/java/org/apache/hadoop/hbase/procedure2/ProcedureExecutor.java");
	printf("inject HBASE_23682...\n");
#else
	system("cp ./fixed/ProcedureExecutor.java ./hbase-2.2.2/hbase-procedure/src/main/java/org/apache/hadoop/hbase/procedure2/ProcedureExecutor.java");
	printf("don't inject HBASE_23682...\n");
#endif


#ifdef HBASE_24189
	system("cp ./buggy/CommonFSUtils.java ./hbase-2.2.2/hbase-common/src/main/java/org/apache/hadoop/hbase/util/CommonFSUtils.java");
	system("cp ./buggy/WALSplitter.java ./hbase-2.2.2/hbase-server/src/main/java/org/apache/hadoop/hbase/wal/WALSplitter.java");
	printf("inject HBASE_24189...\n");
#else
	system("cp ./fixed/CommonFSUtils.java ./hbase-2.2.2/hbase-common/src/main/java/org/apache/hadoop/hbase/util/CommonFSUtils.java");
	system("cp ./fixed/WALSplitter.java ./hbase-2.2.2/hbase-server/src/main/java/org/apache/hadoop/hbase/wal/WALSplitter.java");
	printf("don't inject HBASE_24189...\n");
#endif


#ifdef HBASE_24135
	system("cp ./buggy/RSGroupAdminEndpoint.java ./hbase-2.2.2/hbase-rsgroup/src/main/java/org/apache/hadoop/hbase/rsgroup/RSGroupAdminEndpoint.java");
	system("cp ./buggy/RSGroupInfoManager.java ./hbase-2.2.2/hbase-rsgroup/src/main/java/org/apache/hadoop/hbase/rsgroup/RSGroupInfoManager.java");
	system("cp ./buggy/RSGroupInfoManagerImpl.java ./hbase-2.2.2/hbase-rsgroup/src/main/java/org/apache/hadoop/hbase/rsgroup/RSGroupInfoManagerImpl.java");
	system("cp ./buggy/RSGroupUtil.java ./hbase-2.2.2/hbase-rsgroup/src/main/java/org/apache/hadoop/hbase/rsgroup/RSGroupUtil.java");
	printf("inject HBASE_24135...\n");
#else
	system("cp ./fixed/RSGroupAdminEndpoint.java ./hbase-2.2.2/hbase-rsgroup/src/main/java/org/apache/hadoop/hbase/rsgroup/RSGroupAdminEndpoint.java");
	system("cp ./fixed/RSGroupInfoManager.java ./hbase-2.2.2/hbase-rsgroup/src/main/java/org/apache/hadoop/hbase/rsgroup/RSGroupInfoManager.java");
	system("cp ./fixed/RSGroupInfoManagerImpl.java ./hbase-2.2.2/hbase-rsgroup/src/main/java/org/apache/hadoop/hbase/rsgroup/RSGroupInfoManagerImpl.java");
	system("cp ./fixed/RSGroupUtil.java ./hbase-2.2.2/hbase-rsgroup/src/main/java/org/apache/hadoop/hbase/rsgroup/RSGroupUtil.java");
	printf("don't inject HBASE_24135...\n");
#endif


}
