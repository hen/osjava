/*
 * org.osjava.threads.AbstractExtendedRunnable
 *
 * $Id$
 * $Rev$
 * $Date$
 * $Author$
 *
 * Created on Aug 01, 2002
 *
 * Copyright (c) 2002-2004, Robert M. Zigweid
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

import org.apache.log4j.Logger;

/**
 * Abstract implementation of {@link ExtendedRunnable} which handles the default
 * methods.
 *
 * @author Robert M. Zigweid
 * @version $Revision$ $Date$
 *
 */
public abstract class AbstractExtendedRunnable
    implements ExtendedRunnable {
    /** 
     * boolean field containing the value of whether to abort or not.
     */
    private boolean abort = false;

    /**
     * Defines an AbstractExtendedRunnable.
     */
    public AbstractExtendedRunnable() {
        super();
    }

    /**
     * Sets the value to determine whether or not the thread should abort.  If 
     * the <code>abort</code>is true, the thread is to begin the abort 
     * sequence.  If it is false, the abort sequence is to be stopped and
     * normal functionality resumed.  It may not be possible to stop an abort
     * sequence for the thread.
     * 
     * @param abort a boolean value indicating whether or not the thread is to 
     *              abort its execution
     * 
     * @see org.osjava.threads.ExtendedRunnable#setAbort(boolean)
     */
    public void setAbort(boolean abort) {
        this.abort = abort;
    }

    /** 
     * Gets the current status of whether or not the {@link ExtendedRunnable}
     * is supposed to abort.
     * 
     * @return boolean value indicating whether or not the ExtendedRunnable
     *         is to cease execution.
     */
    public boolean isAborting() {
        return this.abort;
    }

    /**
     * Runs the thread.  <code>run()</code> is intended to be overloaded.
     * 
     * @see java.lang.Runnable#run()
     */
    public abstract void run();

    /**
     * Prevents the cloneing of an ExtendedRunnable
     * 
     * @return nothing.  This method will never return an object
     * @throws CloneNotSupportedException to indicate that this 
     */
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException(
            "An AbstractExtendedRunnable should not be cloned.");
    }

    public void wakeup() {
        System.out.println("Testing -- AbstractRunnable");
        synchronized(this) {
            this.notify();
        }
    }
}