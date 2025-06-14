/*
 * VWrapper.java
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
 * The <code>VWrapper</code> class implements a visual wrapper that
 * holds a visual object.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class VWrapper implements Visualizable {
    /** The wrapped visual object. */
    protected Visualizable visualizable;


    /**
     * Constructs a visual wrapper with the wrapped visual object.
     *
     * @param visualizable the wrapped visual object.
     */
    public VWrapper(Visualizable visualizable) {
	if (visualizable == null)
	    throw new NullPointerException();
	this.visualizable = visualizable;
    }


    /**
     * Returns the wrapped visual object.
     *
     * @return the wrapped visual object.
     */
    public Visualizable getVisualizable() {
	return visualizable;
    }

    /**
     * Sets the wrapped visual object to be the specified visual object.
     *
     * @param visualizable the specified visual object.
     */
    public void setVisualizable(Visualizable visualizable) {
	if (visualizable == null)
	    throw new NullPointerException();
	this.visualizable = visualizable;
    }

    /**
     * Returns the size of this visual object.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	return visualizable.getSize();
    }

    /**
     * Resizes the visual object to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	visualizable.setSize(d);
    }

    /**
     * Checks if the visual object is resizable.
     * @see jp.kyasu.graphics.Visualizable#isResizable()
     */
    public boolean isResizable() {
	return visualizable.isResizable();
    }

    /**
     * Paints the visual object at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	visualizable.paint(g, p);
    }

    /**
     * Returns a clone of this visual object.
     */
    public Object clone() {
	try {
	    VWrapper vwrapper = (VWrapper)super.clone();
	    vwrapper.visualizable = (Visualizable)visualizable.clone();
	    return vwrapper;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }
}
