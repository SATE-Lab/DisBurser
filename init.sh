#!/bin/bash

# delete existing tars
function delete_matching_files {
    local dir=$1

    # 遍历目录中的文件和子目录
    for file in "$dir"/*; do
        if [[ -f "$file" && "$file" =~ (activemq-|apache-cassandra-|hadoop-|hbase-|kafka_|rocketmq-|zookeeper-).*\.tar\.gz ]]; then
            # 如果是符合条件的文件，则删除之
            echo "delete file: $file"
            rm "$file"
        elif [[ -d "$file" ]]; then
            # 如果是子目录，则递归调用函数
            delete_matching_files "$file"
        fi
    done
}


delete_matching_files "$(pwd)/Benchmark"


