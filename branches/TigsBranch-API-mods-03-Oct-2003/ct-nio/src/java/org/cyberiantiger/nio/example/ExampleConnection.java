package org.cyberiantiger.nio.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.cyberiantiger.nio.*;

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
            BufferedWriter chanWriter = 
                new BufferedWriter(handler.getChannelWriter());
            chanWriter.write("Testing Connection to Server!");
            chanWriter.flush();
            handler.close();

        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
