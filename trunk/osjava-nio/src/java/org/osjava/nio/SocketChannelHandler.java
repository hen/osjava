/*
 * org.osjava.nio.SocketChannelHandler
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

import java.net.Socket;

import java.nio.ByteBuffer;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SocketChannelHandler extends AbstractChannelHandler 
implements ByteBroker 
{
    /**
     * The underlying SocketChannel this ChannelHandler wraps.
     */
    protected SocketChannel chan;
    
    /**
     * Buffer to hold data to be written to the channel
     */
    protected ByteBuffer writeBuffer = ByteBuffer.allocate(0x10000);

    /**
     * Buffer to hold unhandled data read from the channel
     */
    protected ByteBuffer readBuffer = ByteBuffer.allocate(0x10000);

    /**
     * The ByteBroker which handles data read from the channel
     */
    protected ByteBroker aBroker = null;

    /**
     * A flag to indicate we've reached the end of stream for reading
     * from the channel
     */
    protected boolean readClosed = false;

    /**
     * A flag to indicate we've reached the ned of stream for writing
     * to the channel
     */
    protected boolean writeClosed = false;

    public SocketChannelHandler(SocketChannel chan,IOThread thread)
        throws IOException {
        super(chan, thread);
        this.chan = chan;
    }
    
    /**
     * Set the ByteBroker this ChannelHandler uses.
     */
    public void setByteBroker(ByteBroker aBroker) {
        this.aBroker = aBroker;
    }
    
    /** 
     * Method to read all of the data from the socketChannel and put it into 
     * the queue of data to be read from this.
     */
    public void readFromChannel() throws IOException {
        if(readClosed) {
            System.out.println("readFromChannel called after readClosed, "+
                    "SHOULD NOT happen (tm)");
            return;
        }
        
        /*
         * If buffer isn't alread full
         */
        if(readBuffer.hasRemaining()) {
            /*
             * hopefully read some data
             */
            try {
                readClosed = ( -1 == chan.read(readBuffer) );
            } catch (IOException ioe) {
                try {
                    ioe.printStackTrace();
                    close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }

        /*
         * If we have any data, and a broker set, give them data to play with.
         */
        if(readBuffer.position() > 0 && aBroker != null) {
            readBuffer.flip();
            aBroker.broker(readBuffer, readClosed);
            readBuffer.compact();
        }

        if(readClosed) {
            getThread().removeInterestOp(key, SelectionKey.OP_READ);
        }
    }

    public void writeToChannel() throws IOException {
        if(writeClosed && writeBuffer.position() == 0) {
            return;
        }
        
        /*
         * If we have data to write, write some
         */
        if(writeBuffer.position() > 0) {
            writeBuffer.flip();
            try {
                chan.write(writeBuffer);
            } catch (IOException ioe) {
                try {
                    close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
            writeBuffer.compact();
        } else if(writeClosed) {
            
        }

        /*
         * If we have any data in the read buffer see if the broker wants it
         * yet. (they might be able to do something with it now there's
         * space in the write buffer)
         */
        if(readBuffer.position() > 0 && aBroker != null) {
            readBuffer.flip();
            aBroker.broker(readBuffer, readClosed);
            readBuffer.compact();
        }
    }

    public void connect() throws IOException {
        /*
         * If we're already connected, just remove our interest in the
         * OP_CONNECT event.
         */
        if (chan.isConnected()) {
            getThread().removeInterestOp(key, SelectionKey.OP_CONNECT);
            return;
        }
        /* 
         * If our channel is waiting for the connection to be completed
         * complete it and remove our interest in the OP_CONNECT event.
         */
        if (chan.isConnectionPending()) {
            if (chan.finishConnect()) {
                getThread().removeInterestOp(key, SelectionKey.OP_CONNECT);
            } else {
                close();
            }
            return;
        }
        /*
         * This should never be reached, we've got an OP_CONNECT event
         * and our chan is not already connected and not in the connection
         * pending state
         */
        throw new IllegalStateException("Received OP_CONNECT event for a " +
                "channel which is not in the pending state, nor is it " +
                "connected.");
    }
    
    public void close() throws IOException {
        super.close();
        /* TODO: Close the associated buffers cleanly. */
        writeClosed = true;
        readClosed = true;
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
            throw new IllegalStateException("SelectionKey has already been set");
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


    /*
     * Methods from ByteBroker
     */
    public void broker(ByteBuffer data, boolean close) {
        if(writeClosed) {
            /* FIXME */
            throw new RuntimeException("Write closed, but still being sent data");
        }
        if(writeBuffer.hasRemaining() && data.hasRemaining()) {
            if(data.remaining() <= writeBuffer.remaining()) {
                writeBuffer.put(data);
            } else {
                /* 
                 * all buffers created via .allocate() have a backing array and
                 * an array offset of 0 so we can cheat here
                 * WTF there isn't a provided to do this, I don't know.
                 * even a .get(ByteBuffer) method would do !
                 */
                data.get(writeBuffer.array(), 
                        writeBuffer.position(), 
                        writeBuffer.remaining());
            }
        }
        if(close && !data.hasRemaining()) {
            writeClosed = true;
        }
        if(writeBuffer.position() > 0) {
            getThread().addInterestOp(getSelectionKey(), SelectionKey.OP_WRITE);
        }
    }

    public int broker(byte[] data, boolean close) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        broker(buffer, close);
        return buffer.position();
    }

    public int broker(byte[] data, int offset, int len, boolean close) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        broker(buffer, close);
        return buffer.position() - offset;
    }
}
