/* 
 * org.osjava.threads.ExtendedThreadGroup
 * 
 * $Revision: 1.2 $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An extended {@link ThreadGroup} that adds additional accessor API for easier 
 * management. 
 * <p>
 * This implementation of ThreadGroup has better access to its 
 * children, be they other ExtendedThreadGroups, or {@link ExtendedThreads}.
 * 
 * @author Robert M. Zigweid
 * @version $Revision: 1.2 $ $Date: 2003/11/03 08:55:23 $
 */
public class ExtendedThreadGroup extends ThreadGroup {
    /**
     * Map of all of the threads that this ExtendedThreadGroup is the parent of
     * The key is the name of the thread, and the value is the 
     * {@link ExtendedThread}.
     */
    private Map threadChildren = new HashMap();

    /**
     * Map of all of the groups that this ExtendedThreadGroup is the parent of
     * The key is the name of the thread, and the value is the 
     * ExtendedThreadGroup object
     */
    private Map threadGroupChildren = new HashMap();

    /**
     * Constructs a new ExtendedThreadGroup with the name <code>name</code> .
     * The parent of the group is the parent of the currently
     * running ExtendedThread.  If the thread running is not an ExtendedThread
     * the parent of the group is the ThreadGroup returned by 
     * {@link ThreadManager#getMasterThreadGroup()}.
     * <p>
     * The checkAccess {@link ThreadGroup#checkAccess()} method of the 
     * parent thread group is called with no arguments, which may result in 
     * a security exception.
     * <p>
     * 
     * @param name the name of the thread group
     * 
     * @throws InvalidThreadParentException if the created thread group cannot
     *                                      be parented by the specified parent.
     * @throws SecurityException if the current thread cannot create a thread 
     *                           in the specified thread group.
     */
    protected ExtendedThreadGroup(String name)
        throws InvalidThreadParentException, SecurityException {
        this(ThreadManager.getMasterThreadGroup(), name);
    }
    
    /**
     * Constructs a new ExtendedThreadGroup with the parent thread group 
     * <code>parent</code> and hte name <code>name</code>.
     * <p>
     * The checkAccess {@link ThreadGroup#checkAccess()} method of the 
     * parent thread group is called with no arguments, which may result in 
     * a security exception.
     * 
     * @param parent an ExtendedThreadGroup which is the parent of the this 
     *               object
     * @param name   the name of the thread group
     * 
     * @throws InvalidThreadParentException if the created thread group cannot
     *                                      be parented by the specified parent.
     * @throws SecurityException if the current thread cannot create a thread 
     *                           in the specified thread group.
     */
    protected ExtendedThreadGroup(ExtendedThreadGroup parent, String name)
        throws InvalidThreadParentException, SecurityException {
        super(checkParent(parent), name);
        if (getParent() != null &&
            getParent() instanceof ExtendedThreadGroup) {
            ((ExtendedThreadGroup)getParent()).addThreadGroup(this);
        }
    }

    /**
     * Returns the appropriate ThreadGroup parent to the ExtendedThreadGroup
     * that is created.  This method is only intended to be called from the 
     * {@link #Constructor(ExtendedThreadGroup,String) ExtendedThreadGroup}
     * constructor in the instance that a <code>null</code> is passed as the 
     * <code>parent</code> parameter.  If <code>parent</code> is not null, 
     * it is returned directly.  If it is null, the ThreadManager is queried
     * for the 'master' group.  If that returns null, then the ThreadGroup
     * that is the parent of the current Thread is returned.
     * 
     * @param parent the parent to be tested.
     * @return the ThreadGroup that is a valid parent.
     */
    private static ThreadGroup checkParent(ExtendedThreadGroup parent) {
        ThreadGroup ret=parent;
        if(ret==null) {
            ret=ThreadManager.getMasterThreadGroup();
        }
        if(ret==null) {
            ret=Thread.currentThread().getThreadGroup();
        }
        return ret;
    }

    /**
     * Cleans up dead threads and destroyed threadgroups
     */
    private void cleanUp() {
        Set keys;
        Iterator it;

        /* Do Threads first */
        keys = threadChildren.keySet();
        it = keys.iterator();
        while (it.hasNext()) {
            String next = (String)it.next();
            ExtendedThread thread = (ExtendedThread)threadChildren.get(next);

            if (!thread.isAlive() && thread.isStarted()) {
                threadChildren.remove(next);
            }
            if (threadChildren.get(next) == null) {
                threadChildren.remove(next);
            }
        }

        /* ThreadGroups */
        keys = threadGroupChildren.keySet();
        it = keys.iterator();

        while (it.hasNext()) {
            String next = (String)it.next();
            ExtendedThreadGroup threadGroup =
                (ExtendedThreadGroup)threadGroupChildren.get(next);
            if (threadGroup.isDestroyed()) {
                threadGroupChildren.remove(next);
            }
            if (threadGroupChildren.get(next) == null) {
                threadGroupChildren.remove(next);
            }
        }
    }

    /**
     * Adds an {@link ExtendedThread} to the ExtendedThreadGroup.  This is a 
     * somewhat tricky method because the <code>newThread</code> is passed as 
     * a parameter must have been created specifying this object as its parent
     * because {@link Thread} does not have any methods which allow the
     * parent of the ThreadGroup to be set after it's creation. 
     * <p>
     * An InvalidThreadParentException is thrown if <code>newThread</code>
     * does not have a parent of this object.   
     * 
     * @param newThread The ExtendedThread to be parented.
     * 
     * @throws InvalidThreadParentException if <code>newThread<code>'s parent 
     *                                      is not this object.
     */
    public void addThread(ExtendedThread newThread)
        throws InvalidThreadParentException {
        cleanUp();
        /* Check first to ensure that the Thread is actually parented by 
         * this ThreadGroup */
        if (!newThread.getThreadGroup().equals(this)) {
            /* Throw an exception. */
            throw new InvalidThreadParentException();
        }

        threadChildren.put(newThread.getName(), newThread);
    }

    /**
     * Adds an ExtendedThreadGroup to the ExtendedThreadGroup.  This is a 
     * somewhat tricky method because the ExtendedThread that is passed as 
     * a parameter must have been created specifying this object as its parent
     * because {@link ThreadGroup} does not have any methods which allow the 
     * parent of the ThreadGroup to be set after it's creation. 
     * <p>
     * An InvalidThreadParentException is thrown if <code>newThreadGroup</code>
     * does not have a parent of this object.   
     * 
     * @param newThreadGroup The ExtendedThreadGroup to be parented.
     * 
     * @throws InvalidThreadParentException if <code>newThreadGroup<code>'s 
     *         parent is not this object.
     */
    public void addThreadGroup(ExtendedThreadGroup newThreadGroup)
        throws InvalidThreadParentException {
        cleanUp();

        /* Check first to ensure that the ThreadGroup is actually parented by 
         * this ThreadGroup.  */
        if (!newThreadGroup.getParent().equals(this)) {
            /* Throw an exception. */
            throw new InvalidThreadParentException();
        }
        threadGroupChildren.put(newThreadGroup.getName(), newThreadGroup);
    }

    /**
     * Returns a Collection of all of the names of the 
     * {@link ExtendedThreads} that this object parents, but not those of 
     * the ExtendedThreadGroups that this object parents.
     * 
     * @return a Collection of the names of all of the threads.
     */
    public Collection getThreadNames() {
        return getThreadNames(false);
    }

    /**
      * Returns a Collection of all of the names of the 
      * {@link ExtendedThreads} that this object parents. If <code>recurse
      * </code> is true, the names of all of the ExtendedThreads in all of 
      * the ExtendedThreadGroups that this one is the ancestor of are included.
      * If <code>recurse</code> is false, only the ExtendedThreads that are 
      * directly parented are included.
      *
      * @param recurse If recurse is true, decend into child 
      *                ExtendedThreadGroups
      * 
      * @return A Collection containing the names of the threads.
      */
    public Collection getThreadNames(boolean recurse) {
        Collection ret = new ArrayList();
        Collection subGroups;
        Iterator it;

        cleanUp();

        if (recurse == false) {
            return threadChildren.keySet();
        }
        ret.addAll(getThreadNames());

        subGroups = getThreadGroupNames();
        it = subGroups.iterator();
        while (it.hasNext()) {
            ExtendedThreadGroup next = getThreadGroup((String)it.next());
            Collection toAdd = next.getThreadNames(true);
            ret.addAll(toAdd);
        }
        return ret;
    }

    /**
     * Returns a Collection of all of the names of the ThreadGroups that 
     * this ExtendedThreadGroup parents.
     * 
     * @return A Collection of Strings which represent the names of the 
     *         ExtendedThreads.
     */
    public Collection getThreadGroupNames() {
        return getThreadGroupNames(false);
    }

    /**
     * Returns a Collection of all of the names of the 
     * {@link ThreadsGroups} that this ExtendedThreadGroup parents.  If 
     * <code>recurse</code> is true, the names of all of the 
     * ExtendedThreads in all of the ExtendedThreadGroups that this one is the 
     * ancestor of are included.  If <code>recurse</code> is false, only the 
     * ExtendedThreads that are directly parented are included.
     *
     * @param recurse If recurse is true, decend into child ThreadGroups.
     * 
     * @return A Collection of Strings which represent the names of the 
     *         ExtendedThreads.
     */
    public Collection getThreadGroupNames(boolean recurse) {
        Collection ret = new ArrayList();
        Collection subGroups;
        Iterator it;

        cleanUp();

        if (!recurse) {
            return threadGroupChildren.keySet();
        }
        ret = getThreadGroupNames();

        subGroups = getThreadGroupNames();
        it = subGroups.iterator();
        while (it.hasNext()) {
            ExtendedThreadGroup next = getThreadGroup((String)it.next());
            ret.addAll(next.getThreadGroupNames(true));
        }
        return ret;
    }

    /** 
     * Returns the specific {@link ExtendedThread} that this object parents 
     * that has the name <code>name</code>. Child ExtendedThreadGroups
     * are not recursed into.
     * 
     * @param name The name of the ExtendedThread to retrieve.
     * 
     * @return An ExtendedThread with the name <code>name</code>.
     */
    public ExtendedThread getThread(String name) {
        return getThread(name, false);
    }

    /** 
     * Returns the specific {@link ExtendedThread} that this object parents.
     * If <code>recurse</codee> is true, all ExtendedThreadGroups that this 
     * object is an ancestor of are looked at.  If it is false, only the 
     * objects children are looked at.
     * 
     * @param name The name of the Thread to retrieve.
     * @param recurse If true, recurse into subThreadGroups to find the 
     *                Thread.
     * 
     * @return An ExtendedThread with the name <code>name</code>.
     */
    public ExtendedThread getThread(String name, boolean recurse) {
        Set allThreads;
        Iterator it;
        ExtendedThread ret;

        cleanUp();

        if (!recurse) {
            return (ExtendedThread)threadChildren.get(name);
        }

        allThreads = threadGroupChildren.keySet();
        it = allThreads.iterator();

        if ((ret = getThread(name, false)) != null) {
            return ret;
        }
        while (it.hasNext()) {
            String next = (String)it.next();
            ret = getThreadGroup(next).getThread(name, recurse);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    /** 
     * Returns the ExtendedThreadGroup that is identified by <code>name</code>.
     * This does not recurse into sub thread groups.
     * If <code>recurse</codee> is true, all ExtendedThreadGroups that this 
     * object is an ancestor of are looked at.  If it is false, only the 
     * objects children are looked at.
     * 
     * @param name The name of the Thread to retrieve.
     * 
     * @return An ExtendedThreadGroup with the name <code>name</code>.
     */
    public ExtendedThreadGroup getThreadGroup(String name) {
        return getThreadGroup(name, false);
    }

    /** 
     * Returns the specific ExtendedThread that this ExtendedThreadGroup 
     * parents.
     * 
     * @param name The name of the Thread to retrieve
     * @param recurse If true, search in grandchildren and other decendants
     * 
     * @return An ExtendedThreadGroup with the name <code>name</code>.
     */
    public ExtendedThreadGroup getThreadGroup(String name, boolean recurse) {
        Set allGroups;
        Iterator it;
        ExtendedThreadGroup ret;

        cleanUp();

        if (name.equals(getName())) {
            return this;
        }

        if (!recurse) {
            return (ExtendedThreadGroup)threadGroupChildren.get(name);
        }

        allGroups = threadGroupChildren.keySet();
        it = allGroups.iterator();

        if ((ret = getThreadGroup(name, false)) != null) {
            return ret;
        }

        while (it.hasNext()) {
            ExtendedThreadGroup next = getThreadGroup((String)it.next(), true);
            ret = next.getThreadGroup(name, recurse);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    /**
     * Invokes <code>start()</code> on all of the {@link ExtendedThreads}
     *  that this group is an ancestor of.
     */
    public void start() {
        Iterator it = threadChildren.keySet().iterator();

        while (it.hasNext()) {
            ExtendedThread next = (ExtendedThread)it.next();
            if (next == null) {
                it.remove();
            }
            next.start();
        }

        it = threadGroupChildren.keySet().iterator();
        while (it.hasNext()) {
            ExtendedThreadGroup next = (ExtendedThreadGroup)it.next();
            if (next == null) {
                it.remove();
            }
            next.start();
        }
    }

    /**
     * Run setAbort() on all of the threads that this group is an ancestor of
     * 
     * @param abort Boolean value determining whether or not the thread is to 
     *              be aborted, or can be set to halt a previously declared 
     *              abort
     */
    public void setAbort(boolean abort) {
        Iterator it = threadChildren.keySet().iterator();

        while (it.hasNext()) {
            Object next = threadChildren.get(it.next());
            ((ExtendedThread)next).setAbort(abort);
        }

        it = threadGroupChildren.keySet().iterator();
        while (it.hasNext()) {
            ((ExtendedThreadGroup)it.next()).setAbort(abort);
        }
    }
}
