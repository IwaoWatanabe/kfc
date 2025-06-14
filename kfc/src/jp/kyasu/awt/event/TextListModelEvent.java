/*
 * TextListModelEvent.java
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
import jp.kyasu.graphics.text.TextLayoutChange;

/**
 * The TextListModel event that is originated from a <code>TextListModel</code>
 * to <code>ListModelListener</code>s.
 *
 * @see 	jp.kyasu.awt.TextListModel
 *
 * @version 	11 Mar 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextListModelEvent extends ListModelEvent {
    /**
     * The text layout change object.
     * This is valid when id is LIST_MODEL_REPLACED.
     */
    protected TextLayoutChange textLayoutChange;


    /**
     * Constructs a list model event with the specified list model (event
     * source), id, range to be replaced, item count changed, and text
     * layout change object.
     *
     * @param model            the list model (event source).
     * @param id               the id.
     * @param begin            the beginning index to be replaced, inclusive.
     * @param end              the ending index to be replaced, exclusive.
     * @param itemCountChanged the item count changed.
     * @param textLayoutChange the text layout change object.
     * @exception IllegalArgumentException if the id is not LIST_MODEL_REPLACED.
     */
    public TextListModelEvent(ListModel model, int id, int begin, int end,
			      int itemCountChanged,
			      TextLayoutChange textLayoutChange)
    {
	super(model, id, begin, end, itemCountChanged);
	if (textLayoutChange == null)
	    throw new NullPointerException();
	this.textLayoutChange = textLayoutChange;
    }


    /**
     * Returns the text layout change object.
     * This operation is valid when id is LIST_MODEL_REPLACED.
     */
    public TextLayoutChange getTextLayoutChange() {
	return textLayoutChange;
    }

    public String paramString() {
	return super.paramString() + ",textLayoutChange="+textLayoutChange;
    }
}
