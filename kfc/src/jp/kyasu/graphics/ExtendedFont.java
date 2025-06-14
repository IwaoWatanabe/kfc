/*
 * ExtendedFont.java
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.util.Hashtable;

/**
 * The <code>ExtendedFont</code> class implements an extended font object.
 * The extended font has a color attribute and an underline attribute.
 * <p>
 * The extended font is immutable.
 *
 * @version 	25 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class ExtendedFont implements java.io.Serializable {
    /** The font metrics of this font. */
    protected FontMetrics metrics;

    /** The color attribute of this font. */
    protected Color color;

    /** The underline attribute of this font. */
    protected boolean underline;


    /** The cashe for the font metrics. */
    static private final Hashtable FontMetricsCashe = new Hashtable();

    /**
     * Returns the font metrics in the cashe for the given font. If
     * the font metrics is not in the cashe, creates a font metrics and
     * puts it into the cache.
     */
    static protected synchronized FontMetrics getFontMetrics(Font font) {
	if (font == null)
	    throw new NullPointerException();
	FontMetrics fm = (FontMetrics)FontMetricsCashe.get(font);
	if (fm == null) {
	    fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
	    FontMetricsCashe.put(font, fm);
	}
	return fm;
    }


    /**
     * Constructs an extended font with the specified name, style and size.
     *
     * @param name  the name of the font.
     * @param style the style of the font.
     * @param size  the point size of the font.
     */
    public ExtendedFont(String name, int style, int size) {
	this(name, style, size, null, false);
    }

    /**
     * Constructs an extended font with the specified name, style, size
     * and color.
     *
     * @param name  the name of the font.
     * @param style the style of the font.
     * @param size  the point size of the font.
     * @param color the color of the font.
     */
    public ExtendedFont(String name, int style, int size, Color color) {
	this(name, style, size, color, false);
    }

    /**
     * Constructs an extended font with the specified name, style, size
     * and underline.
     *
     * @param name      the name of the font.
     * @param style     the style of the font.
     * @param size      the point size of the font.
     * @param underline the font is underlined.
     */
    public ExtendedFont(String name, int style, int size, boolean underline) {
	this(name, style, size, null, underline);
    }

    /**
     * Constructs an extended font with the specified name, style, size,
     * color and underline.
     *
     * @param name      the name of the font.
     * @param style     the style of the font.
     * @param size      the point size of the font.
     * @param color     the color of the font.
     * @param underline the font is underlined.
     */
    public ExtendedFont(String name, int style, int size,
			Color color, boolean underline)
    {
	if (name == null)
	    throw new NullPointerException();
	metrics = getFontMetrics(new Font(name, style, size));
	this.color     = color;
	this.underline = underline;
    }

    /**
     * Constructs an extended font with the specified font.
     *
     * @param font      the font object.
     */
    public ExtendedFont(Font font) {
	this(font, null, false);
    }

    /**
     * Constructs an extended font with the specified font and color.
     *
     * @param font      the font object.
     * @param color     the color of the font.
     */
    public ExtendedFont(Font font, Color color) {
	this(font, color, false);
    }

    /**
     * Constructs an extended font with the specified font and underline.
     *
     * @param font      the font object.
     * @param underline the font is underlined.
     */
    public ExtendedFont(Font font, boolean underline) {
	this(font, null, underline);
    }

    /**
     * Constructs an extended font with the specified font, color and
     * underline.
     *
     * @param font      the font object.
     * @param color     the color of the font.
     * @param underline the font is underlined.
     */
    public ExtendedFont(Font font, Color color, boolean underline) {
	if (font == null)
	    throw new NullPointerException();
	metrics = getFontMetrics(font);
	this.color     = color;
	this.underline = underline;
    }

    /** Returns the platform specific family name of the font. */
    public String getFamily() { return getFont().getFamily(); }

    /** Returns the logical name of the font. */
    public String getName()   { return getFont().getName(); }

    /**
     * Returns the style of the font.
     * @see java.awt.Font#PLAIN
     * @see java.awt.Font#BOLD
     * @see java.awt.Font#ITALIC
     */
    public int getStyle()     { return getFont().getStyle(); }

    /** Returns the point size of the font. */
    public int getSize()      { return getFont().getSize(); }

    /** Returns true if the font is plain. */
    public boolean isPlain()  { return getFont().isPlain(); }

    /** Returns true if the font is bold. */
    public boolean isBold()   { return getFont().isBold(); }

    /** Returns true if the font is italic. */
    public boolean isItalic() { return getFont().isItalic(); }

    /**
     * Returns the font object in this extended font.
     */
    public Font getFont() {
	return metrics.getFont();
    }

    /**
     * Returns the font metrics for this extended font.
     */
    public FontMetrics getFontMetrics() {
	return metrics;
    }

    /**
     * Returns the color of the extended font.
     */
    public Color getColor() {
	return color;
    }

    /**
     * Checks if this extended font is underlined.
     */
    public boolean isUnderline() {
	return underline;
    }

    /**
     * Creates a new font by replicating this font with a new color
     * object associated with it.
     *
     * @param  color the color object for the new font.
     * @return a new font.
     */
    public ExtendedFont deriveFont(Color color) {
	return new ExtendedFont(getFont(), color);
    }

    /**
     * Creates a new font by replicating this font with a new underline
     * attribute associated with it.
     *
     * @param  underline the underline attribute for the new font.
     * @return a new font.
     */
    public ExtendedFont deriveFont(boolean underline) {
	return new ExtendedFont(getFont(), underline);
    }

    /**
     * Returns a hashcode for this font.
     */
    public int hashCode() {
	int hash = getFont().hashCode();
	if (color != null) hash ^= color.hashCode();
	if (underline) hash ^= (new Boolean(true)).hashCode();
	return hash;
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
	    //return equalsStyle((ExtendedFont)anObject);
	    ExtendedFont exFont = (ExtendedFont)anObject;
	    return (metrics   == exFont.metrics   &&
		    underline == exFont.underline &&
		    (color == null ?
			exFont.color == null :
			color.equals(exFont.color)));
	}
	return false;
    }

    /**
     * Compares the styles of two fonts for equality.
     */
    public boolean equalsStyle(ExtendedFont exFont) {
	return (getStyle() == exFont.getStyle()    &&
		getSize()  == exFont.getSize()     &&
		getName().equals(exFont.getName()) &&
		underline == exFont.underline      &&
		(color == null ?
			exFont.color == null :
			color.equals(exFont.color)));
    }

    /**
     * Returns the string representation of this extended font.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("name=" + getName() + ",style=");
	if (isBold()) {
	    buffer.append(isItalic() ? "bolditalic" : "bold");
	}
	else {
	    buffer.append(isItalic() ? "italic" : "plain");
	}
	buffer.append(",size=" + getSize());
	if (underline)
	    buffer.append(",underline=true");
	if (color != null)
	    buffer.append(",color=" + color);
	return getClass().getName() + "[" + buffer.toString() + "]";
    }


    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	s.defaultReadObject();
	// Make the metrics to be unique.
	metrics = getFontMetrics(getFont());
    }
}
