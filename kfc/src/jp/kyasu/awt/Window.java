/*
 * Window.java
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * A <code>Window</code> object is a top-level window with no borders and no
 * menubar. It could be used to implement a pop-up menu. The default layout
 * for a window is <code>BorderLayout</code>.
 * <p>
 * If the JDK has the <code>sun.awt.windows.WWindowPeer#getFocusPeer()</code>
 * bug (the JDK for Windows 95/NT version 1.1.4 or before), an application
 * that uses the <code>jp.kyasu.awt</code> package should use this class
 * instead of <code>java.awt.Window</code>.
 * <p>
 * Because of this bug, if a lightwight component is focus traversable,
 * an application will be hung up.
 *
 * @see 	java.awt.Window
 * @see 	jp.kyasu.awt.AWTResources#HAS_FOCUS_BUG
 *
 * @version 	12 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class Window extends java.awt.Window {

    /**
     * Constructs a new invisible window.
     * <p>
     * The window is not initially visible. Call the <code>show</code>
     * method to cause the window to become visible.
     *
     * @param parent the main application frame.
     */
    public Window(java.awt.Frame parent) {
	super(parent);
	setForeground(AWTResources.FOREGROUND_COLOR);
	setBackground(AWTResources.BACKGROUND_COLOR);
    }


    /**
     * Updates this component.
     */
    public void update(Graphics g) {
	Color save = g.getColor();
	g.setColor(getBackground());
	Rectangle r = g.getClipBounds();
	if (r != null) {
	    g.fillRect(r.x, r.y, r.width, r.height);
	}
	else {
	    Dimension d = getSize();
	    g.fillRect(0, 0, d.width, d.height);
	}
	g.setColor(save);
	paint(g);
    }

    /**
     * Returns the child component of this Window which has focus if and
     * only if this Window is active.
     *
     * @return the component with focus, or null if no children have focus
     *         assigned to them.
     * @see    jp.kyasu.awt.AWTResources#HAS_FOCUS_BUG
     */
    public Component getFocusOwner() {
	if (AWTResources.HAS_FOCUS_BUG) {
	    return AWTResources.getFocusOwnerWorkaround(super.getFocusOwner());
	}
	else {
	    return super.getFocusOwner();
	}
    }
}
