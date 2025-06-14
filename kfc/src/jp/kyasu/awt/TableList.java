/*
 * TableList.java
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
import jp.kyasu.awt.text.TableListView;
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
 * The <code>TableList</code> component presents the user with a scrolling
 * table list that has multiple columns. The table list can be set up so that
 * the user can choose either one row or multiple rows.
 * <p>
 * A TableList is an MVC-based component. The <i>model</i> of the TableList is
 * a <code>TextListModel</code> object, the <i>view</i> of the TableList is a
 * <code>TextListView (TableListView)</code> object, and the <i>controller</i>
 * of the TableList is a <code>TextListController</code> object.
 * <p>
 * A TableList emits a <code>ListActionEvent</code> and a
 * <code>ListItemEvent</code>.
 *
 * @see		jp.kyasu.awt.ListModel
 * @see		jp.kyasu.awt.TextListModel
 * @see		jp.kyasu.awt.text.TableListView
 * @see		jp.kyasu.awt.text.TextListView
 * @see		jp.kyasu.awt.text.TextListController
 * @see		jp.kyasu.awt.event.ListActionEvent
 * @see		jp.kyasu.awt.event.ListItemEvent
 *
 * @version 	12 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class TableList extends EventProxyContainer
	implements ItemSelectable, ActionListener, ItemListener
{
    protected TextListModel listModel;
    protected TextListView listView;
    protected TableListView tableView;
    protected TextListController listController;
    protected ScrollPanel scrollPanel;
    protected int rows;
    transient protected ItemListener itemListener;
    transient protected ActionListener actionListener;


    /** Allows single selection. */
    static public final int SINGLE_SELECTION =
				TextListController.SINGLE_SELECTION;

    /** Allows AWT style multiple selections. */
    static public final int MULTIPLE_SELECTIONS =
				TextListController.MULTIPLE_SELECTIONS;

    /** Allows Windows style multiple selections. */
    static public final int SHIFT_MULTIPLE_SELECTIONS =
				TextListController.SHIFT_MULTIPLE_SELECTIONS;

    /** Do not auto resize column when table is resized. */
    static public final int AUTO_RESIZE_OFF =
				TableListView.AUTO_RESIZE_OFF;

    /** Auto resize last column only when table is resized */
    static public final int AUTO_RESIZE_LAST_COLUMN =
				TableListView.AUTO_RESIZE_LAST_COLUMN;

    /** Proportionately resize all columns when table is resized */
    static public final int AUTO_RESIZE_ALL_COLUMNS =
				TableListView.AUTO_RESIZE_ALL_COLUMNS;

    /**
     * The default table list style.
     */
    static public final RichTextStyle DEFAULT_TABLE_STYLE =
					RichTextStyle.DEFAULT_LIST_STYLE;
			/*
			RichTextStyle.DEFAULT_LIST_STYLE.deriveStyle(
				new TextStyle("SansSerif", Font.PLAIN, 12));
			*/

    /**
     * The default column button alignment.
     */
    static public final int DEFAULT_BUTTON_ALIGN = Button.LEFT;


    /**
     * The default number of visible rows is 4. A list with zero rows
     * is unusable and unsightly.
     */
    static protected final int DEFAULT_VISIBLE_ROWS = 4;


    /**
     * Creates the column buttons for the table list with the specified column
     * titles, alignments of the column titles, and the text style of the
     * buttons.
     * @param columnTitles the column titles.
     * @param columnAligns the alignments of the column titles.
     * @param textSTyle    the text style of the buttons.
     * @see jp.kyasu.awt.Label#LEFT
     * @see jp.kyasu.awt.Label#CENTER
     * @see jp.kyasu.awt.Label#RIGHT
     */
    static protected Button[] createColumnButtons(String columnTitles[],
						  int columnAligns[],
						  TextStyle textStyle)
    {
	if (columnTitles == null)
	    throw new NullPointerException();

	Button buttons[] = new Button[columnTitles.length];
	int aindex = 0;
	int align = DEFAULT_BUTTON_ALIGN;
	for (int i = 0; i < columnTitles.length; i++) {
	    buttons[i] = new Button(new Text(columnTitles[i], textStyle));
	    if (columnAligns != null && aindex < columnAligns.length) {
		align = columnAligns[aindex++];
	    }
	    buttons[i].setAlignment(align);
	}
	return buttons;
    }


    /**
     * Constructs an empty table list with the specified column titles.
     * @param columnTitles the column titles.
     */
    public TableList(String columnTitles[]) {
	this(0, columnTitles);
    }

    /**
     * Constructs an empty table list with the specified number of visual
     * lines and column titles.
     * @param rows         the number of visual lines in the table list.
     * @param columnTitles the column titles.
     */
    public TableList(int rows, String columnTitles[]) {
	this(rows, columnTitles, null);
    }

    /**
     * Constructs an empty table list with the specified number of visual
     * lines, column titles, and widths of the columns.
     * @param rows         the number of visual lines in the table list.
     * @param columnTitles the column titles.
     * @param columnWidths  the widths of the columns, or null.
     * @see jp.kyasu.awt.Label#LEFT
     * @see jp.kyasu.awt.Label#CENTER
     * @see jp.kyasu.awt.Label#RIGHT
     */
    public TableList(int rows, String columnTitles[], int columnWidths[]) {
	this(rows, columnTitles, columnWidths, DEFAULT_TABLE_STYLE);
    }

    /**
     * Constructs an empty table list with the specified column titles,
     * widths of the columns, and the rich text style of the table list.
     * @param rows          the number of visual lines in the table list.
     * @param columnTitles  the column titles.
     * @param columnWidths  the widths of the columns, or null.
     * @param richTextStyle the rich text style of the table list.
     * @see jp.kyasu.awt.Label#LEFT
     * @see jp.kyasu.awt.Label#CENTER
     * @see jp.kyasu.awt.Label#RIGHT
     */
    public TableList(int rows, String columnTitles[], int columnWidths[],
		     RichTextStyle richTextStyle)
    {
	this(rows, columnTitles, null, columnWidths, richTextStyle);
    }

    /**
     * Constructs an empty table list with the specified column titles,
     * alignments of the column titles, widths of the columns, and the
     * rich text style of the table list.
     * @param rows          the number of visual lines in the table list.
     * @param columnTitles  the column titles.
     * @param columnAligns  the alignments of the column titles, or null.
     * @param columnWidths  the widths of the columns, or null.
     * @param richTextStyle the rich text style of the table list.
     * @see jp.kyasu.awt.Label#LEFT
     * @see jp.kyasu.awt.Label#CENTER
     * @see jp.kyasu.awt.Label#RIGHT
     */
    public TableList(int rows, String columnTitles[], int columnAligns[],
		     int columnWidths[], RichTextStyle richTextStyle)
    {
	if (columnTitles == null)
	    throw new NullPointerException();
	if (columnWidths != null && columnWidths.length != columnTitles.length)
	    throw new IllegalArgumentException("the numbers of titles and widths do not match");
	Button buttons[] = createColumnButtons(columnTitles, columnAligns,
					       richTextStyle.getTextStyle());
	if (columnWidths == null) {
	    columnWidths = new int[buttons.length];
	    for (int i = 0; i < buttons.length; i++) {
		columnWidths[i] = buttons[i].getPreferredSize().width;
	    }
	}
	TextListModel model = new DefaultTextListModel(columnWidths.length,
						       columnWidths,
						       richTextStyle);
	initialize(model, rows, buttons);
    }

    /**
     * Constructs a table list with the specified model, number of visual
     * lines, and column titles.
     * @param model        the text list model.
     * @param rows         the number of visual lines in the table list.
     * @param columnTitles the column titles.
     */
    public TableList(TextListModel model, int rows, String columnTitles[]) {
	this(model, rows, columnTitles, null);
    }

    /**
     * Constructs a table list with the specified model, number of visual
     * lines, column titles, and alignments of the column titles.
     * @param model        the text list model.
     * @param rows         the number of visual lines in the table list.
     * @param columnTitles the column titles.
     * @param columnAligns the alignments of the column titles, or null.
     * @see jp.kyasu.awt.Label#LEFT
     * @see jp.kyasu.awt.Label#CENTER
     * @see jp.kyasu.awt.Label#RIGHT
     */
    public TableList(TextListModel model, int rows,
		    String columnTitles[], int columnAligns[])
    {
	this(model, rows,
	     createColumnButtons(
		columnTitles, columnAligns,
		model.getTextList().getRichTextStyle().getTextStyle()));
    }

    /**
     * Constructs a table list with the specified model, number of visual
     * lines, and column buttons.
     * @param model   the text list model.
     * @param rows    the number of visual lines in the table list.
     * @param buttons the buttons for column titles.
     */
    public TableList(TextListModel model, int rows, Button buttons[]) {
	initialize(model, rows, buttons);
    }


    /**
     * Initializes the table list with the specified model, number of visual
     * lines, and column buttons.
     * @param model   the text list model.
     * @param rows    the number of visual lines in the table list.
     * @param buttons the buttons for column titles.
     */
    protected void initialize(TextListModel model, int rows, Button buttons[]) {
	if (model == null || buttons == null)
	    throw new NullPointerException();
	int columns = model.getColumnCount();
	if (columns < 1) {
	    throw new IllegalArgumentException(
			"table list model does not have multiple columns");
	}
	if (columns != buttons.length) {
	    throw new IllegalArgumentException(
			"the number of column buttons does not match the number of columns of the table list model");
	}

	tableView = new TableListView(model, buttons);
	listModel = tableView.getModel();
	listView = tableView.getView();
	listController = listView.getController();

	tableView.addActionListener(this);
	listController.addItemListener(this);
	listController.addActionListener(this);

	this.rows = (rows > 0 ? rows : DEFAULT_VISIBLE_ROWS);
	itemListener   = null;
	actionListener = null;

	int scrollbars = (tableView.getAutoResizeMode() == AUTO_RESIZE_OFF ?
				ScrollPanel.SCROLLBARS_BOTH :
				ScrollPanel.SCROLLBARS_VERTICAL_ONLY);
	scrollPanel = new ScrollPanel(scrollbars,
				      ScrollPanel.SCROLLBARS_AS_NEEDED);
	scrollPanel.add(tableView);

	BorderedPanel bp = new BorderedPanel();
	bp.add(scrollPanel, BorderLayout.CENTER);

	setLayout(new BorderLayout());
	add(bp, BorderLayout.CENTER);

	//super.setForeground(listView.getForeground());
	//super.setBackground(listView.getBackground());
	super.setFont(
	   listModel.getTextList().getRichTextStyle().getTextStyle().getFont());
	super.setCursor(listView.getCursor());
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
    }

    /**
     * Adds the specified action listener to receive action events from
     * this list. Action events occur when a user double-clicks
     * on a list item.
     * @param l the action listener.
     */
    public synchronized void addActionListener(ActionListener l) {
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
	if (tableView != null) {
	    Button buttons[] = tableView.getColumnButtons();
	    for (int i = 0; i < buttons.length; i++) {
		buttons[i].setFont(f);
	    }
	    if (isShowing()) {
		tableView.repaintNow();
	    }
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
	if (tableView != null) {
	    tableView.setEnabled(b);
	}
    }

    /**
     * Returns the preferred dimensions for a table list with the specified
     * number of visual lines.
     * @param rows number of visual lines in the table list.
     * @return the preferred dimensions for displaying this table list.
     */
    public Dimension getPreferredSize(int rows) {
	synchronized (getTreeLock()) {
	    Dimension d = tableView.getPreferredSize(rows);
	    ScrollPanel sp = (ScrollPanel)tableView.getParent(); // ScrollPanel
	    if (tableView.getVMaximum() > d.height && sp.vScrollbar != null) {
		d.width += sp.vScrollbar.getPreferredSize().width;
	    }
	    Container c = sp.getParent(); // BorderedPanel
	    Insets insets = c.getInsets();
	    d.width  += (insets.left + insets.right);
	    d.height += (insets.top + insets.bottom);
	    return d;
	}
    }

    /**
     * Returns the preferred size of this table list.
     * @return the preferred dimensions for displaying this table list.
     */
    public Dimension getPreferredSize() {
	synchronized (getTreeLock()) {
	    return ((rows > 0) ?
			getPreferredSize(rows) :
			super.getPreferredSize());
	}
    }

    /**
     * Returns the minumum dimensions for a table list with the specified
     * number of visual lines.
     * @param rows number of visual lines in the table list.
     * @return the minimum dimensions for displaying this table list.
     */
    public Dimension getMinimumSize(int rows) {
	synchronized (getTreeLock()) {
	    Dimension d = tableView.getMinimumSize(rows);
	    ScrollPanel sp = (ScrollPanel)tableView.getParent(); // ScrollPanel
	    if (tableView.getVMaximum() > d.height && sp.vScrollbar != null) {
		d.width += sp.vScrollbar.getPreferredSize().width;
	    }
	    if (tableView.getHMaximum() > d.width && sp.hScrollbar != null) {
		d.height += sp.hScrollbar.getPreferredSize().height;
	    }
	    Container c = sp.getParent(); // BorderedPanel
	    Insets insets = c.getInsets();
	    d.width  += (insets.left + insets.right);
	    d.height += (insets.top + insets.bottom);
	    return d;
	}
    }

    /**
     * Returns the minimum size of this table list.
     * @return the minimum dimensions needed to display this table list.
     */
    public Dimension getMinimumSize() {
	synchronized (getTreeLock()) {
	    return ((rows > 0) ?
			getMinimumSize(rows) :
			super.getMinimumSize());
	}
    }


    // ======== TableList APIs ========

    /**
     * Returns the auto resize mode of the panel. The default is
     * AUTO_RESIZE_ALL_COLUMNS.
     *
     * @return the auto resize mode of the table.
     * @see #setAutoResizeMode(int)
     */
    public int getAutoResizeMode() {
	return tableView.getAutoResizeMode();
    }

    /**
     * Sets the the auto resize mode of the panel.
     *
     * @param mode the auto resize mode.
     * @see #getAutoResizeMode()
     * @see #AUTO_RESIZE_OFF
     * @see #AUTO_RESIZE_LAST_COLUMN
     * @see #AUTO_RESIZE_ALL_COLUMNS
     */
    public void setAutoResizeMode(int mode) {
	if (getAutoResizeMode() == mode)
	    return;
	boolean valid = isValid();
	switch (mode) {
	case AUTO_RESIZE_OFF:
	    scrollPanel.setScrollbarVisibility(ScrollPanel.SCROLLBARS_BOTH);
	    break;
	case AUTO_RESIZE_LAST_COLUMN:
	case AUTO_RESIZE_ALL_COLUMNS:
	    scrollPanel.setScrollbarVisibility(
					ScrollPanel.SCROLLBARS_VERTICAL_ONLY);
	    break;
	}
	tableView.setAutoResizeMode(mode);
	if (valid) {
	    invalidate();
	    validate();
	}
    }

    /**
     * Get the number of visible lines in this table list.
     * @return the number of visible lines in this table list.
     * @see #setRows(int)
     */
    public int getRows() {
	return rows;
    }

    /**
     * Sets the number of visible lines in this table list.
     * @param rows the number of visible lines in this table list.
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
     * Returns the selected items on the list in an array of Objects.
     * @see java.awt.ItemSelectable
     */
    public Object[] getSelectedObjects() {
	return listController.getSelectedObjects();
    }

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
     * Returns the buttons for the column titles.
     */
    public synchronized Button[] getColumnButtons() {
	return tableView.getColumnButtons();
    }

    /**
     * Returns the column titles.
     */
    public synchronized String[] getColumnTitles() {
	Button buttons[] = tableView.getColumnButtons();
	String strs[] = new String[buttons.length];
	for (int i = 0; i < strs.length; i++) {
	    strs[i] = buttons[i].getLabel();
	}
	return strs;
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
     * Returns the number of items in the list.
     */
    public int getItemCount() {
	return listController.getItemCount();
    }

    /**
     * Returns the number of columns in the list.
     */
    public int getColumnCount() {
	return listController.getColumnCount();
    }

    /**
     * Returns the string item associated with the specified index (row)
     * and column.
     */
    public String getItem(int index, int column) {
	return listController.getItem(index, column);
    }

    /**
     * Returns the text item associated with the specified index (row)
     * and column.
     */
    public Text getTextItem(int index, int column) {
	return listController.getTextItem(index, column);
    }

    /**
     * Sets the item associated with the specified index (row) and column
     * to the specified string value.
     */
    public synchronized void setItem(int index, int column, String item) {
	listController.setItem(index, column, item);
    }

    /**
     * Sets the item associated with the specified index (row) and column
     * to the specified text value.
     */
    public void setItem(int index, int column, Text item) {
	setTextItem(index, column, item);
    }

    /**
     * Sets the item associated with the specified index (row) and column
     * to the specified text value.
     */
    public synchronized void setTextItem(int index, int column, Text item) {
	listController.setTextItem(index, column, item);
    }

    /**
     * Sets the items of the list to the specified string row by column items.
     */
    public synchronized void setItems(String items[][]) {
	listController.setItems(items);
    }

    /**
     * Sets the items of the list to the specified text row by column items.
     */
    public void setItems(Text items[][]) {
	setTextItems(items);
    }

    /**
     * Sets the items of the list to the specified text row by column items.
     */
    public synchronized void setTextItems(Text items[][]) {
	listController.setTextItems(items);
    }

    /**
     * Returns the string items associated with the specified row index.
     */
    public synchronized String[] getRowItems(int index) {
	return listController.getRowItems(index);
    }

    /**
     * Returns the text items associated with the specified row index.
     */
    public Text[] getRowTextItems(int index) {
	return listController.getRowTextItems(index);
    }

    /**
     * Returns the item associated with the specified row index.
     */
    public synchronized String getRowItem(int index) {
	return listController.getRowItem(index);
    }

    /**
     * Returns the text item associated with the specified row index.
     */
    public synchronized Text getRowTextItem(int index) {
	return listController.getRowTextItem(index);
    }

    /**
     * Returns the string items associated with the specified column index.
     */
    public synchronized String[] getItems(int column) {
	return listController.getItems(column);
    }

    /**
     * Returns the text items associated with the specified column index.
     */
    public Text[] getTextItems(int column) {
	return listController.getTextItems(column);
    }

    /**
     * Adds the specified string row item to the end of the table list.
     */
    public void addItem(String item[]) {
	addItem(item, -1);
    }

    /**
     * Adds the specified string row item to the specified row index.
     */
    public synchronized void addItem(String item[], int index) {
	listController.addItem(item, index);
    }

    /**
     * Adds the specified text row item to the end of the table list.
     */
    public void addItem(Text item[]) {
	addItem(item, -1);
    }

    /**
     * Adds the specified text row item to the specified row index.
     */
    public void addItem(Text item[], int index) {
	addTextItem(item, index);
    }

    /**
     * Adds the specified text row item to the end of the table list.
     */
    public void addTextItem(Text item[]) {
	addTextItem(item, -1);
    }

    /**
     * Adds the specified text row item to the specified row index.
     */
    public synchronized void addTextItem(Text item[], int index) {
	listController.addTextItem(item, index);
    }

    /**
     * Replaces the item at the specified row index with the new strings.
     */
    public synchronized void replaceItem(String newValue[], int index) {
	listController.replaceItem(newValue, index);
    }

    /**
     * Replaces the item at the specified row index with the new texts.
     */
    public void replaceItem(Text newValue[], int index) {
	replaceTextItem(newValue, index);
    }

    /**
     * Replaces the item at the specified row index with the new texts.
     */
    public synchronized void replaceTextItem(Text newValue[], int index) {
	listController.replaceTextItem(newValue, index);
    }

    /**
     * Removes all items from the list.
     */
    public synchronized void removeAll() {
	listController.removeAll();
    }

    /**
     * Remove the item at the specified row position.
     */
    public void remove(int position) {
	remove(position, position);
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
     * Determines if the specified row in this scrolling list is selected.
     * @param index the row to be checked.
     * @return <code>true</code> if the specified row has been selected;
     *         <code>false</code> otherwise.
     */
    public boolean isIndexSelected(int index) {
	return listController.isIndexSelected(index);
    }

    /**
     * Gets the index of the selected row on the list,
     * @return the index of the selected row, or <code>-1</code> if no
     *         item is selected, or if more that one row is selected.
     */
    public synchronized int getSelectedIndex() {
	return listController.getSelectedIndex();
    }

    /**
     * Gets the selected row indexes on the list.
     * @return an array of the selected row indexes of this scrolling list.
     */
    public synchronized int[] getSelectedIndexes() {
	return listController.getSelectedIndexes();
    }

    /**
     * Selects the row at the specified index in the scrolling list.
     * @param index the position of the row to select.
     */
    public synchronized void select(int index) {
	listController.select(index);
    }

    /**
     * Deselects the row at the specified index.
     * <p>
     * If the row at the specified index is not selected, or if the
     * index is out of range, then the operation is ignored.
     * @param index the position of the row to deselect.
     */
    public synchronized void deselect(int index) {
	listController.deselect(index);
    }

    /**
     * Gets the index of the row that was last made visible by the method
     * <code>makeVisible</code>.
     * @return the index of the row that was last made visible.
     * @see #makeVisible(int)
     */
    public int getVisibleIndex() {
	return listController.getVisibleIndex();
    }

    /**
     * Makes the row at the specified index visible.
     * @param index the position of the row.
     * @see #getVisibleIndex()
     */
    public synchronized void makeVisible(int index) {
	listController.makeVisible(index);
    }


    /**
     * Returns the thickness of the scroll bar.
     * @see #setScrollbarThickness(int)
     */
    public int getScrollbarThickness() {
	ScrollPanel sp = (ScrollPanel)tableView.getParent();
	return sp.getScrollbarThickness();
    }

    /**
     * Sets the thickness of the scroll bar.
     * @see #setScrollbarThickness()
     */
    public synchronized void setScrollbarThickness(int thickness) {
	ScrollPanel sp = (ScrollPanel)tableView.getParent();
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
