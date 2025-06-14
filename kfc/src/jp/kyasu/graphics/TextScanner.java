/*
 * TextScanner.java
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
import jp.kyasu.util.VArray;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.text.BreakIterator;
import java.util.Locale;

/**
 * The <code>TextScanner</code> class scans text to lay out text, to draw
 * text, and to compute the position of text.
 *
 * @version 	24 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextScanner {
    /** The text to be scanned. */
    protected Text text;

    /** The rich text style that knows a text wrapping mode. */
    protected RichTextStyle richTextStyle;

    /** The line wrapping style. */
    protected int lineWrap;

    /** The locale of the text that is used for word breaking. */
    protected Locale locale;

    /**
     * The current x position.
     */
    public int destX = 0;

    /**
     * The last index scanned.
     */
    public int lastIndex = 0;

    /**
     * The left margin for scanning.
     */
    public int leftMargin = 0;

    /**
     * The right margin (edge) for scanning.
     */
    public int rightMargin = 0;

    /**
     * The ending index in the text for scanning (exclusive).
     */
    public int runEnd = 0;

    /**
     * The beginning index of a line (inclusive).
     */
    public int lineBegin = 0;

    /**
     * The ending index of a line (exclusive).
     */
    public int lineEnd = 0;

    /**
     * The height of a line.
     */
    public int lineHeight = 0;

    /**
     * The baseline of a line.
     */
    public int baseline = 0;

    /**
     * The last stop condition scanned.
     */
    public int lastCondition = 0;

    /** The scanned text attachents. */
    protected VArray attachments;

    /** The current font metrics. */
    protected FontMetrics metrics = null;

    /** The widths of the first 256 characters from the current font metrics. */
    protected int widths[] = null;

    /** The current width of a tab. */
    protected int tabWidth = 0;

    /**
     * The character displayed instead of a character that is not
     * defined in the current font.
     */
    protected char   notInFontChar       = '\uffff';

    /**
     * The string representation of the <code>notInFontChar</code>.
     */
    protected String notInFontCharString = "\uffff";

    /** The current width of <code>notInFontChar</code>. */
    protected int notInFontCharWidth = 0;


    /**
     * The stop condition constant that shows the scanning runs to the end.
     */
    static public final int END_OF_RUN		= -1;

    /**
     * The stop condition constant that shows the scanning runs across the
     * the right edge.
     */
    static public final int CROSSED_X		= -2;

    /**
     * The stop condition constant that shows the character encountered
     * is a tab character.
     */
    static public final int TAB			= -3;

    /**
     * The stop condition constant that shows the character encountered
     * is a line separator.
     *
     * @see jp.kyasu.graphics.Text#LINE_SEPARATOR_CHAR
     */
    static public final int LINE_SEPARATOR	= -4;

    /**
     * The stop condition constant that shows the character encountered
     * is a line break.
     *
     * @see jp.kyasu.graphics.Text#LINE_BREAK_CHAR
     */
    static public final int LINE_BREAK		= -5;

    /**
     * The stop condition constant that shows the character encountered
     * is not defined in the current font.
     */
    static public final int CHAR_NOT_IN_FONT	= -6;

    /**
     * The stop condition constant that shows the character encountered
     * is an attachment mark.
     *
     * @see jp.kyasu.graphics.Text#ATTACHMENT_CHAR
     */
    static public final int ATTACHMENT		= -7;

    /**
     * The stop condition that shows the character encountered must be
     * ignored. This constant is not currently used.
     */
    static protected final int IGNORE		= -8;

    /**
     * The stop conditions for the first 256 characters. This constant is
     * used for scanning as is.
     */
    static public final int NO_STOPS[]         = new int[256];

    /**
     * The stop conditions for the first 256 characters. This constant is
     * used for ignoring the ANSI control characters.
     */
    static public final int SIMPLE_STOPS[]     = new int[256];

    /**
     * The stop conditions for the first 256 characters. This constant is
     * used for scanning the text without line break, i.e., scanning
     * '\f', '\n' (<code>Text.LINE_SEPARATOR_CHAR</code>) as
     * <code>LINE_SEPARATOR</code>.
     *
     * @see jp.kyasu.graphics.Text#LINE_SEPARATOR_CHAR
     * @see jp.kyasu.graphics.RichTextStyle#isJavaLineSeparator()
     */
    static public final int JAVA_STOPS[]       = new int[256];

    /**
     * The stop conditions for the first 256 characters. This constant is
     * used for scanning the text with line break, i.e., scanning
     * '\f', '\n' (<code>Text.LINE_SEPARATOR_CHAR</code>) as
     * <code>LINE_SEPARATOR</code>, and '\r'
     * (<code>Text.LINE_BREAK_CHAR</code>) as <code>LINE_BREAK</code>.
     *
     * @see jp.kyasu.graphics.Text#LINE_SEPARATOR_CHAR
     * @see jp.kyasu.graphics.Text#LINE_BREAK_CHAR
     * @see jp.kyasu.graphics.RichTextStyle#isJavaLineSeparatorWithBreak()
     */
    static public final int JAVA_BREAK_STOPS[] = new int[256];

    /**
     * The stop conditions for the first 256 characters. This constant is
     * used for scanning list items.
     *
     * @see jp.kyasu.graphics.Text#LIST_SEPARATOR_CHAR
     * @see jp.kyasu.graphics.RichTextStyle#isListSeparator()
     */
    static public final int LIST_STOPS[]       = new int[256];

    /**
     * The stop conditions for the first 256 characters. This constant is
     * not currently used.
     */
    static private final int SYSTEM_STOPS[]    = new int[256]; // not supported

    static {
	int i;

	for (i = 0; i < 256; i++) NO_STOPS[i] = 0;

	for (i = 0  ; i <= 31 ; i++) SIMPLE_STOPS[i] = CHAR_NOT_IN_FONT;
	for (i = 32 ; i <= 126; i++) SIMPLE_STOPS[i] = 0;
	//for (i = 127; i <= 159; i++) SIMPLE_STOPS[i] = CHAR_NOT_IN_FONT;
	for (i = 127; i <= 159; i++) SIMPLE_STOPS[i] = 0;
	for (i = 160; i <= 255; i++) SIMPLE_STOPS[i] = 0;

	System.arraycopy(SIMPLE_STOPS, 0, LIST_STOPS, 0, 256);
	LIST_STOPS[(int)'\t']                     = TAB;
	LIST_STOPS[(int)Text.LIST_SEPARATOR_CHAR] = LINE_SEPARATOR;

	System.arraycopy(SIMPLE_STOPS, 0, JAVA_STOPS, 0, 256);
	JAVA_STOPS[(int)'\t']                     = TAB;
	JAVA_STOPS[(int)'\f']                     = LINE_SEPARATOR;
	JAVA_STOPS[(int)Text.LINE_SEPARATOR_CHAR] = LINE_SEPARATOR;

	System.arraycopy(JAVA_STOPS, 0, JAVA_BREAK_STOPS, 0, 256);
	JAVA_BREAK_STOPS[(int)Text.LINE_BREAK_CHAR] = LINE_BREAK;

	// not supported
	System.arraycopy(SIMPLE_STOPS, 0, SYSTEM_STOPS, 0, 256);
	SYSTEM_STOPS[(int)'\t'] = TAB;
	SYSTEM_STOPS[(int)'\f'] = LINE_SEPARATOR;
	SYSTEM_STOPS[(int)'\n'] = LINE_SEPARATOR;
	SYSTEM_STOPS[(int)'\r'] = LINE_SEPARATOR;
	String lineSep = System.getProperty("line.separator", "\n");
	for (i = lineSep.length() - 2; i >= 0; --i) {
	    int c;
	    if ((c = lineSep.charAt(i)) < 256)
		SYSTEM_STOPS[c] = IGNORE;
	}
    }


    /**
     * Constructs a text scanner with the rich text to be scanned, the
     * line wrapping style, and the locale of the text.
     *
     * @param richText the rich text style.
     * @param lineWrap the line wrapping style.
     * @param locale   the locale of the text.
     */
    public TextScanner(RichText richText, int lineWrap, Locale locale) {
	this(richText.getText(), richText.getRichTextStyle(), lineWrap, locale);
    }

    /**
     * Constructs a text scanner with the text to be scanned, the rich
     * text style, the line wrapping style, and the locale of the text.
     *
     * @param text          the text to be scanned.
     * @param richTextStyle the rich text style.
     * @param lineWrap      the line wrapping style.
     * @param locale        the locale of the text.
     */
    public TextScanner(Text text, RichTextStyle richTextStyle, int lineWrap,
		       Locale locale)
    {
	if (text == null || richTextStyle == null || locale == null)
	    throw new NullPointerException();
	setLineWrap(lineWrap);
	this.text          = text;
	this.richTextStyle = richTextStyle;
	this.locale        = locale;
	this.attachments   = new VArray(TextAttachment.class);
    }


    /**
     * Breaks text into a line.
     *
     * @param  beginIndex       the beginning index of text to scan.
     * @param  compositionWidth the composition width of a line.
     * @param  lineTop          the top position of a line.
     * @param  pStyle           the paragraph style of a line.
     * @param  lineInfo         the line information into which the scanning
     *                          results are stored.
     * @return the next line top position, or <code>-1</code> if no more
     *         layout needed.
     */
    public int doLayoutLine(int beginIndex,
			    int compositionWidth,
			    int lineTop,
			    ParagraphStyle pStyle,
			    TextLineInfo lineInfo)
    {
	return doLayoutLine(beginIndex,
			    pStyle.getLeftIndent(),
			    compositionWidth,
			    lineTop,
			    pStyle,
			    lineInfo);
    }

    /**
     * Breaks text into a line.
     *
     * @param  beginIndex       the beginning index of text to scan.
     * @param  startX           the starting x position to scan.
     * @param  compositionWidth the composition width of a line.
     * @param  lineTop          the top position of a line.
     * @param  pStyle           the paragraph style of a line.
     * @param  lineInfo         the line information into which the scanning
     *                          results are stored.
     * @return the next line top position, or <code>-1</code> if no more
     *         layout needed.
     */
    public int doLayoutLine(int beginIndex,
			    int startX,
			    int compositionWidth,
			    int lineTop,
			    ParagraphStyle pStyle,
			    TextLineInfo lineInfo)
    {
	return doLayoutLine(beginIndex,
			    startX,
			    pStyle.getLeftIndent(),
			    (compositionWidth == Integer.MAX_VALUE ?
				Integer.MAX_VALUE :
				compositionWidth - pStyle.getRightIndent()),
			    pStyle.getTabWidth(),
			    lineTop,
			    pStyle,
			    lineInfo);
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
			    TextLineInfo lineInfo)
    {
	return doLayoutLine(beginIndex, startX, leftMgn, rightMgn, tabW,
			    lineTop, pStyle, getStops(), lineInfo);
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
	int length = text.length();
	if (beginIndex >= length) {
	    if (lastCondition == 0) {
		char ch;
		if (length > 0 && (ch = text.getChar(length - 1)) < 256) {
		    lastCondition = stops[ch];
		    switch (lastCondition) {
		    case LINE_SEPARATOR:
		    case LINE_BREAK:
			break;
		    default:
			lastCondition = END_OF_RUN;
			break;
		    }
		}
		else
		    lastCondition = END_OF_RUN;
	    }
	    return -1;
	}

	char chars[] = text.getCharArray();

	lineBegin = lastIndex = beginIndex;
	destX = startX;
	leftMargin = leftMgn;
	rightMargin = rightMgn;
	tabWidth = tabW;
	lineHeight = baseline = 0;
	boolean isParagraphMark;

	attachments.setLength(0);

	if (lineWrap == RichTextStyle.NO_WRAP)
	    rightMargin = Integer.MAX_VALUE;

      outer:
	for (;;) {
	    runEnd = lastIndex + text.getRunLengthAt(lastIndex);
	    setFontMetrics(text.getTextStyleAt(lastIndex).getFontMetrics());
	    lineHeight = Math.max(lineHeight, metrics.getHeight());
	    baseline = Math.max(baseline, metrics.getDescent());
	  inner:
	    for (;;) {
		lastCondition = scanChars(lastIndex, runEnd, chars, rightMargin,
					  stops);
		int answer = doLayoutLineStop(lastCondition);
		if (answer < 0) { // END_OF_RUN
		    if (lastIndex >= length) {
			lineEnd = length;
			isParagraphMark = true;
			break outer;
		    }
		    else {
			continue outer;
		    }
		}
		else if (answer > 0) {
		    isParagraphMark = (lastCondition == LINE_SEPARATOR);
		    break outer;
		}
		else { // answer == 0
		    continue inner;
		}
	    }
	}

	if (attachments.length() > 0) {
	    int len = attachments.length();
	    int maxTop = 0;
	    int maxBottom = 0;
	    int maxMiddle = 0;
	    for (int i = 0; i < len; i++) {
		TextAttachment ta = (TextAttachment)attachments.get(i);
		int h = ta.getSize().height;
		switch (ta.getAlignment()) {
		case TextAttachment.TOP:
		    if (h > maxTop) maxTop = h; break;
		case TextAttachment.BOTTOM:
		    if (h > maxBottom) maxBottom = h; break;
		case TextAttachment.MIDDLE:
		default:
		    if (h > maxMiddle) maxMiddle = h; break;
		}
	    }
	    int md = (maxMiddle <= lineHeight ? 0 : (maxMiddle-lineHeight) / 2);
	    int ma = (maxMiddle <= lineHeight ? 0 : (maxMiddle-lineHeight)-md);
	    int ascent = Math.max(ma, maxBottom - (lineHeight - baseline));
	    int descent = Math.max(md, maxTop - lineHeight);
	    if (ascent > 0) lineHeight += ascent;
	    if (descent > 0) { lineHeight += descent; baseline += descent; }
	}

	if (lineWrap == RichTextStyle.NO_WRAP)
	    rightMargin = rightMgn;

	lineInfo.lineBegin       = lineBegin;
	lineInfo.lineEnd         = lineEnd;
	lineInfo.remainWidth     = rightMargin - destX;
	lineInfo.lineHeight      = lineHeight;
	lineInfo.baseline        = baseline;
	lineInfo.y               = lineTop;
	lineInfo.paragraphStyle  = pStyle;
	lineInfo.isParagraphMark = isParagraphMark;
	lineInfo.lineSkip        = lineHeight + pStyle.getLineSpace();
	if (isParagraphMark) lineInfo.lineSkip += pStyle.getParagraphSpace();

	return lineTop + lineInfo.lineSkip;
    }


    /**
     * Draws a line.
     *
     * @param g        the graphics.
     * @param offset   the offset position to draw.
     * @param lineInfo the line information of a line to be drawn.
     * @param pStyle   the paragraph style of a line.
     */
    public void drawLine(Graphics g, Point offset,
			 TextLineInfo lineInfo, ParagraphStyle pStyle)
    {
	drawLineTo(g, offset, lineInfo.lineEnd, lineInfo, pStyle);
    }

    /**
     * Draws a line to the specified ending index of text.
     *
     * @param g        the graphics.
     * @param offset   the offset position to draw.
     * @param endIndex the ending index of text to draw. (exclusive)
     * @param lineInfo the line information of a line to be drawn.
     * @param pStyle   the paragraph style of a line.
     */
    public void drawLineTo(Graphics g, Point offset,
			   int endIndex,
			   TextLineInfo lineInfo, ParagraphStyle pStyle)
    {
	drawLineTo(g, offset,
		   lineInfo.lineBegin, endIndex, lineInfo.remainWidth,
		   lineInfo.lineHeight, lineInfo.baseline,
		   pStyle);
    }

    /**
     * Draws a line to the specified ending index of text.
     *
     * @param g           the graphics.
     * @param offset      the offset position to draw.
     * @param lineBegin   the beginning index of a line. (inclusive)
     * @param endIndex    the ending index of text to draw. (exclusive)
     * @param remainWidth the remaining width (space) of a line.
     * @param lineHeight  the height of a line.
     * @param baseline    the baseline of a line.
     * @param pStyle      the paragraph style of a line.
     */
    public void drawLineTo(Graphics g, Point offset,
			   int lineBegin, int endIndex, int remainWidth,
			   int lineHeight, int baseline,
			   ParagraphStyle pStyle)
    {
	int alignOffset;
	switch (pStyle.getAlignment()) {
	case ParagraphStyle.RIGHT:
	    alignOffset = remainWidth;
	    break;
	case ParagraphStyle.CENTER:
	    alignOffset = remainWidth / 2;
	    break;
	case ParagraphStyle.LEFT:
	default:
	    alignOffset = 0;
	    break;
	}
	drawLineFromTo(g, offset, pStyle.getLeftIndent() + alignOffset,
		       lineBegin, endIndex,
		       lineHeight, baseline,
		       pStyle.getLeftIndent() + alignOffset,
		       pStyle.getTabWidth());
    }

    /**
     * Draws a line from the beginning index to the ending index of text.
     *
     * @param g          the graphics.
     * @param offset     the offset position to draw.
     * @param startX     the starting x position to draw.
     * @param beginIndex the beginning index of text to draw. (inclusive)
     * @param endIndex   the ending index of text to draw. (exclusive)
     * @param lineInfo   the line information of a line to be drawn.
     * @param pStyle     the paragraph style of a line.
     */
    public void drawLineFromTo(Graphics g, Point offset, int startX,
			       int beginIndex, int endIndex,
			       TextLineInfo lineInfo, ParagraphStyle pStyle)
    {
	drawLineFromTo(g, offset, startX,
		       beginIndex, endIndex, lineInfo.remainWidth,
		       lineInfo.lineHeight, lineInfo.baseline,
		       pStyle);
    }

    /**
     * Draws a line from the beginning index to the ending index of text.
     *
     * @param g           the graphics.
     * @param offset      the offset position to draw.
     * @param startX      the starting x position to draw.
     * @param beginIndex  the beginning index of text to draw. (inclusive)
     * @param endIndex    the ending index of text to draw. (exclusive)
     * @param remainWidth the remaining width (space) of a line.
     * @param lineHeight  the height of a line.
     * @param baseline    the baseline of a line.
     * @param pStyle      the paragraph style of a line.
     */
    public void drawLineFromTo(Graphics g, Point offset, int startX,
			       int beginIndex, int endIndex, int remainWidth,
			       int lineHeight, int baseline,
			       ParagraphStyle pStyle)
    {
	int alignOffset;
	switch (pStyle.getAlignment()) {
	case ParagraphStyle.RIGHT:
	    alignOffset = remainWidth;
	    break;
	case ParagraphStyle.CENTER:
	    alignOffset = remainWidth / 2;
	    break;
	case ParagraphStyle.LEFT:
	default:
	    alignOffset = 0;
	    break;
	}
	drawLineFromTo(g, offset, startX,
		       beginIndex, endIndex,
		       lineHeight, baseline,
		       pStyle.getLeftIndent() + alignOffset,
		       pStyle.getTabWidth());
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
     */
    public void drawLineFromTo(Graphics g, Point offset, int startX,
			       int beginIndex, int endIndex,
			       int lineHeight, int baseline,
			       int leftMgn, int tabW)
    {
	drawLineFromTo(g, offset, startX,
		       beginIndex, endIndex,
		       lineHeight, baseline,
		       leftMgn, tabW,
		       getStops());
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
	if (beginIndex >= text.length())
	    return;

	this.lineHeight = lineHeight;
	this.baseline = baseline;

	int offX = offset.x;
	int offY = offset.y;
	int drawY = lineHeight - baseline;
	char chars[] = text.getCharArray();

	lineBegin = lastIndex = beginIndex;
	lineEnd = endIndex;
	destX = startX;
	leftMargin = leftMgn;
	rightMargin = Integer.MAX_VALUE;
	tabWidth = tabW;

      outer:
	for (;;) {
	    int rStart = lastIndex - text.getRunOffsetAt(lastIndex);
	    int rEnd   = lastIndex + text.getRunLengthAt(lastIndex);
	    runEnd = (rEnd > endIndex ? endIndex : rEnd);
	    TextStyle tStyle = text.getTextStyleAt(lastIndex);
	    setFontMetrics(tStyle.getFontMetrics());
	  inner:
	    for (;;) {
		int drawIndex = lastIndex;
		int drawX = destX;
		lastCondition = scanChars(lastIndex, runEnd, chars, rightMargin,
					  stops);
		//if (destX > drawX) {
		if (lastIndex > drawIndex) {
		    tStyle.drawText(g, chars, drawIndex, lastIndex - drawIndex,
				       drawIndex == rStart, lastIndex == rEnd,
				       drawX + offX, offY,
				       destX - drawX, lineHeight, drawY);
		}
		int answer = doDrawLineStop(lastCondition, g, offset, drawY,
					    rStart, rEnd, tStyle);
		if (answer < 0) { // END_OF_RUN
		    if (lastIndex >= endIndex) {
			break outer;
		    }
		    else {
			continue outer;
		    }
		}
		else if (answer > 0) {
		    break outer;
		}
		else { // answer == 0
		    continue inner;
		}
	    }
	}
    }


    /**
     * Computes the positin of the character in the text.
     *
     * @param  textIndex thea
     * @param  lineInfo  the information of a line where the character is in.
     * @param  pStyle    the paragraph style of a line.
     * @return the computed index and x position of the character.
     */
    public int[] linePositionAt(int textIndex,
				TextLineInfo lineInfo,
				ParagraphStyle pStyle)
    {
	if (textIndex < lineInfo.lineBegin)
	    textIndex = lineInfo.lineBegin;
	else if (textIndex > lineInfo.lineEnd)
	    textIndex = lineInfo.lineEnd;

	int alignOffset;
	switch (pStyle.getAlignment()) {
	case ParagraphStyle.RIGHT:
	    alignOffset = lineInfo.remainWidth;
	    break;
	case ParagraphStyle.CENTER:
	    alignOffset = lineInfo.remainWidth / 2;
	    break;
	case ParagraphStyle.LEFT:
	default:
	    alignOffset = 0;
	    break;
	}

	return linePositionAt(new Point(Integer.MAX_VALUE, 0),
			      pStyle.getLeftIndent() + alignOffset,
			      lineInfo.lineBegin, textIndex,
			      pStyle.getLeftIndent() + alignOffset,
			      pStyle.getTabWidth());
    }

    /**
     * Computes the positin of the character in the text.
     *
     * @param  point    the character position in the text.
     * @param  lineInfo the information of a line where the character is in.
     * @param  pStyle   the paragraph style of a line.
     * @return the computed index and x position of the character.
     */
    public int[] linePositionAt(Point point,
				TextLineInfo lineInfo,
				ParagraphStyle pStyle)
    {
	int alignOffset;
	switch (pStyle.getAlignment()) {
	case ParagraphStyle.RIGHT:
	    alignOffset = lineInfo.remainWidth;
	    break;
	case ParagraphStyle.CENTER:
	    alignOffset = lineInfo.remainWidth / 2;
	    break;
	case ParagraphStyle.LEFT:
	default:
	    alignOffset = 0;
	    break;
	}

	return linePositionAt(point, pStyle.getLeftIndent() + alignOffset,
			      lineInfo.lineBegin, lineInfo.lineEnd,
			      pStyle.getLeftIndent() + alignOffset,
			      pStyle.getTabWidth());
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
     * @return the computed index and x position of the character.
     */
    public int[] linePositionAt(Point point, int startX,
				int beginIndex, int endIndex,
				int leftMgn, int tabW)
    {
	return linePositionAt(point, startX, beginIndex, endIndex,
			      leftMgn, tabW,
			      getStops());
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
	if (beginIndex >= text.length()) {
	    return new int[]{ beginIndex, startX };
	}

	char chars[] = text.getCharArray();

	lineBegin = lastIndex = beginIndex;
	lineEnd = endIndex;
	destX = startX;
	leftMargin = leftMgn;
	rightMargin = point.x;
	tabWidth = tabW;

      outer:
	for (;;) {
	    runEnd = lastIndex + text.getRunLengthAt(lastIndex);
	    if (runEnd > endIndex) runEnd = endIndex;
	    setFontMetrics(text.getTextStyleAt(lastIndex).getFontMetrics());
	  inner:
	    for (;;) {
		lastCondition = scanChars(lastIndex, runEnd, chars, rightMargin,
					  stops);
		int answer = doTextPositionStop(lastCondition);
		if (answer < 0) { // END_OF_RUN
		    if (lastIndex >= endIndex) {
			break outer;
		    }
		    else {
			continue outer;
		    }
		}
		else if (answer > 0) {
		    break outer;
		}
		else { // answer == 0
		    continue inner;
		}
	    }
	}
	return new int[]{ lastIndex, destX };
    }

    /**
     * Sets the character displayed instead of a character that is not
     * defined in the current font.
     */
    public void setNotInFontChar(char ch) {
	notInFontChar = ch;
	notInFontCharString = new String(new char[]{ ch });
    }

    /**
     * Returns the advance width of the specified character in the text.
     *
     * @param  ch the character to be measured.
     * @return the advance width of the specified character.
     */
    public int charWidth(int ch)
    {
	return charWidth((char)ch);
    }

    /**
     * Returns the advance width of the specified character in the text.
     *
     * @param  ch the character to be measured.
     * @return the advance width of the specified character.
     */
    public int charWidth(char ch)
    {
	if (ch < 256)
	    return widths[ch];
	else {
	    //charWidth() returns incorrect value with large font on Windows
	    //return metrics.charWidth(ch);
	    return metrics.charsWidth(new char[]{ ch }, 0, 1);
	}
    }

    /**
     * Returns the advance width of a character in the specified index of
     * the text with the specified scanning context.
     *
     * @param  textIndex   the index of a character to be measured.
     * @param  startX      the starting x position to scan.
     * @param  layoutWidth the composition width.
     * @param  lineInfo    the information of a line where a character is in.
     * @param  pStyle      the paragraph style of a line.
     * @return the advance width of the specified character.
     */
    public int charWidthAt(int textIndex, int startX, int layoutWidth,
			   TextLineInfo lineInfo, ParagraphStyle pStyle)
    {
	if (textIndex < 0 || textIndex >= text.length())
	    return 0;

	int alignOffset;
	switch (pStyle.getAlignment()) {
	case ParagraphStyle.RIGHT:
	    alignOffset = lineInfo.remainWidth;
	    break;
	case ParagraphStyle.CENTER:
	    alignOffset = lineInfo.remainWidth / 2;
	    break;
	case ParagraphStyle.LEFT:
	default:
	    alignOffset = 0;
	    break;
	}
	char ch = text.getChar(textIndex);
	destX = startX;
	leftMargin = pStyle.getLeftIndent() + alignOffset;
	rightMargin = layoutWidth - pStyle.getRightIndent();
	tabWidth = pStyle.getTabWidth();
	if (ch == Text.ATTACHMENT_CHAR) {
	    TextAttachment ta = text.getAttachmentAt(textIndex);
	    if (ta == null)
		return 0;
	    else
		return ta.getSize().width;
	}
	setFontMetrics(text.getTextStyleAt(textIndex).getFontMetrics());
	return (ch == '\t' ? nextTab(destX) - destX : charWidth(ch));
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
	int newDestX;

	switch (stop) {
	case END_OF_RUN:
	    return -1;

	case LINE_SEPARATOR:
	case LINE_BREAK:
	    lineEnd = lastIndex + 1;
	    return 1;

	case CROSSED_X:
	    lineEnd = lastIndex;

	    if (lineEnd == lineBegin) { // veeery narrow
		char c = text.getChar(lastIndex);
		if (c == '\t')
		    destX = nextTab(destX);
		else
		    destX += charWidth(c);
		lineEnd = ++lastIndex;
		if (lastIndex >= runEnd) {
		    lastCondition = END_OF_RUN;
		    return -1;
		}
		return 1;
	    }

	    if (lineWrap == RichTextStyle.WORD_WRAP) // check word wrap
	    {
	    BreakIterator boundary = BreakIterator.getWordInstance(locale);
	    boundary.setText(
		text.getCharacterIterator(lineBegin, lineEnd, lineBegin));
	    char chars[] = text.getCharArray();
	    int end = boundary.last();
	    int start = boundary.previous();
	    if (start > lineBegin && start != BreakIterator.DONE) {
		if (Character.isSpaceChar(chars[start])) {
		    if (start < lineEnd) {
			start = BreakIterator.DONE;
		    }
		    else {
			start = boundary.previous();
		    }
		}
	    }
	    if (start > lineBegin && start != BreakIterator.DONE) {
		int i = lastIndex - 1;
		int offset = text.getRunOffsetAt(i);
		boolean metricsChanged = false;
		for (; i >= start; --i) {
		    if (offset-- < 0) {
			offset = text.getRunOffsetAt(i);
			setFontMetrics(text.getTextStyleAt(i).getFontMetrics());
			metricsChanged = true;
		    }
		    char c = chars[i];
		    if (c == Text.ATTACHMENT_CHAR) {
			start = i + 1;
			break;
		    }
		    else if (c == '\t')
			destX = prevTab(destX);
		    else
			destX -= charWidth(c);
		}
		lineEnd = start;

		if (metricsChanged) { // recompute lineHeight and baseline
		    lineHeight = baseline = 0;
		    i = lineBegin;
		    do {
			setFontMetrics(text.getTextStyleAt(i).getFontMetrics());
		    	lineHeight = Math.max(lineHeight, metrics.getHeight());
		    	baseline = Math.max(baseline, metrics.getDescent());
		    } while ((i += text.getRunLengthAt(i)) < start);
		}
	    }
	    }

	    return 1;

	case TAB:
	    newDestX = nextTab(destX);
	    break;

	case CHAR_NOT_IN_FONT:
	    newDestX = destX + notInFontCharWidth;
	    break;

	case ATTACHMENT:
	    TextAttachment ta = text.getAttachmentAt(lastIndex);
	    if (ta == null) {
		newDestX = destX;
	    }
	    else {
		if (ta.isVariableWidth()) {
		    int taWidth =
			(int)((rightMargin-leftMargin) * ta.getRatioToWidth());
		    newDestX = destX + taWidth;
		    ta.setSize(new Dimension(taWidth, ta.getSize().height));
		}
		else
		    newDestX = destX + ta.getSize().width;
		if (newDestX > rightMargin) {
		    lastCondition = CROSSED_X;
		    lineEnd = lastIndex;
		    if (lineEnd == lineBegin) { // veeery narrow
			destX = newDestX;
			lineEnd = ++lastIndex;
			attachments.append(ta);
			if (lastIndex >= runEnd) {
			    lastCondition = END_OF_RUN;
			    return -1;
			}
		    }
		    return 1; // do not care about the word wrapping
	    	}
		attachments.append(ta);
	    }
	    break;

	default:
	    throw new InternalError("not happen: " + stop);
	}

	if (newDestX > rightMargin) {
	    lastCondition = CROSSED_X;
	    return doLayoutLineStop(CROSSED_X);
	}
	else {
	    destX = newDestX;
	    if (++lastIndex >= runEnd) {
		lastCondition = END_OF_RUN;
		return doLayoutLineStop(END_OF_RUN);
	    }
	}
	return 0;
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
	int newDestX;

	switch (stop) {
	case END_OF_RUN:
	    return -1;

	case LINE_SEPARATOR:
	case LINE_BREAK:
	    lastIndex++;
	    return 0;

	case TAB:
	    newDestX = nextTab(destX);
	    tStyle.drawText(g, null, 0, 0,
			       lastIndex == rStart, lastIndex == rEnd,
			       destX + offset.x, offset.y,
			       newDestX - destX, lineHeight, drawY);
	    break;

	case CHAR_NOT_IN_FONT:
	    g.drawString(notInFontCharString, destX+offset.x, drawY+offset.y);
	    tStyle.drawText(g, null, 0, 0,
			       lastIndex == rStart, lastIndex == rEnd,
			       destX + offset.x, offset.y,
			       notInFontCharWidth, lineHeight, drawY);
	    newDestX = destX + notInFontCharWidth;
	    break;

	case ATTACHMENT:
	    TextAttachment ta = text.getAttachmentAt(lastIndex);
	    if (ta == null) {
		newDestX = destX;
	    }
	    else {
		Dimension d = ta.getSize();
		newDestX = destX + d.width;
		//int topY = drawY - (lineHeight - baseline);
		int topY = 0;
		Color color = null;
		if (tStyle.getExtendedFont().getColor() != null) {
		    color = g.getColor();
		    g.setColor(tStyle.getExtendedFont().getColor());
		}
		switch (ta.getAlignment()) {
		case TextAttachment.TOP:
		    ta.paint(g, new Point(destX + offset.x,
					  topY + offset.y));
		    break;
		case TextAttachment.BOTTOM:
		    ta.paint(g,
			new Point(destX + offset.x,
				  topY + offset.y +
					(lineHeight - baseline - d.height)));
		    break;
		case TextAttachment.MIDDLE:
		default:
		    ta.paint(g,
			new Point(destX + offset.x,
				  topY + offset.y + ((lineHeight-d.height)/2)));
		    break;
		}
		/*
		tStyle.drawText(g, null, 0, 0,
				   lastIndex == rStart, lastIndex == rEnd,
				   destX + offset.x, offset.y,
				   d.width, lineHeight, drawY);
		*/
		if (color != null) g.setColor(color);
	    }
	    break;

	case CROSSED_X:
	default:
	    throw new InternalError("not happen: " + stop);
	}

	if (newDestX > rightMargin) { // not happen
	    lastCondition = CROSSED_X;
	    return doDrawLineStop(CROSSED_X, g, offset, drawY,
				  rStart, rEnd, tStyle);
	}
	else {
	    destX = newDestX;
	    if (++lastIndex >= runEnd) {
		lastCondition = END_OF_RUN;
		return doDrawLineStop(END_OF_RUN, g, offset, drawY,
				      rStart, rEnd, tStyle);
	    }
	}
	return 0;
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
	int newDestX;

	switch (stop) {
	case END_OF_RUN:
	    return -1;

	case LINE_SEPARATOR:
	case LINE_BREAK:
	    //++lastIndex;
	    return 1;

	case CROSSED_X:
	    if (lastIndex < text.length()) {
		char ch = text.getChar(lastIndex);
		int cw = (ch == '\t' ? nextTab(destX) - destX : charWidth(ch));
		if (cw > 0 && (rightMargin - destX) * 2 > cw) {
		    ++lastIndex;
		    destX += cw;
		}
	    }
	    return 1;

	case TAB:
	    newDestX = nextTab(destX);
	    break;

	case CHAR_NOT_IN_FONT:
	    newDestX = destX + notInFontCharWidth;
	    break;

	case ATTACHMENT:
	    TextAttachment ta = text.getAttachmentAt(lastIndex);
	    if (ta == null) {
		newDestX = destX;
	    }
	    else {
		newDestX = destX + ta.getSize().width;
		if (newDestX > rightMargin) {
		    lastCondition = CROSSED_X;
		    if (lastIndex < text.length()) {
			int cw = newDestX - destX;
			if (cw > 0 && (rightMargin - destX) * 2 > cw) {
			    ++lastIndex;
			    destX += cw;
			}
		    }
		    return 1;
		}
	    }
	    break;

	default:
	    throw new InternalError("not happen: " + stop);
	}

	if (newDestX > rightMargin) {
	    lastCondition = CROSSED_X;
	    return doTextPositionStop(CROSSED_X);
	}
	else {
	    destX = newDestX;
	    if (++lastIndex >= runEnd) {
		lastCondition = END_OF_RUN;
		return doTextPositionStop(END_OF_RUN);
	    }
	}
	return 0;
    }

    /**
     * Scans the array of characters and returns the stop condition
     * if the scanning runs to the <code>end</code>, the scanning runs
     * across the <code>rightX</code>, or the scanning encounters the
     * stop condition defined in the <code>stops</code>.
     *
     * @param  begin  the beginning index of scanning. (inclusive)
     * @param  end    the ending index of scanning. (exclusive)
     * @param  chars  the array of characters to be scanned.
     * @param  rightX the right edge for scanning.
     * @param  stops  the stop conditions for scanning.
     * @return the stop condition.
     */
    protected int scanChars(int begin,
			    int end,
			    char chars[],
			    int rightX,
			    int stops[])
    {
	return fastScanChars(begin, end, chars, rightX, stops,
			(rightX == Integer.MAX_VALUE ? Integer.MAX_VALUE : 16));
	//return slowScanChars(begin, end, chars, rightX, stops);
    }

    /**
     * Scans the array of characters and returns the stop condition
     * if the scanning runs to the <code>end</code>, the scanning runs
     * across the <code>rightX</code>, or the scanning encounters the
     * stop condition defined in the <code>stops</code>.
     * <p>
     * This operation is efficient even if all characters in the array
     * are larger than 256.
     *
     * @param  begin      the beginning index of scanning. (inclusive)
     * @param  end        the ending index of scanning. (exclusive)
     * @param  chars      the array of characters to be scanned.
     * @param  rightX     the right edge for scanning.
     * @param  stops      the stop conditions for scanning.
     * @param  bufferSize the buffer size for calling
     *                    FontMetrics#charsWidth(char[], int, int)
     * @return the stop condition.
     */
    protected final int fastScanChars(int begin,
				      int end,
				      char chars[],
				      int rightX,
				      int stops[],
				      int bufferSize)
    {
	int mbCount = 0;
	int i = begin;
	for (;;) {
	    int newDestX;
	    char ch;
	    if (i >= end) {
		if (mbCount == 0) {
		    lastIndex = end;
		    return END_OF_RUN;
		}
		// fall through
	    }
	    else if ((ch = chars[i]) < 256) {
		if (mbCount == 0) {
		    int stop;
		    if ((stop = stops[ch]) < 0) {
			// if (stop == IGNORE) continue;
			lastIndex = i;
			return stop;
		    }
		    newDestX = destX + widths[ch];
		    if (newDestX > rightX) {
			lastIndex = i;
			return CROSSED_X;
		    }
		    destX = newDestX;
		    ++i;
		    continue;
		}
		// fall through
	    }
	    else if (ch == Text.ATTACHMENT_CHAR) { // attachment character
		if (mbCount == 0) {
		    lastIndex = i;
		    return ATTACHMENT;
		}
		// fall through
	    }
	    else if (mbCount < bufferSize) {
		++mbCount;
		++i;
		continue;
	    }
	    newDestX = destX + metrics.charsWidth(chars, i - mbCount, mbCount);
	    if (newDestX > rightX) {
		fastScanCharsForMultiBytes(i - mbCount, i, chars, rightX);
		return CROSSED_X;
	    }
	    destX = newDestX;
	    mbCount = 0;
	}
    }

    /**
     * Scans the array of characters that are larger than 256 until
     * the scanning runs across the <code>rightX</code>.
     *
     * @param  begin  the beginning index of scanning. (inclusive)
     * @param  end    the ending index of scanning. (exclusive)
     * @param  chars  the array of characters to be scanned.
     * @param  rightX the right edge for scanning.
     */
    protected final void fastScanCharsForMultiBytes(int begin,
						    int end,
						    char chars[],
						    int rightX)
    {
	/* Returns incorrect value with large font on Windows. Why?
	for (int i = begin; i < end; i++) {
	    //charWidth() returns incorrect value with large font on Windows
	    //int newDestX = destX + metrics.charWidth(chars[i]);
	    int newDestX = destX + metrics.charsWidth(chars, i, 1);
	    if (newDestX > rightX) {
		lastIndex = i;
		return;
	    }
	    destX = newDestX;
	}
	throw new InternalError("not happen");
	*/

	/* Returns correct value - a little slow
	int lastX = destX;
	for (int i = begin; i < end; i++) {
	    int newDestX = destX + metrics.charsWidth(chars, begin, i-begin+1);
	    if (newDestX > rightX) {
		destX = lastX;
		lastIndex = i;
		return;
	    }
	    lastX = newDestX;
	}
	throw new InternalError("not happen");
	*/

      loop:
	for (;;) {
	    if ((end - begin) > 2) {
		int mid = (begin + end) / 2;
		int newDestX = destX +
				metrics.charsWidth(chars, begin, mid - begin);
		if (newDestX <= rightX) {
		    destX = newDestX;
		    begin = mid;
		    continue loop;
		}
		else {
		    end = mid;
		    continue loop;
		}
	    }
	    int lastX = destX;
	    for (int i = begin; i < end; i++) {
		int newDestX = destX +
				metrics.charsWidth(chars, begin, i - begin + 1);
		if (newDestX > rightX) {
		    destX = lastX;
		    lastIndex = i;
		    return;
		}
		lastX = newDestX;
	    }
	    throw new InternalError("not happen");
	}
    }

    /**
     * Scans the array of characters and returns the stop condition
     * if the scanning runs to the <code>end</code>, the scanning runs
     * across the <code>rightX</code>, or the scanning encounters the
     * stop condition defined in the <code>stops</code>.
     * <p>
     * This operation is not efficient because it calls
     * FontMetrics#charsWidth(char) at each character.
     *
     * @param  begin  the beginning index of scanning. (inclusive)
     * @param  end    the ending index of scanning. (exclusive)
     * @param  chars  the array of characters to be scanned.
     * @param  rightX the right edge for scanning.
     * @param  stops  the stop conditions for scanning.
     * @return the stop condition.
     */
    protected final int slowScanChars(int begin,
				      int end,
				      char chars[],
				      int rightX,
				      int stops[])
    {
	for (int i = begin; i < end; i++) {
	    char ch = chars[i];
	    int newDestX;
	    if (ch < 256) {
		int stop;
		if ((stop = stops[ch]) < 0) {
		    // if (stop == IGNORE) continue;
		    lastIndex = i;
		    return stop;
		}
		newDestX = destX + widths[ch];
	    }
	    else {
		if (ch == Text.ATTACHMENT_CHAR) { // attachment character
		    lastIndex = i;
		    return ATTACHMENT;
		}
		//charWidth() returns incorrect value with large font on Windows
		//newDestX = destX + metrics.charWidth(ch);
		newDestX = destX + metrics.charsWidth(chars, i, 1);
	    }
	    if (newDestX > rightX) {
		lastIndex = i;
		return CROSSED_X;
	    }
	    destX = newDestX;
	}
	lastIndex = end;
	return END_OF_RUN;
    }

    /**
     * Sets the line wrapping style.
     */
    protected void setLineWrap(int lineWrap) {
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
     * Sets the font metrics.
     *
     * @param metrics the font metrics.
     */
    protected void setFontMetrics(FontMetrics metrics) {
	this.metrics = metrics;
	widths = metrics.getWidths();
	if (tabWidth <= 0) {
	    tabWidth = richTextStyle.getTabWidth();
	}
	notInFontCharWidth = metrics.charWidth(notInFontChar);
    }

    /**
     * Returns the stop conditions.
     *
     * @return the stop conditions.
     */
    protected int[] getStops() {
	if (richTextStyle.isJavaLineSeparator())
	    return JAVA_STOPS;
	else if (richTextStyle.isJavaLineSeparatorWithBreak())
	    return JAVA_BREAK_STOPS;
	else if (richTextStyle.isListSeparator())
	    return LIST_STOPS;
	else
	    return NO_STOPS;
    }

    /**
     * Returns a next tab position form the specified x position.
     *
     * @param  x the specified x position.
     * @return a next tab position.
     */
    protected int nextTab(int x) {
	return x + tabWidth - ((x - leftMargin) % tabWidth);
    }

    /**
     * Returns a previous tab position form the specified x position.
     *
     * @param  x the specified x position.
     * @return a previous tab position.
     */
    protected int prevTab(int x) {
	return x - ((x - leftMargin) % tabWidth);
    }
}
