/*
 * DocumentEditor.java
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
import jp.kyasu.awt.Choice;
import jp.kyasu.awt.Dialog;
import jp.kyasu.awt.TextEditModel;
import jp.kyasu.awt.ToolBar;
import jp.kyasu.awt.event.TextPositionEvent;
import jp.kyasu.awt.text.Keymap;
import jp.kyasu.awt.text.TextCaret;
import jp.kyasu.graphics.BasicPSModifier;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.ParagraphStyleModifier;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.VColoredWrapper;
import jp.kyasu.graphics.VRectangle;
import jp.kyasu.graphics.Visualizable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Enumeration;

/**
 * A <code>DocumentEditor</code> object is a multi-line area that displays
 * text. It is suited for editing styled documents.
 * It is created with tool bar.
 *
 * @version 	19 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class DocumentEditor extends RichTextEditor {
    protected boolean writeTargetIsObject = true;

    static public final String A_SAVE_AS_TEXT   = "saveAsText";
    static public final String A_SAVE_AS_OBJECT = "saveAsObject";

    static public final String A_LIST           = "list";
    static public final String A_UNLIST         = "unlist";


    /** The indentation of the list. */
    static protected final int LIST_INDENT         = 50;

    /** The heading space for the list. */
    static protected final int LIST_HEADING_SPACE  = 8;

    /** The heading visual object for the list. */
    static protected final Visualizable LIST_HEADING =
		new VColoredWrapper(
			new VRectangle(4, 4, VRectangle.PLAIN), Color.black);


    /**
     * Constructs a new rich text area with tool bar.
     * This rich text area is created with vertical scroll bar.
     */
    public DocumentEditor() {
	this(true);
    }

    /**
     * Constructs a new rich text area with tool bar. If
     * <code>showToolBar</code> is true, then shows the tool bar initially;
     * otherwise hides.
     * This rich text area is created with vertical scroll bar.
     * @param showToolBar if true, then shows the tool bar initially;
     *                    otherwise hides.
     */
    public DocumentEditor(boolean showToolBar) {
	this(20, 65, showToolBar);
    }

    /**
     * Constructs a new rich text area with tool bar, with the specified
     * number of rows and columns.
     * This rich text area is created with vertical scroll bar.
     * @param rows    the number of rows
     * @param columns the number of columns.
     */
    public DocumentEditor(int rows, int columns) {
	this(rows, columns, true);
    }

    /**
     * Constructs a new rich text area with tool bar, with the specified
     * number of rows and columns. If <code>showToolBar</code> is true,
     * then shows the tool bar initially; otherwise hidden.
     * This rich text area is created with vertical scroll bar.
     * @param rows        the number of rows
     * @param columns     the number of columns.
     * @param showToolBar if true, then shows the tool bar initially;
     *                    otherwise hides.
     */
    public DocumentEditor(int rows, int columns, boolean showToolBar) {
	this(rows, columns, showToolBar, null, null, null);
    }

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
    public DocumentEditor(int rows, int columns, boolean showToolBar,
			  ActionListener openActionListener,
			  ActionListener saveActionListener,
			  ActionListener printActionListener)
    {
	super(rows, columns, showToolBar, openActionListener,
					  saveActionListener,
					  printActionListener);
    }


    protected Component createTextComponent(TextEditModel model,
					    int rows, int columns)
    {
	Component textComp = super.createTextComponent(model, rows, columns);

	super.setFont(
	  editModel.getRichText().getRichTextStyle().getTextStyle().getFont());
	setForeground(Color.black);
	setBackground(Color.white);
	//setSelectionForeground(Color.black);
	//setSelectionBackground(Color.lightGray);

	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	//setTextCaret(new TextCaret(TextCaret.HAT_CARET, true));
	//setCaretColor(Color.black);

	//setWordWrap(true);
	Keymap keymap = getKeymap();
	keymap.setKeyCodeMap(KeyEvent.VK_ENTER, Event.SHIFT_MASK, "newbreak");
	setKeymap(keymap);

	return textComp;
    }


    /**
     * Makes the selected text large.
     */
    public void make_font_large() {
	set_font_size_diff(2);
    }

    /**
     * Makes the selected text small.
     */
    public void make_font_small() {
	set_font_size_diff(-2);
    }

    class ListPSModifier
	implements ParagraphStyleModifier, java.io.Serializable
    {
	Visualizable heading;
	int headingSpace;

	ListPSModifier(Visualizable heading, int headingSpace) {
	    this.heading      = heading;
	    this.headingSpace = headingSpace;
	}

	public ParagraphStyle modify(ParagraphStyle pStyle) {
	    if (pStyle.getHeadingSpace() == headingSpace &&
		pStyle.getHeading()      == heading)
	    {
		return pStyle;
	    }
	    int listLevel = getListLevel(pStyle);
	    if (listLevel == 0) listLevel = 1;
	    return new ParagraphStyle(pStyle.getStyleName(),
				      pStyle.getAlignment(),
				      getListIndent(listLevel),
				      pStyle.getRightIndent(),
				      pStyle.getLineSpace(),
				      pStyle.getParagraphSpace(),
				      pStyle.getTabWidth(),
				      heading,
				      headingSpace,
				      pStyle.getBaseStyle());
	}
    }

    /**
     * Makes the selected paragraph as a list.
     */
    public void make_list() {
	modifySelectionParagraphStyle(
	    new ListPSModifier(LIST_HEADING, LIST_HEADING_SPACE));
    }

    /**
     * Clears the selected paragraph as a list.
     */
    public void clear_list() {
	BasicPSModifier modifier = new BasicPSModifier();
	modifier.put(BasicPSModifier.HEADING, BasicPSModifier.NULL);
	modifier.put(BasicPSModifier.HEADING_SPACE, 0);
	modifySelectionParagraphStyle(modifier);
    }

    class LeftIndentPSModifier
	implements ParagraphStyleModifier, java.io.Serializable
    {
	boolean increase;

	LeftIndentPSModifier(boolean increase) {
	    this.increase = increase;
	}

	public ParagraphStyle modify(ParagraphStyle pStyle) {
	    int listLevel = getListLevel(pStyle);
	    if (increase) {
		++listLevel;
	    }
	    else { // decrease
		if (listLevel > 0)
		    --listLevel;
	    }
	    Visualizable heading = (listLevel==0 ? null : pStyle.getHeading());
	    int headingSpace = (listLevel == 0 ? 0 : pStyle.getHeadingSpace());

	    return new ParagraphStyle(pStyle.getStyleName(),
				      pStyle.getAlignment(),
				      getListIndent(listLevel),
				      pStyle.getRightIndent(),
				      pStyle.getLineSpace(),
				      pStyle.getParagraphSpace(),
				      pStyle.getTabWidth(),
				      heading,
				      headingSpace,
				      pStyle.getBaseStyle());
	}
    }

    /**
     * Increases the selected paragraph indentation.
     */
    public void increase_indent() {
	modifySelectionParagraphStyle(new LeftIndentPSModifier(true));
    }

    /**
     * Decreases the selected paragraph indentation.
     */
    public void decrease_indent() {
	modifySelectionParagraphStyle(new LeftIndentPSModifier(false));
    }

    /**
     * Invoked when an action occurs.
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand();
	if (command.equals(A_INC_INDENT)) {
	    increase_indent();
	}
	else if (command.equals(A_DEC_INDENT)) {
	    decrease_indent();
	}
	else if (command.equals(A_SAVE_AS_TEXT)) {
	    save_file_as(false);
	}
	else if (command.equals(A_SAVE_AS_OBJECT)) {
	    save_file_as(true);
	}
	else if (command.equals(A_LIST)) {
	    make_list();
	}
	else if (command.equals(A_UNLIST)) {
	    clear_list();
	}
	else {
	    super.actionPerformed(e);
	}
    }

    /**
     * Invoked when the position of the text has changed.
     * @see java.awt.event.TextPositionListener
     */
    public void textPositionChanged(TextPositionEvent te) {
	super.textPositionChanged(te); // must be first

	if (boldButton != null || italicButton != null ||
	    underlineButton != null)
	{
	    int begin = te.getSelectionBeginIndex();
	    int end = te.getSelectionEndIndex();
	    boolean bold      = false;
	    boolean italic    = false;
	    boolean underline = false;
	    for (Enumeration e = textStyles(begin, end); e.hasMoreElements(); )
	    {
		TextStyle tStyle = (TextStyle)e.nextElement();
		if (tStyle.getFont().isBold())
		    bold = true;
		if (tStyle.getFont().isItalic())
		    italic = true;
		if (tStyle.getExtendedFont().isUnderline())
		    underline = true;
	    }
	    if (boldButton != null) boldButton.setState(bold);
	    if (italicButton != null) italicButton.setState(italic);
	    if (underlineButton != null) underlineButton.setState(underline);
	}
    }

    /**
     * Opens the specified file.
     */
    public void open_file(File file) {
	boolean ok = false;
	try {
	    InputStream input = new FileInputStream(file);
	    disableSubComps();
	    ok = loadAsObject(input);
	    input.close();

	    if (!ok) {
		super.open_file(file);
		return;
	    }
	}
	catch (IOException e) {
	    warn(e);
	}
	finally {
	    enableSubComps();
	}

	if (ok) {
	    setWriteTarget(file, true);
	}
    }

    /**
     * Saves the text into the specified file.
     */
    public void save_file_as(File file) {
	save_file_as(file, writeTargetIsObject);
    }

    /**
     * Saves the text into the selected file.
     * @param serialize if true, saves the text as an object.
     */
    public void save_file_as(boolean serialize) {
	File file = getFileFromSaveDialog(
			getToolTip(A_SAVE),
			(writeTarget != null ? writeTarget.getParent() : null),
			(writeTarget != null ? writeTarget.getName() : null));
	if (file == null)
	    return;
	save_file_as(file, serialize);
    }

    /**
     * Saves the text into the specified file.
     * @param file      the file to store into.
     * @param serialize if true, saves the text as an object.
     */
    public void save_file_as(File file, boolean serialize) {
	if (!serialize) {
	    super.save_file_as(file);
	    return;
	}

	boolean ok = false;
	try {
	    OutputStream output = new FileOutputStream(file);
	    disableSubComps();
	    ok = saveAsObject(output);
	    output.close();
	}
	catch (IOException e) {
	    warn(e);
	}
	finally {
	    enableSubComps();
	}

	if (ok) {
	    setWriteTarget(file, true);
	}
    }

    /**
     * Loads the contents of the specified stream as object into
     * this component.
     * @param stream the stream to be loaded.
     * @return true if the loading was succeeded.
     */
    public boolean loadAsObject(java.io.InputStream stream) {
	boolean loadOk = false;
	try {
	    java.io.ObjectInput in = new java.io.ObjectInputStream(stream);
	    RichText richText = (RichText)in.readObject();
	    setRichText(richText);
	    clearUndo();
	    textChanged = false;
	    loadOk = true;
	}
	catch (Exception e) {
	    //warn(e);
	}
	return loadOk;
    }

    /**
     * Saves the contents of this component as object into the
     * specified stream.
     * @param stream the stream to save into.
     * @return true if the saving was succeeded.
     */
    public boolean saveAsObject(java.io.OutputStream stream) {
	boolean saveOk = false;
	try {
	    java.io.ObjectOutput out = new java.io.ObjectOutputStream(stream);
	    out.writeObject(getRichText());
	    out.flush();
	    textChanged = false;
	    saveOk = true;
	}
	catch (Exception e) {
	    warn(e);
	}
	return saveOk;
    }


    /**
     * Creates a tool bar.
     * @param showToolBar         if true, then shows the tool bar initially;
     *                            otherwise hides.
     * @param openActionListener  the action listener that receives action
     *                            events from the open button in tool bar.
     * @param saveActionListener  the action listener that receives action
     *                            events from the save button in tool bar.
     * @param printActionListener the action listener that receives action
     *                            events from the print button in tool bar.
     */
    protected ToolBar createToolBar(boolean showToolBar,
				    ActionListener openActionListener,
				    ActionListener saveActionListener,
				    ActionListener printActionListener)
    {
	Component name = createFontNameComponent();
	Component[] font = createFontComponents1();
	Component[] font1 = new Component[font.length + 1];
	font1[0] = name;
	System.arraycopy(font, 0, font1, 1, font.length);

	Component[] font2 = createFontComponents2();
	Component[] align = createAlignmentComponents();
	Component[] list  = createListComponents();
	Component[][] bar1 = new Component[][]{ font1, font2, align, list };

	Component[] file   = createFileComponents(openActionListener,
						  saveActionListener);
	Component[] print  = createPrintComponents(printActionListener);
	Component[] find   = createFindComponents(false);
	Component[] edit   = createEditComponents();
	Component[] insert = createInsertComponents();

	Component[][] bar2 =
			new Component[][]{ file, print, find, edit, insert };
	return new ToolBar(new Component[][][]{ bar1, bar2 }, showToolBar);
    }

    class FontNameItemListener implements ItemListener, java.io.Serializable {
	public void itemStateChanged(ItemEvent e) {
	    set_font_name(e);
	}
    }

    protected Component createFontNameComponent() {
	Choice name = new Choice();
	name.addItem("SansSerif");
	name.addItem("Serif");
	name.addItem("Monospaced");
	name.addItem("Dialog");
	name.setToolTipText(getToolTip("fontName"));
	name.addItemListener(new FontNameItemListener());

	name.setEnabled(false);
	addSubComp(name);
	addCaretDisableComp(name);

	return name;
    }

    protected Component[] createListComponents() {
	Button list = createIconButton(A_LIST);
	list.addActionListener(this);
	Button unlist = createIconButton(A_UNLIST);
	unlist.addActionListener(this);
	Button decindent = createIconButton(A_DEC_INDENT);
	decindent.addActionListener(this);
	Button incindent = createIconButton(A_INC_INDENT);
	incindent.addActionListener(this);

	return new Component[]{ list, unlist, decindent, incindent };
    }

    protected Component[] createInsertComponents() {
	Button hr = createIconButton(A_HR);
	hr.addActionListener(this);
	Button image = createIconButton(A_IMAGE);
	image.addActionListener(this);

	return new Component[]{ hr, image };
    }

    protected Menu createViewMenu() {
	Menu menu = new Menu(getToolLabel(L_VIEW));
	menu.add(createCheckboxMenuItem(I_INCREMENTAL_LOAD, isIncrementalLoad(),
					this));
	menu.addSeparator();
	menu.add(createReadCharSetMenu());
	menu.add(createWriteCharSetMenu());
	return menu;
    }

    protected Menu createInsertMenu() {
	Menu menu = new Menu(getToolLabel(L_INSERT));
	menu.add(createMenuItem(A_HR,    this));
	menu.add(createMenuItem(A_IMAGE, this));
	return menu;
    }

    protected Menu createFormatMenu() {
	Menu menu = new Menu(getToolLabel(L_FORMAT));

	Menu font = new Menu(getToolLabel(L_FONT_STYLE));
	font.add(createMenuItem(A_BOLD,       this));
	font.add(createMenuItem(A_ITALIC,     this));
	font.add(createMenuItem(A_UNDERLINE,  this));
	font.setEnabled(false);
	addCaretDisableItem(font);
	menu.add(font);

	MenuItem clear = createMenuItem(A_CLEAR_STYLE, this);
	clear.setEnabled(false);
	addCaretDisableItem(clear);
	menu.add(clear);

	menu.addSeparator();

	MenuItem large = createMenuItem(A_LARGE, this);
	MenuItem small = createMenuItem(A_SMALL, this);
	large.setEnabled(false);
	small.setEnabled(false);
	addCaretDisableItem(large);
	addCaretDisableItem(small);
	menu.add(large);
	menu.add(small);

	menu.addSeparator();

	Menu align = new Menu(getToolLabel(L_ALIGN));
	align.add(createMenuItem(A_LEFT,   this));
	align.add(createMenuItem(A_CENTER, this));
	align.add(createMenuItem(A_RIGHT,  this));
	menu.add(align);

	Menu list = new Menu(getToolLabel(L_LIST));
	list.add(createMenuItem(A_LIST,   this));
	list.add(createMenuItem(A_UNLIST, this));
	menu.add(list);

	menu.addSeparator();

	menu.add(createMenuItem(A_INC_INDENT, this));
	menu.add(createMenuItem(A_DEC_INDENT, this));

	return menu;
    }

    protected int getListLevel(ParagraphStyle pStyle) {
	ParagraphStyle baseStyle =
			getRichText().getRichTextStyle().getParagraphStyle();
	return (pStyle.getLeftIndent() - baseStyle.getLeftIndent())
								/ LIST_INDENT;
    }

    protected int getListIndent(int listLevel) {
	ParagraphStyle baseStyle =
			getRichText().getRichTextStyle().getParagraphStyle();
	return baseStyle.getLeftIndent() + (LIST_INDENT * listLevel);
    }

    protected void setWriteTarget(File file) {
	setWriteTarget(file, false);
    }

    protected void setWriteTarget(File file, boolean isObject) {
	super.setWriteTarget(file);
	writeTargetIsObject = isObject;
    }


    /** Executes the examples. */
    public static void main(String args[]) {
	DocumentEditor editor = new DocumentEditor();
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame("DocumentEditor");
	java.awt.MenuBar bar = new java.awt.MenuBar();
	bar.add(editor.getEditMenu());
	bar.add(editor.getViewMenu());
	bar.add(editor.getInsertMenu());
	bar.add(editor.getFormatMenu());
	f.setMenuBar(bar);
	jp.kyasu.awt.NativePanel p = new jp.kyasu.awt.NativePanel();
	p.add(editor, java.awt.BorderLayout.CENTER);
	f.add(p, java.awt.BorderLayout.CENTER);
	f.pack();
	f.setVisible(true);
    }
}
