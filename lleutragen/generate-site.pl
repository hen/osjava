#!/usr/bin/perl -w

use Template;
use XML::Simple;
use Data::Dumper;

my $db = XMLin('db/db.xml', forcearray=>1);

my $tt = Template->new();
my $input = 'templates/lleutranet.tt';

# order is a big hack. simple-xml's hash usage is a bit crappen.
my @sections = sort { $db->{'section'}->{$a}->{'order'} cmp $db->{'section'}->{$b}->{'order'} } (keys %{$db->{'section'}});

foreach $section (@sections) {
    print "Generating $section. \n";
    my $vars = { 
        db => $db,
        this_section => $section, 
        sections => \@sections
    };
    $tt->process($input, $vars, "docs/$section.html") || die $tt->error();
}


#print Dumper($db);

#$db = $db->{'section'};


#print Dumper($db);

#foreach $section (keys %{$db}) {
#    foreach $image ( @{$db->{$section}->{'image'}} ) {
#        $imgUri = $image->{'uri'};
#        print "$imgUri for image\n";
#    }
#    foreach $category ( keys %{$db->{$section}->{'category'}} ) {
#        foreach $bookmark ( @{$db->{$section}->{'category'}->{$category}->{'bookmark'}} ) {
#            $uri = $bookmark->{'uri'};
#            $label = $bookmark->{'label'};
#            print "$uri/$label\n";
#        }
#    }
#}
