package com.generationjava.apps.jpe;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

/**
* OpenFileHandler handles all the request from the opened menu
* extends Handler object so it can be loaded in JPE object
*/
public class OpenFileHandler extends AbstractHandler {

        // hashtable with the opened files
        private Hashtable openfiles=new Hashtable();

        // pointer to its menu
        private Menu menu;

        // used to signify whether the ctrl-,/ctrl-. is in effect.
        private boolean existsBefore;
        private boolean existsAfter;

        /**
        * create the opened file handler
        */
        public OpenFileHandler(JPE jpe) {
                super(jpe);
                setName(getText("JPE.menu.Opened"));
        }

        /**
        * reopen all the files defined by the properties (config.xml)
        */
        public void openFiles() {
                Vector vec = getJPE().getProperties("openfile");
                if (vec!=null) {
                        boolean first = true;
                        String resetname = null;
                        Enumeration e=vec.elements();
                        while (e.hasMoreElements()) {
                                Hashtable onenode=(Hashtable)e.nextElement();
                                String filename=(String)onenode.get("filename");
                                if (filename.indexOf("JPE:")==0) {
                                        filename=getJPE().getMyPath()+"\\"+filename.substring(4);
                                }
                                OpenFile file=new OpenFile(filename);
                                String lockstate=(String)onenode.get("locked");
                                if (lockstate.equals("true")) {
                                        file.setLocked();
                                        getJPE().setLockState(true);
                                }
                                String pos = (String)onenode.get("cursorpos");
                                try {
                                        int num = Integer.parseInt(pos);
                                        file.setCaretPosition(num);
                                } catch(NumberFormatException nfe) {
                                        Log.log("Bad data in config for "+filename+"'s cursor position.");
                                }
                                addFile(file);
                                if(first) {
                                        resetname = filename;
                                        first = false;
                                }
                        }
                        reset(resetname);
                }
        }

        /**
        *  called by superclass to create its menu
        */
        public void createMenu(MenuBar bar) {
                menu = new Menu( getName() );
                menu.addActionListener(this);        
                bar.add(menu);
        }


        /**
        *  AWT callback (implemented in Handler) for menu events
        */
    public void actionPerformed(ActionEvent evt)        {
                String cmd = evt.getActionCommand();
                switchOpenFile(cmd);
        }

        /**
        * switch to the first opened file (called on close)
        * will open the readme if no files are left
        */
        public boolean switchOpenFile() {
                // get the 'first' in the hashtable
                Enumeration e=openfiles.keys();
                if (e.hasMoreElements()) {
                        // one found switch to it
                        String filename=(String)e.nextElement();
                        return(switchOpenFile(filename));
                } else {
                        // what no open files anymore ? lets open the default then
                        OpenFile file=new OpenFile(getJPE().getMyPath()+"\\readme.txt");
                        // since the file was closed open it first
                        addFile(file);
                        // use the readme files
                        Editor editor=getJPE().getEditor();
                        editor.useFile(file);
                        reset(getJPE().getMyPath()+"\\readme.txt");
                }        
                return(false);
        }

        /**
        * switch to a opened file, will return false if it was not
        * opened
        */
        public boolean switchOpenFile(String filename) {
                if (filename.indexOf("JPE:")==0) {
                        filename=getJPE().getMyPath()+"\\"+filename.substring(4);
                }

                // get the requested file from our list
                OpenFile file=(OpenFile)openfiles.get(filename);
                if (file!=null) {
                        // file found so make it active and return true
                        Editor editor=getJPE().getEditor();
                        editor.useFile(file);
                        reset(filename);
                        return(true);
                } else {
                        // file was not opened return false
                        return(false);
                }
        }

        private void reset(String filename) {
                int menusz = menu.getItemCount();
                existsBefore = false;
                existsAfter = false;
                for(int i=0;i<menusz;i++) {
                        menu.getItem(i).deleteShortcut();
                }
                for(int i=0;i<menusz;i++) {
                        if(filename.equals(menu.getItem(i).getLabel())) {
                                if(i != 0) {
                                        existsBefore = true;
                                        menu.getItem(i-1).setShortcut(new MenuShortcut(','));
                                }
                                if(i != menusz - 1) {
                                        existsAfter = true;
                                        menu.getItem(i+1).setShortcut(new MenuShortcut('.'));
                                }
                                break;
                        }
                }
        }

        /**
        * return the requested openfile
        */
        public OpenFile getOpenFile(String filename) {
                // get the requested file from our list
                OpenFile file=(OpenFile)openfiles.get(filename);
                // return it
                return(file);
        }


        /**
        * add a openfile to our opened list, will also result in it
        * being added to the opened menu
        */
        public void addFile(OpenFile file) {
                // put the given openfile in the list by its name
                openfiles.put(file.getName(),file);
                // add the file to the menu pulldown
                menu.add(file.getName());
                reset(file.getName());
                // update the properties (in config.xml)
                updateProperties();
        }

        /**
        * remove given file from the opened list, also results in removing
        * it from the opened menu.
        */
        public void removeFile(OpenFile file) {
                // remove openfile from the list
                openfiles.remove(file.getName());
                // empty the opened menu (bad way needs to be changed)
                menu.removeAll();
                // enum all openfiles to create a new menu list
                Enumeration e=openfiles.elements();
                while (e.hasMoreElements()) {
                        OpenFile nfile=(OpenFile)e.nextElement();
                        // add the file to the menu pulldown
                        menu.add( nfile.getName() );
                }
                // update the properties (in config.xml)
                updateProperties();
        }

        /**
        * is the given name loaded as allready ?
        */
        public boolean isLoaded(String name) {
                OpenFile file=(OpenFile)openfiles.get(name);
                if (file!=null) return(true);
                return(false);
        }

        /**
        * create new file propertie vector and store the new list
        * into the propertie system (that auto saves it to config.xml).
        * so the config.xml is uptodate again
        */
        public void updateProperties() {
                // setup the n new vector
                Vector results=new Vector();
                // enum all the openfiles
                Enumeration e=openfiles.keys();
                while (e.hasMoreElements()) {
                        // create a new node for a file 
                        Hashtable onenode=new Hashtable();
                        // store the type
                        onenode.put("nodename","openfile");
                        // get file name
                        String filename=(String)e.nextElement();
                        // store filename
                        onenode.put("filename",filename);
                        OpenFile file=(OpenFile)openfiles.get(filename);
                        onenode.put("locked",""+file.isLocked());
                        onenode.put("cursorpos",""+file.getCaretPosition());
                        // add the config node to the vector
                        results.addElement(onenode);
                }
                //  store/replace the current vector with our new one
                getJPE().putProperties("openfile",results);
        }

        /**
        * all files saved ?
        */
        public boolean allFilesSaved() {
                Enumeration e=openfiles.elements();
                while (e.hasMoreElements()) {
                        OpenFile file=(OpenFile)e.nextElement();
                        if (file.isDirty()) {
                                return(false);
                        }
                }
                return(true);
        }

        public boolean endOfList(char c) {
                if(c == ',') {
                        return !existsBefore;
                } else
                if(c == '.') {
                        return !existsAfter;
                } else {
                        return false;
                }
        }

}
