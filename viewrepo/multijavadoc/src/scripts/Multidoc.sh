#! /bin/sh

unset CLASSPATH

CLASSPATH="."

for i in *.jar; do 
    CLASSPATH="$CLASSPATH:$i"
done

export CLASSPATH

if [ ! -f Multidoc.class ]; then
    javac Multidoc.java
fi

java Multidoc $@
