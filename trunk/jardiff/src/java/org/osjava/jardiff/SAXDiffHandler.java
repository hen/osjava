/*
 * org.osjava.jardiff.SAXDiffHandler
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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.objectweb.asm.Type;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A specific type of DiffHandler which uses SAX to create an XML document
 * describing the changes in the diff.
 *
 * @author <a href="mailto:antony@cyberiantiger.org">Antony Riley</a>
 */
public class SAXDiffHandler implements DiffHandler
{
    /**
     * The XML namespace used.
     */
    public static final String XML_URI = "http://www.osjava.org/jardiff/0.1";

    /**
     * The javax.xml.transform.sax.TransformerHandler used to convert
     * SAX events to text.
     */
    private final TransformerHandler handler;
    
    /**
     * A reusable AttributesImpl to avoid having to instantiate this class
     * all over the place throughout the handler.
     */
    protected final AttributesImpl attr;
    
    /**
     * Create a new SAXDiffHandler which writes to System.out
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public SAXDiffHandler() throws DiffException {
        attr = new AttributesImpl();
        try {
            SAXTransformerFactory stf
                = (SAXTransformerFactory) TransformerFactory.newInstance();
            handler = stf.newTransformerHandler();
            handler.setResult(new StreamResult(System.out));
        } catch (TransformerConfigurationException tce) {
            throw new DiffException(tce);
        }
    }
    
    /**
     * Create a new SAXDiffHandler with the specified TransformerHandler.
     * This method allows the user to choose what they are going to do with
     * the output in a flexible manner, and allows anything from forwarding
     * the events to their own SAX contenthandler to building a DOM tree to
     * writing to an OutputStream.
     *
     * @param handler The SAX transformer handler to send SAX events to.
     */
    public SAXDiffHandler(TransformerHandler handler) {
        attr = new AttributesImpl();
        this.handler = handler;
    }
    
    /**
     * Start the diff.
     * This writes out the start of a &lt;diff&gt; node.
     *
     * @param oldJar ignored
     * @param newJar ignored
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startDiff(String oldJar, String newJar) throws DiffException {
        try {
            handler.startDocument();
            attr.addAttribute(XML_URI, "", "old", "CDATA", oldJar); 
            attr.addAttribute(XML_URI, "", "new", "CDATA", newJar); 
            handler.startElement(XML_URI, "", "diff", attr);
            attr.clear();
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Start the removed node.
     * This writes out a &lt;removed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startRemoved() throws DiffException {
        try {
            handler.startElement(XML_URI, "", "removed", attr);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out class info for a removed class.
     * This writes out the nodes describing a class
     *
     * @param info The info to write out.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classRemoved(ClassInfo info) throws DiffException {
        try {
            writeClassInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * End the removed section.
     * This closes the &lt;removed&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endRemoved() throws DiffException {
        try {
            handler.endElement(XML_URI, "", "removed");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Start the added section.
     * This opens the &lt;added&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startAdded() throws DiffException {
        try {
            handler.startElement(XML_URI, "", "added", attr);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out the class info for an added class.
     * This writes out the nodes describing an added class.
     *
     * @param info The class info describing the added class.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classAdded(ClassInfo info) throws DiffException {
        try {
            writeClassInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * End the added section.
     * This closes the &lt;added&gt; tag.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endAdded() throws DiffException {
        try {
            handler.endElement(XML_URI, "", "added");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Start the changed section.
     * This writes out the &lt;changed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startChanged() throws DiffException {
        try {
            handler.startElement(XML_URI, "", "changed", attr);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Start a changed section for an individual class.
     * This writes out an &lt;classchanged&gt; node with the real class
     * name as the name attribute.
     *
     * @param internalName the internal name of the class that has changed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void startClassChanged(String internalName) throws DiffException {
        try {
            attr.addAttribute(XML_URI, "", "name", "CDATA", 
                                 Tools.getClassName(internalName));
            handler.startElement(XML_URI, "", "classchanged", attr);
            attr.clear();
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out info about a removed field.
     * This just writes out the field info, it will be inside a start/end
     * removed section.
     *
     * @param info Info about the field that's been removed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldRemoved(FieldInfo info) throws DiffException {
        try {
            writeFieldInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out info about a removed method.
     * This just writes out the method info, it will be inside a start/end 
     * removed section.
     *
     * @param info Info about the method that's been removed.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodRemoved(MethodInfo info) throws DiffException {
        try {
            writeMethodInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out info about an added field.
     * This just writes out the field info, it will be inside a start/end 
     * added section.
     *
     * @param info Info about the added field.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldAdded(FieldInfo info) throws DiffException {
        try {
            writeFieldInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out info about a added method.
     * This just writes out the method info, it will be inside a start/end
     * added section.
     *
     * @param info Info about the added method.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodAdded(MethodInfo info) throws DiffException {
        try {
            writeMethodInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out info aboout a changed class.
     * This writes out a &lt;classchange&gt; node, followed by a 
     * &lt;from&gt; node, with the old information about the class
     * followed by a &lt;to&gt; node with the new information about the
     * class.
     *
     * @param oldInfo Info about the old class.
     * @param newInfo Info about the new class.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void classChanged(ClassInfo oldInfo, ClassInfo newInfo)
        throws DiffException {
        try {
            handler.startElement(XML_URI, "", "classchange", attr);
            handler.startElement(XML_URI, "", "from", attr);
            writeClassInfo(oldInfo);
            handler.endElement(XML_URI, "", "from");
            handler.startElement(XML_URI, "", "to", attr);
            writeClassInfo(newInfo);
            handler.endElement(XML_URI, "", "to");
            handler.endElement(XML_URI, "", "classchange");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out info aboout a changed field.
     * This writes out a &lt;fieldchange&gt; node, followed by a 
     * &lt;from&gt; node, with the old information about the field
     * followed by a &lt;to&gt; node with the new information about the
     * field.
     *
     * @param oldInfo Info about the old field.
     * @param newInfo Info about the new field.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void fieldChanged(FieldInfo oldInfo, FieldInfo newInfo)
        throws DiffException {
        try {
            handler.startElement(XML_URI, "", "fieldchange", attr);
            handler.startElement(XML_URI, "", "from", attr);
            writeFieldInfo(oldInfo);
            handler.endElement(XML_URI, "", "from");
            handler.startElement(XML_URI, "", "to", attr);
            writeFieldInfo(newInfo);
            handler.endElement(XML_URI, "", "to");
            handler.endElement(XML_URI, "", "fieldchange");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out info aboout a changed method.
     * This writes out a &lt;methodchange&gt; node, followed by a 
     * &lt;from&gt; node, with the old information about the method
     * followed by a &lt;to&gt; node with the new information about the
     * method.
     *
     * @param oldInfo Info about the old method.
     * @param newInfo Info about the new method.
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void methodChanged(MethodInfo oldInfo, MethodInfo newInfo)
        throws DiffException {
        try {
            handler.startElement(XML_URI, "", "methodchange", attr);
            handler.startElement(XML_URI, "", "from", attr);
            writeMethodInfo(oldInfo);
            handler.endElement(XML_URI, "", "from");
            handler.startElement(XML_URI, "", "to", attr);
            writeMethodInfo(newInfo);
            handler.endElement(XML_URI, "", "to");
            handler.endElement(XML_URI, "", "methodchange");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * End the changed section for an individual class.
     * This closes the &lt;classchanged&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endClassChanged() throws DiffException {
        try {
            handler.endElement(XML_URI, "",
                               "classchanged");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * End the changed section.
     * This closes the &lt;changed&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endChanged() throws DiffException {
        try {
            handler.endElement(XML_URI, "",
                               "changed");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * End the diff.
     * This closes the &lt;diff&gt; node.
     *
     * @throws DiffException when there is an underlying exception, e.g.
     *                       writing to a file caused an IOException
     */
    public void endDiff() throws DiffException {
        try {
            handler.endElement(XML_URI, "",
                               "diff");
            handler.endDocument();
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    /**
     * Write out information about a class.
     * This writes out a &lt;class&gt; node, which contains information about
     * what interfaces are implemented each in a &lt;implements&gt; node.
     *
     * @param info Info about the class to write out.
     * @throws SAXException If a SAX exception is thrown by the SAX API.
     */
    protected void writeClassInfo(ClassInfo info) throws SAXException {
        addAccessFlags(info);
        if (info.getName() != null)
            attr.addAttribute(XML_URI, "", "name", "CDATA", 
                              Tools.getClassName(info.getName()));
        if (info.getSignature() != null)
            attr.addAttribute(XML_URI, "", "signature", "CDATA", 
                              info.getSignature());
        if (info.getSupername() != null)
            attr.addAttribute(XML_URI, "", "superclass", "CDATA",
                              Tools.getClassName(info.getSupername()));
        handler.startElement(XML_URI, "", "class", attr);
        attr.clear();
        String[] interfaces = info.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            attr.addAttribute(XML_URI, "", "name", "CDATA", 
                              Tools.getClassName(interfaces[i]));
            handler.startElement(XML_URI, "", "implements", attr);
            attr.clear();
            handler.endElement(XML_URI, "", "implements");
        }
        handler.endElement(XML_URI, "", "class");
    }
    
    /**
     * Write out information about a method.
     * This writes out a &lt;method&gt; node which contains information about
     * the arguments, the return type, and the exceptions thrown by the 
     * method.
     *
     * @param info Info about the method.
     * @throws SAXException If a SAX exception is thrown by the SAX API.
     */
    protected void writeMethodInfo(MethodInfo info) throws SAXException {
        addAccessFlags(info);
        if (info.getName() != null)
            attr.addAttribute(XML_URI, "", "name", "CDATA", info.getName());
        if (info.getSignature() != null)
            attr.addAttribute(XML_URI, "", "signature", "CDATA", 
                              info.getSignature());
        handler.startElement(XML_URI, "", "method", attr);
        attr.clear();
        if (info.getDesc() != null)
            addMethodNodes(info.getDesc());
        String[] exceptions = info.getExceptions();
        if (exceptions != null) {
            for (int i = 0; i < exceptions.length; i++) {
                attr.addAttribute(XML_URI, "", "name", "CDATA",
                                  Tools.getClassName(exceptions[i]));
                handler.startElement(XML_URI, "", "exception", attr);
                handler.endElement(XML_URI, "", "exception");
                attr.clear();
            }
        }
        handler.endElement(XML_URI, "", "method");
    }
    
    /**
     * Write out information about a field.
     * This writes out a &lt;field&gt; node with attributes describing the
     * field.
     *
     * @param info Info about the field.
     * @throws SAXException If a SAX exception is thrown by the SAX API.
     */
    protected void writeFieldInfo(FieldInfo info) throws SAXException {
        addAccessFlags(info);
        if (info.getName() != null)
            attr.addAttribute(XML_URI, "", "name", "CDATA", info.getName());
        if (info.getSignature() != null)
            attr.addAttribute(XML_URI, "", "signature", "CDATA", 
                              info.getSignature());
        if (info.getValue() != null)
            attr.addAttribute(XML_URI, "", "value", "CDATA", 
                              info.getValue().toString());
        handler.startElement(XML_URI, "", "field", attr);
        attr.clear();
        if (info.getDesc() != null)
            addTypeNode(info.getDesc());
        handler.endElement(XML_URI, "", "field");
    }
    
    /**
     * Add attributes describing some access flags.
     * This adds the attributes to the attr field.
     *
     * @see #attr
     * @param info Info describing the access flags.
     * @throws SAXException If a SAX exception is thrown by the SAX API.
     */
    protected void addAccessFlags(AbstractInfo info) throws SAXException {
        attr.addAttribute(XML_URI, "", "access", "CDATA", 
                              info.getAccessType());
        if (info.isAbstract())
            attr.addAttribute(XML_URI, "", "abstract", "CDATA", "yes");
        if (info.isAnnotation())
            attr.addAttribute(XML_URI, "", "annotation", "CDATA", "yes");
        if (info.isBridge())
            attr.addAttribute(XML_URI, "", "bridge", "CDATA", "yes");
        if (info.isDeprecated())
            attr.addAttribute(XML_URI, "", "deprecated", "CDATA", "yes");
        if (info.isEnum())
            attr.addAttribute(XML_URI, "", "enum", "CDATA", "yes");
        if (info.isFinal())
            attr.addAttribute(XML_URI, "", "final", "CDATA", "yes");
        if (info.isInterface())
            attr.addAttribute(XML_URI, "", "interface", "CDATA", "yes");
        if (info.isNative())
            attr.addAttribute(XML_URI, "", "native", "CDATA", "yes");
        if (info.isStatic())
            attr.addAttribute(XML_URI, "", "static", "CDATA", "yes");
        if (info.isStrict())
            attr.addAttribute(XML_URI, "", "strict", "CDATA", "yes");
        if (info.isSuper())
            attr.addAttribute(XML_URI, "", "super", "CDATA", "yes");
        if (info.isSynchronized())
            attr.addAttribute(XML_URI, "", "synchronized", "CDATA", "yes");
        if (info.isSynthetic())
            attr.addAttribute(XML_URI, "", "synthetic", "CDATA", "yes");
        if (info.isTransient())
            attr.addAttribute(XML_URI, "", "transient", "CDATA", "yes");
        if (info.isVarargs())
            attr.addAttribute(XML_URI, "", "varargs", "CDATA", "yes");
        if (info.isVolatile())
            attr.addAttribute(XML_URI, "", "volatile", "CDATA", "yes");
    }
    
    /**
     * Add the method nodes for the method descriptor.
     * This writes out an &lt;arguments&gt; node containing the 
     * argument types for the method, followed by a &lt;return&gt; node
     * containing the return type.
     *
     * @param desc The descriptor for the method to write out.
     * @throws SAXException If a SAX exception is thrown by the SAX API.
     */
    protected void addMethodNodes(String desc) throws SAXException {
        Type[] args = Type.getArgumentTypes(desc);
        Type ret = Type.getReturnType(desc);
        handler.startElement(XML_URI, "", "arguments", attr);
        for (int i = 0; i < args.length; i++)
            addTypeNode(args[i]);
        handler.endElement(XML_URI, "", "arguments");
        handler.startElement(XML_URI, "", "return", attr);
        addTypeNode(ret);
        handler.endElement(XML_URI, "", "return");
    }
    
    /**
     * Add a type node for the specified descriptor.
     *
     * @param desc A type descriptor.
     * @throws SAXException If a SAX exception is thrown by the SAX API.
     */
    protected void addTypeNode(String desc) throws SAXException {
        addTypeNode(Type.getType(desc));
    }
    
    /**
     * Add a type node for the specified type.
     * This writes out a &lt;type&gt; node with attributes describing
     * the type.
     *
     * @param type The type to describe.
     * @throws SAXException If a SAX exception is thrown by the SAX API.
     */
    protected void addTypeNode(Type type) throws SAXException {
        int i = type.getSort();
        if (i == Type.ARRAY) {
            attr.addAttribute(XML_URI, "", "array", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "dimensions", "CDATA",
                              "" + type.getDimensions());
            type = type.getElementType();
            i = type.getSort();
        }
        switch (i) {
        case Type.BOOLEAN:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "boolean");
            break;
        case Type.BYTE:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "byte");
            break;
        case Type.CHAR:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "char");
            break;
        case Type.DOUBLE:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "double");
            break;
        case Type.FLOAT:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "float");
            break;
        case Type.INT:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "int");
            break;
        case Type.LONG:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "long");
            break;
        case Type.OBJECT:
            attr.addAttribute(XML_URI, "", "name", "CDATA",
                              Tools.getClassName(type.getInternalName()));
            break;
        case Type.SHORT:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "short");
            break;
        case Type.VOID:
            attr.addAttribute(XML_URI, "", "primitive", "CDATA", "yes");
            attr.addAttribute(XML_URI, "", "name", "CDATA", "void");
            break;
        }
        handler.startElement(XML_URI, "", "type", attr);
        handler.endElement(XML_URI, "", "type");
        attr.clear();
    }
}
