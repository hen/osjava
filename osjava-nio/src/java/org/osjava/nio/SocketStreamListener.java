package org.osjava.nio;

import java.io.IOException;

public interface SocketStreamListener {

    public void setSocketChannelHandlerAcceptor(
            SocketChannelHandlerAcceptor acceptor
            );

    public void close() throws IOException;

}
