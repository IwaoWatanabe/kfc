/*
 * ScrollPanel.java
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

import jp.kyasu.awt.event.ScrollEvent;
import jp.kyasu.awt.event.ScrollListener;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>ScrollPanel</code> class implements horizontal and/or
 * vertical scrolling panel for a single child component.
 *
 * @see 	jp.kyasu.awt.Scrollable
 * @see 	jp.kyasu.awt.event.ScrollEvent
 * @see 	jp.kyasu.awt.event.ScrollListener
 *
 * @version 	10 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class ScrollPanel extends Panel
	implements AdjustmentListener, ScrollListener
{
    protected int scrollbarVisibility;
    protected int scrollbarDisplayPolicy;
    protected Scrollable scrollable;
    protected Scrollbar vScrollbar;
    protected Scrollbar hScrollbar;


    /**
     * Create and display both vertical and horizontal scrollbars.
     */
    static public final int SCROLLBARS_BOTH =
				java.awt.TextArea.SCROLLBARS_BOTH;

    /**
     * Create and display vertical scrollbar only.
     */
    static public final int SCROLLBARS_VERTICAL_ONLY =
				java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY;

    /**
     * Create and display horizontal scrollbar only.
     */
    static public final int SCROLLBARS_HORIZONTAL_ONLY =
				java.awt.TextArea.SCROLLBARS_HORIZONTAL_ONLY;

    /**
     * Do not create or display any scrollbars.
     */
    static public final int SCROLLBARS_NONE =
				java.awt.TextArea.SCROLLBARS_NONE;

    /**
     * Specifies that horizontal/vertical scrollbar should be shown
     * only when the size of the child exceeds the size of the scrollpane
     * in the horizontal/vertical dimension.
     */
    static public final int SCROLLBARS_AS_NEEDED =
				java.awt.ScrollPane.SCROLLBARS_AS_NEEDED;

    /**
     * Specifies that horizontal/vertical scrollbars should always be
     * shown regardless of the respective sizes of the scrollpane and child.
     */
    static public final int SCROLLBARS_ALWAYS =
				java.awt.ScrollPane.SCROLLBARS_ALWAYS;


    /**
     * Constructs a new scroll container with the specified scrollbar
     * visibility and scrollbar display policy.
     *
     * @param scrollbarVisibility    constant for what scrollbars are created.
     * @param scrollbarDisplayPolicy policy for when scrollbars should be shown.
     */
    public ScrollPanel(int scrollbarVisibility, int scrollbarDisplayPolicy) {
	super(null, new Insets(0, 0, 0, 0));
	setScrollbarVisibility(scrollbarVisibility);
	setScrollbarDisplayPolicy(scrollbarDisplayPolicy);
    }


    /**
     * Returns the visibility for the scrollbars.
     *
     * @see #setScrollbarVisibility(int)
     * @see #SCROLLBARS_BOTH
     * @see #SCROLLBARS_VERTICAL_ONLY
     * @see #SCROLLBARS_HORIZONTAL_ONLY
     * @see #SCROLLBARS_NONE
     */
    public int getScrollbarVisibility() {
	return scrollbarVisibility;
    }

    /**
     * Sets the visibility for the scrollbars.
     *
     * @see #getScrollbarVisibility()
     * @see #SCROLLBARS_BOTH
     * @see #SCROLLBARS_VERTICAL_ONLY
     * @see #SCROLLBARS_HORIZONTAL_ONLY
     * @see #SCROLLBARS_NONE
     */
    public void setScrollbarVisibility(int scrollbarVisibility) {
	Scrollbar oldVScrollbar = vScrollbar;
	Scrollbar oldHScrollbar = hScrollbar;
	switch (scrollbarVisibility) {
	case SCROLLBARS_BOTH:
	    this.scrollbarVisibility = scrollbarVisibility;
	    vScrollbar = new Scrollbar(Scrollbar.VERTICAL);
	    hScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
	    break;
	case SCROLLBARS_HORIZONTAL_ONLY:
	    this.scrollbarVisibility = scrollbarVisibility;
	    vScrollbar = null;
	    hScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
	    break;
	case SCROLLBARS_VERTICAL_ONLY:
	    this.scrollbarVisibility = scrollbarVisibility;
	    vScrollbar = new Scrollbar(Scrollbar.VERTICAL);
	    hScrollbar = null;
	    break;
	case SCROLLBARS_NONE:
	    this.scrollbarVisibility = scrollbarVisibility;
	    vScrollbar = null;
	    hScrollbar = null;
	    break;
	default:
	    throw new IllegalArgumentException(
		"improper scrollbarVisibility: " + scrollbarVisibility);
	}
	if (oldVScrollbar != null) {
	    remove(oldVScrollbar);
	    oldVScrollbar.removeAdjustmentListener(this);
	}
	if (oldHScrollbar != null) {
	    remove(oldHScrollbar);
	    oldHScrollbar.removeAdjustmentListener(this);
	}
	if (vScrollbar != null) {
	    super.addImpl(vScrollbar, null, -1);
	    vScrollbar.addAdjustmentListener(this);
	}
	if (hScrollbar != null) {
	    super.addImpl(hScrollbar, null, -1);
	    hScrollbar.addAdjustmentListener(this);
	}
	invalidate();
    }

    /**
     * Returns the display policy for the scrollbars.
     *
     * @see #setScrollbarDisplayPolicy(int)
     * @see #SCROLLBARS_AS_NEEDED
     * @see #SCROLLBARS_ALWAYS
     */
    public int getScrollbarDisplayPolicy() {
	return scrollbarDisplayPolicy;
    }

    /**
     * Sets the display policy for the scrollbars.
     *
     * @see #getScrollbarDisplayPolicy()
     * @see #SCROLLBARS_AS_NEEDED
     * @see #SCROLLBARS_ALWAYS
     */
    public void setScrollbarDisplayPolicy(int scrollbarDisplayPolicy) {
	switch (scrollbarDisplayPolicy) {
	case SCROLLBARS_AS_NEEDED:
	case SCROLLBARS_ALWAYS:
	    this.scrollbarDisplayPolicy = scrollbarDisplayPolicy;
	    break;
	default:
	    throw new IllegalArgumentException(
		"improper scrollbarDisplayPolicy: " + scrollbarDisplayPolicy);
	}
	invalidate();
    }

    /**
     * Adds the specified component to this scroll container.
     * If the scroll container has an existing child component, that
     * component is removed and the new one is added.
     *
     * @param comp        the component to be added.
     * @param constraints not applicable.
     * @param index       the position of child component (must be <= 0).
     */
    protected void addImpl(Component comp, Object constraints, int index) {
	synchronized (getTreeLock()) {
	    if (!(comp instanceof Scrollable)) {
		comp = new ScrollWrapper(comp);
	    }
	    if (index > 0) {
		throw new IllegalArgumentException("position greater than 0");
	    }
	    if (getComponentCount() > 0) {
		Component comps[] = getComponents();
		for (int i = 0; i < comps.length; i++) {
		    Component c = comps[i];
		    if (c != vScrollbar && c != hScrollbar) {
			remove(c);
		    }
		}
	    }
	    super.addImpl(comp, constraints, index);
	    scrollable = (Scrollable)comp;
	    scrollable.addScrollListener(this);
	}
    }

    /**
     * Removes the component, specified by <code>index</code>,
     * from this container.
     * @param index the index of the component to be removed.
     */
    public void remove(int index) {
	synchronized (getTreeLock()) {
	    Component c = getComponent(index);
	    if (c instanceof Scrollable) {
		((Scrollable)c).removeScrollListener(this);
	    }
	    super.remove(index);
	}
    }

    /**
     * Sets the layout manager for this container.
     *
     * @param mgr the specified layout manager.
     */
    public void setLayout(LayoutManager mgr) {
	// ignore
    }

    /**
     * Paints this panel.
     */
    public void paint(Graphics g) {
	if (vScrollbar != null && vScrollbar.isVisible() &&
	    hScrollbar != null && hScrollbar.isVisible())
	{
	    int thickness = getScrollbarThickness();
	    Dimension d = getSize();
	    Insets insets = getInsets();
	    g.setColor(getBackground());
	    g.fillRect(d.width - insets.right - thickness,
		       d.height - insets.bottom - thickness,
		       thickness, thickness);
	    g.setColor(getForeground());
	}
	super.paint(g);
    }

    /**
     * Returns the preferred size of this panel.
     */
    public Dimension getPreferredSize() {
	synchronized (getTreeLock()) {
	    int x = 0;
	    int y = 0;
	    if (scrollable != null && ((Component)scrollable).isVisible()) {
		Dimension d = ((Component)scrollable).getPreferredSize();
		x += d.width;
		y += d.height;
	    }
	    if (scrollbarDisplayPolicy == SCROLLBARS_ALWAYS) {
		int thickness = getScrollbarThickness();
		if (vScrollbar != null && vScrollbar.isVisible()) {
		    x += thickness;
		}
		if (hScrollbar != null && hScrollbar.isVisible()) {
		    y += thickness;
		}
	    }
	    Insets insets = getInsets();
	    return new Dimension(x + (insets.left + insets.right),
				 y + (insets.top  + insets.bottom));
	}
    }

    /**
     * Returns the minimum size of this panel.
     */
    public Dimension getMinimumSize() {
	synchronized (getTreeLock()) {
	    int x = 0;
	    int y = 0;
	    if (scrollable != null && ((Component)scrollable).isVisible()) {
		Dimension d = ((Component)scrollable).getMinimumSize();
		x += d.width;
		y += d.height;
	    }
	    if (scrollbarDisplayPolicy == SCROLLBARS_ALWAYS) {
		int thickness = getScrollbarThickness();
		if (vScrollbar != null && vScrollbar.isVisible()) {
		    x += thickness;
		}
		if (hScrollbar != null && hScrollbar.isVisible()) {
		    y += thickness;
		}
	    }
	    Insets insets = getInsets();
	    return new Dimension(x + (insets.left + insets.right),
				 y + (insets.top  + insets.bottom));
	}
    }

    /**
     * Lays out this panel.
     */
    public void doLayout() {
	if (scrollbarDisplayPolicy == SCROLLBARS_AS_NEEDED) {
	    boolean visibles[] = checkLayout();
	    if (visibles != null) {
		if (vScrollbar != null) {
		    vScrollbar.setVisible(visibles[0]);
		}
		if (hScrollbar != null) {
		    hScrollbar.setVisible(visibles[1]);
		}
	    }
	}
	int thickness = getScrollbarThickness();
	Dimension d = getSize();
	Insets insets = getInsets();
	boolean vVisible = (vScrollbar != null && vScrollbar.isVisible());
	boolean hVisible = (hScrollbar != null && hScrollbar.isVisible());
	if (vVisible) {
	    vScrollbar.setBounds(d.width - insets.right - thickness,
				 insets.top,
				 thickness,
				 d.height - (insets.top + insets.bottom) -
						(hVisible ? thickness : 0));
	}
	if (hVisible) {
	    hScrollbar.setBounds(insets.left,
				 d.height - insets.bottom - thickness,
				 d.width - (insets.left + insets.right) -
						(vVisible ? thickness : 0),
				 thickness);
	}
	if (scrollable != null && ((Component)scrollable).isVisible()) {
	    ((Component)scrollable).setBounds(
				insets.left,
				insets.top,
				d.width - (insets.left + insets.right) -
						(vVisible ? thickness : 0),
				d.height - (insets.top + insets.bottom) -
						(hVisible ? thickness : 0));
	}
	updateHScrollbar(scrollable);
	updateVScrollbar(scrollable);
    }

    protected boolean[] checkLayout() {
	if (scrollbarDisplayPolicy != SCROLLBARS_AS_NEEDED) {
	    return null;
	}

	if (scrollable == null) {
	    if ((vScrollbar != null && vScrollbar.isVisible()) ||
		(hScrollbar != null && hScrollbar.isVisible()))
	    {
		return new boolean[]{ false, false };
	    }
	    else {
		return null;
	    }
	}

	int thickness = getScrollbarThickness();
	Dimension d = getSize();
	Insets insets = getInsets();
	int width  = d.width  - (insets.left + insets.right);
	int height = d.height - (insets.top + insets.bottom);

	int scrollWidth  = scrollable.getHMaximum();
	int scrollHeight = scrollable.getVMaximum();

	boolean vVisible = false;
	boolean hVisible = false;

	int oldW, oldH;
	do {
	    oldW = width;
	    oldH = height;
	    if (vScrollbar != null && !vVisible && scrollHeight > height) {
		vVisible = true;
		width -= thickness;
	    }
	    if (hScrollbar != null && !hVisible && scrollWidth > width) {
		hVisible = true;
		height -= thickness;
	    }
	} while (oldW != width || oldH != height);

	if ((vScrollbar != null && vScrollbar.isVisible() != vVisible) ||
	    (hScrollbar != null && hScrollbar.isVisible() != hVisible))
	{
	    return new boolean[]{ vVisible, hVisible };
	}
	else {
	    return null;
	}
    }

    /**
     * Enables or disables this panel.
     */
    public synchronized void setEnabled(boolean b) {
	super.setEnabled(b);
	((Component)scrollable).setEnabled(b);
	if (vScrollbar != null) vScrollbar.setEnabled(b);
	if (hScrollbar != null) hScrollbar.setEnabled(b);
    }

    /**
     * Returns the thickness of the scroll bar.
     * @see #setScrollbarThickness(int)
     */
    public int getScrollbarThickness() {
	if (vScrollbar == null && hScrollbar == null)
	    return 0;
	else if (vScrollbar != null)
	    return vScrollbar.getScrollbarThickness();
	else // hScrollbar != null
	    return hScrollbar.getScrollbarThickness();
    }

    /**
     * Sets the thickness of the scroll bar.
     * @see #getScrollbarThickness()
     */
    public synchronized void setScrollbarThickness(int thickness) {
	if (vScrollbar == null && hScrollbar == null)
	    return;
	if (getScrollbarThickness() == thickness)
	    return;
	if (vScrollbar != null)
	    vScrollbar.setScrollbarThickness(thickness);
	if (hScrollbar != null)
	    hScrollbar.setScrollbarThickness(thickness);
	invalidate();
    }

    /**
     * Invoked when the value of the adjustable has changed.
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
	switch(e.getAdjustmentType()) {
	case AdjustmentEvent.UNIT_INCREMENT:
	case AdjustmentEvent.UNIT_DECREMENT:
	case AdjustmentEvent.BLOCK_INCREMENT:
	case AdjustmentEvent.BLOCK_DECREMENT:
	case AdjustmentEvent.TRACK:
	    switch (e.getAdjustable().getOrientation()) {
	    case Adjustable.HORIZONTAL:
		scrollable.setHValue(e.getValue());
		break;
	    case Adjustable.VERTICAL:
		scrollable.setVValue(e.getValue());
		break;
	    }
	    break;
	}
    }

    /**
     * Invoked when the value of the scrollable has changed.
     */
    public void scrollValueChanged(ScrollEvent e) {
	Scrollable src = e.getScrollable();
	int orient = e.getOrientation();

	switch (e.getID()) {
	case ScrollEvent.SCROLL_VALUE_CHANGED:
	    if (orient == ScrollEvent.HORIZONTAL ||
		orient == ScrollEvent.BOTH)
	    {
		if (hScrollbar != null) {
		    hScrollbar.setValue(src.getHValue());
		}
	    }
	    if (orient == ScrollEvent.VERTICAL ||
		orient == ScrollEvent.BOTH)
	    {
		if (vScrollbar != null) {
		    vScrollbar.setValue(src.getVValue());
		}
	    }
	    break;
	case ScrollEvent.SCROLL_SIZE_CHANGED:
	    if (scrollbarDisplayPolicy == SCROLLBARS_AS_NEEDED) {
		if (checkLayout() != null) {
		    invalidate();
		    validate();
		    return;
		}
	    }
	    if (orient == ScrollEvent.HORIZONTAL ||
		orient == ScrollEvent.BOTH)
	    {
		updateHScrollbar(src);
	    }
	    if (orient == ScrollEvent.VERTICAL ||
		orient == ScrollEvent.BOTH)
	    {
		updateVScrollbar(src);
	    }
	    break;
	}
    }

    protected void updateVScrollbar(Scrollable s) {
	if (vScrollbar != null) {
	    vScrollbar.setUnitIncrement(s.getVUnitIncrement());
	    vScrollbar.setBlockIncrement(s.getVBlockIncrement());
	    vScrollbar.setValues(s.getVValue(),
				 s.getVVisibleAmount(),
				 s.getVMinimum(),
				 s.getVMaximum());
	}
    }

    protected void updateHScrollbar(Scrollable s) {
	if (hScrollbar != null) {
	    hScrollbar.setUnitIncrement(s.getHUnitIncrement());
	    hScrollbar.setBlockIncrement(s.getHBlockIncrement());
	    hScrollbar.setValues(s.getHValue(),
				 s.getHVisibleAmount(),
				 s.getHMinimum(),
				 s.getHMaximum());
	}
    }
}


/**
 * The Scroll wrapper.
 */
class ScrollWrapper extends KContainer implements Scrollable {
    Point offset;
    Dimension compSize;
    transient Vector scrollListeners = null;


    ScrollWrapper(Component comp) {
	add(comp);
	setLayout(null);
	compSize = comp.getPreferredSize();
	offset = new Point(0, 0);
	scrollListeners = null;
    }


    public Dimension getPreferredSize() {
	return getComponent(0).getPreferredSize();
    }

    public Dimension getMinimumSize() {
	return getComponent(0).getMinimumSize();
    }

    public void paint(Graphics g) {
	Dimension d = getSize();

	if (d.width > compSize.width) {
	    g.setColor(getBackground());
	    g.fillRect(compSize.width + offset.x,
		       0,
		       d.width - (compSize.width + offset.x),
		       d.height);
	    g.setColor(getForeground());
	}
	if (d.height > compSize.height) {
	    g.setColor(getBackground());
	    g.fillRect(0,
		       compSize.height + offset.y,
		       d.width,
		       d.height - (compSize.height + offset.y));
	    g.setColor(getForeground());
	}

	super.paint(g);
    }

    public void doLayout() {
	Dimension d = getSize();
	offset.y = -Math.max(0, Math.min(-offset.y,
					 compSize.height - getSize().height));
	offset.x = -Math.max(0, Math.min(-offset.x,
					 compSize.width - getSize().width));
	getComponent(0).setBounds(offset.x, offset.y,
				  compSize.width, compSize.height);
    }

    /**
     * Gets the vertical minimum value of the scrollable object.
     */
    public int getVMinimum() {
	return 0;
    }

    /**
     * Gets the horizontal minimum value of the scrollable object.
     */
    public int getHMinimum() {
	return 0;
    }

    /**
     * Gets the vertical maximum value of the scrollable object.
     */
    public int getVMaximum() {
	return compSize.height;
    }

    /**
     * Gets the horizontal maximum value of the scrollable object.
     */
    public int getHMaximum() {
	return compSize.width;
    }

    /**
     * Gets the vertical unit value increment for the scrollable object.
     */
    public int getVUnitIncrement() {
	return 1;
    }

    /**
     * Gets the horizontal unit value increment for the scrollable object.
     */
    public int getHUnitIncrement() {
	return 1;
    }

    /**
     * Gets the vertical block value increment for the scrollable object.
     */
    public int getVBlockIncrement() {
	return getSize().height;
    }

    /**
     * Gets the horizontal block value increment for the scrollable object.
     */
    public int getHBlockIncrement() {
	return getSize().width;
    }

    /**
     * Gets the vertical length of the propertional indicator.
     */
    public int getVVisibleAmount() {
	return getSize().height;
    }

    /**
     * Gets the horizontal length of the propertional indicator.
     */
    public int getHVisibleAmount() {
	return getSize().width;
    }

    /**
     * Gets the vertical current value of the scrollable object.
     */
    public int getVValue() {
	return -offset.y;
    }

    /**
     * Gets the horizontal current value of the scrollable object.
     */
    public int getHValue() {
	return -offset.x;
    }

    /**
     * Sets the vertical current value of the scrollable object.
     * @param v the current value.
     */
    public void setVValue(int v) {
	v = Math.max(0, Math.min(v, compSize.height - getSize().height));
	offset.y = -v;
	invalidate();
	validate();
    }

    /**
     * Sets the horizontal current value of the scrollable object.
     * @param v the current value.
     */
    public void setHValue(int v) {
	v = Math.max(0, Math.min(v, compSize.width - getSize().width));
	offset.x = -v;
	invalidate();
	validate();
    }

    /**
     * Add a listener to recieve scroll events when the value
     * of the scroll component changes.
     * @param l the listener to recieve events.
     */
    public void addScrollListener(ScrollListener l) {
	if (l == null)
	    return;
	if (scrollListeners == null)
	    scrollListeners = new Vector();
	scrollListeners.addElement(l);
    }

    /**
     * Removes an scroll listener.
     * @param l the listener being removed.
     */
    public void removeScrollListener(ScrollListener l) {
	if (scrollListeners == null)
	    return;
	scrollListeners.removeElement(l);
	if (scrollListeners.size() == 0)
	    scrollListeners = null;
    }

    /** Notifies the specified scroll event to the scroll listeners. */
    protected void notifyScrollListeners(ScrollEvent event) {
	if (scrollListeners == null)
	    return;
	for (Enumeration e = scrollListeners.elements(); e.hasMoreElements(); )
	{
	    ((ScrollListener)e.nextElement()).scrollValueChanged(event);
	}
    }


    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	if (scrollListeners != null) {
	    for (Enumeration e = scrollListeners.elements();
		 e.hasMoreElements();
		 )
	    {
		ScrollListener l = (ScrollListener)e.nextElement();
		if (l instanceof java.io.Serializable) {
		    s.writeObject(l);
		}
	    }
	}
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	s.defaultReadObject();
	Object listenerOrNull;
	while ((listenerOrNull = s.readObject()) != null) {
	    addScrollListener((ScrollListener)listenerOrNull);
	}
    }
}
