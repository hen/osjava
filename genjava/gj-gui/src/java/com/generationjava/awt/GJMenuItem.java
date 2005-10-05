package com.generationjava.awt;

import java.awt.MenuItem;
import java.awt.MenuShortcut;

// exists to get around a bug in MenuItem.
public class GJMenuItem extends MenuItem {

    public GJMenuItem(String label, MenuShortcut sc) {
        super(label,sc);
        setActionCommand(label);
    }
    
    public GJMenuItem(String label) {
        super(label);
    }
    
    public GJMenuItem(String label, String action) {
        super(label);
        setActionCommand(action);
    }
    public GJMenuItem(String label, MenuShortcut sc, String action) {
        super(label,sc);
        setActionCommand(action);
    }

}
