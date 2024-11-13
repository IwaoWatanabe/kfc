/*
 * ModTextStyle.java
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

import java.awt.Font;

/**
 * The <code>ModTextStyle</code> implements the text style with the font
 * modifier. The font modifier of the modifier text style is fired, when
 * the text with the modifier text style is in the paragraph whose style
 * has the base text style.
 *
 * @see 	jp.kyasu.graphics.ParagraphStyle
 *
 * @version 	22 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class ModTextStyle extends TextStyle {
    /** The font modifier. */
    protected FontModifier modifier;


    /**
     * Constructs a modifier text style that has the same contents as
     * the specified text style.
     *
     * @param defaultStyle the text style.
     */
    public ModTextStyle(TextStyle defaultStyle) {
	this(defaultStyle, null);
    }

    /**
     * Constructs a modifier text style that has the same contents as
     * the specified text style, with the specified font modifier.
     *
     * @param defaultStyle the text style.
     * @param modifier     the font modifier.
     */
    public ModTextStyle(TextStyle defaultStyle, FontModifier modifier) {
	super(defaultStyle.getExtendedFont());
	action = defaultStyle.action; // share
	setFontModifier(modifier);
    }

    /**
     * Constructs a modifier text style with the specified font.
     *
     * @param font the font for the style.
     */
    public ModTextStyle(Font font) {
	this(font, null);
    }

    /**
     * Constructs a modifier text style with the specified font and font
     * modifier.
     *
     * @param font     the font for the style.
     * @param modifier the font modifier.
     */
    public ModTextStyle(Font font, FontModifier modifier) {
	this(new ExtendedFont(font), modifier);
    }

    /**
     * Constructs a modifier text style with the specified extended font.
     *
     * @param exFont   the extended font for the style.
     */
    public ModTextStyle(ExtendedFont exFont) {
	this(exFont, null);
    }

    /**
     * Constructs a modifier text style with the specified extended font
     * and font modifier.
     *
     * @param exFont   the extended font for the style.
     * @param modifier the font modifier.
     */
    public ModTextStyle(ExtendedFont exFont, FontModifier modifier) {
	super(exFont);
	setFontModifier(modifier);
    }


    /**
     * Returns the font modifier.
     */
    public FontModifier getFontModifier() {
	return modifier;
    }

    /**
     * Sets the font modifier to be the specified font modifier.
     */
    protected void setFontModifier(FontModifier modifier) {
	if (modifier != null) {
	    modifier = modifier.deriveCleanFontModifier();
	    if (modifier.isEmpty()) {
		modifier = null;
	    }
	}
	this.modifier = modifier;
    }

    /**
     * Creates a new style by replicating this style with a new extended
     * font object and font modifier associated with it.
     *
     * @param exFont   the extended font object for the new style.
     * @param modifier the font modifier for the new style.
     * @return a new style.
     */
    public ModTextStyle deriveStyle(ExtendedFont exFont, FontModifier modifier)
    {
	ModTextStyle modStyle = (ModTextStyle)super.deriveStyle(exFont);
	modStyle.setFontModifier(modifier);
	return modStyle;
    }

    /**
     * Returns a concrete text style.
     *
     * @return the concrete text style.
     */
    public TextStyle concreteStyle() {
	TextStyle tStyle = new TextStyle(exFont);
	tStyle.action = action;
	return tStyle;
    }

    /**
     * Creates a new style by modifying this style to be based on the
     * specified base text style.
     *
     * @param  baseStyle the base text style.
     * @return the modified version of the base text style.
     * @see jp.kyasu.graphics.Text#baseStyleOn(int, int, jp.kyasu.graphics.TextStyle)
     */
    protected TextStyle basedOn(TextStyle baseStyle) {
	if (baseStyle == null)
	    throw new NullPointerException();

	ModTextStyle modStyle;

	if (action == null && modifier == null &&
	    (baseStyle instanceof ModTextStyle))
	{
	    modStyle = (ModTextStyle)baseStyle;
	    if (modStyle.action == null && modStyle.modifier == null) {
		return baseStyle;
	    }
	}

	if (modifier != null) {
	    modStyle = deriveStyle(modifier.modify(baseStyle.exFont), modifier);
	}
	else {
	    modStyle = deriveStyle(baseStyle.exFont, modifier);
	}
	modStyle.action = action; // share
	return modStyle;
    }

    /**
     * Returns a hashcode for this modifier text style.
     */
    public int hashCode() {
	int h = super.hashCode();
	if (modifier != null) h ^= modifier.hashCode();
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
	    ModTextStyle textStyle = (ModTextStyle)anObject;
	    return (equalsFontAndAction(textStyle) &&
		    (modifier == null ?
			textStyle.modifier == null :
			modifier.equals(textStyle.modifier)));
	}
	return false;
    }

    /**
     * Returns a clone of this modifier text style.
     */
    public Object clone() {
	ModTextStyle mts = (ModTextStyle)super.clone();
	mts.setFontModifier(modifier == null ?
				null : (FontModifier)modifier.clone());
	return mts;
    }

    /**
     * Returns the string representation of this modifier text style.
     */
    public String toString() {
	if (modifier == null) {
	    return getClass().getName() + "[]";
	}
	else {
	    return getClass().getName() + "[" + modifier.toString() + "]";
	}
    }
}
