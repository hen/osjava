#!/bin/bash

function usage() {
    echo 'Usage:'
    echo ' ./nightly-build.sh all       -  Will build every component specified in NIGHTLY.txt. '
    echo ' ./nightly-build.sh update    -  Will build only components that have had a changed file since the last update. '
    echo ' ./nightly-build.sh <project> -  Will build only the component specified. '
}

rm -f LAST_BUILD SVN_UPDATE REASON

if [ "x$1x" != "xx" ];
then
    if [ $1 = 'all' ];
    then
        LIST=`cat NIGHTLY.txt | awk '{print $2}' | grep -v '^#'`
	echo 'No updates: Built because all components were built. ' > REASON
        for i in `cat NIGHTLY.txt | sed 's/ /::::/'`
        do
            checkoutDir=`echo $i | sed 's/.*:::://'`
            if [ ! -e $checkoutDir ];
            then
                svn co `echo $i | sed 's/::::/\//'` $checkoutDir
            fi
        done
    elif [ $1 = 'update' ];
    then
        LIST=`svn -u status | grep -v '^\?' | grep -v '^A' | grep -v '^M' | grep -v 'Status against revision' | awk '{print $3}' | grep -o -f NIGHTLY.txt  | sort -u`
        svn update | grep -v '^?' > REASON
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

for i in $LIST
do
    rm -fr report/$i
    echo "Building $i"
    echo $i >> LAST_BUILD
    mkdir -p report/$i
    cd $i
    # cleaning
    rm -fr target/ maven.log velocity.log ERROR.log OUTPUT.log
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
    # run checkstyle, pmd, junit report, javadoc, xref
    echo "Building documentation"
    maven -b maven-simian-plugin maven-jdepend-plugin:report maven-jcoverage-plugin:report checkstyle maven-junit-report-plugin:report maven-jxr-plugin:report pmd javadoc 2> DOCS-ERROR.log > DOCS-OUTPUT.log
    
    # somehow create a site from these reports
    if [ -d target/docs/apidocs/ ];
    then
        mv target/docs/apidocs/ $reportDir/$i/javadoc
    fi
    if [ -d target/docs/jcoverage/ ];
    then
        mv target/docs/jcoverage/ $reportDir/$i/jcoverage
    fi
    if [ -f target/generated-xdocs/simian-report.xml ];
    then
        mv target/generated-xdocs/simian-report.xml $reportDir/$i
    fi
    if [ -f target/generated-xdocs/jdepend-report.xml ];
    then
        mv target/generated-xdocs/jdepend-report.xml $reportDir/$i
    fi
    if [ -f target/generated-xdocs/pmd-report.xml ];
    then
        mv target/generated-xdocs/pmd-report.xml $reportDir/$i
    fi
    if [ -d target/docs/xref/ ];
    then
        mv target/docs/xref $reportDir/$i
    fi
    if [ -d target/docs/xref-test/ ];
    then
        mv target/docs/xref-test $reportDir/$i
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
        rm -f $reportDir/$i/*.jar
        mv target/$i*.jar $reportDir/$i/$i-`date +%Y%m%d`.jar
    fi
    date +"%Y/%m/%d %k:%M" > $reportDir/$i/BUILD_TIME
    if [ -e $buildDir/SVN_UPDATE ];
    then
        cat $buildDir/SVN_UPDATE | grep ^$i > $reportDir/$i/REASON
    fi
    if [ -e $buildDir/REASON ];
    then
        cat $buildDir/REASON > $reportDir/$i/REASON
    fi
    echo $BUILD_DURATION > $reportDir/$i/BUILD_DURATION

    echo "Finished with $i"
    cd -
done
