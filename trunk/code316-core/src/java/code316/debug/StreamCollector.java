package code316.debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StreamCollector implements StreamListener {
    private ByteArrayOutputStream collector = new ByteArrayOutputStream();
    public static final int WRITE_DIRECTION = 1;
    public static final int READ_DIRECTION = 2;
    private int currentDirection = 0;
    private int index = 0;
    private List directionChanges = new ArrayList();

    private void checkDirection(int direction) {
        if ( this.currentDirection != direction ) {
            // mark direction change
            directionChanges.add(new Integer(this.index));
            this.currentDirection = direction;                  
        }        
    }

        
    public void read(int data) throws IOException {
        checkDirection(READ_DIRECTION);
        this.collector.write(data);
        this.index++;        
    }


    public void wrote(int data) throws IOException {
        checkDirection(WRITE_DIRECTION);
        this.collector.write(data);
        this.index++;
    }

    public void read(byte[] buffer, int start, int length) throws IOException {
        checkDirection(READ_DIRECTION);
        this.collector.write(buffer, start, length);
        this.index += length;      
    }

    public void wrote(byte[] buffer, int offset, int length) throws IOException {
        checkDirection(WRITE_DIRECTION);
        this.collector.write(buffer, offset, length);
        this.index += length;        
    }
    
    public byte[] getCollectedBytes() {
        return this.collector.toByteArray();        
    }
    
    public String getFormattedBytes() {
        byte buffer[] = this.collector.toByteArray();
        String test = new String (buffer);
        int nextmark = -1;
        StringBuffer sb = new StringBuffer(buffer.length + this.directionChanges.size());
        
        if ( this.directionChanges .size() > 0 ) {
            nextmark = ((Integer) this.directionChanges.remove(0)).intValue();            
        }
        
        for (int i = 0; i < buffer.length; i++) {
            if ( nextmark != -1 ) {
                if ( nextmark == i) {
                    nextmark = -1;
                    sb.append("\n");
                    if ( this.directionChanges .size() > 0 ) {
                        nextmark = ((Integer) this.directionChanges.remove(0)).intValue();            
                    } 
                }
            }

            if ( Character.isLetterOrDigit((char) buffer[i])) {
                sb.append((char) buffer[i]);
            }
            else {
                sb.append("[").append(buffer[i]).append("]");
            }
        }
        
        return sb.toString();
    }
}
