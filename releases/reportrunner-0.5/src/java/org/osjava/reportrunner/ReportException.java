package org.osjava.reportrunner;

public class ReportException extends RuntimeException {

    public ReportException() {
        super();
    }

    public ReportException(String msg) {
        super(msg);
    }

    public ReportException(Throwable t) {
        super(t);
    }

    public ReportException(String msg, Throwable t) {
        super(msg, t);
    }

}
