package com.generationjava.awt;

import java.awt.Window;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * A set of useful routines for manipulating Window objects.
 */
public class WindowUtilities {

    static public final int TOP     = 0;
    static public final int CENTRE  = 1;
    static public final int BOTTOM  = 2;
    static public final int LEFT    = 3;
    static public final int RIGHT   = 4;

    /**
     * Centre the given window on the screen,
     *
     * @param w Window to centre.
     */
    static public void centreWindowOnScreen(Window w) {
        positionWindowOnScreen(w,CENTRE,CENTRE);
    }

    /**
     * Position the given window on the screen at the given location,
     *
     * @param w Window to centre.
     * @param x int x co-ordinate.
     * @param y int y co-ordinate.
     */
    static public void positionWindowOnScreen(Window w, int x, int y) {
        Toolkit tk = w.getToolkit();
              Dimension scrn = tk.getScreenSize();
              Dimension win  = w.getSize();
              int nx = 0,ny = 0;
        switch(x) {
          case LEFT:   nx = 0; break;
                case CENTRE: nx = (int)((scrn.width-win.width)/2); break;
                case RIGHT:  nx = (int)(scrn.width-win.width); break;
              }
              switch(y) {
          case TOP:    ny = 0; break;
                case CENTRE: ny = (int)((scrn.height-win.height)/2); break;
                case BOTTOM: ny = (int)(scrn.height-win.height); break;
        }
              w.setLocation(nx,ny);
    }

    /**
     * Resizes the given window to the given ratio of the screen size,
     *
     * @param w Window to centre.
     * @param ratio double value to change the window size to.
     */
    static public void sizeWindowOnScreen(Window w, double ratio) {
        Toolkit tk = w.getToolkit();
        Dimension scrn = tk.getScreenSize();
        w.setSize( new Dimension( (int)(scrn.width*ratio), (int)(scrn.height*ratio) ) );
    }

    static public void maxWindowOnScreen(Window w) {
        sizeWindowOnScreen(w, 1);
    }

}
