/*
 * VText.java
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Locale;

/**
 * The <code>VText</code> class implements the visual text. This class
 * provides the interface of the visual object to the <code>Text</code>
 * class.
 *
 * @see		jp.kyasu.graphics.Text
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class VText implements Visualizable {
    /** The text. */
    protected Text text;

    /** The width of the text. */
    protected int width;

    /** The height of the text. */
    protected int lineHeight;

    /** The baseline of the text. */
    protected int baseline;


    /**
     * The rich text style for the visual text.
     */
    static protected final RichTextStyle DEFAULT_STYLE =
	    new RichTextStyle(
		RichTextStyle.NO_WRAP,
		RichTextStyle.JAVA_LINE_SEPARATOR,
		false,
		TextStyle.DEFAULT_STYLE,
		new ParagraphStyle(ParagraphStyle.LEFT, 0, 0, 0, 0, 0));


    /**
     * Constructs a visual text width the specified string.
     *
     * @param str the string.
     */
    public VText(String str) {
	this(new Text(str));
    }

    /**
     * Constructs a visual text width the specified text.
     *
     * @param text the text.
     */
    public VText(Text text) {
	setText(text);
    }

    /**
     * Returns the text in this visual text.
     *
     * @return the text in this visual text.
     */
    public Text getText() {
	return text;
    }

    /**
     * Sets the text of this visual text to be the specified text.
     *
     * @param text the specified text.
     */
    public void setText(Text text) {
	if (text == null)
	    throw new NullPointerException();
	this.text = text;
	TextScanner scanner = getScanner();
	TextLineInfo lineInfo = new TextLineInfo();
	scanner.doLayoutLine(0, 0, 0, Integer.MAX_VALUE, 0, -1,
			     DEFAULT_STYLE.paragraphStyle,
			     TextScanner.SIMPLE_STOPS,
			     lineInfo);
	width = scanner.destX;
	lineHeight = lineInfo.lineHeight;
	baseline = lineInfo.baseline;
    }

    /**
     * Returns the size of this visual text.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	return new Dimension(width, lineHeight);
    }

    /**
     * Resizes the visual text to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	// do nothing
    }

    /**
     * Checks if the visual text is resizable.
     * @return <code>false</code>.
     * @see jp.kyasu.graphics.Visualizable#isResizable()
     */
    public boolean isResizable() {
	return false;
    }

    /**
     * Paints the visual text at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	Color color = g.getColor();
	Font font = g.getFont();
	getScanner().drawLineFromTo(g, p, 0, 0, text.length(),
				    lineHeight, baseline,
				    0, 0, TextScanner.SIMPLE_STOPS);
	g.setColor(color);
	g.setFont(font);
    }

    /**
     * Returns a clone of this visual text.
     */
    public Object clone() {
	try {
	    VText vtext = (VText)super.clone();
	    vtext.text       = text; // share
	    vtext.width      = width;
	    vtext.lineHeight = lineHeight;
	    vtext.baseline   = baseline;
	    return vtext;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /** Returns the text scanner for the visual text. */
    protected TextScanner getScanner() {
	TextScanner scanner = new TextScanner(text, DEFAULT_STYLE,
					      RichTextStyle.NO_WRAP,
					      Locale.getDefault());
	scanner.setNotInFontChar(' ');
	return scanner;
    }
}
