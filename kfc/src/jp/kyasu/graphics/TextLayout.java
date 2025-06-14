/*
 * TextLayout.java
 *
 * Copyright (c) 1997, 1998, 1999 Kazuki YASUMATSU.  All Rights Reserved.
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

import jp.kyasu.graphics.text.TextChange;
import jp.kyasu.graphics.text.TextLayoutChange;
import jp.kyasu.graphics.text.TextLineInfo;
import jp.kyasu.graphics.text.TextPositionInfo;
import jp.kyasu.util.RunArray;
import jp.kyasu.util.VArray;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Locale;

/**
 * The <code>TextLayout</code> class implements the layout of the
 * <code>RichText</code>. The text layout composes the <code>RichText</code>
 * object into multiple paragraphs that are separated by the line end
 * character (<code>RichTextStyle.getLineEndChar()</code>). Each paragraph
 * has its own paragraph style, if the
 * <code>RichTextStyle.multipleParagraphStylesAllowed()</code> is
 * <code>true</code>. The <code>RichText</code> object in the paragraph is
 * composed into multiple lines that are broken at the layout width.
 *
 * @see 	jp.kyasu.graphics.RichText
 * @see 	jp.kyasu.graphics.TextLines
 *
 * @version 	21 Nov 1999
 * @author 	Kazuki YASUMATSU
 */
public class TextLayout extends TextLines implements Visualizable {
    /** The line wrapping style. */
    protected int lineWrap;

    /** The layout width. */
    protected int layoutWidth;

    /** The preferred layout width. */
    protected int preferredLayoutWidth;

    /** The locale for the layout. */
    protected Locale locale;

    /**
     * The line height is constant or not when the
     * <code>rtStyle.variableLineHeight</code> is false.
     */
    protected boolean lineHeightConstant;

    /** The echo character for the layout. */
    protected char echoChar;


    /**
     * Constructs a text layout with the specified rich text.
     *
     * @param richText the rich text to be laid out.
     */
    public TextLayout(RichText richText) {
	this(richText, richText.getRichTextStyle().getLineWrap(),
	     Locale.getDefault());
    }

    /**
     * Constructs a text layout with the specified rich text and locale.
     *
     * @param richText the rich text to be laid out.
     * @param locale   the locale for a layout.
     */
    public TextLayout(RichText richText, Locale locale) {
	this(richText, richText.getRichTextStyle().getLineWrap(), locale);
    }

    /**
     * Constructs a text layout with the specified rich text and line
     * wrapping style.
     *
     * @param richText the rich text to be laid out.
     * @param lineWrap the line wrapping style.
     * @see jp.kyasu.graphics.RichTextStyle#CHAR_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#WORD_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#NO_WRAP
     */
    public TextLayout(RichText richText, int lineWrap) {
	this(richText, lineWrap, Locale.getDefault());
    }

    /**
     * Constructs a text layout with the specified rich text, line
     * wrapping style, and locale.
     *
     * @param richText the rich text to be laid out.
     * @param lineWrap the line wrapping style.
     * @param locale   the locale for a layout.
     * @see jp.kyasu.graphics.RichTextStyle#CHAR_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#WORD_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#NO_WRAP
     */
    public TextLayout(RichText richText, int lineWrap, Locale locale) {
	super(richText);
	if (locale == null)
	    throw new NullPointerException();
	setLineWrapInner(lineWrap);
	this.locale = locale;

	this.layoutWidth = 0;
	this.layoutHeight = 0;
	this.preferredLayoutWidth = 0;

	this.lineHeightConstant = false;
	this.echoChar = 0;
    }


    /**
     * Returns the size of this layout.
     *
     * @see #setSize(java.awt.Dimension)
     * @see #setWidth(int)
     * @see jp.kyasu.graphics.Visualizable
     */
    public Dimension getSize() {
	return (isValid() ?
		    new Dimension(layoutWidth, layoutHeight) :
		    new Dimension(0, 0));
    }

    /**
     * Resizes the layout to the specified dimension.
     *
     * @see #getSize()
     * @see #setWidth(int)
     * @see jp.kyasu.graphics.Visualizable
     */
    public void setSize(Dimension d) {
	setWidth(d.width);
    }

    /**
     * Checks if the layout is resizable.
     * @see jp.kyasu.graphics.Visualizable
     */
    public boolean isResizable() {
	return false;
    }

    /**
     * Paints this layout at the specified location.
     *
     * @param g the specified graphics.
     * @param p the location in the graphics to be painted.
     * @see jp.kyasu.graphics.Visualizable
     */
    public void paint(Graphics g, Point p) {
	if (isValid()) {
	    draw(g, p, getTextPositionAt(0),
		       getTextPositionAt(richText.length()));
	}
    }

    /**
     * Returns a clone of this layout.
     */
    public Object clone() {
	try {
	    TextLayout layout = (TextLayout)super.clone();
	    layout.richText             = (RichText)richText.clone();
	    layout.rtStyle              = rtStyle; // share
	    layout.layoutWidth          = layoutWidth;
	    layout.layoutHeight         = layoutHeight;
	    layout.preferredLayoutWidth = preferredLayoutWidth;
	    layout.lineHeight           = lineHeight;
	    layout.baseline             = baseline;
	    layout.lineWrap             = lineWrap;
	    layout.echoChar             = echoChar;
	    layout.locale               = locale; // share
	    layout.lineHeightConstant   = lineHeightConstant;
	    layout.lineBegins =
		(lineBegins == null ? null : (VArray)lineBegins.clone());
	    layout.remainWidths =
		(remainWidths == null ? null : (VArray)remainWidths.clone());
	    layout.baselines =
		(baselines == null ? null : (VArray)baselines.clone());
	    layout.lineTops =
		(lineTops == null ? null : (VArray)lineTops.clone());
	    return layout;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }


    /**
     * Returns the locale of this layout.
     */
    public Locale getLocale() {
	return locale;
    }

    /**
     * Sets the locale of this layout to be the specified locale.
     */
    public void setLocale(Locale locale) {
	if (locale == null)
	    throw new NullPointerException();
	this.locale = locale;
    }

    /**
     * Returns the character to be used for echoing.
     *
     * @see #setEchoChar(char)
     * @see #echoCharIsSet()
     * @see jp.kyasu.graphics.EchoTextScanner
     * @see jp.kyasu.awt.TextField#getEchoChar()
     */
    public char getEchoChar() {
	return echoChar;
    }

    /**
     * Sets the echo character for this layout.
     *
     * @see #getEchoChar()
     * @see #echoCharIsSet()
     * @see jp.kyasu.graphics.EchoTextScanner
     * @see jp.kyasu.awt.TextField#setEchoChar(char)
     */
    public synchronized TextLayoutChange setEchoChar(char c) {
	if (echoChar == c)
	    return new TextLayoutChange(TextLayoutChange.NO_REPAINT);
	echoChar = c;
	invalidate();
	validate();
	return new TextLayoutChange(TextLayoutChange.FULL_REPAINT);
    }

    /**
     * Checks if this layout has a character set for echoing.
     *
     * @see #getEchoChar()
     * @see #setEchoChar(char)
     * @see jp.kyasu.graphics.EchoTextScanner
     * @see jp.kyasu.awt.TextField#echoCharIsSet()
     */
    public boolean echoCharIsSet() {
	return echoChar != 0;
    }

    /**
     * Tests if the line height is constant.
     */
    public boolean isLineHeightConstant() {
	return lineHeightConstant;
    }

    /**
     * Sets the line height is constant.
     */
    public void setLineHeightConstant(boolean b) {
	lineHeightConstant = b;
	if (lineHeightConstant && !rtStyle.variableLineHeight) {
	    FontMetrics fm = rtStyle.textStyle.getFontMetrics();
	    lineHeight = fm.getHeight();
	    baseline   = fm.getDescent();
	}
    }

    /**
     * Returns the line wrapping style.
     *
     * @see jp.kyasu.graphics.RichTextStyle#CHAR_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#WORD_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#NO_WRAP
     */
    public final int getLineWrap() {
	return lineWrap;
    }

    /**
     * Checks if the line wrapping is done at the character boundary.
     *
     * @see #getLineWrap()
     * @see jp.kyasu.graphics.RichTextStyle#CHAR_WRAP
     */
    public final boolean isCharWrap() {
	return lineWrap == RichTextStyle.CHAR_WRAP;
    }

    /**
     * Checks if the line wrapping is done at the word boundary.
     *
     * @see #getLineWrap()
     * @see jp.kyasu.graphics.RichTextStyle#WORD_WRAP
     */
    public final boolean isWordWrap() {
	return lineWrap == RichTextStyle.WORD_WRAP;
    }

    /**
     * Checks if the line wrapping is done at the line separator.
     *
     * @see #getLineWrap()
     * @see jp.kyasu.graphics.RichTextStyle#NO_WRAP
     */
    public final boolean isNoWrap() {
	return lineWrap == RichTextStyle.NO_WRAP;
    }

    /**
     * Sets the line wrapping style.
     */
    public synchronized TextLayoutChange setLineWrap(int lineWrap) {
	if (this.lineWrap == lineWrap)
	    return new TextLayoutChange(TextLayoutChange.NO_REPAINT);
	setLineWrapInner(lineWrap);
	invalidate();
	validate();
	return new TextLayoutChange(TextLayoutChange.FULL_REPAINT);
    }

    /**
     * Sets the line wrapping style.
     */
    protected void setLineWrapInner(int lineWrap) {
	switch (lineWrap) {
	case RichTextStyle.CHAR_WRAP:
	case RichTextStyle.WORD_WRAP:
	case RichTextStyle.NO_WRAP:
	    this.lineWrap = lineWrap;
	    return;
	}
	throw new IllegalArgumentException("improper lineWrap: " + lineWrap);
    }

    /**
     * Returns the preferred line increment size for the scroll of
     * this layout.
     *
     * @see jp.kyasu.awt.text.TextView#getPreferredSize(int, int)
     */
    public int getPreferredLineIncrementSize() {
	int height;
	if (!rtStyle.variableLineHeight && lineHeight > 0) {
	    height = lineHeight;
	}
	else {
	    TextStyle tstyle = rtStyle.textStyle;
		// (text.isEmpty()? rtStyle.textStyle: text.getTextStyleAt(0));
	    height = tstyle.getFontMetrics().getHeight();
	}
	return height + rtStyle.paragraphStyle.lineSpace;
    }

    /**
     * Returns the preferred character increment size for the scroll of
     * this layout.
     *
     * @see jp.kyasu.awt.text.TextView#getPreferredSize(int, int)
     */
    public int getPreferredCharIncrementSize() {
	TextStyle tstyle = rtStyle.textStyle;
		// (text.isEmpty()? rtStyle.textStyle: text.getTextStyleAt(0));
	FontMetrics fm = tstyle.getFontMetrics();
	//return fm.charWidth(' ');
	return (fm.charWidth('a') + fm.charWidth('A')) / 2;
    }

    /**
     * Ensures that a component has a valid layout.
     *
     * @see jp.kyasu.graphics.TextLines#isValid()
     * @see #invalidate()
     */
    public synchronized void validate() {
	if (!isValid()) {
	    if (preferredLayoutWidth > 0) {
		doLayout(preferredLayoutWidth);
	    }
	    else if (isNoWrap()) {
		doLayout(1);
	    }
	    else {
		doLayout(Integer.MAX_VALUE);
	    }
	}
    }

    /**
     * Invalidates the layout. The layout is marked as needing to be laid out.
     *
     * @see jp.kyasu.graphics.TextLines#isValid()
     * @see #validate()
     */
    public synchronized void invalidate() {
	invalidateLines();
    }

    /**
     * Resizes the layout to the specified width. The height of the layout
     * is automatically computed.
     *
     * @param width the width of the layout.
     * @see   #getSize()
     */
    public synchronized void setWidth(int width) {
	if (width <= 0)
	    throw new IllegalArgumentException("improper width: " + width);
	if (!isValid()) {
	    doLayout(width);
	}
	else if (preferredLayoutWidth != width) {
	    redoLayout(width);
	}
    }

    /**
     * Returns the advance width of a character in the specified text
     * position.
     *
     * @param  posInfo the text position for a character to be measured.
     * @return the advance width of the character in the specified text
     *         position.
     */
    public int charWidthAt(TextPositionInfo posInfo) {
	return getScanner().charWidthAt(posInfo.textIndex,
					posInfo.x,
					layoutWidth,
					posInfo,
					posInfo.paragraphStyle);
    }

    /**
     * Returns the text position information at the specified index of the
     * text.
     *
     * @param  textIndex the index of the text to search for.
     * @return the text position information; or <code>null</code>,
     *         if this layout does not have a valid layout.
     */
    public synchronized TextPositionInfo getTextPositionAt(int textIndex) {
	TextPositionInfo posInfo =
			getIncompleteTextPositionNearby(null, textIndex);
	if (posInfo == null)
	    return null;
	int idxAndX[] = getScanner().linePositionAt(textIndex,
						    posInfo,
						    posInfo.paragraphStyle);
	posInfo.textIndex = idxAndX[0];
	posInfo.x = idxAndX[1];
	return posInfo;
    }

    /**
     * Returns the text position information at the specified location in the
     * layout.
     *
     * @param  point the location to search for.
     * @return the text position information; or <code>null</code>,
     *         if this layout does not have a valid layout.
     */
    public synchronized TextPositionInfo getTextPositionAt(Point point) {
	TextPositionInfo posInfo =
			getIncompleteTextPositionNearby(null, point);
	if (posInfo == null)
	    return null;
	int idxAndX[] = getScanner().linePositionAt(point,
						    posInfo,
						    posInfo.paragraphStyle);
	posInfo.textIndex = idxAndX[0];
	posInfo.x = idxAndX[1];
	return posInfo;
    }

    /**
     * Returns the text position information nearby the specfied text
     * position information at the specified index of the text.
     *
     * @param  posInfo   the text position information used as the
     *                   starting point for searching. If null, the
     *                   searching starts from the beginning or ending
     *                   of the layout according to the index of the text.
     * @param  textIndex the index of the text to search for.
     * @return the text position information.
     */
    public synchronized TextPositionInfo getTextPositionNearby(
						TextPositionInfo posInfo,
						int textIndex)
    {
	assert(isValid());
	if (posInfo != null && posInfo.textIndex == textIndex)
	    return posInfo;
	posInfo = getIncompleteTextPositionNearby(posInfo, textIndex);
	assert(posInfo != null);
	int idxAndX[] = getScanner().linePositionAt(textIndex,
						    posInfo,
						    posInfo.paragraphStyle);
	posInfo.textIndex = idxAndX[0];
	posInfo.x = idxAndX[1];
	return posInfo;
    }

    /**
     * Returns the text position information nearby the specfied text
     * position information at the specified location in the layout.
     *
     * @param  posInfo the text position information used as the starting
     *                 point for searching. If null, the searching starts
     *                 from the top or bottom of the layout according to
     *                 the location.
     * @param  point   the location to search for.
     * @return the text position information.
     */
    public synchronized TextPositionInfo getTextPositionNearby(
						TextPositionInfo posInfo,
						Point point)
    {
	assert(isValid());
	posInfo = getIncompleteTextPositionNearby(posInfo, point);
	assert(posInfo != null);
	int idxAndX[] = getScanner().linePositionAt(point,
						    posInfo,
						    posInfo.paragraphStyle);
	posInfo.textIndex = idxAndX[0];
	posInfo.x = idxAndX[1];
	return posInfo;
    }

    /**
     * Returns the text position information next to the specfied text
     * position information.
     *
     * @param  posInfo the specified text position information.
     * @return the text position information next to the specified text
     *         position information.
     */
    public synchronized TextPositionInfo getTextPositionNextTo(
						TextPositionInfo posInfo)
    {
	assert(isValid());

	int textLength = richText.length();
	if (posInfo.textIndex >= textLength)
	    return posInfo;
	if (posInfo.lineIndex >= getLineCount() - 1) { // last line
	    // fall through
	}
	else {
	    int lineEnd = getLineBeginAt(posInfo.lineIndex + 1);
	    if (posInfo.textIndex + 1 < lineEnd) {
		// fall through
	    }
	    else {
		return getTextPositionAtLineBegin(posInfo.lineIndex + 1);
	    }
	}
	TextPositionInfo nextInfo = new TextPositionInfo(posInfo);
	nextInfo.textIndex += 1;
	nextInfo.x += charWidthAt(posInfo);
	return nextInfo;
    }

    /**
     * Returns the text position information previous to the specfied text
     * position information.
     *
     * @param  posInfo the specified text position information.
     * @return the text position information previous to the specified text
     *         position information.
     */
    public synchronized TextPositionInfo getTextPositionPrevTo(
						TextPositionInfo posInfo)
    {
	assert(isValid());

	if (posInfo.textIndex > 0)
	    return getTextPositionNearby(posInfo, posInfo.textIndex - 1);
	else
	    return posInfo;
    }

    /**
     * Returns the text position information at the beginning of the line
     * that is laid over the specified y position.
     *
     * @param  y the y position to search for.
     * @return the text position information; or <code>null</code>,
     *         if this layout does not have a valid layout.
     * @see    #getLineBeginPositionOver(jp.kyasu.graphics.text.TextPositionInfo, int)
     */
    public TextPositionInfo getLineBeginPositionOver(int y) {
	return getLineBeginPositionOver(null, y);
    }

    /**
     * Returns the text position information at the beginning of the line
     * that is laid over the specified y position.
     *
     * @param  posInfo the text position information used as the starting
     *                 point for searching. If null, the searching starts
     *                 from the top or bottom of the layout according to
     *                 the location.
     * @param  y       the y position to search for.
     * @return the text position information; or <code>null</code>,
     *         if this layout does not have a valid layout.
     */
    public synchronized TextPositionInfo getLineBeginPositionOver(
						TextPositionInfo posInfo,
						int y)
    {
	//if (!isValid()) return null;
	validate();
	int lineIndex = getLineIndexNearby(posInfo, new Point(0, y));
	return getTextPositionAtLineBegin(lineIndex);
    }

    /**
     * Returns the text position information at the beginning of the line
     * that is laid under the specified y position. If the specified y
     * position is greater than the height of this layout, returns
     * the text position information at the end of the layout.
     *
     * @param  y the y position to search for.
     * @return the text position information; or <code>null</code>,
     *         if this layout does not have a valid layout.
     * @see    #getLineBeginPositionUnder(jp.kyasu.graphics.text.TextPositionInfo, int)
     */
    public TextPositionInfo getLineBeginPositionUnder(int y) {
	return getLineBeginPositionUnder(null, y);
    }

    /**
     * Returns the text position information at the beginning of the line
     * that is laid under the specified y position. If the specified y
     * position is greater than the height of this layout, returns the
     * text position information at the end of the layout.
     *
     * @param  posInfo the text position information used as the starting
     *                 point for searching. If null, the searching starts
     *                 from the top or bottom of the layout according to
     *                 the location.
     * @param  y       the y position to search for.
     * @return the text position information; or <code>null</code>,
     *         if this layout does not have a valid layout.
     */
    public synchronized TextPositionInfo getLineBeginPositionUnder(
						TextPositionInfo posInfo,
						int y)
    {
	//if (!isValid()) return null;
	validate();
	int lineIndex = getLineIndexNearby(posInfo, new Point(0, y));
	if (lineIndex < getLineCount() - 1)
	    return getTextPositionAtLineBegin(lineIndex + 1);

	posInfo = getTextPositionAtLineBegin(lineIndex);
	int idxAndX[] = getScanner().linePositionAt(richText.length(),
						    posInfo,
						    posInfo.paragraphStyle);
	posInfo.textIndex = idxAndX[0];
	posInfo.x = idxAndX[1];
	return posInfo;
    }

    /**
     * Returns the text position information from the specfied text index
     * and line index.
     */
    protected TextPositionInfo getTextPositionAt(int textIndex, int lineIndex)
    {
	assert(isValid());
	TextPositionInfo posInfo =
			getIncompleteTextPositionAt(textIndex, lineIndex);
	assert(posInfo != null);
	int idxAndX[] = getScanner().linePositionAt(textIndex,
						    posInfo,
						    posInfo.paragraphStyle);
	posInfo.textIndex = idxAndX[0];
	posInfo.x = idxAndX[1];
	return posInfo;
    }


    /**
     * Lays out the rich text with the specified width. The layout
     * should not have a valid layout.
     */
    protected synchronized void doLayout(int width) {
	assert(!isValid());
	assert(width > 0);

	preferredLayoutWidth = width;

	if (richText.isEmpty()) {
	    Dimension d = doLayoutForEmpty(width, lineWrap);
	    layoutWidth = d.width;
	    layoutHeight = d.height;
	    return;
	}

	validateLines();

	int index = 0;
	int newWidth = 0;
	int height = 0;
	if (!lineHeightConstant) {
	    lineHeight = baseline = 0;
	}
	TextScanner scanner = getScanner();
	TextLineInfo info = new TextLineInfo();
	ParagraphStyle pStyle = richText.getParagraphStyleAt(index);
	int nextTop;
	while ((nextTop =
		scanner.doLayoutLine(index, width, height, pStyle, info)) >= 0)
	{
	    append(info);
	    height = nextTop;
	    index = info.lineEnd;
	    if (scanner.destX > newWidth) newWidth = scanner.destX;
	    if (!rtStyle.variableLineHeight) {
		if (!lineHeightConstant) {
		    if (info.lineHeight > lineHeight)
			lineHeight = info.lineHeight;
		    if (info.baseline > baseline)
			baseline = info.baseline;
		}
	    }
	    else {
		if (info.isParagraphMark) {
		    pStyle = richText.getParagraphStyleAt(index);
		}
	    }
	}
	if (scanner.lastCondition == TextScanner.LINE_SEPARATOR ||
	    scanner.lastCondition == TextScanner.LINE_BREAK)
	{
	    height += duplicateLastLine(width, height,
				scanner.lastCondition==TextScanner.LINE_BREAK);
	}
	if (isNoWrap()) {
	    if (newWidth > width) {
		changeRemainWidths(newWidth - width);
		layoutWidth = newWidth;
	    }
	    else {
		layoutWidth = width;
	    }
	}
	else if (newWidth > width) {
	    layoutWidth = width; // ignore newWidth
	}
	else {
	    layoutWidth = width;
	}
	if (!rtStyle.variableLineHeight) {
	    layoutHeight = getLineCount() *
				(lineHeight + rtStyle.paragraphStyle.lineSpace);
	}
	else {
	    layoutHeight = height;
	}
	assert(isParagraphMarkAt(getLineCount() - 1));
    }

    /**
     * Re-lays out this layout with the specified width. The layout should
     * be a valid layout.
     */
    protected synchronized void redoLayout(int width) {
	assert(isValid());
	assert(width > 0);
	assert(layoutWidth > 0);

	preferredLayoutWidth = width;

	if (richText.isEmpty()) {
	    Dimension d = doLayoutForEmpty(width, lineWrap);
	    layoutWidth = d.width;
	    layoutHeight = d.height;
	    return;
	}

	int index = 0;
	int textLength = richText.length();
	TextScanner scanner = getScanner();
	TextLineInfo info = new TextLineInfo();

	if (!rtStyle.variableLineHeight) {
	    int newWidth = 0;
	    int oldBegins[] = (int[])lineBegins.getArray();
	    int oldRemains[] = (int[])remainWidths.getArray();
	    int oldLineIndex = 0;
	    int oldLineCount = getLineCount();
	    invalidateLines();
	    validateLines();
	    int diff = width - layoutWidth;
	outer1:
	    for (;;) {
		if (oldLineIndex >= oldLineCount - 2) {
		    // fall through to scan last two lines.
		}
		else { // oldLineIndex < oldLineCount - 2
		    if (isParagraphMark(oldBegins[oldLineIndex])) {
			int nrw = oldRemains[oldLineIndex] + diff;
			if (nrw >= 0) {
			    lineBegins.append(oldBegins[oldLineIndex]);
			    remainWidths.append(nrw);
			    if ((width - nrw) > newWidth)
				newWidth = (width - nrw);
			    index = unmarkLineBegin(oldBegins[++oldLineIndex]);
			    continue outer1;
			}
			++oldLineIndex;
		    }
		    else {
			while (++oldLineIndex < oldLineCount) {
			    if (isParagraphMark(oldBegins[oldLineIndex])) {
				++oldLineIndex;
				break;
			    }
			}
		    }
		}
		while (scanner.doLayoutLine(
			index, width, 0, rtStyle.paragraphStyle, info) >= 0)
		{
		    append(info);
		    index = info.lineEnd;
		    if (scanner.destX > newWidth) newWidth = scanner.destX;
		    if (info.isParagraphMark) {
			if (index >= textLength)
			    break outer1;
			else
			    continue outer1;
		    }
		}
		break outer1;
	    }
	    if (scanner.lastCondition == TextScanner.LINE_SEPARATOR ||
		scanner.lastCondition == TextScanner.LINE_BREAK)
	    {
		duplicateLastLine(width, 0,
				scanner.lastCondition==TextScanner.LINE_BREAK);
	    }
	    if (isNoWrap()) {
		if (newWidth > width) {
		    changeRemainWidths(newWidth - width);
		    layoutWidth = newWidth;
		}
		else {
		    layoutWidth = width;
		}
	    }
	    else if (newWidth > width) {
		layoutWidth = width; // ignore newWidth;
	    }
	    else {
		layoutWidth = width;
	    }
	    layoutHeight = getLineCount() *
				(lineHeight + rtStyle.paragraphStyle.lineSpace);
	    assert(isParagraphMarkAt(getLineCount() - 1));
	}
	else {
	    int newWidth = 0;
	    int height = 0;
	    int oldBegins[] = (int[])lineBegins.getArray();
	    int oldLineIndex = 0;
	    int oldLineCount = getLineCount();
	    invalidateLines();
	    validateLines();
	outer2:
	    for (;;) {
		ParagraphStyle pStyle = null;
		while (oldLineIndex < oldLineCount) {
		    if (isParagraphMark(oldBegins[oldLineIndex])) {
			pStyle = richText.getParagraphStyleAt(
				unmarkLineBegin(oldBegins[oldLineIndex]));
			oldLineIndex++;
			break;
		    }
		    oldLineIndex++;
		}
		int nextTop;
		while ((nextTop = scanner.doLayoutLine(
				index, width, height, pStyle, info)) >= 0)
		{
		    append(info);
		    height = nextTop;
		    index = info.lineEnd;
		    if (scanner.destX > newWidth) newWidth = scanner.destX;
		    if (info.isParagraphMark) {
			if (index >= textLength)
			    break outer2;
			else
			    continue outer2;
		    }
		}
		break outer2;
	    }
	    if (scanner.lastCondition == TextScanner.LINE_SEPARATOR ||
		scanner.lastCondition == TextScanner.LINE_BREAK)
	    {
		height += duplicateLastLine(width, height,
			scanner.lastCondition == TextScanner.LINE_BREAK);
	    }
	    if (isNoWrap()) {
		if (newWidth > width) {
		    changeRemainWidths(newWidth - width);
		    layoutWidth = newWidth;
		}
		else {
		    layoutWidth = width;
		}
	    }
	    else if (newWidth > width) {
		layoutWidth = width; // ignore newWidth
	    }
	    else {
		layoutWidth = width;
	    }
	    layoutHeight = height;
	    assert(isParagraphMarkAt(getLineCount() - 1));
	}
    }

    /**
     * Draws the specified range of this layout to the specified graphics
     * at the specified location.
     *
     * @param g            the graphics.
     * @param offset       the offset position to draw
     * @param begin        the beginning index of this layout to draw,
     *                     inclusive.
     * @param end          the ending index of this layout to draw,
     *                     exclusive.
     */
    public void draw(Graphics g, Point offset,
		     TextPositionInfo begin, TextPositionInfo end)
    {
	draw(g, offset, begin, end, null, false, false, false);
    }

    /**
     * Draws the specified range of this layout to the specified graphics
     * at the specified location, with the specified background color.
     *
     * @param g            the graphics.
     * @param offset       the offset position to draw
     * @param begin        the beginning index of this layout to draw,
     *                     inclusive.
     * @param end          the ending index of this layout to draw,
     *                     exclusive.
     * @param bgColor      the background color.
     */
    public void draw(Graphics g, Point offset,
		     TextPositionInfo begin, TextPositionInfo end,
		     Color bgColor)
    {
	draw(g, offset, begin, end, bgColor, false, false, false);
    }

    /**
     * Draws the specified range of this layout to the specified
     * graphics at the specified location, with the specified background
     * color and various flags.
     *
     * @param g            the graphics.
     * @param offset       the offset position to draw
     * @param begin        the beginning index of this layout to draw,
     *                     inclusive.
     * @param end          the ending index of this layout to draw,
     *                     exclusive.
     * @param bgColor      the background color.
     * @param bgFromBegin  if true, fills the background from the beginning
     *                     of the line at the <code>begin</code> index.
     * @param bgToEnd      if true, fills the background to the ending of
     *                     the line at the <code>end</code> index.
     */
    public void draw(Graphics g, Point offset,
		     TextPositionInfo begin, TextPositionInfo end,
		     Color bgColor,
		     boolean bgFromBegin, boolean bgToEnd)
    {
	draw(g, offset, begin, end, bgColor, bgFromBegin, bgToEnd, false);
    }

    /**
     * Draws the specified range of this layout to the specified
     * graphics at the specified location, with the specified background
     * color and various flags.
     *
     * @param g            the graphics.
     * @param offset       the offset position to draw
     * @param begin        the beginning index of this layout to draw,
     *                     inclusive.
     * @param end          the ending index of this layout to draw,
     *                     exclusive.
     * @param bgColor      the background color.
     * @param bgFromBegin  if true, fills the background from the beginning
     *                     of the line at the <code>begin</code> index.
     * @param bgToEnd      if true, fills the background to the ending of
     *                     the line at the <code>end</code> index.
     * @param ignoreIndent if true, fills the background ignoring the margin
     *                     indentation.
     */
    public void draw(Graphics g, Point offset,
		     TextPositionInfo begin, TextPositionInfo end,
		     Color bgColor,
		     boolean bgFromBegin, boolean bgToEnd,
		     boolean ignoreIndent)
    {
	draw(g, offset, begin, end, bgColor, bgFromBegin, bgToEnd,
	     ignoreIndent, layoutWidth);
    }

    /**
     * Draws the specified range of this layout to the specified
     * graphics at the specified location, with the specified background
     * color, various flags and width for drawing.
     *
     * @param g            the graphics.
     * @param offset       the offset position to draw
     * @param begin        the beginning index of this layout to draw,
     *                     inclusive.
     * @param end          the ending index of this layout to draw,
     *                     exclusive.
     * @param bgColor      the background color.
     * @param bgFromBegin  if true, fills the background from the beginning
     *                     of the line at the <code>begin</code> index.
     * @param bgToEnd      if true, fills the background to the ending of
     *                     the line at the <code>end</code> index.
     * @param ignoreIndent if true, fills the background ignoring the margin
     *                     indentation.
     * @param layoutWidth  the width for drawing.
     */
    public void draw(Graphics g, Point offset,
		     TextPositionInfo begin, TextPositionInfo end,
		     Color bgColor,
		     boolean bgFromBegin, boolean bgToEnd,
		     boolean ignoreIndent,
		     int layoutWidth)
    {
	assert(isValid());

	Graphics bg = null;
	if (bgColor != null) {
	    bg = g.create();
	    bg.setColor(bgColor);
	}
	Point p = new Point(offset);
	TextScanner scanner = getScanner(g);
	p.y += begin.y;
	int endLineIndex = end.lineIndex;
	int height = begin.lineSkip;

	if (begin.lineIndex == endLineIndex) {
	    if (bg != null) {
		int leftIndent, rightIndent;
		if (ignoreIndent)
		    leftIndent = rightIndent = 0;
		else {
		    leftIndent = begin.paragraphStyle.leftIndent;
		    rightIndent = begin.paragraphStyle.rightIndent;
		}
		if (bgFromBegin) {
		    if (bgToEnd)
			bg.fillRect(leftIndent + p.x, p.y,
				    layoutWidth - rightIndent - leftIndent,
				    height);
		    else
			bg.fillRect(leftIndent + p.x, p.y,
				    end.x - leftIndent,
				    height);
		}
		else {
		    if (bgToEnd)
			bg.fillRect(begin.x + p.x, p.y,
				    layoutWidth - rightIndent - begin.x,
				    height);
		    else
			bg.fillRect(begin.x + p.x, p.y,
				    end.x - begin.x,
				    height);
		}
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
	    if (bg != null) bg.dispose();
	    return;
	}

	if (bg != null) {
	    int leftIndent, rightIndent;
	    if (ignoreIndent)
		leftIndent = rightIndent = 0;
	    else {
		leftIndent = begin.paragraphStyle.leftIndent;
		rightIndent = begin.paragraphStyle.rightIndent;
	    }
	    if (bgFromBegin) {
		bg.fillRect(leftIndent + p.x, p.y,
			    layoutWidth - rightIndent - leftIndent,
			    height);
	    }
	    else {
		bg.fillRect(begin.x + p.x, p.y,
			    layoutWidth - rightIndent - begin.x,
			    height);
	    }
	}
	if (rtStyle.variableLineHeight && begin.paragraphStyle.hasHeading() &&
	    (begin.lineIndex == 0 || isParagraphMarkAt(begin.lineIndex - 1)))
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
	p.y += height;

	if (!rtStyle.variableLineHeight) {
	    int leftIndent, rightIndent;
	    if (ignoreIndent)
		leftIndent = rightIndent = 0;
	    else {
		leftIndent = rtStyle.paragraphStyle.leftIndent;
		rightIndent = rtStyle.paragraphStyle.rightIndent;
	    }
	    int lBegins[] = (int[])lineBegins.getArray();
	    int lineBegin = unmarkLineBegin(lBegins[begin.lineIndex + 1]);
	    for (int i = begin.lineIndex + 1; i < endLineIndex; i++) {
		int lineEnd = unmarkLineBegin(lBegins[i + 1]);
		if (bg != null) {
		    bg.fillRect(leftIndent + p.x, p.y,
				layoutWidth - rightIndent - leftIndent,
				lineHeight + rtStyle.paragraphStyle.lineSpace);
		}
		scanner.drawLineTo(g, p,
				   lineBegin, lineEnd,
				   remainWidths.getInt(i),
				   lineHeight, baseline,
				   rtStyle.paragraphStyle);
		p.y += (lineHeight + rtStyle.paragraphStyle.lineSpace);
		lineBegin = lineEnd;
	    }
	    assert(lineBegin == end.lineBegin);
	    assert(p.y == end.y + offset.y);
	    if (bg != null) {
		if (bgToEnd) {
		    bg.fillRect(leftIndent + p.x, p.y,
				layoutWidth - rightIndent - leftIndent,
				lineHeight + rtStyle.paragraphStyle.lineSpace);
		}
		else {
		    bg.fillRect(leftIndent + p.x, p.y,
				end.x - leftIndent,
				lineHeight + rtStyle.paragraphStyle.lineSpace);
		}
	    }
	    scanner.drawLineTo(g, p, end.textIndex, end,
			       rtStyle.paragraphStyle);
	}
	else {
	    int lBegins[] = (int[])lineBegins.getArray();
	    int lTops[] = (int[])lineTops.getArray();
	    int leftIndent = 0;
	    int rightIndent = 0;
	    ParagraphStyle pStyle = null;
	    int lineBegin = unmarkLineBegin(lBegins[begin.lineIndex + 1]);
	    int lineTop = lTops[begin.lineIndex + 1];
	    for (int i = begin.lineIndex + 1; i < endLineIndex; i++) {
		int lineEnd = unmarkLineBegin(lBegins[i + 1]);
		int nextTop = lTops[i + 1];
		if (pStyle == null) {
		    pStyle = richText.getParagraphStyleAt(lineBegin);
		    if (!ignoreIndent) {
			leftIndent = pStyle.leftIndent;
			rightIndent = pStyle.rightIndent;
		    }
		}
		int lh = height = nextTop - lineTop;
		boolean isParaMark = isParagraphMark(lBegins[i]);
		if (isParaMark) {
		    lh -= pStyle.paragraphSpace;
		}
		if (bg != null) {
		    bg.fillRect(leftIndent + p.x, p.y,
				layoutWidth - rightIndent - leftIndent,
				height);
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

	    //assert(p.y == end.y + offset.y);

	    pStyle = end.paragraphStyle;
	    if (bg != null) {
		if (!ignoreIndent) {
		    leftIndent = pStyle.leftIndent;
		    rightIndent = pStyle.rightIndent;
		}
		height = end.lineSkip;
		if (bgToEnd) {
		    bg.fillRect(leftIndent + p.x, p.y,
				layoutWidth - rightIndent - leftIndent,
				height);
		}
		else {
		    bg.fillRect(leftIndent + p.x, p.y,
				end.x - leftIndent,
				height);
		}
	    }
	    if (pStyle.hasHeading() &&
		isParagraphMark(lBegins[end.lineIndex - 1]))
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

	if (bg != null) bg.dispose();
    }

    /**
     * Replaces the specified range of the rich text in this layout with
     * the specified replacement text object.
     *
     * @param  begin the beginning text position to replace, inclusive.
     * @param  end   the ending text position to replace, exclusive.
     * @param  rep   a replacement <code>Text</code> object.
     * @return the <code>TextLayoutChange</code> object that provides an
     *         information of changes in the layout made by this method.
     */
    public TextLayoutChange replace(TextPositionInfo begin,
				    TextPositionInfo end,
				    Text replacement)
    {
	assert(isValid());
	TextChange change = richText.replace(begin.textIndex,
					     end.textIndex,
					     replacement);
	return updateLayout(change, begin, end);
    }

    /**
     * Sets the text style in the specified range of the rich text in this
     * layout to be the specified text style.
     *
     * @param  begin     the beginning text position to set, inclusive.
     * @param  end       the ending text position to set, exclusive.
     * @param  textStyle the text style.
     * @return the <code>TextLayoutChange</code> object that provides an
     *         information of changes in the layout made by this method.
     */
    public TextLayoutChange setTextStyle(TextPositionInfo begin,
					 TextPositionInfo end,
					 TextStyle textStyle)
    {
	assert(isValid());
	TextChange change = richText.setTextStyle(begin.textIndex,
						  end.textIndex,
						  textStyle);
	return updateLayout(change, begin, end);
    }

    /**
     * Modifies the text style in the specified range of the rich text in
     * this layout by using the specified text style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the text style modifier.
     * @return the <code>TextLayoutChange</code> object that provides an
     *         information of changes in the layout made by this method.
     */
    public TextLayoutChange modifyTextStyle(TextPositionInfo begin,
					    TextPositionInfo end,
					    TextStyleModifier modifier)
    {
	assert(isValid());
	TextChange change = richText.modifyTextStyle(begin.textIndex,
						     end.textIndex,
						     modifier);
	return updateLayout(change, begin, end);
    }

    /**
     * Sets the paragraph style in the specified range of the rich text
     * in this layout to be the specified paragraph style.
     *
     * @param  begin  the beginning text position to set, inclusive.
     * @param  end    the ending text position to set, exclusive.
     * @param  pStyle the paragraph style.
     * @return the <code>TextLayoutChange</code> object that provides an
     *         information of changes in the layout made by this method.
     */
    public TextLayoutChange setParagraphStyle(TextPositionInfo begin,
					      TextPositionInfo end,
					      ParagraphStyle pStyle)
    {
	assert(isValid());
	TextChange change = richText.setParagraphStyle(begin.textIndex,
						       end.textIndex,	
						       pStyle);
	return updateLayout(change, begin, end);
    }

    /**
     * Modifies the paragraph style in the specified range of the rich text
     * in this layout by using the specified paragraph style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the paragraph style modifier.
     * @return the <code>TextLayoutChange</code> object that provides an
     *         information of changes in the layout made by this method.
     */
    public TextLayoutChange modifyParagraphStyle(
					TextPositionInfo begin,
					TextPositionInfo end,
					ParagraphStyleModifier modifier)
    {
	assert(isValid());
	TextChange change = richText.modifyParagraphStyle(begin.textIndex,
							  end.textIndex,
							  modifier);
	return updateLayout(change, begin, end);
    }

    /**
     * Updates this layout according to the specified <code>TextChange</code>
     * object.
     *
     * @param  change the <code>TextChange</code> object.
     * @return the <code>TextLayoutChange</code> object that provides an
     *         information of changes in the layout made by this method.
     */
    public TextLayoutChange updateLayout(TextChange change) {
	return updateLayout(change, null, null);
    }

    /**
     * Updates this layout according to the specified <code>TextChange</code>
     * object, with the position hints.
     *
     * @param  change    the <code>TextChange</code> object.
     * @param  beginHint the beginning hint text position.
     * @param  endHint   the ending hint text position.
     * @return the <code>TextLayoutChange</code> object that provides an
     *         information of changes in the layout made by this method.
     */
    public synchronized TextLayoutChange updateLayout(
						TextChange change,
						TextPositionInfo beginHint,
						TextPositionInfo endHint)
    {
	rtStyle = richText.getRichTextStyle(); // update

	assert(isValid());
	assert(!rtStyle.variableLineHeight ? true :
		richText.length() + 1 == richText.paragraphStyles.length());

	if (change.isNoLayout()) {
	    return new TextLayoutChange(TextLayoutChange.NO_REPAINT);
	}
	else if (change.isFullLayout()) {
	    invalidate();
	    validate();
	    return new TextLayoutChange(TextLayoutChange.FULL_REPAINT);
	}

	int begin  = change.layoutBegin;
	int oldEnd = change.layoutEnd;

	int newLength = richText.length();
	if (newLength == 0) {
	    invalidate();
	    validate();
	    return new TextLayoutChange(TextLayoutChange.FULL_REPAINT);
	}

	int diff = change.lengthChanged;
	int newEnd = oldEnd + diff;
	int oldLength = newLength - diff;

	int beginLineIndex = getLineIndexNearby(beginHint, begin, oldLength);
	int oldEndLineIndex = getLineIndexNearby(endHint,
						 Math.min(oldEnd, oldLength),
						 oldLength);
	int beginLineBegin = unmarkLineBegin(lineBegins.getInt(beginLineIndex));
	int validLineIndex = oldEndLineIndex + 1;
	int validLineBegin =
		(validLineIndex < getLineCount() ?
		    unmarkLineBegin(lineBegins.getInt(validLineIndex)) + diff :
		    newLength + 1); // never match to valid

	int lBeginLineIndex = beginLineIndex;
	if (rtStyle.isWordWrap() && lBeginLineIndex > 0 &&
	    !isParagraphMarkAt(lBeginLineIndex - 1))
	{
	    char c = richText.getChar(beginLineBegin - 1);
	    if (c != Text.LINE_BREAK_CHAR)
		--lBeginLineIndex;
	}

	TextScanner scanner = getScanner();
	TextLineInfo info = new TextLineInfo();

	if (!rtStyle.variableLineHeight) {
	    int newWidth = layoutWidth;
	    int newLineHeight = lineHeight;
	    int newBaseline = baseline;
	    int lBegins[] = (int[])lineBegins.getArray();
	    int lineCount = getLineCount();
	    int oldLineBegin = unmarkLineBegin(lBegins[beginLineIndex]);
	    int index = unmarkLineBegin(lBegins[lBeginLineIndex]);
	    TextLines changed = new TextLines(richText);
	    changed.validateLines();
	    int nextTop;
	    while ((nextTop = scanner.doLayoutLine(
	    	index, layoutWidth, 0, rtStyle.paragraphStyle, info)) >= 0)
	    {
		changed.append(info);
		index = info.lineEnd;
		if (info.lineHeight > newLineHeight)
		    newLineHeight = info.lineHeight;
		if (info.baseline > newBaseline) newBaseline = info.baseline;
		if (scanner.destX > newWidth) newWidth = scanner.destX;
		while (index > validLineBegin) {
		    if (++validLineIndex >= lineCount)
			break;
		    validLineBegin = unmarkLineBegin(lBegins[validLineIndex])
								+ diff;
		}
		if (validLineIndex > beginLineIndex && index == validLineBegin)
		    break;
	    }

	    if (nextTop < 0) { // scanned to the end
		if (scanner.lastCondition == TextScanner.LINE_SEPARATOR ||
		    scanner.lastCondition == TextScanner.LINE_BREAK)
		{
		    changed.duplicateLastLine(layoutWidth, 0,
				scanner.lastCondition==TextScanner.LINE_BREAK);
		}
		replace(lBeginLineIndex, lineCount, changed);
		if (changed.getLineCount() == 0) { // last chars removed
		    markLineAt(getLineCount() - 1);
		}
	    }
	    else {
		replace(lBeginLineIndex, validLineIndex, changed);
		validLineIndex = lBeginLineIndex + changed.getLineCount();
		lBegins = (int[])lineBegins.getArray();
		int newLineCount = getLineCount();
		for (int i = validLineIndex; i < newLineCount; i++) {
		    int val = lBegins[i];
		    lBegins[i] = markLineBegin(unmarkLineBegin(val) + diff,
					       val < 0); // isParagraphMark()
		}
	    }

	    int oldLayoutWidth = layoutWidth;
	    if (isNoWrap()) {
		assert(layoutWidth >= preferredLayoutWidth);
		if (newWidth > layoutWidth) {
		    changeRemainWidths(newWidth - layoutWidth);
		    layoutWidth = newWidth;
		}
		else {
		    int w = reComputeRemainWidths(layoutWidth);
		    if (w >= preferredLayoutWidth) {
			layoutWidth = w;
		    }
		    else {
			changeRemainWidths(preferredLayoutWidth - w);
			layoutWidth = preferredLayoutWidth;
		    }
		}
	    }
	    int newLineCount = getLineCount();
	    layoutHeight = newLineCount *
				(lineHeight + rtStyle.paragraphStyle.lineSpace);
	    if (!lineHeightConstant &&
		(newLineHeight > lineHeight || newBaseline > baseline))
	    {
		if (newLineHeight > lineHeight) lineHeight = newLineHeight;
		if (newBaseline > baseline) baseline = newBaseline;
		return new TextLayoutChange(TextLayoutChange.FULL_REPAINT);
	    }

	    TextPositionInfo paintBegin;
	    boolean paintFromLineBegin;
	    if (lBeginLineIndex < beginLineIndex &&
		(beginLineIndex >= newLineCount ||
		oldLineBegin != getLineBeginAt(beginLineIndex)))
	    {
		paintBegin = getTextPositionAtLineBegin(lBeginLineIndex);
		paintFromLineBegin = true;
	    }
	    else {
		boolean notLastLine = (beginLineIndex < newLineCount - 1);
		int lineEnd = (notLastLine ?
					getLineBeginAt(beginLineIndex + 1) :
					newLength);
		if (rtStyle.paragraphStyle.alignment != ParagraphStyle.LEFT) {
		    paintBegin =
			getTextPositionAtLineBegin(
				begin >= newLength ? // last chars removed
					newLineCount - 1 : beginLineIndex);
		    paintFromLineBegin = true;
		}
		else if (notLastLine && begin >= lineEnd) { // WORD WRAPED
		    paintBegin = getTextPositionAt(lineEnd - 1, beginLineIndex);
		    paintFromLineBegin = false;
		}
		else if (begin >= newLength) { // last chars removed
		    paintBegin = getTextPositionAt(newLength);
		    paintFromLineBegin = false;
		}
		else {
		    if (beginHint == null || beginHint.textIndex != begin) {
			paintBegin = getTextPositionAt(begin, beginLineIndex);
		    }
		    else {
			paintBegin = new TextPositionInfo(beginHint);
			paintBegin.lineEnd = lineEnd;
			paintBegin.remainWidth =
					remainWidths.getInt(beginLineIndex);
			paintBegin.isParagraphMark =
					isParagraphMarkAt(beginLineIndex);
		    }
		    paintFromLineBegin = false;
		}
	    }
	    TextPositionInfo paintEnd;
	    boolean paintToLineEnd;
	    if (nextTop < 0) { // scanned to the end
		paintEnd = getTextPositionAt(newLength);
		paintToLineEnd = true;
	    }
	    else {
		paintEnd = getTextPositionAtLineBegin(validLineIndex);
		paintToLineEnd = false;
	    }

	    assert(isParagraphMarkAt(getLineCount() - 1));

	    return new TextLayoutChange(
			paintBegin,
			paintEnd,
			paintFromLineBegin,
			paintToLineEnd,
			(newLineCount - lineCount) *
			    (lineHeight + rtStyle.paragraphStyle.lineSpace),
			layoutWidth - oldLayoutWidth);
	}

	int newWidth = layoutWidth;
	int lBegins[] = (int[])lineBegins.getArray();
	int lineCount = getLineCount();

	ParagraphStyle beginStyle = richText.getParagraphStyleAt(begin);
	int beginLineY = lineTops.getInt(beginLineIndex);
	int beginY = beginLineY;
	if (lBeginLineIndex < beginLineIndex) {
	    beginY = lineTops.getInt(lBeginLineIndex);
	}

	int newY = beginY;
	int oldLineBegin   = unmarkLineBegin(lBegins[beginLineIndex]);
	int oldBaseline    = baselines.getInt(beginLineIndex);
	int oldLineTop     = lineTops.getInt(beginLineIndex);
	int oldNextLineTop = (beginLineIndex < lineCount - 1 ?
					lineTops.getInt(beginLineIndex + 1) :
					layoutHeight);
	boolean oldIsParaMark = isParagraphMark(lBegins[beginLineIndex]);
	int index = unmarkLineBegin(lBegins[lBeginLineIndex]);
	TextLines changed = new TextLines(richText);
	changed.validateLines();
	ParagraphStyle pStyle = beginStyle;
	int nextTop;
	while ((nextTop =
	    scanner.doLayoutLine(index, layoutWidth, newY, pStyle, info)) >= 0)
	{
	    changed.append(info);
	    newY = nextTop;
	    index = info.lineEnd;
	    if (scanner.destX > newWidth) newWidth = scanner.destX;
	    while (index > validLineBegin) {
		if (++validLineIndex >= lineCount)
		    break;
		validLineBegin = unmarkLineBegin(lBegins[validLineIndex])+diff;
	    }
	    if (validLineIndex > beginLineIndex && index == validLineBegin)
		break;
	    if (info.isParagraphMark) {
		pStyle = richText.getParagraphStyleAt(index);
	    }
	}

	int oldY;
	if (nextTop < 0) { // scanned to the end
	    oldY = layoutHeight;
	    if (scanner.lastCondition == TextScanner.LINE_SEPARATOR ||
		scanner.lastCondition == TextScanner.LINE_BREAK)
	    {
		newY += changed.duplicateLastLine(layoutWidth, newY,
				scanner.lastCondition==TextScanner.LINE_BREAK);
	    }
	    replace(lBeginLineIndex, lineCount, changed);
	    if (changed.getLineCount() == 0) { // last chars removed
		markLineAt(getLineCount() - 1);
	    }
	}
	else {
	    if (validLineIndex >= lineCount) {
		oldY = layoutHeight;
	    }
	    else {
		oldY = lineTops.getInt(validLineIndex);
	    }
	    replace(lBeginLineIndex, validLineIndex, changed);
	    validLineIndex = lBeginLineIndex + changed.getLineCount();
	    lBegins = (int[])lineBegins.getArray();
	    int lTops[] = (int[])lineTops.getArray();
	    int topDiff = newY - oldY;
	    int newLineCount = getLineCount();
	    for (int i = validLineIndex; i < newLineCount; i++) {
		int val = lBegins[i];
		lBegins[i] = markLineBegin(unmarkLineBegin(val) + diff,
					   val < 0); // isParagraphMark()
		lTops[i] += topDiff;
	    }
	}

	int oldLayoutWidth  = layoutWidth;
	int oldLayoutHeight = layoutHeight;
	if (isNoWrap()) {
	    assert(layoutWidth >= preferredLayoutWidth);
	    if (newWidth > layoutWidth) {
		changeRemainWidths(newWidth - layoutWidth);
		layoutWidth = newWidth;
	    }
	    else {
		int w = reComputeRemainWidths(layoutWidth);
		if (w >= preferredLayoutWidth) {
		    layoutWidth = w;
		}
		else {
		    changeRemainWidths(preferredLayoutWidth - w);
		    layoutWidth = preferredLayoutWidth;
		}
	    }
	}
	layoutHeight += (newY - oldY);

	int newLineCount = getLineCount();
	TextPositionInfo paintBegin;
	boolean paintFromLineBegin;
	if (lBeginLineIndex < beginLineIndex &&
	    (beginLineIndex >= newLineCount ||
	    oldLineBegin != getLineBeginAt(beginLineIndex)))
	{
	    paintBegin = getTextPositionAtLineBegin(lBeginLineIndex);
	    paintFromLineBegin = true;
	}
	else {
	    boolean notLastLine = (beginLineIndex < newLineCount - 1);
	    int lineEnd = (notLastLine ?
				getLineBeginAt(beginLineIndex + 1) :
				newLength);
	    if (beginStyle.alignment != ParagraphStyle.LEFT) {
		paintBegin =
			getTextPositionAtLineBegin(
				begin >= newLength ? // last chars removed
					newLineCount - 1 : beginLineIndex);
		paintFromLineBegin = true;
	    }
	    else if (notLastLine && begin >= lineEnd) { // WORD WRAPED
		paintBegin = getTextPositionAt(lineEnd - 1, beginLineIndex);
		paintFromLineBegin = false;
	    }
	    else if (begin >= newLength) { // last chars removed
		paintBegin = getTextPositionAt(newLength);
		paintFromLineBegin = false;
	    }
	    else if (!change.paragraphStyleChanged                       &&
		     oldBaseline    == baselines.getInt(beginLineIndex)  &&
		     oldLineTop     == lineTops.getInt(beginLineIndex)   &&
		     oldNextLineTop == (beginLineIndex < newLineCount - 1 ?
					lineTops.getInt(beginLineIndex + 1) :
					layoutHeight)                    &&
		     oldIsParaMark  == isParagraphMarkAt(beginLineIndex))
	    {
		if (beginHint == null || beginHint.textIndex != begin) {
		    paintBegin = getTextPositionAt(begin, beginLineIndex);
		}
		else {
		    paintBegin = new TextPositionInfo(beginHint);
		    paintBegin.lineEnd = lineEnd;
		    paintBegin.remainWidth =
					remainWidths.getInt(beginLineIndex);
		}
		paintFromLineBegin = false;
	    }
	    else {
		paintBegin = getTextPositionAtLineBegin(beginLineIndex);
		paintFromLineBegin = true;
	    }
	}
	TextPositionInfo paintEnd;
	boolean paintToLineEnd;
	if (nextTop < 0) { // scanned to the end
	    paintEnd = getTextPositionAt(newLength);
	    paintToLineEnd = true;
	}
	else {
	    paintEnd = getTextPositionAtLineBegin(validLineIndex);
	    paintToLineEnd = false;
	}

	// If paragraph style was changed, paints from line begin to end.
	if (change.paragraphStyleChanged) {
	    paintFromLineBegin = true;
	    //paintToLineEnd     = true;
	}

	assert(isParagraphMarkAt(getLineCount() - 1));

	return new TextLayoutChange(paintBegin,
				    paintEnd,
				    paintFromLineBegin,
				    paintToLineEnd,
				    layoutHeight - oldLayoutHeight,
				    layoutWidth - oldLayoutWidth);
    }

    /** Returns the scanner for this layout. */
    protected TextScanner getScanner(Graphics g) {
	return getScanner();
    }

    /** Returns the scanner for this layout. */
    protected TextScanner getScanner() {
	return (echoCharIsSet() ?
		new EchoTextScanner(richText, lineWrap, echoChar, locale) :
		new TextScanner(richText, lineWrap, locale));
    }
}
