/*
 * VObject.java
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
 * The <code>VObject</code> class is an abstract base class for all objects
 * that implement the <code>Visualizable</code> interface and have a width
 * and a height dimension.
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public abstract class VObject implements Visualizable {
    /** The width of the visual object. */
    protected int width;

    /** The height of the visual object. */
    protected int height;


    /**
     * Constructs a visual object.
     */
    public VObject() {
	this(0, 0);
    }

    /**
     * Constructs a visual object with the specified width and height.
     *
     * @param width  the width of the visual object.
     * @param height the height of the visual object.
     */
    public VObject(int width, int height) {
	this.height = height;
	this.width  = width;
    }


    /**
     * Returns the size of this visual object.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	return new Dimension(width, height);
    }

    /**
     * Resizes the visual object to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	width  = d.width;
	height = d.height;
    }

    /**
     * Checks if the visual object is resizable.
     * @return <code>true</code>.
     * @see jp.kyasu.graphics.Visualizable#isResizable()
     */
    public boolean isResizable() {
	return true;
    }

    /**
     * Paints the visual object at the specified location.
     * The subclasses should override this method.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public abstract void paint(Graphics g, Point p);

    /**
     * Returns a clone of this visual object.
     */
    public Object clone() {
	try {
	    VObject vobject = (VObject)super.clone();
	    vobject.width  = width;
	    vobject.height = height;
	    return vobject;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }
}
