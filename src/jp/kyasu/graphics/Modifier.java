/*
 * Modifier.java
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

package jp.kyasu.graphics;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The <code>Modifier</code> class is the abstract base class for all
 * modifiers which define attributes and values for the modification.
 *
 * @version 	08 May 1998
 * @author 	Kazuki YASUMATSU
 */
public abstract class Modifier implements Cloneable, java.io.Serializable {
    /** the attributes and values for the modification. */
    protected Hashtable description;


    /**
     * The constant for the value "NULL".
     */
    static public final String NULL = "";


    /**
     * Constructs an empty modifier.
     */
    public Modifier() {
	description = new Hashtable(1, 1);
    }

    /**
     * Constructs a modifier that has the same attributes and values as
     * the specified modifier.
     *
     * @param modifier the modifier.
     */
    public Modifier(Modifier modifier) {
	this();
	if (modifier == null)
	    throw new NullPointerException();
	Enumeration ke = modifier.description.keys();
	Enumeration ve = modifier.description.elements();
	while (ke.hasMoreElements()) {
	    String key   = (String)ke.nextElement();
	    Object value = ve.nextElement();
	    put(key, value);
	}
    }


    /**
     * Returns the size of this modifier, i.e., the number of attributes
     * in this modifier.
     *
     * @return the size of this modifier.
     */
    public int size() {
	return description.size();
    }

    /**
     * Tests if this modifier has no attributes.
     *
     * @return <code>true</code> if this modifier has no attributes;
     *         <code>false</code> otherwise.
     */
    public boolean isEmpty() {
	return description.isEmpty();
    }

    /**
     * Returns an enumeration of the names of the attributes in this modifier.
     *
     * @return an enumeration of the names of the attributes in this modifier.
     */
    public Enumeration names() {
	return description.keys();
    }

    /**
     * Returns an enumeration of the values of the attributes in this modifier.
     *
     * @return an enumeration of the values of the attributes in this modifier.
     */
    public Enumeration values() {
	return description.elements();
    }

    /**
     * Tests if the attribute with the specified name is in this modifier.
     *
     * @param  name the name of the attribute.
     * @return <code>true</code> if the attribute with the specified name
     *         is in this modifier; <code>false</code> otherwise.
     */
    public boolean contains(String name) {
	return description.containsKey(name);
    }

    /**
     * Returns the value of the attribute with the specified name.
     *
     * @param  name the name of the attribute.
     * @return the value of the attribute with the specified name;
     *         <code>null</code> if the attribute is not defined.
     */
    public Object get(String name) {
	return description.get(name);
    }

    /**
     * Removes the attribute with the specified name.
     *
     * @param  name the name of the attribute.
     * @return the previous value of the attribute with the specified name;
     *         <code>null</code> if the attribute is not defined.
     */
    public Object remove(String name) {
	return description.remove(name);
    }

    /**
     * Puts a new attribute with the specified name and value.
     *
     * @param  name  the name of the attribute.
     * @param  value the value of the attribute.
     * @return the previous value of the attribute with the specified name;
     *         <code>null</code> if the attribute is not defined.
     */
    public Object put(String name, Object value) {
	return description.put(name, value);
    }

    /**
     * Puts a new attribute with the specified name and boolean value.
     */
    public Object put(String name, boolean value) {
	return put(name, new Boolean(value));
    }

    /**
     * Puts a new attribute with the specified name and byte value.
     */
    public Object put(String name, byte value) {
	return put(name, new Byte(value));
    }

    /**
     * Puts a new attribute with the specified name and character value.
     */
    public Object put(String name, char value) {
	return put(name, new Character(value));
    }

    /**
     * Puts a new attribute with the specified name and integer value.
     */
    public Object put(String name, int value) {
	return put(name, new Integer(value));
    }

    /**
     * Puts a new attribute with the specified name and long value.
     */
    public Object put(String name, long value) {
	return put(name, new Long(value));
    }

    /**
     * Puts a new attribute with the specified name and short value.
     */
    public Object put(String name, short value) {
	return put(name, new Short(value));
    }

    /**
     * Puts a new attribute with the specified name and float value.
     */
    public Object put(String name, float value) {
	return put(name, new Float(value));
    }

    /**
     * Puts a new attribute with the specified name and double value.
     */
    public Object put(String name, double value) {
	return put(name, new Double(value));
    }

    /**
     * Clears this modifier so that it contains no attributes.
     */
    public void clear() {
	description.clear();
    }

    /**
     * Modifies the given modifier, i.e., Creates the modified version
     * of the given modifier by putting all attributes in this
     * modifier into the given modifier. If the value of the attribute
     * to be put is "NULL", removes the corresponding attribute from
     * the given modifier.
     *
     * @param  modifier the given modifier.
     * @return the modified version of the given modifier; or the given
     *         modifier, if the modification has no effect on the given
     *         modifier.
     * @see    #NULL
     */
    public Modifier modify(Modifier modifier) {
	if (modifier == null)
	    throw new NullPointerException();
	if (isEmpty()) {
	    return modifier;
	}

	Modifier newModifier = (Modifier)modifier.clone();
	boolean modified = false;

	Enumeration ke = description.keys();
	Enumeration ve = description.elements();
	while (ke.hasMoreElements()) {
	    String key   = (String)ke.nextElement();
	    Object value = ve.nextElement();
	    if (NULL.equals(value)) {
		if (newModifier.remove(key) != null)
		    modified = true;
	    }
	    else {
		if (!value.equals(newModifier.put(key, value)))
		    modified = true;
	    }
	}

	if (modified) {
	    return newModifier;
	}
	else {
	    return modifier;
	}
    }

    /**
     * Returns a hashcode for this modifier.
     */
    public int hashCode() {
	int h = 0;
	Enumeration ke = description.keys();
	Enumeration ve = description.elements();
	while (ke.hasMoreElements()) {
	    h ^= ke.nextElement().hashCode();
	    h ^= ve.nextElement().hashCode();
	}
	return h;
    }

    /**
     * Compares two objects for equality.
     */
    public boolean equals(Object anObject) {
	if (this == anObject)
	    return true;
	if (anObject == null)
	    return false;
	if (getClass() == anObject.getClass()) {
	    return equalsDescription(((Modifier)anObject).description);
	}
	return false;
    }

    /**
     * Compares two descriptions in modifiers for equality.
     */
    protected boolean equalsDescription(Hashtable desc) {
	if (description.size() != desc.size()) {
	    return false;
	}
	Enumeration ke = description.keys();
	Enumeration ve = description.elements();
	while (ke.hasMoreElements()) {
	    String key   = (String)ke.nextElement();
	    Object value = ve.nextElement();
	    if (!value.equals(desc.get(key))) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Returns a clone of this modifier.
     */
    public Object clone() {
	try {
	    Modifier modifier = (Modifier)super.clone();
	    modifier.description = (Hashtable)description.clone();
	    return modifier;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns a string representation of this modifier.
     */
    public String toString() {
	return getClass().getName() + "[" + description.toString() + "]";
    }
}
