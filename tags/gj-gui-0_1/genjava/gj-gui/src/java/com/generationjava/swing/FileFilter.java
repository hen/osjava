package com.generationjava.swing;

import java.io.File;

public class FileFilter extends javax.swing.filechooser.FileFilter 
    implements java.io.FileFilter 
{

    private String description;
    private java.io.FileFilter filter;

    public FileFilter(java.io.FileFilter filter, String description) {
        this.filter = filter;
        this.description = description;
    }

    public boolean accept(File file) {
        return this.accept(file);
    }

    public String getDescription() {
        return this.description;
    }
}
