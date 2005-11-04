package org.osjava.scripts4java;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.objectweb.asm.Type;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SAXDiffHandler implements DiffHandler
{
    public static final String XML_URI = "http://www.osjava.org/jardiff/0.1";
    private final TransformerHandler handler;
    private final AttributesImpl attr;
    
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
    
    public SAXDiffHandler(TransformerHandler handler) {
        attr = new AttributesImpl();
        this.handler = handler;
    }
    
    public void startDiff(String oldJar, String newJar) throws DiffException {
        try {
            handler.startDocument();
            handler.startElement(XML_URI, "", "diff", attr);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void startRemoved() throws DiffException {
        try {
            handler.startElement(XML_URI, "", "removed", attr);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void classRemoved(ClassInfo info) throws DiffException {
        try {
            writeClassInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void endRemoved() throws DiffException {
        try {
            handler.endElement(XML_URI, "", "removed");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void startAdded() throws DiffException {
        try {
            handler.startElement(XML_URI, "", "added", attr);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void classAdded(ClassInfo info) throws DiffException {
        try {
            writeClassInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void endAdded() throws DiffException {
        try {
            handler.endElement(XML_URI, "", "added");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void startChanged() throws DiffException {
        try {
            handler.startElement(XML_URI, "", "changed", attr);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
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
    
    public void fieldRemoved(FieldInfo info) throws DiffException {
        try {
            writeFieldInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void methodRemoved(MethodInfo info) throws DiffException {
        try {
            writeMethodInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void fieldAdded(FieldInfo info) throws DiffException {
        try {
            writeFieldInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void methodAdded(MethodInfo info) throws DiffException {
        try {
            writeMethodInfo(info);
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
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
    
    public void endClassChanged() throws DiffException {
        try {
            handler.endElement(XML_URI, "",
                               "classchanged");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void endChanged() throws DiffException {
        try {
            handler.endElement(XML_URI, "",
                               "changed");
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
    public void endDiff() throws DiffException {
        try {
            handler.endElement(XML_URI, "",
                               "diff");
            handler.endDocument();
        } catch (SAXException se) {
            throw new DiffException(se);
        }
    }
    
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
    
    protected void addTypeNode(String desc) throws SAXException {
        addTypeNode(Type.getType(desc));
    }
    
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
