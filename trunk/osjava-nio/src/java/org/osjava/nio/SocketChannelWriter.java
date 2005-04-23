/*
 * org.osjava.nio.SocketChannelWriter
 *
 * $Id$
 * $URL$
 * $Rev$
 * $Date$
 * $Author$
 *
 * Copyright (c) 2003-2005, Anthony Riley, Robert M. Zigweid
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name of the OSJava-NIO nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.nio;

import java.io.IOException;
import java.io.Writer;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import java.nio.channels.SelectionKey;

/**
 * Writer for use with a {@link ChannelHandler}.  This class makes 
 * communicating over a SelectableChannel easier by not having to 
 * interact directly with the ByteBuffers that are used for passing 
 * information through the Channel.
 *  
 * @author Robert M. Zigweid
 * @version $Rev$ $Date$
 */
public class SocketChannelWriter extends Writer {
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    
    private ChannelHandler parent = null;
    
    /**
     * Create a new SocketChannelWriter with a controlling 
     * {@link ChannelHandler} <code>parent</code>.
     * 
     * @param parent the controlling ChannelHandler.  The ChannelHandler
     *        has access to the ByteBuffer that is used by the
     *        SelectableChannel.
     */
    public SocketChannelWriter(ChannelHandler parent) {
        this.parent = parent;
        /* 
         * Fix the buffer's limit to be 0 initially.  It gets reset when it
         * needs to be.
         */
        buffer.limit(0);
    }

    /**
     * Return the ByteBuffer currently being used by the Writer.  There is no 
     * guarantee that the ByteBuffer returned is going to be the one that is used
     * later.  The ByteBuffer is periodically replaced as more space for it is 
     * needed.
     * 
     * @return the ByteBuffer currently being used by the Writer
     */
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
     * Write data to underlying ByteBuffer. 
     * 
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] cbuf, int off, int len) {
        /* 
         * If the buffer is null the channel has been closed, and we need to 
         * do nothing 
         */
        if(buffer == null) {
            return;
        }
        
        /* 
         * Create a new indirect ByteBuffer which will be appended to the 
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
        /* 
         * If the buffer is null the channel has been closed, and we need to 
         * do nothing 
         */
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
        /* Make sure tha tthe Selector isn't blocked.*/
        /* 
         * XXX: This isn't a perfect solution. There's a race condition 
         *      here on multi-threaded implementations
         *      (which is expected).
         */
        parent.getThread().addInterestOp(key, SelectionKey.OP_WRITE);
        key.selector().wakeup();
    }

    /**
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
    
    /**
     * @see java.io.Writer#close()
     */
    public void close() throws IOException {
        parent.close();
    }
}
