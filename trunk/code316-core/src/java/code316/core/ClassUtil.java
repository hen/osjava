package code316.core;



public class ClassUtil {
    /**
     * Returns an instance of the supplied className.
     * @param className - Name of class to be instantiated.
     * @return an instance of the class or null if the class could not be instantiated
     * @exception IllegalArgumentException if the className is null.
     */
    public static Object quiteInstantiate(String className) {
		if (className == null) {
		    throw new IllegalArgumentException("invalid value for className: " + className);
		}	

        try {
            return instantiate(className);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Returns an instance of the supplied className.
     * @param className - Name of class to be instantiated.
     * @exception IllegalArgumentException if the className is null.
     */
    public static Object instantiate(String className)
                              throws ClassNotFoundException, 
                                     InstantiationException, 
                                     IllegalAccessException {
        if (className == null) {
            throw new IllegalArgumentException("invalid value for className : "
                                               + className);
        }

        Class _class = Class.forName(className);

        return _class.newInstance();
    }
}