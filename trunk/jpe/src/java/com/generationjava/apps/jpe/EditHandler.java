package com.generationjava.apps.jpe;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
* File handler handles all the request from the file menu
* extends Handler object so it can be loaded in JPE object
*/
public class EditHandler extends AbstractHandler implements ClipboardOwner {


        // Strings for the pulldown
        String CutString=getText("JPE.menu.Edit.cut");
        String CopyString=getText("JPE.menu.Edit.copy");
        String PasteString=getText("JPE.menu.Edit.paste");
        String SelectAllString=getText("JPE.menu.Edit.selectAll");
        String FindString=getText("JPE.menu.Edit.find.find");
        String SubstituteString=getText("JPE.menu.Edit.find.substitute");
        String FindAgainString=getText("JPE.menu.Edit.find.findAgain");
        String GotoString=getText("JPE.menu.Edit.goto.gotoLine");
        String GotoErrorString=getText("JPE.menu.Edit.goto.gotoError");

        // copy buffer for now is kept in a simple string
        // having to handle this way as System clipboard is buggy.
        String copyBuffer;
        String searchkey="";

        // handles the multi-question substitution functionality
        private String sub_pattern;
        private String sub_replace;

        /**
        * create the edit handler
        */
        public EditHandler(JPE jpe) {
                super(jpe);
                setName(getText("JPE.menu.Edit"));
        }


        /**
        *  called by superclass to create its menu
        */
        public void createMenu(MenuBar bar) {
                Menu m = new Menu(getName());
                m.add(new JPEMenuItem(CutString,new MenuShortcut('X')));
                m.add(new JPEMenuItem(CopyString,new MenuShortcut('C')));
                m.add(new JPEMenuItem(PasteString,new MenuShortcut('V')));

                Menu m3=new Menu(getText("JPE.menu.Edit.find"));
                m3.add(new JPEMenuItem(FindString,new MenuShortcut('F')));
                m3.add(new JPEMenuItem(SubstituteString,new MenuShortcut('/')));
                m3.add(new JPEMenuItem(FindAgainString,new MenuShortcut('F',true)));
                m3.addActionListener(this);
                m.add(m3);                

                Menu m2=new Menu(getText("JPE.menu.Edit.goto"));
                m2.add(new JPEMenuItem(GotoString,new MenuShortcut('G',true)));
                m2.add(new JPEMenuItem(GotoErrorString,new MenuShortcut('G')));
                m2.addActionListener(this);
                m.add(m2);                
                        
                m.addSeparator();
                m.add(new JPEMenuItem(SelectAllString, new MenuShortcut('A')));
                m.addActionListener(this);        
                bar.add(m);
        }


        /**
        *  AWT callback (implemented in Handler) for menu events
        */
    public void actionPerformed(ActionEvent evt)        {
                String cmd = evt.getActionCommand();
                if (cmd.equals(CopyString)) {
                        doCopy();
                } else if (cmd.equals(CutString)) {
                        doCut();
                } else if (cmd.equals(PasteString)) {
                        doPaste();
                } else if (cmd.equals(SelectAllString)) {
                        doSelectAll();
                } else if (cmd.equals(FindString)) {
                        doFind();
                } else if (cmd.equals(SubstituteString)) {
                        doSubstitute();
                } else if (cmd.equals(FindAgainString)) {
                        doFindAgain();
                } else if (cmd.equals(GotoString)) {
                        doGoto();
                } else if (cmd.equals(GotoErrorString)) {
                        doGotoError();
                }
        }


        /**
        * do find uses the askInterface
        */
        public void doFind() {
                getJPE().setAskMode(this,"find","Find = ");
        }


        /**
        * do substitute uses the askInterface
        * then asks another question
        */
        public void doSubstitute() {
                getJPE().setAskMode(this,"replaceQuestion","Pattern = ");
                getJPE().setAskMode(this,"flagQuestion","Replace = ");
                getJPE().setAskMode(this,"substitute","Flag(g:global) = ");
        }


        /**
        * handles replace question by using the askInterface
        * then asks another question
        */
        public void handleReplaceQuestion(String pattern) {
                if("".equals(pattern)) {
                        sub_pattern = searchkey;
                } else {
                        sub_pattern = pattern;
                }
        }


        /**
        * handles flag question by using the askInterface
        * then asks another question
        */
        public void handleFlagQuestion(String replace) {
                sub_replace = replace;
        }

        /**
        * handle substitute
        */
        public void handleSubstitute(String flag) {
                getJPE().say("Substituting '"+sub_pattern+"' with '"+sub_replace+"'");
                Editor editor=getJPE().getEditor();
                if (editor != null) {
                        if (editor.substitute(sub_pattern,sub_replace,flag)) {
                                getJPE().say("Regexp Subst = '"+sub_pattern+"' with '"+sub_replace+"'");
                        } else {
                                getJPE().say("'"+sub_pattern+"' not Regexp Subst");
                        }
                }
        }

        /**
        * handle find
        */
        public void handleFind(String key) {
                if(!"".equals(key)) {
                        this.searchkey=key;
                } else {
                        key = this.searchkey;
                }
                getJPE().say("Searching for = '"+key+"'");
                Editor editor=getJPE().getEditor();
                if (editor != null) {
                        if (key.startsWith("/")) {
                                key = key.substring(1);
                                if (editor.gotoLineWithRegexp(key)) {
                                        getJPE().say("Regexp Found = '"+key+"'");
                                } else {
                                        getJPE().say("'"+key+"' not Regexp Found");
                                }
                        } else {
                                if (editor.gotoLineWith(key)) {
                                        getJPE().say("Found = '"+key+"'");
                                } else {
                                        getJPE().say("'"+key+"' not Found");
                                }
                        }
                }        
        }

        /**
        * do find again
        */
        public void doFindAgain() {
                getJPE().say("Searching for = '"+searchkey+"'");
                Editor editor=getJPE().getEditor();
                if (editor!=null) {
                        if (editor.gotoLineWith(searchkey,editor.getCursorPos()+1)) {
                                        getJPE().say("Found = '"+searchkey+"'");
                                } else {
                                        getJPE().say("no more '"+searchkey+"' not Found");
                        }
                }        
        }

        /**
        * goto the first error
        */
        public void doGotoError() {
                getJPE().say("Jumping to first Error");
                Editor editor=getJPE().getEditor();
                if (editor!=null) {
                        OpenFile file=editor.getEditFile();
                        int linenumber=file.getErrorPos();
                        if (linenumber!=-1) {
                                editor.gotoLine(linenumber);
                                getJPE().say("Jumped first error at line "+linenumber+" (ctrl-h to toggle log)");
                                return;
                        }
                }
                getJPE().say("No compile errors detected");
        }


        /**
        * do goto line uses the askInterface
        */
        public void doGoto() {
                getJPE().setAskMode(this,"goto","Goto line number = ");
        }

        /**
        * handle find
        */
        public void handleGoto(String key) {
                getJPE().say("Jumping to line "+key);
                try {
                        Editor editor=getJPE().getEditor();
                        int linenumber=Integer.parseInt(key);
                        if (editor!=null) editor.gotoLine(linenumber);
                        getJPE().say("Jumped to line "+key);
                } catch(NumberFormatException nfe) {
                        Log.log(nfe);
                        getJPE().say("Error in jumping to line "+key);
                }
        }

        /**
        * Handler the ask callback and finish the command started that
        * started with the setAskMode.
        */
        public void askCallback(String command,String result) {
                if (command.equals("find")) {
                        handleFind(result);
                } else if (command.equals("goto")) {
                        handleGoto(result);
                } else if (command.equals("substitute")) {
                        handleSubstitute(result);
                } else if (command.equals("replaceQuestion")) {
                        handleReplaceQuestion(result);
                } else if (command.equals("flagQuestion")) {
                        handleFlagQuestion(result);
                } 
        }

        public void lostOwnership(Clipboard cb, Transferable t) {
        }

        /**
        * copy the selected text to the buffer
        */
        public void doCopy() {
                Editor editor=getJPE().getEditor();
                copyBuffer=editor.getSelectedText();
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                cb.setContents( new StringSelection(copyBuffer), this);
                getJPE().say("Text Copy");
        }

        /**
        * select the whole text
        */
        public void doSelectAll() {
                Editor editor=getJPE().getEditor();
                getJPE().say("Select All");
                editor.selectAll();
        }

        /**
        * Cut the selected text and place it in the copy buffer
        */
        public void doCut() {
                Editor editor=getJPE().getEditor();
                copyBuffer=editor.cutSelectedText();
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                cb.setContents( new StringSelection(copyBuffer), this);
                getJPE().say("Text Cut");
        }

        /**
        * insert the current buffer into the textareas at the cursor pos
        */
        public void doPaste() {
                Editor editor=getJPE().getEditor();
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable tr = cb.getContents(this);
                String cbBuffer = "";
                try {
                        cbBuffer = (String)tr.getTransferData(DataFlavor.stringFlavor);
                } catch(IOException ioe) {
                        Log.log(ioe);
                } catch(UnsupportedFlavorException ufe) {
                        Log.log(ufe);
                }

                if(cbBuffer != null) {
                        // psion bug that adds on a newline.
                        cbBuffer = cbBuffer.substring(0,cbBuffer.length()-1);
                        String tmp = copyBuffer;
                        // make 255 a property
                        if(tmp.length() >= 255) {
                                // psion clipboard size limit bug
                                tmp = tmp.substring(0,255);
                        }
                        // use the clipboard version if it's not the same as the first 
                        // 254 letters of the internal buffer.
                        if(!tmp.equals(cbBuffer)) {
                                copyBuffer = cbBuffer;
                        }
                }

                if(copyBuffer!=null) {
                        editor.insertText(copyBuffer);
                        getJPE().say("Text Paste");
                } else {
                        getJPE().say("Text Paste Failed");
                }
        }

}
