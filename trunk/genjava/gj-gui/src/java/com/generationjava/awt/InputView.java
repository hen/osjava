package com.generationjava.awt;

import java.awt.TextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

/**
 * Allows a TextArea to be used as the STDIN. It is generic enough 
 * that it may be used as another InputStream than STDIN. 
 * Works via newline characters, so it's possible to hit newline and 
 * not be on the last line of the text-area.
 * Possibly refactor this so that it only works if you goto the end and 
 * hit return. Maybe even shift-return a la Mathematica.
 *
 * @author bayard@generationjava.com
 */
public class InputView extends TextArea {

    private InputStream stream;

    public InputView(String name, int i, int j) {
        super(name,i,j);
        stream = new StringInputStream(this);
        setEditable(true);
    }

    public InputStream getStream() {
        return this.stream;
    }

    public void redirectIn() {
        System.setIn( this.stream );
    }

}

// needs refactoring to make more useful. but not nice anyway.
// now with buffering, and optimisation for byte[]
class StringInputStream extends InputStream implements KeyListener {

    private InputView view;
    private byte[] buffer;
    private int idx = -1;

    public StringInputStream(InputView view) {
        this.view = view;   // bad to do this? caches the source of textevent
        view.addKeyListener(this);
    }

    public void keyPressed(KeyEvent ke) {
    }
    public void keyReleased(KeyEvent ke) {
    }
    // listen for new lines. read whenever newline appears
    public void keyTyped(KeyEvent ke) {
        if(ke.getKeyCode() != KeyEvent.VK_ENTER) {
            return;
        }
        String txt = view.getText();
        
        String line = StringUtils.chomp(txt);
        // TODO: Handle the fact the separator isn't returned here
        line = StringUtils.substringAfterLast(line, SystemUtils.LINE_SEPARATOR);
        line = StringUtils.stripStart(line, SystemUtils.LINE_SEPARATOR);
        byte[] newbuffer = line.getBytes();
        if(idx != -1) {
            byte[] tmp = new byte[newbuffer.length+buffer.length-idx];
            System.arraycopy(buffer, idx, tmp, 0, buffer.length - idx);
            System.arraycopy(newbuffer, 0, tmp, buffer.length - idx, newbuffer.length);
            newbuffer = tmp;
        }
        idx = 0;
        buffer = newbuffer;
    }

    public int read(byte[] b, int off, int len) {
        if(idx == -1) {
            return -1;
        }
        int avail = buffer.length - idx - 1;
        if(avail > len) {
            avail = len;
        }
        System.arraycopy(buffer, idx, b, off, avail);
        idx += avail;
        return avail;
    }

    public int read() throws IOException {
        if(idx == -1) {
            return -1;
        }
        if(idx == buffer.length) {
            idx = -1;
            buffer = null;
            return -1;
        }
        return buffer[idx++];
    }

}
