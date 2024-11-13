/*
 * Panel.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * The <code>Panel</code> is the simplest lightweight container class.
 * The default layout manager for a panel is <code>FlowLayout</code>.
 *
 * @version 	30 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class Panel extends KContainer {
    /** The insets of the panel. */
    protected Insets insets;


    static protected final LayoutManager PanelLayout = new FlowLayout();

    static protected final Insets PanelInsets = new Insets(0, 0, 0, 0);


    /**
     * Creates a new panel using the default layout manager.
     */
    public Panel() {
	this(PanelLayout, PanelInsets);
    }

    /**
     * Creates a new panel with the specified insets.
     *
     * @param insets the insets of the panel.
     */
    public Panel(Insets insets) {
	this(PanelLayout, insets);
    }

    /**
     * Creates a new panel with the specified layout manager.
     *
     * @param layout the layout manager for the panel.
     */
    public Panel(LayoutManager layout) {
	this(layout, PanelInsets);
    }

    /**
     * Creates a new panel with the specified layout manager and insets.
     *
     * @param layout the layout manager for the panel.
     * @param insets the insets of the panel.
     */
    public Panel(LayoutManager layout, Insets insets) {
	setLayout(layout);
	setInsets(insets);
    }

    /**
     * Returns the insets of this panel.
     */
    public Insets getInsets() {
	return insets;
    }

    /**
     * Sets the insets of this panel.
     */
    public void setInsets(Insets insets) {
	if (insets == null)
	    throw new NullPointerException();
	this.insets = insets;
	invalidate();
    }
}
