/*
 * CodeEditorApplet.java
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
import jp.kyasu.editor.CodeEditor;

import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 *  An example of KFC CodeEditor.
 *
 * @version 	21 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class CodeEditorApplet extends Applet {

    public String getAppletInfo() {
	return "An example of KFC Code Editor";
    }

    public void init() {
	add(createCodeEditor(), java.awt.BorderLayout.CENTER);
    }

    protected CodeEditor createCodeEditor() {
	CodeEditor editor = new CodeEditor();
	editor.setText(getSourceCode());
	return editor;
    }

    protected String getSourceCode() {
	StringBuffer buffer = new StringBuffer();
	try {
	    URL url = new URL(getCodeBase(), "applets/CodeEditorApplet.java");
	    InputStream stream = url.openStream();
	    Reader in = new BufferedReader(new InputStreamReader(stream));
	    int c;
	    while ((c = in.read()) != -1) {
		buffer.append((char)c);
	    }
	    stream.close();
	}
	//catch (MalformedURLException e) {}
	catch (Exception e) {}

	return buffer.toString();
    }
}
