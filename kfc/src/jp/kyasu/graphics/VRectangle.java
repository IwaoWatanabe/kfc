/*
 * VRectangle.java
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
import java.awt.Point;

/**
 * The <code>VRectangle</code> class implements a visual rectangle.
 * The rectangle creates different visual presentations according to
 * the style.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VRectangle extends VObject {
    /** The style of the rectangle. */
    protected int style;


    /**
     * The rectangle style constat for the plain rectangle.
     */
    static public final int PLAIN           = 0;

    /**
     * The rectangle style constat for the outline of the rectangle.
     */
    static public final int OUTLINE         = 1;

    /**
     * The rectangle style constat for the rounded corner rectangle.
     */
    static public final int ROUNDED         = 2;

    /**
     * The rectangle style constat for the outline of the rounded corner
     * rectangle.
     */
    static public final int ROUNDED_OUTLINE = 3;

    /**
     * The rectangle style constat for the 3-D raised rectangle.
     */
    static public final int RAISED          = 4;

    /**
     * The rectangle style constat for the outline of the 3-D raised rectangle.
     */
    static public final int RAISED_OUTLINE  = 5;

    /**
     * The rectangle style constat for the 3-D sunk rectangle.
     */
    static public final int SUNK            = 6;

    /**
     * The rectangle style constat for the outline of the 3-D sunk rectangle.
     */
    static public final int SUNK_OUTLINE    = 7;


    /**
     * Constructs a plain rectangle.
     */
    public VRectangle() {
	this(0, 0);
    }

    /**
     * Constructs a rectangle with the specified style.
     *
     * @param style the style of the rectangle.
     */
    public VRectangle(int style) {
	this(0, 0, style);
    }

    /**
     * Constructs a plain rectangle with the specified width and height.
     *
     * @param width  the width of the rectangle.
     * @param height the height of the rectangle.
     */
    public VRectangle(int width, int height) {
	this(width, height, PLAIN);
    }

    /**
     * Constructs a rectangle with the specified width and height, and
     * the specified style.
     *
     * @param width  the width of the rectangle.
     * @param height the height of the rectangle.
     * @param style  the style of the rectangle.
     */
    public VRectangle(int width, int height, int style) {
	super(width, height);
	setStyle(style);
    }

    /**
     * Returns the style of the rectangle.
     *
     * @return the style of the rectangle.
     * @see    #PLAIN
     * @see    #OUTLINE
     * @see    #ROUNDED
     * @see    #ROUNDED_OUTLINE
     * @see    #RAISED
     * @see    #RAISED_OUTLINE
     * @see    #SUNK
     * @see    #SUNK_OUTLINE
     */
    public int getStyle() {
	return style;
    }

    /**
     * Sets the style of the rectangle to be the specified style.
     *
     * @param style the style of the rectangle.
     * @see   #PLAIN
     * @see   #OUTLINE
     * @see   #ROUNDED
     * @see   #ROUNDED_OUTLINE
     * @see   #RAISED
     * @see   #RAISED_OUTLINE
     * @see   #SUNK
     * @see   #SUNK_OUTLINE
     */
    public void setStyle(int style) {
	switch (style) {
	case PLAIN:
	case OUTLINE:
	case ROUNDED:
	case ROUNDED_OUTLINE:
	case RAISED:
	case RAISED_OUTLINE:
	case SUNK:
	case SUNK_OUTLINE:
	    this.style = style;
	    return;
	}
	throw new IllegalArgumentException("improper style: " + style);
    }

    /**
     * Paints the rectangle at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	switch (style) {
	case OUTLINE:
	    g.drawRect(p.x, p.y, width, height); break;
	case ROUNDED:
	    g.fillRoundRect(p.x, p.y, width, height, 2, 2); break;
	case ROUNDED_OUTLINE:
	    g.drawRoundRect(p.x, p.y, width, height, 2, 2); break;
	case RAISED:
	    g.fill3DRect(p.x, p.y, width, height, true); break;
	case RAISED_OUTLINE:
	    g.draw3DRect(p.x, p.y, width, height, true); break;
	case SUNK:
	    g.fill3DRect(p.x, p.y, width, height, false); break;
	case SUNK_OUTLINE:
	    g.draw3DRect(p.x, p.y, width, height, false); break;
	case PLAIN:
	default:
	    g.fillRect(p.x, p.y, width, height); break;
	}
    }

    /**
     * Returns a clone of this rectangle.
     */
    public Object clone() {
	VRectangle vr = (VRectangle)super.clone();
	vr.style = style;
	return vr;
    }
}
