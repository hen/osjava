javac -classpath .:servlet-api.jar:jsp-api.jar org/osjava/taglib/trail/*.java 
jar cf trail-taglib-1.0.jar org/osjava/taglib/trail/*.class META-INF/taglib.tld
