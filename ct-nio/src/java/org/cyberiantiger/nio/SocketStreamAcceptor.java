package org.cyberiantiger.nio;

public interface SocketStreamAcceptor {

    /**
     * This method must call sch.register(IOThread) to regsiter the
     * SocketChannelHandler with an IOThread, if it wishes to read/
     * write from it.
     * sch also Implements IOStream, and it must also setup an appropriate
     * input and output
     */
    public void acceptSocketChannelHandler(SocketChannelHandler sch);

}
