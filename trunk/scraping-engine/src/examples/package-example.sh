tar -zcf scraping-examples-for-`grep current ../../project.xml  | head -1 | sed 's/<[^>]*>//g' | sed 's/ //g'`.tar.gz --exclude .svn Comics
