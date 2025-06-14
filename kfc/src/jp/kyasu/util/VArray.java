/*
 * VArray.java
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

import java.lang.reflect.Array;
import java.util.Enumeration;

/**
 * The <code>VArray</code> class implements a variable length
 * (growable) array of objects and primitive types. Like an array,
 * it contains components that can be accessed using an integer
 * index. However, the size of a <code>VArray</code> can grow or
 * shrink as needed to accommodate adding and removing items after
 * the <code>VArray</code> has been created.
 * <p>
 * A <code>VArray</code> is constructed with the specified component
 * type. For example:
 * <pre>
 * VArray varray1 = new VArray(Object.class);
 * VArray varray2 = new VArray(String.class);
 * VArray varray3 = new VArray(int.class);
 * </pre>
 * The <code>VArray</code> has many useful operations. For example:
 * <pre>
 *     VArray varray1 = new VArray(int.class);
 *     varray1.append(1).append(2).append(3);
 *         // { 1, 2, 3 }
 *     VArray varray2 = (VArray)varray1.clone();
 *     varray1.append(varray2);
 *         // { 1, 2, 3, 1, 2, 3 }
 *     varray1.insert(0, varray2);
 *         // { 1, 2, 3, 1, 2, 3, 1, 2, 3 }
 *     varray1.insert(varray1.length(), varray2);
 *         // { 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3 }
 *     varray1.insert(3, varray2);
 *         // { 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3 }
 *     varray1.remove(0, 3);
 *         // { 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3 }
 *     varray1.remove(3, 3);
 *         // { 1, 2, 3, 1, 2, 3, 1, 2, 3 }
 *     varray1.replace(0, 0, varray2);
 *         // { 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3 }
 *     varray1.replace(varray1.length(), varray1.length(), varray2);
 *         // { 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3 }
 *     varray1.replace(3, 12, varray2);
 *         // { 1, 2, 3, 1, 2, 3, 1, 2, 3 }
 *     varray1.sort();
 *         // { 1, 1, 1, 2, 2, 2, 3, 3, 3 }
 * </pre>
 *
 * @version 	24 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class VArray implements Cloneable, java.io.Serializable {
    /**
     * The array buffer into which the components of the array are
     * stored. The capacity of the array is the length of this
     * array buffer.
     */
    protected Object array;

    /**
     * The number of valid components in the array.
     */
    protected int count;

    /**
     * The maximum amount by which the capacity of the array is
     * automatically incremented when its size becomes greater than
     * its capacity. If the <code>maxCapacityIncrement</code> is not
     * a positive value or the capacity is less than
     * <code>maxCapacityIncrement</code>, the capacity of the array
     * is doubled each time it needs to grow.
     */
    protected int maxCapacityIncrement;

    /**
     * True if the component type is not a primitive type.
     */
    protected boolean componentIsObject;


    /**
     * The default maximum amount by which the capacity of the
     * array is automatically incremented.
     */
    static protected final int DEFAULT_MAX_CAPACITY_INCREMENT = (4 * 1024);

    /**
     * The default maximum amount by which the capacity of the
     * array of <code>char</code>is automatically incremented.
     */
    static protected final int DEFAULT_STRING_MAX_CAPACITY_INCREMENT = (64 * 1024);


    /**
     * Constructs an empty array with the specified component type.
     *
     * @param componentType the component type of the array.
     */
    public VArray(Class componentType) {
	this(componentType, 16);
    }

    /**
     * Constructs an empty array with the specified component type
     * and initial capacity.
     *
     * @param componentType   the component type of the array.
     * @param initialCapacity the initial capacity of the array.
     */
    public VArray(Class componentType, int initialCapacity) {
	this(componentType, initialCapacity, DEFAULT_MAX_CAPACITY_INCREMENT);
    }

    /**
     * Constructs an empty array with the specified component type,
     * initial capacity, and the maximum capacity increment size.
     *
     * @param componentType        the component type of the array.
     * @param initialCapacity      the initial capacity of the array.
     * @param maxCapacityIncrement the maximum amount by which the capacity
     *                             is increased when the array overflows.
     */
    public VArray(Class componentType,
		  int initialCapacity,
		  int maxCapacityIncrement)
    {
	if (componentType == null)
	    throw new NullPointerException();
	this.array = Array.newInstance(componentType, initialCapacity);
	this.count = 0;
	this.maxCapacityIncrement = maxCapacityIncrement;
	this.componentIsObject = isObjectType(componentType);
    }

    /**
     * Constructs an array with the specified array buffer.
     *
     * @param array the buffer of the array.
     */
    public VArray(Object array) {
	this(array, DEFAULT_MAX_CAPACITY_INCREMENT);
    }

    /**
     * Constructs an array with the specified array buffer and
     * the maximum capacity increment size.
     *
     * @param array                the buffer of the array.
     * @param maxCapacityIncrement the maximum amount by which the capacity
     *                             is increased when the array overflows.
     */
    public VArray(Object array, int maxCapacityIncrement) {
	if (array == null)
	    throw new NullPointerException();
	this.array = array;
	this.count = Array.getLength(array);
	this.maxCapacityIncrement = maxCapacityIncrement;
	this.componentIsObject =
			isObjectType(array.getClass().getComponentType());
    }

    /**
     * Constructs an array with the contents of the string.
     *
     * @param string a string.
     */
    public VArray(String string) {
	this(string, DEFAULT_STRING_MAX_CAPACITY_INCREMENT);
    }

    /**
     * Constructs an array with the contents of the string
     * and the maximum capacity increment size.
     *
     * @param string               a string.
     * @param maxCapacityIncrement the maximum amount by which the capacity
     *                             is increased when the array overflows.
     */
    public VArray(String string, int maxCapacityIncrement) {
	if (string == null)
	    throw new NullPointerException();
	this.count = string.length();
	this.array = new char[this.count];
	string.getChars(0, this.count, (char[])this.array, 0);
	this.maxCapacityIncrement = maxCapacityIncrement;
	this.componentIsObject = false;
    }


    /**
     * Returns the buffer of this array. An application should not modify
     * the returned array.
     *
     * @return the buffer of this array.
     */
    public final Object getArray() {
	return array;
    }

    /**
     * Returns an array object whoes length is trimmed to be this
     * array's current length.
     *
     * @return an array object whoes length is trimmed to be this
     *         array's current length.
     */
    public final Object getTrimmedArray() {
	Object trimmedArray = newArray(count);
	System.arraycopy(array, 0, trimmedArray, 0, count);
	return trimmedArray;
    }

    /**
     * Returns the component type of this array.
     *
     * @return the component type of this array.
     */
    public final Class getComponentType() {
	return array.getClass().getComponentType();
    }

    /**
     * Returns the size of this array, i.e., the number of components
     * in this array.
     *
     * @return the size of this array.
     */
    public final int size() {
	return count;
    }

    /**
     * Returns the size of this array, i.e., the number of components
     * in this array.
     *
     * @return the size of this array.
     */
    public final int getSize() {
	return count;
    }

    /**
     * Returns the length of this array, i.e., the number of components
     * in this array.
     *
     * @return the length of this array.
     */
    public final int length() {
	return count;
    }

    /**
     * Returns the length of this array, i.e., the number of components
     * in this array.
     *
     * @return the length of this array.
     */
    public final int getLength() {
	return count;
    }

    /**
     * Tests if this array has no components.
     *
     * @return <code>true</code> if this array has no components;
     *         <code>false</code> otherwise.
     */
    public final boolean isEmpty() {
	return count == 0;
    }

    /**
     * Returns a hashcode for this array.
     *
     * @return  a hash code value for this array.
     */
    public int hashCode() {
	int h = 0;
	int off = 0;
	int len = count;
	int skip = (len >= 16 ? len / 8 : 1);
	Class componentType = getComponentType();
	if (componentType.equals(boolean.class)) {
	    boolean val[] = (boolean[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + (val[off] ? 1231 : 1237);
	    }
	}
	else if (componentType.equals(byte.class)) {
	    byte val[] = (byte[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + (int)val[off];
	    }
	}
	else if (componentType.equals(char.class)) {
	    char val[] = (char[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + (int)val[off];
	    }
	}
	else if (componentType.equals(short.class)) {
	    short val[] = (short[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + (int)val[off];
	    }
	}
	else if (componentType.equals(int.class)) {
	    int val[] = (int[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + val[off];
	    }
	}
	else if (componentType.equals(long.class)) {
	    long val[] = (long[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + (int)(val[off] ^ (val[off] >> 32));
	    }
	}
	else if (componentType.equals(float.class)) {
	    float val[] = (float[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + Float.floatToIntBits(val[off]);
	    }
	}
	else if (componentType.equals(double.class)) {
	    double val[] = (double[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		long bits = Double.doubleToLongBits(val[off]);
		h = (h * 39) + (int)(bits ^ (bits >> 32));
	    }
	}
	else {
	    Object val[] = (Object[])array;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + val[off].hashCode();
	    }
	}
	return h;
    }

    /**
     * Compares two Objects for equality.
     *
     * @param  anObject the reference object with which to compare.
     * @return <code>true</code> if this array is the same as the anObject
     *         argument; <code>false</code> otherwise.
     */
    public boolean equals(Object anObject) {
	if (this == anObject)
	    return true;
	if (anObject == null)
	    return false;
	if (anObject instanceof VArray) {
	    VArray anotherVArray = (VArray)anObject;
	    int n = count;
	    Class componentType = getComponentType();
	    if (n == anotherVArray.count &&
		componentType.equals(anotherVArray.getComponentType()))
	    {
		int i = 0;
		int j = 0;
		if (componentType.equals(boolean.class)) {
		    while (n-- != 0) {
			if (getBoolean(i++) != anotherVArray.getBoolean(j++))
			    return false;
		    }
		    return true;
		}
		else if (componentType.equals(byte.class)) {
		    while (n-- != 0) {
			if (getByte(i++) != anotherVArray.getByte(j++))
			    return false;
		    }
		    return true;
		}
		else if (componentType.equals(char.class)) {
		    while (n-- != 0) {
			if (getChar(i++) != anotherVArray.getChar(j++))
			    return false;
		    }
		    return true;
		}
		else if (componentType.equals(short.class)) {
		    while (n-- != 0) {
			if (getShort(i++) != anotherVArray.getShort(j++))
			    return false;
		    }
		    return true;
		}
		else if (componentType.equals(int.class)) {
		    while (n-- != 0) {
			if (getInt(i++) != anotherVArray.getInt(j++))
			    return false;
		    }
		    return true;
		}
		else if (componentType.equals(long.class)) {
		    while (n-- != 0) {
			if (getLong(i++) != anotherVArray.getLong(j++))
			    return false;
		    }
		    return true;
		}
		else if (componentType.equals(float.class)) {
		    while (n-- != 0) {
			if (getFloat(i++) != anotherVArray.getFloat(j++))
			    return false;
		    }
		    return true;
		}
		else if (componentType.equals(double.class)) {
		    while (n-- != 0) {
			if (getDouble(i++) != anotherVArray.getDouble(j++))
			    return false;
		    }
		    return true;
		}
		else {
		    while (n-- != 0) {
			if (!get(i++).equals(anotherVArray.get(j++)))
			    return false;
		    }
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Returns an enumeration of the components of this array.
     *
     * @return an enumeration of the components of this array.
     */
    public final Enumeration elements() {
	return new VArrayEnumerator(this);
    }

    /**
     * Returns the component at the specified index, as an object.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#get(java.lang.Object, int)
     */
    public final Object get(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.get(array, index);
    }

    /**
     * Returns the component at the specified index, as a boolean.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#getBoolean(java.lang.Object, int)
     */
    public final boolean getBoolean(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.getBoolean(array, index);
    }

    /**
     * Returns the component at the specified index, as a byte.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#getByte(java.lang.Object, int)
     */
    public final byte getByte(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.getByte(array, index);
    }

    /**
     * Returns the component at the specified index, as a char.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#getChar(java.lang.Object, int)
     */
    public final char getChar(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.getChar(array, index);
    }

    /**
     * Returns the component at the specified index, as a short.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#getShort(java.lang.Object, int)
     */
    public final short getShort(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.getShort(array, index);
    }

    /**
     * Returns the component at the specified index, as an int.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#getInt(java.lang.Object, int)
     */
    public final int getInt(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.getInt(array, index);
    }

    /**
     * Returns the component at the specified index, as a long.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#getLong(java.lang.Object, int)
     */
    public final long getLong(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.getLong(array, index);
    }

    /**
     * Returns the component at the specified index, as a float.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#getFloat(java.lang.Object, int)
     */
    public final float getFloat(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.getFloat(array, index);
    }

    /**
     * Returns the component at the specified index, as a double.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to the return type.
     * @see       java.lang.reflect.Array#getDouble(java.lang.Object, int)
     */
    public final double getDouble(int index) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	return Array.getDouble(array, index);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified object.
     *
     * @param     index the specified index.
     * @param     value what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#set(java.lang.Object, int, java.lang.Object)
     */
    public final void set(int index, Object value) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.set(array, index, value);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified boolean value.
     *
     * @param     index the specified index.
     * @param     b     what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#setBoolean(java.lang.Object, int, boolean)
     */
    public final void setBoolean(int index, boolean b) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.setBoolean(array, index, b);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified byte value.
     *
     * @param     index the specified index.
     * @param     b     what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#setByte(java.lang.Object, int, byte)
     */
    public final void setByte(int index, byte b) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.setByte(array, index, b);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified char value.
     *
     * @param     index the specified index.
     * @param     c     what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#setChar(java.lang.Object, int, char)
     */
    public final void setChar(int index, char c) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.setChar(array, index, c);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified short value.
     *
     * @param     index the specified index.
     * @param     s     what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#setShort(java.lang.Object, int, short)
     */
    public final void setShort(int index, short s) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.setShort(array, index, s);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified int value.
     *
     * @param     index the specified index.
     * @param     i     what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#setInt(java.lang.Object, int, int)
     */
    public final void setInt(int index, int i) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.setInt(array, index, i);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified long value.
     *
     * @param     index the specified index.
     * @param     l     what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#setLong(java.lang.Object, int, long)
     */
    public final void setLong(int index, long l) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.setLong(array, index, l);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified float value.
     *
     * @param     index the specified index.
     * @param     f     what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#setFloat(java.lang.Object, int, float)
     */
    public final void setFloat(int index, float f) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.setFloat(array, index, f);
    }

    /**
     * Sets the component at the specified index of this array to be
     * the specified double value.
     *
     * @param     index the specified index.
     * @param     d     what the component is to be set to.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     * @exception IllegalArgumentException if the indexed element cannot
     *            be converted to this array's component type.
     * @see       java.lang.reflect.Array#setDouble(java.lang.Object, int, double)
     */
    public final void setDouble(int index, double d) {
	if (index >= count) throw new ArrayIndexOutOfBoundsException(index);
	Array.setDouble(array, index, d);
    }

    /**
     * Sets the length of this array.
     *
     * @param newLength the new length of this array.
     */
    public final void setLength(int newLength) {
	if (newLength < 0) {
	    throw new ArrayIndexOutOfBoundsException(newLength);
	}
	ensureCapacity(newLength);
	count = newLength;
    }

    /**
     * Trims the capacity of this array to be the array's current length.
     */
    public final void trim() {
	int capacity = capacity();
	if (count < capacity) {
	    Object newArray = newArray(count);
	    System.arraycopy(array, 0, newArray, 0, count);
	    array = newArray;
	}
    }

    /**
     * Searches for the first occurence of the specified object, testing
     * for equality using the <code>equals</code> method.
     *
     * @param   value the specified object.
     * @return  the index of the first occurrence of the specified object
     *          in this array; returns <code>-1</code> if the object is
     *          not found.
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    public final int indexOf(Object value) {
	return indexOf(value, 0);
    }

    /**
     * Searches for the first occurence of the specified object, beginning
     * the search at <code>fromIndex</code>, and testing for equality
     * using the <code>equals</code> method.
     *
     * @param   value     the specified object.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified object
     *          in this array; returns <code>-1</code> if the object is
     *          not found.
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    public final int indexOf(Object value, int fromIndex) {
	int length = length();
	Object v[] = (Object[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i].equals(value))
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified object,
     * testing for equality using the <code>equals</code> method.
     *
     * @param   value the specified object.
     * @return  the index of the last occurrence of the specified object
     *          in this array; returns <code>-1</code> if the object is
     *          not found.
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    public final int lastIndexOf(Object value) {
	return lastIndexOf(value, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified object,
     * beginning the search at <code>fromIndex</code>, and testing for
     * equality using the <code>equals</code> method.
     *
     * @param   value     the specified object.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified object
     *          in this array; returns <code>-1</code> if the object is
     *          not found.
     * @see     java.lang.Object#equals(java.lang.Object)
     */
    public final int lastIndexOf(Object value, int fromIndex) {
	int length = length();
	Object v[] = (Object[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i].equals(value))
		return i;
	}
	return -1;
    }

    /**
     * Searches for the first occurence of the specified boolean value.
     *
     * @param   b the specified boolean value.
     * @return  the index of the first occurrence of the specified boolean
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(boolean b) {
	return indexOf(b, 0);
    }

    /**
     * Searches for the first occurence of the specified boolean value,
     * beginning the search at <code>fromIndex</code>.
     *
     * @param   b         the specified boolean value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified boolean
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(boolean b, int fromIndex) {
	int length = length();
	boolean v[] = (boolean[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i] == b)
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified boolean
     * value.
     *
     * @param   b the specified boolean value.
     * @return  the index of the last occurrence of the specified boolean
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(boolean b) {
	return lastIndexOf(b, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified boolean
     * value, beginning the search at <code>fromIndex</code>.
     *
     * @param   b         the specified boolean value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified boolean
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(boolean b, int fromIndex) {
	int length = length();
	boolean v[] = (boolean[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == b)
		return i;
	}
	return -1;
    }

    /**
     * Searches for the first occurence of the specified byte value.
     *
     * @param   b the specified byte value.
     * @return  the index of the first occurrence of the specified byte
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(byte b) {
	return indexOf(b, 0);
    }

    /**
     * Searches for the first occurence of the specified byte value,
     * beginning the search at <code>fromIndex</code>.
     *
     * @param   b         the specified byte value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified byte
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(byte b, int fromIndex) {
	int length = length();
	byte v[] = (byte[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i] == b)
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified byte
     * value.
     *
     * @param   b the specified byte value.
     * @return  the index of the last occurrence of the specified byte
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(byte b) {
	return lastIndexOf(b, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified byte
     * value, beginning the search at <code>fromIndex</code>.
     *
     * @param   b         the specified byte value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified byte
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(byte b, int fromIndex) {
	int length = length();
	byte v[] = (byte[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == b)
		return i;
	}
	return -1;
    }

    /**
     * Searches for the first occurence of the specified char value.
     *
     * @param   c the specified char value.
     * @return  the index of the first occurrence of the specified char
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(char c) {
	return indexOf(c, 0);
    }

    /**
     * Searches for the first occurence of the specified char value,
     * beginning the search at <code>fromIndex</code>.
     *
     * @param   c         the specified char value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified char
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(char c, int fromIndex) {
	int length = length();
	char v[] = (char[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i] == c)
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified char
     * value.
     *
     * @param   c the specified char value.
     * @return  the index of the last occurrence of the specified char
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(char c) {
	return lastIndexOf(c, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified char
     * value, beginning the search at <code>fromIndex</code>.
     *
     * @param   c         the specified char value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified char
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(char c, int fromIndex) {
	int length = length();
	char v[] = (char[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == c)
		return i;
	}
	return -1;
    }

    /**
     * Searches for the first occurence of the specified short value.
     *
     * @param   s the specified short value.
     * @return  the index of the first occurrence of the specified short
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(short s) {
	return indexOf(s, 0);
    }

    /**
     * Searches for the first occurence of the specified short value,
     * beginning the search at <code>fromIndex</code>.
     *
     * @param   s         the specified short value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified short
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(short s, int fromIndex) {
	int length = length();
	short v[] = (short[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i] == s)
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified short
     * value.
     *
     * @param   s the specified short value.
     * @return  the index of the last occurrence of the specified short
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(short s) {
	return lastIndexOf(s, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified short
     * value, beginning the search at <code>fromIndex</code>.
     *
     * @param   s         the specified short value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified short
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(short s, int fromIndex) {
	int length = length();
	short v[] = (short[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == s)
		return i;
	}
	return -1;
    }

    /**
     * Searches for the first occurence of the specified int value.
     *
     * @param   iv the specified int value.
     * @return  the index of the first occurrence of the specified int
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(int iv) {
	return indexOf(iv, 0);
    }

    /**
     * Searches for the first occurence of the specified int value,
     * beginning the search at <code>fromIndex</code>.
     *
     * @param   iv        the specified int value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified int
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(int iv, int fromIndex) {
	int length = length();
	int v[] = (int[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i] == iv)
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified int
     * value.
     *
     * @param   iv the specified int value.
     * @return  the index of the last occurrence of the specified int
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(int iv) {
	return lastIndexOf(iv, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified int
     * value, beginning the search at <code>fromIndex</code>.
     *
     * @param   iv        the specified int value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified int
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(int iv, int fromIndex) {
	int length = length();
	int v[] = (int[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == iv)
		return i;
	}
	return -1;
    }

    /**
     * Searches for the first occurence of the specified long value.
     *
     * @param   l the specified long value.
     * @return  the index of the first occurrence of the specified long
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(long l) {
	return indexOf(l, 0);
    }

    /**
     * Searches for the first occurence of the specified long value,
     * beginning the search at <code>fromIndex</code>.
     *
     * @param   l         the specified long value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified long
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(long l, int fromIndex) {
	int length = length();
	long v[] = (long[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i] == l)
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified long
     * value.
     *
     * @param   l the specified long value.
     * @return  the index of the last occurrence of the specified long
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(long l) {
	return lastIndexOf(l, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified long
     * value, beginning the search at <code>fromIndex</code>.
     *
     * @param   l         the specified long value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified long
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(long l, int fromIndex) {
	int length = length();
	long v[] = (long[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == l)
		return i;
	}
	return -1;
    }

    /**
     * Searches for the first occurence of the specified float value.
     *
     * @param   f the specified float value.
     * @return  the index of the first occurrence of the specified float
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(float f) {
	return indexOf(f, 0);
    }

    /**
     * Searches for the first occurence of the specified float value,
     * beginning the search at <code>fromIndex</code>.
     *
     * @param   f         the specified float value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified float
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(float f, int fromIndex) {
	int length = length();
	float v[] = (float[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i] == f)
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified float
     * value.
     *
     * @param   f the specified float value.
     * @return  the index of the last occurrence of the specified float
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(float f) {
	return lastIndexOf(f, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified float
     * value, beginning the search at <code>fromIndex</code>.
     *
     * @param   f         the specified float value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified float
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(float f, int fromIndex) {
	int length = length();
	float v[] = (float[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == f)
		return i;
	}
	return -1;
    }

    /**
     * Searches for the first occurence of the specified double value.
     *
     * @param   d the specified double value.
     * @return  the index of the first occurrence of the specified double
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(double d) {
	return indexOf(d, 0);
    }

    /**
     * Searches for the first occurence of the specified double value,
     * beginning the search at <code>fromIndex</code>.
     *
     * @param   d         the specified double value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the first occurrence of the specified double
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int indexOf(double d, int fromIndex) {
	int length = length();
	double v[] = (double[])getArray();
	int i = ((fromIndex < 0) ? 0 : fromIndex);
	for (; i < length; i++) {
	    if (v[i] == d)
		return i;
	}
	return -1;
    }

    /**
     * Searches backwards for the last occurence of the specified double
     * value.
     *
     * @param   d the specified double value.
     * @return  the index of the last occurrence of the specified double
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(double d) {
	return lastIndexOf(d, length() - 1);
    }

    /**
     * Searches backwards for the last occurence of the specified double
     * value, beginning the search at <code>fromIndex</code>.
     *
     * @param   d         the specified double value.
     * @param   fromIndex the index to start searching from.
     * @return  the index of the last occurrence of the specified double
     *          value in this array; returns <code>-1</code> if the value is
     *          not found.
     */
    public final int lastIndexOf(double d, int fromIndex) {
	int length = length();
	double v[] = (double[])getArray();
	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == d)
		return i;
	}
	return -1;
    }

    /**
     * Removes all components from this array and sets its length to zero.
     */
    public final void removeAll() {
	array = newArray(16);
	count = 0;
    }

    /**
     * Removes the components in this array from the specified
     * <code>offset</code>. The number of the components to be removed is
     * specified by the <code>size</code>. Each component in this array
     * with an index greater or equal to <code>offset+size</code> is
     * shifted downward.
     *
     * @param     offset the start index of the components to be removed.
     * @param     size   the number of the components to be removed.
     * @exception ArrayIndexOutOfBoundsException if the <code>offset</code>
     *            or the <code>size</code> were invalid.
     */
    public final void remove(int offset, int size) {
	if ((offset < 0) || (offset + size > count)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	if (size > 0) {
	    System.arraycopy(array, offset + size,
			     array, offset,
			     count - (offset + size));
	    count -= size;
	    shrinkCapacity();
	    if (componentIsObject) { // for GC
		int limit = Math.min(capacity(), count + size);
		Object oarray[] = (Object[])array;
		for (int i = count; i < limit; i++) {
		    oarray[i] = null;
		}
	    }
	}
    }

    /**
     * Returns a new array that is a subarray of this array. The subarray
     * begins at the specified index and extends to the end of this array.
     *
     * @param     beginIndex the beginning index, inclusive.
     * @return    the subarray.
     * @exception ArrayIndexOutOfBoundsException if the
     *            <code>beginIndex</code> is out of range.
     */
    public final VArray subarray(int beginIndex) {
	return subarray(beginIndex, count);
    }

    /**
     * Returns a new array that is a subarray of this array. The subarray
     * begins at the specified <code>beginIndex</code> and extends to the
     * component at index <code>endIndex-1</code>.
     *
     * @param     beginIndex the beginning index, inclusive.
     * @param     endIndex   the ending index, exclusive.
     * @return    the subarray.
     * @exception ArrayIndexOutOfBoundsException if the <code>beginIndex</code>
     *            or the <code>endIndex</code> is out of range.
     */
    public final VArray subarray(int beginIndex, int endIndex) {
	if ((beginIndex < 0) || (endIndex > count) || (beginIndex > endIndex))
	{
	    throw new ArrayIndexOutOfBoundsException();
	}
	int subLength = endIndex - beginIndex;
	Object newArray = newArray(subLength);
	System.arraycopy(array, beginIndex, newArray, 0, subLength);
	return new VArray(newArray, maxCapacityIncrement);
    }

    /**
     * Appends the object to this array.
     *
     * @param  value an object.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(Object value) {
	ensureCapacity(count + 1);
	Array.set(array, count++, value);
	return this;
    }

    /**
     * Appends the boolean value to this array.
     *
     * @param  b a boolean value.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(boolean b) {
	ensureCapacity(count + 1);
	Array.setBoolean(array, count++, b);
	return this;
    }

    /**
     * Appends the byte value to this array.
     *
     * @param  b a byte value.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(byte b) {
	ensureCapacity(count + 1);
	Array.setByte(array, count++, b);
	return this;
    }

    /**
     * Appends the char value to this array.
     *
     * @param  c a char value.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(char c) {
	ensureCapacity(count + 1);
	Array.setChar(array, count++, c);
	return this;
    }

    /**
     * Appends the short value to this array.
     *
     * @param  s a short value.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(short s) {
	ensureCapacity(count + 1);
	Array.setShort(array, count++, s);
	return this;
    }

    /**
     * Appends the int value to this array.
     *
     * @param  i an int value.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(int i) {
	ensureCapacity(count + 1);
	Array.setInt(array, count++, i);
	return this;
    }

    /**
     * Appends the long value to this array.
     *
     * @param  l a long value.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(long l) {
	ensureCapacity(count + 1);
	Array.setLong(array, count++, l);
	return this;
    }

    /**
     * Appends the float value to this array.
     *
     * @param  f a float value.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(float f) {
	ensureCapacity(count + 1);
	Array.setFloat(array, count++, f);
	return this;
    }

    /**
     * Appends the double value to this array.
     *
     * @param  d a double value.
     * @return this array.
     * @exception IllegalArgumentException if the value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(double d) {
	ensureCapacity(count + 1);
	Array.setDouble(array, count++, d);
	return this;
    }

    /**
     * Appends the characters of the <code>string</code> to this array.
     *
     * @param  str a string.
     * @return this array.
     * @exception IllegalArgumentException if the character value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(String str) {
    	if (getComponentType() == char.class) {
	    return append(str, 0, str.length());
	}
	else {
	    ensureCapacity(count + 1);
	    Array.set(array, count++, str);
	    return this;
	}
    }

    /**
     * Appends the characters of the <code>string</code> from the specified
     * <code>begin</code> index to the specified <code>endIndex-1</code> index.
     *
     * @param  str   a string.
     * @param  begin the beginning index of the string, inclusive.
     * @param  end   the ending index of the string, exclusive.
     * @return this array.
     * @exception IllegalArgumentException if the character value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(String str, int begin, int end) {
	int length = end - begin;
	char carray[] = new char[length];
	str.getChars(begin, end, carray, 0);
	return append(carray, begin, end);
    }

    /**
     * Appends the characters of the array object to this array.
     *
     * @param  carray an array of characters.
     * @return this array.
     * @exception IllegalArgumentException if the character value cannot
     *            be converted to this array's component type.
     */
    public final VArray append(char carray[]) {
	return append(carray, 0, carray.length);
    }

    /**
     * Appends the characters of the array object to this array.
     *
     * @param  carray an array of characters.
     * @param  begin  the beginning index of the character array, inclusive.
     * @param  end    the ending index of the character array, exclusive.
     * @return this array.
     * @exception IllegalArgumentException if the character value cannot
     *            be converted to this array's component type.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>
     *            or the <code>end</code> is out of range.
     */
    public final VArray append(char carray[], int begin, int end) {
	if ((begin < 0) || (end > carray.length) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	int length = end - begin;
	ensureCapacity(count + length);
	System.arraycopy(carray, begin,
			 array, count,
			 length);
	count += length;
	return this;
    }

    /**
     * Appends the components of the <code>VArray</code> object to
     * this array.
     *
     * @param  varray an <code>VArray</code> object.
     * @return this array.
     * @exception IllegalArgumentException if the component type of
     *            the argument cannot be converted to this array's
     *            component type.
     */
    public final VArray append(VArray varray) {
	return append(varray, 0, varray.length());
    }

    /**
     * Appends the components of the <code>VArray</code> object to
     * this array.
     *
     * @param  varray an <code>VArray</code> object.
     * @param  begin  the beginning index of the array, inclusive.
     * @param  end    the ending index of the array, exclusive.
     * @return this array.
     * @exception IllegalArgumentException if the component type of
     *            the argument cannot be converted to this array's
     *            component type.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>
     *            or the <code>end</code> is out of range.
     */
    public final VArray append(VArray varray, int begin, int end) {
	if ((begin < 0) || (end > varray.count) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	int length = end - begin;
	ensureCapacity(count + length);
	System.arraycopy(varray.array, begin,
			 array, count,
			 length);
	count += length;
	return this;
    }

    /**
     * Inserts the components of the <code>VArray</code> object to
     * this array from the specified <code>offset</code>.
     *
     * @param  offset the start index of the components to be inserted.
     * @param  varray an <code>VArray</code> object.
     * @return this array.
     * @exception IllegalArgumentException if the component type of
     *            the argument cannot be converted to this array's
     *            component type.
     */
    public final VArray insert(int offset, VArray varray) {
	return insert(offset, varray, 0, varray.length());
    }

    /**
     * Inserts the components of the <code>VArray</code> object to
     * this array from the specified <code>offset</code>.
     *
     * @param  offset the start index of the components to be inserted.
     * @param  varray an <code>VArray</code> object.
     * @param  begin  the beginning index of the array, inclusive.
     * @param  end    the ending index of the array, exclusive.
     * @return this array.
     * @exception IllegalArgumentException if the component type of
     *            the argument cannot be converted to this array's
     *            component type.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>
     *            or the <code>end</code> is out of range.
     */
    public final VArray insert(int offset, VArray varray, int begin, int end) {
	if ((offset < 0) || (offset > count)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	if ((begin < 0) || (end > varray.count) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	int length = end - begin;
	if (length > 0) {
	    ensureCapacity(count + length);
	    System.arraycopy(array, offset,
			     array, offset + length,
			     count - offset);
	    System.arraycopy(varray.array, begin,
			     array, offset,
			     length);
	    count += length;
	}
	return this;
    }

    /**
     * Inserts spaces to this array from the specified <code>offset</code>.
     *
     * @param  offset the start index of spaces to be inserted.
     * @param  size   the number of spaces to be inserted.
     * @return this array.
     * @exception ArrayIndexOutOfBoundsException if the <code>offset</code>
     *            or the <code>size</code> were invalid.
     */
    protected final VArray insertSpace(int offset, int size) {
	if ((offset < 0) || (offset > count)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	if (size > 0) {
	    ensureCapacity(count + size);
	    System.arraycopy(array, offset,
			     array, offset + size,
			     count - offset);
	    count += size;
	}
	return this;
    }

    /**
     * Replaces the components of this array with the components of the
     * <code>VArray</code> object.
     *
     * @param  begin  the beginning index to replace, inclusive.
     * @param  end    the ending index to replace, exclusive.
     * @param  varray a replacement <code>VArray</code> object.
     * @return this array.
     * @exception IllegalArgumentException if the component type of
     *            the argument cannot be converted to this array's
     *            component type.
     */
    public final VArray replace(int begin, int end, VArray varray) {
	return replace(begin, end, varray, 0, varray.length());
    }

    /**
     * Replaces the components of this array with the components of the
     * <code>VArray</code> object.
     *
     * @param  begin  the beginning index to replace, inclusive.
     * @param  end    the ending index to replace, exclusive.
     * @param  varray a replacement <code>VArray</code> object.
     * @param  rBegin the beginning index of the replacement, inclusive.
     * @param  rEnd   the ending index of the replacement, exclusive.
     * @return this array.
     * @exception IllegalArgumentException if the component type of
     *            the argument cannot be converted to this array's
     *            component type.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>,
     *            the <code>end</code>, the <code>rBegin</code>, or
     *            the <code>rEnd</code> is out of range.
     */
    public final VArray replace(int begin, int end, VArray varray,
				int rBegin, int rEnd)
    {
	if ((begin < 0) || (end > count)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	if ((rBegin < 0) || (rEnd > varray.count) || (rBegin > rEnd)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	int length = end - begin;
	int rLength = rEnd - rBegin;
	int newLength = count - length + rLength;
	ensureCapacity(newLength);
	int slideIndex = begin + rLength;
	if (end != slideIndex) {
	    System.arraycopy(array, end,
			     array, slideIndex,
			     newLength - slideIndex);
	}
	System.arraycopy(varray.array, rBegin,
			 array, begin,
			 rLength);
	length = count;
	count = newLength;
	if (newLength < length) {
	    shrinkCapacity();
	    if (componentIsObject) { // for GC
		int limit = Math.min(capacity(), length);
		Object oarray[] = (Object[])array;
		for (int i = newLength; i < limit; i++) {
		    oarray[i] = null;
		}
	    }
	}
	return this;
    }

    /**
     * Sorts the components of this array.
     *
     * @see jp.kyasu.util.Sorter#quicksort(java.lang.Object, int, int)
     */
    public final void sort() {
	(new Sorter()).quicksort(array, 0, count - 1);
    }

    /**
     * Sorts the components of this array with the specified comparer.
     *
     * @param comparer a comparer used by sorting.
     * @see   jp.kyasu.util.Comparer
     * @see   jp.kyasu.util.Sorter#quicksort(java.lang.Object, int, int)
     */
    public final void sort(Comparer comparer) {
	(new Sorter(comparer)).quicksort(array, 0, count - 1);
    }

    /**
     * Sorts the components of this array with the specified comparer.
     *
     * @param i        the beginning index to sort, inclusive.
     * @param j        the ending index to sort, inclusive.
     * @param comparer a comparer used by sorting.
     * @see   jp.kyasu.util.Comparer
     * @see   jp.kyasu.util.Sorter#quicksort(java.lang.Object, int, int)
     */
    public final void sort(int i, int j, Comparer comparer) {
	if (i >= j)
	    return;
	if ((i < 0) || (j >= count)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	(new Sorter(comparer)).quicksort(array, i, j);
    }

    /**
     * Returns a clone of this array.
     *
     * @return a clone of this array.
     */
    public Object clone() {
	try {
	    VArray va = (VArray)super.clone();
	    va.count = count;
	    va.maxCapacityIncrement = maxCapacityIncrement;
	    va.array = newArray(count);
	    System.arraycopy(array, 0, va.array, 0, count);
	    return va;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns a string representation of this array.
     *
     * @return a string representation of this array.
     */
    public String toString() {
	Class componentType = getComponentType();
	int max = length() - 1;
	StringBuffer buf = new StringBuffer();
	buf.append("[");
				
	if (componentType.equals(boolean.class)) {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(getInt(i));
		if (i < max) buf.append(", ");
	    }
	}
	else if (componentType.equals(byte.class)) {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(getByte(i));
		if (i < max) buf.append(", ");
	    }
	}
	else if (componentType.equals(char.class)) {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(getChar(i));
		if (i < max) buf.append(", ");
	    }
	}
	else if (componentType.equals(short.class)) {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(getShort(i));
		if (i < max) buf.append(", ");
	    }
	}
	else if (componentType.equals(int.class)) {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(getInt(i));
		if (i < max) buf.append(", ");
	    }
	}
	else if (componentType.equals(long.class)) {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(getLong(i));
		if (i < max) buf.append(", ");
	    }
	}
	else if (componentType.equals(float.class)) {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(getFloat(i));
		if (i < max) buf.append(", ");
	    }
	}
	else if (componentType.equals(double.class)) {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(getDouble(i));
		if (i < max) buf.append(", ");
	    }
	}
	else {
	    for (int i = 0 ; i <= max ; i++) {
		buf.append(get(i));
		if (i < max) buf.append(", ");
	    }
	}

	buf.append("]");
	return buf.toString();
    }

    /** Returns the capacity of this array. */
    protected final int capacity() {
	return Array.getLength(array);
    }

    /**
     * Increases the capacity of this array, if necessary, to ensure
     * that it can hold at least the number of components specified by
     * the minimum capacity argument.
     */
    protected final void ensureCapacity(int minimumCapacity) {
	int capacity = capacity();
	if (minimumCapacity > capacity) {
	    int capacityIncrement = capacity;
	    if (maxCapacityIncrement > 0 &&
		capacityIncrement > maxCapacityIncrement)
	    {
		capacityIncrement = maxCapacityIncrement;
	    }
	    int newCapacity = capacity + capacityIncrement;
	    if (minimumCapacity > newCapacity) {
		newCapacity = minimumCapacity;
	    }
	    Object newArray = newArray(newCapacity);
	    System.arraycopy(array, 0, newArray, 0, count);
	    array = newArray;
	}
    }

    /** Shrinks the capacity of this array, if necessary. */
    protected final void shrinkCapacity() {
	int capacity = capacity();
	if (capacity < (8 * 4))
	    return;
	int newCapacity = capacity / 4;
	if (count < newCapacity) {
	    Object newArray = newArray(newCapacity);
	    System.arraycopy(array, 0, newArray, 0, count);
	    array = newArray;
	}
    }

    /**
     * Returns the new array whose component type equals to the component
     * type of this array.
     */
    protected final Object newArray(int length) {
	return Array.newInstance(getComponentType(), length);
    }

    /**
     * Returns true if the specified component type is not a primitive type.
     */
    protected boolean isObjectType(Class componentType) {
	return !(componentType == boolean.class ||
		 componentType == byte.class    ||
		 componentType == char.class    ||
		 componentType == short.class   ||
		 componentType == int.class     ||
		 componentType == long.class    ||
		 componentType == float.class   ||
		 componentType == double.class);
    }
}


final
class VArrayEnumerator implements Enumeration {
    VArray varray;
    int index;

    VArrayEnumerator(VArray varray) {
	this.varray = varray;
	this.index  = 0;
    }

    public boolean hasMoreElements() {
	return index < varray.count;
    }

    public Object nextElement() {
	//synchronized (varray) {
	    if (index < varray.count) {
		return Array.get(varray.array, index++);
	    }
	//}
	throw new java.util.NoSuchElementException("VArrayEnumerator");
    }
}
