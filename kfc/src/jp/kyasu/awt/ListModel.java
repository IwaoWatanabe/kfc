/*
 * ListModel.java
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

import jp.kyasu.awt.event.ListModelListener;

/**
 * The model interface for an object that acts as a list model.
 *
 * @see 	jp.kyasu.awt.event.ListModelEvent
 * @see 	jp.kyasu.awt.event.ListModelListener
 *
 * @version 	18 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public interface ListModel {

    /**
     * Adds the specified list model listener to receive list model events
     * from this list model.
     *
     * @param listener the list model listener.
     */
    public void addListModelListener(ListModelListener listener);

    /**
     * Removes the specified list model listener so it no longer receives
     * list model events from this list model.
     *
     * @param listener the list model listener.
     */
    public void removeListModelListener(ListModelListener listener);

    /**
     * Returns the number of columns in the list.
     */
    public int getColumnCount();

    /**
     * Returns the column widths of the list.
     */
    public int[] getColumnWidths();

    /**
     * Sets the column widths of the list to the specified widths.
     *
     * @param colWidths the column widths.
     */
    public void setColumnWidths(int colWidths[]);

    /**
     * Returns the number of items in the list.
     */
    public int getItemCount();

    /**
     * Returns the item associated with the specified index (row) and column.
     *
     * @param index  the row position of the item.
     * @param column the column position of the item.
     * @return an item that is associated with the specified index and column.
     */
    public Object getItem(int index, int column);

    /**
     * Sets the item associated with the specified index (row) and column
     * to the specified value.
     *
     * @param index  the row position of the item.
     * @param column the column position of the item.
     * @param value  the new value.
     */
    public void setItem(int index, int column, Object value);

    /**
     * Returns the items associated with the specified row index.
     *
     * @param index the row position of the items.
     * @return an item that is associated with the specified row index.
     */
    public Object[] getRowItems(int index);

    /**
     * Returns the items associated with the specified column index.
     *
     * @param column the column position of the items.
     * @return an item that is associated with the specified column.
     */
    public Object[] getItems(int column);

    /**
     * Replaces the items in the specified range with the specified items.
     *
     * @param begin  the beginning index to replace, inclusive.
     * @param end    the ending index to replace, exclusive.
     * @param items  the replacement row by column items.
     */
    public void replaceItems(int begin, int end, Object items[][]);

    /**
     * Returns the number of selected rows.
     */
    public int getSelectedCount();

    /**
     * Checks if the row at the specified index is selected.
     *
     * @param index the row position to be checked.
     * @return <code>true</code> if the specified row has been selected;
     *         <code>false</code> otherwise.
     */
    public boolean isIndexSelected(int index);

    /**
     * Returns the indices of the selected rows.
     *
     * @return an array of the indices of the selected rows.
     */
    public int[] getSelectedIndexes();

    /**
     * Changes the selection to be the specified range.
     *
     * @param start the starting index to select, inclusive.
     * @param end   the ending index to select, inclusive.
     * @return an array of the indices of the added items and an array of
     *         the indices of removed items, or <code>null</code> if the
     *         selection has not been changed.
     */
    public int[][] setSelection(int start, int end);

    /**
     * Changes the selection to be the set union of the current selection
     * and indices in the specified range.
     *
     * @param start the starting index to select, inclusive.
     * @param end   the ending index to select, inclusive.
     * @return an array of the indices of the added items, or
     *         <code>null</code> if the selection has not been changed.
     */
    public int[] addSelection(int start, int end);

    /**
     * Changes the selection to be the set difference of the current
     * selection and indices in the specified range.
     *
     * @param start the starting index to select, inclusive.
     * @param end   the ending index to select, inclusive.
     * @return an array of the indices of the removed items, or
     *         <code>null</code> if the selection has not been changed.
     */
    public int[] removeSelection(int start, int end);
}
