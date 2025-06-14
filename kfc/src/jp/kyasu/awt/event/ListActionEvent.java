/*
 * ListActionEvent.java
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

package jp.kyasu.awt.event;

import java.awt.event.ActionEvent;

/**
 * The List actin event that is originated from a <code>List</code>.
 *
 * @see 	jp.kyasu.awt.List
 * @see 	jp.kyasu.awt.TableList
 *
 * @version 	23 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class ListActionEvent extends ActionEvent {
    /** The item of the list. */
    protected Object item;

    /** the row index. */
    protected int row;

    /** the column index. */
    protected int column;

    /** True, if the column button is pressed. */
    protected boolean buttonPressed;


    /**
     * Constructs a ListActionEvent object with the specified source,
     * command string, item, row index, and column index.
     *
     * @param source  the object where the event originated.
     * @param command the command string.
     * @param item    the item.
     * @param row     the row index.
     * @param column  the column index.
     */
    public ListActionEvent(Object source, String command, Object item,
			   int row, int column)
    {
	this(source, command, item, row, column, false);
    }

    /**
     * Constructs a ListActionEvent object with the specified source,
     * command string, item, row index, column index, and the boolean that
     * indicates the column button is pressed.
     *
     * @param source        the object where the event originated.
     * @param command       the command string.
     * @param item          the item.
     * @param row           the row index.
     * @param column        the column index.
     * @param buttonPressed the column button is pressed.
     */
    public ListActionEvent(Object source, String command, Object item,
			   int row, int column, boolean buttonPressed)
    {
        this(source, ACTION_PERFORMED, command, item, row, column,
	     buttonPressed);
    }

    /**
     * Constructs a ListActionEvent object with the specified source, id,
     * command string, item, row index, column index, and the boolean that
     * indicates the column button is pressed.
     *
     * @param source        the object where the event originated.
     * @param id            the event type.
     * @param command       the command string.
     * @param item          the item.
     * @param row           the row index.
     * @param column        the column index.
     * @param buttonPressed the column button is pressed.
     */
    public ListActionEvent(Object source, int id, String command, Object item,
			   int row, int column, boolean buttonPressed)
    {
        super(source, id, command);
	this.item   = item;
        this.row    = row;
        this.column = column;
	this.buttonPressed = buttonPressed;
    }


    /**
     * Returns the item of this event.
     */
    public Object getItem() {
        return item;
    }

    /**
     * Returns the row index of this event.
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column index of this event.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns true if the column button is pressed.
     */
    public boolean isButtonPressed() {
        return buttonPressed;
    }

    public String paramString() {
	return super.paramString()
		+ ",item="          + item
		+ ",row="           + row
		+ ",column="        + column
		+ ",buttonPressed=" + buttonPressed;
    }
}
