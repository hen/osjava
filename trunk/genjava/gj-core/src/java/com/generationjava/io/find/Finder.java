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
// @file   Finder.java
// @author bayard@generationjava.com
// @create 2001-03-25

package com.generationjava.io.find;

import java.io.File;
import java.util.Map;

/**
 * A Finder of Files. Though the structure the files are in is 
 * unspecified.
 */
public interface Finder {

    // OPTIONS
    public static final Object DAYSTART = "DAYSTART";
    public static final Object DEPTH = "DEPTH";
    public static final Object MAXDEPTH = "MAXDEPTH";
    public static final Object MINDEPTH = "MINDEPTH";
    public static final Object IGNORE_HIDDEN_DIRS = "IGNORE_HIDDEN_DIRS";

    // Time based tests
    public static final Object MIN = "MIN";
    public static final Object NEWER = "NEWER";
    public static final Object TIME = "TIME";

    // size based tests
    public static final Object EMPTY = "EMPTY";
    public static final Object SIZE = "SIZE";

    // name based tests
    public static final Object NAME = "NAME";
    public static final Object INAME = "INAME";
    public static final Object NNAME = "NNAME";
    public static final Object PATH = "PATH";
    public static final Object IPATH = "IPATH";
    public static final Object NPATH = "NPATH";
    public static final Object REGEX = "REGEX";
    public static final Object IREGEX = "IREGEX";
    public static final Object NREGEX = "NREGEX";

    // type of file
    public static final Object TYPE = "TYPE";      // supports 'd' and 'f'

    // permission replacements
    public static final Object CAN_READ = "CAN_READ";
    public static final Object CAN_WRITE = "CAN_WRITE";

    public void addFindListener(FindListener fl);
    public void removeFindListener(FindListener fl);

    public File[] find(File root);
    public File[] find(File root, Map options);

}
