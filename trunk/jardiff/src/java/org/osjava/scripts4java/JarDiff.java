package org.osjava.scripts4java.jardiff;

import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.io.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class JarDiff {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.out.println("Usage: JarDiff <oldjar> <newjar>");
            return;
        }
        JarDiff jd = new JarDiff(new File(args[0]), new File(args[1]));
        Document doc = jd.diff(new SimpleDiffCriteria(), new SimpleNodeFormat());
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT,"yes");
        trans.transform(new DOMSource(doc), new StreamResult(System.out));
    }


    private Map oldClassInfo = new HashMap();
    private Map newClassInfo = new HashMap();

    public JarDiff(File oldJarFile, File newJarFile) throws Exception {
        Enumeration e;
        JarFile oldJar = new JarFile(oldJarFile);
        JarFile newJar = new JarFile(newJarFile);
        e = oldJar.entries();
        while(e.hasMoreElements()) {
            JarEntry entry = (JarEntry) e.nextElement();
            String name = entry.getName();
            if(!entry.isDirectory() && name.endsWith(".class")) {
                ClassReader reader = 
                    new ClassReader(oldJar.getInputStream(entry));
                ClassInfo info = new ClassInfo(reader);
                oldClassInfo.put(info.getName(),info);
            }
        }
        e = newJar.entries();
        while(e.hasMoreElements()) {
            JarEntry entry = (JarEntry) e.nextElement();
            String name = entry.getName();
            if(!entry.isDirectory() && name.endsWith(".class")) {
                ClassReader reader = 
                    new ClassReader(newJar.getInputStream(entry));
                ClassInfo info = new ClassInfo(reader);
                newClassInfo.put(info.getName(),info);
            }
        }
    }

    public Document diff(DiffCriteria criteria, NodeFormat format) throws Exception {
        Iterator i;
        Set onlyOld = new TreeSet(oldClassInfo.keySet());
        Set onlyNew = new TreeSet(newClassInfo.keySet());
        Set both = new TreeSet(oldClassInfo.keySet());
        onlyOld.removeAll(newClassInfo.keySet());
        onlyNew.removeAll(oldClassInfo.keySet());
        both.retainAll(newClassInfo.keySet());

        Document ret = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .getDOMImplementation()
            .createDocument(null,"diff",null);
        Element e = ret.getDocumentElement();
        Element removed = ret.createElement("removed");
        e.appendChild(removed);
        Element added = ret.createElement("added");
        e.appendChild(added);
        Element changed = ret.createElement("changed");
        e.appendChild(changed);
        i = onlyOld.iterator();
        while(i.hasNext()) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) oldClassInfo.get(s);
            if(criteria.validClass(ci)) format.formatClass(ret, removed, ci);
        }
        i = onlyNew.iterator();
        while(i.hasNext()) {
            String s = (String) i.next();
            ClassInfo ci = (ClassInfo) newClassInfo.get(s);
            if(criteria.validClass(ci)) format.formatClass(ret, added, ci);
        }
        i = both.iterator();
        while(i.hasNext()) {
            showClassChange(ret, changed, (String) i.next(), criteria, format);
        }
        return ret;
    }

    private void showClassChange(
            final Document doc, 
            final Element root, 
            final String name,
            final DiffCriteria criteria,
            final NodeFormat format)
    {
        Iterator i;
        ClassInfo oldInfo = (ClassInfo) oldClassInfo.get(name);
        ClassInfo newInfo = (ClassInfo) newClassInfo.get(name);
        boolean differs = false;
        if(!criteria.validClass(oldInfo) && !criteria.validClass(oldInfo)) {
            /* Neither class meets criteria, return */
            return;
        }
        Element classe = doc.createElement("class");
        classe.setAttribute("name",format.getClassName(name));
        Element removed = doc.createElement("removed");
        classe.appendChild(removed);
        Element added = doc.createElement("added");
        classe.appendChild(added);
        Element changed = doc.createElement("changed");
        classe.appendChild(changed);
        if(criteria.differs(oldInfo, newInfo)) {
            differs = true;
            Element classchange = doc.createElement("classchange");
            changed.appendChild(classchange);
            Element from = doc.createElement("from");
            classchange.appendChild(from);
            Element to = doc.createElement("to");
            classchange.appendChild(to);
            format.formatClass(doc,from,oldInfo);
            format.formatClass(doc,to,newInfo);
        }
        Set onlyOld = new TreeSet(oldInfo.getFieldMap().keySet());
        Set onlyNew = new TreeSet(newInfo.getFieldMap().keySet());
        Set both = new TreeSet(oldInfo.getFieldMap().keySet());
        onlyOld.removeAll(newInfo.getFieldMap().keySet());
        onlyNew.removeAll(oldInfo.getFieldMap().keySet());
        both.retainAll(newInfo.getFieldMap().keySet());
        i = onlyOld.iterator();
        while(i.hasNext()) {
            String sig = (String) i.next();
            FieldInfo fi = (FieldInfo) oldInfo.getFieldMap().get(sig);
            if(criteria.validField(fi)) {
                format.formatField(doc, removed, fi);
                differs = true;
            }
        }
        i = onlyNew.iterator();
        while(i.hasNext()) {
            String sig = (String) i.next();
            FieldInfo fi = (FieldInfo) newInfo.getFieldMap().get(sig);
            if(criteria.validField(fi)) {
                format.formatField(doc, added, fi);
                differs = true;
            }
        }
        i = both.iterator();
        while(i.hasNext()) {
            String sig = (String) i.next();
            FieldInfo oldFi = (FieldInfo) oldInfo.getFieldMap().get(sig);
            FieldInfo newFi = (FieldInfo) newInfo.getFieldMap().get(sig);
            if(criteria.validField(oldFi) || criteria.validField(newFi)) {
                if(criteria.differs(oldFi, newFi)) {
                    differs = true;
                    Element fieldchange = doc.createElement("fieldchange");
                    changed.appendChild(fieldchange);
                    Element from = doc.createElement("from");
                    fieldchange.appendChild(from);
                    Element to = doc.createElement("to");
                    fieldchange.appendChild(to);
                    format.formatField(doc, from, oldFi);
                    format.formatField(doc, to, newFi);
                }
            }
        }
        onlyOld = new TreeSet(oldInfo.getMethodMap().keySet());
        onlyNew = new TreeSet(newInfo.getMethodMap().keySet());
        both = new TreeSet(oldInfo.getMethodMap().keySet());
        onlyOld.removeAll(newInfo.getMethodMap().keySet());
        onlyNew.removeAll(oldInfo.getMethodMap().keySet());
        both.retainAll(newInfo.getMethodMap().keySet());
        i = onlyOld.iterator();
        while(i.hasNext()) {
            String sig = (String) i.next();
            MethodInfo mi = (MethodInfo) oldInfo.getMethodMap().get(sig);
            if(criteria.validMethod(mi)) {
                format.formatMethod(doc, removed, mi);
                differs = true;
            }
        }
        i = onlyNew.iterator();
        while(i.hasNext()) {
            String sig = (String) i.next();
            MethodInfo mi = (MethodInfo) newInfo.getMethodMap().get(sig);
            if(criteria.validMethod(mi)) {
                format.formatMethod(doc, added, mi);
                differs = true;
            }
        }
        i = both.iterator();
        while(i.hasNext()) {
            String sig = (String) i.next();
            MethodInfo oldMi = (MethodInfo) oldInfo.getMethodMap().get(sig);
            MethodInfo newMi = (MethodInfo) newInfo.getMethodMap().get(sig);
            if(criteria.validMethod(oldMi) || criteria.validMethod(newMi)) {
                if(criteria.differs(oldMi, newMi)) {
                    Element methodchange = doc.createElement("methodchange");
                    changed.appendChild(methodchange);
                    Element from = doc.createElement("from");
                    methodchange.appendChild(from);
                    Element to = doc.createElement("to");
                    methodchange.appendChild(to);
                    format.formatMethod(doc, from, oldMi);
                    format.formatMethod(doc, to, newMi);
                    differs = true;
                }
            }
        }
        if(differs) {
            root.appendChild(classe);
        }
    }


    public static class ClassInfo extends EmptyVisitor {
        private int version;
        private int access;
        private String name;
        private String signature;
        private String supername;
        private String[] interfaces;
        private Map methodMap = new HashMap();
        private Map fieldMap = new HashMap();

        public ClassInfo(ClassReader reader) {
            reader.accept(this,true);
        }

        public int getVersion() {
            return version;
        }

        public int getAccess() {
            return access;
        }

        public String getName() {
            return name;
        }

        public String getSignature() {
            return signature;
        }

        public String getSupername() {
            return supername;
        }

        public String[] getInterfaces() {
            return interfaces;
        }

        public Map getMethodMap() {
            return methodMap;
        }

        public Map getFieldMap() {
            return fieldMap;
        }

        public void visit(
                int version,
                int access,
                String name,
                String signature,
                String supername,
                String[] interfaces)
        {
            this.version = version;
            this.access = access;
            this.name = name;
            this.signature = signature;
            this.supername = supername;
            this.interfaces = interfaces;
        }

        public MethodVisitor visitMethod(
                int access, 
                String name, 
                String desc, 
                String signature, 
                String[] exceptions) 
        {
            methodMap.put(
                    name+desc,
                    new MethodInfo(
                        access, 
                        name, 
                        desc, 
                        signature, 
                        exceptions));
            return null;
        }

        public FieldVisitor visitField(
                int access,
                String name,
                String desc,
                String signature,
                Object value) 
        {
            fieldMap.put(
                    name,
                    new FieldInfo(
                        access,
                        name,
                        desc,
                        signature,
                        value)); 
            return this;
        }
    }

    public static class MethodInfo {
        private int access;
        private String name;
        private String desc;
        private String signature;
        private String[] exceptions;
        public MethodInfo(  
                int access,
                String name,
                String desc,
                String signature,
                String[] exceptions)
        {
            this.access = access;
            this.name = name;
            this.desc = desc;
            this.signature = signature;
            this.exceptions = exceptions;
        }
        
        public int getAccess() {
            return access;
        }
        
        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public String getSignature() {
            return signature;
        }

        public String[] getExceptions() {
            return exceptions;
        }
    }

    public static class FieldInfo {
        private int access;
        private String name;
        private String desc;
        private String signature;
        private Object value;
        public FieldInfo(
                int access,
                String name,
                String desc,
                String signature,
                Object value) 
        {
            this.access = access;
            this.name = name;
            this.desc = desc;
            this.signature = signature;
            this.value = value;
        }

        public int getAccess() {
            return access;
        }
        
        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public String getSignature() {
            return signature;
        }

        public Object getValue() {
            return value;
        }
    }

    public static interface DiffCriteria {
        public boolean validClass(ClassInfo info);
        public boolean validMethod(MethodInfo info);
        public boolean validField(FieldInfo info);
        public boolean differs(ClassInfo oldInfo, ClassInfo newInfo);
        public boolean differs(MethodInfo oldInfo, MethodInfo newInfo);
        public boolean differs(FieldInfo oldInfo, FieldInfo newInfo);
    }

    public static class SimpleDiffCriteria implements DiffCriteria {
        public boolean validClass(ClassInfo info) {
            /*
             * Public or Protected and NOT synthetic.
             */
            int access = info.getAccess();
            if( (access & Opcodes.ACC_SYNTHETIC) > 0) {
                return false;
            }
            if( (access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
                return false;
            }
            return true;
        }
        public boolean validMethod(MethodInfo info) {
            /*
             * Public or Protected and NOT synthetic.
             */
            int access = info.getAccess();
            if( (access & Opcodes.ACC_SYNTHETIC) > 0) {
                return false;
            }
            if( (access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
                return false;
            }
            return true;
        }
        public boolean validField(FieldInfo info) {
            /*
             * Public or Protected and NOT synthetic.
             */
            int access = info.getAccess();
            if( (access & Opcodes.ACC_SYNTHETIC) > 0) {
                return false;
            }
            if( (access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
                return false;
            }
            return true;
        }
        public boolean differs(ClassInfo oldInfo, ClassInfo newInfo) {
            if(oldInfo.getAccess() != newInfo.getAccess()) {
                return true;
            }
            if(!oldInfo.getSupername().equals(newInfo.getSupername())) {
                return true;
            }
            Set oldInterfaces = 
                new HashSet(Arrays.asList(oldInfo.getInterfaces()));
            Set newInterfaces = 
                new HashSet(Arrays.asList(newInfo.getInterfaces()));
            if(!oldInterfaces.equals(newInterfaces)) {
                return true;
            }
            return false;
        }
        public boolean differs(MethodInfo oldInfo, MethodInfo newInfo) {
            if(oldInfo.getAccess() != newInfo.getAccess()) {
                return true;
            }
            if(
                    oldInfo.getExceptions() == null || 
                    newInfo.getExceptions() == null
              )
            {
                if(oldInfo.getExceptions() != newInfo.getExceptions()) {
                    return true;
                }
            } else {
                Set oldExceptions =
                    new HashSet(Arrays.asList(oldInfo.getExceptions()));
                Set newExceptions =
                    new HashSet(Arrays.asList(newInfo.getExceptions()));
                if(!oldExceptions.equals(newExceptions)) {
                    return true;
                }
            }
            return false;
        }
        public boolean differs(FieldInfo oldInfo, FieldInfo newInfo) {
            if(oldInfo.getAccess() != newInfo.getAccess()) {
                return true;
            }
            if(oldInfo.getValue() == null || newInfo.getValue() == null) {
                if(oldInfo.getValue() != newInfo.getValue()) return true;
            } else if(!oldInfo.getValue().equals(newInfo.getValue())) {
                return true;
            }
            return false;
        }
    }

    public static interface NodeFormat {
        public void formatClass(Document doc, Element e, ClassInfo info);
        public void formatMethod(Document doc, Element e, MethodInfo info);
        public void formatField(Document doc, Element e, FieldInfo info);
        public String getClassName(String internalName);
    }

    public static class SimpleNodeFormat implements NodeFormat {
        public void formatClass(Document doc, Element e, ClassInfo info) {
            /* Bit rough & raw atm */
            Element f = doc.createElement("class");
            addAccessFlags(f,info.getAccess());
            if(info.getName() != null) f.setAttribute("name",getClassName(info.getName()));
            if(info.getSignature() != null) f.setAttribute("signature",info.getSignature());
            if(info.getSupername() != null) f.setAttribute("supername",getClassName(info.getSupername()));
            for(int i=0;i<info.getInterfaces().length;i++) {
                Element g = doc.createElement("interface");
                g.setAttribute("name",getClassName(info.getInterfaces()[i]));
                f.appendChild(g);
            }
            e.appendChild(f);
        }

        public void formatMethod(Document doc, Element e, MethodInfo info) {
            /* Bit rough & raw atm */
            Element f = doc.createElement("method");
            addAccessFlags(f,info.getAccess());
            if(info.getName() != null) f.setAttribute("name",info.getName());
            if(info.getDesc() != null) addMethodNodes(doc,f,info.getDesc());
            if(info.getSignature() != null) f.setAttribute("signature",info.getSignature());
            if(info.getExceptions() != null) {
                for(int i=0;i<info.getExceptions().length;i++) {
                    Element g = doc.createElement("exception");
                    g.setAttribute("name",getClassName(info.getExceptions()[i]));
                    f.appendChild(g);
                }
            }
            e.appendChild(f);
        }

        public void formatField(Document doc, Element e, FieldInfo info) {
            /* Bit rough & raw atm */
            Element f = doc.createElement("field");
            addAccessFlags(f,info.getAccess());
            if(info.getName() != null) f.setAttribute("name",info.getName());
            if(info.getDesc() != null) addTypeNodes(doc,f,info.getDesc());
            if(info.getSignature() != null) f.setAttribute("signature",info.getSignature());
            if(info.getValue() != null) f.setAttribute("value",info.getValue().toString());
            e.appendChild(f);
        }
        
        /**
         * Get the class name for a given java internal name
         */
        public String getClassName(String internalName) {
            StringBuffer ret = new StringBuffer(internalName.length());
            for(int i=0;i<internalName.length();i++) {
                char ch = internalName.charAt(i);
                switch(ch) {
                    case '/':
                    case '$':
                        ret.append('.');
                        break;
                    default:
                        ret.append(ch);
                }
            }
            return ret.toString();
        }

        /**
         * Add a description of the method desc string to element e
         */
        protected void addMethodNodes(
                final Document doc, final Element e, final String desc) 
        {
            Type[] args = Type.getArgumentTypes(desc);
            Type ret = Type.getReturnType(desc);
            Element argsElement = doc.createElement("arguments");
            e.appendChild(argsElement);
            for(int i=0;i<args.length;i++) {
                addTypeNodes(doc, argsElement, args[i]);
            }
            Element retElement = doc.createElement("return");
            e.appendChild(retElement);
            addTypeNodes(doc,retElement,ret);
        }

        /**
         * Add a description of the field desc string to element e
         */
        protected void addTypeNodes(
                final Document doc, final Element e, final String desc) 
        {
            addTypeNodes(doc, e, Type.getType(desc));
        }

        /**
         * Add a description of the asm type to element e
         */
        protected void addTypeNodes(
                final Document doc, final Element e, final Type type) {
            int i = type.getSort();
            Element tmp = null;
            switch(i) {
                case Type.ARRAY:
                    tmp = doc.createElement("array");
                    tmp.setAttribute("dimensions",""+type.getDimensions());
                    addTypeNodes(doc, tmp, type.getElementType());
                    break;
                case Type.BOOLEAN:
                    tmp = doc.createElement("boolean");
                    break;
                case Type.BYTE:
                    tmp = doc.createElement("byte");
                    break;
                case Type.CHAR:
                    tmp = doc.createElement("char");
                    break;
                case Type.DOUBLE:
                    tmp = doc.createElement("double");
                    break;
                case Type.FLOAT:
                    tmp = doc.createElement("float");
                    break;
                case Type.INT:
                    tmp = doc.createElement("int");
                    break;
                case Type.LONG:
                    tmp = doc.createElement("long");
                    break;
                case Type.OBJECT:
                    tmp = doc.createElement("object");
                    tmp.setAttribute("class",getClassName(type.getInternalName()));
                    break;
                case Type.SHORT:
                    tmp = doc.createElement("short");
                    break;
                case Type.VOID:
                    tmp = doc.createElement("void");
                    break;
            }
            e.appendChild(tmp);
        }
        /**
         * Add the access flags to the element as attributes
         */
        protected void addAccessFlags(Element e, int access) {
            String accType;
            if((access & Opcodes.ACC_PUBLIC) != 0) {
                accType = "public";
            } else if((access & Opcodes.ACC_PROTECTED) != 0) {
                accType = "protected";
            } else if((access & Opcodes.ACC_PRIVATE) != 0) {
                accType = "private";
            } else {
                accType = "package";
            }
            e.setAttribute("access",accType);
            if((access & Opcodes.ACC_ABSTRACT) != 0) {
                e.setAttribute("abstract","yes");
            }
            if((access & Opcodes.ACC_ANNOTATION) != 0) {
                e.setAttribute("annotation","yes");
            }
            if((access & Opcodes.ACC_BRIDGE) != 0) {
                e.setAttribute("bridge","yes");
            }
            if((access & Opcodes.ACC_DEPRECATED) != 0) {
                e.setAttribute("deprecated","yes");
            }
            if((access & Opcodes.ACC_ENUM) != 0) {
                e.setAttribute("enum","yes");
            }
            if((access & Opcodes.ACC_FINAL) != 0) {
                e.setAttribute("final","yes");
            }
            if((access & Opcodes.ACC_INTERFACE) != 0) {
                e.setAttribute("interface","yes");
            }
            if((access & Opcodes.ACC_NATIVE) != 0) {
                e.setAttribute("native","yes");
            }
            if((access & Opcodes.ACC_STATIC) != 0) {
                e.setAttribute("static","yes");
            }
            if((access & Opcodes.ACC_STRICT) != 0) {
                e.setAttribute("strict","yes");
            }
            if((access & Opcodes.ACC_SUPER) != 0) {
                e.setAttribute("super","yes");
            }
            if((access & Opcodes.ACC_SYNCHRONIZED) != 0) {
                e.setAttribute("synchronized","yes");
            }
            if((access & Opcodes.ACC_SYNTHETIC) != 0) {
                e.setAttribute("synthetic","yes");
            }
            if((access & Opcodes.ACC_TRANSIENT) != 0) {
                e.setAttribute("transient","yes");
            }
            if((access & Opcodes.ACC_VARARGS) != 0) {
                e.setAttribute("varargs","yes");
            }
            if((access & Opcodes.ACC_VOLATILE) != 0) {
                e.setAttribute("volatile","yes");
            }
        }
    }
}
