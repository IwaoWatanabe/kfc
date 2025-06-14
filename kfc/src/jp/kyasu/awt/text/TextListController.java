/*
 * TextListController.java
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
import jp.kyasu.awt.Dialog;
import jp.kyasu.awt.Label;
import jp.kyasu.awt.Panel;
import jp.kyasu.awt.TextField;
import jp.kyasu.awt.TextListModel;
import jp.kyasu.awt.event.ListActionEvent;
import jp.kyasu.awt.event.ListItemEvent;
import jp.kyasu.awt.event.ListModelEvent;
import jp.kyasu.awt.event.ListModelListener;
import jp.kyasu.awt.event.TextListModelEvent;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextBuffer;
import jp.kyasu.graphics.TextLayout;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.text.TextLayoutChange;
import jp.kyasu.graphics.text.TextLineInfo;
import jp.kyasu.graphics.text.TextPositionInfo;
import jp.kyasu.util.VArray;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.ItemSelectable;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.event.*;

/**
 * The <code>TextListController</code> class implements a view of a MVC model
 * for the text list. The model of the MVC model is a <code>TextListModel</code>
 * object and the view of the MVC model is a <code>TextListView</code> object.
 *
 * @see 	jp.kyasu.awt.TextListModel
 * @see 	jp.kyasu.awt.text.TextListView
 *
 * @version 	18 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextListController extends TextController
	implements ActionListener, ItemSelectable
{
    protected TextListModel model;
    protected TextListView view;
    protected int selectionMode;
    protected boolean deselectionEnabled;
    protected boolean movingSelectionEnabled;
    protected int lastShiftSelectedIndex;
    protected PopupMenu popupMenu;
    transient protected ItemListener itemListener;
    transient protected ActionListener actionListener;


    /**
     * Allows single selection.
     */
    static public final int SINGLE_SELECTION          = 0;

    /**
     * Allows AWT style multiple selections.
     */
    static public final int MULTIPLE_SELECTIONS       = 1;

    /**
     * Allows Windows style multiple selections.
     */
    static public final int SHIFT_MULTIPLE_SELECTIONS = 2;


    /**
     * Constructs a text list controller with the specified text list view.
     *
     * @param view the text list view.
     */
    public TextListController(TextListView view) {
	super();
	if (view == null)
	    throw new NullPointerException();
	model = view.model;
	this.view = view;

	clickToFocus = true; // overrides default

	selectionVisibleAtFocus = true;
	view.setSelectionVisible(false);

	selectionMode = SINGLE_SELECTION;
	deselectionEnabled     = false; // JDK1.1 compatible.
	movingSelectionEnabled = false;
	lastShiftSelectedIndex = -1;

	setPopupMenu(createPopupMenu());

	itemListener = null;
	actionListener = null;
    }


    /**
     * Returns the model of this controller.
     */
    public TextListModel getModel() {
	return model;
    }

    /**
     * Returns the view of this controller.
     */
    public TextView getView() {
	return view;
    }

    /**
     * Returns the selected items or null if no items are selected.
     * @see java.awt.ItemSelectable
     */
    public Object[] getSelectedObjects() {
	int sel[] = model.getSelectedIndexes();
	String strs[] = new String[sel.length];
	for (int i = 0; i < sel.length; i++) {
	    Text text = getRowTextItem(sel[i]);
	    strs[i] = Text.getSystemString(text.toString());
	}
	return strs;
    }

    /**
     * Adds the specified item listener to receive item events from
     * this controller.
     *
     * @param l the item listener.
     * @see java.awt.ItemSelectable
     */
    public void addItemListener(ItemListener l) {
	itemListener = AWTEventMulticaster.add(itemListener, l);
    }

    /**
     * Removes the specified item listener so it no longer receives item
     * events from this controller.
     *
     * @param l the item listener.
     * @see java.awt.ItemSelectable
     */
    public void removeItemListener(ItemListener l) {
	itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /**
     * Notifies the item event to the item listeners.
     *
     * @param row      the row index.
     * @param column   the column index.
     * @param selected true if the item has been selected.
     */
    protected void notifyItemListeners(int row, int column, boolean selected) {
	if (itemListener != null) {
	    ItemEvent e = new ListItemEvent(this,
					    ItemEvent.ITEM_STATE_CHANGED,
					    (selected ?
						ItemEvent.SELECTED :
						ItemEvent.DESELECTED),
					    row, column);
	    itemListener.itemStateChanged(e);
	}
    }

    /**
     * Notifies the item event to the item listeners.
     *
     * @param index    the index of the changed items.
     * @param indices  the row indices of the changed items.
     * @param selected true if the items have been selected.
     */
    protected void notifyItemListeners(int index, int indices[],
				       boolean selected)
    {
	if (itemListener != null) {
	    ItemEvent e = new ListItemEvent(this,
					    ItemEvent.ITEM_STATE_CHANGED,
					    new Integer(index),
					    (selected ?
						ItemEvent.SELECTED :
						ItemEvent.DESELECTED),
					    indices,
					    -1);
	    itemListener.itemStateChanged(e);
	}
    }

    /**
     * Adds the specified action listener to receive action events from
     * this controller.
     *
     * @param l the action listener.
     */
    public void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
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

    /**
     * Notifies the action event to the action listeners.
     *
     * @param item    the item.
     * @param row     the row index.
     * @param column  the column index.
     */
    protected void notifyActionListeners(Text item, int row, int column) {
	notifyActionListeners(Text.getSystemString(item.toString()),
			      item, row, column);
    }

    /**
     * Notifies the action event to the action listeners.
     *
     * @param command the action command.
     * @param item    the item.
     * @param row     the row index.
     * @param column  the column index.
     */
    protected void notifyActionListeners(String command, Text item,
					 int row, int column)
    {
	if (actionListener != null) {
	    ActionEvent e = new ListActionEvent(this, command, item,
						row, column);
	    actionListener.actionPerformed(e);
	}
    }

    /**
     * Returns the selection mode.
     * @see #setSelectionMode(int)
     * @see #SINGLE_SELECTION
     * @see #MULTIPLE_SELECTIONS
     * @see #SHIFT_MULTIPLE_SELECTIONS
     */
    public int getSelectionMode() {
	return selectionMode;
    }

    /**
     * Sets the selection mode.
     * @see #getSelectionMode()
     * @see #SINGLE_SELECTION
     * @see #MULTIPLE_SELECTIONS
     * @see #SHIFT_MULTIPLE_SELECTIONS
     */
    public synchronized void setSelectionMode(int mode) {
	if (selectionMode == mode)
	    return;
	switch (mode) {
	case SINGLE_SELECTION:
	case MULTIPLE_SELECTIONS:
	case SHIFT_MULTIPLE_SELECTIONS:
	    selectionMode = mode;
	    break;
	default:
	    throw new IllegalArgumentException("improper selection mode: " + mode);
	}
	if (selectionMode == SINGLE_SELECTION) {
	    int sel[] = model.getSelectedIndexes();
	    if (sel.length > 1) {
		for (int i = 1; i < sel.length; i++) {
		    deselect(sel[i]);
		}
	    }
	}
	else if (selectionMode == SHIFT_MULTIPLE_SELECTIONS) {
	    deselectionEnabled     = true;
	    lastShiftSelectedIndex = -1;
	}
    }

    /**
     * Tests if the deselection is enabled when the selection mode is a single
     * selection.
     * @see #setDeselectionEnabled(boolean)
     */
    public boolean isDeselectionEnabled() {
	return deselectionEnabled;
    }

    /**
     * Makes the deselection enabled when the selection mode is a single
     * selection.
     * @see #isDeselectionEnabled()
     */
    public synchronized void setDeselectionEnabled(boolean b) {
	deselectionEnabled = b;
    }

    /**
     * Tests if the selection follows the mouse.
     * @see #setMovingSelectionEnabled(boolean)
     */
    public boolean isMovingSelectionEnabled() {
	return movingSelectionEnabled;
    }

    /**
     * Makes the selection follow the mouse.
     * @see #isMovingSelectionEnabled()
     */
    public synchronized void setMovingSelectionEnabled(boolean b) {
	movingSelectionEnabled = b;
    }

    /**
     * Returns the popup menu of this controller.
     * @see #setPopupMenu(java.awt.PopupMenu)
     */
    public PopupMenu getPopupMenu() {
	return popupMenu;
    }

    /**
     * Sets the popup menu of this controller.
     * @see #getPopupMenu()
     */
    public synchronized void setPopupMenu(PopupMenu menu) {
	if (popupMenu != null)
	    view.remove(popupMenu);
	popupMenu = menu;
	if (popupMenu != null)
	    view.add(popupMenu);
    }


    // ================ ListModel stuff ================

    /**
     * Returns the number of items in the list.
     */
    public int getItemCount() {
	return model.getItemCount();
    }

    /**
     * Returns the number of columns in the list.
     */
    public int getColumnCount() {
	return model.getColumnCount();
    }

    /**
     * Returns the string item associated with the specified index (row)
     * and column.
     */
    public String getItem(int index, int column) {
	return Text.getSystemString(getTextItem(index, column).toString());
    }

    /**
     * Returns the text item associated with the specified index (row)
     * and column.
     */
    public Text getTextItem(int index, int column) {
	return (Text)model.getItem(index, column);
    }

    /**
     * Sets the item associated with the specified index (row) and column
     * to the specified string value.
     */
    public synchronized void setItem(int index, int column, String item) {
	model.setItem(index, column, item);
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
	model.setItem(index, column, item);
    }

    /**
     * Sets the items of the list to the specified string row by column items.
     */
    public synchronized void setItems(String items[][]) {
	model.replaceItems(0, model.getItemCount(), items);
	model.removeSelection(0, model.getItemCount() - 1);
	lastShiftSelectedIndex = -1;
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
	model.replaceItems(0, model.getItemCount(), items);
	model.removeSelection(0, model.getItemCount() - 1);
	lastShiftSelectedIndex = -1;
    }

    /**
     * Returns the string items associated with the specified row index.
     */
    public synchronized String[] getRowItems(int index) {
	Text texts[] = getRowTextItems(index);
	int len = texts.length;
	String strs[] = new String[len];
	for (int i = 0; i < len; i++) {
	    strs[i] = Text.getSystemString(texts[i].toString());
	}
	return strs;
    }

    /**
     * Returns the text items associated with the specified row index.
     */
    public Text[] getRowTextItems(int index) {
	return (Text[])model.getRowItems(index);
    }

    /**
     * Returns the item associated with the specified row index.
     */
    public synchronized String getRowItem(int index) {
	return Text.getSystemString(getRowTextItem(index).toString());
    }

    /**
     * Returns the text item associated with the specified row index.
     */
    public synchronized Text getRowTextItem(int index) {
	Text texts[] = (Text[])model.getRowItems(index);
	Text text = texts[0];
	for (int i = 1; i < texts.length; i++) {
	    text.append('\t').append(texts[i]);
	}
	return text;
    }

    /**
     * Returns the string items associated with the specified column index.
     */
    public synchronized String[] getItems(int column) {
	Text texts[] = getTextItems(column);
	int len = texts.length;
	String strs[] = new String[len];
	for (int i = 0; i < len; i++) {
	    strs[i] = Text.getSystemString(texts[i].toString());
	}
	return strs;
    }

    /**
     * Returns the text items associated with the specified column index.
     */
    public Text[] getTextItems(int column) {
	return (Text[])model.getItems(column);
    }

    /**
     * Adds the specified string row item to the specified row index.
     */
    public synchronized void addItem(String item[], int index) {
	int itemCount = model.getItemCount();
	if (index < -1 || index >= itemCount) {
	    index = -1;
	}
	if (index == -1) {
	    model.replaceItems(itemCount, itemCount, new String[][]{ item });
	}
	else {
	    model.replaceItems(index, index, new String[][]{ item });
	}
    }

    /**
     * Adds the specified text row item to the specified row index.
     */
    public void addItem(Text item[], int index) {
	addTextItem(item, index);
    }

    /**
     * Adds the specified text row item to the specified row index.
     */
    public synchronized void addTextItem(Text item[], int index) {
	int itemCount = model.getItemCount();
	if (index < -1 || index >= itemCount) {
	    index = -1;
	}
	if (index == -1) {
	    model.replaceItems(itemCount, itemCount, new Text[][]{ item });
	}
	else {
	    model.replaceItems(index, index, new Text[][]{ item });
	}
    }

    /**
     * Replaces the item at the specified row index with the new strings.
     */
    public synchronized void replaceItem(String newValue[], int index) {
	int itemCount = getItemCount();
	if (index < 0 || index >= itemCount) {
	    return;
	}
	model.replaceItems(index, index + 1, new String[][]{ newValue });
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
	int itemCount = getItemCount();
	if (index < 0 || index >= itemCount) {
	    return;
	}
	model.replaceItems(index, index + 1, new Text[][]{ newValue });
    }

    /**
     * Removes all items from the list.
     */
    public synchronized void removeAll() {
	model.replaceItems(0, model.getItemCount(),
			   new String[0][getColumnCount()]);
	lastShiftSelectedIndex = -1;
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
	if (start < 0 || end >= model.getItemCount()) {
	    return;
	}
	model.replaceItems(start, end + 1,
			   new String[0][getColumnCount()]);
	lastShiftSelectedIndex = -1;
    }

    /**
     * Remove the items at the specified row indices.
     * @param indices the row indices to be removed.
     */
    public synchronized void remove(int indices[]) {
	if (indices == null)
	    return;
	if (indices.length == 0)
	    return;
	int itemCount = model.getItemCount();
	VArray vindices = new VArray(int.class);
	for (int i = 0; i < indices.length; i++) {
	    int index = indices[i];
	    if (0 <= index && index < itemCount) {
		vindices.append(index);
	    }
	}
	if (vindices.isEmpty())
	    return;
	vindices.sort();

	model.removeListModelListener(view);
	try {
	    int rindices[] = (int[])vindices.getTrimmedArray();
	    String emptyItem[][] = new String[0][getColumnCount()];
	    for (int i = 0; i < rindices.length; i++) {
		int r = rindices[i] - i;
		model.replaceItems(r, r + 1, emptyItem);
	    }
	}
	finally {
	    model.addListModelListener(view);
	}

	lastShiftSelectedIndex = -1;
	view.layout.invalidate();
	view.layout.validate();
	view.updateAfterReplaced(
		new TextListModelEvent(
			model,
			ListModelEvent.LIST_MODEL_REPLACED,
			0, model.getItemCount(), -vindices.length(),
			new TextLayoutChange(TextLayoutChange.FULL_REPAINT)));
    }

    /**
     * Remove the items at the indices of the selected rows.
     */
    public void removeSelectedIndexes() {
	remove(model.getSelectedIndexes());
    }

    /**
     * Checks if the row at the specified index is selected.
     */
    public boolean isIndexSelected(int index) {
	return model.isIndexSelected(index);
    }

    /**
     * Returns the index of the selected row.
     */
    public synchronized int getSelectedIndex() {
	int sel[] = model.getSelectedIndexes();
	return (sel.length == 1) ? sel[0] : -1;
    }

    /**
     * Returns the indices of the selected rows.
     */
    public int[] getSelectedIndexes() {
	return model.getSelectedIndexes();
    }

    // ================ List stuff ================

    /**
     * Selects the row at the specified index.
     * @param index the position of the row to select.
     */
    public void select(int index) {
	select(index, -1, true, false);
    }

    /**
     * Selects the row at the specified index.
     * @param index       the position of the row to select.
     * @param column      the position of the column to select.
     * @param makeVisible if true, makes the selected row visible.
     * @param emitEvent   if true, emits the item event.
     */
    public synchronized void select(int index, int column,
				    boolean makeVisible, boolean emitEvent)
    {
	if (model.isIndexSelected(index))
	    return;

	if (selectionMode == SINGLE_SELECTION) {
	    model.setSelection(index, index);
	}
	else {
	    model.addSelection(index, index);
	}

	if (makeVisible) {
	    makeVisible(index);
	}

	if (emitEvent) {
	    notifyItemListeners(index, column, true);
	}
    }

    /**
     * Deselects the row at the specified index.
     * @param index the position of the row to deselect.
     */
    public void deselect(int index) {
	deselect(index, -1, true, false);
    }

    /**
     * Deselects the row at the specified index.
     * @param index       the position of the row to deselect.
     * @param column      the position of the column to deselect.
     * @param makeVisible if true, makes the deselected row visible.
     * @param emitEvent   if true, emits the item event.
     */
    public synchronized void deselect(int index, int column,
				      boolean makeVisible, boolean emitEvent)
    {
	if (!model.isIndexSelected(index))
	    return;

	model.removeSelection(index, index);

	if (makeVisible) {
	    makeVisible(index);
	}

	if (emitEvent) {
	    notifyItemListeners(index, column, false);
	}
    }

    /**
     * Checks if this controller allows multiple selections.
     * @see #setMultipleMode(boolean)
     * @see #getSelectionMode()
     * @see #setSelectionMode(int)
     */
    public boolean isMultipleMode() {
	return (selectionMode != SINGLE_SELECTION);
    }

    /**
     * Sets the flag that determines whether this controller allows
     * multiple selections.
     * @see #isMultipleMode()
     * @see #getSelectionMode()
     * @see #setSelectionMode(int)
     */
    public void setMultipleMode(boolean b) {
	setSelectionMode(b ? MULTIPLE_SELECTIONS : SINGLE_SELECTION);
    }

    /**
     * Returns the index of the row that was last made visible.
     * @see #makeVisible(int)
     */
    public int getVisibleIndex() {
	return view.getVisibleIndex();
    }

    /**
     * Makes the row at the specified index visible.
     * @see #getVisibleIndex()
     */
    public synchronized void makeVisible(int index) {
	if (index == view.getVisibleIndex())
	    return;
	if (!view.isShowing()) {
	    view.setVisibleIndex(index);
	}
	else {
	    view.hideSelection();
	    view.setVisibleIndex(index);
	    TextPositionInfo posInfo = view.getVisiblePosition();
	    view.scrollYTo(posInfo);
	    view.showSelection();
	}
    }

    /**
     * Returns the number of visible lines in the view.
     */
    public int getRows() {
	return view.getRows();
    }

    // ================ Listener ================

    /**
     * Invoked when the mouse has been pressed on a component.
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(MouseEvent e) {
	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	/*
	if (movingSelectionEnabled) {
	    mouseReleased(new MouseEvent(e.getComponent(),
					 MouseEvent.MOUSE_RELEASED, // e.getID()
					 e.getWhen(),
					 e.getModifiers(),
					 e.getX(),
					 e.getY(),
					 1, //e.getClickCount()
					 e.isPopupTrigger()));
	    return;
	}
	*/

	if (clickToFocus && !movingSelectionEnabled) {
	    view.requestFocus();
	    e.consume();
	}

	if (model.getItemCount() == 0)
	    return;

	if (e.getClickCount() > 1)
	    return;

	e.consume();

	//if (view.isEditable() && (e.isPopupTrigger() || e.isMetaDown())) {
	if (e.isPopupTrigger() || e.isMetaDown()) {
	    if (popupMenu != null) {
		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	    }
	    return;
	}

	int lineIndex =
		view.getLineIndexNearby(view.getVisibleBegin(), e.getPoint());

	if (selectionMode == SINGLE_SELECTION) {
	    if (deselectionEnabled) {
		makeVisible(lineIndex);
	    }
	    else {
		select(lineIndex, getColumn(e), true, false);
	    }
	}
	else if (selectionMode == MULTIPLE_SELECTIONS) {
	    if (lineIndex != view.getVisibleIndex()) {
		makeVisible(lineIndex);
	    }
	}
	else { // selectionMode == SHIFT_MULTIPLE_SELECTIONS
	    if (e.isControlDown()) {
		if (model.isIndexSelected(lineIndex)) {
		    deselect(lineIndex, getColumn(e), true, true);
		}
		else {
		    select(lineIndex, getColumn(e), true, true);
		}
		lastShiftSelectedIndex = -1;
	    }
	    else {
		shiftMultipleSelectAction(lineIndex, getColumn(e),
					  e.isShiftDown());
	    }
	}
    }

    /**
     * Invoked when the mouse has been released on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(MouseEvent e) {
	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (model.getItemCount() == 0)
	    return;

	int index = view.getVisibleIndex();
	if (index < 0)
	    return;

	e.consume();

	if (selectionMode == SINGLE_SELECTION) {
	    if (!model.isIndexSelected(index)) {
		select(index, getColumn(e), false, true);
	    }
	    else if (deselectionEnabled) {
		deselect(index, getColumn(e), false, true);
	    }
	    else {
		notifyItemListeners(index, getColumn(e), true);
	    }
	}
	else if (selectionMode == MULTIPLE_SELECTIONS) {
	    if (!model.isIndexSelected(index)) {
		select(index, getColumn(e), false, true);
	    }
	    else {
		deselect(index, getColumn(e), false, true);
	    }
	}
	else { // selectionMode == SHIFT_MULTIPLE_SELECTIONS
	}

	if (e.getClickCount() == 2) {
	    int column = getColumn(e);
	    notifyActionListeners(getTextItem(index, column), index, column);
	}
    }

    /**
     * Invoked when the mouse button has been moved on a component.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseMoved(MouseEvent e) {
	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (!movingSelectionEnabled)
	    return;

	if (model.getItemCount() == 0)
	    return;

	e.consume();

	mouseDragAction(e);
    }

    /**
     * Invoked when the mouse button is pressed on a component and then dragged.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseDragged(MouseEvent e) {
	// mousePressed -> mouseDragged* -> mouseReleased

	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (model.getItemCount() == 0)
	    return;

	e.consume();

	mouseDragAction(e);
    }

    protected void mouseDragAction(MouseEvent e) {
	int lineIndex =
		view.getLineIndexNearby(view.getVisibleBegin(), e.getPoint());

	if (selectionMode == SINGLE_SELECTION) {
	    if (deselectionEnabled) {
		makeVisible(lineIndex);
	    }
	    else {
		select(lineIndex, getColumn(e), true, false);
	    }
	}
	else if (selectionMode == MULTIPLE_SELECTIONS) {
	    if (lineIndex != view.getVisibleIndex()) {
		makeVisible(lineIndex);
	    }
	}
	else { // selectionMode == SHIFT_MULTIPLE_SELECTIONS
	}
    }

    /**
     * Invoked when a key has been pressed.
     * @see java.awt.event.KeyListener
     */
    public void keyPressed(KeyEvent e) {
	// This method is invoked by the special keys (CTL, SHIFT, etc.).

	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (model.getItemCount() == 0)
	    return;

	int vis = view.getVisibleIndex();
	switch (e.getKeyCode()) {
	case KeyEvent.VK_UP:
	    e.consume();
	    if (vis > 0) {
		if (selectionMode == SINGLE_SELECTION) {
		    if (model.isIndexSelected(vis)) {
			select(vis - 1, -1, true, true);
		    }
		    else {
			makeVisible(vis - 1);
		    }
		}
		else if (selectionMode == MULTIPLE_SELECTIONS) {
		    if (e.isShiftDown()) {
			if (model.isIndexSelected(vis)) {
			    if (!model.isIndexSelected(vis - 1)) {
				select(vis - 1, -1, true, true);
			    }
			    else {
				makeVisible(vis - 1);
			    }
			}
			else {
			    if (model.isIndexSelected(vis - 1)) {
				deselect(vis - 1, -1, true, true);
			    }
			    else {
				makeVisible(vis - 1);
			    }
			}
		    }
		    else {
			makeVisible(vis - 1);
		    }
		}
		else { // selectionMode == SHIFT_MULTIPLE_SELECTIONS
		    if (e.isControlDown()) {
			makeVisible(vis - 1);
		    }
		    else {
			shiftMultipleSelectAction(vis - 1, -1, e.isShiftDown());
		    }
		}
	    }
	    break;
	case KeyEvent.VK_DOWN:
	    e.consume();
	    if (vis < model.getItemCount() - 1) {
		if (selectionMode == SINGLE_SELECTION) {
		    if (model.isIndexSelected(vis)) {
			select(vis + 1, -1, true, true);
		    }
		    else {
			makeVisible(vis + 1);
		    }
		}
		else if (selectionMode == MULTIPLE_SELECTIONS) {
		    if (e.isShiftDown()) {
			if (model.isIndexSelected(vis)) {
			    if (!model.isIndexSelected(vis + 1)) {
				select(vis + 1, -1, true, true);
			    }
			    else {
				makeVisible(vis + 1);
			    }
			}
			else {
			    if (model.isIndexSelected(vis + 1)) {
				deselect(vis + 1, -1, true, true);
			    }
			    else {
				makeVisible(vis + 1);
			    }
			}
		    }
		    else {
			makeVisible(vis + 1);
		    }
		}
		else { // selectionMode == SHIFT_MULTIPLE_SELECTIONS
		    if (e.isControlDown()) {
			makeVisible(vis + 1);
		    }
		    else {
			shiftMultipleSelectAction(vis + 1, -1, e.isShiftDown());
		    }
		}
	    }
	    break;
	case KeyEvent.VK_ENTER:
	    e.consume();
	    if (!model.isIndexSelected(vis)) {
		select(vis, -1, false, true);
	    }
	    notifyActionListeners(getRowTextItem(vis), vis, -1);
	    break;
	case KeyEvent.VK_SPACE:
	    if (movingSelectionEnabled) {
		e.consume();
		if (!model.isIndexSelected(vis)) {
		    select(vis, -1, false, true);
		}
	    	notifyActionListeners(getRowTextItem(vis), vis, -1);
	    }
	    else if (selectionMode == MULTIPLE_SELECTIONS) {
		e.consume();
		if (model.isIndexSelected(vis)) {
		    deselect(vis, -1, true, true);
		}
		else {
		    select(vis, -1, true, true);
		}
	    }
	    else { // selectionMode == SHIFT_MULTIPLE_SELECTIONS
		e.consume();
		if (model.isIndexSelected(vis)) {
		    int change[][] = model.setSelection(vis, vis);
		    if (change != null) {
			/*
			int added[] = change[0];
			if (added.length > 0) {
			    notifyItemListeners(lineIndex, added, true);
			}
			*/
			int removed[] = change[1];
			if (removed.length > 0) {
			    notifyItemListeners(removed[0], removed, false);
			}
		    }
		}
		else {
		    select(vis, -1, true, true);
		}
	    }
	    break;
	}
    }

    /**
     * Invoked when an action occurs.
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(ActionEvent e) {
	if (!view.isEnabled())
	    return;

	String command = e.getActionCommand();
	boolean isShiftDown = ((e.getModifiers() & Event.SHIFT_MASK) != 0);
	if (command.equals(A_COPY)) {
	    int index = getSelectedIndex();
	    if (index < 0) {
		return;
	    }
	    Text text = getRowTextItem(index);
	    setCutBuffer(text);
	    if (isShiftDown) {
		setClipboardText(text);
	    }
	}
	else if (command.equals(A_FIND)) {
	    findTextLine();
	}
    }

    // ================ Protected ================

    /**
     * Returns the column from the specified mouse event.
     */
    protected int getColumn(MouseEvent e) {
	int columns = model.getColumnCount();
	if (columns == 1) {
	    return 0;
	}
	int pos = e.getX();
	int colWidths[] = model.getColumnWidths();
	int x = view.offset.x;
	for (int i = 0; i < colWidths.length; i++) {
	    x += colWidths[i];
	    if (pos < x) {
		return i;
	    }
	}
	return columns - 1;
    }

    /**
     * Does an action for SHIFT_MULTIPLE_SELECTIONS.
     */
    protected void shiftMultipleSelectAction(int lineIndex, int column,
					     boolean isShiftDown)
    {
	if (selectionMode != SHIFT_MULTIPLE_SELECTIONS)
	    return;

	if (isShiftDown &&
	    (model.getSelectedCount() == 1 || lastShiftSelectedIndex >= 0))
	{
	    if (model.getSelectedCount() == 1) {
		lastShiftSelectedIndex = getSelectedIndex();
	    }
	    int low, high;
	    if (lineIndex < lastShiftSelectedIndex) {
		low  = lineIndex;
		high = lastShiftSelectedIndex;
	    }
	    else if (lineIndex > lastShiftSelectedIndex) {
		low  = lastShiftSelectedIndex;
		high = lineIndex;
	    }
	    else {
		low = high = lastShiftSelectedIndex;
	    }

	    int change[][] = model.setSelection(low, high);
	    makeVisible(lineIndex);

	    if (change != null) {
		int added[] = change[0];
		if (added.length > 0) {
		    notifyItemListeners(lineIndex, added, true);
		}
		/*
		int removed[] = change[1];
		if (removed.length > 0) {
		    notifyItemListeners(removed[0], removed, false);
		}
		*/
	    }
	    else {
		notifyItemListeners(lineIndex, column, true);
	    }
	}
	else {
	    int change[][] = model.setSelection(lineIndex, lineIndex);
	    makeVisible(lineIndex);

	    if (change != null) {
		int added[] = change[0];
		if (added.length > 0) {
		    notifyItemListeners(lineIndex, added, true);
		}
		/*
		int removed[] = change[1];
		if (removed.length > 0) {
		    notifyItemListeners(removed[0], removed, false);
		}
		*/
	    }
	    else {
		notifyItemListeners(lineIndex, column, true);
	    }

	    lastShiftSelectedIndex = -1;
	}
    }

    /**
     * Creates a popup menu.
     */
    protected PopupMenu createPopupMenu() {
	PopupMenu menu = new PopupMenu();
	menu.add(createMenuItem(L_COPY, A_COPY, null));
	menu.addSeparator();
	menu.add(createMenuItem(L_FIND, A_FIND, null));
	return menu;
    }

    /**
     * Creates a menu item with the specified label and shortcut.
     */
    protected MenuItem createMenuItem(String label, String action,
				      String shortcut)
    {
	MenuItem mi = new MenuItem(label);
	if (shortcut != null && shortcut.length() > 0) {
	    mi.setShortcut(new MenuShortcut(shortcut.charAt(0)));
	}
	mi.setActionCommand(action);
	mi.addActionListener(this);
	return mi;
    }

    /**
     * Enables or disables the specified menu.
     */
    protected void setMenuEnabled(Menu menu, boolean b) {
	int count = menu.getItemCount();
	for (int i = 0; i < count; i++) {
	    MenuItem item = menu.getItem(i);
	    /*
	    if (!L_COPY.equals(item.getLabel()) &&
		!L_FIND.equals(item.getLabel()))
	    */
	    if (!A_COPY.equals(item.getActionCommand()) &&
		!A_FIND.equals(item.getActionCommand()))
	    {
		item.setEnabled(b);
	    }
	}
    }

    /**
     * Finds the string in the text lists.
     */
    public void findTextLine() {
	Dialog dialog = createFindDialog(null);
	dialog.setVisible(true);
    }

    /**
     * Finds the specified string in the text lists.
     */
    public boolean findTextLine(String str) {
	if (str.length() == 0)
	    return false;
	TextPositionInfo posInfo = view.getVisiblePosition();
	int startIndex = 0;
	if (model.getItemCount() > 0 && posInfo != null) {
	    startIndex = posInfo.lineEnd;
	}
	return findTextLine(str, startIndex);
    }

    /**
     * Finds the specified string in the text lists starting at the specified
     * index.
     */
    protected boolean findTextLine(String str, int startIndex) {
	if (str.length() == 0)
	    return false;
	int textIndex =
		model.getTextList().getRichText().getText().indexOf(
							    str, startIndex);
	if (textIndex >= 0) {
	    TextPositionInfo posInfo = view.getTextPositionAt(textIndex);
	    select(posInfo.lineIndex, -1, true, true);
	    return true;
	}
	else {
	    if (Dialog.confirm(view.getFrame(),
			getResourceString(
			    "kfc.text.findContinueLabel",
			    "End of text reached; continue from beggining?")))
	    {
		return findTextLine(str, 0);
	    }
	}
	return false;
    }

    /**
     * Creates a dialog for the find operation with the specified initial
     * string.
     */
    protected Dialog createFindDialog(String initStr) {
	final Dialog dialog = new Dialog(
				view.getFrame(),
				getResourceString("kfc.text.findTitle", "Find"),
				true);
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	Panel p1 = new Panel();
	p1.setLayout(gridbag);
	Label label = new Label(
			getResourceString("kfc.text.findFieldLabel", "Find:"));
	c.gridwidth = GridBagConstraints.RELATIVE;
	gridbag.setConstraints(label, c);
	p1.add(label);
	final TextField ffield = new TextField(30);
	if (initStr != null) {
	    ffield.setText(initStr);
	}
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(ffield, c);
	p1.add(ffield);
	dialog.add(p1, BorderLayout.CENTER);

	Panel p2 = new Panel();
	Button b = new Button(
			getResourceString("kfc.text.findStartLabel", "Find"));
	ActionListener al = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		//view.setSelectionVisible(true);
		findTextLine(ffield.getText());
	    }
	};
	ffield.addActionListener(al);
	b.addActionListener(al);
	p2.add(b);
	b = new Button(
			getResourceString("kfc.text.findEndLabel", "Close"));
	b.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dialog.setVisible(false);
		dialog.dispose();
	    }
	});
	p2.add(b);

	dialog.add(p2, BorderLayout.SOUTH);
	dialog.pack();
	return dialog;
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
