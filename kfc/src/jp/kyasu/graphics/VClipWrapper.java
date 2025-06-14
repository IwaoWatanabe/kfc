/*
 * VClipWrapper.java
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
import java.awt.Rectangle;

/**
 * The <code>VClipWrapper</code> class provides a clippng rectangle
 * for a visual object.
 *
 * @version 	29 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class VClipWrapper extends VWrapper {
    /** The width of the clip wrapper. */
    protected int width;

    /** The height of the clip wrapper. */
    protected int height;


    /**
     * Constructs a clip wrapper with the wrapped visual object.
     *
     * @param visualizable the wrapped visual object.
     */
    public VClipWrapper(Visualizable visualizable) {
	this(visualizable,
	     visualizable.getSize().width, visualizable.getSize().height);
    }

    /**
     * Constructs a clip wrapper with the wrapped visual object,
     * clipping width and clipping height.
     *
     * @param visualizable the wrapped visual object.
     * @param width        the width of the clip wrapper.
     * @param height       the height of the clip wrapepr.
     */
    public VClipWrapper(Visualizable visualizable, int width, int height) {
	super(visualizable);
	this.width  = width;
	this.height = height;
    }


    /**
     * Returns the size of the clipping rectangle.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	return new Dimension(width, height);
    }

    /**
     * Resizes the clipping rectangle to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	width  = d.width;
	height = d.height;
    }

    /**
     * Checks if the clipping rectangle is resizable.
     * @return <code>true</code>.
     * @see jp.kyasu.graphics.Visualizable#isResizable()
     */
    public boolean isResizable() {
	return true;
    }

    /**
     * Paints the visual object at the specified location, with the clipping
     * rectangle.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	int x = p.x;
	int y = p.y;
	Rectangle clip = g.getClipBounds();
	if (clip != null) {
	    g.clipRect(x, y, width, height);
	}
	else {
	    g = g.create();
	    g.clipRect(x, y, width, height);
	}
	Dimension d = visualizable.getSize();
	if (d.width  < width)  x += (width  - d.width)  / 2;
	if (d.height < height) y += (height - d.height) / 2;
	visualizable.paint(g, new Point(x, y));
	if (clip != null) {
	    g.setClip(clip.x, clip.y, clip.width, clip.height);
	}
	else {
	    g.dispose();
	}
    }

    /**
     * Returns a clone of this clip wrapper.
     */
    public Object clone() {
	VClipWrapper vcwrapper = (VClipWrapper)super.clone();
	vcwrapper.width  = width;
	vcwrapper.height = height;
	return vcwrapper;
    }
}
