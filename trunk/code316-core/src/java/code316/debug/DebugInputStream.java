package code316.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Collects all bytes read from the backing InputStream.
 */
public class DebugInputStream extends InputStream {
    private InputStream backingStream;
    private ByteArrayOutputStream collector = new ByteArrayOutputStream();
    private StreamListener listener;
    
    public DebugInputStream(InputStream backingStream) {
        this.backingStream = backingStream;
    }

    public int read() throws IOException {
        int read = this.backingStream.read();
        this.collector.write(read);
        if ( this.listener != null ) {
            this.listener.read(read);
        }
        
        return read;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int read = this.backingStream.read(b, off, len);
        this.collector.write(b, off, read);
        if ( this.listener != null ) {
            this.listener.read(b, off, read);
        }
         
        return read;
    }

    public int available() throws IOException {
        return this.backingStream.available();
    }

    public void close() throws IOException {
        this.backingStream.close();
    }

    public synchronized void mark(int readlimit) {
        this.backingStream.mark(readlimit);
    }

    public boolean markSupported() {
        return this.backingStream.markSupported();
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public synchronized void reset() throws IOException {
        this.backingStream.reset();
    }

    public long skip(long n) throws IOException {
        return this.backingStream.skip(n);
    }
    
    public byte[] getCollectedBytes() {
        return this.collector.toByteArray();
    }
    
    public void addStreamListener(StreamListener listener) {
        this.listener = listener;
    }
}
