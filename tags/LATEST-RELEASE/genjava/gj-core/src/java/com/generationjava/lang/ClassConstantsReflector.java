/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Genjava-Core nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
// hacked out of org.cyberiantiger's lovely class object model
package com.generationjava.lang;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.io.*;

/**
 * A cut down copy of org.cyberiantiger's class object model.
 * It provides a List of all the classes that a class file immediately
 * depends on.
 */
public class ClassConstantsReflector {

	static public void main(String[] args) throws Throwable {
		ClassConstantsReflector c = new ClassConstantsReflector();
        for(int i=0; i<args.length; i++) {
            System.err.println("Class=" +c.getFullyQualifiedDottedName(args[i]));
            System.err.println("Name=" + c.getName() + "; super=" + c.getSuperName());
            System.err.println(c.getClassConstants(args[i]));
            System.err.println("\n");
        }
    }

    static final public int MAGIC = 0xCAFEBABE;

	static final public byte CLASS = 7;
	static final public byte FIELD_REF = 9;
	static final public byte METHOD_REF = 10;
	static final public byte INTERFACE_METHOD_REF = 11;
	static final public byte STRING = 8;
	static final public byte INTEGER = 3;
	static final public byte FLOAT = 4;
	static final public byte LONG = 5;
	static final public byte DOUBLE = 6;
	static final public byte NAME_AND_TYPE = 12;
	static final public byte UTF8 = 1;

    private String name = null;

    public String getName() {
        return this.name;
    }

    public String getSuperName() {
        return ""+this.superClass;
    }

    public String getFullyQualifiedDottedName(String filename) throws IOException {
        String name = getFullyQualifiedName(filename);
        return StringUtils.replace(name, "/", ".");
    }
    public String getFullyQualifiedName(String filename) throws IOException {
        String name = null;

        try {
            DataInputStream in = new DataInputStream(new FileInputStream(filename));
            if(in.readInt() != MAGIC) {
                // not a .class file
                throw new IOException("Not a class file");
            }

            in.readUnsignedShort();  // minor version
            in.readUnsignedShort();  // major version
            int length = in.readUnsignedShort();  // length. of file?
            in.readByte(); // CLASS=7
            in.readUnsignedShort();  // some class value
            in.readByte(); // UTF8=1
            name = in.readUTF();
            in.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        return name;
    }
    public List getClassConstants(String classname) {
        this.name = classname.substring(0, classname.length()-6);
        this.name = this.name.replace('/','.');
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(classname));
            readFrom(in);
            resolve();
            return getDependencies();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return new ArrayList();
	}

    List getDependencies() {
        ArrayList list = new ArrayList();
        Iterator iterator = constantPool.iterator();
        while(iterator.hasNext()) {
            Object obj = iterator.next();
            if(obj instanceof C7) {
                list.add(obj.toString());
            }
        }
        return list;
    }

	// Class data
	int minorVersion;
	int majorVersion;
	ArrayList constantPool;
	int accessFlags;
	int this_class;
	Object thisClass;
	int super_class;
	Object superClass;

    // Read the byte codes and parse/decode into constantPool
	public void readFrom(DataInputStream in) throws IOException {
		if(in.readInt() != MAGIC) throw new IOException("Bad Magic Number");
		minorVersion = in.readUnsignedShort();
		majorVersion = in.readUnsignedShort();
		// check version number
		if(!(majorVersion == 45 || (majorVersion == 46 && minorVersion == 0))){
			throw new IOException("Unsupported version number");
		}
		int length = in.readUnsignedShort();
		constantPool = new ArrayList(length);
		// Hack
		constantPool.add(null);

		for(int i=1; i<length; i++) {
            int readByte = in.readByte();
			switch(readByte) {
			  case CLASS:
			    C7 c7 = new C7();
			    c7.readFrom(in);
			    constantPool.add(c7);
			    break;
			  case UTF8:
                C1 c1 = new C1();
			    c1.readFrom(in);
			    constantPool.add(c1);
			    break;

                // we ignore all the other possible constants.
                // though we do read the necessary bytes
			  case FIELD_REF:
                in.readUnsignedShort();
                in.readUnsignedShort();
			    constantPool.add(null);
			    break;
			  case METHOD_REF:
                in.readUnsignedShort();
                in.readUnsignedShort();
			    constantPool.add(null);
			    break;
			  case INTERFACE_METHOD_REF:
                in.readUnsignedShort();
                in.readUnsignedShort();
			    constantPool.add(null);
			    break;
			  case STRING:
                in.readUnsignedShort();
			    constantPool.add(null);
			    break;
			  case INTEGER:
                in.readInt();
			    constantPool.add(null);
			    break;
			  case FLOAT:
                in.readFloat();
			    constantPool.add(null);
			    break;
			  case LONG:
                i++;
                in.readLong();
			    constantPool.add(null);
			    constantPool.add(null);
			    break;
			  case DOUBLE:
                i++;
                in.readDouble();
			    constantPool.add(null);
			    constantPool.add(null);
			    break;
			  case NAME_AND_TYPE:
                in.readUnsignedShort();
                in.readUnsignedShort();
			    constantPool.add(null);
			    break;
			  default:
				throw new IOException("Invalid Constant Found " + readByte +
                                      " at " + i + " of " + length);
			}
		}
        // various bits of class info
        accessFlags = in.readUnsignedShort();
        this_class = in.readUnsignedShort();
        super_class = in.readUnsignedShort(); 

        // STOP HERE. Class has more data, we don't care.
        in.close();
	}

	public void resolve() {
		Iterator i = constantPool.iterator();
		// skip first value coz it's null
		i.next();

        Object obj;
		while(i.hasNext()) {
			obj = i.next();
            if(obj instanceof C7) {
                ((C7)obj).resolve();
            }
		}

		if(this_class == 0 || this_class >= constantPool.size() ||
		   super_class == 0 || super_class >= constantPool.size() ) {
			throw new RuntimeException("Invalid Constant Pool Reference");
		}

		Object ob = constantPool.get(this_class);
		if(!(ob instanceof C7) ) {
			throw new RuntimeException("Wrong type of object at reference in constant pool");
		}
		thisClass = (C7) ob;
		ob = constantPool.get(super_class);
		if(!(ob instanceof C7) ) {
			throw new RuntimeException("Wrong type of object at reference in constant pool");
		}
		superClass = (C7) ob;
	}

	class C1 {
		String value;
		C1() {}
		C1(String value) {
			this.value = value;
		}
		public byte getType() { return UTF8; }
		public String getValue() { return value; }
		public void readFrom(DataInputStream in) throws IOException {
			value = in.readUTF();
		}
		public boolean equals(Object obj) {
			return (obj instanceof C1) && ((C1)obj).value.equals(value);
		}
		public int hashCode() { return value.hashCode(); }
		public String toString() { return value; }
	}

	class C7 {
		int index;
		C1 name;
		public byte getType() { return CLASS; }
		public C1 getClassName() { return name; }
		public void readFrom(DataInputStream in) throws IOException {
			index = in.readUnsignedShort();
		}
		public void resolve() {
			if(index == 0) {
				throw new RuntimeException("Invalid Constant Pool Reference: "+index);
			}
            if(index >= constantPool.size()) {
				throw new RuntimeException("Invalid Constant Pool Reference: "+index+"/"+constantPool.size());
			}
			Object ob = constantPool.get(index);
			if( !(ob instanceof C1) ) {
				throw new RuntimeException("Wrong type of object at reference in constant pool");
			}
			name = (C1) ob;
		}
		public boolean equals(Object obj) {
			return (obj instanceof C7) && ((C7)obj).name.equals(name);
		}
		public int hashCode() { return name.hashCode(); }
		public String toString() { return name.toString().replace('/','.');}
	}

}
