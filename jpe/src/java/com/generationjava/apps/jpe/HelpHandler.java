package com.generationjava.apps.jpe;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;

/**
 * Handles the Help information.
*/
public class HelpHandler extends AbstractHandler {

        // Strings for the pulldown
        private String HelpString=getText("JPE.menu.Help");
        private String AboutString=getText("JPE.menu.Help.about");

        /**
        * create the extra handler
        */
        public HelpHandler(JPE jpe) {
                super(jpe);
                setName(HelpString);
        }


        /**
        *  called by superclass to create its menu
        */
        public void createMenu(MenuBar bar) {
                Menu m = new Menu(getName());
                m.add(new JPEMenuItem(AboutString,new MenuShortcut('A',true)));

                m.addActionListener(this);

                bar.setHelpMenu(m);
        }

        /**
        *  AWT callback (implemented in Handler) for menu events
        */
    public void actionPerformed(ActionEvent evt)        {
                String cmd = evt.getActionCommand();
                if (cmd.equals(AboutString)) {
                        doAbout();
            }
        }

    public void doAbout() {
        getJPE().say(getText("JPE.version"));
    }

        /**
        * Handler the ask callback and finish the command started that
        * started with the setAskMode.
        */
//        public void askCallback(String command,String result) {
//        }


}
