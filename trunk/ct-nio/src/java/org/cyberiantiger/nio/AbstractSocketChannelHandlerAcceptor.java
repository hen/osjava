package org.cyberiantiger.nio;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractSocketChannelHandlerAcceptor 
implements SocketChannelHandlerAcceptor
{

    private IOThread myThread;

    public AbstractSocketChannelHandlerAcceptor(IOThread myThread) {
        this.myThread = myThread;
    }

    public void acceptSocketChannelHandler(SocketChannelHandler sch) {
        if(acceptConnection(sch.getSocket())) {
            setupConnection(sch);
            sch.register(myThread);
        } else {
            try {
                sch.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    protected boolean acceptConnection(Socket con) {
        return true;
    }

    protected abstract void setupConnection(SocketStream stream);

}
