package org.cyberiantiger.nio.test;

import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import org.cyberiantiger.nio.*;

public class Test {

    public static void main(String[] args) throws Exception {
	ConnectionThread thread = new ConnectionThread();

	new Thread(thread).start();

	thread.listen(
		new InetSocketAddress(4444),
		new TestConnectionFactory()
		);
    }

}

class TestConnectionFactory implements ConnectionFactory {
    public Connection newConnection(Socket sock) {
	System.out.println("New Connection: "+sock);
	return new TestConnection();
    }
}

class TestConnection implements Connection {

    private Connection con;

    public void setConnection(Connection con) {
	this.con = con;
    }

    public void write(ByteBuffer bytes) {
	System.out.println("Written: "+bytes);
	con.close(bytes);
    }

    public void close(ByteBuffer bytes) {
	System.out.println("Closed: "+bytes);
	con = null;
    }
}

