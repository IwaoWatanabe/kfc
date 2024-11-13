/*
 * VCheckbox.java
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
 * The <code>VCheckbox</code> class implements the visual checkbox that
 * acts as a checkbox model. The checkbox creates different visual
 * presentations according to the style and the state.
 *
 * @see		jp.kyasu.awt.Checkbox
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class VCheckbox extends VAbstractButton {
    /** The style of the checkbox. */
    protected int style;


    /**
     * The checkbox style constat for the exclusive style.
     */
    static public final int EXCLUSIVE = 0;

    /**
     * The checkbox style constat for the inclusive style.
     */
    static public final int INCLUSIVE = 1;


    /** The constat for the box size. */
    static protected final Dimension BoxSize = new Dimension(13, 13);

    /** The constat for the offset. */
    static protected final int Offset = 4;


    /**
     * Constructs a exclusive checkbox with the <code>false</code> state.
     */
    public VCheckbox() {
	this(false, EXCLUSIVE);
    }

    /**
     * Constructs a exclusive checkbox with the specified state.
     *
     * @param state the state of the checkbox.
     */
    public VCheckbox(boolean state) {
	this(state, EXCLUSIVE);
    }

    /**
     * Constructs a checkbox with the <code>false</code> state,
     * and the specified style.
     *
     * @param style the style of teh checkbox.
     */
    public VCheckbox(int style) {
	this(false, style);
    }

    /**
     * Constructs a checkbox with the specified state and the specified style.
     *
     * @param state the state of teh checkbox.
     * @param style the style of teh checkbox.
     */
    public VCheckbox(boolean state, int style) {
	this("", state, style);
    }

    /**
     * Constructs a exclusive checkbox with the <code>false</code> state,
     * and the specified string.
     *
     * @param str the string.
     */
    public VCheckbox(String str) {
	this(str, false, EXCLUSIVE);
    }

    /**
     * Constructs a exclusive checkbox with the specified string and the
     * specified state.
     *
     * @param str   the string.
     * @param state the state of the checkbox.
     */
    public VCheckbox(String str, boolean state) {
	this(str, state, EXCLUSIVE);
    }

    /**
     * Constructs a checkbox with the <code>false</code> state,
     * the specified string, and the specified style.
     *
     * @param str   the string.
     * @param style the style of the checkbox.
     */
    public VCheckbox(String str, int style) {
	this(str, false, style);
    }

    /**
     * Constructs a checkbox with the specified string, the specified state,
     * and the specified style.
     *
     * @param str   the string.
     * @param state the state of teh checkbox.
     * @param style the style of teh checkbox.
     */
    public VCheckbox(String str, boolean state, int style) {
	this(new Text(str), state, style);
    }

    /**
     * Constructs a exclusive checkbox with the <code>false</code> state,
     * and the specified text.
     *
     * @param text the text.
     */
    public VCheckbox(Text text) {
	this(text, false, EXCLUSIVE);
    }

    /**
     * Constructs a exclusive checkbox with the specified text and the
     * specified state.
     *
     * @param text  the text.
     * @param state the state of the checkbox.
     */
    public VCheckbox(Text text, boolean state) {
	this(text, state, EXCLUSIVE);
    }

    /**
     * Constructs a checkbox with the <code>false</code> state,
     * the specified text, and the specified style.
     *
     * @param text  the text.
     * @param style the style of the checkbox.
     */
    public VCheckbox(Text text, int style) {
	this(text, false, style);
    }

    /**
     * Constructs a checkbox with the specified text, the specified state,
     * and the specified style.
     *
     * @param text  the text.
     * @param state the state of teh checkbox.
     * @param style the style of teh checkbox.
     */
    public VCheckbox(Text text, boolean state, int style) {
	this(new VText(text), state, style);
    }

    /**
     * Constructs a exclusive checkbox with the <code>false</code> state,
     * and the specified visual object.
     *
     * @param visualizable the visual object.
     */
    public VCheckbox(Visualizable visualizable) {
	this(visualizable, false, EXCLUSIVE);
    }

    /**
     * Constructs a exclusive checkbox with the specified visual object
     * and the specified state.
     *
     * @param visualizable the visual object.
     * @param state        the state of the checkbox.
     */
    public VCheckbox(Visualizable visualizable, boolean state) {
	this(visualizable, state, EXCLUSIVE);
    }

    /**
     * Constructs a checkbox with the <code>false</code> state,
     * the specified visual object, and the specified style.
     *
     * @param visualizable the visual object.
     * @param style        the style of the checkbox.
     */
    public VCheckbox(Visualizable visualizable, int style) {
	this(visualizable, false, style);
    }

    /**
     * Constructs a checkbox with the specified visual object,
     * the specified state, and the specified style.
     *
     * @param visualizable the visual object.
     * @param state        the state of teh checkbox.
     * @param style        the style of teh checkbox.
     */
    public VCheckbox(Visualizable visualizable, boolean state, int style) {
	this(visualizable, true, false, state, style);
    }

    /**
     * Constructs a checkbox with the specified visual object,
     * the specified states, and the specified style.
     *
     * @param visualizable the visual object.
     * @param enabled      the enabled state of teh checkbox.
     * @param focused      the focused state of teh checkbox.
     * @param state        the state of teh checkbox.
     * @param style        the style of teh checkbox.
     */
    protected VCheckbox(Visualizable visualizable, boolean enabled,
			boolean focused, boolean state, int style)
    {
	super(visualizable, enabled, focused, state);
	setStyle(style);
    }


    /**
     * Creates a new checkbox by replicating this checkbox with a new visual
     * object associated with it.
     *
     * @param  visualizable the visual object for the new checkbox.
     * @return a new checkbox.
     */
    public VLabel deriveLabel(Visualizable visualizable) {
	if (visualizable == null)
	    throw new NullPointerException();
	return new VCheckbox(visualizable, enabled, focused, state, style);
    }

    /**
     * Returns the style of the checkbox.
     *
     * @return the style of the checkbox.
     * @see    #EXCLUSIVE
     * @see    #INCLUSIVE
     */
    public int getStyle() {
	return style;
    }

    /**
     * Sets the style of the checkbox to the specified style.
     *
     * @param style the style of the checkbox.
     * @see   #EXCLUSIVE
     * @see   #INCLUSIVE
     */
    public void setStyle(int style) {
	switch (style) {
	case EXCLUSIVE:
	case INCLUSIVE:
	    this.style = style;
	    return;
	}
	throw new IllegalArgumentException("improper style: " + style);
    }

    /**
     * Returns the size of this checkbox.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	Dimension d = getFocusedSize();
	int width  = d.width + (Offset * 2) + BoxSize.width;
	int height = Math.max(d.height, BoxSize.height);
	return new Dimension(width, height);
    }

    /**
     * Resizes the checkbox to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	int width  = d.width  - ((Offset * 2) + BoxSize.width);
	int height = d.height;
	Dimension size = getFocusedSize();
	if (size.width < width || size.height < height) {
	    setFocusedSize(new Dimension(width, height));
	}
    }

    /**
     * Paints the checkbox at the specified location with the component.
     *
     * @param g    the graphics.
     * @param p    the location.
     * @param comp the component used to make the disabled presentation.
     */
    public void paint(Graphics g, Point p, Component comp) {
	Dimension d = getSize();
	int bx = p.x + Offset;
	if (style == EXCLUSIVE) {
	    int by = p.y + ((d.height - (BoxSize.height - 1)) / 2);
	    paintExclusiveCheckbox(g, bx, by);
	}
	else {
	    int by = p.y + ((d.height - BoxSize.height) / 2);
	    paintInclusiveCheckbox(g, bx, by);
	}
	super.paint(g,
		    new Point(p.x + BoxSize.width + (Offset * 2), p.y),
		    comp);
    }

    /**
     * Returns a clone of this checkbox.
     */
    public Object clone() {
	VCheckbox vbox = (VCheckbox)super.clone();
	vbox.style = style;
	return vbox;
    }

    /**
     * Paints the exclusive checkbox at the specified location.
     */
    protected void paintExclusiveCheckbox(Graphics g, int x, int y)
    {
	Color save = g.getColor();
	if (enabled) {
	    g.setColor(Color.white);
	    g.drawLine(x + 4, y + 2, x + 7, y + 2);
	    g.drawLine(x + 3, y + 3, x + 8, y + 3);
	    g.drawLine(x + 2, y + 4, x + 9, y + 4);
	    g.drawLine(x + 2, y + 5, x + 9, y + 5);
	    g.drawLine(x + 2, y + 6, x + 9, y + 6);
	    g.drawLine(x + 2, y + 7, x + 9, y + 7);
	    g.drawLine(x + 3, y + 8, x + 8, y + 8);
	    g.drawLine(x + 4, y + 9, x + 7, y + 9);
	}
	g.setColor(Color.gray);
	g.drawLine(x + 4, y + 0, x + 7, y + 0);
	g.drawLine(x + 2, y + 1, x + 3, y + 1);
	g.drawLine(x + 8, y + 1, x + 9, y + 1);
	g.drawLine(x + 1, y + 2, x + 1, y + 3);
	g.drawLine(x + 0, y + 4, x + 0, y + 7);
	g.drawLine(x + 1, y + 8, x + 1, y + 9);
	g.setColor(Color.black);
	g.drawLine(x + 4, y + 1, x + 7, y + 1);
	g.drawLine(x + 2, y + 2, x + 3, y + 2);
	g.drawLine(x + 8, y + 2, x + 9, y + 2);
	g.drawLine(x + 2, y + 3, x + 2, y + 3);
	g.drawLine(x + 2, y + 8, x + 2, y + 8);
	g.drawLine(x + 1, y + 4, x + 1, y + 7);
	g.setColor(Color.lightGray);
	g.drawLine(x + 4, y + 10, x + 7, y + 10);
	g.drawLine(x + 2, y + 9, x + 3, y + 9);
	g.drawLine(x + 8, y + 9, x + 9, y + 9);
	g.drawLine(x + 9, y + 3, x + 9, y + 3);
	g.drawLine(x + 9, y + 8, x + 9, y + 8);
	g.drawLine(x + 10, y + 4, x + 10, y + 7);
	g.setColor(Color.white);
	g.drawLine(x + 4, y + 11, x + 7, y + 11);
	g.drawLine(x + 2, y + 10, x + 3, y + 10);
	g.drawLine(x + 8, y + 10, x + 9, y + 10);
	g.drawLine(x + 11, y + 4, x + 11, y + 7);
	g.drawLine(x + 10, y + 2, x + 10, y + 3);
	g.drawLine(x + 10, y + 8, x + 10, y + 9);
	if (state) {
	    g.setColor(Color.black);
	    g.drawLine(x + 5, y + 4, x + 6, y + 4);
	    g.drawLine(x + 4, y + 5, x + 7, y + 5);
	    g.drawLine(x + 4, y + 6, x + 7, y + 6);
	    g.drawLine(x + 5, y + 7, x + 6, y + 7);
	}
	g.setColor(save);
    }

    /**
     * Paints the inclusive checkbox at the specified location.
     */
    protected void paintInclusiveCheckbox(Graphics g, int x, int y) {
	int width  = BoxSize.width;
	int height = BoxSize.height;
	Color save = g.getColor();
	if (enabled) {
	    g.setColor(Color.white);
	    g.fillRect(x, y, width, height);
	}
	VBorder border = new V3DBorder(width, height, false);
	border.paint(g, x, y, width, height);
	if (state) {
	    g.setColor(Color.black);
	    Insets binsets = border.getInsets();
	    x += (binsets.left + 1);
	    y += (binsets.top  + 1);
	    g.drawLine(x + 0, y + 2, x + 0, y + 4);
	    g.drawLine(x + 1, y + 3, x + 1, y + 5);
	    g.drawLine(x + 2, y + 4, x + 2, y + 6);
	    g.drawLine(x + 3, y + 3, x + 3, y + 5);
	    g.drawLine(x + 4, y + 2, x + 4, y + 4);
	    g.drawLine(x + 5, y + 1, x + 5, y + 3);
	    g.drawLine(x + 6, y + 0, x + 6, y + 2);
	}
	g.setColor(save);
    }
}
