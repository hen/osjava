package com.generationjava.apps.jpe;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.awt.Font;
import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
* Editor, handles the window of the selected file
* at the moment uses a TextArea to handle all the text
* commands.
*/
public class Editor extends Frame 
                                                implements         WindowListener,
                                                                                KeyListener, 
                                                                                TextListener 
{
        private JPETextArea textArea;
        public OpenFile file;
        JPE jpe;

        // used for autoindentation
        private long aiTimeStamp = -1;

        // used to tell the file dirtyness to ignore text changes 
        // caused by loading a new file, or flipping to a log.
        private boolean openingFile = false;

        /**
        * create the Editor, it doesn't open the window at
        * this stage.
        */
        public Editor(MenuBar bar,JPE jpe) {
                textArea = new JPETextArea(jpe);
        textArea.addTextListener(this);
                setMenuBar(bar);
                this.jpe=jpe;
                setTitle();
                textArea.addKeyListener(this);
                add("Center", textArea);
                addWindowListener(this);
        }

        /**
        * init the Text/Edit window, looks in the xml setup
        * for its screensize and fontsize
        */
        public void init() {

                // get the defined machine ( from config.xml )
                String setup=jpe.getProperty("setup","machine");
                
                // if no defined machine was found default to netbook
                if (setup==null) {
                        this.setSize(640, 480);
                } else

                // figure out the used machine                
                if (setup.equals("netbook") ) {
                        // setup to netbook requested
                        this.setSize(640, 480);
                } else if (setup.equals("psion7") ) {
                        // setup to psion7 requested
                        this.setSize(640, 480);
                } else if (setup.equals("5mx")) {
                        // setup to 5mx requested
                        this.setSize(640, 240);
                } else if (setup.equals("revo")) {
                        // setup to revo requested
                        this.setSize(480, 160);
                } else {
                        // if unknown machine defined also default to netbook
                        this.setSize(640, 480);
                } 

                // open and make this window visible                
                this.setVisible(true);  

                // get the defined fontsize ( from config.xml )
                String fontsize=jpe.getProperty("setup","fontsize");

                // if no fontsize is defined default to 15        
                if (setup==null) setFontSize(15);

                // try to parse the defined fontsize
                try {
                        // set the defined fontsize
                        setFontSize(Integer.parseInt(fontsize));
                } catch(NumberFormatException e) {
                        // if something goes wrong default to 15
                        setFontSize(15);
                }

                // get the defined machine ( from config.xml )
                String fontname=jpe.getProperty("setup","fontname");
                if (fontname!=null) {
                        if (fontname.equals("Arial")) {
                                setNewFont("Arial");                                
                        } else if (fontname.equals("Times")) {
                                setNewFont("Times New Roman");                                
                        } else if (fontname.equals("Courier")) {
                                setNewFont("Courier");                                
                        }
                }
        }


        /**
        * set the window to a new size and make it visible at this size
        */
        public void setWindowSize(int sizex, int sizey) {
                this.setSize(sizex,sizey);
                this.setVisible(true);
        }
        
        /**
        * set default text to the title bar
        */
        private void setTitle()
        {
                if (file==null) {
                        setTitle("No file Selected ");
                } else {
                        setTitle("File : "+file.getName());
                }
        }
        

        /**
        * increase the fontsize (zoom) with 1
        */
        public int doFontUp() {
                // get current selected font
                Font curf=textArea.getFont();

                // get the name of this font
                String name=curf.getName();

                // get the size of this font
                int size=curf.getSize();

                // create a new font based on the old one but bigger
                Font newf=new Font(name,Font.PLAIN,size+1);

                // set this new font into the textArea                
                textArea.setFont(newf);

                // return the new size
                return(size+1);
        }

        /**
        * set the fontsize of the textArea to new size
        */
        public void setFontSize(int size) {
                // get the current selected font
                Font curf=textArea.getFont();

                // get the name of this font
                String name=curf.getName();

                // create a new font based on the old one with new size
                Font newf=new Font(name,Font.PLAIN,size);

                // set this new font into the textArea
                textArea.setFont(newf);
        }


        /**
        * decrease the fontsize (zoom) with 1
        */
        public int doFontDown() {
                // get the current selected font
                Font curf=textArea.getFont();

                // get the name of this font
                String name=curf.getName();

                // get the size of the font
                int size=curf.getSize();

                // create a new font based on the old one but smaller
                Font newf=new Font(name,Font.PLAIN,size-1);

                // set this new font into the textArea
                textArea.setFont(newf);

                // return the new size of the current font
                return(size-1);
        }

        /**
        * decrease the fontsize (zoom) with 1
        */
        public void setNewFont(String name) {
                // get the current selected font
                Font curf=textArea.getFont();

                // get the size of the font
                int size=curf.getSize();

                // create a new font based on the old one but smaller
                Font newf=new Font(name,Font.PLAIN,size);

                // set this new font into the textArea
                textArea.setFont(newf);
        }
        


        public void keyPressed(KeyEvent e)        {
                        jpe.say();
        }

        public void gotoLine(int linenumber) {
                // need a smart way to do this
                String body=textArea.getText();
                int lastpos=1;
                int line=1;
                int pos=body.indexOf("\n",lastpos);
                while (pos!=-1 && line!=(linenumber-1)) {
                        line=line+1;
                        lastpos=pos+1;
                        pos=body.indexOf("\n",lastpos);
                }
                textArea.setCaretPosition(pos+1);
        }

        public boolean gotoLineWith(String key) {
          return this.gotoLineWith(key,0);
        }

        public boolean gotoLineWith(String key,int startpos) {
                // need a smart way to do this
                String body=textArea.getText();
                int pos=body.indexOf(key,startpos);
                if (pos!=-1) {
                        textArea.setCaretPosition(pos);
                        return(true);
                } else {
                        return(false);
                }
        }

        public boolean gotoLineWithRegexp(String key) {
          return this.gotoLineWithRegexp(key,0);
        }

        public boolean gotoLineWithRegexp(String key,int startpos) {
                String body = textArea.getText();
                int pos = -1;
     try {
                        RE re = new RE(key);
                        REMatch match = re.getMatch(body,startpos);
                        if(match != null) {
                                pos = match.getStartIndex();
                        }
                } catch(REException ree) {
                        Log.log(ree);
                }
                if (pos != -1) {
                        textArea.setCaretPosition(pos);
                        return true;
                } else {
                        return false;
                }
        }

        public boolean substitute(String key, String replace, String flag) {
                return this.substitute(key,replace,0,flag);
        }

        public boolean substitute(String key, String replace, int startpos, String flag) {
                String body = getSelectedText();
                boolean selected = true;
                String subst = null;
                int pos = -1;
                if( (body == null) || (body.equals("")) ) {
                        body = textArea.getText();
                        selected = false;
                }
                try {
                        RE re = new RE(key);
                        REMatch match = re.getMatch(body,startpos);
                        if(match != null) {
                                pos = match.getStartIndex();
                                if("g".equals(flag)) {
                                        subst = re.substituteAll(body, replace, startpos);
                                } else {
                                        subst = re.substitute(body, replace, startpos);
                                }
                                if(selected) {
                                        insertText(subst);
                                } else {
                                        setText(subst);
                                }
                        }
                } catch(REException ree) {
                        Log.log(ree);
                }
                if (pos != -1) {
                        textArea.setCaretPosition(pos);
                        return true;
                } else {
                        return false;
                }
        }

        public void useFile(OpenFile file) {
                this.useFile(file,true);
        }

        public void useFile(OpenFile file,boolean save) {
                openingFile = true;

                if(save) {
                        //  save information
                        if (this.file != null) {
                                this.file.setCaretPosition(textArea.getCaretPosition());
                                this.file.setText(textArea.getText());
                        }
                }

                // load new file
                this.file=file;
                jpe.setCompileState(file.isCompiled());
                jpe.setSaveState(!file.isDirty());
                jpe.setLockState(file.isLocked());
                textArea.setText(file.getText());
                textArea.setCaretPosition(file.getCaretPosition());
                textArea.setEditable(!file.isLocked());
//                file.clearDirty();
                repaint();
                jpe.putProperty("setup","selectedfile",file.getName());
                jpe.say();
        }

        public String getText() {
                return textArea.getText();
        }

        public void setText(String txt) {
                textArea.setText(txt);
        }

        public OpenFile getEditFile() {
                file.setText(textArea.getText());
                return(file);
        }

        public String getEditFilename() {
                return(file.getName());
        }


        public String getSelectedText() {
                return(textArea.getSelectedText());
        }

        public String cutSelectedText() {
                String text=textArea.getSelectedText();
                textArea.replaceRange("",textArea.getSelectionStart(),textArea.getSelectionEnd());
                return(text);
        }
        
        public void insertText(String body) {
                insertText(body,textArea.getCaretPosition());
        }

        public void insertText(String body, int pos) {
                        String sel = textArea.getSelectedText();
                if(sel != null) {
                        textArea.replaceRange(body,textArea.getSelectionStart(),textArea.getSelectionEnd());
                } else {
                        textArea.insert(body,pos);
                }
        }

        public void selectAll() {
                textArea.selectAll();
        }

        public int getCursorPos() {
                return(textArea.getCaretPosition());
        }

        public boolean getAskMode() {
                return(textArea.getAskMode());
        }

        public void setAskMode(AskInterface askCaller,String command,String usermsg,boolean oneshot) {
                textArea.setAskMode(askCaller,command,usermsg,oneshot);
        }

        public int getCursorLine() {
                // need a smart way to figure out the line number
                String body=textArea.getText();
                int curpos=getCursorPos();
                int lastpos=1;
                int line=1;
                int pos=body.indexOf("\n",lastpos);
                while (pos!=-1 && pos<curpos) {
                        line=line+1;
                        lastpos=pos+1;
                        pos=body.indexOf("\n",lastpos);
                }
                return(line);
        }


    public void windowDeiconified(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
    }

    public void windowOpened(WindowEvent event) {
    }

    public void windowClosed(WindowEvent event) {
    }

    public void windowClosing(WindowEvent event) {
                jpe.requestExit();
    }

        public void keyTyped(KeyEvent e)        {
                // very bad trick to filter for dirty key needs a fix !
//Log.log("Key Typed:'"+e.getKeyChar()+"'");
                /*
                int val=(int)e.getKeyChar();
                if (val>10 & !file.isLocked()) {
                        file.setDirty();
                        jpe.setCompileState(file.isCompiled());
                }
                jpe.setSaveState(!file.isDirty());
                */
        }

    public void textValueChanged(TextEvent te) {
                if( openingFile ) {
                        openingFile = false;
                        return;
                }
                if(aiTimeStamp != -1) {
                        long tmp = aiTimeStamp + 100L;
                        aiTimeStamp = -1;
                        if( tmp > System.currentTimeMillis()) {
//                                Log.log("Before+100: "+tmp+" - "+System.currentTimeMillis());
                                autoindent();
                        }
                }
                if( (!openingFile) && (file != null) && (jpe != null) ) {
                        file.setDirty();
                        jpe.setCompileState(file.isCompiled());
                        jpe.setSaveState(!file.isDirty());
                }
    }

        public void keyReleased(KeyEvent e) {
        }

        public void setEditable(boolean state) {
                textArea.setEditable(state);
        }


        public void autoindentInit() {
                aiTimeStamp = System.currentTimeMillis();
        }

        public void autoindent() {
                // find the previous newline.
                // grab the whitespace chars after it.
                // insert them after the current character.
                // position cursor in right place.
                int idx = getCursorPos();
                if(idx < 2) {
                        return;
                }
                String text = getText();
//                if( (text.charAt(idx) == '\n') && (text.charAt(idx-1) == '\n') ) {
//                        return;
//                }
                int nl = -1;
                for(int i=idx-2;i>0;i--) {
                        if(text.charAt(i) == '\n') {
                                nl = i;
                                break;
                        } else 
                        if(i == 0) {
                            nl = 0;
                        }
                }
                if(nl == -1) {
                        return;
                }

                String whitespace = "";
                for(int i=nl+1;i<idx;i++) {
                        if(text.charAt(i) == '\n') {
                                break;
                        } else
                        if(Character.isWhitespace(text.charAt(i))) {
                                whitespace += text.charAt(i);
                        } else {
                                break;
                        }
                }
//Log.log("Found whitespace of: '"+whitespace+"'");
                insertText(whitespace,idx);
                textArea.setCaretPosition(idx+whitespace.length());
        }

    public boolean stringContext(String text, int idx) {
        if(text.charAt(idx-1) == '\'') {
            return true;
        }
        int count = 0;
        int nl = text.lastIndexOf("\n",idx-1);
        
        while( ( (idx = text.lastIndexOf('"',idx-1) ) != -1) &&
                 ( idx > nl ) )
        {
                    if( text.charAt(idx-1) == '\\') {
                            if( text.charAt(idx-2) == '\\') {
                                if(!commentContext(text,idx)) {
                                    count++;
                                }
                            }
                        } else
                        if( text.charAt(idx-1) == '\'') {
                                // means there's single quotes around a double quote.
                                // as in the while loop above.
                        } else {
                            if(!commentContext(text,idx)) {
                                count++;
                            }
                        }
                }
                return ( (count % 2) == 1 );
    }
    
    // not too hot. can't handle comment chars in comments.
    public boolean commentContext(String text, int idx) {
        int com1 = text.lastIndexOf("/*",idx);
        int com2 = text.lastIndexOf("*/",idx);
        int com3 = text.lastIndexOf("//",idx);
        int nl = text.lastIndexOf("\n",idx);
        
        if( (com1 > com2) || (com3 > nl) ) {
            return true;
        } else {
            return false;
        }
    }

    public int lastIndexOf(String text, char chr, int idx) {
        int tmp;
        while( (tmp = text.lastIndexOf(chr,idx)) != -1) {
                if(!stringContext(text,tmp)) {
                    if(!commentContext(text,tmp)) {
                        return tmp;
                    }
                }
                idx = tmp-1;
            }
            return tmp;
        }
        
        // When someone does a certain character, it has a partner 
        // character which the character moves the cursor to and 
        // selects before returning.
        // Various issues exist: ie) context of the character:
        // bob { "hh{" }      should ignore the embedded one.
        // "g\""                 should ignore the escaped "
        // " shouldn't span lines but other brackets should.
        // might not just be single chars. ie) /* to */
        // so lots of rules involved.
        public void doPairing(char key) {
            int hilite = -1;
                int idx = getCursorPos();
                String text = getText();
                int close = 0;
                int open = 0;
                int tmp = idx;
                
                if(key == '"') {
                    if(!stringContext(text, tmp)) {
                        return;
                    } else {
                           if( (text.charAt(idx-1) == '\\') &&
                                (text.charAt(idx-2) != '\\') ) 
                            {
                                return;
                            } else
                        if(commentContext(text, tmp)) {
                            return;
                        } else {
                            while(open != -1) {
                                open = text.lastIndexOf('"',tmp);
                                if(open == -1) {
                                    return;
                                }
                                if(!stringContext(text, open) ) {
                                    hilite = open;
                                    break;
                                } else {
                                    tmp = open;
                                }
                            }
                        }
                    }                    
                } else
                if(stringContext(text, idx)) {
                    return;
                }
                
                if(key == '/') {
                        if(!commentContext(text, idx)) {
                        return;
                    } else {
                        if(text.charAt(idx-1) == '*') {
                            while(open != -1) {
                                open = text.lastIndexOf("/*",tmp);
                                close = text.lastIndexOf("*/",tmp);
                                if(open == -1) {
                                    return;
                                }
                                if(open > close) {
                                    hilite = open;
                                    break;
                                } else {
                                    return;
                                }
                            }
                        } else {
                            return;                            
                        }
                    }
                } else
                if(commentContext(text,idx)) {
                    return;
                }

            if(key == ']') {
                char openkey = '[';
                while(hilite == -1) {
                    open = lastIndexOf(text, openkey,tmp);
                    close = lastIndexOf(text, key,tmp);
                    if(open == -1) {
                        return;
                    } else
                    if(open > close) {
                        hilite = open;
                        break;
                    } else {
                        return;
                    }
                }
            }            
                                
                char openchr = 'q';
                char closechr = 'q';
                
                if(hilite == -1) {
                switch(key) {
                    case '}' :
                        openchr = '{';
                        closechr = '}';
                        break;
                    case ')' :
                        openchr = '(';
                        closechr = ')';
                        break;
                    default:
                        return;
                }
            }

        if(openchr != 'q') {
            // need to move backwards, grabbing braces 
            // until there is one more opening brace than closing
            // then that opening brace is our baby
            int count = 0;
            while( (count != -1) && (open != -1) ) {
                 open = lastIndexOf(text, openchr, tmp);
                 close = lastIndexOf(text, closechr, tmp);
                 if(open > close) {
                     tmp = open-1;
                     count--;
                 } else {
                     tmp = close-1;
                     count++;
                 }
            }
            hilite = open;
            }
            
                if(hilite != -1) {
                    textArea.setCaretPosition(hilite);
                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    textArea.setCaretPosition(idx);
                }
        }

}
