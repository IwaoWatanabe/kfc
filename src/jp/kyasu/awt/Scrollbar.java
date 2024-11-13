/*
 * Scrollbar.java
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
import jp.kyasu.graphics.VArrow;
import jp.kyasu.graphics.VButton;

import java.awt.AWTEventMulticaster;
import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * The <code>Scrollbar</code> class embodies a scroll bar, a familiar
 * user-interface object. A scroll bar provides a convenient means for
 * allowing a user to select from a range of values.
 *
 * @version 	25 Jul 1998
 * @author 	Kazuki YASUMATSU
 */
public class Scrollbar extends KComponent
	implements Adjustable, MouseListener, MouseMotionListener
{
    protected int value;
    protected int visibleAmount;
    protected int maximum;
    protected int minimum;
    protected int orientation;
    protected int unitIncrement;
    protected int blockIncrement;
    transient protected AdjustmentListener adjustmentListener;

    protected V3DBorder marker;
    protected VButton leftButton;
    protected VButton rightButton;
    protected VButton upButton;
    protected VButton downButton;
    protected int thickness;

    transient protected int   lastPressedValue;
    transient protected int   lastPressedType = -1;
    transient protected Point lastPressedPoint;

    protected ScrollActionListener scrollActionListener;
    protected Timer scrollTimer;


    /**
     * A constant that indicates a horizontal scroll bar.
     */
    static public final int HORIZONTAL = java.awt.Scrollbar.HORIZONTAL;

    /**
     * A constant that indicates a vertical scroll bar.
     */
    static public final int VERTICAL   = java.awt.Scrollbar.VERTICAL;


    /**
     * The default scrollbar thickness.
     */
    static public final int SCROLLBAR_THICKNESS =
	AWTResources.getResourceInteger("kfc.scrollbar.thickness", 16);

    /**
     * The default foreground color of the scroll bar.
     */
    static protected final Color FOREGROUND_COLOR =
	AWTResources.getResourceColor("kfc.scrollbar.foreground", Color.black);

    /**
     * The default background color of the scroll bar.
     */
    static protected final Color BACKGROUND_COLOR =
	AWTResources.getResourceColor("kfc.scrollbar.background",
				      new Color(224, 224, 224));

    /**
     * The default selection background color of the scroll bar.
     */
    static protected final Color SELECTION_BACKGROUND_COLOR =
	AWTResources.getResourceColor("kfc.scrollbar.selectionBackground",
				      Color.black);

    /**
     * The default button color of the scroll bar.
     */
    static protected final Color BUTTON_COLOR =
	AWTResources.getResourceColor("kfc.scrollbar.buttonColor",
				      Color.lightGray);

    /**
     * The default marker color of the scroll bar.
     */
    static protected final Color MARKER_COLOR =
	AWTResources.getResourceColor("kfc.scrollbar.markerColor",
				      Color.lightGray);

    /**
     * The default first interval of scrolling.
     */
    static protected final int SCROLL_FIRST_INTERVAL =
	AWTResources.getResourceInteger("kfc.scrollbar.firstInterval", 500);

    /**
     * The default interval of scrolling.
     */
    static protected final int SCROLL_INTERVAL       =
	AWTResources.getResourceInteger("kfc.scrollbar.interval", 100);


    /**
     * Constructs a new vertical scroll bar.
     */
    public Scrollbar() {
	this(VERTICAL, 0, 10, 0, 100);
    }

    /**
     * Constructs a new scroll bar with the specified orientation.
     * @param orientation the orientation of the scroll bar.
     */
    public Scrollbar(int orientation) {
	this(orientation, 0, 10, 0, 100);
    }

    /**
     * Constructs a new scroll bar with the specified orientation,
     * initial value, page size, and minimum and maximum values.
     * @param orientation the orientation of the scroll bar.
     * @param value       the initial value of the scroll bar.
     * @param visible     the size of the scroll bar's bubble, representing
     *                    the visible portion; the scroll bar uses this
     *                    value when paging up or down by a page.
     * @param minimum     the minimum value of the scroll bar.
     * @param maximum     the maximum value of the scroll bar.
     */
    public Scrollbar(int orientation, int value, int visible,
		     int minimum, int maximum)
    {
	switch (orientation) {
	case HORIZONTAL:
	case VERTICAL:
	    this.orientation = orientation;
	    break;
	default:
	    throw new IllegalArgumentException("illegal scrollbar orientation");
	}
	unitIncrement = 1;
	blockIncrement = 10;
	setValues(value, visible, minimum, maximum);

	thickness = SCROLLBAR_THICKNESS;

	marker = new V3DBorder();
	Dimension d = new Dimension(thickness, thickness);
	makeHorizontalButtons(d);
	makeVerticalButtons(d);

	addMouseListener(this);
	addMouseMotionListener(this);

	setForeground(FOREGROUND_COLOR);
	setBackground(BACKGROUND_COLOR);

	lastPressedValue = value;
	lastPressedType  = -1;
	lastPressedPoint = null;

	scrollActionListener = new ScrollActionListener();
	scrollTimer = new Timer(SCROLL_FIRST_INTERVAL, SCROLL_INTERVAL,
				scrollActionListener);
    }


    /**
     * Adds the specified adjustment listener to receive instances of
     * <code>AdjustmentEvent</code> from this scroll bar.
     * @param l the adjustment listener.
     */
    public synchronized void addAdjustmentListener(AdjustmentListener l) {
	adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
	enableEvents(0); // mark newEventsOnly
    }

    /**
     * Removes the specified adjustment listener so that it no longer
     * receives instances of <code>AdjustmentEvent</code> from this scroll bar.
     * @param l the adjustment listener.
     */
    public synchronized void removeAdjustmentListener(AdjustmentListener l) {
	adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, l);
    }

    /** Notifies the adjustment event to the adjustment listeners. */
    protected void notifyAdjustmentListeners(AdjustmentEvent e) {
	if (adjustmentListener != null) {
	    if (isDirectNotification()) {
		adjustmentListener.adjustmentValueChanged(e);
	    }
	    else {
		e = new AdjustmentEvent(
				e.getAdjustable(),
				e.getID() + java.awt.AWTEvent.RESERVED_ID_MAX,
				e.getAdjustmentType(),
				e.getValue());
		EventPoster.postEvent(e);
	    }
	}
	else {
	    postOldEvent(e);
	}
    }

    protected void processEvent(java.awt.AWTEvent e) {
	if (adjustmentListener != null && (e instanceof AdjustmentEvent)) {
	    AdjustmentEvent ae = (AdjustmentEvent)e;
	    if (ae.getID() > java.awt.AWTEvent.RESERVED_ID_MAX) {
		ae = new AdjustmentEvent(
				ae.getAdjustable(),
				ae.getID() - java.awt.AWTEvent.RESERVED_ID_MAX,
				ae.getAdjustmentType(),
				ae.getValue());
		adjustmentListener.adjustmentValueChanged(ae);
		return;
	    }
	}
	super.processEvent(e);
    }

    /**
     * Returns the preferred size of this scroll bar.
     */
    public Dimension getPreferredSize() {
	synchronized (getTreeLock()) {
	    if (orientation == HORIZONTAL) {
		return new Dimension(100, thickness);
	    }
	    else {
		return new Dimension(thickness, 100);
	    }
	}
    }

    /**
     * Moves and resizes this scroll bar.
     */
    public void setBounds(int x, int y, int width, int height) {
	if (orientation == HORIZONTAL) {
	    if (width < (height * 2))
		makeHorizontalButtons(new Dimension(width / 2, height));
	    else
		makeHorizontalButtons(new Dimension(height, height));
	}
	else {
	    if (height < (width * 2))
		makeVerticalButtons(new Dimension(width, height / 2));
	    else
		makeVerticalButtons(new Dimension(width, width));
	}
	super.setBounds(x, y, width, height);
    }

    /**
     * Paints the scroll bar.
     */
    protected void paintOn(Graphics g) {
	Color save = g.getColor();
	Dimension d = getSize();
	g.setColor(getBackground());
	g.fillRect(0, 0, d.width, d.height);
	if (orientation == HORIZONTAL) {
	    paintLeftButton(g);
	    paintRightButton(g);
	    paintHorizontalVisibleMarker(g);
	}
	else {
	    paintUpButton(g);
	    paintDownButton(g);
	    paintVerticalVisibleMarker(g);
	}
	g.setColor(save);
    }


    // ======== java.awt.Scrollbar APIs ========

    /**
     * Returns the orientation of this scroll bar.
     * @return the orientation of this scroll bar, either
     *         <code>Scrollbar.HORIZONTAL</code> or
     *         <code>Scrollbar.VERTICAL</code>.
     * @see #setOrientation(int)
     */
    public int getOrientation() {
	return orientation;
    }

    /**
     * Sets the orientation for this scroll bar.
     * @param orientation the orientation of this scroll bar, either
     *                    <code>Scrollbar.HORIZONTAL</code> or
     *                    <code>Scrollbar.VERTICAL</code>.
     * @see #getOrientation()
     */
    public synchronized void setOrientation(int orientation) {
	if (orientation == this.orientation) {
	    return;
	}
	switch (orientation) {
	case HORIZONTAL:
	case VERTICAL:
	    this.orientation = orientation;
	    break;
	default:
	    throw new IllegalArgumentException("illegal scrollbar orientation");
	}

	invalidate();
    }

    /**
     * Gets the current value of this scroll bar.
     * @return the current value of this scroll bar.
     * @see #getMinimum()
     * @see #getMaximum()
     */
    public int getValue() {
	return value;
    }

    /**
     * Sets the value of this scroll bar to the specified value.
     * @param newValue the new value of the scroll bar.
     * @see #setValues(int, int, int, int)
     * @see #getValue()
     * @see #getMinimum()
     * @see #getMaximum()
     */
    public synchronized void setValue(int newValue) {
    	setValues(newValue, visibleAmount, minimum, maximum);
    }

    /**
     * Gets the minimum value of this scroll bar.
     * @return the minimum value of this scroll bar.
     * @see #getValue()
     * @see #getMaximum()
     */
    public int getMinimum() {
	return minimum;
    }

    /**
     * Sets the minimum value of this scroll bar.
     * @param newMinimum the new minimum value for this scroll bar.
     * @see #setValues(int, int, int, int)
     * @see #setMaximum(int)
     */
    public synchronized void setMinimum(int newMinimum) {
	setValues(value, visibleAmount, newMinimum, maximum);
    }

    /**
     * Gets the maximum value of this scroll bar.
     * @return the maximum value of this scroll bar.
     * @see #getValue()
     * @see #getMinimum()
     */
    public int getMaximum() {
	return maximum;
    }

    /**
     * Sets the maximum value of this scroll bar.
     * @param newMaximum the new maximum value for this scroll bar.
     * @see #setValues(int, int, int, int)
     * @see #setMinimum(int)
     */
    public synchronized void setMaximum(int newMaximum) {
    	setValues(value, visibleAmount, minimum, newMaximum);
    }

    /**
     * Gets the visible amount of this scroll bar.
     * @return the visible amount of this scroll bar.
     * @see #setVisibleAmount(int)
     */
    public int getVisibleAmount() {
	return visibleAmount;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getVisibleAmount()</code>.
     */
    public int getVisible() {
	return getVisibleAmount();
    }

    /**
     * Sets the visible amount of this scroll bar.
     * @param newAmount the amount visible per page.
     * @see #getVisibleAmount()
     * @see #setValues(int, int, int, int)
     */
    public synchronized void setVisibleAmount(int newAmount) {
    	setValues(value, newAmount, minimum, maximum);
    }

    /**
     * Sets the unit increment for this scroll bar.
     * @param v the amount by which to increment or decrement the scroll
     *          bar's value.
     * @see #getUnitIncrement()
     */
    public synchronized void setUnitIncrement(int v) {
	unitIncrement = v;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setUnitIncrement(int)</code>.
     */
    public void setLineIncrement(int v) {
	setUnitIncrement(v);
    }

    /**
     * Gets the unit increment for this scrollbar.
     * @return the unit increment of this scroll bar.
     * @see #setUnitIncrement()
     */
    public int getUnitIncrement() {
	return unitIncrement;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getUnitIncrement()</code>.
     */
    public int getLineIncrement() {
	return getUnitIncrement();
    }

    /**
     * Sets the block increment for this scroll bar.
     * @param v the amount by which to increment or decrement the scroll
     *          bar's value.
     * @see #getBlockIncrement()
     */
    public synchronized void setBlockIncrement(int v) {
	blockIncrement = v;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setBlockIncrement()</code>.
     */
    public void setPageIncrement(int v) {
	setBlockIncrement(v);
    }

    /**
     * Gets the block increment of this scroll bar.
     * @return the block increment of this scroll bar.
     * @see #setBlockIncrement(int)
     */
    public int getBlockIncrement() {
	return blockIncrement;
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getBlockIncrement()</code>.
     */
    public int getPageIncrement() {
	return getBlockIncrement();
    }

    /**
     * Sets the values of four properties for this scroll bar.
     * <p>
     * This method simultaneously and synchronously sets the values
     * of four scroll bar properties, assuring that the values of
     * these properties are mutually consistent. It enforces the
     * constraints that maximum cannot be less than minimum, and that
     * value cannot be less than the minimum or greater than the maximum.
     * @param value   the position in the current window.
     * @param visible the amount visible per page.
     * @param minimum the minimum value of the scroll bar.
     * @param maximum the maximum value of the scroll bar.
     */
    public synchronized void setValues(int value, int visible,
				       int minimum, int maximum)
    {
	if (maximum <= minimum) {
	    maximum = minimum + 1;
	}
	if (visible > maximum - minimum) {
	    visible = maximum - minimum;
	}
	if (visible < 1) {
	    visible = 1;
	}
	if (value < minimum) {
	    value = minimum;
	}
	if (value > maximum - visible) {
	    value = maximum - visible;
	}

	if (this.visibleAmount == visible &&
	    this.minimum == minimum && this.maximum == maximum)
	{
	    if (this.value == value) {
		return;
	    }
	    else {
		int oldValue = this.value;
		this.value = value;
		if (isShowing()) {
		    repaintMovedVisibleMarker(oldValue);
		}
	    }
	}
	else {
	    this.value = value;
	    this.visibleAmount = visible;
	    this.minimum = minimum;
	    this.maximum = maximum;
	    if (isShowing()) {
		repaintVisibleMarker();
		//repaintNow();
	    }
	}
    }


    // ================ Enhanced APIs ================

    /**
     * Returns the thickness of the scroll bar.
     * @see #setScrollbarThickness(int)
     */
    public int getScrollbarThickness() {
	return thickness;
    }

    /**
     * Sets the thickness of the scroll bar.
     * @see #getScrollbarThickness()
     */
    public synchronized void setScrollbarThickness(int thickness) {
	thickness = Math.max(thickness, 6);
	if (this.thickness == thickness)
	    return;
	this.thickness = thickness;
	Dimension d = new Dimension(this.thickness, this.thickness);
	makeHorizontalButtons(d);
	makeVerticalButtons(d);
	invalidate();
    }


    // ================ Listeners ================

    /**
     * Invoked when the mouse has been clicked on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when the mouse has been pressed on a component.
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(MouseEvent e) {
	if (!isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	e.consume();

	int increment = 0;

	synchronized (this) {
	    lastPressedValue = value;
	    lastPressedPoint = e.getPoint();
	    if (orientation == HORIZONTAL)
		lastPressedType =
				getHorizontalAdjustmentType(lastPressedPoint.x);
	    else
		lastPressedType = getVerticalAdjustmentType(lastPressedPoint.y);
	    Graphics g;
	    switch (lastPressedType) {
	    case AdjustmentEvent.UNIT_DECREMENT:
		if (orientation == HORIZONTAL)
		    leftButton.setState(true);
		else
		    upButton.setState(true);
		g = getPreferredGraphics();
		if (g != null) {
		    if (orientation == HORIZONTAL)
			paintLeftButton(g);
		    else
			paintUpButton(g);
		    g.dispose();
		    syncGraphics();
		}
		//scrollAndNotify(-unitIncrement, lastPressedType);
		//startScrollTimer(-unitIncrement, lastPressedType);
		increment = -unitIncrement;
		break;

	    case AdjustmentEvent.UNIT_INCREMENT:
		if (orientation == HORIZONTAL)
		    rightButton.setState(true);
		else
		    downButton.setState(true);
		g = getPreferredGraphics();
		if (g != null) {
		    if (orientation == HORIZONTAL)
			paintRightButton(g);
		    else
			paintDownButton(g);
		    g.dispose();
		    syncGraphics();
		}
		//scrollAndNotify(unitIncrement, lastPressedType);
		//startScrollTimer(unitIncrement, lastPressedType);
		increment = unitIncrement;
		break;

	    case AdjustmentEvent.BLOCK_DECREMENT:
		g = getPreferredGraphics();
		if (g != null) {
		    g.setColor(SELECTION_BACKGROUND_COLOR);
		    if (orientation == HORIZONTAL)
			paintHorizontalBlockDecrement(g);
		    else
			paintVerticalBlockDecrement(g);
		    g.dispose();
		    syncGraphics();
		}
		//scrollAndNotify(-blockIncrement, lastPressedType);
		//startScrollTimer(-blockIncrement, lastPressedType);
		increment = -blockIncrement;
		break;

	    case AdjustmentEvent.BLOCK_INCREMENT:
		g = getPreferredGraphics();
		if (g != null) {
		    g.setColor(SELECTION_BACKGROUND_COLOR);
		    if (orientation == HORIZONTAL)
			paintHorizontalBlockIncrement(g);
		    else
			paintVerticalBlockIncrement(g);
		    g.dispose();
		    syncGraphics();
		}
		//scrollAndNotify(blockIncrement, lastPressedType);
		//startScrollTimer(blockIncrement, lastPressedType);
		increment = blockIncrement;
		break;

	    case AdjustmentEvent.TRACK:
	    default:
		return;
	    }
	}

	scrollAndNotify(increment, lastPressedType);
	startScrollTimer(increment, lastPressedType);
    }

    /**
     * Invoked when the mouse has been released on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(MouseEvent e) {
	if (!isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	e.consume();

	stopScrollTimer();
	performMouseReleasedAction();
    }

    protected synchronized void performMouseReleasedAction() {
	Graphics g;
	switch (lastPressedType) {
	case AdjustmentEvent.UNIT_DECREMENT:
	    if (orientation == HORIZONTAL)
		leftButton.setState(false);
	    else
		upButton.setState(false);
	    g = getPreferredGraphics();
	    if (g != null) {
		if (orientation == HORIZONTAL)
		    paintLeftButton(g);
		else
		    paintUpButton(g);
		g.dispose();
		syncGraphics();
	    }
	    break;

	case AdjustmentEvent.UNIT_INCREMENT:
	    if (orientation == HORIZONTAL)
		rightButton.setState(false);
	    else
		downButton.setState(false);
	    g = getPreferredGraphics();
	    if (g != null) {
		if (orientation == HORIZONTAL)
		    paintRightButton(g);
		else
		    paintDownButton(g);
		g.dispose();
		syncGraphics();
	    }
	    break;

	case AdjustmentEvent.BLOCK_DECREMENT:
	    g = getPreferredGraphics();
	    if (g != null) {
		g.setColor(BACKGROUND_COLOR);
		if (orientation == HORIZONTAL)
		    paintHorizontalBlockDecrement(g);
		else
		    paintVerticalBlockDecrement(g);
		g.dispose();
		syncGraphics();
	    }
	    break;

	case AdjustmentEvent.BLOCK_INCREMENT:
	    g = getPreferredGraphics();
	    if (g != null) {
		g.setColor(BACKGROUND_COLOR);
		if (orientation == HORIZONTAL)
		    paintHorizontalBlockIncrement(g);
		else
		    paintVerticalBlockIncrement(g);
		g.dispose();
		syncGraphics();
	    }
	    break;

	case AdjustmentEvent.TRACK:
	default:
	    break;
	}

	lastPressedValue = value;
	lastPressedType = -1;
	lastPressedPoint = null;
    }

    /**
     * Invoked when the mouse enters a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * Invoked when the mouse button is pressed on a component and then dragged.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseDragged(MouseEvent e) {
	if (!isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	e.consume();

	int oldValue = value;

	synchronized (this) {
	    switch (lastPressedType) {
	    case AdjustmentEvent.UNIT_DECREMENT:
	    case AdjustmentEvent.UNIT_INCREMENT:
	    case AdjustmentEvent.BLOCK_DECREMENT:
	    case AdjustmentEvent.BLOCK_INCREMENT:
		lastPressedPoint = e.getPoint();
		if (checkMouseReleased()) {
		    stopScrollTimer();
		    performMouseReleasedAction();
		}
		return;

	    case AdjustmentEvent.TRACK:
		break;

	    default:
		return;
	    }

	    // AdjustmentEvent.TRACK
	    Point currentPoint = e.getPoint();
	    if (lastPressedPoint == null) {
		lastPressedPoint = currentPoint;
		lastPressedValue = value;
		return;
	    }
	    Dimension d = getSize();
	    int width  = d.width;
	    int height = d.height;
	    if (orientation == HORIZONTAL) {
		int diff = currentPoint.x - lastPressedPoint.x;
		int availableW = getHorizontalMaximumBounds().width;
		setValues(lastPressedValue + (diff * maximum / availableW),
			  visibleAmount, minimum, maximum);
	    }
	    else {
		int diff = currentPoint.y - lastPressedPoint.y;
		int availableH = getVerticalMaximumBounds().height;
		setValues(lastPressedValue + (diff * maximum / availableH),
			  visibleAmount, minimum, maximum);
	    }
	}

	if (value != oldValue && isShowing()) {
	    AdjustmentEvent ae =
		new AdjustmentEvent(this,
				    AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
				    AdjustmentEvent.TRACK,
				    value);
	    notifyAdjustmentListeners(ae);
	}
    }

    /**
     * Invoked when the mouse button has been moved on a component.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseMoved(MouseEvent e) {}


    // ================ Protected ================

    protected void repaintVisibleMarker() {
	Graphics g = getPreferredGraphics();
	if (g == null)
	    return;
	if (orientation == HORIZONTAL) {
	    Rectangle r = getHorizontalMaximumBounds();
	    if (r.width > 0) {
		g.setColor(getBackground());
		g.fillRect(r.x, r.y, r.width, r.height);
		paintHorizontalVisibleMarker(g);
	    }
	}
	else {
	    Rectangle r = getVerticalMaximumBounds();
	    if (r.height > 0) {
		g.setColor(getBackground());
		g.fillRect(r.x, r.y, r.width, r.height);
		paintVerticalVisibleMarker(g);
	    }
	}
	g.dispose();
	syncGraphics();
    }

    protected void repaintMovedVisibleMarker(int oldValue) {
	if (oldValue == value)
	    return;
	Graphics g = getPreferredGraphics();
	if (g == null)
	    return;
	Insets insets = marker.getInsets();
	if (orientation == HORIZONTAL) {
	    Rectangle or = getHorizontalVisibleBounds(oldValue);
	    if (or.width <= 0)
		return;
	    Rectangle r = getHorizontalVisibleBounds();
	    g.setColor(MARKER_COLOR);
	    if (oldValue < value) {
		g.fillRect(or.x + or.width - insets.right, r.y,
			   r.x - or.x + insets.right, r.height);
		g.setColor(getBackground());
		g.fillRect(or.x, or.y, r.x - or.x, or.height);
	    }
	    else { // value < oldValue
		g.fillRect(r.x, r.y, or.x - r.x + insets.left, r.height);
		g.setColor(getBackground());
		g.fillRect(r.x + r.width, or.y, or.x - r.x, or.height);
	    }
	    marker.paint(g, r.x, r.y, r.width, r.height);
	}
	else {
	    Rectangle or = getVerticalVisibleBounds(oldValue);
	    if (or.height <= 0)
		return;
	    Rectangle r = getVerticalVisibleBounds();
	    g.setColor(MARKER_COLOR);
	    if (oldValue < value) {
		g.fillRect(r.x, or.y + or.height - insets.bottom,
			   r.width, r.y - or.y + insets.bottom);
		g.setColor(getBackground());
		g.fillRect(or.x, or.y, or.width, r.y - or.y);
	    }
	    else { // value < oldValue
		g.fillRect(r.x, r.y, r.width, or.y - r.y + insets.top);
		g.setColor(getBackground());
		g.fillRect(or.x, r.y + r.height, or.width, or.y - r.y);
	    }
	    marker.paint(g, r.x, r.y, r.width, r.height);
	}
	g.dispose();
	syncGraphics();
    }

    protected void paintLeftButton(Graphics g) {
	g.setColor(BUTTON_COLOR);
	Rectangle r = getLeftButtonBounds();
	g.fillRect(r.x, r.y, r.width, r.height);
	g.setColor(getForeground());
	leftButton.paint(g, new Point(r.x, r.y));
    }

    protected void paintRightButton(Graphics g) {
	g.setColor(BUTTON_COLOR);
	Rectangle r = getRightButtonBounds();
	g.fillRect(r.x, r.y, r.width, r.height);
	g.setColor(getForeground());
	rightButton.paint(g, new Point(r.x, r.y));
    }

    protected void paintUpButton(Graphics g) {
	g.setColor(BUTTON_COLOR);
	Rectangle r = getUpButtonBounds();
	g.fillRect(r.x, r.y, r.width, r.height);
	g.setColor(getForeground());
	upButton.paint(g, new Point(r.x, r.y));
    }

    protected void paintDownButton(Graphics g) {
	g.setColor(BUTTON_COLOR);
	Rectangle r = getDownButtonBounds();
	g.fillRect(r.x, r.y, r.width, r.height);
	g.setColor(getForeground());
	downButton.paint(g, new Point(r.x, r.y));
    }

    protected void paintHorizontalVisibleMarker(Graphics g) {
	Rectangle r = getHorizontalVisibleBounds();
	if (r.width == 0)
	    return;
	g.setColor(MARKER_COLOR);
	g.fillRect(r.x, r.y, r.width, r.height);
	g.setColor(getForeground());
	marker.paint(g, r.x, r.y, r.width, r.height);
    }

    protected void paintVerticalVisibleMarker(Graphics g) {
	Rectangle r = getVerticalVisibleBounds();
	if (r.height == 0)
	    return;
	g.setColor(MARKER_COLOR);
	g.fillRect(r.x, r.y, r.width, r.height);
	g.setColor(getForeground());
	marker.paint(g, r.x, r.y, r.width, r.height);
    }

    protected void paintHorizontalBlockDecrement(Graphics g) {
	Rectangle r1 = getLeftButtonBounds();
	Rectangle r2 = getHorizontalVisibleBounds();
	g.fillRect(r1.x + r1.width, r1.y, r2.x - (r1.x + r1.width), r1.height);
    }

    protected void paintHorizontalBlockIncrement(Graphics g) {
	Rectangle r1 = getHorizontalVisibleBounds();
	Rectangle r2 = getRightButtonBounds();
	g.fillRect(r1.x + r1.width, r1.y, r2.x - (r1.x + r1.width), r1.height);
    }

    protected void paintVerticalBlockDecrement(Graphics g) {
	Rectangle r1 = getUpButtonBounds();
	Rectangle r2 = getVerticalVisibleBounds();
	g.fillRect(r1.x, r1.y + r1.height, r1.width, r2.y - (r1.y + r1.height));
    }

    protected void paintVerticalBlockIncrement(Graphics g) {
	Rectangle r1 = getVerticalVisibleBounds();
	Rectangle r2 = getDownButtonBounds();
	g.fillRect(r1.x, r1.y + r1.height, r1.width, r2.y - (r1.y + r1.height));
    }

    protected Rectangle getLeftButtonBounds() {
	Dimension ld = leftButton.getSize();
	return new Rectangle(0, 0, ld.width, ld.height);
    }

    protected Rectangle getRightButtonBounds() {
	Dimension d = getSize();
	Dimension rd = rightButton.getSize();
	return new Rectangle(d.width - rd.width, 0, rd.width, rd.height);
    }

    protected Rectangle getHorizontalMaximumBounds() {
	Dimension d = getSize();
	Dimension ld = leftButton.getSize();
	int availableW = d.width - (ld.width + rightButton.getSize().width);
	return new Rectangle(ld.width, 0, availableW, d.height);
    }

    protected Rectangle getHorizontalVisibleBounds() {
	return getHorizontalVisibleBounds(value);
    }

    protected Rectangle getHorizontalVisibleBounds(int value) {
	if (maximum <= 0)
	    return new Rectangle(0, 0, 0, 0);
	Dimension d = getSize();
	Dimension ld = leftButton.getSize();
	int availableW = d.width - (ld.width + rightButton.getSize().width);
	if (availableW <= 0)
	    return new Rectangle(0, 0, 0, 0);
	int visibleW = Math.min(
			    Math.max(availableW * visibleAmount / maximum, 3),
			    availableW);
	int maxVisibleX = ld.width + (availableW - visibleW);
	int visibleX = (value >= (maximum - visibleAmount) ?
			    maxVisibleX :
			    Math.min(ld.width + (availableW * value / maximum),
				     maxVisibleX));
	return new Rectangle(visibleX, 0, visibleW, d.height);
    }

    protected int getHorizontalAdjustmentType(int x) {
	if (maximum <= 0)
	    return -1;
	Dimension ld = leftButton.getSize();
	if (x < ld.width)
	    return AdjustmentEvent.UNIT_DECREMENT;
	Dimension d = getSize();
	int availableW = d.width - (ld.width + rightButton.getSize().width);
	if (availableW <= 0)
	    return AdjustmentEvent.UNIT_INCREMENT;
	int visibleW = Math.min(
			    Math.max(availableW * visibleAmount / maximum, 3),
			    availableW);
	int maxVisibleX = ld.width + (availableW - visibleW);
	int visibleX = (value >= (maximum - visibleAmount) ?
			    maxVisibleX :
			    Math.min(ld.width + (availableW * value / maximum),
				     maxVisibleX));
	if (x < visibleX)
	    return AdjustmentEvent.BLOCK_DECREMENT;
	if (x < visibleX + visibleW)
	    return AdjustmentEvent.TRACK;
	if (x < ld.width + availableW)
	    return AdjustmentEvent.BLOCK_INCREMENT;
	return AdjustmentEvent.UNIT_INCREMENT;
    }

    protected Rectangle getUpButtonBounds() {
	Dimension ud = upButton.getSize();
	return new Rectangle(0, 0, ud.width, ud.height);
    }

    protected Rectangle getDownButtonBounds() {
	Dimension d = getSize();
	Dimension dd = downButton.getSize();
	return new Rectangle(0, d.height - dd.height, dd.width, dd.height);
    }

    protected Rectangle getVerticalMaximumBounds() {
	Dimension d = getSize();
	Dimension ud = upButton.getSize();
	int availableH = d.height - (ud.height + downButton.getSize().height);
	return new Rectangle(0, ud.height, d.width, availableH);
    }

    protected Rectangle getVerticalVisibleBounds() {
	return getVerticalVisibleBounds(value);
    }

    protected Rectangle getVerticalVisibleBounds(int value) {
	if (maximum <= 0)
	    return new Rectangle(0, 0, 0, 0);
	Dimension d = getSize();
	Dimension ud = upButton.getSize();
	int availableH = d.height - (ud.height + downButton.getSize().height);
	if (availableH <= 0)
	    return new Rectangle(0, 0, 0, 0);
	int visibleH = Math.min(
			    Math.max(availableH * visibleAmount / maximum, 3),
			    availableH);
	int maxVisibleY = ud.height + (availableH - visibleH);
	int visibleY = (value >= (maximum - visibleAmount) ?
			    maxVisibleY :
			    Math.min(ud.height + (availableH * value / maximum),
				     maxVisibleY));
	return new Rectangle(0, visibleY, d.width, visibleH);
    }

    protected int getVerticalAdjustmentType(int y) {
	if (maximum <= 0)
	    return -1;
	Dimension ud = upButton.getSize();
	if (y < ud.height)
	    return AdjustmentEvent.UNIT_DECREMENT;
	Dimension d = getSize();
	int availableH = d.height - (ud.height + downButton.getSize().height);
	if (availableH <= 0)
	    return AdjustmentEvent.UNIT_INCREMENT;
	int visibleH = Math.min(
			    Math.max(availableH * visibleAmount / maximum, 3),
			    availableH);
	int maxVisibleY = ud.height + (availableH - visibleH);
	int visibleY = (value >= (maximum - visibleAmount) ?
			    maxVisibleY :
			    Math.min(ud.height + (availableH * value / maximum),
				     maxVisibleY));
	if (y < visibleY)
	    return AdjustmentEvent.BLOCK_DECREMENT;
	if (y < visibleY + visibleH)
	    return AdjustmentEvent.TRACK;
	if (y < ud.height + availableH)
	    return AdjustmentEvent.BLOCK_INCREMENT;
	return AdjustmentEvent.UNIT_INCREMENT;
    }

    protected void makeHorizontalButtons(Dimension d) {
	leftButton  = new VButton(new VArrow(VArrow.LEFT));
	rightButton = new VButton(new VArrow(VArrow.RIGHT));
	leftButton.setSize(d);
	rightButton.setSize(d);
    }

    protected void makeVerticalButtons(Dimension d) {
	upButton    = new VButton(new VArrow(VArrow.UP));
	downButton  = new VButton(new VArrow(VArrow.DOWN));
	upButton.setSize(d);
	downButton.setSize(d);
    }

    protected void scrollAndNotify(int increment, int adjustType) {
	AdjustmentEvent ae = null;

	synchronized (this) {
	    int oldValue = value;
	    setValues(value + increment, visibleAmount, minimum, maximum);
	    if (value == oldValue)
		return;
	    if (isShowing()) {
		switch (adjustType) {
		case AdjustmentEvent.UNIT_DECREMENT:
		case AdjustmentEvent.UNIT_INCREMENT:
		case AdjustmentEvent.BLOCK_DECREMENT:
		case AdjustmentEvent.BLOCK_INCREMENT:
		    break;
		default:
		    adjustType = AdjustmentEvent.TRACK;
		    break;
		}
		ae = new AdjustmentEvent(
				this,
				AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
				adjustType,
				value);
	    }
	}

	if (ae != null) {
	    notifyAdjustmentListeners(ae);
	}
    }

    protected synchronized boolean checkMouseReleased() {
	switch (lastPressedType) {
	case AdjustmentEvent.UNIT_DECREMENT:
	case AdjustmentEvent.UNIT_INCREMENT:
	case AdjustmentEvent.BLOCK_DECREMENT:
	case AdjustmentEvent.BLOCK_INCREMENT:
	    break;
	case AdjustmentEvent.TRACK:
	default:
	    return true;
	}

	int pressedType = -1;
	Dimension d = getSize();
	if ((new Rectangle(0, 0, d.width, d.height)).contains(lastPressedPoint))
	{
	    if (orientation == HORIZONTAL)
		pressedType = getHorizontalAdjustmentType(lastPressedPoint.x);
	    else
		pressedType = getVerticalAdjustmentType(lastPressedPoint.y);
	}
	if (pressedType != lastPressedType) {
	    return true;
	}
	return false;
    }

    protected synchronized void startScrollTimer(int increment, int adjustType)
    {
	scrollTimer.stop();
	scrollActionListener.increment  = increment;
	scrollActionListener.adjustType = adjustType;
	scrollTimer.start();
    }

    protected synchronized void stopScrollTimer() {
	scrollTimer.stop();
    }

    class ScrollActionListener implements ActionListener, java.io.Serializable
    {
	int increment;
	int adjustType;

	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == scrollTimer) {
		if (checkMouseReleased()) {
		    scrollTimer.stop();
		    performMouseReleasedAction();
		    return;
		}
		scrollAndNotify(increment, adjustType);
	    }
	}
    }


    /** Internal constant for serialization */
    static protected final String adjustmentListenerL = "adjustmentL".intern();

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	jp.kyasu.awt.ListenerSerializer.write(s,
					      adjustmentListenerL,
					      adjustmentListener);
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws ClassNotFoundException, java.io.IOException
    {
	s.defaultReadObject();
	Object keyOrNull;
	while ((keyOrNull = s.readObject()) != null) {
	    String key = ((String)keyOrNull).intern();
	    if (key == adjustmentListenerL)
		addAdjustmentListener((AdjustmentListener)s.readObject());
	    else // skip value for unrecognized key
		s.readObject();
	}
	scrollTimer.stop();
	lastPressedValue = value;
	lastPressedType  = -1;
	lastPressedPoint = null;
    }
}
