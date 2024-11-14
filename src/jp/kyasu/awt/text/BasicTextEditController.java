/*
 * BasicTextEditController.java
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
import jp.kyasu.awt.TextEditModel;
import jp.kyasu.awt.Dialog;
import jp.kyasu.awt.Undo;
import jp.kyasu.graphics.ClickableTextAction;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.ParagraphStyleModifier;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.TextStyleModifier;
import jp.kyasu.graphics.text.TextPositionInfo;
import jp.kyasu.graphics.VActiveButton;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.event.*;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.Enumeration;

// /*if[JDK1.2]
import jp.kyasu.graphics.BasicTSModifier;
import jp.kyasu.graphics.TextBuffer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodHighlight;
import java.text.AttributedCharacterIterator;
import java.util.Map;
/*end[JDK1.2]*/

/**
 * The <code>BasicTextEditController</code> class implements a controller
 * of a MVC model for the text editing. The model of the MVC model is a
 * <code>TextEditModel</code> object and the view of the MVC model is a
 * <code>TextEditView</code> object.
 * <p>
 * The <code>BasicTextEditController</code> class implements basic operations
 * for the text editing. The <code>TextEditController</code> class (a
 * subclass of this class) implements full operations for the text editing.
 * <p>
 * The principal editing operations on a <code>BasicTextEditController</code>
 * are the <code>replaceRange (replaceSelection)</code>,
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
 *
 * @see 	jp.kyasu.awt.TextEditModel
 * @see 	jp.kyasu.awt.text.TextEditView
 * @see 	jp.kyasu.awt.text.TextEditController
 *
 * @version 	18 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class BasicTextEditController extends TextController
	implements ActionListener
	// /*if[JDK1.2]
	, InputMethodListener
	/*end[JDK1.2]*/
{
    protected TextEditModel model;
    protected TextEditView view;
    protected KeyBinding keyBinding;
    protected boolean clickable;
    protected Text typeInText;
    protected TextStyle typeInStyle;
    protected Menu editMenu;
    protected PopupMenu popupMenu;
    transient protected TextPositionInfo dragOrigin;
    transient protected TextPositionInfo dragCurrent;
    transient protected Undo lastUndo;


    /**
     * Constructs a text edit controller with the specified text edit view.
     *
     * @param view the text edit view.
     */
    public BasicTextEditController(TextEditView view) {
	super();
	if (view == null)
	    throw new NullPointerException();
	model = view.model;
	this.view = view;

	keyBinding = new KeyBinding();

	selectionVisibleAtFocus = false;
	/*
	if (selectionVisibleAtFocus) {
	    view.setSelectionVisible(false);
	}
	*/

	clickable = false;
	typeInStyle = model.getRichText().getRichTextStyle().getTextStyle();
	typeInText = new Text(" ", typeInStyle);

	editMenu = createEditMenu();
	setPopupMenu(createPopupMenu());

	dragOrigin = dragCurrent = null;
	lastUndo = null;
    }


    /**
     * Returns the model of this controller.
     */
    public TextEditModel getModel() {
	return model;
    }

    /**
     * Returns the view of this controller.
     */
    public TextView getView() {
	return view;
    }

    /**
     * Tests if the selection becomes visible when the view is focused.
     */
    public boolean isSelectionVisibleAtFocus() {
	return selectionVisibleAtFocus && view.selectionIsCaret();
    }

    /**
     * Returns the text in the clipboard.
     */
    public Text getClipboardText() {
	return getClipboardText(typeInStyle);
    }

    /**
     * Returns the keymap of this controller.
     * @see #setKeymap(jp.kyasu.awt.text.Keymap)
     */
    public Keymap getKeymap() {
	return keyBinding.getKeymap();
    }

    /**
     * Sets the keymap of this controller.
     * @see #getKeymap()
     */
    public synchronized void setKeymap(Keymap keymap) {
	keyBinding.setKeymap(keymap);
    }

    /**
     * Returns the key binding of this controller.
     * @see #getKeyAction(java.lang.String)
     * @see #addKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyActionNamed(java.lang.String)
     */
    public KeyBinding getKeyBinding() {
	return keyBinding;
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
	return keyBinding.getKeyAction(actionName);
    }

    /**
     * Adds the key action to the key binding of this controller.
     * @param keyAction the key action object.
     * @see #getKeyAction(java.lang.String)
     * @see #removeKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #removeKeyActionNamed(java.lang.String)
     */
    public synchronized void addKeyAction(KeyAction keyAction) {
	keyBinding.addKeyAction(keyAction);
    }

    /**
     * Removes the key action from the key binding of this controller.
     * @param keyAction the key action object.
     * @see #addKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #getKeyAction(java.lang.String)
     * @see #removeKeyActionNamed(java.lang.String)
     */
    public synchronized void removeKeyAction(KeyAction keyAction) {
	keyBinding.removeKeyAction(keyAction);
    }

    /**
     * Removes the key action named the specified name from the key binding
     * of this controller.
     * @param actionName the name of the key action.
     * @see #addKeyAction(jp.kyasu.awt.text.KeyAction)
     * @see #getKeyAction(java.lang.String)
     * @see #removeKeyAction(jp.kyasu.awt.text.KeyAction)
     */
    public synchronized void removeKeyActionNamed(String actionName) {
	keyBinding.removeKeyActionNamed(actionName);
    }

    /**
     * Performs the key action named the specified name.
     * @param actionName the name of the key action.
     * @see #performKeyAction(java.lang.String, char)
     */
    public void performKeyAction(String actionName) {
	performKeyAction(actionName, ' ');
    }

    /**
     * Performs the key action named the specified name.
     * @param actionName the name of the key action.
     * @param keyChar    the key character for the key action.
     * @see #performKeyAction(java.lang.String)
     */
    public synchronized void performKeyAction(String actionName, char keyChar)
    {
	KeyAction action = keyBinding.getKeyAction(actionName);
	if (action != null) {
	    action.perform(keyChar);
	}
    }

    /**
     * Tests if this controller handles <code>ClickableTextAction</code>.
     * @see #setClickable(boolean)
     * @see jp.kyasu.graphics.ClickableTextAction
     */
    public boolean isClickable() {
	return clickable;
    }

    /**
     * Makes this controller handle <code>ClickableTextAction</code>.
     * @see #isClickable()
     * @see jp.kyasu.graphics.ClickableTextAction
     */
    public void setClickable(boolean b) {
	clickable = b;
    }

    /**
     * Returns the edit menu of this controller.
     */
    public Menu getEditMenu() {
	return editMenu;
    }

    /**
     * Returns the popup menu of this controller.
     * @see #setPopupMenu(java.awt.PopupMenu)
     */
    public synchronized PopupMenu getPopupMenu() {
	return popupMenu;
    }

    /**
     * Sets the popup menu of this controller.
     * @see #getPopupMenu()
     */
    public void setPopupMenu(PopupMenu menu) {
	if (popupMenu != null)
	    view.remove(popupMenu);
	popupMenu = menu;
	if (popupMenu != null)
	    view.add(popupMenu);
    }


    // ================ TextComponent APIs ================

    /**
     * Returns the string of the view of this controller.
     * @see #getString(java.awt.String)
     * @see #setString(java.awt.String)
     */
    public String getString() {
	return getString(System.getProperty("line.separator", "\n"));
    }

    /**
     * Returns the string of the view of this controller.
     * @param separator the separator string.
     * @see #getString()
     * @see #setString(java.awt.String)
     * @see jp.kyasu.graphics.Text#getSystemString(java.awt.String, java.awt.String)
     */
    public String getString(String separator) {
	return Text.getSystemString(getText().toString(), separator);
    }

    /**
     * Sets the string of the view of this controller.
     * @see #getString()
     */
    public synchronized void setString(String str) {
	if (str == null) str = "";
	setText(
	    new Text(Text.getJavaString(str),
		     model.getRichText().getRichTextStyle().getTextStyle()));
    }

    /**
     * Returns the selected string from the view of this controller.
     */
    public String getSelectedString() {
	return Text.getSystemString(getSelectedText().toString());
    }

    /**
     * Indicates whether or not the view of this controller is editable.
     * @see #setEditable(boolean)
     */
    public boolean isEditable() {
	return view.isEditable();
    }

    /**
     * Sets the flag that determines whether or not the view this controller
     * is editable.
     * @see #isEditable()
     */
    public synchronized void setEditable(boolean b) {
	view.setEditable(b);
	setMenuEnabled(editMenu, b);
	setMenuEnabled(popupMenu, b);
    }

    /**
     * Returns the start position of the selected text.
     * @see #setSelectionStart(int)
     * @see #getSelectionEnd()
     */
    public int getSelectionStart() {
	TextPositionInfo posInfo = view.getSelectionBegin();
	return (posInfo != null ? posInfo.textIndex : 0);
    }

    /**
     * Sets the selection start for the view of this controller to the
     * specified position.
     * @see #getSelectionStart()
     * @see #setSelectionEnd(int)
     */
    public void setSelectionStart(int selectionStart) {
	select(selectionStart, getSelectionEnd());
    }

    /**
     * Returns the end position of the selected text.
     * @see #setSelectionEnd(int)
     * @see #getSelectionStart()
     */
    public int getSelectionEnd() {
	TextPositionInfo posInfo = view.getSelectionEnd();
	return (posInfo != null ? posInfo.textIndex : 0);
    }

    /**
     * Sets the selection end for the view of this controller to the
     * specified position.
     * @see #getSelectionEnd()
     * @see #setSelectionStart(int)
     */
    public void setSelectionEnd(int selectionEnd) {
	select(getSelectionStart(), selectionEnd);
    }

    /**
     * Selects the text between the specified start and end positions.
     * @param selectionStart the start position of the text to select.
     * @param selectionEnd   the end position of the text to select.
     * @see #setSelectionStart(int)
     * @see #setSelectionEnd(int)
     * @see #select(int, int, boolean, boolean)
     * @see #selectAll()
     */
    public void select(int selectionStart, int selectionEnd) {
	select(selectionStart, selectionEnd, true, false);
    }

    /**
     * Selects all the text.
     * @see #select(int, int)
     */
    public void selectAll() {
	select(0, model.getRichText().length());
    }

    /**
     * Returns the position of the text insertion caret.
     * @see #setCaretPosition(int)
     */
    public int getCaretPosition() {
    	return getSelectionStart();
    }

    /**
     * Sets the position of the text insertion caret.
     * @see #getCaretPosition()
     * @see #setCaretPosition(int, boolean)
     */
    public void setCaretPosition(int position) {
	setCaretPosition(position, false);
    }

    /**
     * Tests if the selection is caret, i.e., null selection.
     */
    public boolean selectionIsCaret() {
	return view.selectionIsCaret();
    }


    // ================ TextArea APIs ================

    /**
     * Inserts the specified string at the specified position in the view
     * of this controller.
     * @param str the string to insert.
     * @param pos the position at which to insert.
     * @see #replaceRange(java.lang.String, int, int)
     */
    public void insert(String str, int pos) {
	if (str == null) str = "";
	replaceRange(str, pos, pos);
    }

    /**
     * Appends the given string to the current text.
     * @param str the string to append.
     * @see #insert(java.lang.String, int)
     */
    public void append(String str) {
	if (str == null) str = "";
	insert(str, model.getRichText().length());
    }

    /**
     * Replaces string between the indicated start and end positions
     * with the specified replacement string.
     * @param str   the string to use as the replacement.
     * @param start the start position, inclusive.
     * @param end   the end position, exclusive.
     * @see #replaceRange(jp.kyasu.graphics.Text, int, int)
     */
    public void replaceRange(String str, int start, int end) {
	if (str == null) str = "";
	replaceRange(new Text(str, typeInStyle), start, end);
    }

    /**
     * Returns the number of rows in the view of this controller.
     * @see #getColumns()
     */
    public int getRows() {
	return view.getRows();
    }

    /**
     * Returns the number of columns in the view of this controller.
     * @see #getRows()
     */
    public int getColumns() {
	return view.getColumns();
    }


    // ================ TextField APIs ================

    /**
     * Returns the character that is to be used for echoing.
     * @see #setEchoChar(char)
     * @see #echoCharIsSet()
     */
    public char getEchoChar() {
	 return view.layout.getEchoChar();
    }

    /**
     * Sets the echo character for the view of this controller.
     * @see #getEchoChar()
     * @see #echoCharIsSet()
     */
    public synchronized void setEchoChar(char c) {
	view.layout.setEchoChar(c);
	model.setRichText(model.getRichText());
    }

    /**
     * Indicates whether or not the view of this controller has a character
     * set for echoing.
     * @see #getEchoChar()
     * @see #setEchoChar(char)
     */
    public boolean echoCharIsSet() {
	return view.layout.echoCharIsSet();
    }


    // ================ Enhanced APIs ================

    /**
     * Returns the current text style of this controller.
     */
    public TextStyle getCurrentTextStyle() {
	return typeInStyle;
    }

    /**
     * Returns the text style at the specified index in the view of this
     * controller.
     * @return the text style at the specified index, or <code>null</code>
     *         if the index is out of range.
     */
    public TextStyle getTextStyleAt(int index) {
	RichText richText = model.getRichText();
	if (index < 0 || index >= richText.length())
	    return null;
	return richText.getTextStyleAt(index);
    }

    /**
     * Returns the number of the text styles in the text of the view of
     * this controller.
     */
    public int getTextStyleCount() {
	return model.getRichText().getText().getTextStyleCount();
    }

    /**
     * Returns all text styles in the text of the view of this controller.
     */
    public TextStyle[] getTextStyles() {
	return model.getRichText().getText().getTextStyles();
    }

    /**
     * Returns the text styles in the text of the view of this controller.
     *
     * @param  begin  the beginning index to get text styles, inclusive.
     * @param  end    the ending index to get text styles, exclusive.
     */
    public TextStyle[] getTextStyles(int begin, int end) {
	return model.getRichText().getText().getTextStyles(begin, end);
    }

    /**
     * Returns an enumeration of the text styles of text of the view of
     * this controller.
     */
    public Enumeration textStyles() {
	return model.getRichText().getText().textStyles();
    }

    /**
     * Returns an enumeration of the text styles of text of the view of
     * this controller.
     *
     * @param begin the beginning index to get styles, inclusive.
     * @param end   the ending index to get styles, exclusive.
     */
    public Enumeration textStyles(int begin, int end) {
	return model.getRichText().getText().textStyles(begin, end);
    }

    /**
     * Returns the paragraph style at the specified index in the view of
     * this controller.
     * @return the paragraph style at the specified index, or <code>null</code>
     *         if the index is out of range.
     */
    public ParagraphStyle getParagraphStyleAt(int index) {
	RichText richText = model.getRichText();
	if (index < 0 || index > richText.length())
	    return null;
	return richText.getParagraphStyleAt(index);
    }

    /**
     * Returns the number of the paragraph styles in the text of the view
     * of this controller.
     */
    public int getParagraphStyleCount() {
	return model.getRichText().getParagraphStyleCount();
    }

    /**
     * Returns all paragraph styles in the text of the view of this controller.
     */
    public ParagraphStyle[] getParagraphStyles() {
	return model.getRichText().getParagraphStyles();
    }

    /**
     * Returns the paragraph styles in the text of the view of this controller.
     *
     * @param begin the beginning index of the text to get paragraph styles,
     *              inclusive.
     * @param end   the ending index of the text to get paragraph styles,
     *              exclusive.
     */
    public ParagraphStyle[] getParagraphStyles(int begin, int end) {
	return model.getRichText().getParagraphStyles(begin, end);
    }

    /**
     * Returns an enumeration of the paragraph styles of the text of the
     * view of this controller.
     */
    public Enumeration paragraphStyles() {
	return model.getRichText().paragraphStyles();
    }

    /**
     * Returns an enumeration of the paragraph styles of the text of the
     * view of this controller.
     *
     * @param begin the beginning index to get styles, inclusive.
     * @param end   the ending index to get styles, exclusive.
     */
    public Enumeration paragraphStyles(int begin, int end) {
	return model.getRichText().paragraphStyles(begin, end);
    }

    /**
     * Returns the text of the view of this controller.
     * @see #setText(jp.kyasu.graphics.Text)
     */
    public Text getText() {
	return model.getRichText().getText();
    }

    /**
     * Sets the text of the view of this controller.
     * @see #getText()
     */
    public synchronized void setText(Text text) {
	view.setText(text);
	clearUndo();
    }

    /**
     * Returns the rich text of the view of this controller.
     * @see #setRichText(jp.kyasu.graphics.RichText)
     */
    public RichText getRichText() {
	return model.getRichText();
    }

    /**
     * Sets the rich text of the view of this controller.
     * @see #getRichText()
     */
    public synchronized void setRichText(RichText richText) {
	view.setRichText(richText);
	clearUndo();
    }

    /**
     * Returns the selected text from the view of this controller.
     */
    public Text getSelectedText() {
	return view.getSelectedText();
    }

    /**
     * Selects the text between the specified start and end positions.
     * @param selectionStart the start position of the text to select.
     * @param selectionEnd   the end position of the text to select.
     * @param scroll         if true, scrolls the view after selection done.
     * @see #select(int, int, boolean, boolean)
     */
    public void select(int selectionStart, int selectionEnd, boolean scroll) {
	select(selectionStart, selectionEnd, scroll, false);
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
    public void select(int selectionStart, int selectionEnd,
		       boolean scroll, boolean top)
    {
	int len = model.getRichText().length();
	if (selectionStart < 0) {
	    selectionStart = 0;
	}
	if (selectionEnd > len) {
	    selectionEnd = len;
	}
	if (selectionEnd < selectionStart) {
	    selectionEnd = selectionStart;
	}
	if (selectionStart > selectionEnd) {
	    selectionStart = selectionEnd;
	}
	TextPositionInfo begin = view.getTextPositionAt(selectionStart);
	TextPositionInfo end = view.getTextPositionNearby(begin, selectionEnd);
	select(begin, end, scroll, top);
    }

    protected synchronized void select(TextPositionInfo begin,
				       TextPositionInfo end,
				       boolean scroll, boolean top)
    {
	view.hideSelection();
	setSelectionBeginEnd(begin, end);
	if (scroll) {
	    if (top) {
		view.scrollTo(new Point(0, -view.getSelectionBegin().y));
	    }
	    else {
		view.scrollTo(view.getSelectionBegin());
	    }
	}
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Sets the position of the text insertion caret.
     * @param position the position of the caret.
     * @param top      if true, scrolls to the top of the view.
     * @see #setCaretPosition(int)
     */
    public void setCaretPosition(int position, boolean top) {
	if (position < 0) {
	    throw new IllegalArgumentException("position less than zero.");
	}
	int len = model.getRichText().length();
	if (position > len) {
	    position = len;
	}
	select(position, position, true, top);
    }

    /**
     * Inserts the specified text at the specified position in the view
     * of this controller.
     * @param text the text to insert.
     * @param pos  the position at which to insert.
     * @see #insert(jp.kyasu.graphics.Text, int, boolean)
     */
    public void insert(Text text, int pos) {
	insert(text, pos, true);
    }

    /**
     * Inserts the specified text at the specified position in the view
     * of this controller.
     * @param text the text to insert.
     * @param pos  the position at which to insert.
     * @param scroll if true, scrolls the view after the insertion.
     * @see #insert(jp.kyasu.graphics.Text, int)
     * @see #replaceRange(jp.kyasu.graphics.Text, int, int, boolean)
     */
    public void insert(Text text, int pos, boolean scroll) {
	replaceRange(text, pos, pos, scroll);
    }

    /**
     * Appends the given text to the current text.
     * @param text the text to append.
     * @see #append(jp.kyasu.graphics.Text, boolean)
     */
    public void append(Text text) {
	append(text, true);
    }

    /**
     * Appends the given text to the current text.
     * @param text the text to append.
     * @param scroll if true, scrolls the view after the appending.
     * @see #insert(jp.kyasu.graphics.Text, int, boolean)
     */
    public void append(Text text, boolean scroll) {
	insert(text, model.getRichText().length(), scroll);
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
	replaceRange(text, start, end, true);
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
	if (start > end) {
	    int i = start;
	    start = end;
	    end = i;
	}
	replaceRange(text,
		     view.getTextPositionAt(start),
		     view.getTextPositionAt(end),
		     scroll);
    }

    /**
     * Replaces string in the range of the current selection with the
     * specified replacement string.
     * @param str the string to use as the replacement.
     * @see #replaceSelection(jp.kyasu.graphics.Text)
     */
    public void replaceSelection(String str) {
	if (str == null) str = "";
	replaceSelection(new Text(str, typeInStyle));
    }

    /**
     * Replaces text in the range of the current selection with the
     * specified replacement text.
     * @param text the text to use as the replacement.
     * @see #replaceSelection(jp.kyasu.graphics.Text, boolean)
     */
    public void replaceSelection(Text text) {
	replaceSelection(text, true);
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
	replaceRange(text,
		     view.getSelectionBegin(),
		     view.getSelectionEnd(),
		     scroll);
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
	setRangeTextStyle(style, start, end, true);
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
	if (style == null)
	    return;
	if (start > end) {
	    int i = start;
	    start = end;
	    end = i;
	}
	changeTextStyle(style, null,
			view.getTextPositionAt(start),
			view.getTextPositionAt(end),
			scroll);
    }

    /**
     * Sets the text style in the range of the current selection to the
     * specified text style.
     * @param style the text style to be set.
     * @see #setSelectionTextStyle(jp.kyasu.graphics.TextStyle, boolean)
     */
    public void setSelectionTextStyle(TextStyle style) {
	setSelectionTextStyle(style, true);
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
	if (style == null)
	    return;
	changeTextStyle(style, null,
			view.getSelectionBegin(),
			view.getSelectionEnd(),
			scroll);
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
	modifyRangeTextStyle(modifier, start, end, true);
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
	if (modifier == null)
	    return;
	if (start > end) {
	    int i = start;
	    start = end;
	    end = i;
	}
	changeTextStyle(null, modifier,
			view.getTextPositionAt(start),
			view.getTextPositionAt(end),
			scroll);
    }

    /**
     * Modifies the text style in the range of the current selection by the
     * specified text style modifier.
     * @param modifier the text style modifier.
     * @see #modifySelectionTextStyle(jp.kyasu.graphics.TextStyleModifier, boolean)
     */
    public void modifySelectionTextStyle(TextStyleModifier modifier) {
	modifySelectionTextStyle(modifier, true);
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
	if (modifier == null)
	    return;
	changeTextStyle(null, modifier,
			view.getSelectionBegin(),
			view.getSelectionEnd(),
			scroll);
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
	setRangeParagraphStyle(style, start, end, true);
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
	if (style == null)
	    return;
	if (start > end) {
	    int i = start;
	    start = end;
	    end = i;
	}
	changeParagraphStyle(style, null,
			     view.getTextPositionAt(start),
			     view.getTextPositionAt(end),
			     scroll);
    }

    /**
     * Sets the paragraph style in the range of the current selection to the
     * specified paragraph style.
     * @param style the paragraph style to be set.
     * @see #setSelectionParagraphStyle(jp.kyasu.graphics.ParagraphStyle, boolean)
     */
    public void setSelectionParagraphStyle(ParagraphStyle style) {
	setSelectionParagraphStyle(style, true);
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
	if (style == null)
	    return;
	changeParagraphStyle(style, null,
			     view.getSelectionBegin(),
			     view.getSelectionEnd(),
			     scroll);
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
	modifyRangeParagraphStyle(modifier, start, end, true);
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
	if (modifier == null)
	    return;
	if (start > end) {
	    int i = start;
	    start = end;
	    end = i;
	}
	changeParagraphStyle(null, modifier,
			     view.getTextPositionAt(start),
			     view.getTextPositionAt(end),
			     scroll);
    }

    /**
     * Modifies the paragraph style in the range of the current selection by
     * the specified paragraph style modifier.
     * @param modifier the paragraph style modifier.
     * @see #modifySelectionParagraphStyle(jp.kyasu.graphics.ParagraphStyleModifier, boolean)
     */
    public void modifySelectionParagraphStyle(ParagraphStyleModifier modifier)
    {
	modifySelectionParagraphStyle(modifier, true);
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
	if (modifier == null)
	    return;
	changeParagraphStyle(null, modifier,
			     view.getSelectionBegin(),
			     view.getSelectionEnd(),
			     scroll);
    }

    /**
     * Prints the text in the view of this controller with the specified
     * flag determining to print a page number in footer.
     *
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(boolean printPageNum) {
	print(RichText.DEFAULT_PRINT_INSETS, null, printPageNum);
    }

    /**
     * Prints the text in the view of this controller with the specified
     * header string and flag determining to print a page number in footer.
     *
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(String header, boolean printPageNum) {
	print(RichText.DEFAULT_PRINT_INSETS, header, printPageNum);
    }

    /**
     * Prints the text in the view of this controller with the specified
     * insets, header string, and flag determining to print a page number
     * in footer.
     *
     * @param insets       the insets of a printing medium (paper).
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(Insets insets, String header, boolean printPageNum) {
	PrintJob job =
		view.getToolkit().getPrintJob(view.getFrame(), "Print", null);
	if (job == null)
	    return;
	print(job, insets, header, printPageNum);
    }

    /**
     * Prints the text in the view of this controller to a print device
     * provided from the specified print job, with the specified header string
     * and flag determining to print a page number in footer.
     *
     * @param job          the print job.
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(PrintJob job, String header, boolean printPageNum) {
	print(job, RichText.DEFAULT_PRINT_INSETS, header, printPageNum);
    }

    /**
     * Prints the text in the view of this controller to a print device
     * provided from the specified print job, with the specified insets,
     * header string, and flag determining to print a page number in footer.
     *
     * @param job          the print job.
     * @param insets       the insets of a printing medium (paper).
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public synchronized void print(PrintJob job, Insets insets,
				   String header, boolean printPageNum)
    {
	if (job == null || insets == null)
	    return;

	Cursor save = view.getCursor();
	try {
	    view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    model.getRichText().print(job, insets, view.getLineWrap(),
				      header, printPageNum);
	}
	finally { view.setCursor(save); }
    }


    // ================ Listener ================

    /**
     * Invoked when the mouse has been clicked on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseClicked(MouseEvent e) {
	// mousePressed -> mouseReleased -> mouseClicked

	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	int clickCount = e.getClickCount();
	if (clickCount < 2)
	    return;

	e.consume();

	TextPositionInfo posInfo = view.getTextPositionNearby(
							view.getVisibleBegin(),
							e.getPoint());

	if (clickCount == 2) {
	    if (posInfo.textIndex == 0 ||
		posInfo.textIndex >= model.getRichText().length())
	    {
		select_all();
		return;
	    }
	    if (select_braces(posInfo)) {
		return;
	    }
	    select_word(posInfo);
	    return;
	}
	else if (clickCount == 3) {
	    select_line(posInfo);
	    return;
	}
	else {
	    if (view.selectionIsCaret() &&
		view.getSelectionBegin().textIndex == posInfo.textIndex)
	    {
		return;
	    }
	    view.scrollTo(posInfo);
	    view.hideSelection();
	    setSelectionBeginEnd(posInfo);
	    view.showSelection();
	    notifyTextPositionListeners();
	    dragOrigin = dragCurrent = posInfo;
	}
    }

    /**
     * Invoked when the mouse has been pressed on a component.
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(MouseEvent e) {
	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	//if (clickToFocus) {
	if (view.isEditable()) {
	    view.requestFocus();
	    view.startTextCaret();
	    e.consume();
	}

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

	TextPositionInfo posInfo = view.getTextPositionNearby(
							view.getVisibleBegin(),
							e.getPoint());

	if (e.isShiftDown()) {
	    TextPositionInfo begin = view.getSelectionBegin();
	    TextPositionInfo end   = view.getSelectionEnd();
	    if (begin == null || end == null) {
		view.scrollTo(posInfo);
		view.hideSelection();
		setSelectionBeginEnd(posInfo);
		view.showSelection();
		dragOrigin = dragCurrent = posInfo;
	    }
	    else if (begin.textIndex <= posInfo.textIndex) {
		dragOrigin  = begin;
		dragCurrent = end;
	    }
	    else {
		dragOrigin  = end;
		dragCurrent = begin;
	    }
	    dragSelectionTo(posInfo);
	    return;
	}

	view.scrollTo(posInfo);
	view.hideSelection();
	setSelectionBeginEnd(posInfo);
	view.showSelection();
	dragOrigin = dragCurrent = posInfo;

	/*
	if (clickable && !e.isControlDown()) {
	    int textIndex = posInfo.textIndex;
	    if ((e.getX() - view.offset.x) < posInfo.x) {
		--textIndex;
	    }
	    Text text = model.getRichText().getText();
	    if (textIndex >= 0 && textIndex < text.length()) {
		TextStyle ts = text.getTextStyleAt(textIndex);
		ClickableTextAction action = ts.getClickableTextAction();
		if (action != null) {
		    view.performClickableTextAction(action);
		}
	    }
	}
	*/
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

	e.consume();

	dragOrigin = dragCurrent = null;

	if (e.getClickCount() < 2)
	    notifyTextPositionListeners();

	if (clickable && !e.isControlDown() && view.selectionIsCaret()) {
	    if (e.getClickCount() > 1)
		return;
	    if (e.isPopupTrigger() || e.isMetaDown())
		return;
	    if (e.isShiftDown())
		return;

	    TextPositionInfo posInfo = view.getSelectionBegin();
	    int textIndex = posInfo.textIndex;
	    if ((e.getX() - view.offset.x) < posInfo.x) {
		--textIndex;
	    }
	    Text text = model.getRichText().getText();
	    if (textIndex >= 0 && textIndex < text.length()) {
		TextStyle ts = text.getTextStyleAt(textIndex);
		ClickableTextAction action = ts.getClickableTextAction();
		if (action != null) {
		    view.performClickableTextAction(action);
		}
	    }
	}
    }

    /**
     * Invoked when the mouse enters a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseEntered(MouseEvent e) {
	if (!view.isEnabled())
	    return;
	if (!view.isEditable())
	    return;
	if (!clickToFocus) {
	    view.requestFocus();
	}
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

	e.consume();

	dragSelectionTo(view.getTextPositionNearby(
						view.getVisibleBegin(),
						e.getPoint()));
    }

    /**
     * Invoked when a key has been typed.
     * @see java.awt.event.KeyListener
     */
    public void keyTyped(KeyEvent e) {
	// keyPressed -> keyTyped -> keyReleased
	// This method is NOT invoked by the special keys (CTL, SHIFT, etc.).

	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (!view.isEditable()) {
	    e.consume();
	    return;
	}

	/*
	if (e.getKeyCode() != KeyEvent.VK_UNDEFINED)
	    return;
	*/

	KeyAction a = keyBinding.getKeyAction(e);
	if (a != null) {
	    a.perform(e.getKeyChar());
	    e.consume();
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

	if (!view.isEditable()) {
	    //view.getToolkit().beep();
	    e.consume();
	    return;
	}

	/*
	if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED)
	    return;
	*/

	KeyAction a = keyBinding.getKeyAction(e);
	if (a != null) {
	    a.perform(e.getKeyChar());
	    e.consume();
	}
    }

    /**
     * Invoked when a component gains the keyboard focus.
     * @see java.awt.event.FocusListener
     */
    public void focusGained(FocusEvent e) {
	//synchronized (view) {
	    if (!view.isEnabled())
		return;
	    //if (e.isTemporary()) return;
	    super.focusGained(e);
	    view.startTextCaret();
	//}
    }

    /**
     * Invoked when a component loses the keyboard focus.
     * @see java.awt.event.FocusListener
     */
    public void focusLost(FocusEvent e) {
	//synchronized (view) {
	    if (!view.isEnabled())
		return;
	    //if (e.isTemporary()) return;
	    super.focusLost(e);
	    view.stopTextCaret();
	//}
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
	    copy_clipboard();
	}
	else if (command.equals(A_CUT)) {
	    cut_clipboard();
	}
	else if (command.equals(A_PASTE)) {
	    if (!isShiftDown)
		paste_clipboard();
	    else
		paste_cutbuffer();
	}
	else if (command.equals(A_FIND)) {
	    find_word();
	}
	else if (command.equals(A_UNDO)) {
	    undo();
	}
	else if (command.equals(A_PRINT)) {
	    print(true);
	}
    }


    // ================ Functions ================

    static protected final String OPEN_BRACES  = "({[<\"'";
    static protected final String CLOSE_BRACES = ")}]>\"'";

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts character at the insertion cursor.
     */
    public void insert_character(char c) {
	if (c == Text.LINE_BREAK_CHAR &&
	    !model.getRichText().getRichTextStyle().
					isJavaLineSeparatorWithBreak())
	{
	    return;
	}
	typeInText.setChar(0, c);
	replaceSelection(typeInText);
    }

    /**
     * Undo the last change.
     */
    public void undo() {
	if (lastUndo != null) {
	    lastUndo = lastUndo.undo();
	}
    }

    /**
     * Clears the undo of the last change.
     */
    public void clearUndo() {
	lastUndo = null;
    }

    /**
     * Sets the undo.
     */
    public void setUndo(Undo undo) {
	lastUndo = undo;
    }

    /**
     * Copies the current selection to the clipboard.
     */
    public void copy_clipboard() {
	Text text = view.getSelectedText();
	if (!text.isEmpty()) {
	    setCutBuffer(text);
	    setClipboardText(text);
	}
    }

    /**
     * Cuts the current selection to the clipboard.
     */
    public void cut_clipboard() {
	Text text = view.getSelectedText();
	if (!text.isEmpty()) {
	    setCutBuffer(text);
	    setClipboardText(text);
	    replaceSelection(new Text());
	}
    }

    /**
     * Pastes the the cut buffer before the insertion cursor.
     */
    public void paste_cutbuffer() {
	Text text = getCutBufferText();
	if (text != null) {
	    replaceSelection(text);
	}
    }

    /**
     * Pastes the the clipboard before the insertion cursor.
     */
    public void paste_clipboard() {
	if (lostClipboardOwnership) {
	    Text text = getClipboardText();
	    if (text != null) {
		replaceSelection(text);
	    }
	}
	else {
	    paste_cutbuffer();
	}
    }

    /**
     * Select all text.
     */
    public void select_all() {
	view.hideSelection();
	setSelectionBeginEnd(
	    view.getTextPositionAt(0),
	    view.getTextPositionAt(model.getRichText().length()));
	view.showSelection();
	view.scrollTo(view.getSelectionBegin());
	notifyTextPositionListeners();
    }

    /**
     * Select braces at the selection start position.
     * @see #select_braces(jp.kyasu.graphics.text.TextPositionInfo)
     */
    public boolean select_braces() {
	return select_braces(view.getSelectionBegin());
    }

    /**
     * Select braces at the specified text position.
     * @see #select_braces()
     */
    public boolean select_braces(TextPositionInfo posInfo) {
	int index = posInfo.textIndex;
	Text text = model.getRichText().getText();
	int bIndex;
	if (index > 0 &&
	    (bIndex = OPEN_BRACES.indexOf(text.getChar(index - 1))) >= 0)
	{
	    int matchCount = 0;
	    char ob = OPEN_BRACES.charAt(bIndex);
	    char cb = CLOSE_BRACES.charAt(bIndex);
	    CharacterIterator iterator = text.getCharacterIterator(index);
	    for (char c = iterator.current();
		 c != CharacterIterator.DONE;
		 c = iterator.next())
	    {
		if (c == cb) {
		    if (matchCount == 0) {
			view.hideSelection();
			setSelectionBeginEnd(
			    posInfo,
			    view.getTextPositionNearby(posInfo,
						       iterator.getIndex()));
			view.showSelection();
			view.scrollTo(view.getSelectionBegin());
			notifyTextPositionListeners();
			return true;
		    }
		    --matchCount;
		}
		else if (c == ob) {
		    matchCount++;
		}
	    }
	}
	if (index > 0 && index < text.length() &&
	    (bIndex = CLOSE_BRACES.indexOf(text.getChar(index))) >= 0)
	{
	    int matchCount = 0;
	    char ob = OPEN_BRACES.charAt(bIndex);
	    char cb = CLOSE_BRACES.charAt(bIndex);
	    CharacterIterator iterator = text.getCharacterIterator(index - 1);
	    for (char c = iterator.current();
		 c != CharacterIterator.DONE;
		 c = iterator.previous())
	    {
		if (c == ob) {
		    if (matchCount == 0) {
			view.hideSelection();
			setSelectionBeginEnd(
			    view.getTextPositionNearby(posInfo,
						       iterator.getIndex() + 1),
			    posInfo);
			view.showSelection();
			view.scrollTo(view.getSelectionBegin());
			notifyTextPositionListeners();
			return true;
		    }
		    --matchCount;
		}
		else if (c == cb) {
		    matchCount++;
		}
	    }
	}
	return false;
    }

    /**
     * Select a line at the selection start position.
     * @see #select_line(jp.kyasu.graphics.text.TextPositionInfo)
     */
    public void select_line() {
	select_line(view.getSelectionBegin());
    }

    /**
     * Select a line at the specified text position.
     * @see #select_line()
     */
    public void select_line(TextPositionInfo posInfo) {
	int paraBegin =
		model.getRichText().paragraphBeginIndexOf(posInfo.textIndex);
	int paraEnd =
		model.getRichText().paragraphEndIndexOf(posInfo.textIndex);
	view.hideSelection();
	setSelectionBeginEnd(
	    view.getTextPositionNearby(posInfo, paraBegin),
	    view.getTextPositionNearby(posInfo, paraEnd));
	view.showSelection();
	view.scrollTo(view.getSelectionBegin());
	notifyTextPositionListeners();
    }

    /**
     * Select a word at the selection start position.
     * @see #select_word(jp.kyasu.graphics.text.TextPositionInfo)
     */
    public void select_word() {
	select_word(view.getSelectionBegin());
    }

    /**
     * Select a word at the specified text position.
     * @see #select_word()
     */
    public void select_word(TextPositionInfo posInfo) {
	Text text = model.getRichText().getText();
	int index = posInfo.textIndex;
	if (index >= text.length())
	    index = text.length() - 1;
	BreakIterator boundary= BreakIterator.getWordInstance(view.getLocale());
	boundary.setText(text.getCharacterIterator(index));
	int end = boundary.following(index);
	int begin = boundary.previous();
	if (begin != BreakIterator.DONE &&
	    end != BreakIterator.DONE &&
	    begin <= index && index < end)
	{
	    view.hideSelection();
	    setSelectionBeginEnd(
		view.getTextPositionNearby(posInfo, begin),
		view.getTextPositionNearby(posInfo, end));
	    view.showSelection();
	    view.scrollTo(view.getSelectionBegin());
	    notifyTextPositionListeners();
	}
    }

    /**
     * Finds the word and move the insertion cursor to the founded word.
     * @see #find_word(java.lang.String)
     * @see #find_word(java.lang.String, java.lang.String)
     * @see #find_word(java.lang.String, java.lang.String, int)
     */
    public void find_word() {
	String sel = view.getSelectedText().toString();
	if (sel.length() > 32)
	    sel = sel.substring(0, 32);
	Dialog dialog = createFindDialog(sel);
	dialog.setVisible(true);
    }

    /**
     * Finds the word and move the insertion cursor to the founded word.
     * @param find the word to be found.
     * @return true if the word has been found, false otherwise.
     * @see #find_word()
     * @see #find_word(java.lang.String, java.lang.String)
     * @see #find_word(java.lang.String, java.lang.String, int)
     */
    public boolean find_word(String find) {
	return find_word(find, null);
    }

    /**
     * Finds the word and move the insertion cursor to the founded word.
     * @param find the word to be found.
     * @param rep  the replacement string.
     * @return true if the word has been found, false otherwise.
     * @see #find_word()
     * @see #find_word(java.lang.String)
     * @see #find_word(java.lang.String, java.lang.String, int)
     */
    public boolean find_word(String find, String rep) {
	return find_word(find, rep, view.getSelectionBegin().textIndex + 1);
    }

    /**
     * Finds the word and move the insertion cursor to the founded word.
     * @param find       the word to be found.
     * @param rep        the replacement string.
     * @param startIndex the starting index to find.
     * @return true if the word has been found, false otherwise.
     * @see #find_word(java.lang.String)
     * @see #find_word(java.lang.String, java.lang.String)
     * @see #find_word(java.lang.String, java.lang.String, int)
     */
    public boolean find_word(String find, String rep, int startIndex) {
	if (find == null || find.length() == 0)
	    return false;
	Text text = model.getRichText().getText();
	if (startIndex >= text.length()) {
	    startIndex = 0;
	}
	int textIndex = text.indexOf(find, startIndex);
	if (textIndex >= 0) {
	    TextPositionInfo posInfo = view.getTextPositionAt(textIndex);
	    view.hideSelection();
	    setSelectionBeginEnd(
		posInfo,
		view.getTextPositionNearby(posInfo, textIndex + find.length()));
	    if (rep != null) {
		view.showSelection();
		replaceSelection(rep);
	    }
	    else {
		view.scrollTo(view.getSelectionBegin());
		view.showSelection();
		notifyTextPositionListeners();
	    }
	    return true;
	}
	else {
	    if (Dialog.confirm(view.getFrame(),
		    getResourceString(
			"kfc.text.findContinueLabel",
			"End of text reached; continue from beggining?")))
	    {
		return find_word(find, rep, 0);
	    }
	}
	return false;
    }

    /**
     * Creates a dialog for finding a word.
     * @param initStr the initial string to prompt.
     * @see #find_word()
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
	// Do not transfer focus.
	Keymap keymap = ffield.getKeymap();
	keymap.setKeyCodeMap(KeyEvent.VK_TAB, "tab");
	ffield.setKeymap(keymap);
	if (initStr != null) {
	    ffield.setText(initStr);
	}
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(ffield, c);
	p1.add(ffield);
	label = new Label(
		getResourceString("kfc.text.replaceFieldLabel", "Replace:"));
	if (!view.isEditable()) label.setEnabled(false);
	c.gridwidth = GridBagConstraints.RELATIVE;
	gridbag.setConstraints(label, c);
	p1.add(label);
	final TextField rfield = new TextField(30);
	// Do not transfer focus.
	keymap = rfield.getKeymap();
	keymap.setKeyCodeMap(KeyEvent.VK_TAB, "tab");
	rfield.setKeymap(keymap);
	if (!view.isEditable()) rfield.setEditable(false);
	c.gridwidth = GridBagConstraints.REMAINDER;
	gridbag.setConstraints(rfield, c);
	p1.add(rfield);
	dialog.add(p1, BorderLayout.CENTER);

	Panel p2 = new Panel();
	Button b = new Button(
			getResourceString("kfc.text.findStartLabel", "Find"));
	ActionListener al = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		view.setSelectionVisible(true);
		find_word(ffield.getText(), null);
	    }
	};
	ffield.addActionListener(al);
	b.addActionListener(al);
	p2.add(b);
	/*
	Button repB = new Button(
		getResourceString("kfc.text.replaceStartLabel", "Replace"));
	*/
	Button repB = new Button(new VActiveButton(
		getResourceString("kfc.text.replaceStartLabel", "Replace")));
	repB.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		view.setSelectionVisible(true);
		find_word(ffield.getText(), rfield.getText());
	    }
	});
	if (!view.isEditable()) repB.setEnabled(false);
	p2.add(repB);
	/*
	Button repAllB = new Button(
		getResourceString("kfc.text.replaceAllLabel", "Replace All"));
	*/
	Button repAllB = new Button(new VActiveButton(
		getResourceString("kfc.text.replaceAllLabel", "Replace All")));
	repAllB.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		view.setSelectionVisible(true);
		while (find_word(ffield.getText(), rfield.getText()))
		    view.setSelectionVisible(true);
	    }
	});
	if (!view.isEditable()) repAllB.setEnabled(false);
	p2.add(repAllB);
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


    // ================ Protected ================

    /**
     * Creates a menu for the editing.
     */
    protected Menu createEditMenu() {
	Menu menu = new Menu(L_EDIT);
	menu.add(createMenuItem(L_COPY,  A_COPY,  null));
	menu.add(createMenuItem(L_CUT,   A_CUT,   null));
	menu.add(createMenuItem(L_PASTE, A_PASTE, null));
	menu.addSeparator();
	menu.add(createMenuItem(L_FIND,  A_FIND,  null));
	menu.addSeparator();
	menu.add(createMenuItem(L_UNDO,  A_UNDO,  null));
	menu.addSeparator();
	menu.add(createMenuItem(L_PRINT, A_PRINT, null));
	return menu;
    }

    /**
     * Creates a popup menu.
     */
    protected PopupMenu createPopupMenu() {
	PopupMenu menu = new PopupMenu();
	menu.add(createMenuItem(L_COPY,  A_COPY,  null));
	menu.add(createMenuItem(L_CUT,   A_CUT,   null));
	menu.add(createMenuItem(L_PASTE, A_PASTE, null));
	menu.addSeparator();
	menu.add(createMenuItem(L_FIND,  A_FIND,  null));
	menu.addSeparator();
	menu.add(createMenuItem(L_UNDO,  A_UNDO,  null));
	menu.addSeparator();
	menu.add(createMenuItem(L_PRINT, A_PRINT, null));
	return menu;
    }

    /**
     * Creates a menu item.
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
     * Enables or disaples the specified menu.
     */
    protected void setMenuEnabled(Menu menu, boolean b) {
	int count = menu.getItemCount();
	for (int i = 0; i < count; i++) {
	    MenuItem item = menu.getItem(i);
	    /*
	    if (!L_COPY.equals(item.getLabel()) &&
	    	!L_FIND.equals(item.getLabel()) &&
	    	!L_PRINT.equals(item.getLabel()))
	    */
	    if (!A_COPY.equals(item.getActionCommand()) &&
	    	!A_FIND.equals(item.getActionCommand()) &&
	    	!A_PRINT.equals(item.getActionCommand()))
	    {
		item.setEnabled(b);
	    }
	}
    }

    /**
     * Sets the beginning position of the selection.
     */
    protected void setSelectionBegin(TextPositionInfo posInfo) {
	view.selectionBegin = posInfo;
	setCurrentTypeIn(posInfo);
    }

    /**
     * Sets the ending position of the selection.
     */
    protected void setSelectionEnd(TextPositionInfo posInfo) {
	view.selectionEnd = posInfo;
    }

    /**
     * Sets the beginning and ending positions of the selection.
     */
    protected void setSelectionBeginEnd(TextPositionInfo posInfo) {
	view.selectionBegin = view.selectionEnd = posInfo;
	setCurrentTypeIn(posInfo);
    }

    /**
     * Sets the beginning and ending positions of the selection.
     */
    protected void setSelectionBeginEnd(TextPositionInfo begin,
					TextPositionInfo end)
    {
	view.selectionBegin = begin;
	view.selectionEnd = end;
	setCurrentTypeIn(begin);
    }

    /**
     * Notifies the text position event to the text position listeners.
     */
    protected void notifyTextPositionListeners() {
	if (selectionVisibleAtFocus && !view.selectionIsCaret() &&
	    !view.isSelectionVisible())
	{
	    view.setSelectionVisible(true);
	}
	view.notifyTextPositionListeners();
    }

    /**
     * Sets the current (type in) text style from the specified text position.
     */
    protected void setCurrentTypeIn(TextPositionInfo posInfo) {
	Text text = model.getRichText().getText();
	int length = text.length();
	if (posInfo == null || length == 0) {
	    typeInStyle = model.getRichText().getRichTextStyle().getTextStyle();
	    typeInText = new Text(" ", typeInStyle);
	    return;
	}
	int index = posInfo.textIndex;
	if (index > 0) --index;
	typeInStyle = text.getTextStyleAt(index);
	typeInText = new Text(" ", typeInStyle);
    }

    /**
     * Expands and moves the selection according to the specified text position.
     */
    protected void dragSelectionTo(TextPositionInfo posInfo) {
	if (dragOrigin == null || dragCurrent == null) {
	    // not happen
	    dragOrigin = dragCurrent = posInfo;
	    view.scrollTo(posInfo);
	    view.hideSelection();
	    setSelectionBeginEnd(posInfo);
	    view.showSelection();
	    return;
	}
	if (dragCurrent.textIndex == posInfo.textIndex) {
	    return;
	}

	view.scrollTo(posInfo);

	if (dragOrigin.textIndex == posInfo.textIndex) {
	    dragCurrent = posInfo;
	    view.hideSelection();
	    setSelectionBeginEnd(posInfo);
	    view.showSelection();
	    return;
	}
	if (dragOrigin.textIndex == dragCurrent.textIndex) {
	    view.hideSelection();
	    dragCurrent = posInfo;
	    if (dragOrigin.textIndex < dragCurrent.textIndex)
		setSelectionBeginEnd(dragOrigin, dragCurrent);
	    else
		setSelectionBeginEnd(dragCurrent, dragOrigin);
	    view.showSelection();
	    return;
	}
	if (dragOrigin.textIndex < dragCurrent.textIndex) {
	    if (posInfo.textIndex < dragOrigin.textIndex) {
		view.hideSelection();
		setSelectionBeginEnd(posInfo, dragOrigin);
		view.showSelection();
		dragCurrent = posInfo;
	    }
	    else if (dragCurrent.textIndex < posInfo.textIndex) {
		view.paintSelection(dragCurrent,
				    posInfo,
				    view.getSelectionForeground(),
				    view.getSelectionBackground());
		setSelectionBeginEnd(dragOrigin, posInfo);
		dragCurrent = posInfo;
	    }
	    else { // dragOrigin < posInfo < dragCurrent
		view.paintSelection(posInfo,
				    dragCurrent,
				    view.getForeground(),
				    view.getBackground());
		setSelectionBeginEnd(dragOrigin, posInfo);
		dragCurrent = posInfo;
	    }
	}
	else { // dragCurrent.textIndex < dragOrigin.textIndex
	    if (posInfo.textIndex < dragCurrent.textIndex) {
		view.paintSelection(posInfo,
				    dragCurrent,
				    view.getSelectionForeground(),
				    view.getSelectionBackground());
		setSelectionBeginEnd(posInfo, dragOrigin);
		dragCurrent = posInfo;
	    }
	    else if (dragOrigin.textIndex < posInfo.textIndex) {
		view.hideSelection();
		setSelectionBeginEnd(dragOrigin, posInfo);
		view.showSelection();
		dragCurrent = posInfo;
	    }
	    else { // dragCurrent < posInfo < dragOrigin
		view.paintSelection(dragCurrent,
				    posInfo,
				    view.getForeground(),
				    view.getBackground());
		setSelectionBeginEnd(posInfo, dragOrigin);
		dragCurrent = posInfo;
	    }
	}
    }

    /**
     * Replaces the text of the model with the specified text.
     *
     * @param text   the replacement text.
     * @param begin  the beginning position to replace, inclusive.
     * @param end    the endign position to replace, exclusive.
     * @param scroll if true, scrolls the view after replace done.
     */
    protected synchronized void replaceRange(Text text,
					     TextPositionInfo begin,
					     TextPositionInfo end,
					     boolean scroll)
    {
	if (!view.isEditable() || begin == null || end == null)
	    return;
	if (begin.textIndex == end.textIndex && text.length() == 0)
	    return;

	lastUndo = model.replace(begin.textIndex, end.textIndex, text);

	if (view.isShowing()) {
	    if (scroll) {
	    	view.scrollTo(view.getSelectionBegin());
	    }
	}

	/*
	int newIndex = begin.textIndex + text.length();
	if (newIndex < 0)
	    newIndex = 0;
	else if (newIndex > model.getRichText().length)
	    newIndex = model.getRichText().length;
	TextPositionInfo newPos = null;
	if (view.getSelectionBegin().textIndex != newIndex) {
	    if (view.getSelectionEnd().textIndex == newIndex)
		newPos = view.getSelectionEnd();
	    else
		newPos = view.getTextPositionAt(newIndex);
	}
	else if (view.getSelectionEnd().textIndex != newIndex) {
	    // view.getSelectionBegin().textIndex == newIndex
	    newPos = view.getSelectionBegin();
	}

	if (!view.isShowing()) {
	    if (newPos != null) {
		setSelectionBeginEnd(newPos);
	    }
	}
	else {
	    if (newPos != null) {
		view.hideSelection();
		setSelectionBeginEnd(newPos);
		view.showSelection(); // show caret
	    }
	    if (scroll) {
	    	view.scrollTo(view.getSelectionBegin());
	    }
	}
	*/
    }

    /**
     * Changes the text style of the model with the specified style or modifier.
     *
     * @param style    the new text style or <code>null</code>.
     * @param modifier the text style modifier or <code>null</code>.
     * @param begin    the beginning position to change, inclusive.
     * @param end      the endign position to change, exclusive.
     * @param scroll   if true, scrolls the view after change done.
     */
    protected synchronized void changeTextStyle(TextStyle style,
						TextStyleModifier modifier,
						TextPositionInfo begin,
						TextPositionInfo end,
						boolean scroll)
    {
	if (!view.isEditable() || begin == null || end == null)
	    return;
	if (begin.textIndex == end.textIndex)
	    return;

	/*
	int selBegin = view.getSelectionBegin().textIndex;
	int selEnd   = view.getSelectionEnd().textIndex;
	*/

	if (style != null) {
	    lastUndo =
		model.setTextStyle(begin.textIndex, end.textIndex, style);
	}
	else if (modifier != null) {
	    lastUndo =
		model.modifyTextStyle(begin.textIndex, end.textIndex, modifier);
	}
	else {
	    return;
	}

	if (view.isShowing()) {
	    if (scroll) {
	    	view.scrollTo(view.getSelectionBegin());
	    }
	}

	/*
	TextPositionInfo selBeginPos = null;
	TextPositionInfo selEndPos   = null;
	if (view.getSelectionBegin().textIndex != selBegin) {
	    selBeginPos = view.getTextPositionAt(selBegin);
	}
	if (view.getSelectionEnd().textIndex != selEnd) {
	    selEndPos = view.getTextPositionAt(selEnd);
	}

	if (!view.isShowing()) {
	    if (selBeginPos != null || selEndPos != null) {
		setSelectionBeginEnd(selBeginPos, selEndPos);
	    }
	}
	else {
	    if (selBeginPos != null || selEndPos != null) {
		view.hideSelection();
		setSelectionBeginEnd(selBeginPos, selEndPos);
		view.showSelection();
	    }
	    if (scroll) {
	    	view.scrollTo(view.getSelectionBegin());
	    }
	}
	*/
    }

    /**
     * Changes the paragraph style of the model with the specified style or
     * modifier.
     *
     * @param style    the new paragraph style or <code>null</code>.
     * @param modifier the paragraph style modifier or <code>null</code>.
     * @param begin    the beginning position to change, inclusive.
     * @param end      the endign position to change, exclusive.
     * @param scroll   if true, scrolls the view after change done.
     */
    protected synchronized void changeParagraphStyle(ParagraphStyle style,
						     ParagraphStyleModifier modifier,
						     TextPositionInfo begin,
						     TextPositionInfo end,
						     boolean scroll)
    {
	if (!view.isEditable() || begin == null || end == null)
	    return;

	/*
	int selBegin = view.getSelectionBegin().textIndex;
	int selEnd   = view.getSelectionEnd().textIndex;
	*/

	if (style != null) {
	    lastUndo =
		model.setParagraphStyle(begin.textIndex, end.textIndex, style);
	}
	else if (modifier != null) {
	    lastUndo = model.modifyParagraphStyle(begin.textIndex,
						  end.textIndex, modifier);
	}
	else {
	    return;
	}

	if (view.isShowing()) {
	    if (scroll) {
	    	view.scrollTo(view.getSelectionBegin());
	    }
	}

	/*
	TextPositionInfo selBeginPos = null;
	TextPositionInfo selEndPos   = null;
	if (view.getSelectionBegin().textIndex != selBegin) {
	    selBeginPos = view.getTextPositionAt(selBegin);
	}
	if (view.getSelectionEnd().textIndex != selEnd) {
	    if (selBegin == selEnd && selBeginPos != null)
		selEndPos = selBeginPos;
	    else
		selEndPos = view.getTextPositionAt(selEnd);
	}

	if (!view.isShowing()) {
	    if (selBeginPos != null || selEndPos != null) {
		setSelectionBeginEnd(selBeginPos, selEndPos);
	    }
	}
	else {
	    if (selBeginPos != null || selEndPos != null) {
		view.hideSelection();
		setSelectionBeginEnd(selBeginPos, selEndPos);
		view.showSelection();
	    }
	    if (scroll) {
	    	view.scrollTo(view.getSelectionBegin());
	    }
	}
	*/
    }

    // /*if[JDK1.2]

    /**
     * Adds this controller to the view.
     */
    // /*if[JDK1.2]
    protected void addToView() {
	super.addToView();
	getView().addInputMethodListener(this);
    }

    /**
     * Invoked when the text entered through an input method has changed.
     */
    // /*if[JDK1.2]
    public void inputMethodTextChanged(InputMethodEvent e) {
	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (!view.isEditable()) {
	    //view.getToolkit().beep();
	    e.consume();
	    return;
	}

	replaceInputMethodText(e);
	setInputMethodCaretPosition(e);

	e.consume();
    }

    /**
     * Invoked when the caret within composed text has changed.
     */
    // /*if[JDK1.2]
    public void caretPositionChanged(InputMethodEvent e) {
	if (!view.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (!view.isEditable()) {
	    //view.getToolkit().beep();
	    e.consume();
	    return;
	}

	setInputMethodCaretPosition(e);

	e.consume();
    }


    protected TextStyle savedTypeInStyle;
    protected TextPositionInfo composedTextBegin;
    protected TextPositionInfo composedTextEnd;


    //
    // Replaces the current input method (composed) text according to
    // the passed input method event. This method also inserts the
    // committed text into the document.
    //
    protected void replaceInputMethodText(InputMethodEvent e) {
	int commitCount = e.getCommittedCharacterCount();
	AttributedCharacterIterator text = e.getText();

	if (text == null) {
	    // old composed text deletion
	    if (composedTextBegin != null) {
		replaceRange(new Text("", savedTypeInStyle),
			     composedTextBegin, composedTextEnd, true);
		composedTextBegin = composedTextEnd = null;
	    }
	    if (savedTypeInStyle != null) {
		typeInStyle = savedTypeInStyle;
		typeInText = new Text(" ", typeInStyle);
		savedTypeInStyle = null;
	    }
	}
	else {
	    if (savedTypeInStyle == null) {
		savedTypeInStyle = typeInStyle;
	    }
	    TextBuffer buffer = new TextBuffer();
	    buffer.setTextStyle(savedTypeInStyle);

	    // committed text insertion
	    if (commitCount > 0) {
		for (char c = text.first();
		     commitCount > 0;
		     c = text.next(), commitCount--)
		{
		    buffer.append(c);
		}
	    }

	    int composedTextIndex = text.getIndex();
	    if (composedTextIndex >= text.getEndIndex()) {
		if (composedTextBegin != null) {
		    replaceRange(buffer.toText(),
				 composedTextBegin, composedTextEnd, true);
		    composedTextBegin = composedTextEnd = null;
		}
		else {
		    replaceSelection(buffer.toText());
		}
		if (savedTypeInStyle != null) {
		    typeInStyle = savedTypeInStyle;
		    typeInText = new Text(" ", typeInStyle);
		    savedTypeInStyle = null;
		}
	    }
	    else { // composedTextIndex < text.getEndIndex()
		// new composed text insertion
		ComposedTextStyle composedStyle =
			new ComposedTextStyle(savedTypeInStyle, null);
		BasicTSModifier modifier = new BasicTSModifier();
		modifier.put(BasicTSModifier.COLOR,
			     view.getSelectionForeground());
		ComposedTextStyle selectedStyle =
			new ComposedTextStyle(
					modifier.modify(savedTypeInStyle),
					view.getSelectionBackground());
		buffer.setTextStyle(composedStyle);

		int commitLen = buffer.length();
		char c = text.setIndex(composedTextIndex);
		while (c != CharacterIterator.DONE) {
		    Map attrs = text.getAttributes();
		    InputMethodHighlight hl =
		    	(InputMethodHighlight)attrs.get(
					TextAttribute.INPUT_METHOD_HIGHLIGHT);
		    if (hl == null) {
			buffer.setTextStyle(composedStyle);
		    }
		    else {
			if (hl.isSelected()) {
			    buffer.setTextStyle(selectedStyle);
			}
			else {
			    buffer.setTextStyle(composedStyle);
			}
			switch (hl.getState()) {
			case InputMethodHighlight.RAW_TEXT:
			case InputMethodHighlight.CONVERTED_TEXT:
			}
		    }
		    int runLimit = text.getRunLimit();
		    while (text.getIndex() < runLimit) {
			buffer.append(c);
			c = text.next();
		    }
		    c = text.setIndex(runLimit);
		}

		if (composedTextBegin != null) {
		    replaceRange(buffer.toText(),
				 composedTextBegin, composedTextEnd, true);
		    composedTextEnd =
			view.getTextPositionAt(
				composedTextBegin.textIndex + buffer.length());
		}
		else {
		    replaceSelection(buffer.toText());
		    composedTextEnd = view.getSelectionEnd();
		}
		composedTextBegin =
			view.getTextPositionNearby(composedTextEnd,
				composedTextEnd.textIndex -
						(buffer.length() - commitLen));
	    }
	}
    }

    //
    // Sets the caret position according to the passed input method
    // event. Also, sets/resets composed text caret appropriately.
    //
    protected void setInputMethodCaretPosition(InputMethodEvent e) {
	if (composedTextBegin != null) {
	    int index = composedTextBegin.textIndex;
	    TextHitInfo caretPos = e.getCaret();
	    if (caretPos != null) {
		index += caretPos.getInsertionIndex();
	    }
	    TextPositionInfo pos =
	    	view.getTextPositionNearby(composedTextBegin, index);
	    select(pos, pos, true, false);
	}
    }


    static class ComposedTextStyle extends TextStyle {
	static Stroke DashedStroke =
		new BasicStroke(1.0f,
	    			BasicStroke.CAP_SQUARE,
				BasicStroke.JOIN_MITER,
				10.0f,
				new float[]{ 2.0f, 2.0f },
				0.0f);

	Color selectionColor;

	public ComposedTextStyle(TextStyle textStyle, Color selectionColor) {
	    super(textStyle.getFont(),
		  textStyle.getExtendedFont().getColor(),
		  false);
	    this.selectionColor = selectionColor;
	}

	public void drawText(Graphics g, char text[], int offset, int length,
			     boolean isRunStart, boolean isRunEnd,
			     int x, int y, int width, int height,
			     int baseLine)
	{
	    if (selectionColor != null) {
		Color color = g.getColor();
		g.setColor(selectionColor);
		g.fillRect(x, y, width, height);
		g.setColor(color);
	    }

	    super.drawText(g, text, offset, length, isRunStart, isRunEnd,
			   x, y, width, height, baseLine);

	    Graphics2D g2 = (Graphics2D)g;
	    Stroke s = g2.getStroke();
	    g2.setStroke(DashedStroke);
	    g2.drawLine(x, y + height - 1, x + width, y + height - 1);
	    g2.setStroke(s);
	}

	public int hashCode() {
	    int hash = super.hashCode();
	    if (selectionColor != null) {
		hash ^= selectionColor.hashCode();
	    }
	    return hash;
	}

	public boolean equals(Object anObject) {
	    if (this == anObject)
		return true;
	    if (anObject == null)
		return false;
	    if (getClass() == anObject.getClass()) {
		ComposedTextStyle style = (ComposedTextStyle)anObject;
		return equalsFontAndAction(style) &&
			(selectionColor == null ?
				style.selectionColor == null :
				selectionColor.equals(style.selectionColor));
	    }
	    return false;
	}
    }

    /*end[JDK1.2]*/

}
