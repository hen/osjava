#!/usr/bin/perl

sub numerically { $a <=> $b; }

while(<>) {
    $report{$_} = $report{$_} + 1;
}

while( ($key,$value) = each(%report) ) {
    push @report, ("$value - $key");
}

$"="\n";

print reverse sort numerically @report;
