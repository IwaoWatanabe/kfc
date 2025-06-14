/*
 * VOval.java
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
 * The <code>VOval</code> class implements a visual oval. The oval
 * creates different visual presentations according to the style.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VOval extends VObject {
    /** The style of the oval. */
    protected int style;


    /**
     * The oval style constat for the plain oval.
     */
    static public final int PLAIN   = 0;

    /**
     * The oval style constat for the outline of the oval.
     */
    static public final int OUTLINE = 1;


    /**
     * Constructs a plain oval.
     */
    public VOval() {
	this(0, 0);
    }

    /**
     * Constructs an oval with the specified style.
     *
     * @param style the style of the oval.
     */
    public VOval(int style) {
	this(0, 0, style);
    }

    /**
     * Constructs a plain oval with the specified width and height.
     *
     * @param width  the width of the oval.
     * @param height the height of the oval.
     */
    public VOval(int width, int height) {
	this(width, height, PLAIN);
    }

    /**
     * Constructs an oval with the specified width and height, and
     * the specified style.
     *
     * @param width  the width of the oval.
     * @param height the height of the oval.
     * @param style  the style of the oval.
     */
    public VOval(int width, int height, int style) {
	super(width, height);
	setStyle(style);
    }

    /**
     * Returns the style of the oval.
     *
     * @return the style of the oval.
     * @see    #PLAIN
     * @see    #OUTLINE
     */
    public int getStyle() {
	return style;
    }

    /**
     * Sets the style of the oval to be the specified style.
     *
     * @param style the style of the oval.
     * @see   #PLAIN
     * @see   #OUTLINE
     */
    public void setStyle(int style) {
	switch (style) {
	case PLAIN:
	case OUTLINE:
	    this.style = style;
	    return;
	}
	throw new IllegalArgumentException("improper style: " + style);
    }

    /**
     * Paints the oval at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	switch (style) {
	case OUTLINE:
	    g.drawOval(p.x, p.y, width, height);
	    break;
	case PLAIN:
	default:
	    g.fillOval(p.x, p.y, width, height);
	    break;
	}
    }

    /**
     * Returns a clone of this oval.
     */
    public Object clone() {
	VOval voval = (VOval)super.clone();
	voval.style = style;
	return voval;
    }
}
