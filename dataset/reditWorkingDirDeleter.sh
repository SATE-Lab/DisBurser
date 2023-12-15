#!/bin/bash

# 函数用于删除目录中的 .ReditWorkingDirectory
delete_rwd_directories() {
  local dir="$1"
  
  # 查找并删除名为 .ReditWorkingDirectory 的目录
  find "$dir" -type d -name ".ReditWorkingDirectory" -print -exec rm -r {} \;
  find "$dir" -type d -name "target" -print -exec rm -r {} \;
}

# 删除当前目录中的 .ReditWorkingDirectory
delete_rwd_directories "."

# 递归遍历并删除所有子目录中的 .ReditWorkingDirectory
for subdir in $(find . -type d -name ".ReditWorkingDirectory"); do
  delete_rwd_directories "$subdir"
done

