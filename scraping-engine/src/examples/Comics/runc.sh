CP=.
for i in lib/*.jar
do
    CP=$CP:$i
done
javac -classpath cron/:$CP com/generationjava/*/*.java
