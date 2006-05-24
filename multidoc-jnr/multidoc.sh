#!/bin/sh

CP="."

for i in lib/*.jar; do 
    CP=$CP:$i
done

java -classpath $CP:target/multidoc-0.2.jar org.osjava.multidoc.Multidoc $@
