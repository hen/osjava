package com.generationjava.apps.jpe;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
* base class for all the handlers JPE uses to allow for loadable parts
* still needs alot of work
*/
public class AbstractHandler implements ActionListener, Handler, AskInterface {
        private JPE jpe;
        private String name;

        /**
        * construct the handler
        */
        public AbstractHandler(JPE jpe) {
                this("Unknown",jpe);
        }

        public AbstractHandler(String name, JPE jpe) {
                setJPE(jpe);
                setName(name);
        }

        public JPE getJPE() {
                return jpe;
        }

        public void setJPE(JPE jpe) {
                this.jpe = jpe;
        }

    public String getText(String key) {
        return TextController.getInstance().getText(key);
    }

    public String getText(String key, Object value) {
        return TextController.getInstance().getText(key,value);    
    }

    public String getText(String key, Object value1, Object value2) {
        return TextController.getInstance().getText(key,value1,value2);
    }

    public String getText(String key, Object[] vals) {
        return TextController.getInstance().getText(key,vals);
    }
        /**
        * create menu, should be overridden by its subclasses
        */
        public void createMenu(MenuBar bar) {
                Menu m = new Menu(getName());
                m.add(getName());
                m.addActionListener(this);        
                bar.add(m);
        }

        /**
        * return the name of the handler
        */
        public String getName() {
                return name;
        }

        public void setName(String s) {
                name = s;
        }

        /**
        * awt callback handler should be overriden by its subclasses
        */
    public void actionPerformed(ActionEvent evt)        {
                String cmd = evt.getActionCommand();
                System.out.println(getName()+"="+cmd);
        }

        /**
        *
        */
        public void askCallback(String command,String result) {
                System.out.println("AskCall back used with cmd='"+command+"' and result='"+result+"' should be overridden by subclass");
        }
}
