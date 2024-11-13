/*
 * FastSyntaxColoringModel.java
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

import jp.kyasu.awt.event.TextModelEvent;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.text.TextChange;

import java.awt.Color;
import java.awt.Font;

/**
 * The <code>FastSyntaxColoringModel</code> class implements the
 * <code>TextEditModel</code> interface.
 * The <code>FastSyntaxColoringModel</code> object hilights the syntax of
 * the text representing a program written in a computer language.
 * <p>
 * The <code>FastSyntaxColoringModel</code> hilights the syntax faster than
 * the <code>SyntaxColoringModel</code> but it cannot change the font styles
 * for the tokens.
 *
 * @see		jp.kyasu.awt.util.LanguageTokenizer
 * @see		jp.kyasu.awt.util.LanguageTokenizerFactory
 *
 * @version 	09 May 1998
 * @author 	Kazuki YASUMATSU
 */
public class FastSyntaxColoringModel extends SyntaxColoringModel {

    /**
     * Constructs a model with the specified rich text and factory of the
     * language tokenizer.
     *
     * @param richText the rich text.
     * @param factory  the factory of the language tokenizer.
     */
    public FastSyntaxColoringModel(RichText richText,
				   LanguageTokenizerFactory factory)
    {
	super(richText, factory);
    }

    /**
     * Constructs a model with the specified rich text, factory of the
     * language tokenizer, and base font.
     *
     * @param richText the rich text.
     * @param factory  the factory of the language tokenizer.
     * @param baseFont the base font of the model.
     */
    public FastSyntaxColoringModel(RichText richText,
				   LanguageTokenizerFactory factory,
				   Font baseFont)
    {
	super(richText, factory, baseFont);
    }

    /**
     * Constructs an empty model.
     */
    protected FastSyntaxColoringModel() {
	super();
    }


    /**
     * Sets the font style for the normal tokens.
     */
    public void setNormalStyleAndColor(int fontStyle, Color color) {
	normalStyle = new NamedTextStyle(NORMAL_STYLE, baseFont, color);
    }

    /**
     * Sets the font style for the keyword tokens.
     */
    public void setKeywordStyleAndColor(int fontStyle, Color color) {
	keywordStyle = new NamedTextStyle(KEYWORD_STYLE, baseFont, color);
    }

    /**
     * Sets the font style for the constant tokens.
     */
    public void setConstantStyleAndColor(int fontStyle, Color color) {
	constantStyle = new NamedTextStyle(CONSTANT_STYLE, baseFont, color);
	mlConstantStyle =
		new NamedTextStyle(MULTILINE_CONSTANT_STYLE, baseFont, color);
    }

    /**
     * Sets the font style for the comment tokens.
     */
    public void setCommentStyleAndColor(int fontStyle, Color color) {
	commentStyle = new NamedTextStyle(COMMENT_STYLE, baseFont, color);
	mlCommentStyle =
		new NamedTextStyle(MULTILINE_COMMENT_STYLE, baseFont, color);
    }


    /** Notifies the text model event to the text model listeners. */
    protected void notifyTextModelListeners(int begin, int end,
					    TextChange change)
    {
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					begin, end, change));
    }

    /**
     * Returns the default font style for the normal tokens.
     */
    protected int getDefaultNormalStyle()   { return Font.PLAIN; }

    /**
     * Returns the default font color for the normal tokens.
     */
    protected Color getDefaultNormalColor() { return null; }

    /**
     * Returns the default font style for the keyword tokens.
     */
    protected int getDefaultKeywordStyle()   { return Font.PLAIN; }

    /**
     * Returns the default font color for the keyword tokens.
     */
    protected Color getDefaultKeywordColor() { return Color.blue; }

    /**
     * Returns the default font style for the constant tokens.
     */
    protected int getDefaultConstantStyle()   { return Font.PLAIN; }

    /**
     * Returns the default font color for the constant tokens.
     */
    protected Color getDefaultConstantColor() { return new Color(128, 0, 0); }

    /**
     * Returns the default font style for the comment tokens.
     */
    protected int getDefaultCommentStyle()   { return Font.PLAIN; }

    /**
     * Returns the default font color for the comment tokens.
     */
    protected Color getDefaultCommentColor() { return new Color(0, 128, 0); }
}
