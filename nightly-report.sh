#!/bin/bash

function usage() {
    echo 'Usage:'
    echo ' ./nightly-report.sh all       -  Will report every component specified in NIGHTLY.txt. '
    echo ' ./nightly-report.sh update    -  Will report only components that are in the LAST_BUILD file. '
    echo ' ./nightly-report.sh <project> -  Will report only the component specified. '
}

if [ "x$1x" != "xx" ];
then
    if [ $1 = 'all' ];
    then
        LIST=`cat NIGHTLY.txt | grep -v '^#'`
    elif [ $1 = 'update' ];
    then
        if [ ! -e LAST_BUILD ];
        then
           echo "There is not a LAST_BUILD file, so no report will be created. "
           exit
        fi
        LIST=`cat LAST_BUILD`
    else
        LIST=$1   # $* ?
    fi
else
    usage
    exit
fi

buildDir=`pwd`
reportDir=`pwd`/report

index=$reportDir/index.html

echo '<html><body>' > $index
echo '<h1>OSJava Nightly build</h1>' >> $index

echo '<ul>' >> $index

for i in `cat NIGHTLY.txt`
do
    cd $reportDir/$i
    local_report=report_`echo $i | sed 's/\//_/g'`.html
    report=$reportDir/$local_report

    if [ -f FAILED ];
    then
        echo "<li><font color='#FF0000'>failed</font>&nbsp;&nbsp;- <a href='$local_report'>$i</a></li>" >> $index
    else
        echo "<li><font color='#00FF00'>success</font>&nbsp;- <a href='$local_report'>$i</a></li>" >> $index
    fi

    cd -
done

echo '</ul>' >> $index
echo '</html></body>' >> $index

for i in $LIST
do

    # now we create a pretty site
    cd $reportDir/$i
    local_report=report_`echo $i | sed 's/\//_/g'`.html
    report=$reportDir/$local_report


    echo '<html><body>' > $report
    echo "<h1>$i Report</h1>" >> $report

    if [ -e BUILD_TIME ]; 
    then
        echo "<table><tr><td>Last built:</td><td>`cat BUILD_TIME`</td></tr><tr><td>Build took:</td><td>`cat BUILD_DURATION`</td></tr></table>" >> $report
    fi

    echo '<ul>' >> $report
   
    if [ -f checkstyle-report.xml ];
    then
        echo "<li><a href='$i/checkstyle-report.html'>checkstyle</a></li>" >> $report
        cat checkstyle-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > checkstyle-report.html
    fi

    if [ -f pmd-report.xml ];
    then
        echo "<li><a href='$i/pmd-report.html'>pmd</a></li>" >> $report
        cat pmd-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > pmd-report.html
    fi

    if [ -d javadoc/ ];
    then
        echo "<li><a href='$i/javadoc/'>javadoc</a></li>" >> $report
    fi

    echo '<hr>' >> $report

    if [ -f junit-report.xml ];
    then
        echo "<li><a href='$i/junit-report.html'>junit</a></li>" >> $report
        cat junit-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > junit-report.html
    fi

    echo '</ul>' >> $report

    echo '<hr>' >> $report

    if [ -f FAILED ];
    then
        echo '<pre>' `cat FAILED` '</pre>' >> $report
        echo '<hr>' >> $report
        echo '<pre>' `cat ERROR.log | sed 's/$/<br>/'` '</pre>' >> $report

    else
        echo '<pre>Build successful.</pre>' >> $report

    fi

    if [ -e $buildDir/SVN_UPDATE ];
    then
        echo "<hr><h2>Built because:</h2><pre>`cat $buildDir/SVN_UPDATE`</pre>" >> $report
    fi

    echo '</body></html>' >> $report

    cd -
done
