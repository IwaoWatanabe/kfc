/*
 * Frame.java
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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;

/**
 * A Frame is a top-level window with a title and a border. The default
 * layout for a frame is <code>BorderLayout</code>.
 * <p>
 * If the JDK has the <code>sun.awt.windows.WWindowPeer#getFocusPeer()</code>
 * bug (the JDK for Windows 95/NT version 1.1.4 or before), an application
 * that uses the <code>jp.kyasu.awt</code> package should use this class
 * instead of <code>java.awt.Frame</code>.
 * <p>
 * Because of this bug, if a lightwight component is focus traversable,
 * an application will be hung up.
 *
 * @see 	java.awt.Frame
 * @see 	jp.kyasu.awt.AWTResources#HAS_FOCUS_BUG
 *
 * @version 	12 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class Frame extends java.awt.Frame {

    /**
     * If true, paints background.
     */
    protected boolean paintBackground = false;


    /**
     * Constructs a new instance of <code>Frame</code> that is
     * initially invisible.
     */
    public Frame() {
	super();
	setForeground(AWTResources.FOREGROUND_COLOR);
	setBackground(AWTResources.BACKGROUND_COLOR);

	enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
    }

    /**
     * Constructs a new, initially invisible <code>Frame</code> object
     * with the specified title.
     *
     * @param title the title for the frame.
     */
    public Frame(String title) {
	super(title);
	setForeground(AWTResources.FOREGROUND_COLOR);
	setBackground(AWTResources.BACKGROUND_COLOR);

	enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
    }


    /**
     * Processes component events occurring on this component.
     */
    protected void processComponentEvent(ComponentEvent e) {
	switch (e.getID()) {
	case ComponentEvent.COMPONENT_SHOWN:
	case ComponentEvent.COMPONENT_RESIZED:
	    paintBackground = true;
	    break;
	}
	super.processComponentEvent(e);
    }


    /**
     * Updates this component.
     */
    public void update(Graphics g) {
	if (paintBackground) {
	    paintBackground = false;
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
	}
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
