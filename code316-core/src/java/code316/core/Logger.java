package code316.core;

import java.io.OutputStream;

public class Logger extends Category {
    public Logger(Class _class) {
        super(_class);
    }

    public Logger(String name) {
        super(name);
    }

    public Logger() {
        super();
    }

    public Logger(int logLevel) {
        super(logLevel);
    }

    public Logger(OutputStream os, String name) {
        super(os, name);
    }
    
    

}
