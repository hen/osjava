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
// UrlW.java
package com.generationjava.net;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import com.generationjava.io.FileW;
import org.apache.commons.lang.StringUtils;

public class UrlW {

    static public InputStream openUrlStream(String urlStr) throws IOException {
        URL url = makeUrl(urlStr);
        return openUrlStream(url);
    }

    static public URL makeUrl(String urlStr) throws MalformedURLException {
        if(urlStr.indexOf("://") == -1) {
            urlStr = "http://"+urlStr;
        }
        return new URL(urlStr);
    }

    static public InputStream openUrlStream(URL url) throws IOException {
        return url.openStream();
    }

    static public Object getContent(String urlStr) throws IOException {
        URL url = makeUrl(urlStr);
        return getContent(url);
    }

    static public Object getContent(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        Object obj = conn.getContent();
        if(obj instanceof InputStream) {
            obj = getContent((InputStream)obj);
        }
        return obj;
    }

    static public Object getContent(InputStream in) throws IOException {
        String str = FileW.loadFile(in);
        str = StringUtils.chopNewline(str);
        str = StringUtils.chopNewline(str);
        return str;
    }

    static public InputStream doPost(String urlStr, String data) throws IOException {
        URL url = new URL(urlStr);
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);

        OutputStream raw = uc.getOutputStream();
        Writer writer = new OutputStreamWriter(new BufferedOutputStream(raw), "8859_1");
        writer.write(data);
        writer.write("\r\n");
        writer.flush();
        writer.close();

        return uc.getInputStream();
    }
}
