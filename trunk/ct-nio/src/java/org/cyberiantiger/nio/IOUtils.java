package org.cyberiantiger.nio;

import java.nio.channels.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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
                new ServerSocketChannelHandler(chan);

            ServerSocket socket = chan.socket();
            socket.bind(addr);

            handler.setSocketChannelHandlerAcceptor(acceptor);
            handler.register(thread);
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
            IOThread thread,
            Stream stream) 
        throws IOException 
        {
            SocketChannel chan = SocketChannel.open();
            SocketChannelHandler handler = 
                new SocketChannelHandler(chan);

            chan.connect(addr);

            handler.writeTo(stream);
            stream.writeTo(handler);

            handler.register(thread);
            return handler;
        }
}
