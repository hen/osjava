for xml in comingsoon.xml listbooks.xml listcategories.xml listpublishers.xml newbooks.xml newreviews.xml tmpbook.xml
do
    java Style content/$xml pergamum.xsl
done
