package org.cyberiantiger.nio.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import org.cyberiantiger.nio.*;

public class ExampleListener {

    public static void main(String[] args) {
        try {
            IOThread iot = new IOThread();
            iot.start();

            IOUtils.listen(
                    new InetSocketAddress(9999),
                    iot,
                    new AbstractSocketChannelHandlerAcceptor(iot) {

                    protected boolean acceptConnection(Socket sock) {
                    System.out.println("Accepting a connection: "+sock);
                    return super.acceptConnection(sock);
                    }

                    protected Stream createStream() {
                    // This is the stream which handles the new connection
                    return new Stream() {
                    protected Output out;

                    public void writeTo(Output out) {
                    this.out = out;
                    }

                    public void write(ByteBuffer data) {
                    System.out.println("Received data: "+data);
                    // Send the data back to the socket as is
                    out.write(data);
                    }

                    public void close(ByteBuffer data) {
                        System.out.println(
                                "Connection closed remotedly with " +
                                "remaining data: "+data);
                    }
                    };
                    }
                    }
                    );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
