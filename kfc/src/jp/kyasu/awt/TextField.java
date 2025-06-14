/*
 * TextField.java
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
import jp.kyasu.awt.text.KeyBinding;
import jp.kyasu.awt.text.Keymap;
import jp.kyasu.awt.text.TextEditController;
import jp.kyasu.awt.text.TextEditView;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.V3DBorder;
import jp.kyasu.graphics.VBorder;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * TextField is a component that allows the editing of a single line of text.
 *
 * @version 	31 Aug 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextField extends TextComponent {
    transient protected ActionListener actionListener;

    /**
     * The default rich text style for the text field.
     */
    static public final RichTextStyle DEFAULT_FIELD_STYLE =
	new RichTextStyle(
		RichTextStyle.NO_WRAP,
		RichTextStyle.JAVA_LINE_SEPARATOR,
		false,
		new TextStyle("Monospaced", Font.PLAIN, 12),
		new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 2, 0, 0));


    class NotifyKeyAction implements KeyAction, java.io.Serializable {
	public String getName()           { return "notify-action-listeners"; }
	public void perform(char keyChar) { notifyActionListeners(); }
    }

    /*
    class TransferFocusKeyAction implements KeyAction, java.io.Serializable {
	public String getName()           { return "transfer-focus"; }
	public void perform(char keyChar) { editView.transferFocus(); }
    };
    */


    /**
     * Constructs a new text field.
     */
    public TextField() {
	this("", 0);
    }

    /**
     * Constructs a new text field initialized with the specified string.
     * @param string the string to be displayed.
     */
    public TextField(String string) {
	this(string, string.length());
    }

    /**
     * Constructs a new TextField with the specified number of columns.
     * @param columns the number of columns
     */
    public TextField(int columns) {
	this("", columns);
    }

    /**
     * Constructs a new text field initialized with the specified string
     * to be displayed, and wide enough to hold the specified number of
     * characters.
     * @param string  the string to be displayed.
     * @param columns the number of characters.
     */
    public TextField(String string, int columns) {
	this(new RichText(new Text((string == null ?
					"" : Text.getJavaString(string)),
					DEFAULT_FIELD_STYLE.getTextStyle()),
			  DEFAULT_FIELD_STYLE),
	     columns);
    }

    /**
     * Constructs a new text field initialized with the specified rich text.
     * @param richText the rich text to be displayed.
     */
    public TextField(RichText richText) {
	this(richText, richText.length());
    }

    /**
     * Constructs a new text field initialized with the specified rich text
     * to be displayed, and wide enough to hold the specified number of
     * characters.
     * @param richText the rich text to be displayed.
     * @param columns  the number of characters.
     */
    public TextField(RichText richText, int columns) {
	this(new DefaultTextEditModel(richText), columns);
    }

    /**
     * Constructs a new text field with the specified model.
     * @param model the text edit model.
     */
    public TextField(TextEditModel model) {
	this(model, model.getRichText().length());
    }

    /**
     * Constructs a new text field with the specified model and wide enough
     * to hold the specified number of characters.
     * @param model   the text edit model.
     * @param columns the number of characters.
     */
    public TextField(TextEditModel model, int columns) {
	this(model, columns, new V3DBorder(false));
    }

    /**
     * Constructs a new text field with the specified model, wide enough
     * to hold the specified number of characters, and border visual.
     * @param model   the text edit model.
     * @param columns the number of characters.
     * @param border  the border visual of the text field.
     */
    public TextField(TextEditModel model, int columns, VBorder border) {
	super();
	if (model == null)
	    throw new NullPointerException();

	editModel = model;
	editView = new TextEditView(editModel);
	editController = editView.getController();

	super.setFont(
	  editModel.getRichText().getRichTextStyle().getTextStyle().getFont());

	setLayout(new BorderLayout());
	if (border == null) {
	    add(editView, BorderLayout.CENTER);
	}
	else {
	    BorderedPanel bp = new BorderedPanel(border);
	    bp.add(editView, BorderLayout.CENTER);
	    add(bp, BorderLayout.CENTER);
	}

	super.setCursor(editView.getCursor());

	this.rows    = 1;
	this.columns = columns;

	actionListener = null;

	editView.setLineWrap(NO_WRAP);
	editView.setSelectionVisible(false);
	editController.setSelectionVisibleAtFocus(true);

	editController.addKeyAction(new NotifyKeyAction());
	//editController.addKeyAction(new TransferFocusKeyAction());
	Keymap keymap = editController.getKeymap();
	keymap.setKeyCodeMap(KeyEvent.VK_ENTER, "notify-action-listeners");
	//keymap.setKeyCodeMap(KeyEvent.VK_TAB, "transfer-focus");
	keymap.removeKeyCodeMap(KeyEvent.VK_TAB);
	keymap.setKeyCodeMap(KeyEvent.VK_TAB, java.awt.Event.CTRL_MASK,  "tab");
	keymap.setKeyCodeMap(KeyEvent.VK_TAB, java.awt.Event.SHIFT_MASK, "tab");
	editController.setKeymap(keymap);
    }


    /**
     * Moves and resizes this component.
     */
    public synchronized void setBounds(int x, int y, int width, int height) {
	Dimension d = getPreferredSize();
	if (height > d.height) {
	    y += (height - d.height) / 2;
	    height = d.height;
	}
	super.setBounds(x, y, width, height);
    }

    /**
     * Adds the specified action listener to recieve action events from
     * this text field.
     * @param l the action listener.
     */
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
    }

    /**
     * Removes the specified action listener so that it no longer receives
     * action events from this text field.
     * @param l the action listener.
     */
    public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    /** Notifies the action event to the action listeners. */
    protected void notifyActionListeners() {
	ActionEvent e = new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED,
					editController.getString());
	if (actionListener != null) {
	    actionListener.actionPerformed(e);
	}
	else {
	    postOldEvent(e);
	}
    }

    // ======== Redefines TextComponent APIs ========

    /**
     * Sets the flag that determines whether or not this text component is
     * editable.
     */
    public void setEditable(boolean b) {
	setEditable(b, true);
    }

    /**
     * Returns an enumerated value that indicates which scroll bars
     * the text component uses.
     */
    public int getScrollbarVisibility() {
	return SCROLLBARS_NONE;
    }

    /**
     * Determines the preferred size of a text component with the specified
     * number of rows and columns.
     * @param rows    the number of rows.
     * @param columns the number of columns, ignored.
     * @return the preferred dimensions required to display the text component
     *         with the specified number of rows and columns.
     */
    public Dimension getPreferredSize(int rows, int columns) {
	return getPreferredSize(columns);
    }

    /**
     * Returns the thickness of the scroll bar.
     */
    public int getScrollbarThickness() {
	return 0;
    }

    /**
     * Sets the thickness of the scroll bar.
     */
    public synchronized void setScrollbarThickness(int thickness) {
	// do nothing
    }

    // ======== java.awt.TextField APIs ========

    /**
     * Returns the character that is to be used for echoing.
     * @return the echo character for this text field.
     * @see #setEchoChar(char)
     * @see #echoCharIsSet()
     */
    public char getEchoChar() {
	return editController.getEchoChar();
    }

    /**
     * Sets the echo character for this text field.
     * @param c the echo character for this text field.
     * @see #getEchoChar()
     * @see #echoCharIsSet()
     */
    public synchronized void setEchoChar(char c) {
	editController.setEchoChar(c);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setEchoChar(char)</code>.
     */
    public void setEchoCharacter(char c) {
	setEchoChar(c);
    }

    /**
     * Indicates whether or not this text field has a character set
     * for echoing.
     * @return <code>true</code> if this text field has a character set
     *         for echoing; <code>false</code> otherwise.
     * @see #getEchoChar()
     * @see #setEchoChar(char)
     */
    public boolean echoCharIsSet() {
	return editController.echoCharIsSet();
    }

    /**
     * Returns the preferred size of this text field with the specified
     * number of columns.
     * @param columns the number of columns.
     * @return the preferred dimensions for displaying this text field.
     */
    public Dimension getPreferredSize(int columns) {
	synchronized (getTreeLock()) {
	    if (columns <= 0) columns = 1;
	    int width = editView.getPreferredWidth(columns);
	    int height = editView.getPreferredHeight(1);
	    Container c = editView.getParent(); // BorderedPanel
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
    public Dimension preferredSize(int columns) {
	return getPreferredSize(columns);
    }

    /**
     * Returns the preferred size of this text field.
     * @return the preferred dimensions for displaying this text field.
     */
    public Dimension getPreferredSize() {
	return getPreferredSize(columns > 0 ?
				    columns :
				    editModel.getRichText().length());
    }

    /**
     * Returns the minumum dimensions for a text field with the specified
     * number of columns.
     * @param columns the number of columns in this text field.
     */
    public Dimension getMinimumSize(int columns) {
	return getPreferredSize(columns);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getMinimumSize(int)</code>.
     */
    public Dimension minimumSize(int columns) {
	return getMinimumSize(columns);
    }

    /**
     * Returns the minumum dimensions for this text field.
     * @return the minimum dimensions for displaying this text field.
     */
    public Dimension getMinimumSize() {
	return getMinimumSize(columns > 0 ? columns : 1);
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
    }

    private void readObject(java.io.ObjectInputStream s)
	throws ClassNotFoundException, java.io.IOException
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
    }
}
