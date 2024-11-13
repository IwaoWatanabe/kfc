/*
 * KContainer.java
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ItemEvent;

/**
 * A KContainer object is a component that can contain other components.
 * <p>
 * The KContainer class is the abstract base class for all containers
 * in this package.
 *
 * @see 	java.awt.Container
 *
 * @version 	25 Jul 1998
 * @author 	Kazuki YASUMATSU
 */
public abstract class KContainer extends java.awt.Container {
    /** True if the keyboard focus traversal is enabled. */
    protected boolean focusTraversable;

    protected KContainer() {
	setForeground(AWTResources.FOREGROUND_COLOR);
	setBackground(AWTResources.BACKGROUND_COLOR);
	focusTraversable = true;
    }


    /**
     * Checks if the keyboard focus traversal is enabled.
     */
    public boolean isFocusTraversable() {
	if (focusTraversable) {
	    return super.isFocusTraversable();
	}
	return false;
    }

    /**
     * Enables or disables the keyboard focus traversal.
     */
    public void setFocusTraversable(boolean b) {
	focusTraversable = b;
    }

    /**
     * If true, notifies listeners directly without using the event queue.
     */
    public boolean isDirectNotification() {
	return AWTResources.IS_DIRECT_NOTIFICATION;
    }


    /**
     * Enables or disables this component.
     */
    public void setEnabled(boolean b) {
	if (b) {
	    super.enable();
	}
	else {
	    super.disable();
	}
    }

    /** Enables this component. */
    public void enable()          { setEnabled(true); }

    /* Enables or disables this component. */
    public void enable(boolean b) { setEnabled(b); }

    /** Disables this component. */
    public void disable()         { setEnabled(false); }


    /**
     * Shows or hides this component.
     */
    public void setVisible(boolean b) {
	if (b) {
	    super.show();
	}
	else {
	    super.hide();
	}
    }

    /* Shows this component. */
    public void show()          { setVisible(true); }

    /* Shows or hides this component. */
    public void show(boolean b) { setVisible(b); }

    /* Hides this component. */
    public void hide()          { setVisible(false); }


    /**
     * Returns the location of this component.
     */
    public Point getLocation() {
	return super.location();
    }

    /** Returns the location of this component. */
    public Point location() { return getLocation(); }


    /**
     * Returns the size of this component.
     */
    public Dimension getSize() {
	return super.size();
    }

    /** Returns the size of this component. */
    public Dimension size() { return getSize(); }


    /**
     * Returns the bounds of this component.
     */
    public Rectangle getBounds() {
	return super.bounds();
    }

    /* Returns the bounds of this component. */
    public Rectangle bounds() { return getBounds(); }


    /**
     * Moves and resizes this component.
     */
    public void setBounds(int x, int y, int width, int height) {
	super.reshape(x, y, width, height);
    }

    /** Moves and resizes this component. */
    public void reshape(int x, int y, int width, int height) {
	setBounds(x, y, width, height);
    }


    /**
     * Returns the preferred size of this component.
     */
    public Dimension getPreferredSize() {
	return super.preferredSize();
    }

    /** Returns the preferred size of this component. */
    public Dimension preferredSize() { return getPreferredSize(); }


    /**
     * Returns the minimum size of this component.
     */
    public Dimension getMinimumSize() {
	return super.minimumSize();
    }

    /** Returns the minimum size of this component. */
    public Dimension minimumSize() { return getMinimumSize(); }


    /**
     * Lays out this component.
     */
    public void doLayout() {
	super.layout();
    }

    /** Lays out this component. */
    public void layout() { doLayout(); }


    /**
     * Returns the insets of this component.
     */
    public Insets getInsets() {
	return super.insets();
    }

    /** Returns the insets of this component. */
    public Insets insets() { return getInsets(); }


    /**
     * Updates this component.
     */
    public void update(Graphics g) {
	paint(g);
    }

    /**
     * Paints this component and all of its subcomponents immediately.
     */
    public void repaintNow() {
	if (isShowing()) {
	    Graphics g = getGraphics();
	    if (g == null)
		return;
	    try {
		paint(g);
	    }
	    finally {
		g.dispose();
	    }
	}
    }

    /**
     * Requests that this component get the input focus.
     * <p>
     * For the <code>sun.awt.windows.WWindowPeer#getFocusPeer()</code> bug
     * (it exists in the JDK for Windows 95/NT version 1.1.4 or before),
     * if a lightwight component is focus traversable, an application will
     * be hung up.
     *
     * @see jp.kyasu.awt.AWTResources#HAS_FOCUS_BUG
     */
    public void requestFocus() {
	if (!AWTResources.HAS_FOCUS_BUG) {
	    super.requestFocus();
	}
	else {
	    synchronized (AWTResources.FOCUS_LOCK) {
		boolean save = AWTResources.IN_REQUEST_FOCUS;
		AWTResources.IN_REQUEST_FOCUS = true;
		super.requestFocus();
		AWTResources.IN_REQUEST_FOCUS = save;
	    }
	}
    }

    /**
     * Notifies this component that it has been added to a container
     * and if a peer is required, it should be created.
     * <p>
     * The component state is checked for avoiding the JDK bug.
     *
     * @see jp.kyasu.awt.AWTResources#checkComponentState(java.awt.Component)
     */
    public void addNotify() {
	super.addNotify();
	AWTResources.checkComponentState(this);
    }

    /**
     * Returns the top fram of this component.
     */
    public java.awt.Frame getFrame() {
	for (Container c = getParent(); c != null; c = c.getParent()) {
	    if (c instanceof java.awt.Frame)
		return (java.awt.Frame)c;
	}
	return null;
    }

    /**
     * Posts a 1.0 style event.
     */
    protected void postOldEvent(AWTEvent e) {
	Object src = e.getSource();
	int newid = e.getID();
	Event oe = null;

	switch(e.getID()) {
	case ActionEvent.ACTION_PERFORMED:
	    ActionEvent ae = (ActionEvent)e;
	    String cmd;
	    if (src instanceof jp.kyasu.awt.Button) {
		cmd = ((jp.kyasu.awt.Button)src).getLabel();
	    }
	    else if (src instanceof java.awt.MenuItem) {
		cmd = ((java.awt.MenuItem)src).getLabel();
	    }
	    else {
		cmd = ae.getActionCommand();
	    }
	    oe = new Event(src, 0, newid, 0, 0, 0, ae.getModifiers(), cmd);
	    break;

	case ItemEvent.ITEM_STATE_CHANGED:
	    ItemEvent ie = (ItemEvent)e;
	    Object arg;
	    if (src instanceof jp.kyasu.awt.List) {
		newid = (ie.getStateChange() == ItemEvent.SELECTED ?
	                 Event.LIST_SELECT : Event.LIST_DESELECT);
		arg = ie.getItem();
	    }
	    else {
		newid = Event.ACTION_EVENT;
		if (src instanceof jp.kyasu.awt.Choice) {
	            arg = ie.getItem();
		}
		else { // Checkbox
	            arg = new Boolean(ie.getStateChange()==ItemEvent.SELECTED);
		}
	    }
	    oe = new Event(src, newid, arg);
	    break;

	case AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED:
	    AdjustmentEvent aje = (AdjustmentEvent)e;
	    switch(aje.getAdjustmentType()) {
	    case AdjustmentEvent.UNIT_INCREMENT:
		newid = Event.SCROLL_LINE_DOWN;
		break;
	    case AdjustmentEvent.UNIT_DECREMENT:
		newid = Event.SCROLL_LINE_UP;
		break;
	    case AdjustmentEvent.BLOCK_INCREMENT:
		newid = Event.SCROLL_PAGE_DOWN;
		break;
	    case AdjustmentEvent.BLOCK_DECREMENT:
		newid = Event.SCROLL_PAGE_UP;
		break;
	    case AdjustmentEvent.TRACK:
		newid = Event.SCROLL_ABSOLUTE;
		break;
	    default:
		return;
	    }
	    oe = new Event(src, newid, new Integer(aje.getValue()));
	    break;
	}

	if (oe != null) {
	    postEvent(oe);
	}
    }
}
