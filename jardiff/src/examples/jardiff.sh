#! /bin/sh


if [ ! -f jardiff-0.1.jar ]; then
  wget 'http://www.ibiblio.org/maven/jardiff/jars/jardiff-0.1.jar' ||
  exit 1
fi

if [ ! -f jardiff-0.2.jar ]; then
  wget 'http://www.ibiblio.org/maven/jardiff/jars/jardiff-0.2.jar' ||
  exit 1
fi

java -jar ../../target/jardiff-0.2-uber.jar -f jardiff-0.1.jar \
-t jardiff-0.2.jar -o html -O jardiff-0.1-0.2.html \
-fa "http://dist.osjava.org/releases/multidoc-jnr/jardiff/0.1" \
-ta "http://dist.osjava.org/releases/multidoc-jnr/jardiff/0.2" \
-s "diff.css"
