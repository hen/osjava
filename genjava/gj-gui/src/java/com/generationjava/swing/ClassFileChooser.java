package com.generationjava.swing;

import java.awt.*;
import javax.swing.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

/**
 * A class to interactively choose a class file.
 *
 * @author bayard@generationjava.com
 * @date   2000-09-01
 */
public class ClassFileChooser extends JFileChooser {

    static public void main(String[] args) {
        ClassFileChooser widget = new ClassFileChooser();
        widget.showOpenDialog(null);
        File file = widget.getSelectedFile();
        Class clss = widget.getSelectedClass();
        System.out.println(""+file+" , "+clss);
        System.exit(0);
    }

    static public boolean classInstanceOf(Class clss, String inst) {
        return false;
    }

    static public boolean classImplements(Class clss, String exts) {
        return false;
    }

    static public boolean classExtends(Class clss, String exts) {
        return false;
    }

    public ClassFileChooser() {
        super();
        this.setFileFilter( new ExtensionFileFilter(".class") );
    }

    // must do its best to return a dotted notation system
    public Class getSelectedClass() {
        if(getSelectedFile() == null) {
            return null;
        }

        File file = getSelectedFile();
        String directory = file.getParent();
        String name = file.getName();
        String sep = File.separator;
        String classname = name;
        String packagename = null;
        String loadclass = null;

        // first it trys for a similarily named .java file
        int idx = name.lastIndexOf(".");
        if(idx != -1) {
            classname = classname.substring(0,idx);
        }

        // trys to read a package name from it
        try {
            BufferedReader reader = new BufferedReader( new FileReader( new File( directory+sep+classname+".java" ) ) );
            String line = reader.readLine().trim();
            while(line != null) {
                int pck = line.indexOf("package ");
                if(pck == 0) {
                    packagename = line.substring(8,line.length()-1);
                    break;
                }
                pck = line.indexOf("public ");
                if(pck == 0) {
                    break;
                }
                pck = line.indexOf("class ");
                if(pck == 0) {
                    break;
                }
                pck = line.indexOf("abstract ");
                if(pck == 0) {
                    break;
                }
                line = reader.readLine();
            }

            if(packagename != null) {
                loadclass = packagename+"."+classname;
            } else {
                loadclass = classname;
            }
            return Class.forName(loadclass);
        } catch(IOException ioe) {
            System.err.println("No such file: "+directory+sep+classname+".java");
            // couldn't find a java file. must try next plan
        } catch(ClassNotFoundException cnfe) {
            System.err.println("No such class: '"+loadclass+"'");
            // couldn't find a java file. must try next plan
        } catch(IllegalArgumentException iae) {
            System.err.println("Problems with class: '"+loadclass+"'");
            // couldn't find a java file. must try next plan
        }

        // then it trys to remove pwd

        // then it sneakily works backwards to a com directory

        // then it gives up

        // replaces all file separators for .'s

        // returns
        return null;
    }
}
