#!/bin/bash

declare -a defs
current_directory=$(pwd)

function get_defs {
	# 读取FaultSeed.h文件的行数
	n=$(wc -l < FaultSeed.h)
	for ((i=1; i<=n; i++)); do
	  defs[$i]=''
	done
	# 读取FaultSeed.h文件
	i=1
	while read line; do
	  # 判断是否有类似于/* #define AMQ_6059 */或 // #define AMQ_6059 这种格式的宏定义
	  if [[ $line =~ ^/\*[[:space:]]*#define\ ([A-Z0-9_]+)[[:space:]]*\*/ ]]; then
		macro=${BASH_REMATCH[1]}
		defs[$i]=-D"$macro"
	  elif [[ $line =~ ^//[[:space:]]*#define\ ([A-Z0-9_]+) ]]; then
	  	macro=${BASH_REMATCH[1]}
	    defs[$i]=-D"$macro"
	  fi
	  # 增加行号
	  i=$((i+1))
	done < FaultSeed.h
	# 打印所有的defx
	echo "gcc injection parameters are: "
	for ((i=1; i<=n; i++)); do
	  echo "def$i = ${defs[$i]}"
	done
	
}


function build_from_source {
	cFile=inject.c
	exeFile=inject
	srcName=activemq-parent-5.14.1-src
	system=activemq-5.14.1
	libJar1=activemq-all-5.14.1.jar
	libJar2=activemq-broker-5.14.1.jar
	libJar3=activemq-stomp-5.14.1.jar
	libJar4=activemq-client-5.14.1.jar
	libJar5=activemq-kahadb-store-5.14.1.jar
	tar=activemq-5.14.1.tar.gz
	
	gcc_cmd="gcc ${defs[@]} $cFile -o $exeFile"
	echo $gcc_cmd

	if [ -f $cFile ]
	then
	    $gcc_cmd
	    echo "gcc compile success"
	    ./$exeFile
	else
	  echo "Error: $cFile not found !"
	fi

	cd ./$srcName/activemq-all
	echo "current working directory: `pwd`"
	mvn -DskipTests clean install
	cp ./target/$libJar1 ../../

	cd ../activemq-broker
	echo "current working directory: `pwd`"
	mvn -DskipTests clean install
	cp ./target/$libJar2 ../../


	cd ..
	cd ..
	cp $libJar1 ./$system/
	cp $libJar2 ./$system/lib/
	tar -zcvf $tar $system
	rm -rf $libJar1 $libJar2
}

function overwrite_tar {
	tar1="activemq-5.14.1.tar.gz"
	tar2="activemq-5.14.2.tar.gz"
	
	cd "$current_directory"
	
	flag=0
	# 遍历数组元素
	for elem in "${defs[@]}"; do
		# 检查元素长度
		if [ ${#elem} -eq 0 ]; then
		    # 如果元素长度为0，拷贝 tar2 到当前目录并重命名为 tar1
		    cp "./tar/$tar2" "./$tar1"
		    # 修改内层文件名
			local base_name=$(basename "./$tar1" | sed 's/\.\(tar\.gz\|zip\)$//')
			local temp_dir=$(mktemp -d)
			tar -xzf "$current_directory/$tar1" -C "$temp_dir"
			rm -rf "./$tar1"
			orginal_folder_name=$(ls "$temp_dir")
			echo $orginal_folder_name
			mv "$temp_dir/${orginal_folder_name}" "$temp_dir/${base_name}"
			tar -czf "$current_directory/$tar1" -C "$temp_dir" "$base_name"
			rm -rf "$temp_dir"
			echo "Copied $tar2 to ./$tar1"
		    flag=1
		    break
		fi
	done

	# 如果没有满足条件的元素，拷贝 tar1 到当前目录
	if [[ $flag -eq 0 ]]; then
		cp "./tar/$tar1" "./"
		echo "Copied $tar1 to ./"
	fi
	
	chmod 777 $tar1
	
	
}


get_defs
build_from_source
overwrite_tar


