#!/bin/bash


# 定义起点目录
base_dir="/home/zdc/DataLinux/code2/RediB/dataset"

# 定义结果目录
result_dir="/home/zdc/performAnalysis"



# 打印提示信息
echo
echo "Running mvn test and collecting docker stats..."
echo

# 递归遍历所有子目录
find "$base_dir" -type d | sort | while read -r dir; do
    # 检查子目录是否包含pom.xml文件
    if [ -e "$dir/pom.xml" ]; then
    	# 打印提示信息
	echo "Deleting working directories using reditWorkingDirDeleter.sh script..."
	echo

	# 执行reditWorkingDirDeleter.sh脚本
	sudo /home/zdc/DataLinux/code2/RediB/dataset/reditWorkingDirDeleter.sh
    
        # 获取目录名作为case-name
        case_name=$(basename "$dir")

	for run in {2..3}; do
            # 生成时间戳
            timestamp=$(date +"%Y%m%d_%H%M%S")

            # 定义结果文件名
            result_file="$result_dir/${case_name}-${timestamp}-run${run}.txt"
            timeout_file="$result_dir/${case_name}-${timestamp}-run${run}-timeout.txt"
            mvn_out_file="$result_dir/${case_name}-${timestamp}-run${run}-mvn-out.txt"
            docker_stats_file="$result_dir/${case_name}-${timestamp}-run${run}-docker_stats.json"
            
            echo "working on $case_name"
            echo
            
            # 先compile一下
            cd "$dir" && mvn compile -Dmaven.repo.local=/home/zdc/.m2/repository

            # 在当前目录下运行mvn test，将输出保存到文件
            (cd "$dir" && timeout 5m mvn test -Dmaven.repo.local=/home/zdc/.m2/repository > "$mvn_out_file" 2>&1) &
            
            # 获取mvn test的进程ID
      	    mvnPID=$!

            # 后台循环，每秒收集docker stats信息
            # (while true; do docker stats --no-stream --format "json" >> "$docker_stats_file"; done) &
                        # 后台循环，每秒收集docker stats信息
            (while true; do
                current_time=$(date +"%Y-%m-%d %H:%M:%S.%3N")
                echo $current_time >> $docker_stats_file
                docker stats --no-stream --format "json" >> "$docker_stats_file"
            done) &
            
            # 获取docker stats的进程ID
	    dockerPID=$!

            # 等待mvn test完成
            wait $mvnPID

            # 停止收集docker stats信息的后台循环
            kill $dockerPID && wait $dockerPID

            # 打印提示信息
            if [ $? -eq 0 ]; then
                echo "Run $run of mvn test completed successfully for case: $case_name"
            elif [ $? -eq 124 ]; then
                echo "Run $run of mvn test timed out for case: $case_name"
            else
                echo "Run $run of mvn test failed for case: $case_name"
            fi

            # 如果mvn test运行时间超过5分钟，重命名文件
            if [ $? -eq 124 ]; then
                mv "$result_file" "$timeout_file"
            fi

            echo
        done
        
    fi
done

# 打印完成信息
echo "Script execution completed."

