package code316.beans;

import java.lang.reflect.Method;


public class BeanProperty {
    private String name;
    private Method getter;
    private Method setter;
    private Class type;
        
        
    public boolean isValid() {
        if ( this.getter == null
                || this.setter == null
                || this.name == null
                || this.type == null) {
            return false;                        
        }
            
        return true;            
    }
        
    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }
}
