package com.generationjava.swing;

import java.awt.Color;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class BackgroundTheme extends DefaultMetalTheme {
    
    private final ColorUIResource resource;

    public BackgroundTheme(Color color) {
        resource = new ColorUIResource(color);
    }

    public ColorUIResource getSecondary3() {
        return resource;
    }
}
