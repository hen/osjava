package com.generationjava.apps.jpe;

import java.awt.MenuItem;
import java.awt.MenuShortcut;

public class JPEMenuItem extends MenuItem {

    // this is to get around buggy JVMs that don't default to 
    // the label when a shortcut key is pressed.
    public JPEMenuItem(String label, MenuShortcut sc) {
        super(label,sc);
        setActionCommand(label);
    }
    
    public JPEMenuItem(String label) {
        super(label);
    }

}
