package com.generationjava.awt;

import java.awt.TextArea;
//import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Provides a TextArea which receives its text from an 
 * an OutputStream. There are two methods of use, one is to 
 * create a ConsoleView and then call redirectErr or redirectOut
 * to make it grab these STD streams. The other is to 
 * create a ConsoleView and get the Stream from it. Then write 
 * to the stream.
 * By default the view is uneditable, but it can be turned on if 
 * need be.
 *
 * TODO: A redirect File which possibly uses FilePoller to 
 *       update the view.
 *
 * @author bayard@generationjava.com
 */
public class ConsoleView extends TextArea {

    private PrintStream stream = null;

    public ConsoleView(String name, int i, int j) {
        super(name,i,j);
        stream = new PrintStream( new StringOutputStream(this, i) );
        setEditable(false);
    }

    public PrintStream getStream() {
        return this.stream;
    }

    public void redirectErr() {
        System.setErr( this.stream );
    }
    public void redirectOut() {
        System.setOut( this.stream );
    }
    /*
    public void redirectFile(File file) {
        // use FilePoller
    }
    */

}

// needs refactoring to make more useful. but not nice anyway.
// now with buffering, and optimisation for byte[]
class StringOutputStream extends OutputStream {

    private ConsoleView view;
    private int buffer_width;
    private byte[] buffer;
    private int idx = 0;

    public StringOutputStream(ConsoleView view, int buffer_width) {
        this.view = view;
        this.buffer_width = buffer_width;
        this.buffer = new byte[buffer_width];
    }

    public void write(byte[] bts, int st, int end) {
        if(idx != 0) {
            view.append( new String(buffer,0,idx) );
            idx = 0;
        }
        view.append( new String(bts,st,end) );
        view.setCaretPosition( view.getText().length() );
    }

    public void write(int b) throws IOException {
        buffer[idx] = (byte)b;
        idx++;
        if(b == (int)'\n') {
            view.append( new String(buffer,0,idx) );
            view.setCaretPosition( view.getText().length() );
            idx = 0;
        } else
        if(idx == buffer_width) {
            view.append( new String(buffer) );
            view.setCaretPosition( view.getText().length() );
            idx = 0;
        } else {
        }
    }

}
