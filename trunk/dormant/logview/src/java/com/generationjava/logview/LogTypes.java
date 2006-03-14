package com.generationjava.logview;

import com.generationjava.logview.log.SimpleLogType;

import com.generationjava.patterns.registry.ClassRegistry;
import com.generationjava.patterns.registry.Registry;

// has to be a class to hold the static code. Arse eh?
abstract public class LogTypes {

    static public LogType STRING = new SimpleLogType("String");
    static public LogType DATE = new SimpleLogType("Date");
    static public LogType URL = new SimpleLogType("Url");
    static public LogType EMAIL = new SimpleLogType("Email");
    static public LogType TIME = new SimpleLogType("Time");
    static public LogType INTEGER = new SimpleLogType("Integer");
    static public LogType FILENAME = new SimpleLogType("Filename");
    static public LogType IP = new SimpleLogType("IP");
    static public LogType LOGEVENT = new SimpleLogType("LogEvent");

    // static registry
    static private Registry registry = new ClassRegistry();

    static {
        registry.register("String", STRING);
        registry.register("Date", DATE);
        registry.register("Url", URL);
        registry.register("Email", EMAIL);
        registry.register("Time", TIME);
        registry.register("Integer", INTEGER);
        registry.register("Filename", FILENAME);
        registry.register("IP", IP);
        registry.register("LogEvent", LOGEVENT);
    }

    static public LogType getLogType(String name) {
        return (LogType)registry.get(name);
    }

    static public void registerLogType(String name, LogType type) {
        registry.register(name, type);
    }

}
