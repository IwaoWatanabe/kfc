/*
 * VPaneBorder.java
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
 * The <code>VPaneBorder</code> class implements a pane border.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VPaneBorder extends VBorder {
    /** The insets. */
    protected Insets insets;


    /**
     * Constructs a pane border.
     */
    public VPaneBorder() {
	this(0, 0);
    }

    /**
     * Constructs a pane border with the specified width and height.
     *
     * @param width  the width of the pane border.
     * @param height the height of the pane border.
     */
    public VPaneBorder(int width, int height) {
	super(width, height);
	int lineThickness = 2;
	insets = new Insets(lineThickness, lineThickness,
			    lineThickness, lineThickness);
    }

    /**
     * Constructs a pane border with the specified insets.
     *
     * @param insets the insets of the pane border.
     */
    public VPaneBorder(Insets insets) {
	this(0, 0, insets);
    }

    /**
     * Constructs a pane border with the specified width and height,
     * and the specified insets.
     *
     * @param width  the width of the pane border.
     * @param height the height of the pane border.
     * @param insets the insets of the pane border.
     */
    public VPaneBorder(int width, int height, Insets insets) {
	super(width, height);
	if (insets == null)
	    throw new NullPointerException();
	setInsets(insets);
    }


    /**
     * Returns the insets of this pane border.
     * @see jp.kyasu.graphics.VBorder#getInsets()
     */
    public Insets getInsets() {
	return new Insets(insets.top, insets.left, insets.bottom, insets.right);
    }

    /**
     * Sets the insets of this pane border to be the specified insets.
     *
     * @param insets the insets.
     */
    public void setInsets(Insets insets) {
	if (insets == null)
	    throw new NullPointerException();
	this.insets = insets;
	/*
	int lineThickness = 2;
	this.insets = new Insets(Math.max(insets.top, lineThickness),
				 Math.max(insets.left, lineThickness),
				 Math.max(insets.bottom, lineThickness),
				 Math.max(insets.right, lineThickness));
	*/
    }

    /**
     * Paints the pane border at the specified location,
     * with the specified dimension.
     * @see jp.kyasu.graphics.VBorder#paint(java.awt.Graphics, int, int, int, int)
     */
    public void paint(Graphics g, int x, int y, int width, int height) {
	int lineThickness = 2;
	int topY    = (insets.top - lineThickness)    / 2;
	int leftX   = (insets.left - lineThickness)   / 2;
	int bottomY = (insets.bottom - lineThickness) / 2;
	int rightX  = (insets.right - lineThickness)  / 2;
	x += leftX;
	y += topY;
	width  -= (leftX + rightX);
	height -= (topY + bottomY);

	Color save = g.getColor();
	g.setColor(Color.white);

	// top
	if (insets.top >= lineThickness)
	    g.drawLine(x + 1, y + 1, x + width - 3, y + 1);
	// left
	if (insets.left >= lineThickness)
	    g.drawLine(x + 1, y + 1, x + 1, y + height - 3);
	// bottom
	if (insets.bottom >= lineThickness)
	    g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
	// right
	if (insets.right >= lineThickness)
	    g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);

	g.setColor(Color.gray);

	// top
	if (insets.top >= lineThickness)
	    g.drawLine(x, y, x + width - 2, y);
	// left
	if (insets.left >= lineThickness)
	    g.drawLine(x, y, x, y + height - 2);
	// bottom
	if (insets.bottom >= lineThickness)
	    g.drawLine(x, y + height - 2, x + width - 2, y + height - 2);
	if (insets.right >= lineThickness)
	// right
	    g.drawLine(x + width - 2, y, x + width - 2, y + height - 2);

	g.setColor(save);
    }

    /**
     * Returns a clone of this pane border.
     */
    public Object clone() {
	VPaneBorder vborder = (VPaneBorder)super.clone();
	vborder.insets = getInsets();
	return vborder;
    }
}
