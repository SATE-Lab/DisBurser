#!/bin/bash

def1=-D'AMQ_6010'
def2=-D'AMQ_6059'
def3=-D'AMQ_6062'
cFile=inject.c
exeFile=inject
srcName=activemq-parent-5.12.0-src
system=activemq-5.12.0
libJar1=activemq-all-5.12.0.jar
libJar2=activemq-broker-5.12.0.jar
libJar3=activemq-amqp-5.12.0.jar
tar=activemq-5.12.0.tar.gz

# Compile C file if exists
if [ -f $cFile ]; then
    gcc $def1 $def2 $def3 $cFile -o $exeFile
    echo "gcc compile success"
    ./$exeFile
else
  echo "Error: $cFile not found!"
fi

# Change directory to compile ActiveMQ

cd ./$srcName/activemq-all
echo "current working directory: `pwd`"
mvn -DskipTests clean install
cp ./target/$libJar1 ../../

cd ../activemq-broker
echo "current working directory: `pwd`"
mvn -DskipTests clean install
cp ./target/$libJar2 ../../

cd ../activemq-amqp
echo "current working directory: `pwd`"
mvn -DskipTests clean install
cp ./target/$libJar3 ../../

# ... Add ActiveMQ compilation commands here ...
# Replace JAR files
cp ./target/$libJar1 ../../
cp ./target/$libJar2 ../../
cp ./target/$libJar3 ../../

# Check if directory exists and create if not
for mq_dir in "$system/mq1" "$system/mq2"; do
    if [ ! -d "$mq_dir" ]; then
        echo "Directory $mq_dir not found. Creating..."
        mkdir -p "$mq_dir"
    fi
    cp "$libJar1" "$mq_dir/"
done

# Package the modified content
tar -czvf $tar $system
echo "Packaging complete: $tar"
