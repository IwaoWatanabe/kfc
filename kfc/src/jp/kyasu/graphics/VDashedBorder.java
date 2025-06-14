/*
 * VDashedBorder.java
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
 * The <code>VDashedBorder</code> class implements a dashed line border.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VDashedBorder extends VBorder {

    /**
     * Constructs a dashed line border.
     */
    public VDashedBorder() {
	this(0, 0);
    }

    /**
     * Constructs a dashed line border with the specified width and height.
     *
     * @param width  the width of the dashed line border.
     * @param height the height of the dashed line border.
     */
    public VDashedBorder(int width, int height) {
	super(width, height);
    }


    /**
     * Returns the insets of this dashed line border.
     * @see jp.kyasu.graphics.VBorder#getInsets()
     */
    public Insets getInsets() {
	return new Insets(1, 1, 1, 1);
    }

    /**
     * Paints the dashed line border at the specified location,
     * with the specified dimension.
     * @see jp.kyasu.graphics.VBorder#paint(java.awt.Graphics, int, int, int, int)
     */
    public void paint(Graphics g, int x, int y, int width, int height) {
	int dx, dy, limit;
	// top
	dx = x;
	dy = y;
	limit = x + width;
	for (; dx < limit; dx += 2) {
	    g.drawLine(dx, dy, dx, dy);
	}
	// right
	dx = x + width - 1;
	dy = y;
	limit = y + height;
	for (; dy < limit; dy += 2) {
	    g.drawLine(dx, dy, dx, dy);
	}
	// bottom
	dx = x + width - 1;
	dy = y + height - 1;
	limit = x;
	for (; dx >= limit; dx -= 2) {
	    g.drawLine(dx, dy, dx, dy);
	}
	// left
	dx = x;
	dy = y + height - 1;
	limit = y;
	for (; dy >= limit; dy -= 2) {
	    g.drawLine(dx, dy, dx, dy);
	}
    }
}
