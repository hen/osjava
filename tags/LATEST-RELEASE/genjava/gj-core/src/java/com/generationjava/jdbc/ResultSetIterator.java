/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Genjava-Core nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
package com.generationjava.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Iterator;

public class ResultSetIterator implements Iterator {

    private ResultSet rs;
    private boolean hasNextCalledLast = false;
    private Object val = null;

    public ResultSetIterator(ResultSet rs) {
        this.rs = rs;
    }

    public boolean hasNext() {
        if(!this.hasNextCalledLast) {
            this.hasNextCalledLast = true;
            doNext();
        }
        return this.val != null;
    }

    public Object next() {
        if(this.val == null) {
            doNext();
        }
        this.hasNextCalledLast = false;
        return this.val;
    }

    private void doNext() {
        try {
            rs.next();
            this.val = JdbcW.resultSetToArray(rs);
        } catch(SQLException sqle) {
            //sqle.printStackTrace();
            this.val = null;
        }
    }

    public void remove() {
//        throw new UnsupportedOperationException("Cannot remove from a "+
//            "java.sql.ResultSet." );
        try {
            this.rs.deleteRow();
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
    }

}
