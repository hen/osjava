/* Tools - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.osjava.scripts4java;

public final class Tools
{
    private Tools() {
	/* empty */
    }
    
    public static final String getClassName(String internalName) {
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
