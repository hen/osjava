package code316.debug;

import java.io.IOException;


public interface StreamListener {
    void wrote(int data) throws IOException;
    void wrote(byte buffer[], int start, int offset) throws IOException;
        
    void read(int data) throws IOException;
    void read(byte buffer[], int start, int offset) throws IOException;
}
