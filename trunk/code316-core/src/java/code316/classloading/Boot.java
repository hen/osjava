package code316.classloading;

import java.io.File;

import java.lang.reflect.Method;

public class Boot
{
    private static String guppyHome;

    public static void main(String[] args)
    {
        if (System.getProperty("guppy.home") == null)
        {
            throw new IllegalArgumentException("guppy.home must be set");
        }

        setGuppyHome(System.getProperty("guppy.home").toString());

        // create and install a class loader
        ClassLoaderX kingLoader = new ClassLoaderX("rootloader");

        Thread.currentThread().setContextClassLoader(kingLoader);

        // Load our startup class and call its process() method
        try
        {
            String libDir = getGuppyHome() + "lib" + File.separator;
            File home = new File(libDir);
            File[] files = home.listFiles();

            for (int i = 0; i < files.length; i++)
            {
                File file = files[i];
                String name = file.getName().toLowerCase();

                if (name.endsWith(".jar") || name.endsWith(".zip"))
                {
                    kingLoader.addClassSource(libDir + file.getName());
                }
            }

            Class _class = kingLoader.findClass("com.code316.guppy.MainFrame");
            Class[] paramTypes = { String[].class, ClassLoaderX.class };
            Method main = _class.getMethod("main", paramTypes);

            Object[] params = { args, kingLoader };

            main.invoke(null, params);

            //MainFrame mf = new MainFrame(kingLoader);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception during startup processing");
        }
    }

    private static void setGuppyHome(String path)
    {
        if (path == null)
        {
            throw new IllegalArgumentException("invalid value for path: "
                                               + path);
        }

        if (!path.endsWith(File.separator))
        {
            path += File.separator;
        }

        guppyHome = path;
    }

    private static String getGuppyHome()
    {
        return guppyHome;
    }
}