/*
 * List.java
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

import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextLayout;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.V3DBorder;
import jp.kyasu.graphics.VBorder;
import jp.kyasu.awt.text.TextListController;
import jp.kyasu.awt.text.TextListView;
import jp.kyasu.awt.event.ListActionEvent;
import jp.kyasu.awt.event.ListItemEvent;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The <code>List</code> component presents the user with a scrolling list
 * of text items. The list can be set up so that the user can choose either
 * one item or multiple items.
 * <p>
 * A List is an MVC-based component. The <i>model</i> of the List is a
 * <code>TextListModel</code> object, the <i>view</i> of the List is a
 * <code>TextListView</code> object, and the <i>controller</i> of the List
 * is a <code>TextListController</code> object.
 * <p>
 * A List emits a <code>ListActionEvent</code> and a <code>ListItemEvent</code>.
 *
 * @see		jp.kyasu.awt.ListModel
 * @see		jp.kyasu.awt.TextListModel
 * @see		jp.kyasu.awt.text.TextListView
 * @see		jp.kyasu.awt.text.TextListController
 * @see		jp.kyasu.awt.event.ListActionEvent
 * @see		jp.kyasu.awt.event.ListItemEvent
 *
 * @version 	12 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class List extends EventProxyContainer
	implements ItemSelectable, ActionListener, ItemListener
{
    protected TextListModel listModel;
    protected TextListView listView;
    protected TextListController listController;
    protected int rows;
    transient protected ItemListener itemListener;
    transient protected ActionListener actionListener;


    /**
     * Allows single selection.
     */
    static public final int SINGLE_SELECTION =
				TextListController.SINGLE_SELECTION;

    /**
     * Allows AWT style multiple selections.
     */
    static public final int MULTIPLE_SELECTIONS =
				TextListController.MULTIPLE_SELECTIONS;

    /**
     * Allows Windows style multiple selections.
     */
    static public final int SHIFT_MULTIPLE_SELECTIONS =
				TextListController.SHIFT_MULTIPLE_SELECTIONS;

    /**
     * The default number of visible rows is 4. A list with zero rows
     * is unusable and unsightly.
     */
    static protected final int DEFAULT_VISIBLE_ROWS = 4;



    /**
     * Creates a new scrolling list. Initially there are no visible
     * lines, and only one item can be selected from the list.
     */
    public List() {
	this(0, false);
    }

    /**
     * Creates a new scrolling list initialized with the specified
     * number of visible lines. By default, multiple selections are
     * not allowed.
     * @param rows the number of items to show.
     */
    public List(int rows) {
    	this(rows, false);
    }

    /**
     * Creates a new scrolling list initialized to display the specified
     * number of rows. If the value of <code>multipleMode</code> is
     * <code>true</code>, then the user can select multiple items from
     * the list. If it is <code>false</code>, only one item at a time
     * can be selected.
     * @param rows         the number of items to show.
     * @param multipleMode if <code>true</code>, then multiple selections
     *                     are allowed; otherwise, only one item can be
     *                     selected at a time.
     */
    public List(int rows, boolean multipleMode) {
	this(rows, multipleMode, ScrollPanel.SCROLLBARS_BOTH);
    }

    /**
     * Creates a new scrolling list initialized to display the specified
     * number of rows, multipleMode, and scroll bar visibility..
     * @param rows         the number of items to show.
     * @param multipleMode if <code>true</code>, then multiple selections
     *                     are allowed; otherwise, only one item can be
     *                     selected at a time.
     * @param scrollbars   a constant that determines what scrollbars are
     *                     created to view the list.
     */
    public List(int rows, boolean multipleMode, int scrollbars) {
	this(RichTextStyle.DEFAULT_LIST_STYLE,
	     rows, multipleMode, scrollbars);
    }

    /**
     * Creates a new scrolling list with the specified style and number of rows.
     * @param richTextStyle the style of the text list model.
     * @param rows          the number of items to show.
     */
    public List(RichTextStyle richTextStyle, int rows) {
	this(richTextStyle, rows, false);
    }

    /**
     * Creates a new scrolling list with the specified style, number of rows,
     * and multipleMode.
     * @param richTextStyle the style of the text list model.
     * @param rows          the number of items to show.
     * @param multipleMode  if <code>true</code>, then multiple selections
     *                      are allowed; otherwise, only one item can be
     */
    public List(RichTextStyle richTextStyle, int rows, boolean multipleMode) {
	this(richTextStyle, rows, multipleMode, ScrollPanel.SCROLLBARS_BOTH);
    }

    /**
     * Creates a new scrolling list with the specified style, number of rows,
     * multipleMode, and scroll bar visibility.
     * @param richTextStyle the style of the text list model.
     * @param rows          the number of items to show.
     * @param multipleMode  if <code>true</code>, then multiple selections
     *                      are allowed; otherwise, only one item can be
     * @param scrollbars    a constant that determines what scrollbars are
     *                      created to view the list.
     */
    public List(RichTextStyle richTextStyle, int rows, boolean multipleMode,
		int scrollbars)
    {
	this(new DefaultTextListModel(richTextStyle), rows, multipleMode,
	     scrollbars, new V3DBorder(false));
    }

    /**
     * Creates a new scrolling list with the specified model and number of rows.
     * @param textListModel the text list model.
     * @param rows          the number of items to show.
     */
    public List(TextListModel textListModel, int rows) {
	this(textListModel, rows, false);
    }

    /**
     * Creates a new scrolling list with the specified model, number of rows,
     * and multipleMode.
     * @param textListModel the text list model.
     * @param rows          the number of items to show.
     * @param multipleMode  if <code>true</code>, then multiple selections
     *                      are allowed; otherwise, only one item can be
     */
    public List(TextListModel textListModel, int rows, boolean multipleMode) {
	this(textListModel, rows, multipleMode, ScrollPanel.SCROLLBARS_BOTH);
    }

    /**
     * Creates a new scrolling list with the specified model, number of rows,
     * multipleMode, and scroll bar visibility.
     * @param textListModel the text list model.
     * @param rows          the number of items to show.
     * @param multipleMode  if <code>true</code>, then multiple selections
     *                      are allowed; otherwise, only one item can be
     * @param scrollbars    a constant that determines what scrollbars are
     *                      created to view the list.
     */
    public List(TextListModel textListModel, int rows, boolean multipleMode,
		int scrollbars)
    {
	this(textListModel, rows, multipleMode, scrollbars,
	     new V3DBorder(false));
    }

    /**
     * Creates a new scrolling list with the specified model, number of rows,
     * multipleMode, scroll bar visibility, and border visual.
     * @param textListModel the text list model.
     * @param rows          the number of items to show.
     * @param multipleMode  if <code>true</code>, then multiple selections
     *                      are allowed; otherwise, only one item can be
     * @param scrollbars    a constant that determines what scrollbars are
     *                      created to view the list.
     * @param border        the border visual of the list.
     */
    public List(TextListModel textListModel, int rows, boolean multipleMode,
		int scrollbars, VBorder border)
    {
	if (textListModel == null || border == null)
	    throw new NullPointerException();
	if (textListModel.getColumnCount() != 1)
	    throw new IllegalArgumentException("invalid number of columns");
	ScrollPanel sp = new ScrollPanel(scrollbars,
					 ScrollPanel.SCROLLBARS_AS_NEEDED);

	listModel = textListModel;
	listView = new TextListView(listModel);
	listController = listView.getController();
	listController.setMultipleMode(multipleMode);

	listController.addItemListener(this);
	listController.addActionListener(this);

	sp.add(listView);

	BorderedPanel bp = new BorderedPanel(border);
	bp.add(sp, BorderLayout.CENTER);

	setLayout(new BorderLayout());
	add(bp, BorderLayout.CENTER);

	this.rows = (rows > 0 ? rows : DEFAULT_VISIBLE_ROWS);

	itemListener   = null;
	actionListener = null;

	//super.setForeground(listView.getForeground());
	//super.setBackground(listView.getBackground());
	super.setFont(
	   listModel.getTextList().getRichTextStyle().getTextStyle().getFont());
	super.setCursor(listView.getCursor());
    }

    /**
     * Creates a new scrolling list with the specified model.
     * This constructor is used by the subclasses only.
     */
    protected List(TextListModel textListModel) {
	if (textListModel == null)
	    throw new NullPointerException();
	if (textListModel.getColumnCount() != 1)
	    throw new IllegalArgumentException("invalid number of columns");
	listModel = textListModel;

	itemListener   = null;
	actionListener = null;
    }


    /**
     * Gets the event source component.
     */
    protected Component getEventSource() {
	return listView;
    }

    /**
     * Adds the specified item listener to receive item events from this list.
     * @param l the item listener.
     */
    public synchronized void addItemListener(ItemListener l) {
	/*
	if (itemListener == null && l != null) {
	    listController.addItemListener(this);
	}
	*/
	itemListener = AWTEventMulticaster.add(itemListener, l);
	enableEvents(0); // mark newEventsOnly
    }

    /**
     * Removes the specified item listener so that it no longer receives
     * item events from this list.
     * @param l the item listener.
     */
    public synchronized void removeItemListener(ItemListener l) {
	itemListener = AWTEventMulticaster.remove(itemListener, l);
	/*
	if (itemListener == null) {
	    listController.removeItemListener(this);
	}
	*/
    }

    /**
     * Adds the specified action listener to receive action events from
     * this list. Action events occur when a user double-clicks
     * on a list item.
     * @param l the action listener.
     */
    public synchronized void addActionListener(ActionListener l) {
	/*
	if (actionListener == null && l != null) {
	    listController.addActionListener(this);
	}
	*/
	actionListener = AWTEventMulticaster.add(actionListener, l);
	enableEvents(0); // mark newEventsOnly
    }

    /**
     * Removes the specified action listener so that it no longer receives
     * action events from this list. Action events occur when a user
     * double-clicks on a list item.
     * @param l the action listener.
     */
    public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
	/*
	if (actionListener == null) {
	    listController.removeActionListener(this);
	}
	*/
    }

    /**
     * Invoked when an item's state has been changed.
     * @see java.awt.event.ItemListener
     */
    public void itemStateChanged(ItemEvent e) {
	if (itemListener != null && (e instanceof ListItemEvent)) {
	    ListItemEvent le = (ListItemEvent)e;
	    if (isDirectNotification()) {
		e = new ListItemEvent(this,
				      le.getID(),
				      le.getItem(),
				      le.getStateChange(),
				      le.getItems(),
				      le.getColumn());
		itemListener.itemStateChanged(e);
	    }
	    else {
		e = new ListItemEvent(
				this,
				le.getID() + java.awt.AWTEvent.RESERVED_ID_MAX,
				le.getItem(),
				le.getStateChange(),
				le.getItems(),
				le.getColumn());
		EventPoster.postEvent(e);
	    }
	}
	else {
	    e = new ItemEvent(this, e.getID(), e.getItem(), e.getStateChange());
	    postOldEvent(e);
	}
    }

    /**
     * Invoked when an action occurs.
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(ActionEvent e) {
	if (actionListener != null && (e instanceof ListActionEvent)) {
	    ListActionEvent le = (ListActionEvent)e;
	    if (isDirectNotification()) {
		e = new ListActionEvent(this,
					le.getID(),
					le.getActionCommand(),
					le.getItem(),
					le.getRow(),
					le.getColumn(),
					le.isButtonPressed());
		actionListener.actionPerformed(e);
	    }
	    else {
		e = new ListActionEvent(
				this,
				le.getID() + java.awt.AWTEvent.RESERVED_ID_MAX,
				le.getActionCommand(),
				le.getItem(),
				le.getRow(),
				le.getColumn(),
				le.isButtonPressed());
		EventPoster.postEvent(e);
	    }
	}
	else {
	    e = new ActionEvent(this,
				e.getID(),
				e.getActionCommand(),
				e.getModifiers());
	    postOldEvent(e);
	}
    }

    protected void processEvent(java.awt.AWTEvent e) {
	if (itemListener != null && (e instanceof ListItemEvent)) {
	    ListItemEvent ie = (ListItemEvent)e;
	    if (ie.getID() > java.awt.AWTEvent.RESERVED_ID_MAX) {
		ie = new ListItemEvent(
				ie.getItemSelectable(),
				ie.getID() - java.awt.AWTEvent.RESERVED_ID_MAX,
				ie.getItem(),
				ie.getStateChange(),
				ie.getItems(),
				ie.getColumn());
		itemListener.itemStateChanged(ie);
		return;
	    }
	}
	if (actionListener != null && (e instanceof ListActionEvent)) {
	    ListActionEvent ae = (ListActionEvent)e;
	    if (ae.getID() > java.awt.AWTEvent.RESERVED_ID_MAX) {
		ae = new ListActionEvent(
				ae.getSource(),
				ae.getID() - java.awt.AWTEvent.RESERVED_ID_MAX,
				ae.getActionCommand(),
				ae.getItem(),
				ae.getRow(),
				ae.getColumn(),
				ae.isButtonPressed());
		actionListener.actionPerformed(ae);
		return;
	    }
	}
	super.processEvent(e);
    }

    /**
     * Sets the foreground color of this list.
     */
    public synchronized void setForeground(Color c) {
	super.setForeground(c);
	if (listView != null) {
	    listView.setForeground(c);
	    if (isShowing()) {
		listView.repaintNow();
	    }
	}
    }

    /**
     * Sets the background color of this list.
     */
    public synchronized void setBackground(Color c) {
	super.setBackground(c);
	if (listView != null) {
	    listView.setBackground(c);
	    if (isShowing()) {
		listView.repaintNow();
	    }
	}
    }

    /**
     * Sets the font of this list.
     */
    public synchronized void setFont(Font f) {
	super.setFont(f);
	if (listView != null) {
	    listView.setFont(f);
	}
    }

    /**
     * Sets the cursor of this list.
     */
    public synchronized void setCursor(Cursor c) {
	super.setCursor(c);
	if (listView != null) {
	    listView.setCursor(c);
	}
    }

    /**
     * Enables or disables this list.
     */
    public synchronized void setEnabled(boolean b) {
	super.setEnabled(b);
	if (listView != null) {
	    listView.setEnabled(b);
	}
    }

    // ======== java.awt.List APIs ========

    /**
     * Returns the number of items in the list.
     * @return the number of items in the list.
     * @see #getItem()
     */
    public int getItemCount() {
	return listController.getItemCount();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getItemCount()</code>.
     */
    public int countItems() {
	return getItemCount();
    }

    /**
     * Returns the item associated with the specified index.
     * @param index the position of the item.
     * @return an item that is associated with the specified index.
     * @see #getItemCount()
     */
    public String getItem(int index) {
	return listController.getItem(index, 0);
    }

    /**
     * Returns the items in the list.
     * @return a string array containing items of the list.
     */
    public String[] getItems() {
	return listController.getItems(0);
    }

    /**
     * Adds the specified item to the end of scrolling list.
     * @param item the item to be added.
     */
    public void add(String item) {
	addItem(item);
    }

    /**
     * Adds the specified item to the end of scrolling list.
     */
    public void addItem(String item) {
	addItem(item, -1);
    }

    /**
     * Adds the specified item to the the scrolling list.
     * The index is zero-based. If value of the index is <code>-1</code>
     * then the item is added to the end. If value of the index is greater
     * than the number of items in the list, the item is added at the end.
     * @param item  the item to be added.
     * @param index the position at which to add the item.
     */
    public void add(String item, int index) {
	addItem(item, index);
    }

    /**
     * Adds the specified item to the the scrolling list.
     * The index is zero-based. If value of the index is <code>-1</code>
     * then the item is added to the end. If value of the index is greater
     * than the number of items in the list, the item is added at the end.
     * @param item  the item to be added.
     * @param index the position at which to add the item.
     */
    public synchronized void addItem(String item, int index) {
	if (item == null) item = "";
	listController.addItem(new String[]{ item }, index);
    }

    /**
     * Replaces the item at the specified index in the scrolling list
     * with the new string.
     * @param newValue a new string to replace an existing item.
     * @param index    the position of the item to replace.
     */
    public synchronized void replaceItem(String newValue, int index) {
	if (newValue == null) newValue = "";
	listController.replaceItem(new String[]{ newValue }, index);
    }

    /**
     * Removes all items from this list.
     * @see #remove(int)
     * @see #delItems()
     */
    public synchronized void removeAll() {
	listController.removeAll();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>removeAll()</code>.
     */
    public void clear() {
	removeAll();
    }

    /**
     * Removes the first occurrence of an item from the list.
     * @param item the item to remove from the list.
     */
    public synchronized void remove(String item) {
	int itemCount = listController.getItemCount();
	for (int i = 0; i < itemCount; i++) {
	    String str = listController.getItem(i, 0);
	    if (str.equals(item)) {
		listController.remove(i);
		return;
	    }
	}
	throw new IllegalArgumentException("item " + item +
						" not found in list");
    }

    /**
     * Remove the item at the specified position from this scrolling list.
     * @param position the index of the item to delete.
     * @see #add(java.awt.String, int)
     */
    public synchronized void remove(int position) {
	listController.remove(position);
    }

    /**
     * Removes the item at the specified position from this list.
     */
    public void delItem(int position) {
	remove(position);
    }

    /**
     * @deprecated As of JDK version 1.1,
     */
    public synchronized void delItems(int start, int end) {
	listController.remove(start, end + 1);
    }

    /**
     * Gets the index of the selected item on the list,
     * @return the index of the selected item, or <code>-1</code> if no
     *         item is selected, or if more that one item is selected.
     */
    public synchronized int getSelectedIndex() {
	return listController.getSelectedIndex();
    }

    /**
     * Gets the selected indexes on the list.
     * @return an array of the selected indexes of this scrolling list.
     */
    public synchronized int[] getSelectedIndexes() {
	return listController.getSelectedIndexes();
    }

    /**
     * Get the selected item on this scrolling list.
     * @return the selected item on the list, or null if no item is selected.
     */
    public synchronized String getSelectedItem() {
	int index = listController.getSelectedIndex();
	return (index < 0 ? null : listController.getItem(index, 0));
    }

    /**
     * Get the selected items on this scrolling list.
     * @return an array of the selected items on this scrolling list.
     */
    public synchronized String[] getSelectedItems() {
	int sel[] = listController.getSelectedIndexes();
	String str[] = new String[sel.length];
	for (int i = 0; i < sel.length; i++) {
	    str[i] = listController.getItem(sel[i], 0);
	}
	return str;
    }

    /**
     * Returns the selected items on the list in an array of Objects.
     * @see java.awt.ItemSelectable
     */
    public Object[] getSelectedObjects() {
	return getSelectedItems();
    }

    /**
     * Selects the item at the specified index in the scrolling list.
     * @param index the position of the item to select.
     */
    public synchronized void select(int index) {
	listController.select(index);
    }

    /**
     * Deselects the item at the specified index.
     * <p>
     * If the item at the specified index is not selected, or if the
     * index is out of range, then the operation is ignored.
     * @param index the position of the item to deselect.
     */
    public synchronized void deselect(int index) {
	listController.deselect(index);
    }

    /**
     * Determines if the specified item in this scrolling list is selected.
     * @param index the item to be checked.
     * @return <code>true</code> if the specified item has been selected;
     *         <code>false</code> otherwise.
     */
    public boolean isIndexSelected(int index) {
	return listController.isIndexSelected(index);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>isIndexSelected(int)</code>.
     */
    public boolean isSelected(int index) {
	return isIndexSelected(index);
    }

    /**
     * Get the number of visible lines in this list.
     * @return the number of visible lines in this scrolling list.
     * @see #setRows(int)
     */
    public int getRows() {
	return rows;
    }

    /**
     * Sets the number of visible lines in this list.
     * @param rows the number of visible lines in this scrolling list.
     * @see #getRows()
     */
    public void setRows(int rows) {
	if (rows <= 0)
	    return;
	if (this.rows == rows)
	    return;
	this.rows = rows;
	invalidate();
    }

    /**
     * Determines whether this list allows multiple selections.
     * @return <code>true</code> if this list allows multiple selections;
     *         <code>false</code> otherwise.
     * @see #setMultipleMode(int)
     * @see #getSelectionMode()
     * @see #setSelectionMode(int)
     */
    public boolean isMultipleMode() {
	return listController.isMultipleMode();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>isMultipleMode()</code>.
     */
    public boolean allowsMultipleSelections() {
	return isMultipleMode();
    }

    /**
     * Sets the flag that determines whether this list allows multiple
     * selections.
     * @param b if <code>true</code> then multiple selections are allowed;
     *          otherwise, only one item from the list can be selected at once.
     * @see #isMultipleMode()
     * @see #getSelectionMode()
     * @see #setSelectionMode(int)
     */
    public synchronized void setMultipleMode(boolean b) {
	listController.setMultipleMode(b);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setMultipleMode(boolean)</code>.
     */
    public void setMultipleSelections(boolean b) {
	setMultipleMode(b);
    }

    /**
     * Gets the index of the item that was last made visible by the method
     * <code>makeVisible</code>.
     * @return the index of the item that was last made visible.
     * @see #makeVisible(int)
     */
    public int getVisibleIndex() {
	return listController.getVisibleIndex();
    }

    /**
     * Makes the item at the specified index visible.
     * @param index the position of the item.
     * @see #getVisibleIndex()
     */
    public synchronized void makeVisible(int index) {
	listController.makeVisible(index);
    }

    /**
     * Returns the preferred dimensions for a list with the specified number
     * of rows.
     * @param rows number of rows in the list.
     * @return the preferred dimensions for displaying this scrolling list.
     */
    public Dimension getPreferredSize(int rows) {
	synchronized (getTreeLock()) {
	    int width = listView.getPreferredSize().width;
	    int height = listView.getPreferredHeight(rows);
	    ScrollPanel sp = (ScrollPanel)listView.getParent(); // ScrollPanel
	    if (listView.getVMaximum() > height && sp.vScrollbar != null) {
		width += sp.vScrollbar.getPreferredSize().width;
	    }
	    Container c = sp.getParent(); // BorderedPanel
	    Insets insets = c.getInsets();
	    width  += (insets.left + insets.right);
	    height += (insets.top + insets.bottom);
	    return new Dimension(width, height);
	}
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize(int)</code>.
     */
    public Dimension preferredSize(int rows) {
	return getPreferredSize(rows);
    }

    /**
     * Returns the preferred size of this scrolling list.
     * @return the preferred dimensions for displaying this scrolling list.
     */
    public Dimension getPreferredSize() {
	synchronized (getTreeLock()) {
	    return ((rows > 0) ?
			getPreferredSize(rows) :
			super.getPreferredSize());
	}
    }

    /**
     * Returns the minumum dimensions for a list with the specified number
     * of rows.
     * @param rows number of rows in the list.
     * @return the minimum dimensions for displaying this scrolling list.
     */
    public Dimension getMinimumSize(int rows) {
	return getPreferredSize(rows);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize(int)</code>.
     */
    public Dimension minimumSize(int rows) {
	return getMinimumSize(rows);
    }

    /**
     * Returns the minimum size of this scrolling list.
     * @return the minimum dimensions needed to display this scrolling list.
     */
    public Dimension getMinimumSize() {
	synchronized (getTreeLock()) {
	    return ((rows > 0) ? getMinimumSize(rows) : super.getMinimumSize());
	}
    }

    // ======== java.awt.Choice APIs ========

    /**
     * Sets the selected item in this list to be the item whose name is
     * equal to the specified string. If more than one item matches
     * (is equal to) the specified string, the one with the smallest
     * index is selected.
     * @param item the specified string
     */
    public synchronized void select(String item) {
	int itemCount = listController.getItemCount();
	for (int i = 0; i < itemCount; i++) {
	    String str = listController.getItem(i, 0);
	    if (str.equals(item)) {
		listController.select(i);
		return;
	    }
	}
    }

    // ================ Enhanced APIs ================

    /**
     * Returns the model of this list.
     */
    public TextListModel getModel() {
	return listModel;
    }

    /**
     * Returns the view of this list.
     */
    public TextListView getView() {
	return listView;
    }

    /**
     * Returns the controller of this list.
     */
    public TextListController getController() {
	return listController;
    }

    /**
     * Returns the selection foreground color.
     * @see #setSelectionForeground(java.awt.Color)
     */
    public Color getSelectionForeground() {
	return listView.getSelectionForeground();
    }

    /**
     * Sets the selection foreground color.
     * @see #getSelectionForeground()
     */
    public synchronized void setSelectionForeground(Color c) {
	listView.setSelectionForeground(c);
    }

    /**
     * Returns the selection background color.
     * @see #setSelectionBackground(java.awt.Color)
     */
    public Color getSelectionBackground() {
	return listView.getSelectionBackground();
    }

    /**
     * Sets the selection background color.
     * @see #getSelectionBackground()
     */
    public synchronized void setSelectionBackground(Color c) {
	listView.setSelectionBackground(c);
    }

    /**
     * Returns the popup menu of this list.
     * @see #setPopupMenu(java.awt.PopupMenu)
     */
    public PopupMenu getPopupMenu() {
	return listController.getPopupMenu();
    }

    /**
     * Sets the popup menu of this list.
     * @see #getPopupMenu()
     */
    public synchronized void setPopupMenu(PopupMenu menu) {
	listController.setPopupMenu(menu);
    }

    /**
     * Tests if the list requests the focus when the mouse is clicked.
     *
     * @return <code>true</code> if the list requests the focus when the
     *         mouse is clicked, <code>false</code> if the list requests
     *         the focus when the mouse enters the view.
     * @see #isMouseFocus()
     */
    public boolean isClickToFocus() {
	return listController.isClickToFocus();
    }

    /**
     * Tests if the list requests the focus when the mouse enters the list.
     *
     * @see #isClickToFocus()
     */
    public boolean isMouseFocus() {
	return listController.isMouseFocus();
    }

    /**
     * Makes the list request the focus when the mouse is clicked.
     *
     * @see #isClickToFocus()
     * @see #setMouseFocus()
     */
    public void setClickToFocus() {
	listController.setClickToFocus();
    }

    /**
     * Makes the list request the focus when the mouse enters the list.
     *
     * @see #isMouseFocus()
     * @see #setClickToFocus()
     */
    public void setMouseFocus() {
	listController.setMouseFocus();
    }

    /**
     * Tests if the deselection is enabled when the selection mode is a single
     * selection.
     * @see #setDeselectionEnabled(boolean)
     */
    public boolean isDeselectionEnabled() {
	return listController.isDeselectionEnabled();
    }

    /**
     * Makes the deselection enabled when the selection mode is a single
     * selection.
     * @see #isDeselectionEnabled()
     */
    public synchronized void setDeselectionEnabled(boolean b) {
	listController.setDeselectionEnabled(b);
    }

    /**
     * Returns the selection mode.
     * @see #setSelectionMode(int)
     * @see #SINGLE_SELECTION
     * @see #MULTIPLE_SELECTIONS
     * @see #SHIFT_MULTIPLE_SELECTIONS
     */
    public int getSelectionMode() {
	return listController.getSelectionMode();
    }

    /**
     * Sets the selection mode.
     * @see #getSelectionMode()
     * @see #SINGLE_SELECTION
     * @see #MULTIPLE_SELECTIONS
     * @see #SHIFT_MULTIPLE_SELECTIONS
     */
    public synchronized void setSelectionMode(int mode) {
	listController.setSelectionMode(mode);
    }

    /**
     * Sets the items of the list to the specified string items.
     */
    public synchronized void setItems(String items[]) {
	String strs[][] = new String[items.length][1];
	for (int i = 0; i < items.length; i++) {
	    strs[i][0] = items[i];
	}
	listController.setItems(strs);
    }

    /**
     * Sets the items of the list to the specified text items.
     */
    public void setItems(Text items[]) {
	setTextItems(items);
    }

    /**
     * Sets the items of the list to the specified text items.
     */
    public synchronized void setTextItems(Text items[]) {
	Text texts[][] = new Text[items.length][1];
	for (int i = 0; i < items.length; i++) {
	    texts[i][0] = items[i];
	}
	listController.setTextItems(texts);
    }

    /**
     * Returns the text item associated with the specified index.
     * @param index the position of the item.
     * @return a text item that is associated with the specified index.
     */
    public Text getTextItem(int index) {
	return listController.getTextItem(index, 0);
    }

    /**
     * Returns the text items in the list.
     * @return a text array containing items of the list.
     */
    public Text[] getTextItems() {
	return listController.getTextItems(0);
    }

    /**
     * Adds the specified text item to the end of scrolling list.
     * @param item the text item to be added.
     */
    public void add(Text item) {
	addItem(item);
    }

    /**
     * Adds the specified text item to the end of scrolling list.
     */
    public void addItem(Text item) {
	addItem(item, -1);
    }

    /**
     * Adds the specified item to the scrolling list.
     * The index is zero-based. If value of the index is <code>-1</code>
     * then the item is added to the end. If value of the index is greater
     * than the number of items in the list, the item is added at the end.
     * @param item  the text item to be added.
     * @param index the position at which to add the item.
     */
    public void add(Text item, int index) {
	addItem(item, index);
    }

    /**
     * Adds the specified text item to the scrolling list.
     * The index is zero-based. If value of the index is <code>-1</code>
     * then the item is added to the end. If value of the index is greater
     * than the number of items in the list, the item is added at the end.
     * @param item  the text item to be added.
     * @param index the position at which to add the item.
     */
    public void addItem(Text item, int index) {
	addTextItem(item, index);
    }

    /**
     * Adds the specified text item to the end of the scrolling list.
     * @param item  the text item to be added.
     */
    public void addTextItem(Text item) {
	addTextItem(item, -1);
    }

    /**
     * Adds the specified text item to the end of the scrolling list.
     * The index is zero-based. If value of the index is <code>-1</code>
     * then the item is added to the end. If value of the index is greater
     * than the number of items in the list, the item is added at the end.
     * @param item  the text item to be added.
     * @param index the position at which to add the item.
     */
    public synchronized void addTextItem(Text item, int index) {
	listController.addTextItem(new Text[]{ item }, index);
    }

    /**
     * Replaces the item at the specified index in the scrolling list
     * with the new text.
     * @param newValue a new text to replace an existing item.
     * @param index    the position of the item to replace.
     */
    public void replaceItem(Text newValue, int index) {
	replaceTextItem(newValue, index);
    }

    /**
     * Replaces the item at the specified index in the scrolling list
     * with the new text.
     * @param newValue a new text to replace an existing item.
     * @param index    the position of the item to replace.
     */
    public synchronized void replaceTextItem(Text newValue, int index) {
	listController.replaceTextItem(new Text[]{ newValue }, index);
    }

    /**
     * Get the selected text item on this scrolling list.
     * @return the selected item on the list, or null if no item is selected.
     */
    public synchronized Text getSelectedTextItem() {
	int index = listController.getSelectedIndex();
	return (index < 0 ? null : listController.getTextItem(index, 0));
    }

    /**
     * Get the selected text items on this scrolling list.
     * @return an array of the selected items on this scrolling list.
     */
    public synchronized Text[] getSelectedTextItems() {
	int sel[] = listController.getSelectedIndexes();
	Text text[] = new Text[sel.length];
	for (int i = 0; i < sel.length; i++) {
	    text[i] = listController.getTextItem(sel[i], 0);
	}
	return text;
    }

    /**
     * Remove the items at the specified row range.
     * @param start the starting row position, inclusive.
     * @param end   the ending row position, inclusive.
     */
    public synchronized void remove(int start, int end) {
	listController.remove(start, end);
    }

    /**
     * Remove the items at the specified row indices.
     * @param indices the row indices to be removed.
     */
    public synchronized void remove(int indices[]) {
	listController.remove(indices);
    }

    /**
     * Remove the items at the indices of the selected rows.
     */
    public synchronized void removeSelectedIndexes() {
	listController.removeSelectedIndexes();
    }


    /**
     * Returns the thickness of the scroll bar.
     * @see #setScrollbarThickness(int)
     */
    public int getScrollbarThickness() {
	ScrollPanel sp = (ScrollPanel)listView.getParent();
	return sp.getScrollbarThickness();
    }

    /**
     * Sets the thickness of the scroll bar.
     * @see #setScrollbarThickness()
     */
    public synchronized void setScrollbarThickness(int thickness) {
	ScrollPanel sp = (ScrollPanel)listView.getParent();
	if (thickness == sp.getScrollbarThickness())
	    return;
	sp.setScrollbarThickness(thickness);
	invalidate();
    }


    /** Internal constant for serialization */
    static protected final String itemListenerK = "itemL".intern();

    /** Internal constant for serialization */
    static protected final String actionListenerK = "actionL".intern();

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	jp.kyasu.awt.ListenerSerializer.write(s,
					      itemListenerK,
					      itemListener);
	jp.kyasu.awt.ListenerSerializer.write(s,
					      actionListenerK,
					      actionListener);
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws ClassNotFoundException, java.io.IOException
    {
	s.defaultReadObject();

	//listController.removeItemListener(this);
	//listController.removeActionListener(this);

	Object keyOrNull;
	while ((keyOrNull = s.readObject()) != null) {
	    String key = ((String)keyOrNull).intern();
	    if (key == itemListenerK)
		addItemListener((ItemListener)s.readObject());
	    else if (key == actionListenerK)
		addActionListener((ActionListener)s.readObject());
	    else // skip value for unrecognized key
		s.readObject();
	}
    }
}
