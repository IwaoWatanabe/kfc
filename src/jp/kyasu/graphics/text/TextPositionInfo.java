/*
 * TextPositionInfo.java
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
 * The <code>TextPositionInfo</code> class provides an information of
 * position in <code>TextLayout</code>.
 *
 * @version 	09 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class TextPositionInfo extends TextLineInfo {
    /**
     * The index of text at this position.
     */
    public int textIndex;

    /**
     * The index of line at this position.
     */
    public int lineIndex;

    /**
     * The x position.
     */
    public int x;


    /**
     * Constructs an empty text position information.
     */
    public TextPositionInfo() {
	super();
	textIndex = 0;
	lineIndex = 0;
	x         = 0;
    }

    /**
     * Constructs a text position information with the specified informations.
     *
     * @param textIndex       the index of text.
     * @param lineIndex       the index of line.
     * @param x               the x position.
     * @param y               the y position.
     * @param lineBegin       the beginning text index.
     * @param lineEnd         the ending text index.
     * @param remainWidth     the remaining width.
     * @param lineHeight      the height.
     * @param lineSkip        the height plus space after line and paragraph.
     * @param baseline        the baseline.
     * @param paragraphStyle  the paragraph style.
     * @param isParagraphMark <code>true</code>when end of a paragraph.
     */
    public TextPositionInfo(int textIndex, int lineIndex, int x, int y,
			    int lineBegin, int lineEnd, int remainWidth,
			    int lineHeight, int lineSkip, int baseline,
			    ParagraphStyle paragraphStyle,
			    boolean isParagraphMark)
    {
	super(lineBegin, lineEnd, remainWidth, lineHeight, lineSkip, baseline,
	      y, paragraphStyle, isParagraphMark);
	this.textIndex = textIndex;
	this.lineIndex = lineIndex;
	this.x         = x;
    }

    /**
     * Constructs a text position information with the specified text line
     * information and other informations.
     *
     * @param textIndex the index of text.
     * @param lineIndex the index of line.
     * @param x         the x position.
     * @param lineInfo  the text line information.
     */
    public TextPositionInfo(int textIndex, int lineIndex, int x,
			    TextLineInfo lineInfo)
    {
	super(lineInfo);
	this.textIndex = textIndex;
	this.lineIndex = lineIndex;
	this.x         = x;
    }

    /**
     * Constructs a text position information with the same information
     * as the specified text position information.
     *
     * @param posInfo the text position information.
     */
    public TextPositionInfo(TextPositionInfo posInfo) {
	super(posInfo);
	this.textIndex = posInfo.textIndex;
	this.lineIndex = posInfo.lineIndex;
	this.x         = posInfo.x;
    }

    /**
     * Returns a string representation of the parameters.
     */
    public String paramString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("textIndex=" + textIndex);
	buffer.append(",lineIndex=" + lineIndex);
	buffer.append("," + super.paramString());
	buffer.append(",x=" + x);
	return buffer.toString();
    }
}
