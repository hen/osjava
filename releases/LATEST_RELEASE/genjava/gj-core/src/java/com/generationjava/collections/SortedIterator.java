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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;

import com.generationjava.compare.ObjectComparator;

/**
 * An Iterator which can be sorted.
 */
public class SortedIterator implements Iterator {

    private ArrayList cache;
    private Iterator iterator;
    private boolean started;

    public SortedIterator(Iterator iterator) {
        this.cache = new ArrayList();
        int idx = 0;
        while(iterator.hasNext()) {
            this.cache.add( new SortIndex(idx, iterator.next()) );
            idx++;
        }
        if(this.iterator == null) {
            this.iterator = cache.iterator();
        }
    }

    /// Start of Iterator
    public Object next() {
        this.started = true;
        return ((SortIndex)this.iterator.next()).getIndexed();
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public void remove() {
        throw new UnsupportedOperationException("Unable to remove once it is sorted. ");
    }
    /// End of Iterator

    public void sort(Comparator cmp) {
        if(this.started) {
            throw new RuntimeException("Cannot sort as this iterator has been read from. ");
        }
        SortComparator srtr = new SortComparator(cmp);
        Collections.sort(cache, srtr);
        if(this.iterator == null) {
            this.iterator = cache.iterator();
        }
    }

    public void sort() {
        sort( new ObjectComparator() );
    }


}

class SortIndex {

    private int idx;
    private Object object;

    public SortIndex(int idx, Object object) {
        this.idx = idx;
        this.object = object;
    }

    public int getIndex() {
        return this.idx;
    }

    public Object getIndexed() {
        return this.object;
    }

}

class SortComparator implements java.util.Comparator {

    private Comparator cmp;

    public SortComparator(Comparator cmp) {
        this.cmp = cmp;
    }

    public int compare(Object o1, Object o2) {
        SortIndex si1 = (SortIndex)o1;
        SortIndex si2 = (SortIndex)o2;
        o1 = si1.getIndexed();
        o2 = si2.getIndexed();
        return this.cmp.compare(o1,o2);
    }

}
