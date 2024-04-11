# RediI

 [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 


RediI(**Re**gression framework for **Di**stributed system **I**nfrastructure) is an infrastructure or the **deterministic** reproduction of distributed system failures. RediI provides both a dataset of known distributed-systems bugs called RediD and a toolset called RediT. 

<!-- With dockers running on the machine, we can easily simulate the operation of a real-world distributed system on Redit and find defects of the system by the test events.

Currently, node failure, network partition, network delay, network packet loss, and clock drift is supported.

For Java, we can force a specific order between nodes in order to reproduce a specific time-sensitive scenario and inject failures before or after a specific method is called when a specific stack trace is present. -->

Please refer to benchmark/README.md for more information on how to run RediT.


# Dataset

We provide bugs in each version of each distributed system and their error injection scripts. You can quickly switch between error and repair versions through the instructions in the data set, and test RediT or your tool on different versions.

# Testcase
We provide recurring test cases for RediI. You can use the recurring test cases according to the instructions in the Testcase folder, so that you can quickly get started with our framework.


# Questions

You can open an issue in the project to inform us of your problems. 

# License

RediB is licensed under [MIT](https://opensource.org/licenses/MIT) and is freely available on Github.
