/*
 * org.osjava.jndi.GenericContext_createSubContextTest
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Feb 14, 2005
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
 * + Neither the name of the Simple-JNDI nor the names of its contributors may
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


package org.osjava.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

/**
 * @author rzigweid
 */
public class GenericContextTest extends TestCase {
    Context context = null;

    /**
     * @param arg0
     */
    public GenericContextTest(String arg0) {
        super(arg0);
    }

    public void setUp() {
        /* Initial configuration voodoo for the default context. */
        Hashtable contextEnv = new Hashtable();
        /* The intial context. */
        contextEnv.put("java.naming.factory.initial", "org.osjava.jndi.GenericContextFactory");
        /* The default is 'flat', which isn't hierarchial and not what
         *  I want. */
        contextEnv.put("jndi.syntax.direction", "left_to_right");
        /* Separatator is required for non-flat */
        contextEnv.put("jndi.syntax.separator", ".");
        
        try {
            context = new InitialContext(contextEnv);
        } catch(NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void tearDown() {
        try {
            context.close();
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void testCreateSubContext1() {
        try {
            /* Create the subContext object */
            context.createSubcontext("path");
            /* Make sure that it exists. */
            Object sub = context.lookup("path");
            assertNotNull(sub);
        } catch (NamingException e) {
            fail("NamingException " + e.getMessage());
        }
    }
}
