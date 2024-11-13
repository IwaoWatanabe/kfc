/*
 * TextModel.java
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

import jp.kyasu.awt.event.TextModelListener;
import jp.kyasu.graphics.RichText;

/**
 * The model interface for an object that acts as a text model.
 *
 * @see 	jp.kyasu.awt.event.TextModelEvent
 * @see 	jp.kyasu.awt.event.TextModelListener
 *
 * @version 	11 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public interface TextModel {

    /**
     * Adds the specified text model listener to receive text model events
     * from this text model.
     *
     * @param listener the text model listener.
     */
    public void addTextModelListener(TextModelListener listener);

    /**
     * Removes the specified text model listener so it no longer receives
     * text model events from this text model.
     *
     * @param listener the text model listener.
     */
    public void removeTextModelListener(TextModelListener listener);

    /**
     * Returns the rich text of this text model.
     *
     * @return the rich text.
     */
    public RichText getRichText();

    /**
     * Sets the rich text of this text model to be the specified rich text.
     *
     * @param richText the rich text.
     */
    public void setRichText(RichText richText);

}
