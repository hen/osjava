package org.cyberiantiger.mudclient.ui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import org.cyberiantiger.console.*;

public class ConsoleView extends JComponent implements ConsoleModelListener
{

    private ConsoleModel model;

    private Insets borderArea;
    private int rowHeight;
    private int charWidth;

    public ConsoleView(ConsoleModel model) {
	this.model = model;
	model.addView(this);
	setOpaque(true);
	borderArea = new Insets(0,0,0,0);
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

    public void setFont(Font f) {
	super.setFont(f);
	FontMetrics fm = getFontMetrics(f);
	rowHeight = fm.getHeight();
	charWidth = fm.charWidth('A');
    }

    /**
     * Resize our model depending on our size
     */
    public void resizeConsole() {
	if(isVisible()) {
	    int width = getWidth() / charWidth;
	    int height = getHeight() / rowHeight;

	    if(width < 1) {
		width = 1;
	    }
	    if(height < 1) {
		height = 1;
	    }

	    borderArea.left = (getWidth() - width * charWidth)/2;
	    borderArea.top = (getHeight() - height * rowHeight)/2;
	    borderArea.right = getWidth() - width * charWidth - borderArea.left;
	    borderArea.bottom = getHeight() - height * rowHeight - borderArea.top;

	    synchronized(this) {
		model.addAction(new ResizeConsoleAction(width,height));
	    }
	}
    }

    public synchronized void paintComponent(Graphics g) {
	int width = getWidth();
	int height = getHeight();
	g.setFont(getFont());
	FontMetrics fm = g.getFontMetrics();
	{
	    g.setColor(getBackground());
	    g.fillRect(0,0,width,borderArea.top);
	    g.fillRect(0,height-borderArea.bottom,width,borderArea.bottom);
	    g.fillRect(0,borderArea.top,borderArea.left,height-borderArea.top-borderArea.bottom);
	    g.fillRect(width-borderArea.right,borderArea.top,borderArea.right,height-borderArea.top-borderArea.bottom);
	    g.translate(borderArea.left,borderArea.top);
	}
	int text_offset = rowHeight - fm.getDescent();
	int y = 0;

	for(int i=0;i<model.getHeight();i++) {
	    Line line = model.getViewLine(i);
	    if(line == null) {
		g.setColor(getBackground());
		g.fillRect(0,y,width,y+rowHeight);
	    } else {
		LineSegment segment = line.first;
		int x = 0;
		while(segment != null) {
		    int chars_width = 
		    fm.charsWidth(segment.data,segment.offset,segment.len);
		    g.setColor(getBackgroundColor(segment.attr));
		    g.fillRect(x,y,chars_width,rowHeight);
		    g.setColor(getForegroundColor(segment.attr));
		    g.drawChars(segment.data,segment.offset,segment.len,x,y+text_offset);
		    x += chars_width;
		    segment = segment.next;
		}
		g.setColor(getBackground());
		g.fillRect(x,y,width-x,rowHeight);
	    }
	    y+=rowHeight;
	}
	if(model.hasSelection()) {
	    Point start = model.getSelectionStart();
	    Point end = model.getSelectionEnd();
	    g.setXORMode(getBackground());
	    g.setColor(getForeground());
	    if(start.y == end.y) {
		g.fillRect(start.x * charWidth, start.y * rowHeight, (end.x - start.x) * charWidth, rowHeight);
	    } else {
		g.fillRect(start.x * charWidth, start.y * rowHeight, (model.getWidth() - start.x) * charWidth, rowHeight);
		g.fillRect(0,end.y * rowHeight, end.x * charWidth, rowHeight);
		if(end.y - start.y > 1) {
		    g.fillRect(0,(start.y+1)*rowHeight,model.getWidth()*charWidth, (end.y-start.y-1)*rowHeight);
		}
	    }
	}
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
	return new Dimension(
		model.getWidth() * charWidth, 
		model.getHeight() * rowHeight
		);

    }

    public Dimension getMinimumSize() {
	return new Dimension( charWidth, rowHeight);
    }

    /**
     * Write something to our model
     */
    public synchronized void consoleAction(ConsoleAction action) {
	model.addAction(action);
    }

    public Point getCharacterAt(Point p) {
	if(
		p.x > borderArea.left && 
		p.x < getWidth()-borderArea.right &&
		p.y > borderArea.top && 
		p.y < getHeight() - borderArea.bottom
	  ) {
	    return new Point(
		    (p.x - borderArea.left) / charWidth,
		    (p.y - borderArea.top) / rowHeight
		    );
	} else {
	    return null;
	}
    }

    protected void processComponentEvent(ComponentEvent ce) {
	super.processComponentEvent(ce);
	if(
		ce.getID() == ComponentEvent.COMPONENT_RESIZED ||
		ce.getID() == ComponentEvent.COMPONENT_SHOWN
		) 
	{
	    resizeConsole();
	}
    }

    int xStart;
    int yStart;

    protected void processMouseEvent(MouseEvent me) {
	super.processMouseEvent(me);
	if(me.getID() == MouseEvent.MOUSE_PRESSED) {
	    Point p = getCharacterAt(me.getPoint());
	    if(p!=null) {
		model.startSelection(p);
		repaint();
	    }
	} else if(me.getID() == MouseEvent.MOUSE_RELEASED) {
	    Point p = getCharacterAt(me.getPoint());
	    if(p!=null) {
		model.endSelection(p);
		repaint();
	    }
	    if(model.hasSelection()) {
		String selection = model.getSelection();
		if(selection != null) {
		    getToolkit().getSystemClipboard().setContents(
			    new StringSelection(selection),
			    new ClipboardOwner() {
				public void lostOwnership(
				    Clipboard clip, 
				    Transferable trans
				    ) 
				{
				    model.cancelSelection();
				    repaint();
				}
			    }
			    );
		}
	    }
	} else if(me.getID() == MouseEvent.MOUSE_CLICKED) {
	    model.cancelSelection();
	}
    }

    protected void processMouseMotionEvent(MouseEvent me) {
	super.processMouseMotionEvent(me);
	if(me.getID() == MouseEvent.MOUSE_DRAGGED) {
	    Point p = getCharacterAt(me.getPoint());
	    if(p!=null) {
		model.endSelection(p);
		repaint();
	    }
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
