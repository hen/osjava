package code316.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FileUtil {
    private volatile static int fileIndex = 1;
    public static String getUnusedFileName(String path, String seed, String extension) throws IOException {
        File file = null;
        path = endPath(path);
        while (true) {
            file = new File(path + seed + fileIndex + extension);
            
            if ( !file.exists() ) return file.getAbsolutePath();
            
            fileIndex++;            
        }
    }

	/**
	 * Returns a list of java.io.File objects that are immediate children of 
	 * the given path name.
	 * 
 	 * Does not include "." or ".."
 	 * 
	 * @exception IllegalArgument If the supplied name is a not a directory.
	 * @exception FileNotFoundException If the supplied path does not exist.
	 */
	public static List getChildren(String path) throws FileNotFoundException {
		return getChildren(new File(path));
	}

    /**
     * Returns a list of java.io.File objects that are immediate children of 
     * the given path name and whose file extension matches the supplied extension 
     * argument.
     * 
     * Does not include "." or ".."
     * 
     * @param path path to search
     * @param extension file extension to filter on, e.g. "txt" or ".txt"
     * 
     * @exception IllegalArgument If the supplied name is a not a directory.
     * @exception FileNotFoundException If the supplied path does not exist.
     */
    public static List getChildrenWithExtension(String path, String extension) throws FileNotFoundException {
        List children = getChildren(new File(path));
        List selected = new ArrayList();
        Iterator tor = children.iterator();
        
        while (tor.hasNext()) {
            File file = (File) tor.next();
            if ( file.toString().endsWith(extension) ) {
                selected.add(file);
            }
        }
        
        return selected;
    }

	
	
	/**
	 * Returns a list of java.io.File objects that are immediate children of 
	 * the given path name.
	 * 
 	 * Does not include "." or ".."
 	 * 
	 * @exception IllegalArgument If the supplied name is a not a directory.
	 * @exception FileNotFoundException If the supplied path does not exist.
	 */
	public static List getChildren(File path) throws FileNotFoundException {
		if (path == null) {
            throw new IllegalArgumentException("invalid value for path: " + path);
        }

        
        if (!(path.isDirectory()) ) {
            throw new IllegalArgumentException(path + " is not a directory");
        }
        
		List children = new ArrayList();
		File files[] = path.listFiles();
		
		for (int i = 0; i < files.length; i++) {
            children.add(files[i]);            
        }
        
		return children;
	}




	/**
	 * Returns all the immediate children of the supplied path that are
	 * themselves directories as a list of java.io.File objects.
	 * 
	 * Does not include "." or ".."
	 * 
	 * @exception IllegalArgument If the supplied name is a not a directory.
	 * @exception FileNotFoundException If the supplied path does not exist.
	 */
	public static List getChildDirectories(String path) throws FileNotFoundException {
		return getChildDirectories(new File(path));
	}


	
	/**
	 * Returns all the immediate children of the supplied path that are
	 * themselves directories as a list of java.io.File objects.
	 * 
	 * Does not include "." or ".."
	 * 
	 * @exception IllegalArgument If the supplied name is a not a directory.
	 * @exception FileNotFoundException If the supplied path does not exist.
	 */
	public static List getChildDirectories(File path) throws FileNotFoundException {
		List children = getChildren(path);
		List directories = new ArrayList();
		Iterator tor = children.iterator();
		while (tor.hasNext()) {
            File file = (File) tor.next();
            if ( file.isDirectory() ) {
            	directories.add(file);
            }
        }
		return directories;
	}
	
	
	
    /**
     * Writes the supplied content to a file with the supplied name.
     */
    public static void writeToFile(String content, String fileName)
                            throws IOException {
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(content.getBytes());
        out.close();
    }


    /**
     * Returns the contents of the supplied filename as a String.
     */
    public static String loadTextFile(String fileName) throws IOException {
		return new String(loadFile(fileName));    	
    }



	public static byte[] loadFile(String fileName, ClassLoader loader) throws IOException {
        if (fileName == null) {
            throw new IllegalArgumentException("invalid value for fileName: "
                                               + fileName);
        }

        InputStream in = getFileAsStream(fileName, loader);
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

        in.close();

        return out.toByteArray();
	}

    /**
     * Returns the contents of the supplied filename as an array of bytes.
     */	
    public static byte[] loadFile(String fileName) throws IOException {
    	return loadFile(fileName, null);
    }

	public static String[] loadLines(String fileName) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		
		
		ArrayList lines = new ArrayList();
        String line = null;

        while ((line = in.readLine()) != null) {
        	lines.add(line.trim());        	
        }
		
		return (String[]) lines.toArray(new String[lines.size()]);
	}


    /** 
     * Trys to make a fileName.
     * <p>
     * Replaces any non-ascii, non-alphanumeric characters
     * with the underscore character '_'.
     * </p>
     */
    public static String getSafeFileName(String fileName) {
    	if ( fileName == null ) {
    		return null;
    	}
    	
        fileName = fileName.toLowerCase();

        int count = fileName.length();
        int maxLength = 128;
        char[] buffer = new char[count];
        fileName.getChars(0, count, buffer, 0);

        if (maxLength > buffer.length) {
            maxLength = buffer.length;
        }

        boolean changed = false;

        for (int i = 0; i < maxLength; i++) {
            char ch = (char) buffer[i];

            if ((ch >= 48 && ch <= 57)) {
                continue;
            }
            else if ((ch >= 65 && ch <= 90)) {
                continue;
            }
            else if ((ch >= 97 && ch <= 122)) {
                continue;
            }

            buffer[i] = '_';
            changed = true;
        }

        if (changed) {
            fileName = new String(buffer, 0, maxLength);
        }

        return fileName;
    }
    
    
    
    public static String getExtension(String fileName) {
        return getExtension(fileName, ".");
    }
    
    public static String getExtension(String fileName, String extensionSeparator) {
        if ( fileName == null ) {
            return null;
        }
        
        int pos = fileName.lastIndexOf(extensionSeparator);        
        return (pos != -1 && pos < fileName.length()) 
                    ? fileName.substring(pos + 1) 
                    : "";
    }
    
    /**
     * Appends File.separator to the end of the supplied
     * path name if needed.
     */
    public static String endPath(String path) {
    	if (path == null) {
            throw new IllegalArgumentException("invalid value for path: " + path);
        }
 
 		if ( path.endsWith(File.separator) ) {
 			return path;
 		}
 		
    	return path + File.separator;
    }
    
    
    
    public static InputStream getFileAsStream(String name) throws FileNotFoundException {
    	return getFileAsStream(name, null);
    }
    
    
    /**
     * Returns an InputStream for the supplied file name.
	 */
    public static InputStream getFileAsStream(String name, ClassLoader loader) throws FileNotFoundException {
    	if (name == null) {
            throw new IllegalArgumentException("invalid value for name: " + name);
        }
        


  		FileNotFoundException e = null;
 		InputStream in = null;
 		
 		try {
        	return new FileInputStream(name);    
        }
        catch (FileNotFoundException fnfe) {
        	e = fnfe;
        }
        
        System.out.println("loader: " + loader);

		if ( loader == null ) {    	
	    	in = ClassLoader.getSystemResourceAsStream(name);
		}
		else {
			in = loader.getResourceAsStream(name);
		}
        
        if ( in == null ) {
        	throw e;
        }        
        
        return in;     
    }
}