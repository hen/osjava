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
 * HTML helping static methods.
 * Due to case insensitivity, these methods can't match the XML ones.
 */
public class HtmlW {

    /**
     * Place &lt;br&gt;'s before each newline.
     */
    static public String nl2br(String str) {
        return StringUtils.replace(str, "\n", "<br />\n");
    }


    /// TODO: escape all html char entitie
    static public String escapeHtml(String str) {
        // handles & < > " '
        str = XmlW.escapeXml(str);

        str = StringUtils.replace(str, "\u00a0", "&nbsp;");
        str = StringUtils.replace(str, "\u00a1", "&iexcl;");
        str = StringUtils.replace(str, "\u00a2", "&cent;");
        str = StringUtils.replace(str, "\u00a3", "&pound;");
        str = StringUtils.replace(str, "\u00a4", "&curren;");
        str = StringUtils.replace(str, "\u00a5", "&yen;");
        str = StringUtils.replace(str, "\u00a6", "&brvbar;");
        str = StringUtils.replace(str, "\u00a7", "&sect;");
        str = StringUtils.replace(str, "\u00a8", "&uml;");
        str = StringUtils.replace(str, "\u00a9", "&copy;");
        str = StringUtils.replace(str, "\u00aa", "&ordf;");
        str = StringUtils.replace(str, "\u00ab", "&laquo;");
        str = StringUtils.replace(str, "\u00ac", "&not;");
        str = StringUtils.replace(str, "\u00ad", "&shy;");
        str = StringUtils.replace(str, "\u00ae", "&reg;");
        str = StringUtils.replace(str, "\u00af", "&macr;");
/*
&deg;
&amp;#176
&plusmn;
&amp;#177
&sup2;
&amp;#178
&sup3;
&amp;#179
&acute;
&amp;#180
&micro;
&amp;#181
&para;
&amp;#182
&middot;
&amp;#183
&cedil;
&amp;#184
&sup1;
&amp;#185
&ordm;
&amp;#186
&raquo;
&amp;#187
&frac14;
&amp;#188
&frac12;
&amp;#189
&frac34;
&amp;#190
&iquest;
&amp;#191
&Agrave;
&amp;#192
&Aacute;
&amp;#193
&Acirc;
&amp;#194
&Atilde;
&amp;#195
&Auml;
&amp;#196
&Aring;
&amp;#197
&AElig;
&amp;#198
&Ccedil;
&amp;#199
&Egrave;
&amp;#200
&Eacute;
&amp;#201
&Ecirc;
&amp;#202
&Euml;
&amp;#203
&Igrave;
&amp;#204
&Iacute;
&amp;#205
&Icirc;
&amp;#206
&Iuml;
&amp;#207
*/

        return str;
    }

    // add unescape for the html entities

    /**
     * Remove any html tags from a String.
     */
    static public String removeHtml(String str) {
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
            }
            if(!inTag) {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    //-------------------------------------------------------------
    // Variants of the XmlW methods that work on case-insensitivity
    //-------------------------------------------------------------
    static public int getIndexOpeningTag(String tag, String text) {
        return XmlW.getIndexOpeningTag(tag.toLowerCase(), text.toLowerCase() );
    }
    static public int getIndexClosingTag(String tag, String text) {
        return XmlW.getIndexClosingTag(tag.toLowerCase(), text.toLowerCase() );
    }

    // Copy of XmlW at the moment
    static public String getContent(String tag, String text) {
        int idx = HtmlW.getIndexOpeningTag(tag, text);
//        System.err.println("IDX"+text+": "+idx);
        if(idx == -1) {
            return "";
        }
        text = text.substring(idx);
        int end = HtmlW.getIndexClosingTag(tag, text);
//        System.err.println("END[]: "+end);
        idx = text.indexOf('>');
//        System.err.println("EDX["+text+"]: "+idx);
        if(idx == -1) {
            return "";
        }
        return text.substring(idx+1, end);
    }

    // Copies of XmlW. Need to merge these.
    static public String getAttribute(String attribute, String text) {
        return getAttribute(attribute, text, 0);
    }
    static public String getAttribute(String attribute, String text, int idx) {
         int close = text.indexOf(">", idx);
         int attrIdx = text.toLowerCase().indexOf(attribute.toLowerCase()+"=\"", idx);
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
         return XmlW.unescapeXml(text.substring(attrStartIdx, attrCloseIdx));
    }

}
