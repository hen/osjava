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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A List which wraps another List. Intended mainly to 
 * be used as a superclass.
 */
public class ProxyList implements List {

    private List list;
    
    public ProxyList(List list) {
        this.list = list;
    }

    public int size() {
        return this.list.size();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public boolean contains(Object obj) {
        return this.list.contains(obj);
    }

    public Iterator iterator() {
        return this.list.iterator();
    }

    public Object[] toArray() {
        return this.list.toArray();
    }

    public Object[] toArray(Object[] objs) {
        return this.list.toArray(objs);
    }

    public boolean add(Object obj) {
        return this.list.add(obj);
    }

    public boolean remove(Object obj) {
        return this.list.remove(obj);
    }

    public boolean containsAll(Collection coll) {
        return this.list.containsAll(coll);
    }

    public boolean addAll(Collection coll) {
        return this.list.addAll(coll);
    }

    public boolean addAll(int i, Collection coll) {
        return this.list.addAll(i,coll);
    }

    public boolean removeAll(Collection coll) {
        return this.list.removeAll(coll);
    }

    public boolean retainAll(Collection coll) {
        return this.list.retainAll(coll);
    }

    public void clear() {
        this.list.clear();
    }

    public Object get(int i) {
        return this.list.get(i);
    }

    public Object set(int i, Object obj) {
        return this.list.set(i,obj);
    }

    public void add(int i, Object obj) {
        this.list.add(i, obj);
    }

    public Object remove(int i) {
        return this.list.remove(i);
    }

    public int indexOf(Object obj) {
        return this.list.indexOf(obj);
    }

    public int lastIndexOf(Object obj) {
        return this.list.lastIndexOf(obj);
    }

    public ListIterator listIterator() {
        return this.list.listIterator();
    }

    public ListIterator listIterator(int i) {
        return this.list.listIterator(i);
    }

    public List subList(int i, int j) {
        return this.list.subList(i,j);
    }


    public String toString() {
        return this.list.toString();
    }

}
