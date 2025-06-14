/*
 * Visualizable.java
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
 * The interface for objects which can create a visual presentation
 * of itself.
 *
 * @version 	23 Sep 1997
 * @author 	Kazuki YASUMATSU
 */
public interface Visualizable extends Cloneable, java.io.Serializable {
    /**
     * Returns the size of this visual object.
     *
     * @return the size of this visual object.
     */
    public Dimension getSize();

    /**
     * Resizes the visual object to the specified dimension.
     *
     * @param d the visual object dimension.
     */
    public void setSize(Dimension d);

    /**
     * Checks if the visual object is resizable.
     */
    public boolean isResizable();

    /**
     * Paints the visual object at the specified location.
     * The color in the graphics is used as is.
     *
     * @param g the specified graphics.
     * @param p the location in the graphics to be painted.
     */
    public void paint(Graphics g, Point p);

    /**
     * Returns a clone of this visual object.
     *
     * @return a clone of this visual object.
     */
    public Object clone();
}
