package code316.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;


/**
 * A helper class built to mimic basic functions of log4j when log4j is not
 * around.  Used for debugging and testing only. 
 * 
 * @author david.petersheim
 */
public class Category extends OutputStream {
    private static PrintStream commonOut;
    private static int id = 1;
    public static final int DEBUG = 50;
    public static final int INFO = 40;
    public static final int WARN = 30;
    public static final int ERROR = 20;
    public static final int FATAL = 10;
    private int instanceId = id++;
    private int logLevel = DEBUG;
    private boolean capturing = false;
    private PrintStream out = null;
    private StringBuffer captured;
    private String name = "";
    private String prefix = null;
    private SimpleDateFormat formatter = new SimpleDateFormat(
                                                 "ddMMMyyyy kk:mm:ss");

    public Category(Class _class) {
        this(_class.getName());
    }

    public Category(String name) {
        setName(name);
    }

    public Category() {
        this("NONAME");
    }

    public Category(int logLevel) {
        this("NONAME");
        setLogLevel(logLevel);
    }

    public Category(final OutputStream os, String name) {
        this.out = new PrintStream(os);

        if (commonOut == null) {
            commonOut = this.out;
        }

        setName(name);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (os != null) {
                        os.close();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Category getInstance(Class _class) {
        return new Category(_class);
    }

    private static int getLevelForName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("invalid value for name: "
                                               + name);
        }

        if (name.equals("DEBUG")) {
            return DEBUG;
        }
        else if (name.equals("INFO")) {
            return INFO;
        }
        else if (name.equals("ERROR")) {
            return ERROR;
        }
        else if (name.equals("FATAL")) {
            return FATAL;
        }
        else if (name.equals("WARN")) {
            return WARN;
        }

        throw new IllegalStateException("unknown level name: " + name);
    }

    public static void main(String[] args) {
        int level = Category.getLevelForName(args[0]);

        Category log = new Category("TT");

        log.setLogLevel(level);
        System.out.println("loglevel set to:" + level);

        log.debug("DEBUG MESSAGE");
        log.info("INFO MESSAGE");
        log.fatal("FATAL MESSAGE");
        log.error("ERROR MESSAGE");
        log.warn("WARN MESSAGE");
    }

    public void debug(Object o) {
        if (this.logLevel < DEBUG) {
            return;
        }

        outImpl(o, DEBUG);
    }

    public void debug(Object o, Throwable t) {
        if (this.logLevel < DEBUG) {
            return;
        }

        outImpl(o, DEBUG);
        outImpl(t, DEBUG);
    }

    private void errorImpl(Object o) {
        if (this.logLevel < ERROR) {
            return;
        }

        outImpl(o, ERROR);
    }

    public void error(Object o) {
        errorImpl(o);
    }

    public void error(Object o, Throwable t) {
        errorImpl(o);
        errorImpl(t);
    }

    public void info(Object o) {
        if (this.logLevel < INFO) {
            return;
        }

        outImpl(o, INFO);
    }

    public void info(Object o, Throwable t) {
        if (this.logLevel < INFO) {
            return;
        }

        info(o);
        info(t);
    }

    public void warn(Object o) {
        if (this.logLevel < WARN) {
            return;
        }

        outImpl(o, WARN);
    }

    public void warn(Object o, Throwable t) {
        warn(o);
        warn(t);
    }

    public void fatal(Object o) {
        if (this.logLevel < FATAL) {
            return;
        }

        outImpl(o, FATAL);
    }

    public void fatal(Object o, Throwable t) {
        errorImpl(o);
        errorImpl(t);
    }

    private String getTimeStamp() {
        return formatter.format(new Date());
    }

    private String getLevelPrefix(int level) {
        switch (level) {
        case Category.DEBUG:
            return "DEBUG] ";

        case Category.INFO:
            return "INFO] ";

        case Category.ERROR:
            return "ERROR] ";

        case Category.WARN:
            return "WARN] ";

        case Category.FATAL:
            return "FATAL] ";

        default:
            return "FAULT] ";
        }
    }

    private void outImpl(Object o, int level) {
        String value = String.valueOf(o);
        String entry = "[" + getTimeStamp() + "-"
                       + Thread.currentThread().getName() + ":" + this.prefix
                       + getLevelPrefix(level) + value;

        if (o instanceof Throwable) {
            ((Throwable) o).printStackTrace();
        }

        if (commonOut != null) {
            synchronized (commonOut) {
                commonOut.println(entry);

                if (o instanceof Properties) {
                    ((Properties) o).list(commonOut);
                }

                if (o instanceof Throwable) {
                    ((Throwable) o).printStackTrace(commonOut);
                }

                commonOut.flush();
            }

            if ((this.out != null) && (this.out != commonOut)) {
                this.out.println(entry);

                if (o instanceof Properties) {
                    ((Properties) o).list(this.out);
                }

                if (o instanceof Throwable) {
                    ((Throwable) o).printStackTrace(this.out);
                }

                this.out.flush();
            }
        }

        System.out.println(entry);

        if (capturing) {
            captured.append(entry + "\n");
        }
    }

    public String getCaptured(boolean reset) {
        String capturedContent = getCaptured();

        if (reset && (this.captured != null)) {
            this.captured.setLength(0);
        }

        return capturedContent;
    }

    public String getCaptured() {
        if (this.captured != null) {
            return this.captured.toString();
        }

        return null;
    }

    public boolean isCapturing() {
        return capturing;
    }

    public void setCapturing(boolean capturing) {
        this.capturing = capturing;

        if (this.capturing) {
            if (this.captured == null) {
                captured = new StringBuffer();
            }
        }
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public PrintStream getOut() {
        return this.out;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            return;
        }

        this.name = name;

        String shortName = name;
        int pos = shortName.lastIndexOf('.');

        if ((pos != -1) && (pos < (shortName.length() - 1))) {
            shortName = shortName.substring(pos + 1);
        }

        this.prefix = shortName + "(" + instanceId + ")-";
    }

    public static String getStackTrace() {
        StringWriter sout = new StringWriter();
        PrintWriter out = new PrintWriter(sout);
        Exception e = new Exception();

        e.printStackTrace(out);
        out.close();

        String breakAt = "at ";
        int pos = sout.toString().indexOf(breakAt);

        return (sout.toString().substring(pos + breakAt.length()));
    }

    public void write(byte[] b, int off, int len) throws IOException {
        info(new String(b, off, len));
    }

    public void write(byte[] b) throws IOException {
        info(new String(b));
    }

    public void write(int b) throws IOException {
        byte[] buffer = { (byte) b };

        write(buffer);
    }
}