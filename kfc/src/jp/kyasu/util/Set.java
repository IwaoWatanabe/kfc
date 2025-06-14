/*
 * Set.java
 *
 * Copyright (c) 1997, 1998 Kazuki YASUMATSU.  All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose and without fee or royalty is hereby
 * granted, provided that both the above copyright notice and this
 * permission notice appear in all copies of the software and
 * documentation or portions thereof, including modifications, that you
 * make.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE NO
 * REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED. BY WAY OF EXAMPLE,
 * BUT NOT LIMITATION, COPYRIGHT HOLDERS MAKE NO REPRESENTATIONS OR
 * WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR
 * THAT THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY
 * THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 * COPYRIGHT HOLDERS WILL BEAR NO LIABILITY FOR ANY USE OF THIS SOFTWARE
 * OR DOCUMENTATION.
 */

package jp.kyasu.util;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The <code>Set</code> implements a collection of components that
 * are not duplicated.
 *
 * @see 	java.util.Hashtable
 *
 * @version 	23 Sep 1997
 * @author 	Kazuki YASUMATSU
 */
public class Set extends Hashtable {
    /**
     * The dummy value.
     */
    static protected final Object Value = new Object();


    /**
     * Constructs an empty set.
     */
    public Set() {
	super();
    }

    /**
     * Constructs an empty set with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the set.
     */
    public Set(int initialCapacity) {
	super(initialCapacity);
    }

    /**
     * Constructs an empty set with the specified initial capacity
     * and load factor.
     *
     * @param initialCapacity the initial capacity of the set.
     * @param loadFactor      a number between 0.0 and 1.0.
     */
    public Set(int initialCapacity, float loadFactor) {
	super(initialCapacity, loadFactor);
    }


    /**
     * Returns an enumeration of the values in this set.
     *
     * @return an enumeration of the values in this set.
     * @see    java.util.Enumeration
     * @see    java.util.Hashtable#keys()
     */
    public Enumeration elements() {
	return super.keys();
    }

    /**
     * Tests if the specified value in this set.
     *
     * @return <code>true</code> if the <code>value</code> argument
     *         in this set; <code>false</code> otherwise.
     * @see    java.util.Hashtable#containsKey(java.lang.Object)
     */
    public boolean contains(Object obj) {
	return super.containsKey(obj);
    }

    /**
     * Adds the object to this set. If this set already contains
     * the object, the object is not added.
     *
     * @param obj an object.
     * @see #addElement(java.lang.Object)
     */
    public void add(Object obj) {
	addElement(obj);
    }

    /**
     * Adds the object to this set. If this set already contains
     * the object, the object is not added.
     *
     * @param obj an object.
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    public void addElement(Object obj) {
	super.put(obj, Value);
    }

    /**
     * Adds the all components of the <code>set</code> to this set.
     * If this set already contains the component to be added,
     * the component is not added.
     *
     * @param set a set.
     */
    public synchronized void add(Set set) {
	for (Enumeration e = set.elements(); e.hasMoreElements(); ) {
	    addElement(e.nextElement());
	}
    }

    /**
     * Removes the object from this set.
     *
     * @param obj an object.
     * @see   java.util.Hashtable#remove(java.lang.Object)
     */
    public void removeElement(Object obj) {
	remove(obj);
    }

    /**
     * Removes all components from this array and sets its length to zero.
     *
     * @see java.util.Hashtable#clear()
     */
    public void removeAllElements() {
	super.clear();
    }
}
