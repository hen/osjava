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
    echo ' ./nightly-build.sh all    -  Will build every component specified in NIGHTLY.txt. '
    echo ' ./nightly-build.sh update -  Will build only components that have had a changed file since the last update. '
}

if [ "x$1x" != "xx" ];
then
    if [ $1 == 'all' ];
    then
        LIST=`cat NIGHTLY.txt | grep -v '^#'`
    elif [ $1 == 'update' ];
    then
        LIST=`svn -u status | grep -v '^?' | grep -v 'Status against revision' | awk '{print $3}' | grep -o -f NIGHTLY.txt  | sort -u`
        svn update | grep -v '^?'
    else
        usage
        exit
    fi
else
    usage
    exit
fi

if [ ! -d report/ ]; 
then
    mkdir report/
fi
reportDir=`pwd`/report

for i in $LIST
do
    silentrm report/$i
#F#    failed=0
    echo "Building $i"
    mkdir -p report/$i
    cd $i
    # cleaning
    silentrm target/ maven.log velocity.log ERROR.log OUTPUT.log
    # building
    maven -b jar 2> ERROR.log > OUTPUT.log
    if [ $? -ne 0 ];
    then
        # create failed report
        echo "Failed to build $i"
        echo "Failed to build $i" > $reportDir/$i/FAILED
        mv ERROR.log OUTPUT.log $reportDir/$i
#F#        failed=1
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
#F#            failed=1
        fi
    fi
    # run checkstyle, pmd, junit report, javadoc
#F#    if [ $failed -eq 0 ];
#F#    then
        echo "Building documentation"
        maven -b checkstyle maven-junit-report-plugin:report pmd javadoc 2> DOCS-ERROR.log > DOCS-OUTPUT.log
#F#    fi
    
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

    echo "Finished with $i"
    cd -
done
