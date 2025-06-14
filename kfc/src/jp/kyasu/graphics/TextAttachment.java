/*
 * TextAttachment.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

/**
 * The <code>TextAttachment</code> class implements the visual item that
 * can be stored in the text. The text attachment can wrap any visual object.
 * The text attachment has the ratio to the width of layout of the rich text.
 * If the ratio is set (the ratio is greater than 0.0f), the width of the
 * text attachment becomes variable and it is resized in the ratio to the
 * width of the layout of the rich text.
 *
 * @see		jp.kyasu.graphics.Text
 * @see		jp.kyasu.graphics.TextLayout
 *
 * @version 	28 Sep 1997
 * @author 	Kazuki YASUMATSU
 */
public class TextAttachment extends VWrapper {
    /** The name of the attachment. */
    protected String name;

    /** The vertical alignment of the attachment. */
    protected int alignment;

    /** The ratio to the width of the attachment. */
    protected float ratioToWidth;


    /**
     * The constant for the vertical middle alignment.
     */
    static public final int MIDDLE = 0;

    /**
     * The constant for the vertical top alignment.
     */
    static public final int TOP    = 1;

    /**
     * The constant for the vertical bottom alignment.
     */
    static public final int BOTTOM = 2;


    /**
     * Constructs a text attachment with the specified visual object.
     *
     * @param visualizable the visual object.
     */
    public TextAttachment(Visualizable visualizable) {
	this(null, visualizable);
    }

    /**
     * Constructs a text attachment with the specified visual object and
     * vertical alignment.
     *
     * @param visualizable the visual object.
     * @param alignment    the vertical alignment.
     */
    public TextAttachment(Visualizable visualizable, int alignment) {
	this(null, visualizable, alignment);
    }

    /**
     * Constructs a text attachment with the specified visual object and
     * ratio to the width.
     *
     * @param visualizable the visual object.
     * @param f            the ratio to the width.
     */
    public TextAttachment(Visualizable visualizable, float f) {
	this(null, visualizable, f);
    }

    /**
     * Constructs a text attachment with the specified visual object,
     * vertical alignment and ratio to the width.
     *
     * @param visualizable the visual object.
     * @param alignment    the vertical alignment.
     * @param f            the ratio to the width.
     */
    public TextAttachment(Visualizable visualizable, int alignment, float f) {
	this(null, visualizable, alignment, f);
    }

    /**
     * Constructs a text attachment with the specified name and visual object.
     *
     * @param name         the name.
     * @param visualizable the visual object.
     */
    public TextAttachment(String name, Visualizable visualizable) {
	this(name, visualizable, MIDDLE);
    }

    /**
     * Constructs a text attachment with the specified name, visual object
     * and vertical alignment.
     *
     * @param name         the name.
     * @param visualizable the visual object.
     * @param alignment    the vertical alignment.
     */
    public TextAttachment(String name, Visualizable visualizable, int alignment)
    {
	this(name, visualizable, alignment, 0.0f);
    }

    /**
     * Constructs a text attachment with the specified name, visual object
     * and ratio to the width.
     *
     * @param name         the name.
     * @param visualizable the visual object.
     * @param f            the ratio to the width.
     */
    public TextAttachment(String name, Visualizable visualizable, float f) {
	this(name, visualizable, MIDDLE, f);
    }

    /**
     * Constructs a text attachment with the specified name, visual object,
     * vertical alignment and ratio to the width.
     *
     * @param name         the name.
     * @param visualizable the visual object.
     * @param alignment    the vertical alignment.
     * @param f            the ratio to the width.
     */
    public TextAttachment(String name, Visualizable visualizable,
			  int alignment, float f)
    {
	super(visualizable);
	this.name = name;
	this.ratioToWidth = Math.min(f, 1.0f);
	setAlignment(alignment);
    }


    /**
     * Returns the name of this text attachment.
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the vertical alignment of this text attachment.
     *
     * @see #MIDDLE
     * @see #TOP
     * @see #BOTTOM
     */
    public int getAlignment() {
	return alignment;
    }

    /**
     * Sets the vertical alignment of this text attachment to be the
     * specified alignment.
     *
     * @param alignment the vertical alignment.
     * @see   #MIDDLE
     * @see   #TOP
     * @see   #BOTTOM
     */
    public void setAlignment(int alignment) {
	switch (alignment) {
	case MIDDLE:
	case TOP:
	case BOTTOM:
	    this.alignment = alignment;
	    return;
	}
	throw new IllegalArgumentException("improper alignment: " + alignment);
    }

    /**
     * Checks if the width of the text attachment is variable.
     */
    public boolean isVariableWidth() {
	return ratioToWidth > 0.0f;
    }

    /**
     * Returns the ratio to the width of the layout of the rich text.
     */
    public float getRatioToWidth() {
	return ratioToWidth;
    }

    /**
     * Sets the ratio to the width of the layout of the rich text.
     *
     * @param f the ratio that should be smaller than 1.0f.
     */
    public void setRatioToWidth(float f) {
	ratioToWidth = Math.min(f, 1.0f);
    }

    /**
     * Returns a clone of this text attachment.
     */
    public Object clone() {
	TextAttachment ta = (TextAttachment)super.clone();
	ta.name         = name; // share
	ta.alignment    = alignment;
	ta.ratioToWidth = ratioToWidth;
	return ta;
    }
}
