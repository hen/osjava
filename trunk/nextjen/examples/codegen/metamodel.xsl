<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="iso-8859-1"/>

  <xsl:template match="metamodel">
    <xsl:processing-instruction name="nextjen">template="interface.xsl" select="/entitylist/entity" writeTo="concat(@path,'/interfaces/',@name,'.java')"</xsl:processing-instruction>
    <xsl:processing-instruction name="nextjen">template="concrete.xsl" select="/entitylist/entity" writeTo="concat(@path,'/concrete/Concrete',@name,'.java')"</xsl:processing-instruction>
<entitylist>
    <xsl:apply-templates select="categories/category/entity"/>
</entitylist>
  </xsl:template>

  <xsl:template match="entity">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:attribute name="package"><xsl:value-of select="../@name"/></xsl:attribute>
      <xsl:attribute name="path"><xsl:value-of select="translate(../@name,'.','/')"/></xsl:attribute>
      <xsl:apply-templates select="*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="attribute|setattribute|listattribute|mapattribute">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:attribute name="capname"><xsl:value-of select="concat(translate(substring(@name,1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),substring(@name,2))"/></xsl:attribute>
      <xsl:if test="@type">
        <xsl:variable name="type" select="@type"/>
        <xsl:attribute name="typeClass"><xsl:value-of select="/metamodel/types/type[@name=$type]/@class"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@keytype">
        <xsl:variable name="type" select="@keytype"/>
        <xsl:attribute name="keytypeClass"><xsl:value-of select="/metamodel/types/type[@name=$type]/@class"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@valuetype">
        <xsl:variable name="type" select="@valuetype"/>
        <xsl:attribute name="valuetypeClass"><xsl:value-of select="/metamodel/types/type[@name=$type]/@class"/></xsl:attribute>
      </xsl:if>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
