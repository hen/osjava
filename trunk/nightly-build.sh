#!/bin/bash

# Big nasty rm that blows away quietly
function silentrm() {
    for file in $*
    do
        if [ -f $file ];
        then
            rm $file
        fi
        if [ -d $file ];
        then
            rm -r $file
        fi
    done
}

function usage() {
    echo 'Usage:'
    echo ' ./nightly-build.sh all       -  Will build every component specified in NIGHTLY.txt. '
    echo ' ./nightly-build.sh update    -  Will build only components that have had a changed file since the last update. '
    echo ' ./nightly-build.sh <project> -  Will build only the component specified. '
}

if [ "x$1x" != "xx" ];
then
    if [ $1 = 'all' ];
    then
        LIST=`cat NIGHTLY.txt | grep -v '^#'`
	echo 'No updates: Built because all components were built. ' > REASON
    elif [ $1 = 'update' ];
    then
        LIST=`svn -u status | grep -v '^\?' | grep -v '^A' | grep -v '^M' | grep -v 'Status against revision' | awk '{print $3}' | grep -o -f NIGHTLY.txt  | sort -u`
        svn update | grep -v '^?' > SVN_UPDATE
    else
        LIST=$1   # $* ?
	echo 'No updates: Built because someone specifically chose to build it. ' > REASON
    fi
else
    usage
    exit
fi

if [ ! -d report/ ]; 
then
    mkdir report/
fi
buildDir=`pwd`
reportDir=`pwd`/report

silentrm LAST_BUILD

for i in $LIST
do
    silentrm report/$i
    echo "Building $i"
    echo $i >> LAST_BUILD
    mkdir -p report/$i
    cd $i
    # cleaning
    silentrm target/ maven.log velocity.log ERROR.log OUTPUT.log
    # building
    TIME_START=`date +%s`
    maven -b jar 2> ERROR.log > OUTPUT.log
    TIME_END=`date +%s`
    BUILD_DURATION=`echo $TIME_END - $TIME_START | bc`
    BUILD_DURATION=`date -d "1970-01-01 UTC $BUILD_DURATION seconds" +"%M minutes %S seconds"`
    if [ $? -ne 0 ];
    then
        # create failed report
        echo "Failed to build $i"
        echo "Failed to build $i" > $reportDir/$i/FAILED
        mv ERROR.log OUTPUT.log $reportDir/$i
    fi
    if [ -e ERROR.log ];
    then
        count=`cat ERROR.log | grep -v 'deprecat' | wc -l | sed 's/ //g'`
        if [ $count != '0' ];
        then
            # errors in report
            echo "There were errors in building $i"
            echo "There were errors in building $i" > $reportDir/$i/FAILED
            mv ERROR.log OUTPUT.log $reportDir/$i
        fi
    fi
    # run checkstyle, pmd, junit report, javadoc
    echo "Building documentation"
    maven -b checkstyle maven-junit-report-plugin:report pmd javadoc 2> DOCS-ERROR.log > DOCS-OUTPUT.log
    
    # somehow create a site from these reports
    if [ -d target/docs/apidocs/ ];
    then
        mv target/docs/apidocs/ $reportDir/$i/javadoc
    fi
    if [ -f target/generated-xdocs/pmd-report.xml ];
    then
        mv target/generated-xdocs/pmd-report.xml $reportDir/$i
    fi
    if [ -f target/generated-xdocs/checkstyle-report.xml ];
    then
        mv target/generated-xdocs/checkstyle-report.xml $reportDir/$i
    fi
    if [ -f target/generated-xdocs/junit-report.xml ];
    then
        mv target/generated-xdocs/junit-report.xml $reportDir/$i
    fi
    if [ -f target/$i*.jar ];
    then
        silentrm $reportDir/$i/*.jar
        mv target/$i*.jar $reportDir/$i/$i-`date +%Y%m%d`.jar
    fi
    date +"%Y/%m/%d %k:%M" > $reportDir/$i/BUILD_TIME
    if [ -e SVN_UPDATE ];
    then
        cat $buildDir/SVN_UPDATE | grep ^$i > $reportDir/$i/SVN_UPDATE
    else
        cat $buildDir/REASON > $reportDir/$i/SVN_UPDATE
    fi
    echo $BUILD_DURATION > $reportDir/$i/BUILD_DURATION

    echo "Finished with $i"
    cd -
done
