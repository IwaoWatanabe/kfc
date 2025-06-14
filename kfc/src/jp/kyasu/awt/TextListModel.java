/*
 * TextListModel.java
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

import jp.kyasu.graphics.TextList;
import jp.kyasu.graphics.TextStyle;

/**
 * The model interface for an object that acts as a model for
 * <code>TextListController</code> and <code>TextListView</code>.
 *
 * @see 	jp.kyasu.awt.List
 * @see 	jp.kyasu.awt.text.TextListController
 * @see 	jp.kyasu.awt.text.TextListView
 * @see 	jp.kyasu.awt.event.ListModelListener
 * @see 	jp.kyasu.awt.event.TextListModelEvent
 *
 * @version 	22 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public interface TextListModel extends ListModel {

    /**
     * Returns the text list of this text list model.
     *
     * @return the text list.
     */
    public TextList getTextList();

    /**
     * Sets the text style of this text list model.
     *
     * @param textStyle the text style.
     */
    public void setTextStyle(TextStyle textStyle);
}
