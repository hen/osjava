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
package com.generationjava.net;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Simple class allowing URLs to be obtained.
 */
public class WGet {

    // full url: http://www.generationjava.com/
    /**
     * Provide a string url and it downloads the contents at 
     * that url.
     */
    public String downloadPage(String urlStr) {
        if(urlStr.indexOf("http://") == -1) {
            urlStr = "http://"+urlStr;
        } 
        StringBuffer sb = new StringBuffer();
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch(MalformedURLException mue) {
            System.err.println("Had nasty url: "+urlStr);
            mue.printStackTrace();
            return "";
        }
        try {
            InputStream is = url.openStream();
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while( (line = bis.readLine()) != null) {
                sb.append(line);
            }
        } catch(IOException ioe) {
            System.err.println("Had problems with: "+urlStr);
            ioe.printStackTrace();
        }
        return sb.toString();
    }
}
