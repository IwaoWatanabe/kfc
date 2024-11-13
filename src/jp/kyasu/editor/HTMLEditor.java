/*
 * HTMLEditor.java
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
import jp.kyasu.awt.Label;
import jp.kyasu.awt.MultipleUndo;
import jp.kyasu.awt.TextEditModel;
import jp.kyasu.awt.TextField;
import jp.kyasu.awt.ToggleButton;
import jp.kyasu.awt.ToolBar;
import jp.kyasu.awt.Undo;
import jp.kyasu.awt.text.Keymap;
import jp.kyasu.awt.text.TextCaret;
import jp.kyasu.awt.event.TextModelEvent;
import jp.kyasu.awt.event.TextPositionEvent;
import jp.kyasu.awt.util.HTMLTextEditModel;
import jp.kyasu.graphics.ClickableTextAction;
import jp.kyasu.graphics.BasicPSModifier;
import jp.kyasu.graphics.BasicTSModifier;
import jp.kyasu.graphics.FontModifier;
import jp.kyasu.graphics.ModTextStyle;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.ParagraphStyleModifier;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextAttachment;
import jp.kyasu.graphics.TextBuffer;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.TextStyleModifier;
import jp.kyasu.graphics.VImage;
import jp.kyasu.graphics.html.DefaultHTMLReaderTarget;
import jp.kyasu.graphics.html.HTMLReader;
import jp.kyasu.graphics.html.HTMLReaderTarget;
import jp.kyasu.graphics.html.HTMLStyle;
import jp.kyasu.graphics.html.HTMLText;
import jp.kyasu.graphics.html.HTMLWriter;
import jp.kyasu.graphics.html.VAnchor;
import jp.kyasu.graphics.text.TextChange;
import jp.kyasu.util.RunArray;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Font;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 * A <code>HTMLEditor</code> object is a multi-line area that displays
 * text. It is suited for editing HTML documents.
 * It is created with tool bar.
 *
 * @version 	19 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class HTMLEditor extends RichTextEditor {
    transient protected ActionListener linkActionListener;
    transient protected Stack history;
    transient protected int historyIndex;

    transient protected InputStream loadInputStream;
    transient protected Thread backgroundThread;

    protected ToggleButton linkButton;
    protected Button stopButton;
    protected Button forwardButton;
    protected Button backwardButton;
    protected ToggleButton listButton;
    protected ToggleButton olistButton;
    protected TextField urlField;


    static public final int MAX_HISTORY = 10;

    static public final String L_PARA_STYLE = "pstyle";
    static public final String L_VARIABLE   = "variableFont";
    static public final String L_FIXED      = "fixedFont";
    static public final String L_S_STYLE    = "smallStyle";
    static public final String L_M_STYLE    = "mediumStyle";
    static public final String L_L_STYLE    = "largeStyle";
    static public final String L_VL_STYLE   = "veryLargeStyle";

    static public final String I_LINK       = "link";
    static public final String I_ULIST      = "list";
    static public final String I_OLIST      = "olist";

    static public final String A_LINK       = I_LINK;
    static public final String A_ULIST      = I_ULIST;
    static public final String A_OLIST      = I_OLIST;

    static public final String A_ANCHOR     = "anchor";
    static public final String A_FORWARD    = "forward";
    static public final String A_BACKWARD   = "backward";
    static public final String A_STOP       = "stop";
    static public final String A_RELOAD     = "reload";
    static public final String A_DOC_TITLE  = "preview";

    static public final String P_URL        = "url";
    static public final String P_TITLE      = "title";
    static public final String P_BACKGROUND = "background";
    static public final String P_FOREGROUND = "foreground";

    static protected final Hashtable PStyles = new Hashtable();

    static {
	PStyles.put(EditorResources.getResourceString("normal"),  "P");
	PStyles.put(EditorResources.getResourceString("h1"),      "H1");
	PStyles.put(EditorResources.getResourceString("h2"),      "H2");
	PStyles.put(EditorResources.getResourceString("h3"),      "H3");
	PStyles.put(EditorResources.getResourceString("h4"),      "H4");
	PStyles.put(EditorResources.getResourceString("h5"),      "H5");
	PStyles.put(EditorResources.getResourceString("h6"),      "H6");
	PStyles.put(EditorResources.getResourceString("address"), "ADDRESS");
	PStyles.put(EditorResources.getResourceString("pre"),     "PRE");
	PStyles.put(EditorResources.getResourceString("li"),      "LI");
	PStyles.put(EditorResources.getResourceString("dd"),      "DD");
	PStyles.put(EditorResources.getResourceString("dt"),      "DT");
    }

    static protected final Hashtable HTMLStyles = new Hashtable();

    static {
	HTMLStyles.put(L_S_STYLE, new HTMLStyle(10));
	HTMLStyles.put(L_M_STYLE, new HTMLStyle(12));
	HTMLStyles.put(L_L_STYLE, new HTMLStyle(14));
	HTMLStyles.put(L_VL_STYLE,
	    new HTMLStyle(
		new TextStyle(HTMLStyle.DEFAULT_BASE_FONT_NAME, Font.PLAIN, 18),
		new ParagraphStyle(ParagraphStyle.LEFT, 8, 8, 0, 0)));
    }

    class DefaultLinkAction implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    URL url;
	    try {
		//url = new URL(getURL(), e.getActionCommand());
		// JDK1.2 bug? workaround
		String spec = e.getActionCommand();
		int start = 0;
		int limit = spec.length();
		while ((limit > 0) && (spec.charAt(limit - 1) <= ' ')) {
		    limit--;	// eliminate trailing whitespace
		}
		while ((start < limit) && (spec.charAt(start) <= ' ')) {
		    start++;	// eliminate leading whitespace
		}
		if (start < limit) {
		    spec = spec.substring(start, limit);
		    if (spec.charAt(0) == '#') {
			String base = getURL().toExternalForm();
			int index = base.indexOf('#');
			if (index >= 0) {
			    base = base.substring(0, index);
			}
			url = new URL(base + spec);
		    }
		    else {
			url = new URL(getURL(), spec);
		    }
		}
		else {
		    // spec is empty
		    url = getURL();
		}
	    }
	    catch (MalformedURLException ex) {
		warn(ex);
		return;
	    }

	    goto_page(url);
	}
    }


    /**
     * Constructs a new html text area with tool bar.
     * This html text area is created with vertical scroll bar.
     */
    public HTMLEditor() {
	this(true);
    }

    /**
     * Constructs a new html text area with tool bar. If
     * <code>showToolBar</code> is true, then shows the tool bar initially;
     * otherwise hides.
     * This html text area is created with vertical scroll bar.
     * @param showToolBar if true, then shows the tool bar initially;
     *                    otherwise hides.
     */
    public HTMLEditor(boolean showToolBar) {
	this(20, 65, showToolBar);
    }

    /**
     * Constructs a new html text area with tool bar, with the specified
     * number of rows and columns.
     * This html text area is created with vertical scroll bar.
     * @param rows    the number of rows
     * @param columns the number of columns.
     */
    public HTMLEditor(int rows, int columns) {
	this(rows, columns, true);
    }

    /**
     * Constructs a new html text area with tool bar, with the specified
     * number of rows and columns. If <code>showToolBar</code> is true,
     * then shows the tool bar initially; otherwise hidden.
     * This html text area is created with vertical scroll bar.
     * @param rows        the number of rows
     * @param columns     the number of columns.
     * @param showToolBar if true, then shows the tool bar initially;
     *                    otherwise hides.
     */
    public HTMLEditor(int rows, int columns, boolean showToolBar) {
	this(rows, columns, showToolBar, null, null, null);
    }

    /**
     * Constructs a new html text area with tool bar, with the specified
     * number of rows and columns. If <code>showToolBar</code> is true,
     * then shows the tool bar initially; otherwise hidden.
     * This html text area is created with vertical scroll bar.
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
    public HTMLEditor(int rows, int columns, boolean showToolBar,
		      ActionListener openActionListener,
		      ActionListener saveActionListener,
		      ActionListener printActionListener)
    {
	super(rows, columns, showToolBar, openActionListener,
					  saveActionListener,
					  printActionListener);

	this.linkActionListener = new DefaultLinkAction();
	this.history      = new Stack();
	this.historyIndex = -1;
	this.loadInputStream  = null;
	this.backgroundThread = null;
    }


    protected TextEditModel createDefaultTextEditModel() {
	return new HTMLTextEditModel((HTMLStyle)HTMLStyles.get(L_M_STYLE));
    }

    protected Component createTextComponent(TextEditModel model,
					    int rows, int columns)
    {
	Component textComp = super.createTextComponent(model, rows, columns);

	setClickable(true);

	super.setFont(getHTMLStyle().getDefaultTextStyle().getFont());

	setForeground(getHTMLText().getTextColor());
	setBackground(getHTMLText().getBackgroundColor());
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
     * Sets the foreground color of this text component to be the specified
     * color.
     */
    public void setForeground(Color color) {
	Color oldValue = getForeground();
	if (color != null && editModel != null) {
	    getHTMLText().setTextColor(color);
	}
	if ((oldValue == null ? color == null : oldValue.equals(color)))
	    return;
	super.setForeground(color);
	if (change != null) {
	    change.firePropertyChange(P_FOREGROUND, oldValue, color);
	}
    }

    /**
     * Sets the background color of this text component to be the specified
     * color.
     */
    public void setBackground(Color color) {
	Color oldValue = getBackground();
	if (color != null && editModel != null) {
	    getHTMLText().setBackgroundColor(color);
	}
	if ((oldValue == null ? color == null : oldValue.equals(color)))
	    return;
	super.setBackground(color);
	if (change != null) {
	    change.firePropertyChange(P_BACKGROUND, oldValue, color);
	}
    }

    /**
     * Sets the string of this text component.
     * This operation is not allowed.
     */
    public void setText(String str) {
	throw new RuntimeException("This operation not allowed");
    }

    /**
     * Sets the string of this text component.
     * This operation is not allowed.
     */
    public void setTEXT(Text text) {
	throw new RuntimeException("This operation not allowed");
    }

    /**
     * Sets the html text of this text component.
     * This operation is not allowed.
     */
    public void setRichText(RichText rtext) {
	throw new RuntimeException("This operation not allowed");
    }

    /**
     * Sets the action listener for the link to the specified listener.
     */
    public void setLinkActionListener(ActionListener l) {
	if (l == null)
	    throw new NullPointerException();
	linkActionListener = l;
    }

    /**
     * Returns the html text of this text component.
     * @see #setHTMLText(jp.kyasu.graphics.html.HTMLText)
     */
    public HTMLText getHTMLText() {
	return (HTMLText)super.getRichText();
    }

    /**
     * Sets the html text of this text component.
     * @see #getHTMLText()
     */
    public void setHTMLText(HTMLText htmlText) {
	if (htmlText == null)
	    throw new NullPointerException();
	super.setBackground(htmlText.getBackgroundColor());
	super.setForeground(htmlText.getTextColor());

	super.setRichText(htmlText);
    }

    /**
     * Returns the html style of this text component.
     */
    public HTMLStyle getHTMLStyle() {
	return getHTMLText().getHTMLStyle();
    }

    /**
     * Sets the html style of this html document to be the specified style.
     */
    public void setHTMLStyle(HTMLStyle htmlStyle) {
	TextChange change = getHTMLText().setHTMLStyle(htmlStyle);
	editView.textModelChanged(new TextModelEvent(
					editModel,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
    }

    /**
     * Returns the url of this text component.
     */
    public URL getURL() {
	return getHTMLText().getURL();
    }

    /**
     * Sets the url of this text component to be the specified url.
     */
    public void setURL(URL url) {
	HTMLText htmlText = getHTMLText();
	URL oldValue = htmlText.getURL();
	htmlText.setURL(url);
	if (urlField != null) {
	    urlField.setText(url == null ? "" : url.toExternalForm());
	}
	if ((oldValue == null ? url == null : oldValue.equals(url)))
	    return;
	if (change != null) {
	    change.firePropertyChange(P_URL, oldValue, url);
	}
    }

    /**
     * Returns the title of this text component.
     */
    public String getTitle() {
	return getHTMLText().getTitle();
    }

    /**
     * Sets the title of this text component to be the specified string.
     */
    public void setTitle(String title) {
	HTMLText htmlText = getHTMLText();
	String oldValue = htmlText.getTitle();
	htmlText.setTitle(title);
	if ((oldValue == null ? title == null : oldValue.equals(title)))
	    return;
	if (change != null) {
	    change.firePropertyChange(P_TITLE, oldValue, title);
	}
    }

    /*
    /*
     * Returns the background color of this text component.
     *
    public Color getBackgroundColor() {
	return getHTMLText().getBackgroundColor();
    }

    /*
     * Sets the background color of this text component to be the specified
     * color.
     *
    public void setBackgroundColor(Color color) {
	getHTMLText().setBackgroundColor(color);
    }

    /*
     * Returns the text color of this text component.
     *
    public Color getTextColor() {
	return getHTMLText().getTextColor();
    }

    /*
     * Sets the text color of this text component to be the specified color.
     *
    public void setTextColor(Color color) {
	getHTMLText().setTextColor(color);
    }
    */

    /**
     * Returns the link color of this text component.
     */
    public Color getLinkColor() {
	return getHTMLText().getLinkColor();
    }

    /**
     * Sets the link color of this text component to be the specified color.
     */
    public void setLinkColor(Color color) {
	getHTMLText().setLinkColor(color);
    }

    /**
     * Returns the names of all target anchors (references) in this html
     * document.
     */
    public String[] getAllAnchorNames() {
	return getHTMLText().getAllAnchorNames();
    }

    /**
     * Returns the index of the specified target anchor (reference) in this
     * html document.
     *
     * @param  name the name of the target anchor (reference).
     * @return the index of the target anchor (reference); or <code>-1</code>
     *         if the target anchor (reference) does not exist.
     */
    public int getAnchorIndex(String name) {
	return getHTMLText().getAnchorIndex(name);
    }

    /**
     * Disables sub components.
     * @see #enableSubComps()
     * @see #disableSubComps(boolean)
     */
    public void disableSubComps() {
	disableSubComps(true);
    }

    /**
     * Disables sub components.
     * @param withStop if true, updates the stop button state.
     * @see #disableSubComps()
     */
    public synchronized void disableSubComps(boolean withStop) {
	if (savedTextCursor != null)
	    return;

	if (withStop) {
	    stopButton.setEnabled(true);
	}
	super.disableSubComps();
    }

    /**
     * Enables sub components.
     * @see #disableSubComps()
     * @see #enableSubComps(boolean)
     */
    public void enableSubComps() {
	enableSubComps(true);
    }

    /**
     * Enables sub components.
     * @param withStop if true, updates the stop button state.
     * @see #enableSubComps()
     */
    public synchronized void enableSubComps(boolean withStop) {
	if (savedTextCursor == null)
	    return;

	if (withStop) {
	    stopButton.setEnabled(false);
	}
	super.enableSubComps();
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
	else if (command.equals(A_LINK)) {
	    insert_link();
	}
	else if (command.equals(A_ULIST)) {
	    make_list();
	}
	else if (command.equals(A_OLIST)) {
	    make_ordered_list();
	}
	else if (command.equals(A_ANCHOR)) {
	    insert_anchor();
	}
	else if (command.equals(A_FORWARD)) {
	    forward_page();
	}
	else if (command.equals(A_BACKWARD)) {
	    backward_page();
	}
	else if (command.equals(A_STOP)) {
	    stop_loading();
	}
	else if (command.equals(A_RELOAD)) {
	    reload_page();
	}
	else if (command.equals(A_DOC_TITLE)) {
	    edit_document_property();
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
	if (obj == null || !(obj instanceof String)) {
	    super.itemStateChanged(e);
	    return;
	}

	String command = (String)checkboxMenuMap.get(obj);
	if (command == null) command = (String)obj;
	boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
	if (command.equals(I_LINK)) {
	    if (selected)
	    	insert_link();
	    else
	    	delete_link();
	}
	else if (command.equals(I_ULIST)) {
	    if (selected)
		make_list();
	    else
	    	clear_list();
	}
	else if (command.equals(I_OLIST)) {
	    if (selected)
		make_ordered_list();
	    else
	    	clear_ordered_list();
	}
	else {
	    super.itemStateChanged(e);
	}
    }

    /**
     * Invoked when the position of the text has changed.
     * @see java.awt.event.TextPositionListener
     */
    public void textPositionChanged(TextPositionEvent te) {
	super.textPositionChanged(te); // must be first

	int begin = te.getSelectionBeginIndex();
	int end = te.getSelectionEndIndex();

	if (boldButton != null || italicButton != null ||
	    underlineButton != null || linkButton != null)
	{
	    boolean bold      = false;
	    boolean italic    = false;
	    boolean underline = false;
	    boolean link      = false;
	    for (Enumeration e = textStyles(begin, end); e.hasMoreElements(); )
	    {
		TextStyle tStyle = (TextStyle)e.nextElement();
		boolean click = (tStyle.getClickableTextAction() != null);
		if (click) link = true;
		if (tStyle instanceof ModTextStyle) {
		    FontModifier modifier =
				((ModTextStyle)tStyle).getFontModifier();
		    if (modifier != null) {
			if (modifier.contains(FontModifier.BOLD))
			    bold = true;
			if (modifier.contains(FontModifier.ITALIC))
			    italic = true;
			if (!click && modifier.contains(FontModifier.UNDERLINE))
			    underline = true;
		    }
		}
		else {
		    if (tStyle.getFont().isBold())
			bold = true;
		    if (tStyle.getFont().isItalic())
			italic = true;
		    if (!click && tStyle.getExtendedFont().isUnderline())
			underline = true;
		}
	    }
	    if (boldButton != null) boldButton.setState(bold);
	    if (italicButton != null) italicButton.setState(italic);
	    if (underlineButton != null) underlineButton.setState(underline);
	    if (linkButton != null) linkButton.setState(link);
	}

	if (listButton != null || olistButton != null) {
	    if (begin == end)
		++end;
	    boolean list  = false;
	    boolean olist = false;
	    for (Enumeration e = paragraphStyles(begin, end);
		 e.hasMoreElements();
		)
	    {
		String style = ((ParagraphStyle)e.nextElement()).getStyleName();
		if ("LI-UL".equals(style)) {
		    list = true;
		}
		else if ("LI-OL".equals(style)) {
		    olist = true;
		}
	    }
	    if (listButton != null) listButton.setState(list);
	    if (olistButton != null) olistButton.setState(olist);
	}
    }

    /**
     * Prints the file.
     */
    public void print_file() {
	URL url = getURL();
	print_file(url != null ? url.toExternalForm() : null);
    }

    /**
     * Inserts a link into the selected text.
     */
    public void insert_link() {
	if (selectionIsCaret())
	    return;
	String urlString = Dialog.request(getFrame(),
					  getToolLabel("requestURL"),
					  40);
	if (urlString.length() == 0)
	    return;
	insert_link(urlString);
    }

    /**
     * Inserts a link with the specified url string into the selected text.
     */
    public void insert_link(String urlString) {
	if (selectionIsCaret())
	    return;
	ClickableTextAction action = new ClickableTextAction(urlString);
	action.addActionListener(linkActionListener);
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.CLICKABLE, action);
	modifier.put(BasicTSModifier.COLOR,     getLinkColor());
	modifier.put(BasicTSModifier.UNDERLINE, true);
	modifySelectionTextStyle(modifier);
    }

    /**
     * Inserts a link with the specified url event into the selected text.
     */
    public void insert_link(ActionEvent e) {
	String urlString = e.getActionCommand();
	if (urlString.length() == 0)
	    return;
	insert_link(urlString);
    }

    /**
     * Deletes a link from the selected text.
     */
    public void delete_link() {
	if (selectionIsCaret())
	    return;
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.CLICKABLE, BasicTSModifier.NULL);
	modifier.put(BasicTSModifier.COLOR,     BasicTSModifier.NULL);
	modifier.put(BasicTSModifier.UNDERLINE, BasicTSModifier.NULL);
	modifySelectionTextStyle(modifier);
    }

    class FontSizeModifier implements TextStyleModifier, java.io.Serializable
    {
	boolean large;

	FontSizeModifier(boolean large) {
	    this.large = large;
	}

	public TextStyle modify(TextStyle tStyle) {
	    if (!(tStyle instanceof ModTextStyle))
		return tStyle;
	    int diff = 0;
	    FontModifier fmodifier = ((ModTextStyle)tStyle).getFontModifier();
	    if (fmodifier != null) {
		Object obj = fmodifier.get(FontModifier.SIZE_DIFF);
		if (obj != null && (obj instanceof Integer)) {
		    diff = ((Integer)obj).intValue();
		}
	    }
	    HTMLStyle htmlStyle = getHTMLStyle();
	    int index = htmlStyle.getHTMLFontIndex(diff);
	    if (large) {
		if (index == 7)
		    return tStyle;
		BasicTSModifier modifier = new BasicTSModifier();
		modifier.put(BasicTSModifier.SIZE_DIFF,
			     htmlStyle.getFontPointDifference(index + 1));
		return modifier.modify(tStyle);
	    }
	    else { // small
		if (index == 1)
		    return tStyle;
		BasicTSModifier modifier = new BasicTSModifier();
		modifier.put(BasicTSModifier.SIZE_DIFF,
			     htmlStyle.getFontPointDifference(index - 1));
		return modifier.modify(tStyle);
	    }
	}
    }

    /**
     * Makes the selected text large.
     */
    public void make_font_large() {
	if (selectionIsCaret())
	    return;
	modifySelectionTextStyle(new FontSizeModifier(true));
    }

    /**
     * Makes the selected text small.
     */
    public void make_font_small() {
	if (selectionIsCaret())
	    return;
	modifySelectionTextStyle(new FontSizeModifier(false));
    }

    class HTMLPSModifier
	implements ParagraphStyleModifier, java.io.Serializable
    {
	int listMode;
	ParagraphStyle newStyle;

	HTMLPSModifier(int listMode) {
	    this.listMode = listMode;
	    this.newStyle = null;
	}

	HTMLPSModifier(ParagraphStyle pStyle) {
	    this.listMode = 0;
	    this.newStyle = pStyle;
	}

	public ParagraphStyle modify(ParagraphStyle pStyle) {
	    HTMLStyle htmlStyle = getHTMLStyle();
	    int align = pStyle.getAlignment();
	    int bqLevel = htmlStyle.getBqIncrementLevel(pStyle);
	    int listLevel = htmlStyle.getListIncrementLevel(pStyle);
	    switch (listMode) {
	    case 0: // new non list style
		bqLevel += listLevel;
		listLevel = 0;
		pStyle = newStyle;
		break;
	    case 1: // un list
		if (isListStyle(pStyle)) {
		    bqLevel += listLevel;
		    listLevel = 0;
		    pStyle = htmlStyle.getDefaultParagraphStyle();
		}
		else {
		    return pStyle;
		}
		break;
	    case 2: // unordered list
		if (!("LI-UL".equals(pStyle.getStyleName()))) {
		    listLevel += bqLevel;
		    bqLevel = 0;
		    if (listLevel < 1) listLevel = 1;
		    pStyle = htmlStyle.getULIParagraphStyle(listLevel,
							    getForeground());
		}
		break;
	    case 3: // ordered list
		if (!("LI-OL".equals(pStyle.getStyleName()))) {
		    listLevel += bqLevel;
		    bqLevel = 0;
		    if (listLevel < 1) listLevel = 1;
		    pStyle = htmlStyle.getOLIParagraphStyle(listLevel,
							    0, getForeground());
		}
		break;
	    case 4: // description title
		if (!("DT".equals(pStyle.getStyleName()))) {
		    listLevel += bqLevel;
		    bqLevel = 0;
		    if (listLevel < 1) listLevel = 1;
		    pStyle = htmlStyle.getDTParagraphStyle(listLevel);
		}
		break;
	    case 5: // description definition
		if (!("DD".equals(pStyle.getStyleName()))) {
		    listLevel += bqLevel;
		    bqLevel = 0;
		    if (listLevel < 1) listLevel = 1;
		    ++listLevel;
		    pStyle = htmlStyle.getDDParagraphStyle(listLevel);
		}
		break;
	    default:
		return pStyle;
	    }
	    if (align != ParagraphStyle.LEFT || bqLevel > 0) {
		BasicPSModifier modifier = new BasicPSModifier();
		if (align != ParagraphStyle.LEFT) {
		    modifier.put(BasicPSModifier.ALIGNMENT, align);
		}
		if (bqLevel > 0) {
		    modifier.put(BasicPSModifier.LEFT_INDENT,
			     htmlStyle.getLeftIndentation(bqLevel, listLevel));
		    modifier.put(BasicPSModifier.RIGHT_INDENT,
			     htmlStyle.getRightIndentation(bqLevel, listLevel));
		}
		pStyle = pStyle.deriveStyle(modifier);
	    }
	    return pStyle;
	}
    }

    /**
     * Sets the paragraph style named by the specified name.
     */
    public void set_paragraph_style(String name) {
	if (name == null)
	    return;

	if (name.equals("LI")) {
	    int r[] = getListStyleRange();
	    modifyRangeParagraphStyle(new HTMLPSModifier(2), r[0], r[1]);
	}
	else if (name.equals("DT")) {
	    int r[] = getListStyleRange();
	    modifyRangeParagraphStyle(new HTMLPSModifier(4), r[0], r[1]);
	}
	else if (name.equals("DD")) {
	    int r[] = getListStyleRange();
	    modifyRangeParagraphStyle(new HTMLPSModifier(5), r[0], r[1]);
	}
	else {
	    ParagraphStyle pStyle = getHTMLStyle().getParagraphStyle(name);
	    if (pStyle == null)
		return;
	    int r[] = getListStyleRange();
	    modifyRangeParagraphStyle(new HTMLPSModifier(pStyle), r[0], r[1]);
	}
    }

    /**
     * Makes the selected paragraph as a list.
     */
    public void make_list() {
	int range[] = getListStyleRange();
	modifyRangeParagraphStyle(new HTMLPSModifier(2), range[0], range[1]);
    }

    /**
     * Clears the selected paragraph as a list.
     */
    public void clear_list() {
	int range[] = getListStyleRange();
	modifyRangeParagraphStyle(new HTMLPSModifier(1), range[0], range[1]);
    }

    /**
     * Makes the selected paragraph as an ordered list.
     */
    public void make_ordered_list() {
	int range[] = getListStyleRange();
	modifyRangeParagraphStyle(new HTMLPSModifier(3), range[0], range[1]);
    }

    /**
     * Clears the selected paragraph as an ordered list.
     */
    public void clear_ordered_list() {
	clear_list();
    }

    class LeftIndentPSModifier
	implements ParagraphStyleModifier, java.io.Serializable
    {
	boolean increment;

	LeftIndentPSModifier(boolean increment) {
	    this.increment = increment;
	}

	public ParagraphStyle modify(ParagraphStyle pStyle) {
	    HTMLStyle htmlStyle = getHTMLStyle();
	    int align = pStyle.getAlignment();
	    int bqLevel = htmlStyle.getBqIncrementLevel(pStyle);
	    int listLevel = htmlStyle.getListIncrementLevel(pStyle);
	    boolean needToBqLevel = (bqLevel > 0);
	    String pStyleName = pStyle.getStyleName();
	    if ("LI-UL".equals(pStyleName)) {
		if (increment) { ++listLevel; }
		else { listLevel = Math.max(listLevel - 1, 1); }
		pStyle = htmlStyle.getULIParagraphStyle(listLevel,
							getForeground());
	    }
	    else if ("LI-OL".equals(pStyleName)) {
		if (increment) { ++listLevel; }
		else { listLevel = Math.max(listLevel - 1, 1); }
		int index = htmlStyle.getOLIIndex(pStyle.getHeading());
		pStyle =
		    htmlStyle.getOLIParagraphStyle(listLevel, index,
						   getForeground());
	    }
	    else if ("DT".equals(pStyleName)) {
		if (increment) { ++listLevel; }
		else { listLevel = Math.max(listLevel - 1, 0); }
		pStyle = htmlStyle.getDTParagraphStyle(listLevel);
	    }
	    else if ("DD".equals(pStyleName)) {
		if (increment) { ++listLevel; }
		else { listLevel = Math.max(listLevel - 1, 1); }
		pStyle = htmlStyle.getDDParagraphStyle(listLevel);
	    }
	    else {
		if (increment) { ++bqLevel; }
		else if (bqLevel > 0) { --bqLevel; }
		else {
		    return pStyle;
		}
		needToBqLevel = true;
	    }
	    if (align != ParagraphStyle.LEFT || needToBqLevel) {
		BasicPSModifier modifier = new BasicPSModifier();
		if (align != ParagraphStyle.LEFT) {
		    modifier.put(BasicPSModifier.ALIGNMENT, align);
		}
		if (needToBqLevel) {
		    modifier.put(BasicPSModifier.LEFT_INDENT,
			     htmlStyle.getLeftIndentation(bqLevel, listLevel));
		    modifier.put(BasicPSModifier.RIGHT_INDENT,
			     htmlStyle.getRightIndentation(bqLevel, listLevel));
		}
		pStyle = pStyle.deriveStyle(modifier);
	    }
	    return pStyle;
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
     * Inserts an anchor.
     */
    public void insert_anchor() {
	String target = Dialog.request(getFrame(),
				       getToolLabel("requestAnchor"));
	if (target.length() == 0)
	    return;
	insert_anchor(target);
    }

    /**
     * Inserts an anchor with the specified target string.
     */
    public void insert_anchor(String target) {
	Text text = new Text(new TextAttachment(new VAnchor(target)),
			     getInsertionStyle());
	replaceSelection(text);
    }

    /**
     * Inserts an anchor with the specified target event into the selected text.
     */
    public void insert_anchor(ActionEvent e) {
	String target = e.getActionCommand();
	if (target.length() == 0)
	    return;
	insert_anchor(target);
    }

    /**
     * Goes to the page with the specified url.
     */
    public void goto_page(URL url) {
	if (url == null)
	    throw new NullPointerException();
	load(url, false);
    }

    /**
     * Goes to the page with the specified url string.
     */
    public void goto_page(String urlString) {
	URL url;
	try {
	    url = new URL(urlString);
	}
	catch (MalformedURLException e) {
	    warn(e);
	    return;
	}
	goto_page(url);
    }

    /**
     * Goes to the page with the specified url action.
     */
    public void goto_page(ActionEvent e) {
	goto_page(e.getActionCommand());
    }

    /**
     * Reloads the current page.
     */
    public void reload_page() {
	URL url = getURL();
	if (url == null || historyIndex < 0 || history.isEmpty()) {
	    goto_page(getToolLabel(L_KFC_URL));
	}
	load(url, true);
    }

    /**
     * Goes to the next page.
     */
    public void forward_page() {
	if (historyIndex < 0 || historyIndex == history.size() - 1) {
	    return;
	}
	updateLocationOfTextInHistory();
	++historyIndex;
	if (historyIndex == history.size() - 1) {
	    forwardButton.setEnabled(false);
	}
	if (historyIndex > 0) {
	    backwardButton.setEnabled(true);
	}
	gotoHistory(historyIndex);
    }

    /**
     * Goes to the previouse page.
     */
    public void backward_page() {
	if (historyIndex < 0 || historyIndex == 0) {
	    return;
	}
	updateLocationOfTextInHistory();
	--historyIndex;
	if (historyIndex == 0) {
	    backwardButton.setEnabled(false);
	}
	if (historyIndex < history.size() - 1) {
	    forwardButton.setEnabled(true);
	}
	gotoHistory(historyIndex);
    }

    /**
     * Stops the loading.
     */
    public void stop_loading() {
	closeLoadInputStream();
	if (backgroundThread != null) {
	    Thread thread = backgroundThread;
	    backgroundThread = null;
	    thread.interrupt();
	    try { thread.join(); } // wait for backgroundThread to die
	    catch (InterruptedException e) {}
	}
    }

    /**
     * Edits the document property.
     */
    public void edit_document_property() {
	String title = getTitle();
	title = Dialog.request(getFrame(),
			       getToolTip(A_DOC_TITLE) + ":",
			       (title == null ? "" : title),
			       40);
	if (title.length() == 0)
	    return;
	setTitle(title);
    }

    /**
     * Opens the new file.
     */
    public void open_file() {
	// TODO
	super.open_file();
    }

    /**
     * Opens the specified file.
     */
    public void open_file(File file) {
	goto_page(fileToURLString(file));
    }

    /**
     * Saves the text into the file.
     */
    public void save_file() {
	// TODO
	writeTarget = new File("edit.html");
	URL url = getURL();
	String file;
	if (url != null && (file = url.getFile()) != null) {
	    int index = file.lastIndexOf('/');
	    if (index >= 0 && index < file.length() - 1) {
		file = file.substring(index + 1, file.length());
		index = file.lastIndexOf('.');
		if (index >= 1) {
		    file = file.substring(0, index);
		    writeTarget = new File(file + ".html");
		}
	    }
	}

	save_file_as();
    }

    /**
     * Saves the text into the specified file.
     */
    public void save_file_as(File file) {
	boolean ok = false;
	try {
	    disableSubComps(false);
	    ok = saveAsHTML(file);
	}
	finally {
	    enableSubComps(false);
	}

	if (ok) {
	    setWriteTarget(file);
	}
    }

    /**
     * Saves the contents of this component as a HTML into the specified file.
     * @param file the file to be saved into.
     * @return true if the saving was succeeded.
     */
    public boolean saveAsHTML(File file) {
	boolean saveOk = false;
	try {
	    HTMLWriter htmlWriter = new HTMLWriter(getHTMLText());
	    htmlWriter.writeTo(file, writeEncoding);
	    textChanged = false;
	    saveOk = true;
	}
	catch (Exception e) {
	    warn(e);
	}
	return saveOk;
    }

    class LoadRunnable implements Runnable {
	URL url;
	boolean reload;

	LoadRunnable(URL url, boolean reload) {
	    this.url    = url;
	    this.reload = reload;
	}

	public void run() {
	    Exception ex = null;
	    try {
		disableSubComps();

		if (!reload && loadHistory(url))
		    return;

		URLConnection conn = url.openConnection();
		conn.getURL();
		conn.getContentType();
		loadInputStream = conn.getInputStream();

		load(conn, reload);
	    }
	    catch (IOException ie)       { ex = ie; }
	    catch (SecurityException se) { ex = se; }
	    finally {
		loadInputStream  = null;
		backgroundThread = null;

		enableSubComps();
	    }

	    if (ex != null) {
		warn(ex);
	    }
	}
    }

    protected void load(URL url, boolean reload) {
	if (backgroundThread != null)
	    return;
	backgroundThread = new Thread(new LoadRunnable(url, reload));
	try {
	    int p = Math.max(Thread.currentThread().getPriority() - 1,
			     Thread.MIN_PRIORITY);
	    backgroundThread.setPriority(p);
	}
	catch (SecurityException e) {}
	backgroundThread.start();
    }

    protected boolean loadHistory(URL url) {
	HistoryElement e = getHistoryElement(url);
	if (e == null)
	    return false;

	updateLocationOfTextInHistory();

	HTMLText htmlText = e.htmlText;
	htmlText.setURL(url);

	setHTMLText(htmlText);
	setURL(url);
	setTitle(htmlText.getTitle());
	setBackground(htmlText.getBackgroundColor());
	setForeground(htmlText.getTextColor());
	setLinkColor(htmlText.getLinkColor());

	textChanged = false;

	String ref = getURL().getRef();
	int index = -1;
	if (ref != null) {
	    index = getAnchorIndex(ref);
	}

	// enables at here for setCaretPosition() and pushToHistory()
	enableSubComps();

	setCaretPosition((index >= 0 ? index : 0), true);

	pushToHistory(getHTMLText());

	return true;
    }

    protected void load(URLConnection conn, boolean reload) {
	URL url = conn.getURL();
	String contentType = conn.getContentType();

	updateLocationOfTextInHistory();

	setHTMLText(new HTMLText(getHTMLStyle()));
	setURL(url);
	setTitle(url.toExternalForm());
	setBackground(Color.white);
	setForeground(Color.black);
	setLinkColor(Color.blue);

	try {
	    if (contentType == null) {
		loadError(conn, "No content type");
	    }
	    if (contentType.equals("text/html")) {
		loadHTML(conn, url);
	    }
	    else if (contentType.equals("text/plain")) {
		loadText(conn);
	    }
	    else if (contentType.equals("image/gif")      ||
		     contentType.equals("image/jpeg")     ||
		     contentType.equals("image/x-bitmap") ||
		     contentType.equals("image/x-pixmap"))
	    {
		loadImage(conn, url);
	    }
	    else {
		loadError(conn, "Unknown content type");
	    }
	}
	catch (IOException e) {
	}

	closeLoadInputStream();

	textChanged = false;

	String ref = getURL().getRef();
	int index = -1;
	if (ref != null) {
	    index = getAnchorIndex(ref);
	}

	// enables at here for setCaretPosition() and pushToHistory()
	enableSubComps();

	setCaretPosition((index >= 0 ? index : 0), true);

	if (reload) {
	    updateHistory(getHTMLText());
	}
	else {
	    pushToHistory(getHTMLText());
	}
    }

    protected void loadHTML(URLConnection conn, URL url) throws IOException {
	HTMLReader htmlReader = new HTMLReader(getHTMLStyle(),
					       linkActionListener);
	HTMLReaderTarget target =
		(incrementalLoad ?
			(HTMLReaderTarget)new HTMLEditorTarget(this) :
			(HTMLReaderTarget)new DefaultHTMLReaderTarget());
	BufferedReader reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream(),
						      readEncoding),
				(32 * 1024));

	try {
	    URL oldURL = getURL();
	    String oldTitle = getTitle();

	    htmlReader.read(url, reader, target);
	    if (!incrementalLoad) {
		HTMLText htmlText = target.getHTMLText();

		setHTMLText(htmlText);

		// fires PropertyChangeEvents
		URL newURL = htmlText.getURL();
		String newTitle = htmlText.getTitle();
		if (!oldURL.equals(newURL)) {
		    htmlText.setURL(oldURL);
		    setURL(newURL);
		}
		if (!oldTitle.equals(newTitle)) {
		    htmlText.setTitle(oldTitle);
		    setTitle(newTitle);
		}
		setBackground(htmlText.getBackgroundColor());
		setForeground(htmlText.getTextColor());
	    }
	}
	finally {
	    target.close();
	    reader.close();
	}
    }

    protected void loadText(URLConnection conn) throws IOException {
	BufferedReader reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream(),
						      readEncoding),
				(32 * 1024));

	try {
	    int incCount = (incrementalLoad ? INC_LINE_COUNT : 0);

	    ParagraphStyle pStyle = getHTMLStyle().getParagraphStyle("PRE");
	    setCaretPosition(0);
	    boolean editable = isEditable();
	    try {
		setEditable(true);
		setSelectionParagraphStyle(pStyle);
	    }
	    finally {
		setEditable(editable);
	    }

	    TextStyle tStyle = pStyle.getBaseStyle();
	    TextBuffer buffer = new TextBuffer();
	    buffer.setTextStyle(tStyle);
	    int lineCount = 0;
	    String line;
	    while ((line = reader.readLine()) != null) {
		if (loadInputStream == null)
		    throw new IOException();

		if (incCount > 0 && ++lineCount > incCount) {
		    appendAnyway(buffer.toText(), false);
		    lineCount = 0;
		    buffer = new TextBuffer();
		    buffer.setTextStyle(tStyle);
		}
		buffer.append(line).append(Text.LINE_BREAK_CHAR);
	    }
	    if (buffer.length() > 0) {
		appendAnyway(buffer.toText(), false);
	    }
	}
	finally {
	    reader.close();
	}
    }

    protected void loadImage(URLConnection conn, URL url) {
	Image image = getToolkit().getImage(url);

	setCaretPosition(0);
	appendAnyway(
		new Text(
		    new TextAttachment(new VImage(image, url)),
		    getHTMLStyle().getDefaultParagraphStyle().getBaseStyle()),
		false);
    }

    protected void loadError(URLConnection conn, String errorMessage) {
	setCaretPosition(0);
	appendAnyway(
		new Text(
		    errorMessage,
		    getHTMLStyle().getDefaultParagraphStyle().getBaseStyle()),
		false);
    }

    protected HistoryElement getHistoryElement(URL url) {
	if (history.isEmpty()) {
	    return null;
	}
	for (Enumeration e = history.elements(); e.hasMoreElements(); ) {
	    HistoryElement elem = (HistoryElement)e.nextElement();
	    if (url.sameFile(elem.htmlText.getURL())) {
		return elem;
	    }
	}
	return null;
    }

    protected void pushToHistory(HTMLText htmlText) {
	if (historyIndex >= 0) {
	    while (historyIndex < history.size() - 1) {
		history.pop();
	    }
	}
	HistoryElement elem = new HistoryElement(
					htmlText,
					getLocationOfText(),
					htmlText.getURL().getRef());
	history.push(elem);
	while (history.size() > MAX_HISTORY) {
	    history.removeElementAt(0);
	}
	historyIndex = history.size() - 1;
	forwardButton.setEnabled(false);
	if (historyIndex > 0) {
	    backwardButton.setEnabled(true);
	}
    }

    protected void updateHistory(HTMLText htmlText) {
	if (historyIndex < 0) {
	    return;
	}
	HistoryElement elem = new HistoryElement(
					htmlText,
					getLocationOfText(),
					htmlText.getURL().getRef());
	history.setElementAt(elem, historyIndex);
    }

    protected void gotoHistory(int index) {
	historyIndex = index;
	HistoryElement elem = (HistoryElement)history.elementAt(historyIndex);
	HTMLText htmlText = elem.htmlText;
	URL url = htmlText.getURL();
	if (elem.ref == null) {
	    if (url.getRef() != null) {
		try {
		    htmlText.setURL(new URL(url.getProtocol(),
					    url.getHost(),
					    url.getPort(),
					    url.getFile()));
		}
		catch (MalformedURLException e) {}
	    }
	}
	else {
	    if (!elem.ref.equals(url.getRef())) {
		try {
		    htmlText.setURL(new URL(url.getProtocol(),
					    url.getHost(),
					    url.getPort(),
					    url.getFile() + "#" + elem.ref));
		}
		catch (MalformedURLException e) {}
	    }
	}

	URL oldURL = getURL();
	String oldTitle = getTitle();

	setHTMLText(htmlText);

	// fires PropertyChangeEvents
	URL newURL = htmlText.getURL();
	String newTitle = htmlText.getTitle();
	if (!oldURL.equals(newURL)) {
	    htmlText.setURL(oldURL);
	    setURL(newURL);
	}
	if (!oldTitle.equals(newTitle)) {
	    htmlText.setTitle(oldTitle);
	    setTitle(newTitle);
	}

	setForeground(htmlText.getTextColor());
	setBackground(htmlText.getBackgroundColor());
	setLinkColor(htmlText.getLinkColor());
	setLocationOfText(elem.locationOfText);
    }

    protected void updateLocationOfTextInHistory() {
	if (historyIndex < 0 || history.isEmpty()) {
	    return;
	}
	HistoryElement elem = (HistoryElement)history.elementAt(historyIndex);
	elem.locationOfText = getLocationOfText();
    }

    protected void closeLoadInputStream() {
	if (loadInputStream != null) {
	    try { loadInputStream.close(); } // I/O exception will occur.
	    catch (IOException e) {}
	    finally { loadInputStream = null; }
	}
    }


    class URLAction implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    goto_page(e);
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
	Component web1[] = createWebComponents1();
	Component web2[] = createWebComponents2();
	Component[][] bar1 = new Component[][]{ web1, web2 };

	Component pstyle = createParagraphStyleComponent();
	Component name = createFontNameComponent();
	Component[] font = createFontComponents1();
	Component[] font1 = new Component[font.length + 2];
	font1[0] = pstyle;
	font1[1] = name;
	System.arraycopy(font, 0, font1, 2, font.length);

	Component[] font2 = createFontComponents2();
	Component[] align = createAlignmentComponents();
	Component[] list  = createListComponents();
	Component[][] bar2 = new Component[][]{ font1, font2, align, list };

	Component[] file   = createFileComponents(openActionListener,
						  saveActionListener);
	Component[] print  = createPrintComponents(printActionListener);
	Component[] find   = createFindComponents(false);
	Component[] edit   = createEditComponents();
	Component[] doc    = createDocumentComponents();
	Component[] insert = createInsertComponents();

	Component[][] bar3 = new Component[][]{ file, print, find, edit, doc,
						insert };
	return new ToolBar(new Component[][][]{ bar1, bar2, bar3 },
			   showToolBar);
    }

    protected Component[] createWebComponents1() {
	backwardButton = createIconButton(A_BACKWARD);
	backwardButton.addActionListener(this);
	forwardButton = createIconButton(A_FORWARD);
	forwardButton.addActionListener(this);
	Button reload = createIconButton(A_RELOAD);
	reload.addActionListener(this);
	stopButton = createIconButton(A_STOP);
	stopButton.addActionListener(this);

	forwardButton.setEnabled(false);
	backwardButton.setEnabled(false);
	stopButton.setEnabled(false);
	subComps.removeElement(stopButton);

	return new Component[]{ backwardButton, forwardButton, reload,
				stopButton };
    }

    protected Component[] createWebComponents2() {
	Label label = new Label("URL");
	urlField = new TextField(40);
	urlField.addActionListener(new URLAction());

	addSubComp(urlField);

	return new Component[]{ label, urlField };
    }

    class PStyleItemListener implements ItemListener, java.io.Serializable {
	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() != ItemEvent.SELECTED)
		return;
	    Object obj = e.getItem();
	    if (obj != null && (obj instanceof String)) {
		String value = (String)PStyles.get((String)obj);
		if (value != null) {
		    set_paragraph_style(value);
		}
	    }
	}
    }

    protected Component createParagraphStyleComponent() {
	Choice pstyle = new Choice();
	String styles[] = { "normal", "h1", "h2", "h3", "h4", "h5", "h6",
			    "address", "pre", "li", "dd", "dt" };
	for (int i = 0; i < styles.length; i++) {
	    pstyle.addItem(getToolLabel(styles[i]));
	}
	pstyle.setToolTipText(getToolTip(L_PARA_STYLE));
	pstyle.addItemListener(new PStyleItemListener());

	addSubComp(pstyle);

	return pstyle;
    }

    class FontNameItemListener implements ItemListener, java.io.Serializable {
	String variable = getToolLabel(L_VARIABLE);
	String fixed    = getToolLabel(L_FIXED);

	public void itemStateChanged(ItemEvent e) {
	    if (e.getStateChange() != ItemEvent.SELECTED)
		return;
	    Object obj = e.getItem();
	    if (obj != null && (obj instanceof String)) {
		String s = (String)obj;
		if (s.equals(variable)) {
		    set_font_name("SansSerif");
		}
		else if (s.equals(fixed)) {
		    set_font_name("Monospaced");
		}
	    }
	}
    }

    protected Component createFontNameComponent() {
	Choice name = new Choice();
	name.addItem(getToolLabel(L_VARIABLE));
	name.addItem(getToolLabel(L_FIXED));
	name.setToolTipText(getToolTip(L_FONT_NAME));
	name.addItemListener(new FontNameItemListener());

	name.setEnabled(false);
	addSubComp(name);
	addCaretDisableComp(name);

	return name;
    }

    protected Component[] createListComponents() {
	listButton = createIconToggleButton(I_ULIST);
	listButton.addItemListener(this);
	olistButton = createIconToggleButton(I_OLIST);
	olistButton.addItemListener(this);
	Button decindent = createIconButton(A_DEC_INDENT);
	decindent.addActionListener(this);
	Button incindent = createIconButton(A_INC_INDENT);
	incindent.addActionListener(this);
	return new Component[]{ listButton, olistButton, decindent, incindent };
    }

    protected Component[] createDocumentComponents() {
	Button doc = createIconButton(A_DOC_TITLE);
	doc.addActionListener(this);

	return new Component[]{ doc };
    }

    protected Component[] createInsertComponents() {
	linkButton = createIconToggleButton(I_LINK);
	linkButton.addItemListener(this);
	Button anchor = createIconButton(A_ANCHOR);
	anchor.addActionListener(this);
	Button hr = createIconButton(A_HR);
	hr.addActionListener(this);
	Button image = createIconButton(A_IMAGE);
	image.addActionListener(this);

	linkButton.setEnabled(false);
	addCaretDisableComp(linkButton);

	return new Component[]{ linkButton, anchor, hr, image };
    }

    protected Menu createViewMenu() {
	Menu menu = new Menu(getToolLabel(L_VIEW));
	menu.add(createFontMenu());
	menu.add(createMenuItem(A_DOC_TITLE, this));
	menu.addSeparator();
	menu.add(createMenuItem(A_RELOAD, this));
	menu.addSeparator();
	menu.add(createCheckboxMenuItem(I_INCREMENTAL_LOAD, isIncrementalLoad(),
					this));
	menu.addSeparator();
	menu.add(createReadCharSetMenu());
	menu.add(createWriteCharSetMenu());
	return menu;
    }

    class HTMLFontSelection implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    String command = e.getActionCommand();
	    HTMLStyle htmlStyle = (HTMLStyle)HTMLStyles.get(command);
	    if (htmlStyle == null)
		return;
	    if (htmlStyle == getHTMLStyle())
		return;
	    setHTMLStyle(htmlStyle);
	}
    }

    protected Menu createFontMenu() {
	SelectionMenu menu = new SelectionMenu(getToolLabel(L_FONT));
	menu.addActionListener(new HTMLFontSelection());
	String names[] = { L_S_STYLE, L_M_STYLE, L_L_STYLE, L_VL_STYLE };
	HTMLStyle htmlStyle = getHTMLStyle();
	for (int i = 0; i < names.length; i++) {
	    String command = names[i];
	    String label = getToolLabel(command);
	    boolean state = (htmlStyle == HTMLStyles.get(command));
	    menu.add(label, command, state);
	}
	return menu;
    }

    protected Menu createInsertMenu() {
	Menu menu = new Menu(getToolLabel(L_INSERT));
	MenuItem link = createMenuItem(A_LINK, this);
	link.setEnabled(false);
	addCaretDisableItem(link);
	menu.add(link);
	menu.add(createMenuItem(A_ANCHOR, this));
	menu.addSeparator();
	menu.add(createMenuItem(A_HR,     this));
	menu.add(createMenuItem(A_IMAGE,  this));
	return menu;
    }

    protected Menu createFormatMenu() {
	Menu menu = new Menu(getToolLabel(L_FORMAT));

	Menu font = new Menu(getToolLabel(L_FONT_STYLE));
	font.add(createMenuItem(A_BOLD,      this));
	font.add(createMenuItem(A_ITALIC,    this));
	font.add(createMenuItem(A_UNDERLINE, this));
	font.setEnabled(false);
	addCaretDisableItem(font);
	menu.add(font);

	MenuItem clear = createMenuItem(A_CLEAR_STYLE, this);
	clear.setEnabled(false);
	addCaretDisableItem(clear);
	menu.add(clear);

	menu.addSeparator();

	MenuItem large = createMenuItem(A_LARGE, this);
	MenuItem small = createMenuItem(A_LARGE, this);
	large.setEnabled(false);
	small.setEnabled(false);
	addCaretDisableItem(large);
	addCaretDisableItem(small);
	menu.add(large);
	menu.add(small);

	menu.addSeparator();

	menu.add(createPStyleMenu());

	Menu align = new Menu(getToolLabel(L_ALIGN));
	align.add(createMenuItem(A_LEFT,   this));
	align.add(createMenuItem(A_CENTER, this));
	align.add(createMenuItem(A_RIGHT,  this));
	menu.add(align);

	Menu list = new Menu(getToolLabel(L_LIST));
	list.add(createMenuItem(A_ULIST, this));
	list.add(createMenuItem(A_OLIST, this));
	menu.add(list);

	menu.addSeparator();

	menu.add(createMenuItem(A_INC_INDENT, this));
	menu.add(createMenuItem(A_DEC_INDENT, this));

	return menu;
    }

    class PStyleActionListener implements ActionListener, java.io.Serializable
    {
	public void actionPerformed(ActionEvent e) {
	    String value = (String)PStyles.get(e.getActionCommand());
	    if (value != null) {
		set_paragraph_style(value);
	    }
	}
    }

    protected Menu createPStyleMenu() {
	Menu menu = new Menu(getToolLabel(L_PARA_STYLE));
	ActionListener l = new PStyleActionListener();
	String styles[] = { "normal", "h1", "h2", "h3", "h4", "h5", "h6",
			    "address", "pre", "li", "dd", "dt" };
	for (int i = 0; i < styles.length; i++) {
	    String style = styles[i];
	    String label = getToolLabel(style);
	    MenuItem mi = new MenuItem(label);
	    mi.setActionCommand(label);
	    mi.addActionListener(l);
	    menu.add(mi);
	}
	return menu;
    }

    protected void insertTextAttachmentAsLine(TextAttachment ta) {
	TextBuffer buffer = new TextBuffer();
	buffer.setTextStyle(getInsertionStyle());
	buffer.append(Text.LINE_SEPARATOR_CHAR);
	buffer.append(ta);
	buffer.append(Text.LINE_SEPARATOR_CHAR);

	MultipleUndo mundo = new MultipleUndo();

	int start = getSelectionStart();
	int end   = getSelectionEnd();
	Undo undo = getModel().replace(start, end, buffer.toText());
	if (undo != null) {
	    mundo.addUndo(undo);
	}

	ParagraphStyle pStyle = getHTMLStyle().getParagraphStyle("P");
	int pos = getCaretPosition();

	undo = getModel().setParagraphStyle(pos - 1, pos - 1, pStyle);
	if (undo != null) {
	    mundo.addUndo(undo);
	}

	getController().setUndo(mundo);

	/*
	setCaretPosition(getCaretPosition() - 1);
	setSelectionParagraphStyle(pStyle);
	setCaretPosition(getCaretPosition() + 1);
	*/
    }

    protected int[] getListStyleRange() {
	int start = getSelectionStart();
	HTMLText htmlText = getHTMLText();
	RunArray pstyles = htmlText.getParagraphStyleRuns();
	if (isListStyle((ParagraphStyle)pstyles.get(start))) {
	    int runStart = start - pstyles.getRunOffsetAt(start);
	    int runEnd   = start + pstyles.getRunLengthAt(start);
	    if (runEnd > htmlText.length())
		runEnd = htmlText.length();
	    return new int[]{ runStart, runEnd };
	}
	else {
	    int end = getSelectionEnd();
	    return new int[]{ start, end };
	}
    }

    protected final boolean isListStyle(ParagraphStyle pStyle) {
	String styleName = pStyle.getStyleName();
	return (styleName != null && (styleName.equals("LI-UL") ||
				      styleName.equals("LI-OL") ||
				      styleName.equals("DT")    ||
				      styleName.equals("DD")));
    }

    protected String fileToURLString(File file) {
	Vector path = new Vector();
	File f = new File(file.getAbsolutePath());
	path.insertElementAt(f.getName(), 0);
	String name;
	while ((name = f.getParent()) != null) {
	    f = new File(name);
	    if (f.getName().length() == 0)
		break;
	    path.insertElementAt(f.getName(), 0);
	}
	StringBuffer buffer = new StringBuffer();
	buffer.append("file:");
	for (Enumeration e = path.elements(); e.hasMoreElements(); ) {
	    buffer.append('/').append((String)e.nextElement());
	}
	return buffer.toString();
    }


    /** Internal constant for serialization */
    static protected final String actionListenerK = "actionL".intern();

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();

	jp.kyasu.awt.ListenerSerializer.write(s,
					      actionListenerK,
					      linkActionListener);
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
		linkActionListener = (ActionListener)s.readObject();
	    else // skip value for unrecognized key
		s.readObject();
	}

	history      = new Stack();
	historyIndex = -1;
	loadInputStream  = null;
	backgroundThread = null;
    }


    /** Executes the examples. */
    public static void main(String args[]) {
	HTMLEditor editor = new HTMLEditor();
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame("HTMLEditor");
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


final
class HistoryElement {
    HTMLText htmlText;
    String ref;
    Point locationOfText;

    public HistoryElement(HTMLText htmlText, Point locationOfText, String ref)
    {
	if (htmlText == null || locationOfText == null)
	    throw new NullPointerException();
	this.htmlText       = htmlText;
	this.locationOfText = locationOfText;
	this.ref            = ref;
    }
}
