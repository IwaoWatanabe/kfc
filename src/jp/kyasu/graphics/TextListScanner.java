/*
 * TextListScanner.java
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

package jp.kyasu.graphics;

import jp.kyasu.graphics.text.TextLineInfo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Locale;

/**
 * The <code>TextListScanner</code> class scans text list to lay out text list,
 * to draw text list, and to compute the position of text list.
 *
 * @version 	22 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextListScanner extends TextScanner {
    /** The column width. */
    protected int colWidths[];

    /** The column index. */
    protected int colIndex;

    /** The column x position. */
    protected int colX;

    /** The clipping rectangle. */
    protected Rectangle clipRect;


    /** The column white space length. */
    static protected final int COLUMN_SPACE = 4;


    /**
     * The stop condition constant that shows the character encountered
     * is a list tab.
     *
     * @see jp.kyasu.graphics.Text#LIST_COL_SEPARATOR_CHAR
     */
    static public final int LIST_COLUMN = IGNORE - 1;

    /**
     * The stop conditions for the first 256 characters. This constant is
     * used for scanning list items with list tabs.
     *
     * @see jp.kyasu.graphics.Text#LIST_SEPARATOR_CHAR
     * @see jp.kyasu.graphics.Text#LIST_COL_SEPARATOR_CHAR
     * @see jp.kyasu.graphics.RichTextStyle#isListSeparator()
     */
    static public final int LIST_COLUMN_STOPS[] = new int[256];

    static {
	System.arraycopy(LIST_STOPS, 0, LIST_COLUMN_STOPS, 0, 256);
	LIST_COLUMN_STOPS[(int)Text.LIST_COL_SEPARATOR_CHAR] = LIST_COLUMN;
    }


    /**
     * Constructs a text list scanner with the rich text to be scanned, line
     * wrapping style, locale of the text, and column widths.
     *
     * @param richText  the rich text style.
     * @param lineWrap  the line wrapping style.
     * @param locale    the locale of the text.
     * @param colWidths the column widths.
     */
    public TextListScanner(RichText richText, int lineWrap, Locale locale,
			   int colWidths[])
    {
	this(richText.getText(), richText.getRichTextStyle(), lineWrap, locale,
	     colWidths);
    }

    /**
     * Constructs a text list scanner with the text to be scanned, rich text
     * style, line wrapping style, locale of the text, and column widths.
     *
     * @param text          the text to be scanned.
     * @param richTextStyle the rich text style.
     * @param lineWrap      the line wrapping style.
     * @param locale        the locale of the text.
     * @param colWidths     the column widths.
     */
    public TextListScanner(Text text, RichTextStyle richTextStyle, int lineWrap,
			   Locale locale, int colWidths[])
    {
	super(text, richTextStyle, lineWrap, locale);
	if (colWidths == null)
	    throw new NullPointerException();
	this.colWidths = colWidths;
	colIndex = 0;
	colX     = 0;
	clipRect = null;
    }


    /**
     * Sets the clipping rectangle.
     */
    public void setClipRect(Rectangle clipRect) {
	if (clipRect == null)
	    throw new NullPointerException();
	this.clipRect = clipRect;
    }

    /**
     * Breaks text into a line.
     *
     * @param  beginIndex the beginning index of text to scan.
     * @param  startX     the starting x position to scan.
     * @param  leftMgn    the left margin of a line.
     * @param  rightMgn   the right margin (edge) of a line.
     * @param  tabW       the tab width used to scan.
     * @param  lineTop    the top position of a line.
     * @param  pStyle     the paragraph style of a line.
     * @param  stops      the stop conditions for scanning.
     * @param  lineInfo   the line information into which the scanning
     *                    results are stored.
     * @return the next line top position, or <code>-1</code> if no more
     *         layout needed.
     */
    public int doLayoutLine(int beginIndex,
			    int startX,
			    int leftMgn,
			    int rightMgn,
			    int tabW,
			    int lineTop,
			    ParagraphStyle pStyle,
			    int stops[],
			    TextLineInfo lineInfo)
    {
	colIndex = 0;
	colX     = 0;
	return super.doLayoutLine(beginIndex, startX, leftMgn, rightMgn, tabW,
				  lineTop, pStyle, stops, lineInfo);
    }

    /**
     * Draws a line from the beginning index to the ending index of text.
     *
     * @param g          the graphics.
     * @param offset     the offset position to draw.
     * @param startX     the starting x position to draw.
     * @param beginIndex the beginning index of text to draw. (inclusive)
     * @param endIndex   the ending index of text to draw. (exclusive)
     * @param lineHeight the height of a line.
     * @param baseline   the baseline of a line.
     * @param leftMgn    the left margin of a line.
     * @param tabW       the tab width used to scan.
     * @param stops      the stop conditions for scanning.
     */
    public void drawLineFromTo(Graphics g, Point offset, int startX,
			       int beginIndex, int endIndex,
			       int lineHeight, int baseline,
			       int leftMgn, int tabW,
			       int stops[])
    {
	colIndex = 0;
	colX     = 0;
	if (colWidths.length > 0) {
	    int left  = 0 + offset.x;
	    int right = left + colWidths[0] - COLUMN_SPACE;
	    if (left > clipRect.x + clipRect.width || right < clipRect.x) {
		g.setClip(0, 0, 0, 0);
	    }
	    else {
		left = Math.max(left, clipRect.x);
		right = Math.min(right, clipRect.x + clipRect.width);
		g.setClip(left, clipRect.y,
			  Math.max(right - left, 0), clipRect.height);
	    }
	}
	super.drawLineFromTo(g, offset, startX, beginIndex, endIndex,
			     lineHeight, baseline, leftMgn, tabW, stops);
	g.setClip(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
    }

    /**
     * Computes the positin of the character in the text.
     *
     * @param  point      the character position in the text.
     * @param  startX     the starting x position to scan.
     * @param  beginIndex the beginning index of a line where the character
     *                    is in.
     * @param  endIndex   the ending index of a line where the character is in.
     * @param  leftMgn    the left margin of a line.
     * @param  tabW       the tab width used to scan.
     * @param  stops      the stop conditions for scanning.
     * @return the computed index and x position of the character.
     */
    public int[] linePositionAt(Point point, int startX,
				int beginIndex, int endIndex,
				int leftMgn, int tabW,
				int stops[])
    {
	colIndex = 0;
	colX     = 0;
	return super.linePositionAt(point, startX, beginIndex, endIndex,
				    leftMgn, tabW, stops);
    }

    /**
     * Handles the stop condition for breaking text into a line.
     *
     * @param  stop the stop condition.
     * @return <code>-1</code> if the scanning runs to the end;
     *         <code>1</code> if the scanning runs across the right edge or
     *         the scanning encounters a line separator or a line break;
     *         <code>0</code> otherwise.
     */
    protected int doLayoutLineStop(int stop) {
	if (stop == LIST_COLUMN) {
	    if (colIndex >= colWidths.length) {
		if (++lastIndex >= runEnd) {
		    lastCondition = END_OF_RUN;
		    return doLayoutLineStop(END_OF_RUN);
		}
		return 0;
	    }
	    colX += colWidths[colIndex++];
	    if (colX > rightMargin) {
		lastCondition = CROSSED_X;
		return doLayoutLineStop(CROSSED_X);
	    }
	    else {
		destX = colX;
		if (++lastIndex >= runEnd) {
		    lastCondition = END_OF_RUN;
		    return doLayoutLineStop(END_OF_RUN);
		}
	    }
	    return 0;
	}
	return super.doLayoutLineStop(stop);
    }

    /**
     * Handles the stop condition for drawing a line.
     *
     * @param stop   the stop condition.
     * @param g      the graphics.
     * @param offset the offset position to draw.
     * @param drawY  the current y position.
     * @param rStart the run start index.
     * @param rEnd   the run end index.
     * @param tStyle the current text style.
     * @return <code>-1</code> if the scanning runs to the end;
     *         <code>0</code> otherwise.
     */
    protected int doDrawLineStop(int stop,
				 Graphics g,
				 Point offset,
				 int drawY,
				 int rStart, int rEnd,
				 TextStyle tStyle)
    {
	if (stop == LIST_COLUMN) {
	    if (colIndex >= colWidths.length) {
		if (++lastIndex >= runEnd) {
		    lastCondition = END_OF_RUN;
		    return doDrawLineStop(END_OF_RUN, g, offset, drawY,
					  rStart, rEnd, tStyle);
		}
		return 0;
	    }
	    colX += colWidths[colIndex++];
	    int left = colX + offset.x;
	    int right;
	    if (colIndex < colWidths.length) {
		right = left + colWidths[colIndex] - COLUMN_SPACE;
	    }
	    else {
		right = clipRect.x + clipRect.width;
	    }
	    if (left > clipRect.x + clipRect.width || right < clipRect.x) {
		g.setClip(0, 0, 0, 0);
	    }
	    else {
		left = Math.max(left, clipRect.x);
		right = Math.min(right, clipRect.x + clipRect.width);
		g.setClip(left, clipRect.y,
			  Math.max(right - left, 0), clipRect.height);
	    }
	    if (colX > rightMargin) { // not happen
		lastCondition = CROSSED_X;
		return doDrawLineStop(CROSSED_X, g, offset, drawY,
				      rStart, rEnd, tStyle);
	    }
	    else {
		destX = colX;
		if (++lastIndex >= runEnd) {
		    lastCondition = END_OF_RUN;
		    return doDrawLineStop(END_OF_RUN, g, offset, drawY,
					  rStart, rEnd, tStyle);
		}
	    }
	    return 0;
	}
	return super.doDrawLineStop(stop, g, offset, drawY,
				    rStart, rEnd, tStyle);
    }

    /**
     * Handles the stop condition for computing a position of character.
     *
     * @param  stop the stop condition.
     * @return <code>-1</code> if the scanning runs to the end;
     *         <code>1</code> if the scanning runs across the right edge or
     *         the scanning encounters a line separator or a line break;
     *         <code>0</code> otherwise.
     */
    protected int doTextPositionStop(int stop) {
	if (stop == LIST_COLUMN) {
	    if (colIndex >= colWidths.length) {
		if (++lastIndex >= runEnd) {
		    lastCondition = END_OF_RUN;
		    return doTextPositionStop(END_OF_RUN);
		}
		return 0;
	    }
	    colX += colWidths[colIndex++];
	    if (colX > rightMargin) {
		lastCondition = CROSSED_X;
		return doTextPositionStop(CROSSED_X);
	    }
	    else {
		destX = colX;
		if (++lastIndex >= runEnd) {
		    lastCondition = END_OF_RUN;
		    return doTextPositionStop(END_OF_RUN);
		}
	    }
	    return 0;
	}
	return super.doTextPositionStop(stop);
    }

    /**
     * Returns the stop conditions.
     *
     * @return the stop conditions.
     */
    protected int[] getStops() {
	return LIST_COLUMN_STOPS;
    }
}
