/*
 * PopupWindow.java
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

/**
 * A <code>PopupWindow</code> object is a pop-up window with no borders and
 * no menubar. The default layout for a window is <code>BorderLayout</code>.
 *
 * @version 	24 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class PopupWindow extends Window {
    /**
     * The top window of the origin component.
     */
    transient protected java.awt.Window originWindow;

    /**
     * The window listener for the top window of the origin component.
     */
    protected OriginWindowListener originWindowListener;

    /**
     * The mouse listener for the top window of the origin component.
     */
    protected GrabMouseListener grabMouseListener;


    /**
     * The mouse listener that grabs the mouse events of the origin window and
     * all of its subcomponents.
     */
    class GrabMouseListener
	implements MouseListener, MouseMotionListener, java.io.Serializable
    {
	public void mousePressed(MouseEvent e)  {
	    if (!containsMousePoint(e)) {
		e.consume();
		setVisible(false);
	    }
	}
	public void mouseClicked(MouseEvent e)  {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e)  {}
	public void mouseExited(MouseEvent e)   {}
	public void mouseDragged(MouseEvent e)  {}
	public void mouseMoved(MouseEvent e)    {}
    }

    /**
     * The window listener that listens the window event of the origin window.
     */
    class OriginWindowListener extends WindowAdapter
	implements java.io.Serializable
    {
	public void windowClosed(WindowEvent e) {
	    setVisible(false);
	}
	public void windowIconified(WindowEvent e) {
	    setVisible(false);
	}
    };

    /**
     * Constructs a new invisible pop-up window.
     *
     * @param parent the main application frame.
     */
    public PopupWindow(java.awt.Frame parent) {
	super(parent);
	originWindow = null;
	originWindowListener = new OriginWindowListener();
	grabMouseListener    = new GrabMouseListener();
    }


    /**
     * Shows this pop-up window at the x, y position relative to an origin
     * component. The origin component must be contained within the
     * component hierarchy of the pop-up window's parent. Both the origin
     * and the parent must be showing on the screen for this method to be
     * valid.
     *
     * @param origin the component which defines the coordinate space.
     * @param x      the x coordinate position to pop-up the window.
     * @param y      the y coordinate position to pop-up the window.
     */
    public synchronized void show(Component origin, int x, int y) {
	Component p = getParent();
	if (p == null) {
	    throw new NullPointerException("parent is null");
	}
	if (p != origin &&
	    p instanceof Container && !((Container)p).isAncestorOf(origin))
	{
	    throw new IllegalArgumentException(
					"origin not in parent's hierarchy");
	}
	if (p.getPeer() == null || !p.isShowing()) {
	    throw new RuntimeException("parent not showing on screen");
	}
	originWindow = getTopWindow(origin);
	if (originWindow != null) {
	    originWindow.addWindowListener(originWindowListener);
	    originWindow.addMouseListener(grabMouseListener);
	    originWindow.addMouseMotionListener(grabMouseListener);
	    grabMouseEvent(originWindow, grabMouseListener);
	}
	Point l = origin.getLocationOnScreen();
	setLocation(l.x + x, l.y + y);
	super.show();
    }

    /**
     * Shows this pop-up window. This method is not supported.
     */
    public void show() {
	throw new IllegalArgumentException("This method is not supported.");
    }

    /**
     * Hides this pop-up window.
     */
    public synchronized void hide() {
	if (originWindow != null) {
	    originWindow.removeWindowListener(originWindowListener);
	    originWindow.removeMouseListener(grabMouseListener);
	    originWindow.removeMouseMotionListener(grabMouseListener);
	    releaseMouseEvent(originWindow, grabMouseListener);
	    originWindow = null;
	}
	super.hide();
    }


    /**
     * Adds the specified mouse listener to the specified container
     * and all of its subcomponents.
     */
    protected void grabMouseEvent(Container c, GrabMouseListener l) {
	int ncomponents = c.getComponentCount();
	Component component[] = c.getComponents();
	for (int i = 0; i < ncomponents; i++) {
	    Component comp = component[i];
	    comp.addMouseListener(l);
	    comp.addMouseMotionListener(l);
	    if (comp instanceof Container) {
		grabMouseEvent((Container)comp, l);
	    }
	}
    }

    /**
     * Removes the specified mouse listener from the specified container
     * and all of its subcomponents.
     */
    protected void releaseMouseEvent(Container c, GrabMouseListener l) {
	int ncomponents = c.getComponentCount();
	Component component[] = c.getComponents();
	for (int i = 0; i < ncomponents; i++) {
	    Component comp = component[i];
	    comp.removeMouseListener(l);
	    comp.removeMouseMotionListener(l);
	    if (comp instanceof Container) {
		releaseMouseEvent((Container)comp, l);
	    }
	}
    }

    /**
     * Gets the top-level window of the specified component.
     */
    protected java.awt.Window getTopWindow(Component c) {
	while (c != null) {
	    if (c instanceof java.awt.Window) {
		return (java.awt.Window)c;
	    }
	    c = c.getParent();
	}
	return null;
    }

    /**
     * Checks if this pop-up window contains the point of the specified
     * mouse event.
     */
    protected boolean containsMousePoint(MouseEvent e) {
	if (!isVisible()) {
	    return false;
	}
	Component c = e.getComponent();
	Point p = e.getPoint();
	Point p1 = c.getLocationOnScreen();
	Point p2 = getLocationOnScreen();
	return contains(p1.x + p.x - p2.x, p1.y + p.y - p2.y);
    }
}
