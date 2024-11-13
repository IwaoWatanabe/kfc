/*
 * DocumentEditorApplet.java
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
import jp.kyasu.editor.DocumentEditor;
import jp.kyasu.graphics.*;

import java.awt.Color;
import java.awt.Font;
import java.util.Hashtable;

/**
 *  An example of KFC DocumentEditor.
 *
 * @version 	21 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class DocumentEditorApplet extends Applet {

    public String getAppletInfo() {
	return "An example of KFC Document Editor";
    }

    public void init() {
	add(createDocumentEditor(), java.awt.BorderLayout.CENTER);
    }


    protected DocumentEditor createDocumentEditor() {
	DocumentEditor editor = new DocumentEditor();
	editor.setRichText(createRichText());
	return editor;
    }

    static final Hashtable TextStyles = new Hashtable();
    static final Hashtable ParaStyles = new Hashtable();

    static {
	TextStyles.put("default",
		new TextStyle("SansSerif", Font.PLAIN, 12));
	TextStyles.put("bold",
		new TextStyle("SansSerif", Font.BOLD, 12));
	TextStyles.put("italic",
		new TextStyle("SansSerif", Font.ITALIC, 12));
	TextStyles.put("red",
		new TextStyle("SansSerif", Font.PLAIN, 12, Color.red));
	TextStyles.put("blue",
		new TextStyle("SansSerif", Font.PLAIN, 12, Color.blue));
	TextStyles.put("large-bold",
		new TextStyle("SansSerif", Font.BOLD, 16));
	TextStyles.put("Large-bold",
		new TextStyle("SansSerif", Font.BOLD, 20));

	ParaStyles.put("default",
		new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 0, 0));
	ParaStyles.put("paragraph",
		new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 0, 10));
	ParaStyles.put("center",
		new ParagraphStyle(ParagraphStyle.CENTER, 2, 2, 0, 0));
	ParaStyles.put("right",
		new ParagraphStyle(ParagraphStyle.RIGHT, 2, 2, 0, 0));
	ParaStyles.put("title",
		new ParagraphStyle(ParagraphStyle.CENTER, 2, 2, 0, 10));
	Visualizable v = new VColoredWrapper(
		new VRectangle(4, 4, VRectangle.PLAIN), Color.black);
	ParaStyles.put("itemize",
		new ParagraphStyle(ParagraphStyle.LEFT, 50, 2, 0, 0, 0, v, 8));
    }

    protected RichText createRichText() {
	TextBuffer buffer = new TextBuffer();

	setParagraphStyle(buffer, "title");
	setTextStyle(buffer, "Large-bold");
	buffer.append("Linux version 2.0\n");
	buffer.append(createVImage("linux_logo.gif"));
	buffer.append("\n");

	setParagraphStyle(buffer, "right");
	setTextStyle(buffer, "blue");
	buffer.append("From Linux README\n\n");

	setParagraphStyle(buffer, "paragraph");
	setTextStyle(buffer, "italic");
	buffer.append("These are the release notes for linux version 2.0. Read them carefully, as they tell you what this is all about, explain how to install the kernel, and what to do if something goes wrong.\n");

	setParagraphStyle(buffer, "title");
	setTextStyle(buffer, "large-bold");
	buffer.append("What is Linux?\n");

	setParagraphStyle(buffer, "paragraph");
	setTextStyle(buffer, "default");
	buffer.append("Linux is a Unix clone written from scratch by Linus Torvalds with assistance from a loosely-knit team of hackers across the Net. It aims towards POSIX compliance.\n");
	buffer.append("It has all the features you would expect in a modern fully-fledged Unix, including true multitasking, virtual memory, shared libraries, demand loading, shared copy-on-write executables, proper memory management and TCP/IP networking.\n");
	buffer.append("It is distributed under ");
	setTextStyle(buffer, "red");
	buffer.append("the GNU General Public License.\n");

	setParagraphStyle(buffer, "title");
	setTextStyle(buffer, "large-bold");
	buffer.append("On What Hardware Does It Run?\n");

	setParagraphStyle(buffer, "paragraph");
	setTextStyle(buffer, "default");
	buffer.append("Linux was first developed for 386/486-based PCs. These days it also runs on DEC Alphas, SUN Sparcs, M68000 machines (like Atari and Amiga), MIPS and PowerPC.\n");

	setParagraphStyle(buffer, "title");
	setTextStyle(buffer, "large-bold");
	buffer.append("Documentation\n");

	setParagraphStyle(buffer, "itemize");
	setTextStyle(buffer, "default");
	buffer.append("there is a lot of documentation available both in electronic form on the internet and in books, both Linux-specific and pertaining to general UNIX questions. I'd recommend looking into the documentation subdirectories on any Linux ftp site for the LDP (Linux Documentation Project) books. This README is not meant to be documentation on the system: there are much better sources available.\n");
	buffer.append("There are various readme's in the kernel Documentation/ subdirectory: these typically contain kernel-specific installation notes for some drivers for example.\n");

	//setParagraphStyle(buffer, "default");
	setParagraphStyle(buffer, "paragraph");
	setTextStyle(buffer, "default");
	buffer.append("...");

	return buffer.toRichText(RichTextStyle.DEFAULT_DOCUMENT_STYLE);
    }

    protected void setTextStyle(TextBuffer buffer, String name) {
	buffer.setTextStyle((TextStyle)TextStyles.get(name));
    }

    protected void setParagraphStyle(TextBuffer buffer, String name) {
	buffer.setParagraphStyle((ParagraphStyle)ParaStyles.get(name));
    }

    protected VImage createVImage(String filename) {
	return new VImage(getImage(getCodeBase(), "applets/images/" + filename));
    }
}
