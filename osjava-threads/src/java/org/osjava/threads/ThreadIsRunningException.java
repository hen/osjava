/*
 * org.osjava.threads.ThreadIsRunningException
 * 
 * $Id$
 * $URL:$
 * $Rev$
 * $Date$
 * $Author:$
 * 
 * Created on Sep 28, 2004.
 * Copyright (c) 2004, Robert M. Zigweid 
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
 * + Neither the name of the OSJava-Threads nor the names of its contributors may
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

import javax.naming.NamingException;

/**
 * Exception intended to be thrown if an {@link ExtendedRunnable} is being 
 * added to or removed from a ThreadContext.
 * 
 * @author Robert M. Zigweid
 * @version $Rev$ $Date$
 * @since OSJava-Threads 2.0
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ThreadIsRunningException extends NamingException {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new ThreadIsRunningException with the default reason.
     * @see java.lang.Exception#Exception()
     */
    public ThreadIsRunningException() {
        super("Thread is running.");
    }

    /**
     * Creates a new ThreadIsRunningException with the supplied message.
     * 
     * @param message the reason that will be reported when the exception is 
     *        thrown.
     * @see java.lang.Exception#Exception(java.lang.String)
     */
    public ThreadIsRunningException(String message) {
        super(message);
    }
}
