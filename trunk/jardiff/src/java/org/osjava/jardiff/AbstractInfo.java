/*
 * org.osjava.jardiff.AbstractInfo
 *
 * $Id: IOThread.java 1952 2005-08-28 18:03:41Z cybertiger $
 * $URL: https://svn.osjava.org/svn/osjava/trunk/osjava-nio/src/java/org/osjava/nio/IOThread.java $
 * $Rev: 1952 $
 * $Date: 2005-08-28 18:03:41 +0000 (Sun, 28 Aug 2005) $
 * $Author: cybertiger $
 *
 * Copyright (c) 2005, Antony Riley
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name JarDiff nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.jardiff;

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
