package code316.core;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

public class WindowUtil {
    public static Point getCenteredLocation(Window window) {
		if (window == null) {
            throw new IllegalArgumentException("invalid value for window: " + window);
        }
        
        return getScreenCenteredLocation(window.getSize());    	
    }
    
    public static Point getScreenCenteredLocation(Dimension d) {
    	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		int x = (int) ((screen.getWidth() - d.getWidth()) / 2);
    	int y = (int) ((screen.getHeight() - d.getHeight()) / 2);
    
		return new Point(x, y);    	
    }
    public static void centerWindow(Window win) {
    	if ( win == null ) {
    		return;
    	}
    	
    	win.setLocation(getCenteredLocation(win));
    }
}
