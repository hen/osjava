package com.generationjava.apps.jpe;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
* Log handler handles all the request from the log menu
* extends Handler object so it can be loaded in JPE object
*/
public class LogHandler extends AbstractHandler {

        // new printstreams to handle the rerouted output/error
        PrintStream        lpso;
        PrintStream        lpse;
        PrintStream        jpse;


        // Strings for the pulldown
        String StdOutString=getText("JPE.menu.Log.out");
        String StdErrString=getText("JPE.menu.Log.err");
        String JpeErrString=getText("JPE.menu.Log.jpe");

        // swap file keeps pointer to the file that was open when the
        // user called the out/error window to support toggle function
        OpenFile swapFile;

        // Openfiles to store the data we get from the rerouted streams
        OpenFile stdOut;
        OpenFile stdErr;
        OpenFile jpeErr;
        
        /**
        * create the log handler
        */
        public LogHandler(JPE jpe) {
                super(jpe);
                setName(getText("JPE.menu.Log"));

                // create the Openfile that will hold our StdOut data
                stdOut=new OpenFile(StdOutString);
                // create a new stream for stdout
                Log2AreaStream laso=new Log2AreaStream(new ByteArrayOutputStream());
                // set the file in our our new stream so it knows where to leave its data
                laso.setFile(stdOut);
                // create a new printStream from our stream
                lpso=new PrintStream(laso);
                // ask the system/jvm to reroute stdout to our new stream
                System.setOut(lpso);

                // create the Openfile that will hold our StdErr data
                stdErr=new OpenFile(StdErrString);
                // create a new stream for stderr
                Log2AreaStream lase=new Log2AreaStream(new ByteArrayOutputStream());
                // set the file in our our new stream so it knows where to leave its data
                lase.setFile(stdErr);
                // create a new printStream from our stream
                lpse=new PrintStream(lase);
                // ask the system/jvm to reroute stderr to our new stream
                System.setErr(lpse);

                jpeErr=new OpenFile(JpeErrString);
                Log2AreaStream jase=new Log2AreaStream(new ByteArrayOutputStream());
                jase.setFile(jpeErr);
                jpse=new PrintStream(jase);
                Log.setErr(jpse);

        }

        /**
        *  called by superclass to create its menu
        */
        public void createMenu(MenuBar bar) {
                Menu m = new Menu( getName() );
                m.add(new JPEMenuItem(StdOutString,new MenuShortcut('D')));
                m.add(new JPEMenuItem(StdErrString,new MenuShortcut('H')));
                m.add(new JPEMenuItem(JpeErrString, new MenuShortcut('H',true)));
                m.addActionListener(this);        
                bar.add(m);
        }


        /**
        *  AWT callback (implemented in Handler) for menu events
        */
        public void actionPerformed(ActionEvent evt)        {
                String cmd = evt.getActionCommand();
                if (cmd.equals(StdOutString)) {
                        doSwitchFile(stdOut);
                } else if (cmd.equals(StdErrString)) {
                        doSwitchFile(stdErr);
                } else if (cmd.equals(JpeErrString)) {
                        doSwitchFile(jpeErr);
                }
        }

        public void doSwitchFile(OpenFile file) {
                // get the text handler
                Editor editor = getJPE().getEditor();                
                if(file == editor.getEditFile()) {
                        // switch to swap file since we were allready active
                        editor.useFile(swapFile);
                } else {
                        // switch to file
                        OpenFile current = editor.getEditFile();
//                        if(file != current) {
                                if( stdOut != current ) {
                                        if( stdErr != current ) {
                                                if( jpeErr != current ) {
                                                        swapFile = editor.getEditFile();
                                                }
                                        }
                                }
//                        }
                        editor.useFile(file);
                }
        }

        /**
        * clear both the stdout and stderror file
        */
        public void clearLogs() {
                stdOut.setText("");
                stdErr.setText("");
        }

        /**
        * obtain the text from stderror (used by the compiler)
        */
        public String getErrorText() {
                if(stdErr != null) {
                        return stdErr.getText();
                } else {
                        return null;
                }
        }
        
}
