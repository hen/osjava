package code316.classloading;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassLoaderX extends ClassLoader {
    private static int id = 1;
    private static List loaders = Collections.synchronizedList(new ArrayList());
    private static ClassLoaderX root;
    private List sources = new ArrayList();
    private boolean archivesLoaded;
    private Hashtable loaded = new Hashtable();
    private String name = "";
    private int myId = id++;
    private Logger log;

    public ClassLoaderX(String name) {
        log = new Logger(name);
        log.debug("created loader (default parent): " + name);
        log.debug("adding self (root) to loaders");
    }

    public ClassLoaderX(ClassLoader parent, String name) {
        super(parent);

        log = new Logger(name);
        log.debug("created loader: " + name);

        if (parent instanceof ClassLoaderX) {
            log.debug("parent is ClassLoaderX");
        }

        log.debug("adding self to loaders");
    }

    public Class findClass(String name) {
        log.debug("find class called");

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        log.debug("thread loader: " + cl);

        if (!(getParent() instanceof ClassLoaderX)
            && cl instanceof ClassLoaderX
            && (cl != this)) {
            log.debug("cascading load to thread loader");

            ClassLoaderX l = (ClassLoaderX) cl;

            return l.findClass(name);
        }

        return findClassDirect(name);
    }

    public List which(String name) {
        List found = new ArrayList();

        Iterator tor = sources.iterator();

        while (tor.hasNext()) {
            Object source = tor.next();

            InputStream in = null;
            try {
                in = getInputStream(source, name);
            }
            catch (IOException e1) {}

            if (in != null) {
                if ( found == null ) {
                    found = new ArrayList();
                }
                
                found.add(source);
                
                try {
                    in.close();
                }
                catch (IOException e) {}                    
            }
        }

        return found;
    }

    protected Class findClassDirect(String name) {
        String classFileName = getClassFileName(name);

        if (this.loaded.containsKey(classFileName)) {
            log.debug("using cached class");

            return (Class) this.loaded.get(name);
        }

        byte[] buffer = loadBytes(classFileName);

        if (buffer == null) {
            log.debug("class not found, will try parent loader");

            if ((root != this) || (getParent() == null)) {
                throw new IllegalStateException(
                    "no parent to load class from: " + name);
            }

            try {
                return getParent().loadClass(name);
            }
            catch (ClassNotFoundException e) {
                log.error("parent couldn't load class");
                throw new NoClassDefFoundError("could not load class: " + name);
            }
        }

        log.debug("calling define class: " + name);

        Class _class = defineClass(name, buffer, 0, buffer.length);

        log.debug("parent defined class: " + name);
        this.loaded.put(classFileName, _class);

        ClassLoader cl = _class.getClassLoader();

        if (cl != this) {
            log.debug("why aren't we the loader!!!");
        }

        return _class;
    }

    public void clearCache() {
        this.loaded.clear();
    }

    private byte[] loadBytes(String name) {
        log.debug("loading resource: " + name);

        try {
            Iterator tor = sources.iterator();

            while (tor.hasNext()) {
                Object source = tor.next();

                InputStream in = getInputStream(source, name);

                if (in != null) {
                    return getBytesFromStream(in);
                }
            }
        }
        catch (IOException e) {
            log.debug(e);
        }

        return null;
    }

    private byte[] getBytesFromStream(InputStream source) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead = 0;
        byte[] buffer = new byte[4096];
        int read = -1;

        while ((read = source.read(buffer)) != -1) {
            log.debug("read: " + read);
            out.write(buffer, 0, read);
        }

        source.close();

        return out.toByteArray();
    }

    private String getClassFileName(String className) {
        if (className == null) {
            throw new IllegalArgumentException(
                "invalid value for className: " + className);
        }

        log.debug("getting class name for: " + className);

        if (className.endsWith(".class")) {
            int lastDot = className.lastIndexOf('.');

            className = className.substring(0, lastDot);
        }

        char[] buffer = className.toCharArray();
        char[] newBuffer = new char[buffer.length];

        for (int i = 0; i < buffer.length; i++) {
            char ch = buffer[i];

            if (ch == '.') {
                newBuffer[i] = '/';
            }
            else {
                newBuffer[i] = ch;
            }
        }

        log.debug("built classname: " + new String(newBuffer) + ".class");

        return (new String(newBuffer) + ".class");
    }

    public static void main(String[] args) {
        //        ClassLoaderX loader = new ClassLoaderX();
        //        loader.testLoader(args[1]);
    }

    private static List getChildren(File path) throws FileNotFoundException {
        if (path == null) {
            throw new IllegalArgumentException(
                "invalid value for path: " + path);
        }

        if (!(path.isDirectory())) {
            throw new IllegalArgumentException(path + " is not a directory");
        }

        List children = new ArrayList();
        File[] files = path.listFiles();

        for (int i = 0; i < files.length; i++) {
            children.add(files[i]);
        }

        return children;
    }

    /**
     * The string is either the name of a directory or the name of a jar/zip file.
     * Anything else will cause an IOException.
     * If the file does not exist, then an IOException is thrown.
     */
    public void addClassSource(String source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException(
                "invalid value for element: " + source);
        }

        if (source.toLowerCase().endsWith(".jar")
            || source.toLowerCase().endsWith(".zip")) {
            try {
                JarFile jar = new JarFile(new File(source));

                log.debug("adding source as jar: " + source);

                this.sources.add(jar);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            // better be a path
            if (!source.endsWith(File.separator)) {
                source += File.separator;
            }

            log.debug("adding source as path: " + source);

            File path = new File(source);

            if (!path.exists() || !path.isDirectory()) {
                throw new IOException(
                    path + " does not exist or is not a directory");
            }

            this.sources.add(path);
        }
    }

    public InputStream getResourceAsStream(String name) {
        byte[] bytes = loadBytes(name);

        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        }

        return null;
    }

    public byte[] getResourceBytes(String name) {
        return loadBytes(name);
    }

    private InputStream getInputStream(Object source, String name)
        throws IOException {
        if (source instanceof JarFile) {
            return getInputStream((JarFile) source, name);
        }
        else if (source instanceof File) {
            return getInputStream((File) source, name);
        }
        else {
            throw new IllegalStateException(
                "don't know how to load from source: " + source);
        }
    }

    private InputStream getInputStream(JarFile source, String name)
        throws IOException {
        log.debug("looking for archive entry: " + name);

        if (name.indexOf("jtds") != -1) {
            Enumeration e = source.entries();

            while (e.hasMoreElements()) {
                JarEntry entry = (JarEntry) e.nextElement();

                log.debug("entry: " + entry.getName());
            }
        }

        if (source.getEntry(name) == null) {
            return null;
        }

        log.debug("found resource in: " + source.getName());

        return source.getInputStream(source.getEntry(name));
    }

    private InputStream getInputStream(File source, String name)
        throws IOException {
        File file = new File(source + File.separator + name);

        if (file.exists()) {
            return new FileInputStream(file);
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}