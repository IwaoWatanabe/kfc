/*
 * VColoredWrapper.java
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
import java.awt.Point;

/**
 * The <code>VColoredWrapper</code> class provides the color attributes
 * (foreground color and background color) for a visual object.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VColoredWrapper extends VWrapper {
    /** The foreground color. */
    protected Color foreground;

    /** The background color. */
    protected Color background;


    /**
     * Constructs a colored wrapper with the wrapped visual object.
     *
     * @param visualizable the wrapped visual object.
     */
    public VColoredWrapper(Visualizable visualizable) {
	this(visualizable, null, null);
    }

    /**
     * Constructs a colored wrapper with the wrapped visual object and
     * foreground color.
     *
     * @param visualizable the wrapped visual object.
     * @param foreground   the foreground color.
     */
    public VColoredWrapper(Visualizable visualizable, Color foreground) {
	this(visualizable, foreground, null);
    }

    /**
     * Constructs a colored wrapper with the wrapped visual object,
     * foreground color and background color.
     *
     * @param visualizable the wrapped visual object.
     * @param foreground   the foreground color.
     * @param background   the background color.
     */
    public VColoredWrapper(Visualizable visualizable,
			   Color foreground, Color background)
    {
	super(visualizable);
	this.foreground = foreground;
	this.background = background;
    }


    /**
     * Returns the foreground color.
     *
     * @return the foreground color.
     */
    public Color getForeground() {
	return foreground;
    }

    /**
     * Sets the foreground color to be the specified color.
     *
     * @param color the specified color.
     */
    public void setForeground(Color color) {
	foreground = color;
    }

    /**
     * Returns the background color.
     *
     * @return the background color.
     */
    public Color getBackground() {
	return background;
    }

    /**
     * Sets the background color to be the specified color.
     *
     * @param color the specified color.
     */
    public void setBackground(Color color) {
	background = color;
    }

    /**
     * Paints the visual object at the specified location, with the color
     * attributes.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	Color save = g.getColor();
	if (background != null) {
	    Dimension d = getSize();
	    g.setColor(background);
	    g.fillRect(p.x, p.y, d.width, d.height);
	}
	if (foreground != null) {
	    g.setColor(foreground);
	}
	visualizable.paint(g, p);
	g.setColor(save);
    }

    /**
     * Returns a clone of this visual object.
     */
    public Object clone() {
	VColoredWrapper vcwrapper = (VColoredWrapper)super.clone();
	vcwrapper.foreground = foreground;
	vcwrapper.background = background;
	return vcwrapper;
    }
}
