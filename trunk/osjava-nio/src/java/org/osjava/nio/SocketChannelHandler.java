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

import java.net.Socket;

import java.nio.ByteBuffer;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SocketChannelHandler 
    extends AbstractChannelHandler 
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
            throw new RuntimeException("readFromChannel called after " +
                    "readClosed, this is a bug !");
        }
        if(!readBuffer.hasRemaining()) {
            throw new RuntimeException("readFromChannel called when " +
                    "readBuffer is full, this is a bug !");
        }
        
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

        /*
         * Pass the data to aBroker
         */
        if(aBroker != null) {
            readBuffer.flip();
            try {
                aBroker.broker(readBuffer, readClosed);
            } catch (BrokerException be) {} /* Ignored */
            readBuffer.compact();
        }

        /*
         * No longer interested in read ops
         * (readBuffer full, or stream closed by the remote end)
         */
        if(readClosed || !readBuffer.hasRemaining()) {
            getThread().removeInterestOp(this, SelectionKey.OP_READ);
        }
    }

    public void writeToChannel() throws IOException {
        if(writeBuffer.position() == 0) {
            System.out.println("writeToChannel called when there is " +
                    "no data to be written, this is a BUG.");
            System.out.println("If you see this message lots you " +
                    "need to turn off OP_WRITE");
            getThread().removeInterestOp(this, SelectionKey.OP_WRITE);
            return;
        }
        
        /* 
         * Write some data to the channel
         */
        writeBuffer.flip();
        try {
            chan.write(writeBuffer);
        } catch (IOException ioe) {
            /*
             * Some bugger has closed the stream at the other end
             * (or the network connection died, or some other such evil
             * ness).
             * 
             * Best way I can think of to handle this is to empty the
             * write buffer, remove interest in write ops, and set
             * the writeClosed variable to true.
             *
             * However if we do this we can pretty much guarantee 
             * something will continue trying to write to us.
             *
             * Though typically we'll handle this by throwing
             * some exception to indicate that the damn stream 
             * has been closed. (Right now it's just a RuntimeException)
             *
             * Information in the writeBuffer is lost, this is unavoidable.
             */
            getThread().removeInterestOp(this, SelectionKey.OP_WRITE);
            writeClosed = true;
            writeBuffer.clear();
            return;
        }
        writeBuffer.compact();

        /*
         * If buffer is empty
         */
        if(writeBuffer.position() == 0) {
            /*
             * And we've been told to close the stream
             */
            if(writeClosed) {
                /* TODO: half close the socket */
            }
            /* 
             * Remove interest in write operations
             */
            getThread().removeInterestOp(this, SelectionKey.OP_WRITE);
        }

        /*
         * Now we've hopefully made some space in the writeBuffer
         * If there's data in the readBuffer dispatch it.
         */
        if(readBuffer.position() > 0 && aBroker != null) {
            boolean readBufferFull = !readBuffer.hasRemaining();
            readBuffer.flip();
            try {
                aBroker.broker(readBuffer, readClosed);
            } catch (BrokerException be) {} /* Ignored */
            readBuffer.compact();
            /*
             * If the read buffer was full and it isn't any more
             * we need to reregister interest in READ operations
             */
            if(readBufferFull && readBuffer.hasRemaining()) {
                getThread().addInterestOp(this, SelectionKey.OP_READ);
            }
        }
    }

    public void connect() throws IOException {
        /*
         * If we're already connected, just remove our interest in the
         * OP_CONNECT event.
         */
        if (chan.isConnected()) {
            getThread().removeInterestOp(this, SelectionKey.OP_CONNECT);
            return;
        }
        /* 
         * If our channel is waiting for the connection to be completed
         * complete it and remove our interest in the OP_CONNECT event.
         */
        if (chan.isConnectionPending()) {
            if (chan.finishConnect()) {
                getThread().removeInterestOp(this, SelectionKey.OP_CONNECT);
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
            throw new BrokerException("Stream closed " +
                    "(either by you, or an error was encountered)");
        }
        
        if(writeBuffer.hasRemaining() && data.hasRemaining()) {
            /* FIXME: This logic is flawed. You want to append to the end of 
             *        the writeBuffer's data, not overwrite it. maybe limit()?*/
            if(data.remaining() <= writeBuffer.remaining()) {
                writeBuffer.put(data);
            } else {
                /* 
                 * all buffers created via .allocate() have a backing array and
                 * an array offset of 0 so we can cheat here
                 * WTF there isn't a provided method to do this, I don't know.
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
        
        /* XXX: I think more work needs to go here -- RMZ */
        if(writeBuffer.position() > 0) {
            /* If there's no selection key yet, we need to queue information up. */
            if(getSelectionKey() != null) {
                getThread().addInterestOp(this, SelectionKey.OP_WRITE);
            }
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
