package com.generationjava.apps.jpe;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;

import jstyle.JSBeautifier;
import jstyle.JSFormatter;

import com.generationjava.apps.jpe.compiler.CommandLineCompiler;
import com.generationjava.apps.jpe.compiler.ToolsCompiler;

/**
* Extra handler handles all the request from the extra menu
* extends Handler object so it can be loaded in JPE object
*/
public class ExtraHandler extends AbstractHandler {

        JPEClassloader loader;

        // Strings for the pulldown
        private String CompileString=getText("JPE.menu.Extra.compile");
        private String FontUpString=getText("JPE.menu.Extra.font.increase");
        private String FontDownString=getText("JPE.menu.Extra.font.decrease");
        private String SetRevoString="Revo";
        private String Set5mxString="5mx";
        private String SetPsion7String="Psion 7";
        private String SetNetbookString="Netbook";
        private String FontArialString="Arial";
        private String FontTimesString="Times";
        private String FontCourierString="Courier";
        private String RunAppString=getText("JPE.menu.Extra.run.run");
        private String RunAppParamString=getText("JPE.menu.Extra.run.withParam");
        private String reformatString=getText("JPE.menu.Extra.style.reformat");
        private String beautifyString=getText("JPE.menu.Extra.style.beautify");
        private String GarbageString=getText("JPE.menu.Extra.garbage");
        private String FixImportsString=getText("JPE.menu.Extra.style.fixImports");

        private String kjcString = "koji";
        private String javacString = "javac";
        private String toolsString = "tools.jar";
        private String jikesString = "jikes";

    private String runAppParam;
    private Compiler compiler;

        /**
        * create the extra handler
        */
        public ExtraHandler(JPE jpe) {
                super(jpe);
                setName(getText("JPE.menu.Extra"));
                
                // TODO: Use a getProperty
                this.compiler = new CommandLineCompiler();
                LogHandler lh=(LogHandler)getJPE().getHandler("Log");
                this.compiler.setErrorStream(lh.lpse);
        }


        /**
        *  called by superclass to create its menu
        */
        public void createMenu(MenuBar bar) {
                Menu m = new Menu(getName());
                m.add(new JPEMenuItem(CompileString,new MenuShortcut('M')));
                m.add(new JPEMenuItem(GarbageString,new MenuShortcut('M',true)));

                Menu submenu3 = new Menu(getText("JPE.menu.Extra.run"));
                submenu3.add(new JPEMenuItem(RunAppString,new MenuShortcut('R')));
                submenu3.add(new JPEMenuItem(RunAppParamString,new MenuShortcut('R',true)));
                submenu3.addActionListener(this);
                m.add(submenu3);

                Menu submenu = new Menu(getText("JPE.menu.Extra.font"));
                submenu.add(new JPEMenuItem(FontUpString));
                submenu.add(new JPEMenuItem(FontDownString));
                submenu.add(FontArialString);
                submenu.add(FontCourierString);
                submenu.add(FontTimesString);
                submenu.addActionListener(this);
                m.add(submenu);

                // create the machine selector
                Menu submenu2 = new Menu(getText("JPE.menu.Extra.machine"));
                submenu2.add(SetRevoString);
                submenu2.add(Set5mxString);
                submenu2.add(SetPsion7String);
                submenu2.add(SetNetbookString);
                submenu2.addActionListener(this);
                m.add(submenu2);

                Menu submenu4 = new Menu(getText("JPE.menu.Extra.style"));
                submenu4.add(reformatString);
                submenu4.add(beautifyString);
                submenu4.add(FixImportsString);
                submenu4.addActionListener(this);
                m.add(submenu4);
                
                Menu submenu5 = new Menu(getText("JPE.menu.Extra.compiler"));
                submenu5.add(kjcString);
                submenu5.add(toolsString);
                submenu5.add(jikesString);
                submenu5.add(javacString);
                submenu5.addActionListener(this);
                m.add(submenu5);

                m.addActionListener(this);

                bar.add(m);
        }

        /**
        *  AWT callback (implemented in Handler) for menu events
        */
    public void actionPerformed(ActionEvent evt)        {
                String cmd = evt.getActionCommand();
                if (cmd.equals(CompileString)) {
                        doCompile();
                } else if (cmd.equals(GarbageString)) {
                        doGarbage();
                } else if (cmd.equals(FontUpString)) {
                        doFontUp();
                } else if (cmd.equals(FontDownString)) {
                        doFontDown();
                } else if (cmd.equals(SetNetbookString)) {
                        doNetbookSetup();
                } else if (cmd.equals(SetPsion7String)) {
                        doNetbookSetup();
                } else if (cmd.equals(Set5mxString)) {
                        do5mxSetup();
                } else if (cmd.equals(SetRevoString)) {
                        doRevoSetup();
                } else if (cmd.equals(FontTimesString)) {
                        setNewFont("Times New Roman");
                } else if (cmd.equals(FontArialString)) {
                        setNewFont("Arial");
                } else if (cmd.equals(FontCourierString)) {
                        setNewFont("Courier");
                } else if (cmd.equals(RunAppString)) {
                        doRunApp();
                } else if (cmd.equals(RunAppParamString)) {
                        doRunAppParam();
                } else if(cmd.equals(reformatString)) {
                        doStyle("format");
                } else if(cmd.equals(beautifyString)) {
                        doStyle("beautify");
                } else if(cmd.equals(FixImportsString)) {
                        fixImports();
                } else if(cmd.equals(jikesString)) {
                    this.compiler = new CommandLineCompiler("jikes", "");
                } else if(cmd.equals(javacString)) {
                    this.compiler = new CommandLineCompiler();
                } else if(cmd.equals(kjcString)) {
                    this.compiler = new CommandLineCompiler("kjc", "");
                } else if(cmd.equals(toolsString)) {
                    this.compiler = new ToolsCompiler();
                }
        }

        public void doGarbage() {
        System.gc();
                getJPE().say("Attempting to garbage collect.");
            }

        public void doRunApp() {
                handleRunApp(null);
        }

        /**
        * do find uses the askInterface
        */
        public void doRunAppParam() {
                getJPE().setAskMode(this,"runparam","Start with param line  : ");
        }

        
        public void handleRunApp(String params) {
                Editor editor=(Editor)getJPE().getEditor();        
                OpenFile file=editor.getEditFile();
                String filename=file.getName();
                if (filename.indexOf(".java")==-1) {
                        getJPE().say("Not a java file, can only run java applications");
                        return;
                }
                filename=filename.substring(0,filename.length()-4)+"class";

                
                // create a new classloader 
                JPEClassloader loader=new JPEClassloader();


                if ("".equals(params)) {
                        params = runAppParam;
                }

                String argv[];
                if (params == null) {
                        argv = new String[0];
                } else {
                        // store
                        runAppParam = params;

                        // build up a vector of the params
                        StringTokenizer tok=new StringTokenizer(params," \n\r");
                        Vector pr=new Vector();
                        while (tok.hasMoreTokens()) {
                                pr.addElement(tok.nextToken());
                        } 
                        argv = new String[pr.size()];
                        int i=0;
                        for (i=0;i<pr.size();i++) {
                                argv[i] = (String)pr.elementAt(i);
                        }
                }

                Class newclass=loader.loadClass(filename,true);
                Method mets[]=newclass.getMethods();
                for (int i=0;i<mets.length;i++) {
                        Method met=mets[i];
                        if (met.getName().equals("main")) {
                                try {
                                        Object obs[] = new Object[1];
                                        obs[0]=argv;
                                        getJPE().say("Run called on : "+filename);
                                        met.invoke(null,obs);
                                        getJPE().say("Run finished : "+filename);
                                        return;
                                } catch(InvocationTargetException ite) {
                                        ite.getTargetException().printStackTrace();
                                        getJPE().say("An application error has happened (crtl-h for error)");
                                        return;
                                } catch(Exception e) {
                                        e.printStackTrace();
                                        getJPE().say("A runtime error has happened (crtl-h for error)");
                                        return;
                                }
                        }
                }
                getJPE().say("Can't find main(argv[]) sure its a java application ?");
        }

        /**
        * switch to netbook setup
        */
        public void doNetbookSetup() {
                Editor editor=(Editor)getJPE().getEditor();        
                editor.setWindowSize(640,480);
                getJPE().say("Switched to Netbook setup");
                getJPE().putProperty("setup","machine","netbook");
        }

        /**
        * switch to 5mx
        */
        public void do5mxSetup() {
                Editor editor=(Editor)getJPE().getEditor();        
                editor.setWindowSize(640,240);
                getJPE().say("Switched to 5mx setup");
                getJPE().putProperty("setup","machine","5mx");
        }

        /**
        * switch to revo
        */
        public void doRevoSetup() {
                Editor editor=(Editor)getJPE().getEditor();        
                editor.setWindowSize(480,160);
                getJPE().say("Switched to Revo setup");
                getJPE().putProperty("setup","machine","revo");
        }

        /**
        * shift our fontsize up by 1
        */
        public void doFontUp() {
                Editor editor=(Editor)getJPE().getEditor();        
                int size=editor.doFontUp();
                getJPE().say("Font size now "+size);
                getJPE().putProperty("setup","fontsize",""+size);
        }

        /**
        * shift our fontsize down by 1
        */
        public void doFontDown() {
                Editor editor=(Editor)getJPE().getEditor();        
                int size=editor.doFontDown();
                getJPE().say("Font size now "+size);
                getJPE().putProperty("setup","fontsize",""+size);
        }

        /**
        * change font
        */
        public void setNewFont(String name) {
                Editor editor=(Editor)getJPE().getEditor();        
                editor.setNewFont(name);
                getJPE().say("Font now "+name);
                getJPE().putProperty("setup","fontname",name);
        }


        /**
        * perform a compile on the selected file
        */
        public void doCompile() {
        	
                // lets see if we can find the compiler
                try {
                        Class cl=Class.forName("sun.tools.javac.Main");
                } catch(Exception e) {
                        Log.log(e);
                        getJPE().say("Can't find the compiler, installed new class files ??");
                        return;
                }

                // get the text handler
                Editor editor=(Editor)getJPE().getEditor();        
                if (editor!=null) {

                        // get the selected file                
                        OpenFile file=editor.getEditFile();
                        if (file!=null) {

                                // signal user we are saving the file before we compile                
                                getJPE().say("Saving : "+file.getName());
                                // save the selected file                
                                file.saveFile();
        
                                // create params for the compiler                 
                                String[] argv = new String[1];
                                argv[0]=file.getName();
        
                                if (file.getName().indexOf(".java")==-1) {
                                        getJPE().say("You may only compile *.java files");
                                        return;
                                }

                                // signal the user we start compiler        
                                getJPE().say("Compiling "+file.getName());
                                try {
                                        // clear the logs before the compile                
                                        LogHandler logger=(LogHandler)getJPE().getHandler("Log");
                                        if (logger!=null) logger.clearLogs();

									// call the compiler                
                                        this.compiler.compile(file.getName());
                                        
                                } catch(CompilerException ce) {                                	
									LogHandler lh=(LogHandler)getJPE().getHandler("Log");
									ce.printStackTrace(lh.jpse);
                                } catch(Exception e) {
                                        // ignore this to catch Exit(0)
                                        //e.printStackTrace();
                                }

                                // check the compile log for errors
                                int epos=checkCompileError();
                                file.setErrorPos(epos);
                                if (epos==-1) {
                                        file.setCompiled();
                                        getJPE().setCompileState(true);
                                }
                        }
                }
        }

        /**
        * check if we had compile errors (simple version for now)
        */
        public int checkCompileError() {
                // get the log handler
                LogHandler lh=(LogHandler)getJPE().getHandler("Log");
                if (lh!=null) {
                                // get the text from the log handler
                                String errorbody=lh.getErrorText();
                                // simple check to see if we have a error
                                if (errorbody.indexOf("error")==-1) {                
                                        // signal we don't have a error                        
                                        getJPE().say("Compile Done ( okay )");
                                        return(-1);
                                } else if (errorbody.indexOf("javac")!=-1) {
                                        getJPE().say("Compiler not found : check readme how to install it !!");
                                        return(-1);
                                } else {
                                        // signal we have errors                
                                        getJPE().say("Compile Done ( Errors Found !! , crtl-g to jump to error )");
                                        // very simple parser to find the error
                                        int e1=errorbody.indexOf(':',5);
                                        if (e1!=-1) {
                                                int e2=errorbody.indexOf(':',e1+1);
                                                try {
                                                        String tmp=errorbody.substring(e1+1,e2);
                                                        System.out.println(tmp);
                                                        int e3=Integer.parseInt(tmp);
                                                        return(e3);
                                                } catch(Exception e) {}
                                        }
                                }
                }
                return(-1);
        }

        private void doStyle(String nom) {
                JSBeautifier jsb = new JSBeautifier();
                JSFormatter jsf = new JSFormatter();

                Editor editor=(Editor)getJPE().getEditor();
                String txt = editor.getText();
                try {
                        StringWriter swtr = new StringWriter();
                        PrintWriter pwtr = new PrintWriter(swtr);
                        BufferedReader bfrdr = new BufferedReader(new StringReader(txt));

                        if("format".equals(nom)) {
                                jsf.format(bfrdr,pwtr);
                        } else
                        if("beautify".equals(nom)) {
                                jsb.beautifyReader(bfrdr,pwtr);
                        }

                        pwtr.flush();
                        bfrdr.close();
                        editor.setText(swtr.toString());
                        pwtr.close();
                } catch(IOException ioe) {
                        // no need to close writers as none of them use resources.
                        ioe.printStackTrace();
                }
        }

        public void fixImports() {
                Editor editor=(Editor)getJPE().getEditor();
                String txt = editor.getText();
                ImportFixer fix = new ImportFixer();
                txt = fix.fixImport(txt);
                if(txt != null) {
                        editor.setText(txt);
                }
        }

        /**
        * Handler the ask callback and finish the command started that
        * started with the setAskMode.
        */
        public void askCallback(String command,String result) {
                if (command.equals("runparam")) {
                        handleRunApp(result);
                } 
        }


}
