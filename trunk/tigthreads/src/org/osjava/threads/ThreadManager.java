/* 
 * org.osjava.threads.ThreadManager
 * 
 * $Revision: 1.5 $
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

import javax.naming.NameAlreadyBoundException;

/**
 * Thread Manager for a central access to all threads utilized by an 
 * application
 * 
 * @author Robert M. Zigweid
 * @version $Revision: 1.5 $ $Date: 2004/02/12 01:28:46 $
 */
public class ThreadManager {
    /* TODO: Something to prevent cloning this object should be added */
    /* TODO: This is currently SecurityManager insensitive.  Some of these 
     *       creation methods should throw SecurityExceptions too. */
    /** 
     * Instance of the ThreadManager that is actually utilized.
     */
    private static ThreadManager instance;

    /** 
     * Main ExtendedThreadGroup through which all other ExtendedThread and 
     * ExtendedThreadGroups can be accessed.
     */
    private ExtendedThreadGroup masterGroup;
    
    /**
     * Create the ThreadManager. 
     */
    private ThreadManager() {}

    /**
     * Returns the instance of the ThreadManager that is utilized by the static
     * methods.
     * @return the instance of the ThreadManager that is utilized by the 
     *         static methods.
     */
    private static synchronized ThreadManager instanceOf() {
        if (instance == null) {
            instance = new ThreadManager();
            try {
                instance.masterGroup = new ExtendedThreadGroup(null, "master");
            } catch (InvalidThreadParentException e) {
                /* this really shouldn't happen in this situation, so if it 
                 * does, we are going to end the thread.  This likely should
                 * be fixed with a better type of exception handling */
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Gets the 'master' thread group.  The master thread group is the 
     * thread group that is an ancestor to all threads and thread groups 
     * that the ThreadManager knows about.
     * 
     * @return an {@link ExtendedThreadGroup} representing the 'master' thread 
     *         group.
     */
    public static ExtendedThreadGroup getMasterThreadGroup() {
        return getThreadGroup("master");
    }

    /**
     * Creates a new managed {@link ExtendedThread} parented by the master 
     * thread group and a name of <code>name</code>.  The 
     * {@link ExtendedRunnable} object used for the thread is itself.
     * 
     * @param name the name of the thread to be created, which must be unique.
     * 
     * @return the created ExtendedThread
     * 
     * @throws NameAlreadyBoundException if <code>name</code> is already 
     *                                   being used by any of the decendents
     *                                   of the master thread group.
     * @throws InvalidThreadParentException if the created ExtendedThread 
     *                                      cannot be parented by the master
     *                                      thread group.
     */
    public static ExtendedThread createThread(String name)
        throws InvalidThreadParentException, NameAlreadyBoundException {
        return createThread(name, instanceOf().masterGroup, null);
    }

    /**
     * Creates a new managed {@link ExtendedThread} parented by the master 
     * thread group with a name of <code>name</code> and <code>target</code>
     * as the {@link ExtendedRunnable} utilized by the thread.
     *  
     * @param name   The name of the thread, which must be unique.
     * @param target The ExtendedRunnable target to be passed to the created
     *               thread,
     * 
     * @return the created ExtendedThread
     *
     * @throws NameAlreadyBoundException if <code>name</code> is already 
     *                                   being used by any of the decendents
     *                                   of the master thread group.
     * @throws InvalidThreadParentException if the created ExtendedThread 
     *                                      cannot be parented by the master
     *                                      thread group.
     */
    public static ExtendedThread createThread(
        String name,
        ExtendedRunnable target)
        throws InvalidThreadParentException, NameAlreadyBoundException {
        return createThread(name, instanceOf().masterGroup, target);
    }

    /**
     * Creates a new managed {@link ExtendedThread} parented by the 
     * {@link ExtendedThreadGroup} <code>group</code> with a name <code>
     * name</code>.  The {@link ExtendedRunnable} utilized by the thread is 
     * itself.
     *
     * @param name  the name of the thread which must be unique.
     * @param group the ExtendedThreadGroup to which  the thread is to be added.
     * 
     * @return the created ExtendedThread
     * 
     * @throws NameAlreadyBoundException if <code>name</code> is already 
     *                                   being used by any of the decendents
     *                                   of the master thread group.
     * @throws InvalidThreadParentException if the created ExtendedThreadGroup
     *                                      cannot be parented by <code>group
     *                                      <code>.     
     */
    public static ExtendedThread createThread(
        String name,
        ExtendedThreadGroup group)
        throws InvalidThreadParentException, NameAlreadyBoundException {

        return createThread(name, group, null);
    }

    /**
     * Creates a new managed {@link ExtendedThread} parented by the 
     * {@link ExtendedThreadGroup} identified by the String <code>group</code>
     * with a name <code>name</code> and utilizing itself as the 
     * {@link ExtendedRunnable} object.
     *
     * @param name The name of the thread which must be unique.
     * @param group The name of the ExtendedThreadGroup to which
     *              the thread is to be a member.
     * 
     * @return the created ExtendedThread
     * 
     * @throws NameAlreadyBoundException if <code>name</code> is already 
     *                                   being used by any of the decendents
     *                                   of the master thread group.
     * @throws InvalidThreadParentException if the created ExtendedThreadGroup
     *                                      cannot be parented by the 
     *                                      thread group identified by <code>
     *                                      groupName</code>.
     */
    public static ExtendedThread createThread(String name, String group)
        throws InvalidThreadParentException, NameAlreadyBoundException {
        return createThread(name, group, null);
    }

    /**
     * Creates a new managed {@link ExtendedThread} parented by the 
     * {@link ExtendedThreadGroup} identified by the String <code>group</code>
     * with a name <code>name</code> and utilizing <ocde>target</code> as the 
     * {@link ExtendedRunnable} object.
     *
     * @param name name of the thread which must be unique.
     * @param group name of the ExtendedThreadGroup to which
     *              the thread is to be a member.
     * @param target an ExtendedRunnable target to be passed to the created
     *               thread,
     * 
     * @return the created ExtendedThread
     * 
     * @throws NameAlreadyBoundException if <code>name</code> is already 
     *                                   being used by any of the decendents
     *                                   of the master thread group.
     * @throws InvalidThreadParentException if the created ExtendedThreadGroup  
     *                                      cannot be parented by the 
     *                                      thread group identified by <code>
     *                                      groupName</code>.
     */
    public static ExtendedThread createThread(
        String name,
        String group,
        ExtendedRunnable target)
        throws InvalidThreadParentException, NameAlreadyBoundException {
        ExtendedThreadGroup threadGroup =
            instanceOf().masterGroup.getThreadGroup(group, true);

        if (threadGroup == null) {
            throw new InvalidThreadParentException();
        }
        return createThread(name, threadGroup, target);
    }

    /**
     * Creates a new managed {@link ExtendedThread} parented by the 
     * {@link ExtendedThreadGroup} <code>group</code>
     * with a name <code>name</code> and utilizing <ocde>target</code> as the 
     * {@link ExtendedRunnable} object.
     *
     * @param name name of the thread which must be unique.
     * @param group ExtendedThreadGroup to which the Thread is to be added
     * @param target an ExtendedRunnable target to be passed to the created
     *               thread.
     * 
     * @return the created ExtendedThread
     * 
     * @throws NameAlreadyBoundException if <code>name</code> is already 
     *                                   being used by any of the decendents
     *                                   of the master thread group.
     * @throws InvalidThreadParentException if the created ExtendedThreadGroup
     *                                      cannot be parented by <code>group
     *                                      <code>. 
     */
    public static ExtendedThread createThread(
        String name,
        ExtendedThreadGroup group,
        ExtendedRunnable target)
        throws InvalidThreadParentException, NameAlreadyBoundException {

        ExtendedThread newThread;

        if (!instanceOf()
            .masterGroup
            .getThreadGroup(group.getName(), true)
            .equals(group)) {
            throw new InvalidThreadParentException();
        }

        if (instanceOf().masterGroup.getThreadNames(true).contains(name)) {
            throw new NameAlreadyBoundException();
        }

        newThread = new ExtendedThread(group, target, name);
        group.addThread(newThread);
        return newThread;
    }

    /**
     * Add an already created Thread 
     * @param name the name to add the Thread as.
     * @param inThread thread to add to the ThreadManager
     * @throws InvalidThreadParentException if an invalid ThreadParent is 
     *         the threads parent
     * @throws NameAlreadyBoundException if the name of the Thread is already 
     *         bound.
     * @throws InvalidRunnableException if {@link ExtendedRunnable} is not 
     *         implemented
     */
    public static void addThread(String name, Thread inThread)
        throws InvalidThreadParentException, InvalidRunnableException,
               NameAlreadyBoundException {
        /* Make sure that the inThread implements ExtendedRunnable */
        if(!(inThread instanceof ExtendedRunnable)) {
            throw new InvalidRunnableException();
        }
        
        /* Ensure that the name is not already used.*/
        if(instanceOf().masterGroup.getThreadNames(true).contains(name)) {
            throw new NameAlreadyBoundException();
        }
        
        /* The ThreadGroup that inThread is in, must be an
         *  ExtendedThreadGroup */
        if(!(inThread.getThreadGroup() instanceof ExtendedThreadGroup)) {
            throw new InvalidThreadParentException();
        }
        
        /* Check the parent of the thread to ensure that the ThreadManager knows
         * about it */
        ExtendedThreadGroup threadGroup = 
            (ExtendedThreadGroup)inThread.getThreadGroup();
        String groupName = threadGroup.getName();
        ExtendedThreadGroup testedGroup = getThreadGroup(groupName);
        
        if(testedGroup != threadGroup) {
            throw new InvalidThreadParentException();
        }
        
        threadGroup.addThread(inThread);
    }
    
    /**
     * Gets a registered {@link Thread} by its <code>name</code>.
     * All decendant thread groups are checked for the name of this thread.
     *
     * @param name the name of the Thread to retreive.
     * 
     * @return the Thread matching the name <code>name</code>.
     */
    public static Thread getThread(String name) {
        return instanceOf().masterGroup.getThread(name, true);
    }

    /**                                                       
     * Creates a new managed {@link ExtendedThreadGroup} parented by the master
     * thread group and a name of <code>name</code>.       
     *                                                        
     * @param name the name of the thread to be created, which must be unique.
     *                      
     * @return the created ExtendedThreadGroup.
     *                                   
     * @throws NameAlreadyBoundException if <code>name</code> is already    
     *                                   being used by any of the decendents
     *                                   of the master thread group.        
     * @throws InvalidThreadParentException if the created ExtendedThreadGroup
     *                                      cannot be parented by the master
     *                                      thread group.     
     */                                                    
    public static ExtendedThreadGroup createThreadGroup(String name)
        throws InvalidThreadParentException, NameAlreadyBoundException {
        return createThreadGroup(name, instanceOf().masterGroup);
    }

    /**
     * Creates a new managed {@link ExtendedThreadGroup} with a name of <code>
     * name</code> and parented by the thread group identified by the name 
     * <code>groupName</code>.
     *
     * @param name The name of the ExtendedThreadGroup which must be unique.
     * @param groupName The name of the ExtendedThreadGroup to which  
     *                  the new ExtendedThreadGroup is to be added.
     * 
     * @return the created ExtendedThreadGroup.
     * 
     * @throws NameAlreadyBoundException if <code>name</code> is already    
     *                                   being used by any of the decendents
     *                                   of the master thread group.        
     * @throws InvalidThreadParentException if the created ExtendedThreadGroup  
     *                                      cannot be parented by the 
     *                                      thread group identified by <code>
     *                                      groupName</code>.
     */
    public static ExtendedThreadGroup createThreadGroup(
        String name,
        String groupName)
        throws InvalidThreadParentException, NameAlreadyBoundException {
        ExtendedThreadGroup threadGroup =
            instanceOf().masterGroup.getThreadGroup(groupName, true);
        if (threadGroup == null) {
            throw new InvalidThreadParentException();
        }
        return createThreadGroup(name, threadGroup);
    }

    /**
     * Creates a new managed {@link ExtendedThreadGroup} with a name of <code>
     * name</code> and parented by the thread group <code>group</code>.
     *
     * @param name The name of the thread which must be unique.
     * @param parent The ExtendedThreadGroup to which the new 
     *              ExtendedThreadGroup is added.
     * 
     * @return the created ExtendedThreadGroup.
     * 
     * @throws NameAlreadyBoundException if <code>name</code> is already    
     *                                   being used by any of the decendents
     *                                   of the master thread group.        
     * @throws InvalidThreadParentException if the created ExtendedThreadGroup
     *                                      cannot be parented by <code>parent
     *                                      <code>.
     */
    public static ExtendedThreadGroup createThreadGroup(
        String name,
        ExtendedThreadGroup parent)
        throws InvalidThreadParentException, NameAlreadyBoundException {

        ExtendedThreadGroup newThreadGroup;

        if (!instanceOf()
            .masterGroup
            .getThreadGroup(parent.getName(), true)
            .equals(parent)) {
            throw new InvalidThreadParentException();
        }

        if (instanceOf()
            .masterGroup
            .getThreadGroupNames(true)
            .contains(name)) {
            throw new NameAlreadyBoundException();
        }

        newThreadGroup =
            new ExtendedThreadGroup(instanceOf().masterGroup, name);
        parent.addThreadGroup(newThreadGroup);
        return newThreadGroup;
    }

    /**
     * Gets a registered {@link ExtendedThreadGroup} identified by <code>
     * name</code>.
     *
     * @param name the name of the ExtendedThreadGroup to retreive.
     * 
     * @return The ExtendedThreadGroup identified by <code>name</code>.
     */
    public static ExtendedThreadGroup getThreadGroup(String name) {
        /* Checks for nulls in the instance, and in the masterGroup */
        if(instance==null) {
            return null;
        }
        if(instanceOf().masterGroup==null) {
            return null;
        }
        return instanceOf().masterGroup.getThreadGroup(name, true);
    }
    
    /** 
     * Notify an {@link ExtendedThread}.  This method handles
     * synchronizatio issues, and allows a Thread managed by the this object
     * to be have <code>notify()</code> called in it.  If the named Thread 
     * is not managed by the ThreadManager, this method has no effect.
     * 
     * @param name the name of the <code>ExtendedThread</code> or 
     *        <code>ExtendedThreadGroup</code>.
     */
    public static void notifyThread(String name) {
        System.out.println("Looking to notify thread -- " + name);
        ExtendedRunnable thread = (ExtendedRunnable)getThread(name);
        System.out.println("Found -- " + thread);
        if(thread != null) {
            /* If the found thread implements ExtendedRunnable, we really want
             * to be notifying the ExtendedRunnable object instead of the 
             * Thread itself.  -- Robert */
            if(thread instanceof ExtendedThread)  {
                thread = ((ExtendedThread)thread).getRunnable();
            }
            synchronized(thread) {
                thread.notify();
            }
        }
    }

    /**
     * Starts the execution of an {@link ExtendedThread} or all of the threads
     * in an {@link ExtendedThreadGroup} identified by the name <code>name
     * </code>.  The found object's <code>start()</code> method is called from
     * here.
     * 
     * @param name the name of the thread or thread group.
     */
    public static void start(String name) {
        Thread thread = getThread(name);
        ExtendedThreadGroup group = null;

        /* Handle the easy instance of the single thread */
        if (thread != null) {
            thread.start();
            return;
        }

        group = getThreadGroup(name);
        if (group == null) {
            /* If group is also null, there's nothing to do */
            return;
        }

        group.start();
    }

    /**
     * Stops the execution of an {@link ExtendedThread} or all of the threads
     * in an {@link ExtendedThreadGroup} identified by the name <code>name
     * </code>.  The found object's <code>setAbort(boolean)</code> method is 
     * called from here.
     * 
     * @param name the name of the thread or thread group.
     */
    public static void stop(String name) {
        ExtendedRunnable thread = (ExtendedRunnable)getThread(name);
        ExtendedThreadGroup group = null;

        /* Handle the easy instance of working with just the single thread */
        if (thread != null) {
            thread.setAbort(true);
            return;
        }
        
        group = getThreadGroup(name);
        if (group == null) {
            /* If group is also null, there's nothing to do */
            return;
        }

        group.setAbort(true);
    }
    
    /**
     * Prevents the cloneing of an ThreadManager
     * 
     * @return nothing.  This method will never return an object
     * @throws CloneNotSupportedException to indicate that this 
     */
    protected Object clone() throws CloneNotSupportedException {
        throw new 
        CloneNotSupportedException("ThreadManager cannot be cloned.");
    }
}
