package com.generationjava.awt;

import java.awt.Image;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.Toolkit;

/**
 * Provides a Canvas on wihch an Image is drawn. 
 * Originally this functionality was encased in a swing class 
 * called ImagePanel, however this version was taken 
 * from the David Geary AWT book.
 */
public class ImageCanvas extends Canvas implements ImageSource {

    private Image image;
    private ImageSource source;

    public ImageCanvas(String file) {
        setImage( load( file ) );
    }
    public ImageCanvas(Image image) {
        waitForImage(image);
        this.image = image;
    }
    
    public ImageCanvas(ImageSource source) {
        this.source = source;
    }

    public void paint(Graphics g) {
        g.drawImage( getImage(), 0, 0, this);
    }

    public void update(Graphics g) {
        paint(g);
    }

    public Dimension getPreferredSize() {
        return new Dimension( getImage().getWidth(this), getImage().getHeight(this) );
    }
    
    public Image load(String file) {
        Image img = Toolkit.getDefaultToolkit().getImage(file);
        waitForImage(img);
        return img;
    }
    
    private void waitForImage(Image img) {
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(img,0);
        try {
            mt.waitForID(0);
        } catch(Exception e) { 
            e.printStackTrace(); 
        }
    }
    
    public void setImage(Image image) {
        this.image = image;
        this.source = null;
    }
    
    public Image getImage() {
        if( source != null ) {
            return this.source.getImage();
        } else {
            return this.image;
        }
    }
    
    public void setImage(ImageSource source) {
        this.source = source;
        this.image = null;
    }

}