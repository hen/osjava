package com.generationjava.logview;

import java.text.ParseException;
import java.util.Iterator;

public interface Log extends Cloneable {

    public LogIterator iterator();

    public String getName();

    public Iterator iterateFieldNames();

    public Object cloneObject() throws CloneNotSupportedException;

}
