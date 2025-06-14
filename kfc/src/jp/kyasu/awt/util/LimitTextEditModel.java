/*
 * LimitTextEditModel.java
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
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.Text;

/**
 * The <code>LimitTextEditModel</code> class implements the
 * <code>TextEditModel</code> interface. The <code>LimitTextEditModel</code>
 * object limits the length of the text.
 * <p>
 * An example of using the <code>LimitTextEditModel</code> is:
 * <pre>
 *     final Dialog dialog = new Dialog(new Frame(), "LimitTextEditModel", true);
 *     dialog.setLayout(new FlowLayout());
 *     dialog.addWindowListener(new WindowAdapter() {
 *         public void windowClosing(WindowEvent e) {
 *             dialog.setVisible(false);
 *             System.exit(0);
 *         }
 *     });
 *     Label label = new Label("Input message (<= 10):");
 *     dialog.add(label);
 *     RichText rtext = new RichText(TextField.DEFAULT_FIELD_STYLE);
 *     TextField field = new TextField(new LimitTextEditModel(rtext, 10), 10);
 *     dialog.add(field);
 *     dialog.pack();
 *     dialog.setVisible(true);
 * </pre>
 *
 * @see 	jp.kyasu.awt.TextEditModel
 *
 * @version 	15 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class LimitTextEditModel extends DefaultTextEditModel {
    protected int limitSize;


    /**
     * Constructs a model with the specified rich text and limit length of the
     * text.
     *
     * @param richText  the rich text.
     * @param limitSize the limit length of the text.
     */
    public LimitTextEditModel(RichText richText, int limitSize) {
	super();
	if (richText == null)
	    throw new NullPointerException();
	if (limitSize <= 0) {
	    throw new IllegalArgumentException("improper limitSize " +
								limitSize);
	}
	this.limitSize = limitSize;
	setRichText(richText);
    }


    /**
     * Sets the rich text of this model to be the specified rich text.
     *
     * @param richText the rich text.
     */
    public synchronized void setRichText(RichText richText) {
	if (richText == null)
	    throw new NullPointerException();
	if (richText.length() > limitSize) {
	    super.setRichText(richText.subtext(0, limitSize));
	}
	else {
	    super.setRichText(richText);
	}
    }

    /**
     * Replaces the specified range of the rich text of this model with
     * the specified replacement text object.
     *
     * @param  begin the beginning text position to replace, inclusive.
     * @param  end   the ending text position to replace, exclusive.
     * @param  rep   a replacement <code>Text</code> object.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public synchronized Undo replace(int begin, int end, Text rep) {
	int newSize = richText.length() + rep.length() - (end - begin);

	if (newSize > limitSize) {
	    java.awt.Toolkit.getDefaultToolkit().beep();
	    return null;
	}
	else {
	    return super.replace(begin, end, rep);
	}
    }


    /*
    public static void main(String arg[]) {
	final jp.kyasu.awt.Dialog dialog = new jp.kyasu.awt.Dialog(
						new jp.kyasu.awt.Frame(),
						"LimitTextEditModel",
						true);
	dialog.setLayout(new java.awt.FlowLayout());
	dialog.addWindowListener(new java.awt.event.WindowAdapter() {
	    public void windowClosing(java.awt.event.WindowEvent e) {
		dialog.setVisible(false);
		System.exit(0);
	    }
	});
	jp.kyasu.awt.Label label =
		new jp.kyasu.awt.Label("Input message (<= 10):");
	dialog.add(label);
	jp.kyasu.graphics.RichText rtext =
		new jp.kyasu.graphics.RichText(
			jp.kyasu.awt.TextField.DEFAULT_FIELD_STYLE);
	jp.kyasu.awt.TextField field =
		new jp.kyasu.awt.TextField(new LimitTextEditModel(rtext, 10),
					   10);
	dialog.add(field);
	dialog.pack();
	dialog.setVisible(true);
    }
    */
}
