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
package org.osjava.norbert;

import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.MalformedURLException;

import com.generationjava.net.UrlW;

/**
 * A Client which may be used to decide which urls on a website 
 * may be looked at, according to the norobots specification 
 * located at: 
 * http://www.robotstxt.org/wc/norobots-rfc.html
 */
public class NoRobotClient {

    private String userAgent;
    private RulesEngine rules;
    private String base;

    /**
     * Create a Client for a particular user-agent name. 
     *
     * @param userAgent name for the robot
     */
    public NoRobotClient(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Head to a website and suck in their robots.txt file. 
     * Note that the URL passed in is for the website and does 
     * not include the robots.txt file itself.
     *
     * @param baseUrl of the site
     */
    public void parse(URL baseUrl) {

        this.rules = new RulesEngine();

        this.base = baseUrl.toExternalForm();

        URL txtUrl = null;
        try {
            // fetch baseUrl+"robots.txt"
            txtUrl = new URL(baseUrl, "robots.txt");
        } catch(MalformedURLException murle) {
            System.err.println("MURLE: "+murle.getMessage());
            // we can do what we want
            return;
        }

        String txt = null;
        try {
            txt = ""+UrlW.getContent(txtUrl);
        } catch(IOException ioe) {
            System.err.println("IOE: "+ioe.getMessage());
            // we can do what we want
            return;
        }


        // Classic basic parser style, read an element at a time, 
        // changing a state variable [checkAllows]

        // take each line, one at a time
        BufferedReader rdr = new BufferedReader( new StringReader(txt) );
        String line = "";
        String value = null;
        boolean checkAllows = false;
        try {
            while( (line = rdr.readLine()) != null ) {
                // trim whitespace from either side
                line = line.trim();

                // ignore startsWith('#')
                if(line.startsWith("#")) {
                    continue;
                }

                // TODO: Make comparisons case-insensitive

                // if User-agent == this.userAgent or *, then 
                // record the rest up until end or next User-agent
                // then quit (? check spec)
                if(line.startsWith("User-agent:")) {

                    if(checkAllows) {
                        // we've just finished reading allows/disallows
                        if(this.rules.isEmpty()) {
                            // multiple user agents in a line, let's 
                            // wait til we get rules
                            continue;
                        } else {
                            break;
                        }
                    }

                    value = line.substring(11).trim();
                    if(value.equals("*") || value.equals(this.userAgent)) {
                        checkAllows = true;
                        continue;
                    }
                } else {
                    // if not, then store if we're currently the user agent
                    if(checkAllows) {
                        if(line.startsWith("Allow:")) {
                            value = line.substring(6).trim();
                            value = URLDecoder.decode(value);
                            this.rules.allowPath( value );
                        } else 
                        if(line.startsWith("Disallow:")) {
                            value = line.substring(9).trim();
                            value = URLDecoder.decode(value);
                            this.rules.disallowPath( value );
                        } else {
                            // ignore
                            continue;
                        }
                    } else {
                        // ignore
                        continue;
                    }
                }
            }
//            System.err.println(this.rules);
        } catch (IOException ioe) {
            return;
        }
    }

    /**
     * Decide if the parsed website will allow this URL to be 
     * be seen. 
     *
     * Note that parse(URL) must be called before this method 
     * is called. 
     *
     * @param url in question
     * @return is the url allowed?
     *
     * @throws IllegalStateException when parse has not been called
     */
    public boolean isUrlAllowed(URL url) throws IllegalStateException, IllegalArgumentException {
        if(rules == null) {
            throw new IllegalStateException("You must call parse before you call this method.  ");
        }

        String urlStr = url.toExternalForm();
        if(!urlStr.startsWith(this.base)) {
            throw new IllegalArgumentException("Illegal to use a different url, " + urlStr + ",  for this robots.txt: "+this.base);
        }
        urlStr = urlStr.substring( this.base.length() - 1);
        if("/robots.txt".equals(urlStr)) {
            return true;
        }
        urlStr = URLDecoder.decode( urlStr );
//        System.err.println("Considering: "+urlStr);
        return this.rules.isAllowed( urlStr );
    }

}
