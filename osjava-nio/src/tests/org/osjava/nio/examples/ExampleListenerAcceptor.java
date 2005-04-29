/*
 * Created on Nov 9, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.osjava.nio.examples;

import java.io.IOException;
import java.net.Socket;

import org.osjava.nio.AbstractSocketChannelHandlerAcceptor;
import org.osjava.nio.IOThread;
import org.osjava.nio.SocketChannelHandler;

/**
 * @author rzigweid
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ExampleListenerAcceptor extends 
AbstractSocketChannelHandlerAcceptor 
{

    public ExampleListenerAcceptor(IOThread thread) {
        super(thread);
    }

    public void acceptSocketChannelHandler(SocketChannelHandler sch)
        throws IOException
    {
        super.acceptSocketChannelHandler(sch);
        sch.setByteBroker(sch); /* Tell it to echo */
    }


    protected boolean acceptConnection(Socket sock) {
        System.out.println("Accepting a connection: " + sock);
        return super.acceptConnection(sock);
    }

}
