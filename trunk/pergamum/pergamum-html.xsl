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
       <link href="css/style.css" rel="stylesheet" type="text/css" />
      </head>
      <body>
       <div class="banner"><a href="http://www.pergamum.org/">Pergamum</a>: Books on Open Source Java</div>
       <div class="help"><a href="help.html">?</a></div>
       <div class="page">
         <xsl:apply-templates/>
       </div>
      </body>
     </html>
  </xsl:template>

  <xsl:template match="pg:rss-link">
    <xsl:variable name="name">
      <xsl:value-of select="."/>
    </xsl:variable>
    <a href="{$name}.rss">&lt;rss/&gt;</a>
  </xsl:template>

  <xsl:template match="pg:publisher-list">
    <div class="sub-title"><xsl:value-of select="."/></div>
    <ul class="publisher-list">
        <xsl:for-each select="document('db/publishers.xml')/publishers/publisher">
            <xsl:sort select="@name"/>
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
      <xsl:sort select="@name"/>
      <div class="category-title"><xsl:value-of select="@name"/></div>
      <ul class="library-list">
        <xsl:for-each select="book">
          <xsl:sort select="."/>
          <xsl:call-template name="pg:book"/>
        </xsl:for-each>
      </ul>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="pg:category-list">
    <div class="sub-title"><xsl:value-of select="."/></div>
    <ul class="category-list">
        <xsl:for-each select="document('db/categories.xml')/category-list/category">
            <xsl:sort select="@name"/>
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
    <xsl:variable name="url">
      <xsl:value-of select="document('db/publishers.xml')/publishers/publisher[@id=$id]/@url"/>
    </xsl:variable>
    <div class="sub-title"><xsl:value-of select="$name"/> - <a href="{$url}"><xsl:value-of select="$url"/></a></div>
    <ul>
      <xsl:for-each select="document('db/books.xml')/library/book[@publisher=$id]">
        <xsl:sort select="@name"/>
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
        <xsl:sort select="@name"/>
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
    <li><span class="book-name"><a href="book-{$isbn}.html"><xsl:value-of select="$name"/></a></span> - <span class="publisher-name">(<a href="{$pubUri}"><xsl:value-of select="$pubName"/></a>)</span></li>
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
    <xsl:variable name="published">
      <xsl:value-of select="document('db/books.xml')/library/book[@isbn=$isbn]/@published"/>
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
    <div class="sub-title"><xsl:value-of select="$name"/></div><br/>
    <div class="book-url">Visit the book&apos;s site: <a href="{$url}"><xsl:value-of select="$url"/></a></div><br/>
    <div class="publisher-name">(<a href="{$pubUri}"><xsl:value-of select="$pubName"/></a>)</div><br/>
    <div class="publisher-date"><xsl:value-of select="$published"/></div><br/>
    <div class="books-links"><span class="ext"><a href="http://www.amazon.com/exec/obidos/tg/detail/-/{$isbn}">[amz]</a></span> / <span class="ext"><a href="http://www.bookpool.com/.x/1/sm/{$isbn}">[bkp]</a></span> / <span class="ext"><a href="http://www.javashelf.com/servlet/books/{$isbn}">[jshf]</a></span></div><br/>
    <div class="sub-title">Review</div><br/>
    <div class="review-text"><xsl:copy-of select="$review"/></div>
  </xsl:template>

  <xsl:template match="pg:include">
    <xsl:variable name="src">
      <xsl:value-of select="@src"/>
    </xsl:variable>
    <xsl:copy-of select="document($src)/:html/:body/:div[@class='page']"/>
  </xsl:template>

  <!-- pass unrecognized nodes along unchanged -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>  

</xsl:stylesheet>
