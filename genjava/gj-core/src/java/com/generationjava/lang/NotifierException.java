/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Genjava-Core nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
package com.generationjava.lang;

/**
 * <p>Exception thrown when something goes wrong in notifying.</p>
 *
 * @author <a href="mailto:bayard@apache.org">Henri Yandell</a>
 * @since 2.0
 * @version $Id$
 */
public class NotifierException extends Exception {

    /**
     * <p>Constructs a new <code>NotifierException</code> without specified
     * detail message.</p>
     */
    public NotifierException() {
        super();
    }

    /**
     * <p>Constructs a new <code>NotifierException</code> with specified
     * detail message.</p>
     *
     * @param msg  the error message.
     */
    public NotifierException(String msg) {
        super(msg);
    }

    /**
     * <p>Constructs a new <code>NotifierException</code> with specified
     * nested <code>Throwable</code> root cause.</p>
     *
     * @param rootCause  the <code>Exception</code> or <code>Error</code> that
     *  caused this exception to be thrown.
     */
    public NotifierException(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * <p>Constructs a new <code>NotifierException</code> with specified
     * detail message and nested <code>Throwable</code> root cause.</p>
     *
     * @param msg        the error message.
     * @param rootCause  the <code>Exception</code> or <code>Error</code> that
     * caused this exception to be thrown.
     */
    public NotifierException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

}

