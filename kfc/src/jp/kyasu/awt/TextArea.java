/*
 * TextArea.java
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

/**
 * A TextArea object is a multi-line area that displays text. It can
 * be set to allow editing or read-only modes.
 *
 * @version 	29 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class TextArea extends TextComponent {

    /**
     * Constructs a new text area.
     * This text area is created with vertical bar.
     */
    public TextArea() {
	this("", 0, 0, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text area with the specified string.
     * This text area is created with vertical scroll bar.
     * @param string the string to be displayed.
     */
    public TextArea(String string) {
	this(string, 0, 0, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text area with the specified number of rows and columns.
     * This text area is created with vertical scroll bar.
     * @param rows    the number of rows
     * @param columns the number of columns.
     */
    public TextArea(int rows, int columns) {
	this("", rows, columns, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text area with the specified string, and with the
     * specified number of rows and columns.
     * This text area is created with vertical scroll bar.
     * @param string  the string to be displayed.
     * @param rows    the number of rows.
     * @param columns the number of columns.
     */
    public TextArea(String string, int rows, int columns) {
	this(string, rows, columns, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text area with the specified text, and with the
     * rows, columns, and scroll bar visibility.
     * @param string     the string to be displayed.
     * @param rows       the number of rows.
     * @param columns    the number of columns.
     * @param scrollbars a constant that determines what scrollbars are created
     *                   to view the text area.
     */
    public TextArea(String string, int rows, int columns, int scrollbars) {
	super(string, scrollbars);
	this.rows    = rows;
	this.columns = columns;
    }

    /**
     * Constructs a new text area with the specified rich text.
     * This text area is created with vertical scroll bar.
     * @param richText the rich text to be displayed.
     */
    public TextArea(RichText richText) {
	this(richText, 0, 0, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text area with the specified rich text, and with the
     * specified number of rows and columns.
     * This text area is created with vertical scroll bar.
     * @param richText the rich text to be displayed.
     * @param rows     the number of rows.
     * @param columns  the number of columns.
     */
    public TextArea(RichText richText, int rows, int columns) {
	this(richText, rows, columns, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text area with the specified rich text, and with the
     * rows, columns, and scroll bar visibility.
     * @param richText   the rich text to be displayed.
     * @param rows       the number of rows.
     * @param columns    the number of columns.
     * @param scrollbars a constant that determines what scrollbars are created
     *                   to view the text area.
     */
    public TextArea(RichText richText, int rows, int columns, int scrollbars) {
	this(new DefaultTextEditModel(richText), rows, columns, scrollbars);
    }

    /**
     * Constructs a new text area with the specified model.
     * This text area is created with vertical scroll bar.
     * @param model the text edit model.
     */
    public TextArea(TextEditModel model) {
	this(model, 0, 0, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text area with the specified model, and with the
     * specified number of rows and columns.
     * This text area is created with vertical scroll bar.
     * @param model   the text edit model.
     * @param rows    the number of rows.
     * @param columns the number of columns.
     */
    public TextArea(TextEditModel model, int rows, int columns) {
	this(model, rows, columns, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text area with the specified model, and with the
     * rows, columns, and scroll bar visibility.
     * @param model      the text edit model.
     * @param rows       the number of rows.
     * @param columns    the number of columns.
     * @param scrollbars a constant that determines what scrollbars are created
     *                   to view the text area.
     */
    public TextArea(TextEditModel model, int rows, int columns, int scrollbars)
    {
	super(model, scrollbars);
	this.rows    = rows;
	this.columns = columns;
    }
}
