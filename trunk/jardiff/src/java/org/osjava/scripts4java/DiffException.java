/* DiffException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.osjava.scripts4java;

public class DiffException extends Exception
{
    public DiffException(Exception toWrap) {
	super((Throwable) toWrap);
    }
}
