#!/bin/bash

function usage() {
    echo 'Usage:'
    echo ' ./nightly-build.sh all       -  Will build every component specified in NIGHTLY.txt. '
    echo ' ./nightly-build.sh update    -  Will build only components that have had a changed file since the last update. '
    echo ' ./nightly-build.sh <project> -  Will build only the component specified. '
}

if [ "x${SCM}x" = "xx" ];
then
    export SCM=SVN
fi

rm -f LAST_BUILD SCM_UPDATE REASON
buildDir=`pwd`

if [ "x$1x" != "xx" ];
then
    if [ $1 = 'all' ];
    then
        INIT='forced'
        LIST=`cat NIGHTLY.txt | grep -v '^#' | awk '{print $2}'`
        echo 'Forced build of all components. ' > REASON
        for i in `cat NIGHTLY.txt | grep -v '^#' | sed 's/ /::::/'`
        do
            checkoutDir=`echo $i | sed 's/.*:::://'`
            if [ ! -e $checkoutDir ];
            then
                if [ $SCM = 'SVN' ];
                then
                    svn co `echo $i | sed 's/::::/\//'` $checkoutDir
                fi
                if [ $SCM = 'CVS' ];
                then
                    cvs -d `echo $i | sed 's/::::.*//'` co $checkoutDir
                fi
            fi
        done
    elif [ $1 = 'update' ];
    then
        INIT='update'
        for i in `cat NIGHTLY.txt | grep -v '^#' | awk '{print $2}'`
        do
            if [ -d $i ];
            then
                cd $i
                if [ $SCM = 'SVN' ];
                then
                    UPDATES=`svn -u status | grep -v '^\?' | grep -v '^A' | grep -v '^M' | grep -v 'Status against revision' | awk '{print $3}'`
                fi
                if [ $SCM = 'CVS' ];
                then
                    UPDATES=`cvs -nq update 2>/dev/null | grep -v '^\?' | grep -v '^A' | grep -v '^M' | awk '{print $2}'`
                fi
                if [ "x${UPDATES}x" != "xx" ];
                then
                    LIST="$LIST $i"
#BUG: this does not quite work as we now put these all in the same file and they don't contain the project name
                    if [ $SCM = 'SVN' ];
                    then
                        svn update | grep -v '^?' | sed "s/^/$i /" >> $buildDir/SCM_UPDATE
                    fi
                    if [ $SCM = 'CVS' ];
                    then
                        cvs -q update 2>/dev/null | grep -v '^?' | sed "s/^/$i /" >> $buildDir/SCM_UPDATE
                    fi
                fi
                cd -
            fi
        done
    else
        INIT='forced'
# needs to handle doing the checkout if it's not there?
        LIST=$1   # $* ?
        echo 'Forcd update: Built because someone specifically chose to build it. ' > REASON
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
        if [ $INIT = 'update' ];
        then
            cat $reportDir/$i/ERROR.log | mail -s "Failed to build $i" bayard@osjava.org
        fi
    fi
    if [ -e ERROR.log ];
    then
        count=`cat ERROR.log | grep 'BUILD FAILED\|build cannot continue' | wc -l | sed 's/ //g'`
        if [ $count != '0' ];
        then
            # errors in report
            echo "There were errors in building $i"
            echo "There were errors in building $i" > $reportDir/$i/FAILED
            if [ $count != '0' ];
            then
                echo >> ERROR.log
                cat OUTPUT.log | grep '\[ERROR\] TEST' >> ERROR.log
            fi
            mv ERROR.log OUTPUT.log $reportDir/$i
            if [ $INIT = 'update' ];
            then
                cat $reportDir/$i/ERROR.log | mail -s "Failed to build $i" bayard@osjava.org
            fi
        fi
    fi
    # run checkstyle, pmd, junit report, javadoc, xref
    echo "Building documentation"
    maven -b maven-simian-plugin maven-jdepend-plugin:report maven-jcoverage-plugin:report checkstyle maven-junit-report-plugin:report maven-jxr-plugin:report pmd javadoc 2> DOCS-ERROR.log > DOCS-OUTPUT.log
    
    # somehow create a site from these reports
    if [ -d target/docs/apidocs/ ];
    then
        mv target/docs/apidocs/ $reportDir/$i/apidocs
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

# Needs to get smarter...
    DEPLOYABLES=`ls -1 target/*.?ar 2>/dev/null`
#    rm -f $reportDir/$i/builds/*.jar
    DT=`date +%Y%m%d`
    for t in $DEPLOYABLES
    do
        mkdir -p $reportDir/$i/builds/
        file_name=`echo $t | sed 's/target\///' | sed 's/\.[^\.]*ar//'`
        file_ext=`echo $t | sed 's/target\///' | sed 's/.*\.//g'`
        mv $t $reportDir/$i/builds/${file_name}-$DT.${file_ext}
    done

    date +"%Y/%m/%d %k:%M" > $reportDir/$i/BUILD_TIME
    if [ -e $buildDir/SCM_UPDATE ];
    then
        cat $buildDir/SCM_UPDATE | grep "$i " > $reportDir/$i/REASON
    fi
    if [ -e $buildDir/REASON ];
    then
        cat $buildDir/REASON > $reportDir/$i/REASON
    fi
    echo $BUILD_DURATION > $reportDir/$i/BUILD_DURATION

    echo "Finished with $i"
    cd -
done
