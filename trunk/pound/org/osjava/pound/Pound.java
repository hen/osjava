package org.osjava.pound;

import java.awt.*;
import java.awt.event.*;

public class Pound extends Frame implements KeyListener {

    public static void main(String[] args) {
        Pound p = new Pound();
        p.show();
    }

    public Pound() {
        super("Pound");
        this.addKeyListener(this);
    }


    // KeyListener
    public void keyPressed(KeyEvent ke) {
        // ignore
    }

    public void keyReleased(KeyEvent ke) {
        // ignore
    }

    public void keyTyped(KeyEvent ke) {
        int keyCode = ke.getKeyCode();

        if(keyCode == KeyEvent.VK_ESCAPE) {
            System.err.println("Quitting. ");
            this.dispose();
            System.exit(0);
        }

        System.err.println("Pressed: "+KeyEvent.getKeyText(keyCode));
    }

}
