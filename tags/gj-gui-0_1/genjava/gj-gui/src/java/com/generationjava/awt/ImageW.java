package com.generationjava.awt;

import java.awt.Image;

public class ImageW {

    private String name;
    private Image image;

    public ImageW(String name, Image image) {
        this.name = name;
        this.image = image;
    }
    
    public String toString() {
        return this.name;
    }

    public Image getImage() {
        return this.image;
    }
    
    public void close() {
        this.image.flush();
    }

}