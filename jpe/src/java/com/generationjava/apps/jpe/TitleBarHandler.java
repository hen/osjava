package com.generationjava.apps.jpe;

import java.io.*;

public class TitleBarHandler extends AbstractHandler {
        Editor editor;
        String oldpre="";
        String oldfix="";
        String cs=" ";
        String ss=" ";
        String ls=" ";

        public TitleBarHandler(JPE jpe) {
                super("TitleBar",jpe);
        }

        public void say() {
                if (editor==null) {
                        editor=getJPE().getEditor();
                }
                if (editor.file==null) {
                        editor.setTitle("JPE: No file Selected");
                } else {
                        String tmp="File "+editor.file.getName();
                        this.say(tmp);
                }
        }

        public void say(String line) {
                if (editor==null) editor=getJPE().getEditor();
                line = "JPE : "+line;

                if (!oldpre.equals(line)) {
                        oldpre=line;
                        oldfix="";
                        while ((oldpre.length()+oldfix.length())<76) {
                        oldfix+=" ";
                        }
                }
                editor.setTitle(oldpre+oldfix+ss+" "+cs+" "+ls+"   "+editor.getCursorLine());
        }

        public void setSaveState(boolean state) {
                if (state) {
                        ss="s";
                } else {
                        ss=" ";
                }
        }

        public void setCompileState(boolean state) {
                if (state) {
                        cs="c";
                } else {
                        cs=" ";
                }
        }

        public void setLockState(boolean state) {
                if (state) {
                        ls="l";
                } else {
                        ls=" ";
                }
        }

}
