<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <xsl:template match="category">
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

  <xsl:template match="subcategory">
    <h3><xsl:value-of select="@name"/></h3>
    <ul><xsl:apply-templates/></ul>
  </xsl:template>

  <xsl:template match="book">
    <xsl:variable name="isbn">
      <xsl:value-of select="@isbn"/>
    </xsl:variable>
    <xsl:variable name="url">
      <xsl:value-of select="@url"/>
    </xsl:variable>
    <xsl:variable name="pub">
      <xsl:value-of select="@publisher"/>
    </xsl:variable>
    <xsl:variable name="pubUri">
      <xsl:value-of select="document('publishers.xml')/publishers/publisher[@id=$pub]/@url"/>
    </xsl:variable>
    <li><div align="left"><a href="{$url}"><xsl:value-of select="@name"/></a></div> 
    <div align="right">(<a href="{$pubUri}"><xsl:value-of select="document('publishers.xml')/publishers/publisher[@id=$pub]/@name"/></a>) - <a href="http://www.amazon.com/exec/obidos/tg/detail/-/{$isbn}">[amz]</a> - <a href="http://www.bookpool.com/.x/1/sm/{$isbn}">[bkp]</a></div></li>
  </xsl:template>

</xsl:stylesheet>
