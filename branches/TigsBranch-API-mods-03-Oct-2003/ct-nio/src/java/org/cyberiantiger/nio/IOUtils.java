package org.cyberiantiger.nio;

import java.nio.channels.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * Utility methods....
 *
 * These are the stuff most people will probably use, they're intended to
 * make using the package easier, without having to mess around with the
 * gubbings too much ;)
 */
public class IOUtils {

    /**
     * Listen on the specified port, create a new ServerSocketHandler wrapping
     * the specified channel, register it with the specified IOThread
     * and return it.
     */
    public static ServerSocketChannelHandler listen(
            InetSocketAddress addr,
            IOThread thread,
            SocketChannelHandlerAcceptor acceptor
            ) 
        throws IOException
        {
            ServerSocketChannel chan = ServerSocketChannel.open();
            ServerSocketChannelHandler handler = 
                new ServerSocketChannelHandler(chan,thread);
            int ops = chan.validOps();
            SelectionKey key=null;

            ServerSocket socket = chan.socket();
            System.out.println("Address is -- " + addr);
            socket.bind(addr);

            // Feckski Offski, I hate the way *everything* throws IOException
            // It is *NOT* an IOException if you try and register a closed 
            // channel, it is an IllegalStateException.
            //
            // Fuckwits.
            try {
                key = thread.register(handler, ops);
            } catch (IOException ioe) {
                throw new IllegalStateException("Underlying Channel is closed");
            }

            handler.setSocketChannelHandlerAcceptor(acceptor);
            return handler;
        }


    /**
     * Connect to the specified address, binding the stream to the created
     * stream, (in a loop), and using the specified IOThread.
     *
     * Note: There is no error handling here, if connectiong fails, I have
     * no clue what will happen, but this method will not throw an exception
     * as the underlying channel will be in a non-blocking mode, and hence
     * .connect() will return immediately, and an error will be thrown
     * when .finishConnect() is invoked by the SocketChannelHandler
     */
    public static SocketChannelHandler connect(
            InetSocketAddress addr,
            IOThread thread) 
        throws IOException 
        {
            SocketChannel chan = SocketChannel.open();
            SocketChannelHandler handler = 
                new SocketChannelHandler(chan,thread);
            int ops = chan.validOps();
            SelectionKey key=null;
            
            // Feckski Offski, I hate the way *everything* throws IOException
            // It is *NOT* an IOException if you try and register a closed 
            // channel, it is an IllegalStateException.
            //
            // Fuckwits.
            try {
                key = thread.register(handler, ops);
            } catch (IOException ioe) {
                throw new IllegalStateException("Underlying Channel is closed");
            }

            chan.connect(addr);

            return handler;
        }
}
