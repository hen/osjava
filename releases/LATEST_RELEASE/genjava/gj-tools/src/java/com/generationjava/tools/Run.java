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
package com.generationjava.tools;

import com.generationjava.collections.CollectionsW;
import com.generationjava.lang.ClassW;
import org.apache.commons.lang.StringUtils;
import com.generationjava.util.PropertiesLoader;
import java.util.Properties;

/**
 * A class which runs tools.
 */
public class Run {

    static public void main(String[] strs) {
        /*
        PropertiesLoader pl = new PropertiesLoader();
        Properties props = pl.findProperties("com/generationjava/test/test.properties");
        if(props != null) {
            String location = props.getProperty("scaffold.package");
            if (strs[0].indexOf(".") == -1) {
                strs[0] = location+"."+strs[0];
            }
            if (strs[0].indexOf("Scaffold") == -1) {
                strs[0] += "Scaffold";
            }
        } else {
            System.err.println("Can't find com.generationjava.test.test.properties.");
        }
        */
        strs[0] = "com.generationjava.tools."+StringUtils.capitalise(strs[0])+"Tool";

        CommandLineToolRunner cltr = new CommandLineToolRunner();
        cltr.runTool(ClassW.getClass(strs[0]), CollectionsW.getSubArray(strs,1));
    }

}
