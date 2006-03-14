package com.generationjava.logview;

import org.apache.commons.lang.exception.NestableRuntimeException;

public class LogViewRuntimeException extends NestableRuntimeException {

    public LogViewRuntimeException() {
        super();
    }

    public LogViewRuntimeException(String msg) {
        super(msg);
    }

    public LogViewRuntimeException(Throwable t) {
        super(t);
    }

    public LogViewRuntimeException(String msg, Throwable t) {
        super(msg,t);
    }

}
