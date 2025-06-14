/*
 * EchoTextScanner.java
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Locale;

/**
 * The <code>EchoTextScanner</code> class scans text to lay out text, to draw
 * text, and to compute the position of text. Each character in the text is
 * treated as the specified echo character. The <code>EchoTextScanner</code>
 * class is useful when the user input shouldn't be echoed to the screen,
 * as in the case of a password field.
 *
 * @version 	22 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class EchoTextScanner extends TextScanner {
    /** The array of echo characters. */
    protected char echoChars[];

    /** The width of a echo character. */
    protected int echoCharWidth;


    /**
     * Constructs a echo text scanner with the rich text to be scanned,
     * the line wrapping style, the echo character, and the locale of
     * the text.
     *
     * @param richText the rich text to be scanned.
     * @param lineWrap the line wrapping style.
     * @param echoChar the echo character.
     * @param locale   the locale of the text.
     */
    public EchoTextScanner(RichText richText, int lineWrap, char echoChar,
			   Locale locale)
    {
	this(richText.getText(), richText.getRichTextStyle(), lineWrap,
	     echoChar, locale);
    }

    /**
     * Constructs a echo text scanner with the text to be scanned, the
     * rich text style, the line wrapping style, the echo character, and
     * the locale of the text.
     *
     * @param text          the text to be scanned.
     * @param richTextStyle the rich text style.
     * @param lineWrap      the line wrapping style.
     * @param echoChar      the echo character.
     * @param locale        the locale of the text.
     */
    public EchoTextScanner(Text text, RichTextStyle richTextStyle,
			   int lineWrap, char echoChar, Locale locale)
    {
	super(text, richTextStyle, lineWrap, locale);
	echoChars = new char[128];
	for (int i = 0; i < echoChars.length; i++)
	    echoChars[i] = echoChar;
	echoCharWidth = 0;
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

	Font save = g.getFont();

      outer:
	for (;;) {
	    int rStart = lastIndex - text.getRunOffsetAt(lastIndex);
	    int rEnd   = lastIndex + text.getRunLengthAt(lastIndex);
	    runEnd = (rEnd > endIndex ? endIndex : rEnd);
	    TextStyle tStyle = text.getTextStyleAt(lastIndex);
	    setFontMetrics(tStyle.getFontMetrics());
	    g.setFont(tStyle.getFont());
	  inner:
	    for (;;) {
		int drawIndex = lastIndex;
		int drawX = destX;
		lastCondition = scanChars(lastIndex, runEnd, chars, rightMargin,
					  getStops());
		//if (destX > drawX) {
		if (lastIndex > drawIndex) {
		    int x = 0;
		    int i = drawIndex;
		    while (i < lastIndex) {
			int length = Math.min(echoChars.length, lastIndex - i);
			g.drawChars(echoChars, 0, length,
					drawX + offX + x,
					drawY + offY);
			x += (echoCharWidth * length);
			i += length;
		    }
		    tStyle.drawText(g, null, 0, 0,
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

	g.setFont(save);
    }


    /**
     * Returns the advance width of the specified character in the text.
     *
     * @param  ch the character to be measured.
     * @return the advance width of the specified character.
     */
    public int charWidth(char ch)
    {
	return echoCharWidth;
    }

    /**
     * Returns the advance width of a character in the specified index of text
     * with the specified scanning context.
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

	tabWidth = pStyle.getTabWidth();
	setFontMetrics(text.getTextStyleAt(textIndex).getFontMetrics());
	return echoCharWidth;
    }

    /**
     * Handles the stop condition for breaking text into a line.
     *
     * @param  stop the stop condition.
     * @return <code>-1</code> if the scanning runs to the end;
     *         <code>1</code> if the scanning runs across the right edge.
     */
    protected int doLayoutLineStop(int stop) {
	switch (stop) {
	case END_OF_RUN:
	    return -1;

	case CROSSED_X:
	    lineEnd = lastIndex;
	    if (lineEnd == lineBegin) { // veeery narrow
		destX += echoCharWidth;
		lineEnd = ++lastIndex;
		return 1;
	    }
	    return 1;

	default:
	    throw new InternalError("not happen: " + stop);
	}
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
     * @return <code>-1</code> if the scanning runs to the end.
     */
    protected int doDrawLineStop(int stop,
				 Graphics g,
				 Point offset,
				 int drawY,
				 int rStart, int rEnd,
				 TextStyle tStyle)
    {
	switch (stop) {
	case END_OF_RUN:
	    return -1;

	case CROSSED_X:
	default:
	    throw new InternalError("not happen: " + stop);
	}
    }

    /**
     * Handles the stop condition for computing a position of character.
     *
     * @param  stop the stop condition.
     * @return <code>-1</code> if the scanning runs to the end;
     *         <code>1</code> if the scanning runs across the right edge or
     *         the scanning encounters a line separator or a line break.
     */
    protected int doTextPositionStop(int stop) {
	switch (stop) {
	case END_OF_RUN:
	    return -1;

	case CROSSED_X:
	    if (lastIndex < text.length()) {
		int cw = echoCharWidth;
		if (cw > 0 && (rightMargin - destX) * 2 > cw) {
		    ++lastIndex;
		    destX += cw;
		}
	    }
	    return 1;

	default:
	    throw new InternalError("not happen: " + stop);
	}
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
	for (int i = begin; i < end; i++) {
	    int newDestX = destX + echoCharWidth;
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
     * Sets the font metrics.
     *
     * @param metrics the font metrics.
     */
    protected void setFontMetrics(FontMetrics metrics) {
	super.setFontMetrics(metrics);
	echoCharWidth = metrics.charWidth(echoChars[0]);
    }
}
