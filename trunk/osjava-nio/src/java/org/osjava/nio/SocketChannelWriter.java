/*
 * Created on Nov 8, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.osjava.nio;

import java.io.IOException;
import java.io.Writer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * @author rzigweid
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SocketChannelWriter extends Writer {
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    
    private ChannelHandler parent = null;
    
    public SocketChannelWriter(ChannelHandler parent) {
        this.parent = parent;
        /* 
         * Fix the buffer's limit to be 0 initially.  It gets reset when it
         * needs to be.
         */
        buffer.limit(0);
    }

    public ByteBuffer readByteBuffer() {
        /* If there isn't a valid buffer, then we need to abort. */
        /* NOTE: I'm not sure that i like this not throwing an IO exception 
         *       here the method that calls it certainly does --Tig */
        if(buffer == null) {
            return null;
        }

        /* Make a copy of the stored information so that we can reset this 
         * buffer */
        buffer.rewind();
        ByteBuffer ret = ByteBuffer.allocateDirect(buffer.remaining());
        ret.put(buffer);
        ret.flip();
        
        /* 
         * Rewind the buffer again so that it does not reuse the previous
         * data.
         */
        buffer.rewind();
        buffer.limit(0);
        return ret;
    }
    
    /**
     * Write data to the ByteBuffer that is 
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] cbuf, int off, int len) {
        /* if the buffer is null the channel has been closed, and we need to 
         * do nothing */
        if(buffer == null) {
            return;
        }
        
        /* Create a new indirect ByteBuffer which will be appended to the 
         * SocketChannel's writeBuffer 
         */
        ByteBuffer newBuf = ByteBuffer.allocate((len - off) * 2);
        
        for(int i = off;i < len; i++) {
            newBuf.putChar(cbuf[i]);
        }
        newBuf.flip();
        write(newBuf);
    }
    
    /**
     * Add data to the writer.  Data added to the SocketChannelReader is always
     * appended to the end of the buffer.  The buffer is expanded as necessary.
     * The ByteBuffer <code>data</code> is read from its <code>data.position()
     * </code> to <code>data.limit()</code>.
     * 
     * @param data the Bytebuffer containing the data to be written into 
     *        the writer.
     */
    public void write(ByteBuffer data) {        
        /* if the buffer is null the channel has been closed, and we need to 
         * do nothing */
        if(buffer == null) {
            return;
        }

        /* Set the position mark so that we can return to it. */
        buffer.mark();
        
        /* Find the last point where there is valid data.  This always should 
         * be the current limit */
        buffer.position(buffer.limit());
        
        /* Make sure the limit is properly set at the end of the buffer */
        buffer.limit(buffer.capacity());

        /* Cheat and use the BufferOverflowException to our advantage to 
         * determine if there is room */
        try {
            buffer.put(data);
        } catch(BufferOverflowException e) {
            /* Create a new ByteBuffer that will be large enough to fit both
             * of the buffers */
            int newsize = buffer.capacity() + 
                (data.remaining() - buffer.remaining());
            ByteBuffer newbuf=ByteBuffer.allocate(newsize);
            
            /* Save the mark point */
            buffer.reset();
            int pos = buffer.position();
            
            /* rewind the buffer so we don't lose any data */
            buffer.rewind();
            
            /* Fill the new buffer with the data and set the positions and 
             * marks appropriately */
            newbuf.put(buffer);
            newbuf.put(data);
            newbuf.limit(newbuf.position());
            newbuf.position(pos);
            newbuf.mark();
            newbuf.position(newbuf.limit());
            
            /* Make the newbuffer our buffer */
            buffer = newbuf;
        }
        
        /* Set the limit to the end of the data like it's supposed to be */
        buffer.limit(buffer.position());
        
        /* Reset the buffer to the mark */
        buffer.reset();
        
        /* Let the selector know that we are ready to write. */
        SelectionKey key = parent.getSelectionKey();
        key.interestOps( key.interestOps() | SelectionKey.OP_WRITE);
    }

    /* (non-Javadoc)
     * @see java.io.Writer#flush()
     */
    public void flush() throws IOException {
        parent.writeToChannel();

    }

    /** 
     * The parent is closing.  The buffer is no longer valid.  Clean it.
     * @param parentClosed If false, nothing is done.  If true, the parent is 
     *        closing too.
     */
    public void close(boolean parentClosed) {
        if(!parentClosed) {
            return;
        }
        buffer = null;
    }
    
    /* (non-Javadoc)
     * @see java.io.Writer#close()
     */
    public void close() throws IOException {
        parent.close();
    }
}
