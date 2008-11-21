#! /bin/sh


# Deploy script for mudclient.

java-config -s 1

export JAVA_HOME="$(java-config -O)"

mvn clean install

jarsigner target/mudclient-1.0.jar thawtefreemail

cp target/mudclient-1.0.jar /www/www.cyberiantiger.org/

java-config -s 2
