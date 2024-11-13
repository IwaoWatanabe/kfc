/*
 * Notepad.java
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

import jp.kyasu.editor.TextEditor;

import java.beans.PropertyChangeEvent;
import java.io.File;

/**
 * An note pad class.
 *
 * @version 	21 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class Notepad extends Pad {

    public Notepad() {
	super(new TextEditor());
    }


    public String getPadName() {
	return "Notepad";
    }

    public void propertyChange(PropertyChangeEvent e) {
	Object obj = e.getSource();
	String name = e.getPropertyName();
	if (obj == editor) {
	    if (name.equals(TextEditor.P_FILE)) {
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


    /** Executes the examples. */
    public static void main(String args[]) {
	Notepad pad = new Notepad();
	pad.open();
	if (args.length > 0) {
	    pad.editor.open_file(new File(args[0]));
	}
    }
}
