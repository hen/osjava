package com.generationjava.logview;

import java.util.Iterator;

public interface LogSource extends Iterator {

    public String nextEntry();

    public String currentEntry();

    public void reset();

}
