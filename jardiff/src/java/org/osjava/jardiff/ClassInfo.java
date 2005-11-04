package org.osjava.scripts4java;
import java.util.Map;

public final class ClassInfo extends AbstractInfo
{
    private int version;
    private String signature;
    private String supername;
    private String[] interfaces;
    private Map methodMap;
    private Map fieldMap;
    
    public ClassInfo(int version, int access, String name, String signature,
                     String supername, String[] interfaces, Map methodMap,
                     Map fieldMap) {
        super(access, name);
        this.version = version;
        this.signature = signature;
        this.supername = supername;
        this.interfaces = interfaces;
        this.methodMap = methodMap;
        this.fieldMap = fieldMap;
    }
    
    public final int getVersion() {
        return version;
    }
    
    public final String getSignature() {
        return signature;
    }
    
    public final String getSupername() {
        return supername;
    }
    
    public final String[] getInterfaces() {
        return interfaces;
    }
    
    public final Map getMethodMap() {
        return methodMap;
    }
    
    public final Map getFieldMap() {
        return fieldMap;
    }
}
