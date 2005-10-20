package org.osjava.scripts4java;

public final class FieldInfo extends AbstractInfo
{
    private String desc;
    private String signature;
    private Object value;
    
    public FieldInfo(int access, String name, String desc, String signature,
		     Object value) {
	super(access, name);
	this.desc = desc;
	this.signature = signature;
	this.value = value;
    }
    
    public final String getDesc() {
	return desc;
    }
    
    public final String getSignature() {
	return signature;
    }
    
    public final Object getValue() {
	return value;
    }
}
