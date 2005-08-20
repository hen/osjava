for i in `ls -1d */`
do
  i=`echo $i | sed 's/\/$//'`

  cp header-template.html $i/versions.html

  for j in `ls -1d $i/*/`
  do
    j=`echo $j | sed 's/\/$//'`
    v=`echo $j | sed 's/.*\///'`
    echo "<nobr><a name='$v'><font class='FrameItemFont'><a href='$v/index.html'>$i $v</a></font></nobr><br/>" >> $i/versions.html
  done

  echo '</body></html>' >> $i/versions.html

  cp blank-template.html $i/blank.html
  cp index-template.html $i/index.html

done

