/*
 * org.osjava.threads.ThreadNamingEnumeration
 * 
 * $Id$
 * $LastChangedRevision$
 * $LastChangedDate$
 * $LastChangedBy$
 * $Author$
 * 
 * Created on Apr 6, 2004
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

import java.util.Iterator;
import java.util.Collection;
import java.util.NoSuchElementException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * <Insert Type Description here>
 * 
 * @author Robert M. Zigweid
 * @version $LastChangedRevision $ $LastChangedDate$
 */
public class ThreadNamingEnumeration
    implements NamingEnumeration {
    
    private Iterator iterator = null;
    
    /**
     * Create a new ThreadNamingEnumeration with <code>source</code> as its datasource.
     * 
     * @param source a Map which is the datasource.
     */
    public ThreadNamingEnumeration(Collection source) {
        iterator = source.iterator();
    }
    
    /**
     * Returns the next object in the enumeration
     * 
     * @throws NamingException if a naming exception is encoutnered while attempting to retrieve the next element.
     * @throws NoSuchElementException if there is not a next element available.
     * @see NamingEnumeration#next()
     */
    public Object next() throws NamingException {
        if(iterator != null && 
           iterator.hasNext()) {
            return iterator.next();
        }
        throw new NoSuchElementException();
    }

    /**
     * Determines whether there are anymore elements in the enumeration
     * 
     * @throws NamingException if a naming exception is encountered while attempting to determine if there is a next element.
     * @see NamingEnumeration#hasMore() 
     */
    public boolean hasMore() throws NamingException {
        if(iterator != null) {
            return iterator.hasNext();
        }
        return false;
    }

    /** 
     * Closes the enumeration.  Any calls to methods in the enumeration after it is closed is undefined.
     * 
     * @throws NamingException if an exception is encountered while closing the enumeration.
     * @see NamingEnumeration#close()
     */
    public void close() throws NamingException {
        iterator = null;
    }

    /**
     * Returns true if there are more elements in the enumeration.
     * 
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements() {
        if(iterator != null) {
            return iterator.hasNext();
        }
        return false;
    }

    /**
     * Returns the next element of the enumeration
     * 
     * @throws NoSuchElementException if no more elements exist.
     * @see java.util.Enumeration#nextElement()
     */
    public Object nextElement() {
        if(iterator == null ||
           !iterator.hasNext()) {
            throw new NoSuchElementException();
        }
        return iterator.next();
    }

}
