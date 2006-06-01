
maven1=$1

for i in ${maven1}/*/jars
do
    name=`echo $i | sed 's/.*\/\([^\/]*\)\/jars/\1/'`
    NUM_JARS=`ls -1 $i/*.jar | wc -l`
    if [ "$NUM_JARS" -lt 2 ]
    then
        echo "Skipping ${name} - only one jar"
    else
        echo "Generating data for ${name}"
        ./gen.rb $i/*.jar > multijardiff/${name}.xml
        echo "Rendering chart for ${name}"
        java -classpath lib/jfreechart-1.0.1.jar:lib/jcommon-1.0.4.jar:. -Djava.awt.headless=true CreateChart multijardiff/${name}.xml "${name}" multijardiff/${name}.png
    fi
done

