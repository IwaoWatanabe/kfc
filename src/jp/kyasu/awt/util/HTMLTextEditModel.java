/*
 * HTMLTextEditModel.java
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

package jp.kyasu.awt.util;

import jp.kyasu.awt.DefaultTextEditModel;
import jp.kyasu.awt.Undo;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.html.HTMLText;
import jp.kyasu.graphics.html.HTMLStyle;

/**
 * The <code>HTMLTextEditModel</code> class is an implementation of the
 * <code>TextEditModel</code> interface for the <code>HTMLText</code>.
 *
 * @version 	09 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class HTMLTextEditModel extends DefaultTextEditModel {

    /**
     * Constructs an empty html text edit model with the specified html style.
     *
     * @param htmlStyle the html style.
     */
    public HTMLTextEditModel(HTMLStyle htmlStyle) {
	this(new HTMLText(htmlStyle));
    }

    /**
     * Constructs a html text edit model with the specified html text.
     *
     * @param htmlText the html text.
     */
    public HTMLTextEditModel(HTMLText htmlText) {
	super(htmlText);
    }


    /**
     * Sets the rich text of this text model to be the specified rich text.
     *
     * @param richText the rich text.
     */
    public void setRichText(RichText richText) {
	if (!(richText instanceof HTMLText))
	    throw new IllegalArgumentException("HTMLText expected");
	super.setRichText(richText);
    }

    /*
     * Sets the paragraph style in the specified range of the rich text
     * of this text edit model to be the specified paragraph style.
     *
     * @param  begin  the beginning text position to set, inclusive.
     * @param  end    the ending text position to set, exclusive.
     * @param  pStyle the paragraph style.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public Undo setParagraphStyle(int begin, int end, ParagraphStyle pStyle) {
	if (!pStyle.hasBaseStyle())
	    throw new IllegalArgumentException(
					"ParagraphStyle must have a baseStyle");
	return super.setParagraphStyle(begin, end, pStyle);
    }
}
