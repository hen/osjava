package org.osjava.scripts4java;

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
        return (access & 0x1) != 0;
    }
    
    public final boolean isProtected() {
        return (access & 0x4) != 0;
    }
    
    public final boolean isPackagePrivate() {
        return (access & 0x7) == 0;
    }
    
    public final boolean isPrivate() {
        return (access & 0x2) != 0;
    }
    
    public final boolean isAbstract() {
        return (access & 0x400) != 0;
    }
    
    public final boolean isAnnotation() {
        return (access & 0x2000) != 0;
    }
    
    public final boolean isBridge() {
        return (access & 0x40) != 0;
    }
    
    public final boolean isDeprecated() {
        return (access & 0x20000) != 0;
    }
    
    public final boolean isEnum() {
        return (access & 0x4000) != 0;
    }
    
    public final boolean isFinal() {
        return (access & 0x10) != 0;
    }
    
    public final boolean isInterface() {
        return (access & 0x200) != 0;
    }
    
    public final boolean isNative() {
        return (access & 0x100) != 0;
    }
    
    public final boolean isStatic() {
        return (access & 0x8) != 0;
    }
    
    public final boolean isStrict() {
        return (access & 0x800) != 0;
    }
    
    public final boolean isSuper() {
        return (access & 0x20) != 0;
    }
    
    public final boolean isSynchronized() {
        return (access & 0x20) != 0;
    }
    
    public final boolean isSynthetic() {
        return (access & 0x1000) != 0;
    }
    
    public final boolean isTransient() {
        return (access & 0x80) != 0;
    }
    
    public final boolean isVarargs() {
        return (access & 0x80) != 0;
    }
    
    public final boolean isVolatile() {
        return (access & 0x40) != 0;
    }
    
    public final String getAccessType() {
        if (isPublic())
            return "public";
        if (isProtected())
            return "protected";
        if (isPrivate())
            return "private";
        return "package";
    }
}
