# DisBurser_Benchmark
Dataset include the Benchmark we collect from JIRA, with various versions of specific distributed systems.

## Environment Requirement
1. Ubuntu 20.04.1 LTS recommended
2. Git lfs. **Attention: git lfs must be explicitly stated**
3. Java 8 recommended
4. Maven 3.x
5. Run under ROOT authority 
## Environment Setting
Steps:

<!-- 1. Download docker
```python
# Install curl tool
sudo apt install curl

# Download docker from Aliyun
curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun
```
Enter following command to promise the docker can use the local port.
```python
# Add docker group
sudo groupadd docker

# Add current user to docker group
sudo usermod -aG docker $USER

# Restart docker service to enable the setting
sudo systemctl restart docker
``` -->

1. Setting environment variable.

Clean all openjdk on machine.

Set the path of JDK, Maven3, AspectJ. 
**DON'T use the apt tool to download them. It will need more effort to set them.**

For IDEA user, you have to enter the ASPECTJ_HOME manually in "Run - Edit Configurations -Environment variables"


## Switch Version
Steps:
1. Clone code
2. Check build.sh under the specific system version
3. Confirm the issue which is need for trigger or not, delete the unnecessary macro definiton in bash
4. Download the required **source code** of the specific version.
5. Run bash and will get the package of the reuqired version of issue



## Explanation

- Due to the large storage space occupied by the source code packages and binary packages of the distributed system, these packages in this anonymous project will be omitted and download URLs will be provided.

- We provide two implementation methods for simulating patch repairs in the benchmark test suite file inject.c in this anonymous project:

  1. Use the io stream to dynamically repair the buggy version file to obtain the fixed version file.

  2. Provide the fixed version file directly.

  In short, the first method is more in line with the patch repair process, and the second method is more convenient and simple.

- We involved some other system versions when building test cases, so some additional versions are included in the benchmark for use by test cases, which are temporarily out of the scope of benchmark and do not include test suites. I think these versions will be involved in the subsequent expansion of the benchmark.
