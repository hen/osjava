package com.generationjava.apps.jpe;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;

public class JPEClassloader extends ClassLoader {
        private Hashtable cache = new Hashtable();

/*        private Hashtable ourclasses = new Hashtable();
        private boolean ours = false;
        private String newname;

        private int netloaded=0,netfailed=0;
        private int diskloaded=0,diskfailed=0;
        private int sysloaded=0,sysfailed=0;
*/

        public JPEClassloader() {
        }

        public synchronized Class loadClass(String name, boolean resolve) {
//                System.out.println("Classloader="+name);
                Class c = (Class) cache.get(name);

                if (c==null) { 
                        byte data[] = loadClassfile(name);
                        if (data != null) {
                                try {
                                        c = defineClass(data,0,data.length);
//                                        System.out.println("CLASS="+c);
                                        if (c != null) {
                                                cache.put(name,c);
                                                cache.put(c.getName(),c);
                                        }
                                } catch(Exception e) {
//                                        System.out.println("JPEClassloader: Can't load systemclass "+e);
                                        e.printStackTrace();
                                }
                        } else {
                                try {
                                        c=findSystemClass(name);
                                } catch(Exception e) {
                                        System.out.println("JPEClassloader2 : Can't load systemclass "+e);
//                                        e.printStackTrace();
                                }
                        }
                }
                return c;
        }

        
        private Class defineClass(String name, byte data[], boolean resolve) {
//                System.out.println("Classloader="+name);
                Class c = null;
                if (data != null) {
                        try {
                                // For 1.1 this should be :
                                c=defineClass(data,0,data.length);
                        } catch (ArrayIndexOutOfBoundsException a) {
                                System.out.println("JPEClassloader: Class ["+name+"] could not be loaded"); 
                        }
                        if (resolve) resolveClass(c);
                }
                if (c != null) {
                        // Always store package name as well
                        cache.put(c.getName(),c);
                        cache.put(name,c); // Overwrites previous put in case of name being the package name
//                        System.out.println("Added default class : "+name);        
                }
                return c;
        }
        

        /**
         * Load a class from disk
         */
        private byte[] loadClassfile(String name) {
                FileInputStream scan = null;
                File scanfile=null;
                 int filesize=0;
                byte data[]=null;

                if (name.indexOf(".class")==-1) {
                        System.out.println("NAME1="+name);
                        name=name.replace('.',File.separatorChar);
                        System.out.println("NAME2="+name);
                        name="c:\\MJC\\"+name+".class";
                        System.out.println("NAME3="+name);
                }

                scanfile = new File(name); 

                try {
                        scan = new FileInputStream(scanfile);
                } catch(Exception e) {
                        return null;
                }
                try {
                        filesize = (int)scanfile.length();
                } catch (Exception e) {
                        System.out.println("JPEClassLoader: Can't determine filesize : "+name);
                        return null;
                }
                data = new byte[filesize];
                try {
                        scan.read(data,0,filesize);
                } catch(Exception e) {
                        System.out.println("JPEClassLoader: Can't read from : "+name);        
                }
                return data;
        }


}
