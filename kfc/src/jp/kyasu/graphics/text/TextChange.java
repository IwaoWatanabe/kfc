/*
 * TextChange.java
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

package jp.kyasu.graphics.text;

/**
 * The <code>TextChange</code> class provides an information of changes
 * made on a <code>RichText</code> object.
 *
 * @see 	jp.kyasu.graphics.RichText
 *
 * @version 	11 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class TextChange {
    /**
     * The change status.
     * @see #PARTIAL_LAYOUT
     * @see #NO_LAYOUT
     * @see #FULL_LAYOUT
     */
    public int changeStatus;

    /**
     * The beginning index of the text changed, inclusive.
     * This variable is valid when partail layout is needed.
     */
    public int begin;

    /**
     * The ending index of the text changed, exclusive.
     * This variable is valid when partail layout is needed.
     */
    public int end;

    /**
     * The beginning index of the text to be laid out, inclusive.
     * This variable is valid when partail layout is needed.
     */
    public int layoutBegin;

    /**
     * The ending index of the text to be laid out, exclusive.
     * This variable is valid when partail layout is needed.
     */
    public int layoutEnd;

    /**
     * The length changed.
     * This variable is valid when partail layout is needed.
     */
    public int lengthChanged;

    /**
     * True if the text was replaced.
     * This variable is valid when partail layout is needed.
     */
    public boolean textReplaced;

    /**
     * True if the paragraph style at the <code>layoutBegin</code> index
     * of the text was changed.
     * This variable is valid when partail layout is needed.
     */
    public boolean paragraphStyleChanged;


    /**
     * Constant for the partial layout.
     */
    static public final int PARTIAL_LAYOUT = 0;

    /**
     * Constant for the no layout.
     */
    static public final int NO_LAYOUT      = 1;

    /**
     * Constant for the full layout.
     */
    static public final int FULL_LAYOUT    = 2;


    /**
     * Constructs a partial change information.
     *
     * @param begin                 the beginning index of the text changed,
     *                              inclusive.
     * @param end                   the ending index of the text changed,
     *                              exclusive.
     * @param layoutBegin           the beginning index of the text to be
     *                              laid out, inclusive.
     * @param layoutEnd             the ending index of the text to be laid
     *                              out, exclusive.
     * @param lengthChanged         the length changed.
     * @param textReplaced          <code>true</code> if the text was replaced.
     * @param paragraphStyleChanged <code>true</code> if the paragraph style
     *                              at the <code>layoutBegin</code> index of
     *                              the text was changed.
     */
    public TextChange(int begin, int end, int layoutBegin, int layoutEnd,
		      int lengthChanged, boolean textReplaced,
		      boolean paragraphStyleChanged)
    {
	this.begin                 = begin;
	this.end                   = end;
	this.layoutBegin           = layoutBegin;
	this.layoutEnd             = layoutEnd;
	this.lengthChanged         = lengthChanged;
	this.textReplaced          = textReplaced;
	this.paragraphStyleChanged = paragraphStyleChanged;
	changeStatus    = PARTIAL_LAYOUT;
    }

    /**
     * Constructs a non or full change information.
     *
     * @param changeStatus the change status. <code>NO_LAYOUT</code> and
     *                     <code>FULL_LAYOUT</code> are valied.
     */
    public TextChange(int changeStatus) {
	switch (changeStatus) {
	case NO_LAYOUT:
	case FULL_LAYOUT:
	    this.changeStatus = changeStatus;
	    break;
	default:
	    throw new IllegalArgumentException("improper changeStatus: " +
								changeStatus);
	}
	begin                 = 0;
	end                   = 0;
	layoutBegin           = 0;
	layoutEnd             = 0;
	lengthChanged         = 0;
	textReplaced          = false;
	paragraphStyleChanged = false;
    }


    /**
     * Checks if the partial layout is needed.
     */
    public boolean isPartialLayout() {
	return changeStatus == PARTIAL_LAYOUT;
    }

    /**
     * Checks if no layout is needed.
     */
    public boolean isNoLayout() {
	return changeStatus == NO_LAYOUT;
    }

    /**
     * Checks if the full layout is needed.
     */
    public boolean isFullLayout() {
	return changeStatus == FULL_LAYOUT;
    }

    /**
     * Returns a string representation of this object.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	if (isNoLayout()) {
	    buffer.append("no layout");
	}
	else if (isFullLayout()) {
	    buffer.append("full layout");
	}
	else { // isPartialLayout()
	    buffer.append("begin="                  + begin);
	    buffer.append(",end="                   + end);
	    buffer.append(",layoutBegin="           + layoutBegin);
	    buffer.append(",layoutEnd="             + layoutEnd);
	    buffer.append(",lengthChanged="         + lengthChanged);
	    buffer.append(",textReplaced="          + textReplaced);
	    buffer.append(",paragraphStyleChanged=" + paragraphStyleChanged);
	}
	return getClass().getName() + "[" + buffer.toString() + "]";
    }
}
