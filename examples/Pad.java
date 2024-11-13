/*
 * Pad.java
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

import jp.kyasu.awt.*;
import jp.kyasu.graphics.*;
import jp.kyasu.editor.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.*;
import java.beans.*;
import java.util.Vector;
import java.lang.reflect.*;

/**
 * An abstract pad class.
 *
 * @version 	15 May 1999
 * @author 	Kazuki YASUMATSU
 */
public abstract class Pad implements ActionListener, PropertyChangeListener {
    protected TextEditor editor;
    protected MenuItem closeMenuItem;
    protected MenuItem exitMenuItem;


    static protected final Font PAD_FONT = TextStyle.DEFAULT_STYLE.getFont();

    static protected final PadManager Manager = new PadManager();


    // Initialize KFC Rendering Hints.
    static {
	try {
	    String antialias = System.getProperty("antialias");
	    if (antialias != null && antialias.equals("true")) {
		/*
		jp.kyasu.awt.AWTResources.RENDERING_HINTS.put(
			java.awt.RenderingHints.KEY_ANTIALIASING,
			java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		*/
		Class renderingHintsClass =
		    Class.forName("java.awt.RenderingHints");
		Object keyTextAntialiasing =
		  renderingHintsClass.getField("KEY_ANTIALIASING").get(null);
		Object valueTextAntialiasOn =
		  renderingHintsClass.getField("VALUE_ANTIALIAS_ON").get(null);
		Class awtResourcesClass =
		    Class.forName("jp.kyasu.awt.AWTResources");
		Object renderingHints =
		    awtResourcesClass.getField("RENDERING_HINTS").get(null);
		Method putMethod =
		    renderingHints.getClass().getMethod("put",
			new Class[] { Object.class, Object.class });
		putMethod.invoke(renderingHints,
		    new Object[]{ keyTextAntialiasing, valueTextAntialiasOn });
	    }
	}
	catch (Exception e) {
	}
    }


    protected Pad(TextEditor editor) {
	if (editor == null)
	    throw new NullPointerException();
	this.editor = editor;
	editor.addPropertyChangeListener(this);
    }

    abstract public String getPadName();

    class CloseWindow extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
	    if (Manager.getPadCount() > 1) {
		closeWindow();
	    }
	    else {
		exitWindow();
	    }
	}
    }

    public void open() {
	if (Manager.contains(this))
	    throw new RuntimeException("pad is already opened");

	Frame frame = new Frame(getPadName());
	frame.addWindowListener(new CloseWindow());
	frame.setMenuBar(createMenuBar());
	NativePanel p = new NativePanel();
	p.add(editor, BorderLayout.CENTER);
	frame.add(p, BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);

	Manager.addPad(this);
    }

    public void openNewWindow() {
	try {
	    Pad pad = (Pad)getClass().newInstance();
	    pad.open();
	}
	catch (Exception e) {}
    }

    public void closeWindow() {
	if (!Manager.contains(this))
	    throw new RuntimeException("pad is not opened");

	java.awt.Frame frame = editor.getFrame();
	if (editor.isTextChanged()) {
	    if (!Dialog.confirm(
			frame,
			editor.getToolLabel(TextEditor.L_CLOSE_CONFIRM)))
	    {
		return;
	    }
	}
	frame.setVisible(false);
	frame.dispose();

	Manager.removePad(this);
    }

    public void exitWindow() {
	if (!Manager.contains(this))
	    throw new RuntimeException("pad is not opened");

	if (editor.isTextChanged() &&
	    !Dialog.confirm(
			editor.getFrame(),
			editor.getToolLabel(TextEditor.L_EXIT_CONFIRM)))
	{
	    return;
	}

	System.exit(0);
    }

    public void showVersion() {
	String padName = getPadName();
	TextBuffer buffer = new TextBuffer();
	buffer.setTextStyle(new TextStyle("SansSerif", Font.PLAIN, 12));
	buffer.append(editor.getIcon("java"));
	buffer.append(padName + "\n\n");
	buffer.setFontStyle(Font.ITALIC);
	buffer.append("This software includes KFC.\n\n");
	buffer.setColor(Color.blue);
	buffer.setFontStyle(Font.BOLD);
	buffer.append(editor.getToolLabel(TextEditor.L_KFC_AUTHOR) + "\n");
	buffer.setTextStyle(new TextStyle("Monospaced", Font.PLAIN, 10));
	buffer.append(editor.getToolLabel(TextEditor.L_KFC_ADDRESS) + "\n");
	buffer.append(editor.getToolLabel(TextEditor.L_KFC_ADDRESS2) + "\n");
	buffer.setColor(Color.blue);
	buffer.append(editor.getToolLabel(TextEditor.L_KFC_URL));
	Visualizable v = new VBorderedWrapper(
				new VRichText(buffer),
				new VTitledPaneBorder(
				    editor.getToolLabel(TextEditor.L_VERSION)));
	/*
	Dialog.message(editor.getFrame(),
		       editor.getToolLabel(TextEditor.L_VERSION),
		       v);
	*/
	final Dialog dialog = new Dialog(
				editor.getFrame(),
				editor.getToolLabel(TextEditor.L_VERSION),
				true);
	Panel p = new Panel();
	Button b = new Button("Show KFC Copyright");
	b.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		Dialog.showKFCCopyright(editor.getFrame());
	    }
	});
	p.add(b);
	b = new Button(
		AWTResources.getResourceString("kfc.dialog.okLabel", "OK"));
	b.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dialog.setVisible(false);
	    }
	});
	p.add(b);
	dialog.add(new Label(v), BorderLayout.CENTER);
	dialog.add(p, BorderLayout.SOUTH);
	dialog.pack();
	dialog.setVisible(true);
	dialog.dispose();
    }


    public void actionPerformed(ActionEvent e) {
	String command = e.getActionCommand();
	if (command.equals(TextEditor.L_NEW_WIN)) {
	    openNewWindow();
	}
	else if (command.equals(TextEditor.L_CLOSE)) {
	    closeWindow();
	}
	else if (command.equals(TextEditor.L_EXIT)) {
	    exitWindow();
	}
	else if (command.equals(TextEditor.L_VERSION)) {
	    showVersion();
	}
    }

    public void propertyChange(PropertyChangeEvent e) {
	Object obj = e.getSource();
	String name = e.getPropertyName();
	if (obj == Manager) {
	    if (name.equals(PadManager.PROP_PAD_COUNT)) {
		int count = ((Integer)e.getNewValue()).intValue();
		if (closeMenuItem != null) {
		    closeMenuItem.setEnabled(count > 1);
		}
		if (exitMenuItem != null) {
		    exitMenuItem.setEnabled(count == 1);
		}
	    }
	}
	else if (obj == editor) {
	    if (name.equals(TextEditor.P_SUB_COMPS)) {
		java.awt.Frame f = editor.getFrame();
		MenuBar bar = (f == null ? null : f.getMenuBar());
		if (bar != null) {
		    boolean b = ((Boolean)e.getNewValue()).booleanValue();
		    setMenuBarEnabled(bar, b);
		}
	    }
	}
    }


    protected MenuBar createMenuBar() {
	MenuBar mbar = new MenuBar();
	mbar.setFont(PAD_FONT);
	mbar.add(createFileMenu());
	Menu menu = editor.getEditMenu();
	if (menu != null) mbar.add(menu);
	menu = editor.getViewMenu();
	if (menu != null) mbar.add(menu);
	menu = editor.getInsertMenu();
	if (menu != null) mbar.add(menu);
	menu = editor.getFormatMenu();
	if (menu != null) mbar.add(menu);
	mbar.setHelpMenu(createHelpMenu());
	return mbar;
    }

    protected Menu createFileMenu() {
	Menu menu = new Menu(editor.getToolLabel(TextEditor.L_FILE));

	menu.add(createMenuItem(editor.getToolLabel(TextEditor.L_NEW_WIN),
				TextEditor.L_NEW_WIN, this));
	menu.addSeparator();
	menu.add(createMenuItem(editor.getToolLabel(TextEditor.A_OPEN),
				TextEditor.A_OPEN, editor));
	menu.add(createMenuItem(editor.getToolLabel(TextEditor.A_SAVE),
				TextEditor.A_SAVE, editor));
	menu.add(createMenuItem(editor.getToolLabel(TextEditor.A_SAVE_AS),
				TextEditor.A_SAVE_AS, editor));
	menu.addSeparator();
	menu.add(createMenuItem(editor.getToolLabel(TextEditor.A_PRINT),
				TextEditor.A_PRINT, editor));
	menu.addSeparator();
	closeMenuItem = createMenuItem(editor.getToolLabel(TextEditor.L_CLOSE),
				       TextEditor.L_CLOSE, this);
	menu.add(closeMenuItem);
	exitMenuItem = createMenuItem(editor.getToolLabel(TextEditor.L_EXIT),
				      TextEditor.L_EXIT, this);
	menu.add(exitMenuItem);

	return menu;
    }

    protected Menu createHelpMenu() {
	Menu menu = new Menu(editor.getToolLabel(TextEditor.L_HELP));

	menu.add(createMenuItem(editor.getToolLabel(TextEditor.L_VERSION),
				TextEditor.L_VERSION, this));

	return menu;
    }

    protected void setMenuBarEnabled(MenuBar menuBar, boolean enabled) {
	int count = menuBar.getMenuCount();
	for (int i = 0; i < count; i++) {
	    Menu menu = menuBar.getMenu(i);
	    menu.setEnabled(enabled);
	}
	Menu helpMenu = menuBar.getHelpMenu();
	if (helpMenu != null) {
	    helpMenu.setEnabled(enabled);
	}
    }

    protected MenuItem createMenuItem(String label, String action,
				      ActionListener l)
    {
	MenuItem mi = new MenuItem(label);
	mi.setActionCommand(action);
	mi.addActionListener(l);
	return mi;
    }
}


final
class PadManager {
    PropertyChangeSupport change;
    Vector pads;

    static final String PROP_PAD_COUNT = "padCount";


    PadManager() {
	change = new PropertyChangeSupport(this);
	pads = new Vector();
    }


    synchronized int getPadCount() {
	return pads.size();
    }

    synchronized boolean contains(Pad pad) {
	return pads.contains(pad);
    }

    synchronized void addPad(Pad pad) {
	change.addPropertyChangeListener(pad);

	int oldValue = pads.size();
	pads.addElement(pad);
	change.firePropertyChange(PROP_PAD_COUNT,
				  new Integer(oldValue),
				  new Integer(pads.size()));
    }

    synchronized void removePad(Pad pad) {
	change.removePropertyChangeListener(pad);

	int oldValue = pads.size();
	pads.removeElement(pad);
	change.firePropertyChange(PROP_PAD_COUNT,
				  new Integer(oldValue),
				  new Integer(pads.size()));
    }
}
