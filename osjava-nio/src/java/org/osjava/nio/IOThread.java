/*
 * org.osjava.nio.IOThread
 *
 * $Id$
 * $URL$
 * $Rev$
 * $Date$
 * $Author$
 *
 * Copyright (c) 2003-2005, Anthony Riley, Robert M. Zigweid
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
 * + Neither the name of the OSJava-NIO nor the names of its contributors may
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

package org.osjava.nio;

import java.io.IOException;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import java.util.LinkedList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A Thread facilitating the use of non-blocking
 * {@link SelectableChannel SelectableChannels}.
 * This Thread can be plugged in with the OSJava-Threads ThreadContext
 * implementation.<p>
 *
 * For {@link Selector Selector's} that are registered with a Thread of
 * this class, it is undefined if the {@link SelectionKey SelectionKeys}
 * of the Selectors are modifified outside of the IOThread they are registered
 * with.  This is an unfortunate side effect of synchronization issues.  While
 * a Selector and a SelectionKey are both threadsafe, the key sets of a
 * Selector are not.  Instead the {@link #addInterestOp} and 
 * {@link #removeInterestOp} methods are provided for this synchronization.
 *
 * @author Anthony Riley and Robert M. Zigweid
 * @version $Rev$ $Date$
 */
public class IOThread extends Thread {

    private Selector mySelector;


    /**
     * List of Tasks to perform with this thread
     */
    private List tasks = new LinkedList();

    /**
     * Field containing the boolean value which indicates whether or not the
     * thread should abort.  This field exists for interoperability with
     * OSJava-Threads ExtendedRunnable class.
     */
    private volatile boolean abort=false;

    /**
     * Create a new IOThread.
     *
     * @throws IOException If there is a problem creating the Selector
     */
    public IOThread() throws IOException {
        super();
        mySelector = Selector.open();
    }

    /**
     * Create a new IOThread.
     *
     * @param name The name of the thread.
     * @throws IOException If there is a problem creating the Selector
     */
    public IOThread(String name)
        throws IOException {
            super(name);
            mySelector = Selector.open();
        }

    /**
     * Create a new IOThread.
     *
     * @param group the Threadgroup which the created Thread is a member
     * @param name The name of the thread.
     * @throws IOException If there is a problem creating the Selector
     */
    public IOThread(ThreadGroup group, String name)
        throws IOException {
            super(group, name);
            mySelector = Selector.open();
        }

    public void queueTask(Runnable task) {
        synchronized(tasks) {
            tasks.add(task);
        }
        /*
         * XXX: There are possible timing issues with this
         */
        mySelector.wakeup();
    }

    /**
     * Add an interestOp to the specified <code>key</code>.  This will
     * queue the op for addition in the next cycle of the thread.
     * Modification of keys outside of this mechanism is not recommended.
     * Adding the op here, does not guarantee that the op will end up
     * getting added to the key when the thread cycles.
     *
     * @param handler the handler who's SelectionKey will have the interestOp
     *        added.
     * @param op the operation to add.
     */
    public void addInterestOp(ChannelHandler handler, int op) {
        if(Thread.currentThread() != this) {
            /* Would have used IllegalAccessException, but it isn't a runtime
             * exception. This needs to be a runtime, because there's no
             * reason the caller should have to worry about catching it
             */
            throw new SecurityException("Can't be accessed with this thread");
        }
        SelectionKey key = handler.getSelectableChannel().keyFor(mySelector);
        Logger logger = Logger.getLogger(this.getClass());
        logger.debug("Adding to key '" + key + "' op '" + op + "'");

        /* 
         * Null check necessary because of tasks.  The key might have been 
         * destroyed while the task was queued.
         */
        if(key != null && key.isValid()) {
            key.interestOps(key.interestOps() | op);
        } else {
            logger.debug("invalid key");
        }
    }

    /**
     * Remove an interestOp from the specified <code>key</code>.  This will
     * queue the op for removal in the next cycle of the thread.
     * Modification of keys outside of this mechanism is not recommended.
     * Removing the op here, does not guarantee that the op will end up
     * getting added to the key when the thread cycles.
     *
     * @param handler the handler who's key will have the interestOp removed.
     * @param op the operation to remove.
     */
    public void removeInterestOp(ChannelHandler handler, int op) {
        if(Thread.currentThread() != this) {
            /* Would have used IllegalAccessException, but it isn't a runtime
             * exception. This needs to be a runtime, because there's no
             * reason the caller should have to worry about catching it
             */
            throw new SecurityException("Can't be accessed with this thread");
        }
        SelectionKey key = handler.getSelectableChannel().keyFor(mySelector);
        Logger logger = Logger.getLogger(this.getClass());
        logger.debug("Removing from key '" + key + "' op '" + op + "'");

        /* 
         * Null check necessary because of tasks.  The key might have been 
         * destroyed while the task was queued.
         */
        if(key != null && key.isValid()) {
            key.interestOps(key.interestOps() & ~op);
        } else {
            logger.debug("invalid key");
        }
    }

    /**
     * Register a {@link ChannelHandler} with the IOThread.  This serves to
     * make the Thread's {@link Selector} aware of the {@link SelectionKey}
     * the channel is associated with.  Handlers from blocking
     * {@link SelectableChannel SelectableChannel's} cannot be registered.
     *
     * @param handler the ChannelHandler for the Selectable Channel
     * @param ops the initial ops that the returned {@link SelectionKey} is
     *        to be interested in.
     * @throws ClosedChannelException if the underlying channel is closed.
     * @throws IllegalStateException if the underlying channel is a blocking
     *         channel.
     */
    public void register(ChannelHandler handler, int ops) throws ClosedChannelException, IllegalStateException 
    {
        if(Thread.currentThread() != this) {
            /* Would have used IllegalAccessException, but it isn't a runtime
             * exception. This needs to be a runtime, because there's no
             * reason the caller should have to worry about catching it
             */
            throw new SecurityException("Can't be accessed with this thread");
        }
        SelectableChannel chan = handler.getSelectableChannel();

        if (chan.isBlocking()) {
            throw new IllegalStateException("SelectableChannel is blocking");
        }

        SelectionKey key = chan.register(mySelector, ops, handler);
    }

    /**
     * Deregisters a {@link ChannelHandler} <code>handler</code> from the thread.
     * After deregistering, changes in the state of the thread's {@link
     * SelectionKey} will be ignored.
     *
     * @param handler the ChannelHandler to deregister.
     */
    public void deregister(ChannelHandler handler) {
        if(Thread.currentThread() != this) {
            /* Would have used IllegalAccessException, but it isn't a runtime
             * exception. This needs to be a runtime, because there's no
             * reason the caller should have to worry about catching it
             */
            throw new SecurityException("Can't be accessed with this thread");
        }
        SelectionKey key = handler.getSelectableChannel().keyFor(mySelector);
        /* 
         * Null check necessary because of tasks.  The key might have been 
         * destroyed while the task was queued.
         */
        if(key != null) {
            key.cancel();
        }
    }

    /**
     * The looping run method of the class.  All Threads have this.
     */
    public void run() {
        Logger logger = Logger.getLogger(this.getClass());
        while (!isAborting()) {
            try {
                do {
                    synchronized(tasks) {
                        while(tasks.size() > 0) {
                            Runnable r = (Runnable) tasks.remove(0);
                            synchronized(r) {
                                r.run();
                                r.notifyAll();
                            } 
                        }
                    }
                } while(mySelector.select() == 0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set keys = mySelector.selectedKeys();
            Iterator i = keys.iterator();
            while (i.hasNext()) {
                SelectionKey key = (SelectionKey)i.next();
                ChannelHandler handler = (ChannelHandler)key.attachment();
                if (key.isValid()) {
                    int ops = key.readyOps();
                    if ((ops & SelectionKey.OP_ACCEPT) != 0) {
                        try {
                            handler.accept();
                        } catch (ClosedChannelException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if ((ops & SelectionKey.OP_CONNECT) != 0) {
                        try {
                            handler.connect();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if ((ops & SelectionKey.OP_READ) != 0) {
                        try {
                            handler.readFromChannel();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if ((ops & SelectionKey.OP_WRITE) != 0) {
                        try {
                            handler.writeToChannel();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                i.remove();
            }
        }
        // XXX: This doesn't work !!!!
        // XXX: This is currently not tested (assumed to break hideously)
        // Cleanup
        // This is where we cleanup the mess left when we're interupted
        // TODO: Only close channels if 'closeOnExit' is set.
        Iterator i = mySelector.keys().iterator();
        while (i.hasNext()) {
            SelectionKey key = (SelectionKey)i.next();
            ChannelHandler handler = (ChannelHandler)key.attachment();
            if (key.isValid()) {
                try {
                    handler.close();
                    deregister(handler);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        try {
            mySelector.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method to set the thread to cease execution gracefully.  This method
     * exists for interoperability with ExtendedRunnable from the
     * OSJava-Threads package, but is in no way required.
     *
     * @param abort boolean value indicating whether or not the thread should
     *              cease execution.
     */
    public void setAbort(boolean abort) {
        this.abort=abort;
    }

    /**
     * Method returning whether or not the thread is in the process of ceasing
     * to run.  This method exists for interoperability with ExtendedRunnable
     * from the OSJava-Threads package, but is in no way required.
     *
     * @return boolean value indicating whether or  not the thread is in the
     *         process of ceasing execution.
     */
    public boolean isAborting() {
        return abort;
    }
}
