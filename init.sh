#!/bin/bash

# delete existing tars
function delete_existing_tars {
    local dir=$1

    # 遍历目录中的文件和子目录
    for file in "$dir"/*; do
        if [[ -f "$file" && "$file" =~ (activemq-|apache-cassandra-|hadoop-|hbase-|kafka_|rocketmq-|zookeeper-).*\.tar\.gz ]]; then
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
    local download_path=$2
    local filename=$3

    # 如果使用wget，可以用以下命令
    wget "$url" -O "$download_path/$filename"
}

function download_amq {
	# amq 5.12.0 (5.12.0, 5.12.2, 5.13.1)
    local amq_5_12_0_tars_path="$(pwd)/Benchmark/Activemq/v5.12.0/tars"

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
    local amq_5_14_0_tars_path="$(pwd)/Benchmark/Activemq/v5.14.0/tars"

    local amq_5_14_0_web="https://archive.apache.org/dist/activemq/5.14.0/apache-activemq-5.14.0-bin.tar.gz"
    local amq_5_14_1_web="https://archive.apache.org/dist/activemq/5.14.1/apache-activemq-5.14.1-bin.tar.gz"
    local amq_5_15_0_web="https://archive.apache.org/dist/activemq/5.15.0/apache-activemq-5.15.0-bin.tar.gz"
    local amq_5_15_3_web="https://archive.apache.org/dist/activemq/5.15.3/apache-activemq-5.15.3-bin.tar.gz"
    local amq_5_14_0_tar_name="activemq-5.14.0.tar.gz"
    local amq_5_14_1_tar_name="activemq-5.14.1.tar.gz"
    local amq_5_15_0_tar_name="activemq-5.15.0.tar.gz"
    local amq_5_15_3_tar_name="activemq-5.15.3.tar.gz"

    mkdir "amq_5_14_0_tars_path"
    download_resource $amq_5_14_0_web "$amq_5_14_0_tars_path" $amq_5_14_0_tar_name
    download_resource $amq_5_14_1_web "$amq_5_14_0_tars_path" $amq_5_14_1_tar_name
    download_resource $amq_5_15_0_web "$amq_5_14_0_tars_path" $amq_5_15_0_tar_name
    download_resource $amq_5_15_3_web "$amq_5_14_0_tars_path" $amq_5_15_3_tar_name

    # amq 5.14.1 (5.14.1, 5.14.2)

    local amq_5_14_1_tars_path="$(pwd)/Benchmark/Activemq/v5.14.1/tars"

    local amq_5_14_1_web="https://archive.apache.org/dist/activemq/5.14.1/apache-activemq-5.14.1-bin.tar.gz"
    local amq_5_14_2_web="https://archive.apache.org/dist/activemq/5.14.2/apache-activemq-5.14.2-bin.tar.gz"
    local amq_5_14_1_tar_name="activemq-5.14.1.tar.gz"
    local amq_5_14_2_tar_name="activemq-5.14.2.tar.gz"

    mkdir "amq_5_14_1_tars_path"
    download_resource $amq_5_14_1_web "$amq_5_14_1_tars_path" $amq_5_14_1_tar_name
    download_resource $amq_5_14_2_web "$amq_5_14_1_tars_path" $amq_5_14_2_tar_name

    # amq 5.15.0 (5.15.0, 5.15.1, 5.15.9)
    local amq_5_15_0_tars_path="$(pwd)/Benchmark/Activemq/v5.15.0/tars"

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
    local cas_2_2_16_tar_path="$(pwd)/Benchmark/Cassandra/v2.2.16/tars"
    local cas_3_7_tar_path="$(pwd)/Benchmark/Cassandra/v3.7/tars"
    local cas_3_11_2_tar_path="$(pwd)/Benchmark/Cassandra/v3.11.2/tars"
    local cas_3_11_3_tar_path="$(pwd)/Benchmark/Cassandra/v3.11.3/tars"
    local cas_3_11_6_tar_path="$(pwd)/Benchmark/Cassandra/v3.11.6/tars"

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

download_hdfs{

}


delete_existing_tars "$(pwd)/Benchmark"
download_amq

# TODO chmod 777 all files
# apt install wget



}