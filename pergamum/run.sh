javac Style.java
java Style  content/book.xml pergamum-html.xsl db/books.xml book isbn
java Style  content/publisher-books.xml pergamum-html.xsl db/publishers.xml publisher id
java Style  content/category-books.xml pergamum-html.xsl db/categories.xml category id

for xml in comingsoon.xml listbooks.xml listcategories.xml listpublishers.xml newbooks.xml newreviews.xml help.xml
do
    java Style content/$xml pergamum-html.xsl
done

for xml in comingsoon.xml newbooks.xml newreviews.xml
do
    java Style content/$xml pergamum-rss.xsl
done

java Style content/index.xml pergamum-html.xsl
