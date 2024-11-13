/*
 * VButton.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

/**
 * The <code>VButton</code> class implements the visual button that
 * acts as a button model. The button creates different visual presentations
 * according to the style and the state.
 *
 * @see 	jp.kyasu.awt.AbstractButton
 * @see 	jp.kyasu.awt.Button
 * @see 	jp.kyasu.awt.ToggleButton
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class VButton extends VAbstractButton {
    /** The border of the button. */
    protected V3DButtonBorder border;

    /** The insets of the button. */
    protected Insets insets;


    /**
     * The button style constat for the trigger style.
     */
    static public final int TRIGGER = 0;

    /**
     * The button style constat for the toggle style.
     */
    static public final int TOGGLE  = 1;


    /**
     * Constructs a trigger button with the <code>false</code> state.
     */
    public VButton() {
	this(false, TRIGGER);
    }

    /**
     * Constructs a trigger button with the specified state.
     *
     * @param state the state of the button.
     */
    public VButton(boolean state) {
	this(state, TRIGGER);
    }

    /**
     * Constructs a button with the <code>false</code> state,
     * and the specified style.
     *
     * @param style the style of teh button.
     */
    public VButton(int style) {
	this(false, style);
    }

    /**
     * Constructs a button with the specified state and the specified style.
     *
     * @param state the state of teh button.
     * @param style the style of teh button.
     */
    public VButton(boolean state, int style) {
	this("", state, style);
    }

    /**
     * Constructs a trigger button with the <code>false</code> state,
     * and the specified string.
     *
     * @param str the string.
     */
    public VButton(String str) {
	this(str, false, TRIGGER);
    }

    /**
     * Constructs a trigger button with the specified string and the
     * specified state.
     *
     * @param str   the string.
     * @param state the state of the button.
     */
    public VButton(String str, boolean state) {
	this(str, state, TRIGGER);
    }

    /**
     * Constructs a button with the <code>false</code> state,
     * the specified string, and the specified style.
     *
     * @param str   the string.
     * @param style the style of the button.
     */
    public VButton(String str, int style) {
	this(str, false, style);
    }

    /**
     * Constructs a button with the specified string, the specified state,
     * and the specified style.
     *
     * @param str   the string.
     * @param state the state of teh button.
     * @param style the style of teh button.
     */
    public VButton(String str, boolean state, int style) {
	this(new Text(str), state, style);
    }

    /**
     * Constructs a trigger button with the <code>false</code> state,
     * and the specified text.
     *
     * @param text the text.
     */
    public VButton(Text text) {
	this(text, false, TRIGGER);
    }

    /**
     * Constructs a trigger button with the specified text and the
     * specified state.
     *
     * @param text  the text.
     * @param state the state of the button.
     */
    public VButton(Text text, boolean state) {
	this(text, state, TRIGGER);
    }

    /**
     * Constructs a button with the <code>false</code> state,
     * the specified text, and the specified style.
     *
     * @param text  the text.
     * @param style the style of the button.
     */
    public VButton(Text text, int style) {
	this(text, false, style);
    }

    /**
     * Constructs a button with the specified text, the specified state,
     * and the specified style.
     *
     * @param text  the text.
     * @param state the state of teh button.
     * @param style the style of teh button.
     */
    public VButton(Text text, boolean state, int style) {
	this(new VText(text), state, style);
    }

    /**
     * Constructs a trigger button with the <code>false</code> state,
     * and the specified visual object.
     *
     * @param visualizable the visual object.
     */
    public VButton(Visualizable visualizable) {
	this(visualizable, false, TRIGGER);
    }

    /**
     * Constructs a trigger button with the specified visual object
     * and the specified state.
     *
     * @param visualizable the visual object.
     * @param state        the state of the button.
     */
    public VButton(Visualizable visualizable, boolean state) {
	this(visualizable, state, TRIGGER);
    }

    /**
     * Constructs a button with the <code>false</code> state,
     * the specified visual object, and the specified style.
     *
     * @param visualizable the visual object.
     * @param style        the style of the button.
     */
    public VButton(Visualizable visualizable, int style) {
	this(visualizable, false, style);
    }

    /**
     * Constructs a button with the specified visual object,
     * the specified state, and the specified style.
     *
     * @param visualizable the visual object.
     * @param state        the state of teh button.
     * @param style        the style of teh button.
     */
    public VButton(Visualizable visualizable, boolean state, int style) {
	this(visualizable, true, false, state, style);
    }

    /**
     * Constructs a button with the specified visual object,
     * states, and style.
     *
     * @param visualizable the visual object.
     * @param enabled      the enabled state of teh button.
     * @param focused      the focused state of teh button.
     * @param state        the state of teh button.
     * @param style        the style of teh button.
     */
    protected VButton(Visualizable visualizable, boolean enabled,
		      boolean focused, boolean state, int style)
    {
	this(visualizable, enabled, focused, state, style,
	     new V3DButtonBorder(!state, style));
    }

    /**
     * Constructs a button with the specified visual object,
     * states, style, and border.
     *
     * @param visualizable the visual object.
     * @param enabled      the enabled state of teh button.
     * @param focused      the focused state of teh button.
     * @param state        the state of teh button.
     * @param style        the style of teh button.
     * @param border       the border of teh button.
     */
    protected VButton(Visualizable visualizable, boolean enabled,
		      boolean focused, boolean state, int style,
		      V3DButtonBorder border)
    {
	super(visualizable, enabled, focused, state);
	this.border = border;
	this.insets = border.getInsets();
	insets =
	    new Insets(insets.top, insets.left, insets.bottom, insets.right);
	if (this.visualizable instanceof VText) {
	    insets.top    += 3;
	    insets.bottom += 3;
	    insets.left   += 4;
	    insets.right  += 4;
	}
	else {
	    insets.top    += 1;
	    insets.bottom += 1;
	    insets.left   += 1;
	    insets.right  += 1;
	}
    }


    /**
     * Creates a new button by replicating this button with a new visual
     * object associated with it.
     *
     * @param  visualizable the visual object for the new button.
     * @return a new button.
     */
    public VLabel deriveLabel(Visualizable visualizable) {
	if (visualizable == null)
	    throw new NullPointerException();
	return new VButton(visualizable, enabled, focused, state, getStyle());
    }

    /**
     * Returns the style of the button.
     *
     * @return the style of the button.
     * @see    #TRIGGER
     * @see    #TOGGLE
     */
    public int getStyle() {
	return border.getStyle();
    }

    /**
     * Sets the style of the button to the specified style.
     *
     * @param style the style of the button.
     * @see   #TRIGGER
     * @see   #TOGGLE
     */
    public void setStyle(int style) {
	border.setStyle(style);
    }

    /**
     * Sets the button to the specifed boolean state.
     * @see jp.kyasu.graphics.VAbstractButton#setState(boolean)
     */
    public void setState(boolean b) {
	super.setState(b);
	border.setRaised(!state);
    }

    /**
     * Returns the insets of this button.
     *
     * @return the insets of this button.
     */
    public Insets getInsets() {
	return insets;
    }

    /**
     * Sets the insets of this button to be the specified insets.
     *
     * @param insets the insets.
     */
    public void setInsets(Insets insets) {
	if (insets == null)
	    throw new NullPointerException();
	Insets binsets = border.getInsets();
	this.insets =
	    new Insets(Math.max(insets.top,    binsets.top),
		       Math.max(insets.left,   binsets.left),
		       Math.max(insets.bottom, binsets.bottom),
		       Math.max(insets.right,  binsets.right));
    }

    /**
     * Returns the size of this button.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	Dimension d = getFocusedSize();
	int width  = d.width  + (insets.left + insets.right);
	int height = d.height + (insets.top + insets.bottom);
	return new Dimension(width, height);
    }

    /**
     * Resizes the button to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	Insets binsets = border.getInsets();
	int width  = d.width  - (binsets.left + binsets.right);
	int height = d.height - (binsets.top + binsets.bottom);
	Dimension size = getFocusedSize();
	boolean needResize = false;
	if (width >= size.width) {
	    insets.left  = binsets.left  + ((width - size.width) / 2);
	    insets.right = binsets.right + ((width - size.width) -
						((width - size.width) / 2));
	    width = size.width;
	}
	else {
	    insets.left  = binsets.left;
	    insets.right = binsets.right;
	    needResize = true;
	}
	if (height >= size.height) {
	    insets.top    = binsets.top    + ((height - size.height) / 2);
	    insets.bottom = binsets.bottom + ((height - size.height) -
						((height - size.height) / 2));
	    height = size.height;
	}
	else {
	    insets.top    = binsets.top;
	    insets.bottom = binsets.bottom;
	    needResize = true;
	}

	if (needResize) {
	    if (width < 0) width = 0;
	    if (height < 0) height = 0;
	    setFocusedSize(new Dimension(width, height));
	}
    }

    /**
     * Paints the button at the specified location with the component.
     *
     * @param g    the graphics.
     * @param p    the location.
     * @param comp the component used to make the disabled presentation.
     */
    public void paint(Graphics g, Point p, Component comp) {
	Dimension d = getSize();
	int width  = d.width  - (insets.left + insets.right);
	int height = d.height - (insets.top + insets.bottom);
	if (width > 0 && height > 0) {
	    super.paint(g,
			new Point(p.x + insets.left, p.y + insets.top),
			comp,
			!state);
	}
	if (comp != null) {
	    d = comp.getSize();
	    paintBorder(g, 0, 0, d.width, d.height);
	}
	else {
	    paintBorder(g, p.x, p.y, d.width, d.height);
	}
    }

    /**
     * Returns a clone of this button.
     */
    public Object clone() {
	VButton vbutton = (VButton)super.clone();
	vbutton.border = border; // share
	vbutton.insets =
	    new Insets(insets.top, insets.left, insets.bottom, insets.right);
	return vbutton;
    }


    /**
     * Paints the border of the button with the specified dimension
     * at the specified location.
     */
    protected void paintBorder(Graphics g, int x, int y, int width, int height)
    {
	if (width > 0 && height > 0) {
	    border.paint(g, x, y, width, height);
	}
    }
}
