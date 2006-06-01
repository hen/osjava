#!/usr/bin/ruby

# The purpose of this script is to turn a chain of versions into a chain of 
# MAJOR MINOR BUGFIX SNAPSHOT elements

# Question. Should snapshots automatically be ignored, 
# or should there be another script that filters snapshots 
# out?

def get_version(version)
  if version =~ /[0-9]+\.[0-9]+\.[0-9]+\.jar/
    return version.scan(/([0-9]+)\.([0-9]+)\.([0-9]+\.jar)/)
  end
  if version =~ /[0-9]+\.[0-9]+\.jar/
    return version.scan(/([0-9]+)\.([0-9]+\.jar)/)
  end
  return nil
end

pre_v = ARGV.shift
pre_version = get_version(pre_v)[0]

# What to do if this first one is a snapshot, which is quite likely?

ARGV.each { |v|
  version = get_version(v)

  if version == nil
    puts "#{pre_v} to #{v} = SNAPSHOT"
    next
  end

  version = version[0]

  if version[0] != pre_version[0]
    puts "#{pre_v} to #{v} = MAJOR"
  else
  if version[1] != pre_version[1]
    puts "#{pre_v} to #{v} = MINOR"
  else
  if version[2] != pre_version[2]
    puts "#{pre_v} to #{v} = BUGFIX"
  else
    puts "#{pre_v} to #{v} = SAME"
  end
  end
  end

  pre_version = version
  pre_v = v
}
