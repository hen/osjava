package code316.event;

import java.beans.Beans;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class MessagePump {
    private Method methods[];
    private Propagator propagators[];
    private Class listenerType;
    private List listeners = new LinkedList();
    
    public MessagePump(Class listenerType) {
        this.methods = listenerType.getMethods();
        this.listenerType = listenerType;
    }
    
    public boolean removeListener(Object listener) {
        return this.listeners.remove(listener);
    }
    
    public void addListener(Object listener) {
        if ( !Beans.isInstanceOf(listener, listenerType) ) {
            throw new IllegalArgumentException("wrong type for listener. "
                                    + " expected: " + this.listenerType
                                    + " received: " + listener.getClass());
        }

        this.listeners.add(listener);
    }
    
    public void pump(String methodName) {
        pump(methodName, null);
    }
    
    public void pump(String methodName, Object arg) {
        try {
            Method m = getMethod(methodName, this.methods);
            if ( m != null ) {
                Iterator tor = Collections.unmodifiableList(this.listeners).iterator();
                while (tor.hasNext()) {
                    if ( arg != null ) {
                        m.invoke(methodName, new Object[]{arg});
                    }
                    else {                    
                        m.invoke(methodName, null);
                    }
                }
            }
            else {
                throw new IllegalStateException("unknown method: " + methodName);
            }            
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    public void pump(String methodName, Object args[]) {        
     
    }
    
    private static Method getMethod(String name, Method methods[]) {
        for (int i = 0; i < methods.length; i++) {
            if ( methods[i].getName().equals(name)) {
                return methods[i];
            }                
        }
        
        return null;
    }    
}
