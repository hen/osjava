<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">

  <xsl:template match="diff">
     <html>
      <body>
       <h2>Comparing <xsl:value-of select="@old"/> to <xsl:value-of select="@new"/></h2>
       <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0">
        <TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor"> <TD COLSPAN="3"><FONT SIZE="+2"> <B>Changes Summary</B></FONT></TD></TR>
        <xsl:apply-templates/>
       </TABLE>
      </body>
     </html>
  </xsl:template>

  <xsl:template match="removed">
    <xsl:for-each select="class">
      <xsl:call-template name="removed-class"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="removed-class">
    <tr bgcolor="ffaaaa"><td>Class removed</td><td colspan="2"><xsl:value-of select="@name"/></td></tr>
  </xsl:template>

  <xsl:template match="added">
    <xsl:for-each select="class">
      <xsl:call-template name="added-class"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="added-class">
    <tr bgcolor="aaffaa"><td>Class added</td><td colspan="2"><xsl:value-of select="@name"/></td></tr>
  </xsl:template>

  <xsl:template match="changed">
    <xsl:for-each select="classchanged">
      <xsl:call-template name="changed-class"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="changed-class">
   <!-- TODO...lala...-->
  </xsl:template>

  <!-- pass unrecognized nodes along unchanged -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>  

</xsl:stylesheet>
