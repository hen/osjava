package com.generationjava.payload;

import java.util.*;

import java.io.*;

public class IOUtils {

    static void pushBytes(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1023];
        while(true) {
            int size = in.read(buffer);
            if(size == -1) {
                break;
            }
            out.write(buffer,0,size);
        }
    }

    static String readToString(InputStream in) throws IOException {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while( (line = rdr.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    static void closeQuietly(InputStream in) {
        if(in != null) {
            try {
                in.close();
            } catch(IOException ioe) {
                // ignore
            }
        }
    }

    static void closeQuietly(OutputStream out) {
        if(out != null) {
            try {
                out.close();
            } catch(IOException ioe) {
                // ignore
            }
        }
    }

}
