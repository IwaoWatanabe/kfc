/*
 * BorderedPanel.java
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

package jp.kyasu.awt;

import jp.kyasu.graphics.V3DBorder;
import jp.kyasu.graphics.VBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * The <code>BorderedPanel</code> is the bordered lightweight container class.
 * The default layout manager for a bordered panel is <code>BorderLayout</code>.
 *
 * @see		jp.kyasu.graphics.VBorder
 *
 * @version 	10 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class BorderedPanel extends KContainer {

    /** The visual border of this panel. */
    protected VBorder border;


    /**
     * Creates a new panel using the default layout manager and border.
     */
    public BorderedPanel() {
	this(new V3DBorder(false));
    }

    /**
     * Creates a new panel with the specified border.
     *
     * @param border the visual border of the panel.
     */
    public BorderedPanel(VBorder border) {
	this(new BorderLayout(), border);
    }

    /**
     * Creates a new panel with the specified layout manager.
     *
     * @param layout the layout manager for the panel.
     */
    public BorderedPanel(LayoutManager layout) {
	this(layout, new V3DBorder(false));
    }

    /**
     * Creates a new panel with the specified layout manager and border.
     *
     * @param layout the layout manager for the panel.
     * @param border the visual border of the panel.
     */
    public BorderedPanel(LayoutManager layout, VBorder border) {
	if (layout == null || border == null)
	    throw new NullPointerException();
	setLayout(layout);
	this.border = border;
    }

    /**
     * Returns the insets of this panel.
     */
    public Insets getInsets() {
	return border.getInsets();
    }

    /**
     * Returns the visual border of this panel.
     */
    public VBorder getBorder() {
	return border;
    }

    /**
     * Paints this panel.
     */
    public void paint(Graphics g) {
	super.paint(g);
	paintBorder(g);
    }

    /**
     * Paints the border of this panel.
     */
    protected void paintBorder(Graphics g) {
	Dimension d = getSize();
	border.paint(g, 0, 0, d.width, d.height);
    }
}
