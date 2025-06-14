/*
 * TextPositionEvent.java
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

import jp.kyasu.graphics.text.TextPositionInfo;

import java.awt.Component;

/**
 * The TextPosition event that is originated from a <code>TextEditView</code>
 * object to <code>TextPositionListener</code>s.
 *
 * @see 	jp.kyasu.awt.text.TextEditView
 * @see 	jp.kyasu.awt.event.TextPositionListener
 *
 * @version 	13 Apr 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextPositionEvent extends java.awt.AWTEvent {
    protected TextPositionInfo selectionBegin;
    protected TextPositionInfo selectionEnd;


    /**
     * Marks the first integer id for the range of text position event ids.
     */
    static public final int TEXT_POSITION_FIRST =
					java.awt.AWTEvent.RESERVED_ID_MAX + 1;

    /**
     * Marks the last integer id for the range of text position event ids.
     */
    static public final int TEXT_POSITION_LAST  = TEXT_POSITION_FIRST;

    /**
     * The text position changed event.
     */
    static public final int TEXT_POSITION_CHANGED = TEXT_POSITION_FIRST;


    /**
     * Constructs a TextPositionEvent object with the specified source, id,
     * beginning position, and ending position.
     *
     * @param source the object where the event originated.
     * @param id     the event type.
     * @param begin  the beginning position of the selection, inclusive.
     * @param end    the ending position of the selection, exclusive.
     */
    public TextPositionEvent(Object source, int id,
			     TextPositionInfo begin, TextPositionInfo end)
    {
        super(source, id);
	if (begin == null || end == null)
	    throw new NullPointerException();
	if (id != TEXT_POSITION_CHANGED) {
	    throw new IllegalArgumentException("improper id: " + id);
	}
	selectionBegin = begin;
	selectionEnd   = end;
    }


    /**
     * Returns the beginning position information of the selection.
     */
    public TextPositionInfo getSelectionBegin() {
        return selectionBegin;
    }

    /**
     * Returns the ending position information of the selection.
     */
    public TextPositionInfo getSelectionEnd() {
        return selectionEnd;
    }

    /**
     * Returns the beginning position index of the selection.
     */
    public int getSelectionBeginIndex() {
        return selectionBegin.textIndex;
    }

    /**
     * Returns the ending position index of the selection.
     */
    public int getSelectionEndIndex() {
        return selectionEnd.textIndex;
    }

    /**
     * Returns the beginning position line index of the selection.
     */
    public int getSelectionBeginLineIndex() {
        return selectionBegin.lineIndex;
    }

    /**
     * Returns the ending position line index of the selection.
     */
    public int getSelectionEndLineIndex() {
        return selectionEnd.lineIndex;
    }

    /**
     * Returns true if the selection is caret, i.e., null selection.
     */
    public boolean selectionIsCaret() {
	return selectionBegin.textIndex == selectionEnd.textIndex;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case TEXT_POSITION_CHANGED:
              typeStr = "TEXT_POSITION_CHANGED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr + ",begin=" + selectionBegin + ",end=" + selectionEnd;
    }
}
