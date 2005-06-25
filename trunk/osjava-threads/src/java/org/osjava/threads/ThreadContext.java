/*
 * org.osjava.threads.ThreadContext
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Mar 24, 2004
 *
 * Copyright (c) 2004, Robert M. Zigweid All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.osjava.sj.jndi.AbstractContext;
import org.osjava.naming.InvalidObjectTypeException;


/**
 * A Context for managing Threads and ThreadGroups.
 * <br/><br/>
 * This context depends upon the Directory-Naming package available from 
 * http://incubator.apache.org.
 * <br/><br/>
 * A ThreadContext object can only be bound once in a hierarchy of Contexts.
 * Being bound in more than one place will result in undefined behavior.  
 * Additionally, even though a ThreadContext object can be duplicated as 
 * other Contexts, renaming Threads bound to the Context will result in 
 * undefined behavior, and this activity is not at all recommended.
 * 
 * @author Robert M. Zigweid
 * @version $Rev$ $LastChangedDate$
 * @since OSJava Threads 2.0
 */

public class ThreadContext
    extends AbstractContext
    implements Context {

    private boolean closing;

    /* **************
     * Constructors *
     * **************/
    /**
     * Create a ThreadContext which will use the environment of the passed
     * {@link AbstractContext}
     *
     * @param that the AbstractContext which will supply the environment for 
     *        the newly created ThreadContext.
     */
    public ThreadContext(AbstractContext that) {
        super(that);
    }

    /**
     * Create a ThreadContext which will use the environment of the passed
     * {@link AbstractContext}
     *
     * @param that the AbstractContext which will supply the environment for 
     *        the newly created ThreadContext.
     * @param name the Name of the ThreadContext.  It can be null, and if it
     *        is considered to be the root context.
     * @throws NamingException if a naming exception is encountered.
     */
    public ThreadContext(AbstractContext that, Name name) throws NamingException {
        super(that);
        setNameInNamespace(name);
    }

    /**
     * Create a ThreadContext which will use the environment of the passed
     * {@link AbstractContext}
     *
     * @param that the AbstractContext which will supply the environment for 
     *        the newly created ThreadContext.
     * @param name the String representation of the name of the ThreadContext.
     *        It can be null, and if it is considered to be the root context.
     * @throws NamingException if a naming exception is encountered.
     */
    public ThreadContext(AbstractContext that, String name) throws NamingException {
        super(that);
        setNameInNamespace(name);
    }

    /**
     * Create a ThreadContext with a specific name.  This constructor is 
     * private and only intended to be invoked from this object.  If <code>
     * null</code> is passed as the name, it is expected to be a root context.
     * 
     * @param env the environment to be used by the subcontext.
     * @param name The name of the ThreadContext.  It can be null, and if it is
     *        considered to be the root context.
     * @throws NamingException if a naming exception is encountered.
     */
    public ThreadContext(Hashtable env, Name name) throws NamingException {
        /* 
         * Directory-naming uses string based names, which is fine, but 
         * it does not accept a Name for the name, which I think is wrong.
         */
        super(env);
        setNameInNamespace(name);
    }

    /**
     * Create a ThreadContext with a specific name.  This constructor is 
     * private and only intended to be invoked from this object.  If <code>
     * null</code> is passed as the name, it is expected to be a root context.
     * 
     * @param env the environment to be used by the subcontext.
     * @param name The name of the ThreadContext.  It can be null, and if it is
     *        considered to be the root context.
     * @throws NamingException if a naming exception is encountered.
     */
    public ThreadContext(Hashtable env, String name) throws NamingException {
        super(env);
        setNameInNamespace(name);
    }

    /* ************************
     * Class Specific Methods *
     * ************************/
    /**
     * Generate a name for the thread.
     *   
     * @return the newly generated String that represents a new name for
     *         for use by a Thread. 
     * @throws NamingException
     */
    private String generateNextThreadName() throws NamingException {
        int high = -1;
        NamingEnumeration enumeration = list("");
        while(enumeration.hasMore()) {
            NameClassPair next = (NameClassPair)enumeration.next();
            /* Get the last section of the name */
            String namePart = next.getName();
            if(!namePart.startsWith("Thread-")) {
               continue;
            }
            int test = Integer.valueOf(namePart.substring(7)).intValue();
            if(test > high) {
                high = test;
            }
        }
        return "Thread-" + high + 1;
    }
    /**
     * Create a new ExtendedThread with the Name <code>name</code>.  The name 
     * is relative to this context and all subcontexts must already be
     * created.  Null or empty names are not permitted.  The <code>target
     * </code>, if it is not null, will be wrapped in an ExtendedThread.  The 
     * target's run() method will be called when the thread is started.
     * 
     * @param target the target Runnable to bind to the context..
     * @return the newly created ExtendedThread
     * @throws NameAlreadyBoundException if <code>name</code> is already bound 
     *         to another object.
     * @throws ThreadIsRunningException if the target is a running thread.
     * @throws NamingException if another naming exception is encountered.
     */
    public Thread createThread(Runnable target)
        throws NameAlreadyBoundException, NamingException, ThreadIsRunningException {
        return this.createThread(target, (Name)null);
    }
        
    /**
     * Create a new ExtendedThread with the Name <code>name</code>.  The name 
     * is relative to this context and all subcontexts must already be
     * created.  Null or empty names are not permitted.  The <code>target
     * </code>, if it is not null, will be wrapped in an ExtendedThread.  The 
     * target's run() method will be called when the thread is started.
     * 
     * @param target the target of the newly created thread. 
     * @param name the name of the thread.
     * @return the newly created ExtendedThread
     * @throws NameAlreadyBoundException if <code>name</code> is already bound 
     *         to another object.
     * @throws ThreadIsRunningException if the target is a running thread.
     * @throws NamingException if another naming exception is encountered.
     */
    public Thread createThread(Runnable target, Name name)
        throws NameAlreadyBoundException, NamingException, ThreadIsRunningException {
        /* The target must not be null */
        if(target == null) {
            throw new  NullPointerException("Target must not be null");
        }
        /* 
         * If no name is supplied, make up a name for the Thread based upon 
         * the context. 
         */        
        if(name == null || name.isEmpty()) {
            /* Generate a new name for the thread */
            Name newName = getNameParser((Name)null).parse(getNameInNamespace());
            String add = generateNextThreadName();
            newName.add(add);
            name = getNameParser(getNameInNamespace()).parse(add);
        }
        /* Make sure that the target is not already running. */
        if(target instanceof Thread) {
             if(((Thread)target).isAlive()) {
                 throw new ThreadIsRunningException();
             }
             
             /* 
              * The thread must implement ExtendedRunnable.  If it isn't,
              * wrap it in an ExtendedThread
              */
             if(!(target instanceof ExtendedRunnable)) {
                 /*
                  * Wrapping target in ExtendedThread because it's not 
                  * ExtendedRunnable 
                  */
                 Thread newTarget = new ExtendedThread(target, ((Thread)target).getName());
                 /* Reset the target variable to the new thread. */
                 target = newTarget;
             }
             
             /* Use the supplied name to name the thread */
             /* XXX: Might want to move this to using the whole name */
             ((Thread)target).setName(name.getSuffix(name.size() - 1).toString());
        } else if(target instanceof ExtendedRunnable) {
            String threadName = null;
            /* Use the supplied name to name the thread */
            /* XXX: Might want to move this to using the whole name */
            threadName = name.getSuffix(name.size() - 1).toString();
            target = new ExtendedThread(target, threadName);
        }
        
        /* Check to see if the name is already bound to something */
        Object next = lookup(name.getPrefix(1));
        /* Not presently bound. */
        if(next == null) {
            /* The subcontext doesn't exist if the name > 1 */
            if(name.size() > 1) {
                throw new NameNotFoundException();
            }
            
            bind(name, target);
            return (Thread)target;
        }
        
        /* Bound to a subContext */
        if(next instanceof ThreadContext) {
            return ((ThreadContext)next).createThread(target, name.getSuffix(1));
        } else if(next instanceof Context) {
            /* 
             * XXX: Should this be changed to some other exception?  I thought
             *      there was an exception for missing subcontext.
             */
            throw new NamingException("Invalid subcontext.  Subcontext must be a ThreadContext");
        } else {
            /* Bound to something else. */
            throw new NameAlreadyBoundException("Name already bound");
        }
    }

    /**
     * Create a new ExtendedThread with the Name <code>name</code>.  The name 
     * is relative to this context and all subcontexts must already be
     * created.  Null or empty names are not permitted.  The <code>target
     * </code>, if it is not null, will be wrapped in an ExtendedThread.  The 
     * target's run() method will be called when the thread is started.
     * 
     * @param target the target of the newly created thread. 
     * @param name the name of the thread.
     * @return the newly created ExtendedThread
     * @throws NameAlreadyBoundException if <code>name</code> is already bound 
     *         to another object.
     * @throws ThreadIsRunningException if the target is a running thread.
     * @throws NamingException if another naming exception is encountered.
     */
    public Thread createThread(Runnable target, String name)
        throws NameAlreadyBoundException, NamingException, ThreadIsRunningException {
        NameParser nameParser = getNameParser((Name)null);
        return this.createThread(target, nameParser.parse(name));
    }
        
    /** 
     * Notify an {@link ExtendedThread}.  This method handles
     * synchronization issues, and allows a Thread managed by the this object
     * to be have <code>notify()</code> called in it.  The name specified 
     * is relative to this context.  
     * 
     * @param name the name of the ExtendedThread. 
     * @throws NameNotFoundException if the subcontext specified is not found.
     * @throws NamingException if a naming exception is encountered.
     */
    public void notifyThread(Name name) throws NameNotFoundException, NamingException {
        if(name == null || name.isEmpty()) {
            NamingEnumeration list = list(name);
            while(list.hasMore()) {
                Object next = list.next();
                if(next instanceof ExtendedThread) {
                    synchronized(next) {
                        ((ExtendedRunnable)next).wakeup();
                        continue;
                    }
                }
                if(next instanceof ThreadContext) {
                    ((ThreadContext)next).notifyThread(name);
                }
                /* Ignore anything not matching those two types. */
            }
            return;
        }
        
        Object obj = lookup(name);
        if(obj instanceof Thread) {
            synchronized (obj) {
                ((ExtendedRunnable)obj).wakeup();
            }
            return;
        }

        if(obj instanceof ThreadContext) {
            ((ThreadContext)obj).notifyThread((Name)null);
            return;
        }
        
        /* 
         * XXX: Should this be changed to some other exception?  I thought
         *      there was an exception for missing subcontext..
         */
        throw new NamingException("Invalid subcontext.  Subcontext must be a ThreadContext");
    }
    
    /** 
     * Notify an {@link ExtendedThread}.  This method handles
     * synchronizatio issues, and allows a Thread managed by the this object
     * to be have <code>notify()</code> called in it.  The name specified 
     * is relative to this context. 
     * 
     * @param name the name of the ExtendedThread.
     * @throws NameNotFoundException if the subcontext specified is not found.
     * @throws NamingException if another naming exception is encountered.
     */
    public void notifyThread(String name) 
        throws NamingException, NameNotFoundException {
        NameParser nameParser = getNameParser((Name)null);
        notifyThread(nameParser.parse(name));
    }
    
    /**
     * Invokes <code>start()</code> on all of the {@link ExtendedThread
     * ExtendedThreads} in this context and its subcontexts.
     * 
     * @param name the name of the ExtendedThread or ThreadContext 
     *        start.
     * @throws NameNotFoundException if the name cannot be found in the 
     *         context
     * @throws NamingException if a naming exception is encountered.
     */
    public void start(Name name) throws NameNotFoundException, NamingException {
        /* 
         * If the name is empty, we don't need to look anything up.  We know 
         * that the target is this context.
         */
        if(name == null || name.isEmpty()) {
            NamingEnumeration list = listBindings(name);
            while(list.hasMore()) {
                Binding next = (Binding)list.next();
                Object item = next.getObject();
                if(item instanceof Thread) {
                    synchronized(item) {
                        ((Thread)item).start();
                        continue;
                    }
                }
                if(item instanceof ThreadContext) {
                    ((ThreadContext)item).start(name.getSuffix(1));
                }
                /* Ignore anything not matching those two types. */
            }
            return;
        }

        Object obj = lookup(name);
        if(obj instanceof ExtendedThread) {
            synchronized (obj) {
                ((ExtendedThread)obj).start();
            }
            return;
        }
        
        if(obj instanceof ThreadContext) {
            Object next = lookup(name.getPrefix(1));
            ((ThreadContext)next).start(name.getSuffix(name.size()));
            return;
        }
        
        /* 
         * XXX: Should this be changed to some other exception?  I thought
         *      there was an exception for missing subcontext..
         */
        throw new NamingException("Invalid subcontext.  Subcontext must be a ThreadContext");
    }
    
    /**
     * Invokes <code>start()</code> on all of the {@link ExtendedThread
     * ExtendedThreads} in this context and its subcontexts.
     * 
     * @param name the name of the ExtendedThread or ThreadContext to start.
     * @throws NameNotFoundException if the name cannot be found in the 
     *         context.
     * @throws NamingException if a naming exception is encountered.
     */
    public void start(String name) throws NameNotFoundException, NamingException {
        NameParser nameParser = getNameParser((Name)null);
        start(nameParser.parse(name));
    }

    /**
     * Run setAbort() on all of the threads that this context is an ancestor
     * of or the specified {@link ExtendedThread}.
     * 
     * @param name of the ThreadContext or ExtendedThread that is a 
     *        decendant of the ThreadContext.
     * @param abort Boolean value deterMadmining whether or not the thread is to 
     *        be aborted, or can be set to halt a previously declared abort.
     * @throws NameNotFoundException if the name cannot be found.
     * @throws NamingException if a naming exception is encoutnered.
     */
    public void setAbort(Name name, boolean abort) throws NameNotFoundException, NamingException {
        Object obj = lookup(name);
        /* 
         * Take care of the easy case first, pointing at an actual 
         * thread.
         */
        if(obj instanceof ExtendedThread) {
            synchronized (obj) {
                ((ExtendedThread)obj).setAbort(abort);
            }
            return;
        }
        
        if(name == null || name.isEmpty()) {
            NamingEnumeration list = list(name);
            while(list.hasMore()) {
                Object next = list.next();
                if(next instanceof ExtendedThread) {
                    synchronized(next) {
                        ((ExtendedThread)obj).start();
                        continue;
                    }
                }
                if(next instanceof ThreadContext) {
                    ((ThreadContext)next).setAbort(name, abort);
                }
                /* Ignore anything not matching those two types. */
            }
            return;
        }
        
        Object next = lookup(name.getPrefix(1));
        if(next instanceof ThreadContext) {
            ((ThreadContext)next).setAbort(name.getSuffix(name.size()), abort);
            return;
        }
        
        /* 
         * XXX: Should this be changed to some other exception?  I thought
         *      there was an exception for missing subcontext..
         */
        throw new NamingException("Invalid subcontext.  Subcontext must be a ThreadContext");    }

    /**
     * Run setAbort() on all of the threads that this context is an ancestor
     * of or the specified {@link ExtendedThread}.
     * 
     * @param name of the ThreadContext or ExtendedThread that is a 
     *        decendant of the ThreadContext.
     * @param abort Boolean value deterMadmining whether or not the thread is to 
     *        be aborted, or can be set to halt a previously declared abort.
     * @throws NameNotFoundException if the name cannot be found..
     * @throws NamingException if a naming exception is encountered.
     */
    public void setAbort(String name, boolean abort) throws NameNotFoundException, NamingException {
        NameParser nameParser = getNameParser((Name)null);
        setAbort(nameParser.parse(name), abort);
    }
    
    
    /* *************************************
     * Methods overriding AbstractContext. *
     * *************************************/
    /**
     * Bind the Object <code>obj</code> to the Name <code>name</code>.  The 
     * object must be an {@link ExtendedRunnable}.  The Name <code>name</code>
     * is relative to this context.  
     * 
     * @param name The name to bind the Object to.
     * @param obj The object that is being bound in the ThreadContext.
     * @throws NamingException if the object cannot be bound to the name.
     * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
     */
    public void bind(Name name, Object obj) throws NamingException {
        if(obj == null) {
            throw new InvalidObjectTypeException("Objects in this context must implement " +
                            "org.osjava.threads.ExtendedRunnable or ThreadContext.  " + 
                            "Object is null");
        }
        /*
         * Only the following types are allowed to be bound through this 
         * method: 
         *      ExtendedRunnable
         *      ThreadContext
         */
        /* 
         * TODO: If this is a Runnable of any sort, we can wrap an
         * ExtendedThread around it, solving this problem.
         */
        if(!(obj instanceof ExtendedRunnable) &&
           !(obj instanceof ThreadContext)) {
            throw new InvalidObjectTypeException("Objects in this context must implement " +
                    "org.osjava.threads.ExtendedRunnable or ThreadContext.  " + 
                    "Object is of type '" + obj.getClass() + "'.");
        }
        /* 
         * If the thread is an instance of Thread, make sure that it is not 
         * alive.
         */
        if(obj instanceof Thread &&
           ((Thread)obj).isAlive()) {
            throw new ThreadIsRunningException("A thread stored in the context cannot already be running.");
        }
        
        super.bind(name, obj);
    }

    /**
     * Bind the Object <code>obj</code> to the Name <code>name</code>.  The object
     * must be an {@link ExtendedRunnable}, or {@link ThreadContext}.
     * 
     * @param name The name to bind the Object to.
     * @param obj The object that is being bound in the ThreadContext.
     * @throws NamingException if the object cannot be bound to the name.
     * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
     */
    public void bind(String name, Object obj) throws NamingException {
        NameParser nameParser = getNameParser((Name)null);
        bind(nameParser.parse(name), obj);
    }

    /** 
     * Unbind a Thread from the ThreadContext.
     * <br/><br/>
     * <b>NOTE:</b> This does not destroy the {#link ExtendedRunnable} that 
     * was bound in the context.  It only removes it from the ThreadContext.
     * A running ExtendedRunnable cannot be removed from the context. 
     * 
     * @param name the name of the object to unbind from the ThreadContext.
     * @throws ThreadIsRunningException if the thread that is being unbound is
     *         running.
     * @throws NamingException if a name exception is encountered.
     * @see javax.naming.Context#unbind(javax.naming.Name)
     */
    /*
     * XXX: It is possible that this method should actually stop a thread
     *      or threads if the object referred to is a context 
     */
    public void unbind(Name name) throws NamingException {
        Object thread = lookup(name);
        
        /* 
         * If the thread is an instance of Thread, make sure that it is not 
         * alive.
         */
        if(thread instanceof Thread &&
           ((Thread)thread).isAlive()) {
            throw new ThreadIsRunningException("A running thread cannot be removed from the context.");
        }
        if(thread != null) {
           super.unbind(name);
        }
    }

    /**
     * Unbind a Thread from the ThreadContext.
     * <br/><br/>
     * <b>NOTE:</b> This does not destroy the object that was bound in the 
     * ThreadContext.  It only removes it.
     * 
     * @see javax.naming.Context#unbind(java.lang.String)
     */
    public void unbind(String name) throws NamingException {
        NameParser nameParser = getNameParser((Name)null);
        unbind(nameParser.parse(name));
    }

    /**
     * Binds an Object <code>obj</code> to the Name <code>name</code> 
     * overwriting any existing binding.  All intermediate Contexts must
     * already exist, or a NamingException is thrown.
     * 
     * @param name the Name of the object to be bound.
     * @param obj the Object to be bound.
     * @throws NamingException if the object cannot be bound or a naming 
     *         exception is encountered.
     * @see javax.naming.Context#rebind(javax.naming.Name,java.lang.Object)
     */
    public void rebind(Name name, Object obj) throws NamingException {
        super.rebind(name, obj);
        if(obj instanceof Thread) {
            ((Thread)obj).setName(name.toString());
        }
    }

    /**
     * Binds an Object <code>obj</code> to the Name <code>name</code> 
     * overwriting any existing binding.  All intermediate Contexts must
     * already exist, or a NamingException is thrown.
     * 
     * @param name the String of the object to be bound.
     * @param obj the Object to be bound.
     * @throws NamingException if the object cannot be bound or a naming 
     *         exception is encountered.
     * @see javax.naming.Context#rebind(java.lang.String,java.lang.Object)
     */
    public void rebind(String name, Object obj) throws NamingException {
        NameParser nameParser = getNameParser((Name)null);
        rebind(nameParser.parse(name), obj);
    }

    /**
     * Binds a new name to the object bound to an old name and unbinds the old 
     * name Both names are relative to this context.  The method can throw a 
     * ThreadIsRunningException if either the {@link ExtendedRunnable} bound 
     * to either the old name or the new name are still running.
     * 
     * @param oldName the name of the existing binding. It must not be empty
     * @param newName the name of the new binding.  It must not be empty.
     * @throws NamingException if a naming exception is encountered.
     * @throws NameAlreadyBoundException if the name is already used. 
     * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
     */
    public void rename(Name oldName, Name newName) throws NamingException {
        /* Confirm that this works.  We might have to catch the exception */
        Object old = lookup(oldName);
        super.rename(oldName, newName);
        /*  If the object is a Thread, give it the new name. */
        if(old instanceof Thread) {
            ((Thread)old).setName(newName.toString());
        }
    }

    /**
     * Binds a new name to the object bound to an old name and unbinds the old 
     * name. Both names are relative to this context.
     * 
     * @param oldName the name of the existing binding. It must not be empty
     * @param newName the name of the new binding.  It must not be empty.
     * @throws NamingException if a naming exception is encountered.
     * @throws NameAlreadyBoundException if the name is already used. 
     * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
     */
    public void rename(String oldName, String newName) throws NamingException {
        NameParser nameParser = getNameParser((Name)null);
        rename(nameParser.parse(oldName), nameParser.parse(newName));
    }

    /**
     * Destroy the named subcontext from this context.  The name must be 
     * relative to this context.  All ExtendedThreads in the context are 
     * stopped before the context is destroyed. 
     * 
     * @param name the name of the context to be destroyed.
     * @throws NamingException if a naming exception is encountered.
     */
    public void destroySubcontext(Name name) throws NamingException {
        Object obj = lookup(name);
        /* 
         * We want to make sure that all of the threads are stopped before 
         * destroying the context.  Otherwise we can end up with dangling 
         * threads.
         */
        if((obj instanceof Context)) {
            ((Context)obj).close();
            super.destroySubcontext(name);
        }
        return;
    }

    /**
     * Destroy the named subcontext from this context.  The name must be 
     * relative to this context. 
     * 
     * @param name the name of the context to be destroyed.
     * @throws NamingException if a naming exception is encountered.
     */
    public void destroySubcontext(String name) throws NamingException {
        NameParser nameParser = getNameParser((Name)null);
        destroySubcontext(nameParser.parse(name));
    }

    /**
     * Creates and binds a new threading context.  The context is bound to  
     * <code>name</code>.  All intermediate contexts and the target context
     * must already exist.
     * 
     * @param name The name of the new context.
     * @return the newly created ThreadContext
     * @throws NameAlreadyBoundException if <code>name</code is already bound.
     * @throws NameNotFoundException if the target or intermediate context 
     *         does not already exist.
     * @throws NamingException if another naming exception occurs.
     * @see javax.naming.Context#createSubcontext(javax.naming.Name)
     */
    public Context createSubcontext(Name name) throws NamingException {
        Name contextName = getNameParser((Name)null).parse(getNameInNamespace());
        contextName.addAll(name);
        ThreadContext newContext = new ThreadContext(this, name);
        bind(name, newContext);
        return newContext;
    }

    /**
     * Creates and binds a new threading context.  The context is bound to  
     * <code>name</code>.  All intermediate contexts and the target context
     * must already exist.
     * 
     * @param name The name of the new context.
     * @return the newly created ThreadContext
     * @throws NameAlreadyBoundException if <code>name</code is already bound.
     * @throws NameNotFoundException if the target or intermediate context 
     *         does not already exist.
     * @throws NamingException if another naming exception occurs.
     * @see javax.naming.Context#createSubcontext(java.lang.String)
     */
    public Context createSubcontext(String name) throws NamingException {
        NameParser nameParser = getNameParser((Name)null);
        return createSubcontext(nameParser.parse(name));
    }

    /**
     * Close this context.  Closing the context will attempt to abort all 
     * of the Threads in this context, and all subcontexts.  The subcontexts
     * will also be closed.
     * 
     * @throws NamingException if a naming exception is encountered.
     * @see javax.naming.Context#close()
     */
    public void close() throws NamingException {
        /* Don't try anything if we're already in the process of closing */
        if(closing) {
            return;
        }
        setAbort("", true);
        Collection threads = new ArrayList();
        NamingEnumeration ne = list("");
        /* 
         * Go through the list.  
         * * Tell all subcontexts to close
         * * Get the list of all threads in this context (but not decendants).
         */
        while(ne.hasMore()) {
            Object next = ne.next();
            if(next instanceof Context ) {
                ((Context)next).close();
                continue;
            }
            if(next instanceof ExtendedRunnable) {
                threads.add(next);
                continue;
            }
        }
        
        while(threads.size() > 0) {
            Iterator it = threads.iterator();
            while(it.hasNext()) {
                ExtendedThread thread = (ExtendedThread)ne.next();
                thread.setAbort(true);
                if(!thread.isAlive()) {
                    threads.remove(thread);
                    continue;
                }
            }
            /* Wait for a second and try again if there us anything left.*/
            /*
             * TODO: Wait time shoudl be based upon a value in the environment
             *       of the context.
             */
            synchronized(this) {
                try {
                    wait(1000);
                } catch(InterruptedException e) {
                    /* TODO: Something ebetter needs done here. */
                    e.printStackTrace();
                }
            }
        }
    }
}

