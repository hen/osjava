package org.osjava.scripts4java;

public final class MethodInfo extends AbstractInfo
{
    private String desc;
    private String signature;
    private String[] exceptions;
    
    public MethodInfo(int access, String name, String desc, String signature,
		      String[] exceptions) {
	super(access, name);
	this.desc = desc;
	this.signature = signature;
	this.exceptions = exceptions;
    }
    
    public final String getDesc() {
	return desc;
    }
    
    public final String getSignature() {
	return signature;
    }
    
    public final String[] getExceptions() {
	return exceptions;
    }
}
