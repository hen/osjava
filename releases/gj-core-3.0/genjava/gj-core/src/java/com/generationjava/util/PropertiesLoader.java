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
package com.generationjava.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Find a Properties object from somewhere.
 */
public class PropertiesLoader {

    private Properties properties = null;

    /**
     * Uses a java.util.Properties object to load properties into.
     */
    public PropertiesLoader() {
        this(new Properties());
    }

    /**
     * Use the passed in properties object if need be.
     */
    public PropertiesLoader(Properties properties) {
        this.properties = properties;
    }

    /**
     * Find a properties with the given filename.
     */
    public Properties findProperties(String filename) {
        Properties props;

        // use FileW.loadClasspathFile ???
        ClassLoader loader = this.getClass().getClassLoader();

        if(loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }

        if(loader != null) {
            URL url = loader.getResource(filename);
            if(url == null) {
                url = loader.getResource("/"+filename);
            }
            if(url != null) {
                try {
                    InputStream in = url.openStream();
                    props = (Properties)this.properties.clone();
                    props.load(in);
                    return props;
                } catch(IOException ioe) {
                }
            }
        }

        props = loadProperties(filename);
        if(props != null) {
            return props;
        }

        props = loadProperties(System.getProperty("properties.path"), filename);
        if(props != null) {
            return props;
        }

        props = loadProperties(System.getProperty("user.dir"),filename);
        if(props != null) {
            return props;
        }

        props = loadProperties(System.getProperty("user.home"), filename);
        return props;
    }

    /**
     * Load a Properties file with the given directory and filename.
     */
    public Properties loadProperties(String directory, String filename) {
        return loadProperties(directory + File.separatorChar + filename);
    }

    /**
     * Load a Properties file with the given pathname.
     */
    public Properties loadProperties(String pathname) {
        try {
            Properties loadedProperties = (Properties)this.properties.clone();
            FileInputStream fis = new FileInputStream(pathname);
            loadedProperties.load(fis);
            fis.close();
            return loadedProperties;
        } catch(FileNotFoundException fnfe) {
            return null;
        } catch(IOException fnfe) {
            return null;
        }
    }

}
