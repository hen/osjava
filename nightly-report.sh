#!/bin/bash

reportDir=`pwd`/report

index=$reportDir/index.html

echo '<html><body>' > $index
echo '<h1>OSJava Nightly build</h1>' >> $index

echo '<ul>' >> $index

for i in `cat NIGHTLY.txt | grep -v '^#' `
do

    # now we create a pretty site
    cd $reportDir/$i
    local_report=report_`echo $i | sed 's/\//_/g'`.html
    report=$reportDir/$local_report


    echo '<html><body>' > $report
    echo "<h1>$i Report</h1>" >> $report

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

    if [ -f junit-report.xml ];
    then
        echo "<li><a href='$i/junit-report.html'>junit</a></li>" >> $report
        cat junit-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > junit-report.html
    fi

    if [ -d javadoc/ ];
    then
        echo "<li><a href='$i/javadoc/'>javadoc</a></li>" >> $report
    fi

    echo '</ul>' >> $report

    echo '<hr>' >> $report

    if [ -f FAILED ];
    then
        echo '<pre>' `cat FAILED` '</pre>' >> $report
        echo '<hr>' >> $report
        echo '<pre>' `cat ERROR.log | sed 's/$/<br>/'` '</pre>' >> $report

        echo "<li><font color='#FF0000'>failed</font>&nbsp;&nbsp;- <a href='$local_report'>$i</a></li>" >> $index
    else
        echo '<pre>Build successful.</pre>' >> $report

        echo "<li><font color='#00FF00'>success</font>&nbsp;- <a href='$local_report'>$i</a></li>" >> $index
    fi
    echo '</body></html>' >> $report

    cd -
done

echo '</ul>' >> $index
echo '</html></body>' >> $index
