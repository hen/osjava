package org.cyberiantiger.mudclient.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.cyberiantiger.console.*;

public class ConsoleView extends JComponent implements ConsoleModelListener
{

    private ConsoleModel model;
    private int lineOffset = 0;

    public ConsoleView(ConsoleModel model) {
	this.model = model;
	model.addView(this);
	setOpaque(true);
	setFont(new Font("monospaced",Font.PLAIN,14));
	setForeground(Color.white);
	setBackground(Color.black);
	setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
	enableEvents(
		AWTEvent.MOUSE_EVENT_MASK |
		AWTEvent.MOUSE_MOTION_EVENT_MASK |
		AWTEvent.MOUSE_WHEEL_EVENT_MASK |
		AWTEvent.COMPONENT_EVENT_MASK);

    }

    public synchronized void paintComponent(Graphics g) {
	int width = getWidth();
	int height = getHeight();
	g.setFont(getFont());
	FontMetrics fm = g.getFontMetrics();
	int char_width = fm.charWidth('A');
	int row_height = fm.getHeight();
	{
	    // Fill the borders, and translate the origin
	    int w = model.getWidth() * char_width;
	    int x = (width - w) / 2;
	    int h = model.getHeight() * row_height;
	    int y = (height - h) / 2;
	    g.setColor(getBackground());
	    g.fillRect(0,0,width,y);
	    g.fillRect(0,y+h,width,height-y-h);
	    g.fillRect(0,y,x,h);
	    g.fillRect(x+w,y,width-x-w,h);
	    g.translate(x,y);
	}
	int text_offset = row_height - fm.getDescent();
	int y = 0;
	for(int i=0;i<model.getHeight();i++) {
	    Line line = model.getLine(lineOffset + i);
	    if(line == null) {
		g.setColor(getBackground());
		g.fillRect(0,y,width,y+row_height);
	    } else {
		LineSegment segment = line.first;
		int x = 0;
		while(segment != null) {
		    int chars_width = fm.charsWidth(segment.data,segment.offset,segment.len);
		    fm.charsWidth(segment.data,segment.offset,segment.len);
		    g.setColor(getBackgroundColor(segment.attr));
		    g.fillRect(x,y,chars_width,row_height);
		    g.setColor(getForegroundColor(segment.attr));
		    g.drawChars(segment.data,segment.offset,segment.len,x,y+text_offset);
		    x += chars_width;
		    segment = segment.next;
		}
		g.setColor(getBackground());
		g.fillRect(x,y,width-x,row_height);
	    }
	    y+=row_height;
	}
	g.fillRect(0,y,width,height);
    }

    public int getRowHeight() {
	return getFontMetrics(getFont()).getHeight();
    }

    public Color getForegroundColor(int attr) {
	switch(attr & 7) {
	    case ConsoleModel.BLACK:
		return Color.black;
	    case ConsoleModel.RED:
		return Color.red;
	    case ConsoleModel.YELLOW:
		return Color.yellow;
	    case ConsoleModel.GREEN:
		return Color.green;
	    case ConsoleModel.CYAN:
		return Color.cyan;
	    case ConsoleModel.BLUE:
		return Color.blue;
	    case ConsoleModel.MAGENTA:
		return Color.magenta;
	    case ConsoleModel.WHITE:
		return Color.white;
	}
	return Color.white;
    }

    public void setLineOffset(int lineOffset) {
	this.lineOffset = lineOffset;
	repaint();
    }

    public Color getBackgroundColor(int attr) {
	switch((attr>>8) & 7) {
	    case ConsoleModel.BLACK:
		return Color.black;
	    case ConsoleModel.RED:
		return Color.red;
	    case ConsoleModel.YELLOW:
		return Color.yellow;
	    case ConsoleModel.GREEN:
		return Color.green;
	    case ConsoleModel.CYAN:
		return Color.cyan;
	    case ConsoleModel.BLUE:
		return Color.blue;
	    case ConsoleModel.MAGENTA:
		return Color.magenta;
	    case ConsoleModel.WHITE:
		return Color.white;
	}
	return Color.black;
    }

    public Dimension getPreferredSize() {
	FontMetrics fm = getFontMetrics(getFont());
	int row_height = fm.getHeight();
	int char_width = fm.charWidth('A');
	return new Dimension(
		model.getWidth() * char_width, 
		model.getHeight() * row_height
		);

    }

    public Dimension getMinimumSize() {
	FontMetrics fm = getFontMetrics(getFont());
	int row_height = fm.getHeight();
	int char_width = fm.charWidth('A');
	return new Dimension(
		char_width, 
		row_height
		);
    }

    /**
     * Write something to our model
     */
    public synchronized void consoleAction(ConsoleAction action) {
	cancelSelection();
	model.addAction(action);
    }

    /**
     * Resize our model depending on our size
     */
    public synchronized void resizeConsole() {
	FontMetrics fm = getFontMetrics(getFont());
	int width = getWidth() /  fm.charWidth('A');
	int height = getHeight() / fm.getHeight();

	if(width < 1) {
	    width = 1;
	}
	if(height < 1) {
	    height = 1;
	}
	model.addAction(new ResizeConsoleAction(width,height));
    }

    private boolean doing_selection;
    private int x_start;
    private int y_start;

    private int char_x_start;
    private int char_y_start;
    private int char_x_end;
    private int char_y_end;

    private void startSelection(int x, int y) {
    }

    private void continueSelection(int x, int y) {
    }

    private void endSelection(int x, int y) {
    }

    private void cancelSelection() {
    }

    protected void processComponentEvent(ComponentEvent ce) {
	super.processComponentEvent(ce);
	if(ce.getID() == ComponentEvent.COMPONENT_RESIZED) {
	    resizeConsole();
	}
    }

    protected void processMouseEvent(MouseEvent me) {
	super.processMouseEvent(me);
	if(me.getID() == MouseEvent.MOUSE_PRESSED) {
	    startSelection(me.getX(),me.getY());
	} else if(me.getID() == MouseEvent.MOUSE_RELEASED) {
	    endSelection(me.getX(),me.getY());
	}
    }

    protected void processMouseMotionEvent(MouseEvent me) {
	super.processMouseMotionEvent(me);
	if(me.getID() == MouseEvent.MOUSE_DRAGGED) {
	    continueSelection(me.getX(),me.getY());
	}
    }

    protected void processMouseWhellEvent(MouseWheelEvent mwe) {
	System.out.println("Got MouseWheelEvent: "+mwe);
	super.processMouseWheelEvent(mwe);
    }

    public void consoleChanged() {
	repaint();
    }
}
