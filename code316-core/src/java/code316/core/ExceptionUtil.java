package code316.core;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ExceptionUtil {
    public static String stackTraceToString(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));        
        return writer.toString();
    }
}
