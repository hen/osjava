package org.osjava.nio.examples;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.CharBuffer;
import org.osjava.nio.IOThread;
import org.osjava.nio.IOUtils;
import org.osjava.nio.SocketChannelHandler;
import org.osjava.nio.CharToByteBroker;
import org.osjava.nio.ByteToCharBroker;
import org.osjava.nio.AbstractCharBroker;

public class ExampleConnection {

    public static void main(String[] args) {
        // Connect to localhost port 9999 and write 'hello'
        // Then close the connection
        try {
            IOThread iot = new IOThread();
            iot.start();
            final SocketChannelHandler handler = 
                IOUtils.connect(
                        new InetSocketAddress("localhost",9999),
                        iot);
            handler.setByteBroker(new ByteToCharBroker(
                        /* 
                         * Anonymous inner class evilness
                         */
                        new AbstractCharBroker() {
                            public void broker(CharBuffer data, boolean close) {
                                /*
                                 * Print data to System.out
                                 */
                                System.out.print(data);
                                /*
                                 * clear data to indicate we've used the
                                 * characters up
                                 */
                                data.position(data.limit());
                                /* 
                                 * If close is set, exit
                                 */
                                if(close) {
                                    System.exit(0);
                                }
                            }
                        }));
            CharToByteBroker broker = new CharToByteBroker(handler);
            broker.broker("Hello",true);

        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
