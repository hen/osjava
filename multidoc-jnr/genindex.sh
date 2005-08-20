for i in `ls -1d */`
do
  i=`echo $i | sed 's/\/$//'`

  cp header-template.html $i/versions.html

  for j in `ls -1d $i/*/`
  do
    j=`echo $j | sed 's/\/$//'`
    v=`echo $j | sed 's/.*\///'`
    if [ -f $j/overview-frame.html ];
    then
        u="'packageListFrame', '$v/overview-frame.html'";
    else
        f=`find $j -type f -name 'package-frame.html' | sed 's/[^\/]*\///'`;
        u="'packageListFrame', '$f'";
    fi

    u="$u, 'packageFrame','$v/allclasses-frame.html'";

    if [ -f $j/overview-summary.html ];
    then
        u="$u, 'classFrame', '$v/overview-summary.html'";
    else
        f=`find $j -type f -name 'package-summary.html' | sed 's/[^\/]*\///'`;
        u="$u, 'classFrame', '$f'";
    fi

    echo "<nobr><a name=\"$v\"><font class=\"FrameItemFont\"><a href=\"javascript:load($u)\">$i $v</a></font></nobr><br/>" >> $i/versions.html
  done

  echo '</body></html>' >> $i/versions.html

  cp blank-template.html $i/blank.html
  cp index-template.html $i/index.html

done

