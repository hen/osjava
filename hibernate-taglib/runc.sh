javac -classpath .:commons-beanutils.jar:servlet-api.jar:hibernate2.jar:jsp-api.jar org/osjava/hibernate/taglib/*.java 
jar cf hibernate-taglib.jar org/osjava/hibernate/taglib/*.class META-INF/taglib.tld
