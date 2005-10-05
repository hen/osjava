package com.generationjava.swing;

import javax.swing.filechooser.FileFilter;
import java.util.ArrayList;
import java.io.File;

public class ExtensionFileFilter extends FileFilter {

    private ArrayList myExtensions  = new ArrayList();
    private String    myDescription = "Default Extension File Filter";

    public ExtensionFileFilter() {
    }

    public ExtensionFileFilter(String ext) {
        addExtension(ext);
    }

    //          Whether the given file is accepted by this filter. 
    public boolean accept(File f) {
        String filename = f.getName().toLowerCase();
        int index = filename.lastIndexOf(".");
        if(f.isDirectory()) { return true; }
        if(index == -1) { return false; }
        else if(index == filename.length()-1) { return false; }
        else if(myExtensions.contains(filename.substring(index+1))) { 
            return true; 
        } else 
            return false;
    }

    public void addExtension(String str) {
        myExtensions.add(str);
    }

    public void setDescription(String str) {
        myDescription = str;
    }
    //          The description of this filter. 
    public String getDescription() {
        return myDescription;
    }
}
