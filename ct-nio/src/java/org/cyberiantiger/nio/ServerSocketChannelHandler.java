package org.cyberiantiger.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SelectableChannel;

public class ServerSocketChannelHandler 
extends AbstractChannelHandler 
implements SocketStreamListener 
{
    private ServerSocketChannel chan;
    private SocketStreamAcceptor acceptor;

    public ServerSocketChannelHandler(
	    ServerSocketChannel chan
	    ) 
    throws IOException
    {
	chan.configureBlocking(false);
	this.chan = chan;
    }

    public void setSocketStreamAcceptor(SocketStreamAcceptor acceptor) {
	this.acceptor = acceptor;
    }

    public void accept() {
	try {
	    SocketChannel sockChan = chan.accept();
	    if(sockChan != null) {
		// Create a new SocketChannelHandler
		SocketChannelHandler sch = 
		new SocketChannelHandler(sockChan);

		acceptor.acceptSocketStream(sch);
	    }
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
    }

    public void close() throws IOException {
	chan.close();
	acceptor = null;
    }
    
    public SelectableChannel getSelectableChannel() {
	return chan;
    }

    public ServerSocket getServerSocket() {
	return chan.socket();
    }
}
