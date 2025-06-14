/*
 * DefaultHTMLReaderTarget.java
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

package jp.kyasu.graphics.html;

import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextAttachment;
import jp.kyasu.graphics.TextBuffer;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

/**
 * The <code>DefaultHTMLReaderTarget</code> class is a default implementation
 * of the interface for the target into which the html reader renders the
 * HTML document.
 *
 * @see 	jp.kyasu.graphics.html.HTMLReader
 * @see 	jp.kyasu.graphics.html.HTMLReaderTarget
 *
 * @version 	09 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class DefaultHTMLReaderTarget implements HTMLReaderTarget {
    /** The text buffer. */
    protected TextBuffer buffer;

    /** The html style. */
    protected HTMLStyle htmlStyle;

    /** The document url. */
    protected URL url;

    /** The document title. */
    protected String title;

    /** The background color. */
    protected Color backgroundColor;

    /** The text color. */
    protected Color textColor;

    /** The link color. */
    protected Color linkColor;


    /**
     * Constructs a default html reader target.
     */
    public DefaultHTMLReaderTarget() {
	title           = "";
	backgroundColor = Color.white;
	textColor       = Color.black;
	linkColor       = Color.blue;
    }

    /**
     * Opens (Initializes) this target with the specified url and html style.
     */
    public void open(URL url, HTMLStyle htmlStyle) throws IOException {
	this.url = url;
	this.htmlStyle = htmlStyle;
	buffer = new TextBuffer();
	buffer.setTextStyle(htmlStyle.getDefaultTextStyle());
	buffer.setParagraphStyle(htmlStyle.getDefaultParagraphStyle());
    }

    /**
     * Closes (Finalizes) this target.
     */
    public void close() throws IOException {
    }

    /**
     * Returns the length of the data written.
     */
    public int getLength() {
	return buffer.length();
    }

    /**
     * Returns the character at the specified index in the data written.
     */
    public char getChar(int index) {
	return buffer.getChar(index);
    }

    /**
     * Returns the text attachment at the specified index in the data written.
     */
    public TextAttachment getAttachmentAt(int index) {
	return buffer.getAttachmentAt(index);
    }

    /**
     * Appends the specified text into the target.
     */
    public void append(Text text) throws IOException {
	buffer.append(text);
    }

    /**
     * Sets the current paragraph style of the target to be the specified
     * paragraph style.
     */
    public void setParagraphStyle(ParagraphStyle paragraphStyle)
	throws IOException
    {
	buffer.setParagraphStyle(paragraphStyle);
    }

    /**
     * Sets the document title (the <code>TITLE</code> tag) to be the specified
     * string.
     */
    public void setTitle(String string) throws IOException {
	title = string;
    }

    /**
     * Sets the background color (the <code>BGCOLOR</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     */
    public void setBackgroundColor(Color color) throws IOException {
	backgroundColor = color;
    }

    /**
     * Sets the text color (the <code>TEXT</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     */
    public void setTextColor(Color color) throws IOException {
	textColor = color;
    }

    /**
     * Sets the text color (the <code>LINK</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     */
    public void setLinkColor(Color color) throws IOException {
	linkColor = color;
    }

    /**
     * Returns the string representation of the data written.
     */
    public String getString() {
	return buffer.toString();
    }

    /**
     * Returns the rich text representation of the data written.
     */
    public RichText getRichText() {
	return buffer.toRichText(htmlStyle.getDefaultRichTextStyle());
    }

    /**
     * Returns the html text representation of the data written.
     */
    public HTMLText getHTMLText() {
	HTMLText htmlText = new HTMLText(getRichText(), htmlStyle);
	htmlText.setURL(url);
	htmlText.setTitle(title);
	htmlText.setBackgroundColor(backgroundColor);
	htmlText.setTextColor(textColor);
	htmlText.setLinkColor(linkColor);
	return htmlText;
    }
}
