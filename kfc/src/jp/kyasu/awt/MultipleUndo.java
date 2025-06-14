/*
 * MultipleUndo.java
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

import java.util.Enumeration;
import java.util.Vector;

/**
 * A <code>MultipleUndo</code> object can perform multiple undo operations.
 *
 * @version 	14 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class MultipleUndo implements Undo {
    transient protected Vector undos;


    /**
     * Constructs an empty multiple undo object.
     */
    public MultipleUndo() {
    }


    /**
     * Adds an undo object.
     */
    public void addUndo(Undo undo) {
	if (undos == null)
	    undos = new Vector();
	undos.insertElementAt(undo, 0);
    }

    /**
     * Performs an undo operation.
     *
     * @return the undo operation for this undo object (redo operation),
     *         or <code>null</code> if the redo is not supported.
     * @see jp.kyasu.awt.Undo
     */
    public Undo undo() {
	MultipleUndo mundo = new MultipleUndo();
	if (undos == null)
	    return mundo;
	for (Enumeration e = undos.elements(); e.hasMoreElements(); ) {
	    Undo undo = ((Undo)e.nextElement()).undo();
	    if (undo != null) {
		mundo.addUndo(undo);
	    }
	}
	return mundo;
    }
}
