package com.generationjava.apps.jpe;

// Log.java

import java.io.PrintStream;
import java.util.Date;

public class Log extends Object {

        static PrintStream err = System.err;

        public static void setErr(PrintStream ps) {
                err = ps;
        }

        public static void log(Throwable t) {
                log(t.getMessage(),t);
        }

        public static void log(String str, Throwable t) {
                log(str);
                t.printStackTrace(err);
        }

        public static void log(String str) {
                if(str == null) {
                        log("null");
                } else {
                        String date = ""+new Date();
                        err.println("**"+date+"**");
                        err.println(str);
                }
        }

        public static void log(Object obj) {
                if(obj == null) {
                        log("null");
                } else {
                        log(obj.toString());
                }
        }
}
