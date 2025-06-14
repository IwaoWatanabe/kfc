/*
 * VPlainBorder.java
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

import java.awt.Graphics;
import java.awt.Insets;

/**
 * The <code>VPlainBorder</code> class implements a plain border.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VPlainBorder extends VBorder {
    /** The insets. */
    protected Insets insets;


    /**
     * Constructs a plain border.
     */
    public VPlainBorder() {
	this(0, 0);
    }

    /**
     * Constructs a plain border with the specified insets.
     *
     * @param insets the insets of the plain border.
     */
    public VPlainBorder(Insets insets) {
	this(0, 0, insets);
    }

    /**
     * Constructs a plain border with the specified width and height.
     *
     * @param width  the width of the plain border.
     * @param height the height of the plain border.
     */
    public VPlainBorder(int width, int height) {
	this(width, height, new Insets(1, 1, 1, 1));
    }

    /**
     * Constructs a plain border with the specified width and height,
     * and the specified insets.
     *
     * @param width  the width of the plain border.
     * @param height the height of the plain border.
     * @param insets the insets of the plain border.
     */
    public VPlainBorder(int width, int height, Insets insets) {
	super(width, height);
	if (insets == null)
	    throw new NullPointerException();
	this.insets = insets;
    }


    /**
     * Returns the insets of this plain border.
     * @see jp.kyasu.graphics.VBorder#getInsets()
     */
    public Insets getInsets() {
	return new Insets(insets.top, insets.left, insets.bottom, insets.right);
    }

    /**
     * Sets the insets of this plain border to be the specified insets.
     *
     * @param insets the insets.
     */
    public void setInsets(Insets insets) {
	if (insets == null)
	    throw new NullPointerException();
	this.insets = insets;
    }

    /**
     * Paints the plain border at the specified location,
     * with the specified dimension.
     * @see jp.kyasu.graphics.VBorder#paint(java.awt.Graphics, int, int, int, int)
     */
    public void paint(Graphics g, int x, int y, int width, int height) {
	// top
	g.fillRect(x, y, width, insets.top);
	// left
	g.fillRect(x, y, insets.left, height);
	// bottom
	g.fillRect(x, y + height - insets.bottom, width, insets.bottom);
	// right
	g.fillRect(x + width - insets.right, y, insets.right, height);
    }

    /**
     * Returns a clone of this plain border.
     */
    public Object clone() {
	VPlainBorder vborder = (VPlainBorder)super.clone();
	vborder.insets = getInsets();
	return vborder;
    }
}
