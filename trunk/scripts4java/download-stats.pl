#!/usr/bin/perl
# Henri Yandell
# Based on reportcount.pl

sub numerically { $a <=> $b; }

# grep 'Sep/2005' /home/sites/org.osjava/logs/dist-apache-access_log | grep 'zip\|tar.gz' | awk '{print $7}' | sed 's/\/releases\/official//' | sed 's/\/[^\/]*$//' | /home/hen/bin/reportcount.pl 
# Read each line in dist-apache-access_log
# Group $7 by Month
# Create reportcount of each group
# Put into one matrix
# Generate email

open( LOG, "/home/sites/org.osjava/logs/dist-apache-access_log") or die("Unable to open log file");

while(<LOG>) {
    if(/zip/ or /tar\.gz/) {
        # TODO: Check for broken downloads
        /.*\[\d\d\/([^:]*):/;
        $month = $1;
        $month =~ /(\w\w\w)\/(\d\d\d\d)/;

          if($1 eq "Jan") { 
              $month = "$2-01";
          }
          elsif($1 eq "Feb") {
              $month = "$2-02";
          }
          elsif($1 eq "Mar") {
              $month = "$2-03";
          }
          elsif($1 eq "Apr") {
              $month = "$2-04";
          }
          elsif($1 eq "May") {
              $month = "$2-05";
          }
          elsif($1 eq "Jun") {
              $month = "$2-06";
          }
          elsif($1 eq "Jul") {
              $month = "$2-07";
          }
          elsif($1 eq "Aug") {
              $month = "$2-08";
          }
          elsif($1 eq "Sep") {
              $month = "$2-09";
          }
          elsif($1 eq "Oct") {
              $month = "$2-10";
          }
          elsif($1 eq "Nov") {
              $month = "$2-11";
          }
          elsif($1 eq "Dec") {
              $month = "$2-12";
          }

        @words = split;
        $file = $words[6];

        # lil bit dumb
        $file =~ s!^/releases!!;
        $file =~ s!^/official!!;
        $file =~ s!^/!!;
        $file =~ s!/[^/]*$!!;
        $file =~ s!genjava/!!;

        $months{$month} = $month;
        $totals{$file} = $totals{$file} + 1;

        $reports{$file}{$month} = $reports{$file}{$month} + 1;
    }

}

close(LOG);

print "<table border='1'><tr><th>Project</th><th>";
print join('</th><th>', sort keys %months);
print "</th><th>Total</th></tr>\n";
foreach $file (sort keys %reports) {
   $report = $reports{$file};
   my @text = ();
   print "<tr><td align='center'><b>$file</b></td><td>";
   foreach $month (sort keys %months) {
      $count = $report->{$month};
      if(not exists($report->{$month})) {
          $count = 0;
      }
      push @text, ($count);
   }
   print join("</td><td align='center'>", @text);
   print "</td><td align='center'>$totals{$file}</td></tr>\n";
}
#print "<tr><td align='center'><b>Total</b></td><td>";
#my @text = ();
#foreach $month (sort keys %months) {
#   push @text, ($count);
#}
#print join("</td><td align='center'>", @text);
#print "</td><td align='center'>$totals</td></tr>\n";
print "</table>\n";
