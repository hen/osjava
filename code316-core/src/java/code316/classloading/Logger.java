package code316.classloading;

import java.io.FileOutputStream;
import java.io.PrintStream;

class Logger
{
    private StringBuffer log = new StringBuffer();
    private PrintStream out;
    private String name;

    public Logger(String name)
    {
        this.name = name;

        try
        {
            FileOutputStream o = new FileOutputStream("debug" + name + ".log");

            out = new PrintStream(o);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void error(Object o)
    {
        System.err.println(o);
    }

    public void fatal(Object o)
    {
        System.err.println(o);
    }

    public void warn(Object o)
    {
        System.out.println(o);
    }

    public void info(Object o)
    {
        System.out.println(o);
    }

    public void debug(Object o)
    {
        if (this.out != null)
        {
            out.println("[" + this.name + "] " + o);
            out.flush();
        }

        System.out.println("[" + name + "] " + o);
    }
}