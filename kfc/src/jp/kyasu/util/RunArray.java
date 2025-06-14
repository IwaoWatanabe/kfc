/*
 * RunArray.java
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

/**
 * The <code>RunArray</code> class implements a space-efficient growable
 * array that tends to be constant over long runs of the possible indices.
 *
 * @version 	17 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class RunArray implements Cloneable, java.io.Serializable {
    /**
     * The buffer of the run length. Each indicates the number
     * of indices over which the corresponding value (in
     * <code>values</code>) is constant.
     */
    protected VArray runs;

    /**
     * The value buffer into which the components of the array
     * are stored.
     */
    protected VArray values;

    /**
     * The number of valid components in the array (Sum of run lengths).
     */
    protected int count;

    /**
     * The cache index of <code>runs</code>.
     */
    protected int cacheRunIndex;

    /**
     * The cache index of the <code>RunArray</code>
     * (Sum of run lengths up to <code>cacheRunIndex</code>).
     */
    protected int cacheRunStart;


    /**
     * Constructs an empty array.
     */
    public RunArray() {
	this(Object.class);
    }

    /**
     * Constructs an empty array with the specified component type.
     *
     * @param     componentType the component type of the array.
     * @exception IllegalArgumentException if the specified component type
     *            is a primitive type.
     */
    public RunArray(Class componentType) {
	values = new VArray(componentType);
	if (!values.componentIsObject) {
	    throw new IllegalArgumentException();
	}
	runs = new VArray(int.class);
	count = 0;
	cacheRunIndex = 0;
	cacheRunStart = 0;
    }

    /**
     * Constructs an array with the specified size (length), whose
     * every component equals to the specified value.
     *
     * @param size  the size (length) of the array.
     * @param value the components of the array.
     */
    public RunArray(int size, Object value) {
	this(size, value, Object.class);
    }

    /**
     * Constructs an array with the specified size (length), whose
     * every component equals to the specified value.
     *
     * @param size          the size (length) of the array.
     * @param value         the components of the array.
     * @param componentType the component type of the array.
     * @exception IllegalArgumentException if the specified component type
     *            is a primitive type.
     */
    public RunArray(int size, Object value, Class componentType) {
	this(componentType);
	if (value == null)
	    throw new NullPointerException();
	runs.append(size);
	values.append(value);
	count = size;
    }

    /**
     * Constructs an array with the specified runs, values, and count.
     *
     * @param runs   the run length array of the array.
     * @param values the value array of the array.
     * @param count  the count of the array.
     */
    protected RunArray(VArray runs, VArray values, int count) {
	if (runs == null || values == null)
	    throw new NullPointerException();
	if (!values.componentIsObject) {
	    throw new IllegalArgumentException();
	}
	this.runs   = runs;
	this.values = values;
	this.count  = count;
	cacheRunIndex = 0;
	cacheRunStart = 0;
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
     * Returns the length of this array, i.e., the number of components
     * in this array.
     *
     * @return the length of this array.
     */
    public final int length() {
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
     * Returns an enumeration of the components of this array.
     *
     * @return  an enumeration of the components of this array.
     */
    public final Enumeration elements() {
	return new RunArrayEnumerator(this);
    }

    /**
     * Returns an enumeration of the components of this array.
     *
     * @param begin the beginning index to get components, inclusive.
     * @param end   the ending index to get components, exclusive.
     * @return an enumeration of the components of this array.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>, or
     *            the <code>end</code> is out of range.
     */
    public final Enumeration elements(int begin, int end) {
	if ((begin < 0) || (end > count) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	return new RunArrayEnumerator(this, begin, end);
    }

    /**
     * Returns the component type of this array.
     *
     * @return the component type of this array.
     */
    public final Class getComponentType() {
	return values.getComponentType();
    }

    /**
     * Returns the number of the components in this array.
     *
     * @return the number of the components in this array.
     */
    public final int getValueCount() {
	return values.count;
    }

    /**
     * Returns the components in this array.
     *
     * @return the components in this array.
     */
    public final Object[] getValues() {
	return (Object[])values.getTrimmedArray();
    }

    /**
     * Returns the components in this array.
     *
     * @param  begin  the beginning index to get components, inclusive.
     * @param  end    the ending index to get components, exclusive.
     * @return the components in this array.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>, or
     *            the <code>end</code> is out of range.
     */
    public final Object[] getValues(int begin, int end) {
	if ((begin < 0) || (end > count) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	if (begin == end) {
	    return (Object[])values.subarray(0, 0).getTrimmedArray();
	}
	int runAndOffset[] = getRunAndOffset(begin);
	int bRun = runAndOffset[0];
	runAndOffset = getRunAndOffset(end - 1);
	int eRun = runAndOffset[0];
	return (Object[])values.subarray(bRun, eRun + 1).getTrimmedArray();
    }

    /**
     * Returns the component object at the specified index.
     *
     * @param     index an index into this array.
     * @return    the component at the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     */
    public final Object get(int index) {
	if (index < 0 || index >= count) {
	    throw new ArrayIndexOutOfBoundsException(index);
	}
	if (values.count == 1) {
	    return values.get(0);
	}
	int runAndOffset[] = getRunAndOffset(index);
	int run = runAndOffset[0];
	return values.get(run);
    }

    /**
     * Returns the run length (the number of the constant occurrence)
     * at the specified index.
     *
     * @param  index an index into this array.
     * @return the run length from the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     */
    public final int getRunLengthAt(int index) {
	if (index < 0 || index >= count) {
	    throw new ArrayIndexOutOfBoundsException(index);
	}
	if (runs.count == 1) {
	    return runs.getInt(0) - index;
	}
	int runAndOffset[] = getRunAndOffset(index);
	int run = runAndOffset[0];
	int offset = runAndOffset[1];
	return runs.getInt(run) - offset;
    }

    /**
     * Returns the run offset (the starting index of the constant
     * occurrence) at the specified index.
     *
     * @param  index an index into this array.
     * @return the run offset from the specified index.
     * @exception ArrayIndexOutOfBoundsException if an invalid index was
     *            given.
     */
    public final int getRunOffsetAt(int index) {
	if (index < 0 || index >= count) {
	    throw new ArrayIndexOutOfBoundsException(index);
	}
	if (runs.count == 1) {
	    return index;
	}
	int runAndOffset[] = getRunAndOffset(index);
	return runAndOffset[1];
    }

    /**
     * Returns a hashcode for this array.
     *
     * @return  a hash code value for this array.
     */
    public int hashCode() {
	return count ^ runs.hashCode() ^ values.hashCode();
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
	if (anObject instanceof RunArray) {
	    RunArray runArray = (RunArray)anObject;
	    if (count == runArray.count &&
		runs.equals(runArray.runs) &&
		values.equals(runArray.values))
	    {
		return true;
	    }
	}
	return false;
    }

    /**
     * Removes all components from this array and sets its length to zero.
     */
    public final void removeAll() {
	runs.removeAll();
	values.removeAll();
	count = 0;
	cacheRunIndex = 0;
	cacheRunStart = 0;
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
	if (size <= 0)
	    return;
	if (count == 0)
	    return;

	replace(offset, offset + size, new RunArray());
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
    public final RunArray subarray(int beginIndex) {
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
     * @exception ArrayIndexOutOfBoundsException if the
     *            <code>beginIndex</code> or the <code>endIndex</code> is
     *            out of range.
     */
    public final RunArray subarray(int beginIndex, int endIndex) {
	if ((beginIndex < 0) || (endIndex > count) || (beginIndex > endIndex))
	{
	    throw new ArrayIndexOutOfBoundsException();
	}

	if (beginIndex == endIndex) {
	    return new RunArray();
	}

	int runAndOffset[] = getRunAndOffset(beginIndex);
	int bRun    = runAndOffset[0];
	int bOffset = runAndOffset[1];
	runAndOffset = getRunAndOffset(endIndex - 1);
	int eRun    = runAndOffset[0];
	int eOffset = runAndOffset[1];
	VArray subRuns   = runs.subarray(bRun, eRun + 1);
	VArray subValues = values.subarray(bRun, eRun + 1);
	if (bRun == eRun) {
	    subRuns.setInt(0, eOffset - bOffset + 1);
	}
	else {
	    subRuns.setInt(0, runs.getInt(bRun) - bOffset);
	    subRuns.setInt(eRun - bRun, eOffset + 1);
	}

	return new RunArray(subRuns, subValues, endIndex - beginIndex);
    }

    /**
     * Appends the components of the <code>RunArray</code> object to
     * this array.
     *
     * @param  array an <code>RunArray</code> object.
     * @return this array.
     */
    public final RunArray append(RunArray array) {
	if (array.count == 0) {
	    return this;
	}
	if (count == 0) {
	    copyFrom(array);
	    return this;
	}

	int last = runs.count - 1;
	if (values.get(last).equals(array.values.get(0))) {
	    runs.setInt(last, runs.getInt(last) + array.runs.getInt(0));
	    if (array.runs.count > 1) {
		runs.append(array.runs, 1, array.runs.count);
		values.append(array.values, 1, array.values.count);
	    }
	}
	else {
	    runs.append(array.runs);
	    values.append(array.values);
	}

	count += array.count;
	return this;
    }

    /**
     * Appends the object to this array.
     *
     * @param  obj an object.
     * @return this array.
     */
    public final RunArray append(Object obj) {
	int last = runs.count - 1;
	if (last >= 0 && values.get(last).equals(obj)) {
	    runs.setInt(last, runs.getInt(last) + 1);
	}
	else {
	    runs.append(1);
	    values.append(obj);
	}
	count++;
	return this;
    }

    /**
     * Grows the length of this array to the current array's length
     * plus the specified <code>length</code>. If this array isn't empty,
     * the last value is added to the end of the array. Otherwise,
     * <code>defObj</code> is added to the end of the array.
     *
     * @param  length object.
     * @param  defObj an object.
     * @return this array.
     */
    public final RunArray append(int length, Object defObj) {
	int last = runs.count - 1;
	if (last >= 0) {
	    runs.setInt(last, runs.getInt(last) + length);
	}
	else {
	    runs.append(length);
	    values.append(defObj);
	}
	count += length;
	return this;
    }

    /**
     * Inserts the components of the <code>RunArray</code> object to
     * this array from the specified <code>offset</code>.
     *
     * @param     offset the start index of the components to be inserted.
     * @param     array  a <code>RunArray</code> object.
     * @return    this array.
     * @exception ArrayIndexOutOfBoundsException if the <code>offset</code>
     *            was invalid.
     */
    public final RunArray insert(int offset, RunArray array) {
	if ((offset < 0) || (offset > count)) {
	    throw new ArrayIndexOutOfBoundsException();
	}

	return replace(offset, offset, array);
    }

    /**
     * Replaces the components of this array with the components of the
     * <code>RunArray</code> object.
     *
     * @param  begin the beginning index to replace, inclusive.
     * @param  end   the ending index to replace, exclusive.
     * @param  array a replacement <code>RunArray</code> object.
     * @return this array.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>
     *            or the <code>end</code> is out of range.
     */
    public final RunArray replace(int begin, int end, RunArray array) {
	if ((begin < 0) || (end > count) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}

	if (begin == end && array.count == 0) {
	    return this;
	}

	if ((begin == 0 && end == count) || count == 0) { // replace all
	    copyFrom(array);
	    return this;
	}

	int runAndOffset[];
	int bRun    = -1;
	int bOffset = 0;
	int eRun    = -1;
	int eOffset = 0;
	int saveCacheRunIndex = 0;
	int saveCacheRunStart = 0;
	if (begin > 0) {
	    runAndOffset = getRunAndOffset(begin - 1);
	    saveCacheRunIndex = cacheRunIndex;
	    saveCacheRunStart = cacheRunStart;
	    bRun    = runAndOffset[0];
	    bOffset = runAndOffset[1];
	}
	if (end < count) {
	    runAndOffset = getRunAndOffset(end);
	    eRun    = runAndOffset[0];
	    eOffset = runAndOffset[1];
	}

	replace(bRun, bOffset, eRun, eOffset, array.runs, array.values);

	count += array.count - (end - begin);
	cacheRunIndex = saveCacheRunIndex;
	cacheRunStart = saveCacheRunStart;

	return this;
    }

    /**
     * Returns a string representation of this array.
     *
     * @return a string representation of this array.
     */
    public String toString() {
	int max = count - 1;
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	for (int i = 0 ; i <= max ; i++) {
	    buf.append(get(i));
	    if (i < max) buf.append(", ");
	}
	buf.append("]");
	return buf.toString();
    }

    /**
     * Returns a clone of this array.
     *
     * @return a clone of this array.
     */
    public Object clone() {
	try {
	    RunArray ra = (RunArray)super.clone();
	    ra.runs   = (VArray)runs.clone();
	    ra.values = (VArray)values.clone();
	    ra.count  = count;
	    ra.cacheRunIndex = cacheRunIndex;
	    ra.cacheRunStart = cacheRunStart;
	    return ra;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /** Returns the run length and the run offset at the specified index. */
    protected final int[] getRunAndOffset(int index) {
	int runIndex, limit, offset, runStart, runLength;

	if (index >= cacheRunStart) {
	    runIndex = cacheRunIndex;
	    runStart = cacheRunStart;
	}
	else {
	    runIndex = 0;
	    runStart = 0;
	}
	limit = runs.count - 1;
	offset = index - runStart;
	int iruns[] = (int[])runs.array;
	while (runIndex < limit &&
	       (runLength = iruns[runIndex]) <= offset)
	{
	    offset -= runLength;
	    runIndex++;
	    runStart += runLength;
	}
	cacheRunIndex = runIndex;
	cacheRunStart = runStart;
	return new int[]{ runIndex, offset };
    }

    /**
     * Replaces the components of this array with the runs and values.
     *
     * @param bRun    the beginning index of runs, inclusive.
     * @param bOffset the beginning offset of runs.
     * @param eRun    the ending index of runs, inclusive.
     * @param eOffset the ending offset of runs.
     * @param rRuns   a run length array.
     * @param rValues a value array.
     */
    protected final void replace(int bRun, int bOffset,
				 int eRun, int eOffset,
				 VArray rRuns, VArray rValues)
    {
	if ((bRun < 0 && eRun < 0) || runs.count == 0) { // replace all
	    runs.replace(0, runs.count, rRuns);
	    values.replace(0, values.count, rValues);
	    return;
	}

	int rcount = runs.count;
	int rrCount = rRuns.count;

	if (bRun < 0) { // replace from head
	    // eRun >= 0
	    int eCount = runs.getInt(eRun) - eOffset;
	    if (rrCount == 0) { // remove head
		runs.setInt(eRun, eCount);
		if (eRun > 0) {
		    runs.remove(0, eRun);
		    values.remove(0, eRun);
		}
	    }
	    else if (rValues.get(rrCount - 1).equals(values.get(eRun))) {
		// merge
		runs.setInt(eRun, eCount + rRuns.getInt(rrCount - 1));
		if (rrCount == 1) {
		    if (eRun > 0) {
			runs.remove(0, eRun);
			values.remove(0, eRun);
		    }
		}
		else { // rrCount > 1
		    runs.replace(0, eRun, rRuns, 0, rrCount - 1);
		    values.replace(0, eRun, rValues, 0, rrCount - 1);
		}
	    }
	    else {
		runs.setInt(eRun, eCount);
		runs.replace(0, eRun, rRuns, 0, rrCount);
		values.replace(0, eRun, rValues, 0, rrCount);
	    }
	    return;
	}
	else if (eRun < 0) { // replace to tail
	    // bRun >= 0
	    if (rrCount == 0) { // remove tail
		runs.setInt(bRun, bOffset + 1);
		if (rcount > bRun + 1) {
		    runs.remove(bRun + 1, rcount - (bRun + 1));
		    values.remove(bRun + 1, rcount - (bRun + 1));
		}
	    }
	    else if (values.get(bRun).equals(rValues.get(0))) {
		// merge
		runs.setInt(bRun, bOffset + 1 + rRuns.getInt(0));
		if (rrCount == 1) {
		    if (rcount > bRun + 1) {
			runs.remove(bRun + 1, rcount - (bRun + 1));
			values.remove(bRun + 1, rcount - (bRun + 1));
		    }
		}
		else { // rrCount > 1
		    runs.replace(bRun + 1, rcount, rRuns, 1, rrCount);
		    values.replace(bRun + 1, rcount, rValues, 1, rrCount);
		}
	    }
	    else {
		runs.setInt(bRun, bOffset + 1);
		runs.replace(bRun + 1, rcount, rRuns, 0, rrCount);
		values.replace(bRun + 1, rcount, rValues, 0, rrCount);
	    }
	    return;
	}

	// Now, bRun >= 0 && eRun >= 0 && runs.count > 0

	int bCount = bOffset + 1;
	int eCount = runs.getInt(eRun) - eOffset;

	if (rrCount == 0) { // remove
	    if (bRun == eRun) {
		runs.setInt(bRun, bCount + eCount);
	    }
	    else {
		if (bRun+1 < eRun && values.get(bRun).equals(values.get(eRun)))
		{
		    // merge
		    runs.setInt(bRun, bCount + eCount);
		    runs.remove(bRun + 1, eRun - bRun);
		    values.remove(bRun + 1, eRun - bRun);
		}
		else {
		    runs.setInt(bRun, bCount);
		    runs.setInt(eRun, eCount);
		    runs.remove(bRun + 1, eRun - (bRun + 1));
		    values.remove(bRun + 1, eRun - (bRun + 1));
		}
	    }
	    return;
	}

	// Now, bRun >= 0 && eRun >= 0 && runs.count > 0 && rRuns.count > 0

	if (bRun == eRun) {
	    Object val = values.get(bRun);
	    boolean headEqual = val.equals(rValues.get(0));
	    boolean tailEqual =
		(rrCount == 1 ? headEqual : val.equals(rValues.get(rrCount-1)));
	    if (headEqual) {
		if (tailEqual) {
		    if (rrCount == 1) {
			runs.setInt(bRun, bCount + eCount + rRuns.getInt(0));
		    }
		    else { // rrCount >= 3
			runs.insertSpace(bRun + 1, rrCount - 1);
			values.insertSpace(bRun + 1, rrCount - 1);
			runs.setInt(bRun, bCount + rRuns.getInt(0));
			runs.setInt(bRun + rrCount - 1,
				    eCount + rRuns.getInt(rrCount - 1));
			values.set(bRun + rrCount - 1, val);
			runs.replace(bRun + 1, bRun + rrCount - 1,
				     rRuns, 1, rrCount - 1);
			values.replace(bRun + 1, bRun + rrCount - 1,
				       rValues, 1, rrCount - 1);
		    }
		}
		else { // rrCount >= 2
		    runs.insertSpace(bRun + 1, rrCount);
		    values.insertSpace(bRun + 1, rrCount);
		    runs.setInt(bRun, bCount + rRuns.getInt(0));
		    runs.setInt(bRun + rrCount, eCount);
		    values.set(bRun + rrCount, val);
		    runs.replace(bRun + 1, bRun + rrCount,
				 rRuns, 1, rrCount);
		    values.replace(bRun + 1, bRun + rrCount,
				   rValues, 1, rrCount);
		}
	    }
	    else {
		if (tailEqual) { // rrCount >= 2
		    runs.insertSpace(bRun + 1, rrCount);
		    values.insertSpace(bRun + 1, rrCount);
		    runs.setInt(bRun, bCount);
		    runs.setInt(bRun + rrCount,
				eCount + rRuns.getInt(rrCount - 1));
		    values.set(bRun + rrCount, val);
		    runs.replace(bRun + 1, bRun + rrCount,
				 rRuns, 0, rrCount - 1);
		    values.replace(bRun + 1, bRun + rrCount,
				   rValues, 0, rrCount - 1);
		}
		else {
		    runs.insertSpace(bRun + 1, rrCount + 1);
		    values.insertSpace(bRun + 1, rrCount + 1);
		    runs.setInt(bRun, bCount);
		    runs.setInt(bRun + rrCount + 1, eCount);
		    values.set(bRun + rrCount + 1, val);
		    runs.replace(bRun + 1, bRun + rrCount + 1,
				 rRuns, 0, rrCount);
		    values.replace(bRun + 1, bRun + rrCount + 1,
				   rValues, 0, rrCount);
		}
	    }
	}
	else {
	    boolean headEqual = values.get(bRun).equals(rValues.get(0));
	    boolean tailEqual = values.get(eRun).equals(rValues.get(rrCount-1));
	    if (headEqual) {
		if (tailEqual) {
		    if (rrCount == 1) {
			runs.setInt(bRun, bCount + eCount + rRuns.getInt(0));
			runs.remove(bRun + 1, eRun - bRun);
			values.remove(bRun + 1, eRun - bRun);
		    }
		    else { // rrCount >= 2
			runs.setInt(bRun, bCount + rRuns.getInt(0));
			runs.setInt(eRun, eCount + rRuns.getInt(rrCount - 1));
			if (rrCount > 2) {
			    runs.replace(bRun + 1, eRun, rRuns, 1, rrCount - 1);
			    values.replace(bRun+1, eRun, rValues, 1, rrCount-1);
			}
		    }
		}
		else { // rrCount >= 2
		    runs.setInt(bRun, bCount + rRuns.getInt(0));
		    runs.setInt(eRun, eCount);
		    runs.replace(bRun + 1, eRun, rRuns, 1, rrCount);
		    values.replace(bRun + 1, eRun, rValues, 1, rrCount);
		}
	    }
	    else {
		if (tailEqual) { // rrCount >= 2
		    runs.setInt(bRun, bCount);
		    runs.setInt(eRun, eCount + rRuns.getInt(rrCount - 1));
		    runs.replace(bRun + 1, eRun, rRuns, 0, rrCount - 1);
		    values.replace(bRun + 1, eRun, rValues, 0, rrCount - 1);
		}
		else {
		    runs.setInt(bRun, bCount);
		    runs.setInt(eRun, eCount);
		    runs.replace(bRun + 1, eRun, rRuns, 0, rrCount);
		    values.replace(bRun + 1, eRun, rValues, 0, rrCount);
		}
	    }
	}
    }

    /** Copies into the contents of this array from the specified array. */
    protected final void copyFrom(RunArray array) {
	runs.replace(0, runs.count, array.runs);
	values.replace(0, values.count, array.values);
	count = array.count;
	cacheRunIndex = array.cacheRunIndex;
	cacheRunStart = array.cacheRunStart;
    }


    /*
    public static void main(String argv[]) {
	RunArray array = new RunArray();
	array.append(new RunArray(5, java.awt.Color.black));
	array.append(new RunArray(5, java.awt.Color.black));
	array.append(new RunArray(5, java.awt.Color.white));
	array.append(new RunArray(5, java.awt.Color.white));
	array.append(new RunArray(10, java.awt.Color.red));
	array.append(new RunArray(10, java.awt.Color.green));
	array.append(new RunArray(10, java.awt.Color.blue));
	System.out.println(array.runs.toString());
	System.out.println(array.values.toString());

	if (!array.equals(array.clone())) {
	    System.out.println("equals() ERROR!");
	}

	int max = array.length();
	for (int i = 0 ; i < max ; i++) {
	    System.out.println("" + array.getRunLengthAt(i) + ": "
					+ array.get(i));
	}

	RunArray a1 = (RunArray)array.clone();
	RunArray a2 = (RunArray)array.clone();
	a1.replace(6, 46, new RunArray());
	a2.remove(6, 40);
	if (!a1.equals(a2)) {
	    System.out.println("remove() ERROR1!");
	}
	a1 = (RunArray)array.clone();
	a2 = (RunArray)array.clone();
	a1.replace(0, 46, new RunArray());
	a2.remove(0, 46);
	if (!a1.equals(a2)) {
	    System.out.println("remove() ERROR2!");
	}
	a1 = (RunArray)array.clone();
	a2 = (RunArray)array.clone();
	a1.replace(6, 50, new RunArray());
	a2.remove(6, 44);
	if (!a1.equals(a2)) {
	    System.out.println("remove() ERROR3!");
	}

	a1 = (RunArray)array.clone();
	a2 = (RunArray)array.clone();
	a1.replace(2, 2, new RunArray(10, java.awt.Color.black));
	a2.insert(2, new RunArray(10, java.awt.Color.black));
	if (!a1.equals(a2)) {
	    System.out.println("insert() ERROR1!");
	}
	a1 = (RunArray)array.clone();
	a2 = (RunArray)array.clone();
	a1.replace(2, 2, new RunArray(10, java.awt.Color.red));
	a2.insert(2, new RunArray(10, java.awt.Color.red));
	if (!a1.equals(a2)) {
	    System.out.println("insert() ERROR2!");
	}
	a1 = (RunArray)array.clone();
	a2 = (RunArray)array.clone();
	a1.replace(0, 0, new RunArray(10, java.awt.Color.black));
	a2.insert(0, new RunArray(10, java.awt.Color.black));
	if (!a1.equals(a2)) {
	    System.out.println("insert() ERROR3!");
	}
	a1 = (RunArray)array.clone();
	a2 = (RunArray)array.clone();
	a1.replace(a1.length(), a1.length(), new RunArray(10, java.awt.Color.black));
	a2.insert(a2.length(), new RunArray(10, java.awt.Color.black));
	if (!a1.equals(a2)) {
	    System.out.println("insert() ERROR4!");
	}

	RunArray sub;
	System.out.println("========");
	sub = array.subarray(0, 0);
	System.out.println(sub.runs.toString());
	System.out.println(sub.values.toString());
	System.out.println("========");
	sub = array.subarray(0, 10);
	System.out.println(sub.runs.toString());
	System.out.println(sub.values.toString());
	System.out.println("========");
	sub = array.subarray(0, 9);
	System.out.println(sub.runs.toString());
	System.out.println(sub.values.toString());
	System.out.println("========");
	sub = array.subarray(1, 9);
	System.out.println(sub.runs.toString());
	System.out.println(sub.values.toString());
	System.out.println("========");
	sub = array.subarray(40, 50);
	System.out.println(sub.runs.toString());
	System.out.println(sub.values.toString());
	System.out.println("========");
	sub = array.subarray(0, 50);
	System.out.println(sub.runs.toString());
	System.out.println(sub.values.toString());

	RunArray rep;
	System.out.println("========");
	rep = ((RunArray)(array.clone())).replace(
			0, 8, new RunArray(10, java.awt.Color.black));
	System.out.println(rep.length());
	System.out.println(rep.runs.toString());
	System.out.println(rep.values.toString());
	System.out.println("========");
	rep = ((RunArray)(array.clone())).replace(
			0, 8, new RunArray(10, java.awt.Color.red));
	System.out.println(rep.length());
	System.out.println(rep.runs.toString());
	System.out.println(rep.values.toString());
	System.out.println("========");
	rep = ((RunArray)(array.clone())).replace(
			5, 45, new RunArray(10, java.awt.Color.blue));
	System.out.println(rep.length());
	System.out.println(rep.runs.toString());
	System.out.println(rep.values.toString());
	System.out.println("========");
	rep = (new RunArray(500, java.awt.Color.black)).replace(
			100, 400, new RunArray(200, java.awt.Color.black));
	System.out.println(rep.length());
	System.out.println(rep.runs.toString());
	System.out.println(rep.values.toString());
	System.out.println("========");
	rep = (new RunArray(10, java.awt.Color.red)).append(
			new RunArray(500, java.awt.Color.black));
	rep = rep.replace(10, 10, new RunArray(1, java.awt.Color.black));
	System.out.println(rep.length());
	System.out.println(rep.runs.toString());
	System.out.println(rep.values.toString());
	System.out.println("========");
	rep = (new RunArray(10, java.awt.Color.red)).append(
			new RunArray(500, java.awt.Color.black));
	rep = rep.replace(510, 510, new RunArray(1, java.awt.Color.black));
	System.out.println(rep.length());
	System.out.println(rep.runs.toString());
	System.out.println(rep.values.toString());
    }
    */
}


final
class RunArrayEnumerator implements Enumeration {
    RunArray run;
    int runIndex;
    int runLimit;

    RunArrayEnumerator(RunArray run) {
	this(run, 0, run.count);
    }

    RunArrayEnumerator(RunArray run, int begin, int end) {
	this.run = run;
	if (begin < end) {
	    runIndex = run.getRunAndOffset(begin)[0];
	    runLimit = run.getRunAndOffset(end - 1)[0];
	}
	else {
	    runIndex = 0;
	    runLimit = -1;
	}
    }

    public boolean hasMoreElements() {
	return runIndex <= runLimit;
    }

    public Object nextElement() {
	//synchronized (run) {
	    if (runIndex <= runLimit) {
		return java.lang.reflect.Array.get(run.values.array,
						   runIndex++);
	    }
	//}
	throw new java.util.NoSuchElementException("RunArrayEnumerator");
    }
}
