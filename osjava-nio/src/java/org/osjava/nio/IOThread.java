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
     * The last exception from executing a task.
     * This is set after a task is executed.
     */
    private Exception lastTaskException;

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
    public IOThread(String name) throws IOException {
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
    public IOThread(ThreadGroup group, String name) throws IOException {
        super(group, name);
        mySelector = Selector.open();
    }

    /**
     * Queue a task to be executed by this thread.
     *
     * This is to allow other threads to queue tasks for this
     * thread to execute.
     *
     * @param task The task to queue.
     * @throws TaskException If there is an exception throw executing the task.
     */
    public void doTask(Runnable task) {
        if(Thread.currentThread() == this) {
            task.run();
        } else {
            synchronized(task) {
                synchronized(tasks) {
                    tasks.add(task);
                }
                mySelector.wakeup();
                try {
                    task.wait();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                if(lastTaskException != null) {
                    throw new TaskException(lastTaskException);
                }
            }
        }
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
     * @throws IllegalArgumentException If handler has not been register, or 
     * it's SelectionKey has been canceled.
     */
    public void addInterestOp(final ChannelHandler handler, final int op) {
        if(Thread.currentThread() != this) {
            /*
             * Execute this via doTask, synchronize on the runnable to
             * ensure it is carried out before we return.
             */
            Runnable r = new Runnable() {
                public void run() {
                    addInterestOp(handler,op);
                }
            };
            doTask(r);
        } else {
            SelectionKey key = handler.getSelectableChannel().keyFor(mySelector);

            /* 
             * Null check necessary because of tasks.  The key might have been 
             * destroyed while the task was queued.
             */
            if(key != null && key.isValid()) {
                key.interestOps(key.interestOps() | op);
            } else {
                throw new IllegalArgumentException("ChannelHandler's key is either no longer valid, or has not been registered");
            }
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
     * @throws IllegalArgumentException If handler has not been register, or 
     * it's SelectionKey has been canceled.
     */
    public void removeInterestOp(final ChannelHandler handler, final int op) {
        if(Thread.currentThread() != this) {
            /*
             * Execute this via doTask, synchronize on the runnable to
             * ensure it is carried out before we return.
             */
            Runnable r = new Runnable() {
                public void run() {
                    removeInterestOp(handler,op);
                }
            };
            doTask(r);
        } else {
            SelectionKey key = handler.getSelectableChannel().keyFor(mySelector);

            /* 
             * Null check necessary because of tasks.  The key might have been 
             * destroyed while the task was queued.
             */
            if(key != null && key.isValid()) {
                key.interestOps(key.interestOps() & ~op);
            } else {
                throw new IllegalArgumentException("ChannelHandler's key is either no longer valid, or has not been registered");
            }
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
     * @throws RuntimeException if either ChannelClosedException or 
     * IllegalStateException are thrown when this is called from another
     * thread.
     */
    public void register(final ChannelHandler handler, final int ops) throws ClosedChannelException, IllegalStateException 
    {
        if(Thread.currentThread() != this) {
            /*
             * Execute this via doTask, synchronize on the runnable to
             * ensure it is carried out before we return.
             */
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        register(handler,ops);
                    } catch (ClosedChannelException cce) {
                        throw new TaskException(cce);
                    }
                }
            };
            doTask(r);
        } else {
            SelectableChannel chan = handler.getSelectableChannel();

            if (chan.isBlocking()) {
                throw new IllegalStateException("SelectableChannel is blocking");
            }
            SelectionKey key = chan.register(mySelector, ops, handler);
        }
    }

    /**
     * Deregisters a {@link ChannelHandler} <code>handler</code> from the 
     * thread.
     * After deregistering, changes in the state of the thread's {@link
     * SelectionKey} will be ignored.
     *
     * @param handler the ChannelHandler to deregister.
     */
    public void deregister(final ChannelHandler handler) {
        if(Thread.currentThread() != this) {
            /*
             * Execute this via doTask, synchronize on the runnable to
             * ensure it is carried out before we return.
             */
            Runnable r = new Runnable() {
                public void run() {
                    deregister(handler);
                }
            };
            doTask(r);
        } else {
            SelectionKey key = handler.getSelectableChannel().keyFor(mySelector);
            /* 
             * Null check necessary because of tasks.  The key might have been 
             * destroyed while the task was queued.
             * 
             * If key is already canceled no need to throw an exception as
             * the request is effectively successful.
             */
            if(key != null) {
                key.cancel();
            }
        }
    }

    /**
     * The looping run method of the class.  All Threads have this.
     */
    public void run() {
        while (!isAborting()) {
            int keyCount;
            try {
                keyCount = mySelector.select();
            } catch (IOException io) {
                setAbort(true);
                io.printStackTrace();
                continue;
            }
            System.out.println("Executing run loop with "+keyCount+" selected and "+tasks.size()+" tasks");
            while(tasks.size() > 0) {
                Runnable r;
                synchronized(tasks) {
                    r = (Runnable) tasks.remove(0);
                }
                synchronized(r) {
                    Exception tmp = null;
                    try {
                        r.run();
                    } catch (RuntimeException re) {
                        tmp = re;
                    }
                    lastTaskException = tmp;
                    r.notifyAll();
                } 
            }

            if(keyCount == 0) continue;

            Set keys = mySelector.selectedKeys();
            Iterator i = keys.iterator();
            while (i.hasNext()) {
                SelectionKey key = (SelectionKey)i.next();
                ChannelHandler handler = (ChannelHandler)key.attachment();

                i.remove();

                if(!key.isValid()) continue;

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
        }
        // This should now work in a satisfactory manner.
        // Close all handlers associated with this IOThread.
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
        // Close mySelector
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
        mySelector.wakeup();
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
