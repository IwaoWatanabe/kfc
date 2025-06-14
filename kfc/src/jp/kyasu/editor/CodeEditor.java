/*
 * CodeEditor.java
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

import jp.kyasu.awt.TextEditModel;
import jp.kyasu.awt.ToggleButton;
import jp.kyasu.awt.ToolBar;
import jp.kyasu.awt.text.TextCaret;
import jp.kyasu.awt.util.JavaSyntaxColoringModel;
import jp.kyasu.graphics.RichTextStyle;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A <code>CodeEditor</code> object is a multi-line area that displays text.
 * It is suited for coding and it supports the syntax coloring of
 * <code>C</code>, <code>C++</code>, and <code>Java</code>.
 * It is created with tool bar.
 *
 * @see 	jp.kyasu.awt.util.JavaSyntaxColoringModel;
 *
 * @version 	20 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class CodeEditor extends TextEditor {
    protected ToggleButton syntaxColorButton;


    static public final String I_SYNTAX_COLOR = "scolor";
    static public final String L_LANG_MODE    = "langMode";

    static public final String P_LANG_MODE    = L_LANG_MODE;
    static public final String P_SYNTAX_COLOR = I_SYNTAX_COLOR;

    static public final Vector JavaSuffixes = new Vector();
    static public final Vector CSuffixes    = new Vector();
    static public final Vector CPPSuffixes  = new Vector();

    static {
	String suffixes = EditorResources.getResourceString("javaSuffixes");
	StringTokenizer st = new StringTokenizer(suffixes);
	while (st.hasMoreTokens()) JavaSuffixes.addElement(st.nextToken());

	suffixes = EditorResources.getResourceString("cSuffixes");
	st = new StringTokenizer(suffixes);
	while (st.hasMoreTokens()) CSuffixes.addElement(st.nextToken());

	suffixes = EditorResources.getResourceString("cppSuffixes");
	st = new StringTokenizer(suffixes);
	while (st.hasMoreTokens()) CPPSuffixes.addElement(st.nextToken());
    }


    /**
     * Constructs a new code text area with tool bar.
     * This code text area is created with vertical scroll bar.
     */
    public CodeEditor() {
	this(true);
    }

    /**
     * Constructs a new code text area with tool bar. If
     * <code>showToolBar</code> is true, then shows the tool bar initially;
     * otherwise hides.
     * This code text area is created with vertical scroll bar.
     * @param showToolBar if true, then shows the tool bar initially;
     *                    otherwise hides.
     */
    public CodeEditor(boolean showToolBar) {
	this(20, 80, showToolBar);
    }

    /**
     * Constructs a new code text area with tool bar, with the specified
     * number of rows and columns.
     * This code text area is created with vertical scroll bar.
     * @param rows    the number of rows
     * @param columns the number of columns.
     */
    public CodeEditor(int rows, int columns) {
	this(rows, columns, true);
    }

    /**
     * Constructs a new code text area with tool bar, with the specified
     * number of rows and columns. If <code>showToolBar</code> is true,
     * then shows the tool bar initially; otherwise hidden.
     * This code text area is created with vertical scroll bar.
     * @param rows        the number of rows
     * @param columns     the number of columns.
     * @param showToolBar if true, then shows the tool bar initially;
     *                    otherwise hides.
     */
    public CodeEditor(int rows, int columns, boolean showToolBar) {
	this(rows, columns, showToolBar, null, null, null);
    }

    /**
     * Constructs a new code text area with tool bar, with the specified
     * number of rows and columns. If <code>showToolBar</code> is true,
     * then shows the tool bar initially; otherwise hidden.
     * This code text area is created with vertical scroll bar.
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
    public CodeEditor(int rows, int columns, boolean showToolBar,
		      ActionListener openActionListener,
		      ActionListener saveActionListener,
		      ActionListener printActionListener)
    {
	super(rows, columns, showToolBar, openActionListener,
					  saveActionListener,
					  printActionListener);
    }


    protected TextEditModel createDefaultTextEditModel() {
	return new JavaSyntaxColoringModel();
    }

    protected Component createTextComponent(TextEditModel model,
					    int rows, int columns)
    {
	Component textComp = super.createTextComponent(model, rows, columns);

	super.setFont(getCodeModel().getBaseFont());
	setForeground(Color.black);
	setBackground(Color.white);
	setSelectionForeground(Color.black);
	setSelectionBackground(Color.lightGray);

	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	//setTextCaret(new TextCaret(TextCaret.HAT_CARET, true));
	//setCaretColor(Color.black);

	//setWordWrap(false);
	setSoftTab(true);
	setAutoIndentEnabled(true);
	setShowMatchEnabled(true);

	return textComp;
    }

    protected JavaSyntaxColoringModel getCodeModel() {
	return (JavaSyntaxColoringModel)editModel;
    }


    /**
     * Sets the font of this component.
     */
    public void setFont(Font font) {
	getCodeModel().setBaseFont(font);
	super.setFont(font);
    }

    /**
     * Returns the language mode that is one of "Java", "C", and "C++".
     * @see #isJavaMode()
     * @see #isCMode()
     * @see #isCPPMode()
     */
    public String getLangMode() {
	if (isCMode()) {
	    return "C";
	}
	else if (isCPPMode()) {
	    return "C++";
	}
	else { // isJavaMode()
	    return "Java";
	}
    }

    /**
     * Checks if this component colors the syntax of the Java language.
     * @see #setJavaMode()
     */
    public boolean isJavaMode() {
	return getCodeModel().isJavaMode();
    }

    /**
     * Makes this component colors the syntax of the Java language.
     * @see #isJavaMode()
     */
    public void setJavaMode() {
	if (isJavaMode())
	    return;
	String oldValue = getLangMode();
	getCodeModel().setJavaMode();
	setRichText(getRichText());
	if (change != null) {
	    change.firePropertyChange(P_LANG_MODE, oldValue, getLangMode());
	}
    }

    /**
     * Checks if this component colors the syntax of the C language.
     * @see #setCMode()
     */
    public boolean isCMode() {
	return getCodeModel().isCMode();
    }

    /**
     * Makes this component colors the syntax of the C language.
     * @see #isCMode()
     */
    public void setCMode() {
	if (isCMode())
	    return;
	String oldValue = getLangMode();
	getCodeModel().setCMode();
	setRichText(getRichText());
	if (change != null) {
	    change.firePropertyChange(P_LANG_MODE, oldValue, getLangMode());
	}
    }

    /**
     * Checks if this component colors the syntax of the C++ language.
     * @see #setCPPMode()
     */
    public boolean isCPPMode() {
	return getCodeModel().isCPPMode();
    }

    /**
     * Makes this component colors the syntax of the C++ language.
     * @see #isCPPMode()
     */
    public void setCPPMode() {
	if (isCPPMode())
	    return;
	String oldValue = getLangMode();
	getCodeModel().setCPPMode();
	setRichText(getRichText());
	if (change != null) {
	    change.firePropertyChange(P_LANG_MODE, oldValue, getLangMode());
	}
    }

    /**
     * Tests if the syntax coloring is enabled.
     */
    public boolean isSyntaxColoringEnabled() {
	return getCodeModel().isSyntaxColoringEnabled();
    }

    /**
     * Enables or disables the syntax coloring.
     */
    public void setSyntaxColoringEnabled(boolean b) {
	boolean oldValue = isSyntaxColoringEnabled();
	if (oldValue == b)
	    return;
	getCodeModel().setSyntaxColoringEnabled(b);
	setRichText(getRichText());
	if (syntaxColorButton.getState() != b) {
	    syntaxColorButton.setState(b);
	}
	if (change != null) {
	    change.firePropertyChange(P_SYNTAX_COLOR,
				      new Boolean(oldValue),
				      new Boolean(b));
	}
    }

    /**
     * Returns the font color for the normal tokens.
     */
    public Color getNormalColor() {
	return getCodeModel().getNormalColor();
    }

    /**
     * Sets the font color for the normal tokens.
     */
    public void setNormalColor(Color color) {
	Color c = getNormalColor();
	if ((c == null ? color == null : c.equals(color)))
	    return;
	getCodeModel().setNormalColor(color);
	setRichText(getRichText());
    }

    /**
     * Returns the font color for the keyword tokens.
     */
    public Color getKeywordColor() {
	return getCodeModel().getKeywordColor();
    }

    /**
     * Sets the font color for the keyword tokens.
     */
    public void setKeywordColor(Color color) {
	Color c = getKeywordColor();
	if ((c == null ? color == null : c.equals(color)))
	    return;
	getCodeModel().setKeywordColor(color);
	setRichText(getRichText());
    }

    /**
     * Returns the font color for the constant tokens.
     */
    public Color getConstantColor() {
	return getCodeModel().getConstantColor();
    }

    /**
     * Sets the font color for the constant tokens.
     */
    public void setConstantColor(Color color) {
	Color c = getConstantColor();
	if ((c == null ? color == null : c.equals(color)))
	    return;
	getCodeModel().setConstantColor(color);
	setRichText(getRichText());
    }

    /**
     * Returns the font color for the comment tokens.
     */
    public Color getCommentColor() {
	return getCodeModel().getCommentColor();
    }

    /**
     * Sets the font color for the comment tokens.
     */
    public void setCommentColor(Color color) {
	Color c = getCommentColor();
	if ((c == null ? color == null : c.equals(color)))
	    return;
	getCodeModel().setCommentColor(color);
	setRichText(getRichText());
    }

    /**
     * Invoked when an item's state has been changed.
     * @see java.awt.event.ItemListener
     */
    public void itemStateChanged(ItemEvent e) {
	Object obj = e.getItem();
	if (obj == null || !(obj instanceof String)) {
	    super.itemStateChanged(e);
	    return;
	}

	String command = (String)checkboxMenuMap.get(obj);
	if (command == null) command = (String)obj;
	if (command.equals(I_SYNTAX_COLOR)) {
	    setSyntaxColoringEnabled(e.getStateChange() == ItemEvent.SELECTED);
	}
	else {
	    super.itemStateChanged(e);
	}
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
	Component[] file  = createFileComponents(openActionListener,
						 saveActionListener);
	Component[] print = createPrintComponents(printActionListener);
	Component[] find  = createFindComponents(true);
	Component[] edit  = createEditComponents();
	Component[] color = createSyntaxColorComponents();

	Component[][] bar = new Component[][]{ file, print, find, edit, color };
	return new ToolBar(new Component[][][]{ bar }, showToolBar);
    }

    protected Component[] createSyntaxColorComponents() {
	syntaxColorButton = createIconToggleButton(I_SYNTAX_COLOR);
	syntaxColorButton.addItemListener(this);
	syntaxColorButton.setState(isSyntaxColoringEnabled());
	return new Component[]{ syntaxColorButton };
    }

    protected Menu createViewMenu() {
	Menu menu = super.createViewMenu();
	menu.addSeparator();
	menu.add(createLangModeMenu());
	return menu;
    }

    class LangModeSelection implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    String command = e.getActionCommand();
	    if (command.equals("Java")) {
		setJavaMode();
	    }
	    else if (command.equals("C")) {
		setCMode();
	    }
	    else if (command.equals("C++")) {
		setCPPMode();
	    }
	}
    }

    protected Menu createLangModeMenu() {
	SelectionMenu menu = new SelectionMenu(getToolLabel(L_LANG_MODE));
	menu.addActionListener(new LangModeSelection());
	menu.add("Java", "Java", isJavaMode());
	menu.add("C",    "C",    isCMode());
	menu.add("C++",  "C++",  isCPPMode());
	return menu;
    }

    protected void setWriteTarget(File file) {
	if (file != null) {
	    String name = file.getName();
	    for (Enumeration e = JavaSuffixes.elements(); e.hasMoreElements(); )
	    {
		if (name.endsWith((String)e.nextElement())) {
		    super.setWriteTarget(file);
		    setJavaMode();
		    return;
		}
	    }
	    for (Enumeration e = CSuffixes.elements(); e.hasMoreElements(); ) {
		if (name.endsWith((String)e.nextElement())) {
		    super.setWriteTarget(file);
		    setCMode();
		    return;
		}
	    }
	    for (Enumeration e = CPPSuffixes.elements(); e.hasMoreElements(); )
	    {
		if (name.endsWith((String)e.nextElement())) {
		    super.setWriteTarget(file);
		    setCPPMode();
		    return;
		}
	    }
	}
	super.setWriteTarget(file);
    }


    /** Executes the examples. */
    public static void main(String args[]) {
	CodeEditor editor = new CodeEditor();
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame("CodeEditor");
	java.awt.MenuBar bar = new java.awt.MenuBar();
	bar.add(editor.getEditMenu());
	bar.add(editor.getViewMenu());
	f.setMenuBar(bar);
	jp.kyasu.awt.NativePanel p = new jp.kyasu.awt.NativePanel();
	p.add(editor, java.awt.BorderLayout.CENTER);
	f.add(p, java.awt.BorderLayout.CENTER);
	f.pack();
	f.setVisible(true);
    }
}
