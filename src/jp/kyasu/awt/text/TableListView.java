/*
 * TableListView.java
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

package jp.kyasu.awt.text;

import jp.kyasu.awt.Button;
import jp.kyasu.awt.Panel;
import jp.kyasu.awt.Scrollable;
import jp.kyasu.awt.Scrollbar;
import jp.kyasu.awt.SplitPanel;
import jp.kyasu.awt.TextListModel;
import jp.kyasu.awt.event.ListActionEvent;
import jp.kyasu.awt.event.ScrollEvent;
import jp.kyasu.awt.event.ScrollListener;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>TableListView</code> class implements a composite view of the
 * <code>TextListView</code>s.
 *
 * @see		jp.kyasu.awt.TextListModel
 * @see		jp.kyasu.awt.text.TextListView
 * @see		jp.kyasu.awt.text.TextListController
 *
 * @version 	12 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class TableListView extends Panel
	implements ActionListener, Scrollable, ScrollListener
{
    protected SplitPanel panel;
    protected TextListModel model;
    protected TextListView view;
    transient protected ActionListener actionListener;
    transient protected Vector scrollListeners;


    /** Do not auto resize column when table is resized. */
    static public final int AUTO_RESIZE_OFF =
				SplitPanel.AUTO_RESIZE_OFF;

    /** Auto resize last column only when table is resized */
    static public final int AUTO_RESIZE_LAST_COLUMN =
				SplitPanel.AUTO_RESIZE_LAST_COMPONENT;

    /** Proportionately resize all columns when table is resized */
    static public final int AUTO_RESIZE_ALL_COLUMNS =
				SplitPanel.AUTO_RESIZE_ALL_COMPONENTS;


    /**
     * The split panel for the table view.
     */
    class TableSplitPanel extends SplitPanel {

	TableSplitPanel(int orientation) {
	    super(orientation);
	}

	public Graphics getGraphics() {
	    //Graphics g = TableListView.this.getGraphics();
	    Graphics g = getParent().getGraphics();
	    if (g != null) {
		Point p = getLocation();
		g.translate(p.x, p.y);
	    }
	    return g;
	}

	public Graphics getGraphicsForSplit() {
	    return getGraphics();
	}

	public Dimension getSizeForSplit() {
	    //return TableListView.this.getSize();
	    return getParent().getSize();
	}

	public void splitValueChanged(int newSizes[]) {
	    model.setColumnWidths(newSizes);
	}

	public void layoutChanged() {
	    super.layoutChanged();
	    //TableListView.this.invalidate();
	    //TableListView.this.validate();
	    getParent().invalidate();
	    getParent().validate();
	}
    }


    /**
     * Constructs a table list view with the specified model.
     *
     * @param model the text list movel.
     */
    public TableListView(TextListModel model, Button buttons[]) {
	super(null, new Insets(0, 0, 0, 0));
	if (model == null || buttons == null)
	    throw new NullPointerException();
	int columns = model.getColumnCount();
	if (columns < 1) {
	    throw new IllegalArgumentException(
				"table model does not have multiple columns");
	}
	if (columns != buttons.length) {
	    throw new IllegalArgumentException(
				"the number of column titles is invalid");
	}

	this.model = model;
	view = new TextListView(model);
	view.addScrollListener(this);
	scrollListeners = null;

	int colWidths[] = model.getColumnWidths();

	panel = new TableSplitPanel(SplitPanel.HORIZONTAL);
	panel.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
	for (int i = 0; i < columns; i++) {
	    Button b = buttons[i];
	    b.addActionListener(this);
	    b.getController().setFocusEmphasizeEnabled(false);
	    panel.add(b, new Integer(colWidths[i]));
	}

	super.addImpl(panel, null, -1);
	super.addImpl(view, null, -1);
    }


    /**
     * Returns the auto resize mode of the panel. The default is
     * AUTO_RESIZE_ALL_COLUMNS.
     *
     * @return the auto resize mode of the table.
     */
    public int getAutoResizeMode() {
	return panel.getAutoResizeMode();
    }

    /**
     * Sets the the auto resize mode of the panel.
     *
     * @param mode the auto resize mode.
     * @see #AUTO_RESIZE_OFF
     * @see #AUTO_RESIZE_LAST_COLUMN
     * @see #AUTO_RESIZE_ALL_COLUMNS
     */
    public void setAutoResizeMode(int mode) {
	switch (mode) {
	case AUTO_RESIZE_OFF:
	case AUTO_RESIZE_LAST_COLUMN:
	case AUTO_RESIZE_ALL_COLUMNS:
	    break;
	default:
	    throw new IllegalArgumentException("improper auto resize mode");
	}
	panel.setAutoResizeMode(mode);
	invalidate();
    }

    /**
     * Adds the specified action listener to receive action events from
     * this controller.
     *
     * @param l the action listener.
     */
    public void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
	enableEvents(0); // mark newEventsOnly
    }

    /**
     * Removes the specified action listener so it no longer receives action
     * events from this controller.
     *
     * @param l the action listener.
     */
    public void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    /** Notifies the specified action event to the action listeners. */
    protected void notifyActionListeners(ActionEvent event) {
	if (actionListener != null) {
	    actionListener.actionPerformed(event);
	}
    }

    /**
     * Invoked when an action occurs.
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(ActionEvent e) {
	int column = getColumnFromButton((Component)e.getSource());
	if (column < 0) {
	    return;
	}
	notifyActionListeners(new ListActionEvent(this, e.getActionCommand(),
						  null, -1, column, true));
    }

    protected int getColumnFromButton(Component button) {
	Component comps[] = panel.getComponents();
	int column = 0;
	for (int i = 0; i < comps.length; i++) {
	    Component comp = comps[i];
	    if (comp instanceof Button) {
		if (comp == button) {
		    return column;
		}
		column++;
	    }
	}
	return -1;
    }


    /**
     * Returns the model of this table list view.
     */
    public TextListModel getModel() {
	return model;
    }

    /**
     * Returns the view of this table list view.
     */
    public TextListView getView() {
	return view;
    }

    /**
     * Returns the column buttons of this table list view.
     */
    public synchronized Button[] getColumnButtons() {
	Button buttons[] = new Button[model.getColumnCount()];
	Component comps[] = panel.getComponents();
	int bindex = 0;
	for (int i = 0; i < comps.length; i++) {
	    Component comp = comps[i];
	    if (comp instanceof Button) {
		buttons[bindex++] = (Button)comp;
	    }
	}
	return buttons;
    }

    /**
     * Enables or disables this table list view.
     */
    public synchronized void setEnabled(boolean b) {
	view.setEnabled(b);
	panel.setEnabled(b);
	Button buttons[] = getColumnButtons();
	for (int i = 0; i < buttons.length; i++) {
	    buttons[i].setEnabled(b);
	}
	super.setEnabled(b);
    }

    /**
     * Returns the total size of the column widths.
     */
    public synchronized int getColumnTotalSize() {
	int colWidths[] = model.getColumnWidths();
	int w = 0;
	for (int i = 0; i < colWidths.length; i++) {
	    w += colWidths[i];
	}
	return w;
    }

    /**
     * Returns the preferred height for the buttons.
     */
    public int getPreferredButtonsHeight() {
	return Scrollbar.SCROLLBAR_THICKNESS;
    }

    /**
     * Paints this view.
     */
    public void paint(Graphics g) {
	Dimension d = getSize();
	Dimension dp = panel.getSize();
	if (dp.width < d.width) {
	    g.setColor(getBackground());
	    g.fillRect(dp.width + view.offset.x,
		       0,
		       d.width - (dp.width + view.offset.x),
		       dp.height);
	    g.setColor(getForeground());
	}
	super.paint(g);
    }


    /**
     * Sets the layout manager for this view.
     */
    public void setLayout(LayoutManager mgr) {
	// do nothing
    }

    /**
     * Adds the specified component to this view.
     */
    protected void addImpl(Component comp, Object constraints, int index) {
	// do nothing
    }

    /**
     * Returns the preferred size of this view with the specified number of
     * rows.
     * @param rows number of rows in the table list.
     */
    public Dimension getPreferredSize(int rows) {
	synchronized (getTreeLock()) {
	    if (rows <= 0) rows = 1;
	    int width = getColumnTotalSize();
	    int height = view.getPreferredHeight(rows);
	    Insets insets = getInsets();
	    return new Dimension(width + (insets.left + insets.right),
				 height + getPreferredButtonsHeight() +
					(insets.top + insets.bottom));
	}
    }

    /**
     * Returns the preferred size of this view.
     */
    public Dimension getPreferredSize() {
	return getPreferredSize(model.getItemCount());
    }

    /**
     * Returns the minimum size of this view with the specified number of
     * rows.
     * @param rows number of rows in the table list.
     */
    public Dimension getMinimumSize(int rows) {
	synchronized (getTreeLock()) {
	    if (rows <= 0) rows = 1;
	    int width = model.getColumnCount() * SplitPanel.MIN_COMPONENT_SIZE;
	    int height = view.getPreferredHeight(rows);
	    Insets insets = getInsets();
	    return new Dimension(width + (insets.left + insets.right),
				 height + getPreferredButtonsHeight() +
					(insets.top + insets.bottom));
	}
    }

    /**
     * Returns the minimum size of this view.
     */
    public Dimension getMinimumSize() {
	return getMinimumSize(1);
    }

    /**
     * Lays out this view.
     */
    public void doLayout() {
	Dimension d = getSize();
	Insets insets = getInsets();
	int width = getColumnTotalSize();
	int bHeight = getPreferredButtonsHeight();

	Dimension viewSize = view.getSize();
	int viewWidth = d.width - (insets.left + insets.right);
	int viewHeight = d.height - (insets.top + insets.bottom) - bHeight;

	if (getAutoResizeMode() == AUTO_RESIZE_OFF) {
	    panel.setBounds(insets.left + view.offset.x,
			    insets.top,
			    width,
			    bHeight);
	}
	else {
	    panel.setBounds(insets.left,
			    insets.top,
			    d.width - (insets.left + insets.right),
			    bHeight);
	}
	if (viewWidth != viewSize.width || viewHeight != viewSize.height) {
	    view.setBounds(insets.left,
			   insets.top + bHeight,
			   d.width - (insets.left + insets.right),
			   d.height - (insets.top + insets.bottom) - bHeight);
	}
	notifyScrollListeners(new ScrollEvent(
		this, ScrollEvent.SCROLL_SIZE_CHANGED, ScrollEvent.BOTH));
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
	return view.getVMaximum() + panel.getSize().height;
    }

    /**
     * Gets the horizontal maximum value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHMaximum() {
	return getColumnTotalSize();
	//return panel.getSize().width;
    }

    /**
     * Gets the vertical unit value increment for the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVUnitIncrement() {
	return view.getVUnitIncrement();
    }

    /**
     * Gets the horizontal unit value increment for the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHUnitIncrement() {
	return view.getHUnitIncrement();
    }

    /**
     * Gets the vertical block value increment for the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVBlockIncrement() {
	return view.getVBlockIncrement();
    }

    /**
     * Gets the horizontal block value increment for the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHBlockIncrement() {
	return view.getHBlockIncrement();
    }

    /**
     * Gets the vertical length of the propertional indicator.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVVisibleAmount() {
	return view.getVVisibleAmount() + panel.getSize().height;
    }

    /**
     * Gets the horizontal length of the propertional indicator.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHVisibleAmount() {
	return view.getHVisibleAmount();
    }

    /**
     * Gets the vertical current value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getVValue() {
	return view.getVValue();
    }

    /**
     * Gets the horizontal current value of the scrollable object.
     * @see jp.kyasu.awt.Scrollable
     */
    public int getHValue() {
	return view.getHValue();
    }

    /**
     * Sets the vertical current value of the scrollable object.
     * @param v the current value.
     * @see jp.kyasu.awt.Scrollable
     */
    public void setVValue(int v) {
	view.setVValue(v);
    }

    /**
     * Sets the horizontal current value of the scrollable object.
     * @param v the current value.
     * @see jp.kyasu.awt.Scrollable
     */
    public void setHValue(int v) {
	view.setHValue(v);
	invalidate();
	validate();
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
     * Invoked when the value of the scrollable has changed.
     * @see jp.kyasu.awt.event.ScrollListener
     */
    public void scrollValueChanged(ScrollEvent e) {
	Scrollable src = e.getScrollable();
	int orient = e.getOrientation();

	switch(e.getID()) {
	case ScrollEvent.SCROLL_VALUE_CHANGED:
	    if (orient == ScrollEvent.HORIZONTAL ||
		orient == ScrollEvent.BOTH)
	    {
		invalidate();
		validate();
		//setHValue(src.getHValue());
	    }
	    /*
	    if (orient == ScrollEvent.VERTICAL ||
		orient == ScrollEvent.BOTH)
	    {
		setVValue(src.getVValue());
	    }
	    */
	    break;
	case ScrollEvent.SCROLL_SIZE_CHANGED:
	    break;
	}

	notifyScrollListeners(new ScrollEvent(this, e.getID(), orient));
    }


    /** Internal constant for serialization */
    static protected final String actionListenerK = "actionL".intern();

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();

	jp.kyasu.awt.ListenerSerializer.write(s,
					      actionListenerK,
					      actionListener);
	s.writeObject(null);

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

	Object keyOrNull;
	while ((keyOrNull = s.readObject()) != null) {
	    String key = ((String)keyOrNull).intern();
	    if (key == actionListenerK)
		addActionListener((ActionListener)s.readObject());
	    else // skip value for unrecognized key
		s.readObject();
	}

	Object listenerOrNull;
	while ((listenerOrNull = s.readObject()) != null) {
	    addScrollListener((ScrollListener)listenerOrNull);
	}
    }
}
