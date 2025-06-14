/*
 * FontModifier.java
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
import java.util.Enumeration;

/**
 * The <code>FontModifier</code> class implements the modifier that can
 * create the modified version of the immutable extended font object.
 * <p>
 * An example of the modification is:
 * <pre>
 *     ExtendedFont exFont = new ExtendedFont("SansSerif", Font.PLAIN, 12);
 *     FontModifier modifier = new FontModifier();
 *     modifier.put(FontModifier.BOLD,  true);
 *     modifier.put(FontModifier.SIZE,  14);
 *     modifier.put(FontModifier.COLOR, Color.red);
 *     exFont = modifier.modify(exFont);
 *         // exFont: new ExtendedFont("SansSerif", Font.BOLD, 14, Color.red)
 *     modifier.clear();
 *     modifier.put(FontModifier.ITALIC,    true);
 *     modifier.put(FontModifier.SIZE_DIFF, -2);
 *     modifier.put(FontModifier.COLOR,     FontModifier.NULL);
 *     exFont = modifier.modify(exFont);
 *         // exFont: new ExtendedFont("SansSerif", Font.BOLD | Font.ITALIC, 12)
 * </pre>
 *
 * @see		jp.kyasu.graphics.ExtendedFont
 *
 * @version 	08 May 1998
 * @author 	Kazuki YASUMATSU
 */
public class FontModifier extends Modifier {
    /**
     * The constant for the attribute "name". The value of this attribute
     * should be a String.
     */
    static public final String NAME      = "name";

    /**
     * The constant for the attribute "bold". The value of this attribute
     * should be a boolean.
     *
     * @see java.awt.Font#BOLD
     */
    static public final String BOLD      = "bold";

    /**
     * The constant for the attribute "italic". The value of this attribute
     * should be a boolean.
     *
     * @see java.awt.Font#ITALIC
     */
    static public final String ITALIC    = "italic";

    /**
     * The constant for the attribute "size". The value of this attribute
     * should be an integer.
     */
    static public final String SIZE      = "size";

    /**
     * The constant for the attribute "size". The value of this attribute
     * should be an integer. The modification is done by adding the
     * specified size to the size of the font to be modified.
     */
    static public final String SIZE_DIFF = "size_diff";

    /**
     * The constant for the attribute "color". The value of this attribute
     * should be a Color.
     */
    static public final String COLOR     = "color";

    /**
     * The constant for the attribute "underline". The value of this attribute
     * should be a boolean.
     */
    static public final String UNDERLINE = "underline";


    /** The minimum font size. */
    static protected final int MIN_FONT_SIZE = 2;


    /**
     * Constructs an empty font modifier.
     */
    public FontModifier() {
	super();
    }

    /**
     * Constructs a font modifier that has the same attributes and values as
     * the specified font modifier.
     *
     * @param modifier the font modifier.
     */
    public FontModifier(FontModifier modifier) {
	super(modifier);
    }


    /**
     * Creates a new font modifier by removing the attributes whose value
     * is "NULL".
     */
    public FontModifier deriveCleanFontModifier() {
	FontModifier modifier = new FontModifier();
	Enumeration ke = description.keys();
	Enumeration ve = description.elements();
	while (ke.hasMoreElements()) {
	    String key   = (String)ke.nextElement();
	    Object value = ve.nextElement();
	    if (!NULL.equals(value)) {
		modifier.put(key, value);
	    }
	}
	return modifier;
    }

    /**
     * Modifies the given extended font, i.e., Creates the modified version
     * of the given extended font.
     *
     * @param  exFont the given extended font.
     * @return the modified version of the given extended font;
     *         or the given extended font, if the modification has no
     *         effect on the given extended font.
     */
    public ExtendedFont modify(ExtendedFont exFont) {
	if (exFont == null)
	    throw new NullPointerException();

	if (isEmpty()) {
	    return exFont;
	}

	String name       = exFont.getName();
	int style         = exFont.getStyle();
	int size          = exFont.getSize();
	Color color       = exFont.getColor();
	boolean underline = exFont.isUnderline();

	boolean modified = false;
	Object value;

	if ((value = get(NAME)) != null && (value instanceof String)) {
	    String newName = (String)value;
	    if (!name.equals(newName)) {
		name = newName;
		modified = true;
	    }
	}

	if ((value = get(BOLD)) != null) {
	    if (NULL.equals(value)) {
		if ((style & Font.BOLD) != 0) {
		    style &= ~Font.BOLD;
		    modified = true;
		}
	    }
	    else if (value instanceof Boolean) {
		boolean isBold = ((Boolean)value).booleanValue();
		if (isBold) {
		    if ((style & Font.BOLD) == 0) {
			style |= Font.BOLD;
			modified = true;
		    }
		}
		else {
		    if ((style & Font.BOLD) != 0) {
			style &= ~Font.BOLD;
			modified = true;
		    }
		}
	    }
	}

	if ((value = get(ITALIC)) != null) {
	    if (NULL.equals(value)) {
		if ((style & Font.ITALIC) != 0) {
		    style &= ~Font.ITALIC;
		    modified = true;
		}
	    }
	    else if (value instanceof Boolean) {
		boolean isItalic = ((Boolean)value).booleanValue();
		if (isItalic) {
		    if ((style & Font.ITALIC) == 0) {
			style |= Font.ITALIC;
			modified = true;
		    }
		}
		else {
		    if ((style & Font.ITALIC) != 0) {
			style &= ~Font.ITALIC;
			modified = true;
		    }
		}
	    }
	}

	if ((value = get(SIZE)) != null && (value instanceof Integer)) {
	    int newSize = ((Integer)value).intValue();
	    if (newSize < MIN_FONT_SIZE) newSize = MIN_FONT_SIZE;
	    if (size != newSize) {
		size = newSize;
		modified = true;
	    }
	}
	else if ((value = get(SIZE_DIFF)) != null && (value instanceof Integer))
	{
	    int newSize = size + ((Integer)value).intValue();
	    if (newSize < MIN_FONT_SIZE) newSize = MIN_FONT_SIZE;
	    if (size != newSize) {
		size = newSize;
		modified = true;
	    }
	}

	if ((value = get(COLOR)) != null) {
	    if (NULL.equals(value)) {
		if (color != null) {
		    color = null;
		    modified = true;
		}
	    }
	    else if (value instanceof Color) {
		Color newColor = (Color)value;
		if (color == null || !color.equals(newColor)) {
		    color = newColor;
		    modified = true;
		}
	    }
	}

	if ((value = get(UNDERLINE)) != null) {
	    if (NULL.equals(value)) {
		if (underline) {
		    underline = false;
		    modified = true;
		}
	    }
	    else if (value instanceof Boolean) {
		boolean newUnderline = ((Boolean)value).booleanValue();
		if (underline != newUnderline) {
		    underline = newUnderline;
		    modified = true;
		}
	    }
	}

	if (modified) {
	    return new ExtendedFont(name, style, size, color, underline);
	}
	else {
	    return exFont;
	}
    }
}
