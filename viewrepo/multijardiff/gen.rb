#!/usr/bin/ruby

require 'rexml/document'
require 'rexml/streamlistener'
include REXML

class Counter
    include StreamListener

    def initialize
        @total = 0
        @added = 0
        @removed = 0
        @changed = 0
    end

    def tag_start(name, attributes)
        if name == 'added'
            @tag = 'added'
        end
        if name == 'removed'
            @tag = 'removed'
        end
        if name == 'changed'
            @tag = 'changed'
        end
        if name == 'class' || name == 'method'
            if attributes['signatureSize']
                if @tag == 'added'
                    @total += Integer(attributes['signatureSize'])
                    @added += Integer(attributes['signatureSize'])
                end
                if @tag == 'removed'
                    @removed += Integer(attributes['signatureSize'])
                end
                if @tag == 'changed'
                    @changed += Integer(attributes['signatureSize'])
                end
            end
        end
    end
    def tag_end(name)
        if name == 'added' || name == 'removed' || name == 'changed'
            @tag = nil
        end
    end

    attr_reader :total, :added, :removed, :changed
end

def count(txt)
    counter = Counter.new
    parser = Parsers::StreamParser.new(txt, counter)
    parser.parse
    return [counter.total, counter.added, counter.removed, counter.changed]
end

# Expects an ordered list of jars to be passed in

  f = File.open(ARGV[0]) or die "Unable to open file #{ARGV[0]}"
  jars=[]
  f.each_line { |line|
    jars.push line.chomp
  }

prev_jar = jars.shift

prev_txt = `jardiff -t #{prev_jar} -f empty.jar`
release_date = `unzip -l #{prev_jar}  | grep MANIFEST.MF | sed 's/ *META.*$//' | sed 's/^ *[0-9]* *//'`
(total, added, removed, changed) = count(prev_txt)
prev_name = File.basename(prev_jar).sub(/^.*-([0-9].*).jar$/, '\1')
puts "#{prev_name} = #{total}, #{release_date}"

jars.each { |jar|
    diff_txt = `jardiff -f #{prev_jar} -t #{jar}`
    (total, added, removed, changed) = count(diff_txt)
    puts "diff = #{added}, #{removed}, #{changed}"

    curr_txt = `jardiff -t #{jar} -f empty.jar`
    release_date = `unzip -l #{jar}  | grep MANIFEST.MF | sed 's/ *META.*$//' | sed 's/^ *[0-9]* *//'`
    (total, added, removed, changed) = count(curr_txt)
    jar_name = File.basename(jar).sub(/^.*-([0-9].*).jar$/, '\1')
    puts "#{jar_name} = #{total}, #{release_date}"

    prev_jar = jar
}
