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
        LIST=`cat NIGHTLY.txt | awk '{print $2}' | grep -v '^#'`
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

cat header.inc | sed 's/\${TITLE}/Nightly Build/' > $index

echo '<ul id="project_list">' >> $index

for i in `cat NIGHTLY.txt | awk '{print $2}' | grep -v '^#'`
do
    if [ ! -d $reportDir/$i ];
    then
        continue
    fi

    cd $reportDir/$i
    local_report=report_`echo $i | sed 's/\//_/g'`.html
    report=$reportDir/$local_report

    if [ -f FAILED ];
    then
        echo "<li class='project_line'><img class='failed_image' src='images/failed.gif'> <a class='failed_link' href='$local_report'>$i</a></li>" >> $index
    else
        echo "<li class='project_line'><img class='success_image' src='images/success.gif'> <a class='success_link' href='$local_report'>$i</a></li>" >> $index
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

    cat $buildDir/header.inc | sed "s/\${TITLE}/$i Nightly Build/" > $report

    if [ -e BUILD_TIME ]; 
    then
        echo "<div class='build_info'>Last built: `cat BUILD_TIME`<br/>Build took: `cat BUILD_DURATION`</div><br/>" >> $report
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

    if [ -d apidocs/ ];
    then
        echo "<li><a href='$i/apidocs/'>apidocs</a></li>" >> $report
    fi

    if [ -d jcoverage/ ];
    then
        echo "<li><a href='$i/jcoverage/'>jcoverage</a></li>" >> $report
    fi

    if [ -f junit-report.xml ];
    then
        echo "<li><a href='$i/junit-report.html'>junit</a></li>" >> $report
        cat junit-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > junit-report.html
    fi

    if [ -f simian-report.xml ];
    then
        echo "<li><a href='$i/simian-report.html'>simian</a></li>" >> $report
        cat simian-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > simian-report.html
    fi

    if [ -f jdepend-report.xml ];
    then
        echo "<li><a href='$i/jdepend-report.html'>jdepend</a></li>" >> $report
        cat jdepend-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > jdepend-report.html
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

    if [ -e REASON ];
    then
        echo "<hr><h2>Built because:</h2><pre>`cat REASON`</pre>" >> $report
    fi

    echo '</body></html>' >> $report

    cd -
done
