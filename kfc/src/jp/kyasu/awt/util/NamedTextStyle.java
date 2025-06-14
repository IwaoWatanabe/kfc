/*
 * NamedTextStyle.java
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

package jp.kyasu.awt.util;

import jp.kyasu.graphics.ExtendedFont;
import jp.kyasu.graphics.TextStyle;

import java.awt.Color;
import java.awt.Font;

/**
 * The <code>NamedTextStyle</code> class implements the style for the text
 * object. The <code>NamedTextStyle</code> object has a style name.
 *
 * @see 	jp.kyasu.graphics.Text
 *
 * @version 	27 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class NamedTextStyle extends TextStyle {
    protected String styleName;


    /**
     * Constructs a named text style with the specified style name, font name,
     * font style and font size.
     *
     * @param styleName the style name of the text style.
     * @param name      the name of the font.
     * @param style     the style of the font.
     * @param size      the point size of the font.
     */
    public NamedTextStyle(String styleName, String name, int style, int size) {
	this(styleName, name, style, size, null, false);
    }

    /**
     * Constructs a named text style with the specified style name, font name,
     * font style, font size and color.
     *
     * @param styleName the style name of the text style.
     * @param name      the name of the font.
     * @param style     the style of the font.
     * @param size      the point size of the font.
     * @param color     the color of the font.
     */
    public NamedTextStyle(String styleName, String name, int style, int size,
			  Color color)
    {
	this(styleName, name, style, size, color, false);
    }

    /**
     * Constructs a named text style with the specified style name, font name,
     * font style, font size, color, and underline.
     *
     * @param styleName the style name of the text style.
     * @param name      the name of the font.
     * @param style     the style of the font.
     * @param size      the point size of the font.
     * @param color     the color of the font.
     * @param underline the font is underlined.
     */
    public NamedTextStyle(String styleName, String name, int style, int size,
			  Color color, boolean underline)
    {
	this(styleName, new ExtendedFont(name, style, size, color, underline));
    }

    /**
     * Constructs a named text style with the specified style name and font.
     *
     * @param styleName the style name of the text style.
     * @param font      the font of the style.
     */
    public NamedTextStyle(String styleName, Font font) {
	this(styleName, font, null, false);
    }

    /**
     * Constructs a named text style with the specified style name, font,
     * and color.
     *
     * @param styleName the style name of the text style.
     * @param font      the font of the style.
     * @param color     the color of the font.
     */
    public NamedTextStyle(String styleName, Font font, Color color) {
	this(styleName, font, color, false);
    }

    /**
     * Constructs a named text style with the specified style name, font,
     * color, and underline.
     *
     * @param styleName the style name of the text style.
     * @param font      the font of the style.
     * @param color     the color of the font.
     * @param underline the font is underlined.
     */
    public NamedTextStyle(String styleName, Font font,
			  Color color, boolean underline)
    {
	this(styleName, new ExtendedFont(font, color, underline));
    }

    /**
     * Constructs a named text style with the specified style name and
     * extended font.
     *
     * @param styleName the style name of the text style.
     * @param exFont    the extended font of the style.
     */
    public NamedTextStyle(String styleName, ExtendedFont exFont) {
	super(exFont);
	setStyleName(styleName);
    }


    /**
     * Returns the style name of this named text style.
     */
    public String getStyleName() {
	return styleName;
    }

    /**
     * Sets the style name of this named text style.
     */
    public void setStyleName(String styleName) {
	if (styleName == null)
	    throw new NullPointerException();
	this.styleName = styleName;
    }

    /**
     * Returns a hashcode for this named text style.
     */
    public int hashCode() {
	return super.hashCode() ^ styleName.hashCode();
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
	    NamedTextStyle textStyle = (NamedTextStyle)anObject;
	    return (styleName.equals(textStyle.styleName) &&
		    equalsFontAndAction(textStyle));
	}
	return false;
    }

    /**
     * Returns a clone of this text style.
     */
    public Object clone() {
	NamedTextStyle nts = (NamedTextStyle)super.clone();
	nts.styleName = styleName; // share
	return nts;
    }
}
