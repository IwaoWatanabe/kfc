/*
 * TextLines.java
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

import jp.kyasu.graphics.text.TextLineInfo;
import jp.kyasu.graphics.text.TextPositionInfo;
import jp.kyasu.util.RunArray;
import jp.kyasu.util.VArray;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;

/**
 * The <code>TextLines</code> class implements an information of the lines
 * into which the rich text is composed. This class is a super class of the
 * <code>TextLayout</code> class. An instance of this class can not be
 * created outside this package.
 *
 * @see 	jp.kyasu.graphics.TextLayout
 *
 * @version 	20 Nov 1999
 * @author 	Kazuki YASUMATSU
 */
public class TextLines implements java.io.Serializable {
    /** The rich text to be composed into lines. */
    protected RichText richText;

    /** The style for the rich text. */
    protected RichTextStyle rtStyle;

    /** The layout height. */
    protected int layoutHeight;

    /** The array for the beginning index of the line. */
    protected VArray lineBegins;

    /** The array for the remaining width of the line. */
    protected VArray remainWidths;

    /**
     * The array for the baseline of the line.
     * This value used when the <code>rtStyle.variableLineHeight</code>
     * is true.
     */
    protected VArray baselines;

    /**
     * The array for the top position of the line.
     * This value used when the <code>rtStyle.variableLineHeight</code>
     * is true.
     */
    protected VArray lineTops;

    /**
     * The height of all lines.
     * This value used when the <code>rtStyle.variableLineHeight</code>
     * is false.
     */
    protected int lineHeight;

    /**
     * The baseline of all lines.
     * This value used when the <code>rtStyle.variableLineHeight</code>
     * is false.
     */
    protected int baseline;


    /**
     * Constructs a text lines with the specified rich text.
     *
     * @param richText the rich text.
     */
    TextLines(RichText richText) {
	if (richText == null)
	    throw new NullPointerException();
	this.richText = richText;
	this.rtStyle  = richText.rtStyle;

	layoutHeight = 0;

	lineBegins   = null;
	remainWidths = null;
	baselines    = null;
	lineTops     = null;

	lineHeight = 0;
	baseline   = 0;
    }

    /**
     * Constructs a text lines that has the same contents as the
     * specified text lines.
     *
     * @param textLines the text lines.
     */
    TextLines(TextLines textLines) {
	if (textLines == null)
	    throw new NullPointerException();
	richText        = textLines.richText; // share
	rtStyle         = textLines.rtStyle; // share
	layoutHeight    = textLines.layoutHeight;
	lineBegins      = (VArray)textLines.lineBegins.clone();
	remainWidths    = (VArray)textLines.remainWidths.clone();
	if (rtStyle.variableLineHeight) {
	    baselines   = (VArray)textLines.baselines.clone();
	    lineTops    = (VArray)textLines.lineTops.clone();
	}
	else {
	    baselines   = null;
	    lineTops    = null;
	}
	lineHeight = textLines.lineHeight;
	baseline   = textLines.baseline;
    }


    /**
     * Returns the text to be composed into lines.
     */
    public final Text getText() {
	return richText.getText();
    }

    /**
     * Returns the rich text to be composed into lines.
     */
    public final RichText getRichText() {
	return richText;
    }

    /**
     * Returns the rich text style of this text lines.
     */
    public final RichTextStyle getRichTextStyle() {
	return rtStyle;
    }

    /**
     * Returns the number of the lines in this text lines.
     */
    public final int getLineCount() {
	return (lineBegins == null ? 0 : lineBegins.length());
    }

    /**
     * Returns the beginning index of the line at the specified index.
     */
    public final int getLineBeginAt(int lineIndex) {
	return unmarkLineBegin(lineBegins.getInt(lineIndex));
    }

    /**
     * Returns the ending index of the line at the specified index.
     */
    public final int getLineEndAt(int lineIndex) {
	if (lineIndex < 0 || lineIndex >= lineBegins.length()) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	if (lineIndex < lineBegins.length() - 1) {
	    return unmarkLineBegin(lineBegins.getInt(lineIndex + 1));
	}
	else {
	    return richText.length();
	}
    }

    /**
     * Checks if the line at the specified index is a paragraph end.
     */
    public final boolean isParagraphMarkAt(int lineIndex) {
	return isParagraphMark(lineBegins.getInt(lineIndex));
    }

    /**
     * Returns the remaining width of the line at the specified index.
     */
    public final int getRemainWidthAt(int lineIndex) {
	return remainWidths.getInt(lineIndex);
    }

    /**
     * Returns the line skip space at the specified line index.
     */
    public final int getLineSkipAt(int lineIndex) {
	if (lineIndex < 0 || lineIndex >= getLineCount())
	    throw new ArrayIndexOutOfBoundsException();
	if (!rtStyle.variableLineHeight) {
	    return lineHeight + rtStyle.paragraphStyle.lineSpace;
	}
	else {
	    if (lineIndex < getLineCount() - 1) {
		return lineTops.getInt(lineIndex + 1) -
						lineTops.getInt(lineIndex);
	    }
	    else {
		return layoutHeight - lineTops.getInt(lineIndex);
	    }
	}
    }

    /**
     * Returns the baseline of the line at the specified index.
     */
    public final int getBaselineAt(int lineIndex) {
	if (!rtStyle.variableLineHeight) {
	    if (lineIndex < 0 || lineIndex >= getLineCount()) {
		throw new ArrayIndexOutOfBoundsException();
	    }
	    return baseline;
	}
	else {
	    return baselines.getInt(lineIndex);
	}
    }

    /**
     * Returns the top position of the line at the specified index.
     */
    public final int getLineTopAt(int lineIndex) {
	if (!rtStyle.variableLineHeight) {
	    if (lineIndex < 0 || lineIndex >= getLineCount()) {
		throw new ArrayIndexOutOfBoundsException();
	    }
	    return (lineHeight + rtStyle.paragraphStyle.lineSpace) * lineIndex;
	}
	else {
	    return lineTops.getInt(lineIndex);
	}
    }

    /**
     * Checks if the text lines has a valid layout.
     */
    public boolean isValid() {
	return (lineBegins != null);
    }

    /** Validate lines in this text lines. */
    protected void validateLines() {
	if (isValid())
	    return;
	lineBegins   = new VArray(int.class);
	remainWidths = new VArray(int.class);
	if (rtStyle.variableLineHeight) {
	    baselines = new VArray(int.class);
	    lineTops  = new VArray(int.class);
	}
    }

    /** Invalidate lines in this text lines. */
    protected void invalidateLines() {
	lineBegins   = null;
	baselines    = null;
	lineTops     = null;
	remainWidths = null;
    }

    /** Marks the line at the specified index as a paragraph end. */
    protected final void markLineAt(int lineIndex) {
	int lineBegin = lineBegins.getInt(lineIndex);
	if (!isParagraphMark(lineBegin)) {
	    lineBegins.setInt(lineIndex,
			      markLineBegin(unmarkLineBegin(lineBegin), true));
	}
    }

    /** Unmarks the line at the specified index as no paragraph end. */
    protected final void unmarkLineAt(int lineIndex) {
	int lineBegin = lineBegins.getInt(lineIndex);
	if (isParagraphMark(lineBegin)) {
	    lineBegins.setInt(lineIndex,
			      markLineBegin(unmarkLineBegin(lineBegin), false));
	}
    }

    /**
     * Returns the text line information for the line at the specified
     * line index.
     */
    public final TextLineInfo getTextLineAt(int lineIndex) {
	return getTextLineInto(lineIndex, new TextLineInfo());
    }

    /**
     * Sets the text line information for the line at the specified line
     * index into the specified text line information object, and
     * returns the text line information object set.
     */
    public final TextLineInfo getTextLineInto(int lineIndex,
					      TextLineInfo lineInfo)
    {
	int lBegin = lineBegins.getInt(lineIndex);
	lineInfo.isParagraphMark = isParagraphMark(lBegin);
	lineInfo.lineBegin = unmarkLineBegin(lBegin);
	lineInfo.lineEnd = (lineIndex < getLineCount() - 1 ?
				getLineBeginAt(lineIndex + 1) :
				richText.length());
	lineInfo.remainWidth = remainWidths.getInt(lineIndex);
	if (!rtStyle.variableLineHeight) {
	    lineInfo.lineHeight     = lineHeight;
	    lineInfo.baseline       = baseline;
	    lineInfo.lineSkip       = lineHeight +
					rtStyle.paragraphStyle.lineSpace;
	    lineInfo.paragraphStyle = rtStyle.paragraphStyle;
	    lineInfo.y              = lineInfo.lineSkip * lineIndex;
	}
	else {
	    lineInfo.baseline       = baselines.getInt(lineIndex);
	    lineInfo.paragraphStyle =
			richText.getParagraphStyleAt(lineInfo.lineBegin);
	    lineInfo.y              = lineTops.getInt(lineIndex);
	    int nextTop = (lineIndex < getLineCount() - 1 ?
				lineTops.getInt(lineIndex + 1) :
				layoutHeight);
	    lineInfo.lineSkip      = nextTop - lineInfo.y;
	    int lineHeight = lineInfo.lineSkip -
					lineInfo.paragraphStyle.lineSpace;
	    if (lineInfo.isParagraphMark) {
		lineHeight -= lineInfo.paragraphStyle.paragraphSpace;
	    }
	    lineInfo.lineHeight    = lineHeight;
	}
	return lineInfo;
    }

    /**
     * Returns the text position information at the beginning of the line
     * at the specified line index.
     *
     * @param  lineIndex the line index.
     * @return the text position information.
     */
    public final TextPositionInfo getTextPositionAtLineBegin(int lineIndex) {
	assert(isValid());

	TextPositionInfo posInfo = getIncompleteTextPositionAt(-1, lineIndex);
	int posX = posInfo.paragraphStyle.leftIndent;
	switch (posInfo.paragraphStyle.alignment) {
	case ParagraphStyle.RIGHT:
	    posX += posInfo.remainWidth;
	    break;
	case ParagraphStyle.CENTER:
	    posX += (posInfo.remainWidth / 2);
	    break;
	case ParagraphStyle.LEFT:
	default:
	    // do nothing
	    break;
	}
	posInfo.x = posX;
	return posInfo;
    }

    /**
     * Returns the incomplete text position information from the specified
     * text index and line index. If the text index is less than 0, then
     * the line begin index is used as the text index.
     */
    protected final TextPositionInfo getIncompleteTextPositionAt(int textIndex,
								 int lineIndex)
    {
	TextPositionInfo posInfo = new TextPositionInfo();
	getTextLineInto(lineIndex, posInfo);
	if (textIndex < 0) textIndex = posInfo.lineBegin;
	posInfo.textIndex = textIndex;
	posInfo.lineIndex = lineIndex;
	posInfo.x         = 0;
	return posInfo;
    }

    /**
     * Appends the line described in the specified text line information
     * to this text lines.
     */
    protected final void append(TextLineInfo lineInfo) {
	lineBegins.append(markLineBegin(lineInfo.lineBegin,
					lineInfo.isParagraphMark));
	remainWidths.append(lineInfo.remainWidth);
	if (rtStyle.variableLineHeight) {
	    baselines.append(lineInfo.baseline);
	    lineTops.append(lineInfo.y);
	}
    }

    /**
     * Appends the specified text lines to this text lines.
     */
    protected final void append(TextLines textLines) {
	lineBegins.append(textLines.lineBegins);
	remainWidths.append(textLines.remainWidths);
	if (rtStyle.variableLineHeight) {
	    baselines.append(textLines.baselines);
	    lineTops.append(textLines.lineTops);
	}
    }

    /**
     * Replace the lines of this text lines with the specified text lines.
     *
     * @param begin     the beginning index of the line to replace, inclusive.
     * @param end       the ending index of the line to replace, exclusive.
     * @param textLines a replacement <code>TextLines</code> object.
     */
    protected final void replace(int begin, int end, TextLines textLines) {
	lineBegins.replace(begin, end, textLines.lineBegins);
	remainWidths.replace(begin, end, textLines.remainWidths);
	if (rtStyle.variableLineHeight) {
	    baselines.replace(begin, end, textLines.baselines);
	    lineTops.replace(begin, end, textLines.lineTops);
	}
    }

    /**
     * Returns a new text lines that is a sublines of this text lines.
     * The sublines begins at the specified <code>beginIndex</code> of the
     * line and extends to the line at index <code>endIndex-1</code>.
     *
     * @param     beginIndex the beginning index of the line, inclusive.
     * @param     endIndex   the ending index of the line, exclusive.
     * @return    the sublines.
     */
    protected final TextLines sublines(int beginIndex, int endIndex) {
	TextLines subLines = new TextLines(richText);
	subLines.lineBegins = lineBegins.subarray(beginIndex, endIndex);
	subLines.remainWidths = remainWidths.subarray(beginIndex, endIndex);
	if (rtStyle.variableLineHeight) {
	    subLines.baselines = baselines.subarray(beginIndex, endIndex);
	    subLines.lineTops = lineTops.subarray(beginIndex, endIndex);
	}
	return subLines;
    }

    /**
     * Lays out the empty text lines with the specified width.
     *
     * @param  width    the width to lay out.
     * @param  lineWrap the line wrapping style.
     * @return the layout size.
     */
    protected final Dimension doLayoutForEmpty(int width, int lineWrap) {
	invalidateLines();
	validateLines();
	lineBegins.append(markLineBegin(0, true));
	if (lineWrap == RichTextStyle.NO_WRAP) {
	    /*
	    width = Math.max(1, rtStyle.paragraphStyle.leftIndent +
					rtStyle.paragraphStyle.rightIndent);
	    remainWidths.append(0);
	    */
	    remainWidths.append(width - rtStyle.paragraphStyle.leftIndent
					- rtStyle.paragraphStyle.rightIndent);
	}
	else {
	    remainWidths.append(width - rtStyle.paragraphStyle.leftIndent
					- rtStyle.paragraphStyle.rightIndent);
	}
	FontMetrics fm = rtStyle.textStyle.getFontMetrics();
	int height = fm.getHeight();
	if (!rtStyle.variableLineHeight) {
	    lineHeight = height;
	    baseline   = fm.getDescent();
	    height += rtStyle.paragraphStyle.lineSpace;
	}
	else {
	    baselines.append(fm.getDescent());
	    lineTops.append(0);
	    height += rtStyle.paragraphStyle.lineSpace +
				rtStyle.paragraphStyle.paragraphSpace;
	}
	return new Dimension(width, height);
    }

    /**
     * Changes the remaining widths of this text lines with the
     * specified difference of the layout width.
     */
    protected final void changeRemainWidths(int diff) {
	if (diff != 0) {
	    int rWidths[] = (int[])remainWidths.getArray();
	    int length = remainWidths.length();
	    for (int i = 0; i < length; i++) {
		rWidths[i] += diff;
	    }
	}
    }

    /**
     * Recomputes the remaining widths of this text lines with the
     * specified layout width.
     */
    protected final int reComputeRemainWidths(int width) {
	int rWidths[] = (int[])remainWidths.getArray();
	int length = remainWidths.length();
	int minRemainWidth = Integer.MAX_VALUE;
	for (int i = 0; i < length; i++) {
	    int remainWidth = rWidths[i];
	    if (remainWidth < minRemainWidth)
		minRemainWidth = remainWidth;
	}
	if (minRemainWidth > 0) {
	    changeRemainWidths(-minRemainWidth);
	    width -= minRemainWidth;
	}
	return width;
    }

    /**
     * Duplicates the last line, with the specified length of the text,
     * layout width, the paragraph style of the last line, and flag
     * indicating that the last line is ending with the line break character.
     */
    protected final int duplicateLastLine(int width, int lineTop,
					  boolean endWithBreak)
    {
	int textLength = richText.length();
	ParagraphStyle lastStyle = richText.getParagraphStyleAt(textLength);
	int last = getLineCount() - 1;
	lineBegins.append(markLineBegin(textLength, true));
	remainWidths.append(width - lastStyle.leftIndent
						- lastStyle.rightIndent);
	if (!rtStyle.variableLineHeight) {
	    return lineHeight + rtStyle.paragraphStyle.lineSpace;
	}
	else {
	    assert(lineTop >= 0);
	    FontMetrics fm = (endWithBreak && lastStyle.hasBaseStyle() ?
				lastStyle.getBaseStyle().getFontMetrics() :
				rtStyle.textStyle.getFontMetrics());
	    int height = fm.getHeight();
	    baselines.append(fm.getDescent());
	    lineTops.append(lineTop);
	    return height + lastStyle.lineSpace + lastStyle.paragraphSpace;
	}
    }

    /**
     * Returns the incomplete text position information nearby the
     * specfied text position information at the specified index of the
     * text.
     *
     * @param  posInfo      the text position information used as the
     *                      starting point for searching. If null, the
     *                      searching starts from the beginning or ending
     *                      of the layout according to the index of the text.
     * @param  textIndex    the index of the text to search for.
     * @return the incomplete text position information; or <code>null</code>,
     *         if this layout does not have a valid layout.
     */
    protected final TextPositionInfo getIncompleteTextPositionNearby(
						TextPositionInfo posInfo,
						int textIndex)
    {
	if (!isValid())
	    return null;

	return getIncompleteTextPositionAt(
		textIndex,
		getLineIndexNearby(posInfo, textIndex, richText.length()));
    }

    /**
     * Returns the incomplete text position information nearby the
     * specfied text position information at the specified location.
     *
     * @param  posInfo      the text position information used as the
     *                      starting point for searching. If null, the
     *                      searching starts from the top or bottom of
     *                      the layout according to the location.
     * @param  point        the location to search for.
     * @return the incomplete text position information; or <code>null</code>,
     *         if this layout does not have a valid layout.
     */
    protected final TextPositionInfo getIncompleteTextPositionNearby(
						TextPositionInfo posInfo,
						Point point)
    {
	if (!isValid())
	    return null;

	return getIncompleteTextPositionAt(-1,
					   getLineIndexNearby(posInfo, point));
    }

    /**
     * Returns the line index nearby the specfied text position information
     * at the specified index of the text.
     *
     * @param  posInfo      the text position information used as the
     *                      starting point for searching. If null, the
     *                      searching starts from the beginning or ending
     *                      of the layout according to the index of
     *                      the text.
     * @param  textIndex    the index of the text to search for.
     * @return the line index; or <code>-1</code> if this text lines does
     *         not have a valid layout.
     */
    public final int getLineIndexNearby(TextPositionInfo posInfo,
					int textIndex)
    {
	return getLineIndexNearby(posInfo, textIndex, richText.length());
    }

    /**
     * Returns the line index nearby the specfied text position information
     * at the specified index of the text.
     *
     * @param  posInfo      the text position information used as the
     *                      starting point for searching. If null, the
     *                      searching starts from the beginning or ending
     *                      of the layout according to the index of the text.
     * @param  textIndex    the index of the text to search for.
     * @param  textLength   the length of the text.
     * @return the line index; or <code>-1</code> if this text lines does
     *         not have a valid layout.
     */
    protected final int getLineIndexNearby(TextPositionInfo posInfo,
					   int textIndex,
					   int textLength)
    {
	if (!isValid())
	    return -1;

	if (textIndex < 0 || textIndex > textLength) {
	    throw new ArrayIndexOutOfBoundsException();
	}

	if (posInfo != null && posInfo.textIndex == textIndex)
	    return posInfo.lineIndex;

	int lineCount = getLineCount();
	assert(lineCount > 0);
	if (lineCount == 1) {
	    return 0;
	}
	// special case
	if (textIndex == textLength) {
	    return lineCount - 1;
	}

	int low;
	int high;

	if (posInfo == null) {
	    low = 0;
	    high = lineCount - 1;
	}
	else if (textIndex < posInfo.lineBegin) {
	    low = 0;
	    high = posInfo.lineIndex - 1;
	}
	else if (posInfo.lineEnd <= textIndex) {
	    low = posInfo.lineIndex + 1;
	    high = lineCount - 1;
	}
	else {
	    return posInfo.lineIndex;
	}

	int lBegins[] = (int[])lineBegins.getArray();
	while (low < high) {
	    int lEnd = unmarkLineBegin(lBegins[low + 1]);
	    if (textIndex < lEnd) {
		return low;
	    }
	    else if (++low == high) {
		break;
	    }
	    int hBegin = unmarkLineBegin(lBegins[high]);
	    if (hBegin <= textIndex) {
		return high;
	    }
	    else if (low == --high) {
		break;
	    }
	    int mid = (low + high) / 2;
	    int mBegin = unmarkLineBegin(lBegins[mid]);
	    if (textIndex < mBegin) {
		high = mid - 1;
	    }
	    else {
		low = mid;
	    }
	}
	assert(low == high);
	return low;
    }

    /**
     * Returns the line index nearby the specfied text position information
     * at the specified location.
     *
     * @param  posInfo      the text position information used as the
     *                      starting point for searching. If null, the
     *                      searching starts from the top or bottom of
     *                      the layout according to the location.
     * @param  point        the location to search for.
     * @return the line index; or <code>-1</code> if this text lines does
     *         not have a valid layout.
     */
    public final int getLineIndexNearby(TextPositionInfo posInfo,
					Point point)
    {
	if (!isValid())
	    return -1;

	int y = point.y;

	int lineCount = getLineCount();
	assert(lineCount > 0);

	if (!rtStyle.variableLineHeight) {
	    int lineIndex = y / (lineHeight + rtStyle.paragraphStyle.lineSpace);
	    if (lineIndex < 0)
		return 0;
	    else if (lineIndex >= lineCount)
		return lineCount - 1;
	    else
		return lineIndex;
	}

	if (lineCount == 1) {
	    return 0;
	}

	int low;
	int high;

	if (posInfo == null) {
	    low = 0;
	    high = lineCount - 1;
	}
	else if (y < posInfo.y) {
	    if (posInfo.lineIndex == 0) {
		return 0;
	    }
	    low = 0;
	    high = posInfo.lineIndex - 1;
	}
	else {
	    low = posInfo.lineIndex;
	    high = lineCount - 1;
	}

	int lTops[] = (int[])lineTops.getArray();
	while (low < high) {
	    int lBottom = lTops[low + 1];
	    if (y < lBottom) {
		return low;
	    }
	    else if (++low == high) {
		break;
	    }
	    int hTop = lTops[high];
	    if (hTop <= y) {
		return high;
	    }
	    else if (low == --high) {
		break;
	    }
	    int mid = (low + high) / 2;
	    int mTop = lTops[mid];
	    if (y < mTop) {
		high = mid - 1;
	    }
	    else {
		low = mid;
	    }
	}
	assert(low == high);
	return low;
    }

    /**
     * Encodes the specified beginning index of the line so that it is
     * marked as a paragraph end or not, according to the specified boolean.
     *
     * @see #unmarkLineBegin(int)
     * @see #isParagraphMark(int)
     */
    protected final int markLineBegin(int lineBegin, boolean isParagraphMark) {
	return (isParagraphMark ? -1 - lineBegin : lineBegin);
    }

    /**
     * Decodes the specified encoded beginning index of the line to
     * the beginning index of the line.
     *
     * @see #markLineBegin(int, boolean)
     * @see #isParagraphMark(int)
     */
    protected final int unmarkLineBegin(int lineBegin) {
	return (lineBegin < 0 ? -1 - lineBegin : lineBegin);
    }

    /**
     * Checks if the encoded beginning index of the line is marked as
     * a paragraph end.
     *
     * @see #markLineBegin(int, boolean)
     * @see #unmarkLineBegin(int)
     */
    protected final boolean isParagraphMark(int lineBegin) {
	return lineBegin < 0;
    }

    /**
     * Prints the debbuging information to the <code>System.out</code>.
     * This method is used for DEBUG.
     */
    public void printDebugInfo() {
	if (!isValid()) {
	    System.out.println("0: not valid");
	    return;
	}
	int lineCount = getLineCount();
	for (int i = 0; i < lineCount; i++) {
	    int lineBegin = lineBegins.getInt(i);
	    System.out.print(i + ": lineBegin=" + unmarkLineBegin(lineBegin) +
			     ",remainWidth=" + remainWidths.getInt(i));
	    if (!rtStyle.variableLineHeight) {
		if (isParagraphMark(lineBegin))
		    System.out.print(",paragraphMark");
	    }
	    else {
		System.out.print(",baseline=" + baselines.getInt(i) +
				 ",lineTop=" + lineTops.getInt(i));
		if (isParagraphMark(lineBegin)) {
		    ParagraphStyle pStyle = richText.getParagraphStyleAt(
						unmarkLineBegin(lineBegin));
		    System.out.print(",paragraphMark=" + pStyle.alignment);
		}
	    }
	    System.out.println();
	}
    }

    /** Asserts the given boolean to be <code>true</code>. */
    protected final void assert(boolean b) {
	if (!b) {
	    (new Exception("assertion failed")).printStackTrace();
	}
    }
}
