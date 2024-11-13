/*
 * HTMLEditorTarget.java
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

import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextAttachment;
import jp.kyasu.graphics.TextBuffer;
import jp.kyasu.graphics.html.HTMLStyle;
import jp.kyasu.graphics.html.HTMLText;
import jp.kyasu.graphics.html.HTMLReaderTarget;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

/**
 * A HTMLEditorTarget object is a target into which the HTMLReader
 * renders the HTML document. It retargets the rendering result to
 * the HTMLEditor.
 *
 * @version 	20 Sep 1998
 * @author 	Kazuki YASUMATSU
 */
public class HTMLEditorTarget implements HTMLReaderTarget {
    protected HTMLEditor htmlEditor;
    protected TextBuffer buffer;
    protected ParagraphStyle lastParagraphStyle;


    /**
     * Constructs a new HTMLEditorTarget with the specified HTMLEditor.
     *
     * @param htmlEditor the HTMLEditor.
     */
    public HTMLEditorTarget(HTMLEditor htmlEditor) {
	if (htmlEditor == null)
	    throw new NullPointerException();
	this.htmlEditor = htmlEditor;
    }


    /**
     * Opens (Initializes) this target with the specified url and html style.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void open(URL url, HTMLStyle htmlStyle) throws IOException {
	buffer = new TextBuffer();
	lastParagraphStyle = null;

	htmlEditor.setHTMLText(new HTMLText(htmlStyle));
	htmlEditor.setURL(url);
	htmlEditor.setCaretPosition(0);
	htmlEditor.setSelectionParagraphStyle(
					htmlStyle.getDefaultParagraphStyle());
    }

    /**
     * Closes (Finalizes) this target.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void close() throws IOException {
	flush(null);
    }

    /**
     * Returns the length of the data written.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     */
    public int getLength() {
	HTMLText htmlText = htmlEditor.getHTMLText();
	return htmlText.length() + buffer.length();
    }

    /**
     * Returns the character at the specified index in the data written.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     */
    public char getChar(int index) {
	HTMLText htmlText = htmlEditor.getHTMLText();
	if (index < htmlText.length())
	    return htmlText.getChar(index);
	else
	    return buffer.getChar(index - htmlText.length());
    }

    /**
     * Returns the text attachment at the specified index in the data written.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     */
    public TextAttachment getAttachmentAt(int index) {
	HTMLText htmlText = htmlEditor.getHTMLText();
	if (index < htmlText.length())
	    return htmlText.getAttachmentAt(index);
	else
	    return buffer.getAttachmentAt(index - htmlText.length());
    }

    /**
     * Appends the specified text into the target.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void append(Text text) throws IOException {
	if (htmlEditor.loadInputStream == null)
	    throw new IOException();

	buffer.append(text);
    }

    /**
     * Sets the current paragraph style of the target to be the specified
     * paragraph style.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setParagraphStyle(ParagraphStyle paragraphStyle)
	throws IOException
    {
	if (htmlEditor.loadInputStream == null)
	    throw new IOException();

	if (buffer.isEmpty()) {
	    lastParagraphStyle = paragraphStyle;
	}
	else {
	    flush(paragraphStyle);
	}
    }

    /**
     * Sets the document title (the <code>TITLE</code> tag) to be the specified
     * string.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setTitle(String string) throws IOException {
	htmlEditor.setTitle(string);
    }

    /**
     * Sets the background color (the <code>BGCOLOR</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setBackgroundColor(Color color) throws IOException {
	htmlEditor.setBackground(color);
    }

    /**
     * Sets the text color (the <code>TEXT</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setTextColor(Color color) throws IOException {
	htmlEditor.setForeground(color);
    }

    /**
     * Sets the text color (the <code>LINK</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setLinkColor(Color color) throws IOException {
	htmlEditor.setLinkColor(color);
    }

    /**
     * Returns the string representation of the data written.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     */
    public String getString() {
	return "";
    }

    /**
     * Returns the rich text representation of the data written.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     */
    public RichText getRichText() {
	return htmlEditor.getHTMLText();
    }

    /**
     * Returns the html text representation of the data written.
     * @see jp.kyasu.graphics.html.HTMLReaderTarget
     */
    public HTMLText getHTMLText() {
	return htmlEditor.getHTMLText();
    }


    protected void flush(ParagraphStyle newStyle) {
	boolean editable = htmlEditor.isEditable();
	try {
	    htmlEditor.setEditable(true);

	    Text text = buffer.toText();
	    int len = htmlEditor.getHTMLText().length();

	    /*
	    if (len == 0) {
		if (!text.isEmpty()) {
		    htmlEditor.append(text, false);
		}
		if (lastParagraphStyle != null) {
		    htmlEditor.setRangeParagraphStyle(lastParagraphStyle,
						      len, len, false);
		}
	    }
	    else {
	    */
		if (lastParagraphStyle != null) {
		    htmlEditor.setRangeParagraphStyle(lastParagraphStyle,
						      len, len, false);
		}
		if (!text.isEmpty()) {
		    htmlEditor.append(text, false);
		}
	    /*
	    }
	    */

	    buffer = new TextBuffer();
	    lastParagraphStyle = newStyle;
	}
	finally {
	    htmlEditor.setEditable(editable);
	}
    }
}
