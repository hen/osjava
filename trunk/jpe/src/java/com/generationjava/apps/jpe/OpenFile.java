package com.generationjava.apps.jpe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
* OpenFile defines a opened file within JPE, it handles its buffers
* status and how it gets loaded and saved.
*/
public class OpenFile {

        String filename;
        String text="";
        int savedpos=0;
        int        errorpos=-1;
        boolean dirty=false;
        boolean compiled=false;
        boolean locked=false;

        /**
        * Create a Openfile if possible it will load it from storage
        */
        public OpenFile(String filename) {
                this.filename=filename;
                loadFile();
        }

        

        /**
        * get the name of the file
        */
        public String getName() {
                return(filename);
        }

        /**
        * set a new filename for this file
        */
        public void setName(String filename) {
                this.filename=filename;
        }


        /**
        * load the file from disk into the buffer
        */
        public void loadFile() {
                String part;
                File file=new File(filename);        
                if(!file.exists()) {
                        dirty = false;
                        return;
                }
                try {
                        // optimise here with a size for buffer. use file size
                        StringBuffer all = new StringBuffer((int)file.length());
                        FileReader fr = new FileReader(file);
                        BufferedReader br = new BufferedReader(fr);

                        while ( (part=br.readLine()) != null)
                        {
                                all.append(part);
                                all.append('\n');
                        }

                        br.close();
                        fr.close();
                        text=all.toString();

                        // chop ending newline
                        if(text.length() > 0) {
                                text = text.substring(0,text.length()-1);
                        }
                } catch (IOException ioe) {
                        Log.log(ioe);
                }

                dirty=false;
        }

        /**
        * return the text of this file
        */
        public String getText() {
                return(text);
        }

        /**
        * set the text of this file
        */
        public void setText(String text) {
                this.text=text;
        }

        /**
        * append text to the current file
        */
        public void appendText(String text) {
                this.text+=text;
        }

        
        /**
        * save file to storage
        */        
        public void saveFile() {
                try {
                        File file=new File(filename);
                        FileWriter fw = new FileWriter(file);
                        BufferedWriter bw=new BufferedWriter(fw);
                        bw.write(text,0,text.length());
                        bw.close();
                        fw.close();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                dirty=false;
        }


        /**
        * set the cursor position within the file
        */
        public void setCaretPosition(int pos) {
                savedpos=pos;
        }

        /**
        * get the cursor position within the file
        */
        public int getCaretPosition() {
                return savedpos;
        }

        public int getErrorPos() {
                return errorpos;
        }

        public void setErrorPos(int errorpos) {
                this.errorpos=errorpos;
        }        

        public boolean isCompiled() {
                return compiled;
        }

        public boolean isLocked() {
                return locked;
        }

        public boolean isDirty() {
                return dirty;
        }

        public void setDirty() {
                dirty=true;
                clearCompiled();
        }

        public void clearDirty() {
                dirty=false;
        }

        public void clearCompiled() {
                compiled=false;
        }
        public void setCompiled() {
                compiled=true;
        }

        public void clearLocked() {
                locked=false;
        }

        public void setLocked() {
                locked=true;
        }


}
