all:
	cc -g -Wall -c -I/System/Library/Frameworks/JavaVM.framework/Headers -I/opt/local/include sqlite-jdbc.c
	cc -Wall -dynamiclib -o libsqlite-jdbc.jnilib sqlite-jdbc.o -L/opt/local/lib -lsqlite3 -framework JavaVM
	
clean:
	-rm *.o *.jnilib *.so
