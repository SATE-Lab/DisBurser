#include <stdio.h>
#include <stdlib.h>
int main()
{
#ifdef KA_13488
	system("cp ./buggy/Metadata.java ./kafka-3.0.0-src/clients/src/main/java/org/apache/kafka/clients/Metadata.java");
	printf("inject KA_13488...\n");
#else
	system("cp ./fixed/Metadata.java ./kafka-3.0.0-src/clients/src/main/java/org/apache/kafka/clients/Metadata.java");
	printf("don't inject KA_13488...\n");
#endif

#ifdef KA_13964
	printf("inject KA_13488...\n");
#else
	printf("KA_13488's patch seems conflict with the fixed version, please consider tar replacement method to get fixed result of testcase\n");
#endif

}
