/*
 * org.osjava.threads.ThreadNameParser
 * 
 * $Id$
 * $LastChangedRevision$
 * $LastChangedDate$
 * $LastChangedBy$
 * $Author$
 * 
 * Created on Mar 24, 2004
 * Copyright (c) 2004, Robert M. Zigweid
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer. 
 * 
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution. 
 * 
 * + Neither the name of the TigThreads nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without 
 *   specific prior written permission.
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
package org.osjava.threads;

import java.util.Properties;

import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;

/**
 * The NameParser for the ThreadContext.  
 * 
 * @author Robert M. Zigweid
 * @version $LastChangedRevision $ $LastChangedDate$
 * @since OSJava Threads 2.0
 */
public class ThreadNameParser implements NameParser {
    
    /**
     * The parent Context.  This is necessary for aquiring relevant data, like 
     * Properties that are used.
     */
    private ThreadContext parent = null;
    
    /**
     * Creates a ThreadNameParser.  Any relevant information that is needed, 
     * such as the environment that is passed to {@link CompoundName CompundName}
     * objects that are created.
     */
    public ThreadNameParser(ThreadContext parent) {
        this.parent = parent;
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
        /* Properties from the parent context are in a HashTable. */
        Properties props = new Properties();
        props.putAll(parent.getEnvironment());
        Name ret = new CompoundName(name, props);
        return ret;
    }

}
