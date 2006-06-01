#!/usr/bin/ruby

require 'time'

class Jar
    def initialize(date, jar)
        (month, day, year, hour, minute) = date.scan(/0?([0-9]+)-0?([0-9]+)-0?([0-9]+) 0?([0-9]+):0?([0-9]+)/)[0]
        @date = Time.gm( 2000 + Integer(year), Integer(month), Integer(day), Integer(hour), Integer(minute), 0 )
        @jar = jar
    end

    def <=>(other)
        return @date <=> other.date
    end

    def to_s
        return @jar
    end

    attr_reader :date, :jar
end

jars = []

ARGV.each { |jar|
    release_date = `unzip -l #{jar}  | grep MANIFEST.MF | sed 's/ *META.*$//' | sed 's/^ *[0-9]* *//'`
    jars += [ Jar.new(release_date, jar) ]
}

puts jars.sort
