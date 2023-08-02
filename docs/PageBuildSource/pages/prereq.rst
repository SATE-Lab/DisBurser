=============
Prerequisites
=============

To use |projectName|, you need to install the following dependencies on your machine:

* Ubuntu 20.04 LTS
* Git lfs (Attention: git lfs must be explicitly stated)
* Java 8+
* Maven 3.x
* Aspectj 1.8+, and MUST keep the verison of aspectjrt in pom equal to the Aspectj
* Docker 1.13+ (Make sure the user running your test cases is able to run Docker commands. For example, in Linux, you need
  to add the user to the docker group)
 ..
  * Run under ROOT authority

It is also recommended to use a build system like Maven or Gradle to be able to include |projectName|'s dependency.