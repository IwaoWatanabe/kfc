/*
 * BasicTSModifier.java
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

/**
 * The <code>BasicTSModifier</code> class is a basic implementation of
 * the interface for the text style modifier.
 * <p>
 * An example of the modification is:
 * <pre>
 *     TextStyle textStyle = new TextStyle("SansSerif", Font.PLAIN, 12);
 *     BasicTSModifier modifier = new BasicTSModifier();
 *     modifier.put(BasicTSModifier.BOLD,  true);
 *     modifier.put(BasicTSModifier.SIZE,  14);
 *     modifier.put(BasicTSModifier.COLOR, Color.red);
 *     textStyle = modifier.modify(textStyle);
 *         // textStyle: new TextStyle("SansSerif", Font.BOLD, 14, Color.red)
 *     modifier.clear();
 *     modifier.put(BasicTSModifier.ITALIC,    true);
 *     modifier.put(BasicTSModifier.SIZE_DIFF, -2);
 *     modifier.put(BasicTSModifier.COLOR,     BasicTSModifier.NULL);
 *     textStyle = modifier.modify(textStyle);
 *         // textStyle: new TextStyle("SansSerif", Font.BOLD | Font.ITALIC, 12)
 *     modifier.clear();
 *     modifier.put(BasicTSModifier.FONT,      new Font("Serif", Font.PLAIN, 12));
 *     modifier.put(BasicTSModifier.CLICKABLE, new ClickableTextAction("action"));
 *     textStyle = modifier.modify(textStyle);
 *         // textStyle: new TextStyle("Serif", Font.PLAIN, 12) with action
 *     modifier.clear();
 *     modifier.put(BasicTSModifier.FONT,      new ExtendedFont("Monospaced", Font.PLAIN, 14, Color.red));
 *     modifier.put(BasicTSModifier.CLICKABLE, BasicTSModifier.NULL);
 *     textStyle = modifier.modify(textStyle);
 *         // textStyle: new TextStyle("Monospaced", Font.PLAIN, 14, Color.red)
 * </pre>
 *
 * @see		jp.kyasu.graphics.TextStyleModifier
 * @see		jp.kyasu.graphics.Modifier
 * @see		jp.kyasu.graphics.FontModifier
 *
 * @version 	22 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class BasicTSModifier extends FontModifier implements TextStyleModifier
{
    /**
     * The constant for the attribute "font". The value of this attribute
     * should be a Font or an ExtendedFont.
     *
     * @see jp.kyasu.graphics.ExtendedFont
     */
    static public final String FONT      = "font";

    /**
     * The constant for the attribute "clickable". The value of this attribute
     * should be a ClickableTextAction.
     *
     * @see jp.kyasu.graphics.ClickableTextAction
     */
    static public final String CLICKABLE = "clickable";


    /**
     * Constructs an empty text style modifier.
     */
    public BasicTSModifier() {
	super();
    }

    /**
     * Constructs a text style modifier that has the same attributes and
     * values as the specified font modifier.
     *
     * @param modifier the font modifier.
     */
    public BasicTSModifier(FontModifier modifier) {
	super(modifier);
    }


    /**
     * Modifies the given text style, i.e., Creates the modified version
     * of the given text style.
     *
     * @param  tStyle the given text style.
     * @return the modified version of the given text style; or the given
     *         text style, if the modification has no effect on the given
     *         text style.
     * @see    jp.kyasu.graphics.TextStyleModifier#modify(jp.kyasu.graphics.TextStyle)
     */
    public TextStyle modify(TextStyle tStyle) {
	if (tStyle == null)
	    throw new NullPointerException();

	ExtendedFont exFont = tStyle.getExtendedFont();
	ClickableTextAction action = tStyle.getClickableTextAction();

	boolean modified = false;
	Object value;

	if ((value = get(FONT)) != null) {
	    if (value instanceof Font) {
		Font font = (Font)value;
		if (!exFont.getFont().equals(font)) {
		    exFont = new ExtendedFont(font,
					      exFont.getColor(),
					      exFont.isUnderline());
		    modified = true;
		}
	    }
	    else if (value instanceof ExtendedFont) {
		if (!exFont.equals(value)) {
		    exFont = (ExtendedFont)value;
		    modified = true;
		}
	    }
	}

	if ((value = get(CLICKABLE)) != null) {
	    if (NULL.equals(value)) {
		if (action != null) {
		    action = null;
		    modified = true;
		}
	    }
	    else if (value instanceof ClickableTextAction) {
		ClickableTextAction newAction = (ClickableTextAction)value;
		if (action == null || !action.equals(newAction)) {
		    action = newAction;
		    modified = true;
		}
	    }
	}

	if (tStyle instanceof ModTextStyle) {
	    FontModifier modifier = ((ModTextStyle)tStyle).getFontModifier();
	    if (modifier == null) {
		modifier = this.deriveFontModifier();
		modified = true;
	    }
	    else {
		FontModifier newModifier = (FontModifier)modify(modifier);
		if (modifier != newModifier) {
		    modifier = newModifier;
		    modified = true;
		}
	    }
	    if (!modified) {
		return tStyle;
	    }
	    tStyle = ((ModTextStyle)tStyle).deriveStyle(exFont, modifier);
	    tStyle.setClickableTextAction(action);
	    return tStyle;
	}
	else {
	    ExtendedFont newExFont = modify(exFont);
	    if (exFont != newExFont) {
		exFont = newExFont;
		modified = true;
	    }
	    if (!modified) {
		return tStyle;
	    }
	    tStyle = tStyle.deriveStyle(exFont);
	    tStyle.setClickableTextAction(action);
	    return tStyle;
	}
    }


    /**
     * Creates a new font modifier by replicating this modifier without
     * "FONT" and "CLICKABLE" attributes.
     */
    protected FontModifier deriveFontModifier() {
	FontModifier modifier = new FontModifier(this);
	modifier.remove(FONT);
	modifier.remove(CLICKABLE);
	return modifier;
    }
}
