#!/usr/bin/perl -w

sub usage() {
    print "Usage:\n";
    print " ./nightly-report.sh all       -  Will report every component specified in NIGHTLY.txt. \n";
    print " ./nightly-report.sh update    -  Will report only components that are in the LAST_BUILD file. \n";
#    print " ./nightly-report.sh <project> -  Will report only the component specified. \n";
    exit 1;
}

usage();

# generate new front page
# for each element in the xml
    # generate line in front page

# for each built file
  # generate a report page, with links if targets of said links are available
  # TODO: Make the links unactive if not available, rather than not there
  

#if [ "x$1x" != "xx" ];
#then
#    if [ $1 = 'all' ];
#    then
#        LIST=`cat NIGHTLY.txt | grep -v '^#' | awk '{print $2 "_" $3}' | sed 's/_$//'`
#	LAST_BUILD_DATE="all"
#    elif [ $1 = 'update' ];
#    then
#        if [ ! -e LAST_BUILD ];
#        then
#           echo "There is not a LAST_BUILD file, so no report will be created. "
#           exit
#        fi
#        LIST=`cat LAST_BUILD`
#	LAST_BUILD_DATE=updated:`date -r LAST_BUILD +"%Y\\/%m\\/%d %k:%M"`
#    else
#        LIST=$1   # $* ?
#	LAST_BUILD_DATE="forced for $1"
#    fi
#else
#    usage
#    exit
#fi
#
#buildDir=`pwd`
#reportDir=`pwd`/report
#
#index=$reportDir/index.html
#
#cat header.inc | sed 's/\${TITLE}/Build/' | sed "s/\${TIMESTAMP}/$LAST_BUILD_DATE/" > $index
#
#echo '<ul id="project_list">' >> $index
#
#columns=3
#ALL=`cat NIGHTLY.txt | grep -v '^#' | awk '{print $2 "_" $3}' | sed 's/_$//'`
#all_count=`echo $ALL | wc -w`
#row_count=`echo $all_count/$columns + 1 | bc`
#entry_count=$row_count
#for i in $ALL
#do
#    if [ ! -d $reportDir/$i ];
#    then
#        continue
#    fi
#
#    local_report=report_`echo $i | sed 's/\//_/g'`.html
#
#    if [ "$entry_count" = "$row_count" ];
#    then
#        echo "<div class='project_block'>" >> $index
#    fi
#    entry_count=`echo $entry_count - 1 | bc`
#
#    if [ -f $reportDir/$i/FAILED ];
#    then
#        echo "<li class='project_line'><img class='failed_image' src='images/failed.gif'> <a class='failed_link' href='$local_report'>$i</a></li>" >> $index
#    else
#        echo "<li class='project_line'><img class='success_image' src='images/success.gif'> <a class='success_link' href='$local_report'>$i</a></li>" >> $index
#    fi
#
#    if [ $entry_count = '0' ];
#    then
#        echo "</div>" >> $index
#        entry_count=$row_count;
#    fi
#
#done
#
#echo '</div></ul>' >> $index
#echo '</html></body>' >> $index
#
#for i in $LIST
#do
#    echo "Building report for $i"
#
#    # now we create a pretty site
#    cd $reportDir/$i
#    local_report=report_`echo $i | sed 's/\//_/g'`.html
#    report=$reportDir/$local_report
#
#    tmp=`echo $i | sed 's/\//\\\\\//g'` # wtf, this is a lot of escaping. WHY?
#    cat $buildDir/header.inc | sed "s/\${TITLE}/$tmp Build/" | sed "s/\${TIMESTAMP}/$LAST_BUILD_DATE/" > $report
#
#    if [ -e BUILD_TIME ]; 
#    then
#        echo "<div class='build_info'>Last built: `cat BUILD_TIME`<br/>Build took: `cat BUILD_DURATION`</div><br/>" >> $report
#    fi
#
#    echo '<ul class="doc_menu">' >> $report
#   
#    if [ -f checkstyle-report.xml ];
#    then
#        echo "<li><a href='$i/checkstyle-report.html'>checkstyle</a></li>" >> $report
#        cat checkstyle-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > checkstyle-report.html
#    fi
#
#    if [ -f pmd-report.xml ];
#    then
#        echo "<li><a href='$i/pmd-report.html'>pmd</a></li>" >> $report
#        cat pmd-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > pmd-report.html
#    fi
#
#    if [ -d apidocs/ ];
#    then
#        echo "<li><a href='$i/apidocs/'>javadoc</a></li>" >> $report
#    fi
#
#    if [ -d xref/ ];
#    then
#        echo "<li><a href='$i/xref/'>source</a></li>" >> $report
#    fi
#
#    if [ -f jcoverage/index.html ];
#    then
#        echo "<li><a href='$i/jcoverage/'>jcoverage</a></li>" >> $report
#    fi
#
#    if [ -f junit-report.xml ];
#    then
#        echo "<li><a href='$i/junit-report.html'>junit</a></li>" >> $report
#        cat junit-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > junit-report.html
#    fi
#
#    if [ -f simian-report.xml ];
#    then
#        echo "<li><a href='$i/simian-report.html'>simian</a></li>" >> $report
#        cat simian-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > simian-report.html
#    fi
#
#    if [ -f jdepend-report.xml ];
#    then
#        echo "<li><a href='$i/jdepend-report.html'>jdepend</a></li>" >> $report
#        cat jdepend-report.xml | sed 's/document>/html>/' | sed 's/properties>/head>/' | sed 's/<\/section>//' | sed 's/<\/subsection>//' | sed 's/<section name="\([^"]*\)"/<h1>\1<\/h1/' | sed 's/<subsection name="\([^"]*\)"/<h2>\1<\/h2/' > jdepend-report.html
#    fi
#
#    if [ -d builds/ ];
#    then
#        echo "<li><a href='$i/builds/'>builds</a></li>" >> $report
#    fi
#
#    echo '</ul>' >> $report
#
#    echo '<hr>' >> $report
#
#    if [ -f FAILED ];
#    then
#        echo '<pre>' `cat FAILED` '</pre>' >> $report
#        echo '<hr>' >> $report
#        echo '<pre class="error_text">' `cat ERROR.log | sed 's/$/<br>/'` '</pre>' >> $report
#    else
#        echo '<pre>Build successful.</pre>' >> $report
#
#    fi
#
#    if [ -e REASON ];
#    then
#        echo "<hr><h2>Built because:</h2><pre>`cat REASON`</pre>" >> $report
#    fi
#
#    echo '</body></html>' >> $report
#
#    cd -
#done
