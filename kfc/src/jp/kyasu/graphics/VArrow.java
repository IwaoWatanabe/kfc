/*
 * VArrow.java
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

/**
 * The <code>VArrow</code> class implements a visual arrow. The arrow
 * creates different visual presentations according to the direction.
 *
 * @version 	08 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VArrow extends VObject {
    /** The direction of the arrow. */
    protected int direction;


    /**
     * The arrow direction constat for the up direction.
     */
    static public final int UP    = 0;

    /**
     * The arrow direction constat for the down direction.
     */
    static public final int DOWN  = 1;

    /**
     * The arrow direction constat for the left direction.
     */
    static public final int LEFT  = 2;

    /**
     * The arrow direction constat for the right direction.
     */
    static public final int RIGHT = 3;


    /**
     * Constructs an arrow with the specified direction.
     *
     * @param direction the direction of the arrow.
     */
    public VArrow(int direction) {
	super(9, 9);
	setDirection(direction);
    }

    /**
     * Returns the direction of the arrow.
     *
     * @return the direction of the arrow.
     * @see    #UP
     * @see    #DOWN
     * @see    #LEFT
     * @see    #RIGHT
     */
    public int getDirection() {
	return direction;
    }

    /**
     * Sets the direction of the arrow to the specified direction.
     *
     * @param direction the direction of the checkbox.
     * @see   #UP
     * @see   #DOWN
     * @see   #LEFT
     * @see   #RIGHT
     */
    public void setDirection(int direction) {
	switch (direction) {
	case UP:
	case DOWN:
	case LEFT:
	case RIGHT:
	    this.direction = direction;
	    return;
	}
	throw new IllegalArgumentException("improper direction: " + direction);
    }

    /**
     * Paints the arrow at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	Dimension d = getSize();
	int x = p.x;
	int y = p.y;
	switch (direction) {
	case LEFT:
	    x += Math.max((d.width  - 4 + 1) / 2, 0);
	    //y += Math.max((d.height - 7 + 1) / 2, 0);
	    y += Math.max(d.height / 2, 0);
	    if (d.width > 0) g.drawLine(x + 0, y - 0, x + 0, y + 0);
	    if (d.width > 1) g.drawLine(x + 1, y - 1, x + 1, y + 1);
	    if (d.width > 2) g.drawLine(x + 2, y - 2, x + 2, y + 2);
	    if (d.width > 3) g.drawLine(x + 3, y - 3, x + 3, y + 3);
	    break;
	case RIGHT:
	    x += Math.max((d.width  - 4 + 1) / 2, 0) + 3;
	    //y += Math.max((d.height - 7 + 1) / 2, 0);
	    y += Math.max(d.height / 2, 0);
	    if (d.width > 0) g.drawLine(x - 0, y - 0, x - 0, y + 0);
	    if (d.width > 1) g.drawLine(x - 1, y - 1, x - 1, y + 1);
	    if (d.width > 2) g.drawLine(x - 2, y - 2, x - 2, y + 2);
	    if (d.width > 3) g.drawLine(x - 3, y - 3, x - 3, y + 3);
	    break;
	case UP:
	    //x += Math.max((d.width  - 7 + 1) / 2, 0);
	    x += Math.max(d.width / 2, 0);
	    y += Math.max((d.height - 4 + 1) / 2, 0);
	    if (d.height > 0) g.drawLine(x - 0, y + 0, x + 0, y + 0);
	    if (d.height > 1) g.drawLine(x - 1, y + 1, x + 1, y + 1);
	    if (d.height > 2) g.drawLine(x - 2, y + 2, x + 2, y + 2);
	    if (d.height > 3) g.drawLine(x - 3, y + 3, x + 3, y + 3);
	    break;
	case DOWN:
	    //x += Math.max((d.width  - 7 + 1) / 2, 0);
	    x += Math.max(d.width / 2, 0);
	    y += Math.max((d.height - 4 + 1) / 2, 0) + 3;
	    if (d.height > 0) g.drawLine(x - 0, y - 0, x + 0, y - 0);
	    if (d.height > 1) g.drawLine(x - 1, y - 1, x + 1, y - 1);
	    if (d.height > 2) g.drawLine(x - 2, y - 2, x + 2, y - 2);
	    if (d.height > 3) g.drawLine(x - 3, y - 3, x + 3, y - 3);
	    break;
	};
    }

    /**
     * Returns a clone of this arrow.
     */
    public Object clone() {
	VArrow va = (VArrow)super.clone();
	va.direction = direction;
	return va;
    }
}
