package com.generationjava.apps.jpe;

import java.io.*;

public class Log2AreaStream  extends FilterOutputStream 
{
        OpenFile file;

        public void setFile(OpenFile file) {
                this.file=file;
        }

        
        public Log2AreaStream(OutputStream LStream) {
                super(LStream);
        }
        

        public void write(byte b[]) throws IOException {
                String s=new String(b);
                file.appendText(s);
        }

        public void write(byte b[], int off, int len) throws IOException {
                String s=new String(b, off, len);
                file.appendText(s);
        }
}
