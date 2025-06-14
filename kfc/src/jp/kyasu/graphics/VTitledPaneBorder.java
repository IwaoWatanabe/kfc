/*
 * VTitledPaneBorder.java
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

/**
 * The <code>VTitledPaneBorder</code> class implements a pane border
 * with a title that is a visual object.
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class VTitledPaneBorder extends VPaneBorder {
    /** The title of this pane border. */
    Visualizable title;


    /** The default insets for the title. */
    static protected final Insets TitleInsets = new Insets(4, 2, 4, 2);


    /**
     * Constructs an empty titled pane border.
     */
    public VTitledPaneBorder() {
	this(0, 0, "");
    }

    /**
     * Constructs a pane border with the specified title string.
     *
     * @param str the title string of the pane border.
     */
    public VTitledPaneBorder(String str) {
	this(0, 0, str);
    }

    /**
     * Constructs a pane border with the specified title text.
     *
     * @param text the title text of the pane border.
     */
    public VTitledPaneBorder(Text text) {
	this(0, 0, text);
    }

    /**
     * Constructs an empty titled pane border with the specified
     * width and height.
     *
     * @param width  the width of the pane border.
     * @param height the height of the pane border.
     */
    public VTitledPaneBorder(int width, int height) {
	this(width, height, "");
    }

    /**
     * Constructs a pane border with the specified width and height,
     * and the specified title string.
     *
     * @param width  the width of the pane border.
     * @param height the height of the pane border.
     * @param str    the title string of the pane border.
     */
    public VTitledPaneBorder(int width, int height, String str) {
	this(width, height, new Text(str));
    }

    /**
     * Constructs a pane border with the specified width and height,
     * and the specified title text.
     *
     * @param width  the width of the pane border.
     * @param height the height of the pane border.
     * @param text   the title text of the pane border.
     */
    public VTitledPaneBorder(int width, int height, Text text) {
	this(width, height, new VText(text));
    }

    /**
     * Constructs a pane border with the specified width and height,
     * and the specified title that is a visual object.
     *
     * @param width        the width of the pane border.
     * @param height       the height of the pane border.
     * @param visualizable the title of the pane border.
     */
    public VTitledPaneBorder(int width, int height, Visualizable visualizable)
    {
	super(width, height);
	if (visualizable == null)
	    throw new NullPointerException();
	title = visualizable;
	int th = title.getSize().height;
	setInsets(new Insets(th, th, th, th));
    }


    /**
     * Sets the insets of this pane border to be the specified insets.
     *
     * @param insets the insets.
     */
    public void setInsets(Insets insets) {
	if (insets == null)
	    throw new NullPointerException();
	int lineThickness = 2;
	int tLineThickness = Math.max(lineThickness,
				      title.getSize().height +
					TitleInsets.top + TitleInsets.bottom);
	this.insets = new Insets(Math.max(insets.top,    tLineThickness),
				 Math.max(insets.left,   lineThickness),
				 Math.max(insets.bottom, lineThickness),
				 Math.max(insets.right,  lineThickness));
    }

    /**
     * Returns the title of this pane border.
     *
     * @return the title of this pane border.
     */
    public Visualizable getTitle() {
	return title;
    }

    /**
     * Sets the title of this pane border to be the specified visual object.
     *
     * @param visualizable the visual object.
     */
    public void setTitle(Visualizable visualizable) {
	if (visualizable == null)
	    throw new NullPointerException();
	title = visualizable;
    }

    /**
     * Paints the titled pane border at the specified location,
     * with the specified dimension.
     * @see jp.kyasu.graphics.VBorder#paint(java.awt.Graphics, int, int, int, int)
     */
    public void paint(Graphics g, int x, int y, int width, int height) {
	int lineThickness = 2;
	int topY    = (insets.top - lineThickness)    / 2;
	int leftX   = (insets.left - lineThickness)   / 2;
	int bottomY = (insets.bottom - lineThickness) / 2;
	int rightX  = (insets.right - lineThickness)  / 2;
	int origY = y;
	x += leftX;
	y += topY;
	width  -= (leftX + rightX);
	height -= (topY + bottomY);

	int titleBeginX = x + lineThickness + 5;
	int titleEndX = titleBeginX + title.getSize().width +
					TitleInsets.left + TitleInsets.right;

	Color save = g.getColor();
	g.setColor(Color.white);
	// top
	//g.drawLine(x + 1, y + 1, x + width - 3, y + 1);
	g.drawLine(x + 1, y + 1, titleBeginX, y + 1);
	if (titleEndX <= x + width - 3) {
	    g.drawLine(titleEndX, y + 1, x + width - 3, y + 1);
	}
	// left
	g.drawLine(x + 1, y + 1, x + 1, y + height - 3);
	// bottom
	g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
	// right
	g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
	g.setColor(Color.gray);
	// top
	//g.drawLine(x, y, x + width - 2, y);
	g.drawLine(x, y, titleBeginX, y);
	if (titleEndX <= x + width - 2) {
	    g.drawLine(titleEndX, y, x + width - 2, y);
	}
	// left
	g.drawLine(x, y, x, y + height - 2);
	// bottom
	g.drawLine(x, y + height - 2, x + width - 2, y + height - 2);
	// right
	g.drawLine(x + width - 2, y, x + width - 2, y + height - 2);
	g.setColor(save);

	title.paint(g, new Point(titleBeginX + TitleInsets.left,
				 origY + TitleInsets.top));
    }

    /**
     * Returns a clone of this pane border.
     */
    public Object clone() {
	VTitledPaneBorder vborder = (VTitledPaneBorder)super.clone();
	vborder.title = title; // share
	return vborder;
    }
}
