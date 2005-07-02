/*
 * org.osjava.jdbc.sqlite.TestDriver
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Jun 25, 2005
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
 * + Neither the name of the SQLite-JDBC nor the names of its contributors may
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


package org.osjava.jdbc.sqlite;

import java.io.File;
import java.sql.SQLException;

import org.osjava.jdbc.sqlite.Driver;

import junit.framework.TestCase;

public class TestDriver extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
        try {
            Class.forName("org.osjava.jdbc.sqlite.Driver");
        } catch (ClassNotFoundException e) {
            fail();
        }
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
        /* If the local.db file exists, remove it. */
        File db = new File("local.db");
        if(db.exists()) {
            db.delete();
        }
    }

    /**
     * Test asserts that the URL beginning with jdbc:sqlite returns true.
     */
    public void test_acceptURL1() {
        Driver driver = new Driver();
        try {
            assertTrue(driver.acceptsURL("jdbc:sqlite:local.db"));
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Test asserts that the URL beginning with jdbc:sqlite3 returns true.
     */
    public void test_acceptURL2() {
        Driver driver = new Driver();
        try {
            assertTrue(driver.acceptsURL("jdbc:sqlite3:local.db"));
        } catch (SQLException e) {
            fail();
        }
    }

    /**
     * Test asserts that the URL beginning with jdbc:sqlite3 returns false.
     */
    public void test_acceptURL3() {
        Driver driver = new Driver();
        try {
            assertFalse(driver.acceptsURL("jdbc:sqlite2:local.db"));
        } catch (SQLException e) {
            fail();
        }
    }
    
}
