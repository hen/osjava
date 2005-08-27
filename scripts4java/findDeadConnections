#!/usr/bin/perl -w

@txt = <>;

# hunt for ' Connection (\w*) = ' in a file.
my %connection_vars;

for( $i=0; $i < @txt; $i++ ) {
    if($txt[$i] =~ / Connection (\w*) = /) {
        $connection_vars{$1}++;
    }
}

while( ($variable, $count) = each %connection_vars ) {
    for( $i=0; $i < @txt; $i++ ) {
        # hunt for DbUtils.closeQuietly(\1)
        if($txt[$i] =~ /DbUtils.closeQuietly\($variable\)/) {
            $connection_vars{$variable}--;
        }
        # hunt for DbUtils.close(\1)
        if($txt[$i] =~ /DbUtils.close\($variable\)/) {
            $connection_vars{$variable}--;
        }
        # hunt for JdbcConnectionManagerFactory.close(\1)
        if($txt[$i] =~ /JdbcConnectionManagerFactory\.close\($variable\)/) {
            $connection_vars{$variable}--;
        }
        # hunt for \1.close()
        if($txt[$i] =~ /$variable\.close\(\)/) {
            $connection_vars{$variable}--;
        }
        # hunt for .closeConnection(\1
        if($txt[$i] =~ /\.closeConnection\($variable/) {
            $connection_vars{$variable}--;
        }
    }
}

# match numbers
while( ($variable, $count) = each %connection_vars ) {
    if($count > 0) {
        print "$ARGV: Not enough close statements for $variable. \n";
    } elsif($count < 0) {
        print "$ARGV: Too many close statements for $variable. \n";
    } else {
        print "$ARGV: Matched $variable. \n";
    }
}
