#!/usr/bin/perl -w

use XML::Simple;
use Data::Dumper;

sub usage() {
    print "Usage:\n";
    print " ./integrate-builds.pl foo-integration.xml all       -  Will build every component specified in the given xml file. \n";
    print " ./integrate-builds.pl foo-integration.xml update    -  Will build only components that have had a changed file since the last update. \n";
#    print " ./integrate-builds.pl foo-integration.xml <project> -  Will build only the component specified. \n";
    exit 1;
}

# repository methods
sub checkout {
    my ($repository, $project, $tag, $directory)  = @_;
    if($repository =~ /svn:/) {
        if($tag) {
            $tag = '-'.$tag;
        } else {
            $tag = '';
        }
        $repository = substr( $repository, 4 );
        system "svn co $repository/$project-$tag $directory";
    } 
    elsif($repository =~ /cvs:/) {
        if($tag) {
            $tag = '-r '.$tag;
        } else {
            $tag = '';
        }
        $repository = substr( $repository, 3 );
        system "cvs -qd $repository co $tag -d $directory $project";
    } else {
        warn "Attempt to use unknown type of repository. \n";
    }
}

sub update {
    my ($repository, $directory)  = @_;
    chdir $directory;
    if($repository =~ /svn:/) {
        $repository = substr( $repository, 4 );
        system "svn update";
    } 
    elsif($repository =~ /cvs:/) {
        $repository = substr( $repository, 3 );
        $update_info = `cvs -q update -dP`;
    } else {
        warn "Attempt to use unknown type of repository. \n";
    }
    chdir '..';
    return $update_info;
}
# end of repository methods

if(@ARGV < 2) {
    usage();
}

my $conf_filename = $ARGV[0];
my $action = $ARGV[1];

# This is pretty ugly, need to pass CONF_FILE to XMLin somehow.
open(CONF_FILE, $conf_filename) or die("Unable to open xml configuration: $!");
close(CONF_FILE);
my $conf = XMLin($conf_filename, forcearray=>1);
#NOT YET#$global_repository = $conf->{'buildables'}->{'repository'};
@projects = @{$conf->{'buildables'}[0]->{'buildable'}};

##print Dumper($conf);

my @build_list = ();

# action may be 'all', 'update' or 'project-name' 

# loop over each component in the xml
foreach $buildable (@projects) {
    $project = $buildable->{'project'};
    $repository = $buildable->{'repository'};
    $tag = $buildable->{'tag'};
    if(not $tag) {
        $directory = $project;
    } else {
        $directory = ${project}. '_' . $tag;
    }

    # is component there?
    if(-e $directory) {
        # yes: do update check. 
        $updated = update($repository, $directory);

        # if all:
        if($action eq 'all') {
            # put in list of ones to build - build-list
            push @build_list, $buildable;
        }
        elsif($updated) {
            # put in list of ones to build - build-list
            push @build_list, $buildable;
        }
    } else {
        # no: checkout
        checkout($repository, $project, $tag, $directory);
        # put in list of ones to build - build-list
        push @build_list, $buildable;
    }
}
}


    # loop over each component in build-list
    foreach $buildable (@build_list) {
        # install a snapshot jar
        # build the main jar - record build-time
        # if failed(), add to build-report
        # build the site - maven + ttk
        # deploy deployables
        # report build-time, build-date, update-reason
        print $buildable->{'project'}."\n";
    }

#rm -f LAST_BUILD SCM_UPDATE REASON
#buildDir=`pwd`
#
#if [ ! -d report/ ]; 
#then
#    mkdir report/
#fi
#reportDir=`pwd`/report
#
#for i in $LIST
#do
#    if [ ! -e $i/project.xml ];
#    then
#        echo "Unable to build $i, as not mavenised. "
#        continue
#    fi
#    rm -fr report/$i
#    echo "Building $i"
#    echo $i >> LAST_BUILD
#    mkdir -p report/$i
#    cd $i
#    # cleaning
#    rm -fr target/ maven.log velocity.log ERROR.log OUTPUT.log
#    # building
#    TIME_START=`date +%s`
#    maven -b clean jar:install-snapshot clean jar 2> ERROR.log > OUTPUT.log
#    maven -b clean jar 2> ERROR.log > OUTPUT.log
#    TIME_END=`date +%s`
#    BUILD_DURATION=`echo $TIME_END - $TIME_START | bc`
#    BUILD_DURATION=`date -d "1970-01-01 UTC $BUILD_DURATION seconds" +"%M minutes %S seconds"`
#    if [ $? -ne 0 ];
#    then
#        # create failed report
#        echo "Failed to build $i"
#        echo "Failed to build $i" > $reportDir/$i/FAILED
#        mv ERROR.log OUTPUT.log $reportDir/$i
#        if [ $INIT = 'update' ];
#        then
#            cat $reportDir/$i/ERROR.log | mail -s "Failed to build $i" bayard@osjava.org
#        fi
#    fi
#    if [ -e ERROR.log ];
#    then
#        count=`cat ERROR.log | grep 'BUILD FAILED\|build cannot continue' | wc -l | sed 's/ //g'`
#        if [ $count != '0' ];
#        then
#            # errors in report
#            echo "There were errors in building $i"
#            echo "There were errors in building $i" > $reportDir/$i/FAILED
#            if [ $count != '0' ];
#            then
#                echo >> ERROR.log
#                cat OUTPUT.log | grep '\[ERROR\] TEST' >> ERROR.log
#            fi
#            mv ERROR.log OUTPUT.log $reportDir/$i
#            if [ $INIT = 'update' ];
#            then
#                cat $reportDir/$i/ERROR.log | mail -s "Failed to build $i" bayard@osjava.org
#            fi
#        fi
#    fi
#    # run checkstyle, pmd, junit report, javadoc, xref
#    echo "Building documentation"
#    maven -b maven-simian-plugin maven-jdepend-plugin:report maven-jcoverage-plugin:report checkstyle maven-junit-report-plugin:report maven-jxr-plugin:report pmd javadoc 2> DOCS-ERROR.log > DOCS-OUTPUT.log
#    
#    # somehow create a site from these reports
#    if [ -d target/docs/apidocs/ ];
#    then
#        mv target/docs/apidocs/ $reportDir/$i/apidocs
#    fi
#    if [ -d target/docs/jcoverage/ ];
#    then
#        mv target/docs/jcoverage/ $reportDir/$i/jcoverage
#    fi
#    if [ -f target/generated-xdocs/simian-report.xml ];
#    then
#        mv target/generated-xdocs/simian-report.xml $reportDir/$i
#    fi
#    if [ -f target/generated-xdocs/jdepend-report.xml ];
#    then
#        mv target/generated-xdocs/jdepend-report.xml $reportDir/$i
#    fi
#    if [ -f target/generated-xdocs/pmd-report.xml ];
#    then
#        mv target/generated-xdocs/pmd-report.xml $reportDir/$i
#    fi
#    if [ -d target/docs/xref/ ];
#    then
#        mv target/docs/xref $reportDir/$i
#    fi
#    if [ -d target/docs/xref-test/ ];
#    then
#        mv target/docs/xref-test $reportDir/$i
#    fi
#    if [ -f target/generated-xdocs/checkstyle-report.xml ];
#    then
#        mv target/generated-xdocs/checkstyle-report.xml $reportDir/$i
#    fi
#    if [ -f target/generated-xdocs/junit-report.xml ];
#    then
#        mv target/generated-xdocs/junit-report.xml $reportDir/$i
#    fi
#
## Needs to get smarter...
#    DEPLOYABLES=`ls -1 target/*.?ar 2>/dev/null`
##    rm -f $reportDir/$i/builds/*.jar
#    DT=`date +%Y%m%d`
#    for t in $DEPLOYABLES
#    do
#        mkdir -p $reportDir/$i/builds/
#        file_name=`echo $t | sed 's/target\///' | sed 's/\.[^\.]*ar//'`
#        file_ext=`echo $t | sed 's/target\///' | sed 's/.*\.//g'`
#        mv $t $reportDir/$i/builds/${file_name}-$DT.${file_ext}
#    done
#
#    date +"%Y/%m/%d %k:%M" > $reportDir/$i/BUILD_TIME
#    if [ -e $buildDir/SCM_UPDATE ];
#    then
#        cat $buildDir/SCM_UPDATE | grep "$i " > $reportDir/$i/REASON
#    fi
#    if [ -e $buildDir/REASON ];
#    then
#        cat $buildDir/REASON > $reportDir/$i/REASON
#    fi
#    echo $BUILD_DURATION > $reportDir/$i/BUILD_DURATION
#
#    echo "Finished with $i"
#    cd -
#done
