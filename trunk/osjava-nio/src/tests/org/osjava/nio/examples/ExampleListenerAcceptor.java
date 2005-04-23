/*
 * Created on Nov 9, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.osjava.nio.examples;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.osjava.nio.AbstractSocketChannelHandlerAcceptor;
import org.osjava.nio.IOThread;
import org.osjava.nio.SocketChannelHandler;
import org.osjava.nio.SocketListener;

/**
 * @author rzigweid
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ExampleListenerAcceptor
    extends AbstractSocketChannelHandlerAcceptor 
    implements SocketListener {
    
    SocketChannelHandler handler = null;
        
    public ExampleListenerAcceptor(IOThread thread) {
        super(thread);
    }
                    
    public void acceptSocketChannelHandler(SocketChannelHandler sch) 
            throws IOException {
        handler = sch;
        sch.addListener(this);
        acceptConnection(sch.getSocket());
    }
                    
    protected boolean acceptConnection(Socket sock) {
        System.out.println("Accepting a connection: " + sock);
        return super.acceptConnection(sock);
    }

    /* (non-Javadoc)
     * @see org.osjava.nio.SocketListener#receiveData(java.nio.CharBuffer)
     */
    public void receiveData(ByteBuffer buf) {
        CharBuffer charBuffer = null;
        
        buf.rewind();
        /* Decode the ByteBuffer into a string */
        Charset charset=Charset.forName("utf8");
        CharsetDecoder decoder = charset.newDecoder();
        try {
            charBuffer = decoder.decode(buf);  
        } catch(CharacterCodingException e) {
            e.printStackTrace();
        }

        System.out.println("Received String: " + charBuffer);
       
        /* Create the echo to test the sending mechanism */
        BufferedWriter writer = new BufferedWriter(handler.getChannelWriter());
        try {
            writer.write("You typed: '" + charBuffer.toString() + "'");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}