package com.generationjava.awt;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Window;

/**
 * A utilitiy class to provide an application wide response
 * to window actions.
 */
public class BasicWindowMonitor extends WindowAdapter {

    private boolean exit = true;

    public BasicWindowMonitor() {
    }

    public BasicWindowMonitor(boolean exit) {
        this.exit = exit;
    }

    /**
     * Closes the window down.
     *
     * @param e WindowEvent concerned with the closing window.
     */
    public void windowClosing(WindowEvent e) {
        Window w = e.getWindow();
        w.setVisible(false);
        w.dispose();
        if(this.exit) {
            System.exit(0);
        }
    }
  
}
