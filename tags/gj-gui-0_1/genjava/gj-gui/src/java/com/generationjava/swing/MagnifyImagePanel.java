package com.generationjava.swing;

import java.awt.Image;
import java.awt.Graphics;
import javax.swing.JPanel;
import com.generationjava.awt.ImageLoaderFacade;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class MagnifyImagePanel extends JPanel {

    private Image     myImage       = null;
    private int       imageWidth    = 0;
    private int       imageHeight   = 0;
    private double    magnification = 1.0;
    private Image     croppedImage  = null;
    private Rectangle viewport      = null;

    public MagnifyImagePanel() {
    }

    public void paint(Graphics g) {
        super.paint(g);
        if(getImage() == null) {
            return;
        } else 
        if(magnification < 1.0) {
            int w = (int)(this.getWidth()*magnification);
            int h = (int)(this.getHeight()*magnification);
            int x = (int)((this.getWidth() - w)/2);
            int y = (int)((this.getHeight() - h)/2);
            g.drawImage(getImage(), x, y, w,h ,this);
        } else {
            g.drawImage(getImage(), 0, 0, this.getWidth(), this.getHeight(),this);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension( myImage.getWidth(this), myImage.getHeight(this) );
    }

    public void setImage(Image img) {
        myImage = img;
        croppedImage = null;
        imageWidth  = img.getWidth(this);
        imageHeight = img.getHeight(this);
        Dimension size = new Dimension( imageWidth, imageHeight );
        this.setSize( size );
        viewport = new Rectangle( size );
        invalidate();
        repaint();
    }

    public Image getImage() {
        if( (croppedImage != null) && (magnification > 1.0) ) {
            return croppedImage;
        } else {
            return myImage;
        }
    }

    public void magnify(double x, Point p) {
        if(myImage == null) { return; }
        if(x < 1.0) { // exempt for special-centering handling.
            magnification = x;
            viewport = new Rectangle( imageWidth, imageHeight );
        } else
        {
            Image img = null;
            if(x < magnification) {
                img = myImage;   // need to work on original to go backwards.
                viewport.x = (int)( viewport.x - (imageWidth/x - imageWidth/magnification)/2);
                viewport.y = (int)( viewport.y - (imageHeight/x - imageHeight/magnification)/2);
                viewport.width = (int)(imageWidth/x);
                viewport.height = (int)(imageHeight/x);
            } else {
                img = myImage;
                Point clicked = new Point();
                clicked.x = (int)(viewport.x + p.x*viewport.width/this.getWidth());
                clicked.y = (int)(viewport.y + p.y*viewport.height/this.getHeight());
                viewport.width = (int)(imageWidth/x);
                viewport.height = (int)(imageHeight/x);
                viewport.x = (int)(clicked.x - viewport.width/2);
                viewport.y = (int)(clicked.y - viewport.height/2);
            }
            magnification = x;

            // Validate viewport. Use Math.max ?
            if(viewport.x < 0) viewport.x = 0;
            if(viewport.y < 0) viewport.y = 0;
            if(viewport.x > this.getWidth() - viewport.width) {
                viewport.x = this.getWidth() - viewport.width;
            }
            if(viewport.y > this.getHeight() - viewport.height) {
                viewport.y = this.getHeight() - viewport.height;
            }

            // crop image
            int[] pixels = new int[viewport.width*viewport.height];
            PixelGrabber pg = new PixelGrabber(img, viewport.x, viewport.y,
                                               viewport.width, viewport.height,
                                                   pixels, 0, viewport.width);
            try {   // decide where to propagate this exception to
                pg.grabPixels();
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }

            ImageProducer ip = new MemoryImageSource(viewport.width,viewport.height,
                                                 pixels,0,viewport.width);
            croppedImage = ImageLoaderFacade.getInstance().getImage(ip);

            // clear up resources
            ip = null;
            pixels = null;
        }
        invalidate();
        repaint();        
    }

}

