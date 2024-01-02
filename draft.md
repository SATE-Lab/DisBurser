# run redil

requirements：

- ubuntu22.04 (20.04 may also work)
- docker
- java8
- maven 3.x
- Run under ROOT authority 

install redit helper

```
cd <project_root>/helper
mvn install
```

## method1: using download script

1. download tars of the specific system

   ```
   sudo apt install wget unzip
   cd <project_root>
   sudo ./init.sh amq  # [amq|cas|hdfs|hbase|kafka|rmq|zk]
   ```

2. using [AMQ-6500](https://issues.apache.org/jira/browse/AMQ-6500) as an example

   ```
   cd <project_root>/Benchmark/Activemq/v5.14.1
   ```

3. (optional)  comment `build_from_source` function call in build.sh, if not perform this step, lots of mvn failure will be thrown but testcases can still run smoothly

4. generate tars for target system

   ```
   sudo ./build.sh
   ```

5. run testcase

   ```
   cd <project_root>/Activemq/Redit-Activemq-6500
   mvn test
   ```

6. uncomment issue_id in FaultSeed.h to cancel injection of specific fault

   ```
   cd <project_root>/Benchmark/Activemq/v5.14.1
   # uncomment /* #define AMQ_6500 */ in FaultSeed.h
   ```

7. run the testcase again and output or test result should be different

   ```
   cd <project_root>/Activemq/Redit-Activemq-6500
   mvn test
   ```

   server log or console log is attached in `log` directory of each testcase, for example: `<project_root>/dataset/Activemq/Redit-Activemq-6430/logs` shows  the log differences before and after the issue patch.

   

## method2: build from source

using [AMQ-6500](https://issues.apache.org/jira/browse/AMQ-6500) as an example

1. Download source code tar file and unzip it to `<project_root>/Benchmark/Activemq/v5.14.1/activemq-parent-5.14.1-src/`

2. Download binary tar file and  `<project_root>/Benchmark/Activemq/v5.14.1/activemq-5.14.1/`

3. comment `overwrite_tar` function call in build.sh

4. generate tars for target system

   ```
   sudo ./build.sh
   ```

5. run testcase

   ```
   cd <project_root>/Activemq/Redit-Activemq-6500
   mvn test
   ```



todo: 

- build notice
- naming notice：
  - 
