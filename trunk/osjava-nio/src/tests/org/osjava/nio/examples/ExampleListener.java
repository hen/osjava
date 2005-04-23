package org.osjava.nio.examples;

import java.io.IOException;
import java.net.InetSocketAddress;


import org.osjava.nio.IOThread;
import org.osjava.nio.IOUtils;

public class ExampleListener {

    
    public static void main(String[] args) {
        ExampleListenerAcceptor handler = null;

        try {
            IOThread iot = new IOThread();
        
            handler = new ExampleListenerAcceptor(iot);
        
            iot.start();
            
            IOUtils.listen(new InetSocketAddress(9999), iot, handler);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
