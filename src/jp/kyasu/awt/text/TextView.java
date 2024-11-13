/*
 * TextView.java
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

package jp.kyasu.awt.text;

import jp.kyasu.awt.AWTResources;
import jp.kyasu.awt.KComponent;
import jp.kyasu.awt.Scrollable;
import jp.kyasu.awt.event.ScrollEvent;
import jp.kyasu.awt.event.ScrollListener;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.TextLayout;
import jp.kyasu.graphics.text.TextLineInfo;
import jp.kyasu.graphics.text.TextPositionInfo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

/**
 * The <code>TextView</code> class is an abstract base class that views
 * the <code>TextLayout</code> object.
 *
 * @version 	20 Nov 1999
 * @author 	Kazuki YASUMATSU
 */
public abstract class TextView extends KComponent implements Scrollable {
    protected TextLayout layout;
    protected int lineWrap;
    protected Point offset;
    protected boolean selectionVisible;
    protected boolean selectionShowing;
    protected Color selectionForeground;
    protected Color selectionBackground;

    transient protected Vector scrollListeners;
    transient protected TextPositionInfo _visibleBegin;
    transient protected TextPositionInfo _visibleEnd;

    protected boolean scrolledUp    = false;
    protected boolean scrolledDown  = false;
    protected boolean scrolledLeft  = false;
    protected boolean scrolledRight = false;


    /**
     * The default foreground color.
     */
    static public final Color DEFAULT_FOREGROUND =
	AWTResources.getResourceColor("kfc.text.foreground", Color.black);

    /**
     * The default background color.
     */
    static public final Color DEFAULT_BACKGROUND =
	AWTResources.getResourceColor("kfc.text.background", Color.white);

    /**
     * The default selection foreground color.
     */
    static public final Color DEFAULT_SELECTION_FOREGROUND =
	AWTResources.getResourceColor("kfc.text.selectionForeground",
				      Color.white);

    /**
     * The default selection background color.
     */
    static public final Color DEFAULT_SELECTION_BACKGROUND=
	AWTResources.getResourceColor("kfc.text.selectionBackground",
				      new Color(0, 0, 128));


    /**
     * Constructs a text view with the specified line wrapping style.
     *
     * @param lineWrap the line wrapping style.
     * @see jp.kyasu.graphics.RichTextStyle#CHAR_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#WORD_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#NO_WRAP
     */
    public TextView(int lineWrap) {
	//setDoubleBuffered(true);

	setLineWrapInner(lineWrap);
	offset = new Point(0, 0);

	selectionVisible = true;
	selectionShowing = false;

	selectionForeground = DEFAULT_SELECTION_FOREGROUND;
	selectionBackground = DEFAULT_SELECTION_BACKGROUND;

	setForeground(DEFAULT_FOREGROUND);
	setBackground(DEFAULT_BACKGROUND);

	//setTextLayout(createTextLayout());
    }


    /**
     * Returns the <code>RichText</code> object being viewed.
     */
    public abstract RichText getRichText();

    /**
     * Creates a new <code>TextLayout</code> object.
     */
    protected TextLayout createTextLayout() {
	Locale locale;
	if (layout != null) {
	    locale = layout.getLocale();
	}
	else {
	    try { locale = getLocale(); }
	    catch (Exception e) { locale = Locale.getDefault(); }
	}
	TextLayout newLayout = new TextLayout(getRichText(), lineWrap, locale);
	if (layout != null && layout.echoCharIsSet()) {
	    newLayout.setEchoChar(layout.getEchoChar());
	}
	return newLayout;
    }

    /**
     * Sets the <code>TextLayout</code> object of this view.
     */
    protected synchronized void setTextLayout(TextLayout layout) {
	if (layout == null)
	    throw new NullPointerException();

	selectionShowing = false;
	this.layout = layout;
	try { this.layout.setLocale(getLocale()); }
	catch (Exception e) {}
	offset = new Point(0, 0);
	_visibleBegin = _visibleEnd = null;

	scrolledUp    = false;
	scrolledDown  = false;
	scrolledLeft  = false;
	scrolledRight = false;

	if (isValid()) {
	    layout.setWidth(getSize().width);
	    layoutResized(-1, -1);
	}

	if (isShowing()) {
	    repaintNow();
	}
    }

    /**
     * Returns the line wrapping style.
     * @see #setLineWrap(int)
     */
    public int getLineWrap() {
	return lineWrap;
    }

    /**
     * Sets the line wrapping style.
     *
     * @param lineWrap the line wrapping style.
     * @see #getLineWrap()
     * @see jp.kyasu.graphics.RichTextStyle#CHAR_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#WORD_WRAP
     * @see jp.kyasu.graphics.RichTextStyle#NO_WRAP
     */
    public synchronized void setLineWrap(int lineWrap) {
	if (this.lineWrap == lineWrap)
	    return;
	setLineWrapInner(lineWrap);
	setTextLayout(createTextLayout());
    }

    protected void setLineWrapInner(int lineWrap) {
	switch (lineWrap) {
	case RichTextStyle.CHAR_WRAP:
	case RichTextStyle.WORD_WRAP:
	case RichTextStyle.NO_WRAP:
	    this.lineWrap = lineWrap;
	    return;
	}
	throw new IllegalArgumentException("improper line wrapping");
    }

    /**
     * Sets the locale of this view.
     */
    public synchronized void setLocale(Locale l) {
	super.setLocale(l);
	layout.setLocale(l);
	setTextLayout(createTextLayout());
    }

    /**
     * Tests if the view can be traversed using Tab or Shift-Tab keyboard
     * focus traversal.
     */
    public boolean isFocusTraversable() {
	return true;
    }

    /**
     * Returns the location of the <code>TextLayout</code> object.
     * @see #setLocationOfText(java.awt.Point)
     */
    public Point getLocationOfText() {
	return new Point(offset.x, offset.y);
    }

    /**
     * Sets the location of the <code>TextLayout</code> object.
     * @see #getLocationOfText()
     */
    public void setLocationOfText(Point p) {
	scrollTo(p);
    }

    /**
     * Tests if the selection is visible.
     * @see #setSelectionVisible(boolean)
     */
    public boolean isSelectionVisible() {
	return selectionVisible;
    }

    /**
     * Makes the selection visible.
     * @see #isSelectionVisible()
     */
    public synchronized void setSelectionVisible(boolean b) {
	if (selectionVisible == b)
	    return;
	if (selectionVisible && isShowing()) {
	    hideSelection();
	}
	selectionVisible = b;
	if (selectionVisible && isShowing()) {
	    showSelection();
	}
    }

    /**
     * Returns the selection foreground color.
     * @see #setSelectionForeground(java.awt.Color)
     */
    public Color getSelectionForeground() {
	return selectionForeground;
    }

    /**
     * Sets the selection foreground color.
     * @see #getSelectionForeground()
     */
    public synchronized void setSelectionForeground(Color c) {
	if (c == null || selectionForeground.equals(c))
	    return;
	selectionForeground = c;
	if (isShowing()) {
	    hideSelection();
	    showSelection();
	}
    }

    /**
     * Returns the selection background color.
     * @see #setSelectionBackground(java.awt.Color)
     */
    public Color getSelectionBackground() {
	return selectionBackground;
    }

    /**
     * Sets the selection background color.
     * @see #getSelectionBackground()
     */
    public synchronized void setSelectionBackground(Color c) {
	if (c == null || selectionBackground.equals(c))
	    return;
	selectionBackground = c;
	if (isShowing()) {
	    hideSelection();
	    showSelection();
	}
    }

    /**
     * Returns the preferred size of this view.
     */
    public Dimension getPreferredSize() {
	synchronized (getTreeLock()) {
	    return getPreferredSize(20, 80);
	}
    }

    /**
     * Returns the minimum size of this view.
     */
    public Dimension getMinimumSize() {
	synchronized (getTreeLock()) {
	    return getPreferredSize(1, 1);
	}
    }

    /**
     * Returns the preferred size of this view with the specified number
     * of rows and columns.
     *
     * @param  rows    the number of rows.
     * @param  columns the number of columns.
     * @return the preferred size with the specified number of rows and columns.
     */
    public Dimension getPreferredSize(int rows, int columns) {
	return new Dimension(getPreferredWidth(columns),
			     getPreferredHeight(rows));
    }

    /**
     * Returns the preferred width of this view with the specified number
     * of columns.
     *
     * @param columns the number of columns.
     * @return the preferred width with the specified number of columns.
     */
    public int getPreferredWidth(int columns) {
	ParagraphStyle pstyle =
			getRichText().getRichTextStyle().getParagraphStyle();
	int charInc = layout.getPreferredCharIncrementSize();
	return (charInc * columns) +
			pstyle.getLeftIndent() + pstyle.getRightIndent();
    }

    /**
     * Returns the preferred height of this view with the specified number
     * of rows.
     *
     * @param rows the number of rows.
     * @return the preferred height with the specified number of rows.
     */
    public int getPreferredHeight(int rows) {
	int lineInc = layout.getPreferredLineIncrementSize();
	return (lineInc * rows);
    }

    /**
     * Returns the number of rows.
     */
    public int getRows() {
	return getSize().height / layout.getPreferredLineIncrementSize();
    }

    /**
     * Returns the number of columns.
     */
    public int getColumns() {
	ParagraphStyle pstyle =
			getRichText().getRichTextStyle().getParagraphStyle();
	return (getSize().width -
			pstyle.getLeftIndent() - pstyle.getRightIndent())
				/ layout.getPreferredCharIncrementSize();
    }

    /**
     * Moves and resizes this view.
     *
     * @param <code>x</code> The new <i>x</i>-coordinate of this view.
     * @param <code>y</code> The new <i>y</i>-coordinate of this view.
     * @param <code>width</code> The new <code>width</code> of this view.
     * @param <code>height</code> The new <code>height</code> of this view.
     */
    public synchronized void setBounds(int x, int y, int width, int height) {
	if (isShowing() && needsToRedrawSelection())
	    hideSelection();

	Cursor save = getCursor();
	try {
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    if (width > 0) {
		layout.setWidth(width);
	    }
	}
	finally { setCursor(save); }

	resetLocationOfText();

	if (offset.x != 0 && layout.getSize().width <= width) {
	    offset.x = 0;
	}
	if (offset.y != 0 && layout.getSize().height <= height) {
	    offset.y = 0;
	}

	_visibleBegin = _visibleEnd = null;
	selectionShowing = false;

	super.setBounds(x, y, width, height);

	notifyScrollListeners(
		new ScrollEvent(this, ScrollEvent.SCROLL_SIZE_CHANGED,
				ScrollEvent.BOTH));
    }

    /**
     * Resets the location of the layout text.
     * This method is called from setBounds().
     */
    protected void resetLocationOfText() {
	offset.x = offset.y = 0;
    }

    /**
     * Paints this view.
     */
    protected synchronized void paintOn(Graphics _g) {
	if (isShowing() && needsToRedrawSelection())
	    hideSelection();

	Dimension d = getSize();
	Rectangle r = _g.getClipBounds();
	boolean scrolled =
		(scrolledUp || scrolledDown || scrolledLeft || scrolledRight);
	Graphics g = null;
	if (!doubleBuffered && scrolled) {
	    g = _g.create();
	    if (g != null) {
		if (scrolledUp) {
		    r.height += r.y;
		    r.y = 0;
		}
		if (scrolledDown) {
		    r.height = d.height - r.y;
		}
		if (scrolledLeft) {
		    r.width += r.x;
		    r.x = 0;
		}
		if (scrolledRight) {
		    r.width = d.width - r.x;
		}
		g.clipRect(r.x, r.y, r.width, r.height);
	    }
	}
	if (g == null) {
	    g = _g;
	}
	g.setColor(getBackground());
	g.fillRect(r.x, r.y, r.width, r.height);
	g.setColor(getForeground());

	//paint(g, getVisibleBegin(), getVisibleEnd());

	if (r.x == 0 && r.y == 0 && r.width == d.width && r.height == d.height)
	{
	    paint(g, getVisibleBegin(), getVisibleEnd());
	}
	else {
	    TextPositionInfo begin = layout.getLineBeginPositionOver(
						-offset.y+r.y);
	    TextPositionInfo end = layout.getLineBeginPositionUnder(
						begin,
						-offset.y + r.y + r.height - 1);
	    paint(g, begin, end);
	}

	if (scrolled) {
	    scrolledUp    = false;
	    scrolledDown  = false;
	    scrolledLeft  = false;
	    scrolledRight = false;
	}

	if (g != _g) {
	    g.dispose();
	}
    }

    /**
     * Paints this view with the specified range.
     */
    protected void paint(Graphics g,
			 TextPositionInfo begin, TextPositionInfo end)
    {
	if (!selectionVisible)
	    selectionShowing = false;
	else
	    selectionShowing = true;
	g.setColor(getForeground());
	layout.draw(g, offset, begin, end);
    }

    /**
     * Shows the selection.
     */
    protected synchronized void showSelection() {
	if (!selectionVisible || selectionShowing)
	    return;
	selectionShowing = !selectionShowing;
    }

    /**
     * Hides the selection.
     */
    protected synchronized void hideSelection() {
	if (!selectionVisible || !selectionShowing)
	    return;
	selectionShowing = !selectionShowing;
    }


    /**
     * Gets the vertical minimum value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVMinimum() {
	return 0;
    }

    /**
     * Gets the horizontal minimum value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHMinimum() {
	return 0;
    }

    /**
     * Gets the vertical maximum value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVMaximum() {
	return layout.getSize().height;
    }

    /**
     * Gets the horizontal maximum value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHMaximum() {
	return layout.getSize().width;
    }

    /**
     * Gets the vertical unit value increment for the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVUnitIncrement() {
	return layout.getPreferredLineIncrementSize();
    }

    /**
     * Gets the horizontal unit value increment for the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHUnitIncrement() {
	return layout.getPreferredCharIncrementSize();
    }

    /**
     * Gets the vertical block value increment for the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVBlockIncrement() {
	int height = getSize().height;
	int lineInc = layout.getPreferredLineIncrementSize();
	return (height < lineInc ?
			lineInc : (height / lineInc * lineInc) - lineInc);
    }

    /**
     * Gets the horizontal block value increment for the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHBlockIncrement() {
	int width  = getSize().width;
	int charInc = layout.getPreferredCharIncrementSize();
	return (width < charInc ?
			charInc : (width / charInc * charInc) - charInc);
    }

    /**
     * Gets the vertical length of the propertional indicator.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVVisibleAmount() {
	return getSize().height;
    }

    /**
     * Gets the horizontal length of the propertional indicator.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHVisibleAmount() {
	return getSize().width;
    }

    /**
     * Gets the vertical current value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVValue() {
	return -offset.y;
    }

    /**
     * Gets the horizontal current value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHValue() {
	return -offset.x;
    }

    /**
     * Sets the vertical current value of the scrollable object.
     * @param v the current value.
     * @see jp.kyasu.awt.Scrollable
     */
    public void setVValue(int v) {
	scrollY(-v, false);
    }

    /**
     * Sets the horizontal current value of the scrollable object.
     * @param v the current value.
     * @see jp.kyasu.awt.Scrollable
     */
    public void setHValue(int v) {
	scrollX(-v, false);
    }

    /**
     * Add a listener to recieve scroll events when the value
     * of the scroll component changes.
     * @param l the listener to recieve events.
     * @see jp.kyasu.awt.Scrollable
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
     * @see jp.kyasu.awt.Scrollable
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


    /**
     * Scrolls this component horizontally to the specified x-position.
     */
    protected void scrollX(int x) {
	scrollX(x, true);
    }

    /**
     * Scrolls this component horizontally to the specified x-position.
     * @param x         the x-position.
     * @param emitEvent if true, emits a ScrollEvent object.
     */
    protected synchronized void scrollX(int x, boolean emitEvent) {
	if (offset.x == x)
	    return;

	if (!isShowing()) {
	    offset.x = x;
	    _visibleBegin = _visibleEnd = null;
	    if (emitEvent) {
		notifyScrollListeners(
		    new ScrollEvent(this, ScrollEvent.SCROLL_VALUE_CHANGED,
				    ScrollEvent.HORIZONTAL));
	    }
	    return;
	}

	Dimension d = getSize();
	int dx = Math.abs(x - offset.x);
	if (dx > d.width ||
	    layout.getRichTextStyle().isVariableLineHeight() ||
	    layout.getRichTextStyle().getParagraphStyle().getAlignment()
							!= ParagraphStyle.LEFT)
	{
	    offset.x = x;
	    _visibleBegin = _visibleEnd = null;
	    repaintNow();
	    if (emitEvent) {
		notifyScrollListeners(
		    new ScrollEvent(this, ScrollEvent.SCROLL_VALUE_CHANGED,
				    ScrollEvent.HORIZONTAL));
	    }
	    return;
	}

	Graphics g = getPreferredGraphics();
	if (g == null) {
	    offset.x = x;
	    if (emitEvent) {
		notifyScrollListeners(
		    new ScrollEvent(this, ScrollEvent.SCROLL_VALUE_CHANGED,
				    ScrollEvent.HORIZONTAL));
	    }
	    return;
	}

	boolean oneLine =
	    (layout.getLineCount() == 1 && lineWrap == RichTextStyle.NO_WRAP);

	if (needsToRedrawSelection())
	    hideSelection();

	TextPositionInfo begin = null, end = null;
	if (x < offset.x) { // scroll right (view left)
	    if (oneLine) {
		int cw = layout.charWidthAt(getVisibleEnd());
		begin = layout.getTextPositionAt(
				new Point(-offset.x+d.width-1-cw, -offset.y));
		end = layout.getTextPositionAt(
				new Point(-x+d.width-1, -offset.y+d.height-1));
		_visibleBegin = null;
		_visibleEnd = end;
	    }
	    // copyArea() may be fail.
	    scrolledLeft = true;
	    g.copyArea(dx, 0, d.width - dx, d.height, -dx, 0);
	    g.setColor(getBackground());
	    g.fillRect(d.width - dx, 0, dx, d.height);
	    g.setColor(getForeground());
	    //if (!oneLine) g.setClip(d.width - dx, 0, dx, d.height);
	}
	else { // scroll left (view right)
	    if (oneLine) {
		int cw = layout.charWidthAt(getVisibleBegin());
		begin = layout.getTextPositionAt(
				new Point(-x, -offset.y));
		end = layout.getTextPositionAt(
				new Point(-offset.x+cw, -offset.y+d.height-1));
		_visibleBegin = begin;
		_visibleEnd = null;
	    }
	    // copyArea() may be fail.
	    scrolledRight = true;
	    g.copyArea(0, 0, d.width - dx, d.height, dx, 0);
	    g.setColor(getBackground());
	    g.fillRect(0, 0, dx, d.height);
	    g.setColor(getForeground());
	    //if (!oneLine) g.setClip(0, 0, dx, d.height);
	}
	offset.x = x;
	if (oneLine) {
	    paint(g, begin, end);
	}
	else {
	    _visibleBegin = _visibleEnd = null;
	    paint(g, getVisibleBegin(), getVisibleEnd());
	}
	g.dispose();
	syncGraphics();

	if (needsToRedrawSelection())
	    showSelection();

	if (emitEvent) {
	    notifyScrollListeners(
		new ScrollEvent(this, ScrollEvent.SCROLL_VALUE_CHANGED,
				ScrollEvent.HORIZONTAL));
	}
    }

    /**
     * Scrolls this component vertically to the specified y-position.
     */
    protected void scrollY(int y) {
	scrollY(y, true);
    }

    /**
     * Scrolls this component vertically to the specified y-position.
     * @param x         the y-position.
     * @param emitEvent if true, emits a ScrollEvent object.
     */
    protected synchronized void scrollY(int y, boolean emitEvent) {
	if (offset.y == y)
	    return;

	if (!isShowing()) {
	    offset.y = y;
	    _visibleBegin = _visibleEnd = null;
	    if (emitEvent) {
		notifyScrollListeners(
		    new ScrollEvent(this, ScrollEvent.SCROLL_VALUE_CHANGED,
				    ScrollEvent.VERTICAL));
	    }
	    return;
	}

	Dimension d = getSize();
	int dy = Math.abs(y - offset.y);
	if (dy > d.height) {
	    offset.y = y;
	    _visibleBegin = _visibleEnd = null;
	    repaintNow();
	    if (emitEvent) {
		notifyScrollListeners(
		    new ScrollEvent(this, ScrollEvent.SCROLL_VALUE_CHANGED,
				    ScrollEvent.VERTICAL));
	    }
	    return;
	}

	Graphics g = getPreferredGraphics();
	if (g == null) {
	    offset.y = y;
	    if (emitEvent) {
		notifyScrollListeners(
		    new ScrollEvent(this, ScrollEvent.SCROLL_VALUE_CHANGED,
				    ScrollEvent.VERTICAL));
	    }
	    return;
	}

	if (needsToRedrawSelection())
	    hideSelection();

	TextPositionInfo begin, end;
	if (y < offset.y) { // scroll down (view up)
	    /*
	    begin = layout.getTextPositionAt(
			new Point(-offset.x, -offset.y+d.height));
	    end = layout.getTextPositionAt(
			new Point(-offset.x+d.width-1, -y+d.height-1));
	    */
	    begin = layout.getLineBeginPositionOver(-offset.y + d.height);
	    end = layout.getLineBeginPositionUnder(begin,
						       -y + d.height - 1);
	    _visibleBegin = null;
	    _visibleEnd = end;
	    // copyArea() may be fail.
	    scrolledUp = true;
	    g.copyArea(0, dy, d.width, d.height - dy, 0, -dy);
	    g.setColor(getBackground());
	    g.fillRect(0, d.height - dy, d.width, dy);
	    g.setColor(getForeground());
	}
	else { // scroll up (view down)
	    /*
	    begin = layout.getTextPositionAt(
			new Point(-offset.x, -y));
	    end = layout.getTextPositionAt(
			new Point(-offset.x+d.width-1, -offset.y));
	    */
	    begin = layout.getLineBeginPositionOver(-y);
	    end = layout.getLineBeginPositionUnder(begin, -offset.y);
	    _visibleBegin = begin;
	    _visibleEnd = null;
	    // copyArea() may be fail.
	    scrolledDown = true;
	    g.copyArea(0, 0, d.width, d.height - dy, 0, dy);
	    g.setColor(getBackground());
	    g.fillRect(0, 0, d.width, dy);
	    g.setColor(getForeground());
	}
	offset.y = y;
	paint(g, begin, end);
	g.dispose();
	syncGraphics();

	if (needsToRedrawSelection())
	    showSelection();

	if (emitEvent) {
	    notifyScrollListeners(
		new ScrollEvent(this, ScrollEvent.SCROLL_VALUE_CHANGED,
				ScrollEvent.VERTICAL));
	}
    }

    /**
     * Scrolls this view to the specified point, if possible.
     * @return true, if this view is scrolled.
     */
    protected boolean scrollTo(Point p) {
	return scrollXTo(p.x) | scrollYTo(p.y);
    }

    /**
     * Scrolls this view horizontally to the specified x-position, if possible.
     * @return true, if this view is scrolled.
     */
    protected boolean scrollXTo(int x) {
	int vwidth = getSize().width;
	int twidth = layout.getSize().width;
	if (twidth <= vwidth) {
	    return false;
	}
	x = Math.max(Math.min(x, 0), vwidth - twidth);
	if (x == offset.x) {
	    return false;
	}
	scrollX(x);
	return true;
    }

    /**
     * Scrolls this view vertically to the specified y-position, if possible.
     * @return true, if this view is scrolled.
     */
    protected boolean scrollYTo(int y) {
	int vheight = getSize().height;
	int theight = layout.getSize().height;
	if (theight <= vheight) {
	    return false;
	}
	y = Math.max(Math.min(y, 0), vheight - theight);
	if (y == offset.y) {
	    return false;
	}
	scrollY(y);
	return true;
    }

    /**
     * Scrolls this view to the specified text position, if possible.
     * @return true, if this view is scrolled.
     */
    protected boolean scrollTo(TextPositionInfo posInfo) {
	boolean scrolled = false;
	if (lineWrap == RichTextStyle.NO_WRAP) {
	    scrolled = scrollXTo(posInfo);
	}
	else if (posInfo.x > layout.getSize().width) {
	    scrolled = scrollXTo(posInfo);
	}
	else if (offset.x != 0) {
	    scrollX(0);
	    scrolled = true;
	}
	scrolled |= scrollYTo(posInfo);
	return scrolled;
    }

    /**
     * Scrolls this view horizontally to the specified text position,
     * if possible.
     * @return true, if this view is scrolled.
     */
    protected boolean scrollXTo(TextPositionInfo posInfo) {
	int offX = getScrollXTo(posInfo);
	if (offset.x != offX) {
	    scrollX(offX);
	    return true;
	}
	return false;
    }

    /**
     * Scrolls this view vertically to the specified text position,
     * if possible.
     * @return true, if this view is scrolled.
     */
    protected boolean scrollYTo(TextPositionInfo posInfo) {
	int offY = getScrollYTo(posInfo);
	if (offset.y != offY) {
	    scrollY(offY);
	    return true;
	}
	return false;
    }

    /**
     * Returns the preferred x-position for the specified text position.
     */
    protected int getScrollXTo(TextPositionInfo posInfo) {
	int vwidth = getSize().width;
	int twidth = layout.getSize().width;
	if (twidth <= vwidth)
	    return 0;
	int min = vwidth - twidth;
	int left = posInfo.x;
	int right = posInfo.x + layout.charWidthAt(posInfo) + 1;
	//if (layout.getLineCount() == 1 && left + offset.x <= 0) {
	if (left + offset.x <= 0) {
	    return Math.min(Math.max(vwidth - left, min), 0);
	}
	if (left + offset.x < 0) {
	    return Math.min(Math.max(-left, min), 0);
	}
	else if (right + offset.x > vwidth) {
	    return Math.min(Math.max(vwidth - right, min), 0);
	}
	return Math.min(Math.max(offset.x, min), 0);
    }

    /**
     * Returns the preferred y-position for the specified text position.
     */
    protected int getScrollYTo(TextPositionInfo posInfo) {
	int vheight = getSize().height;
	int theight = layout.getSize().height;
	if (theight <= vheight)
	    return 0;
	int min = vheight - theight;
	int top = posInfo.y;
	int bottom = posInfo.y + posInfo.lineSkip;
	if (top + offset.y < 0) {
	    return Math.min(Math.max(-top, min), 0);
	}
	else if (bottom + offset.y > vheight) {
	    return Math.min(Math.max(vheight - bottom, min), 0);
	}
	return Math.min(Math.max(offset.y, min), 0);
    }

    /**
     * Informs scroll listeners that the text layout of this view is resized.
     * @param oldLayoutWidth  the old layout width.
     * @param oldLayoutHeight the old layout height.
     */
    protected void layoutResized(int oldLayoutWidth, int oldLayoutHeight) {
	int layoutWidth  = layout.getSize().width;
	int layoutHeight = layout.getSize().height;

	if (offset.y != 0 && layoutHeight <= getSize().height) {
	    scrollY(0);
	}
	if (offset.x != 0 && layoutWidth <= getSize().width &&
	    lineWrap == RichTextStyle.NO_WRAP)
	{
	    scrollX(0);
	}

	if (layoutHeight != oldLayoutHeight && layoutWidth != oldLayoutWidth) {
	    notifyScrollListeners(
		new ScrollEvent(this, ScrollEvent.SCROLL_SIZE_CHANGED,
				ScrollEvent.BOTH));
	}
	else if (layoutHeight != oldLayoutHeight) {
	    notifyScrollListeners(
		new ScrollEvent(this, ScrollEvent.SCROLL_SIZE_CHANGED,
				ScrollEvent.VERTICAL));
	}
	else if (layoutWidth != oldLayoutWidth) {
	    notifyScrollListeners(
		new ScrollEvent(this, ScrollEvent.SCROLL_SIZE_CHANGED,
				ScrollEvent.HORIZONTAL));
	}
    }

    /**
     * Returns the beginning text position of the view port.
     */
    protected TextPositionInfo getVisibleBegin() {
	if (_visibleBegin == null) {
	    /*
	    _visibleBegin = layout.getTextPositionAt(
				new Point(-offset.x, -offset.y));
	    */
	    _visibleBegin = layout.getLineBeginPositionOver(-offset.y);
	}
	return _visibleBegin;
    }

    /**
     * Returns the ending text position of the view port.
     */
    protected TextPositionInfo getVisibleEnd() {
	if (_visibleEnd == null) {
	    Dimension d = getSize();
	    /*
	    _visibleEnd = layout.getTextPositionNearby(
			getVisibleBegin(),
			new Point(-offset.x+d.width-1, -offset.y+d.height-1));
	    */
	    _visibleEnd = layout.getLineBeginPositionUnder(
						getVisibleBegin(),
						-offset.y + d.height - 1);
	}
	return _visibleEnd;
    }

    /**
     * Tests if the selection is needed to be redrawn.
     */
    protected boolean needsToRedrawSelection() {
	return false;
    }

    /**
     * Returns the text position at the specified text index.
     */
    protected TextPositionInfo getTextPositionAt(int textIndex) {
	layout.validate();
	return layout.getTextPositionAt(textIndex);
    }

    /**
     * Returns the text position at the specified point.
     */
    protected TextPositionInfo getTextPositionAt(Point point) {
	layout.validate();
	return layout.getTextPositionAt(
			new Point(point.x - offset.x, point.y - offset.y));
    }

    /**
     * Returns the text position at the specified text index nearby the
     * specified text position.
     */
    protected TextPositionInfo getTextPositionNearby(TextPositionInfo posInfo,
						     int textIndex)
    {
	return layout.getTextPositionNearby(posInfo, textIndex);
    }

    /**
     * Returns the text position at the specified point nearby the specified
     * text position.
     */
    protected TextPositionInfo getTextPositionNearby(TextPositionInfo posInfo,
						     Point point)
    {
	return layout.getTextPositionNearby(posInfo,
			new Point(point.x - offset.x, point.y - offset.y));
    }

    /**
     * Returns the text position next to the specified text position.
     */
    protected TextPositionInfo getTextPositionNextTo(TextPositionInfo posInfo)
    {
	return layout.getTextPositionNextTo(posInfo);
    }

    /**
     * Returns the text position previous to the specified text position.
     */
    protected TextPositionInfo getTextPositionPrevTo(TextPositionInfo posInfo)
    {
	return layout.getTextPositionPrevTo(posInfo);
    }

    /**
     * Returns the text line at the specified line index.
     */
    protected TextLineInfo getTextLineAt(int lineIndex) {
	layout.validate();
	return layout.getTextLineAt(lineIndex);
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
