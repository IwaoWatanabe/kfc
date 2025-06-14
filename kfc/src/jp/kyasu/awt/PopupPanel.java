/*
 * PopupPanel.java
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * A <code>PopupPanel</code> object is a panel that can be shown and hidden
 * in a pop-up window. The default layout for a panel is
 * <code>BorderLayout</code>.
 *
 * @see 	jp.kyasu.awt.PopupWindow
 *
 * @version 	30 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class PopupPanel extends Panel {
    /**
     * The pop-up window of the panel.
     */
    transient protected PopupWindow popupWindow;


    /**
     * Creates a new pop-up panel using the default layout manager.
     */
    public PopupPanel() {
	this(new Insets(0, 0, 0, 0));
    }

    /**
     * Creates a new pop-up panel with the specified insets.
     *
     * @param insets the insets of the panel.
     */
    public PopupPanel(Insets insets) {
	this(new BorderLayout(), insets);
    }

    /**
     * Creates a new pop-up panel with the specified layout manager.
     *
     * @param layout the layout manager for the panel.
     */
    public PopupPanel(LayoutManager layout) {
	this(layout, new Insets(0, 0, 0, 0));
    }

    /**
     * Creates a new pop-up panel with the specified layout manager and
     * insets.
     *
     * @param layout the layout manager for the panel.
     * @param insets the insets of the panel.
     */
    public PopupPanel(LayoutManager layout, Insets insets) {
	super(layout, insets);
    }


    /**
     * Shows this panel in a pop-up window at the x, y position relative
     * to an origin component.
     *
     * @param origin the component which defines the coordinate space.
     * @param x      the x coordinate position to pop-up the window.
     * @param y      the y coordinate position to pop-up the window.
     */
    public synchronized void showPopup(Component origin, int x, int y) {
	if (!origin.isShowing()) {
	    throw new RuntimeException("origin not showing on screen");
	}
	java.awt.Frame frame = getFrame(origin);
	if (frame == null) {
	    throw new NullPointerException("frame is null");
	}
	if (popupWindow != null && popupWindow.getParent() != frame) {
	    popupWindow.setVisible(false);
	    popupWindow.remove(this);
	    popupWindow = null;
	}
	if (popupWindow == null) {
	    popupWindow = new PopupWindow(frame);
	    popupWindow.setSize(getSize());
	    popupWindow.setForeground(getForeground());
	    popupWindow.setBackground(getBackground());
	    popupWindow.setFont(getFont());
	    popupWindow.setCursor(getCursor());
	    popupWindow.add(this, BorderLayout.CENTER);
	}
	popupWindow.show(origin, x, y);
    }

    /**
     * Hides the pop-up window of this panel.
     */
    public synchronized void hidePopup() {
	if (popupWindow != null) {
	    popupWindow.setVisible(false);
	}
    }


    /** Returns the frame of the specified component. */
    protected java.awt.Frame getFrame(Component c) {
	while (c != null && !(c instanceof java.awt.Frame)) {
	    c = c.getParent();
	}
	return (java.awt.Frame)c;
    }
}
