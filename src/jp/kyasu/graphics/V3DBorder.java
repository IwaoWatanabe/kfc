/*
 * V3DBorder.java
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
 * The <code>V3DBorder</code> class implements a 3-D highlighted border.
 * The 3-D border has a raised state. The 3-D border creates different
 * visual presentations according to the raised state.
 *
 * @version 	02 May 1998
 * @author 	Kazuki YASUMATSU
 */
public class V3DBorder extends VBorder {
    /**
     * The boolean that determines whether the 3-D border appears to be
     * raised above the surface or sunk into the surface.
     */
    protected boolean raised;


    /**
     * Constructs a raised 3-D border.
     */
    public V3DBorder() {
	this(0, 0);
    }

    /**
     * Constructs a 3-D border with and the boolean that determines
     * the 3-D border to be raised or sunk.
     *
     * @param raised if true, the 3-D border is raised; otherwise, sunk.
     */
    public V3DBorder(boolean raised) {
	this(0, 0, raised);
    }

    /**
     * Constructs a raised 3-D border with the specified width and height.
     *
     * @param width  the width of the 3-D border.
     * @param height the height of the 3-D border.
     */
    public V3DBorder(int width, int height) {
	this(width, height, true);
    }

    /**
     * Constructs a 3-D border with the specified width and height,
     * and the boolean that determines the 3-D border to be raised or sunk.
     *
     * @param width  the width of the 3-D border.
     * @param height the height of the 3-D border.
     * @param raised if true, the 3-D border is raised; otherwise, sunk.
     */
    public V3DBorder(int width, int height, boolean raised) {
	super(width, height);
	this.raised = raised;
    }


    /**
     * Returns the insets of this 3-D border.
     * @see jp.kyasu.graphics.VBorder#getInsets()
     */
    public Insets getInsets() {
	return new Insets(2, 2, 2, 2);
    }

    /**
     * Checks whether this 3-D border appears to be raised or sunk.
     *
     * @return <code>true</code> if this 3-D border appears to be raised;
     *         <code>false</code> otherwise (to be sunk).
     */
    public boolean isRaised() {
	return raised;
    }

    /**
     * Sets this 3-D border to be raised or sunk.
     *
     * @param raised if true, this 3-D border is raised; otherwise, sunk.
     */
    public void setRaised(boolean raised) {
	this.raised = raised;
    }

    /**
     * Paints the 3-D border at the specified location,
     * with the specified dimension.
     * @see jp.kyasu.graphics.VBorder#paint(java.awt.Graphics, int, int, int, int)
     */
    public void paint(Graphics g, int x, int y, int width, int height) {
	Color save = g.getColor();
	if (raised) {
	    paintRaised(g, x, y, width, height);
	}
	else {
	    paintSunk(g, x, y, width, height);
	}
	g.setColor(save);
    }

    /**
     * Returns a clone of this pane border.
     */
    public Object clone() {
	V3DBorder vborder = (V3DBorder)super.clone();
	vborder.raised = raised;
	return vborder;
    }


    /**
     * Paints the raised 3-D border at the specified location,
     * with the specified dimension.
     */
    protected void paintRaised(Graphics g, int x, int y, int width, int height)
    {
	paintTopLeft(g, x, y, width, height, Color.lightGray, Color.white);
	paintBottomRight(g, x, y, width, height, Color.black, Color.gray);
    }

    /**
     * Paints the sunk 3-D border at the specified location,
     * with the specified dimension.
     */
    protected void paintSunk(Graphics g, int x, int y, int width, int height)
    {
	paintTopLeft(g, x, y, width, height, Color.gray, Color.black);
	paintBottomRight(g, x, y, width, height, Color.white, Color.lightGray);
    }

    /**
     * Paints the top and left corners at the specified location,
     * the specified dimension, and the specified colors.
     */
    protected void paintTopLeft(Graphics g,
				int x, int y, int width, int height,
				Color outer, Color inner)
    {
	g.setColor(outer);
	int i = 0;
	g.drawLine(x + i,             y + i,
		   x + width - 1 - i, y + i);
	g.drawLine(x + i, y + i,
		   x + i, y + height - 1 - i);
	if (inner != null) {
	    i = 1;
	    g.setColor(inner);
	    g.drawLine(x + i,             y + i,
		       x + width - 1 - i, y + i);
	    g.drawLine(x + i, y + i,
		       x + i, y + height - 1 - i);
	}
    }

    /**
     * Paints the bottom and right corners at the specified location,
     * the specified dimension, and the specified colors.
     */
    protected void paintBottomRight(Graphics g,
				    int x, int y, int width, int height,
				    Color outer, Color inner)
    {
	g.setColor(outer);
	int i = 0;
	g.drawLine(x + i,             y + height - 1 - i,
		   x + width - 1 - i, y + height - 1 - i);
	g.drawLine(x + width - 1 - i, y + i,
		   x + width - 1 - i, y + height - 1 - i);
	if (inner != null) {
	    i = 1;
	    g.setColor(inner);
	    g.drawLine(x + i,             y + height - 1 - i,
		       x + width - 1 - i, y + height - 1 - i);
	    g.drawLine(x + width - 1 - i, y + i,
		       x + width - 1 - i, y + height - 1 - i);
	}
    }
}
