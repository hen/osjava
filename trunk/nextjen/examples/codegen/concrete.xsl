<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="text" indent="yes" encoding="iso-8859-1"/>

  <xsl:template match="entity">
package <xsl:value-of select="@package"/>.interfaces;

import <xsl:value-of select="@package"/>.interfaces.<xsl:value-of select="@name"/>;

public class Concrete<xsl:value-of select="@name"/> implements <xsl:value-of select="@name"/> {
    <xsl:apply-templates select="*"/>
}
  </xsl:template>

  <xsl:template match="attribute">
    private <xsl:value-of select="concat(@typeClass,' ',@name)"/>;

    public void set<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue) {
        this.<xsl:value-of select="@name"/> = aValue;
    }
    public <xsl:value-of select="@typeClass"/> get<xsl:value-of select="@capname"/>() {
        return this.<xsl:value-of select="@name"/>;
    }
  </xsl:template>

  <xsl:template match="setattribute">
    private java.util.Set&lt;<xsl:value-of select="@typeClass"/>&gt; <xsl:value-of select="@name"/>;

    public void set<xsl:value-of select="@capname"/>Set(java.util.Set&lt;<xsl:value-of select="@typeClass"/>&gt; aSet) {
        this.<xsl:value-of select="@name"/> = aSet;
    }

    public java.util.Set&lt;<xsl:value-of select="@typeClass"/>&gt; get<xsl:value-of select="@capname"/>Set() {
        return this.<xsl:value-of select="@name"/>;
    }

    public boolean add<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue) {
        return this.<xsl:value-of select="@name"/>.add(aValue);
    }

    public boolean remove<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue) {
        return this.<xsl:value-of select="@name"/>.remove(aValue);
    }

    public void clear<xsl:value-of select="@capname"/>Set() {
        this.<xsl:value-of select="@name"/>.clear();
    }

    public void addAll<xsl:value-of select="@capname"/>Set(java.util.Set&lt;<xsl:value-of select="@typeClass"/>&gt; aSet) {
        this.<xsl:value-of select="@name"/>.addAll(aSet);
    }

    public boolean contains<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue) {
        return this.<xsl:value-of select="@name"/>.contains(aValue);
    }
  </xsl:template>

  <xsl:template match="listattribute">
    private java.util.List&lt;<xsl:value-of select="@typeClass"/>&gt; <xsl:value-of select="@name"/>;

    public void set<xsl:value-of select="@capname"/>List(java.util.List&lt;<xsl:value-of select="@typeClass"/>&gt; aList) {
        this.<xsl:value-of select="@name"/> = aList;
    }

    public java.util.List&lt;<xsl:value-of select="@typeClass"/>&gt; get<xsl:value-of select="@capname"/>List() {
        return this.<xsl:value-of select="@name"/>;
    }

    public boolean add<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue) {
        return this.<xsl:value-of select="@name"/>.add(aValue);
    }

    public boolean remove<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue) {
        return this.<xsl:value-of select="@name"/>.remove(aValue);
    }

    public void clear<xsl:value-of select="@capname"/>List() {
        this.<xsl:value-of select="@name"/>.clear();
    }

    public void addAll<xsl:value-of select="@capname"/>List(java.util.List&lt;<xsl:value-of select="@typeClass"/>&gt; aList) {
        this.<xsl:value-of select="@name"/>.addAll(aList);
    }

    public boolean contains<xsl:value-of select="@capname"/>(<xsl:value-of select="@typeClass"/> aValue) {
        return this.<xsl:value-of select="@name"/>.contains(aValue);
    }
  </xsl:template>

  <xsl:template match="mapattribute">
    private java.util.Map&lt;<xsl:value-of select="@keytypeClass"/>,<xsl:value-of select="@valuetypeClass"/>&gt; <xsl:value-of select="@name"/>;

    public void set<xsl:value-of select="@capname"/>Map(java.util.Map&lt;<xsl:value-of select="@keytypeClass"/>,<xsl:value-of select="@valuetypeClass"/>&gt; aMap) {
        this.<xsl:value-of select="@name"/> = aMap;
    }

    public java.util.Map&lt;<xsl:value-of select="@keytypeClass"/>,<xsl:value-of select="@valuetypeClass"/>&gt; get<xsl:value-of select="@capname"/>Map() {
        return this.<xsl:value-of select="@name"/>;
    }

    public <xsl:value-of select="@valuetypeClass"/> put<xsl:value-of select="@capname"/>(<xsl:value-of select="@keytypeClass"/> aKey, <xsl:value-of select="@valuetypeClass"/> aValue) {
        return this.<xsl:value-of select="@name"/>.put(aKey,aValue);
    }
  </xsl:template>

</xsl:stylesheet>
