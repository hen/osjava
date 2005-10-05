package com.generationjava.swing;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

    private Image     myImage       = null;
    private int       imageWidth    = 0;
    private int       imageHeight   = 0;

    public ImagePanel() {
    }

    public void paint(Graphics g) {
        super.paint(g);
        if(getImage() == null) {
            return;
        } else {
            g.drawImage(getImage(), 0, 0, this.getWidth(), this.getHeight(),this);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension( myImage.getWidth(this), myImage.getHeight(this) );
    }

    public void setImage(Image img) {
        myImage = img;
        imageWidth  = img.getWidth(this);
        imageHeight = img.getHeight(this);
        Dimension size = new Dimension( imageWidth, imageHeight );
        this.setSize( size );
        invalidate();
        repaint();
    }

    public Image getImage() {
        return myImage;
    }


}

