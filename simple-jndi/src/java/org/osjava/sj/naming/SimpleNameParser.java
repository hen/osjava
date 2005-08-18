/*
 * org.osjava.threads.ThreadNameParser
 *
 * $URL$
 * $Id$
 * $Rev$
 * $Date$
 * $Author$
 *
 * Created on Mar 24, 2004 by Robert M. Zigweid
 */
package org.osjava.naming;

import java.util.Properties;

import javax.naming.CompoundName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

/**
 * The NameParser for the Simple-JNDI.  
 * 
 * @author Robert M. Zigweid
 * @version $LastChangedRevision $ $LastChangedDate$
 * @since OSJava Threads 2.0
 */
public class SimpleNameParser implements NameParser {
    
    /*
     * The parent Context.  This is necessary for aquiring relevant data, like 
     * Properties that are used.
     */
    private Context parent = null;
    
    /*
     * The properties utilized by the SimpleNameParser when constructing new
     * names.
     */
    private Properties props = new Properties();
    
    /**
     * Creates a ThreadNameParser.  Any relevant information that is needed, 
     * such as the environment that is passed to {@link CompoundName CompundName}
     * objects that are created.
     * 
     * @param parent ThreadContext that utilizes the name parser.
     * @throws NamingException if a naming exception is found.
     */
    public SimpleNameParser(Context parent) throws NamingException {
        this.parent = parent;
        /* Properties from the parent context are in a HashTable. */
        props.putAll(this.parent.getEnvironment());
    }

    /** 
     * Parses a name into its components.<br/>
     * (Copied from {@link javax.naming.NameParser#parse(java.lang.String)}
     * 
     * @param name The non-null string name to parse.
     * @return A non-null parsed form of the name using the naming convention
     *         of this parser.
     * @throws InvalidNameException If the name does not conform to syntax 
     *         defined for the namespace.
     * @throws NamingException If a naming exception was encountered.
     */
    public Name parse(String name) 
        throws InvalidNameException, NamingException {
        if(name == null) {
            name = "";
        }
        Name ret = new CompoundName(name, props);
        return ret;
    }
    
    /**
     * Determine whether or not <code>ob</code> is equal to this object.
     * If the ob is an instance of ThreadNameParser, it is considered to be 
     * equal.
     * <br/><br/>
     * <b>NOTE:</b> The above assumption may actually be false under two
     * circomstances.   Firstly, if the properties utilized by the contexts
     * are different.  Secondly, if the ThreadNameParser is subclassed.
     * 
     * @param ob the objct which is being compared to this one.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object ob) {
        if(ob instanceof SimpleNameParser) {
            return true;
        }
        return false;
    }    
}
