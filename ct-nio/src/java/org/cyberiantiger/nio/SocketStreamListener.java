package org.cyberiantiger.nio;

import java.io.IOException;

public interface SocketStreamListener {

    public void setSocketStreamAcceptor(SocketStreamAcceptor acceptor);

    public void close() throws IOException;

}
