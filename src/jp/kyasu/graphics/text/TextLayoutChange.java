/*
 * TextLayoutChange.java
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
 * The <code>TextLayoutChange</code> class provides an information of
 * changes in a layout made on a <code>TextLayout</code> object.
 *
 * @see 	jp.kyasu.graphics.TextLayout
 *
 * @version 	11 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class TextLayoutChange {
    /**
     * The change status.
     * @see #PARTIAL_REPAINT
     * @see #NO_REPAINT
     * @see #FULL_REPAINT
     */
    public int changeStatus;

    /**
     * The beginning position to repaint, inclusive.
     * This variable is valid when partail repainting is needed.
     */
    public TextPositionInfo paintBegin;

    /**
     * The ending position to repaint, exclusive.
     * This variable is valid when partail repainting is needed.
     */
    public TextPositionInfo paintEnd;

    /**
     * True if repainting is needed from the beginning of the line.
     * This variable is valid when partail repainting is needed.
     */
    public boolean paintFromLineBegin;

    /**
     * True if repainting is needed to the ending of the line.
     * This variable is valid when partail repainting is needed.
     */
    public boolean paintToLineEnd;

    /**
     * The height changed.
     * This variable is valid when partail repainting is needed.
     */
    public int heightChanged;

    /**
     * The width changed.
     * This variable is valid when partail repainting is needed.
     */
    public int widthChanged;


    /**
     * Constant for the partial repaint.
     */
    static public final int PARTIAL_REPAINT = 0;

    /**
     * Constant for no repaint.
     */
    static public final int NO_REPAINT      = 1;

    /**
     * Constant for the full repaint.
     */
    static public final int FULL_REPAINT    = 2;


    /**
     * Constructs a partial change information with the specified informations.
     *
     * @param paintBegin         the beginning position to repaint, inclusive.
     * @param paintEnd           the ending position to repaint, exclusive.
     * @param paintFromLineBegin true if repainting is needed from the
     *                           beginning of the line.
     * @param paintToLineEnd     true if repainting is needed to the ending
     *                           of the line.
     * @param heightChanged      the height changed.
     * @param widthChanged       the width changed.
     */
    public TextLayoutChange(TextPositionInfo paintBegin,
			    TextPositionInfo paintEnd,
			    boolean paintFromLineBegin,
			    boolean paintToLineEnd,
			    int heightChanged,
			    int widthChanged)
    {
	if (paintBegin == null || paintEnd == null)
	    throw new NullPointerException();
	this.paintBegin         = paintBegin;
	this.paintEnd           = paintEnd;
	this.paintFromLineBegin = paintFromLineBegin;
	this.paintToLineEnd     = paintToLineEnd;
	this.heightChanged      = heightChanged;
	this.widthChanged       = widthChanged;
	changeStatus = PARTIAL_REPAINT;
    }

    /**
     * Constructs a non or full change information.
     *
     * @param changeStatus the change status. <code>NO_REPAINT</code> and
     *                     <code>FULL_REPAINT</code> are valied.
     */
    public TextLayoutChange(int changeStatus) {
	switch (changeStatus) {
	case NO_REPAINT:
	case FULL_REPAINT:
	    this.changeStatus = changeStatus;
	    break;
	default:
	    throw new IllegalArgumentException("improper changeStatus: " +
								changeStatus);
	}
	this.paintBegin         = null;
	this.paintEnd           = null;
	this.paintFromLineBegin = false;
	this.paintToLineEnd     = false;
	this.heightChanged      = 0;
	this.widthChanged       = 0;
    }


    /**
     * Checks if the partial repainting is needed.
     */
    public boolean isPartialRepaint() {
	return changeStatus == PARTIAL_REPAINT;
    }

    /**
     * Checks if no repainting is needed.
     */
    public boolean isNoRepaint() {
	return changeStatus == NO_REPAINT;
    }

    /**
     * Checks if the full repainting is needed.
     */
    public boolean isFullRepaint() {
	return changeStatus == FULL_REPAINT;
    }

    /**
     * Returns a string representation of this object.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	if (isNoRepaint()) {
	    buffer.append("no repaint");
	}
	else if (isFullRepaint()) {
	    buffer.append("full repaint");
	}
	else { // isPartialRepaint()
	    buffer.append("paintBegin="          + paintBegin);
	    buffer.append(",paintEnd="           + paintEnd);
	    buffer.append(",paintFromLineBegin=" + paintFromLineBegin);
	    buffer.append(",paintToLineEnd="     + paintToLineEnd);
	    buffer.append(",heightChanged="      + heightChanged);
	    buffer.append(",widthChanged="       + widthChanged);
	}
	return getClass().getName() + "[" + buffer.toString() + "]";
    }
}
