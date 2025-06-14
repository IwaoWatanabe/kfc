/*
 * ListenerSerializer.java
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

package jp.kyasu.awt;

import java.io.ObjectOutputStream;
import java.util.EventListener;

/**
 * The <code>ListenerSerializer</code> serializes the event listener.
 *
 * @version 	02 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class ListenerSerializer extends java.awt.AWTEventMulticaster {

    private ListenerSerializer() {
	super(null, null);
    }

    /*
     * Serializes the specified event listener with the specified key into
     * the specified object output stream.
     *
     * @param s the object output stream.
     * @param k the key string.
     * @param l the event listener to be serialized.
     */
    static public void write(ObjectOutputStream s, String k, EventListener l)
	throws java.io.IOException
    {
	save(s, k, l);
    }
}
