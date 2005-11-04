package org.osjava.scripts4java;

import org.objectweb.asm.Opcodes;

public abstract class AbstractInfo
{
    public final String ACCESS_PUBLIC = "public";
    public final String ACCESS_PROTECTED = "protected";
    public final String ACCESS_PACKAGE = "package";
    public final String ACCESS_PRIVATE = "private";
    private final int access;
    private final String name;
    
    public AbstractInfo(int access, String name) {
        this.access = access;
        this.name = name;
    }
    
    public final int getAccess() {
        return access;
    }
    
    public final String getName() {
        return name;
    }
    
    public final boolean isPublic() {
        return (access & Opcodes.ACC_PUBLIC) != 0;
    }
    
    public final boolean isProtected() {
        return (access & Opcodes.ACC_PROTECTED) != 0;
    }
    
    public final boolean isPackagePrivate() {
        return (access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | 
                    Opcodes.ACC_PRIVATE)) == 0;
    }
    
    public final boolean isPrivate() {
        return (access & Opcodes.ACC_PRIVATE) != 0;
    }
    
    public final boolean isAbstract() {
        return (access & Opcodes.ACC_ABSTRACT) != 0;
    }
    
    public final boolean isAnnotation() {
        return (access & Opcodes.ACC_ANNOTATION) != 0;
    }
    
    public final boolean isBridge() {
        return (access & Opcodes.ACC_BRIDGE) != 0;
    }
    
    public final boolean isDeprecated() {
        return (access & Opcodes.ACC_DEPRECATED) != 0;
    }
    
    public final boolean isEnum() {
        return (access & Opcodes.ACC_ENUM) != 0;
    }
    
    public final boolean isFinal() {
        return (access & Opcodes.ACC_FINAL) != 0;
    }
    
    public final boolean isInterface() {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }
    
    public final boolean isNative() {
        return (access & Opcodes.ACC_NATIVE) != 0;
    }
    
    public final boolean isStatic() {
        return (access & Opcodes.ACC_STATIC) != 0;
    }
    
    public final boolean isStrict() {
        return (access & Opcodes.ACC_STRICT) != 0;
    }
    
    public final boolean isSuper() {
        return (access & Opcodes.ACC_SUPER) != 0;
    }
    
    public final boolean isSynchronized() {
        return (access & Opcodes.ACC_SYNCHRONIZED) != 0;
    }
    
    public final boolean isSynthetic() {
        return (access & Opcodes.ACC_SYNTHETIC) != 0;
    }
    
    public final boolean isTransient() {
        return (access & Opcodes.ACC_TRANSIENT) != 0;
    }
    
    public final boolean isVarargs() {
        return (access & Opcodes.ACC_VARARGS) != 0;
    }
    
    public final boolean isVolatile() {
        return (access & Opcodes.ACC_VOLATILE) != 0;
    }
    
    public final String getAccessType() {
        if (isPublic())
            return ACCESS_PUBLIC;
        if (isProtected())
            return ACCESS_PROTECTED;
        if (isPrivate())
            return ACCESS_PRIVATE;
        return ACCESS_PACKAGE;
    }
}
