/*
 * ListItemEvent.java
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

package jp.kyasu.awt.event;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;

/**
 * The List item event that is originated from a <code>List</code>.
 *
 * @see 	jp.kyasu.awt.List
 * @see 	jp.kyasu.awt.TableList
 *
 * @version 	27 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class ListItemEvent extends ItemEvent {
    /** The indices of items being selected/deselected. */
    protected int items[];

    /** The column index. */
    protected int column;


    /**
     * Constructs a ListItemEvent object with the specified ItemSelectable
     * source, id, item select state, row index, and column index.
     *
     * @param source      the ItemSelectable object where the event originated.
     * @param id          the event type.
     * @param stateChange the state change type which caused the event.
     * @param row         the row index.
     * @param column      the column index.
     */
    public ListItemEvent(ItemSelectable source, int id, int stateChange,
			 int row, int column)
    {
	this(source, id, new Integer(row), stateChange,
	     new int[]{ row }, column);
    }

    /**
     * Constructs a ListItemEvent object with the specified ItemSelectable
     * source, id, item, item select state, indices of items, and column index.
     *
     * @param source      the ItemSelectable object where the event originated.
     * @param id          the event type.
     * @param item        the item where the event occurred.
     * @param stateChange the state change type which caused the event.
     * @param items       the indices of items where the event occurred.
     * @param column      the column index.
     */
    public ListItemEvent(ItemSelectable source, int id, Object item,
			 int stateChange, int items[], int column)
    {
	super(source, id, item, stateChange);
	this.items  = items;
	this.column = column;
    }


    /**
     * Returns the indices of items of this event.
     */
    public int[] getItems() {
        return items;
    }

    /**
     * Returns the column index of this event.
     */
    public int getRow() {
	return ((Integer)getItem()).intValue();
    }

    /**
     * Returns the column index of this event.
     */
    public int getColumn() {
	return column;
    }

    public String paramString() {
	return super.paramString() + ",column="+column;
    }
}
