Atom4J serves as a library to assist in reading and generating Atom API
specified XML.  Atom4J includes an abstract Servlet for assisting in the
implementation of a Servlet which supports the Atom API.  Such a servlet
will soon be included in Roller (http://www.rollerweblogger.org) if you
would like to see an example.

Atom4J does in no way include any security restrictions, beyond an 
abstract authorized() method.  The Atom API currently suggest using
Digest authentication.  Again, this is not included in Atom4J.

Future development may include the reading and writing of Atom feeds as
outlined in the Atom 0.2 Snapshot:
http://diveintomark.org/archives/2003/08/05/atom02

To compile and use the library, you will need:
commons-digester.jar and its associated jars:
	commons-beanutils.jar
	commons-collections.jar
	commons-logging.jar
commons-lang.jar
servlet-2.3.jar

To compile and run the tests, you will need to add 
junit-3.8.1.jar or later.

This version of Atom4J (v0.1) supports the AtomAPI as of 09/01/2003,
currently at version 0.7. 
For more information refer to the following urls:
The Atom wiki : http://www.intertwingly.net/wiki/pie/
Busy developers guide : http://bitworking.org/news/AtomAPI_URIs
The AtomAPI RFC : http://bitworking.org/rfc/draft-gregorio-07.html
RFC wiki : http://notabug.com/2002/awiki/api