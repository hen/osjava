package code316.event;

import java.beans.Beans;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * A utility class to be used by message pumps to
 * propagate messages to a listener.
 */
public class Propagator {
    private List listeners = new ArrayList();

    private Class listenerType;
    private String methodName;
    private String subjectType;
    private Method targetMethod;

    public Propagator(String listenerClass, String listenerMethod, Class _class) {
        try {
            this.listenerType = Class.forName(listenerClass);                       
            this.targetMethod = null;
            
            if (_class != null) {
                targetMethod = this.listenerType.getMethod(listenerMethod, new Class[]{_class});                
            }
            else {
                targetMethod = this.listenerType.getMethod(listenerMethod, null);
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("error creating Propagator: " + e);
        }
    }

    public void addListener(Object listener) {
        if ( !Beans.isInstanceOf(listener, listenerType) ) {
            throw new IllegalArgumentException("wrong type for listener. "
                                    + " expected: " + this.listenerType
                                    + " received: " + listener.getClass());
        }

        this.listeners.add(listener);
    }

    public boolean removeListener(Object listener) {
        return this.listeners.remove(listener);
    }

    public void propagate(Object o) {
        Iterator tor = Collections.unmodifiableList(this.listeners).iterator();
        while (tor.hasNext()) {
            Object listener = tor.next();
            try {
                if ( o == null ) {
                    this.targetMethod.invoke(listener, null);
                }
                else {
                    this.targetMethod.invoke(listener, new Object[]{o});
                }                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
