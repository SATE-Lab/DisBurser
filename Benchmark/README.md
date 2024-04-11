# How to run Redil

requirements：

- ubuntu 22.04 (20.04 may also work)
- docker 24.0.5
- java 8
- maven 3.x
- Run under ROOT authority 

> Notice: Docker 25.x and 26.x do not work with Redil by now
> 
> For information on how to install a specific version of docker please refer to https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository

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

3. (optional)  comment `build_from_source` function call in build.sh, if this step is not performed, lots of mvn failure will be thrown, however testcases can still run

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
   vi ./FaultSeed.h   # uncomment /* #define AMQ_6500 */ in FaultSeed.h
   ```

7. run the testcase again and output or test result should be different

   ```
   cd <project_root>/Activemq/Redit-Activemq-6500
   mvn test
   ```

   - server logs or console logs are attached in `log` directory of each testcase, for example: `<project_root>/dataset/Activemq/Redit-Activemq-6430/logs` shows  the log differences before and after the issue patch.
   - After the testcase has been executed, you can find the server logs in the corresponding project's `.ReditWorkingDirectory` directory

   

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

## Notices

- If build from source failed, please consider using download script to perform tar replacement.
- The name of the tar package should match the name of its internal folder and adhere to the following naming conventions:
  - `activemq-<version>.tar.gz`
  - `apache-cassandra-<version>.tar.gz`
  - `hadoop-<version>.tar.gz`
  - `hdfs-<version>.tar.gz`
  - `kafka-<scala_version>_<kafka_version>.tar.gz`
  - `rocketmq-<version>.tar.gz`
  - `zookeeper-<version>.tar.gz`
- It is worth noting that the naming conventions do not include certain special components such as 'alpha,' 'beta,' 'incubating,' etc.
- If the testcase results differ from expectations, consider adjusting the client version of target system in the pom.xml to the injected/fix version
- If you run into docker permission denied issue, please consider following steps

	```
	# add non-root user to the docker group
	sudo groupadd docker
	sudo usermod -aG docker $USER
	# authorize docker.sock
	sudo chmod 666 /var/run/docker.sock
	```
