/*
 * V3DButtonBorder.java
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
import java.awt.Graphics;
import java.awt.Insets;

/**
 * The <code>V3DButtonBorder</code> class implements a 3-D highlighted
 * border for the buttons. The border has a style, the trigger style or
 * the toggle style. The border creates different visual presentations
 * according to the style.
 *
 * @see		jp.kyasu.graphics.VButton
 * @see		jp.kyasu.graphics.VButton#TRIGGER
 * @see		jp.kyasu.graphics.VButton#TOGGLE
 *
 * @version 	02 May 1998
 * @author 	Kazuki YASUMATSU
 */
public class V3DButtonBorder extends V3DBorder {
    /** The style of the border. */
    protected int style;

    /** True if the border is thin. */
    protected boolean thin;


    /**
     * Constructs a raised border with the trigger style.
     */
    public V3DButtonBorder() {
	this(true, VButton.TRIGGER);
    }

    /**
     * Constructs a border with the trigger style, and the specified
     * boolean that determines the border to be raised or sunk.
     *
     * @param raised if true, the border is raised; otherwise, sunk.
     */
    public V3DButtonBorder(boolean raised) {
	this(raised, VButton.TRIGGER);
    }

    /**
     * Constructs a raised border with the specified style.
     *
     * @param style the style of the border.
     */
    public V3DButtonBorder(int style) {
	this(true, style);
    }

    /**
     * Constructs a border with the specified boolean that determines
     * the border to be raised or sunk, and the specified style.
     *
     * @param raised if true, the border is raised; otherwise, sunk.
     * @param style  the style of the border.
     */
    public V3DButtonBorder(boolean raised, int style) {
	this(raised, style, false);
    }

    /**
     * Constructs a border with the specified boolean that determines
     * the border to be raised or sunk, and the specified style.
     *
     * @param raised if true, the border is raised; otherwise, sunk.
     * @param style  the style of the border.
     * @param thin   if true, the border is thin.
     */
    public V3DButtonBorder(boolean raised, int style, boolean thin) {
	this(0, 0, raised, style, thin);
    }

    /**
     * Constructs a raised border with the trigger style, and the
     * specified width and height.
     *
     * @param width  the width of the border.
     * @param height the height of the border.
     */
    public V3DButtonBorder(int width, int height) {
	this(width, height, true, VButton.TRIGGER);
    }

    /**
     * Constructs a border with the trigger style, the specified width
     * and height, and the specified boolean that determines the border
     * to be raised or sunk,
     *
     * @param width  the width of the border.
     * @param height the height of the border.
     * @param raised if true, the border is raised; otherwise, sunk.
     */
    public V3DButtonBorder(int width, int height, boolean raised) {
	this(width, height, raised, VButton.TRIGGER);
    }

    /**
     * Constructs a raised border with the specified width and height,
     * and the specified style.
     *
     * @param width  the width of the border.
     * @param height the height of the border.
     * @param style  the style of the border.
     */
    public V3DButtonBorder(int width, int height, int style) {
	this(width, height, true, style);
    }

    /**
     * Constructs a border with the specified width and height,
     * the specified boolean that determines the border to be
     * raised or sunk, and the specified style.
     *
     * @param width  the width of the border.
     * @param height the height of the border.
     * @param raised if true, the border is raised; otherwise, sunk.
     * @param style  the style of the border.
     */
    public V3DButtonBorder(int width, int height, boolean raised, int style) {
	this(width, height, raised, style, false);
    }

    /**
     * Constructs a border with the specified width and height,
     * the specified boolean that determines the border to be
     * raised or sunk, and the specified style.
     *
     * @param width  the width of the border.
     * @param height the height of the border.
     * @param raised if true, the border is raised; otherwise, sunk.
     * @param style  the style of the border.
     * @param thin   if true, the border is thin.
     */
    public V3DButtonBorder(int width, int height, boolean raised, int style,
			   boolean thin)
    {
	super(width, height, raised);
	setStyle(style);
	this.thin = thin;
    }


    /**
     * Returns the insets of this 3-D border.
     * @see jp.kyasu.graphics.VBorder#getInsets()
     */
    public Insets getInsets() {
	if (thin) {
	    return new Insets(1, 1, 1, 1);
	}
	else {
	    return super.getInsets();
	}
    }

    /**
     * Returns the style of the border.
     *
     * @return the style of the border.
     * @see    jp.kyasu.graphics.VButton#TRIGGER
     * @see    jp.kyasu.graphics.VButton#TOGGLE
     */
    public int getStyle() {
	return style;
    }

    /**
     * Sets the style of the border to the specified style.
     *
     * @param style the style of the border.
     * @see   jp.kyasu.graphics.VButton#TRIGGER
     * @see   jp.kyasu.graphics.VButton#TOGGLE
     */
    public void setStyle(int style) {
	switch (style) {
	case VButton.TRIGGER:
	case VButton.TOGGLE:
	    this.style = style;
	    return;
	}
	throw new IllegalArgumentException("improper style: " + style);
    }

    /**
     * Returns a clone of this pane border.
     */
    public Object clone() {
	V3DButtonBorder vborder = (V3DButtonBorder)super.clone();
	vborder.style = style;
	return vborder;
    }


    /**
     * Paints the raised 3-D border at the specified location,
     * with the specified dimension.
     */
    protected void paintRaised(Graphics g, int x, int y, int width, int height)
    {
	if (thin) {
	    paintTopLeft(g, x, y, width, height, Color.white, null);
	    paintBottomRight(g, x, y, width, height, Color.gray, null);
	}
	else {
	    super.paintRaised(g, x, y, width, height);
	}
    }

    /**
     * Paints the sunk 3-D border at the specified location,
     * with the specified dimension.
     */
    protected void paintSunk(Graphics g, int x, int y, int width, int height)
    {
	if (thin) {
	    paintTopLeft(g, x, y, width, height, Color.gray, null);
	    paintBottomRight(g, x, y, width, height, Color.white, null);
	    return;
	}

	switch (style) {
	case VButton.TRIGGER:
	    paintTopLeft(g, x, y, width, height, Color.black, Color.gray);
	    paintBottomRight(g, x, y, width, height, Color.black, Color.gray);
	    break;
	case VButton.TOGGLE:
	    super.paintSunk(g, x, y, width, height);
	    break;
	}
    }
}
