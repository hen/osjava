package com.generationjava.awt;

import java.awt.Color;

public class ColorW {

    static public Color getColor(String color) {
        if(color.charAt(0) == '#') {
            color = color.substring(1);
        }
        if(color.length() != 6) {
            return null;
        }
        try {
            int r = Integer.parseInt(color.substring(0,2), 16);
            int g = Integer.parseInt(color.substring(2,4), 16);
            int b = Integer.parseInt(color.substring(4), 16);
            return new Color(r, g, b);
        } catch(NumberFormatException nfe) {
                return null;
        }
    }

    static public void main(String[] str) {
        System.err.println( ColorW.getColor(str[0]) );
    }
    
}
