package org.osjava.nio;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;

public abstract class AbstractSocketChannelHandlerAcceptor 
implements SocketChannelHandlerAcceptor
{

    private IOThread myThread;

    public AbstractSocketChannelHandlerAcceptor(IOThread myThread) {
        this.myThread = myThread;
    }

    public void acceptSocketChannelHandler(SocketChannelHandler sch) 
        throws IOException {
        /* Register the reading op with the channel. */
        myThread.addInterestOp(sch.getSelectionKey(), SelectionKey.OP_READ);
        if(!acceptConnection(sch.getSocket())) {
            sch.close();
        }
    }

    protected boolean acceptConnection(Socket con) {
        return true;
    }
}
