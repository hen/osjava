package org.osjava.nio;

import java.io.IOException;
import java.io.Writer;

import java.net.Socket;

import java.nio.ByteBuffer;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SocketChannelHandler extends AbstractChannelHandler {
    protected SocketChannel chan;
    
    // Channel Writer (holds unwritten data)
    protected SocketChannelWriter writer;

    protected Collection listeners = new ArrayList();

    private boolean doClose = false;

    public SocketChannelHandler(SocketChannel chan,IOThread thread)
        throws IOException {
        super(chan, thread);
        this.chan = chan;
        writer = new SocketChannelWriter(this);
    }
    
    public Collection getChannelListeners() {
        return (Collection) ((ArrayList)listeners).clone();
    }
    
    public Writer getChannelWriter() {
        return writer;
    }
    
    public void addListener(SocketListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(SocketListener listener) {
        while(listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
    
    /** 
     * Method to read all of the data from the socketChannel and put it into 
     * the queue of data to be read from this.
     */
    public void readFromChannel() {
        /* Temporary buffer for transfering the data to the reader. */
        ByteBuffer readBuffer=ByteBuffer.allocate(1024);
        int i=0;
        
        /* If things are closed, we really don't want to be doing anything */
        if(doClose) {
            return;
        }
        
        try {
            /* Read from the socket and put the information into our readbuff.*/
            i = chan.read(readBuffer);
        } catch(IOException e) {
            /* There really should be some better handling here.  Like throwing
             * an exception.  -- Tig */
            e.printStackTrace();
        }
            
        /* Check for end of stream first.  If we've hit the end of the stream
         * it really doesn't matter whether or not anything was read close the
         * channel down.*/
        /* NOTE: I'm not sure if I believe this or not.  The big concern is
         * whether or not it's even practical to try reading anything that the
         * buffer was filled with. -- Tig */
        if(i == -1) {
            try {
                close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        
        /* Test stuff */
        readBuffer.flip();
        
        /* Write the data to the listeners. */
        Iterator it = listeners.iterator(); 
        
        while(it.hasNext()) {
            SocketListener next = (SocketListener) it.next();
            /* Flip the readbuffer so everything is setup for use */
            //readBuffer.flip();
            ByteBuffer tmpBuf = ByteBuffer.allocate(readBuffer.remaining());
            /* copy the readBuffer into the new buffer and flip it */
            tmpBuf.put(readBuffer);
            tmpBuf.flip();
            next.receiveData(tmpBuf);
        }
    }

    public void writeToChannel() throws IOException {
        if(doClose == true) {
            return;
        }
        
        /* Get the data from the Writer so that it can be written */
        ByteBuffer outData = writer.readByteBuffer();
        
        if(outData == null) {
            close();
        }
                
        try {
            chan.write(outData);
        } catch(IOException e) {
            e.printStackTrace();
        }
                
        /* Done writing, tell the key that we're no longer interested in 
         * writing to the channel.  This is essential so that we don't endlessly
         * loop over this and do NOTHING */
        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        
        // Check to see if we have any data left to write, if so,
        // don't listen to anymore write operations because something has
        // happened that we couldn't write to the channel.  
        if (outData.position() < outData.limit()) {
            if (doClose) {
                try {
                    close();
                } catch(IOException e) {
                    /* TODO: Better error handling */
                    e.printStackTrace();
                }
            }
        }
    }

    public void connect() throws IOException {
        try {
            /* If already connected we want to make sure that the socket isn't 
             * interested in connecting.  More than likely we want to throw 
             * an IllegalStateException or something similar to that too, but
             * I'm not going to be putting that in right now.  -- Robert */
            if (chan.isConnected()) {
                key.interestOps(chan.validOps() & ~SelectionKey.OP_CONNECT);
                return;
            }
            if (chan.isConnectionPending()) {
                if (chan.finishConnect()) {
                    key.interestOps(chan.validOps() & ~SelectionKey.OP_CONNECT);
                } else {
                    close();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            close();
        }
    }
    
    public void close() throws IOException {
        super.close();
        /* Close the associated buffers cleanly. */
        writer.close(true);
        doClose = true;
    }

    public SelectableChannel getSelectableChannel() {
        return chan;
    }
    
    /**
     * Set the SelectionKey for the channel.  Only one key can be set for the
     * channel handler.  If it is already set an exception is thrown
     * @param inKey
     */
    public void setSelectionKey(SelectionKey inKey) {
        if(key!=null) {
            /* Return for now, but we need to throw an exception here if it's
             * already set, I think -- Robert */
            return;
        }
        key=inKey;
    }
    
    /**
     * Get the key being used by the channel 
     * @return the SelectionKey
     */
    public SelectionKey getSelectionKey() {
        return key;
    }

    public Socket getSocket() {
        return chan.socket();
    }
    
    /* (non-Javadoc)
     * @see org.cyberiantiger.nio.ChannelHandler#accept()
     */
    public void accept() {
        // TODO: Change to throw an IllegalStateException        
    }
}
