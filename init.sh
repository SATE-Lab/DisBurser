#!/bin/bash

# delete existing tars
function delete_existing_tars {
    local dir=$1

    # 遍历目录中的文件和子目录
    for file in "$dir"/*; do
        if [[ -f "$file" && "$file" =~ (activemq-|apache-cassandra-|hadoop-|hbase-|kafka_|rocketmq-|zookeeper-).*(\.tar\.gz|\.zip) ]]; then
            # 如果是符合条件的文件，则删除之
            echo "delete file: $file"
            rm "$file"
        elif [[ -d "$file" ]]; then
            # 如果是子目录，则递归调用函数
            delete_existing_tars "$file"
        fi
    done
}


function download_resource {
    local url=$1
    local save_path=$2
    local file_name=$3
    local full_path="${save_path}/${file_name}"

    # 检查本地文件是否存在
    if [ -e "$full_path" ]; then
        echo "file [$file_name] already exists in $full_path, skip download"
    else
        # 使用 wget 下载资源
        wget -O "$full_path" "$url"

        # 检查下载是否成功
        if [ $? -eq 0 ]; then
            echo "下载成功！"
        else
            echo "下载失败，请检查输入参数和网络连接。"
        fi
    fi
}

function download_amq {
	# amq 5.12.0 (5.12.0, 5.12.2, 5.13.1)
    local amq_5_12_0_tars_path="$(pwd)/Benchmark/Activemq/v5.12.0/tar"

    local amq_5_12_0_web="https://archive.apache.org/dist/activemq/5.12.0/apache-activemq-5.12.0-bin.tar.gz"
	local amq_5_12_2_web="https://archive.apache.org/dist/activemq/5.12.2/apache-activemq-5.12.2-bin.tar.gz"
	local amq_5_13_1_web="https://archive.apache.org/dist/activemq/5.13.1/apache-activemq-5.13.1-bin.tar.gz"
	local amq_5_12_0_tar_name="activemq-5.12.0.tar.gz"
	local amq_5_12_2_tar_name="activemq-5.12.2.tar.gz"
	local amq_5_13_1_tar_name="activemq-5.13.1.tar.gz"

	mkdir "$amq_5_12_0_tars_path"
	download_resource $amq_5_12_0_web "$amq_5_12_0_tars_path" $amq_5_12_0_tar_name
	download_resource $amq_5_12_2_web "$amq_5_12_0_tars_path" $amq_5_12_2_tar_name
	download_resource $amq_5_13_1_web "$amq_5_12_0_tars_path" $amq_5_13_1_tar_name

	# amq 5.14.0 (5.14.0, 5.14.1)
    local amq_5_14_0_tars_path="$(pwd)/Benchmark/Activemq/v5.14.0/tar"

    local amq_5_14_0_web="https://archive.apache.org/dist/activemq/5.14.0/apache-activemq-5.14.0-bin.tar.gz"
    local amq_5_14_1_web="https://archive.apache.org/dist/activemq/5.14.1/apache-activemq-5.14.1-bin.tar.gz"
    local amq_5_15_0_web="https://archive.apache.org/dist/activemq/5.15.0/apache-activemq-5.15.0-bin.tar.gz"
    local amq_5_15_3_web="https://archive.apache.org/dist/activemq/5.15.3/apache-activemq-5.15.3-bin.tar.gz"
    local amq_5_14_0_tar_name="activemq-5.14.0.tar.gz"
    local amq_5_14_1_tar_name="activemq-5.14.1.tar.gz"
    local amq_5_15_0_tar_name="activemq-5.15.0.tar.gz"
    local amq_5_15_3_tar_name="activemq-5.15.3.tar.gz"

    mkdir "$amq_5_14_0_tars_path"
    download_resource $amq_5_14_0_web "$amq_5_14_0_tars_path" $amq_5_14_0_tar_name
    download_resource $amq_5_14_1_web "$amq_5_14_0_tars_path" $amq_5_14_1_tar_name
    download_resource $amq_5_15_0_web "$amq_5_14_0_tars_path" $amq_5_15_0_tar_name
    download_resource $amq_5_15_3_web "$amq_5_14_0_tars_path" $amq_5_15_3_tar_name

    # amq 5.14.1 (5.14.1, 5.14.2)

    local amq_5_14_1_tars_path="$(pwd)/Benchmark/Activemq/v5.14.1/tar"

    local amq_5_14_1_web="https://archive.apache.org/dist/activemq/5.14.1/apache-activemq-5.14.1-bin.tar.gz"
    local amq_5_14_2_web="https://archive.apache.org/dist/activemq/5.14.2/apache-activemq-5.14.2-bin.tar.gz"
    local amq_5_14_1_tar_name="activemq-5.14.1.tar.gz"
    local amq_5_14_2_tar_name="activemq-5.14.2.tar.gz"

    mkdir "$amq_5_14_1_tars_path"
    download_resource $amq_5_14_1_web "$amq_5_14_1_tars_path" $amq_5_14_1_tar_name
    download_resource $amq_5_14_2_web "$amq_5_14_1_tars_path" $amq_5_14_2_tar_name

    # amq 5.15.0 (5.15.0, 5.15.1, 5.15.9)
    local amq_5_15_0_tars_path="$(pwd)/Benchmark/Activemq/v5.15.0/tar"

    local amq_5_15_0_web="https://archive.apache.org/dist/activemq/5.15.0/apache-activemq-5.15.0-bin.tar.gz"
    local amq_5_15_1_web="https://archive.apache.org/dist/activemq/5.15.1/apache-activemq-5.15.1-bin.tar.gz"
    local amq_5_15_9_web="https://archive.apache.org/dist/activemq/5.15.9/apache-activemq-5.15.9-bin.tar.gz"
    local amq_5_15_0_tar_name="activemq-5.15.0.tar.gz"
    local amq_5_15_1_tar_name="activemq-5.15.1.tar.gz"
    local amq_5_15_9_tar_name="activemq-5.15.9.tar.gz"

    mkdir "$amq_5_15_0_tars_path"
    download_resource $amq_5_15_0_web "$amq_5_15_0_tars_path" $amq_5_15_0_tar_name
    download_resource $amq_5_15_1_web "$amq_5_15_0_tars_path" $amq_5_15_1_tar_name
    download_resource $amq_5_15_9_web "$amq_5_15_0_tars_path" $amq_5_15_9_tar_name
}

function download_cas {
    local cas_2_2_16_tar_path="$(pwd)/Benchmark/Cassandra/v2.2.16/tar"
    local cas_3_7_tar_path="$(pwd)/Benchmark/Cassandra/v3.7/tar"
    local cas_3_11_2_tar_path="$(pwd)/Benchmark/Cassandra/v3.11.2/tar"
    local cas_3_11_3_tar_path="$(pwd)/Benchmark/Cassandra/v3.11.3/tar"
    local cas_3_11_6_tar_path="$(pwd)/Benchmark/Cassandra/v3.11.6/tar"

    mkdir "$cas_2_2_16_tar_path"
    mkdir "$cas_3_7_tar_path"
    mkdir "$cas_3_11_2_tar_path"
    mkdir "$cas_3_11_3_tar_path"
    mkdir "$cas_3_11_6_tar_path"

    # cas 2.2.16 (2.2.16, 2.2.17, 2.2.18)
    local cas_2_2_16_web="https://archive.apache.org/dist/cassandra/2.2.16/apache-cassandra-2.2.16-bin.tar.gz"
    local cas_2_2_17_web="https://archive.apache.org/dist/cassandra/2.2.17/apache-cassandra-2.2.17-bin.tar.gz"
    local cas_2_2_18_web="https://archive.apache.org/dist/cassandra/2.2.18/apache-cassandra-2.2.18-bin.tar.gz"
    local cas_2_2_16_tar_name="apache-cassandra-2.2.16.tar.gz"
    local cas_2_2_17_tar_name="apache-cassandra-2.2.17.tar.gz"
    local cas_2_2_18_tar_name="apache-cassandra-2.2.18.tar.gz"

    download_resource $cas_2_2_16_web "$cas_2_2_16_tar_path" $cas_2_2_16_tar_name
    download_resource $cas_2_2_17_web "$cas_2_2_16_tar_path" $cas_2_2_17_tar_name
    download_resource $cas_2_2_18_web "$cas_2_2_16_tar_path" $cas_2_2_18_tar_name

    # cas 3.7(3.7, 3,8)
    local cas_3_7_web="https://archive.apache.org/dist/cassandra/3.7/apache-cassandra-3.7-bin.tar.gz"
    local cas_3_8_web="https://archive.apache.org/dist/cassandra/3.8/apache-cassandra-3.8-bin.tar.gz"
    local cas_3_7_tar_name="apache-cassandra-3.7.tar.gz"
    local cas_3_8_tar_name="apache-cassandra-3.8.tar.gz"

    download_resource $cas_3_7_web "$cas_3_7_tar_path" $cas_3_7_tar_name
    download_resource $cas_3_8_web "$cas_3_7_tar_path" $cas_3_8_tar_name

    # cas 3.11.2 (3.11.2, 3.11.3)
    local cas_3_11_2_web="https://archive.apache.org/dist/cassandra/3.11.2/apache-cassandra-3.11.2-bin.tar.gz"
    local cas_3_11_3_web="https://archive.apache.org/dist/cassandra/3.11.3/apache-cassandra-3.11.3-bin.tar.gz"
    local cas_3_11_2_tar_name="apache-cassandra-3.11.2.tar.gz"
    local cas_3_11_3_tar_name="apache-cassandra-3.11.3.tar.gz"

    download_resource $cas_3_11_2_web "$cas_3_11_2_tar_path" $cas_3_11_2_tar_name
    download_resource $cas_3_11_3_web "$cas_3_11_2_tar_path" $cas_3_11_3_tar_name

    # cas 3.11.3 (3.11.3, 3.11.7)
    local cas_3_11_3_web="https://archive.apache.org/dist/cassandra/3.11.3/apache-cassandra-3.11.3-bin.tar.gz"
    local cas_3_11_7_web="https://archive.apache.org/dist/cassandra/3.11.7/apache-cassandra-3.11.7-bin.tar.gz"
    local cas_3_11_3_tar_name="apache-cassandra-3.11.3.tar.gz"
    local cas_3_11_7_tar_name="apache-cassandra-3.11.7.tar.gz"

    download_resource $cas_3_11_3_web "$cas_3_11_3_tar_path" $cas_3_11_3_tar_name
    download_resource $cas_3_11_7_web "$cas_3_11_3_tar_path" $cas_3_11_7_tar_name

    # cas 3.11.6(3.11.6 3.11.11 3.11.12 4.0.4)
    local cas_3_11_6_web="https://archive.apache.org/dist/cassandra/3.11.6/apache-cassandra-3.11.6-bin.tar.gz"
    local cas_3_11_11_web="https://archive.apache.org/dist/cassandra/3.11.11/apache-cassandra-3.11.11-bin.tar.gz"
    local cas_3_11_12_web="https://archive.apache.org/dist/cassandra/3.11.12/apache-cassandra-3.11.12-bin.tar.gz"
    local cas_4_0_4_web="https://archive.apache.org/dist/cassandra/4.0.4/apache-cassandra-4.0.4-bin.tar.gz"
    local cas_3_11_6_tar_name="apache-cassandra-3.11.6.tar.gz"
    local cas_3_11_11_tar_name="apache-cassandra-3.11.11.tar.gz"
    local cas_3_11_12_tar_name="apache-cassandra-3.11.12.tar.gz"
    local cas_4_0_4_tar_name="apache-cassandra-4.0.4.tar.gz"

    download_resource $cas_3_11_6_web "$cas_3_11_6_tar_path" $cas_3_11_6_tar_name
    download_resource $cas_3_11_11_web "$cas_3_11_6_tar_path" $cas_3_11_11_tar_name
    download_resource $cas_3_11_12_web "$cas_3_11_6_tar_path" $cas_3_11_12_tar_name
    download_resource $cas_4_0_4_web "$cas_3_11_6_tar_path" $cas_4_0_4_tar_name
}

function download_hdfs {
	local hdfs_2_7_0_tar_path="$(pwd)/Benchmark/Hadoop/v2.7.0/tar"
	local hdfs_3_1_2_tar_path="$(pwd)/Benchmark/Hadoop/v3.1.2/tar"
	local hdfs_3_2_0_tar_path="$(pwd)/Benchmark/Hadoop/v3.2.0/tar"
	
	mkdir "$hdfs_2_7_0_tar_path"
	mkdir "$hdfs_3_1_2_tar_path"
	mkdir "$hdfs_3_2_0_tar_path"
	
	# 2.7.0(2.7.0, 2.7.2, 2.7.3, 2.7.4)
	local hdfs_2_7_0_web="https://archive.apache.org/dist/hadoop/common/hadoop-2.7.0/hadoop-2.7.0.tar.gz"
	local hdfs_2_7_2_web="https://archive.apache.org/dist/hadoop/common/hadoop-2.7.2/hadoop-2.7.2.tar.gz"
	local hdfs_2_7_3_web="https://archive.apache.org/dist/hadoop/common/hadoop-2.7.3/hadoop-2.7.3.tar.gz"
	local hdfs_2_7_4_web="https://archive.apache.org/dist/hadoop/common/hadoop-2.7.4/hadoop-2.7.4.tar.gz"
	local hdfs_2_7_0_tar_name="hadoop-2.7.0.tar.gz"
	local hdfs_2_7_2_tar_name="hadoop-2.7.2.tar.gz"
	local hdfs_2_7_3_tar_name="hadoop-2.7.3.tar.gz"
	local hdfs_2_7_4_tar_name="hadoop-2.7.4.tar.gz"
	
				
	download_resource $hdfs_2_7_0_web "$hdfs_2_7_0_tar_path" $hdfs_2_7_0_tar_name
	download_resource $hdfs_2_7_2_web "$hdfs_2_7_0_tar_path" $hdfs_2_7_2_tar_name
	download_resource $hdfs_2_7_3_web "$hdfs_2_7_0_tar_path" $hdfs_2_7_3_tar_name
	download_resource $hdfs_2_7_4_web "$hdfs_2_7_0_tar_path" $hdfs_2_7_4_tar_name
	
	# 3.1.2(3.1.3, 3.1.4, 3.3.1)
	local hdfs_3_1_2_web="https://archive.apache.org/dist/hadoop/common/hadoop-3.1.2/hadoop-3.1.2.tar.gz"
	local hdfs_3_1_3_web="https://archive.apache.org/dist/hadoop/common/hadoop-3.1.3/hadoop-3.1.3.tar.gz"
	local hdfs_3_1_4_web="https://archive.apache.org/dist/hadoop/common/hadoop-3.1.4/hadoop-3.1.4.tar.gz"
	local hdfs_3_3_1_web="https://archive.apache.org/dist/hadoop/common/hadoop-3.3.1/hadoop-3.3.1.tar.gz"
	local hdfs_3_1_2_tar_name="hadoop-3.1.2.tar.gz"
	local hdfs_3_1_3_tar_name="hadoop-3.1.3.tar.gz"
	local hdfs_3_1_4_tar_name="hadoop-3.1.4.tar.gz"
	local hdfs_3_3_1_tar_name="hadoop-3.3.1.tar.gz"
	
				
	download_resource $hdfs_3_1_2_web "$hdfs_3_1_2_tar_path" $hdfs_3_1_2_tar_name
	download_resource $hdfs_3_1_3_web "$hdfs_3_1_2_tar_path" $hdfs_3_1_3_tar_name
	download_resource $hdfs_3_1_4_web "$hdfs_3_1_2_tar_path" $hdfs_3_1_4_tar_name
	download_resource $hdfs_3_3_1_web "$hdfs_3_1_2_tar_path" $hdfs_3_3_1_tar_name
	
	# 3.2.0 (3.2.0, 3.2.2, 3.3.1)
	local hdfs_3_2_0_web="https://archive.apache.org/dist/hadoop/common/hadoop-3.2.0/hadoop-3.2.0.tar.gz"
	local hdfs_3_2_2_web="https://archive.apache.org/dist/hadoop/common/hadoop-3.2.2/hadoop-3.2.2.tar.gz"
	local hdfs_3_3_1_web="https://archive.apache.org/dist/hadoop/common/hadoop-3.3.1/hadoop-3.3.1.tar.gz"
	local hdfs_3_2_0_tar_name="hadoop-3.2.0.tar.gz"
	local hdfs_3_2_2_tar_name="hadoop-3.2.2.tar.gz"
	local hdfs_3_3_1_tar_name="hadoop-3.3.1.tar.gz"
	
				
	download_resource $hdfs_3_2_0_web "$hdfs_3_2_0_tar_path" $hdfs_3_2_0_tar_name
	download_resource $hdfs_3_2_2_web "$hdfs_3_2_0_tar_path" $hdfs_3_2_2_tar_name
	download_resource $hdfs_3_3_1_web "$hdfs_3_2_0_tar_path" $hdfs_3_3_1_tar_name
}


function download_hbase {
	local hbase_1_4_0_tar_path="$(pwd)/Benchmark/Hbase/v1.4.0/tar"
	local hbase_2_2_2_tar_path="$(pwd)/Benchmark/Hbase/v2.2.2/tar"
	local hbase_2_4_9_tar_path="$(pwd)/Benchmark/Hbase/v2.4.9/tar"
	
	mkdir "$hbase_1_4_0_tar_path"
	mkdir "$hbase_2_2_2_tar_path"
	mkdir "$hbase_2_4_9_tar_path"

	# 1.4.0(1.4.0, 1.5.0)
	local hbase_1_4_0_web="https://archive.apache.org/dist/hbase/1.4.0/hbase-1.4.0-bin.tar.gz"
	local hbase_1_5_0_web="https://archive.apache.org/dist/hbase/1.5.0/hbase-1.5.0-bin.tar.gz"
	local hbase_1_4_0_tar_name="hbase-1.4.0.tar.gz"
	local hbase_1_5_0_tar_name="hbase-1.5.0.tar.gz"
	
	download_resource $hbase_1_4_0_web "$hbase_1_4_0_tar_path" $hbase_1_4_0_tar_name
	download_resource $hbase_1_5_0_web "$hbase_1_4_0_tar_path" $hbase_1_5_0_tar_name
	
	# 2.2.2(2.2.2, 2.2.5, 2.2.6, 2.3.0)
	local hbase_2_2_2_web="https://archive.apache.org/dist/hbase/2.2.2/hbase-2.2.2-bin.tar.gz"
	local hbase_2_2_5_web="https://archive.apache.org/dist/hbase/2.2.5/hbase-2.2.5-bin.tar.gz"
	local hbase_2_2_6_web="https://archive.apache.org/dist/hbase/2.2.6/hbase-2.2.6-bin.tar.gz"
	local hbase_2_3_0_web="https://archive.apache.org/dist/hbase/2.3.0/hbase-2.3.0-bin.tar.gz"
	local hbase_2_2_2_tar_name="hbase-2.2.2.tar.gz"
	local hbase_2_2_5_tar_name="hbase-2.2.5.tar.gz"
	local hbase_2_2_6_tar_name="hbase-2.2.6.tar.gz"
	local hbase_2_3_0_tar_name="hbase-2.3.0.tar.gz"
	download_resource $hbase_2_2_2_web "$hbase_2_2_2_tar_path" $hbase_2_2_2_tar_name
	download_resource $hbase_2_2_5_web "$hbase_2_2_2_tar_path" $hbase_2_2_5_tar_name
	download_resource $hbase_2_2_6_web "$hbase_2_2_2_tar_path" $hbase_2_2_6_tar_name
	download_resource $hbase_2_3_0_web "$hbase_2_2_2_tar_path" $hbase_2_3_0_tar_name
	
	
	# 2.4.9(2.4.9, 2.4.10, 2.4.12, 2.5.0)
	local hbase_2_4_9_web="https://archive.apache.org/dist/hbase/2.4.9/hbase-2.4.9-bin.tar.gz"
	local hbase_2_4_10_web="https://archive.apache.org/dist/hbase/2.4.10/hbase-2.4.10-bin.tar.gz"
	local hbase_2_4_12_web="https://archive.apache.org/dist/hbase/2.4.12/hbase-2.4.12-bin.tar.gz"
	local hbase_2_5_0_web="https://archive.apache.org/dist/hbase/2.5.0/hbase-2.5.0-bin.tar.gz"
	local hbase_2_4_9_tar_name="hbase-2.4.9.tar.gz"
	local hbase_2_4_10_tar_name="hbase-2.4.10.tar.gz"
	local hbase_2_4_12_tar_name="hbase-2.4.12.tar.gz"
	local hbase_2_5_0_tar_name="hbase-2.5.0.tar.gz"
	download_resource $hbase_2_4_9_web "$hbase_2_4_9_tar_path" $hbase_2_4_9_tar_name
	download_resource $hbase_2_4_10_web "$hbase_2_4_9_tar_path" $hbase_2_4_10_tar_name
	download_resource $hbase_2_4_12_web "$hbase_2_4_9_tar_path" $hbase_2_4_12_tar_name
	download_resource $hbase_2_5_0_web "$hbase_2_4_9_tar_path" $hbase_2_5_0_tar_name
}

function download_kafka {
	local kaf_2_0_0_tar_path="$(pwd)/Benchmark/Kafka/v2.0.0/tar"
	local kaf_2_8_0_tar_path="$(pwd)/Benchmark/Kafka/v2.8.0/tar"
	local kaf_3_0_0_tar_path="$(pwd)/Benchmark/Kafka/v3.0.0/tar"
	local kaf_3_3_1_tar_path="$(pwd)/Benchmark/Kafka/v3.3.1/tar"
	
	mkdir "$kaf_2_0_0_tar_path"
	mkdir "$kaf_2_8_0_tar_path"
	mkdir "$kaf_3_0_0_tar_path"
	mkdir "$kaf_3_3_1_tar_path"
	
	# 2.0.0(2.0.0, 2.1.0, 2.4.1)
	local kaf_2_0_0_web="https://archive.apache.org/dist/kafka/2.0.0/kafka_2.12-2.0.0.tgz"
	local kaf_2_1_0_web="https://archive.apache.org/dist/kafka/2.1.0/kafka_2.12-2.1.0.tgz"
	local kaf_2_4_1_web="https://archive.apache.org/dist/kafka/2.4.1/kafka_2.12-2.4.1.tgz"
	local kaf_2_0_0_tar_name="kafka_2.12-2.0.0.tar.gz"
	local kaf_2_1_0_tar_name="kafka_2.12-2.1.0.tar.gz"
	local kaf_2_4_1_tar_name="kafka_2.12-2.4.1.tar.gz"
	download_resource $kaf_2_0_0_web "$kaf_2_0_0_tar_path" $kaf_2_0_0_tar_name
	download_resource $kaf_2_1_0_web "$kaf_2_0_0_tar_path" $kaf_2_1_0_tar_name
	download_resource $kaf_2_4_1_web "$kaf_2_0_0_tar_path" $kaf_2_4_1_tar_name
	
	# 2.8.0(2.8.0, 2.8.2, 3.0.0, 3.2.0)
	local kaf_2_8_0_web="https://archive.apache.org/dist/kafka/2.8.0/kafka_2.13-2.8.0.tgz"
	local kaf_2_8_2_web="https://archive.apache.org/dist/kafka/2.8.2/kafka_2.13-2.8.2.tgz"
	local kaf_3_0_0_web="https://archive.apache.org/dist/kafka/3.0.0/kafka_2.13-3.0.0.tgz"
	local kaf_3_2_0_web="https://archive.apache.org/dist/kafka/3.2.0/kafka_2.13-3.2.0.tgz"
	local kaf_2_8_0_tar_name="kafka_2.13-2.8.0.tar.gz"
	local kaf_2_8_2_tar_name="kafka_2.13-2.8.2.tar.gz"
	local kaf_3_0_0_tar_name="kafka_2.13-3.0.0.tar.gz"
	local kaf_3_2_0_tar_name="kafka_2.13-3.2.0.tar.gz"
	download_resource $kaf_2_8_0_web "$kaf_2_8_0_tar_path" $kaf_2_8_0_tar_name
	download_resource $kaf_2_8_2_web "$kaf_2_8_0_tar_path" $kaf_2_8_2_tar_name
	download_resource $kaf_3_0_0_web "$kaf_2_8_0_tar_path" $kaf_3_0_0_tar_name
	download_resource $kaf_3_2_0_web "$kaf_2_8_0_tar_path" $kaf_3_2_0_tar_name
	
	# 3.0.0(3.0.0, 3.0.1, 3.5.0)
	local kaf_3_0_0_web="https://archive.apache.org/dist/kafka/3.0.0/kafka_2.13-3.0.0.tgz"
	local kaf_3_0_1_web="https://archive.apache.org/dist/kafka/3.0.1/kafka_2.13-3.0.1.tgz"
	local kaf_3_5_0_web="https://archive.apache.org/dist/kafka/3.5.0/kafka_2.13-3.5.0.tgz"
	local kaf_3_0_0_tar_name="kafka_2.13-3.0.0.tar.gz"
	local kaf_3_0_1_tar_name="kafka_2.13-3.0.1.tar.gz"
	local kaf_3_5_0_tar_name="kafka_2.13-3.5.0.tar.gz"
	download_resource $kaf_3_0_0_web "$kaf_3_0_0_tar_path" $kaf_3_0_0_tar_name
	download_resource $kaf_3_0_1_web "$kaf_3_0_0_tar_path" $kaf_3_0_1_tar_name
	download_resource $kaf_3_5_0_web "$kaf_3_0_0_tar_path" $kaf_3_5_0_tar_name
	
	# 3.3.1(3.3.1, 3.3.2)
	local kaf_3_3_1_web="https://archive.apache.org/dist/kafka/3.3.1/kafka_2.13-3.3.1.tgz"
	local kaf_3_3_2_web="https://archive.apache.org/dist/kafka/3.3.2/kafka_2.13-3.3.2.tgz"
	local kaf_3_3_1_tar_name="kafka_2.13-3.3.1.tar.gz"
	local kaf_3_3_2_tar_name="kafka_2.13-3.3.2.tar.gz"
	download_resource $kaf_3_3_1_web "$kaf_3_3_1_tar_path" $kaf_3_3_1_tar_name
	download_resource $kaf_3_3_2_web "$kaf_3_3_1_tar_path" $kaf_3_3_2_tar_name
}


function download_rmq {
	local rmq_4_0_0_tar_path="$(pwd)/Benchmark/Rocketmq/v4.0.0/tar"
	local rmq_4_1_0_tar_path="$(pwd)/Benchmark/Rocketmq/v4.1.0/tar"
	local rmq_4_5_0_tar_path="$(pwd)/Benchmark/Rocketmq/v4.5.0/tar"
	local rmq_4_9_0_tar_path="$(pwd)/Benchmark/Rocketmq/v4.9.0/tar"
	
	mkdir "$rmq_4_0_0_tar_path"
	mkdir "$rmq_4_1_0_tar_path"
	mkdir "$rmq_4_5_0_tar_path"
	mkdir "$rmq_4_9_0_tar_path"
	
	# 4.0.0(4.0.0, 4.2.0)
	local rmq_4_0_0_web="https://archive.apache.org/dist/rocketmq/4.0.0-incubating/rocketmq-all-4.0.0-incubating-bin-release.zip"
	local rmq_4_2_0_web="https://archive.apache.org/dist/rocketmq/4.2.0/rocketmq-all-4.2.0-bin-release.zip"
	local rmq_4_0_0_tar_name="rocketmq-4.0.0.zip"
	local rmq_4_2_0_tar_name="rocketmq-4.2.0.zip"
	download_resource $rmq_4_0_0_web "$rmq_4_0_0_tar_path" $rmq_4_0_0_tar_name
	download_resource $rmq_4_2_0_web "$rmq_4_0_0_tar_path" $rmq_4_2_0_tar_name
	
	# 4.1.0(4.1.0, 4.2.0)
	local rmq_4_1_0_web="https://archive.apache.org/dist/rocketmq/4.1.0-incubating/rocketmq-all-4.1.0-incubating-bin-release.zip"
	local rmq_4_2_0_web="https://archive.apache.org/dist/rocketmq/4.2.0/rocketmq-all-4.2.0-bin-release.zip"
	local rmq_4_1_0_tar_name="rocketmq-4.1.0.zip"
	local rmq_4_2_0_tar_name="rocketmq-4.2.0.zip"
	download_resource $rmq_4_1_0_web "$rmq_4_1_0_tar_path" $rmq_4_1_0_tar_name
	download_resource $rmq_4_2_0_web "$rmq_4_1_0_tar_path" $rmq_4_2_0_tar_name	
	
	# 4.5.0(4.5.0, 4.7.0)
	local rmq_4_5_0_web="https://archive.apache.org/dist/rocketmq/4.5.0/rocketmq-all-4.5.0-bin-release.zip"
	local rmq_4_7_0_web="https://archive.apache.org/dist/rocketmq/4.7.0/rocketmq-all-4.7.0-bin-release.zip"
	local rmq_4_5_0_tar_name="rocketmq-4.5.0.zip"
	local rmq_4_7_0_tar_name="rocketmq-4.7.0.zip"
	download_resource $rmq_4_5_0_web "$rmq_4_5_0_tar_path" $rmq_4_5_0_tar_name
	download_resource $rmq_4_7_0_web "$rmq_4_5_0_tar_path" $rmq_4_7_0_tar_name
	
	
	# 4.9.0(4.9.0, 4.9.1, 4.9.2, 4.9.3)
	local rmq_4_9_0_web="https://archive.apache.org/dist/rocketmq/4.9.0/rocketmq-all-4.9.0-bin-release.zip"
	local rmq_4_9_1_web="https://archive.apache.org/dist/rocketmq/4.9.1/rocketmq-all-4.9.1-bin-release.zip"
	local rmq_4_9_2_web="https://archive.apache.org/dist/rocketmq/4.9.2/rocketmq-all-4.9.2-bin-release.zip"
	local rmq_4_9_3_web="https://archive.apache.org/dist/rocketmq/4.9.3/rocketmq-all-4.9.3-bin-release.zip"
	local rmq_4_9_0_tar_name="rocketmq-4.9.0.zip"
	local rmq_4_9_1_tar_name="rocketmq-4.9.1.zip"
	local rmq_4_9_2_tar_name="rocketmq-4.9.2.zip"
	local rmq_4_9_3_tar_name="rocketmq-4.9.3.zip"
	download_resource $rmq_4_9_0_web "$rmq_4_9_0_tar_path" $rmq_4_9_0_tar_name
	download_resource $rmq_4_9_1_web "$rmq_4_9_0_tar_path" $rmq_4_9_1_tar_name
	download_resource $rmq_4_9_2_web "$rmq_4_9_0_tar_path" $rmq_4_9_2_tar_name
	download_resource $rmq_4_9_3_web "$rmq_4_9_0_tar_path" $rmq_4_9_3_tar_name
	
}

function download_zk {
	local zk_3_5_0_tar_path="$(pwd)/Benchmark/Zookeeper/v3.5.0/tar"
	local zk_3_7_1_tar_path="$(pwd)/Benchmark/Zookeeper/v3.7.1/tar"
	local zk_3_4_2_tar_path="$(pwd)/Benchmark/Zookeeper/v3.4.2/tar"
	local zk_3_6_0_tar_path="$(pwd)/Benchmark/Zookeeper/v3.6.0/tar"
	
	mkdir "$zk_3_5_0_tar_path"
	mkdir "$zk_3_7_1_tar_path"
	mkdir "$zk_3_4_2_tar_path"
	mkdir "$zk_3_6_0_tar_path"
	
	# 3.5.0(3.5.0, 3.5.1, 3.5.2)
	local zk_3_5_0_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.5.0-alpha/zookeeper-3.5.0-alpha.tar.gz"
	local zk_3_5_1_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.5.1-alpha/zookeeper-3.5.1-alpha.tar.gz"
	local zk_3_5_2_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.5.2-alpha/zookeeper-3.5.2-alpha.tar.gz"
	local zk_3_5_4_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.5.4-beta/zookeeper-3.5.4-beta.tar.gz"
	local zk_3_5_0_tar_name="zookeeper-3.5.0.tar.gz"
	local zk_3_5_1_tar_name="zookeeper-3.5.1.tar.gz"
	local zk_3_5_2_tar_name="zookeeper-3.5.2.tar.gz"
	local zk_3_5_4_tar_name="zookeeper-3.5.4.tar.gz"
	download_resource $zk_3_5_0_web "$zk_3_5_0_tar_path" $zk_3_5_0_tar_name
	download_resource $zk_3_5_1_web "$zk_3_5_0_tar_path" $zk_3_5_1_tar_name
	download_resource $zk_3_5_2_web "$zk_3_5_0_tar_path" $zk_3_5_2_tar_name
	download_resource $zk_3_5_4_web "$zk_3_5_0_tar_path" $zk_3_5_4_tar_name
	
	# 3.7.1(3.7.1, 3.8.1, 3.9.0)
	local zk_3_7_1_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.7.1/apache-zookeeper-3.7.1-bin.tar.gz"
	local zk_3_8_1_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.8.1/apache-zookeeper-3.8.1-bin.tar.gz"
	local zk_3_9_0_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.9.0/apache-zookeeper-3.9.0-bin.tar.gz"
	local zk_3_7_1_tar_name="zookeeper-3.7.1.tar.gz"
	local zk_3_8_1_tar_name="zookeeper-3.8.1.tar.gz"
	local zk_3_9_0_tar_name="zookeeper-3.9.0.tar.gz"
	download_resource $zk_3_7_1_web "$zk_3_7_1_tar_path" $zk_3_7_1_tar_name
	download_resource $zk_3_8_1_web "$zk_3_7_1_tar_path" $zk_3_8_1_tar_name
	download_resource $zk_3_9_0_web "$zk_3_7_1_tar_path" $zk_3_9_0_tar_name
	
	# 3.4.2(3.4.2, 3.4.3)
	local zk_3_4_2_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.4.2/zookeeper-3.4.2.tar.gz"
	local zk_3_4_3_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.4.3/zookeeper-3.4.3.tar.gz"
	local zk_3_4_2_tar_name="zookeeper-3.4.2.tar.gz"
	local zk_3_4_3_tar_name="zookeeper-3.4.3.tar.gz"
	download_resource $zk_3_4_2_web "$zk_3_4_2_tar_path" $zk_3_4_2_tar_name
	download_resource $zk_3_4_3_web "$zk_3_4_2_tar_path" $zk_3_4_3_tar_name
	
	
	# 3.6.0(3.6.0, 3.7.0)
	local zk_3_6_0_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.6.0/apache-zookeeper-3.6.0-bin.tar.gz"
	local zk_3_7_0_web="https://archive.apache.org/dist/zookeeper/zookeeper-3.7.0/apache-zookeeper-3.7.0-bin.tar.gz"
	local zk_3_6_0_tar_name="zookeeper-3.6.0.tar.gz"
	local zk_3_7_0_tar_name="zookeeper-3.7.0.tar.gz"
	download_resource $zk_3_6_0_web "$zk_3_6_0_tar_path" $zk_3_6_0_tar_name
	download_resource $zk_3_7_0_web "$zk_3_6_0_tar_path" $zk_3_7_0_tar_name
	
}

function chmod_files {
    local dir=$1
    for file in "$dir"/*; do
        if [[ -f "$file" && "$file" =~ (activemq-|apache-cassandra-|hadoop-|hbase-|kafka_|rocketmq-|zookeeper-).*(\.tar\.gz|\.zip) ]]; then
            echo "found file: $file"
            chmod 777 $file
        elif [[ -d "$file" ]]; then
        	base_name=$(basename "$file")
        	if [[ $base_name == 'tar' ]]; then
        		chmod 777 $file
    		fi
            chmod_files "$file"
        fi
    done
}

function generate_tar {
    local dir=$1
    for file in "$dir"/*; do
        if [[ -f "$file" && "$file" =~ (activemq-|apache-cassandra-|hadoop-|hbase-|kafka_|rocketmq-|zookeeper-).*(\.tar\.gz|\.zip) ]]; then
            echo "found file: $file"
            extract_and_compress $file
        elif [[ -d "$file" ]]; then
            # 如果是子目录，则递归调用函数
            generate_tar "$file"
        fi
    done
}

function extract_and_compress {
    local input_file=$1

    # 提取文件名（不包括扩展名）
    local base_name=$(basename "$input_file" | sed 's/\.\(tar\.gz\|zip\)$//')

    # TODO delete this line after DONE
    if [[ ! "$base_name" =~ "hbase" ]]; then
        return
    fi

    # 提取目录路径
    local dir_path=$(dirname "$input_file")

    # 创建临时目录用于解压
    local temp_dir=$(mktemp -d)    

    # 检查文件类型并进行相应的解压
    if [[ "$input_file" == *.tar.gz ]]; then
        tar -xzf "$input_file" -C "$temp_dir"
        orginal_folder_name=$(ls "$temp_dir")
        if [[ $orginal_folder_name !=  $base_name ]]; then
        	echo "renaming ${orginal_folder_name} to ${base_name}"
			mv "$temp_dir/${orginal_folder_name}" "$temp_dir/${base_name}"
		fi
    elif [[ "$input_file" == *.zip ]]; then
        unzip "$input_file" -d "$temp_dir"
    else
        echo "unsupported file"
        return 1
    fi

    # 重新压缩
    if [[ "$input_file" == *.tar.gz ]]; then
        tar -czf "$dir_path/${base_name}.tar.gz" -C "$temp_dir" "$base_name"
    elif [[ "$input_file" == *.zip ]]; then
        cd "$temp_dir" && zip -r "$dir_path/${base_name}.zip" "$base_name"
    fi

    # 清理临时目录
    rm -rf "$temp_dir"

    echo "extract and recompress done for $1"
    echo 
}



main() {
    if [ "$#" -eq 0 ]; then
        echo "Usage: $0 <amq|cas|hdfs|hbase|kafka|rmq|zk> [amq|cas|hdfs|hbase|kafka|rmq|zk] ..."
        exit 1
    fi
    
    # delete_existing_tars "$(pwd)/Benchmark"

    for arg in "$@"; do
        case "$arg" in
            amq) download_amq ;;
            cas) download_cas ;;
            hdfs) download_hdfs ;;
            hbase) download_hbase ;;
            kafka) download_kafka ;;
            rmq) download_rmq ;;
            zk) download_zk ;;
            *) echo "Invalid argument: $arg. Use one of amq, cas, hdfs, hbase, kafka, rmq, or zk." ;;
        esac
    done
    
    chmod_files "$(pwd)/Benchmark"

    generate_tar "$(pwd)/Benchmark"
    
}

# 执行主函数
main "$@"




