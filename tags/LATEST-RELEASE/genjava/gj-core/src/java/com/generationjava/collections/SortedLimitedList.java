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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Only allows N elements in. Once size N is reached, all 
 * new adds are ignored and inserts mean the one on the end 
 * of the list is removed.
 * The List is kept in a sorted order.
 */
public class SortedLimitedList extends LimitedList {

    private Comparator comparator;

    public SortedLimitedList(int capacity, Comparator comparator) {
        this(new ArrayList(), capacity, comparator);
    }

    public SortedLimitedList(List list, int capacity, Comparator comparator) {
        super(list, capacity);
        this.comparator = comparator;
    }

    public boolean add(Object obj) {
        if(isEmpty()) {
            return super.add(obj);
        }
        if(isFull()) {
            if(comparator.compare(obj, get(size()-1)) <= 0) {
                return false;
            }
        }
        insertIntoSort(obj);
        return true;
    }

    // inserts into correctly sorted position
    private void insertIntoSort(Object obj) {
        int sz = size();
        int i;                      // used outside loop
        for(i=sz-1; i>-1; i--) {
            if(comparator.compare(obj, get(i)) > 0) {
                continue;
            } else {
                break;
            }
        }
        super.add(i+1, obj);
    }

    public boolean addAll(Collection coll) {
        return super.addAll(coll);
    }

    public boolean addAll(int i, Collection coll) {
        return super.addAll(i,coll);
    }

    /// TODO
    public boolean retainAll(Collection coll) {
        return super.retainAll(coll);
    }

    public void add(int i, Object obj) {
        super.add(i,obj);
    }

    public String toString() {
        return super.toString();
    }
}
