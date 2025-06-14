/*
 * VBorder.java
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
import java.awt.Point;

/**
 * The <code>VBorder</code> class is the abstract base class for all
 * visual borders.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public abstract class VBorder extends VObject {

    /**
     * Constructs a visual border.
     */
    public VBorder() {
	this(0, 0);
    }

    /**
     * Constructs a visual border with the specified width and height.
     *
     * @param width  the width of the visual border.
     * @param height the height of the visual border.
     */
    public VBorder(int width, int height) {
	super(width, height);
    }


    /**
     * Returns the insets of this visual border.
     * The subclasses should override this method.
     *
     * @return the insets of this visual border.
     */
    public abstract Insets getInsets();

    /**
     * Paints the visual border at the specified location,
     * with the specified dimension.
     * The subclasses should override this method.
     *
     * @param g      the graphics.
     * @param x      the x location in the graphics.
     * @param y      the y location in the graphics.
     * @param width  the width of border.
     * @param height the height to border.
     */
    public abstract void paint(Graphics g, int x, int y, int width, int height);


    /**
     * Paints the visual border at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	paint(g, p.x, p.y, width, height);
    }
}
