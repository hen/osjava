package com.generationjava.logview;

import org.apache.commons.lang.exception.NestableException;;

public class LogViewException extends NestableException {

    public LogViewException() {
        super();
    }

    public LogViewException(String msg) {
        super(msg);
    }

    public LogViewException(Throwable t) {
        super(t);
    }

    public LogViewException(String msg, Throwable t) {
        super(msg,t);
    }

}
