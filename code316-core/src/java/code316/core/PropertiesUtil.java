package code316.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;


public class PropertiesUtil {
	private static HashMap bundles = new HashMap();

    public PropertiesUtil() {
        super();
    }
    
    /**
     * Gets the value named propertyName from the ResoureBundle 
     * associated with bundleName.
     * 
     * If the specified properties file has been loaded before, 
     * the previously loaded properties will be loaded.
     * 
     * Returns null if the property is not found.
     * 
     * @exception IllegalArgumentException if the properties file 
     * can not be loaded.
	 */
    public static String getProperty(String propertiesFile, String propertyName) {
    	if ( propertiesFile == null || propertyName == null ) {
    		return null;
    	}

		synchronized (bundles) {
			Properties props = (Properties) bundles.get(propertiesFile);
			
			if ( props == null ) {
				try {
                	props = loadPropertiesFromFile(propertiesFile);    
                	bundles.put(propertiesFile, props);
                }
                catch (IOException e) {
                	e.printStackTrace();                	
                	throw new IllegalArgumentException("could not load properties: " + e);
                }
			}
			
			return (String) props.get(propertyName);    				
		}
    }
    
    /**
     * Returns a Properties object loaded from the supplied 
     * fileName.
     * 
     */
    public static Properties loadPropertiesFromFile(String fileName) throws IOException {
    	Properties props = new Properties();    	
    	
		InputStream in = FileUtil.getFileAsStream(fileName);
		
    	props.load(in);
    	
    	in.close();
    	
    	return props;
    }
    

	/**
     * Store the supplied Properties object to the supplied
     * fileName.
     * 
	 * If the header argument is not null, then an ASCII # character, the header string, and a line separator are first written to the output stream. Thus, the header can serve as an identifying comment. 
     */
    public static void storeProperties(Properties properties, String fileName) throws IOException {
    	storeProperties(properties, fileName, null);    	
    }

    
   
	/**
     * Store the supplied Properties object to the supplied
     * fileName.
     * 
	 * If the header argument is not null, then an ASCII # character, the header string, and a line separator are first written to the output stream. Thus, the header can serve as an identifying comment. 
     */
    public static void storeProperties(Properties properties, String fileName, String header) throws IOException {
    	if (properties == null || fileName == null ) {
    		return ;
    	}
    	
		FileOutputStream out = new FileOutputStream(fileName);
		
		properties.store(out, header);
		
		out.close();
    }

    public static boolean isEmpty(Object[] value) {
        if ( value == null || value.length == 0 ) {
            return true;
        }

        return false;
    }   

    
    public static boolean isEmpty(String value) {
        if ( value != null && value.trim().length() != 0 ) {
    		return false;
    	}
    	
    	return true;
    }   
    
    /**
     * @param props Properties object to be filtered.
     * @param filter String that key names must start with to be included
     *               in the returned Map.
     * @return Propeties A Properties object containing properties whose key
     * 					 names match the supplied filter.
     */
    public static Properties filter(Properties props, String filter) {
        Properties filtered = new Properties();
        
        Iterator tor = props.keySet().iterator();
        while (tor.hasNext()) {
            String key = (String) tor.next();
			
			if ( key.startsWith(filter) ) {
			    Object value = props.get(key);
                filtered.put(key, value);			    
			}
        }
        
        return filtered;
    }     
}
