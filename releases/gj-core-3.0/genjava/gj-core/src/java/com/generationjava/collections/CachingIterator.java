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
package com.generationjava.collections;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A piping Iterator which caches all objects that pass through it.
 * When reset is called, it returns to the start of its cache.
 * This however only allows for one version of the iterator at a time 
 * to be used. It gets used again and again.
 * The alternative is to use the static cache method, or the iterate 
 * method. Both of which may be used to get a new Iterator over the 
 * same data. 
 *
 * @date 2001-12-01
 */
public class CachingIterator implements ResetableIterator {

    static Iterator cache(Iterator iterator) {
        if(iterator instanceof CachingIterator) {
            return ((CachingIterator)iterator).iterate();
        } else {
            return new CachingIterator(iterator);
        }
    }

    private Iterator iterator;
    private LinkedList cacheList;
    private Iterator cacheIterator;
    private boolean caching;

    public CachingIterator(Iterator iterator) {
        this.iterator = iterator;
        this.cacheList = new LinkedList();
    } 

    public Object next() {
        if(this.caching) {
            return this.cacheIterator.next();
        } else {
            Object tmp = iterator.next();
            this.cacheList.add(tmp);
            return tmp;
        }
    }
    
    public boolean hasNext() {
        if(this.caching) {
            boolean tmp = this.cacheIterator.hasNext();
            return tmp;
        } else {
            boolean tmp = iterator.hasNext();
            if(!tmp) {
                this.caching = true;
                reset();
            }
            return tmp;
        }
    }
    
    public void remove() {
        if(this.caching) {
            throw new UnsupportedOperationException("Not possible to remove "+
                "from the Iterator once it has begun to cache. ");
        } else {
            this.iterator.remove();
        }
    }

    /**
     * Force a loading of the wrapped iterator into cache.
     */
    public void loadCache() {
        if(!this.caching) {
            while(this.iterator.hasNext()) {
                this.cacheList.add(this.iterator.next());
            }
        }
    }

    public void reset() {
        this.cacheIterator = iterate();
    }

    public Iterator iterate() {
        loadCache();
        return this.cacheList.iterator();
    }
}
