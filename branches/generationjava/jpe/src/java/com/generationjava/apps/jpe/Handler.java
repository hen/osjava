package com.generationjava.apps.jpe;

import java.awt.Menu;
import java.awt.MenuBar;

/**
* handler interface used to handlers against to allow for dynamic
* loading of jpe parts in the future
*/
public interface Handler {
        public void createMenu(MenuBar bar);
        public String getName();
}
