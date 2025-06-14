/*
 * TextEditModel.java
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

import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.ParagraphStyleModifier;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.TextStyleModifier;

import java.awt.event.TextListener;

/**
 * The model interface for an object that acts as a model for
 * <code>TextEditController</code> and <code>TextEditView</code>.
 *
 * @see 	jp.kyasu.awt.TextComponent
 * @see 	jp.kyasu.awt.text.TextEditController
 * @see 	jp.kyasu.awt.text.TextEditView
 * @see 	jp.kyasu.awt.event.TextModelEvent
 * @see 	jp.kyasu.awt.event.TextModelListener
 *
 * @version 	11 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public interface TextEditModel extends TextModel {

    /**
     * Adds the specified text listener to receive text events from
     * this text edit model.
     *
     * @param listener the text listener.
     */
    public void addTextListener(TextListener listener);

    /**
     * Removes the specified text listener so it no longer receives
     * text events from this text edit model.
     *
     * @param listener the text listener.
     */
    public void removeTextListener(TextListener listener);

    /**
     * Replaces the specified range of the rich text of this text edit
     * model with the specified replacement text object.
     *
     * @param  begin the beginning text position to replace, inclusive.
     * @param  end   the ending text position to replace, exclusive.
     * @param  rep   a replacement <code>Text</code> object.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public Undo replace(int begin, int end, Text rep);

    /*
     * Sets the text style in the specified range of the rich text of this
     * text edit model to be the specified text style.
     *
     * @param  begin     the beginning text position to set, inclusive.
     * @param  end       the ending text position to set, exclusive.
     * @param  textStyle the text style.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public Undo setTextStyle(int begin, int end, TextStyle textStyle);

    /*
     * Modifies the text style in the specified range of the rich text of
     * this text edit model by using the specified text style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the text style modifier.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public Undo modifyTextStyle(int begin, int end, TextStyleModifier modifier);

    /*
     * Sets the paragraph style in the specified range of the rich text
     * of this text edit model to be the specified paragraph style.
     *
     * @param  begin  the beginning text position to set, inclusive.
     * @param  end    the ending text position to set, exclusive.
     * @param  pStyle the paragraph style.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public Undo setParagraphStyle(int begin, int end, ParagraphStyle pStyle);

    /*
     * Modifies the paragraph style in the specified range of the rich text of
     * this text edit model by using the specified paragraph style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the paragraph style modifier.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public Undo modifyParagraphStyle(int begin, int end,
				     ParagraphStyleModifier modifier);
}
