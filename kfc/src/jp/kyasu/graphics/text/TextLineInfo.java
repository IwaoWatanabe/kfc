/*
 * TextLineInfo.java
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

import jp.kyasu.graphics.ParagraphStyle;

/**
 * The <code>TextLineInfo</code> class provides an information of the line
 * in <code>TextLayout</code>.
 *
 * @version 	09 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class TextLineInfo {
    /**
     * The beginning text index of this line, inclusive.
     */
    public int lineBegin;

    /**
     * The ending text index of this line, exclusive.
     */
    public int lineEnd;

    /**
     * The remaining width (the remaining space) of this line.
     * The remaining width is roughly same as:
     * the width of a TextLayout -
     *           the width of a text from beginIndex to endIndex.
     */
    public int remainWidth;

    /**
     * The height of this line.
     */
    public int lineHeight;

    /**
     * The skip space of this line, that is, the line height plus
     * an optional space after line and an optional space after paragraph
     * (<code>lineHeight + lineSpace + paragraphSpace</code>).
     */
    public int lineSkip;

    /**
     * The baseline of this line.
     */
    public int baseline;

    /**
     * The top position of this line.
     */
    public int y;

    /**
     * The paragraph style of this line.
     */
    public ParagraphStyle paragraphStyle;

    /**
     * True when this line is end of a paragraph.
     */
    public boolean isParagraphMark;


    /**
     * Constructs an empty text line information.
     */
    public TextLineInfo() {
	this(0, 0, 0, 0, 0, 0, 0, null, false);
    }

    /**
     * Constructs a text line information with the specified informations.
     *
     * @param lineBegin       the beginning text index.
     * @param lineEnd         the ending text index.
     * @param remainWidth     the remaining width.
     * @param lineHeight      the height.
     * @param lineSkip        the height plus space after line and paragraph.
     * @param baseline        the baseline.
     * @param lineTop         the top position of line.
     * @param paragraphStyle  the paragraph style.
     * @param isParagraphMark <code>true</code>when end of a paragraph.
     */
    public TextLineInfo(int lineBegin, int lineEnd, int remainWidth,
			int lineHeight, int lineSkip, int baseline, int lineTop,
			ParagraphStyle paragraphStyle, boolean isParagraphMark)
    {
	this.lineBegin       = lineBegin;
	this.lineEnd         = lineEnd;
	this.remainWidth     = remainWidth;
	this.lineHeight      = lineHeight;
	this.lineSkip        = lineSkip;
	this.baseline        = baseline;
	this.y               = lineTop;
	this.paragraphStyle  = paragraphStyle;
	this.isParagraphMark = isParagraphMark;
    }

    /**
     * Constructs a text line information with the same information
     * as the specified text line information.
     *
     * @param lineInfo the text line information.
     */
    public TextLineInfo(TextLineInfo lineInfo) {
	this.lineBegin       = lineInfo.lineBegin;
	this.lineEnd         = lineInfo.lineEnd;
	this.remainWidth     = lineInfo.remainWidth;
	this.lineHeight      = lineInfo.lineHeight;
	this.lineSkip        = lineInfo.lineSkip;
	this.baseline        = lineInfo.baseline;
	this.y               = lineInfo.y;
	this.paragraphStyle  = lineInfo.paragraphStyle;
	this.isParagraphMark = lineInfo.isParagraphMark;
    }

    /**
     * Returns a string representation of the parameters.
     */
    public String paramString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("lineBegin="    + lineBegin);
	buffer.append(",lineEnd="     + lineEnd);
	buffer.append(",remainWidth=" + remainWidth);
	buffer.append(",lineHeight="  + lineHeight);
	buffer.append(",lineSkip="    + lineSkip);
	buffer.append(",baseline="    + baseline);
	if (paragraphStyle != null) {
	    buffer.append(",paragraphStyle=" + paragraphStyle);
	}
	buffer.append(",isParagraphMark=" + isParagraphMark);
	buffer.append(",y=" + y);
	return buffer.toString();
    }

    /**
     * Returns a string representation of this object.
     */
    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }
}
