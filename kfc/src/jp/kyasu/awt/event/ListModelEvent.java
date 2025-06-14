/*
 * ListModelEvent.java
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

import jp.kyasu.awt.ListModel;

/**
 * The ListModel event that is originated from a <code>ListModel</code> to
 * <code>ListModelListener</code>s.
 *
 * @see 	jp.kyasu.awt.ListModel
 * @see 	jp.kyasu.awt.event.ListModelListener
 *
 * @version 	11 Mar 1998
 * @author 	Kazuki YASUMATSU
 */
public class ListModelEvent extends java.util.EventObject {
    /**
     * The id of the event.
     */
    protected int id;

    /**
     * The selected indices.
     * This is valid when id is LIST_MODEL_SELECTION_CHANGED.
     */
    protected int selectedIndices[];

    /**
     * The deselected indices.
     * This is valid when id is LIST_MODEL_SELECTION_CHANGED.
     */
    protected int deselectedIndices[];

    /**
     * The beginning index to be replaced, inclusive.
     * This is valid when id is LIST_MODEL_REPLACED.
     */
    protected int replaceBegin;

    /**
     * The ending index to be replaced, exclusive.
     * This is valid when id is LIST_MODEL_REPLACED.
     */
    protected int replaceEnd;

    /**
     * The item count changed.
     * This is valid when id is LIST_MODEL_REPLACED.
     */
    protected int itemCountChanged;


    /**
     * Marks the first integer id for the range of list model event ids.
     */
    static public final int LIST_MODEL_FIRST =
					java.awt.AWTEvent.RESERVED_ID_MAX + 1;

    /**
     * Marks the last integer id for the range of list model event ids.
     */
    static public final int LIST_MODEL_LAST  = LIST_MODEL_FIRST + 1;

    /**
     * The list model is replaced.
     */
    static public final int LIST_MODEL_REPLACED          = LIST_MODEL_FIRST;

    /**
     * The selection of the list model is changed.
     */
    static public final int LIST_MODEL_SELECTION_CHANGED =
						LIST_MODEL_REPLACED + 1;


    /**
     * Constructs a list model event with the specified list model (event
     * source), id, the selected indices, and the deselected indices.
     *
     * @param model      the list model (event source).
     * @param id         the id.
     * @param selected   the selected indices.
     * @param deselected the deselected indices.
     * @exception IllegalArgumentException if the id is not
     *            LIST_MODEL_SELECTION_CHANGED.
     */
    public ListModelEvent(ListModel model, int id,
			  int selected[], int deselected[])
    {
	super(model);
	if (selected == null || deselected == null)
	    throw new NullPointerException();
	if (id != LIST_MODEL_SELECTION_CHANGED) {
	    throw new IllegalArgumentException("improper id: " + id);
	}
	this.id                = id;
	this.selectedIndices   = selected;
	this.deselectedIndices = deselected;
	this.replaceBegin      = 0;
	this.replaceEnd        = 0;
	this.itemCountChanged  = 0;
    }

    /**
     * Constructs a list model event with the specified list model (event
     * source), id, range to be replaced, and item count changed.
     *
     * @param model            the list model (event source).
     * @param id               the id.
     * @param replaceBegin     the beginning index to be replaced, inclusive.
     * @param replaceEnd       the ending index to be replaced, exclusive.
     * @param itemCountChanged the item count changed.
     * @exception IllegalArgumentException if the id is not
     *            LIST_MODEL_REPLACED.
     */
    public ListModelEvent(ListModel model, int id, int begin, int end,
			  int itemCountChanged)
    {
	super(model);
	if (id != LIST_MODEL_REPLACED) {
	    throw new IllegalArgumentException("improper id: " + id);
	}
	this.id                = id;
	this.selectedIndices   = null;
	this.deselectedIndices = null;
	this.replaceBegin      = replaceBegin;
	this.replaceEnd        = replaceEnd;
	this.itemCountChanged  = itemCountChanged;
    }


    /**
     * Returns the id of this list model event.
     */
    public int getID() {
	return id;
    }

    /**
     * Returns the list model (event source) of this list model event.
     */
    public ListModel getModel() {
	return (ListModel)source;
    }

    /**
     * Returns the selected indices.
     * This operation is valid when id is LIST_MODEL_SELECTION_CHANGED.
     */
    public int[] getSelectedIndices() {
	return selectedIndices;
    }

    /**
     * Returns the deselected indices.
     * This operation is valid when id is LIST_MODEL_SELECTION_CHANGED.
     */
    public int[] getDeselectedIndices() {
	return deselectedIndices;
    }

    /**
     * Returns the beginning index to be replaced, inclusive.
     * This operation is valid when id is LIST_MODEL_REPLACED.
     */
    public int getReplaceBegin() {
	return replaceBegin;
    }

    /**
     * Returns the beginning index to be replaced, exclusive.
     * This operation is valid when id is LIST_MODEL_REPLACED.
     */
    public int getReplaceEnd() {
	return replaceEnd;
    }

    /**
     * Returns the item count changed.
     * This operation is valid when id is LIST_MODEL_REPLACED.
     */
    public int getItemCountChanged() {
	return itemCountChanged;
    }

    public String paramString() {
	String typeStr;
	switch(id) {
	case LIST_MODEL_REPLACED:
	    typeStr = "LIST_MODEL_REPLACED";
	    break;
	case LIST_MODEL_SELECTION_CHANGED:
	    typeStr = "LIST_MODEL_SELECTION_CHANGED";
	    break;
	default:
	    typeStr = "unknown type";
	    break;
	}
	return typeStr + ",selectedIndices="    + selectedIndices
			+ ",deselectedIndices=" + deselectedIndices
			+ ",replaceBegin="      + replaceBegin
			+ ",replaceEnd="        + replaceEnd
			+ ",itemCountChanged="  + itemCountChanged;
    }
}
