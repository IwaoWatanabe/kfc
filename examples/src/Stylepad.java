/*
 * Stylepad.java
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

import jp.kyasu.editor.DocumentEditor;
import jp.kyasu.editor.TextEditor;

import java.awt.Menu;
import java.beans.PropertyChangeEvent;
import java.io.File;

/**
 * An style pad class.
 *
 * @version 	21 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class Stylepad extends Pad {

    public Stylepad() {
	super(new DocumentEditor());
    }


    public String getPadName() {
	return "Stylepad";
    }

    public void propertyChange(PropertyChangeEvent e) {
	Object obj = e.getSource();
	String name = e.getPropertyName();
	if (obj == editor) {
	    if (name.equals(DocumentEditor.P_FILE)) {
		java.awt.Frame f = editor.getFrame();
		if (f != null) {
		    f.setTitle(getPadName() + ": " +
					((File)e.getNewValue()).getPath());
		}
		return;
	    }
	}
	super.propertyChange(e);
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
	Menu saveas = new Menu(editor.getToolLabel(TextEditor.A_SAVE_AS));
	saveas.add(createMenuItem(
			editor.getToolLabel(DocumentEditor.A_SAVE_AS_TEXT),
			DocumentEditor.A_SAVE_AS_TEXT, editor));
	saveas.add(createMenuItem(
			editor.getToolLabel(DocumentEditor.A_SAVE_AS_OBJECT),
			DocumentEditor.A_SAVE_AS_OBJECT, editor));
	menu.add(saveas);
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


    /** Executes the examples. */
    public static void main(String args[]) {
	Stylepad pad = new Stylepad();
	pad.open();
	if (args.length > 0) {
	    pad.editor.open_file(new File(args[0]));
	}
    }
}
