/*
 * Sorter.java
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

/**
 * The <code>Sorter</code> class implements the quick sort operation.
 *
 * @see 	jp.kyasu.util.Comparer
 * @see 	jp.kyasu.util.CompareAdapter
 *
 * @version 	24 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class Sorter {
    /**
     * The comparer that provides the comparison operations for the sorting.
     */
    protected Comparer comparer;


    /**
     * Constructs a sorter with a default comparer.
     */
    public Sorter() {
	this(new DefaultCompareAdapter());
    }

    /**
     * Constructs a sorter with the specified comparer.
     */
    public Sorter(Comparer comparer) {
	if (comparer == null)
	    throw new NullPointerException();
	this.comparer = comparer;
    }


    /**
     * Returns the comparer of this sorter.
     *
     * @return the comparer of this sorter.
     */
    public final Comparer getComparer() {
	return comparer;
    }

    /**
     * Sets the comparer of this sorter to be the specified comparer.
     *
     * @param comparer a comparer.
     */
    public final void setComparer(Comparer comparer) {
	if (comparer == null)
	    throw new NullPointerException();
	this.comparer = comparer;
    }

    /**
     * Sorts the specified array.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     * @see jp.kyasu.util.VArray#sort()
     * @see jp.kyasu.util.VArray#sort(jp.kyasu.util.Comparer)
     * @see jp.kyasu.util.VArray#sort(int, int, jp.kyasu.util.Comparer)
     */
    public final void quicksort(Object array, int i, int j) {
	Class componentType = array.getClass().getComponentType();
	if (componentType.equals(boolean.class)) {
	    quicksort((boolean[])array, i, j);
	}
	else if (componentType.equals(byte.class)) {
	    quicksort((byte[])array, i, j);
	}
	else if (componentType.equals(char.class)) {
	    quicksort((char[])array, i, j);
	}
	else if (componentType.equals(short.class)) {
	    quicksort((short[])array, i, j);
	}
	else if (componentType.equals(int.class)) {
	    quicksort((int[])array, i, j);
	}
	else if (componentType.equals(long.class)) {
	    quicksort((long[])array, i, j);
	}
	else if (componentType.equals(float.class)) {
	    quicksort((float[])array, i, j);
	}
	else if (componentType.equals(double.class)) {
	    quicksort((double[])array, i, j);
	}
	else {
	    quicksort((Object[])array, i, j);
	}
    }

    /**
     * Sorts the specified array of objects.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(Object array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /**
     * Sorts the specified array of boolean values.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(boolean array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /**
     * Sorts the specified array of byte values.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(byte array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /**
     * Sorts the specified array of char values.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(char array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /**
     * Sorts the specified array of short values.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(short array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /**
     * Sorts the specified array of int values.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(int array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /**
     * Sorts the specified array of long values.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(long array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /**
     * Sorts the specified array of float values.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(float array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /**
     * Sorts the specified array of double values.
     *
     * @param array the array to be sorted.
     * @param i     the beginning index to sort, inclusive.
     * @param j     the ending index to sort, inclusive.
     */
    public final void quicksort(double array[], int i, int j) {
	if (i >= j)
	    return;
	swap(array, i, (i + j) / 2);
	int p = i;
	for (int k = i + 1; k <= j; k++) {
	    if (comparer.compare(array[k], array[i]) < 0) {
		swap(array, ++p, k);
	    }
	}
	swap(array, i, p);
	quicksort(array, i, p - 1);
	quicksort(array, p + 1, j);
    }

    /** Swaps the components in the specified array. */
    protected final void swap(Object array[], int i, int j) {
	Object object = array[i];
	array[i] = array[j];
	array[j] = object;
    }

    /** Swaps the components in the specified array of boolean values. */
    protected final void swap(boolean array[], int i, int j) {
	boolean b = array[i];
	array[i] = array[j];
	array[j] = b;
    }

    /** Swaps the components in the specified array of byte values. */
    protected final void swap(byte array[], int i, int j) {
	byte b = array[i];
	array[i] = array[j];
	array[j] = b;
    }

    /** Swaps the components in the specified array of char values. */
    protected final void swap(char array[], int i, int j) {
	char c = array[i];
	array[i] = array[j];
	array[j] = c;
    }

    /** Swaps the components in the specified array of short values. */
    protected final void swap(short array[], int i, int j) {
	short s = array[i];
	array[i] = array[j];
	array[j] = s;
    }

    /** Swaps the components in the specified array of int values. */
    protected final void swap(int array[], int i, int j) {
	int iv = array[i];
	array[i] = array[j];
	array[j] = iv;
    }

    /** Swaps the components in the specified array of long values. */
    protected final void swap(long array[], int i, int j) {
	long l = array[i];
	array[i] = array[j];
	array[j] = l;
    }

    /** Swaps the components in the specified array of float values. */
    protected final void swap(float array[], int i, int j) {
	float f = array[i];
	array[i] = array[j];
	array[j] = f;
    }

    /** Swaps the components in the specified array of double values. */
    protected final void swap(double array[], int i, int j) {
	double d = array[i];
	array[i] = array[j];
	array[j] = d;
    }
}


/**
 * The default comparer.
 */
class DefaultCompareAdapter extends CompareAdapter {
}
