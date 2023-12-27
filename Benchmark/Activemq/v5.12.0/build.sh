#!/bin/bash
#def123用于定义需要注入的bug，没有注释就说明要注入
#在build前确认java版本为1.8，有mvn
#build前需要将release版本和sourcecode版本丢进相应的文件夹
#在release版本对应的目录下需要新建/mq{?}和/mq{?}/lib，然后在redithelper中，指定新的工作目录为这俩
#build自动打包，就进去了
def1=-D'AMQ_6000'
def2=-D'AMQ_6010'
def3=-D'AMQ_6059'
cFile=inject.c
exeFile=inject
srcName=activemq-parent-5.12.0-src
system=activemq-5.12.0
libJar1=activemq-all-5.12.0.jar
libJar2=activemq-broker-5.12.0.jar
libJar3=activemq-amqp-5.12.0.jar
tar=activemq-5.12.0.tar.gz

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

cd ../activemq-amqp
echo "current working directory: `pwd`"
mvn -DskipTests clean install
cp ./target/$libJar3 ../../

cd ..
cd ..
cp $libJar1 ./$system/
cp $libJar2 ./$system/lib/
cp $libJar3 ./$system/lib/optional/ 
tar -zcvf $tar $system
rm -rf $libJar1 $libJar2 $libJar3
