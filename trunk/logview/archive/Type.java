package com.generationjava.logview.archive;

public interface Type {

    /**
     * Is obj1 equal to obj2, within a given error value epsilon.
     */
    public boolean equals(Object obj1, Object obj2, Object epsilon);

    public void setValue(Object value);
    public Object getValue();

}
