package org.osjava.nio;

import java.net.Socket;

/**
 * An interface to represent a Stream whos underlying code
 * writes and reads from a java.net.Socket
 */
public interface SocketStream {

    /**
     * Get the underlying Socket associated with this SocketStream
     */
    public Socket getSocket();

}
