#!/bin/sh
echo "Starting to run the command $*"

java -cp $CLASSPATH:. -verbose:gc -XX:HeapDumpPath=./dump.hprof -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:./gc.log -XX:+PrintFlagsFinal -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC -Xms20M -Xmx20M -Xmn10M $@