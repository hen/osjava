package org.cyberiantiger.nio;

import java.net.Socket;

/**
 * A class to create new connection objects upon receiving a
 * new connection from the network
 */
public interface ConnectionFactory {

    /**
     * Create a new connection.
     *
     * @param sock The socket associated with the new connection.
     * @return The new Connection object, or null if the connection should be
     * dropped.
     */
    public Connection newConnection(Socket sock);

}
