/*
 * RichText.java
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

import jp.kyasu.graphics.text.TextChange;
import jp.kyasu.graphics.text.TextPositionInfo;
import jp.kyasu.util.RunArray;
import jp.kyasu.util.VArray;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.PrintJob;
import java.text.CharacterIterator;
import java.util.Enumeration;

/**
 * The <code>RichText</code> class implements a text with paragraph styles.
 * The principal operations on a <code>RichText</code> are the
 * <code>replace</code>, <code>setTextStyle</code>,
 * <code>modifyTextStyle</code>, <code>setParagraphStyle</code>,
 * <code>modifyParagraphStyle</code>, and <code>print</code> methods:
 * <ul>
 * <li>The <code>replace</code> method replaces the specified range of the
 *     rich text with the specified replacement text and returns the
 *     <code>TextChange</code> object.
 * <li>The <code>setTextStyle</code> method sets the text style in the
 *     specified range of the rich text to be the specified text style
 *     and returns the <code>TextChange</code> object.
 * <li>The <code>modifyTextStyle</code> method modifies the text style in
 *     the specified range of the rich text by using the specified
 *     <code>TextStyleModifier</code> object and returns the
 *     <code>TextChange</code> object.
 * <li>The <code>setParagraphStyle</code> method sets the paragraph style
 *     at the specified range of the rich text to be the specified
 *     paragraph style and returns the <code>TextChange</code> object.
 * <li>The <code>modifyParagraphStyle</code> method modifies the paragraph
 *     style at the specified range of the rich text by using the
 *     specified <code>ParagraphStyleModifier</code> object and returns the
 *     <code>TextChange</code> object.
 * <li>The <code>print</code> method prints the rich text to a print
 *     device provided from the specified <code>PrintJob</code> object.
 * </ul>
 * <p>
 * The <code>RichText</code> object is laid out (composed) into the lines
 * by a <code>TextLayout</code> object. The <code>TextLayout</code> object
 * composes the <code>RichText</code> object into multiple paragraphs that
 * are separated by the line end character
 * (<code>RichTextStyle.getLineEndChar()</code>). Each paragraph has its
 * own paragraph style, if the
 * <code>RichTextStyle.multipleParagraphStylesAllowed()</code> is
 * <code>true</code>. The <code>RichText</code> object in the paragraph is
 * composed into multiple lines that are broken at the layout width.
 *
 * @see 	jp.kyasu.graphics.TextStyle
 * @see 	jp.kyasu.graphics.TextStyleModifier
 * @see 	jp.kyasu.graphics.ParagraphStyle
 * @see 	jp.kyasu.graphics.ParagraphStyleModifier
 * @see 	jp.kyasu.graphics.text.TextChange
 * @see 	jp.kyasu.graphics.TextLayout
 *
 * @version 	15 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class RichText implements Cloneable, java.io.Serializable {
    /** The text of the rich text. */
    protected Text text;

    /** The style of the rich text. */
    protected RichTextStyle rtStyle;

    /** The paragraph styles. */
    protected RunArray paragraphStyles;

    /** The text style constraint. */
    transient protected TextStyleModifier textStyleConstraint;


    /**
     * Constructs an empty rich text with the specified rich text style.
     *
     * @param rtStyle the rich text style.
     */
    public RichText(RichTextStyle rtStyle) {
	this(new Text(), rtStyle);
    }

    /**
     * Constructs a rich text with the specified string and rich text style.
     *
     * @param string  the initial string.
     * @param rtStyle the rich text style.
     */
    public RichText(String string, RichTextStyle rtStyle) {
	this(new Text(string, rtStyle.getTextStyle()), rtStyle);
    }

    /**
     * Constructs a rich text with the specified text and rich text style.
     *
     * @param text    the initial text.
     * @param rtStyle the rich text style.
     */
    public RichText(Text text, RichTextStyle rtStyle) {
	if (text == null || rtStyle == null)
	    throw new NullPointerException();
	this.text    = text;
	this.rtStyle = rtStyle;
	if (rtStyle.variableLineHeight) {
	    paragraphStyles = new RunArray(text.length() + 1,
					   rtStyle.paragraphStyle,
					   ParagraphStyle.class);
	}
	else {
	    paragraphStyles = null;
	}
	text.baseStyleOn(0, text.length(),
			 (rtStyle.paragraphStyle.hasBaseStyle() ?
				rtStyle.paragraphStyle.getBaseStyle() :
				rtStyle.textStyle));
    }

    /**
     * Constructs a rich text that has the same contents as the specified
     * rich text.
     *
     * @param richText the rich text.
     */
    protected RichText(RichText richText) {
	this(richText.text, richText.rtStyle, richText.paragraphStyles);
    }

    /**
     * Constructs a rich text with the specified text, rich text style,
     * and paragraph styles.
     *
     * @param text            the text.
     * @param rtStyle         the rich text style.
     * @param paragraphStyles the paragraph style.
     */
    protected RichText(Text text, RichTextStyle rtStyle,
		       RunArray paragraphStyles)
    {
	if (text == null || rtStyle == null)
	    throw new NullPointerException();
	this.text            = text;
	this.rtStyle         = rtStyle;
	this.paragraphStyles = paragraphStyles;
    }


    /**
     * Returns the <code>Text</code> object in this rich text.
     * An application should not modify the returned text.
     */
    public final Text getText() {
	return text;
    }

    /**
     * Returns the <code>RichTextStyle</code> object of this rich text.
     */
    public final RichTextStyle getRichTextStyle() {
	return rtStyle;
    }

    /**
     * Tests if this rich text has no characters.
     *
     * @return <code>true</code> if this text has no characters;
     *         <code>false</code> otherwise.
     * @see jp.kyasu.graphics.Text#isEmpty()
     */
    public final boolean isEmpty() {
	return text.isEmpty();
    }

    /**
     * Returns the length of this rich text.
     *
     * @see jp.kyasu.graphics.Text#length()
     */
    public final int length() {
	return text.length();
    }

    /**
     * Returns the character at the specified index.
     *
     * @param  index an index into this rich text.
     * @return the character at the specified index.
     * @see jp.kyasu.graphics.Text#getChar(int)
     */
    public final char getChar(int textIndex) {
	return text.getChar(textIndex);
    }

    /**
     * Returns the character iterator for this text.
     *
     * @return the character iterator for this text.
     */
    public final CharacterIterator getCharacterIterator() {
	return text.getCharacterIterator();
    }

    /**
     * Returns the character iterator for this text, with the specified
     * initial index.
     *
     * @param pos initial iterator position.
     * @return the character iterator for this text.
     */
    public final CharacterIterator getCharacterIterator(int pos) {
	return text.getCharacterIterator(pos);
    }

    /**
     * Returns the character iterator for this text, with the specified
     * range and initial index.
     *
     * @param begin index of the first character.
     * @param end   index of the character following the last character.
     * @param pos   initial iterator position.
     * @return the character iterator for this text.
     */
    public final CharacterIterator getCharacterIterator(int begin, int end,
							int pos)
    {
	return text.getCharacterIterator(begin, end, pos);
    }

    /**
     * Returns an enumeration of the text styles of this text.
     *
     * @return an enumeration of the text styles of this text.
     */
    public final Enumeration textStyles() {
	return text.textStyles();
    }

    /**
     * Returns an enumeration of the text styles of this text.
     *
     * @param begin the beginning index to get styles, inclusive.
     * @param end   the ending index to get styles, exclusive.
     * @return an enumeration of the text styles of this text.
     */
    public final Enumeration textStyles(int begin, int end) {
	return text.textStyles(begin, end);
    }

    /**
     * Returns the text styles as a run array in this rich text.
     * An application should not modify the returned run array.
     * @see jp.kyasu.graphics.Text#getTextStyleRuns()
     */
    public final RunArray getTextStyleRuns() {
	return text.getTextStyleRuns();
    }

    /**
     * Returns the text style at the specified index.
     *
     * @param  index an index into this rich text.
     * @return the text style at the specified index.
     * @see jp.kyasu.graphics.Text#getTextStyleAt()
     */
    public final TextStyle getTextStyleAt(int textIndex) {
	return text.getTextStyleAt(textIndex);
    }

    /**
     * Returns the number of the text styles in this rich text.
     * @see jp.kyasu.graphics.Text#getTextStyleCount()
     */
    public final int getTextStyleCount() {
	return text.getTextStyleCount();
    }

    /**
     * Returns all text styles in this rich text.
     * @see jp.kyasu.graphics.Text#getTextStyles()
     */
    public final TextStyle[] getTextStyles() {
	return text.getTextStyles();
    }

    /**
     * Returns the text styles in this rich text.
     *
     * @param  begin the beginning index to get text styles, inclusive.
     * @param  end   the ending index to get text styles, exclusive.
     * @return the text styles in this rich text.
     * @see jp.kyasu.graphics.Text#getTextStyles(int, int)
     */
    public final TextStyle[] getTextStyles(int begin, int end) {
	return text.getTextStyles(begin, end);
    }

    /**
     * Returns the text attachment at the specified index.
     *
     * @param  index an index into this rich text.
     * @return the text attachment if this rich text contains the text
     *         attachment at the specified index; <code>null</code> otherwise.
     * @see jp.kyasu.graphics.Text#getAttachmentAt(int)
     */
    public final TextAttachment getAttachmentAt(int textIndex) {
	return text.getAttachmentAt(textIndex);
    }

    /**
     * Returns the number of the text attachments in this rich text.
     *
     * @see jp.kyasu.graphics.Text#getAttachmentCount()
     */
    public final int getAttachmentCount() {
	return text.getAttachmentCount();
    }

    /**
     * Returns an enumeration of the paragraph styles of this text.
     *
     * @return an enumeration of the paragraph styles of this text.
     */
    public final Enumeration paragraphStyles() {
	if (rtStyle.variableLineHeight) {
	    return paragraphStyles.elements();
	}
	else {
	    return new SingleEnumerator(rtStyle.paragraphStyle);
	}
    }

    /**
     * Returns an enumeration of the paragraph styles of this text.
     *
     * @param begin the beginning index to get styles, inclusive.
     * @param end   the ending index to get styles, exclusive.
     * @return an enumeration of the paragraph styles of this text.
     */
    public final Enumeration paragraphStyles(int begin, int end) {
	if (rtStyle.variableLineHeight) {
	    return paragraphStyles.elements(begin, end);
	}
	else {
	    if (begin < 0 || end > text.length() + 1 || begin > end)
		throw new ArrayIndexOutOfBoundsException();
	    if (begin == end) {
		return new SingleEnumerator(null);
	    }
	    else {
		return new SingleEnumerator(rtStyle.paragraphStyle);
	    }
	}
    }

    /**
     * Returns the paragraph styles as a run array in this rich text.
     * An application should not modify the returned run array.
     */
    public final RunArray getParagraphStyleRuns() {
	if (rtStyle.variableLineHeight) {
	    return paragraphStyles;
	}
	else {
	    return new RunArray(text.length() + 1,
				rtStyle.paragraphStyle,
				ParagraphStyle.class);
	}
    }

    /**
     * Returns the paragraph style at the specified index.
     *
     * @param  index an index into this rich text.
     * @return the paragraph style at the specified index.
     */
    public final ParagraphStyle getParagraphStyleAt(int textIndex) {
	if (rtStyle.variableLineHeight) {
	    return (ParagraphStyle)paragraphStyles.get(textIndex);
	}
	else {
	    if (textIndex < 0 || textIndex > text.length()) {
		throw new ArrayIndexOutOfBoundsException();
	    }
	    return rtStyle.paragraphStyle;
	}
    }

    /**
     * Returns the number of the paragraph styles in this rich text.
     */
    public final int getParagraphStyleCount() {
	if (rtStyle.variableLineHeight) {
	    return paragraphStyles.getValueCount();
	}
	else {
	    return 1;
	}
    }

    /**
     * Returns all paragraph styles in this rich text.
     */
    public final ParagraphStyle[] getParagraphStyles() {
	if (rtStyle.variableLineHeight) {
	    return (ParagraphStyle[])paragraphStyles.getValues();
	}
	else {
	    return new ParagraphStyle[]{ rtStyle.paragraphStyle };
	}
    }

    /**
     * Returns the paragraph styles in this rich text.
     *
     * @param begin the beginning index of the text to get paragraph styles,
     *              inclusive.
     * @param end   the ending index of the text to get paragraph styles,
     *              exclusive.
     */
    public final ParagraphStyle[] getParagraphStyles(int begin, int end) {
	if (rtStyle.variableLineHeight) {
	    return (ParagraphStyle[])paragraphStyles.getValues(begin, end);
	}
	else {
	    if (begin < 0 || end > text.length() + 1 || begin > end)
		throw new ArrayIndexOutOfBoundsException();
	    if (begin == end) {
		return new ParagraphStyle[0];
	    }
	    else {
		return new ParagraphStyle[]{ rtStyle.paragraphStyle };
	    }
	}
    }

    /**
     * Returns the paragraph styles in this rich text per paragraph.
     *
     * @param begin the beginning index of the text to get paragraph styles,
     *              inclusive.
     * @param end   the ending index of the text to get paragraph styles,
     *              exclusive.
     */
    public final ParagraphStyle[] getParagraphStylesPerParagraph(int begin,
								 int end)
    {
	if (begin < 0 || end > text.length() + 1 || begin > end)
	    throw new ArrayIndexOutOfBoundsException();
	if (begin == end) {
	    return new ParagraphStyle[0];
	}
	VArray varray = new VArray(ParagraphStyle.class);
	int index = begin;
	while (index < end) {
	    if (rtStyle.variableLineHeight) {
		varray.append(paragraphStyles.get(index));
	    }
	    else {
		varray.append(rtStyle.paragraphStyle);
	    }
	    index = nextParagraphBeginIndexOf(index);
	    if (index < 0) index = text.length() + 1;
	}
	return (ParagraphStyle[])varray.getTrimmedArray();
    }

    /**
     * Returns a new rich text that is a subtext of this rich text.
     *
     * @param     begin the beginning index, inclusive.
     * @param     end   the ending index, exclusive.
     * @return    the subtext.
     */
    public RichText subtext(int begin, int end) {
	return new RichText(
			text.subtext(begin, end),
			rtStyle,
			(paragraphStyles == null ? null :
				paragraphStyles.subarray(begin, end + 1)));
    }

    /**
     * Returns the index of the paragraph begin, searching backward starting
     * at the specified index.
     *
     * @param  fromIndex the index to start the search from.
     * @return the index of the the paragraph begin, inclusive.
     */
    public final int paragraphBeginIndexOf(int fromIndex) {
	if (fromIndex <= 0)
	    return 0;
	else
	    return text.lastIndexOf(rtStyle.getLineEndChar(), fromIndex-1) + 1;
    }

    /**
     * Returns the index of the paragraph end, starting the search at
     * the specified index.
     *
     * @param  fromIndex the index to start the search from.
     * @return the index of the paragraph end, inclusive.
     */
    public final int paragraphEndIndexOf(int fromIndex) {
	int index = text.indexOf(rtStyle.getLineEndChar(), fromIndex);
	return (index < 0 ? text.length() - 1 : index);
    }

    /**
     * Returns the index of the next paragraph begin, starting the search
     * at the specified index.
     *
     * @param  fromIndex the index to start the search from.
     * @return the index of the next paragraph end, or <code>-1</code>
     *         if the paragraph is last one.
     */
    public final int nextParagraphBeginIndexOf(int fromIndex) {
	int index = text.indexOf(rtStyle.getLineEndChar(), fromIndex);
	return (index < 0 || index >= text.length() - 1 ? - 1 : index + 1);
    }

    /**
     * Sets the base text style of this rich text to be the specified
     * text style.
     *
     * @param  textStyle the text style.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange setBaseTextStyle(TextStyle textStyle) {
	if (textStyle == null)
	    throw new NullPointerException();
	rtStyle = rtStyle.deriveStyle(textStyle);
	return setTextStyle(0, length(), textStyle);
    }

    /**
     * Returns the text style constraint of this rich text.
     *
     * @return the text style constraint of this rich text.
     * @see #setTextStyleConstraint(jp.kyasu.graphics.TextStyleModifier)
     */
    public TextStyleModifier getTextStyleConstraint() {
	return textStyleConstraint;
    }

    /**
     * Sets the text style constraint of this rich text to be the specified
     * text style modifier.
     *
     * @param constraint the text style modifier.
     * @see #getTextStyleConstraint()
     */
    public void setTextStyleConstraint(TextStyleModifier constraint) {
	if (constraint != null) {
	    if (text != null && rtStyle != null && paragraphStyles != null) {
		textStyleConstraint = null;
		modifyTextStyle(0, length(), constraint);
	    }
	}
	textStyleConstraint = constraint;
    }


    /**
     * Returns a clone of this rich text.
     */
    public Object clone() {
	try {
	    RichText rtext = (RichText)super.clone();
	    rtext.text            = (Text)text.clone();
	    rtext.rtStyle         = rtStyle; // share
	    rtext.paragraphStyles = (RunArray)paragraphStyles.clone();
	    return rtext;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }


    /**
     * Replaces the specified range of this rich text with the specified
     * replacement text object.
     *
     * @param  begin the beginning text position to replace, inclusive.
     * @param  end   the ending text position to replace, exclusive.
     * @param  rep   a replacement <code>Text</code> object.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange replace(int begin, int end, Text rep) {
	if (begin == end && rep.isEmpty())
	    return new TextChange(TextChange.NO_LAYOUT);
	if (begin < 0 || end > text.length() || begin > end)
	    throw new ArrayIndexOutOfBoundsException();
	rep = rep.cloneStyle();
	if (textStyleConstraint != null) {
	    rep.modifyStyle(0, rep.length(), textStyleConstraint);
	}
	if (!rtStyle.variableLineHeight) {
	    rep.baseStyleOn(0, rep.length(),
			    (rtStyle.paragraphStyle.hasBaseStyle() ?
				rtStyle.paragraphStyle.getBaseStyle() :
				rtStyle.textStyle));
	    text.replace(begin, end, rep);
	    return new TextChange(begin, end, begin, end,
				  rep.length() - (end - begin),
				  true, false);
	}
	int paraChangeEnd;
	ParagraphStyle bStyle = (ParagraphStyle)paragraphStyles.get(begin);
	if (begin == end || bStyle.equals(paragraphStyles.get(end))) {
	    paraChangeEnd = end;
	}
	else {
	    if (rep.length() > 0 &&
		rep.getChar(rep.length() - 1) == rtStyle.getLineEndChar())
	    {
		paraChangeEnd = end;
	    }
	    else {
		paraChangeEnd = nextParagraphBeginIndexOf(end);
		if (paraChangeEnd < 0) {
		    paraChangeEnd = text.length() + 1;
		}
	    }
	}
	int lengthChanged = rep.length() - (end - begin);
	int newParaChangeEnd = paraChangeEnd + lengthChanged;
	rep.baseStyleOn(0, rep.length(),
			(bStyle.hasBaseStyle() ?
				bStyle.getBaseStyle() :
				rtStyle.textStyle));
	if (end < paraChangeEnd) {
	    text.baseStyleOn(end,
			     Math.min(paraChangeEnd, text.length()),
			     (bStyle.hasBaseStyle() ?
				bStyle.getBaseStyle() :
				rtStyle.textStyle));
	}
	text.replace(begin, end, rep);
	paragraphStyles.replace(begin, paraChangeEnd,
				new RunArray(newParaChangeEnd - begin,
					     bStyle,
					     ParagraphStyle.class));
	return new TextChange(begin, end, begin, paraChangeEnd, lengthChanged,
			      true, false);
    }

    /**
     * Replaces the specified range of this rich text with the specified
     * replacement rich text object.
     *
     * @param  begin the beginning text position to replace, inclusive.
     * @param  end   the ending text position to replace, exclusive.
     * @param  rep   a replacement <code>RichText</code> object.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange replace(int begin, int end, RichText rep) {
	if (begin == end && rep.isEmpty())
	    return new TextChange(TextChange.NO_LAYOUT);

	if (rep.isEmpty())
	    return replace(begin, end, rep.text);

	if (!rtStyle.variableLineHeight || !rep.rtStyle.variableLineHeight)
	    return replace(begin, end, rep.text);

	if (begin < 0 || end > text.length() || begin > end)
	    throw new ArrayIndexOutOfBoundsException();

	int repLength = rep.length();
	int repParBegin = 0;
	if (begin > 0 && text.getChar(begin - 1) != rtStyle.getLineEndChar()) {
	    repParBegin = rep.nextParagraphBeginIndexOf(0);
	    if (repParBegin < 0) {
		return replace(begin, end, rep.text);
	    }
	}
	int paraChangeEnd;
	ParagraphStyle lastStyle =
			(ParagraphStyle)rep.paragraphStyles.get(repLength - 1);
	if (lastStyle.equals(paragraphStyles.get(end))) {
	    paraChangeEnd = end;
	}
	else {
	    if (rep.getChar(repLength - 1) == rtStyle.getLineEndChar()) {
		paraChangeEnd = end;
	    }
	    else {
		paraChangeEnd = nextParagraphBeginIndexOf(end);
		if (paraChangeEnd < 0) {
		    paraChangeEnd = text.length() + 1;
		}
	    }
	}
	int lengthChanged = repLength - (end - begin);
	if (end < paraChangeEnd) {
	    text.baseStyleOn(end,
			     Math.min(paraChangeEnd, text.length()),
			     (lastStyle.hasBaseStyle() ?
				lastStyle.getBaseStyle() :
				rtStyle.textStyle));
	    paragraphStyles.replace(end, paraChangeEnd,
				    new RunArray(paraChangeEnd - end,
						 lastStyle,
						 ParagraphStyle.class));
	}
	Text repText = rep.text;
	if (textStyleConstraint != null) {
	    repText = repText.cloneStyle();
	    repText.modifyStyle(0, repText.length(), textStyleConstraint);
	}
	text.replace(begin, end, repText);
	RunArray subPars = rep.paragraphStyles.subarray(0, repLength);
	if (repParBegin > 0) {
	    ParagraphStyle bStyle =
				(ParagraphStyle)paragraphStyles.get(begin - 1);
	    subPars.replace(0, repParBegin,
			    new RunArray(repParBegin, bStyle,
					 ParagraphStyle.class));
	    text.baseStyleOn(begin, begin + repParBegin,
			     (bStyle.hasBaseStyle() ?
				bStyle.getBaseStyle() :
				rtStyle.textStyle));
	}
	paragraphStyles.replace(begin, end, subPars);
	return new TextChange(begin, end, begin, paraChangeEnd, lengthChanged,
			      true, false);
    }

    /*
     * Sets the text style in the specified range of this rich text
     * to be the specified text style.
     *
     * @param  begin     the beginning text position to set, inclusive.
     * @param  end       the ending text position to set, exclusive.
     * @param  textStyle the text style.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange setTextStyle(int begin, int end, TextStyle textStyle) {
	if (begin == end)
	    return new TextChange(TextChange.NO_LAYOUT);
	if (textStyleConstraint != null) {
	    textStyle = textStyleConstraint.modify(textStyle);
	}
	if (!rtStyle.variableLineHeight) {
	    text.replaceStyle(begin, end, textStyle);
	    text.baseStyleOn(begin, end,
			     (rtStyle.paragraphStyle.hasBaseStyle() ?
				rtStyle.paragraphStyle.getBaseStyle() :
				rtStyle.textStyle));
	    return new TextChange(begin, end, begin, end, 0, false, false);
	}
	text.replaceStyle(begin, end, textStyle);
	int index = begin;
	while (index < end) {
	    ParagraphStyle pStyle = (ParagraphStyle)paragraphStyles.get(index);
	    int runEnd = index + paragraphStyles.getRunLengthAt(index);
	    if (runEnd > end) runEnd = end;
	    text.baseStyleOn(index, runEnd,
			     (pStyle.hasBaseStyle() ?
				pStyle.getBaseStyle() :
				rtStyle.textStyle));
	    index = runEnd;
	}
	return new TextChange(begin, end, begin, end, 0, false, false);
    }

    /*
     * Sets the text styles in the specified range of this rich text
     * to be the specified text styles.
     *
     * @param  begin      the beginning text position to set, inclusive.
     * @param  end        the ending text position to set, exclusive.
     * @param  textStyles the text styles.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange setTextStyles(int begin, int end, RunArray textStyles) {
	if (textStyles.getComponentType() != TextStyle.class) {
	    throw new IllegalArgumentException(
			"TextStyles must be a RunArray of TextStyle");
	}
	if (textStyles.length() != end - begin) {
	    throw new IllegalArgumentException(
			"The size of TextStyles must equal to end - begin");
	}
	if (begin == end)
	    return new TextChange(TextChange.NO_LAYOUT);
	if (textStyleConstraint != null) {
	    textStyles = (RunArray)textStyles.clone();
	    int index = 0;
	    int limit = textStyles.length();
	    while (index < limit) {
		TextStyle textStyle = (TextStyle)textStyles.get(index);
		int runEnd = index + textStyles.getRunLengthAt(index);
		if (runEnd > limit) runEnd = limit;
		TextStyle modStyle = textStyleConstraint.modify(textStyle);
		if (modStyle != textStyle) {
		    textStyles.replace(index, runEnd,
				       new RunArray(runEnd - index, modStyle,
						    TextStyle.class));
		}
		index = runEnd;
	    }
	}
	if (!rtStyle.variableLineHeight) {
	    text.runs.replace(begin, end, textStyles);
	    text.baseStyleOn(begin, end,
			     (rtStyle.paragraphStyle.hasBaseStyle() ?
				rtStyle.paragraphStyle.getBaseStyle() :
				rtStyle.textStyle));
	    return new TextChange(begin, end, begin, end, 0, false, false);
	}
	text.runs.replace(begin, end, textStyles);
	int index = begin;
	while (index < end) {
	    ParagraphStyle pStyle = (ParagraphStyle)paragraphStyles.get(index);
	    int runEnd = index + paragraphStyles.getRunLengthAt(index);
	    if (runEnd > end) runEnd = end;
	    text.baseStyleOn(index, runEnd,
			     (pStyle.hasBaseStyle() ?
				pStyle.getBaseStyle() :
				rtStyle.textStyle));
	    index = runEnd;
	}
	return new TextChange(begin, end, begin, end, 0, false, false);
    }

    class ConstraintModifier implements TextStyleModifier {
	TextStyleModifier modifier;
	ConstraintModifier(TextStyleModifier modifier) {
	    this.modifier = modifier;
	}
	public TextStyle modify(TextStyle tStyle) {
	    return textStyleConstraint.modify(modifier.modify(tStyle));
	}
    }

    /*
     * Modifies the text style in the specified range of this rich text
     * by using the specified text style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the text style modifier.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange modifyTextStyle(int begin, int end,
				      TextStyleModifier modifier)
    {
	if (begin == end)
	    return new TextChange(TextChange.NO_LAYOUT);
	if (textStyleConstraint != null) {
	    modifier = new ConstraintModifier(modifier);
	}
	text.modifyStyle(begin, end, modifier);
	int index = begin;
	while (index < end) {
	    ParagraphStyle pStyle = (ParagraphStyle)paragraphStyles.get(index);
	    int runEnd = index + paragraphStyles.getRunLengthAt(index);
	    if (runEnd > end) runEnd = end;
	    text.baseStyleOn(index, runEnd,
			     (pStyle.hasBaseStyle() ?
				pStyle.getBaseStyle() :
				rtStyle.textStyle));
	    index = runEnd;
	}
	return new TextChange(begin, end, begin, end, 0, false, false);
    }

    /*
     * Sets the paragraph style in the specified range of this rich text
     * to be the specified paragraph style.
     *
     * @param  begin  the beginning text position to set, inclusive.
     * @param  end    the ending text position to set, exclusive.
     * @param  pStyle the paragraph style.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange setParagraphStyle(int begin, int end,
					ParagraphStyle pStyle)
    {
	if (begin < 0 || end > text.length() + 1 || begin > end)
	    throw new ArrayIndexOutOfBoundsException();

	if (!rtStyle.variableLineHeight)
	    return new TextChange(TextChange.NO_LAYOUT);

	//if (begin == end) return new TextChange(TextChange.NO_LAYOUT);

	int paraChangeBegin = paragraphBeginIndexOf(begin);
	int paraChangeEnd = nextParagraphBeginIndexOf(Math.max(end - 1, 0));
	if (paraChangeEnd < 0) {
	    paraChangeEnd = text.length() + 1;
	}
	paragraphStyles.replace(paraChangeBegin, paraChangeEnd,
				new RunArray(paraChangeEnd - paraChangeBegin,
					     pStyle,
					     ParagraphStyle.class));
	int textLength = text.length();
	if (paraChangeBegin < textLength && pStyle.hasBaseStyle()) {
	    text.baseStyleOn(paraChangeBegin,
			     Math.min(paraChangeEnd, textLength),
			     pStyle.getBaseStyle());
	}
	return new TextChange(begin, end, paraChangeBegin, paraChangeEnd, 0,
			      false, true);
    }

    /*
     * Sets the paragraph styles in the specified range of this rich text
     * to be the specified paragraph styles.
     *
     * @param  begin   the beginning text position to set, inclusive.
     * @param  end     the ending text position to set, exclusive.
     * @param  pStyles the paragraph styles.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange setParagraphStyles(int begin, int end,
					 ParagraphStyle pStyles[])
    {
	if (begin < 0 || end > text.length() + 1 || begin > end)
	    throw new ArrayIndexOutOfBoundsException();

	int pLength = pStyles.length;
	if (pLength == 0) {
	    throw new IllegalArgumentException("ParagraphStyles is empty");
	}

	if (!rtStyle.variableLineHeight)
	    return new TextChange(TextChange.NO_LAYOUT);

	//if (begin == end) return new TextChange(TextChange.NO_LAYOUT);

	int paraChangeBegin = paragraphBeginIndexOf(begin);
	int paraChangeEnd = nextParagraphBeginIndexOf(Math.max(end - 1, 0));
	if (paraChangeEnd < 0) {
	    paraChangeEnd = text.length() + 1;
	}
	int textLength = text.length();
	int pIndex = 0;
	int index = paraChangeBegin;
	while (index < paraChangeEnd) {
	    int pEnd = nextParagraphBeginIndexOf(index);
	    if (pEnd < 0) pEnd = textLength + 1;
	    ParagraphStyle pStyle = (pIndex < pLength ?
					pStyles[pIndex++] :
					pStyles[pLength - 1]);
	    paragraphStyles.replace(index, pEnd,
				    new RunArray(pEnd - index, pStyle,
						 ParagraphStyle.class));
	    if (index < textLength && pStyle.hasBaseStyle()) {
		text.baseStyleOn(index,
				 Math.min(pEnd, textLength),
				 pStyle.getBaseStyle());
	    }
	    index = pEnd;
	}
	return new TextChange(begin, end, paraChangeBegin, paraChangeEnd, 0,
			      false, true);
    }

    /*
     * Modifies the paragraph style in the specified range of this rich text
     * by using the specified paragraph style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the paragraph style modifier.
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this rich text made by this method.
     */
    public TextChange modifyParagraphStyle(int begin, int end,
					   ParagraphStyleModifier modifier)
    {
	if (begin < 0 || end > text.length() + 1 || begin > end)
	    throw new ArrayIndexOutOfBoundsException();

	if (!rtStyle.variableLineHeight)
	    return new TextChange(TextChange.NO_LAYOUT);

	//if (begin == end) return new TextChange(TextChange.NO_LAYOUT);

	int paraChangeBegin = paragraphBeginIndexOf(begin);
	int paraChangeEnd = nextParagraphBeginIndexOf(Math.max(end - 1, 0));
	if (paraChangeEnd < 0) {
	    paraChangeEnd = text.length() + 1;
	}
	int textLength = text.length();
	int index = paraChangeBegin;
	while (index < paraChangeEnd) {
	    ParagraphStyle pStyle = (ParagraphStyle)paragraphStyles.get(index);
	    int runEnd = index + paragraphStyles.getRunLengthAt(index);
	    if (runEnd > paraChangeEnd) runEnd = paraChangeEnd;
	    ParagraphStyle modStyle = modifier.modify(pStyle);
	    if (modStyle != pStyle) {
		paragraphStyles.replace(index, runEnd,
					new RunArray(runEnd - index, modStyle,
						     ParagraphStyle.class));
		if (index < textLength && modStyle.hasBaseStyle()) {
		    text.baseStyleOn(index,
				     Math.min(runEnd, textLength),
				     modStyle.getBaseStyle());
		}
	    }
	    index = runEnd;
	}
	return new TextChange(begin, end, paraChangeBegin, paraChangeEnd, 0,
			      false, true);
    }


    // ======== Printing ========

    /**
     * The constant for the default insets of the printing medium (paper).
     */
    static public final Insets DEFAULT_PRINT_INSETS =
						new Insets(40, 40, 40, 40);


    /**
     * Prints the rich text to a print device provided from the specified
     * print job.
     *
     * @param job the print job.
     */
    public void print(PrintJob job) {
	print(job, null, true);
    }

    /**
     * Prints the rich text to a print device provided from the specified
     * print job, with the specified header string and flag determining to
     * print a page number in footer.
     *
     * @param job          the print job.
     * @param header       the header string.
     * @param printPageNum if true, prints a page number in footer.
     */
    public void print(PrintJob job, String header, boolean printPageNum) {
	print(job, DEFAULT_PRINT_INSETS, rtStyle.getLineWrap(),
	      header, printPageNum);
    }

    /**
     * Prints the rich text to a print device provided from the specified
     * print job, with the specified insets, line wrapping, header string,
     * and flag determining to print a page number in footer.
     *
     * @param job              the print job.
     * @param insets           the insets of a printing medium (paper).
     * @param lineWrap         the line wrapping style.
     * @param header           the header string.
     * @param printPageNum     if true, prints a page number in footer.
     */
    public void print(PrintJob job, Insets insets, int lineWrap,
		      String header, boolean printPageNum)
    {
	print(job, insets, lineWrap, header, printPageNum,
	      new Font("Monospaced", Font.PLAIN, 10), Color.black);
    }

    /**
     * Prints the rich text to a print device provided from the specified
     * print job, with the specified insets, line wrapping, header string,
     * flag determining to print a page number in footer, font for the
     * header/footer, and foreground printing color.
     *
     * @param job              the print job.
     * @param insets           the insets of a printing medium (paper).
     * @param lineWrap         the line wrapping style.
     * @param header           the header string.
     * @param printPageNum     if true, prints a page number in footer.
     * @param headerFooterFont the font for the header and footer.
     * @param foreground       the foreground printing color.
     */
    public void print(PrintJob job, Insets insets, int lineWrap,
		      String header, boolean printPageNum,
		      Font headerFooterFont, Color foreground)
    {
	if (job == null || insets == null || headerFooterFont == null ||
	    foreground == null)
	{
	    throw new NullPointerException();
	}

	Dimension pageSize = job.getPageDimension();
	int width  = pageSize.width  - (insets.left + insets.right);
	int height = pageSize.height - (insets.top + insets.bottom);

	TextLayout layout = new TextLayout(this, lineWrap);
	layout.setWidth(width);
	TextPositionInfo begin = layout.getTextPositionAt(0);
	int pn = 0;
	int y = begin.y;
	while (begin.textIndex < length()) {
	    TextPositionInfo end;
	    if (layout.layoutHeight - y <= height) {
		end = layout.getTextPositionAt(length());
	    }
	    else {
		end = layout.getLineBeginPositionOver(begin, y + height);
		if (begin.lineIndex == end.lineIndex) { // too large
		    end = layout.getLineBeginPositionUnder(begin, y + height);
		}
	    }
	    Graphics pg = job.getGraphics();
	    if (pg != null) {
		pg.setColor(foreground);
		layout.draw(pg, new Point(insets.left, -y + insets.top),
			    begin, end);
		pg.setColor(foreground);
		pg.setFont(headerFooterFont);
		FontMetrics fm = pg.getFontMetrics();
		int fh = fm.getHeight();
		int fa = fm.getAscent();
		if (header != null) {
		    int fw = fm.stringWidth(header);
		    pg.drawString(header, insets.left + (width - fw),
					  fa + (insets.top - fh) / 2);
		}
		if (printPageNum) {
		    String footer = "- " + String.valueOf(++pn) + " -";
		    int fw = fm.stringWidth(footer);
		    pg.drawString(footer, insets.left + ((width - fw) / 2),
					  fa + insets.top + height +
						((insets.bottom - fh) / 2));
		}
		pg.dispose();
	    }
	    y = end.y;
	    begin = end;
	}
	job.end();
    }


    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();

	if (textStyleConstraint != null &&
	    (textStyleConstraint instanceof java.io.Serializable))
	{
	    s.writeObject(textStyleConstraint);
	}
	else {
	    s.writeObject(null);
	}
    }

    private void readObject(java.io.ObjectInputStream s)
	throws ClassNotFoundException, java.io.IOException
    {
	s.defaultReadObject();

	textStyleConstraint = (TextStyleModifier)s.readObject();
    }
}


final
class SingleEnumerator implements Enumeration {
    Object obj;

    SingleEnumerator(Object obj) {
	this.obj = obj;
    }

    public boolean hasMoreElements() {
	return (obj != null);
    }

    public Object nextElement() {
	if (obj != null) {
	    Object elem = obj;
	    obj = null;
	    return elem;
	}
	throw new java.util.NoSuchElementException("SingleEnumerator");
    }
}
