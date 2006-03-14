package com.generationjava.logview;

public interface Loglet {

    public Log parse(Loglet loglet) throws LogViewException;
    public Log parse(Log log) throws LogViewException;
    public Log parse() throws LogViewException;
    public LogEvent parseEvent(LogIterator iterator) throws LogViewException;
    public boolean hasMoreEvents(LogIterator iterator);
    public void setLoglet(Loglet loglet);

    public String[] getFieldNames();

}
