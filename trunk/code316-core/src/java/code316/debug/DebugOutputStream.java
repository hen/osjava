package code316.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;



/**
 * Collects all bytes written to the backing OutputStream
 */
public class DebugOutputStream extends OutputStream {
    private OutputStream backingStream;
    private ByteArrayOutputStream collector = new ByteArrayOutputStream();
    private StreamListener listener;
    
    public DebugOutputStream(OutputStream backingStream) {
        this.backingStream = backingStream;
    }
    
    public void write(int b) throws IOException {
        this.collector.write(b);
        this.backingStream.write(b);
        if (this.listener != null) {
            this.listener.wrote(b);
        }        
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
        this.collector.write(b, off, len);
        this.backingStream.write(b, off, len);
        if (this.listener != null) {
            this.listener.wrote(b, off, len);
        }
    }

    public void write(byte[] b) throws IOException {        
        write(b, 0, b.length);
    }
    
    public void close() throws IOException {
        this.backingStream.close();
    }

    public void flush() throws IOException {
        this.backingStream.flush();
    }
    
    public byte[] getCollectedBytes() {
        return this.collector.toByteArray();
    }
    
    public void addStreamListener(StreamListener listener) {
        this.listener = listener;
    }
}
