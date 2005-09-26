<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="text" indent="yes" encoding="iso-8859-1"/>

  <xsl:template match="entity">
package <xsl:value-of select="@package"/>.interfaces;

public interface <xsl:value-of select="@name"/> {
    <xsl:apply-templates select="*"/>
}
  </xsl:template>

  <xsl:template match="attribute">
public void set<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue);
public <xsl:value-of select="@typeClass"/> get<xsl:value-of select="@capname"/>();
  </xsl:template>

  <xsl:template match="setattribute">
    public void set<xsl:value-of select="@capname"/>Set(java.util.Set&lt;<xsl:value-of select="@typeClass"/>&gt; aSet);
    public java.util.Set&lt;<xsl:value-of select="@typeClass"/>&gt; get<xsl:value-of select="@capname"/>Set();
    public boolean add<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue);
    public boolean remove<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue);
    public void clear<xsl:value-of select="@capname"/>Set();
    public void addAll<xsl:value-of select="@capname"/>Set(java.util.Set&lt;<xsl:value-of select="@typeClass"/>&gt; aSet);
    public boolean contains<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue);
  </xsl:template>

  <xsl:template match="listattribute">
    public void set<xsl:value-of select="@capname"/>List(java.util.List&lt;<xsl:value-of select="@typeClass"/>&gt; aList);
    public java.util.List&lt;<xsl:value-of select="@typeClass"/>&gt; get<xsl:value-of select="@capname"/>List();
    public boolean add<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue);
    public boolean remove<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue);
    public void clear<xsl:value-of select="@capname"/>List();
    public void addAll<xsl:value-of select="@capname"/>List(java.util.List&lt;<xsl:value-of select="@typeClass"/>&gt; aList);
    public boolean contains<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue);
  </xsl:template>

  <xsl:template match="mapattribute">
    public void set<xsl:value-of select="@capname"/>Map(java.util.Map&lt;<xsl:value-of select="@keytypeClass"/>,<xsl:value-of select="@valuetypeClass"/>&gt; aMap);
    public java.util.Map&lt;<xsl:value-of select="@keytypeClass"/>,<xsl:value-of select="@valuetypeClass"/>&gt; get<xsl:value-of select="@capname"/>Map();
    public <xsl:value-of select="@valuetypeClass"/> put<xsl:value-of select="@capname"/>(<xsl:value-of select="@keytypeClass"/> aKey, <xsl:value-of select="@valuetypeClass"/> aValue);
  </xsl:template>

</xsl:stylesheet>
