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
package com.generationjava.web;

import org.apache.commons.lang.StringUtils;

/**
 * XML helping static methods.
 */
final public class XmlW {

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

    static public String getContent(String text, String tag) {
        int idx = XmlW.getIndexOpeningTag(text, tag);
        if(idx == -1) {
            return "";
        }
        text = text.substring(idx);
        int end = XmlW.getIndexClosingTag(text, tag);
        idx = text.indexOf('>');
        if(idx == -1) {
            return "";
        }
        return text.substring(idx+1, end);
    }

    static public int getIndexOpeningTag(String text, String tag) {
        return getIndexOpeningTag(text, tag, 0);
    }
    static public int getIndexOpeningTag(String text, String tag, int start) {
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
    static public int getIndexClosingTag(String text, String tag) {
        return getIndexClosingTag(text, tag, 0);
    }
    static public int getIndexClosingTag(String text, String tag, int start) {
        String open = "<"+tag;
        String close = "</"+tag+">";
        int closeSz = close.length();
        int nextCloseIdx = text.indexOf(close, start);
        if(nextCloseIdx == -1) {
            return -1;
        }
        int count = StringUtils.countMatches(text.substring(start, nextCloseIdx), open);
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

    static public String getAttribute(String text, String attribute) {
        return getAttribute(text, attribute, 0);
    }
    static public String getAttribute(String text, String attribute, int idx) {
        int close = text.indexOf(">", idx);
        int doubleAttrIdx = text.indexOf(attribute+"=\"", idx);
        int singleAttrIdx = text.indexOf(attribute+"='", idx);

        int attrIdx = doubleAttrIdx;
        String endQuote = "\"";
        if(doubleAttrIdx == -1 || (singleAttrIdx != -1 && singleAttrIdx < doubleAttrIdx) ) {
            attrIdx = singleAttrIdx;
            endQuote = "'";
        }

        if(attrIdx == -1) {
            return null;
        }
        if(attrIdx > close) {
            return null;
        }
        int attrStartIdx = attrIdx + attribute.length() + 2;
        int attrCloseIdx = text.indexOf(endQuote, attrStartIdx);
        if(attrCloseIdx > close) {
            return null;
        }
        return unescapeXml(text.substring(attrStartIdx, attrCloseIdx));
    }

}
