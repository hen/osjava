mkdir lib
cd lib
for i in `cat ../dependencies.list`
do
    wget $i
done
cd ..
