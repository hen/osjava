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
import javax.naming.OperationNotSupportedException;

import org.osjava.naming.ContextBindings;
import org.osjava.naming.ContextNames;

/**
 * A Context for managing Threads and ThreadGroups.  The ThreadContext attempts
 * to keep synchronized with an underlying {@link ExtendedThreadGroup 
 * ExtendedThreadGroups}.
 * <br/><br/>
 * This context depends upon the Simple-JNDI <b>TODO: VERSION OF SIMPLE_JNDI</b>
 * package available from http://www.osjava.org.
 * <br/><br/>
 * This Context does not implement subcontexts because that would severely 
 * affect the way that this Context type works with ThreadGroups.  This is 
 * an issue that will be dealt with if the need arises and an adequate 
 * method of accoplishing it can be determined.
 * 
 * @author Robert M. Zigweid
 * @version $Rev$ $LastChangedDate$
 * @since OSJava Threads 2.0
 */

public class ThreadContext
    implements Context {

    /********************** 
     * Field Declarations *
     **********************/
    /* 
     * The Map that backs the context.  All of the bindings are stored here.
     */
    private Map contextStore = new HashMap();

    /* 
     * The root context of this context.  
     */
    private Context rootContext = null;
    
    /* 
     * The name of this context.  There are potential synchronization issues
     * here. The topmost context has no name.  "" will match it, however.
     */
    private Name myName = null;
    
    /* 
     * The NameParser utilized by the Context.
     */
    private NameParser nameParser = null;

    /**
     * Create a root level ThreadContext.
     * 
     * @throws NamingException if a name exception is encountered.
     */
    protected ThreadContext() throws NamingException {
        nameParser = new ThreadNameParser(this);
        rootContext = this;
    }
    
    /****************
     * Constructors *
     ****************/
    
    /** 
     * Create a Threadcontext with a parent ThreadContext <code>parent</code>
     * and the Name <code>name</code>.  If <code>root</code> is null, the 
     * newly created ThreadContext will be a root context, and <code>name
     * </code> must have a size of exactly 1 or a NamingException is bound. 
     * 
     * @param root the Context that is the parent of this ThreadContext.
     * @param name the Name of the newly created ThreadContext
     * @throws NamingException when a name exception is encountered.
     */
    protected ThreadContext(Context root, Name name) throws NamingException {
        /* A context without a name is disallowed. */
        if(name == null || name.size() == 0) {
            throw new NamingException("ThreadContext names must not be null or empty");
        }
        /* 
         * If root is null, we accept the defaults of all of the variables.
         */
        if(root == null) {
            if(name.size() != 1) {
                throw new NamingException("Invalid name '" + name.toString() + "' for root ThreadContext.");
            }
        } else {
            rootContext = root;
            nameParser = new ThreadNameParser(this);
        }
        
        /* 
         * TODO: Not happy with how this looks up the NameParser.  It's based 
         *       upon the root, but looks it up based upon the name.  For 
         *       now, it is okay because it's assumed to be the same name 
         *       parser for the entire hierarchy of Contexts.
         */
        nameParser = root.getNameParser(name);
        myName = name;
    }
    
    /* ************************
     * Class Specific Methods *
     * ************************/
    
    /**
     * Set the name of the ThreadContext.  This method is a point at which
     * the Context hierarchy can be thrown out of sync.  It is assumed that
     * such checks will be made when calling this method.
     * 
     * @param name the new Name of the ThreadContext.
     */
    protected void setName(Name name) {
        myName = name;
    }

    /**
     * Get the name of the ThreadContext
     * @return the Name of the context.
     */
    public Name getName() {
        return myName;
    }
        
    /*********************************************
     * Methods required by implementing Context. *
     *********************************************/
    
    /**
     * Return a {@link Thread} implementing {@link ExtendedRunnable}
     * {@link Runnable},{@link ExtendedThread} or {@link ExtendedThreadGroup}
     * associated with the {@link Name}<code>name</code>.
     * 
     * @param name the Name to lookup.
     * @return a Thread with an ExtendedRunnable Runnable, ExtendedThread, or
     *          ExtendedThreadGroup
     * @throws NamingException when a name exception is encountered.
     * @see javax.naming.Context#lookup(javax.naming.Name)
     */
    public Object lookup(Name name) throws NamingException {
        /*
         * If name is empty then this context is to be cloned.  This is 
         * required based upon the javadoc of Context.  UGH!
         */
        if(name.size() == 0) {
            try {
                return (ThreadContext)this.clone();
            } catch(CloneNotSupportedException e) {
                /* 
                 * TODO: Improve error handling.  I'm not quite sure yet what 
                 *       should be done, but this almost certainly isn't it.
                 */
                e.printStackTrace();
            }
        }
        
        /* Try looking in this context first.*/
        if(name.startsWith(myName)) {
            Name item = name.getSuffix(myName.size());
            /* Look for the name as it would exist in the context store */
            if(contextStore.containsKey(item)) {
                return contextStore.get(item);
            }       
            /* 
             * Look for a sub context that might have the name.  We only need to 
             * look one level deeper than our name, because it is enforced that
             * subcontexts must be named in a hierarchial fashion. If we don't 
             * have a match, return null. 
             */ 
            Name subContext = item.getPrefix(1);
            if(!contextStore.containsKey(subContext)) {
                return null;
            }
            Object context = (Context)contextStore.get(subContext);
            if(!(context instanceof Context)) {
                throw new NamingException("Could not find '" + name.toString() + "' from ThreadContext '" 
                                + myName.toString() + "'.");
            }
            
            /* Pass it to the context to lookup */
            return ((Context)context).lookup(name);
        }
        
        /* 
         * If the name being looked up doesn't start with the same thing as 
         * the name of this context, try to look for the root context.
         */
        if(rootContext != null) {
            Object ret = rootContext.lookup(name);
            if(ret == null) {
                throw new NamingException("Could not find '" + name.toString() + "' from ThreadContext '"                                 
                                + myName.toString() + "'.");
            }
            return ret;
        }
        /* Throw an exception because we couldn't find the name here. */
        throw new NamingException("Could not find '" + name.toString() + "' from ThreadContext '" 
  
                        + myName.toString() + "'.");
    }

    /**
     * Return a {@link Thread} implmenting {@link ExtendedRunnable}
     * {@link Runnable},{@link ExtendedThread} or {@link ExtendedThreadGroup}
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

    /**
     * Bind the Object <code>obj</code> to the Name <code>name</code>.  The object
     * must be an {@link ExtendedRunnable}, or {@link ThreadContext}. 
     * 
     * @param name The name to bind the Object to.
     * @param obj The object that is being bound in the ThreadContext.
     * @throws NamingException if the object cannot be bound to the name.
     * @see javax.naming.Context#bind(javax.naming.Name, java.lang.Object)
     */
    /* ThreadGroups and ExtendedThreadGroups are no longer allowed to be added
     * to the ThreadContext.
     */
    public void bind(Name name, Object obj) throws NamingException {
        /* Determine first if the name is already bound */
        if(contextStore.containsKey(name)) {
            throw new NameAlreadyBoundException("Name " + name.toString()
                + " already bound");
        }
        
        /* 
         * If the name of obj doesn't start with the name of this context, 
         * it is an error, throw a NamingException
         */
        if(!name.startsWith(myName)) {
            /* I'm pretty unhappy with this exception message */
            throw new NamingException("Cannot bind name '" + name + "' to ThreadContext '" + myName + "'.");
        }
        /*
         * Determine if the context that the object is going to already
         * exists and return.  This is determined by the first n-1 elements
         * of the name.
         */
        Object context = lookup(name.getPrefix(name.size() - 1));
        if(context instanceof Context) {
            ((Context)context).bind(name, obj);
            return;
        }
        
        /*
         * Only the following types are allowed to be bound through this 
         * method: 
         *      ExtendedRunnable
         *      ThreadContext
         */
        if(   obj instanceof ExtendedRunnable
           || obj instanceof ThreadContext) {
            contextStore.put(name, obj);
        }
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
        bind(nameParser.parse(name), obj);
    }

    /** 
     * Unbind a Thread from the ThreadContext.
     * <br/><br/>
     * <b>NOTE:</b> This does not destroy the object that was bound in the 
     * ThreadContext.  It only removes it from the ThreadContext.
     * 
     * @param name the name of the object to unbind from the ThreadContext.
     * @throws NamingException if a name exception is encountered.
     * @see javax.naming.Context#unbind(javax.naming.Name)
     */
    /*
     * XXX: It is possible that this method should actually stop a thread
     *      or threads if the object referred to is a context 
     */
    public void unbind(Name name) throws NamingException {
        Object obj = lookup(name);
        if(obj != null) {
           contextStore.remove(name);
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
     * @see javax.naming.Context@rebind(javax.naming.Name, java.lang.Object)
     */
    public void rebind(Name name, Object obj) throws NamingException {
        /* Look up the target context first. */
        Object targetContext = lookup(name.getPrefix(name.size() - 1));
        if(targetContext == null || !(targetContext instanceof Context)) {
            throw new NamingException("Cannot bind object.  Target context does not exist.");
        }
        unbind(name);
        bind(name, obj);
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
     * @see javax.naming.Context@rebind(java.lang.String, java.lang.Object)
     */
    public void rebind(String name, Object obj) throws NamingException {
        rebind(nameParser.parse(name), obj);
    }

    /**
     * Binds a new name to the object bound to an old name and unbinds the old 
     * name. Both names are relative to this context.
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
        if(old == null) {
            throw new NamingException("Name '" + oldName + "' not found in ThreadContext '" + myName + "'.");
        }
        
        /* If the new name is bound throw a NameAlreadyBoundException */
        if(lookup(newName) != null) {
            throw new NameAlreadyBoundException("Name '" + newName + "' already bound");
        }
        
        /* All clear? */
        unbind(oldName);
        
        /* 
         * If the object is a Thread, or a ThreadContext, give it the new 
         * name.
         */
        if(old instanceof Thread) {
            ((Thread)old).setName(newName.toString());
        } else if(old instanceof ThreadContext) {
            ((ThreadContext)old).setName(newName);
        }
        bind(newName, old);
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
    /*
     * TODO: Implement me!  It will not be possible to create an 
     *       implementation that renames ExtendedThreadGroups.  to implement
     *       this a new ExtendedThreadGroup will have to be created and the
     *       Threads and ThreadGroups that are part of it will have to be 
     *       reassigned.  I'm not entirely sure that Threads can be moved
     *       to new ThreadGroups.  
     *       Threads can be easily renamed so are not an issue.
     */
    public void rename(String oldName, String newName) throws NamingException {
        rename(nameParser.parse(oldName), nameParser.parse(newName));
    }

    /**
     * Enumerates the names bound to the context as well as the class names
     * of the objects bound to them.  The contents of subcontexts are not 
     * included.
     * 
     * @param name The name of the context to list.
     * @return an enumeration of the bindings of the context.  Elements of 
     *         the enumeration are of the type <code>NameClassPair</code>
     * @throws NamingException if a naming exceptino is encountered.
     * @see javax.naming.Context#list(javax.naming.Name)
     */
    public NamingEnumeration list(Name name) throws NamingException {
        if("".equals(name)) {
            NamingEnumeration enum = new ContextNames(contextStore);
            return enum;
        }
        Object target = lookup(name);
        if(target instanceof Context) {
            return ((Context)target).list("");
        }
        throw new NotContextException(name + " cannot be listed");
    }

    /**
     * Enumerates the names bound to the context as well as the class names
     * of the objects bound to them.  The contents of subcontexts are not 
     * included.
     * 
     * @param name The name of the context to list.
     * @return an enumeration of the bindings of the context.  Elements of 
     *         the enumeration are of the type <code>NameClassPair</code>
     * @throws NamingException if a naming exceptino is encountered.
     * @see javax.naming.Context#list(java.lang.String)
     */
    public NamingEnumeration list(String name) throws NamingException {
        return list(nameParser.parse(name));
    }

    /**
     * Enumerates the names bound to the context as well as the objects bound
     * to them.
     * 
     * @param name the name of the context to list.
     * @return an enumeration of the bindings of the context.  Elements of
     *         the enumeration are of the type <code>Binding</code>.
     * @throws NamingException if a naming exception is encountered.
     * @see javax.naming.Context#listBindings(javax.naming.Name)
     */
    public NamingEnumeration listBindings(Name name) throws NamingException {
        if("".equals(name)) {
            return new ContextBindings((Map)((HashMap)contextStore).clone());
        }

        Object target = lookup(name);
        if(target instanceof Context) {
            return ((Context)target).list("");
        }
        throw new NotContextException("Bindings of " + name + " cannot be listed");
    }

    /**
     * Enumerates the names bound to the context as well as the objects bound
     * to them.
     * 
     * @param name the name of the context to list.
     * @return an enumeration of the bindings of the context.  Elements of
     *         the enumeration are of the type <code>Binding</code>.
     * @throws NamingException if a naming exception is encountered.
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
        throw new OperationNotSupportedException("destroySubcontext() not supported.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#destroySubcontext(java.lang.String)
     */
    public void destroySubcontext(String name) throws NamingException {
        throw new OperationNotSupportedException("destroySubcontext() not supported.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#createSubcontext(javax.naming.Name)
     */
    public Context createSubcontext(Name name) throws NamingException {
        throw new OperationNotSupportedException("createSubcontext() not supported.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#createSubcontext(java.lang.String)
     */
    public Context createSubcontext(String name) throws NamingException {
        throw new OperationNotSupportedException("createSubcontext() not supported.");
    }

    /**
     * Retrieve the named object following links.
     * This essentialy calls
     * 
     * @see javax.naming.Context#lookupLink(javax.naming.Name)
     */
    public Object lookupLink(Name name) throws NamingException {
        return lookup(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#lookupLink(java.lang.String)
     */
    public Object lookupLink(String name) throws NamingException {
        return lookup(nameParser.parse(name));
    }

    /**
     * Return the parser that is associated with the context.
     * 
     * @see javax.naming.Context#getNameParser(javax.naming.Name)
     */
    public NameParser getNameParser(Name name) throws NamingException {
        /* I'm not entirely sure that I like this implementation.  Given that 
         * there shouldn't really be sub-Contexts, becau    se of the existance of 
         * the ExtendedThreadGroup, there shouldn't really be a sub-Context. */
        return nameParser;
    }

    /**
     * (non-Javadoc)
     * 
     * @see javax.naming.Context#getNameParser(java.lang.String)
     */
    public NameParser getNameParser(String name) throws NamingException {
        return getNameParser(nameParser.parse(name));
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

