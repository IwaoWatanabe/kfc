/*
 * RichTextStyle.java
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

package jp.kyasu.graphics;

import java.awt.Font;

/**
 * The <code>RichTextStyle</code> implements the style for the rich text
 * object. The rich text style has following attributes:
 * <dl>
 * <dt><b>Line wrapping style</b><dd>The line wrapping style; one of
 *     <code>CHAR_WRAP</code>, <code>WORD_WRAP</code> and <code>NO_WRAP</code>.
 * <dt><b>Line end style</b><dd>The line end style; one of
 *     <code>JAVA_LINE_SEPARATOR</code>,
 *     <code>JAVA_LINE_SEPARATOR_WITH_BREAK</code> and
 *     <code>LIST_SEPARATOR</code>.
 * <dt><b>Variable line height</b><dd>The flag determining the line height
 *     of the rich text is variable.
 * <dt><b>Text style</b><dd>The default text style for the rich text.
 * <dt><b>Paragraph style</b><dd>The default paragraph style for the rich
 *     text.
 * <dt><b>Tab width</b><dd>The default tab width for the rich text. The
 *     tab width is automatically computed by multiplying a width of space
 *     character with the default text style by
 *     <code>ParagraphStyle.HARD_TAB_LENGTH</code>.
 * </dl>
 * <p>
 * The rich text style is immutable.
 *
 * @see 	jp.kyasu.graphics.RichText
 * @see 	jp.kyasu.graphics.TextLayout
 * @see 	jp.kyasu.graphics.TextScanner
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class RichTextStyle implements Cloneable, java.io.Serializable {
    /** The line wrapping style. */
    protected int lineWrap;

    /** The line end style. */
    protected int lineEnd;

    /**
     * True if the line height of the rich text is variable (True if
     * multiple paragraph styles are allowed).
     */
    protected boolean variableLineHeight;

    /** The default text style for the rich text. */
    protected TextStyle textStyle;

    /** The default paragraph style for the rich text. */
    protected ParagraphStyle paragraphStyle;

    /** The default tab width. */
    protected int tabWidth;


    /**
     * The constant for the character line wrapping style.
     * <p>
     * The line is wrapped at the character boundary.
     * This style is suited for coding.
     */
    static public final int CHAR_WRAP = 0;

    /**
     * The constant for the word line wrapping style.
     * <p>
     * The line is wrapped at the word boundary.
     * This style is suited for documenting.
     */
    static public final int WORD_WRAP = 1;

    /**
     * The constant for the no line wrapping style.
     * <p>
     * The line is wrapped only at the line separator.
     *
     * @see jp.kyasu.graphics.Text#LINE_SEPARATOR_CHAR
     */
    static public final int NO_WRAP   = 2;

    /**
     * The constant for the java line end style.
     * <p>
     * That is the paragraph is separated by the
     * <code>Text.LINE_SEPARATOR_CHAR</code>.
     *
     * @see jp.kyasu.graphics.Text#LINE_SEPARATOR_CHAR
     * @see jp.kyasu.graphics.TextScanner
     * @see jp.kyasu.graphics.TextScanner#JAVA_STOPS
     */
    static public final int JAVA_LINE_SEPARATOR            = 0;

    /**
     * The constant for the java line end style with line break.
     * <p>
     * That is the paragraph is separated by the
     * <code>Text.LINE_SEPARATOR_CHAR</code> and the line is
     * broken by the <code>Text.LINE_BREAK_CHAR</code>.
     *
     * @see jp.kyasu.graphics.Text#LINE_SEPARATOR_CHAR
     * @see jp.kyasu.graphics.Text#LINE_BREAK_CHAR
     * @see jp.kyasu.graphics.TextScanner
     * @see jp.kyasu.graphics.TextScanner#JAVA_BREAK_STOPS
     */
    static public final int JAVA_LINE_SEPARATOR_WITH_BREAK = 1;

    /**
     * The constant for the list line end style.
     * <p>
     * That is the paragraph (list item) is separated by the
     * <code>Text.LIST_SEPARATOR_CHAR</code>.
     *
     * @see jp.kyasu.graphics.Text#LIST_SEPARATOR_CHAR
     * @see jp.kyasu.graphics.TextScanner#LIST_STOPS
     * @see jp.kyasu.awt.text.TextListView
     */
    static public final int LIST_SEPARATOR                 = 2;

    /*
     * The constant for the system line end style.
     * <p>
     * That is the paragraph is separated by the
     * <code>System.getProperty("line.separator")</code>.
     * <p>
     * This constant is not used.
     */
    static private final int SYSTEM_LINE_SEPARATOR         = 3;

    /**
     * The default rich text style constant for coding.
     */
    static public final RichTextStyle DEFAULT_CODE_STYLE =
	new RichTextStyle(
		CHAR_WRAP,
		JAVA_LINE_SEPARATOR,
		false,
		new TextStyle("Monospaced", Font.PLAIN, 12),
		new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 0, 0, 0));

    /**
     * The default rich text style constant for documenting.
     */
    static public final RichTextStyle DEFAULT_DOCUMENT_STYLE =
	new RichTextStyle(
		WORD_WRAP,
		JAVA_LINE_SEPARATOR_WITH_BREAK,
		true,
		new TextStyle("SansSerif", Font.PLAIN, 12),
		new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 0, 0, 0));

    /**
     * The default rich text style constant for documenting.
     */
    static public final RichTextStyle DEFAULT_LIST_STYLE =
	new RichTextStyle(
		NO_WRAP,
		LIST_SEPARATOR,
		false,
		TextStyle.DEFAULT_STYLE,
		new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 2, 0, 24));


    /**
     * Constructs a rich text style with the specified line wrapping style.
     *
     * @param lineWrap the line wrapping style.
     */
    public RichTextStyle(int lineWrap) {
	this(lineWrap, false);
    }

    /**
     * Constructs a rich text style with the specified line wrapping style
     * and flag determining the line height is variable.
     *
     * @param lineWrap           the line wrapping style.
     * @param variableLineHeight the flag determining the line height is
     *                           variable.
     */
    public RichTextStyle(int lineWrap, boolean variableLineHeight) {
	this(lineWrap, JAVA_LINE_SEPARATOR, variableLineHeight);
    }

    /**
     * Constructs a rich text style with the specified line wrapping style,
     * line end style and flag determining the line height is variable.
     *
     * @param lineWrap           the line wrapping style.
     * @param lineEnd            the line end style.
     * @param variableLineHeight the flag determining the line height is
     *                           variable.
     */
    public RichTextStyle(int lineWrap, int lineEnd, boolean variableLineHeight)
    {
	this(lineWrap, lineEnd, variableLineHeight,
	     TextStyle.DEFAULT_STYLE, ParagraphStyle.DEFAULT_STYLE);
    }

    /**
     * Constructs a rich text style with the specified line wrapping style,
     * line end style, flag determining the line height is variable,
     * default text style and default paragraph style.
     *
     * @param lineWrap           the line wrapping style.
     * @param lineEnd            the line end style.
     * @param variableLineHeight the flag determining the line height is
     *                           variable.
     * @param textStyle          the default text style.
     * @param paragraphStyle     the default paragraph style.
     */
    public RichTextStyle(int lineWrap, int lineEnd, boolean variableLineHeight,
			 TextStyle textStyle, ParagraphStyle paragraphStyle)
    {
	if (textStyle == null || paragraphStyle == null)
	    throw new NullPointerException();
	if (!variableLineHeight && paragraphStyle.paragraphSpace != 0) {
	    throw new IllegalArgumentException("A paragraph space must be 0 when variableLineHeight is false.");
	}
	setLineWrap(lineWrap);
	setLineEnd(lineEnd);
	this.variableLineHeight = variableLineHeight;
	this.textStyle          = textStyle;
	this.paragraphStyle     = paragraphStyle;
	tabWidth = textStyle.getFontMetrics().charWidth(' ') *
						ParagraphStyle.HARD_TAB_LENGTH;
    }


    /**
     * Returns the line wrapping style.
     *
     * @see #CHAR_WRAP
     * @see #WORD_WRAP
     * @see #NO_WRAP
     */
    public final int getLineWrap() {
	return lineWrap;
    }

    /**
     * Returns the line end style.
     *
     * @see #JAVA_LINE_SEPARATOR
     * @see #JAVA_LINE_SEPARATOR_WITH_BREAK
     * @see #LIST_SEPARATOR
     */
    public final int getLineEnd() {
	return lineEnd;
    }

    /**
     * Returns the line end character.
     *
     * @see Text#LINE_SEPARATOR_CHAR
     * @see Text#LIST_SEPARATOR_CHAR
     */
    public final char getLineEndChar() {
	return (lineEnd == LIST_SEPARATOR ?
			Text.LIST_SEPARATOR_CHAR :
			Text.LINE_SEPARATOR_CHAR);
    }

    /**
     * Checks if the line height of the rich text is variable.
     */
    public final boolean isVariableLineHeight() {
	return variableLineHeight;
    }

    /**
     * Checks if multiple paragraph styles are allowed.
     */
    public final boolean multipleParagraphStylesAllowed() {
	return variableLineHeight;
    }

    /**
     * Returns the default text style for the rich text.
     */
    public final TextStyle getTextStyle() {
	return textStyle;
    }

    /**
     * Returns the default paragraph style for the rich text.
     */
    public final ParagraphStyle getParagraphStyle() {
	return paragraphStyle;
    }

    /**
     * Returns the default tab width.
     */
    public int getTabWidth() {
	return tabWidth;
    }

    /**
     * Checks if the line wrapping style is the character line wrapping style.
     *
     * @see #getLineWrap()
     * @see #CHAR_WRAP
     */
    public final boolean isCharWrap() {
	return lineWrap == CHAR_WRAP;
    }

    /**
     * Checks if the line wrapping style is the word line wrapping style.
     *
     * @see #getLineWrap()
     * @see #WORD_WRAP
     */
    public final boolean isWordWrap() {
	return lineWrap == WORD_WRAP;
    }

    /**
     * Checks if the line wrapping style is the no line wrapping style.
     *
     * @see #getLineWrap()
     * @see #NO_WRAP
     */
    public final boolean isNoWrap() {
	return lineWrap == NO_WRAP;
    }

    /**
     * Checks if the line end style is the java line end style.
     *
     * @see #getLineEnd()
     * @see #JAVA_LINE_SEPARATOR
     */
    public final boolean isJavaLineSeparator() {
	return lineEnd == JAVA_LINE_SEPARATOR;
    }

    /**
     * Checks if the line end style is the java line end style with line break.
     *
     * @see #getLineEnd()
     * @see #JAVA_LINE_SEPARATOR_WITH_BREAK
     */
    public final boolean isJavaLineSeparatorWithBreak() {
	return lineEnd == JAVA_LINE_SEPARATOR_WITH_BREAK;
    }

    /**
     * Checks if the line end style is the list line end style.
     *
     * @see #getLineEnd()
     * @see #LIST_SEPARATOR
     */
    public final boolean isListSeparator() {
	return lineEnd == LIST_SEPARATOR;
    }

    /**
     * Checks if the line break is handled.
     *
     * @see #isJavaLineSeparatorWithBreak()
     * @see #JAVA_LINE_SEPARATOR_WITH_BREAK
     */
    public final boolean handleBreak() {
	return lineEnd == JAVA_LINE_SEPARATOR_WITH_BREAK;
    }

    /**
     * Creates a new style by replicating this style with a new line
     * wrapping style and line end style associated with it.
     *
     * @param  lineWrap the line wrapping style for the new style.
     * @param  lineEnd  the line end style for the new style.
     * @return a new style.
     */
    public RichTextStyle deriveStyle(int lineWrap, int lineEnd) {
	RichTextStyle ltStyle = (RichTextStyle)clone();
	ltStyle.setLineWrap(lineWrap);
	ltStyle.setLineEnd(lineEnd);
	return ltStyle;
    }

    /**
     * Creates a new style by replicating this style with a new text
     * style object associated with it.
     *
     * @param  textStyle the text style object for the new style.
     * @return a new style.
     */
    public RichTextStyle deriveStyle(TextStyle textStyle) {
	if (textStyle == null)
	    throw new NullPointerException();
	RichTextStyle ltStyle = (RichTextStyle)clone();
	ltStyle.textStyle = textStyle;
	ltStyle.tabWidth =
		textStyle.getFontMetrics().charWidth(' ') *
						ParagraphStyle.HARD_TAB_LENGTH;
	return ltStyle;
    }

    /**
     * Creates a new style by replicating this style with a new paragraph
     * style object associated with it.
     *
     * @param  paragraphStyle the paragraph style object for the new style.
     * @return a new style.
     */
    public RichTextStyle deriveStyle(ParagraphStyle paragraphStyle) {
	if (paragraphStyle == null)
	    throw new NullPointerException();
	RichTextStyle ltStyle = (RichTextStyle)clone();
	ltStyle.paragraphStyle = paragraphStyle;
	return ltStyle;
    }

    /**
     * Returns a hashcode of this rich text style.
     */
    public int hashCode() {
	return lineWrap ^ lineEnd ^
			(new Boolean(variableLineHeight)).hashCode() ^
			textStyle.hashCode() ^ paragraphStyle.hashCode();
    }

    /**
     * Compares two objects for equality.
     */
    public boolean equals(Object anObject) {
	if (this == anObject)
	    return true;
	if (anObject == null)
	    return false;
	if (anObject instanceof RichTextStyle) {
	    RichTextStyle rtStyle = (RichTextStyle)anObject;
	    return (lineWrap            == rtStyle.lineWrap            &&
		    lineEnd             == rtStyle.lineEnd             &&
		    variableLineHeight  == rtStyle.variableLineHeight  &&
		    textStyle.equals(rtStyle.textStyle)                &&
		    paragraphStyle.equals(rtStyle.paragraphStyle));
	}
	return false;
    }

    /**
     * Returns a clone of this rich text style.
     */
    public Object clone() {
	try {
	    RichTextStyle rts = (RichTextStyle)super.clone();
	    rts.lineWrap       = lineWrap;
	    rts.lineEnd        = lineEnd;
	    rts.textStyle      = textStyle;      // share
	    rts.paragraphStyle = paragraphStyle; // share
	    rts.tabWidth       = tabWidth;
	    return rts;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns the string representation of this rich text style.
     */
    public String toString() {
	String	str = "";

	if (isCharWrap())
	    str = "lineWrap=character";
	else if (isWordWrap())
	    str = "lineWrap=word";
	else //if (isNoWrap())
	    str = "lineWrap=no";

	if (isJavaLineSeparator())
	    str += ",lineEnd=java";
	else if (isJavaLineSeparatorWithBreak())
	    str += ",lineEnd=java_break";
	else //if (isListSeparator())
	    str += ",lineEnd=list";

	if (variableLineHeight)
	    str += ",lineHeight=variable";
	else
	    str += ",lineHeight=fixed";

	return getClass().getName() + "[" + str + "]";
    }

    /** Sets the line wrapping style. */
    protected void setLineWrap(int lineWrap) {
	switch (lineWrap) {
	case CHAR_WRAP:
	case WORD_WRAP:
	case NO_WRAP:
	    this.lineWrap = lineWrap;
	    return;
	}
	throw new IllegalArgumentException("improper lineWrap: " + lineWrap);
    }

    /** Sets the line end style. */
    protected void setLineEnd(int lineEnd) {
	switch (lineEnd) {
	case JAVA_LINE_SEPARATOR:
	case JAVA_LINE_SEPARATOR_WITH_BREAK:
	case LIST_SEPARATOR:
	    this.lineEnd = lineEnd;
	    return;
	}
	throw new IllegalArgumentException("improper lineEnd: " + lineEnd);
    }
}
