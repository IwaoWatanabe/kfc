/*
 * TextStyle.java
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

import jp.kyasu.awt.AWTResources;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The <code>TextStyle</code> class implements the style for the text object.
 * The text style has a font attribute and an action attribute. The action
 * attribute is used for the clickable (sensible) text.
 * <p>
 * The text style is immutable.
 *
 * @see 	jp.kyasu.graphics.Text
 * @see 	jp.kyasu.graphics.ClickableTextAction
 *
 * @version 	22 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextStyle implements Cloneable, java.io.Serializable {
    /** The extended font. */
    protected ExtendedFont exFont;

    /** The clickable text action. */
    protected ClickableTextAction action;


    /**
     * The default text style constant.
     */
    static public final TextStyle DEFAULT_STYLE =
	new TextStyle(
		AWTResources.getResourceString("kfc.font.name",   "Dialog"),
		AWTResources.getResourceInteger("kfc.font.style", Font.PLAIN),
		AWTResources.getResourceInteger("kfc.font.size",  12));


    /**
     * Constructs a text style with the specified name, style and size.
     *
     * @param name  the name of the font.
     * @param style the style of the font.
     * @param size  the point size of the font.
     */
    public TextStyle(String name, int style, int size) {
	this(name, style, size, null, false);
    }

    /**
     * Constructs a text style with the specified name, style, size
     * and color.
     *
     * @param name  the name of the font.
     * @param style the style of the font.
     * @param size  the point size of the font.
     * @param color the color of the font.
     */
    public TextStyle(String name, int style, int size, Color color) {
	this(name, style, size, color, false);
    }

    /**
     * Constructs a text style with the specified name, style, size
     * and underline.
     *
     * @param name      the name of the font.
     * @param style     the style of the font.
     * @param size      the point size of the font.
     * @param underline the font is underlined.
     */
    public TextStyle(String name, int style, int size, boolean underline) {
	this(name, style, size, null, underline);
    }

    /**
     * Constructs a text style with the specified name, style, size,
     * color and underline.
     *
     * @param name      the name of the font.
     * @param style     the style of the font.
     * @param size      the point size of the font.
     * @param color     the color of the font.
     * @param underline the font is underlined.
     */
    public TextStyle(String name, int style, int size,
		     Color color, boolean underline)
    {
	this(new ExtendedFont(name, style, size, color, underline));
    }

    /**
     * Constructs a text style with the specified font.
     *
     * @param font the font for the style.
     */
    public TextStyle(Font font) {
	this(font, null, false);
    }

    /**
     * Constructs a text style with the specified font, color, and underline.
     *
     * @param font      the font for the style.
     * @param color     the color of the font.
     * @param underline the font is underlined.
     */
    public TextStyle(Font font, Color color, boolean underline) {
	this(new ExtendedFont(font, color, underline));
    }

    /**
     * Constructs a text style with the specified extended font.
     *
     * @param exFont the extended font for the style.
     */
    public TextStyle(ExtendedFont exFont) {
	if (exFont == null)
	    throw new NullPointerException();
	this.exFont = exFont;
    }


    /**
     * Returns the extended font of this style.
     */
    public ExtendedFont getExtendedFont() {
	return exFont;
    }

    /**
     * Returns the font of this style.
     */
    public Font getFont() {
	return exFont.getFont();
    }

    /**
     * Returns the font metrics for this style.
     */
    public FontMetrics getFontMetrics() {
	return exFont.getFontMetrics();
    }

    /**
     * Returns the clickable text action of this style.
     *
     * @return the clickable text action of this style.
     */
    public ClickableTextAction getClickableTextAction() {
	return action;
    }

    /**
     * Sets the clickable text action of this style to the specified action.
     *
     * @param action the clickable text action.
     */
    public void setClickableTextAction(ClickableTextAction action) {
	this.action = action;
    }

    /**
     * Checks if this style is clickable (this style has a clickable
     * text action).
     *
     * @return <code>true</code> if this style is clickable (this style
     *         has a clickable text action); <code>false</code> otherwise.
     */
    public boolean isClickable() {
	return (action != null);
    }

    /**
     * Creates a new style by replicating this style with a new font
     * object associated with it.
     *
     * @param  font the font object for the new style.
     * @return a new style.
     */
    public TextStyle deriveStyle(Font font) {
	return deriveStyle(new ExtendedFont(font));
    }

    /**
     * Creates a new style by replicating this style with a new extended
     * font object associated with it.
     *
     * @param  exFont the extended font object for the new style.
     * @return a new style.
     */
    public TextStyle deriveStyle(ExtendedFont exFont) {
	if (exFont == null)
	    throw new NullPointerException();
	TextStyle textStyle = (TextStyle)clone();
	textStyle.exFont = exFont;
	return textStyle;
    }

    /**
     * Creates a new style by replicating this style with a new clickable
     * text action associated with it.
     *
     * @param  action the clickable text action for the new style.
     * @return a new style.
     */
    public TextStyle deriveStyle(ClickableTextAction action) {
	TextStyle textStyle = (TextStyle)clone();
	textStyle.action = action;
	return textStyle;
    }

    /**
     * Creates a new style by modifying this style by a text style
     * modifier.
     *
     * @param  modifier the text style modifier.
     * @return a new style.
     */
    public TextStyle deriveStyle(TextStyleModifier modifier) {
	if (modifier == null)
	    throw new NullPointerException();
	TextStyle textStyle = modifier.modify(this);
	if (textStyle == this) {
	    textStyle = (TextStyle)clone();
	}
	return textStyle;
    }

    /**
     * Creates a new style by replicating this style with a bold style.
     *
     * @return a new bold style.
     */
    public TextStyle deriveBoldStyle() {
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.BOLD, true);
	return deriveStyle(modifier);
    }

    /**
     * Creates a new style by replicating this style with an italic style.
     *
     * @return a new italic style.
     */
    public TextStyle deriveItalicStyle() {
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.ITALIC, true);
	return deriveStyle(modifier);
    }

    /**
     * Returns a concrete text style.
     *
     * @return this text style.
     */
    public TextStyle concreteStyle() {
	return this;
    }

    /**
     * Creates a new style by modifying this style to be based on the
     * specified base text style.
     *
     * @param  baseStyle the base text style.
     * @return this text style.
     * @see jp.kyasu.graphics.Text#baseStyleOn(int, int, jp.kyasu.graphics.TextStyle)
     */
    protected TextStyle basedOn(TextStyle baseStyle) {
	return this;
    }

    /**
     * Draws the text with style to the specified graphics object. If the text
     * is null, draws the style (text attributes) only.
     * @param g          the graphics object.
     * @param text       the text to be drawn, or null.
     * @param offset     the start offset of the text to be drawn.
     * @param length     the number of characters in the text to be drawn.
     * @param isRunStart the offset is run start.
     * @param isRunEnd   the offset + length is run end.
     * @param x          the left of the drawing area.
     * @param y          the top of the drawing area.
     * @param width      the width of the drawing area.
     * @param height     the height of the drawing area.
     * @param baseLine   the base line of the drawing area.
     * @see jp.kyasu.graphics.TextScanner
     */
    public void drawText(Graphics g, char text[], int offset, int length,
				     boolean isRunStart, boolean isRunEnd,
				     int x, int y, int width, int height,
				     int baseLine)
    {

	Color color = null;
	if (exFont.getColor() != null && (text != null || exFont.isUnderline()))
	{
	    color = g.getColor();
	    g.setColor(exFont.getColor());
	}

	if (text != null) {
	    Font font = g.getFont();
	    g.setFont(exFont.getFont());
	    g.drawChars(text, offset, length, x, y + baseLine);
	    g.setFont(font);
	}

	if (exFont.isUnderline()) {
	    g.drawLine(x, y + baseLine, x + width, y + baseLine);
	}

	if (color != null) g.setColor(color);
    }

    /**
     * Returns a hashcode for this text style.
     */
    public int hashCode() {
	int h = exFont.hashCode();
	if (action != null) h ^= action.hashCode();
	return h;
    }

    /**
     * Compares two objects for equality.
     */
    public boolean equals(Object anObject) {
	if (this == anObject)
	    return true;
	if (anObject == null)
	    return false;
	if (getClass() == anObject.getClass()) {
	    return equalsFontAndAction((TextStyle)anObject);
	}
	return false;
    }

    /**
     * Compares font attributes and action attributes of two text styles
     * for equality.
     */
    protected boolean equalsFontAndAction(TextStyle textStyle) {
	return (exFont.equals(textStyle.exFont) &&
		(action == null ?
			textStyle.action == null :
			action.equals(textStyle.action)));
    }

    /**
     * Returns a clone of this text style.
     */
    public Object clone() {
	try {
	    TextStyle ts = (TextStyle)super.clone();
	    ts.exFont = exFont; // share
	    ts.action = action; // share
	    return ts;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns the string representation of this text style.
     */
    public String toString() {
	return getClass().getName() + "[" + exFont.toString() + "]";
    }
}
