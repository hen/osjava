package org.osjava.pound;

import java.awt.*;
import java.awt.event.*;

// plan: use javax.sound to play musical notes on key input
//       have screen change based on keys hit; velocity of keys hit; show letter on screen
//       show animal matching the particular letter and play animal noise
public class Pound extends Frame implements KeyListener {

    public static void main(String[] args) {
        Pound p = new Pound();
        p.show();
    }

    private SimpleCanvas canvas;

    public Pound() {
        super("Pound");
        this.addKeyListener(this);
        canvas = new SimpleCanvas(Color.BLACK);
        this.add(canvas);
        this.pack();
    }


    // KeyListener
    public void keyTyped(KeyEvent ke) {
        // ignore
        ke.consume();
    }

    public void keyReleased(KeyEvent ke) {
        // ignore
        ke.consume();
    }

    public void keyPressed(KeyEvent ke) {
        int keyCode = ke.getKeyCode();

        if(keyCode == KeyEvent.VK_ESCAPE && ke.isShiftDown()) {
            System.err.println("Quitting. ");
            ke.consume();
            this.dispose();
            System.exit(0);
        }

        Color color = this.canvas.getBackground();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        if(r != 255) r++;
        if(g != 255) g++;
        if(b != 255) b++;
        this.canvas.setBackground(new Color(r,g,b));

        System.err.println("Pressed: "+KeyEvent.getKeyText(keyCode)+" "+keyCode+" "+ke.getModifiers());
        ke.consume();
    }

}

class SimpleCanvas extends Canvas {

    private Color color;

    public SimpleCanvas(Color color) { 
        this.color = color;
    }

    public void setBackground(Color color) {
        this.color = color;
        this.invalidate();
        this.repaint();
    }

    public Color getBackground() {
        return this.color;
    }

}
