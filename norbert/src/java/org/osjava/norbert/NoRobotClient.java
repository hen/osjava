package org.osjava.norbert;

import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.generationjava.net.UrlW;

public class NoRobotClient {

    private String userAgent;
    private RulesEngine rules;
    private String base;

    public NoRobotClient(String userAgent) {
        this.userAgent = userAgent;
    }

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

    public boolean isUrlAllowed(URL url) throws RuntimeException {
        String urlStr = url.toExternalForm();
        if(!urlStr.startsWith(this.base)) {
            throw new RuntimeException("Illegal to use a different url, " + urlStr + ",  for this robots.txt: "+this.base);
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
