package org.cyberiantiger.nio.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.cyberiantiger.nio.*;

public class ExampleConnection {

    public static void main(String[] args) {
        // Connect to localhost port 9999 and write 'hello'
        // Then close the connection
        try {
            IOThread iot = new IOThread();
            iot.start();

            Stream myStream = new Stream() {
                private Output out;

                public void writeTo(Output out) {
                    this.out = out;
                    System.out.println("Sending: TEST");
                    out.write(ByteBuffer.wrap("TEST".getBytes()));
                }

                public void write(ByteBuffer data) {
                    byte [] bytes = new byte[data.remaining()];
                    data.get(bytes);
                    System.out.println("Received: "+new String(bytes));
                    out.close(null);
                }

                public void close(ByteBuffer data) {
                    System.out.println("Connection closed with remaining "  +
                            "input data: "+data);
                }
            };

            IOUtils.connect(
                    new InetSocketAddress("localhost",9999),
                    iot,
                    myStream);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
