/* AbstractDiffHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
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
    
    /*synthetic*/ public abstract void endDiff() throws DiffException;
    
    /*synthetic*/ public abstract void endChanged() throws DiffException;
    
    /*synthetic*/ public abstract void endClassChanged() throws DiffException;
    
    /*synthetic*/ public abstract void fieldChanged
	(FieldInfo fieldinfo, FieldInfo fieldinfo_0_) throws DiffException;
    
    /*synthetic*/ public abstract void fieldAdded(FieldInfo fieldinfo)
	throws DiffException;
    
    /*synthetic*/ public abstract void fieldRemoved(FieldInfo fieldinfo)
	throws DiffException;
    
    /*synthetic*/ public abstract void methodChanged
	(MethodInfo methodinfo, MethodInfo methodinfo_1_) throws DiffException;
    
    /*synthetic*/ public abstract void methodAdded(MethodInfo methodinfo)
	throws DiffException;
    
    /*synthetic*/ public abstract void methodRemoved(MethodInfo methodinfo)
	throws DiffException;
    
    /*synthetic*/ public abstract void startClassChanged
	(ClassInfo classinfo, ClassInfo classinfo_2_) throws DiffException;
    
    /*synthetic*/ public abstract void startChanged() throws DiffException;
    
    /*synthetic*/ public abstract void endAdded() throws DiffException;
    
    /*synthetic*/ public abstract void classAdded(ClassInfo classinfo)
	throws DiffException;
    
    /*synthetic*/ public abstract void startAdded() throws DiffException;
    
    /*synthetic*/ public abstract void endRemoved() throws DiffException;
    
    /*synthetic*/ public abstract void classRemoved(ClassInfo classinfo)
	throws DiffException;
    
    /*synthetic*/ public abstract void startRemoved() throws DiffException;
    
    /*synthetic*/ public abstract void startDiff
	(String string, String string_3_) throws DiffException;
}
