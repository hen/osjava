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
 * + Neither the name of XmlWriter nor the names of its contributors 
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
package com.generationjava.io.xml;

import org.apache.commons.lang.StringUtils;

/**
 * XML helping static methods.
 *
 * @author <a href="mailto:bayard@apache.org">Henri Yandell</a>
 * @version 1.0
 */
final public class XmlUtils {

    static public String escapeXml(String str) {
        str = StringUtils.replace(str,"&","&amp;");
        str = StringUtils.replace(str,"<","&lt;");
        str = StringUtils.replace(str,">","&gt;");
        str = StringUtils.replace(str,"\"","&quot;");
        str = StringUtils.replace(str,"'","&apos;");
        return str;
    }

    static public String unescapeXml(String str) {
        str = StringUtils.replace(str,"&amp;","&");
        str = StringUtils.replace(str,"&lt;","<");
        str = StringUtils.replace(str,"&gt;",">");
        str = StringUtils.replace(str,"&quot;","\"");
        str = StringUtils.replace(str,"&apos;","'");
        return str;
    }

    /**
     * Remove any xml tags from a String.
     * Same as HtmlW's method.
     */
    static public String removeXml(String str) {
        int sz = str.length();
        StringBuffer buffer = new StringBuffer(sz);
        boolean inString = false;
        boolean inTag = false;
        for(int i=0; i<sz; i++) {
            char ch = str.charAt(i);
            if(ch == '<') {
                inTag = true;
            } else
            if(ch == '>') {
                inTag = false;
                continue;
            }
            if(!inTag) {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    static public String getContent(String tag, String text) {
        int idx = XmlUtils.getIndexOpeningTag(tag, text);
        if(idx == -1) {
            return "";
        }
        text = text.substring(idx);
        int end = XmlUtils.getIndexClosingTag(tag, text);
        idx = text.indexOf('>');
        if(idx == -1) {
            return "";
        }
        return text.substring(idx+1, end);
    }

    static public int getIndexOpeningTag(String tag, String text) {
        return getIndexOpeningTag(tag, text, 0);
    }
    static private int getIndexOpeningTag(String tag, String text, int start) {
        // consider whitespace?
        int idx = text.indexOf("<"+tag, start);
        if(idx == -1) {
            return -1;
        }
        char next = text.charAt(idx+1+tag.length());
        if( (next == '>') || Character.isWhitespace(next) ) {
            return idx;
        } else {
            return getIndexOpeningTag(tag, text, idx+1);
        }
    }

    // Pass in "para" and a string that starts with 
    // <para> and it will return the index of the matching </para>
    // It assumes well-formed xml. Or well enough.
    static public int getIndexClosingTag(String tag, String text) {
        return getIndexClosingTag(tag, text, 0);
    }
    static public int getIndexClosingTag(String tag, String text, int start) {
        String open = "<"+tag;
        String close = "</"+tag+">";
//        System.err.println("OPEN: "+open);
//        System.err.println("CLOSE: "+close);
        int closeSz = close.length();
        int nextCloseIdx = text.indexOf(close, start);
//        System.err.println("first close: "+nextCloseIdx);
        if(nextCloseIdx == -1) {
            return -1;
        }
        int count = StringUtils.countMatches(text.substring(start, nextCloseIdx), open);
//        System.err.println("count: "+count);
        if(count == 0) {
            return -1;  // tag is never opened
        }
        int expected = 1;
        while(count != expected) {
            nextCloseIdx = text.indexOf(close, nextCloseIdx+closeSz);
            if(nextCloseIdx == -1) {
                return -1;
            }
            count = StringUtils.countMatches(text.substring(start, nextCloseIdx), open);
            expected++;
        }
        return nextCloseIdx;
    }

    static public String getAttribute(String attribute, String text) {
        return getAttribute(attribute, text, 0);
    }
    static public String getAttribute(String attribute, String text, int idx) {
         int close = text.indexOf(">", idx);
         int attrIdx = text.indexOf(attribute+"=\"", idx);
         if(attrIdx == -1) {
             return null;
         }
         if(attrIdx > close) {
             return null;
         }
         int attrStartIdx = attrIdx + attribute.length() + 2;
         int attrCloseIdx = text.indexOf("\"", attrStartIdx);
         if(attrCloseIdx > close) {
             return null;
         }
         return unescapeXml(text.substring(attrStartIdx, attrCloseIdx));
    }

}
