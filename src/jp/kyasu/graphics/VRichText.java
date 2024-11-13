/*
 * VRichText.java
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

import jp.kyasu.graphics.text.TextPositionInfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

/**
 * The <code>VRichText</code> class implements the visual rich text.
 * This class provides the interface of the visual object to the
 * <code>RichText</code> class.
 *
 * @version 	21 Nov 1999
 * @author 	Kazuki YASUMATSU
 */
public class VRichText implements Visualizable {
    /** The rich text. */
    protected RichText richText;

    /** The width of the layout. */
    protected int layoutWidth;

    /** The layout for the rich text. */
    transient protected TextLayout layout;

    /** The beginning poosition of the layout. */
    transient protected TextPositionInfo begin;

    /** The ending poosition of the layout. */
    transient protected TextPositionInfo end;


    /**
     * The default rich text style for the visual rich text with
     * left justification.
     */
    static public final RichTextStyle DEFAULT_LEFT_STYLE =
	new RichTextStyle(
		RichTextStyle.NO_WRAP,
		RichTextStyle.JAVA_LINE_SEPARATOR,
		true,
		new TextStyle("SansSerif", Font.PLAIN, 12),
		new ParagraphStyle(ParagraphStyle.LEFT, 0, 0, 0, 0, 0));

    /**
     * The default rich text style for the visual rich text with
     * right justification.
     */
    static public final RichTextStyle DEFAULT_RIGHT_STYLE =
	new RichTextStyle(
		RichTextStyle.NO_WRAP,
		RichTextStyle.JAVA_LINE_SEPARATOR,
		true,
		new TextStyle("SansSerif", Font.PLAIN, 12),
		new ParagraphStyle(ParagraphStyle.RIGHT, 0, 0, 0, 0, 0));

    /**
     * The default rich text style for the visual rich text with
     * center justification.
     */
    static public final RichTextStyle DEFAULT_CENTER_STYLE =
	new RichTextStyle(
		RichTextStyle.NO_WRAP,
		RichTextStyle.JAVA_LINE_SEPARATOR,
		true,
		new TextStyle("SansSerif", Font.PLAIN, 12),
		new ParagraphStyle(ParagraphStyle.CENTER, 0, 0, 0, 0,0));

    /**
     * Returns the default rich text style with the specified alignment.
     *
     * @return the default rich text style with the specified alignment.
     * @see    jp.kyasu.graphics.ParagraphStyle#LEFT
     * @see    jp.kyasu.graphics.ParagraphStyle#RIGHT
     * @see    jp.kyasu.graphics.ParagraphStyle#CENTER
     */
    static public RichTextStyle getDefaultRichTextStyle(int alignment) {
	switch (alignment) {
	case ParagraphStyle.LEFT:   return DEFAULT_LEFT_STYLE;
	case ParagraphStyle.RIGHT:  return DEFAULT_RIGHT_STYLE;
	case ParagraphStyle.CENTER: return DEFAULT_CENTER_STYLE;
	default:
	    throw new IllegalArgumentException("improper alignment: " +
								alignment);
	}
    }


    /**
     * Constructs a visual rich text with the specified string.
     *
     * @param str the string.
     */
    public VRichText(String str) {
	this(str, ParagraphStyle.LEFT);
    }

    /**
     * Constructs a visual rich text with the specified string and
     * the specified alignment.
     *
     * @param str       the string.
     * @param alignment the alignment of the rich text.
     * @see   jp.kyasu.graphics.ParagraphStyle#LEFT
     * @see   jp.kyasu.graphics.ParagraphStyle#CENTER
     * @see   jp.kyasu.graphics.ParagraphStyle#RIGHT
     */
    public VRichText(String str, int alignment) {
	this(new Text(str, DEFAULT_LEFT_STYLE.textStyle), alignment);
    }

    /**
     * Constructs a visual rich text with the specified text.
     *
     * @param text the text.
     */
    public VRichText(Text text) {
	this(text, ParagraphStyle.LEFT);
    }

    /**
     * Constructs a visual rich text with the specified text and
     * the specified alignment.
     *
     * @param text      the text.
     * @param alignment the alignment of the rich text.
     * @see   jp.kyasu.graphics.ParagraphStyle#LEFT
     * @see   jp.kyasu.graphics.ParagraphStyle#RIGHT
     * @see   jp.kyasu.graphics.ParagraphStyle#CENTER
     */
    public VRichText(Text text, int alignment) {
	this(new RichText(text, getDefaultRichTextStyle(alignment)));
    }

    /**
     * Constructs a visual rich text with the specified text buffer.
     *
     * @param textBuffer the text buffer.
     */
    public VRichText(TextBuffer textBuffer) {
	this(textBuffer, ParagraphStyle.LEFT);
    }

    /**
     * Constructs a visual rich text with the specified text buffer and
     * the specified alignment.
     *
     * @param textBuffer the text buffer.
     * @param alignment  the alignment of the rich text.
     * @see   jp.kyasu.graphics.ParagraphStyle#LEFT
     * @see   jp.kyasu.graphics.ParagraphStyle#RIGHT
     * @see   jp.kyasu.graphics.ParagraphStyle#CENTER
     */
    public VRichText(TextBuffer textBuffer, int alignment) {
	this(textBuffer.toRichText(getDefaultRichTextStyle(alignment)));
    }

    /**
     * Constructs a visual rich text with the specified rich text.
     *
     * @param richText the rich text.
     */
    public VRichText(RichText richText) {
	if (richText == null)
	    throw new NullPointerException();
	this.richText = richText;
	this.layoutWidth = 0;
	this.layout = null;
	this.begin  = null;
	this.end    = null;
    }

    /**
     * Returns the rich text in this visual rich text.
     *
     * @return the rich text in this visual rich text.
     */
    public RichText getRichText() {
	return richText;
    }

    /**
     * Returns the size of this visual rich text.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	return getTextLayout().getSize();
    }

    /**
     * Resizes the visual rich text to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	layoutWidth = d.width;
	if (layout != null) {
	    if (layout.isNoWrap()) {
		if (!layout.isValid()) {
		    layout.setWidth(1);
		}
	    }
	    else {
		layout.setWidth(layoutWidth);
	    }
	}
	begin = null;
	end   = null;
    }

    /**
     * Checks if the visual rich text is resizable.
     * @return <code>false</code>.
     * @see jp.kyasu.graphics.Visualizable#isResizable()
     */
    public boolean isResizable() {
	return false;
    }

    /**
     * Paints the visual rich text at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	Color color = g.getColor();
	Font font = g.getFont();
	getTextLayout().draw(g, p, getBeginPosition(), getEndPosition());
	g.setColor(color);
	g.setFont(font);
    }

    /**
     * Returns a clone of this visual rich text.
     */
    public Object clone() {
	try {
	    VRichText vrtext   = (VRichText)super.clone();
	    vrtext.richText    = richText; // share
	    vrtext.layout      = layout;   // share
	    vrtext.begin       = begin;    // share
	    vrtext.end         = end;      // share
	    vrtext.layoutWidth = layoutWidth;
	    return vrtext;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }


    /** Returns the layout for the rich text. */
    protected TextLayout getTextLayout() {
	if (layout == null) {
	    layout = new TextLayout(richText);
	    if (layout.isNoWrap()) {
		layout.setWidth(1);
	    }
	    else if (layoutWidth > 0) {
		layout.setWidth(layoutWidth);
	    }
	    else {
		layout.validate();
	    }
	}
	return layout;
    }

    /** Returns the beginning position of the rich text. */
    protected TextPositionInfo getBeginPosition() {
	if (begin == null) {
	    begin = getTextLayout().getTextPositionAt(0);
	}
	return begin;
    }

    /** Returns the ending position of the rich text. */
    protected TextPositionInfo getEndPosition() {
	if (end == null) {
	    end = getTextLayout().getTextPositionAt(richText.length());
	}
	return end;
    }
}
