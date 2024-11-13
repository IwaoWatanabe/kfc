/*
 * VHRBorder.java
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
 * The <code>VHRBorder</code> class implements a horizontal line border.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VHRBorder extends VBorder {

    /**
     * Constructs a horizontal line border.
     */
    public VHRBorder() {
	this(3);
    }

    /**
     * Constructs a horizontal line border with the specified height.
     *
     * @param width  the width of the horizontal line border.
     */
    public VHRBorder(int height) {
	this(1, height);
    }

    /**
     * Constructs a horizontal line border with the specified width and height.
     *
     * @param width  the width of the horizontal line border.
     * @param height the height of the horizontal line border.
     */
    public VHRBorder(int width, int height) {
	super(width, height);
    }


    /**
     * Returns the insets of this horizontal line border.
     * @see jp.kyasu.graphics.VBorder#getInsets()
     */
    public Insets getInsets() {
	return new Insets(1, 1, 1, 1);
    }

    /**
     * Paints the horizontal line border at the specified location,
     * with the specified dimension.
     * @see jp.kyasu.graphics.VBorder#paint(java.awt.Graphics, int, int, int, int)
     */
    public void paint(Graphics g, int x, int y, int width, int height) {
	Color save = g.getColor();
	g.setColor(Color.black);
	g.drawLine(x,             y,
		   x + width - 1, y);
	g.drawLine(x, y,
		   x, y + height - 1);
	g.setColor(Color.lightGray);
	g.drawLine(x,             y + height - 1,
		   x + width - 1, y + height - 1);
	g.drawLine(x + width - 1, y,
		   x + width - 1, y + height - 1);
	g.setColor(save);
    }
}
