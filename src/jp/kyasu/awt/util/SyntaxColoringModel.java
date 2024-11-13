/*
 * SyntaxColoringModel.java
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
import jp.kyasu.awt.event.TextModelEvent;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.TextStyleModifier;
import jp.kyasu.graphics.text.TextChange;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.TextEvent;

/**
 * The <code>SyntaxColoringModel</code> class implements the
 * <code>TextEditModel</code> interface. The <code>SyntaxColoringModel</code>
 * object hilights the syntax of the text representing a program written in
 * a computer language.
 *
 * @see 	jp.kyasu.awt.util.LanguageTokenizer
 * @see 	jp.kyasu.awt.util.LanguageTokenizerFactory
 *
 * @version 	13 Aug 1998
 * @author 	Kazuki YASUMATSU
 */
public class SyntaxColoringModel extends DefaultTextEditModel {
    protected LanguageTokenizerFactory factory;
    protected boolean syntaxColoringEnabled;

    protected Font baseFont;
    protected NamedTextStyle normalStyle;
    protected NamedTextStyle keywordStyle;
    protected NamedTextStyle constantStyle;
    protected NamedTextStyle mlConstantStyle;
    protected NamedTextStyle commentStyle;
    protected NamedTextStyle mlCommentStyle;


    /**
     * The normal style name.
     */
    static protected final String NORMAL_STYLE             = "normal";

    /**
     * The keyword style name.
     */
    static protected final String KEYWORD_STYLE            = "keyword";

    /**
     * The constant style name.
     */
    static protected final String CONSTANT_STYLE           = "constant";

    /**
     * The multiline constant style name.
     */
    static protected final String MULTILINE_CONSTANT_STYLE = "ml constant";

    /**
     * The comment style name.
     */
    static protected final String COMMENT_STYLE            = "comment";

    /**
     * The multiline comment style name.
     */
    static protected final String MULTILINE_COMMENT_STYLE  = "ml comment";


    /**
     * Constructs a model with the specified rich text and factory of the
     * language tokenizer.
     *
     * @param richText the rich text.
     * @param factory  the factory of the language tokenizer.
     */
    public SyntaxColoringModel(RichText richText,
			       LanguageTokenizerFactory factory)
    {
	this(richText, factory, new Font("Monospaced", Font.PLAIN, 12));
    }

    /**
     * Constructs a model with the specified rich text, factory of the
     * language tokenizer, and base font.
     *
     * @param richText the rich text.
     * @param factory  the factory of the language tokenizer.
     * @param baseFont the base font of the model.
     */
    public SyntaxColoringModel(RichText richText,
			       LanguageTokenizerFactory factory,
			       Font baseFont)
    {
	super();
	if (richText == null || factory == null || baseFont == null)
	    throw new NullPointerException();
	this.factory = factory;
	syntaxColoringEnabled = true;
	setBaseFont(baseFont);
	setRichText(richText);
    }

    /**
     * Constructs an empty model.
     */
    protected SyntaxColoringModel() {
	super();
    }


    /**
     * Tests if the syntax coloring is enabled.
     */
    public boolean isSyntaxColoringEnabled() {
	return syntaxColoringEnabled;
    }

    /**
     * Enables or disables the syntax coloring.
     */
    public void setSyntaxColoringEnabled(boolean b) {
	syntaxColoringEnabled = b;
    }

    /**
     * Returns the font style for the normal tokens.
     */
    public int getNormalStyle() {
	return (normalStyle == null ? getDefaultNormalStyle() :
			normalStyle.getExtendedFont().getStyle());
    }

    /**
     * Returns the font color for the normal tokens.
     */
    public Color getNormalColor() {
	return (normalStyle == null ? getDefaultNormalColor() :
			normalStyle.getExtendedFont().getColor());
    }

    /**
     * Returns the font style for the keyword tokens.
     */
    public int getKeywordStyle() {
	return (keywordStyle == null ? getDefaultKeywordStyle() :
			keywordStyle.getExtendedFont().getStyle());
    }

    /**
     * Returns the font color for the keyword tokens.
     */
    public Color getKeywordColor() {
	return (keywordStyle == null ? getDefaultKeywordColor() :
			keywordStyle.getExtendedFont().getColor());
    }

    /**
     * Returns the font style for the constant tokens.
     */
    public int getConstantStyle() {
	return (constantStyle == null ? getDefaultConstantStyle() :
			constantStyle.getExtendedFont().getStyle());
    }

    /**
     * Returns the font color for the constant tokens.
     */
    public Color getConstantColor() {
	return (constantStyle == null ? getDefaultConstantColor() :
			constantStyle.getExtendedFont().getColor());
    }

    /**
     * Returns the font style for the comment tokens.
     */
    public int getCommentStyle() {
	return (commentStyle == null ? getDefaultCommentStyle() :
			commentStyle.getExtendedFont().getStyle());
    }

    /**
     * Returns the font color for the comment tokens.
     */
    public Color getCommentColor() {
	return (commentStyle == null ? getDefaultCommentColor() :
			commentStyle.getExtendedFont().getColor());
    }

    /**
     * Sets the font style for the normal tokens.
     */
    public void setNormalStyle(int fontStyle) {
	setNormalStyleAndColor(fontStyle, getNormalColor());
    }

    /**
     * Sets the font color for the normal tokens.
     */
    public void setNormalColor(Color color) {
	setNormalStyleAndColor(getNormalStyle(), color);
    }

    /**
     * Sets the font style and color for the normal tokens.
     */
    public void setNormalStyleAndColor(int fontStyle, Color color) {
	normalStyle = new NamedTextStyle(NORMAL_STYLE,
					 baseFont.getName(),
					 fontStyle,
					 baseFont.getSize(),
					 color);
    }

    /**
     * Sets the font style for the keyword tokens.
     */
    public void setKeywordStyle(int fontStyle) {
	setKeywordStyleAndColor(fontStyle, getKeywordColor());
    }

    /**
     * Sets the font color for the keyword tokens.
     */
    public void setKeywordColor(Color color) {
	setKeywordStyleAndColor(getKeywordStyle(), color);
    }

    /**
     * Sets the font style and color for the keyword tokens.
     */
    public void setKeywordStyleAndColor(int fontStyle, Color color) {
	keywordStyle = new NamedTextStyle(KEYWORD_STYLE,
					  baseFont.getName(),
					  fontStyle,
					  baseFont.getSize(),
					  color);
    }

    /**
     * Sets the font style for the constant tokens.
     */
    public void setConstantStyle(int fontStyle) {
	setConstantStyleAndColor(fontStyle, getConstantColor());
    }

    /**
     * Sets the font color for the constant tokens.
     */
    public void setConstantColor(Color color) {
	setConstantStyleAndColor(getConstantStyle(), color);
    }

    /**
     * Sets the font style and color for the constant tokens.
     */
    public void setConstantStyleAndColor(int fontStyle, Color color) {
	constantStyle = new NamedTextStyle(CONSTANT_STYLE,
					   baseFont.getName(),
					   fontStyle,
					   baseFont.getSize(),
					   color);
	mlConstantStyle = new NamedTextStyle(MULTILINE_CONSTANT_STYLE,
					     baseFont.getName(),
					     fontStyle,
					     baseFont.getSize(),
					     color);
    }

    /**
     * Sets the font style for the comment tokens.
     */
    public void setCommentStyle(int fontStyle) {
	setCommentStyleAndColor(fontStyle, getCommentColor());
    }

    /**
     * Sets the font color for the comment tokens.
     */
    public void setCommentColor(Color color) {
	setCommentStyleAndColor(getCommentStyle(), color);
    }

    /**
     * Sets the font style and color for the comment tokens.
     */
    public void setCommentStyleAndColor(int fontStyle, Color color) {
	commentStyle = new NamedTextStyle(COMMENT_STYLE,
					  baseFont.getName(),
					  fontStyle,
					  baseFont.getSize(),
					  color);
	mlCommentStyle = new NamedTextStyle(MULTILINE_COMMENT_STYLE,
					    baseFont.getName(),
					    fontStyle,
					    baseFont.getSize(),
					    color);
    }

    /**
     * Returns the base font of this model.
     */
    public Font getBaseFont() {
	return baseFont;
    }

    /**
     * Sets the base font of this model.
     */
    public void setBaseFont(Font baseFont) {
	if (baseFont == null)
	    throw new NullPointerException();
	this.baseFont = baseFont;

	setNormalStyleAndColor(getNormalStyle(), getNormalColor());
	setKeywordStyleAndColor(getKeywordStyle(), getKeywordColor());
	setConstantStyleAndColor(getConstantStyle(), getConstantColor());
	setCommentStyleAndColor(getCommentStyle(), getCommentColor());
    }

    /**
     * Sets the rich text of this model to be the specified rich text.
     *
     * @param richText the rich text.
     */
    public synchronized void setRichText(RichText richText) {
	if (richText == null)
	    throw new NullPointerException();
	Font font = richText.getRichTextStyle().getTextStyle().getFont();
	if (baseFont == null || !baseFont.equals(font)) {
	    setBaseFont(font);
	}

	if (!syntaxColoringEnabled || richText.isEmpty()) {
	    richText.setBaseTextStyle(normalStyle);
	    super.setRichText(richText);
	    return;
	}

	Text text = richText.getText();
	if (text.length() > 0) {
	    LanguageTokenizer tokenizer = getTokenizer(text, 0, text.length());
	    coloringSyntax(tokenizer, text);
	}
	super.setRichText(new RichText(text, richText.getRichTextStyle()));
	return;
    }

    /**
     * Replaces the specified range of the rich text of this model with
     * the specified replacement text object.
     *
     * @param  repBegin the beginning text position to replace, inclusive.
     * @param  repEnd   the ending text position to replace, exclusive.
     * @param  rep      a replacement <code>Text</code> object.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public synchronized Undo replace(int repBegin, int repEnd, Text rep) {
	rep = rep.cloneStyle();
	rep.replaceStyle(0, rep.length(), normalStyle);

	if (!syntaxColoringEnabled) {
	    return super.replace(repBegin, repEnd, rep);
	}

	RichText oldRichText = richText.subtext(repBegin, repEnd);
	TextChange change = richText.replace(repBegin, repEnd, rep);
	repEnd = repBegin + rep.length();

	Text text = richText.getText();

	int begin;
	int tokenBeginType;
	boolean beginIsInMultiLineConstant;
	boolean beginIsInMultiLineComment;
	if (repBegin <= 0) {
	    tokenBeginType = LanguageTokenizer.EOT;
	    begin = 0;
	    beginIsInMultiLineConstant = false;
	    beginIsInMultiLineComment = false;
	}
	else {
	    int tokenBegin = repBegin - 1 - text.getRunOffsetAt(repBegin - 1);
	    tokenBeginType = getTokenType(text, tokenBegin);
	    begin = LanguageTokenizer.getPreferredParseBegin(
			text, tokenBeginType, tokenBegin, repBegin - 1);
	    beginIsInMultiLineConstant =
		(tokenBeginType == LanguageTokenizer.MULTILINE_CONSTANT &&
		 begin > tokenBegin);
	    beginIsInMultiLineComment =
		(tokenBeginType == LanguageTokenizer.MULTILINE_COMMENT &&
		 begin > tokenBegin);
	}
	int end;
	int tokenEnd;
	int tokenEndType;
	boolean endIsInMultiLineConstant;
	boolean endIsIsInMultiLineComment;
	if (repEnd >= text.length()) {
	    tokenEndType = LanguageTokenizer.EOT;
	    tokenEnd = text.length();
	    end = tokenEnd;
	    endIsInMultiLineConstant = false;
	    endIsIsInMultiLineComment = false;
	}
	else {
	    tokenEndType = getTokenType(text, repEnd);
	    tokenEnd = repEnd + text.getRunLengthAt(repEnd);
	    end = LanguageTokenizer.getPreferredParseEnd(
			text, tokenEndType, tokenEnd, repEnd);
	    endIsInMultiLineConstant =
			(tokenEndType == LanguageTokenizer.MULTILINE_CONSTANT);
	    endIsIsInMultiLineComment =
			(tokenEndType == LanguageTokenizer.MULTILINE_COMMENT);
	}

	if (begin < end) {
	    LanguageTokenizer tokenizer = getTokenizer(text, begin, end);
	    tokenizer.inMultiLineConstant = beginIsInMultiLineConstant;
	    tokenizer.inMultiLineComment  = beginIsInMultiLineComment;
	    coloringSyntax(tokenizer, text);
	    if (tokenizer.inMultiLineConstant != endIsInMultiLineConstant) {
		if (tokenizer.inMultiLineConstant) {
		    if (end < text.length()) {
			tokenizer = getTokenizer(text, end, text.length());
			tokenizer.inMultiLineConstant = true;
			end = coloringSyntax(tokenizer, text, true, false);
		    }
		}
		else {
		    if (end < tokenEnd) {
			tokenizer = getTokenizer(text, end, tokenEnd);
			tokenizer.inMultiLineConstant = false;
			end = coloringSyntax(tokenizer, text);
		    }
		}
	    }
	    if (tokenizer.inMultiLineComment != endIsIsInMultiLineComment) {
		if (tokenizer.inMultiLineComment) {
		    if (end < text.length()) {
			tokenizer = getTokenizer(text, end, text.length());
			tokenizer.inMultiLineComment = true;
			end = coloringSyntax(tokenizer, text, false, true);
		    }
		}
		else {
		    if (end < tokenEnd) {
			tokenizer = getTokenizer(text, end, tokenEnd);
			tokenizer.inMultiLineComment = false;
			end = coloringSyntax(tokenizer, text);
		    }
		}
	    }
	}

	if (repBegin > 0 && tokenBeginType == getTokenType(text, repBegin - 1))
	{
	    begin = repBegin;
	}
	notifyTextModelListeners(begin, end, change);
	notifyTextListeners(new TextEvent(this, TextEvent.TEXT_VALUE_CHANGED));
	return new ReplaceUndo(repBegin, repEnd, oldRichText);
    }


    /** Notifies the text model event to the text model listeners. */
    protected void notifyTextModelListeners(int begin, int end,
					    TextChange change)
    {
	if (begin < change.layoutBegin) change.layoutBegin = begin;
	if (end > change.layoutEnd)     change.layoutEnd   = end;
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
    }

    /*
     * Sets the text style in the specified range of the rich text of this
     * text edit model to be the specified text style.
     *
     * @param  begin     the beginning text position to set, inclusive.
     * @param  end       the ending text position to set, exclusive.
     * @param  textStyle the text style.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public Undo setTextStyle(int begin, int end, TextStyle textStyle) {
	return null;
    }

    /*
     * Modifies the text style in the specified range of the rich text of
     * this text edit model by using the specified text style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the text style modifier.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public Undo modifyTextStyle(int begin, int end, TextStyleModifier modifier)
    {
	return null;
    }

    /**
     * Replaces the specified range of the rich text of this model with
     * the specified replacement rich text object.
     */
    protected Undo replace(int repBegin, int repEnd, RichText rep) {
	return replace(repBegin, repEnd, rep.getText());
    }

    /**
     * Hilights the syntax of the specified text with the specified tokenizer.
     */
    protected int coloringSyntax(LanguageTokenizer tokenizer, Text text) {
	return coloringSyntax(tokenizer, text, false, false);
    }

    /**
     * Hilights the syntax of the specified text with the specified tokenizer
     * and flags.
     */
    protected int coloringSyntax(LanguageTokenizer tokenizer, Text text,
				 boolean stopAtNotMultiLineConstant,
				 boolean stopAtNotMultiLineComment)
    {
      outer:
	for (;;) {
	    int token = tokenizer.nextToken();
	    switch (token) {
	    case LanguageTokenizer.EOT:
		break outer;
	    case LanguageTokenizer.KEYWORD:
		text.replaceStyle(tokenizer.tokenBegin, tokenizer.tokenEnd,
				  keywordStyle);
		break;
	    case LanguageTokenizer.CONSTANT:
		text.replaceStyle(tokenizer.tokenBegin, tokenizer.tokenEnd,
				  constantStyle);
		break;
	    case LanguageTokenizer.MULTILINE_CONSTANT:
		text.replaceStyle(tokenizer.tokenBegin, tokenizer.tokenEnd,
				  mlConstantStyle);
		break;
	    case LanguageTokenizer.COMMENT:
		text.replaceStyle(tokenizer.tokenBegin, tokenizer.tokenEnd,
				  commentStyle);
		break;
	    case LanguageTokenizer.MULTILINE_COMMENT:
		text.replaceStyle(tokenizer.tokenBegin, tokenizer.tokenEnd,
				  mlCommentStyle);
		break;
	    case LanguageTokenizer.OTHER:
	    default:
		text.replaceStyle(tokenizer.tokenBegin, tokenizer.tokenEnd,
				  normalStyle);
		break;
	    }
	    if (stopAtNotMultiLineConstant &&
		token != LanguageTokenizer.MULTILINE_CONSTANT)
	    {
		break outer;
	    }
	    if (stopAtNotMultiLineComment &&
		token != LanguageTokenizer.MULTILINE_COMMENT)
	    {
		break outer;
	    }
	}
	return tokenizer.tokenEnd;
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
    protected int getDefaultKeywordStyle()   { return Font.BOLD; }

    /**
     * Returns the default font color for the keyword tokens.
     */
    protected Color getDefaultKeywordColor() { return null; }

    /**
     * Returns the default font style for the constant tokens.
     */
    protected int getDefaultConstantStyle()   { return Font.PLAIN; }

    /**
     * Returns the default font color for the constant tokens.
     */
    protected Color getDefaultConstantColor() { return new Color(128, 0, 0); }

    /**
     * Returns the default font style for the constant tokens.
     */
    protected int getDefaultCommentStyle()   { return Font.ITALIC; }

    /**
     * Returns the default font color for the constant tokens.
     */
    protected Color getDefaultCommentColor() { return new Color(0, 128, 0); }


    /**
     * Creates new tokenizer from the factory.
     */
    protected LanguageTokenizer getTokenizer(Text text, int begin, int end)
    {
	return factory.createLanguageTokenizer(text, begin, end);
    }

    /**
     * Gets the token type at the specified index of the specified text.
     */
    protected int getTokenType(Text text, int index) {
	if (index < 0 || index >= text.length())
	    return LanguageTokenizer.OTHER;
	TextStyle ts = text.getTextStyleAt(index);
	if (!(ts instanceof NamedTextStyle))
	    return LanguageTokenizer.OTHER;
	String styleName = ((NamedTextStyle)ts).getStyleName();
	if (styleName.equals(NORMAL_STYLE))
	    return LanguageTokenizer.OTHER;
	else if (styleName.equals(KEYWORD_STYLE))
	    return LanguageTokenizer.KEYWORD;
	else if (styleName.equals(CONSTANT_STYLE))
	    return LanguageTokenizer.CONSTANT;
	else if (styleName.equals(MULTILINE_CONSTANT_STYLE))
	    return LanguageTokenizer.MULTILINE_CONSTANT;
	else if (styleName.equals(COMMENT_STYLE))
	    return LanguageTokenizer.COMMENT;
	else if (styleName.equals(MULTILINE_COMMENT_STYLE))
	    return LanguageTokenizer.MULTILINE_COMMENT;
	else
	    return LanguageTokenizer.OTHER;
    }
}
