/*
 * RichTextEditor.java
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

package jp.kyasu.editor;

import jp.kyasu.awt.Button;
import jp.kyasu.awt.DefaultTextEditModel;
import jp.kyasu.awt.Dialog;
import jp.kyasu.awt.TextEditModel;
import jp.kyasu.awt.ToggleButton;
import jp.kyasu.graphics.BasicPSModifier;
import jp.kyasu.graphics.BasicTSModifier;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextAttachment;
import jp.kyasu.graphics.TextBuffer;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.VHRBorder;
import jp.kyasu.graphics.VImage;
import jp.kyasu.graphics.Visualizable;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;

/**
 * The <code>RichTextEditor</code> is an abstract base class of multi-line
 * text area that is suited for editing styled documents.
 *
 * @see 	jp.kyasu.editor.DocumentEditor
 * @see 	jp.kyasu.editor.HTMLEditor
 *
 * @version 	20 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public abstract class RichTextEditor extends TextEditor {
    protected ToggleButton boldButton;
    protected ToggleButton italicButton;
    protected ToggleButton underlineButton;


    static public final String L_FORMAT      = "format";
    static public final String L_INSERT      = "insert";
    static public final String L_ALIGN       = "align";
    static public final String L_LIST        = "list";

    static public final String I_BOLD        = "bold";
    static public final String I_ITALIC      = "italic";
    static public final String I_UNDERLINE   = "underline";

    static public final String A_BOLD        = I_BOLD;
    static public final String A_ITALIC      = I_ITALIC;
    static public final String A_UNDERLINE   = I_UNDERLINE;

    static public final String A_CLEAR_STYLE = "clearStyle";
    static public final String A_LARGE       = "large";
    static public final String A_SMALL       = "small";
    static public final String A_LEFT        = "left";
    static public final String A_CENTER      = "center";
    static public final String A_RIGHT       = "right";
    static public final String A_INC_INDENT  = "incindent";
    static public final String A_DEC_INDENT  = "decindent";

    static public final String A_IMAGE       = "image";
    static public final String A_HR          = "hr";


    /**
     * Constructs a new rich text area with tool bar, with the specified
     * number of rows and columns. If <code>showToolBar</code> is true,
     * then shows the tool bar initially; otherwise hidden.
     * This rich text area is created with vertical scroll bar.
     * @param rows                the number of rows
     * @param columns             the number of columns.
     * @param showToolBar         if true, then shows the tool bar initially;
     *                            otherwise hides.
     * @param openActionListener  the action listener that receives action
     *                            events from the open button in tool bar.
     * @param saveActionListener  the action listener that receives action
     *                            events from the save button in tool bar.
     * @param printActionListener the action listener that receives action
     *                            events from the print button in tool bar.
     */
    public RichTextEditor(int rows, int columns, boolean showToolBar,
			  ActionListener openActionListener,
			  ActionListener saveActionListener,
			  ActionListener printActionListener)
    {
	super(rows, columns, showToolBar, openActionListener,
					  saveActionListener,
					  printActionListener);
    }


    protected TextEditModel createDefaultTextEditModel() {
	return new DefaultTextEditModel(RichTextStyle.DEFAULT_DOCUMENT_STYLE);
    }


    /**
     * Sets the font name of the selected text to be the specified name.
     */
    public void set_font_name(String name) {
	if (name == null)
	    throw new NullPointerException();
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.NAME, name);
	modifySelectionTextStyle(modifier);
    }

    /**
     * Sets the font name of the selected text to be the selected name.
     * Selected item must be a <code>String</code>.
     */
    public void set_font_name(ItemEvent e) {
	if (e.getStateChange() != ItemEvent.SELECTED)
	    return;
	Object obj = e.getItem();
	if (obj != null && (obj instanceof String)) {
	    set_font_name((String)obj);
	}
    }

    /**
     * Sets the font size of the selected text to be the specified size.
     */
    public void set_font_size(int size) {
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.SIZE, size);
	modifySelectionTextStyle(modifier);
    }

    /**
     * Sets the font size of the selected text differed from the specified size.
     */
    public void set_font_size_diff(int size) {
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.SIZE_DIFF, size);
	modifySelectionTextStyle(modifier);
    }

    /**
     * Sets the font size of the selected text to be the selected size.
     * Selected item must be an <code>Integer</code> or a <code>String</code>
     * such as "2", "+2", and "-2" .
     */
    public void set_font_size(ItemEvent e) {
	if (e.getStateChange() != ItemEvent.SELECTED)
	    return;
	Object obj = e.getItem();
	if (obj == null)
	    return;
	if (obj instanceof Integer) {
	    set_font_size(((Integer)obj).intValue());
	}
	else if (obj instanceof String) {
	    Object signedInt[] = parseSignedInt((String)obj);
	    if (signedInt == null)
		return;
	    String sign = (String)signedInt[0];
	    int val = ((Integer)signedInt[1]).intValue();
	    if ("+".equals(sign))
		set_font_size_diff(val);
	    else if ("-".equals(sign))
		set_font_size_diff(-val);
	    else
		set_font_size(val);
	}
    }

    /** Makes the selected text large. */
    public abstract void make_font_large();

    /** Makes the selected text small. */
    public abstract void make_font_small();

    /**
     * Sets the font color of the selected text to be the specified color.
     */
    public void set_font_color(Color color) {
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	if (color != null)
	    modifier.put(BasicTSModifier.COLOR, color);
	else
	    modifier.put(BasicTSModifier.COLOR, BasicTSModifier.NULL);
	modifySelectionTextStyle(modifier);
    }

    /**
     * Sets the font color of the selected text to be the selected color.
     */
    public void set_font_color(ItemEvent e) {
	if (e.getStateChange() != ItemEvent.SELECTED)
	    return;
	Object obj = e.getItem();
	if (obj == null) {
	    set_font_color((Color)null);
	}
	else if (obj instanceof Color) {
	    set_font_color((Color)obj);
	}
    }

    /**
     * Makes or clears the selected text bold.
     */
    public void make_font_bold(boolean bold) {
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	if (bold)
	    modifier.put(BasicTSModifier.BOLD, true);
	else
	    modifier.put(BasicTSModifier.BOLD, BasicTSModifier.NULL);
	modifySelectionTextStyle(modifier);
    }

    /** Makes the selected text bold. */
    public void make_font_bold() {
	make_font_bold(true);
    }

    /** Clears the selected text bold. */
    public void clear_font_bold() {
	make_font_bold(false);
    }

    /**
     * Makes or clears the selected text italic.
     */
    public void make_font_italic(boolean italic) {
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	if (italic)
	    modifier.put(BasicTSModifier.ITALIC, true);
	else
	    modifier.put(BasicTSModifier.ITALIC, BasicTSModifier.NULL);
	modifySelectionTextStyle(modifier);
    }

    /** Makes the selected text italic. */
    public void make_font_italic() {
	make_font_italic(true);
    }

    /** Clears the selected text italic. */
    public void clear_font_italic() {
	make_font_italic(false);
    }

    /**
     * Makes or clears the selected text underlined.
     */
    public void make_font_underlined(boolean underline) {
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	if (underline)
	    modifier.put(BasicTSModifier.UNDERLINE, true);
	else
	    modifier.put(BasicTSModifier.UNDERLINE, BasicTSModifier.NULL);
	modifySelectionTextStyle(modifier);
    }

    /** Makes or clears the selected text underlined. */
    public void make_font_underlined() {
	make_font_underlined(true);
    }

    /** Clears the selected text underlined. */
    public void clear_font_underlined() {
	make_font_underlined(false);
    }

    /**
     * Clears the all styles of the selected text.
     */
    public void clear_font_styles() {
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.BOLD,      BasicTSModifier.NULL);
	modifier.put(BasicTSModifier.ITALIC,    BasicTSModifier.NULL);
	modifier.put(BasicTSModifier.UNDERLINE, BasicTSModifier.NULL);
	modifier.put(BasicTSModifier.COLOR,     BasicTSModifier.NULL);
	modifier.put(BasicTSModifier.CLICKABLE, BasicTSModifier.NULL);
	modifySelectionTextStyle(modifier);
    }

    /**
     * Sets the alignment of the selected paragraph to be the specified style.
     * @see jp.kyasu.graphics.ParagraphStyle#LEFT
     * @see jp.kyasu.graphics.ParagraphStyle#CENTER
     * @see jp.kyasu.graphics.ParagraphStyle#RIGHT
     */
    public void set_alignment(int align) {
	switch (align) {
	case ParagraphStyle.LEFT:
	case ParagraphStyle.CENTER:
	case ParagraphStyle.RIGHT:
	    break;
	default:
	    throw new IllegalArgumentException("improper alignment: " + align);
	}
	BasicPSModifier modifier = new BasicPSModifier();
	modifier.put(BasicPSModifier.ALIGNMENT, align);
	modifySelectionParagraphStyle(modifier);
    }

    /** Sets the alignment of the selected paragraph to be left. */
    public void align_to_left() {
	set_alignment(ParagraphStyle.LEFT);
    }

    /** Sets the alignment of the selected paragraph to be centered. */
    public void align_to_center() {
	set_alignment(ParagraphStyle.CENTER);
    }

    /** Sets the alignment of the selected paragraph to be right. */
    public void align_to_right() {
	set_alignment(ParagraphStyle.RIGHT);
    }

    /**
     * Inserts a horizontal line.
     */
    public void insert_hr() {
	TextAttachment ta = new TextAttachment(new VHRBorder());
	ta.setRatioToWidth(1.0f);
	insertTextAttachmentAsLine(ta);
    }

    /**
     * Inserts an image.
     */
    public void insert_image() {
	File file = getFileFromLoadDialog(
			getToolTip(A_IMAGE),
			(writeTarget != null ? writeTarget.getParent() : null),
			null);
	if (file == null)
	    return;
	VImage image = new VImage(file.getPath());
	if (image.getImage() == null) {
	    Dialog.warn(getFrame(),
			file.getPath() + getToolLabel("fileNotImage"));
	    return;
	}
	insertVisualizable(image);
    }

    /**
     * Invoked when an action occurs.
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand();
	if (command.equals(A_BOLD)) {
	    make_font_bold();
	}
	else if (command.equals(A_ITALIC)) {
	    make_font_italic();
	}
	else if (command.equals(A_UNDERLINE)) {
	    make_font_underlined();
	}
	else if (command.equals(A_LARGE)) {
	    make_font_large();
	}
	else if (command.equals(A_SMALL)) {
	    make_font_small();
	}
	else if (command.equals(A_CLEAR_STYLE)) {
	    clear_font_styles();
	}
	else if (command.equals(A_LEFT)) {
	    align_to_left();
	}
	else if (command.equals(A_CENTER)) {
	    align_to_center();
	}
	else if (command.equals(A_RIGHT)) {
	    align_to_right();
	}
	else if (command.equals(A_IMAGE)) {
	    insert_image();
	}
	else if (command.equals(A_HR)) {
	    insert_hr();
	}
	else {
	    super.actionPerformed(e);
	}
    }

    /**
     * Invoked when an item's state has been changed.
     * @see java.awt.event.ItemListener
     */
    public void itemStateChanged(ItemEvent e) {
	Object obj = e.getItem();
	if (obj == null || (obj instanceof Color)) {
	    set_font_color((Color)obj);
	    return;
	}
	else if (!(obj instanceof String)) {
	    super.itemStateChanged(e);
	    return;
	}

	String command = (String)checkboxMenuMap.get(obj);
	if (command == null) command = (String)obj;
	boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
	if (command.equals(I_BOLD)) {
	    make_font_bold(selected);
	}
	else if (command.equals(I_ITALIC)) {
	    make_font_italic(selected);
	}
	else if (command.equals(I_UNDERLINE)) {
	    make_font_underlined(selected);
	}
	else {
	    super.itemStateChanged(e);
	}
    }


    protected Component[] createFontComponents1() {
	boldButton = createIconToggleButton(I_BOLD);
	boldButton.addItemListener(this);
	italicButton = createIconToggleButton(I_ITALIC);
	italicButton.addItemListener(this);
	underlineButton = createIconToggleButton(I_UNDERLINE);
	underlineButton.addItemListener(this);

	boldButton.setEnabled(false);
	italicButton.setEnabled(false);
	underlineButton.setEnabled(false);
	addCaretDisableComp(boldButton);
	addCaretDisableComp(italicButton);
	addCaretDisableComp(underlineButton);

	return new Component[]{ boldButton, italicButton, underlineButton };
    }

    protected Component[] createFontComponents2() {
	ColorButton color = new ColorButton();
	color.addItemListener(this);
	color.setToolTipText(getToolTip("fontColor"));
	Button large = createIconButton(A_LARGE);
	large.addActionListener(this);
	Button small = createIconButton(A_SMALL);
	small.addActionListener(this);

	color.setEnabled(false);
	large.setEnabled(false);
	small.setEnabled(false);
	addCaretDisableComp(color);
	addCaretDisableComp(large);
	addCaretDisableComp(small);

	return new Component[]{ color, large, small };
    }

    protected Component[] createAlignmentComponents() {
	Button left = createIconButton(A_LEFT);
	left.addActionListener(this);
	Button center = createIconButton(A_CENTER);
	center.addActionListener(this);
	Button right = createIconButton(A_RIGHT);
	right.addActionListener(this);
	return new Component[]{ left, center, right };
    }

    protected void insertVisualizable(Visualizable v) {
	replaceSelection(new Text(new TextAttachment(v), getInsertionStyle()));
    }

    protected void insertTextAttachmentAsLine(TextAttachment ta) {
	TextBuffer buffer = new TextBuffer();
	buffer.setTextStyle(getInsertionStyle());
	buffer.append(Text.LINE_SEPARATOR_CHAR);
	buffer.append(ta);
	buffer.append(Text.LINE_SEPARATOR_CHAR);
	replaceSelection(buffer.toText());
    }

    protected TextStyle getInsertionStyle() {
	//return getCurrentTextStyle();
	return getRichText().getRichTextStyle().getTextStyle();
    }

    protected Integer parseInt(String s) {
	if (s == null)
	    return null;
	try {
	    return new Integer(Integer.parseInt(s));
	}
	catch (NumberFormatException e) {
	    return null;
	}
    }

    protected Object[] parseSignedInt(String s) {
	if (s == null || s.length() == 0)
	    return null;
	char c = s.charAt(0);
	String sign = null;
	if (c == '+' || c == '-') {
	    s = s.substring(1, s.length());
	    sign = new String(new char[]{ c });
	}
	Integer val = parseInt(s);
	if (val == null)
	    return null;
	return new Object[]{ sign, val };
    }
}
