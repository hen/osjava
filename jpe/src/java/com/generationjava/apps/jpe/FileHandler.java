package com.generationjava.apps.jpe;

import gnu.regexp.RE;
import gnu.regexp.REException;

import java.awt.FileDialog;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
* File handler handles all the request from the file menu
* extends Handler object so it can be loaded in JPE object
*/
public class FileHandler extends AbstractHandler {

        // Strings for the pulldown
        private String OpenString=getText("JPE.menu.File.open");
        private String CloseString=getText("JPE.menu.File.close");
        private String LockString=getText("JPE.menu.File.lockToggle");
        private String SaveString=getText("JPE.menu.File.save");
        private String SaveAsString=getText("JPE.menu.File.saveAs");
        private String NewString=getText("JPE.menu.File.new");
        private String ImportString=getText("JPE.menu.File.import");
        private String ExitString=getText("JPE.menu.File.exit");
        private String TemplateString=getText("JPE.menu.File.template");
        private String RecentString=getText("JPE.menu.File.recent");
        private String RevertString=getText("JPE.menu.File.revert");

        private String dir;

        private Menu recentMenu;
        private int     recentMax;

        private Vector recentFiles;

        /**
        * create the file handler
        */
        public FileHandler(JPE jpe) {
                super(jpe);
                setName(getText("JPE.menu.File"));
                dir = getJPE().getProperty("setup","currentdir");
                if(dir == null) {
                        dir = "c:\\JPE";
                }
                String prop = getJPE().getProperty("setup","recentmax");
                try {
                        recentMax = Integer.parseInt(prop);
                } catch(NumberFormatException nfe) {
                        recentMax = 5;
                }
        }

        /**
        *  called by superclass to create its menu
        */
        public void createMenu(MenuBar bar) {
                Menu m = new Menu( getName() );
                m.add(new JPEMenuItem(NewString,new MenuShortcut('N')));
                m.add(new JPEMenuItem(OpenString,new MenuShortcut('O')));
                m.add(new JPEMenuItem(CloseString,new MenuShortcut('Q')));
                m.add(new JPEMenuItem(LockString,new MenuShortcut('L')));
                m.add(new JPEMenuItem(ImportString,new MenuShortcut('I')));
                m.add(new JPEMenuItem(TemplateString,new MenuShortcut('T')));
                m.add(new JPEMenuItem(SaveString,new MenuShortcut('S')));
                m.add(new JPEMenuItem(SaveAsString,new MenuShortcut('S',true)));
                m.add(new JPEMenuItem(RevertString));
                recentMenu = new Menu(RecentString);
                m.add(recentMenu);
                m.addSeparator();
                m.add(new JPEMenuItem(ExitString,new MenuShortcut('E')));
                recentMenu.addActionListener(this);
                m.addActionListener(this);        
                bar.add(m);

                Vector recents = getJPE().getProperties("recentfile");
                if(recents != null) {
                        Enumeration enum = recents.elements();
                        while(enum.hasMoreElements()) {
                                Hashtable node = (Hashtable)enum.nextElement();
                                addRecent((String)node.get("filename"));
                        }
                }
        }

        /**
        *  AWT callback (implemented in Handler) for menu events
        */
    public void actionPerformed(ActionEvent evt)        {
                String cmd = evt.getActionCommand();
                if (cmd.equals(ExitString)) {
                        doExit();
                } else if (cmd.equals(OpenString)) {
                        openFile();
                } else if (cmd.equals(TemplateString)) {
                        newFile(true);
                } else if (cmd.equals(CloseString)) {
                        closeFile();
                } else if (cmd.equals(LockString)) {
                        lockFile();
                } else if (cmd.equals(NewString)) {
                        newFile(false);
                } else if (cmd.equals(ImportString)) {
                        importFile();
                } else if (cmd.equals(SaveString)) {
                        saveFile();
                } else if (cmd.equals(SaveAsString)) {
                        saveAsFile();
                } else if (cmd.equals(RevertString)) {
                        revertFile();
                } else if (new File(cmd).exists()) {
                        // why delegate?
                        OpenFileHandler ofh=(OpenFileHandler)getJPE().getHandler("Opened");        
                        OpenFile file= new OpenFile(cmd);
                        ofh.addFile(file);
                        getJPE().getEditor().useFile(file);
                        removeRecent(cmd);
                }
        }

        /**
        * do find uses the askInterface
        */
        public void doExit() {
        this.handleExit("y");  // hack to stop problems with 'y' being ignored.
//                getJPE().setAskMode(this,"exit","Exit JPE (y/n) ? ",true);
        }

        /**
        * do Exit
        */
        public void handleExit(String result) {
                if (result.equals("y")) {
                        // check if all files are saved 
                        OpenFileHandler ofh=(OpenFileHandler)getJPE().getHandler("Opened");        
                        if (ofh!=null && ofh.allFilesSaved()) {
                                getJPE().requestExit();
                        } else {
                                //getJPE().say("Exit ignored because files not saved");
                                getJPE().setAskMode(this,"force-exit","Files not saved. Exit JPE (y/n)? ",true);
                        }
                } else {
                        getJPE().say("Exit ignored");
                }
        }

        public void forceExit(String result) {
                if (result.equals("y")) {
                        getJPE().requestExit();
                } else {
                        getJPE().say("Exit ignored");
                }        
        }

        /**
        * create new file, under the name untitled or a followup
        * untitled2, untitled3 etc etc
        */
        public void newFile(boolean template) {
                // get the openfile handler to add the new file
                OpenFileHandler ofh=(OpenFileHandler)getJPE().getHandler("Opened");        
                if (ofh==null) {
                        // no opened handler started so lets start one and add it the
                        // menu.
                        ofh=(OpenFileHandler)getJPE().addHandler(new OpenFileHandler(getJPE()));
                        ofh.createMenu(getJPE().getMenuBar());
                }


                // find a empty untitled filename
                String prefix="untitled";
                String filename=prefix;
                int i=2;
                // repeat until we found one        
                while (ofh.isLoaded(filename)) {
                        filename=prefix+(i++);        
                }

                // open a file with the found name
                OpenFile file=new OpenFile(filename);
                if(template) {
                        // open a dialog to let the user select the file we need to load        
                        String tempfilename = getFileName(FileDialog.LOAD);
                        OpenFile tempfile = new OpenFile(tempfilename);
                        String txt = tempfile.getText();
                        try {
                                RE re = new RE("<getJPE():date>");
                                txt = re.substitute(txt,""+new Date());
                                file.setText(txt);
                        } catch(REException ree) {
                                ree.printStackTrace();
                        }
                }

                // add the file to the opened file list
                ofh.addFile(file);

                // load this file into the text area
                getJPE().getEditor().useFile(file);
        }

        /**
        * open a file, lets the user pick one using a FileDialog
        */
        private void openFile()        {
                // get the openfile handler to add the new file
                OpenFileHandler ofh=(OpenFileHandler)getJPE().getHandler("Opened");        
                if (ofh==null) {
                        // no opened handler started so lets start one and add it the
                        // menu.
                        ofh=(OpenFileHandler)getJPE().addHandler(new OpenFileHandler(getJPE()));
                        ofh.createMenu(getJPE().getMenuBar());
                }

                // open a dialog to let the user select the file we need to load        
                String filename=getFileName(FileDialog.LOAD);

                // lets check if we allready have this file open
                OpenFile file=ofh.getOpenFile(filename);
                if (file==null) {
                        // file not opened at this moment lets open it                
                        file=new OpenFile(filename);
                        ofh.addFile(file);
                }
                
                // load this file into the text area
                getJPE().getEditor().useFile(file);
        }

        private void lockFile() {
                OpenFileHandler ofh=(OpenFileHandler)getJPE().getHandler("Opened");        
                Editor editor=(Editor)getJPE().getEditor();        
                if (editor!=null) {
                        OpenFile file=editor.getEditFile();
                        if (file.isLocked()) {
                                file.clearLocked();
                                getJPE().setLockState(false);
                                getJPE().say("file is now unlocked ( read / write )");
                        } else {
                                file.setLocked();
                                getJPE().setLockState(true);
                                getJPE().say("file is now locked (read only)");
                        }
                        ofh.updateProperties();
                }
        }
        /**
        * close the selected file, meaning removing it from the Opened
        * list and removing it from the auto reload on startup list
        */
        private void closeFile() {
                OpenFileHandler ofh=(OpenFileHandler)getJPE().getHandler("Opened");        
                Editor editor=(Editor)getJPE().getEditor();        
                if (editor!=null) {
                        OpenFile file=editor.getEditFile();
                        if (!file.isDirty()) {
                                ofh.removeFile(file);
                                addRecent(file.getName());
                                ofh.switchOpenFile();
                        } else {
                                getJPE().setAskMode(this,"close","File not saved, save first  (y/n) ? ",true);
                        }
                }
        }

        
        private void handleClose(String result) {
                // get the text handler 
                Editor editor=(Editor)getJPE().getEditor();        
                if (editor!=null) {
                        // get the selected file from the text handler
                        OpenFile file=editor.getEditFile();
                        if (file!=null) {
                                if (result.equals("y")) file.saveFile();
                                file.clearDirty();
                                closeFile();
                        }
                }
        }


        /**
        * ask the user for a filename (using a file requester) and save
        * the current file under that name
        */
        private void saveAsFile() {
                // get openfile handlers
                OpenFileHandler ofh=(OpenFileHandler)getJPE().getHandler("Opened");        
                if (ofh!=null) {
                        // ask the user for the file name
                        String filename=getFileName(FileDialog.SAVE);
                        // get the text handler
                        Editor editor=(Editor)getJPE().getEditor();        
                        // get the selected file                                
                        OpenFile file=editor.getEditFile();
                        // remove the file from the opened list
                        ofh.removeFile(file);
                        if (file!=null) {
                                // set the new name in the file
                                file.setName(filename);
                                getJPE().say("Saving file "+file.getName());
                                // save the file under this new name
                                file.saveFile();
                                // add the file to the opened files again  (now new name)
                                ofh.addFile(file);
                                getJPE().say("Saved file "+file.getName());
                        }
                }
        }


        /**
        * save selected file to disk
        */
        private void saveFile() {
                // get the text handler 
                Editor editor=(Editor)getJPE().getEditor();        
                if (editor!=null) {
                        // get the selected file from the text handler
                        OpenFile file=editor.getEditFile();
                        if (file!=null) {
                                // signal the use we are saving and save it to disk                
                                getJPE().say("Saving file "+file.getName());
                                file.saveFile();
                                getJPE().say("Saved file "+file.getName());
                        }
                }
        }

        /**
        * get the filename from the user using a file requester
        */
        private String getFileName(int mode) {
                String prompt;
                String filename;
                String pathname;
                File   file;

                // depending on load or save set the prompt                
                if (mode == FileDialog.LOAD) {
                        prompt = "Open File ";
                } else {
                        prompt = "Save File As ";
                }

                // create a file requester
                FileDialog d = new FileDialog(getJPE().getEditor(), prompt, mode);

                // set the file filter (not working on epoc it seems)
                d.setFilenameFilter(new JPEFilenameFilter());

                // set the directory to open in (not working?)
                //d.setDirectory("c:\\JPE");
                d.setDirectory(dir);

                // show requester (blocks until user is done)
                d.show();

                // optain the filename
                filename = d.getFile();

                // complete the file name by adding its path 
                pathname = d.getDirectory() + filename; 

                if (filename != null) {
                        dir = d.getDirectory();
                        getJPE().putProperty("setup","currentdir",dir);
                        // if we have a filename return it
                        return(pathname);
                } else {
                        // return null to signal a problem
                        return(null);
                }
        }
        /**
        * Handler the ask callback and finish the command started that
        * started with the setAskMode.
        */
        public void askCallback(String command,String result) {
                if (command.equals("exit")) {
                        handleExit(result);
                } else if (command.equals("force-exit")) {
                        forceExit(result);
                } else if (command.equals("revert")) {
                        handleRevert(result);
                } else if (command.equals("close")) {
                        handleClose(result);
                } 
        }

        public void importFile() {
                Editor editor=getJPE().getEditor();                

                String tempfilename = getFileName(FileDialog.LOAD);
                OpenFile tempfile = new OpenFile(tempfilename);
                String txt = tempfile.getText();

                if (txt != null) {
                        editor.insertText(txt);
                        getJPE().say("Text Import");
                }
        }


        public void revertFile() {
                getJPE().setAskMode(this,"revert","Revert file, all changes will be lost (y/n) ? ",true);
        }

        public void handleRevert(String answer) {
                if("y".equals(answer)) {
                        Editor editor=(Editor)getJPE().getEditor();        
                        if (editor!=null) {
                                // get the selected file from the text handler
                                OpenFile file=editor.getEditFile();
                                if (file!=null) {
                                        // revert the file
                                        file.loadFile();
                                        // tell editor to useFile, without saving
                                        editor.useFile(file,false);
                                }
                        }
                }
        }

        protected void addRecent(String file) {
                if(recentFiles == null) {
                        recentFiles = new Vector();
                }
                for(int i=0;i<recentFiles.size();i++) {
                        if(file.equals(recentFiles.elementAt(i))) {
                                return;
                        }
                }
                recentFiles.insertElementAt(file,0);
                recentMenu.insert(file,0);
                if(recentFiles.size() > recentMax) {
                        recentFiles.setSize(recentMax);
                }
                if(recentMenu.getItemCount() > recentMax) {
                        recentMenu.remove(recentMax);
                }
                updateProperties();
        }

        protected void removeRecent(String file) {
                if(recentFiles == null) {
                        return;
                } else {
                        recentFiles.removeElement(file);
                        for(int i=0;i<recentMenu.getItemCount();i++) {
                                if(recentMenu.getItem(i).getLabel().equals(file)) {
                                        recentMenu.remove(i);
                                }
                        }
                }
                updateProperties();
        }

        public void updateProperties() {
                Vector results=new Vector();
                Enumeration enum=recentFiles.elements();
                while (enum.hasMoreElements()) {
                        Hashtable onenode=new Hashtable();
                        onenode.put("nodename","recentfile");
                        String filename=(String)enum.nextElement();
                        onenode.put("filename",filename);
                        results.addElement(onenode);
                }
                //  store/replace the current vector with our new one
                getJPE().putProperties("recentfile",results);
        }

}
