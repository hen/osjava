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

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * A Set which wraps another Set. Intended mainly to 
 * be used as a superclass.
 */
public class ProxySet implements Set {

    private Set set;
    
    public ProxySet(Set set) {
        this.set = set;
    }

    public boolean add(Object obj) {
     // Adds the specified element to this set if it is not already present (optional operation).	 
        return this.set.add(obj);
    }

    public boolean addAll(Collection c) {
     // Adds all of the elements in the specified collection to this set if they're not already present (optional operation).	 
        return this.set.addAll(c);
    }

    public void clear() {
     // Removes all of the elements from this set (optional operation).	 
        this.set.clear();
    }

    public boolean contains(Object obj) {
     // Returns true if this set contains the specified element.	 
        return this.set.contains(obj);
    }

    public boolean containsAll(Collection c) {
     // Returns true if this set contains all of the elements of the specified collection.	 
        return this.set.containsAll(c);
    }

    public boolean equals(Object obj) {
     // Compares the specified object with this set for equality.
        return this.set.equals(obj);
    }

    public int hashCode() {
     // Returns the hash code value for this this.set.	 
        return this.set.hashCode();
    }

    public boolean isEmpty() {
     // Returns true if this set contains no elements.	 
        return this.set.isEmpty();
    }

    public Iterator iterator() {
     // Returns an iterator over the elements in this this.set.	 
        return this.set.iterator();
    }

    public boolean remove(Object obj) {
     // Removes the specified element from this set if it is present (optional operation).	 
        return this.set.remove(obj);
    }

    public boolean removeAll(Collection c) {
     // Removes from this set all of its elements that are contained in the specified collection (optional operation).	 
        return this.set.removeAll(c);
    }

    public boolean retainAll(Collection c) {
     // Retains only the elements in this set that are contained in the specified collection (optional operation).	 
        return this.set.retainAll(c);
    }

    public int size() {
     // Returns the number of elements in this set (its cardinality).	 
        return this.set.size();
    }

    public Object[] toArray() {	      
     // Returns an array containing all of the elements in this this.set.	 
        return this.set.toArray();
    }

    public Object[] toArray(Object[] arr) {	      
     // Returns an array containing all of the elements in this set whose runtime type is that of the specified array.
        return this.set.toArray(arr);
    }

}
