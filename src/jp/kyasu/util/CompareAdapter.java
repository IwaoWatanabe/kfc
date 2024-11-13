/*
 * CompareAdapter.java
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
 * The adapter for the comparison operations.
 * The methods in this class are empty; this class is provided as a
 * convenience for easily creating comparers by extending this class
 * and overriding only the methods of interest.
 *
 * @see 	jp.kyasu.util.Comparer
 * @see 	jp.kyasu.util.Sorter
 *
 * @version 	15 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public abstract class CompareAdapter implements Comparer {
    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(Object x, Object y) {
	return compare(x.hashCode(), y.hashCode());
    }

    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(boolean x, boolean y) {
	return (x == y ? 0 : (x ? -1 : 1));
    }

    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(byte x, byte y) {
	return (x == y ? 0 : (x < y ? -1 : 1));
    }

    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(char x, char y) {
	return (x == y ? 0 : (x < y ? -1 : 1));
    }

    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(short x, short y) {
	return (x == y ? 0 : (x < y ? -1 : 1));
    }

    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(int x, int y) {
	return (x == y ? 0 : (x < y ? -1 : 1));
    }

    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(long x, long y) {
	return (x == y ? 0 : (x < y ? -1 : 1));
    }

    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(float x, float y) {
	return (x == y ? 0 : (x < y ? -1 : 1));
    }

    /** Returns &lt;0 if x &lt; y, etc. */
    public int compare(double x, double y) {
	return (x == y ? 0 : (x < y ? -1 : 1));
    }
}
