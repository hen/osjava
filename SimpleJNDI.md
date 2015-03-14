## The Problem ##
Simple-JNDI is intended to solve two problems. The first is that of finding a container independent way of opening a database connection, the second is to find a good way of specifying application configurations.

Unit tests or prototype code often need to emulate the environment within which the code is expected to run. A very common one is to get an object of type javax.sql.DataSource from JNDI so a java.sql.Connection to your database of choice may be opened.
Applications need configuration; a JNDI implementation makes a handy location for configuration values. Either as a globally available system, or via IoC through the use of some kind of JNDI configuration facade (see gj-config).

## A Solution ##

A simple implementation of JNDI. It is entirely library based, so no server instances are started, and it sits upon Java .properties files, XML files or Windows-style .ini files, so it is easy to use and simple to understand. The files may be either on the file system or in the classpath.

Simple-JNDI depends on no external jars for its basic functionality, however to get certain optional features you will still need to download external jars. When describing these features, the manual will point out which jars are needed and where to get them.