package org.osjava.nio;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractSocketChannelHandlerAcceptor 
implements SocketChannelHandlerAcceptor
{

    private IOThread myThread;

    public AbstractSocketChannelHandlerAcceptor(IOThread myThread) {
        this.myThread = myThread;
    }

    public void acceptSocketChannelHandler(SocketChannelHandler sch) 
        throws IOException {
        if(!acceptConnection(sch.getSocket())) {
            sch.close();
        }
    }

    protected boolean acceptConnection(Socket con) {
        return true;
    }
}
