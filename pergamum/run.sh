java Style  content/book.xml pergamum.xsl db/books.xml book isbn
java Style  content/publisher-books.xml pergamum.xsl db/publishers.xml publisher id
java Style  content/category-books.xml pergamum.xsl db/categories.xml category id

for xml in comingsoon.xml listbooks.xml listcategories.xml listpublishers.xml newbooks.xml newreviews.xml 
do
    java Style content/$xml pergamum.xsl
done

java Style content/index.xml pergamum.xsl
