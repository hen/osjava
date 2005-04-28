package org.osjava.nio.examples;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.osjava.nio.IOThread;
import org.osjava.nio.IOUtils;
import org.osjava.nio.SocketChannelHandler;
import org.osjava.nio.CharBroker;
import org.osjava.nio.CharToByteBroker;

public class ExampleConnection {

    public static void main(String[] args) {
        SocketChannelHandler handler = null;
        // Connect to localhost port 9999 and write 'hello'
        // Then close the connection
        try {
            IOThread iot = new IOThread();
            iot.start();
            handler = IOUtils.connect(
                                      new InetSocketAddress("localhost",9999),
                                      iot);
            CharBroker broker = new CharToByteBroker(handler);
            broker.broker("Hello",true);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
