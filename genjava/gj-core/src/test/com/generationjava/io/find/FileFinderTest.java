package com.generationjava.io.find;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.IOException;
import java.io.File;

import java.util.HashMap;

public class FileFinderTest extends TestCase {

    public FileFinderTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    // find(File, Map)

    public void testFind() {
        FileFinder finder = new FileFinder();
        HashMap options = new HashMap();
        options.put(Finder.NAME, "project.xml");
        File[] files = finder.find(new File("."), options);
        assertEquals(1, files.length);
    }

}
