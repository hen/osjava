package com.generationjava.apps.jpe;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
*/
public class JPETextArea extends TextArea {

        String askline="";
        String usermsg="";
        String askCommand;
        boolean oneshot=false;
        boolean dirty=false;
        boolean tab=false;
        AskInterface askCaller;
        JPE jpe;
        private Vector queue = new Vector();

        public JPETextArea(JPE jpe) {
                this.jpe=jpe;
        }
        
        private final boolean pairable(char ch) {
        return ( (ch == '}') ||
                   (ch == ')') ||
                   (ch == ']') ||
                   (ch == '/') ||
                   (ch == '"') );
        }

        protected void processKeyEvent(KeyEvent e) {
                AskInterface tmpCaller = null;
                String tmpCommand = null;
                String tmpLine = null;
                if (e.getID()==KeyEvent.KEY_TYPED) {
                        if(e.isControlDown()) {
                                String req = jpe.requestConsume(e);
                                if(req != null) {
                                        e.consume();
                                        jpe.brief(req);
                                        return;
                                }                                
                        }                        
                }                
                if (askCaller==null) {
                        if(queue.isEmpty()) {
                                char tmpkey=e.getKeyChar();
                                if (e.getID()==KeyEvent.KEY_TYPED) {
                                        if(e.getID()!=KeyEvent.VK_DOWN) {
                                                if(!e.isControlDown()) {
                                                        if(tmpkey == '\n') {
                                                                // do autoindenting.
                                                                jpe.getEditor().autoindentInit();
//                                                                Log.log("newline? '"+e.getKeyCode()+"' '"+e.getKeyChar()+"' '"+e.isActionKey()+"' '"+KeyEvent.getKeyText(e.getKeyCode())+"' '"+e.getModifiers()+"' '"+KeyEvent.getKeyModifiersText(e.getModifiers())+"'");
                                                        }
                                                }
                                        }
                                        if(pairable(tmpkey)) {
                                            jpe.getEditor().doPairing(tmpkey);
                                        }
                                }
                                super.processKeyEvent(e);
                                return;
                        } else {
                                Vector v = (Vector)queue.elementAt(0);
                                this.askCaller = (AskInterface)v.elementAt(0);
                                this.askCommand = (String)v.elementAt(1);
                                this.usermsg = (String)v.elementAt(2);
                                this.oneshot = ((Boolean)v.elementAt(3)).booleanValue();
                                this.tab = ((Boolean)v.elementAt(4)).booleanValue();
                                queue.removeElementAt(0);
                        }
                }
                if (e.getID()==KeyEvent.KEY_TYPED) {
                        char tmpkey=e.getKeyChar();
                        if(tmpkey == '\n') {
                                // ignore newlines for this bit.
                        } else
                        if (tmpkey!=KeyEvent.VK_BACK_SPACE) {
                                askline+=tmpkey;
                        } else {
                                if (!askline.equals("")) askline=askline.substring(0,askline.length()-1);
                        }
                        if (oneshot) {
                                e.consume();
                                tmpCaller = askCaller;
                                tmpLine = askline;
                                tmpCommand = askCommand;
                                askCaller=null;
                                askline="";
                                askCommand="";
                                tmpCaller.askCallback(tmpCommand,tmpLine);
                                if(!queue.isEmpty()) {
                                        Vector v = (Vector)queue.elementAt(0);
                                        String tmpmsg = (String)v.elementAt(2);
                                        if (jpe!=null) jpe.say(tmpmsg+"_");
                                }
                        } else if (jpe!=null) jpe.say(usermsg+askline+"_");
                        if(tmpkey != '\n') {
                                e.consume();
                        } else {
                                e.consume();
                                tmpCaller = askCaller;
                                tmpLine = askline;
                                tmpCommand = askCommand;
                                askCaller=null;
                                askline="";
                                askCommand="";
                                tmpCaller.askCallback(tmpCommand,tmpLine);
                                if(!queue.isEmpty()) {
                                        Vector v = (Vector)queue.elementAt(0);
                                        String tmpmsg = (String)v.elementAt(2);
                                        if (jpe!=null) jpe.say(tmpmsg+"_");
                                }
                        }
                } else {
                    e.consume();
                }
        }
        
        public boolean getAskMode() {
                return (askCaller != null);
        }

        public void setAskMode(AskInterface askCaller,String command,String usermsg,boolean oneshot) {
                setAskMode(askCaller,command,usermsg,oneshot,false);
        }

        public void setAskMode(AskInterface askCaller,String command,String usermsg,boolean oneshot,boolean tab) {
                if(getAskMode()) {
                        System.err.println("Trying to ask multiple questions.");
                        Vector v = new Vector();
                        v.addElement(askCaller);
                        v.addElement(command);
                        v.addElement(usermsg);
                        v.addElement(new Boolean(oneshot));
                        v.addElement(new Boolean(tab));
                        queue.addElement(v);
                        return;
                }
                this.askCaller=askCaller;
                this.askCommand=command;
                this.usermsg=usermsg;
                this.oneshot=oneshot;                
                this.tab=tab;                                
                if (jpe!=null) jpe.say(usermsg+"_");
        }

}
