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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;

import org.osjava.naming.ContextBindings;
import org.osjava.naming.ContextNames;
import org.osjava.naming.InvalidObjectTypeException;

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
     * The map of sub contexts. 
     */
    private Map subContexts = new HashMap();
            
    /* 
     * The NameParser utilized by the Context.
     */
    private ThreadNameParser nameParser = null;

    /****************
     * Constructors *
     ****************/
    /**
     * Create a ThreadContext.
     * 
     * @throws NamingException if a naming exception is encountered.
     */
    protected ThreadContext() throws NamingException{
        nameParser = new ThreadNameParser(this);
    }
        
    /* ************************
     * Class Specific Methods *
     * ************************/
            
    /* *******************************************
     * Methods required by implementing Context. *
     * *******************************************/
    
    /**
     * Return a {@link Thread} implementing {@link ExtendedRunnable}
     * {@link Runnable},{@link ExtendedThread} or {@link ExtendedThreadGroup}
     * associated with the {@link Name}<code>name</code>.  The Name passed is
     * relative to this context.
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
        
        Name objName = name.getPrefix(1);
        if(name.size() > 1) {
            /* Look in a subcontext. */
            if(subContexts.containsKey(objName)) {
                return ((Context)subContexts.get(objName)).lookup(name.getSuffix(1));
            } 
            /* TODO: Might need to do a littl emore work here and supply a 
             * reasonable message. */
            throw new NamingException();
        }
        return contextStore.get(objName);
    }

    /**
     * Return a {@link Thread} implmenting {@link ExtendedRunnable}
     * {@link Runnable},{@link ExtendedThread} or {@link ExtendedThreadGroup}
     * associated with the {@link String}<code>name</code>.  The name is
     * relative to this context.
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
     * Bind the Object <code>obj</code> to the Name <code>name</code>.  The 
     * object must be an {@link ExtendedRunnable}, or {@link ThreadContext}.
     * The Name <code>name</code> is relative to this context.  
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
        /* 
         * If the name of obj doesn't start with the name of this context, 
         * it is an error, throw a NamingException
         */
        if(name.size() > 1) {
            Name prefix = name.getPrefix(1);
            if(subContexts.containsKey(prefix)) {
                ((Context)subContexts.get(prefix)).bind(name.getSuffix(1), obj);
                return;
            }
        }

        /* Determine if the name is already bound */
        if(contextStore.containsKey(name)) {
            throw new NameAlreadyBoundException("Name " + name.toString()
                + " already bound");
        }
        
        /* 
         * If the thread is an instance of Thread, make sure that it is not 
         * alive.
         */
        if(obj instanceof Thread &&
           ((Thread)obj).isAlive()) {
            throw new ThreadIsRunningException("A thread stored in the context cannot already be running.");
        }
        
        /*
         * Only the following types are allowed to be bound through this 
         * method: 
         *      ExtendedRunnable
         *      ThreadContext
         */
        if(obj instanceof ExtendedRunnable) {
            contextStore.put(name, obj);
        }
        
        throw new InvalidObjectTypeException("Objects in this context must implement org.osjava.threads.ExtendedRunnable");
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
        ExtendedRunnable thread = (ExtendedRunnable)lookup(name);
        
        /* 
         * If the thread is an instance of Thread, make sure that it is not 
         * alive.
         */
        if(thread instanceof Thread &&
           ((Thread)thread).isAlive()) {
            throw new ThreadIsRunningException("A running thread cannot be removed from the context.");
        }
        if(thread != null) {
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
     * @see javax.naming.Context#rebind(javax.naming.Name,java.lang.Object)
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
     * @see javax.naming.Context#rebind(java.lang.String,java.lang.Object)
     */
    public void rebind(String name, Object obj) throws NamingException {
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
        if(old == null) {
            throw new NamingException("Name '" + oldName + "' not found.");
        }

        /* If the new name is bound throw a NameAlreadyBoundException */
        if(lookup(newName) != null) {
            throw new NameAlreadyBoundException("Name '" + newName + "' already bound");
        }

        unbind(oldName);
        unbind(newName);
        bind(newName, old);
        /* 
         * If the object is a Thread, or a ThreadContext, give it the new 
         * name.
         */
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
     * @throws NotContextException if the name being pointed to is not a 
     *         context
     * @see javax.naming.Context#list(javax.naming.Name)
     */
    public NamingEnumeration list(Name name) throws NamingException {
        if(name.isEmpty()) {
            /* 
             * Because there are two mappings that need to be used here, 
             * create a new mapping and add the two maps to it.  This also 
             * adds the safety of cloning the two maps so the original is
             * unharmed.
             */
            Map enumStore = new HashMap();
            enumStore.putAll(contextStore);
            enumStore.putAll(subContexts);
            NamingEnumeration enumerator = new ContextNames(enumStore);
            return enumerator;
        }
        /* Look for a subcontext */
        Name subName = name.getPrefix(1);
        if(contextStore.containsKey(subName)) {
            /* Nope, actual object */
            throw new NotContextException(name + " cannot be listed");
        }
        if(subContexts.containsKey(subName)) {
            return ((Context)subContexts.get(subName)).list(name.getSuffix(1));
        }
        /* 
         * Couldn't find the subcontext and it wasn't pointing at us, throw
         * an exception.
         */
        throw new NamingException();
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
     * @throws NotContextException if the name being pointed to is not a 
     *         context
     * @see javax.naming.Context#list(java.lang.String)
     */
    public NamingEnumeration list(String name) throws NamingException {
        return list(nameParser.parse(name));
    }

    /**
     * Enumerates the names bound to the context as well as the objects bound
     * to them.  The contents of subcontexts are not included.
     * 
     * @param name the name of the context to list.
     * @return an enumeration of the bindings of the context.  Elements of
     *         the enumeration are of the type <code>Binding</code>.
     * @throws NamingException if a naming exception is encountered.
     * @throws NotContextException if the name being pointed to is not a 
     *         context
     * @see javax.naming.Context#listBindings(javax.naming.Name)
     */
    public NamingEnumeration listBindings(Name name) throws NamingException {
        if("".equals(name)) {
            /* 
             * Because there are two mappings that need to be used here, 
             * create a new mapping and add the two maps to it.  This also 
             * adds the safety of cloning the two maps so the original is
             * unharmed.
             */
            Map enumStore = new HashMap();
            enumStore.putAll(contextStore);
            enumStore.putAll(subContexts);
            return new ContextBindings(enumStore);
        }
        /* Look for a subcontext */
        Name subName = name.getPrefix(1);
        if(contextStore.containsKey(subName)) {
            /* Nope, actual object */
            throw new NotContextException(name + " cannot be listed");
        }
        if(subContexts.containsKey(subName)) {
            return ((Context)subContexts.get(subName)).listBindings(name.getSuffix(1));
        }
        /* 
         * Couldn't find the subcontext and it wasn't pointing at us, throw
         * an exception.
         */
        throw new NamingException();
    }

    /**
     * Enumerates the names bound to the context as well as the objects bound
     * to them.  The contents of subcontexts are not included.
     * 
     * @param name the name of the context to list.
     * @return an enumeration of the bindings of the context.  Elements of
     *         the enumeration are of the type <code>Binding</code>.
     * @throws NamingException if a naming exception is encountered.
     * @throws NotContextException if the name being pointed to is not a 
     *         context
     * @see javax.naming.Context#listBindings(java.lang.String)
     */
    public NamingEnumeration listBindings(String name) throws NamingException {
        return listBindings(nameParser.parse(name));
    }

    /**
     * Destroy the named subcontext from this context.  The name must be 
     * relative to this context. 
     * 
     * @param name the name of the context to be destroyed.
     * @throws NamingException if a naming exception is encountered.
     */
    public void destroySubcontext(Name name) throws NamingException {        
        if(name.size() > 1) {
            if(subContexts.containsKey(name.getPrefix(1))) {
                Context subContext = (Context)subContexts.get(name.getPrefix(1));
                subContext.destroySubcontext(name.getSuffix(1));
                return;
            } 
            /* TODO: Better message might be necessary */
            throw new NameNotFoundException();
        }
        /* Look at the contextStore to see if the name is bound there */
        if(contextStore.containsKey(name)) {
            throw new NotContextException();
        }
        /* Look for the subcontext */
        if(!subContexts.containsKey(name)) {
            throw new NameNotFoundException();
        }
        Context subContext = (Context)subContexts.get(name); 
        /* Look to see if the context is empty */
        NamingEnumeration names = subContext.list("");
        if(names.hasMore()) {
            throw new ContextNotEmptyException();
        }
        subContexts.remove(name);
    }

    /**
     * Destroy the named subcontext from this context.  The name must be 
     * relative to this context. 
     * 
     * @param name the name of the context to be destroyed.
     * @throws NamingException if a naming exception is encountered.
     */
    public void destroySubcontext(String name) throws NamingException {
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
        ThreadContext newContext;
        if(name.size() > 1) {
            if(subContexts.containsKey(name.getPrefix(1))) {
                Context subContext = (Context)subContexts.get(name.getPrefix(1));
                newContext = (ThreadContext)subContext.createSubcontext(name.getSuffix(1));
                return newContext;
            } 
            throw new NameNotFoundException("The subcontext " + name.getPrefix(1) + " was not found.");
        }
        
        if(contextStore.containsKey(name) || subContexts.containsKey(name)) {
            throw new NameAlreadyBoundException();
        }

        newContext = new ThreadContext();
        subContexts.put(name, newContext);
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
        return createSubcontext(nameParser.parse(name));
    }

    /**
     * Retrieve the named object following links.  The <code>name</code> must
     * not be empty.  This essentialy calls {@link #lookup(javax.naming.Name)}.
     * 
     * @param name The name to be looked up.
     * @return The object bound to <code>name</code>.
     * @throws NamingException if a naming exception is encountered.
     * @see javax.naming.Context#lookupLink(javax.naming.Name)
     */
    public Object lookupLink(Name name) throws NamingException {
        return lookup(name);
    }

    /**
     * Retrieve the named object following links.  The <code>name</code> must
     * not be empty.  This essentialy calls {@link #lookup(javax.naming.Name)}.
     * 
     * @param name The name to be looked up.
     * @return The object bound to <code>name</code>.
     * @throws NamingException if a naming exception is encountered.
     * @see javax.naming.Context#lookupLink(javax.naming.Name)
     */
    public Object lookupLink(String name) throws NamingException {
        return lookup(nameParser.parse(name));
    }

    /**
     * Return the parser that is associated with the context.
     * 
     * @param name The name of the context to get the name parser from
     * @return the name parser associated with the named context.
     * @throws NotContextException if the object bound to <code>name</code>
     *         is not a context.
     * @throws NamingException if a naming exception is encountered.
     * @see javax.naming.Context#getNameParser(javax.naming.Name)
     */
    public NameParser getNameParser(Name name) throws NamingException {
        if(name.isEmpty() ) {
            return nameParser;
        }
        Name subName = name.getPrefix(1); 
        if(subContexts.containsKey(subName)) {
            return ((Context)subContexts.get(subName)).getNameParser(name.getSuffix(1));
        }
        throw new NotContextException();
    }

    /**
     * Return the parser that is associated with the context.
     * 
     * @param name The name of the context to get the name parser from
     * @return the name parser associated with the named context.
     * @throws NotContextException if the object bound to <code>name</code>
     *         is not a context.
     * @throws NamingException if a naming exception is encountered.
     * @see javax.naming.Context#getNameParser(java.lang.String)
     */
    public NameParser getNameParser(String name) throws NamingException {
        return getNameParser(nameParser.parse(name));
    }

    /**
     * Create a name from <code>prefix</code> and <code>name</code>, where 
     * prefix is the name of this context.
     * 
     * @param name a name relative to this context.
     * @param prefix the name of this context
     * @return a name which is the composition of <code>prefix</code> and 
     *         <code>name</code>.
     * @throws NamingException if a naming exception is encountered.
     * @see javax.naming.Context#composeName(javax.naming.Name,
     *      javax.naming.Name)
     */
    public Name composeName(Name name, Name prefix) throws NamingException {
        if(name == null || prefix == null) {
            throw new NamingException("Arguments must not be null");
        }
        Name retName = (Name)prefix.clone();
        retName.addAll(name);
        return retName;
    }

    /**
     * Create a name from <code>prefix</code> and <code>name</code>, where 
     * prefix is the name of this context.
     * 
     * @param name a name relative to this context.
     * @param prefix the name of this context
     * @return a name which is the composition of <code>prefix</code> and 
     *         <code>name</code>.
     * @throws NamingException if a naming exception is encountered.
     * @see javax.naming.Context#composeName(java.lang.String, java.lang.String)
     */
    public String composeName(String name, String prefix)
        throws NamingException {
        Name retName = composeName(nameParser.parse(name), nameParser.parse(prefix));
        return nameParser.nameToString(retName);
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

