<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pg="http://www.osjava.org/pergamum/ns"
		xmlns="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  version="1.0">

  <!-- this is our top level match. Should include headers etc later on -->
  <xsl:template match="pg:pergamum">
     <rss version="0.91">
      <channel>
       <title>Pergamum.org: <xsl:value-of select="@name"/></title>
       <link>http://www.pergamum.org/</link>
       <description>The <xsl:value-of select="@name"/> feed. </description>
       <language>en-gb</language>
       <xsl:apply-templates select="div/ul/pg:book"/>
      </channel>
     </rss>
  </xsl:template>

  <!-- turns a book.isbn into a full row -->
  <xsl:template match="pg:book" name="pg:book">
    <xsl:variable name="isbn">
      <xsl:value-of select="."/>
    </xsl:variable>
    <xsl:variable name="pub">
      <xsl:value-of select="document('db/books.xml')/library/book[@isbn=$isbn]/@publisher"/>
    </xsl:variable>
    <xsl:variable name="name">
      <xsl:value-of select="document('db/books.xml')/library/book[@isbn=$isbn]/@name"/>
    </xsl:variable>
    <xsl:variable name="pubUri">
      <xsl:value-of select="document('db/publishers.xml')/publishers/publisher[@id=$pub]/@url"/>
    </xsl:variable>
    <xsl:variable name="pubName">
      <xsl:value-of select="document('db/publishers.xml')/publishers/publisher[@id=$pub]/@name"/>
    </xsl:variable>
    <item>
      <title><xsl:value-of select="$name"/></title>
      <link>http://www.pergamum.org/book-<xsl:value-of select="$isbn"/></link>
      <description>Forthcoming from <xsl:value-of select="$pubName"/></description>
    </item>
  </xsl:template>

</xsl:stylesheet>
