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
package com.generationjava.io;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;

/**
 * Read lines from a Command Line.
 * This allows the code that deals with the commands to be independent 
 * of the command-reading.
 */
public class CommandLine {

    private BufferedReader br = null;
    private CommandLineListener myListener = null;
    private String prompt = "";

    /**
     * Create a CommandLine with the InputStream to read commands 
     * from and the CommandLineListener to send commands to.
     */
    public CommandLine(InputStream is, CommandLineListener cll) {
      this( new InputStreamReader(is), cll);
    }
    
    /**
     * Create a CommandLine with the Reader to read commands 
     * from and the CommandLineListener to send commands to.
     */
    public CommandLine(Reader rdr, CommandLineListener cll) {
        br = new BufferedReader(rdr);
        myListener = cll;
    }
    
    /**
     * Set a prompt to ignore.
     */
    public void setPrompt(String s) {
        prompt = s;
    }

    /**
     * Get the prompt which will be ignored by the CommandLine.
     */
    public String getPrompt() {
        return prompt;
    }
    
    /**
     * Start the CommandLine.
     */
    public void startReading() throws IOException {
        if(myListener == null) {
            throw new IOException("No CommandLineListener set. ");
        }

        String cmd = null;
        while(true) {
            cmd = br.readLine();
            if(cmd.startsWith(prompt)) {
                cmd = cmd.substring(prompt.length());
            }
            myListener.cmdEntered(cmd);
        }
    }

}
