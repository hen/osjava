package com.generationjava.apps.jpe;

import java.io.FilenameFilter;
import java.io.File;

public class JPEFilenameFilter implements FilenameFilter {

        public boolean accept(File file, String filename) {
                System.out.println("JPEFilenameFilter.accept="+filename);
                if (filename.endsWith(".java")) {
                        return true;
                } else if (filename.endsWith(".txt")) {
                        return true;
                } else if (filename.endsWith(".xml")) {
                        return true;
                } else {
                    return false;
        }
        }

}

