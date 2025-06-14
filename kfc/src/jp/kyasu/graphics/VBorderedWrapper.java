/*
 * VBorderedWrapper.java
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
import java.awt.Insets;
import java.awt.Point;

/**
 * The <code>VBorderedWrapper</code> class provides the borders for
 * a visual object.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VBorderedWrapper extends VWrapper {
    /** The border. */
    protected VBorder border;


    /**
     * Constructs a bordered wrapper with the wrapped visual object and
     * the border.
     *
     * @param visualizable the wrapped visual object.
     * @param border       the border.
     */
    public VBorderedWrapper(Visualizable visualizable, VBorder border) {
	super(visualizable);
	if (border == null)
	    throw new NullPointerException();
	this.border = border;
    }

    /**
     * Returns the border of this bordered wrapper.
     *
     * @return the border of this bordered wrapper.
     */
    public VBorder getBorder() {
	return border;
    }

    /**
     * Sets the border of this bordered wrapper to be the specified border.
     *
     * @param border the specified border.
     */
    public void setBorder(VBorder border) {
	if (border == null)
	    throw new NullPointerException();
	this.border = border;
    }

    /**
     * Returns the size of this visual object.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	Dimension d = visualizable.getSize();
	Insets insets = border.getInsets();
	int width  = d.width  + (insets.left + insets.right);
	int height = d.height + (insets.top + insets.bottom);
	return new Dimension(width, height);
    }

    /**
     * Resizes the visual object to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	Dimension size = getSize();
	if (d.width == size.width && d.height == size.height)
	    return;
	Insets insets = border.getInsets();
	int width  = d.width  - (insets.left + insets.right);
	int height = d.height - (insets.top + insets.bottom);
	if (width < 0) width = 0;
	if (height < 0) height = 0;
	visualizable.setSize(new Dimension(width, height));
    }

    /**
     * Paints the visual object at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	Dimension d = visualizable.getSize();
	Insets insets = border.getInsets();
	if (d.width > 0 && d.height > 0) {
	    visualizable.paint(g, new Point(p.x+insets.left, p.y+insets.top));
	}
	d = getSize();
	border.paint(g, p.x, p.y, d.width, d.height);
    }

    /**
     * Returns a clone of this visual object.
     */
    public Object clone() {
	VBorderedWrapper vbwrapper = (VBorderedWrapper)super.clone();
	vbwrapper.border = (VBorder)border.clone();
	return vbwrapper;
    }
}
