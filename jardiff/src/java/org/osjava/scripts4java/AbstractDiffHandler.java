package org.osjava.scripts4java;

public abstract class AbstractDiffHandler implements DiffHandler
{
    protected final String getClassName(String internalName) {
	StringBuffer ret = new StringBuffer(internalName.length());
	for (int i = 0; i < internalName.length(); i++) {
	    char ch = internalName.charAt(i);
	    switch (ch) {
	    case '$':
	    case '/':
		ret.append('.');
		break;
	    default:
		ret.append(ch);
	    }
	}
	return ret.toString();
    }
}
