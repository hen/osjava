// OrderedSet.java
package com.generationjava.jndi.util;

import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * An implementation of Set that guarentees ordering 
 * remains constant.
 */
public class OrderedSet implements Set {

    private List list = null;

    public OrderedSet() {
        this.list = new LinkedList();
    }

    public OrderedSet(Collection c) {
        this();
        if(c != null) {
            Iterator iterator = c.iterator();
            while(iterator.hasNext()) {
                add(iterator.next());
            }
        }
    }
    
    /**
     * Create using the given List as the internal storage method.
     */
    public OrderedSet(List list) {
        this.list = list;
    }
    
    public boolean add(Object obj) {
     // Adds the specified element to this set if it is not already present (optional operation).         
         if(!contains(obj)) {
             list.add(obj);
             return true;
         } else {
             return false;
         }
    }

    public boolean addAll(Collection c) {
     // Adds all of the elements in the specified collection to this set if they're not already present (optional operation).         
        boolean ret = false;
        if(c != null) {
            Iterator iterator = c.iterator();
            while(iterator.hasNext()) {
                if(add(iterator.next())) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    public void clear() {
     // Removes all of the elements from this set (optional operation).         
         list.clear();
    }

    public boolean contains(Object obj) {
     // Returns true if this set contains the specified element.         
         return list.contains(obj);
    }

    public boolean containsAll(Collection c) {
     // Returns true if this set contains all of the elements of the specified collection.         
         return list.containsAll(c);
    }

    public boolean equals(Object obj) {
     // Compares the specified object with this set for equality.
         return list.equals(obj);
    }

    public int hashCode() {
     // Returns the hash code value for this set.         
         return list.hashCode();
    }

    public boolean isEmpty() {
     // Returns true if this set contains no elements.         
         return list.isEmpty();
    }

    public Iterator iterator() {
     // Returns an iterator over the elements in this set.         
         return list.iterator();
    }

    public boolean remove(Object obj) {
     // Removes the specified element from this set if it is present (optional operation).         
         return list.remove(obj);
    }

    public boolean removeAll(Collection c) {
     // Removes from this set all of its elements that are contained in the specified collection (optional operation).         
         return list.removeAll(c);
    }

    public boolean retainAll(Collection c) {
     // Retains only the elements in this set that are contained in the specified collection (optional operation).         
        boolean ret = false;
        if(c != null) {
            Iterator iterator = c.iterator();
            while(iterator.hasNext()) {
                Object obj = iterator.next();
                if(!contains(obj)) {
                    remove(obj);
                    ret = true;
                }
            }
        }
        return ret;
    }

    public int size() {
     // Returns the number of elements in this set (its cardinality).         
         return list.size();
    }

    public Object[] toArray() {              
     // Returns an array containing all of the elements in this set.         
         return list.toArray();
    }

    public Object[] toArray(Object[] arr) {              
     // Returns an array containing all of the elements in this set whose runtime type is that of the specified array.
         return list.toArray(arr);
    }

}
