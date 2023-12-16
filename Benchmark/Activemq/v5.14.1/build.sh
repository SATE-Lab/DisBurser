#!/bin/bash

def1=-D'AMQ_6500'
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

if [ -f $cFile ]
then
    gcc $def1 $cFile -o $exeFile
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
