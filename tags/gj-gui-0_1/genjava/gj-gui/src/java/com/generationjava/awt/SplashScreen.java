package com.generationjava.awt;

import java.awt.Image;
import java.awt.Frame;
import java.awt.Window;
import java.awt.Dimension;

/**
 * An AWT SplashScreen class.
 *
 * // TODO: Add a closing cross so people can stop the loading.
 * //       Add a progess bad.
 */
public class SplashScreen extends Window {

    // Where the image is drawn
    private ImageCanvas canvas = null;

    /**
     * Create a splash screen based on the given image.
     * An anonymous frame will be bound to the splash screen.
     *
     * @param img Image to be shown on the splash screen.
     */
    public SplashScreen(Image img) {
        this(new Frame("Dummy Frame For SplashScreen"),img);   
    }
    
    /**
     * Create a splash screen based on the given image.
     *
     * @param img Image to be shown on the splash screen.
     * @param f   Frame to bind this splash screen to.
     */
    public SplashScreen(Frame f, Image img) {
        super(f);
        canvas = new ImageCanvas(img);
        this.add(canvas, "Center");
        setSize( canvas.getPreferredSize() );
        WindowUtilities.centreWindowOnScreen(this);
    }

    /**
     * Overriden to return the preferred size of the image.
     *
     * @return Dimension measuring the preferred size.
     */
    public Dimension getPreferredSize() {
        return canvas.getPreferredSize();
    }

    /**
     * Make sure this splash screen is at the front when it's shown.
     */
    public void show() {
        super.show();
        this.toFront(); 
    }

}
