# RediI

 [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) 


RediI(**Re**gression framework for **Di**stributed system **I**nfrastructure) is an infrastructure or the **deterministic** reproduction of distributed system failures. RediI provides both a dataset of known distributed-systems bugs called RediD and a toolset called RediT. 

<!-- With dockers running on the machine, we can easily simulate the operation of a real-world distributed system on Redit and find defects of the system by the test events.

Currently, node failure, network partition, network delay, network packet loss, and clock drift is supported.

For Java, we can force a specific order between nodes in order to reproduce a specific time-sensitive scenario and inject failures before or after a specific method is called when a specific stack trace is present. -->

Please refer to benchmark/README.md for more information on how to run RediT.

# Events
With Redil, for a few supported languages, it is possible to inject a failure right before or after a method call where a specific stack trace is present. This happens through defining a set of named internal and test case events, ordering those events in a run sequence string, and let the RediT’s runtime engine enforce the specified order between the nodes. Currently, node start, kill, restart, network partition, network delay, network packet loss, and clock drift are supported.
| Event type    | Event name                | Corresponding java file                                  |
| ------------- | ------------------------- | -------------------------------------------------------- |
| Node event    | start node                | `io.redit.execution.single_node.SingleNodeRuntimeEngine` |
|               | restart node              | `io.redit.execution.single_node.SingleNodeRuntimeEngine` |
|               | stop node                 | `io.redit.execution.single_node.SingleNodeRuntimeEngine` |
|               | kill node                 | `io.redit.execution.single_node.SingleNodeRuntimeEngine` |
| Network event | network partation         | `io.redit.execution.NetworkPartitionManager`             |
|               | remove network partation  | `io.redit.execution.NetworkPartitionManager`             |
|               | reapply network partation | `io.redit.execution.NetworkPartitionManager`             |
|               | network delay             | `io.redit.execution.NetOp`                               |
|               | remove network delay      | `io.redit.execution.NetOp`                               |
|               | network loss              | `io.redit.execution.NetOp`                               |
|               | remove network loss       | `io.redit.execution.NetOp`                               |
| Clock event   | clock drift               | `io.redit.execution.single_node.SingleNodeRuntimeEngine` |
 ## Network Events. 
 Network events refer to changes or disruptions in the communication between nodes within a distributed system. These events can include network failures, packet loss, andnetwork partitioning, all of which can significantly impact the system's performance and reliability.
 ## Node Events. 
Node events refer to changes in the state of a node within a distributed system, which can include actions such as starting, restarting, stopping, or crashing.
 ## Clock Events. 
 Clock events refer to the deviations in time between nodes within a distributed system, caused by differences in local clock design, such as clock drift or synchronization issues.

 # Event timing
| Opeartor | Corresponding java file                   |
| :------: | ----------------------------------------- |
|    \|    | io.redit.verification.RunSequenceVerifier |
|    *     | io.redit.verification.RunSequenceVerifier |
|    ()    | io.redit.verification.RunSequenceVerifier |

# event dependency
|    Event Type     |    Event Name     | Corresponding java file                             |
| :---------------: | :---------------: | --------------------------------------------------- |
|       Block       |    blockBefore    | io.redit.dsl.events.internal.BlockingEvent          |
|                   |    blockAfter     | io.redit.dsl.events.internal.BlockingEvent          |
|                   |   unblockBefore   | io.redit.dsl.events.internal.BlockingEvent          |
|       <br>        |   unblockAfter    | io.redit.dsl.events.internal.BlockingEvent          |
|    StackTrace     |    stackTrace     | io.redit.dsl.events.internal.StackTraceEvent        |
| GarbageCollection | garbageCollection | io.redit.dsl.events.internal.GarbageCollectionEvent |




# Dataset

We provide bugs in each version of each distributed system and their error injection scripts. You can quickly switch between error and repair versions through the instructions in the data set, and test RediT or your tool on different versions.

# Testcase
We provide recurring test cases for RediI. You can use the recurring test cases according to the instructions in the Testcase folder, so that you can quickly get started with our framework.


# Questions

You can open an issue in the project to inform us of your problems. 

# License

RediB is licensed under [MIT](https://opensource.org/licenses/MIT) and is freely available on Github.
