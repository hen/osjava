#!/usr/bin/perl -w

use Data::Dumper;
use XML::Simple;
use Template;
use POSIX qw(strftime);

sub usage() {
    print "Usage:\n";
    print " ./groupReport.pl groups.xml                   -  Will build a groups report for the given file. \n";
    exit 1;
}

sub scrapeBar {
    my ($file, $target)  = @_;
    open(BUILDABLE_INDEX, $file) or (warn("Unable to find buildable index for $file: $!\n") and return);
    # somehow scrape the bar out of it
    my $bar = "";
    while(<BUILDABLE_INDEX>) {
        if(/<!-- the green bar -->/ .. /<\/p>/) {
            s/href="report_/href="$target\/report_/;
            $bar .= $_;
        }
    }
    close(BUILDABLE_INDEX);
    return $bar;
}

if(@ARGV < 1) {
    usage();
}

my $conf_filename = $ARGV[0];

# This is pretty ugly, need to pass CONF_FILE to XMLin somehow.
open(CONF_FILE, $conf_filename) or die("Unable to open xml configuration: $!");
close(CONF_FILE);

my $conf = XMLin($conf_filename, forcearray=>1);
@groups = @{$conf->{'groups'}[0]->{'group'}};
$reportDirectory = $conf->{'site'}[0]->{'target'};

foreach $group (@groups) {
    open(CONF_FILE, $group->{'conf'}) or die("Unable to open xml configuration for $group->{'conf'}: $!");
    close(CONF_FILE);

    my $subconf = XMLin($group->{'conf'}, forcearray=>1);
    $group->{'target'} = $subconf->{'site'}[0]->{'target'};
    $group->{'target'} =~ s/^\Q$reportDirectory\E//;

    $group->{'bar'} = scrapeBar("$reportDirectory/".$group->{'target'}."/index.html", $group->{'target'});
}

$timestamp = strftime("%Y/%m/%e %H:%M", localtime());

print "Building index\n";
my $tt = Template->new();
my $input = 'templates/frontpage.tt';
my $vars = {
    groups => \@groups,
    site => $conf->{'site'}[0],
    timestamp => $timestamp,
};
$tt->process($input, $vars, "$reportDirectory/index.html") || die $tt->error();

