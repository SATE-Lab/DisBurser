#!/bin/bash

def1=-D'AMQ_6430'
def2=-D'AMQ_6697'
def3=-D'AMQ_6847'
cFile=inject.c
exeFile=inject
srcName=activemq-parent-5.14.0-src
system=activemq-5.14.0
libJar1=activemq-all-5.14.0.jar
libJar2=activemq-broker-5.14.0.jar
libJar3=activemq-stomp-5.14.0.jar
libJar4=activemq-client-5.14.0.jar
libJar5=activemq-kahadb-store-5.14.0.jar
tar=activemq-5.14.0.tar.gz

if [ -f $cFile ]
then
    gcc $def1 $def2 $def3 $cFile -o $exeFile
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

cd ../activemq-stomp
echo "current working directory: `pwd`"
mvn -DskipTests clean install
cp ./target/$libJar3 ../../

cd ../activemq-client
echo "current working directory: `pwd`"
mvn -DskipTests clean install
cp ./target/$libJar4 ../../

cd ../activemq-kahadb-store
echo "current working directory: `pwd`"
mvn -DskipTests clean install
cp ./target/$libJar5 ../../

cd ..
cd ..
cp $libJar1 ./$system/
cp $libJar2 ./$system/lib/
cp $libJar3 ./$system/lib/optional/
cp $libJar4 ./$system/lib/
cp $libJar5 ./$system/lib/
tar -zcvf $tar $system
rm -rf $libJar1 $libJar2 $libJar3 $libJar4 $libJar5
