package code316.beans;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class BeanUtil { 
    /**
     * Extracts BeanProperty instances from the supplied class
     * based loosely on the Java beans naming standard.
     * 
     * <p>A method pair of Y getX(), void setX(Y y) would result in a
     * BeanProperty of Y
     * </p>
     * 
     * <p>A method pair of Y isX(), void setX(Y y) would result in a
     * BeanProperty of Y
     * </p>
     * 
     * @param _class
     * @return
     */   
    public static BeanProperty[] getProperties(Class _class) {        
        Method methods[] = _class.getMethods();
        Map properties = new HashMap();
        
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName(); 
            if ( name.startsWith("get") 
                    || name.startsWith("is") ) {                        
                String pName = extractPropertyName(name);
                
                if ( pName == null ) {
                    continue;
                }                        
                BeanProperty p = (BeanProperty) properties.get(pName);
                
                if ( p == null ) {
                    Class returnType = getReturnType(method);
                    
                    if ( returnType == null ) {
                        continue;
                    }
                    
                    p = new BeanProperty();
                    p.setName(pName);
                    p.setType(returnType);
                    
                    properties.put(pName, p);
                }
                else {
                    if ( !p.getType().equals(method.getReturnType()) ) {
                        properties.remove(pName);
                        continue;
                    }
                }
                
                p.setGetter(method);
            }           
            else if ( name.startsWith("set") ) {
                String pName = extractPropertyName(name);
                
                if ( pName == null ) {
                    continue;
                }      
                                  
                BeanProperty p = (BeanProperty) properties.get(pName);
                
                if ( p == null ) {
                    p = new BeanProperty();
                    p.setName(pName);
                    
                    Class paramType = getFirstParameterType(method);
                    if ( paramType == null || getReturnType(method) != null ) {
                        continue;
                    }
                    
                    p.setType(paramType);
                    
                    properties.put(pName, p);
                }
                else {
                    Class paramType = getFirstParameterType(method);
                    if ( !p.getType().equals(paramType) || getReturnType(method) != null ) {
                        properties.remove(pName);
                        continue;
                    }                    
                }
                
                p.setSetter(method);
            } 
        }        
        
        Iterator tor = properties.keySet().iterator();
        while (tor.hasNext()) {
            String key = (String) tor.next();
            BeanProperty p = (BeanProperty) properties.get(key);
            if ( !p.isValid() ) {
                tor.remove();
            }            
        }        
        
        return (BeanProperty[]) properties.values().toArray(new BeanProperty[properties.size()]);
    }

    /**
     * Returns the return type of the supplied method.
     *  
     * @param method
     * @return
     */
    public static Class getReturnType(Method method) {
        Class type = method.getReturnType();
        
        if ( type.equals(Void.TYPE) ) {
            return null;
        }
        return type;
    }

    /**
     * Gets the Class of the methods first parameter.  If there are no
     * arguments, then null is returned;
     * 
     * @param method
     * @return
     */
    public static Class getFirstParameterType(Method method) {
        Class paramTypes[] = method.getParameterTypes();

        if ( paramTypes.length == 0 ) {
            return null;
        }
        return paramTypes[0];        
    }

    /**
     * Extracts a propertyName based on the bean
     * naming standard from the supplied methodName.
     * 
     * e.g. getX, setX, isX
     * @param methodName
     * @return property name or null if a name can not
     * be determined
     * 
     */
    public static String extractPropertyName(String methodName) {
        if ( methodName == null ) {
            return null;            
        }
        
        try {
            if ( methodName.startsWith("get") || methodName.startsWith("set") ) {
                return methodName.substring(3);            
            }
            else if ( methodName.startsWith("is") ) {
                return methodName.substring(2);            
            }            
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return null;
    }
}
