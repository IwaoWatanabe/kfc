/*
 * HTMLEditorApplet.java
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

package applets; // Make security happy

import jp.kyasu.awt.Applet;
import jp.kyasu.editor.HTMLEditor;

import java.net.URL;
import java.net.MalformedURLException;

/**
 *  An example of KFC HTMLEditor.
 *
 * @version 	22 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class HTMLEditorApplet extends Applet {
    HTMLEditor htmlEditor;

    public String getAppletInfo() {
	return "An example of KFC HTML Editor";
    }

    public void init() {
	htmlEditor = createHTMLEditor();
	add(htmlEditor, java.awt.BorderLayout.CENTER);
    }

    public void stop() {
	htmlEditor.stop_loading();

	super.stop(); // call jp.kyasu.awt.Applet.stop() method
    }

    protected HTMLEditor createHTMLEditor() {
	HTMLEditor editor = new HTMLEditor();
	String url = getParameter("URL");
	if (url != null) {
	    try {
		editor.goto_page(new URL(getCodeBase(), url));
	    }
	    //catch (MalformedURLException e) {}
	    catch (Exception e) {}
	}
	return editor;
    }
}
