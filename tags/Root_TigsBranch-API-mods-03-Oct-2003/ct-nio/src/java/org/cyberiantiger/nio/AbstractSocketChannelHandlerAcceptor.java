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
            sch.register(myThread);
            setupSocketStream(sch);
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

    protected void setupSocketStream(SocketStream stream) {
        Stream programStream = createStream();
        stream.writeTo(programStream);
        programStream.writeTo(stream);
    }

    protected abstract Stream createStream();

}