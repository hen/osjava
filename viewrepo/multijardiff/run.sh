#!/bin/bash

multidoc_repo=$1
target=$2
# TODO: Add flag to ignore snapshots

mkdir -p $target

for i in ${multidoc_repo}/*/jars.list
do
    name=`echo $i | sed 's/.*\/\([^\/]*\)\/jars.list/\1/'`
    NUM_JARS=`cat $i | wc -l`
    if [ "$NUM_JARS" -lt 2 ]
    then
        echo "Skipping ${name} - only one jar"
    else
        mkdir -p ${target}/${name}/
        echo "Generating data for ${name}"
         ./gen.rb $i > ${target}/${name}/releases.dat
        echo "Rendering chart for ${name}"
        java -classpath lib/jfreechart-1.0.1.jar:lib/jcommon-1.0.4.jar:. -Djava.awt.headless=true CreateChart ${target}/${name}/releases.dat "${name}" ${target}/${name}/releases.png
        java -Djava.awt.headless=true CreateBar ${target}/${name}/releases.dat ${target}/${name}/bars/
    fi
done
