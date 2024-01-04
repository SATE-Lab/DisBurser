# Redit-Rocket-3281

### Details

Title: ***cannot delete topic/group perms in acl config #3281***

Github link：[https://github.com/apache/rocketmq/issues/3281](https://github.com/apache/rocketmq/issues/3281)

Fixed version: 4.9.2

### Description

The issue tracker is **ONLY** used for bug report(feature request need to follow [RIP process](https://github.com/apache/rocketmq/wiki/RocketMQ-Improvement-Proposal)). Keep in mind, please check whether there is an existing same report before your raise a new one.

Alternately (especially if your communication is not a bug report), you can send mail to our [mailing lists](http://rocketmq.apache.org/about/contact/). We welcome any friendly suggestions, bug fixes, collaboration and other improvements.

Please ensure that your bug report is clear and that it is complete. Otherwise, we may be unable to understand it or to reproduce it, either of which would prevent us from fixing the bug. We strongly recommend the report(bug report or feature request) could include some hints as the following:

**BUG REPORT**

1. Please describe the issue you observed:

- What did you do (The steps to reproduce)?

In `mqadmin updateAclConfig -n localhost:9876 -a abcdefg -s abcdefg -c RaftCluster -g groupa=SUB`
sets group groupPerms to `groupa=SUB`
and then wants to delete groupPerms using
`mqadmin updateAclConfig -n localhost:9876 -a abcdefg -s abcdefg -c RaftCluster -g ""`

- What did you expect to see?
  groupPerms is empty
- What did you see instead?
  cannot delete groupPerms, its still `groupa=SUB`

1. Please tell us about your environment:
2. Other information (e.g. detailed explanation, logs, related issues, suggestions how to fix, etc):

**FEATURE REQUEST**

1. Please describe the feature you are requesting.
2. Provide any additional detail on your proposed use case for this feature.
3. Indicate the importance of this issue to you (blocker, must-have, should-have, nice-to-have). Are you currently using any workarounds to address this issue?
4. If there are some sub-tasks using -[] for each subtask and create a corresponding issue to map to the sub task:

- [sub-task1-issue-number](https://github.com/apache/rocketmq/issues/example_sub_issue1_link_here): sub-task1 description here,
- [sub-task2-issue-number](https://github.com/apache/rocketmq/issues/example_sub_issue2_link_here): sub-task2 description here,
- ...

### Testcase

Reproduced version：4.9.0

Steps to reproduce：

1. Create a RocketMQ cluster with a master server, and a raft cluster with 3 dledger nodes.
2. Set AclEnable=true in brokers' configuration files.
3. Update acl config using `mqadmin  updateAclConfig`, set `groupa=SUB`
4. Update acl config again, and set `-g ""` to delete groupPerms.
5. Check acl config using `mqadmin  getAccessConfigSubCommand`, groupPerms is still `groupa=SUB`

Notice: If the testcase results differ from expectations, consider adjusting the rocketmq-client version in the pom.xml to the injected/fix version.
