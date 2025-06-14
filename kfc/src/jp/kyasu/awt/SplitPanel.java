/*
 * SplitPanel.java
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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;

/**
 * The <code>SplitPanel</code> is the lightweight container class that can
 * resize components by using split bars.
 *
 * @version 	11 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class SplitPanel extends Panel {
    /** The split orientation. */
    protected int orientation;

    /** The gap between components. */
    protected int gap;

    /**
     * This mode value determines if panel automatically resizes the
     * width or height.
     */
    protected int autoResizeMode;


    /** Splits components in a vertical orientation. */
    static public final int VERTICAL   = 0;

    /** Splits components in a horizontal orientation. */
    static public final int HORIZONTAL = 1;


    /** Do not auto resize column when panel is resized. */
    static public final int AUTO_RESIZE_OFF            = 0;

    /** Auto resize last component only when panel is resized */
    static public final int AUTO_RESIZE_LAST_COMPONENT = 1;

    /** Proportionately resize all component when panel is resized */
    static public final int AUTO_RESIZE_ALL_COMPONENTS = 2;


    /** The minimum component size. */
    static public final int MIN_COMPONENT_SIZE = 4;

    /** The thickness for the hidden split bar. */
    static protected final int SPLIT_HIDDEN_THICKNESS = 4;

    /** The thickness for the split line. */
    static protected final int SPLIT_LINE_THICKNESS   = 1;


    /**
     * Creates a new split panel with the specified orientation.
     *
     * @param orientation the split orientation.
     */
    public SplitPanel(int orientation) {
	this(orientation, 0);
    }

    /**
     * Creates a new split panel with the specified orientation and gaps.
     *
     * @param orientation the split orientation.
     * @param gap         the gap between components.
     */
    public SplitPanel(int orientation, int gap) {
	this(orientation, gap, new Insets(0, 0, 0, 0));
    }

    /**
     * Creates a new split panel with the specified orientation and gaps.
     *
     * @param orientation the split orientation.
     * @param gap         the gap between components.
     * @param insets      the insets of the panel.
     */
    public SplitPanel(int orientation, int gap, Insets insets) {
	super(null, insets);
	switch (orientation) {
	case VERTICAL:
	case HORIZONTAL:
	    break;
	default:
	    throw new IllegalArgumentException("improper orientation");
	}
	if (gap < 0)
	    throw new IllegalArgumentException("improper gap");
	this.orientation = orientation;
	this.gap = gap;
	this.autoResizeMode = AUTO_RESIZE_LAST_COMPONENT;
	super.setLayout(new SplitLayout(orientation, gap, autoResizeMode));
    }


    /**
     * Returns the split orientation of this panel.
     *
     * @return the split orientation.
     */
    public int getOrientation() {
	return orientation;
    }

    /**
     * Sets the split orientation of this panel.
     *
     * @param orientation the split orientation.
     */
    public void setOrientation(int orientation) {
	switch (orientation) {
	case VERTICAL:
	case HORIZONTAL:
	    break;
	default:
	    throw new IllegalArgumentException("improper orientation");
	}
	this.orientation = orientation;
	SplitLayout layout = (SplitLayout)getLayout();
	layout.orientation = orientation;
	invalidate();
    }

    /**
     * Returns the gap between components.
     *
     * @return the gap between components.
     */
    public int getGap() {
	return gap;
    }

    /**
     * Sets the gap between components.
     *
     * @param gap the gap between components
     */
    public void setGap(int gap) {
	if (gap < 0)
	    throw new IllegalArgumentException("improper gap");
	this.gap = gap;
	SplitLayout layout = (SplitLayout)getLayout();
	layout.gap = gap;
	invalidate();
    }

    /**
     * Returns the auto resize mode of the panel. The default is
     * AUTO_RESIZE_LAST_COMPONENT.
     *
     * @return the auto resize mode of the table.
     */
    public int getAutoResizeMode() {
	return autoResizeMode;
    }

    /**
     * Sets the the auto resize mode of the panel.
     *
     * @param mode the auto resize mode.
     * @see #AUTO_RESIZE_OFF
     * @see #AUTO_RESIZE_LAST_COMPONENT
     * @see #AUTO_RESIZE_ALL_COMPONENTS
     */
    public void setAutoResizeMode(int mode) {
	switch (mode) {
	case AUTO_RESIZE_OFF:
	case AUTO_RESIZE_LAST_COMPONENT:
	case AUTO_RESIZE_ALL_COMPONENTS:
	    break;
	default:
	    throw new IllegalArgumentException("improper auto resize mode");
	}
	autoResizeMode = mode;
	SplitLayout layout = (SplitLayout)getLayout();
	layout.autoResizeMode = autoResizeMode;
	invalidate();
    }

    /**
     * Returns the split sizes.
     */
    public int[] getSplitSizes() {
	return ((SplitLayout)getLayout()).getSplitSizes(this);
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
     * Returns the graphics object for split.
     *
     * @return the graphics object for split.
     */
    public Graphics getGraphicsForSplit() {
	return getGraphics();
    }

    /**
     * Returns the size for split.
     *
     * @return the size for split.
     */
    public Dimension getSizeForSplit() {
	return getSize();
    }

    /**
     * Informs that the split value is changed.
     */
    public void splitValueChanged(int newSizes[]) {
	// do nothing
    }

    /**
     * Informs that the layout is changed.
     */
    public void layoutChanged() {
	invalidate();
	validate();
    }

    /**
     * Adds the specified component to this container.
     *
     * @param comp        the component to be added.
     * @param constraints not applicable.
     * @param index       the position of child component.
     */
    protected void addImpl(Component comp, Object constraints, int index) {
	synchronized (getTreeLock()) {
	    super.addImpl(comp, constraints, -1);
	    super.addImpl(new SplitBar(orientation), null, 0);
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
	    if (c instanceof SplitBar) {
		return;
	    }
	    super.remove(index);
	    super.remove(0); // remove SplitBar
	}
    }
}


/**
 * The <code>SplitBar</code> class implements a split bar for
 * <code>SplitPanel</code>.
 */
class SplitBar extends KComponent {
    int orientation;
    boolean hidden = true;
    int splitPosition = -1;
    Component prev;
    Component next;

    SplitBar(int orientation) {
	setOrientation(orientation);
	enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    void setOrientation(int orientation) {
	this.orientation = orientation;
	setCursor(orientation == SplitPanel.VERTICAL ?
			Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR) :
			Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
    }

    public void paint(Graphics g) {
	if (!hidden) {
	    Dimension d = getSize();
	    g.setColor(getBackground());
	    g.fillRect(0, 0, d.width, d.height);
	    g.setColor(getForeground());
	}
    }

    protected synchronized void processMouseEvent(MouseEvent e) {
	switch (e.getID()) {
	case MouseEvent.MOUSE_PRESSED:
	    if (contains(e.getX(), e.getY())) {
		Point p = getLocation();
		splitStart(p.x + e.getX(), p.y + e.getY());
	    }
	    break;
	case MouseEvent.MOUSE_RELEASED:
	    Point p = getLocation();
	    splitEnd(p.x + e.getX(), p.y + e.getY());
	    break;
	case MouseEvent.MOUSE_EXITED:
	    if (e.getClickCount() != 0 && splitPosition >= 0) {
		toggleSplitBar();
		splitPosition = -1;
	    }
	    break;
	}
	super.processMouseEvent(e);
    }

    protected synchronized void processMouseMotionEvent(MouseEvent e) {
	switch (e.getID()) {
	case MouseEvent.MOUSE_DRAGGED:
	    Point p = getLocation();
	    splitMoved(p.x + e.getX(), p.y + e.getY());
	    break;
	}
	super.processMouseMotionEvent(e);
    }

    synchronized void splitStart(int x, int y) {
	SplitPanel sp = (SplitPanel)getParent();
	if (sp == null)
	    return;
	splitPosition = (orientation == SplitPanel.VERTICAL ? y : x);
	toggleSplitBar();
    }

    synchronized void splitMoved(int x, int y) {
	SplitPanel sp = (SplitPanel)getParent();
	if (sp == null)
	    return;
	if (splitPosition < 0) {
	    return;
	}
	toggleSplitBar();
	if (orientation == SplitPanel.VERTICAL) {
	    splitPosition = y;
	    if (prev != null) {
		splitPosition = Math.max(splitPosition,
					 prev.getLocation().y +
						SplitPanel.MIN_COMPONENT_SIZE);
	    }
	    if (next != null) {
		splitPosition = Math.min(splitPosition,
					 next.getLocation().y +
						next.getSize().height -
						sp.gap -
						SplitPanel.MIN_COMPONENT_SIZE);
	    }
	}
	else {
	    splitPosition = x;
	    if (prev != null) {
		splitPosition = Math.max(splitPosition,
					 prev.getLocation().x +
						SplitPanel.MIN_COMPONENT_SIZE);
	    }
	    if (next != null) {
		splitPosition = Math.min(splitPosition,
					 next.getLocation().x +
						next.getSize().width -
						sp.gap -
						SplitPanel.MIN_COMPONENT_SIZE);
	    }
	}
	toggleSplitBar();
    }

    synchronized void splitEnd(int x, int y) {
	SplitPanel sp = (SplitPanel)getParent();
	if (sp == null)
	    return;
	if (splitPosition >= 0) {
	    toggleSplitBar();
	    int pos = splitPosition;
	    splitPosition = -1;
	    if (prev != null &&
	    	pos == (orientation == SplitPanel.VERTICAL ?
			prev.getLocation().y + prev.getSize().height:
			prev.getLocation().x + prev.getSize().width))
	    {
		return;
	    }
	    SplitLayout layout = (SplitLayout)sp.getLayout();
	    if (orientation == SplitPanel.VERTICAL) {
		if (prev != null) {
		    int size = pos - prev.getLocation().y;
		    layout.sizes.put(prev, new Integer(size));
		}
		if (next != null) {
		    int size = next.getLocation().y + next.getSize().height
							- (pos + sp.gap);
		    layout.sizes.put(next, new Integer(size));
		}
	    }
	    else {
		if (prev != null) {
		    int size = pos - prev.getLocation().x;
		    layout.sizes.put(prev, new Integer(size));
		}
		if (next != null) {
		    int size = next.getLocation().x + next.getSize().width
							- (pos + sp.gap);
		    layout.sizes.put(next, new Integer(size));
		}
	    }
	    sp.layoutChanged();
	}
    }

    synchronized void toggleSplitBar() {
	SplitPanel sp = (SplitPanel)getParent();
	if (sp == null)
	    return;
	Graphics g = sp.getGraphicsForSplit();
	if (g == null)
	    return;
	Dimension d = sp.getSizeForSplit();

	g.setXORMode(Color.white);
	g.setColor(Color.black);
	int pos = splitPosition - (SplitPanel.SPLIT_LINE_THICKNESS / 2);
	if (orientation == SplitPanel.VERTICAL) {
	    g.fillRect(0, pos, d.width, SplitPanel.SPLIT_LINE_THICKNESS);
	}
	else {
	    g.fillRect(pos, 0, SplitPanel.SPLIT_LINE_THICKNESS, d.height);
	}
	g.dispose();
    }
}


/**
 * The <code>SplitLayout</code> class is a layout manager that lays out a
 * container's components.
 */
class SplitLayout implements LayoutManager2, java.io.Serializable {
    int orientation;
    int gap;
    int autoResizeMode;
    Hashtable sizes;


    /**
     * Constructs a <code>SplitLayout</code> object.
     */
    SplitLayout(int orientation, int gap, int autoResizeMode) {
	this.orientation = orientation;
	this.gap = gap;
	this.autoResizeMode = autoResizeMode;
	sizes = new Hashtable();
    }


    /**
     * Adds the specified component with the specified name to
     * the layout.
     * @param name the component name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
	// do nothing
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component ot be removed
     */
    public void removeLayoutComponent(Component comp) {
	sizes.remove(comp);
    }

    /**
     * Calculates the preferred size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     *
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
	Insets insets = parent.getInsets();
	int compCount = parent.getComponentCount();
	int x = 0;
	int y = 0;

	int ncomps = 0;
	for (int i = 0; i < compCount; i++) {
	    Component c = parent.getComponent(i);
	    if (!(c instanceof SplitBar) && c.isVisible()) {
		ncomps++;
		Dimension d = c.getPreferredSize();
		if (orientation == SplitPanel.VERTICAL) {
		    if (d.width > x) x = d.width;
		    y += d.height;
		}
		else {
		    x += d.width;
		    if (d.height > y) y = d.height;
		}
	    }
	}
	if (ncomps > 1) {
	    if (orientation == SplitPanel.VERTICAL) {
		y += gap * (ncomps - 1);
	    }
	    else {
		x += gap * (ncomps - 1);
	    }
	}
	return new Dimension(x + (insets.left + insets.right),
			     y + (insets.top  + insets.bottom));
    }

    /**
     * Calculates the minimum size dimensions for the specified
     * panel given the components in the specified parent container.
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
	Insets insets = parent.getInsets();
	int compCount = parent.getComponentCount();
	int x = 0;
	int y = 0;

	int ncomps = 0;
	for (int i = 0; i < compCount; i++) {
	    Component c = parent.getComponent(i);
	    if (!(c instanceof SplitBar) && c.isVisible()) {
		ncomps++;
		Dimension d = c.getMinimumSize();
		if (orientation == SplitPanel.VERTICAL) {
		    if (d.width > x) x = d.width;
		    y += d.height;
		}
		else {
		    x += d.width;
		    if (d.height > y) y = d.height;
		}
	    }
	}
	if (ncomps > 1) {
	    if (orientation == SplitPanel.VERTICAL) {
		y += gap * (ncomps - 1);
	    }
	    else {
		x += gap * (ncomps - 1);
	    }
	}
	return new Dimension(x + (insets.left + insets.right),
			     y + (insets.top  + insets.bottom));
    }

    /**
     * Lays out the container in the specified panel.
     * @param parent the component which needs to be laid out
     */
    public void layoutContainer(Container parent) {
	Insets insets = parent.getInsets();
	int compCount = parent.getComponentCount();
	int width  = parent.getSize().width;
	int height = parent.getSize().height;
	width  -= insets.left + insets.right;
	height -= insets.top  + insets.bottom;

	int ncomps = 0;
	int nbars = 0;
	int total = 0;
	int lastSize = 0;
	Component lastComp = null;
	SplitBar bars[] = new SplitBar[compCount];
	for (int i = 0; i < compCount; ++i) {
	    Component c = parent.getComponent(i);
	    if (!c.isVisible()) {
		continue;
	    }
	    else if (c instanceof SplitBar) {
		bars[nbars++] = (SplitBar)c;
	    }
	    else {
		ncomps++;
		lastSize = ((Integer)sizes.get(c)).intValue();
		if (lastSize < 0) {
		    Dimension d = c.getPreferredSize();
		    lastSize = (orientation == SplitPanel.VERTICAL ?
							d.height : d.width);
		    sizes.put(c, new Integer(lastSize));
		}
		total += lastSize;
		lastComp = c;
	    }
	}
	if (ncomps == 0)
	    return;
	int diff = 0;
	if (autoResizeMode == SplitPanel.AUTO_RESIZE_OFF) {
	    // do nothing
	}
	else if (autoResizeMode == SplitPanel.AUTO_RESIZE_LAST_COMPONENT) {
	    if (ncomps > 1) {
		int max = (orientation == SplitPanel.VERTICAL ? height : width);
		max -= gap * (ncomps - 1);
		if (total > max) {
		    int min = (total-lastSize) + SplitPanel.MIN_COMPONENT_SIZE;
		    if (min > max) {
			diff = (min - max) / (ncomps - 1);
		    }
		}
	    }
	}
	else { // autoResizeMode = SplitPanel.AUTO_RESIZE_ALL_COMPONENTS
	    int max = (orientation == SplitPanel.VERTICAL ? height : width);
	    max -= gap * (ncomps - 1);
	    if (total != max) {
		diff = (total - max) / ncomps;
	    }
	}
	int barIndex = 0;
	total = (orientation == SplitPanel.VERTICAL ? insets.top : insets.left);
	Component prev = null;
	for (int i = 0; i < compCount; i++) {
	    Component c = parent.getComponent(i);
	    if (!c.isVisible()) {
		continue;
	    }
	    else if (!(c instanceof SplitBar)) {
		if (prev != null) {
		    SplitBar bar = bars[barIndex++];
		    total = layoutSplitBar(bar, total, insets.left, insets.top,
					   width, height, prev, c);
		}
		prev = c;
		int size = ((Integer)sizes.get(c)).intValue();
		if (orientation == SplitPanel.VERTICAL) {
		    int h = ((autoResizeMode != SplitPanel.AUTO_RESIZE_OFF &&
			      c == lastComp) ?
				height + insets.top - total : size - diff);
		    c.setBounds(insets.left, total, width, h);
		    total += h;
		    sizes.put(c, new Integer(h));
		}
		else {
		    int w = ((autoResizeMode != SplitPanel.AUTO_RESIZE_OFF &&
			      c == lastComp) ?
				width + insets.left - total : size - diff);
		    c.setBounds(total, insets.top, w, height);
		    total += w;
		    sizes.put(c, new Integer(w));
		}
	    }
	}
	if (autoResizeMode == SplitPanel.AUTO_RESIZE_OFF && prev != null) {
	    SplitBar bar = bars[barIndex++];
	    layoutSplitBar(bar, total, insets.left, insets.top,
			   width, height, prev, null);
	}
	while (barIndex < nbars) {
	    SplitBar bar = bars[barIndex++];
	    bar.hidden = true;
	    bar.setBounds(0, 0, 0, 0);
	}
	((SplitPanel)parent).splitValueChanged(getSplitSizes(parent));
    }

    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     * @param comp the component to be added
     * @param constraints  where/how the component is added to the layout.
     */
    public void addLayoutComponent(Component comp, Object constraints) {
	if (constraints instanceof Integer) {
	    sizes.put(comp, constraints);
	}
	else {
	    sizes.put(comp, new Integer(-1));
	}
    }

    /**
     * Returns the maximum size of this component.
     * @see java.awt.Component#getMinimumSize()
     * @see java.awt.Component#getPreferredSize()
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(Container target) {
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container target) {
	return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container target) {
	return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
    }


    int layoutSplitBar(SplitBar bar, int pos, int left, int top,
		       int width, int height, Component prev, Component next)
    {
	bar.setOrientation(orientation);
	bar.prev = prev;
	bar.next = next;
	if (orientation == SplitPanel.VERTICAL) {
	    if (gap == 0) {
		bar.hidden = true;
		bar.setBounds(
			left,
			pos - (SplitPanel.SPLIT_HIDDEN_THICKNESS / 2),
			width,
			SplitPanel.SPLIT_HIDDEN_THICKNESS);
	    }
	    else {
		bar.hidden = false;
		bar.setBounds(left, pos, width, gap);
		pos += gap;
	    }
	}
	else {
	    if (gap == 0) {
		bar.hidden = true;
		bar.setBounds(
			pos - (SplitPanel.SPLIT_HIDDEN_THICKNESS / 2),
			top,
			SplitPanel.SPLIT_HIDDEN_THICKNESS,
			height);
	    }
	    else {
		bar.hidden = false;
		bar.setBounds(pos, top, gap, height);
		pos += gap;
	    }
	}
	return pos;
    }

    int[] getSplitSizes(Container parent) {
	int compCount = parent.getComponentCount();

	int ncomps = 0;
	for (int i = 0; i < compCount; i++) {
	    Component c = parent.getComponent(i);
	    if (!(c instanceof SplitBar) && c.isVisible()) {
		ncomps++;
	    }
	}

	int newSizes[] = new int[ncomps];
	int comp = 0;
	for (int i = 0; i < compCount; i++) {
	    Component c = parent.getComponent(i);
	    if (!(c instanceof SplitBar) && c.isVisible()) {
		newSizes[comp++] = ((Integer)sizes.get(c)).intValue();
	    }
	}

	return newSizes;
    }
}
