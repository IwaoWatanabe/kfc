/*
 * VAnchor.java
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

package jp.kyasu.graphics.html;

import jp.kyasu.graphics.VObject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

/**
 * The <code>VAnchor</code> class implements the target anchor in the
 * HTML documents.
 *
 * @version 	01 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class VAnchor extends VObject {
    /** The name of the anchor. */
    protected String name;

    static protected final Color AnchorColor = Color.blue;

    /**
     * Constructs an anchor with the specified name.
     *
     * @param name the name of the anchor.
     */
    public VAnchor(String name) {
	super(5, 10);
	if (name == null)
	    throw new NullPointerException();
	this.name = name;
    }

    /**
     * Returns the name of this anchor.
     *
     * @return the name of this anchor.
     */
    public String getName() {
	return name;
    }

    /**
     * Resizes the anchor to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	// do nothing
    }

    /**
     * Checks the anchor is resizable or not.
     * @return <code>false</code>.
     * @see jp.kyasu.graphics.Visualizable#isResizable()
     */
    public boolean isResizable() {
	return false;
    }

    /**
     * Paints the anchor at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	int x = p.x;
	int y = p.y;
	Color save = g.getColor();
	g.setColor(AnchorColor);
	g.drawLine(x + 2, y + 0, x + 2, y + 0);
	g.drawLine(x + 1, y + 1, x + 1, y + 2);
	g.drawLine(x + 3, y + 1, x + 3, y + 2);
	g.drawLine(x + 2, y + 3, x + 2, y + 9);
	g.drawLine(x + 0, y + 4, x + 4, y + 4);
	g.drawLine(x + 1, y + 8, x + 3, y + 8);
	g.drawLine(x + 0, y + 7, x + 0, y + 7);
	g.drawLine(x + 4, y + 7, x + 4, y + 7);
	g.setColor(save);
    }

    /**
     * Returns a clone of this visual object.
     */
    public Object clone() {
	VAnchor vanchor = (VAnchor)super.clone();
	vanchor.name = name; // share
	return vanchor;
    }
}
