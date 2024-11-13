/*
 * KComponent.java
 *
 * Copyright (c) 1997, 1998, 1999 Kazuki YASUMATSU.  All Rights Reserved.
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

import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.Visualizable;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

/**
 * A KComponent is an object having a graphical representation
 * that can be displayed on the screen and that can interact with the
 * user.
 * <p>
 * The KComponent class is the abstract base class for all components
 * in this package.
 *
 * @version 	20 Nov 1999
 * @author 	Kazuki YASUMATSU
 */
public abstract class KComponent extends java.awt.Component {
    /** The double buffering. */
    protected boolean doubleBuffered;
    transient protected Image offscreenBuffer;

    /** True if the keyboard focus traversal is enabled. */
    protected boolean focusTraversable;

    static protected Toolkit DefaultToolkit = Toolkit.getDefaultToolkit();


    protected KComponent() {
	setForeground(AWTResources.FOREGROUND_COLOR);
	setBackground(AWTResources.BACKGROUND_COLOR);
	doubleBuffered   = AWTResources.USE_DOUBLE_BUFFER;
	offscreenBuffer  = null;
	focusTraversable = true;
    }


    /**
     * Checks if this component should use a buffer to paint.
     */
    public boolean isDoubleBuffered() {
	return doubleBuffered;
    }

    /**
     * Set whether this component should use a buffer to paint.
     * If set to true, all the drawing from this component will be done in
     * an offscreen painting buffer. The offscreen painting buffer will the
     * be copied onto the screen.
     */
    public void setDoubleBuffered(boolean b) {
	if (doubleBuffered == b)
	    return;
	doubleBuffered  = b;
	offscreenBuffer = null;
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
     * Sets the foreground color of this component.
     */
    public void setForeground(Color c) {
	super.setForeground(c);
	offscreenBuffer = null;
    }

    /**
     * Sets the background color of this component.
     */
    public void setBackground(Color c) {
	super.setBackground(c);
	offscreenBuffer = null;
    }


    /**
     * Paints this component.
     */
    public void paint(Graphics g) {
	if (!isShowing())
	    return;

	Graphics pg = getPreferredGraphics(g);
	if (pg == null)
	    return;
	paintOn(pg);
	if (pg != g) {
	    pg.dispose();
	}

	syncGraphics(g);
    }

    /**
     * Paints this component on the specified graphics object.
     * All subclasses must override this method instead of paint().
     */
    protected void paintOn(Graphics g) {
    }

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
     * Synchronizes this graphics state.
     */
    public void syncGraphics() {
	if (offscreenBuffer != null && isShowing()) {
	    Graphics g = getGraphics();
	    //g.drawImage(offscreenBuffer, 0, 0, this);
	    g.drawImage(offscreenBuffer, 0, 0, null);
	    DefaultToolkit.sync();
	    g.dispose();
	}
    }

    /**
     * Synchronizes this graphics state with the specified clip rectangle.
     *
     * @param x the x coordinate of the rectangle to intersect the clip with
     * @param y the y coordinate of the rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     */
    public void syncGraphics(int x, int y, int width, int height) {
	if (offscreenBuffer != null && isShowing()) {
	    Graphics g = getGraphics();
	    g.clipRect(x, y, width, height);
	    //g.drawImage(offscreenBuffer, 0, 0, this);
	    g.drawImage(offscreenBuffer, 0, 0, null);
	    DefaultToolkit.sync();
	    g.dispose();
	}
    }

    /**
     * Synchronizes this graphics state on the specified graphics context.
     */
    public void syncGraphics(Graphics g) {
	if (offscreenBuffer != null) {
	    //g.drawImage(offscreenBuffer, 0, 0, this);
	    g.drawImage(offscreenBuffer, 0, 0, null);
	    DefaultToolkit.sync();
	}
    }

    /**
     * Creates a preferred graphics context for this component.
     */
    protected Graphics getPreferredGraphics() {
	return getPreferredGraphics(null);
    }

    /**
     * Creates a preferred graphics context for this component with the
     * specified graphics context.
     */
    protected Graphics getPreferredGraphics(Graphics g) {
	Dimension d = getSize();
	if (d.width <= 0 || d.height <= 0)
	    return null;

	Graphics pg;
	if (!doubleBuffered) {
	    if (g != null)
		pg = g;
	    else
		pg = getGraphics();
	}
	else {
	    if (offscreenBuffer == null ||
		offscreenBuffer.getWidth(null)  != d.width ||
		offscreenBuffer.getHeight(null) != d.height)
	    {
		offscreenBuffer = createImage(d.width, d.height);
		pg = offscreenBuffer.getGraphics();
		pg.setColor(getBackground());
		pg.fillRect(0, 0, d.width, d.height);
	    }
	    else {
		pg = offscreenBuffer.getGraphics();
	    }
	}

	Rectangle r = null;
	if (g != null)
	    r = g.getClipBounds();
	if (r == null) {
	    pg.setClip(0, 0, d.width, d.height);
	}
	else {
	    if (r.x < 0) r.x = 0;
	    if (r.y < 0) r.y = 0;
	    if (r.width > d.width) r.width = d.width;
	    if (r.height > d.height) r.height = d.height;
	    pg.setClip(r.x, r.y, r.width, r.height);
	}

	pg.setFont(getFont());
	pg.setColor(getForeground());

	/*if[JDK1.2]
	if (!AWTResources.RENDERING_HINTS.isEmpty()) {
	    java.awt.Graphics2D g2 = (java.awt.Graphics2D)pg;
	    g2.setRenderingHints(AWTResources.RENDERING_HINTS);
	}
	/*end[JDK1.2]*/

	return pg;
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


    // ================ ToolTip ================

    /**
     * The tooltip for this component.
     */
    protected ToolTip toolTip = null;


    /**
     * Return the tooltip string that has been set with
     * <code>setToolTipText()</code>.
     *
     * @return the string of the tool tip.
     */
    public String getToolTipText() {
	return (toolTip == null ? null : toolTip.getToolTipText());
    }

    /**
     * Return the tooltip visual object that has been set with
     * <code>setToolTipVisual()</code>.
     *
     * @return the visual object of the tool tip.
     */
    public Visualizable getToolTipVisual() {
	return (toolTip == null ? null : toolTip.getToolTipVisual());
    }

    /**
     * Registers the string to display in a ToolTip.
     *
     * @param string The string to display when the cursor lingers over the
     *               component. If string is null, then it turns off tool tip
     *               for this component.
     */
    public synchronized void setToolTipText(String string) {
	if (string == null) {
	    toolTip.hidePopup();
	    toolTip = null;
	}
	else {
	    if (toolTip == null)
		toolTip = new ToolTip(string);
	    else
		toolTip.setToolTipText(string);
	    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	    enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
    }

    /**
     * Registers the text object to display in a ToolTip.
     *
     * @param text The text object to display when the cursor lingers over
     *             the component. If text is null, then it turns off tool
     *             tip for this component.
     */
    public synchronized void setToolTipText(Text text) {
	if (text == null) {
	    toolTip.hidePopup();
	    toolTip = null;
	}
	else {
	    if (toolTip == null)
		toolTip = new ToolTip(text);
	    else
		toolTip.setToolTipText(text);
	    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	    enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
    }

    /**
     * Registers the visual object to display in a ToolTip.
     *
     * @param visual The visual object to display when the cursor lingers
     *               over the component. If visual is null, then it turns
     *               off tool tip for this component.
     */
    public synchronized void setToolTipVisual(Visualizable visual) {
	if (visual == null) {
	    toolTip.hidePopup();
	    toolTip = null;
	}
	else {
	    if (toolTip == null)
		toolTip = new ToolTip(visual);
	    else
		toolTip.setToolTipVisual(visual);
	    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	    enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
    }


    protected void processMouseEvent(MouseEvent e) {
	if (toolTip != null) {
	    switch (e.getID()) {
	    case MouseEvent.MOUSE_ENTERED:
		toolTip.hidePopup();
		toolTip.showPopup(this, e.getX(), getSize().height + 2,
				  ToolTip.TOOLTIP_DELAY);
		break;
	    case MouseEvent.MOUSE_PRESSED:
	    case MouseEvent.MOUSE_EXITED:
		toolTip.hidePopup();
		break;
	    }
	}
	super.processMouseEvent(e);
    }

    protected void processMouseMotionEvent(MouseEvent e) {
	if (toolTip != null) {
	    switch (e.getID()) {
	    case MouseEvent.MOUSE_MOVED:
		toolTip.setPopupLocationHint(e.getX(), getSize().height + 2);
		break;
	    }
	}
	super.processMouseMotionEvent(e);
    }
}
