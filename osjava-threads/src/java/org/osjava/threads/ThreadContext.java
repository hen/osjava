/*
 * org.osjava.threads.ThreadContext
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$ 
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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.osjava.naming.ContextBindings;
import org.osjava.naming.ContextNames;

/**
 * A Context for managing Threads and ThreadGroups.
 * 
 * @author Robert M. Zigweid
 * @version $Rev$ $LastChangedDate$
 * @since OSJava Threads 2.0
 */
public class ThreadContext
    implements Context {

    private Map        contextStore = new HashMap();

    private NameParser nameParser   = null;

    protected ThreadContext() {
        nameParser = new ThreadNameParser(this);
    }

    /**
     * Return a {@link Thread}implementing {@link ExtendedRunnable}
     * {@link Runnable},{@link ExtendedThread}or {@link ExtendedThreadGroup}
     * associated with the {@link Name}<code>name</code>.
     * 
     * @param name the Name to lookup.
     * @return a Thread with an ExtendedRunnable Runnable, ExtendedThread, or
     *          ExtendedThreadGroup
     * @throws NamingException if the
     * @see javax.naming.Context#lookup(javax.naming.Name)
     */
    public Object lookup(Name name) throws NamingException {
        return contextStore.get(name);
    }

    /**
     * Return a {@link Thread}implmenting {@link ExtendedRunnable}
     * {@link Runnable},{@link ExtendedThread}or {@link ExtendedThreadGroup}
     * associated with the {@link String}<code>name</code>.
     * 
     * @param name the Name to lookup.
     * @return a Thread with an ExtendedRunnable Runnable, ExtendedThread, or
     *          ExtendedThreadGroup
     * @see javax.naming.Context#lookup(java.lang.String)
     */
    public Object lookup(String name) throws NamingException {
        return lookup(nameParser.parse(name));
    }

    public void bind(Name name, Object obj) throws NamingException {
        /* Determine first if the name is already bound */
        if(contextStore.containsKey(name)) {
            throw new NameAlreadyBoundException("Name " + name.toString()
                + " already bound");
        }

        /* Handle obj being an ExtendedThreadGroup */
        if(obj instanceof ExtendedThreadGroup) {
            ExtendedThreadGroup group = (ExtendedThreadGroup) obj;
            /* Ensure tha name of the ExtendedThreadGroup matches name */
            if( !name.toString().equals(group.getName())) {
                throw new NamingException(
                    "ExtendedThreadGroup name must match the name being bound to.");
            }
            /* Make sure tha it is properly parented. */
            if( !group.getParent().equals(
                ( contextStore.get(name.getPrefix(name.size() - 1)) ))) {
                throw new NamingException(
                    "Cannot bind name because the parent of "
                        + "the ExtendedThreadGroup is imporperly named. ");
            }
            /*
             * bind it and return.
             */
            contextStore.put(name, group);
            return;
        }
        if(obj instanceof ExtendedRunnable) {
            /* Handling for ExtendedThread */
            if(obj instanceof ExtendedThread) {
                ExtendedThread thread = (ExtendedThread)obj;
                if(thread.getThreadGroup() != 
                    contextStore.get(name.getPrefix(name.size() - 1))) {
                    throw new NamingException("Cannot bind Thread because its " +
                            "ThreadGroup is not bound in the ThreadContext.");
                }
                contextStore.put(name, thread);
            }
            /* Handling for Thread */
            if(obj instanceof Thread) {
                Thread thread = (Thread) obj;
                if(thread.getThreadGroup() != 
                    contextStore.get(name.getPrefix(name.size() - 1))) {
                    throw new NamingException("Cannot bind Thread because its " +
                            "ThreadGroup is not bound in the ThreadContext.");
                }
                contextStore.put(name, thread);
                return;
            }
        }
    }

    public void bind(String name, Object obj) throws NamingException {
        bind(nameParser.parse(name), obj);
    }

    /**
     * Method not implemented in ThreadContext
     */
    public void rebind(Name name, Object obj) throws NamingException {
        /* Shouldn't this throw an exception */
        return;
    }

    /**
     * Method not implemented in ThreadContext
     */
    public void rebind(String name, Object obj) throws NamingException {
        /* Shouldn't this throw an exception? */
        return;
    }

    /** 
     * Unbind a Thread from the ThreadContext.
     * 
     * @see javax.naming.Context#unbind(javax.naming.Name)
     */
    public void unbind(Name name) throws NamingException {
        Object obj = lookup(name);
        if(obj == null) {
           return;
        }
    }

    /**
     * Unbind a Threadfrom the ThreadContext/
     * 
     * @see javax.naming.Context#unbind(java.lang.String)
     */
    public void unbind(String name) throws NamingException {
            unbind(nameParser.parse(name));
    }

    /**
     * Method not implemented.
     * 
     * @see javax.naming.Context#rename(javax.naming.Name, javax.naming.Name)
     */
    public void rename(Name oldName, Name newName) throws NamingException {
    }

    /**
     * Method not implemented
     * 
     * @see javax.naming.Context#rename(java.lang.String, java.lang.String)
     */
    public void rename(String oldName, String newName) throws NamingException {
    }

    /*
     * @see javax.naming.Context#list(javax.naming.Name)
     */
    public NamingEnumeration list(Name name) throws NamingException {
        if("".equals(name)) {
            // here we should return a list of the directories and prop files 
            // minus the .properties that are in the root directory
            return new ContextNames((Map)((HashMap)contextStore).clone());
        }

        Object target = lookup(name);
        if(target instanceof Context) {
            return ((Context)target).list("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#list(java.lang.String)
     */
    public NamingEnumeration list(String name) throws NamingException {
        return list(nameParser.parse(name));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#listBindings(javax.naming.Name)
     */
    public NamingEnumeration listBindings(Name name) throws NamingException {
        if("".equals(name)) {
            // here we should return a list of the directories and prop files 
            // minus the .properties that are in the root directory
            return new ContextBindings((Map)((HashMap)contextStore).clone());
        }

        Object target = lookup(name);
        if(target instanceof Context) {
            return ((Context)target).list("");
        }
        throw new NotContextException("Bindings of " + name + " cannot be listed");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#listBindings(java.lang.String)
     */
    public NamingEnumeration listBindings(String name) throws NamingException {
        return listBindings(nameParser.parse(name));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#destroySubcontext(javax.naming.Name)
     */
    public void destroySubcontext(Name name) throws NamingException {
    // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#destroySubcontext(java.lang.String)
     */
    public void destroySubcontext(String name) throws NamingException {
    // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#createSubcontext(javax.naming.Name)
     */
    public Context createSubcontext(Name name) throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#createSubcontext(java.lang.String)
     */
    public Context createSubcontext(String name) throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#lookupLink(javax.naming.Name)
     */
    public Object lookupLink(Name name) throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#lookupLink(java.lang.String)
     */
    public Object lookupLink(String name) throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#getNameParser(javax.naming.Name)
     */
    public NameParser getNameParser(Name name) throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#getNameParser(java.lang.String)
     */
    public NameParser getNameParser(String name) throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#composeName(javax.naming.Name,
     *      javax.naming.Name)
     */
    public Name composeName(Name name, Name prefix) throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
     */
    public String composeName(String name, String prefix)
        throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#addToEnvironment(java.lang.String,
     *      java.lang.Object)
     */
    public Object addToEnvironment(String propName, Object propVal)
        throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#removeFromEnvironment(java.lang.String)
     */
    public Object removeFromEnvironment(String propName) throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#getEnvironment()
     */
    public Hashtable getEnvironment() throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#close()
     */
    public void close() throws NamingException {
    // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#getNameInNamespace()
     */
    public String getNameInNamespace() throws NamingException {
        // TODO Auto-generated method stub
        return null;
    }

}

