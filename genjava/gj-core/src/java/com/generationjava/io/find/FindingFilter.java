/*
 * Copyright (c) 2004, Henri Yandell
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

package com.generationjava.io.find;

import java.util.HashSet;
import java.util.Collection;
import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This is where most of the find functionality occurs. Nearly every option 
 * to find is mapped to a FileFilter, which are then chained together inside 
 * this class. 
 */
public class FindingFilter implements FileFilter {

    private Map options;
    private List filters = new LinkedList();
    private boolean daystart;

    public FindingFilter(Map options) {
        this.options = options;
        Collection entries = options.entrySet();
        Iterator itr = entries.iterator();
        while(itr.hasNext()) {
            Map.Entry entry = (Map.Entry)itr.next();
            if( entry.getKey().equals(Finder.DAYSTART) ) {
                this.daystart = true; 
                continue;
            }
            filters.add( createFilter(entry.getKey(), entry.getValue()) );
        }
    }

    private FileFilter createFilter(Object option, Object argument) {
        if( option.equals(Finder.MIN) ) {
            return new MinFilter(option, argument, this);
        }
        if( option.equals(Finder.NEWER) ) {
            return new NewerFilter(option, argument);
        }
        if( option.equals(Finder.TIME) ) {
            return new TimeFilter(option, argument, this);
        }
        if( option.equals(Finder.EMPTY) ) {
            return new EmptyFilter(option, argument);
        }
        if( option.equals(Finder.SIZE) ) {
            return new SizeFilter(option, argument);
        }
        if( option.equals(Finder.NAME) ) {
            return new NameFilter(option, argument, false);
        }
        if( option.equals(Finder.INAME) ) {
            return new NameFilter(option, argument, true);
        }
        if( option.equals(Finder.PATH) ) {
            return new PathFilter(option, argument, false);
        }
        if( option.equals(Finder.IPATH) ) {
            return new PathFilter(option, argument, true);
        }
        // IREGEX ignored as too much of a pain
        if( option.equals(Finder.REGEX) ) {
            return new RegexFilter(option, argument);
        }
        if( option.equals(Finder.TYPE) ) {
            return new TypeFilter(option, argument);
        }
        if( option.equals(Finder.CAN_READ) ) {
            return new CanReadFilter(option, argument);
        }
        if( option.equals(Finder.CAN_WRITE) ) {
            return new CanWriteFilter(option, argument);
        }
        return null;
    }

    public boolean accept(File file) {
        Iterator itr = filters.iterator();
        while(itr.hasNext()) {
            FileFilter filter = (FileFilter) itr.next();
            if(filter == null) {
                continue;
            }
            boolean result = filter.accept(file);
            if(result == false) {
                return false;
            }
        }
        return true;
    }

    public boolean isDaystartConfigured() {
        return this.daystart;
    }

}

// need to implement the daystart bits
class MinFilter implements FileFilter {
    private Object option;
    private int argument;
    private FindingFilter parent;
    public MinFilter(Object option, Object argument, FindingFilter parent) {
        this.option = option;
        try {
            this.argument = Integer.parseInt(argument.toString());
        } catch(NumberFormatException nfe) {
            throw new IllegalArgumentException("Argument "+argument+" must be an integer.  ");
        }
        this.parent = parent;
    }
    public boolean accept(File file) {
        boolean daystart = this.parent.isDaystartConfigured();
        return file.lastModified() > System.currentTimeMillis() - this.argument * 60000;
    }
}

class NewerFilter implements FileFilter {
    private Object option;
    private File argument;
    public NewerFilter(Object option, Object argument) {
        this.option = option;
        this.argument = new File(argument.toString());
    }
    public boolean accept(File file) {
        return file.lastModified() > this.argument.lastModified();
    }
}

// implement daystart
class TimeFilter implements FileFilter {
    private Object option;
    private int argument;
    private FindingFilter parent;
    public TimeFilter(Object option, Object argument, FindingFilter parent) {
        this.option = option;
        try {
            this.argument = Integer.parseInt(argument.toString());
        } catch(NumberFormatException nfe) {
            throw new IllegalArgumentException("Argument "+argument+" must be an integer.  ");
        }
        this.parent = parent;
    }
    public boolean accept(File file) {
        boolean daystart = this.parent.isDaystartConfigured();
        return file.lastModified() > System.currentTimeMillis() - this.argument * 60000*60*24;
    }
}

class EmptyFilter implements FileFilter {
    private Object option;
    private boolean argument;
    public EmptyFilter(Object option, Object argument) {
        this.option = option;
        this.argument = new Boolean(argument.toString()).booleanValue();
    }
    public boolean accept(File file) {
        return (file.length() == 0) == this.argument;
    }
}

class SizeFilter implements FileFilter {
    private Object option;
    private int argument;
    public SizeFilter(Object option, Object argument) {
        this.option = option;
        try {
            this.argument = Integer.parseInt(argument.toString());
        } catch(NumberFormatException nfe) {
            throw new IllegalArgumentException("Argument "+argument+" must be an integer.  ");
        }
    }
    public boolean accept(File file) {
        return ((int)(file.length()/512))*512 == this.argument;
    }
}

// implement Wildcards
class NameFilter implements FileFilter {
    private Object option;
    private Object argument;
    private boolean ignoreCase;
    public NameFilter(Object option, Object argument, boolean ignoreCaset) {
        this.option = option;
        this.argument = argument;
        this.ignoreCase = ignoreCase;
    }
    public boolean accept(File file) {
        if(this.ignoreCase) {
            return file.getName().toLowerCase().equals(this.argument.toString().toLowerCase());
        } else {
            return file.getName().equals(this.argument.toString());
        }
    }
}

class PathFilter implements FileFilter {
    private Object option;
    private Object argument;
    private boolean ignoreCase;
    public PathFilter(Object option, Object argument, boolean ignoreCaset) {
        this.option = option;
        this.argument = argument;
        this.ignoreCase = ignoreCase;
    }
    public boolean accept(File file) {
        if(this.ignoreCase) {
            return file.getPath().toLowerCase().equals(this.argument.toString().toLowerCase());
        } else {
            return file.getPath().equals(this.argument.toString());
        }
    }
}

class RegexFilter implements FileFilter {
    private Object option;
    private Object argument;
    public RegexFilter(Object option, Object argument) {
        this.option = option;
        this.argument = argument;
    }
    public boolean accept(File file) {
        return file.getPath().matches(this.argument.toString());
    }
}

class TypeFilter implements FileFilter {
    private Object option;
    private Object argument;
    public TypeFilter(Object option, Object argument) {
        this.option = option;
        if(!"d".equals(argument) && !"f".equals(argument)) {
            throw new IllegalArgumentException("Type option must be 'f' or 'd'. ");
        }
        this.argument = argument;
    }
    public boolean accept(File file) {
        if("d".equals(argument)) {
            return file.isDirectory();
        } else
        if("f".equals(argument)) {
            return !file.isDirectory();
        } else {
            throw new IllegalArgumentException("Type option must be 'f' or 'd'. ");
        }
    }
}

class CanReadFilter implements FileFilter {
    private Object option;
    private boolean argument;
    public CanReadFilter(Object option, Object argument) {
        this.option = option;
        this.argument = new Boolean(argument.toString()).booleanValue();
    }
    public boolean accept(File file) {
        return file.canRead() == this.argument;
    }
}

class CanWriteFilter implements FileFilter {
    private Object option;
    private boolean argument;
    public CanWriteFilter(Object option, Object argument) {
        this.option = option;
        this.argument = new Boolean(argument.toString()).booleanValue();
    }
    public boolean accept(File file) {
        return file.canWrite() == this.argument;
    }
}

