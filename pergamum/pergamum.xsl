<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pg="http://www.osjava.org/pergamum/ns"
                xmlns="http://www.w3.org/1999/xhtml"
  version="1.0">

  <!-- this is our top level match. Should include headers etc later on -->
  <xsl:template match="pg:pergamum">
     <html>
      <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
       <title><xsl:value-of select="@name"/></title>
      </head>
      <body>
       <xsl:apply-templates/>
      </body>
     </html>
  </xsl:template>

  <xsl:template match="pg:publisher-list">
    <div class="sub-title"><xsl:value-of select="."/></div>
    <ul>
        <xsl:for-each select="document('db/publishers.xml')/publishers/publisher">
            <xsl:variable name="id">
              <xsl:value-of select="@id"/>
            </xsl:variable>
            <li class="publisher-name"><a href="publisher-books-{$id}.html"><xsl:value-of select="@name"/></a></li>
        </xsl:for-each>
    </ul>
  </xsl:template>

  <!-- turns a category-list entry into a full nested list -->
  <xsl:template match="pg:library-list">
    <xsl:variable name="name">
      <xsl:value-of select="."/>
    </xsl:variable>
    <div class="sub-title"><xsl:value-of select="$name"/></div>
    <xsl:for-each select="document('db/categories.xml')/category-list/category">
      <div class="category-title"><xsl:value-of select="@name"/></div>
      <ul>
        <xsl:for-each select="book">
          <xsl:call-template name="pg:book"/>
        </xsl:for-each>
      </ul>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="pg:category-list">
    <div class="sub-title"><xsl:value-of select="."/></div>
    <ul>
        <xsl:for-each select="document('db/categories.xml')/category-list/category">
            <xsl:variable name="id">
              <xsl:value-of select="@id"/>
            </xsl:variable>
            <li class="category-name"><a href="category-books-{$id}.html"><xsl:value-of select="@name"/></a></li>
        </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="pg:book-list-by-publisher">
    <xsl:variable name="id">
      <xsl:value-of select="."/>
    </xsl:variable>
    <xsl:variable name="name">
      <xsl:value-of select="document('db/publishers.xml')/publishers/publisher[@id=$id]/@name"/>
    </xsl:variable>
    <div class="sub-title"><xsl:value-of select="$name"/></div>
    <ul>
      <xsl:for-each select="document('db/books.xml')/library/book[@publisher=$id]">
        <xsl:variable name="isbn">
          <xsl:value-of select="@isbn"/>
        </xsl:variable>
        <li class="book-name"><a href="book-{$isbn}.html"><xsl:value-of select="@name"/></a></li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="pg:book-list-by-category">
    <xsl:variable name="id">
      <xsl:value-of select="."/>
    </xsl:variable>
    <div class="sub-title"><xsl:value-of select="document('db/categories.xml')/category-list/category[@id=$id]/@name"/></div>
    <ul>
      <xsl:for-each select="document('db/categories.xml')/category-list/category[@id=$id]/book">
        <xsl:call-template name="pg:book"/>
      </xsl:for-each>
    </ul>
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
    <li><div align="left"><a href="book-{$isbn}.html"><xsl:value-of select="$name"/></a></div> 
    <div align="right">(<a href="{$pubUri}"><xsl:value-of select="$pubName"/></a>) - <a href="http://www.amazon.com/exec/obidos/tg/detail/-/{$isbn}">[amz]</a> - <a href="http://www.bookpool.com/.x/1/sm/{$isbn}">[bkp]</a></div></li>
  </xsl:template>

  <!-- turns a book.isbn into a full table -->
  <xsl:template match="pg:book-detailed" name="pg:book-detailed">
    <xsl:variable name="isbn">
      <xsl:value-of select="."/>
    </xsl:variable>
    <xsl:variable name="url">
      <xsl:value-of select="document('db/books.xml')/library/book[@isbn=$isbn]/@url"/>
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
    <xsl:variable name="review">
      <xsl:value-of select="document('db/reviews.xml')/reviews/review[@isbn=$isbn]"/>
    </xsl:variable>
    <div><xsl:value-of select="$name"/></div><br/>
    <div><a href="{$url}">url</a></div><br/>
    <div>(<a href="{$pubUri}"><xsl:value-of select="$pubName"/></a>)</div><br/>
    <div><a href="http://www.amazon.com/exec/obidos/tg/detail/-/{$isbn}">[amz]</a> - <a href="http://www.bookpool.com/.x/1/sm/{$isbn}">[bkp]</a></div><br/>
    <div>Review</div><br/>
    <div><xsl:value-of select="$review"/></div>
  </xsl:template>

  <xsl:template match="pg:include">
    <xsl:variable name="src">
      <xsl:value-of select="@src"/>
    </xsl:variable>
    <xsl:copy-of select="document($src)"/>
  </xsl:template>

  <!-- pass unrecognized nodes along unchanged -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>  

</xsl:stylesheet>
