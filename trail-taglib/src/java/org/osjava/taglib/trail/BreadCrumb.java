/*
 * Copyright (c) 2004, Henri Yandell
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

package org.osjava.taglib.trail;

public class BreadCrumb {
    private String url;
    private String label;

    public BreadCrumb(String url, String label) {
        this.url = url;
        this.label = label;
    }

    public String getUrl() { return this.url; }
    public String getLabel() { return this.label; }

    public int hashCode() { return this.url.hashCode() & this.label.hashCode(); }

    public boolean equals(Object obj) {
        if( !(obj instanceof BreadCrumb) ) {
            return false;
        }

        BreadCrumb b = (BreadCrumb) obj;

        // this is all to make the equality of a url not include the query string
        // Could just use java.net.URL
        String u1 = this.url;
        String u2 = b.url;
        int idx1 = u1.indexOf("?");
        if(idx1 != -1) {
            u1 = u1.substring(0, idx1);
        }
        int idx2 = u2.indexOf("?");
        if(idx2 != -1) {
            u2 = u2.substring(0, idx2);
        }

        return u1.equals(u2) && this.label.equals(b.label);
    }

    public String toString() {
        return "[" + this.label + "|" + this.url + "]";
    }

}
