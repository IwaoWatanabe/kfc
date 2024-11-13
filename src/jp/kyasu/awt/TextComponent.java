/*
 * TextComponent.java
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

import jp.kyasu.awt.text.KeyAction;
import jp.kyasu.awt.text.Keymap;
import jp.kyasu.awt.text.KeyBinding;
import jp.kyasu.awt.text.TextCaret;
import jp.kyasu.awt.text.TextEditController;
import jp.kyasu.awt.text.TextEditView;
import jp.kyasu.awt.event.TextPositionEvent;
import jp.kyasu.awt.event.TextPositionListener;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.ParagraphStyleModifier;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextLayout;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.TextStyleModifier;
import jp.kyasu.graphics.V3DBorder;
import jp.kyasu.graphics.VBorder;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.event.KeyEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A TextComponent is a component that allows the editing of some text.
 * <p>
 * A TextComponent is an MVC-based component. The <i>model</i> of the
 * TextComponent is a <code>TextEditModel</code> object, the <i>view</i> of
 * the TextComponent is a <code>TextEditView</code> object, and the
 * <i>controller</i> of the TextComponent is a
 * <code>TextEditController</code> object.
 * <p>
 * The principal editing operations on a <code>TextComponent</code> are
 * the <code>replaceRange (replaceSelection)</code>,
 * <code>setRangeTextStyle (setSelectionTextStyle)</code>,
 * <code>modifyRangeTextStyle (modifySelectionTextStyle)</code>,
 * <code>setRangeParagraphStyle (setSelectionParagraphStyle)</code> and
 * <code>modifyRangeParagraphStyle (modifySelectionParagraphStyle)</code>
 * methods:
 * <ul>
 * <li>The <code>replaceRange (replaceSelection)</code> method replaces the
 *     specified range (selection) of the text (model) with the specified
 *     replacement text.
 * <li>The <code>setRangeTextStyle (setSelectionTextStyle)</code> method
 *     sets the text style in the specified range (selection) of the text
 *     (model) to be the specified text style.
 * <li>The <code>>modifyRangeTextStyle (modifySelectionTextStyle)</code>
 *     method modifies the text style in the specified range (selection) of
 *     the text (model) by using the specified <code>TextStyleModifier</code>
 *     object.
 * <li>The <code>setRangeParagraphStyle (setSelectionParagraphStyle)</code>
 *     method sets the paragraph style at the specified range (selection)
 *     of the text (model) to be the specified paragraph style.
 * <li>The <code>modifyRangeParagraphStyle
 *     (modifySelectionParagraphStyle)</code> method modifies the paragraph
 *     style at the specified range (selection) of the text (model) by using
 *     the specified <code>ParagraphStyleModifier</code> object.
 * </ul>
 * <p>
 * An application should use the above editing operations and should not
 * edit the text of the TextComponent directly.
 *
 * @see 	jp.kyasu.awt.TextModel
 * @see 	jp.kyasu.awt.TextEditModel
 * @see 	jp.kyasu.awt.text.TextEditView
 * @see 	jp.kyasu.awt.text.BasicTextEditController
 * @see 	jp.kyasu.awt.text.TextEditController
 *
 * @version 	25 Jul 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextComponent extends EventProxyContainer
    implements TextListener, TextPositionListener
{
    protected TextEditModel editModel;
    protected TextEditView editView;
    protected TextEditController editController;
    protected int rows;
    protected int columns;
    transient protected TextListener textListener;
    transient protected Vector textPositionListeners;

    protected Color savedForegroundColor;
    protected Color savedBackgroundColor;
    protected Color savedSelectionForegroundColor;
    protected Color savedSelectionBackgroundColor;

    /**
     * Create and display both vertical and horizontal scrollbars.
     */
    static public final int SCROLLBARS_BOTH =
				java.awt.TextArea.SCROLLBARS_BOTH;

    /**
     * Create and display vertical scrollbar only.
     */
    static public final int SCROLLBARS_VERTICAL_ONLY =
				java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY;

    /**
     * Create and display horizontal scrollbar only.
     */
    static public final int SCROLLBARS_HORIZONTAL_ONLY =
				java.awt.TextArea.SCROLLBARS_HORIZONTAL_ONLY;

    /**
     * Do not create or display any scrollbars.
     */
    static public final int SCROLLBARS_NONE =
				java.awt.TextArea.SCROLLBARS_NONE;

    /**
     * The constant for the character line wrapping style.
     * The line is wrapped at the character boundary.
     */
    static public final int CHAR_WRAP = RichTextStyle.CHAR_WRAP;

    /**
     * The constant for the word line wrapping style.
     * The line is wrapped at the word boundary.
     */
    static public final int WORD_WRAP = RichTextStyle.WORD_WRAP;

    /**
     * The constant for the no line wrapping style.
     * The line is wrapped only at the line separator.
     */
    static public final int NO_WRAP   = RichTextStyle.NO_WRAP;

    /**
     * The default foreground color for the not editable state.
     */
    static public final Color NOT_EDITABLE_FOREGROUND =
	AWTResources.getResourceColor("kfc.text.notEditableForeground",
				      Color.black);

    /**
     * The default background color for the not editable state.
     */
    static public final Color NOT_EDITABLE_BACKGROUND =
	AWTResources.getResourceColor("kfc.text.notEditableBackground",
				      Color.lightGray);

    /**
     * The default selection foreground color for the not editable state.
     */
    static public final Color NOT_EDITABLE_SELECTION_FOREGROUND =
	AWTResources.getResourceColor("kfc.text.notEditableSelectionForeground",
				      Color.white);

    /**
     * The default selection background color for the not editable state.
     */
    static public final Color NOT_EDITABLE_SELECTION_BACKGROUND =
	AWTResources.getResourceColor("kfc.text.notEditableSelectionBackground",
				      new Color(0, 0, 128));

    /**
     * The default rich text style when the vertical scrollbar only.
     */
    static public final RichTextStyle DEFAULT_VERTICAL_STYLE =
					RichTextStyle.DEFAULT_CODE_STYLE;

    /**
     * The default rich text style when the horizontal scrollbar only.
     */
    static public final RichTextStyle DEFAULT_HORIZONTAL_STYLE =
	new RichTextStyle(
		RichTextStyle.NO_WRAP,
		RichTextStyle.JAVA_LINE_SEPARATOR,
		false,
		new TextStyle("Monospaced", Font.PLAIN, 12),
		new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 0, 0, 0));


    /**
     * Constructs a new text component with the specified string.
     * @param string the initial string that the component presents.
     */
    public TextComponent(String string) {
	this(string, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text component with the specified string and scroll
     * bar visibility.
     * @param string     the initial string that the component presents.
     * @param scrollbars a constant that determines what scrollbars are
     *                   created to view the text component.
     */
    public TextComponent(String string, int scrollbars) {
	this(string, scrollbars,
	     ((scrollbars == SCROLLBARS_BOTH ||
	       scrollbars == SCROLLBARS_HORIZONTAL_ONLY) ?
		DEFAULT_HORIZONTAL_STYLE : DEFAULT_VERTICAL_STYLE));
    }

    /**
     * Constructs a new text component with the specified string, scroll bar
     * visibility, and rich text style.
     * @param string     the initial string that the component presents.
     * @param scrollbars a constant that determines what scrollbars are
     *                   created to view the text component.
     * @param rtStyle    the rich text style.
     */
    public TextComponent(String string, int scrollbars, RichTextStyle rtStyle) {
	this(new RichText(new Text((string == null ?
					"" : Text.getJavaString(string)),
			 	   rtStyle.getTextStyle()),
			 rtStyle),
	     scrollbars);
    }

    /**
     * Constructs a new text component with the specified rich text.
     * @param richText the initial rich text that the component presents.
     */
    public TextComponent(RichText richText) {
	this(richText, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text component with the specified rich text and
     * scroll bar visibility.
     * @param richText   the initial rich text that the component presents.
     * @param scrollbars a constant that determines what scrollbars are
     *                   created to view the text component.
     */
    public TextComponent(RichText richText, int scrollbars) {
	this(new DefaultTextEditModel(richText), scrollbars);
    }

    /**
     * Constructs a new text component with the specified model.
     * @param textEditModel the initial model of the MVC component.
     */
    public TextComponent(TextEditModel textEditModel) {
	this(textEditModel, SCROLLBARS_VERTICAL_ONLY);
    }

    /**
     * Constructs a new text component with the specified model and scroll
     * bar visibility.
     * @param textEditModel the initial model of the MVC component.
     * @param scrollbars    a constant that determines what scrollbars are
     *                      created to view the text component.
     */
    public TextComponent(TextEditModel textEditModel, int scrollbars) {
	this(textEditModel, scrollbars, new V3DBorder(false));
    }

    /**
     * Constructs a new text component with the specified model, scroll bar
     * visibility, and border visual.
     * @param textEditModel the initial model of the MVC component.
     * @param scrollbars    a constant that determines what scrollbars are
     *                      created to view the text component.
     * @param border        the border visual of the text component.
     */
    public TextComponent(TextEditModel textEditModel, int scrollbars,
			 VBorder border)
    {
	rows    = 0;
	columns = 0;
	textListener = null;

	setLayout(new BorderLayout());
	add(createTextComponent(textEditModel, scrollbars, border),
	    BorderLayout.CENTER);

	super.setCursor(editView.getCursor());
    }

    /**
     * Constructs a text component.
     */
    protected TextComponent() {
	rows    = 0;
	columns = 0;
	textListener = null;
    }

    protected Component createTextComponent(TextEditModel textEditModel,
					    int scrollbars)
    {
	return createTextComponent(textEditModel, scrollbars,
				   new V3DBorder(false));
    }

    protected Component createTextComponent(TextEditModel textEditModel,
					    int scrollbars, VBorder border)
    {
	if (textEditModel == null)
	    throw new NullPointerException();
	ScrollPanel sp = new ScrollPanel(scrollbars,
					 ScrollPanel.SCROLLBARS_ALWAYS);

	editModel = textEditModel;
	editView = new TextEditView(editModel);
	editController = editView.getController();

	super.setFont(
	  editModel.getRichText().getRichTextStyle().getTextStyle().getFont());

	sp.add(editView);

	if (border == null) {
	    return sp;
	}
	else {
	    BorderedPanel bp = new BorderedPanel(border);
	    bp.add(sp, BorderLayout.CENTER);
	    return bp;
	}
    }


    /**
     * Gets the event source component.
     */
    protected Component getEventSource() {
	return editView;
    }

    /**
     * Adds the specified text event listener to recieve text events from
     * this text component.
     * @param l the text event listener.
     */
    public synchronized void addTextListener(TextListener l) {
	if (l == null)
	    return;
	if (textListener == null) {
	    editModel.addTextListener(this);
	}
	textListener = AWTEventMulticaster.add(textListener, l);
	enableEvents(0); // mark newEventsOnly
    }

    /**
     * Removes the specified text event listener so that it no longer
     * receives text events from this textcomponent
     * @param l the text event listener.
     */
    public synchronized void removeTextListener(TextListener l) {
	textListener = AWTEventMulticaster.remove(textListener, l);
	if (textListener == null) {
	    editModel.removeTextListener(this);
	}
    }

    /**
     * Invoked when the value of the text has changed.
     * @see java.awt.event.TextListener
     */
    public void textValueChanged(TextEvent e) {
	if (textListener != null) {
	    if (isDirectNotification()) {
		textListener.textValueChanged(new TextEvent(this, e.getID()));
	    }
	    else {
		e = new TextEvent(this,
				  e.getID()+java.awt.AWTEvent.RESERVED_ID_MAX);
		EventPoster.postEvent(e);
	    }
	}
    }

    /**
     * Adds the specified text position event listener to recieve text
     * position events from this text component.
     * @param l the text position event listener.
     */
    public synchronized void addTextPositionListener(TextPositionListener l) {
	if (l == null)
	    return;
	if (textPositionListeners == null) {
	    textPositionListeners = new Vector();
	    editView.addTextPositionListener(this);
	}
	textPositionListeners.addElement(l);
    }

    /**
     * Removes the specified text position event listener so that it no
     * longer receives text position events from this text component
     * @param l the text position event listener.
     */
    public synchronized void removeTextPositionListener(TextPositionListener l)
    {
	if (textPositionListeners == null)
	    return;
	textPositionListeners.removeElement(l);
	if (textPositionListeners.size() == 0) {
	    textPositionListeners = null;
	    editView.removeTextPositionListener(this);
	}
    }

    /**
     * Invoked when the position of the text has changed.
     * @see java.awt.event.TextPositionListener
     */
    public void textPositionChanged(TextPositionEvent te) {
	if (textPositionListeners != null) {
	    te = new TextPositionEvent(this, te.getID(),
				       te.getSelectionBegin(),
				       te.getSelectionEnd());
	    for (Enumeration e = textPositionListeners.elements();
		 e.hasMoreElements();
		 )
	    {
		((TextPositionListener)e.nextElement()).textPositionChanged(te);
	    }
	}
    }

    protected void processEvent(java.awt.AWTEvent e) {
	if (textListener != null && (e instanceof TextEvent)) {
	    TextEvent te = (TextEvent)e;
	    if (te.getID() > java.awt.AWTEvent.RESERVED_ID_MAX) {
		te = new TextEvent(
				te.getSource(),
				te.getID() - java.awt.AWTEvent.RESERVED_ID_MAX);
		textListener.textValueChanged(te);
		return;
	    }
	}
	super.processEvent(e);
    }

    /**
     * Sets the foreground color of this text component.
     */
    public synchronized void setForeground(Color c) {
	super.setForeground(c);
	if (editView != null) {
	    editView.setForeground(c);
	    if (isShowing()) {
		editView.repaintNow();
	    }
	}
    }

    /**
     * Sets the background color of this text component.
     */
    public synchronized void setBackground(Color c) {
	super.setBackground(c);
	if (editView != null) {
	    editView.setBackground(c);
	    if (isShowing()) {
		editView.repaintNow();
	    }
	}
    }

    /**
     * Sets the font of this text component.
     */
    public synchronized void setFont(Font font) {
	super.setFont(font);
	if (editView != null) {
	    editView.setFont(font);
	}
    }

    /**
     * Sets the cursor of this text component.
     */
    public synchronized void setCursor(Cursor c) {
	super.setCursor(c);
	if (editView != null) {
	    editView.setCursor(c);
	}
    }

    /**
     * Enables or disables this text component.
     */
    public synchronized void setEnabled(boolean b) {
	super.setEnabled(b);
	if (editView != null) {
	    editView.setEnabled(b);
	}
    }

    // ======== java.awt.TextComponent APIs ========

    /**
     * Sets the string that is presented by this text component to be the
     * specified string.
     * @param str the new string.
     * @see #getText()
     */
    public synchronized void setText(String str) {
	editController.setString(str);
    }

    /**
     * Returns the string that is presented by this text component.
     * @see #setText(java.lang.String)
     * @see #getText(java.lang.String)
     */
    public String getText() {
	return editController.getString();
    }

    /**
     * Returns the string that is presented by this text component.
     * @param separator the preferred line separator string.
     * @see #setText(java.lang.String)
     * @see #getText()
     */
    public String getText(String separator) {
	return editController.getString(separator);
    }

    /**
     * Returns the selected string from the string that is presented by
     * this text component.
     * @return the selected string of this text component.
     * @see #select(int, int)
     */
    public String getSelectedText() {
	return editController.getSelectedString();
    }

    /**
     * Indicates whether or not this text component is editable.
     * @return <code>true</code> if this text component is editable;
     *         <code>false</code> otherwise.
     * @see #setEditable(boolean)
     */
    public boolean isEditable() {
	return editController.isEditable();
    }

    /**
     * Sets the flag that determines whether or not this text component is
     * editable.
     * @param b a flag indicating whether this text component should be user
     *          editable.
     * @see #isEditable()
     * @see #setEditable(boolean, boolean)
     */
    public synchronized void setEditable(boolean b) {
	setEditable(b, false);
    }

    /**
     * Returns the start position of the selected text in this text component.
     * @return the start position of the selected text.
     * @see #setSelectionStart(int)
     * @see #getSelectionEnd()
     */
    public int getSelectionStart() {
	return editController.getSelectionStart();
    }

    /**
     * Sets the selection start for this text component to the specified
     * position.
     * @param selectionStart the start position of the selected text.
     * @see #getSelectionStart()
     * @see #setSelectionEnd(int)
     */
    public void setSelectionStart(int selectionStart) {
	editController.setSelectionStart(selectionStart);
    }

    /**
     * Returns the end position of the selected text in this text component.
     * @return the end position of the selected text.
     * @see #setSelectionEnd(int)
     * @see #getSelectionStart()
     */
    public int getSelectionEnd() {
	return editController.getSelectionEnd();
    }

    /**
     * Sets the selection end for this text component to the specified position.
     * @param selectionEnd the end position of the selected text.
     * @see #getSelectionEnd()
     * @see #setSelectionStart(int)
     */
    public void setSelectionEnd(int selectionEnd) {
	editController.setSelectionEnd(selectionEnd);
    }

    /**
     * Selects the text between the specified start and end positions.
     * @param selectionStart the start position of the text to select.
     * @param selectionEnd the end position of the text to select.
     * @see #setSelectionStart(int)
     * @see #setSelectionEnd(int)
     * @see #selectAll()
     */
    public synchronized void select(int selectionStart, int selectionEnd) {
	editController.select(selectionStart, selectionEnd);
    }

    /**
     * Selects all the text in this text component.
     * @see #select(int, int)
     */
    public void selectAll() {
	editController.selectAll();
    }

    /**
     * Sets the position of the text insertion caret for this text component.
     * @param position the position of the text insertion caret.
     * @see #getCaretPosition()
     */
    public synchronized void setCaretPosition(int position) {
	editController.setCaretPosition(position);
    }

    /**
     * Returns the position of the text insertion caret for this text component.
     * @return the position of the text insertion caret.
     * @see #setCaretPosition(int)
     */
    public int getCaretPosition() {
	return editController.getCaretPosition();
    }

    /**
     * Tests if the selection is caret, i.e., null selection.
     */
    public boolean selectionIsCaret() {
	return editController.selectionIsCaret();
    }


    // ======== java.awt.TextArea APIs ========

    /**
     * Inserts the specified string at the specified position in this
     * text component.
     * @param str the string to insert.
     * @param pos the position at which to insert.
     * @see #setText(java.lang.String)
     * @see #replaceRange(java.lang.String, int, int)
     * @see #append(java.lang.String)
     */
    public synchronized void insert(String str, int pos) {
	editController.insert(str, pos);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>insert(String, int)</code>.
     */
    public void insertText(String str, int pos) {
	insert(str, pos);
    }

    /**
     * Appends the given string to the text component's current text.
     * @param str the text to append.
     * @see #insert(java.lang.String, int)
     */
    public synchronized void append(String str) {
	editController.append(str);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>append(String)</code>.
     */
    public void appendText(String str) {
	append(str);
    }

    /**
     * Replaces text between the indicated start and end positions
     * with the specified replacement text.
     * @param str   the string to use as the replacement.
     * @param start the start position.
     * @param end   the end position.
     * @see #insert(java.lang.String, int)
     */
    public synchronized void replaceRange(String str, int start, int end) {
	editController.replaceRange(str, start, end);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>replaceRange(String, int, int)</code>.
     */
    public void replaceText(String str, int start, int end) {
	replaceRange(str, start, end);
    }

    /**
     * Returns the number of rows in the text component.
     * @return the number of rows in the text component.
     * @see #setRows(int)
     * @see #getColumns()
     */
    public int getRows() {
	return rows;
    }

    /**
     * Sets the number of rows for this text component.
     * @param rows the number of rows.
     * @see #getRows()
     * @see #setColumns(int)
     */
    public void setRows(int rows) {
	int oldVal = this.rows;
	if (rows < 0) {
	    throw new IllegalArgumentException("rows less than zero.");
	}
	if (rows != oldVal) {
	    this.rows = rows;
	    invalidate();
	}
    }

    /**
     * Returns the number of columns in this text component.
     * @return the number of columns in the text component.
     * @see #setColumns(int)
     * @see #getRows()
     */
    public int getColumns() {
	return columns;
    }

    /**
     * Sets the number of columns for this text component.
     * @param columns the number of columns.
     * @see #getColumns()
     * @see #setRows(int)
     */
    public void setColumns(int columns) {
	int oldVal = this.columns;
	if (columns < 0) {
	    throw new IllegalArgumentException("columns less than zero.");
	}
	if (columns != oldVal) {
	    this.columns = columns;
	    invalidate();
	}
    }

    /**
     * Returns an enumerated value that indicates which scroll bars
     * the text component uses.
     * <p>
     * @return an integer that indicates which scroll bars are used.
     * @see #SCROLLBARS_BOTH
     * @see #SCROLLBARS_VERTICAL_ONLY
     * @see #SCROLLBARS_HORIZONTAL_ONLY
     * @see #SCROLLBARS_NONE
     */
    public int getScrollbarVisibility() {
	ScrollPanel sp = (ScrollPanel)editView.getParent();
	return sp.getScrollbarVisibility();
    }

    /**
     * Determines the preferred size of a text component with the specified
     * number of rows and columns.
     * @param rows    the number of rows.
     * @param columns the number of columns.
     * @return the preferred dimensions required to display the text component
     *         with the specified number of rows and columns.
     */
    public Dimension getPreferredSize(int rows, int columns) {
	synchronized (getTreeLock()) {
	    if (rows <= 0) rows = 1;
	    if (columns <= 0) columns = 1;
	    Dimension d = editView.getPreferredSize(rows, columns);
	    ScrollPanel sp = (ScrollPanel)editView.getParent();
	    if (sp.vScrollbar != null && sp.vScrollbar.isVisible()) {
		d.width += sp.vScrollbar.getPreferredSize().width;
	    }
	    if (sp.hScrollbar != null && sp.hScrollbar.isVisible()) {
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
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getPreferredSize(int, int)</code>.
     */
    public Dimension preferredSize(int rows, int columns) {
	return getPreferredSize(rows, columns);
    }

    /**
     * Returns the preferred size of this text component.
     * @return the preferred dimensions needed for this text component.
     */
    public Dimension getPreferredSize() {
	return getPreferredSize((rows > 0    ? rows    : 20),
				(columns > 0 ? columns : 80));
    }

    /**
     * Returns the minimum size of a text component with the specified
     * number of rows and columns.
     * @param rows    the number of rows.
     * @param columns the number of columns.
     * @return the minimum dimensions required to display the text component
     *         with the specified number of rows and columns.
     */
    public Dimension getMinimumSize(int rows, int columns) {
    	return getPreferredSize(rows, columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize(int, int)</code>.
     */
    public Dimension minimumSize(int rows, int columns) {
    	return getMinimumSize(rows, columns);
    }

    /**
     * Returns the minimum size of this text component.
     * @return the preferred dimensions needed for this text component.
     */
    public Dimension getMinimumSize() {
	return getMinimumSize((rows > 0    ? rows    : 1),
			      (columns > 0 ? columns : 1));
    }

    // ================ Enhanced Basic APIs ================

    /**
     * Returns the model of this text component.
     */
    public TextEditModel getModel() {
	return editModel;
    }

    /**
     * Returns the view of this text component.
     */
    public TextEditView getView() {
	return editView;
    }

    /**
     * Returns the controller of this text component.
     */
    public TextEditController getController() {
	return editController;
    }

    /**
     * Returns the rich text of this text component.
     */
    public RichText getRichText() {
	//return editModel.getRichText();
	return editController.getRichText();
    }

    /**
     * Sets the rich text of this text component.
     */
    public synchronized void setRichText(RichText richText) {
	//editModel.setRichText(richText);
	editController.setRichText(richText);
    }

    /**
     * Returns the line wrapping style.
     * @see #setLineWrap(int)
     * @see #CHAR_WRAP
     * @see #WORD_WRAP
     * @see #NO_WRAP
     */
    public int getLineWrap() {
	return editView.getLineWrap();
    }

    /**
     * Sets the line wrapping style.
     *
     * @param lineWrap the line wrapping style.
     * @see #getLineWrap()
     * @see #CHAR_WRAP
     * @see #WORD_WRAP
     * @see #NO_WRAP
     */
    public synchronized void setLineWrap(int lineWrap) {
	editView.setLineWrap(lineWrap);
    }

    /**
     * Returns the location of the text of this text component.
     * @see #setLocationOfText(java.awt.Point)
     */
    public Point getLocationOfText() {
	return editView.getLocationOfText();
    }

    /**
     * Sets the location of the text of this text component.
     * @see #getLocationOfText()
     */
    public synchronized void setLocationOfText(Point p) {
	editView.setLocationOfText(p);
    }

    /**
     * Returns the selection foreground color.
     * @see #setSelectionForeground(java.awt.Color)
     */
    public Color getSelectionForeground() {
	return editView.getSelectionForeground();
    }

    /**
     * Sets the selection foreground color.
     * @see #getSelectionForeground()
     */
    public synchronized void setSelectionForeground(Color c) {
	editView.setSelectionForeground(c);
    }

    /**
     * Returns the selection background color.
     * @see #setSelectionBackground(java.awt.Color)
     */
    public Color getSelectionBackground() {
	return editView.getSelectionBackground();
    }

    /**
     * Sets the selection background color.
     * @see #getSelectionBackground()
     */
    public synchronized void setSelectionBackground(Color c) {
	editView.setSelectionBackground(c);
    }

    /**
     * Returns the caret color.
     * @see #setCaretColor(java.awt.Color)
     */
    public Color getCaretColor() {
	return editView.getCaretColor();
    }

    /**
     * Sets the caret color.
     * @see #getCaretColor()
     */
    public synchronized void setCaretColor(Color c) {
	editView.setCaretColor(c);
    }

    /**
     * Returns the text caret of this text component.
     * @see #setCaretColor(jp.kyasu.awt.text.TextCaret)
     */
    public TextCaret getTextCaret() {
	return editView.getTextCaret();
    }

    /**
     * Sets the text caret of this text component.
     * @see #getTextCaret()
     */
    public synchronized void setTextCaret(TextCaret textCaret) {
	editView.setTextCaret(textCaret);
    }

    /**
     * Returns the edit menu of this text component.
     */
    public Menu getEditMenu() {
	return editController.getEditMenu();
    }

    /**
     * Returns the popup menu of this text component.
     * @see #setPopupMenu(java.awt.PopupMenu)
     */
    public PopupMenu getPopupMenu() {
	return editController.getPopupMenu();
    }

    /**
     * Sets the popup menu of this text component.
     * @see #getPopupMenu()
     */
    public void setPopupMenu(PopupMenu menu) {
	editController.setPopupMenu(menu);
    }

    /**
     * Sets the flag that determines whether or not this text component is
     * editable.
     * @param b a flag indicating whether this text component should be user
     *          editable.
     * @param changeColor if true, change the color of the text component
     *                    according to the editable state.
     * @see #isEditable()
     * @see #setEditable(boolean)
     */
    public synchronized void setEditable(boolean b, boolean changeColor) {
	if (isEditable() == b)
	    return;
	editController.setEditable(b);

	if (!changeColor)
	    return;

	if (isEditable()) {
	    if (savedForegroundColor != null)
	    	editView.setForeground(savedForegroundColor);
	    if (savedBackgroundColor != null)
	    	editView.setBackground(savedBackgroundColor);
	    if (savedSelectionForegroundColor != null)
	    	editView.setSelectionForeground(savedSelectionForegroundColor);
	    if (savedSelectionBackgroundColor != null)
	    	editView.setSelectionBackground(savedSelectionBackgroundColor);
	    savedForegroundColor = null;
	    savedBackgroundColor = null;
	    savedSelectionForegroundColor = null;
	    savedSelectionBackgroundColor = null;
	}
	else {
	    savedForegroundColor = editView.getForeground();
	    savedBackgroundColor = editView.getBackground();
	    savedSelectionForegroundColor = editView.getSelectionForeground();
	    savedSelectionBackgroundColor = editView.getSelectionBackground();
	    editView.setForeground(NOT_EDITABLE_FOREGROUND);
	    editView.setBackground(NOT_EDITABLE_BACKGROUND);
	    editView.setSelectionForeground(NOT_EDITABLE_SELECTION_FOREGROUND);
	    editView.setSelectionBackground(NOT_EDITABLE_SELECTION_BACKGROUND);
	}
	if (isShowing()) {
	    editView.repaintNow();
	}
    }

    /**
     * Tests if the text component requests the focus when the mouse is clicked.
     *
     * @return <code>true</code> if the text component requests the focus when
     *         the mouse is clicked, <code>false</code> if the text component
     *         requests the focus when the mouse enters the view.
     * @see #isMouseFocus()
     */
    public boolean isClickToFocus() {
	return editController.isClickToFocus();
    }

    /**
     * Tests if the text component requests the focus when the mouse enters
     * the component.
     *
     * @see #isClickToFocus()
     */
    public boolean isMouseFocus() {
	return editController.isMouseFocus();
    }

    /**
     * Makes the text component request the focus when the mouse is clicked.
     *
     * @see #isClickToFocus()
     * @see #setMouseFocus()
     */
    public void setClickToFocus() {
	editController.setClickToFocus();
    }

    /**
     * Makes the text component request the focus when the mouse enters the
     * component.
     *
     * @see #isMouseFocus()
     * @see #setClickToFocus()
     */
    public void setMouseFocus() {
	editController.setMouseFocus();
    }

    /**
     * Returns the keymap of this text component.
     * @see #setKeymap(jp.kyasu.awt.text.Keymap)
     */
    public Keymap getKeymap() {
	return editController.getKeymap();
    }

    /**
     * Sets the keymap of this text component.
     * @see #getKeymap()
     */
    public synchronized void setKeymap(Keymap keymap) {
	editController.setKeymap(keymap);
    }

    /**
     * Returns the key binding of this text component.
     * @see #addKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyActionNamed(java.lang.String)
     */
    public KeyBinding getKeyBinding() {
	return editController.getKeyBinding();
    }

    /**
     * Returns the key action object associated with the specified name.
     * @param actionName the name of the key action.
     * @return the key action object, or <code>null</code> if no associated
     *         action exists.
     * @see #addKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyActionNamed(java.lang.String)
     */
    public KeyAction getKeyAction(String actionName) {
	return editController.getKeyAction(actionName);
    }

    /**
     * Adds the key action to the key binding of this text component.
     * @param keyAction the key action object.
     * @see #getKeyAction(java.lang.String)
     * @see #removeKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyActionNamed(java.lang.String)
     */
    public synchronized void addKeyAction(KeyAction keyAction) {
	editController.addKeyAction(keyAction);
    }

    /**
     * Removes the key action from the key binding of this text component.
     * @param keyAction the key action object.
     * @see #getKeyAction(java.lang.String)
     * @see #addKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyActionNamed(java.lang.String)
     */
    public synchronized void removeKeyAction(KeyAction keyAction) {
	editController.removeKeyAction(keyAction);
    }

    /**
     * Removes the key action named the specified name from the key binding
     * of this text component.
     * @param actionName the name of the key action.
     * @see #getKeyAction(java.lang.String)
     * @see #addKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyAction(jp.kyasu.awt.text.KeyAction)
     */
    public synchronized void removeKeyActionNamed(String actionName) {
	editController.removeKeyActionNamed(actionName);
    }

    /**
     * Performs the key action named the specified name.
     * @param actionName the name of the key action.
     * @see #performKeyAction(java.lang.String, char)
     */
    public void performKeyAction(String actionName) {
	editController.performKeyAction(actionName);
    }

    /**
     * Performs the key action named the specified name.
     * @param actionName the name of the key action.
     * @param keyChar    the key character for the key action.
     * @see #performKeyAction(java.lang.String)
     */
    public synchronized void performKeyAction(String actionName, char keyChar)
    {
	editController.performKeyAction(actionName, keyChar);
    }

    /**
     * Tests if this text component handles <code>ClickableTextAction</code>.
     * @see #setClickable(boolean)
     * @see jp.kyasu.graphics.ClickableTextAction
     */
    public boolean isClickable() {
	return editController.isClickable();
    }

    /**
     * Makes this text component handle <code>ClickableTextAction</code>.
     * @see #isClickable()
     * @see jp.kyasu.graphics.ClickableTextAction
     */
    public void setClickable(boolean b) {
	editController.setClickable(b);
    }

    /**
     * Checks if this text component allows soft tab.
     * @see #clearSoftTab()
     * @see #getSoftTab()
     * @see #setSoftTab(int)
     */
    public boolean isSoftTab() {
	return editController.isSoftTab();
    }

    /**
     * Returns the length of soft tab.
     * @return the length of soft tab. if the length is less than 0, soft tab
     *         is not allowed by this text component.
     * @see #setSoftTab(int)
     * @see #isSoftTab()
     * @see #clearSoftTab()
     */
    public int getSoftTab() {
	return editController.getSoftTab();
    }

    /**
     * Sets the length of soft tab.
     * @param i the length of soft tab. if the length is less than 0, disables
     *          soft tab.
     * @see #getSoftTab()
     * @see #isSoftTab()
     * @see #clearSoftTab()
     */
    public void setSoftTab(int i) {
	editController.setSoftTab(i);
    }

    /**
     * Disables soft tab.
     * @see #isSoftTab()
     * @see #getSoftTab()
     * @see #setSoftTab(int)
     */
    public void clearSoftTab() {
	editController.clearSoftTab();
    }

    /**
     * Returns the current text style of this text component.
     */
    public TextStyle getCurrentTextStyle() {
	return editController.getCurrentTextStyle();
    }

    /**
     * Returns the text style at the specified index in this text component.
     * @return the text style at the specified index, or <code>null</code>
     *         if the index is out of range.
     */
    public TextStyle getTextStyleAt(int index) {
	return editController.getTextStyleAt(index);
    }

    /**
     * Returns the number of the text styles in the text of this text component.
     */
    public int getTextStyleCount() {
	return editController.getTextStyleCount();
    }

    /**
     * Returns all text styles in the text of this text component.
     */
    public TextStyle[] getTextStyles() {
	return editController.getTextStyles();
    }

    /**
     * Returns the text styles in the text of this text component.
     *
     * @param  begin  the beginning index to get text styles, inclusive.
     * @param  end    the ending index to get text styles, exclusive.
     */
    public TextStyle[] getTextStyles(int begin, int end) {
	return editController.getTextStyles(begin, end);
    }

    /**
     * Returns an enumeration of the text styles of the text of this text
     * component.
     */
    public Enumeration textStyles() {
	return editController.textStyles();
    }

    /**
     * Returns an enumeration of the text styles of the text of this text
     * component.
     *
     * @param begin the beginning index to get styles, inclusive.
     * @param end   the ending index to get styles, exclusive.
     */
    public Enumeration textStyles(int begin, int end) {
	return editController.textStyles(begin, end);
    }

    /**
     * Returns the paragraph style at the specified index in this text
     * component.
     * @return the paragraph style at the specified index, or <code>null</code>
     *         if the index is out of range.
     */
    public ParagraphStyle getParagraphStyleAt(int index) {
	return editController.getParagraphStyleAt(index);
    }

    /**
     * Returns the number of the paragraph styles in the text of this text
     * component.
     */
    public int getParagraphStyleCount() {
	return editController.getParagraphStyleCount();
    }

    /**
     * Returns all paragraph styles in the text of this text component.
     */
    public ParagraphStyle[] getParagraphStyles() {
	return editController.getParagraphStyles();
    }

    /**
     * Returns the paragraph styles in the text of this text component.
     *
     * @param begin the beginning index of the text to get paragraph styles,
     *              inclusive.
     * @param end   the ending index of the text to get paragraph styles,
     *              exclusive.
     */
    public ParagraphStyle[] getParagraphStyles(int begin, int end) {
	return editController.getParagraphStyles(begin, end);
    }

    /**
     * Returns an enumeration of the paragraph styles of the text of this
     * text component.
     */
    public Enumeration paragraphStyles() {
	return editController.paragraphStyles();
    }

    /**
     * Returns an enumeration of the paragraph styles of the text of this
     * text component.
     *
     * @param begin the beginning index to get styles, inclusive.
     * @param end   the ending index to get styles, exclusive.
     */
    public Enumeration paragraphStyles(int begin, int end) {
	return editController.paragraphStyles(begin, end);
    }

    /**
     * Returns the text of this text component.
     * @see #setTEXT(jp.kyasu.graphics.Text)
     */
    public Text getTEXT() {
	return editController.getText();
    }

    /**
     * Sets the text of this text component.
     * @see #getTEXT()
     */
    public synchronized void setTEXT(Text text) {
	editController.setText(text);
    }

    /**
     * Returns the selected text from this text component.
     */
    public Text getSelectedTEXT() {
	return editController.getSelectedText();
    }

    /**
     * Selects the text between the specified start and end positions.
     * @param selectionStart the start position of the text to select.
     * @param selectionEnd   the end position of the text to select.
     * @param scroll         if true, scrolls the view after selection done.
     * @see #select(int, int, boolean, boolean)
     */
    public void select(int selectionStart, int selectionEnd, boolean scroll) {
	editController.select(selectionStart, selectionEnd, scroll);
    }

    /**
     * Selects the text between the specified start and end positions.
     * @param selectionStart the start position of the text to select.
     * @param selectionEnd   the end position of the text to select.
     * @param scroll         if true, scrolls the view after selection done.
     * @param top            if true, scrolls to the top of the view.
     * @see #select(int, int)
     * @see #select(int, int, boolean)
     */
    public synchronized void select(int selectionStart, int selectionEnd,
				    boolean scroll, boolean top)
    {
	editController.select(selectionStart, selectionEnd, scroll, top);
    }

    /**
     * Sets the position of the text insertion caret.
     * @param position the position of the caret.
     * @param top      if true, scrolls to the top of the view.
     * @see #setCaretPosition(int)
     */
    public void setCaretPosition(int position, boolean top) {
	editController.setCaretPosition(position, top);
    }

    /**
     * Inserts the specified text at the specified position in this text
     * component.
     * @param text the text to insert.
     * @param pos  the position at which to insert.
     * @see #insert(jp.kyasu.graphics.Text, int, boolean)
     */
    public void insert(Text text, int pos) {
	editController.insert(text, pos);
    }

    /**
     * Inserts the specified text at the specified position in this text
     * component.
     * @param text the text to insert.
     * @param pos  the position at which to insert.
     * @param scroll if true, scrolls the view after the insertion.
     * @see #insert(jp.kyasu.graphics.Text, int)
     * @see #replaceRange(jp.kyasu.graphics.Text, int, int, boolean)
     */
    public void insert(Text text, int pos, boolean scroll) {
	editController.insert(text, pos, scroll);
    }

    /**
     * Appends the given text to the current text.
     * @param text the text to append.
     * @see #append(jp.kyasu.graphics.Text, boolean)
     */
    public void append(Text text) {
	editController.append(text);
    }

    /**
     * Appends the given text to the current text.
     * @param text the text to append.
     * @param scroll if true, scrolls the view after the appending.
     * @see #insert(jp.kyasu.graphics.Text, int, boolean)
     */
    public void append(Text text, boolean scroll) {
	editController.append(text, scroll);
    }

    /**
     * Replaces text between the indicated start and end positions
     * with the specified replacement text.
     * @param text  the text to use as the replacement.
     * @param start the start position, inclusive.
     * @param end   the end position, exclusive.
     * @see #replaceRange(jp.kyasu.graphics.Text, int, int, boolean)
     */
    public void replaceRange(Text text, int start, int end) {
	editController.replaceRange(text, start, end);
    }

    /**
     * Replaces text between the indicated start and end positions
     * with the specified replacement text.
     * @param text   the text to use as the replacement.
     * @param start  the start position, inclusive.
     * @param end    the end position, exclusive.
     * @param scroll if true, scrolls the view after replace done.
     * @see #replaceRange(java.lang.String, int, int)
     * @see #replaceRange(jp.kyasu.graphics.Text, int, int)
     * @see #replaceSelection(jp.kyasu.graphics.Text, boolean)
     */
    public void replaceRange(Text text, int start, int end, boolean scroll) {
	editController.replaceRange(text, start, end, scroll);
    }

    /**
     * Replaces string in the range of the current selection with the
     * specified replacement string.
     * @param str the string to use as the replacement.
     * @see #replaceSelection(jp.kyasu.graphics.Text)
     */
    public void replaceSelection(String str) {
	editController.replaceSelection(str);
    }

    /**
     * Replaces text in the range of the current selection with the
     * specified replacement text.
     * @param text the text to use as the replacement.
     * @see #replaceSelection(jp.kyasu.graphics.Text, boolean)
     */
    public void replaceSelection(Text text) {
	editController.replaceSelection(text);
    }

    /**
     * Replaces text in the range of the current selection with the
     * specified replacement text.
     * @param text   the text to use as the replacement.
     * @param scroll if true, scrolls the view after replace done.
     * @see #replaceSelection(java.lang.String)
     * @see #replaceSelection(jp.kyasu.graphics.Text)
     * @see #replaceRange(jp.kyasu.graphics.Text, int, int, boolean)
     */
    public void replaceSelection(Text text, boolean scroll) {
	editController.replaceSelection(text, scroll);
    }

    /**
     * Sets the text style between the indicated start and end positions
     * to the specified text style.
     * @param style the text style to be set.
     * @param start the start position, inclusive.
     * @param end   the end position, exclusive.
     * @see #setRangeTextStyle(jp.kyasu.graphics.TextStyle, int, int, boolean)
     */
    public void setRangeTextStyle(TextStyle style, int start, int end) {
	editController.setRangeTextStyle(style, start, end);
    }

    /**
     * Sets the text style between the indicated start and end positions
     * to the specified text style.
     * @param style  the text style to be set.
     * @param start  the start position, inclusive.
     * @param end    the end position, exclusive.
     * @param scroll if true, scrolls the view after set done.
     * @see #setRangeTextStyle(jp.kyasu.graphics.TextStyle, int, int)
     * @see #setSelectionTextStyle(jp.kyasu.graphics.TextStyle, boolean)
     */
    public void setRangeTextStyle(TextStyle style, int start, int end,
				  boolean scroll)
    {
	editController.setRangeTextStyle(style, start, end, scroll);
    }

    /**
     * Sets the text style in the range of the current selection to the
     * specified text style.
     * @param style the text style to be set.
     * @see #setSelectionTextStyle(jp.kyasu.graphics.TextStyle, boolean)
     */
    public void setSelectionTextStyle(TextStyle style) {
	editController.setSelectionTextStyle(style);
    }

    /**
     * Sets the text style in the range of the current selection to the
     * specified text style.
     * @param style  the text style to be set.
     * @param scroll if true, scrolls the view after set done.
     * @see #setSelectionTextStyle(jp.kyasu.graphics.TextStyle)
     * @see #setRangeTextStyle(jp.kyasu.graphics.TextStyle, int, int, boolean)
     */
    public void setSelectionTextStyle(TextStyle style, boolean scroll) {
	editController.setSelectionTextStyle(style, scroll);
    }


    /**
     * Modifies the text style between the indicated start and end positions
     * by the specified text style modifier.
     * @param modifier the text style modifier.
     * @param start    the start position, inclusive.
     * @param end      the end position, exclusive.
     * @see #modifyRangeTextStyle(jp.kyasu.graphics.TextStyleModifier, int, int, boolean)
     */
    public void modifyRangeTextStyle(TextStyleModifier modifier,
				     int start, int end)
    {
	editController.modifyRangeTextStyle(modifier, start, end);
    }

    /**
     * Modifies the text style between the indicated start and end positions
     * by the specified text style modifier.
     * @param modifier the text style modifier.
     * @param start    the start position, inclusive.
     * @param end      the end position, exclusive.
     * @param scroll   if true, scrolls the view after set done.
     * @see #modifyRangeTextStyle(jp.kyasu.graphics.TextStyleModifier, int, int)
     * @see #modifySelectionTextStyle(jp.kyasu.graphics.TextStyleModifier, boolean)
     */
    public void modifyRangeTextStyle(TextStyleModifier modifier,
				     int start, int end, boolean scroll)
    {
	editController.modifyRangeTextStyle(modifier, start, end, scroll);
    }

    /**
     * Modifies the text style in the range of the current selection by the
     * specified text style modifier.
     * @param modifier the text style modifier.
     * @see #modifySelectionTextStyle(jp.kyasu.graphics.TextStyleModifier, boolean)
     */
    public void modifySelectionTextStyle(TextStyleModifier modifier) {
	editController.modifySelectionTextStyle(modifier);
    }

    /**
     * Modifies the text style in the range of the current selection by the
     * specified text style modifier.
     * @param modifier the text style modifier.
     * @param scroll   if true, scrolls the view after set done.
     * @see #modifySelectionTextStyle(jp.kyasu.graphics.TextStyleModifier)
     * @see #modifyRangeTextStyle(jp.kyasu.graphics.TextStyleModifier, int, int, boolean)
     */
    public void modifySelectionTextStyle(TextStyleModifier modifier,
					 boolean scroll)
    {
	editController.modifySelectionTextStyle(modifier, scroll);
    }

    /**
     * Sets the paragraph style between the indicated start and end positions
     * to the specified paragraph style.
     * @param style the paragraph style to be set.
     * @param start the start position, inclusive.
     * @param end   the end position, exclusive.
     * @see #setRangeParagraphStyle(jp.kyasu.graphics.ParagraphStyle, int, int, boolean)
     */
    public void setRangeParagraphStyle(ParagraphStyle style, int start, int end)
    {
	editController.setRangeParagraphStyle(style, start, end);
    }

    /**
     * Sets the paragraph style between the indicated start and end positions
     * to the specified paragraph style.
     * @param style  the paragraph style to be set.
     * @param start  the start position, inclusive.
     * @param end    the end position, exclusive.
     * @param scroll if true, scrolls the view after set done.
     * @see #setRangeParagraphStyle(jp.kyasu.graphics.ParagraphStyle, int, int)
     * @see #setSelectionParagraphStyle(jp.kyasu.graphics.ParagraphStyle, boolean)
     */
    public void setRangeParagraphStyle(ParagraphStyle style,
				       int start, int end, boolean scroll)
    {
	editController.setRangeParagraphStyle(style, start, end, scroll);
    }

    /**
     * Sets the paragraph style in the range of the current selection to the
     * specified paragraph style.
     * @param style the paragraph style to be set.
     * @see #setSelectionParagraphStyle(jp.kyasu.graphics.ParagraphStyle, boolean)
     */
    public void setSelectionParagraphStyle(ParagraphStyle style) {
	editController.setSelectionParagraphStyle(style);
    }

    /**
     * Sets the paragraph style in the range of the current selection to the
     * specified paragraph style.
     * @param style  the paragraph style to be set.
     * @param scroll if true, scrolls the view after set done.
     * @see #setSelectionParagraphStyle(jp.kyasu.graphics.ParagraphStyle)
     * @see #setRangeParagraphStyle(jp.kyasu.graphics.ParagraphStyle, int, int, boolean)
     */
    public void setSelectionParagraphStyle(ParagraphStyle style, boolean scroll)
    {
	editController.setSelectionParagraphStyle(style, scroll);
    }


    /**
     * Modifies the paragraph style between the indicated start and end
     * positions by the specified paragraph style modifier.
     * @param modifier the paragraph style modifier.
     * @param start    the start position, inclusive.
     * @param end      the end position, exclusive.
     * @see #modifyRangeParagraphStyle(jp.kyasu.graphics.ParagraphStyleModifier, int, int, boolean)
     */
    public void modifyRangeParagraphStyle(ParagraphStyleModifier modifier,
					  int start, int end)
    {
	editController.modifyRangeParagraphStyle(modifier, start, end);
    }

    /**
     * Modifies the paragraph style between the indicated start and end
     * positions by the specified paragraph style modifier.
     * @param modifier the paragraph style modifier.
     * @param start    the start position, inclusive.
     * @param end      the end position, exclusive.
     * @param scroll   if true, scrolls the view after set done.
     * @see #modifyRangeParagraphStyle(jp.kyasu.graphics.ParagraphStyleModifier, int, int)
     * @see #modifySelectionParagraphStyle(jp.kyasu.graphics.ParagraphStyleModifier, boolean)
     */
    public void modifyRangeParagraphStyle(ParagraphStyleModifier modifier,
					  int start, int end, boolean scroll)
    {
	editController.modifyRangeParagraphStyle(modifier, start, end, scroll);
    }

    /**
     * Modifies the paragraph style in the range of the current selection by
     * the specified paragraph style modifier.
     * @param modifier the paragraph style modifier.
     * @see #modifySelectionParagraphStyle(jp.kyasu.graphics.ParagraphStyleModifier, boolean)
     */
    public void modifySelectionParagraphStyle(ParagraphStyleModifier modifier)
    {
	editController.modifySelectionParagraphStyle(modifier);
    }

    /**
     * Modifies the paragraph style in the range of the current selection by
     * the specified paragraph style modifier.
     * @param modifier the paragraph style modifier.
     * @param scroll   if true, scrolls the view after set done.
     * @see #modifySelectionParagraphStyle(jp.kyasu.graphics.ParagraphStyleModifier)
     * @see #modifyRangeParagraphStyle(jp.kyasu.graphics.ParagraphStyleModifier, int, int, boolean)
     */
    public void modifySelectionParagraphStyle(ParagraphStyleModifier modifier,
					      boolean scroll)
    {
	editController.modifySelectionParagraphStyle(modifier, scroll);
    }

    /**
     * Returns the thickness of the scroll bar.
     * @see #setScrollbarThickness(int)
     */
    public int getScrollbarThickness() {
	ScrollPanel sp = (ScrollPanel)editView.getParent();
	return sp.getScrollbarThickness();
    }

    /**
     * Sets the thickness of the scroll bar.
     * @see #setScrollbarThickness()
     */
    public synchronized void setScrollbarThickness(int thickness) {
	ScrollPanel sp = (ScrollPanel)editView.getParent();
	if (thickness == sp.getScrollbarThickness())
	    return;
	sp.setScrollbarThickness(thickness);
	invalidate();
    }


    // ================ Enhanced Utility APIs ================

    /**
     * Prints the text of this text component with the specified flag
     * determining to print a page number in footer.
     *
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(boolean printPageNum) {
	editController.print(printPageNum);
    }

    /**
     * Prints the text of this text component with the specified header
     * string and flag determining to print a page number in footer.
     *
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(String header, boolean printPageNum) {
	editController.print(header, printPageNum);
    }

    /**
     * Prints the text of this text component with the specified insets,
     * header string, and flag determining to print a page number in footer.
     *
     * @param insets       the insets of a printing medium (paper).
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(Insets insets, String header, boolean printPageNum) {
	editController.print(insets, header, printPageNum);
    }

    /**
     * Prints the text of this text component to a print device provided from
     * the specified print job, with the specified header string and flag
     * determining to print a page number in footer.
     *
     * @param job          the print job.
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(PrintJob job, String header, boolean printPageNum) {
	editController.print(job, header, printPageNum);
    }

    /**
     * Prints the text of this text component to a print device provided from
     * the specified print job, with the specified insets, header string, and
     * flag determining to print a page number in footer.
     *
     * @param job          the print job.
     * @param insets       the insets of a printing medium (paper).
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public synchronized void print(PrintJob job, Insets insets,
				   String header, boolean printPageNum)
    {
	editController.print(job, insets, header, printPageNum);
    }

    /**
     * Checks if the auto indent is enabled.
     */
    public boolean isAutoIndentEnabled() {
	Keymap map = editController.getKeymap();
	String actionNames[] = map.getKeyCodeMap(KeyEvent.VK_ENTER);
	return actionNames != null && actionNames.length == 1 &&
		actionNames[0].equals("newline-and-indent");
    }

    /**
     * Enables the auto indent.
     */
    public void setAutoIndentEnabled(boolean autoIndent) {
	Keymap map = editController.getKeymap();
	if (autoIndent) {
	    map.setKeyCodeMap(KeyEvent.VK_ENTER, "newline-and-indent");
	}
	else {
	    map.setKeyCodeMap(KeyEvent.VK_ENTER, "newline");
	}
	editController.setKeymap(map);
    }

    /**
     * Checks if the show match is enabled.
     */
    public boolean isShowMatchEnabled() {
	Keymap map = editController.getKeymap();
	String actionNames[] = map.getKeyCharMap(')');
	return actionNames != null && actionNames.length == 2 &&
		(actionNames[0].equals("show-match") ||
		actionNames[1].equals("show-match"));
    }

    /**
     * Enables the show match.
     */
    public void setShowMatchEnabled(boolean showMatch) {
	Keymap map = editController.getKeymap();
	if (showMatch) {
	    String actions[] = { "insert-character", "show-match" };
	    map.setKeyCharMap(')', actions);
	    map.setKeyCharMap('}', actions);
	    map.setKeyCharMap(']', actions);
	}
	else {
	    map.setKeyCharMap(')', "insert-character");
	    map.setKeyCharMap('}', "insert-character");
	    map.setKeyCharMap(']', "insert-character");
	}
	editController.setKeymap(map);
    }

    /**
     * Clears the undo of the last change.
     */
    public void clearUndo() {
	editController.clearUndo();
    }

    /**
     * Copies the current selection to the clipboard.
     */
    public void copy_clipboard() {
	editController.copy_clipboard();
    }

    /**
     * Cuts the current selection to the clipboard.
     */
    public void cut_clipboard() {
	editController.cut_clipboard();
    }

    /**
     * Moves the insertion cursor one character to the left.
     */
    public void backward_character() {
	editController.backward_character();
    }

    /**
     * Moves the insertion cursor to the first non-whitespace character
     * after the first whitespace character to the left or the beginning of
     * the line.  If the insertion cursor is already at the beginning of a
     * word, moves the insertion cursor to the beginning of the previous word.
     */
    public void backward_word() {
	editController.backward_word();
    }

    /**
     * Causes the terminal to beep.
     */
    public void beep() {
	editController.beep();
    }

    /**
     * Moves the insertion cursor to the beginning of the text.
     */
    public void beginning_of_file() {
	editController.beginning_of_file();
    }

    /**
     * Moves the insertion cursor to the beginning of the line.
     */
    public void beginning_of_line() {
	editController.beginning_of_line();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the character following the insert cursor.
     */
    public void delete_next_character() {
	editController.delete_next_character();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the characters following the insertion cursor to
     * the next space, tab or end of line character.
     */
    public void delete_next_word() {
	editController.delete_next_word();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the character of text immediately preceding the
     * insertion cursor.
     */
    public void delete_previous_character() {
	editController.delete_previous_character();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the characters preceding the insertion cursor to
     * the previous space, tab or beginning of line character.
     */
    public void delete_previous_word() {
	editController.delete_previous_word();
    }

    /**
     * Deletes the current selection.
     */
    public void delete_selection() {
	editController.delete_selection();
    }

    /**
     * Deletes the characters following the insertion cursor to the next
     * end of line character.
     */
    public void delete_to_end_of_line() {
	editController.delete_to_end_of_line();
    }

    /**
     * Deletes the characters preceding the insertion cursor to the previous
     * beginning of line character.
     */
    public void delete_to_start_of_line() {
	editController.delete_to_start_of_line();
    }

    /**
     * Deselects the current selection.
     */
    public void deselect_all() {
	editController.deselect_all();
    }

    /**
     * Moves the insertion cursor to the end of the text.
     */
    public void end_of_file() {
	editController.end_of_file();
    }

    /**
     * Moves the insertion cursor to the end of the line.
     */
    public void end_of_line() {
	editController.end_of_line();
    }

    /**
     * Finds the word and move the insertion cursor to the founded word.
     */
    public void find_word() {
	editController.find_word();
    }

    /**
     * Moves the insertion cursor one character to the right.
     */
    public void forward_character() {
	editController.forward_character();
    }

    /**
     * Moves the insertion cursor to the first whitespace character or
     * end of line following the next non-whitespace character.  If the
     * insertion cursor is already at the end of a word, moves the
     * insertion cursor to the end of the next word.
     */
    public void forward_word() {
	editController.forward_word();
    }

    /**
     * Go to line.
     */
    public void goto_line() {
	editController.goto_line();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts character at the insertion cursor.
     */
    public void insert_character(char c) {
	editController.insert_character(c);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts string at the insertion cursor.
     */
    public void insert_string(String str) {
	editController.insert_string(str);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Otherwise, kills the character following the insertion cursor and
     * stores the character in the cut buffer.
     */
    public void kill_next_character() {
	editController.kill_next_character();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Otherwise, kills the characters following the insertion cursor to
     * the next space, tab or end of line character, and stores the
     * characters in the cut buffer.
     */
    public void kill_next_word() {
	editController.kill_next_word();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Otherwise, kills the character of text immediately preceding the
     * insertion cursor and stores the character in the cut buffer.
     */
    public void kill_previous_character() {
	editController.kill_previous_character();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Otherwise, kills the characters preceding the insertion cursor to
     * the next space, tab or beginning of line character, and stores the
     * characters in the cut buffer.
     */
    public void kill_previous_word() {
	editController.kill_previous_word();
    }

    /**
     * Kills the currently selected text and stores the text in the cut buffer.
     */
    public void kill_selection() {
	editController.kill_selection();
    }

    /**
     * Kills the characters following the insertion cursor to the next end
     * of line character and stores the characters in the cut buffer.
     */
    public void kill_to_end_of_line() {
	editController.kill_to_end_of_line();
    }

    /**
     * Kills the characters preceding the insertion cursor to the next
     * beginning of line character and stores the characters in the cut buffer.
     */
    public void kill_to_start_of_line() {
	editController.kill_to_start_of_line();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts a newbreak at the insertion cursor.
     */
    public void newbreak() {
	editController.newbreak();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts a newline at the insertion cursor.
     */
    public void newline() {
	editController.newline();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts a newline and then the same number of whitespace characters
     * as at the beginning of the previous line.
     */
    public void newline_and_indent() {
	editController.newline_and_indent();
    }

    /**
     * Moves the insertion cursor to the next line.
     */
    public void next_line() {
	editController.next_line();
    }

    /**
     * Moves the insertion cursor forward one page.
     */
    public void next_page() {
	editController.next_page();
    }

    /**
     * Pastes the the clipboard before the insertion cursor.
     */
    public void paste_clipboard() {
	editController.paste_clipboard();
    }

    /**
     * Moves the insertion cursor to the previous line.
     */
    public void previous_line() {
	editController.previous_line();
    }

    /**
     * Moves the insertion cursor back one page.
     */
    public void previous_page() {
	editController.previous_page();
    }

    /**
     * Redraw the display.
     */
    public void redraw_display() {
	editController.redraw_display();
    }

    /**
     * Select all text.
     */
    public void select_all() {
	editController.select_all();
    }

    /**
     * Select a line at the selection start position.
     */
    public void select_line() {
	editController.select_line();
    }

    /**
     * Select a word at the selection start position.
     */
    public void select_word() {
	editController.select_word();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts the soft tab.
     */
    public void tab() {
	editController.tab();
    }

    /**
     * Undo the last change.
     */
    public void undo() {
	editController.undo();
    }

    /**
     * Restores last killed text to the position of the insertion cursor.
     */
    public void unkill() {
	editController.unkill();
    }


    // ================ Private ================

    /** Internal constant for serialization */
    static protected final String textListenerK = "textL".intern();

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();

	jp.kyasu.awt.ListenerSerializer.write(s,
					      textListenerK,
					      textListener);
	s.writeObject(null);

	if (textPositionListeners != null) {
	    for (Enumeration e = textPositionListeners.elements();
		 e.hasMoreElements();
		 )
	    {
		TextPositionListener l = (TextPositionListener)e.nextElement();
		if (l instanceof java.io.Serializable) {
		    s.writeObject(l);
		}
	    }
	}
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws ClassNotFoundException, java.io.IOException
    {
	s.defaultReadObject();

	editModel.removeTextListener(this);
	Object keyOrNull;
	while ((keyOrNull = s.readObject()) != null) {
	    String key = ((String)keyOrNull).intern();
	    if (key == textListenerK)
		addTextListener((TextListener)s.readObject());
	    else // skip value for unrecognized key
		s.readObject();
	}

	editView.removeTextPositionListener(this);
	Object listenerOrNull;
	while ((listenerOrNull = s.readObject()) != null) {
	    addTextPositionListener((TextPositionListener)listenerOrNull);
	}
    }
}
