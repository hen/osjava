/*
 * org.osjava.jardiff.JarDiff
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
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;

public class JarDiff
{
    protected Map oldClassInfo = new HashMap();
    protected Map newClassInfo = new HashMap();
    
    public static void main(String[] args) throws Exception {
        if (args.length != 2)
            System.out.println("Usage: JarDiff <oldjar> <newjar>");
        else {
            JarDiff jd = new JarDiff(new File(args[0]), new File(args[1]));
            jd.diff(new SAXDiffHandler(), new SimpleDiffCriteria());
        }
    }
    
    public JarDiff(File oldJarFile, File newJarFile) throws DiffException {
        try {
            ClassInfoVisitor infoVisitor = new ClassInfoVisitor();
            JarFile oldJar = new JarFile(oldJarFile);
            JarFile newJar = new JarFile(newJarFile);
            Enumeration e = oldJar.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = (JarEntry) e.nextElement();
                String name = entry.getName();
                if (!entry.isDirectory() && name.endsWith(".class")) {
                    ClassReader reader
                        = new ClassReader(oldJar.getInputStream(entry));
                    infoVisitor.reset();
                    reader.accept(infoVisitor, false);
                    ClassInfo ci = infoVisitor.getClassInfo();
                    oldClassInfo.put(ci.getName(), ci);
                }
            }
            e = newJar.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = (JarEntry) e.nextElement();
                String name = entry.getName();
                if (!entry.isDirectory() && name.endsWith(".class")) {
                    ClassReader reader
                        = new ClassReader(newJar.getInputStream(entry));
                    infoVisitor.reset();
                    reader.accept(infoVisitor, false);
                    ClassInfo ci = infoVisitor.getClassInfo();
                    newClassInfo.put(ci.getName(), ci);
                }
            }
        } catch (IOException io) {
            throw new DiffException(io);
        }
    }
    
    public void diff(DiffHandler handler, DiffCriteria criteria)
        throws DiffException {
        java.util.Set onlyOld = new TreeSet(oldClassInfo.keySet());
        java.util.Set onlyNew = new TreeSet(newClassInfo.keySet());
        java.util.Set both = new TreeSet(oldClassInfo.keySet());
        onlyOld.removeAll(newClassInfo.keySet());
        onlyNew.removeAll(oldClassInfo.keySet());
        both.retainAll(newClassInfo.keySet());
        handler.startDiff(null, null);
        handler.startRemoved();
        Iterator i = onlyOld.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) oldClassInfo.get(s);
            if (criteria.validClass(ci))
                handler.classRemoved(ci);
        }
        handler.endRemoved();
        handler.startAdded();
        i = onlyNew.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) newClassInfo.get(s);
            if (criteria.validClass(ci))
                handler.classAdded(ci);
        }
        handler.endAdded();
        java.util.Set removedMethods = new TreeSet();
        java.util.Set removedFields = new TreeSet();
        java.util.Set addedMethods = new TreeSet();
        java.util.Set addedFields = new TreeSet();
        java.util.Set changedMethods = new TreeSet();
        java.util.Set changedFields = new TreeSet();
        handler.startChanged();
        i = both.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            ClassInfo oci = (ClassInfo) oldClassInfo.get(s);
            ClassInfo nci = (ClassInfo) newClassInfo.get(s);
            if (criteria.validClass(oci) || criteria.validClass(nci)) {
                Map oldMethods = oci.getMethodMap();
                Map oldFields = oci.getFieldMap();
                Map newMethods = nci.getMethodMap();
                Map newFields = nci.getFieldMap();
                Iterator j = oldMethods.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validMethod((MethodInfo) entry.getValue()))
                        removedMethods.add(entry.getKey());
                }
                j = oldFields.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validField((FieldInfo) entry.getValue()))
                        removedFields.add(entry.getKey());
                }
                j = newMethods.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validMethod((MethodInfo) entry.getValue()))
                        addedMethods.add(entry.getKey());
                }
                j = newFields.entrySet().iterator();
                while (j.hasNext()) {
                    Map.Entry entry = (Map.Entry) j.next();
                    if (criteria.validField((FieldInfo) entry.getValue()))
                        addedFields.add(entry.getKey());
                }
                changedMethods.addAll(removedMethods);
                changedMethods.retainAll(addedMethods);
                removedMethods.removeAll(changedMethods);
                addedMethods.removeAll(changedMethods);
                changedFields.addAll(removedFields);
                changedFields.retainAll(addedFields);
                removedFields.removeAll(changedFields);
                addedFields.removeAll(changedFields);
                j = changedMethods.iterator();
                while (j.hasNext()) {
                    String desc = (String) j.next();
                    MethodInfo oldInfo = (MethodInfo) oldMethods.get(desc);
                    MethodInfo newInfo = (MethodInfo) newMethods.get(desc);
                    if (!criteria.differs(oldInfo, newInfo))
                        j.remove();
                }
                j = changedFields.iterator();
                while (j.hasNext()) {
                    String desc = (String) j.next();
                    FieldInfo oldInfo = (FieldInfo) oldFields.get(desc);
                    FieldInfo newInfo = (FieldInfo) newFields.get(desc);
                    if (!criteria.differs(oldInfo, newInfo))
                        j.remove();
                }
                boolean classchanged = criteria.differs(oci, nci);
                if (classchanged || !removedMethods.isEmpty()
                    || !removedFields.isEmpty() || !addedMethods.isEmpty()
                    || !addedFields.isEmpty() || !changedMethods.isEmpty()
                    || !changedFields.isEmpty()) {
                    handler.startClassChanged(s);
                    handler.startRemoved();
                    j = removedFields.iterator();
                    while (j.hasNext())
                        handler
                            .fieldRemoved((FieldInfo) oldFields.get(j.next()));
                    j = removedMethods.iterator();
                    while (j.hasNext())
                        handler.methodRemoved((MethodInfo)
                                              oldMethods.get(j.next()));
                    handler.endRemoved();
                    handler.startAdded();
                    j = addedFields.iterator();
                    while (j.hasNext())
                        handler
                            .fieldAdded((FieldInfo) newFields.get(j.next()));
                    j = addedMethods.iterator();
                    while (j.hasNext())
                        handler.methodAdded((MethodInfo)
                                            newMethods.get(j.next()));
                    handler.endAdded();
                    handler.startChanged();
                    if (classchanged)
                        handler.classChanged(oci, nci);
                    j = changedFields.iterator();
                    while (j.hasNext()) {
                        Object tmp = j.next();
                        handler.fieldChanged((FieldInfo) oldFields.get(tmp),
                                             (FieldInfo) newFields.get(tmp));
                    }
                    j = changedMethods.iterator();
                    while (j.hasNext()) {
                        Object tmp = j.next();
                        handler.methodChanged((MethodInfo) oldMethods.get(tmp),
                                              ((MethodInfo)
                                               newMethods.get(tmp)));
                    }
                    handler.endChanged();
                    handler.endClassChanged();
                    removedMethods.clear();
                    removedFields.clear();
                    addedMethods.clear();
                    addedFields.clear();
                    changedMethods.clear();
                    changedFields.clear();
                }
            }
        }
        handler.endChanged();
        handler.endDiff();
    }
}
