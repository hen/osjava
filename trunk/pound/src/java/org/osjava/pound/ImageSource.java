package org.osjava.pound;

import java.util.*;
import java.io.*;
import java.awt.Dimension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Image;

public class ImageSource implements Runnable {
    private String imagePath;
    private File []files;
    private int cacheSize = 1;
    private int index;
    private boolean started;
    private List images = new ArrayList();
    private Thread thread;
    private Dimension canvasSize;


    public void load(String imagePath, int cacheSize) {
        this.images = new ArrayList();
        this.cacheSize = cacheSize;
        this.imagePath = imagePath;
        File path = new File(imagePath);
        this.files = path.listFiles(new FileFilter() {
              public boolean accept(File file) {
                  String name = file.toString().toLowerCase();
                  if (name.endsWith("jpg")
                          || name.endsWith("jpeg")
                          || name.endsWith("png")
                          || name.endsWith("pcd")
                          || name.endsWith("gif")) {
                      return true;    
                  }

                  return false;
              }
          });

        index = 0;  
 
        if (!started) {
            thread = new Thread(this);
            thread.setName("lazy image loader");
            thread.start();
            started = false;
        }  
    }

    private synchronized void reset() {
        notifyAll();
    }

    private synchronized void set() {
        notifyAll();
    }

    public synchronized Image nextImage() {
        log("next image requested");

        if (images.size() == 0) {
            try {
                wait();
            }
            catch (InterruptedException e) {}
        }  

        Image i = (Image) images.get(0);
        images.remove(0);
        thread.interrupt();

        return i;
    }    

    private void log(Object o) {
        System.out.println(o);
    }    

    private Image scale(BufferedImage currentImage) {
      if (canvasSize == null) {
        return currentImage;
      }

      Dimension size = canvasSize;
      int iw = currentImage.getWidth(null);
      int ih = currentImage.getHeight(null);

      if (iw <= size.width && ih <= size.height) {
        log("no scale");
        return currentImage;
      }


      float ir = (float) iw / (float) ih;
      float cr = (float) size.width / (float) size.height;

      Image scaled = null;
      float y = 0;
      float x = 0;

      log("original image size: " + new Dimension(iw, ih));
      long start = System.currentTimeMillis();

      if (ir > cr) {
        log("width bias scaling");
        scaled = currentImage.getScaledInstance(size.width, -1, Image.SCALE_FAST);
        y = size.height / 2 - scaled.getHeight(null) / 2;
      }
      else {
        log("height bias scaling");
        scaled = currentImage.getScaledInstance(-1, size.height, Image.SCALE_FAST);
        x = size.width / 2 - scaled.getWidth(null) / 2;
      }

      long stop = System.currentTimeMillis();
      log("scale took: " + (stop - start));
      
      log("scaled image size: " + new Dimension(scaled.getWidth(null), scaled.getHeight(null)));

      return scaled;
    }

    public void setCanvasSize(Dimension canvasSize) {
      this.canvasSize = canvasSize;
      log("canvas size set: " + canvasSize);
    }

    public Dimension getCanvasSize() {
      return this.canvasSize;
    }  

    public void run() {
        log("thread started");
        int i;
        while (true) {
            if (index >= files.length) {
                index = 0;
            }


            for (i=index; images.size() < cacheSize && i < files.length; i++, index++) {
                try {
                    log("loading: " + files[i]);
                    Image img = ImageIO.read(files[i]);
                    img = scale((BufferedImage) img);
                    images.add(img);
                    reset();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }    
            }
                log("i:" + i + ",index:" + index + ",images.size:" + images.size() + 
                ",cacheSize:" + cacheSize + ",files.length:" + files.length);
            try {
                log("sleeping");
                Thread.sleep(Long.MAX_VALUE);
                log("woke up");
            }
            catch (InterruptedException e) {
                log("interrupted");
            }    
        }
    }
}    
