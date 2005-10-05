package com.generationjava.swing;

import java.awt.Image;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;


public class MultiImagePanel extends JPanel {

    // should we use BorderLayout consts here?
    static public final String EAST  = "EAST";
    static public final String NORTH = "NORTH";
    static public final String WEST  = "WEST";
    static public final String SOUTH = "SOUTH";

    private ArrayList myImages    = new ArrayList();
    private ArrayList myPositions = new ArrayList();

    public MultiImagePanel() {
    }

    public void paint(Graphics g) {
        super.paint(g);
        List imgs = getImages();
        if(imgs == null) return;
        int n = numberOfImages();
        for(int i=0;i<n;i++) {
            g.drawImage((Image)imgs.get(i), (int)((this.getWidth()/n)*i), 0, (int)(this.getWidth()/n), this.getHeight(),this);
        }
    }

    public void setFirstImage(Image img) {
        myImages.add(0,img);
        myPositions.add("First");
    }

    public void addImage(Image img, String position) {
        if(myImages.isEmpty()) {
            // they're not reading instructions.
            // Barf with a RuntimeException
        }
        myImages.add(img);
        myPositions.add(position);
    }

    public List getImages() {
        return myImages;
    }

    public int numberOfImages() {
        return myImages.size();
    }
}

