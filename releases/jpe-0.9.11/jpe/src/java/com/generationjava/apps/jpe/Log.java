package com.generationjava.apps.jpe;

// Log.java

import java.io.*;
import java.util.Date;

public class Log extends Object {

        static PrintStream err = System.err;

        static public void setErr(PrintStream ps) {
                err = ps;
        }

        static public void log(Throwable t) {
                log(t.getMessage(),t);
        }

        static public void log(String str, Throwable t) {
                log(str);
                t.printStackTrace(err);
        }

        static public void log(String str) {
                if(str == null) {
                        log("null");
                } else {
                        String date = ""+new Date();
                        err.println("**"+date+"**");
                        err.println(str);
                }
        }

        static public void log(Object obj) {
                if(obj == null) {
                        log("null");
                } else {
                        log(obj.toString());
                }
        }
}
