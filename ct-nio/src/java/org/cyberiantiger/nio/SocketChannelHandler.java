package org.cyberiantiger.nio;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SelectableChannel;
import java.nio.ByteBuffer;

public class SocketChannelHandler 
extends AbstractChannelHandler
implements SocketStream
{
    protected Output out;
    // The underlying channel
    protected SocketChannel chan;

    // Our write buffer (holds unwritten data
    private ByteBuffer writeBuffer;
    // Our read buffer (used for holding data that has been read)
    private ByteBuffer readBuffer;

    private boolean doClose = false;

    public SocketChannelHandler(
        SocketChannel chan
        ) 
    throws IOException 
    {
    this.chan = chan;
    chan.configureBlocking(false);
    writeBuffer = ByteBuffer.allocateDirect(1024);
    readBuffer = ByteBuffer.allocateDirect(1024);
    }

    public void writeTo(Output out) {
    this.out = out;
    }

    public void read() {
    try {
        // Need to read from the socket, and pass it to an abstract method
        int i = chan.read(readBuffer);
        if(readBuffer.position() != 0) {
        readBuffer.limit(readBuffer.position());
        readBuffer.rewind();
        try {
            out.write(readBuffer);
        } catch (RuntimeException re) {
            re.printStackTrace();
        }
        readBuffer.compact();
        readBuffer.limit(readBuffer.capacity());
        } 
        if(i == -1) {
        close();
        }
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
    }

    public void write() {
    try {
        if(writeBuffer.position() != 0) {
        writeBuffer.limit(writeBuffer.position());
        writeBuffer.rewind();
        chan.write(writeBuffer);
        writeBuffer.compact();
        writeBuffer.limit(writeBuffer.capacity());
        }
        // Check to see if we have any data left to write, if so,
        // don't listen to anymore write operations.
        if(writeBuffer.position() == 0) {
        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        if(doClose) {
            close();
        }
        }
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
    }

    public void connect() {
    try {
        if(chan.isConnectionPending()) {
        chan.finishConnect();
        }
        if(chan.isConnected()) {
        key.interestOps(SelectionKey.OP_READ);
        }
    } catch (IOException ioe) {
        ioe.printStackTrace();
        try {
        close();
        } catch (IOException ioe2) {
        ioe2.printStackTrace();
        }
    }
    }

    public void close() throws IOException {
    key.cancel();
    if(chan.isOpen()) {
        chan.close();
    }
    readBuffer.limit(readBuffer.position());
    readBuffer.rewind();
    out.close(readBuffer);
    out = null;
    }

    public SelectableChannel getSelectableChannel() {
    return chan;
    }

    private void resizeBuffer(int size) {
    int newSize = writeBuffer.capacity() << 1;
    ByteBuffer newBuffer;
    if(newSize >= size) {
        newBuffer = ByteBuffer.allocateDirect(newSize);
    } else {
        newBuffer = ByteBuffer.allocateDirect(size);
    }
    writeBuffer.limit(writeBuffer.position());
    writeBuffer.rewind();
    newBuffer.put(writeBuffer);
    writeBuffer = newBuffer;
    }

    public void setSelectionKey(SelectionKey key) {
    this.key = key;
    }

    public void write(ByteBuffer buffer) {
    // Fill our buffer, and ensure we're interested in write ops.
    if( buffer.remaining() > writeBuffer.remaining() ) {
        resizeBuffer(buffer.remaining()+writeBuffer.position());
    }
    writeBuffer.put(buffer);
    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }

    public void close(ByteBuffer last) {
    if(last != null) write(last);
    if(writeBuffer.position() != 0) {
        doClose = true;
    } else {
        try {
        close();
        } catch (IOException ioe) {
        ioe.printStackTrace();
        }
    }
    }

    public Socket getSocket() {
    return chan.socket();
    }

}
