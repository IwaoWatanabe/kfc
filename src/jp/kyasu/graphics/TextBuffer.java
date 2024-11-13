/*
 * TextBuffer.java
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

import jp.kyasu.graphics.text.TextPositionInfo;
import jp.kyasu.util.RunArray;
import jp.kyasu.util.VArray;

import java.awt.Color;
import java.awt.Font;

/**
 * The <code>TextBuffer</code> class provides a convenience way to create
 * <code>Text</code> object and <code>RichText</code> object.
 * <p>
 * The principal operations on a <code>TextBuffer</code> are the
 * <code>append</code>, <code>setTextStyle</code> and
 * <code>setParagraphStyle</code> methods. The <code>append</code> method
 * is overloaded so as to accept data of any type. Each effectively
 * converts a given datum to a text and then appends the text to the
 * text buffer. The <code>setTextStyle</code> and
 * <code>setParagraphStyle</code> methods set the current text style
 * and paragraph style of the text buffer respectively. The text style
 * is used for the converted text. The paragraph style is used when the
 * data of the text buffer is converted to the rich text representation
 * by the <code>toRichText</code> method.
 *
 * @see		jp.kyasu.graphics.Text
 * @see		jp.kyasu.graphics.RichText
 *
 * @version 	16 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextBuffer implements java.io.Serializable {
    /** The text used for the buffer. */
    protected Text text;

    /** The current text style. */
    protected TextStyle lastStyle;

    /** The paragraph styles. */
    protected VArray paragraphStyles;

    /** The flag indicating whether the buffer is shared. */
    protected boolean shared;


    /** The character buffer extended for the accessing of the buffer data. */
    static class CharArrayWriter extends java.io.CharArrayWriter {
	public CharArrayWriter(int initialSize) { super(initialSize); }
	public char[] getBuffer() { return buf; }
    }


    /**
     * Constructs an empty text buffer.
     */
    public TextBuffer() {
	this(16);
    }

    /**
     * Constructs an empty text buffer with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity.
     */
    public TextBuffer(int initialCapacity) {
	this(initialCapacity, TextStyle.DEFAULT_STYLE);
    }

    /**
     * Constructs an empty text buffer with the specified text style.
     *
     * @param style the text style.
     */
    public TextBuffer(TextStyle style) {
	this(16, style);
    }

    /**
     * Constructs an empty text buffer with the specified initial capacity
     * and text style.
     *
     * @param initialCapacity the initial capacity.
     * @param style           the text style.
     */
    public TextBuffer(int initialCapacity, TextStyle style) {
	if (style == null)
	    throw new NullPointerException();
	text = new Text(initialCapacity);
	lastStyle = style;
	paragraphStyles = new VArray(Object.class);
	shared = false;
    }

    /**
     * Constructs a text buffer so that it represents the same
     * sequence of characters as the string argument.
     *
     * @param str the string.
     */
    public TextBuffer(String str) {
	this(str, TextStyle.DEFAULT_STYLE);
    }

    /**
     * Constructs a text buffer so that it represents the same
     * sequence of characters as the string argument, with the
     * specified text style.
     *
     * @param str   the string.
     * @param style the text style.
     */
    public TextBuffer(String str, TextStyle style) {
	this(str.length() + 16, style);
	append(str);
    }

    /**
     * Constructs a text buffer so that it represents the same string
     * contents as the reader argument.
     *
     * @param     reader the reader to read from.
     * @exception java.io.IOException If an I/O error occurs.
     */
    public TextBuffer(java.io.Reader reader) throws java.io.IOException {
	this(reader, TextStyle.DEFAULT_STYLE);
    }

    /**
     * Constructs a text buffer so that it represents the same string
     * contents as the reader argument, with the specified text style.
     *
     * @param     reader the reader to read from.
     * @param     style  the text style.
     * @exception java.io.IOException If an I/O error occurs.
     * @see jp.kyasu.graphics.Text#copySystemToJavaReadWriter(java.io.Reader, java.io.Writer)
     */
    public TextBuffer(java.io.Reader reader, TextStyle style)
	throws java.io.IOException
    {
	if (reader == null || style == null)
	    throw new NullPointerException();
	CharArrayWriter writer = new CharArrayWriter(8 * 1024);
	Text.copySystemToJavaReadWriter(reader, writer);
	char buffer[] = writer.getBuffer();
	int bufferLength = writer.size();

	lastStyle = style;
	text = new Text(buffer, bufferLength, lastStyle);
	paragraphStyles = new VArray(Object.class);
	shared = false;
    }

    /**
     * Constructs a text buffer so that it represents the same contents
     * as the text argument.
     *
     * @param t the text.
     */
    public TextBuffer(Text t) {
	if (t == null)
	    throw new NullPointerException();
	text = t;
	lastStyle = (text.isEmpty() ?
			TextStyle.DEFAULT_STYLE :
			text.getTextStyleAt(text.length() - 1));
	paragraphStyles = new VArray(Object.class);
	shared = true;
    }


    /**
     * Returns the current length of this text buffer.
     */
    public int length() {
	return text.length();
    }

    /**
     * Checks if this text buffer is currently empty.
     */
    public boolean isEmpty() {
	return text.isEmpty();
    }

    /**
     * Returns the character at a specified index in this text buffer.
     *
     * @param  index the index of the desired character.
     * @return the character at the specified index.
     */
    public char getChar(int index) {
	return text.getChar(index);
    }

    /**
     * Returns the text attachment at a specified index in this text buffer.
     *
     * @param  index the index of the desired text attachment.
     * @return the text attachment at the specified index, or <code>null</code>
     *         if the text attachment does not exist at the specified index.
     */
    public TextAttachment getAttachmentAt(int index) {
	return text.getAttachmentAt(index);
    }

    /**
     * Returns the current text style of this text buffer.
     */
    public TextStyle getCurrentTextStyle() {
	return lastStyle;
    }

    /**
     * Sets the current text style of this text buffer to be the specified
     * text style.
     *
     * @param  style the text style.
     * @return this text buffer.
     */
    public TextBuffer setTextStyle(TextStyle style) {
	if (style == null)
	    throw new NullPointerException();
	if (!lastStyle.equals(style))
	    lastStyle = style;
	return this;
    }

    /**
     * Modifies the current text style of this text buffer by the specified
     * text style modifier.
     *
     * @param  modifier the text style modifier.
     * @return this text buffer.
     */
    public TextBuffer modifyTextStyle(TextStyleModifier modifier) {
	if (modifier == null)
	    throw new NullPointerException();
	return setTextStyle(modifier.modify(lastStyle));
    }

    /**
     * Returns the current paragraph style of this text buffer.
     */
    public ParagraphStyle getCurrentParagraphStyle() {
	int len = paragraphStyles.length();
	if (len == 0) {
	    return null;
	}
	else {
	    return (ParagraphStyle)paragraphStyles.get(len - 1);
	}
    }

    /**
     * Sets the current paragraph style of this text buffer to be the
     * specified paragraph style.
     *
     * @param  style the paragraph style.
     * @return this text buffer.
     */
    public TextBuffer setParagraphStyle(ParagraphStyle style) {
	if (style == null)
	    throw new NullPointerException();
	int len = paragraphStyles.length();
	if (len > 0 &&
	    ((Integer)(paragraphStyles.get(len - 2))).intValue() == length())
	{
	    paragraphStyles.set(len - 1, style);
	}
	else {
	    paragraphStyles.append(new Integer(length()));
	    paragraphStyles.append(style);
	}
	return this;
    }

    /**
     * Modifies the current paragraph style of this text buffer by the
     * specified paragraph style modifier.
     *
     * @param  modifier the paragraph style modifier.
     * @return this text buffer.
     */
    public TextBuffer modifyParagraphStyle(ParagraphStyleModifier modifier) {
	if (modifier == null)
	    throw new NullPointerException();
	ParagraphStyle pStyle = getCurrentParagraphStyle();
	if (pStyle != null) {
	    setParagraphStyle(modifier.modify(pStyle));
	}
	return this;
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the new font name.
     *
     * @param  name the font name for the new text style.
     * @return this text buffer.
     * @see    #modifyTextStyle(jp.kyasu.graphics.TextStyleModifier)
     */
    public TextBuffer setFontName(String name) {
	if (name == null)
	    throw new NullPointerException();
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.NAME, name);
	return modifyTextStyle(modifier);
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the new font style.
     *
     * @param  style the font style for the new text style.
     * @return this text buffer.
     * @see    #modifyTextStyle(jp.kyasu.graphics.TextStyleModifier)
     */
    public TextBuffer setFontStyle(int style) {
	BasicTSModifier modifier = new BasicTSModifier();
	if ((style & Font.BOLD) != 0)
	    modifier.put(BasicTSModifier.BOLD, true);
	else
	    modifier.put(BasicTSModifier.BOLD, BasicTSModifier.NULL);
	if ((style & Font.ITALIC) != 0)
	    modifier.put(BasicTSModifier.ITALIC, true);
	else
	    modifier.put(BasicTSModifier.ITALIC, BasicTSModifier.NULL);
	return modifyTextStyle(modifier);
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the new font size.
     *
     * @param  size the font size for the new text style.
     * @return this text buffer.
     * @see    #modifyTextStyle(jp.kyasu.graphics.TextStyleModifier)
     */
    public TextBuffer setFontSize(int size) {
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.SIZE, size);
	return modifyTextStyle(modifier);
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the new font color.
     *
     * @param  color the font color for the new text style.
     * @return this text buffer.
     * @see    #modifyTextStyle(jp.kyasu.graphics.TextStyleModifier)
     */
    public TextBuffer setColor(Color color) {
	BasicTSModifier modifier = new BasicTSModifier();
	if (color != null) {
	    modifier.put(BasicTSModifier.COLOR, color);
	}
	else {
	    modifier.put(BasicTSModifier.COLOR, BasicTSModifier.NULL);
	}
	return modifyTextStyle(modifier);
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the underline.
     *
     * @param  underline the new text style is underlined.
     * @return this text buffer.
     * @see    #modifyTextStyle(jp.kyasu.graphics.TextStyleModifier)
     */
    public TextBuffer setUnderline(boolean underline) {
	BasicTSModifier modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.UNDERLINE, underline);
	return modifyTextStyle(modifier);
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the plain font style.
     *
     * @return this text buffer.
     * @see    #setFontStyle(int)
     */
    public TextBuffer setFontPlain() {
	return setFontStyle(Font.PLAIN);
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the bold font style.
     *
     * @return this text buffer.
     * @see    #setFontStyle(int)
     */
    public TextBuffer setFontBold() {
	return setFontStyle(Font.BOLD);
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the italic font style.
     *
     * @return this text buffer.
     * @see    #setFontStyle(int)
     */
    public TextBuffer setFontItalic() {
	return setFontStyle(Font.ITALIC);
    }

    /**
     * Modifies the current text style of this text buffer by replicating
     * the current text style with the bold and italic font style.
     *
     * @return this text buffer.
     * @see    #setFontStyle(int)
     */
    public TextBuffer setFontBoldItalic() {
	return setFontStyle(Font.BOLD | Font.ITALIC);
    }

    /**
     * Appends the text representation of the object argument to
     * this text buffer. The argument is converted to a text with the
     * current text style.
     *
     * @param obj an object.
     * @see   #append(java.lang.String)
     */
    public TextBuffer append(Object obj) {
	return append(String.valueOf(obj));
    }

    /**
     * Appends the text representation of the string contents of the
     * reader argument to this text buffer. The argument is converted to
     * a text with the current text style.
     *
     * @param     reader a reader.
     * @see       jp.kyasu.graphics.Text#copySystemToJavaReadWriter(java.io.Reader, java.io.Writer)
     * @exception java.io.IOException If an I/O error occurs.
     */
    public TextBuffer append(java.io.Reader reader) throws java.io.IOException
    {
	if (reader == null) {
	    return append(new Text(String.valueOf(null), lastStyle));
	}
	CharArrayWriter writer = new CharArrayWriter(8 * 1024);
	Text.copySystemToJavaReadWriter(reader, writer);
	char buffer[] = writer.getBuffer();
	int bufferLength = writer.size();
	return append(new Text(buffer, bufferLength, lastStyle));
    }

    /**
     * Appends the text representation of the string argument to
     * this text buffer. The argument is converted to a text with the
     * current text style.
     *
     * @param str a string.
     * @see   #append(jp.kyasu.graphics.Text)
     */
    public TextBuffer append(String str) {
	if (str == null) {
	    str = String.valueOf(null);
	}
	return append(new Text(str, lastStyle));
    }

    /**
     * Appends the text representation of the <code>char</code> array
     * argument to this text buffer.
     * <p>
     * The characters of the array argument are appended, in order, to
     * the contents of this text buffer. The length of this text
     * buffer increases by the length of the argument.
     *
     * @param  str the characters to be appended.
     * @return this text buffer.
     */
    public TextBuffer append(char str[]) {
	return append(str, 0, str.length);
    }

    /**
     * Appends the text representation of a subarray of the
     * <code>char</code> array argument to this text buffer.
     * <p>
     * The characters of the character array <code>str</code>, starting at
     * index <code>offset</code>, are appended, in order, to the contents
     * of this text buffer. The length of this text buffer increases
     * by the value of <code>len</code>.
     *
     * @param  str    the characters to be appended.
     * @param  offset the index of the first character to append.
     * @param  len    the number of characters to append.
     * @return this text buffer.
     */
    public TextBuffer append(char str[], int offset, int len) {
	if ((offset < 0) || (offset + len) > str.length || (len <= 0))
	    return this;
	copyWhenShared();
	text.string.append(str, offset, offset + len);
	text.runs.append(new RunArray(len, lastStyle, TextStyle.class));
	return this;
    }

    /**
     * Appends the text argument to this text buffer. The current text
     * style becomes the last style of the text argument.
     *
     * @param t a text.
     */
    public TextBuffer append(Text t) {
	if (t == null) {
	    t = new Text(String.valueOf(null), lastStyle);
	}
	else if (t.isEmpty()) {
	    return this;
	}
	else {
	    lastStyle = t.getTextStyleAt(t.length() - 1);
	}
	copyWhenShared();
	text.append(t);
	return this;
    }

    /**
     * Appends the text representation of the text attachment argument to
     * this text buffer. The argument is converted to a text with the
     * text attachment.
     *
     * @param ta a text attachment.
     * @see   #append(jp.kyasu.graphics.Text)
     */
    public TextBuffer append(TextAttachment ta) {
	if (ta == null) {
	    return append(new Text(String.valueOf(null), lastStyle));
	}
	return append(new Text(ta, lastStyle));
    }

    /**
     * Appends the text representation of the visual object argument to
     * this text buffer. The argument is converted to a text with a
     * text attachment that wraps the visual object.
     *
     * @param v a visual object.
     * @see   #append(jp.kyasu.graphics.TextAttachment)
     */
    public TextBuffer append(Visualizable v) {
	if (v == null) {
	    return append(new Text(String.valueOf(v), lastStyle));
	}
	return append(new TextAttachment(v));
    }

    /**
     * Appends the text representation of the boolean argument to this
     * text buffer. The argument is converted to a text with the current
     * text style.
     *
     * @param b a boolean.
     * @see   #append(java.lang.String)
     */
    public TextBuffer append(boolean b) {
	return append(String.valueOf(b));
    }

    /**
     * Appends the text representation of the char argument to this
     * text buffer. The argument is converted to a text with the current
     * text style.
     *
     * @param c a char.
     * @see   #append(java.lang.String)
     */
    public TextBuffer append(char c) {
	return append(String.valueOf(c));
    }

    /**
     * Appends the text representation of the int argument to this
     * text buffer. The argument is converted to a text with the current
     * text style.
     *
     * @param i an int.
     * @see   #append(java.lang.String)
     */
    public TextBuffer append(int i) {
	return append(String.valueOf(i));
    }

    /**
     * Appends the text representation of the long argument to this
     * text buffer. The argument is converted to a text with the current
     * text style.
     *
     * @param l a long.
     * @see   #append(java.lang.String)
     */
    public TextBuffer append(long l) {
	return append(String.valueOf(l));
    }

    /**
     * Appends the text representation of the float argument to this
     * text buffer. The argument is converted to a text with the current
     * text style.
     *
     * @param f a float.
     * @see   #append(java.lang.String)
     */
    public TextBuffer append(float f) {
	return append(String.valueOf(f));
    }

    /**
     * Appends the text representation of the double argument to this
     * text buffer. The argument is converted to a text with the current
     * text style.
     *
     * @param d a double.
     * @see   #append(java.lang.String)
     */
    public TextBuffer append(double d) {
	return append(String.valueOf(d));
    }

    /**
     * Converts to a string representing the data in this text buffer.
     *
     * @return a string representation of the text buffer.
     */
    public String toString() {
	return text.toString();
    }

    /**
     * Converts to a system string representing the data in this text buffer.
     *
     * @return a system string representation of the text buffer.
     * @see    jp.kyasu.graphics.Text#getSystemString(java.lang.String, java.lang.String)
     */
    public String toSystemString() {
	return toSystemString(System.getProperty("line.separator", "\n"));
    }

    /**
     * Converts to a system string representing the data in this text buffer,
     * with the specified separator string.
     *
     * @param  separator the separator string for the platform.
     * @return a string representation of the text buffer.
     * @see    jp.kyasu.graphics.Text#getSystemString(java.lang.String, java.lang.String)
     */
    public String toSystemString(String separator) {
	if (separator == null)
	    throw new NullPointerException();
	return Text.getSystemString(text.toString(), separator);
    }

    /**
     * Converts to a text representing the data in this text buffer.
     * Subsequent changes to the text buffer do not affect the contents
     * of the returned <code>Text</code>.
     *
     * @return a text representation of the text buffer.
     */
    public Text toText() {
	shared = true;
	return text;
    }

    /**
     * Converts to a rich text representing the data in this text buffer.
     *
     * @param  richTextStyle the style for a new rich text.
     * @return a rich text representation of the text buffer.
     */
    public RichText toRichText(RichTextStyle richTextStyle) {
	if (richTextStyle == null)
	    throw new NullPointerException();
	RichText richText = new RichText(text, richTextStyle);
	shared = true;
	if (!richTextStyle.isVariableLineHeight() || paragraphStyles.isEmpty())
	    return richText;

	int length = paragraphStyles.length();
	int index = ((Integer)paragraphStyles.get(0)).intValue();
	ParagraphStyle style = (ParagraphStyle)paragraphStyles.get(1);
	for (int i = 2; i < length; i += 2) {
	    int nextIndex = ((Integer)paragraphStyles.get(i)).intValue();
	    richText.setParagraphStyle(index, nextIndex, style);
	    index = nextIndex;
	    style = (ParagraphStyle)paragraphStyles.get(i + 1);
	}
	richText.setParagraphStyle(index, text.length() + 1, style);
	return richText;
    }

    /**
     * Writes to a system string representation of the text buffer to the
     * specified writer.
     *
     * @param     writer the writer to write to.
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void writeTo(java.io.Writer writer) throws java.io.IOException
    {
	writeTo(writer, System.getProperty("line.separator", "\n"));
    }

    /**
     * Writes to a system string representation of the text buffer to the
     * specified writer, with the specified separator string.
     *
     * @param     writer    the writer to write to.
     * @param     separator the separator string for the platform.
     * @exception java.io.IOException If an I/O error occurs.
     * @see       jp.kyasu.graphics.Text#copyJavaToSystemReadWriter(java.io.Reader, java.io.Writer, java.lang.String)
     */
    public void writeTo(java.io.Writer writer, String separator)
	throws java.io.IOException
    {
	java.io.Reader reader =
	    new java.io.CharArrayReader(text.getCharArray(),
					0,
					text.length());
	Text.copyJavaToSystemReadWriter(reader, writer, separator);
    }


    /** Copies the text when this text buffer is shared. */
    protected void copyWhenShared() {
	if (shared) {
	    text = (Text)text.clone();
	    shared = false;
	}
    }
}
