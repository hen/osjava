package code316.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


public class LaunchScriptBuilder {
    public static final String DEFAULT_PROPERTIES_FILE = "run.properties";
    
    
    public static class Launcher {
        public static void main(String[] args) {
            try {
                Runtime.getRuntime().exec(getExecString(args));    
            }
            catch (IOException e) {
                e.printStackTrace();
            }                        
        }
    }
    
    public static void printUsage() {
        System.err.println("use: LaunchScriptBuilder -[sf] <propertiesFile>");
        System.err.println("      -s separator - class path separator");
        System.err.println("      -f separator - file separator");
    }
    
    public static String getExecString(String args[]) throws IOException {
        String pathSeparator = null;
        String propertiesFile = DEFAULT_PROPERTIES_FILE;
        String fileSeparator = null;
       
        if ( args.length != 0 ) {
            propertiesFile = args[args.length-1];
            
            Properties overrides = Args.parse(args, 0, (args.length - 1));
            Iterator tor = overrides.keySet().iterator();
            while (tor.hasNext()) {
                String key = (String) tor.next();
                String val = overrides.getProperty(key);
                
                // has the path separator been overridden
                if ( key.equals("s") ) {
                    pathSeparator = val;                                    
                }
                else if ( key.equals("f")) {
                    fileSeparator = val;                
                }
                else {
                    printUsage();
                    return null;
                }
            }
        }
        
                        
        Properties props = PropertiesUtil.loadPropertiesFromFile(propertiesFile);
        String mainClass = props.getProperty("main");
        
        if ( PropertiesUtil.isEmpty(mainClass) ) {
            throw new IllegalStateException("main is a required property");
        }

        String jvm = props.getProperty("jvm", "java");
        String lib = props.getProperty("lib");                        
        StringBuffer exec = new StringBuffer();
        String additionalPaths = props.getProperty("add.path");
        
        if ( pathSeparator == null ) {
            pathSeparator = props.getProperty("pathSeparator", File.pathSeparator);
        }
        
        if ( fileSeparator == null ) {
            fileSeparator = props.getProperty("fileSeparator", File.separator);
        }
        
        exec.append(jvm).append(" -cp ");
        
        
        // append additional paths
        
        if ( !PropertiesUtil.isEmpty(additionalPaths) ) {
            StringTokenizer st = new StringTokenizer(additionalPaths, ",");
            
            while (st.hasMoreTokens()) {
                exec.append(st.nextToken());

                if ( st.hasMoreTokens() ) {
                    exec.append(pathSeparator);
                }
            }                
        }
                    
        appendArchives(pathSeparator, exec, lib, fileSeparator);       
        
        if ( exec.charAt(exec.length() - 1) != ' ') {
             exec.append(' ');
        }
        
        exec.append(mainClass);

        if ( pathSeparator.equals(":") ) {
            exec.append(" $*");
        }
        else if ( pathSeparator.equals(";") ) {
            exec.append(" %*");
        }
        
        return exec.toString();            
    }
    
    public static void main(String []args) {
        try {
            String exec = getExecString(args);
            System.out.println(exec);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void appendArchives(
            String pathSeparator,
            StringBuffer exec,
            String lib,
            String fileSeparator)
            throws FileNotFoundException {        
        int appended = 0;
        String libString = null;
        List files = FileUtil.getChildren(lib);
        
        if ( lib.endsWith(fileSeparator)) {
            libString = lib;            
        }
        else {
            libString = lib + fileSeparator;
        }
                
        Iterator tor = files.iterator();
        while (tor.hasNext()) {
            File file = (File) tor.next();
            
            String fileName = file.getName(); 
            if ( fileName.endsWith("jar") || fileName.endsWith("zip") ) {               
                exec.append(pathSeparator);
                appended++;
                exec.append(libString).append(fileName);
            }
        }
        
        if (appended > 0) {
            exec.append(" ");
        }
    }    
}
