/*
 * HTMLReaderTarget.java
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

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

/**
 * An interface for the target into which the html reader renders the HTML
 * document.
 *
 * @see 	jp.kyasu.graphics.html.HTMLReader
 *
 * @version 	13 May 1998
 * @author 	Kazuki YASUMATSU
 */
public interface HTMLReaderTarget {
    /**
     * Opens (Initializes) this target with the specified url and html style.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void open(URL url, HTMLStyle htmlStyle) throws IOException;

    /**
     * Closes (Finalizes) this target.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void close() throws IOException;

    /**
     * Returns the length of the data written.
     */
    public int getLength();

    /**
     * Returns the character at the specified index in the data written.
     */
    public char getChar(int index);

    /**
     * Returns the text attachment at the specified index in the data written.
     */
    public TextAttachment getAttachmentAt(int index);

    /**
     * Appends the specified text into the target.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void append(Text text) throws IOException;

    /**
     * Sets the current paragraph style of the target to be the specified
     * paragraph style.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setParagraphStyle(ParagraphStyle paragraphStyle)
	throws IOException;

    /**
     * Sets the document title (the <code>TITLE</code> tag) to be the specified
     * string.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setTitle(String string) throws IOException;

    /**
     * Sets the background color (the <code>BGCOLOR</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setBackgroundColor(Color color) throws IOException;

    /**
     * Sets the text color (the <code>TEXT</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setTextColor(Color color) throws IOException;

    /**
     * Sets the text color (the <code>LINK</code> attribute in the
     * <code>BODY</code> tag) to be the specified color.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void setLinkColor(Color color) throws IOException;

    /**
     * Returns the string representation of the data written.
     */
    public String getString();

    /**
     * Returns the rich text representation of the data written.
     */
    public RichText getRichText();

    /**
     * Returns the html text representation of the data written.
     */
    public HTMLText getHTMLText();
}
