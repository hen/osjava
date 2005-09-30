#!/usr/bin/perl

use strict;
use warnings;

#use Algorithm::Diff;  ??

sub cleanJavap {
    my ($txt) = @_;

    my @lines = ();

    foreach (@$txt) {
        chomp;
        if( /^Compiled from/ ) {
            next;
        }
        if( /throws / ) {
            $lines[$#lines] .= $_;
            $lines[$#lines] =~ s/;      //;
        } else {
            $lines[$#lines + 1] = $_;
        }
    }

    return @lines;
}

sub diff {
    my ($one, $two) = @_;

    my @added = ();
    my @removed = ();
    my @same = ();
    my %newLookup;
    my %oldLookup;
    my $file;

    foreach $file (@$one) { $oldLookup{$file} = 1 }
    foreach $file (@$two) { $newLookup{$file} = 1 }
    
    foreach $file (@$one) {
        if( exists $newLookup{$file} ) {
            push(@same, $file);
        } else {
            push(@removed, $file);
        }
    }

    foreach $file (@$two) {
        unless( exists $oldLookup{$file} ) {
            push(@added, $file);
        }
    }

    return (\@added, \@removed, \@same);
}

################ MAIN ################

# for a given pair of jars
my $old = $ARGV[0];
my $new = $ARGV[1];

my $oldName = $old;
my $newName = $new;
$oldName =~ s!.*/!!;
$newName =~ s!.*/!!;
$oldName =~ s/.jar$//;
$newName =~ s/.jar$//;

print "<h2>Comparing $oldName to $newName</h2>\n";

my @oldFiles = `unzip -t $old | grep 'class' | sed 's/.class   OK//' | sed 's/    testing: //' | sed 's/\\//./g'`;
my @newFiles = `unzip -t $new | grep 'class' | sed 's/.class   OK//' | sed 's/    testing: //' | sed 's/\\//./g'`;

my ($added_ref, $removed_ref, $same_ref) = diff(\@oldFiles, \@newFiles);

foreach (@$added_ref) {
    chomp;
    print "<tr bgcolor=\"aaffaa\"><td>New class</td><td>$_</td><td>&nbsp;</td></tr>\n";
}
foreach (@$removed_ref) {
    chomp;
    print "<tr bgcolor=\"ffaaaa\"><td>Class removed</td><td>$_</td><td>&nbsp;</td></tr>\n";
}

my $file;
foreach $file (@$same_ref) {
    chomp $file;
# TODO: Switch to verbose to get a handle on deprecations
#    my @jold = `javap -verbose -classpath $old $file | grep -v '^const #' | grep -v '   [0-9]*:'`;
#    my @jnew = `javap -verbose -classpath $new $file | grep -v '^const #' | grep -v '   [0-9]*:'`;
    my @jold = `javap -public -classpath $old $file`;
    my @jnew = `javap -public -classpath $new $file`;

    my @oldLines = cleanJavap( \@jold );
    my @newLines = cleanJavap( \@jnew );

    my ($added_method_ref, $removed_method_ref, $same_method_ref) = diff(\@oldLines, \@newLines);

# TODO: Figure out changes versus add/removes
#       Especially when the class line shows, that has to be a change

    foreach (@$added_method_ref) {
        print "<tr bgcolor=\"ccffcc\"><td width=\"15%\">New method</td><td width=\"15%\">$file</td><td>$_</td></tr>\n";
    }
    foreach (@$removed_method_ref) {
        print "<tr bgcolor=\"ffcccc\"><td width=\"15%\">Removed method</td><td width=\"15%\">$file</td><td>$_</td></tr>\n";
    }
}

