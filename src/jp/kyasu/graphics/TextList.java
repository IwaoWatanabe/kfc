/*
 * TextLayout.java
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

import jp.kyasu.graphics.text.TextPositionInfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Locale;

/**
 * The <code>TextList</code> class implements the layout of the
 * <code>RichText</code> object that represents a text list.
 *
 * @version 	10 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextList extends TextLayout {
    /** The column widths. */
    protected int colWidths[];


    /**
     * Constructs a text list with the specified rich text and column widths.
     *
     * @param richText  the rich text to be laid out.
     * @param colWidths the column widths.
     */
    public TextList(RichText richText, int colWidths[]) {
	this(richText, colWidths, Locale.getDefault());
    }

    /**
     * Constructs a text list with the specified rich text, line wrapping
     * style, column widths, and locale.
     *
     * @param richText  the rich text to be laid out.
     * @param locale    the locale.
     * @param colWidths the column widths.
     */
    public TextList(RichText richText, int colWidths[], Locale locale) {
	super(richText, RichTextStyle.NO_WRAP, locale);
	if (!richText.getRichTextStyle().isListSeparator())
	    throw new IllegalArgumentException("improper separator");
	if (colWidths == null)
	    throw new NullPointerException();
	this.colWidths = colWidths;
    }


    /**
     * Returns the column widths.
     */
    public int[] getColumnWidths() {
	return colWidths;
    }

    /**
     * Sets the column widths to be the specified widths.
     */
    public void setColumnWidths(int colWidths[]) {
	if (colWidths == null)
	    throw new NullPointerException();
	this.colWidths = colWidths;
    }


    /**
     * Returns a clone of this object.
     */
    public Object clone() {
	TextList textList = (TextList)super.clone();
	textList.colWidths = new int[colWidths.length];
	System.arraycopy(colWidths, 0, textList.colWidths, 0, colWidths.length);
	return textList;
    }


    /**
     * Draws the specified range of this layout to the specified
     * graphics at the specified location, with the specified colors,
     * width for drawing and selected indices.
     * <p>
     * This method is used for drawing the list items.
     *
     * @param g          the graphics.
     * @param offset     the offset position to draw
     * @param begin      the beginning index of this layout to draw,
     *                   inclusive.
     * @param end        the ending index of this layout to draw,
     *                   exclusive.
     * @param fgColor    the foreground color.
     * @param bgColor    the background color.
     * @param selFgColor the selection foreground color.
     * @param selBgColor the selection background color.
     * @param width      the width for drawing.
     * @param selected   the selected indices of the list items.
     *
     * @see   jp.kyasu.awt.text.TextListView
     */
    public void draw(Graphics g, Point offset,
		     TextPositionInfo begin, TextPositionInfo end,
		     Color fgColor, Color bgColor,
		     Color selFgColor, Color selBgColor,
		     int width, int selected[])
    {
	draw(g, offset, begin, end, fgColor, bgColor, selFgColor, selBgColor,
	     width, selected, false);
    }

    /**
     * Draws the specified range of this layout to the specified
     * graphics at the specified location, with the specified colors,
     * width for drawing, selected indices and redraw flag.
     * <p>
     * This method is used for drawing the list items.
     *
     * @param g          the graphics.
     * @param offset     the offset position to draw
     * @param begin      the beginning index of this layout to draw,
     *                   inclusive.
     * @param end        the ending index of this layout to draw,
     *                   exclusive.
     * @param fgColor    the foreground color.
     * @param bgColor    the background color.
     * @param selFgColor the selection foreground color.
     * @param selBgColor the selection background color.
     * @param width      the width for drawing.
     * @param selected   the selected indices of the list items.
     * @param redrawBg   if true, fills the background even if the list item
     *                   is not selected.
     *
     * @see   jp.kyasu.awt.text.TextListView
     */
    public void draw(Graphics g, Point offset,
		     TextPositionInfo begin, TextPositionInfo end,
		     Color fgColor, Color bgColor,
		     Color selFgColor, Color selBgColor,
		     int width, int selected[], boolean redrawBg)
    {
	assert(isValid());

	/*
	if (begin.textIndex != begin.lineBegin) {
	    begin = getTextPositionAtLineBegin(begin.lineIndex);
	}
	if (end.textIndex != end.lineBegin && end.textIndex != end.lineEnd) {
	    end = getTextPositionNearby(end, end.lineEnd);
	}
	*/

	if (width < layoutWidth) width = layoutWidth;
	Point p = new Point(offset);
	TextScanner scanner = getScanner(g);
	p.y += begin.y;
	int endLineIndex = end.lineIndex;
	int height = begin.lineSkip;

	if (begin.lineIndex == endLineIndex) {
	    if (begin.textIndex != begin.lineBegin ||
		end.textIndex != end.lineEnd)
	    {
		return;
	    }
	    if (isIndexSelected(begin.lineIndex, selected)) {
		g.setColor(selBgColor);
		g.fillRect(p.x, p.y, width, height);
		g.setColor(selFgColor);
	    }
	    else if (redrawBg) {
		g.setColor(bgColor);
		g.fillRect(p.x, p.y, width, height);
		g.setColor(fgColor);
	    }
	    else {
		g.setColor(fgColor);
	    }
	    if (rtStyle.variableLineHeight &&
		begin.paragraphStyle.hasHeading() &&
		(begin.lineIndex == 0 || isParagraphMarkAt(begin.lineIndex-1)))
	    {
		ParagraphStyle bps = begin.paragraphStyle;
		Dimension d = bps.heading.getSize();
		int bh = begin.lineHeight + begin.paragraphStyle.lineSpace;
		bps.heading.paint(g,
		    new Point(
			p.x + bps.leftIndent - bps.headingSpace - d.width,
			p.y + ((bh - d.height) / 2)));
	    }
	    scanner.drawLineFromTo(g, p, begin.x,
				   begin.textIndex, end.textIndex,
				   begin, begin.paragraphStyle);
	    return;
	}

	if (begin.textIndex == begin.lineBegin) {
	    if (isIndexSelected(begin.lineIndex, selected)) {
		g.setColor(selBgColor);
		g.fillRect(p.x, p.y, width, height);
		g.setColor(selFgColor);
	    }
	    else if (redrawBg) {
		g.setColor(bgColor);
		g.fillRect(p.x, p.y, width, height);
		g.setColor(fgColor);
	    }
	    else {
		g.setColor(fgColor);
	    }
	    if (rtStyle.variableLineHeight &&
		begin.paragraphStyle.hasHeading() &&
		(begin.lineIndex == 0 ||
		isParagraphMarkAt(begin.lineIndex - 1)))
	    {
		ParagraphStyle bps = begin.paragraphStyle;
		Dimension d = bps.heading.getSize();
		int bh = begin.lineHeight + begin.paragraphStyle.lineSpace;
		bps.heading.paint(g,
		    new Point(
			p.x + bps.leftIndent - bps.headingSpace - d.width,
			p.y + ((bh - d.height) / 2)));
	    }
	    scanner.drawLineFromTo(g, p, begin.x,
				   begin.textIndex, begin.lineEnd,
				   begin, begin.paragraphStyle);
	}
	p.y += height;

	if (!rtStyle.variableLineHeight) {
	    int lBegins[] = (int[])lineBegins.getArray();
	    int lineBegin = unmarkLineBegin(lBegins[begin.lineIndex + 1]);
	    for (int i = begin.lineIndex + 1; i < endLineIndex; i++) {
		int lineEnd = unmarkLineBegin(lBegins[i + 1]);
		if (isIndexSelected(i, selected)) {
		    g.setColor(selBgColor);
		    g.fillRect(p.x, p.y, width,
			       lineHeight + rtStyle.paragraphStyle.lineSpace);
		    g.setColor(selFgColor);
		}
		else if (redrawBg) {
		    g.setColor(bgColor);
		    g.fillRect(p.x, p.y, width,
			       lineHeight + rtStyle.paragraphStyle.lineSpace);
		    g.setColor(fgColor);
		}
		else {
		    g.setColor(fgColor);
		}
		scanner.drawLineTo(g, p,
				   lineBegin, lineEnd,
				   remainWidths.getInt(i),
				   lineHeight, baseline,
				   rtStyle.paragraphStyle);
		p.y += (lineHeight + rtStyle.paragraphStyle.lineSpace);
		lineBegin = lineEnd;
	    }
	    if (end.textIndex != end.lineEnd) {
		return;
	    }
	    assert(lineBegin == end.lineBegin);
	    assert(p.y == end.y + offset.y);
	    if (isIndexSelected(end.lineIndex, selected)) {
		g.setColor(selBgColor);
		g.fillRect(p.x, p.y, width,
			   lineHeight + rtStyle.paragraphStyle.lineSpace);
		g.setColor(selFgColor);
	    }
	    else if (redrawBg) {
		g.setColor(bgColor);
		g.fillRect(p.x, p.y, width,
			   lineHeight + rtStyle.paragraphStyle.lineSpace);
		g.setColor(fgColor);
	    }
	    else {
		g.setColor(fgColor);
	    }
	    scanner.drawLineTo(g, p, end.textIndex, end,
			       rtStyle.paragraphStyle);
	}
	else {
	    int lBegins[] = (int[])lineBegins.getArray();
	    int lTops[] = (int[])lineTops.getArray();
	    ParagraphStyle pStyle = null;
	    int lineBegin = unmarkLineBegin(lBegins[begin.lineIndex + 1]);
	    int lineTop = lTops[begin.lineIndex + 1];
	    for (int i = begin.lineIndex + 1; i < endLineIndex; i++) {
		int lineEnd = unmarkLineBegin(lBegins[i + 1]);
		int nextTop = lTops[i + 1];
		if (pStyle == null)
		    pStyle = richText.getParagraphStyleAt(lineBegin);
		int lh = height = nextTop - lineTop;
		boolean isParaMark = isParagraphMark(lBegins[i]);
		if (isParaMark)
		    lh -= pStyle.paragraphSpace;
		if (isIndexSelected(i, selected)) {
		    g.setColor(selBgColor);
		    g.fillRect(p.x, p.y, width, height);
		    g.setColor(selFgColor);
		}
		else if (redrawBg) {
		    g.setColor(bgColor);
		    g.fillRect(p.x, p.y, width, height);
		    g.setColor(fgColor);
		}
		else {
		    g.setColor(fgColor);
		}
		if (pStyle.hasHeading() && isParagraphMark(lBegins[i - 1])) {
		    Dimension d = pStyle.heading.getSize();
		    pStyle.heading.paint(g,
				new Point(
					p.x + pStyle.leftIndent
						- pStyle.headingSpace
						- d.width,
					p.y + ((lh - d.height) / 2)));
		}
		scanner.drawLineTo(g, p,
				   lineBegin, lineEnd,
				   remainWidths.getInt(i),
				   lh - pStyle.lineSpace,
				   baselines.getInt(i),
				   pStyle);
		p.y += height;
		lineBegin = lineEnd;
		lineTop = nextTop;
		if (isParaMark)
		    pStyle = null;
	    }
	    if (end.textIndex != end.lineEnd) {
		return;
	    }

	    //assert(p.y == end.y + offset.y);

	    pStyle = end.paragraphStyle;
	    height = end.lineSkip;
	    if (isIndexSelected(end.lineIndex, selected)) {
		g.setColor(selBgColor);
		g.fillRect(p.x, p.y, width, height);
		g.setColor(selFgColor);
	    }
	    else if (redrawBg) {
		g.setColor(bgColor);
		g.fillRect(p.x, p.y, width, height);
		g.setColor(fgColor);
	    }
	    else {
		g.setColor(fgColor);
	    }
	    if (pStyle.hasHeading() &&
		isParagraphMark(lBegins[end.textIndex - 1]))
	    {
		Dimension d = pStyle.heading.getSize();
		int eh = end.lineHeight + end.paragraphStyle.lineSpace;
		pStyle.heading.paint(g,
		    new Point(
			p.x + pStyle.leftIndent - pStyle.headingSpace - d.width,
			p.y + ((eh - d.height) / 2)));
	    }
	    scanner.drawLineTo(g, p, end.textIndex, end, pStyle);
	}
    }


    /** Returns the scanner for this layout. */
    protected TextScanner getScanner(Graphics g) {
	TextListScanner scanner = (TextListScanner)getScanner();
	Rectangle clip = g.getClipBounds();
	if (clip == null) {
	    clip = new Rectangle(0, 0, layoutWidth, layoutHeight);
	}
	scanner.setClipRect(clip);
	return scanner;
    }

    /** Returns the scanner for this layout. */
    protected TextScanner getScanner() {
	return new TextListScanner(richText, lineWrap, locale, colWidths);
    }

    /** Returns true if the index is included in the selected. */
    protected boolean isIndexSelected(int index, int selected[]) {
	if (selected == null)
	    return false;
	int len = selected.length;
	for (int i = 0; i < len; i++) {
	    if (selected[i] == index)
		return true;
	}
	return false;
    }
}
