/* 
 * org.osjava.threads.ExtendedThread
 *
 * $Id$
 * $Rev$
 * $Date$
 * $Author$
 *
 * Created on Aug 01, 2002
 *
 * Copyright (c) 2002-2003, Robert M. Zigweid
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

/**
 * A thread implementation that adds additional accessor API for easier 
 * management.
 * <p>
 * Specifically this implmentation of {@link Thread} adds accessability to the 
 * Runnable which standard Java {@link Thread}s do not.  
 * <p>
 * @author Robert M. Zigweid
 * @version $Rev$ $Date$
 */
public class ExtendedThread extends Thread implements ExtendedRunnable {
    /** 
     * Field containing the {@link Runnable} object 
     */
    private Runnable runnable;

    /** 
     * Field which determines whether or not the {@link Thread} has been 
     * started.
     */
    private boolean started = false;

    /**
     * Creates an ExtendedThread with a specified {@link ExtendedRunnable}
     * as it's run target, and having a name that is automatically generated 
     * in the same fashion as java.lang.Thread.
     * <p>
     * This constructor has the same effect as calling <code>
     * ExtendedThread(null, target)</code>
     * 
     * @param target the Runnable target for the thread.
     */
    protected ExtendedThread(Runnable target) {
        this(target, null);
    }

    /**
     * Creates an ExtendedThread with a specified {@link Runnable}
     * as it's run target, and having the name of <code>name</code>.
     * <p>
     * This constructor has the same effect as calling <code>
     * ExtendedThreadGroup(null, target, name, 0)</code>
     * 
     * @param target the Runnable target for the thread.
     * @param name   the name of the thread.
     */
    public ExtendedThread(Runnable target, String name) {
        super(target, name);
    }

    /**
     * Creates an ExtendedThread with the name <code>name</code>.
     * <p>
     * This constructor has the same effect as calling <code>
     * ExtendedThreadGroup(null, null, name, 0)</code>
     * 
     * @param name the name of the thread.
     */
    protected ExtendedThread(String name) {
        this(null, name);
    }

    /**
     * Creates an ExtendedThread with itself as its run target, and having 
     * the name that is automatically generated in the same fashion as 
     * java.lang.Thread
     * <p>
     * This constructor has the same effect as calling <code>
     * ExtendedThreadGroup(null, null, null, 0)</code>
     */
    protected ExtendedThread() {
        this(null,null);
    }

    /** 
     * Sets the runnable object for the thread.  An 
     * {@link ExtendedRunnable} parameter is accepted..  
     * 
     * @param runnable the ExtendedRunnable object which is to be 
     *                 utilized ExtendedThread.
     */
    public void setRunnable(Runnable runnable) {
        /* TODO: A check needs to be added so that the runnable cannot be 
         * set after the thread is started. */
        this.runnable = runnable;
    }

    /** 
     * Gets the {@link ExtendedRunnable} object for this thread.
     * 
     * @return The ExtendedRunnable object that this ExtendedThread
     * is utilizing.
     */
    public Runnable getRunnable() {
        return runnable;
    }
    
    /**
     * Indicates whether or not the thread has at any time been
     * tarted.  Returning true indicates that the ExtendedThread has at some 
     * point {@link ExtendedThread#start()} has been called.
     * 
     * @return A boolean value 
     */
    public boolean isStarted() {
        return started;
    }

    /** 
     * Starts the thread's execution.  This is a wrapper around the 
     * {@link java.lang.Thread#start()} method, but also sets the value used
     * by {@link #isStarted()} so that it will return true.
     */
    public void start() {
        started = true;
        super.start();
    }

    /**
     * Instructs the threads runnable object to eithr abort or cancel the 
     * abort sequence.  if <code>abort</code> is true, the abort sequence is
     * started in the {@link ExtendedRunnable} object found by <code>
     * getRunnable()</code>.  If <code>abort</code> is false, the aborting 
     * squence is stopped, if it had been started.  There is no guarantee that 
     * the aborting can be stopped once started.  If the Runnable object does
     * not support aborting, this method does nothing.
     * 
     * @param abort boolean value determining whether or not the thread is to 
     *              be aborted, or can be set to halt a previously declared 
     *              abort
     */
    public void setAbort(boolean abort) {
        if(getRunnable() instanceof ExtendedRunnable) {
            ((ExtendedRunnable)getRunnable()).setAbort(abort);
        }
        /* Don't do anything if it's not supported. */
    }

    /**
     * Returns a boolean value indicating  whether or not the
     * ExtendedThread and its associated {@link Runnable} are in the 
     * process of aborting, or already have aborted.  Returning true indicates
     * that the thread has ceased execution, or begun the process of aborting.
     * If the Runnable of this ExtendedThread doesn't support aborting, false 
     * is always returned. 
     * 
     * @return A boolean value indicating whether the thread has aborted or 
     *         not. 
     */
    public boolean isAborting() {
        if(getRunnable() instanceof ExtendedRunnable) {
            return ((ExtendedRunnable)getRunnable()).isAborting();
        }
        /* Return false if the thread can't abort */
        return false;
    }
    
    /**
     * Prevents the cloneing of an ExtendedThread
     * 
     * @return nothing.  This method will never return an object
     * @throws CloneNotSupportedException to indicate that this 
     */
    protected Object clone() throws CloneNotSupportedException {
        throw new  CloneNotSupportedException("ExtendedThread cannot be cloned.");
    }
}
