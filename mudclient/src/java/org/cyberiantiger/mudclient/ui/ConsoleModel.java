package org.cyberiantiger.mudclient.ui;

import java.util.*;
import java.awt.Toolkit;
import org.cyberiantiger.console.*;

/**
 * A Class to represent a text based console
 */
public class ConsoleModel implements Console {

    // The width of the console
    int width;
    // The height of the console
    int height;
    // The lines of buffer that the console uses.
    private int bufferSize;

    // The buffer each element represents a line.
    List buffer;
    // The offset into the buffer, of the first line on the screen
    int buffer_offset = 0;
    // The X position of the cursor
    private int cursor_x = 0;
    // The Y position of the cursor
    private int cursor_y = 0;
    // Current set of attributes
    private int attr = DEFAULT;

    public ConsoleModel(int width, int height, int bufferSize) {
	this.width = width;
	this.height = height;
	this.bufferSize = bufferSize;
	buffer = new LinkedList();
    }

    public int getBufferSize() {
	return buffer_offset;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public void resize(int width, int height) {
	if(width != this.width || height != this.height) {
	    this.width = width;
	    this.height = height;
	    setCursorX(getCursorX());
	    setCursorY(getCursorY());
	}
    }
    
    public void moveCursorX(int x) {
	setCursorX(getCursorX()+x);
    }
    
    public void moveCursorY(int y) {
	setCursorY(getCursorY()+y);
    }

    public int getCursorX() {
	return cursor_x;
    }

    public int getCursorY() {
	return cursor_y;
    }

    public void setCursorX(int x) {
	if(x > width) {
	    cursor_x = x % width;
	    setCursorY(getCursorY()+x/width);
	} else if(x < 0) {
	    cursor_x = 0;
	} else {
	    cursor_x = x;
	}
    }

    public void setCursorY(int y) {
	if(y >= height) {
	    buffer_offset += y - height + 1;
	    cursor_y = height - 1;
	    while(buffer_offset > bufferSize) {
		buffer.remove(0);
		buffer_offset--;
	    }
	} else if(y<0) {
	    cursor_y = 0;
	} else {
	    cursor_y = y;
	}
    }

    public void drawString(String str) {
	if(str.length() > 0) {
	    char[] data = new char[str.length()];
	    str.getChars(0,data.length,data,0);
	    drawString(data,0,data.length);
	}
    }

    public void drawString(char[] chars) {
	drawString(chars,0,chars.length);
    }

    public void drawString(char[] chars, int offset, int len) {
	if(len > 0) {
	    Line current = getCurrentLine();
	    if(cursor_x + len > width) {
		current.overwriteString(cursor_x, attr, chars, offset, width - cursor_x);
		offset += width - cursor_x;
		len -= width - cursor_x;
		setCursorX(0);
		setCursorY(cursor_y + 1);
		drawString(chars, offset, len);
	    } else {
		current.overwriteString(cursor_x, attr, chars, offset, len);
		setCursorX(cursor_x + len);
	    }
	}
    }

    public void setForeground(int color) {
	attr &= 0xffffff00;
	attr |= color;
    }

    public void setBackground(int color) {
	attr &= 0xffff00ff;
	attr |= (color << 8);
    }

    public void setBold(boolean bold) {
	attr |= BOLD;
	if(!bold) {
	    attr ^= BOLD;
	}
    }

    public void setFlash(boolean flash) {
	attr |= FLASH;
	if(!flash) {
	    attr ^= FLASH;
	}
    }

    public void setReverse(boolean reverse) {
	attr |= REVERSE;
	if(!reverse) {
	    attr ^= REVERSE;
	}
    }

    public void clearScreen() {
	for(int i=0;i<height;i++) {
	    Line line = getLine(buffer_offset + i);
	    if(line !=null) {
		line.first = null;
	    }
	}
	setCursorX(0);
	setCursorY(0);
    }

    protected List actions = new ArrayList();

    public void addAction(ConsoleAction action) {
	action.apply(this);
	updateViews();
    }

    public void beep() {
	Toolkit.getDefaultToolkit().beep();
    }

    public String toString() {
	StringBuffer ret = new StringBuffer();
	for(int i=0;i<buffer.size();i++) {
	    ret.append(buffer.get(i));
	    ret.append('\n');
	}
	return ret.toString();
    }

    protected Set views = new HashSet();

    public void addView(ConsoleModelListener view) {
	views.add(view);
    }

    public void removeView(ConsoleModelListener view) {
	views.remove(view);
    }

    protected void updateViews() {
	Iterator i = views.iterator();
	while(i.hasNext()) {
	    ConsoleModelListener view = (ConsoleModelListener) i.next();
	    view.consoleChanged();
	}
    }

    public Line getLine(int line) {
	if(line < 0 || line >= buffer.size()) {
	    return null;
	} else {
	    return (Line) buffer.get(line);
	}
    }

    protected Line getCurrentLine() {
	if(buffer_offset + cursor_y >= buffer.size()) {
	    for(int i=buffer.size();i<=buffer_offset + cursor_y;i++) {
		buffer.add(null);
	    }
	}
	Line ret = (Line) buffer.get(buffer_offset + cursor_y);
	if(ret == null) {
	    ret = new Line();
	    buffer.set(buffer_offset + cursor_y, ret);
	}
	return ret;
    }
}
