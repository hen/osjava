<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pg="http://www.osjava.org/pergamum/ns"
  version="1.0">

  <!-- this is our top level match. Should include headers etc later on -->
  <xsl:template match="pg:pergamum">
     <html>
      <head>
       <title><xsl:value-of select="@name"/></title>
      </head>
      <body>
       <h2><xsl:value-of select="@name"/></h2>
       <xsl:apply-templates/>
      </body>
     </html>
  </xsl:template>

  <!-- turns a category-list entry into a full nested list -->
  <xsl:template match="pg:category-list">
    <xsl:variable name="name">
      <xsl:value-of select="."/>
    </xsl:variable>
    <h3><xsl:value-of select="$name"/></h3>
    <xsl:for-each select="document('categories.xml')/category-list[@name=$name]/category">
      <xsl:call-template name="pg:category"/>
    </xsl:for-each>
  </xsl:template>

  <!-- turns a category entry into a full list -->
  <xsl:template match="pg:category" name="pg:category">
    <h4><xsl:value-of select="@name"/></h4>
    <ul>
      <xsl:for-each select="book">
        <xsl:call-template name="pg:book"/>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <!-- turns a book.isbn into a full row -->
  <xsl:template match="pg:book" name="pg:book">
    <xsl:variable name="isbn">
      <xsl:value-of select="."/>
    </xsl:variable>
    <xsl:variable name="url">
      <xsl:value-of select="document('books.xml')/library/book[@isbn=$isbn]/@url"/>
    </xsl:variable>
    <xsl:variable name="pub">
      <xsl:value-of select="document('books.xml')/library/book[@isbn=$isbn]/@publisher"/>
    </xsl:variable>
    <xsl:variable name="name">
      <xsl:value-of select="document('books.xml')/library/book[@isbn=$isbn]/@name"/>
    </xsl:variable>
    <xsl:variable name="pubUri">
      <xsl:value-of select="document('publishers.xml')/publishers/publisher[@id=$pub]/@url"/>
    </xsl:variable>
    <xsl:variable name="pubName">
      <xsl:value-of select="document('publishers.xml')/publishers/publisher[@id=$pub]/@name"/>
    </xsl:variable>
    <li><div align="left"><a href="{$url}"><xsl:value-of select="$name"/></a></div> 
    <div align="right">(<a href="{$pubUri}"><xsl:value-of select="$pubName"/></a>) - <a href="http://www.amazon.com/exec/obidos/tg/detail/-/{$isbn}">[amz]</a> - <a href="http://www.bookpool.com/.x/1/sm/{$isbn}">[bkp]</a></div></li>
  </xsl:template>

</xsl:stylesheet>
