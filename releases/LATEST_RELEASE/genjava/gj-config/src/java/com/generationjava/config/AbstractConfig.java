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
 * + Neither the name of OSJava nor the names of its contributors 
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
package org.osjava.jndi.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public abstract class AbstractConfig implements Config {

    private String context = "";

    protected abstract Object getValue(String key);

    public Object get(String key) {
        return getValue( getContext()+key );
    }

    public boolean has(String key) {
        return (get(key) != null);
    }

    public Object getAbsolute(String key) {
        return getValue(key);
    }

    public String getString(String key) {
        return (String)get(key);
    }

    public Date getDate(String key) {
        try {
            return java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT).parse(key);
        } catch(java.text.ParseException pe) {
            return null;
        }
    }

    // rely on simple-jndi's type
    public int getInt(String key) {
        try {
            return Integer.parseInt( getString(key) );
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }

    public List getList(String key) {
        Object obj = get(key);
        if(!(obj instanceof List)) {
            List list = new ArrayList(1);
            list.add(obj);
            obj = list;
        }
        return (List)obj;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return this.context;
    }

    public Config cloneConfig() {
        try {
            return (Config)this.clone();
        } catch(CloneNotSupportedException cnse) {
            // ignore
            throw new RuntimeException("Cloning of a Config failed. This should be impossible. ");
        }
    }

}
