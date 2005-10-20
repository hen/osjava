/* SAXDiffHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
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
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "diff", attr);
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void startRemoved() throws DiffException {
	try {
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "removed", attr);
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
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "removed");
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void startAdded() throws DiffException {
	try {
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "added", attr);
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
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "added");
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void startChanged() throws DiffException {
	try {
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "changed", attr);
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void startClassChanged(String internalName) throws DiffException {
	try {
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", Tools.getClassName(internalName));
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "classchanged", attr);
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
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "classchange", attr);
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "from", attr);
	    writeClassInfo(oldInfo);
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "from");
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "", "to",
				 attr);
	    writeClassInfo(newInfo);
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "", "to");
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "classchange");
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void fieldChanged(FieldInfo oldInfo, FieldInfo newInfo)
	throws DiffException {
	try {
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "fieldchange", attr);
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "from", attr);
	    writeFieldInfo(oldInfo);
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "from");
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "", "to",
				 attr);
	    writeFieldInfo(newInfo);
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "", "to");
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "fieldchange");
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void methodChanged(MethodInfo oldInfo, MethodInfo newInfo)
	throws DiffException {
	try {
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "methodchange", attr);
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "from", attr);
	    writeMethodInfo(oldInfo);
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "from");
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "", "to",
				 attr);
	    writeMethodInfo(newInfo);
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "", "to");
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "methodchange");
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void endClassChanged() throws DiffException {
	try {
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "classchanged");
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void endChanged() throws DiffException {
	try {
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "changed");
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    public void endDiff() throws DiffException {
	try {
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "diff");
	    handler.endDocument();
	} catch (SAXException se) {
	    throw new DiffException(se);
	}
    }
    
    protected void writeClassInfo(ClassInfo info) throws SAXException {
	addAccessFlags(info);
	if (info.getName() != null)
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", Tools.getClassName(info.getName()));
	if (info.getSignature() != null)
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "signature", "CDATA", info.getSignature());
	if (info.getSupername() != null)
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "superclass", "CDATA",
			      Tools.getClassName(info.getSupername()));
	handler.startElement("http://www.osjava.org/jardiff/0.1", "", "class",
			     attr);
	attr.clear();
	String[] interfaces = info.getInterfaces();
	for (int i = 0; i < interfaces.length; i++) {
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", Tools.getClassName(interfaces[i]));
	    handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				 "implements", attr);
	    attr.clear();
	    handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			       "implements");
	}
	handler.endElement("http://www.osjava.org/jardiff/0.1", "", "class");
    }
    
    protected void writeMethodInfo(MethodInfo info) throws SAXException {
	addAccessFlags(info);
	if (info.getName() != null)
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", info.getName());
	if (info.getSignature() != null)
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "signature", "CDATA", info.getSignature());
	handler.startElement("http://www.osjava.org/jardiff/0.1", "", "method",
			     attr);
	attr.clear();
	if (info.getDesc() != null)
	    addMethodNodes(info.getDesc());
	String[] exceptions = info.getExceptions();
	if (exceptions != null) {
	    for (int i = 0; i < exceptions.length; i++) {
		attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
				  "name", "CDATA",
				  Tools.getClassName(exceptions[i]));
		handler.startElement("http://www.osjava.org/jardiff/0.1", "",
				     "exception", attr);
		handler.endElement("http://www.osjava.org/jardiff/0.1", "",
				   "exception");
		attr.clear();
	    }
	}
	handler.endElement("http://www.osjava.org/jardiff/0.1", "", "method");
    }
    
    protected void writeFieldInfo(FieldInfo info) throws SAXException {
	addAccessFlags(info);
	if (info.getName() != null)
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", info.getName());
	if (info.getSignature() != null)
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "signature", "CDATA", info.getSignature());
	if (info.getValue() != null)
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "value",
			      "CDATA", info.getValue().toString());
	handler.startElement("http://www.osjava.org/jardiff/0.1", "", "field",
			     attr);
	attr.clear();
	if (info.getDesc() != null)
	    addTypeNode(info.getDesc());
	handler.endElement("http://www.osjava.org/jardiff/0.1", "", "field");
    }
    
    protected void addAccessFlags(AbstractInfo info) throws SAXException {
	attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "access",
			  "CDATA", info.getAccessType());
	if (info.isAbstract())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "abstract", "CDATA", "yes");
	if (info.isAnnotation())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "annotation", "CDATA", "yes");
	if (info.isBridge())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "bridge", "CDATA", "yes");
	if (info.isDeprecated())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "deprecated", "CDATA", "yes");
	if (info.isEnum())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "enum",
			      "CDATA", "yes");
	if (info.isFinal())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "final",
			      "CDATA", "yes");
	if (info.isInterface())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "interface", "CDATA", "yes");
	if (info.isNative())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "native", "CDATA", "yes");
	if (info.isStatic())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "static", "CDATA", "yes");
	if (info.isStrict())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "strict", "CDATA", "yes");
	if (info.isSuper())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "super",
			      "CDATA", "yes");
	if (info.isSynchronized())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "synchronized", "CDATA", "yes");
	if (info.isSynthetic())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "synthetic", "CDATA", "yes");
	if (info.isTransient())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "transient", "CDATA", "yes");
	if (info.isVarargs())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "varargs", "CDATA", "yes");
	if (info.isVolatile())
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "volatile", "CDATA", "yes");
    }
    
    protected void addMethodNodes(String desc) throws SAXException {
	Type[] args = Type.getArgumentTypes(desc);
	Type ret = Type.getReturnType(desc);
	handler.startElement("http://www.osjava.org/jardiff/0.1", "",
			     "arguments", attr);
	for (int i = 0; i < args.length; i++)
	    addTypeNode(args[i]);
	handler.endElement("http://www.osjava.org/jardiff/0.1", "",
			   "arguments");
	handler.startElement("http://www.osjava.org/jardiff/0.1", "", "return",
			     attr);
	addTypeNode(ret);
	handler.endElement("http://www.osjava.org/jardiff/0.1", "", "return");
    }
    
    protected void addTypeNode(String desc) throws SAXException {
	addTypeNode(Type.getType(desc));
    }
    
    protected void addTypeNode(Type type) throws SAXException {
	int i = type.getSort();
	if (i == 9) {
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "array",
			      "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "dimensions", "CDATA",
			      "" + type.getDimensions());
	    type = type.getElementType();
	    i = type.getSort();
	}
	switch (i) {
	case 1:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "boolean");
	    break;
	case 3:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "byte");
	    break;
	case 2:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "char");
	    break;
	case 8:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "double");
	    break;
	case 6:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "float");
	    break;
	case 5:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "int");
	    break;
	case 7:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "long");
	    break;
	case 10:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA",
			      Tools.getClassName(type.getInternalName()));
	    break;
	case 4:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "short");
	    break;
	case 0:
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "",
			      "primitive", "CDATA", "yes");
	    attr.addAttribute("http://www.osjava.org/jardiff/0.1", "", "name",
			      "CDATA", "void");
	    break;
	}
	handler.startElement("http://www.osjava.org/jardiff/0.1", "", "type",
			     attr);
	handler.endElement("http://www.osjava.org/jardiff/0.1", "", "type");
	attr.clear();
    }
}
