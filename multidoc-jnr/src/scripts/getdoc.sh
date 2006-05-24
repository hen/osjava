# DIR should be the location of the checked out svn.osjava.org/site/dist/ directory
DIR=$1
OUTPUT=$1/releases/multidoc-jnr
for i in `ls -1 $DIR/releases/official/*/*.zip $DIR/releases/official/genjava/*/*.zip | grep -v src`
do
  name=`echo $i | sed 's/.*\///' | sed 's/.zip//'`
  version=`echo $name | sed 's/^[^0-9]*\([0-9][0-9]*\..*\)$/\1/'`
  name=`echo $name | sed 's/\([^0-9]*\).*/\1/' | sed 's/-$//'`
  dest=$OUTPUT/$name/$version

  if [ -d $dest ]
  then
    echo "Skipping $dest"
  else
    echo "Grabbing $dest"
    mkdir -p $dest
    unzip -q -d $dest $i *apidocs* 2>&1 | sed "s/caution: filename not matched:.*/No Javadoc in $name $version/"
    mv $dest/*/docs/apidocs/* $dest 2>&1 | grep -v 'No such file or directory'
    rmdir $dest/*/docs/apidocs 2>&1 | grep -v 'No such file or directory'
    rmdir $dest/*/docs/ 2>&1 | grep -v 'No such file or directory'
    rmdir $dest/*/ 2>&1 | grep -v 'Directory not empty' | grep -v 'No such file or directory'
    rmdir $dest/ 2>&1 | grep -v 'Directory not empty' | grep -v 'No such file or directory'
    rmdir $OUTPUT/$name | grep -v 'Directory not empty' | grep -v 'No such file or directory'
  fi
done
