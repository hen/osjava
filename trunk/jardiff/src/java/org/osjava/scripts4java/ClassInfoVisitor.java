package org.osjava.scripts4java;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

public class ClassInfoVisitor extends EmptyVisitor
{
    private int version;
    private int access;
    private String name;
    private String signature;
    private String supername;
    private String[] interfaces;
    private Map methodMap;
    private Map fieldMap;
    
    public void reset() {
        methodMap = new HashMap();
        fieldMap = new HashMap();
    }
    
    public ClassInfo getClassInfo() {
        return new ClassInfo(version, access, name, signature, supername,
                             interfaces, methodMap, fieldMap);
    }
    
    public void visit(int version, int access, String name, String signature,
                      String supername, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.supername = supername;
        this.interfaces = interfaces;
    }
    
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        methodMap.put(name + desc, new MethodInfo(access, name, desc,
                                                  signature, exceptions));
        return null;
    }
    
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        fieldMap.put(name,
                     new FieldInfo(access, name, desc, signature, value));
        return this;
    }
}
