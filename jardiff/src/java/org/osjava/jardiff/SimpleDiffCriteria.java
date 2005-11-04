/*
 * org.osjava.jardiff.SimpleDiffCriteria
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
import java.util.Arrays;
import java.util.HashSet;

public class SimpleDiffCriteria implements DiffCriteria
{
    public boolean validClass(ClassInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    public boolean validMethod(MethodInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    public boolean validField(FieldInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    public boolean differs(ClassInfo oldInfo, ClassInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        if (!oldInfo.getSupername().equals(newInfo.getSupername()))
            return true;
        java.util.Set oldInterfaces
            = new HashSet(Arrays.asList(oldInfo.getInterfaces()));
        java.util.Set newInterfaces
            = new HashSet(Arrays.asList(newInfo.getInterfaces()));
        if (!oldInterfaces.equals(newInterfaces))
            return true;
        return false;
    }
    
    public boolean differs(MethodInfo oldInfo, MethodInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        if (oldInfo.getExceptions() == null
            || newInfo.getExceptions() == null) {
            if (oldInfo.getExceptions() != newInfo.getExceptions())
                return true;
        } else {
            java.util.Set oldExceptions
                = new HashSet(Arrays.asList(oldInfo.getExceptions()));
            java.util.Set newExceptions
                = new HashSet(Arrays.asList(newInfo.getExceptions()));
            if (!oldExceptions.equals(newExceptions))
                return true;
        }
        return false;
    }
    
    public boolean differs(FieldInfo oldInfo, FieldInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        if (oldInfo.getValue() == null || newInfo.getValue() == null) {
            if (oldInfo.getValue() != newInfo.getValue())
                return true;
        } else if (!oldInfo.getValue().equals(newInfo.getValue()))
            return true;
        return false;
    }
}
