/*
 * Comparer.java
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
 * The comparer interface that provides comparison operations.
 *
 * @see 	jp.kyasu.util.CompareAdapter
 * @see 	jp.kyasu.util.Sorter
 *
 * @version 	15 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public interface Comparer {
    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(Object x, Object y);

    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(boolean x, boolean y);

    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(byte x, byte y);

    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(char x, char y);

    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(short x, short y);

    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(int x, int y);

    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(long x, long y);

    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(float x, float y);

    /** Returns &lt;0 if x &lt; y, etc. */
    int compare(double x, double y);
}
