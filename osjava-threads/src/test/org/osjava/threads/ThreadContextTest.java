/*
 * org.osjava.threads.ThreadContextTest
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Feb 15, 2005
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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import junit.framework.TestCase;

/**
 * @author rzigweid
 */
public class ThreadContextTest extends TestCase {
    private Context context = null;

    /**
     * Constructor for the test case, as defined by Junit.
     * @param arg0
     */
    public ThreadContextTest(String arg0) {
        super(arg0);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        /* Initial configuration voodoo for the default context. */
        Hashtable contextEnv = new Hashtable();
        /* The intial context. */
        contextEnv.put("java.naming.factory.initial", "org.osjava.threads.ThreadContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what
         *  I want. */
        contextEnv.put("jndi.syntax.direction", "left_to_right");
        /* Separatator is required for non-flat */
        contextEnv.put("jndi.syntax.separator", ".");
        
        try {
            /*
             * XXX: This is commonly refered to as cheating to get the object
             *      that I really want instead of the InitialContext object
             */
            context = (Context) new InitialContext(contextEnv).lookup("");
        } catch(NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() {
        try {
            ((ThreadContext)context).close();
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Test to create a thread with a random name and no explicit runnable.
     * This test expects to receive a NullPointerException 
     */
    public void testCreateThread0() {
        try {
            ((ThreadContext)context).createThread(null);
        } catch (NullPointerException e) {
            return;
        } catch (NameAlreadyBoundException e) {
            fail("NameAlreadyBoundException: " + e.getMessage());
        } catch (ThreadIsRunningException e) {
            fail("ThreadIsRunningException: " + e.getMessage());
        } catch (NamingException e) {
            fail("NamingException: " + e.getMessage());
        }
        fail("Should be null");
    }
    
    /**
     * Test to create a thread with a random name and a proper Runnable type,
     * in this case, a Thread object.
     */
    public void testCreateThread1() {
        Thread thread = null;
        try {
            thread = ((ThreadContext)context).createThread(new Thread());
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("NullPointerException: " + e.getMessage());
        } catch (NameAlreadyBoundException e) {
            fail("NameAlreadyBoundException: " + e.getMessage());
        } catch (ThreadIsRunningException e) {
            fail("ThreadIsRunningException: " + e.getMessage());
        } catch (NamingException e) {
            fail("NamingException: " + e.getMessage());
        }
        assertNotNull(thread);
    }
    
    /**
     * Test to create a thread with a specific name and a proper Runnable type,
     * in this case, a Thread object.
     */
    public void testCreateThread2() {
        Thread thread = null;
        try {
            thread = ((ThreadContext)context).createThread(new Thread(), "SomeName");
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("NullPointerException: " + e.getMessage());
        } catch (NameAlreadyBoundException e) {
            fail("NameAlreadyBoundException: " + e.getMessage());
        } catch (ThreadIsRunningException e) {
            fail("ThreadIsRunningException: " + e.getMessage());
        } catch (NamingException e) {
            fail("NamingException: " + e.getMessage());
        }
        assertNotNull(thread);
    }
    
    /**
     * Create a thread in a subcontext.
     */
    public void testCreateThread3() {
        Thread thread = null;
        try {
            ((ThreadContext)context).createSubcontext("SomeContext");
        } catch (NamingException e) {
            fail("Creating Subcontext NamingException: " + e.getMessage());
        }
        try {
            thread = ((ThreadContext)context).createThread(new Thread(), "SomeContext.SomeName");
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("NullPointerException: " + e.getMessage());
        } catch (NameAlreadyBoundException e) {
            fail("NameAlreadyBoundException: " + e.getMessage());
        } catch (ThreadIsRunningException e) {
            fail("ThreadIsRunningException: " + e.getMessage());
        } catch (NamingException e) {
            fail("NamingException: " + e.getMessage());
        }
        assertNotNull(thread);
    }

    /** 
     * Notify a specific thread
     */
    public void testNotifyThread0() {
        try {
            ((ThreadContext)context).createThread(new Thread(), "SomeName");
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("NullPointerException: " + e.getMessage());
        } catch (NameAlreadyBoundException e) {
            fail("NameAlreadyBoundException: " + e.getMessage());
        } catch (ThreadIsRunningException e) {
            fail("ThreadIsRunningException: " + e.getMessage());
        } catch (NamingException e) {
            fail("NamingException: " + e.getMessage());
        }
        try {
            ((ThreadContext)context).notifyThread("SomeName");
        } catch (NameNotFoundException e) {
            fail("NameNotFoundException: " + e.getMessage());
        } catch (NamingException e) {
            fail("NamingException: " + e.getMessage());
        }
    }
    
    /** 
     * Notifying a group of threads that are in a context.
     */
    public void testNotifyThread1() {
        try {
            ((ThreadContext)context).createSubcontext("SomeContext");
        } catch (NamingException e) {
            e.printStackTrace();
            fail("Creating Subcontext NamingException: " + e.getMessage());
        }
        try {
            ((ThreadContext)context).createThread(new Thread(), "SomeContext.SomeName");            
            ((ThreadContext)context).createThread(new Thread(), "SomeContext.SomeName2");
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("NullPointerException: " + e.getMessage());
        } catch (NameAlreadyBoundException e) {
            e.printStackTrace();
            fail("NameAlreadyBoundException: " + e.getMessage());
        } catch (ThreadIsRunningException e) {
            e.printStackTrace();
            fail("ThreadIsRunningException: " + e.getMessage());
        } catch (NamingException e) {
            e.printStackTrace();
            fail("NamingException: " + e.getMessage());
        }
        try {
            ((ThreadContext)context).notifyThread("SomeContext");
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail("NullPointerException: " + e.getMessage());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            fail("NameNotFoundException: " + e.getMessage());
        } catch (NamingException e) {
            e.printStackTrace();
            fail("NamingException: " + e.getMessage());
        }
    }

    /**
     * Test to create a sub context
     */
    public void testCreateSubContext0() {
        Object sub = null;
        try {
            /* Create the subContext object */
            context.createSubcontext("ThreadGroup1");
            /* Make sure that it exists. */
            sub = context.lookup("ThreadGroup1");
        } catch (NamingException e) {
            fail("NamingException " + e.getMessage());
        }
        assertNotNull(sub);
    }

}
