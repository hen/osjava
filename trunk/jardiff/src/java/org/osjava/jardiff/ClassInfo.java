/*
 * org.osjava.jardiff.ClassInfo
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
