/*
 * DefaultTextListModel.java
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

import jp.kyasu.awt.event.ListModelEvent;
import jp.kyasu.awt.event.ListModelListener;
import jp.kyasu.awt.event.TextListModelEvent;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextBuffer;
import jp.kyasu.graphics.TextLayout;
import jp.kyasu.graphics.TextList;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.text.TextChange;
import jp.kyasu.graphics.text.TextLineInfo;
import jp.kyasu.graphics.text.TextLayoutChange;
import jp.kyasu.graphics.text.TextPositionInfo;
import jp.kyasu.util.VArray;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

/**
 * The <code>DefaultTextListModel</code> class is a default implementation
 * of the <code>TextListModel</code> interface.
 *
 * @version 	12 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class DefaultTextListModel
	implements TextListModel, java.io.Serializable
{
    /** The text list of the text list model. */
    protected TextList textList;

    /** The number of columns. */
    protected int columns;

    /** True if the model is used in table. */
    protected boolean isTable;

    /** The selected indexes of the text list model. */
    protected int selected[];

    /** The list model listeners of the text list model. */
    transient protected Vector listModelListeners;


    /**
     * Constructs a one column text list model with the default rich text
     * style.
     */
    public DefaultTextListModel() {
	this(RichTextStyle.DEFAULT_LIST_STYLE);
    }

    /**
     * Constructs a one column text list model with the specified rich text
     * style.
     *
     * @param richTextStyle the rich text style.
     */
    public DefaultTextListModel(RichTextStyle richTextStyle) {
	if (!richTextStyle.isListSeparator())
	    throw new IllegalArgumentException("improper separator");
	textList = new TList(new RichText(richTextStyle), new int[0]);
	textList.validate();
	columns = 1;
	isTable = false;
	selected = new int[0];
	listModelListeners = null;
    }


    /**
     * Constructs a text list model with the specified column widths.
     *
     * @param colWidths the column widths.
     */
    public DefaultTextListModel(int colWidths[]) {
	this(colWidths.length, colWidths);
    }

    /**
     * Constructs a text list model with the specified number of columns
     * and column widths.
     *
     * @param columns   the number of columns.
     * @param colWidths the column widths.
     */
    public DefaultTextListModel(int columns, int colWidths[]) {
	this(columns, colWidths, RichTextStyle.DEFAULT_LIST_STYLE);
    }

    /**
     * Constructs a text list model with the specified number of columns,
     * column widths, and rich text style.
     *
     * @param columns       the number of columns.
     * @param colWidths     the column widths.
     * @param richTextStyle the rich text style.
     */
    public DefaultTextListModel(int columns, int colWidths[],
				RichTextStyle richTextStyle)
    {
	if (columns < 1)
	    throw new IllegalArgumentException("improper columns: " + columns);
	if (colWidths.length != columns)
	    throw new IllegalArgumentException("invalid widths length");
	if (!richTextStyle.isListSeparator())
	    throw new IllegalArgumentException("improper separator");
	textList = new TList(new RichText(richTextStyle), colWidths);
	textList.validate();
	this.columns = columns;
	isTable = true;
	selected = new int[0];
	listModelListeners = null;
    }


    /**
     * Returns the text list of this text list model.
     *
     * @return the text list.
     */
    public TextList getTextList() {
	return textList;
    }

    /**
     * Sets the text style of this text list model.
     *
     * @param textStyle the text style.
     */
    public void setTextStyle(TextStyle textStyle) {
	if (textStyle == null)
	    return;

	RichText richText = textList.getRichText();
	TextChange change = richText.setBaseTextStyle(textStyle);
	TextLayoutChange layoutChange = textList.updateLayout(change);

	notifyListModelListeners(new TextListModelEvent(
					this,
					ListModelEvent.LIST_MODEL_REPLACED,
					0, getItemCount(), 0,
					layoutChange));
    }

    /**
     * Adds the specified list model listener to receive list model events
     * from this text list model.
     *
     * @param listener the list model listener.
     */
    public void addListModelListener(ListModelListener listener) {
	if (listener == null)
	    return;
	if (listModelListeners == null)
	    listModelListeners = new Vector();
	listModelListeners.addElement(listener);
    }

    /**
     * Removes the specified list model listener so it no longer receives
     * list model events from this text list model.
     *
     * @param listener the list model listener.
     */
    public void removeListModelListener(ListModelListener listener) {
	if (listModelListeners == null)
	    return;
	listModelListeners.removeElement(listener);
	if (listModelListeners.size() == 0)
	    listModelListeners = null;
    }

    /** Notifies the specified list model event to the list model listeners. */
    protected void notifyListModelListeners(ListModelEvent event) {
	if (listModelListeners == null)
	    return;
	for (Enumeration e = listModelListeners.elements();
	     e.hasMoreElements();
	     )
	{
	    ((ListModelListener)e.nextElement()).listModelChanged(event);
	}
    }

    /**
     * Returns the number of columns in the list.
     */
    public int getColumnCount() {
	return columns;
    }

    /**
     * Returns the column widths of the list.
     */
    public int[] getColumnWidths() {
	if (!isTable) {
	    return new int[]{ textList.getSize().width };
	}
	else {
	    return textList.getColumnWidths();
	}
    }

    /**
     * Sets the column widths of the list to the specified widths.
     *
     * @param colWidths the column widths.
     */
    public synchronized void setColumnWidths(int colWidths[]) {
	if (!isTable) {
	    return;
	}

	if (colWidths.length != columns) {
	    throw new IllegalArgumentException("invalid widths length");
	}

	int oldWidths[] = textList.getColumnWidths();
	int oldWidth = 0;
	for (int i = 0; i < oldWidths.length; i++) {
	    oldWidth += oldWidths[i];
	}
	int width = 0;
	for (int i = 0; i < colWidths.length; i++) {
	    width += colWidths[i];
	}

	textList.setColumnWidths(colWidths);

	if (oldWidths.length != colWidths.length) {
	    int begin = 0;
	    int end = textList.getRichText().length();
	    TextChange change = new TextChange(begin, end, begin, end,
					       0, false, false);
	    TextLayoutChange layoutChange = textList.updateLayout(change);

	    notifyListModelListeners(new TextListModelEvent(
					this,
					ListModelEvent.LIST_MODEL_REPLACED,
					0, getItemCount(), 0,
					layoutChange));
	}

	if (oldWidth != width) {
	    ((TList)textList).setLayoutWidth(width);
	}

	TextLayoutChange layoutChange =
		new TextLayoutChange(
				textList.getTextPositionAt(0),
				textList.getTextPositionAt(
					textList.getRichText().length()),
				true, true, 0, 0);
	notifyListModelListeners(new TextListModelEvent(
					this,
					ListModelEvent.LIST_MODEL_REPLACED,
					0, getItemCount(), 0,
					layoutChange));
    }

    /**
     * Returns the number of items in the list.
     */
    public int getItemCount() {
	return (textList.getRichText().isEmpty() ? 0 : textList.getLineCount());
    }

    /**
     * Returns the item associated with the specified index (row) and column.
     *
     * @param index  the row position of the item.
     * @param column the column position of the item.
     * @return an item that is associated with the specified index and column.
     */
    public Object getItem(int index, int column) {
	if (!isTable) {
	    return getRowText(index);
	}
	else {
	    Text texts[] = (Text[])getRowItems(index);
	    return texts[column];
	}
    }

    /**
     * Sets the item associated with the specified index (row) and column
     * to the specified value.
     *
     * @param index  the row position of the item.
     * @param column the column position of the item.
     * @param value  the new value.
     */
    public synchronized void setItem(int index, int column, Object value) {
	if (value == null)
	    throw new NullPointerException();
	Text text;
	if (value instanceof String) {
	    TextStyle style = textList.getRichTextStyle().getTextStyle();
	    text = new Text((String)value, style);
	}
	else if (value instanceof Text) {
	    text = (Text)value;
	}
	else {
	    throw new IllegalArgumentException("improper value");
	}
	if (!isTable) {
	    replaceItems(index, index + 1, new Text[]{ text });
	}
	else {
	    Text texts[] = (Text[])getRowItems(index);
	    texts[column] = text;
	    TextBuffer buffer = new TextBuffer();
	    for (int i = 0; i < columns; i++) {
		buffer.append(texts[i]).append(Text.LIST_COL_SEPARATOR_CHAR);
	    }
	    replaceItems(index, index + 1, new Text[]{ buffer.toText() });
	}
    }

    /**
     * Returns the items associated with the specified row index.
     *
     * @param index the row position of the items.
     * @return an item that is associated with the specified row index.
     */
    public Object[] getRowItems(int index) {
	Text text = getRowText(index);
	Text texts[] = new Text[columns];
	int begin = 0;
	for (int i = 0; i < columns; i++) {
	    int end = text.indexOf(Text.LIST_COL_SEPARATOR_CHAR, begin);
	    if (end >= 0) {
		texts[i] = text.subtext(begin, end);
		begin = end + 1;
	    }
	    else {
		end = text.length();
		texts[i] = text.subtext(begin, end);
		begin = end;
	    }
	}
	return texts;
    }

    /**
     * Returns the items in the specified column index.
     *
     * @param column the column position of the items.
     * @return items in the specified column.
     */
    public Object[] getItems(int column) {
	int itemCount = getItemCount();
	Text texts[] = new Text[itemCount];
	for (int i = 0; i < itemCount; i++) {
	    texts[i] = (Text)getItem(column, i);
	}
	return texts;
    }

    /**
     * Replaces the items in the specified range with the specified items.
     *
     * @param begin  the beginning index to replace, inclusive.
     * @param end    the ending index to replace, exclusive.
     * @param items  the replacement row by column items.
     */
    public synchronized void replaceItems(int begin, int end, Object items[][])
    {
	if (items == null)
	    throw new NullPointerException();
	int itemCount = getItemCount();
	if (begin < 0 || end > itemCount || begin > end) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	int rows = items.length;
	for (int i = 0; i < rows; i++) {
	    if (items[i].length != columns) {
		throw new IllegalArgumentException("number of columns is invalid");
	    }
	}

	if (begin == end && rows == 0)
	    return;

	String strings[][] = null;
	Text texts[][]     = null;
	if (items instanceof String[][]) {
	    strings = (String[][])items;
	}
	else if (items instanceof Text[][]) {
	    texts = (Text[][])items;
	}
	else {
	    throw new IllegalArgumentException("improper items");
	}

	TextStyle style = textList.getRichTextStyle().getTextStyle();
	Text rowTexts[] = new Text[rows];
	if (!isTable) {
	    for (int i = 0; i < rows; i++) {
		if (strings != null) {
		    rowTexts[i] = new Text(strings[i][0], style);
		}
		else {
		    rowTexts[i] = texts[i][0];
		}
	    }
	}
	else {
	    for (int i = 0; i < rows; i++) {
		TextBuffer buffer = new TextBuffer();
		buffer.setTextStyle(style);
		if (strings != null) {
		    for (int c = 0; c < columns; c++) {
			buffer.append(strings[i][c]);
			buffer.append(Text.LIST_COL_SEPARATOR_CHAR);
		    }
		}
		else {
		    for (int c = 0; c < columns; c++) {
			buffer.append(texts[i][c]);
			buffer.append(Text.LIST_COL_SEPARATOR_CHAR);
		    }
		}
		rowTexts[i] = buffer.toText();
	    }
	}

	replaceItems(begin, end, rowTexts);
    }

    /**
     * Returns the number of selected rows.
     */
    public int getSelectedCount() {
	return selected.length;
    }

    /**
     * Checks if the row at the specified index is selected.
     *
     * @param index the row position to be checked.
     * @return <code>true</code> if the specified row has been selected;
     *         <code>false</code> otherwise.
     */
    public boolean isIndexSelected(int index) {
	if (index < 0 || index >= getItemCount())
	    return false;
	int len = selected.length;
	for (int i = 0; i < len; i++) {
	    if (selected[i] == index)
		return true;
	}
	return false;
    }

    /**
     * Returns the indices of the selected rows.
     *
     * @return an array of the indices of the selected rows.
     */
    public int[] getSelectedIndexes() {
	return selected;
    }

    /**
     * Changes the selection to be the specified range.
     *
     * @param start the starting index to select, inclusive.
     * @param end   the ending index to select, inclusive.
     * @return an array of the indices of the added items and an array of
     *         the indices of removed items, or <code>null</code> if the
     *         selection has not been changed.
     */
    public synchronized int[][] setSelection(int start, int end) {
	if (start < 0 || end >= getItemCount() || start > end)
	    return null;
	int len = selected.length;
	VArray vselected = createSelectedVArray();
	VArray vadded = new VArray(int.class);
	VArray vremoved = new VArray(int.class);
	for (int si = 0; si < len; si++) {
	    int i = vselected.getInt(si);
	    if (i < start || i > end) {
		vselected.remove(si, 1);
		vremoved.append(i);
		--si;
		--len;
	    }
	}
	for (int i = start; i <= end; i++) {
	    if (vselected.indexOf(i) < 0) {
		vselected.append(i);
		vadded.append(i);
	    }
	}
	vselected.sort();
	selected = (int[])vselected.getTrimmedArray();

	if (vadded.isEmpty() && vremoved.isEmpty())
	    return null;
	vadded.sort();
	vremoved.sort();
	int added[]   = (int[])vadded.getTrimmedArray();
	int removed[] = (int[])vremoved.getTrimmedArray();
	notifyListModelListeners(new ListModelEvent(
				    this,
				    ListModelEvent.LIST_MODEL_SELECTION_CHANGED,
				    added, removed));
	return new int[][]{ added, removed };
    }

    /**
     * Changes the selection to be the set union of the current selection
     * and indices in the specified range.
     *
     * @param start the starting index to select, inclusive.
     * @param end   the ending index to select, inclusive.
     * @return an array of the indices of the added items, or
     *         <code>null</code> if the selection has not been changed.
     */
    public synchronized int[] addSelection(int start, int end) {
	if (start < 0 || end >= getItemCount() || start > end)
	    return null;
	VArray vselected = createSelectedVArray();
	VArray vadded = new VArray(int.class);
	for (int i = start; i <= end; i++) {
	    if (vselected.indexOf(i) < 0) {
		vselected.append(i);
		vadded.append(i);
	    }
	}
	vselected.sort();
	selected = (int[])vselected.getTrimmedArray();

	if (vadded.isEmpty())
	    return null;
	vadded.sort();
	int added[] = (int[])vadded.getTrimmedArray();
	notifyListModelListeners(new ListModelEvent(
				    this,
				    ListModelEvent.LIST_MODEL_SELECTION_CHANGED,
				    added, new int[0]));
	return added;
    }

    /**
     * Changes the selection to be the set difference of the current
     * selection and indices in the specified range.
     *
     * @param start the starting index to select, inclusive.
     * @param end   the ending index to select, inclusive.
     * @return an array of the indices of the removed items, or
     *         <code>null</code> if the selection has not been changed.
     */
    public synchronized int[] removeSelection(int start, int end) {
	if (start < 0 || end >= getItemCount() || start > end)
	    return null;
	VArray vselected = createSelectedVArray();
	VArray vremoved = new VArray(int.class);
	for (int i = start; i <= end; i++) {
	    int si = vselected.indexOf(i);
	    if (si >= 0) {
		vselected.remove(si, 1);
		vremoved.append(i);
	    }
	}
	vselected.sort();
	selected = (int[])vselected.getTrimmedArray();

	if (vremoved.isEmpty())
	    return null;
	vremoved.sort();
	int removed[] = (int[])vremoved.getTrimmedArray();
	notifyListModelListeners(new ListModelEvent(
				    this,
				    ListModelEvent.LIST_MODEL_SELECTION_CHANGED,
				    new int[0], removed));
	return removed;
    }


    /**
     * Returns the text associated with the specified row index.
     */
    protected Text getRowText(int index) {
	TextLineInfo lineInfo = textList.getTextLineAt(index);
	if (lineInfo == null || lineInfo.lineBegin == lineInfo.lineEnd)
	    return new Text();
	RichText rtext = textList.getRichText();
	if (lineInfo.lineEnd >= rtext.length())
	    return rtext.getText().subtext(lineInfo.lineBegin,
					   lineInfo.lineEnd);
	else
	    return rtext.getText().subtext(lineInfo.lineBegin,
					   lineInfo.lineEnd - 1);
						// trim last separator
    }

    /**
     * Replaces the items in the specified range with the specified row items.
     *
     * @param begin    the beginning index to replace, inclusive.
     * @param end      the ending index to replace, exclusive.
     * @param items the replacement row items.
     */
    protected synchronized void replaceItems(int begin, int end, Text items[])
    {
	int itemCount = getItemCount();
	if (begin < 0 || end > itemCount || begin > end) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	if (begin == end && items.length == 0)
	    return;

	TextBuffer buffer = new TextBuffer();

	// append to last
	if (begin > 0 && begin == itemCount && items.length > 0) {
	    buffer.append(Text.LIST_SEPARATOR_CHAR);
	}

	int limit = items.length - 1;
	int i;
	for (i = 0; i < limit; i++) {
	    buffer.append(items[i]).append(Text.LIST_SEPARATOR_CHAR);
	}
	if (i == limit) {
	    if (end == itemCount) {
		buffer.append(items[i]);
	    }
	    else {
		buffer.append(items[i]).append(Text.LIST_SEPARATOR_CHAR);
	    }
	}
	TextPositionInfo bPos, ePos;
	if (itemCount == 0) {
	    bPos = ePos = textList.getTextPositionAt(0);
	}
	else {
	    if (begin == itemCount) {
		bPos = textList.getTextPositionAt(
					textList.getRichText().length());
	    }
	    else {
		bPos = textList.getTextPositionAtLineBegin(begin);
	    }

	    if (items.length == 0 && end == itemCount) { // remove to last
		// trim last Text.LIST_SEPARATOR_CHAR
		if (bPos.textIndex > 0) {
		    bPos = textList.getTextPositionPrevTo(bPos);
		}
	    }

	    if (end == itemCount) {
		if (begin == itemCount) {
		    ePos = bPos;
		}
		else {
		    ePos = textList.getTextPositionAt(
					textList.getRichText().length());
		}
	    }
	    else {
		ePos = textList.getTextPositionAtLineBegin(end);
	    }
	}
	TextLayoutChange change = textList.replace(bPos, ePos, buffer.toText());

	if (end - begin != items.length) {
	    int diff = items.length - (end - begin);
	    VArray vselected = new VArray(int.class);
	    int len = selected.length;
	    for (i = 0; i < len; i++) {
		int v = selected[i];
		if (v < begin) {
		    vselected.append(v);
		}
		else if (v >= end) {
		    vselected.append(v + diff);
		}
	    }
	    vselected.sort();
	    selected = (int[])vselected.getTrimmedArray();
	}

	notifyListModelListeners(new TextListModelEvent(
					this,
					ListModelEvent.LIST_MODEL_REPLACED,
					begin, end,
					items.length - (end - begin),
					change));
    }

    protected VArray createSelectedVArray() {
	int array[] = new int[selected.length];
	System.arraycopy(selected, 0, array, 0, selected.length);
	return new VArray(array);
    }


    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	if (listModelListeners != null) {
	    for (Enumeration e = listModelListeners.elements();
		 e.hasMoreElements();
		 )
	    {
		ListModelListener l = (ListModelListener)e.nextElement();
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
	    addListModelListener((ListModelListener)listenerOrNull);
	}
    }
}


/**
 * The TextList object whose layoutWidth is accessible.
 */
class TList extends TextList {

    TList(RichText richText, int colWidths[]) {
	super(richText, colWidths);
    }

    TList(RichText richText, int colWidths[], Locale locale) {
	super(richText, colWidths, locale);
    }

    void setLayoutWidth(int layoutWidth) {
	this.layoutWidth = layoutWidth;
    }
}
