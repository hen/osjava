package com.generationjava.awt;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import com.generationjava.awt.ImageLoaderFacade;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;

public class Zoomer implements ImageSource, InformationListener {

    private ImageSource source;
    private double magnification;
    private Image croppedImage;

    private Rectangle viewport;

    public Zoomer(ImageSource source) {
        this.source = source;
        this.magnification = 1;
    }

    public Image getImage() {
        if (croppedImage == null) {
            croppedImage = this.source.getImage();
            viewport = new Rectangle(croppedImage.getWidth(null),
                    croppedImage.getHeight(null));
        }
        return croppedImage;
    }

    public void report(ReportEvent re) {
        if ("zoom-in".equals(re.getName())) {
            magnify(magnification / 2);
        } else if ("zoom-out".equals(re.getName())) {
            magnify(magnification * 2);
        }
    }

    public Object request(RequestEvent re) {
        return null;
    }

    public void magnify() {
        magnify(magnification);
    }

    public void magnify(double x) {
        Point pt = new Point();
        pt.x = getImage().getWidth(null) / 2;
        pt.y = getImage().getHeight(null) / 2;
        magnify(x, pt);
    }

    public void magnify(double x, Point p) {
        Image img = null;
        if (x > 1.0) {
            img = source.getImage();
            viewport.x = (int)(viewport.x);
            viewport.y = (int)(viewport.y);
            viewport.width = (int)(getImage().getWidth(null));
            viewport.height = (int)(getImage().getHeight(null));
        } else {
            img = source.getImage();
                    img.getHeight(null));
            viewport.width = (int)(img.getWidth(null) * x);
            viewport.height = (int)(img.getHeight(null) * x);
            viewport.x = (int)(p.x - viewport.width / x);
            viewport.y = (int)(p.y - viewport.height / x);
        }
        magnification = x;

        // crop image
        int[] pixels = new int[viewport.width * viewport.height];
        PixelGrabber pg = new PixelGrabber(img, viewport.x, viewport.y,
                viewport.width, viewport.height, pixels, 0, viewport.width);
        try {
            pg.grabPixels();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        ImageProducer ip =
                new MemoryImageSource(viewport.width, viewport.height,
                pixels, 0, viewport.width);
        croppedImage = ImageLoaderFacade.getInstance().getImage(ip);
    }

}
