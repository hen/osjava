#!/bin/bash

export JAVA_HOME=/opt/jdk
PATH=$JAVA_HOME/bin:$PATH

export MAVEN_HOME=/home/hen/apps/maven-1.0.1
PATH=$MAVEN_HOME/bin:$PATH

cd /home/hen/oss/osjava/integration
/usr/bin/svn update report/
./integrate.pl configs/osjava-integration.xml update
/usr/bin/svn status report/ | grep '^\?' | sed 's/\? *//' | xargs /usr/bin/svn add > /dev/null
/usr/bin/svn ci -m 'publishing latest integration changes' report/
cd -
