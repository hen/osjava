for i in `ls -1 /home/sites/org.osjava/dist/releases/official/*/*.zip /home/sites/org.osjava/dist/releases/official/genjava/*/*.zip | grep -v src`
do
  name=`echo $i | sed 's/.*\///' | sed 's/.zip//'`
  version=`echo $name | sed 's/^[^0-9]*\([0-9][0-9]*\..*\)$/\1/'`
  name=`echo $name | sed 's/\([^0-9]*\).*/\1/' | sed 's/-$//'`
  dest=$name/$version

  mkdir -p $dest
  unzip -q -d $dest $i *apidocs* 2>&1 | sed "s/caution: filename not matched:.*/No Javadoc in $name $version/"
  mv $dest/*/docs/apidocs/* $dest 2>&1 | grep -v 'No such file or directory'
  rmdir $dest/*/docs/apidocs 2>&1 | grep -v 'No such file or directory'
  rmdir $dest/*/docs/ 2>&1 | grep -v 'No such file or directory'
  rmdir $dest/*/ 2>&1 | grep -v 'Directory not empty' | grep -v 'No such file or directory'
done
