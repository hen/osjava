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
      <xsl:for-each select="added/method">
        <xsl:call-template name="added-method"/>
      </xsl:for-each>
      <xsl:for-each select="removed/method">
        <xsl:call-template name="removed-method"/>
      </xsl:for-each>
      <xsl:for-each select="changed/methodchange">
        <tr bgcolor="ccccff">
          <td>Method changed</td>
          <td><xsl:value-of select="../../@name"/></td>
          <td>
          <xsl:for-each select="from/method">
            <xsl:call-template name="print-method"/>
          </xsl:for-each>
          <br/>
          <xsl:for-each select="to/method">
            <xsl:call-template name="print-method"/>
          </xsl:for-each>
          </td>
        </tr>
      </xsl:for-each>
      <!-- TODO: handle classchange -->
    </xsl:for-each>
  </xsl:template>

  <!-- NOTE: need a function to output a method to eliminate duplication here -->
  <xsl:template name="added-method">
    <tr bgcolor="ccffcc">
     <td>Method added</td>
     <td><xsl:value-of select="../../@name"/></td>
     <td><xsl:call-template name="print-method"/></td>
    </tr>
  </xsl:template>

  <xsl:template name="removed-method">
    <tr bgcolor="ffcccc">
     <td>Method removed</td>
     <td><xsl:value-of select="../../@name"/></td>
     <td><xsl:call-template name="print-method"/></td>
    </tr>
  </xsl:template>

  <xsl:template name="print-method">
     <code style="white-space:pre"><xsl:if test="@deprecated='yes'"><i>deprecated: </i></xsl:if><xsl:value-of select="@access"/>.<xsl:if test="@static='yes'">static </xsl:if><xsl:value-of select="return/type/@name"/>.<xsl:value-of select="@name"/>(<xsl:for-each select="arguments/type"><xsl:value-of select="@name"/>, </xsl:for-each>)</code>
  </xsl:template>

  <!-- pass unrecognized nodes along unchanged -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>  

</xsl:stylesheet>
