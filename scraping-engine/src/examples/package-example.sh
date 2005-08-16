tar -zcf scraping-Comic-examples-for-`grep current ../../project.xml  | head -1 | sed 's/<[^>]*>//g' | sed 's/ //g'`.tar.gz --exclude .svn Comics
tar -zcf scraping-Ftp-example-for-`grep current ../../project.xml  | head -1 | sed 's/<[^>]*>//g' | sed 's/ //g'`.tar.gz --exclude .svn Ftp
tar -zcf scraping-template-for-`grep current ../../project.xml  | head -1 | sed 's/<[^>]*>//g' | sed 's/ //g'`.tar.gz --exclude .svn template
