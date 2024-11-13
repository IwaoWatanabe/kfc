/*
 * TextEditor.java
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
import jp.kyasu.awt.TextComponent;
import jp.kyasu.awt.TextEditModel;
import jp.kyasu.awt.ToggleButton;
import jp.kyasu.awt.ToolBar;
import jp.kyasu.awt.event.TextPositionEvent;
import jp.kyasu.awt.event.TextPositionListener;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextBuffer;
import jp.kyasu.graphics.VActiveButton;
import jp.kyasu.graphics.VImage;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Font;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PrintJob;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A <code>TextEditor</code> object is a multi-line area that displays text.
 * It can be set to allow editing or read-only modes.
 * It is created with tool bar.
 *
 * @version 	19 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextEditor extends TextComponent
	implements ActionListener, ItemListener
{
    protected PropertyChangeSupport change;
    protected boolean textChanged = false;

    protected String readEncoding  = EditorResources.DEFAULT_READ_CHARSET;
    protected String writeEncoding = EditorResources.DEFAULT_WRITE_CHARSET;
    protected boolean incrementalLoad = true;
    protected File writeTarget = null;

    protected Hashtable checkboxMenuMap = new Hashtable();

    protected Vector subComps          = null;
    protected Vector subCompStates     = null;
    protected Vector caretDisableComps = null;


    static public final String L_KFC_URL          = "kfcURL";
    static public final String L_KFC_AUTHOR       = "kfcAuthor";
    static public final String L_KFC_ADDRESS      = "kfcAddress";
    static public final String L_KFC_ADDRESS2     = "kfcAddress2";

    static public final String L_FILE             = "file";
    static public final String L_NEW_WIN          = "newWin";
    static public final String L_CLOSE            = "close";
    static public final String L_EXIT             = "exit";

    static public final String L_EDIT             = "edit";
    static public final String L_VIEW             = "view";

    static public final String L_HELP             = "help";
    static public final String L_VERSION          = "version";

    static public final String L_FONT             = "font";
    static public final String L_FONT_NAME        = "fontName";
    static public final String L_FONT_STYLE       = "fontStyle";
    static public final String L_FONT_SIZE        = "fontSize";
    static public final String L_FONT_COLOR       = "fontColor";
    static public final String L_READ_ENCODING    = "readEncoding";
    static public final String L_WRITE_ENCODING   = "writeEncoding";

    static public final String L_OPEN_CONFIRM     = "openConfirm";
    static public final String L_CLOSE_CONFIRM    = "closeConfirm";
    static public final String L_EXIT_CONFIRM     = "exitConfirm";

    static public final String A_OPEN             = "open";
    static public final String A_SAVE             = "save";
    static public final String A_SAVE_AS          = "saveAs";
    static public final String A_PRINT            = "print";

    static public final String A_COPY             = "copy";
    static public final String A_CUT              = "cut";
    static public final String A_PASTE            = "paste";
    static public final String A_UNDO             = "undo";
    static public final String A_FIND             = "find";
    static public final String A_GOTO             = "goto";

    static public final String I_WORD_WRAP        = "wordWrap";
    static public final String I_SOFT_TAB         = "softTab";
    static public final String I_AUTO_INDENT      = "autoIndent";
    static public final String I_SHOW_MATCH       = "showMatch";
    static public final String I_INCREMENTAL_LOAD = "incLoad";


    static public final String P_WORD_WRAP        = I_WORD_WRAP;
    static public final String P_SOFT_TAB         = I_SOFT_TAB;
    static public final String P_AUTO_INDENT      = I_AUTO_INDENT;
    static public final String P_SHOW_MATCH       = I_SHOW_MATCH;
    static public final String P_READ_ENCODING    = L_READ_ENCODING;
    static public final String P_WRITE_ENCODING   = L_WRITE_ENCODING;
    static public final String P_INCREMENTAL_LOAD = I_INCREMENTAL_LOAD;
    static public final String P_FILE             = L_FILE;
    static public final String P_SUB_COMPS        = "subComps";


    static protected final int INC_LINE_COUNT = 10;


    /**
     * Constructs a new text area with tool bar.
     * This text area is created with vertical scroll bar.
     */
    public TextEditor() {
	this(true);
    }

    /**
     * Constructs a new text area with tool bar. If <code>showToolBar</code>
     * is true, then shows the tool bar initially; otherwise hides.
     * This text area is created with vertical scroll bar.
     * @param showToolBar if true, then shows the tool bar initially;
     *                    otherwise hides.
     */
    public TextEditor(boolean showToolBar) {
	this(20, 80, showToolBar);
    }

    /**
     * Constructs a new text area with tool bar, with the specified number
     * of rows and columns.
     * This text area is created with vertical scroll bar.
     * @param rows    the number of rows
     * @param columns the number of columns.
     */
    public TextEditor(int rows, int columns) {
	this(rows, columns, true);
    }

    /**
     * Constructs a new text area with tool bar, with the specified number
     * of rows and columns. If <code>showToolBar</code> is true, then shows
     * the tool bar initially; otherwise hidden.
     * This text area is created with vertical scroll bar.
     * @param rows        the number of rows
     * @param columns     the number of columns.
     * @param showToolBar if true, then shows the tool bar initially;
     *                    otherwise hides.
     */
    public TextEditor(int rows, int columns, boolean showToolBar) {
	this(rows, columns, showToolBar, null, null, null);
    }

    /**
     * Constructs a new text area with tool bar, with the specified number
     * of rows and columns. If <code>showToolBar</code> is true, then shows
     * the tool bar initially; otherwise hidden.
     * This text area is created with vertical scroll bar.
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
    public TextEditor(int rows, int columns, boolean showToolBar,
		      ActionListener openActionListener,
		      ActionListener saveActionListener,
		      ActionListener printActionListener)
    {
	super();
	this.change = new PropertyChangeSupport(this);
	Component textComp = createTextComponent(rows, columns);
	ToolBar toolBar = createToolBar(showToolBar,
					openActionListener,
					saveActionListener,
					printActionListener);
	initComponent(textComp, toolBar);
    }


    protected TextEditModel createDefaultTextEditModel() {
	return new DefaultTextEditModel(DEFAULT_VERTICAL_STYLE);
    }

    protected Component createTextComponent(int rows, int columns) {
	return createTextComponent(createDefaultTextEditModel(), rows, columns);
    }

    protected Component createTextComponent(TextEditModel model,
					    int rows, int columns)
    {
	Component textComp =
			createTextComponent(model, SCROLLBARS_VERTICAL_ONLY);
	editModel.addTextListener(this);
	editView.addTextPositionListener(this);

	this.rows    = rows;
	this.columns = columns;

	return textComp;
    }


    protected void initComponent(Component textComp, ToolBar toolBar) {
	setLayout(new BorderLayout(0, 0));
	add(textComp, BorderLayout.CENTER);
	add(toolBar,  BorderLayout.NORTH);

	/*
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	setLayout(gridbag);

	c.gridx = 0;

	c.anchor = GridBagConstraints.WEST;
	c.fill = GridBagConstraints.NONE;
	gridbag.setConstraints(toolBar, c);
	add(toolBar);

	c.gridy = 1;
	c.weightx = 1.0;
	c.weighty = 1.0;
	c.anchor = GridBagConstraints.CENTER;
	c.fill = GridBagConstraints.BOTH;
	gridbag.setConstraints(textComp, c);
	add(textComp);
	*/
    }


    /**
     * Sets the string that is presented by this text component to be the
     * specified string.
     */
    public void setText(String str) {
	super.setText(str);
	textChanged = false;
    }

    /**
     * Sets the text of this text component.
     */
    public void setTEXT(Text text) {
	super.setTEXT(text);
	textChanged = false;
    }

    /**
     * Sets the rich text of this text component.
     */
    public void setRichText(RichText rtext) {
	super.setRichText(rtext);
	textChanged = false;
    }

    /**
     * Invoked when the value of the text has changed.
     * @see java.awt.event.TextListener
     */
    public void textValueChanged(TextEvent e) {
	textChanged = true;
	super.textValueChanged(e);
    }

    /**
     * Checks if the text is changed.
     */
    public boolean isTextChanged() {
	return textChanged;
    }

    /**
     * Sets the text is changed or not.
     */
    public void setTextChanged(boolean b) {
	textChanged = b;
    }

    /**
     * Returns the edit menu for this editor, or <code>null</code>.
     */
    public Menu getEditMenu() {
	return createEditMenu();
    }

    /**
     * Returns the view menu for this editor, or <code>null</code>.
     */
    public Menu getViewMenu() {
	return createViewMenu();
    }

    /**
     * Returns the insert menu for this editor, or <code>null</code>.
     */
    public Menu getInsertMenu() {
	return createInsertMenu();
    }

    /**
     * Returns the format menu for this editor, or <code>null</code>.
     */
    public Menu getFormatMenu() {
	return createFormatMenu();
    }

    /**
     * Returns the tool tip for the spcified name.
     */
    public String getToolTip(String name) {
	String label = getToolLabel(name);
	if (label.length() > 3 && label.endsWith("...")) {
	    label = label.substring(0, label.length() - 3);
	}
	return label;
    }

    /**
     * Returns the tool label for the spcified name.
     */
    public String getToolLabel(String name) {
	return EditorResources.getResourceString(name);
    }

    /**
     * Returns the icon for the spcified name.
     * <p>
     * Available icons are: 'anchor', 'backward', 'bold', 'center', 'color',
     * 'copy', 'cut', 'decindent', 'find', 'forward', 'goto', 'hr', 'image',
     * 'incindent', 'italic', 'java', 'large', 'left', 'link', 'list', 'new',
     * 'olist', 'open', 'paste', 'preview', 'print', 'redo', 'reload',
     * 'right', 'save', 'scolor', 'small', 'stop', 'table', 'underline',
     * 'undo', and 'unlist'.
     */
    public VImage getIcon(String name) {
	String file = "icons/" + name + ".gif";
	return jp.kyasu.awt.AWTResources.getIcon(getClass(), file);
    }

    /**
     * Checks if this editor wraps the line at word boundary.
     */
    public boolean isWordWrap() {
	return getLineWrap() == WORD_WRAP;
    }

    /**
     * Enables the word wrap.
     */
    public void setWordWrap(boolean wordWrap) {
	boolean oldValue = isWordWrap();
	if (oldValue == wordWrap)
	    return;
	super.setLineWrap((int)(wordWrap ? WORD_WRAP : CHAR_WRAP));
	if (change != null) {
	    change.firePropertyChange(P_WORD_WRAP,
				      new Boolean(oldValue),
				      new Boolean(wordWrap));
	}
    }

    /**
     * Enables the soft tab.
     */
    public void setSoftTab(boolean b) {
	boolean oldValue = isSoftTab();
	if (oldValue == b)
	    return;
	super.setSoftTab((int)(b ? 4 : 0));
	if (change != null) {
	    change.firePropertyChange(P_SOFT_TAB,
				      new Boolean(oldValue),
				      new Boolean(b));
	}
    }

    /**
     * Enables the auto indent.
     */
    public void setAutoIndentEnabled(boolean autoIndent) {
	boolean oldValue = isAutoIndentEnabled();
	if (oldValue == autoIndent)
	    return;
	super.setAutoIndentEnabled(autoIndent);
	if (change != null) {
	    change.firePropertyChange(P_AUTO_INDENT,
				      new Boolean(oldValue),
				      new Boolean(autoIndent));
	}
    }

    /**
     * Enables the show match.
     */
    public void setShowMatchEnabled(boolean showMatch) {
	boolean oldValue = isShowMatchEnabled();
	if (oldValue == showMatch)
	    return;
	super.setShowMatchEnabled(showMatch);
	if (change != null) {
	    change.firePropertyChange(P_SHOW_MATCH,
				      new Boolean(oldValue),
				      new Boolean(showMatch));
	}
    }

    /**
     * Returns the encoding name for reading.
     */
    public String getReadEncoding() {
	return readEncoding;
    }

    /**
     * Sets the encoding name for reading to be the specified name.
     */
    public void setReadEncoding(String enc) {
	if (enc == null)
	    throw new NullPointerException();
	if (enc == readEncoding)
	    return;
	String oldValue = readEncoding;
	readEncoding = enc;
	if (change != null) {
	    change.firePropertyChange(P_READ_ENCODING, oldValue, enc);
	}
    }

    /**
     * Returns the encoding name for writing.
     */
    public String getWriteEncoding() {
	return writeEncoding;
    }

    /**
     * Sets the encoding name for writing to be the specified name.
     */
    public void setWriteEncoding(String enc) {
	if (enc == null)
	    throw new NullPointerException();
	if (enc == writeEncoding)
	    return;
	String oldValue = writeEncoding;
	writeEncoding = enc;
	if (change != null) {
	    change.firePropertyChange(P_WRITE_ENCODING, oldValue, enc);
	}
    }

    /**
     * Checks if the loading is done incrementally.
     */
    public boolean isIncrementalLoad() {
	return incrementalLoad;
    }

    /**
     * Enables or disables the incremental loading.
     */
    public void setIncrementalLoad(boolean b) {
	if (b == incrementalLoad)
	    return;
	boolean oldValue = incrementalLoad;
	incrementalLoad = b;
	if (change != null) {
	    change.firePropertyChange(P_INCREMENTAL_LOAD,
				      new Boolean(oldValue), new Boolean(b));
	}
    }

    /**
     * Add a PropertyChangeListener to the listener list.
     * @param listener The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	change.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * @param listener The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
	change.removePropertyChangeListener(listener);
    }

    /**
     * Adds the specified text event listener to recieve text events from
     * this text component.
     * @param l the text event listener.
     */
    public synchronized void addTextListener(TextListener l) {
	if (l == null)
	    return;
	textListener = java.awt.AWTEventMulticaster.add(textListener, l);
	enableEvents(0); // mark newEventsOnly
    }

    /**
     * Removes the specified text event listener so that it no longer
     * receives text events from this textcomponent
     * @param l the text event listener.
     */
    public synchronized void removeTextListener(TextListener l) {
	textListener = java.awt.AWTEventMulticaster.remove(textListener, l);
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
	}
    }

    /**
     * Invoked when the position of the text has changed.
     * @see java.awt.event.TextPositionListener
     */
    public void textPositionChanged(TextPositionEvent te) {
	if (caretDisableComps != null) {
	    boolean b = !te.selectionIsCaret();
	    for (Enumeration e = caretDisableComps.elements();
		 e.hasMoreElements();
		 )
	    {
		Object obj = e.nextElement();
		if (obj instanceof Component) {
		    ((Component)obj).setEnabled(b);
		}
		else if (obj instanceof MenuItem) {
		    ((MenuItem)obj).setEnabled(b);
		}
	    }
	}
	super.textPositionChanged(te);
    }

    transient protected Cursor savedTextCursor = null;

    /**
     * Disables sub components.
     * @see #enableSubComps()
     */
    public synchronized void disableSubComps() {
	if (savedTextCursor != null)
	    return;

	if (change != null) {
	    change.firePropertyChange(P_SUB_COMPS,
				      new Boolean(true), new Boolean(false));
	}

	savedTextCursor = editView.getCursor();
	editView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	//setEditable(false);

	editModel.removeTextListener(this);
	editView.removeTextPositionListener(this);

	if (subComps == null)
	    return;
	subCompStates = new Vector();
	for (Enumeration e = subComps.elements(); e.hasMoreElements(); ) {
	    Component comp = (Component)e.nextElement();
	    subCompStates.addElement(new Boolean(comp.isEnabled()));
	    comp.setEnabled(false);
	}
    }

    /**
     * Enables sub components.
     * @see #disableSubComps()
     */
    public synchronized void enableSubComps() {
	if (savedTextCursor == null)
	    return;

	if (change != null) {
	    change.firePropertyChange(P_SUB_COMPS,
				      new Boolean(false), new Boolean(true));
	}

	editView.setCursor(savedTextCursor);
	savedTextCursor = null;
	//setEditable(true);

	editModel.addTextListener(this);
	editView.addTextPositionListener(this);

	if (subComps == null)
	    return;
	Enumeration s = subCompStates.elements();
	for (Enumeration e = subComps.elements(); e.hasMoreElements(); ) {
	    Component comp = (Component)e.nextElement();
	    boolean enabled = ((Boolean)s.nextElement()).booleanValue();
	    comp.setEnabled(enabled);
	}
	subCompStates = null;
    }

    /**
     * Invoked when an action occurs.
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand();
	if (command.equals(A_OPEN)) {
	    open_file();
	}
	else if (command.equals(A_SAVE)) {
	    save_file();
	}
	else if (command.equals(A_SAVE_AS)) {
	    save_file_as();
	}
	else if (command.equals(A_PRINT)) {
	    print_file();
	}
	else if (command.equals(A_COPY)) {
	    copy_clipboard();
	}
	else if (command.equals(A_CUT)) {
	    cut_clipboard();
	}
	else if (command.equals(A_PASTE)) {
	    paste_clipboard();
	}
	else if (command.equals(A_UNDO)) {
	    undo();
	}
	else if (command.equals(A_FIND)) {
	    find_word();
	}
	else if (command.equals(A_GOTO)) {
	    goto_line();
	}
    }

    /**
     * Invoked when an item's state has been changed.
     * @see java.awt.event.ItemListener
     */
    public void itemStateChanged(ItemEvent e) {
	Object obj = e.getItem();
	if (obj == null || !(obj instanceof String)) {
	    return;
	}

	String command = (String)checkboxMenuMap.get(obj);
	if (command == null) command = (String)obj;
	boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
	if (command.equals(I_WORD_WRAP)) {
	    setWordWrap(selected);
	}
	else if (command.equals(I_SOFT_TAB)) {
	    setSoftTab(selected);
	}
	else if (command.equals(I_AUTO_INDENT)) {
	    setAutoIndentEnabled(selected);
	}
	else if (command.equals(I_SHOW_MATCH)) {
	    setShowMatchEnabled(selected);
	}
	else if (command.equals(I_INCREMENTAL_LOAD)) {
	    setIncrementalLoad(selected);
	}
    }


    /**
     * Opens the new file.
     */
    public void open_file() {
	if (textChanged &&
	    !Dialog.confirm(getFrame(), getToolLabel(L_OPEN_CONFIRM)))
	{
	    return;
	}
	File file = getFileFromLoadDialog(
			getToolTip(A_OPEN),
			(writeTarget != null ? writeTarget.getParent() : null),
			(writeTarget != null ? writeTarget.getName() : null));
	if (file == null)
	    return;
	open_file(file);
    }

    /**
     * Opens the specified file.
     */
    public void open_file(File file) {
	boolean ok = false;
	try {
	    BufferedReader reader = new BufferedReader(
					new InputStreamReader(
						new FileInputStream(file),
						readEncoding));
	    disableSubComps();
	    ok = load(reader);
	    reader.close();
	}
	catch (IOException e) {
	    warn(e);
	}
	finally {
	    enableSubComps();
	}

	if (ok) {
	    setWriteTarget(file);
	}
    }

    /**
     * Saves the text into the current file.
     */
    public void save_file() {
	if (writeTarget == null)
	    save_file_as();
	else
	    save_file_as(writeTarget);
    }

    /**
     * Saves the text into the selected file.
     */
    public void save_file_as() {
	File file = getFileFromSaveDialog(
			getToolTip(A_SAVE),
			(writeTarget != null ? writeTarget.getParent() : null),
			(writeTarget != null ? writeTarget.getName() : null));
	if (file == null)
	    return;
	save_file_as(file);
    }

    /**
     * Saves the text into the specified file.
     */
    public void save_file_as(File file) {
	boolean ok = false;
	try {
	    BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(
						new FileOutputStream(file),
						writeEncoding));
	    disableSubComps();
	    ok = save(writer);
	    writer.close();
	}
	catch (IOException e) {
	    warn(e);
	}
	finally {
	    enableSubComps();
	}

	if (ok) {
	    setWriteTarget(file);
	}
    }

    /**
     * Loads the contents of the specified reader incrementally into
     * this component
     * @param reader the buffered reader to be loaded.
     * @return true if the loading was succeeded.
     */
    public boolean load(BufferedReader reader) {
	int incCount = incrementalLoad ? INC_LINE_COUNT : 0;
	boolean loadOk = false;
	setRichText(new RichText(getRichText().getRichTextStyle()));
	setCaretPosition(0);
	try {
	    TextBuffer buffer =
		new TextBuffer(getRichText().getRichTextStyle().getTextStyle());
	    int lineCount = 0;
	    String line;
	    while ((line = reader.readLine()) != null) {
		if (incCount > 0 && ++lineCount > incCount) {
		    appendAnyway(buffer.toText(), false);
		    lineCount = 0;
		    buffer = new TextBuffer(
			getRichText().getRichTextStyle().getTextStyle());
		}
		buffer.append(line).append(Text.LINE_SEPARATOR_CHAR);
	    }
	    if (buffer.length() > 0) {
		appendAnyway(buffer.toText(), false);
	    }
	    setCaretPosition(0);
	    loadOk = true;
	}
	catch (Exception e) {
	    TextBuffer buffer =
		new TextBuffer(getRichText().getRichTextStyle().getTextStyle());
	    buffer.append("--> " + e.getClass().getName() + " occurred");
	    buffer.append(Text.LINE_SEPARATOR_CHAR);
	    int len = getRichText().length();
	    appendAnyway(buffer.toText(), false);
	    select(len, len + buffer.length() - 1);
	}
	clearUndo();
	textChanged = false;
	return loadOk;
    }

    /**
     * Saves the contents of this component into the specified writer.
     * @param writer the writer to save into.
     * @return true if the saving was succeeded.
     */
    public boolean save(Writer writer) {
	boolean saveOk = false;
	try {
	    TextBuffer buffer = new TextBuffer(getTEXT());
	    buffer.writeTo(writer);
	    textChanged = false;
	    saveOk = true;
	}
	catch (Exception e) {
	    warn(e);
	}
	return saveOk;
    }

    /**
     * Prints the file.
     */
    public void print_file() {
	print_file(writeTarget != null ? writeTarget.getPath() : null);
    }

    protected void print_file(String header) {
	PrintJob job =
	    getToolkit().getPrintJob(getFrame(), getToolTip(A_PRINT), null);
	if (job == null)
	    return;
	try {
	    disableSubComps();
	    print(job, header, true);
	}
	finally {
	    enableSubComps();
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

	Component[][] bar = new Component[][]{ file, print, find, edit };
	return new ToolBar(new Component[][][]{ bar }, showToolBar);
    }

    protected Component[] createFileComponents(
					ActionListener openActionListener,
					ActionListener saveActionListener)
    {
	Button open = createIconButton(A_OPEN);
	open.addActionListener(openActionListener != null ?
					openActionListener : this);
	Button save = createIconButton(A_SAVE);
	save.addActionListener(saveActionListener != null ?
					saveActionListener : this);
	return new Component[]{ open, save };
    }

    protected Component[] createPrintComponents(
					ActionListener printActionListener)
    {
	Button print = createIconButton(A_PRINT);
	print.addActionListener(printActionListener == null ?
					this : printActionListener);
	return new Component[]{ print };
    }

    protected Component[] createFindComponents(boolean withGoto) {
	Button find = createIconButton(A_FIND);
	find.addActionListener(this);
	if (withGoto) {
	    Button gotob = createIconButton(A_GOTO);
	    gotob.addActionListener(this);
	    return new Component[]{ find, gotob };
	}
	else {
	    return new Component[]{ find };
	}
    }

    protected Component[] createEditComponents() {
	Button copy = createIconButton(A_COPY);
	copy.addActionListener(this);
	Button cut = createIconButton(A_CUT);
	cut.addActionListener(this);
	Button paste = createIconButton(A_PASTE);
	paste.addActionListener(this);
	Button undo = createIconButton(A_UNDO);
	undo.addActionListener(this);

	copy.setEnabled(false);
	cut.setEnabled(false);
	addCaretDisableComp(copy);
	addCaretDisableComp(cut);

	return new Component[]{ copy, cut, paste, undo };
    }

    protected Button createIconButton(String command) {
	VImage vimage = getIcon(command);
	Button b = new Button(new VActiveButton(vimage));
	b.setActionCommand(command);
	b.setToolTipText(getToolTip(command));
	addSubComp(b);
	return b;
    }

    protected ToggleButton createIconToggleButton(String command) {
	VImage vimage = getIcon(command);
	ToggleButton b = new ToggleButton(new VActiveButton(vimage));
	b.setActionCommand(command);
	b.setToolTipText(getToolTip(command));
	addSubComp(b);
	return b;
    }

    protected Menu createInsertMenu() {
	return null;
    }

    protected Menu createFormatMenu() {
	return null;
    }

    protected Menu createEditMenu() {
	Menu menu = new Menu(getToolLabel(L_EDIT));
	MenuItem copy = createMenuItem(A_COPY, this);
	MenuItem cut  = createMenuItem(A_CUT,  this);
	menu.add(copy);
	menu.add(cut);
	menu.add(createMenuItem(A_PASTE, this));
	menu.addSeparator();
	menu.add(createMenuItem(A_FIND,  this));
	menu.addSeparator();
	menu.add(createMenuItem(A_UNDO,  this));

	copy.setEnabled(false);
	cut.setEnabled(false);
	addCaretDisableItem(copy);
	addCaretDisableItem(cut);

	return menu;
    }

    protected Menu createViewMenu() {
	Menu menu = new Menu(getToolLabel(L_VIEW));
	menu.add(createFontMenu());
	menu.addSeparator();
	menu.add(createCheckboxMenuItem(I_WORD_WRAP, isWordWrap(), this));
	menu.add(createCheckboxMenuItem(I_SOFT_TAB, isSoftTab(), this));
	menu.add(createCheckboxMenuItem(I_AUTO_INDENT, isAutoIndentEnabled(),
					this));
	menu.add(createCheckboxMenuItem(I_SHOW_MATCH, isShowMatchEnabled(),
					this));
	menu.addSeparator();
	menu.add(createCheckboxMenuItem(I_INCREMENTAL_LOAD, isIncrementalLoad(),
					this));
	menu.addSeparator();
	menu.add(createReadCharSetMenu());
	menu.add(createWriteCharSetMenu());
	return menu;
    }

    class FontSelection implements ActionListener, java.io.Serializable {
	boolean isName;

	FontSelection(boolean isName) {
	    this.isName = isName;
	}

	public void actionPerformed(ActionEvent e) {
	    String command = e.getActionCommand();
	    Font font = getFont();
	    if (isName) {
		if (command.equals(font.getName()))
		    return;
		font = new Font(command, font.getStyle(), font.getSize());
	    }
	    else { // size
		int size;
		try { size = Integer.parseInt(command); }
		catch (NumberFormatException ex) { return; }
		if (size == font.getSize())
		    return;
		font = new Font(font.getName(), font.getStyle(), size);
	    }
	    setFont(font);
	}
    }

    protected Menu createFontMenu() {
	Menu menu = new Menu(getToolLabel(L_FONT));
	Font font = getFont();

	SelectionMenu name = new SelectionMenu(getToolLabel(L_FONT_NAME));
	name.addActionListener(new FontSelection(true));
	String names[] = { "Monospaced", "SansSerif", "Serif", "Dialog" };
	for (int i = 0; i < names.length; i++) {
	    String label = names[i];
	    boolean state = label.equalsIgnoreCase(font.getName());
	    name.add(label, label, state);
	}
	menu.add(name);

	SelectionMenu size = new SelectionMenu(getToolLabel(L_FONT_SIZE));
	size.addActionListener(new FontSelection(false));
	int sizes[] = { 8, 10, 12, 14, 16, 18, 20, 22, 24 };
	for (int i = 0; i < sizes.length; i++) {
	    String label = String.valueOf(sizes[i]);
	    boolean state = (sizes[i] == font.getSize());
	    size.add(label, label, state);
	}
	menu.add(size);

	return menu;
    }

    class CharSetSelection implements ActionListener, java.io.Serializable {
	boolean read;

	CharSetSelection(boolean read) {
	    this.read = read;
	}

	public void actionPerformed(ActionEvent e) {
	    String encoding = e.getActionCommand();
	    if (read) {
		setReadEncoding(encoding);
	    }
	    else {
		setWriteEncoding(encoding);
	    }
	}
    }

    protected CharSetMenu createReadCharSetMenu() {
	CharSetMenu read = new CharSetMenu(getToolLabel(L_READ_ENCODING),
					   getReadEncoding());
	read.addActionListener(new CharSetSelection(true));
	return read;
    }

    protected CharSetMenu createWriteCharSetMenu() {
	CharSetMenu write = new CharSetMenu(getToolLabel(L_WRITE_ENCODING),
					    getWriteEncoding());
	write.addActionListener(new CharSetSelection(false));
	return write;
    }

    protected MenuItem createMenuItem(String command, ActionListener l) {
	MenuItem mi = new MenuItem(getToolLabel(command));
	mi.setActionCommand(command);
	mi.addActionListener(l);
	return mi;
    }

    protected CheckboxMenuItem createCheckboxMenuItem(String command,
						      boolean state,
						      ItemListener l)
    {
	String label = getToolLabel(command);
	CheckboxMenuItem cmi = new CheckboxMenuItem(label, state);
	//cmi.setActionCommand(command);
	checkboxMenuMap.put(label, command);
	cmi.addItemListener(l);
	return cmi;
    }

    protected void addSubComp(Component c) {
	if (subComps == null)
	    subComps = new Vector();
	subComps.addElement(c);
    }

    protected void addCaretDisableComp(Component c) {
	if (caretDisableComps == null)
	    caretDisableComps = new Vector();
	caretDisableComps.addElement(c);
    }

    protected void addCaretDisableItem(MenuItem item) {
	if (caretDisableComps == null)
	    caretDisableComps = new Vector();
	caretDisableComps.addElement(item);
    }

    protected void setWriteTarget(File file) {
	File oldFile = writeTarget;
	if ((oldFile == null ? file == null : oldFile.equals(file)))
	    return;
	writeTarget = file;
	if (change != null) {
	    change.firePropertyChange(P_FILE, oldFile, file);
	}
    }

    protected void warn(Exception e) {
	Dialog.warn(getFrame(),
		    e.getClass().getName() + ": " + e.getMessage());
    }

    protected File getFileFromLoadDialog(String label,
					 String initDir, String initFile)
    {
	FileDialog dialog = new FileDialog(getFrame(), label, FileDialog.LOAD);
	if (initDir != null)
	    dialog.setDirectory(initDir);
	if (initFile != null)
	    dialog.setFile(initFile);
	dialog.setVisible(true);
	String ddir = dialog.getDirectory();
	String dfile = dialog.getFile();
	dialog.dispose();
	if (ddir == null || dfile == null) {
	    return null;
	}
	File f = new File(ddir + dfile);
	if (!f.exists()) {
	    Dialog.warn(getFrame(), f.getPath() + getToolLabel("fileNotExist"));
	    return null;
	}
	else if (!f.isFile()) {
	    Dialog.warn(getFrame(), f.getPath() + getToolLabel("fileNotFile"));
	    return null;
	}
	else if (!f.canRead()) {
	    Dialog.warn(getFrame(), f.getPath() + getToolLabel("fileNotRead"));
	    return null;
	}
	return f;
    }

    protected File getFileFromSaveDialog(String label,
					 String initDir, String initFile)
    {
	FileDialog dialog = new FileDialog(getFrame(), label, FileDialog.SAVE);
	if (initDir != null)
	    dialog.setDirectory(initDir);
	if (initFile != null)
	    dialog.setFile(initFile);
	dialog.setVisible(true);
	String ddir = dialog.getDirectory();
	String dfile = dialog.getFile();
	dialog.dispose();
	if (ddir == null || dfile == null) {
	    return null;
	}
	File f = new File(ddir + dfile);
	if (!f.exists()) {
	    // do not care
	}
	else if (!f.isFile()) {
	    Dialog.warn(getFrame(), f.getPath() + getToolLabel("fileNotFile"));
	    return null;
	}
	else if (!f.canWrite()) {
	    Dialog.warn(getFrame(), f.getPath() + getToolLabel("fileNotWrite"));
	    return null;
	}
	else if (!Dialog.confirm(getFrame(),
				 f.getPath() + getToolLabel("fileOverwrite")))
	{
	    return null;
	}
	return f;
    }

    protected void appendAnyway(Text text, boolean scroll) {
	if (isEditable()) {
	    append(text, scroll);
	}
	else {
	    try {
		setEditable(true);
		append(text, scroll);
	    }
	    finally {
		setEditable(false);
	    }
	}
    }


    /** Executes the examples. */
    public static void main(String args[]) {
	TextEditor editor = new TextEditor();
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame("TextEditor");
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
