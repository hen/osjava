all:
	cc -Wall -c -I/System/Library/Frameworks/JavaVM.framework/Headers sqlite-jdbc.c
	cc -Wall -dynamiclib -o libsqlite-jdbc.jnilib sqlite-jdbc.o -lsqlite3 -framework JavaVM
	
clean:
	-rm *.o *.jnilib *.so