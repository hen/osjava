package com.generationjava.awt;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.MediaTracker;
import java.io.File;
import java.net.URL;
import java.awt.image.ImageProducer;

/**
 * This provides a standard way of loading an image in as a resource.
 *
 * // TODO: Provide a way of informing the person that is loaded.
 */
public class ImageLoaderFacade extends java.awt.Panel {
    
    static private ImageLoaderFacade myObj = null;  // singleton variable

    /**
     * Singleton class therefore no access to this method
     */
    private ImageLoaderFacade() { }

    /**
     * Get the singleton object.
     *
     * @return ImageLoaderFacade singleton object.
     */
    static public ImageLoaderFacade getInstance() {
        if(myObj == null) myObj = new ImageLoaderFacade();
        return myObj;
    }

    /**
     * Get an image.
     *
     * @param imgstr String resource name of the image.
     *               Delimited by '.'s.
     *
     * @return Image desired.
     */
    public Image getImage(String imgstr) {
        Toolkit tk = Toolkit.getDefaultToolkit();

        //URL url = getClass().getResource(imgstr);
        MediaTracker mt = new MediaTracker(this);
        Image img = tk.getImage(imgstr);  // or use url
 
        mt.addImage(img,0);

        try {
            mt.waitForAll();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        
        return img;

        //return tk.createImage(imgstr);
    }

    /**
     * Get an image.
     *
     * @param file   File containing the image.
     *
     * @return Image desired.
     */
    public Image getImage(File file) {
        return getImage(file.getAbsolutePath());
    }

    /**
     * Get an image.
     *
     * @param ip     ImageProducer that will provide the image.
     *
     * @return Image desired.
     */
    public Image getImage(ImageProducer ip) {
        Toolkit tk = Toolkit.getDefaultToolkit();

        MediaTracker mt = new MediaTracker(this);
        Image img = tk.createImage(ip);
 
        mt.addImage(img,0);

        try {
            mt.waitForAll();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        
        return img;        
    }


    /**
     * Get an image.
     *
     * @param imgstr  String file name, using File.separator.
     * @param ext     String ext extension, for example 'gif'
     *
     * @return Image desired.
     */
    public Image getImage(String imgstr, String ext) {
        imgstr = imgstr.replace(".".charAt(0),File.separator.charAt(0));
        return getImage(imgstr+"."+ext);        
    }

    static public Image loadImage(String name) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();

        if(loader != null) {
            URL url = loader.getResource(name);
            if(url == null) {
                url = loader.getResource("/"+name);
            }
            if(url != null) {
                Toolkit tk = Toolkit.getDefaultToolkit();
                Image img = tk.getImage(url);
                return img;
            }
        }

        return null;
    }

}
