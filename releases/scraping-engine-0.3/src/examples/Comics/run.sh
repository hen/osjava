CP=.
for i in lib/*.jar
do
    CP=$CP:$i
done
java -classpath cron/:$CP org.osjava.oscube.container.Engine
