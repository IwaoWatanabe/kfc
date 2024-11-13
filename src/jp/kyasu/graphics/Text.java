/*
 * Text.java
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

import jp.kyasu.util.RunArray;
import jp.kyasu.util.VArray;

import java.text.CharacterIterator;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The <code>Text</code> class implements the growable string with style
 * (<code>TextStyle</code>) and visual items (<code>TextAttachment</code>).
 * <p>
 * In the <code>Text</code>, following three characters has special meaning:
 * <dl>
 * <dt><code>'\n'</code><dd>The line (paragraph) separator that breaks
 *                          the line and separates the paragraph.
 * <dt><code>'\r'</code><dd>The line break that breaks the line but not
 *                          separate the paragraph.
 * <dt><code>'\FFFE'</code><dd>The text attachment mark that indicates
 *                             the position of the text attachment.
 * </dl>
 * For example:
 * <pre>
 *     This is a paragraph 1.\n
 *     This is a paragraph 2\r
 *     continued to the next line.\n
 *     This is a paragraph 3 containing an image \FFFE.\n
 * </pre>
 *
 * @see 	jp.kyasu.graphics.TextStyle
 * @see 	jp.kyasu.graphics.TextAttachment
 *
 * @version 	15 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class Text implements Cloneable, java.io.Serializable {
    /** The growable string. */
    protected VArray string;

    /** The space-efficient text style array. */
    protected RunArray runs;

    /** The maps from the index of the text to the text attachment. */
    protected Hashtable attachments;


    /**
     * The line (paragraph) separator that breaks the line and
     * separates the paragraph.
     *
     * @see jp.kyasu.graphics.TextScanner
     * @see jp.kyasu.graphics.TextScanner#JAVA_STOPS
     */
    static public final char LINE_SEPARATOR_CHAR	= '\n';

    /**
     * The line break that breaks the line but not separate the
     * paragraph.
     *
     * @see jp.kyasu.graphics.TextScanner
     * @see jp.kyasu.graphics.TextScanner#JAVA_STOPS
     * @see jp.kyasu.graphics.TextScanner#JAVA_BREAK_STOPS
     */
    static public final char LINE_BREAK_CHAR		= '\r';

    /**
     * The list separator that separates the list item.
     *
     * @see jp.kyasu.graphics.TextScanner#LIST_STOPS
     */
    static public final char LIST_SEPARATOR_CHAR	= '\000';

    /**
     * The list column separator that separates the list column.
     *
     * @see jp.kyasu.graphics.TextScanner#LIST_STOPS
     * @see jp.kyasu.graphics.TextListScanner#LIST_COLUMN_STOPS
     */
    static public final char LIST_COL_SEPARATOR_CHAR	= '\001';

    /**
     * The text attachment mark that indicates the position of
     * the text attachment.
     *
     * @see jp.kyasu.graphics.TextScanner
     */
    static public final char ATTACHMENT_CHAR		= '\uFFFE';

    /**
     * The default maximum amount by which the capacity of the string
     * is automatically incremented.
     *
     * @see jp.kyasu.util.VArray
     */
    static protected final int DEFAULT_MAX_CAPACITY_INCREMENT = (512 * 1024);

    /**
     * Converts the platform string whose line-end is CR, LF, or CRLF to
     * the string whose internal representation is suitable for the text,
     * i.e., whose line-end is LF.
     *
     * @param  sysString the platform string.
     * @return the string whose internal representation is suitable for
     *         the text.
     * @see    #getJavaString(java.lang.String, int)
     */
    static public String getJavaString(String sysString) {
	return getJavaString(sysString, (int)LINE_SEPARATOR_CHAR);
    }

    /**
     * Converts the platform string whose line-end is CR, LF, or CRLF to
     * the string whose internal representation is suitable for the text,
     * i.e., whose line-end is the specified separator character.
     *
     * @param  sysString the platform string.
     * @param  separator the specified separator character.
     * @return the string whose internal representation is suitable for
     *         the text.
     * @see    #copySystemToJavaReadWriter(java.io.Reader, java.io.Writer, int)
     */
    static public String getJavaString(String sysString, int separatorChar) {
	java.io.Reader sysReader = new java.io.StringReader(sysString);
	java.io.Writer javaWriter = new java.io.StringWriter();
	try { copySystemToJavaReadWriter(sysReader, javaWriter, separatorChar);}
	catch (java.io.IOException e) {}
	return javaWriter.toString();
    }

    /**
     * Converts the string whose internal representation is suitable
     * for the text, i.e., whose line-end is LF and line-break is CR,
     * to the platform string whose line-end is CR, LF, or CRLF.
     *
     * @param  javaString the string whose internal representation is suitable
     *                    for the text.
     * @return the platform string.
     * @see    #getSystemString(java.lang.String, java.lang.String)
     */
    static public String getSystemString(String javaString) {
	return getSystemString(javaString,
			       System.getProperty("line.separator", "\n"));
    }

    /**
     * Converts the string whose internal representation is suitable
     * for the text, i.e., whose line-end is LF and line-break is CR,
     * to the platform string whose line-end is the specified line separator.
     *
     * @param  javaString the string whose internal representation is suitable
     *                    for the text.
     * @param  separator  the specified separator string.
     * @return the platform string.
     * @see    #copyJavaToSystemReadWriter(java.io.Reader, java.io.Writer, java.lang.String)
     */
    static public String getSystemString(String javaString, String separator) {
	java.io.Reader javaReader = new java.io.StringReader(javaString);
	java.io.Writer sysWriter = new java.io.StringWriter();
	try { copyJavaToSystemReadWriter(javaReader, sysWriter, separator); }
	catch (java.io.IOException e) {}
	return sysWriter.toString();
    }

    /**
     * Copies the contents of the specified reader into the specified writer
     * with a conversion from any one of line-ends (CR, LF, or CRLF) to LF.
     * As a result of the conversion, the contents of the writer become
     * suitable for the internal representation of the text.
     *
     * @param     sysReader  the specified reader.
     * @param     javaWriter the specified writer.
     * @exception java.io.IOException If an I/O error occurs.
     * @see       #copySystemToJavaReadWriter(java.io.Reader, java.io.Writer, int)
     */
    static public void copySystemToJavaReadWriter(java.io.Reader sysReader,
						  java.io.Writer javaWriter)
	throws java.io.IOException
    {
	copySystemToJavaReadWriter(sysReader, javaWriter,
				   (int)LINE_SEPARATOR_CHAR);
    }

    /**
     * Copies the contents of the specified reader into the specified writer
     * with a conversion from any one of line-ends (CR, LF, or CRLF) to LF.
     * As a result of the conversion, the contents of the writer become
     * suitable for the internal representation of the text whose line
     * separator equals to the specified separator character.
     *
     * @param     sysReader     the specified reader.
     * @param     javaWriter    the specified writer.
     * @param     separatorChar the specified separator character.
     * @exception java.io.IOException If an I/O error occurs.
     */
    static public void copySystemToJavaReadWriter(java.io.Reader sysReader,
						  java.io.Writer javaWriter,
						  int separatorChar)
	throws java.io.IOException
    {
	int c;
	while ((c = sysReader.read()) != -1) {
	    switch (c) {
	    case '\r':
		javaWriter.write(separatorChar);
		if ((c = sysReader.read()) != '\n' && c != -1)
		    javaWriter.write(c);
		break;
	    case '\n':
		javaWriter.write(separatorChar);
		break;
	    default:
		javaWriter.write(c);
		break;
	    }
	}
    }

    /**
     * Copies the contents of the specified reader into the specified writer
     * with a conversion from CR or LF to the system line separator.
     * As a result of the conversion, the contents of the writer become
     * suitable for the platform.
     *
     * @param     javaReader the specified reader.
     * @param     sysWriter  the specified writer.
     * @exception java.io.IOException If an I/O error occurs.
     * @see       #copyJavaToSystemReadWriter(java.io.Reader, java.io.Writer, java.lang.String)
     */
    static public void copyJavaToSystemReadWriter(java.io.Reader javaReader,
						  java.io.Writer sysWriter)
	throws java.io.IOException
    {
	copyJavaToSystemReadWriter(javaReader, sysWriter,
				   System.getProperty("line.separator", "\n"));
    }

    /**
     * Copies the contents of the specified reader into the specified writer
     * with a conversion from CR or LF to the specified separator.
     * As a result of the conversion, the contents of the writer become
     * suitable for the platform whose line separator equals to the
     * specified separator string.
     *
     * @param     javaReader the specified reader.
     * @param     sysWriter  the specified writer.
     * @param     separator  the specified separator string.
     * @exception java.io.IOException If an I/O error occurs.
     */
    static public void copyJavaToSystemReadWriter(java.io.Reader javaReader,
						  java.io.Writer sysWriter,
						  String separator)
	throws java.io.IOException
    {
	int c;
	while ((c = javaReader.read()) != -1) {
	    switch (c) {
	    case LINE_SEPARATOR_CHAR:
	    case LINE_BREAK_CHAR:
	    case LIST_SEPARATOR_CHAR:
		sysWriter.write(separator);
		break;
	    case LIST_COL_SEPARATOR_CHAR:
		sysWriter.write('\t');
		break;
	    case ATTACHMENT_CHAR:
		sysWriter.write(' ');
		break;
	    default:
		sysWriter.write(c);
		break;
	    }
	}
    }


    /**
     * Constructs an empty text.
     */
    public Text() {
	this(16);
    }

    /**
     * Constructs an empty text with the initial capacity.
     *
     * @param initialCapacity the initial capacity of the text.
     */
    public Text(int initialCapacity) {
	this(initialCapacity, DEFAULT_MAX_CAPACITY_INCREMENT);
    }

    /**
     * Constructs an empty text with the initial capacity and the
     * maximum capacity increment size.
     *
     * @param initialCapacity      the initial capacity of the text.
     * @param maxCapacityIncrement the maximum amount by which the capacity
     *                             is increased when the text overflows.
     */
    public Text(int initialCapacity, int maxCapacityIncrement) {
	string = new VArray(char.class, initialCapacity, maxCapacityIncrement);
	runs = new RunArray(TextStyle.class);
	attachments = null;
    }

    /**
     * Constructs a text with the contents of the string, whose every
     * style equals to the default style.
     *
     * @param str a string.
     */
    public Text(String str) {
	this(str, TextStyle.DEFAULT_STYLE);
    }

    /**
     * Constructs a text with the contents of the string, whose every
     * style equals to the specified style.
     *
     * @param str   a string.
     * @param style the style of the text.
     */
    public Text(String str, TextStyle style) {
	this(str, style, DEFAULT_MAX_CAPACITY_INCREMENT);
    }

    /**
     * Constructs a text with the contents of the string and the
     * maximum capacity increment size, whose every style equals to
     * the specified style.
     *
     * @param str                  a string.
     * @param style                the style of the text.
     * @param maxCapacityIncrement the maximum amount by which the capacity
     *                             is increased when the text overflows.
     */
    public Text(String str, TextStyle style, int maxCapacityIncrement) {
	if (str == null || style == null)
	    throw new NullPointerException();
	string      = new VArray(str, maxCapacityIncrement);
	runs        = new RunArray(str.length(), style, TextStyle.class);
	attachments = null;
    }

    /**
     * Constructs a text with the visual object, whose every style
     * equals to the default style.
     *
     * @param visualizable a visual object.
     */
    public Text(Visualizable visualizable) {
	this(new TextAttachment(visualizable));
    }

    /**
     * Constructs a text with the text attachment, whose every style
     * equals to the default style.
     *
     * @param ta a text attachment.
     */
    public Text(TextAttachment ta) {
	this(ta, TextStyle.DEFAULT_STYLE);
    }

    /**
     * Constructs a text with the text attachment, whose every style
     * equals to the specified style.
     *
     * @param ta    a text attachment.
     * @param style the style of the text.
     */
    public Text(TextAttachment ta, TextStyle style) {
	this(ta, style, DEFAULT_MAX_CAPACITY_INCREMENT);
    }

    /**
     * Constructs a text with the text attachment and the maximum
     * capacity increment size, whose every style equals to the
     * specified style.
     *
     * @param ta                   a text attachment.
     * @param style                the style of the text.
     * @param maxCapacityIncrement the maximum amount by which the capacity
     *                             is increased when the text overflows.
     */
    public Text(TextAttachment ta, TextStyle style, int maxCapacityIncrement) {
	this(new char[1], 1, style, maxCapacityIncrement);
	string.setChar(0, ATTACHMENT_CHAR);
	attachments = new Hashtable();
	attachments.put(new Integer(0), ta);
    }

    /**
     * Constructs a text with the array buffer and the preferred length,
     * whose every style equals to the specified style.
     *
     * @param array                the array buffer of the text.
     * @param arrayLength          the preferred length of the text.
     * @param style                the style of the text.
     */
    protected Text(char array[], int arrayLength, TextStyle style) {
	this(array, arrayLength, style, DEFAULT_MAX_CAPACITY_INCREMENT);
    }

    /**
     * Constructs a text with the array buffer, the preferred length,
     * and the maximum capacity increment size, whose every style equals
     * to the specified style.
     *
     * @param array                the array buffer of the text.
     * @param arrayLength          the preferred length of the text.
     * @param style                the style of the text.
     * @param maxCapacityIncrement the maximum amount by which the capacity
     *                             is increased when the text overflows.
     */
    protected Text(char array[], int arrayLength, TextStyle style,
		   int maxCapacityIncrement)
    {
	if (array == null)
	    throw new NullPointerException();
	string = new VArray(array, maxCapacityIncrement);
	if (arrayLength > array.length)
	    arrayLength = array.length;
	else if (arrayLength < array.length)
	    string.setLength(arrayLength);
	runs = new RunArray(arrayLength, style, TextStyle.class);
	attachments = null;
    }

    /**
     * Constructs a text with the string, the runs, and the attachments.
     *
     * @param string      the string of the text.
     * @param runs        the runs of the text.
     * @param attachments the attachments of the text.
     */
    protected Text(VArray string, RunArray runs, Hashtable attachments) {
	if (string == null || runs == null)
	    throw new NullPointerException();
	this.string      = string;
	this.runs        = runs;
	this.attachments = attachments;
    }


    /**
     * Returns the array of char in this text. An application should not
     * modify the returned array.
     *
     * @return the array of the char in this text.
     */
    protected final char[] getCharArray() {
	return (char[])string.getArray();
    }

    /**
     * Returns the character iterator for this text.
     *
     * @return the character iterator for this text.
     */
    public final CharacterIterator getCharacterIterator() {
	return new TextCharacterIterator(this);
    }

    /**
     * Returns the character iterator for this text, with the specified
     * initial index.
     *
     * @param pos initial iterator position.
     * @return the character iterator for this text.
     */
    public final CharacterIterator getCharacterIterator(int pos) {
	return new TextCharacterIterator(this, pos);
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
	return new TextCharacterIterator(this, begin, end, pos);
    }

    /**
     * Returns an enumeration of the text styles of this text.
     *
     * @return an enumeration of the text styles of this text.
     */
    public final Enumeration textStyles() {
	return runs.elements();
    }

    /**
     * Returns an enumeration of the text styles of this text.
     *
     * @param begin the beginning index to get styles, inclusive.
     * @param end   the ending index to get styles, exclusive.
     * @return an enumeration of the text styles of this text.
     */
    public final Enumeration textStyles(int begin, int end) {
	return runs.elements(begin, end);
    }

    /**
     * Returns the text styles as a run array in this text.
     * An application should not modify the returned run array.
     *
     * @return all text styles as a run array in this text.
     */
    public final RunArray getTextStyleRuns() {
	return runs;
    }

    /**
     * Returns the text style at the specified index.
     *
     * @param  index an index into this text.
     * @return the text style at the specified index.
     */
    public final TextStyle getTextStyleAt(int index) {
	return (TextStyle)runs.get(index);
    }

    /**
     * Returns the number of the text styles in this text.
     *
     * @return the number of the text styles in this text.
     */
    public final int getTextStyleCount() {
	return runs.getValueCount();
    }

    /**
     * Returns all text styles in this text.
     *
     * @return all text styles in this text.
     */
    public final TextStyle[] getTextStyles() {
	return (TextStyle[])runs.getValues();
    }

    /**
     * Returns the text styles in this text.
     *
     * @param  begin  the beginning index to get text styles, inclusive.
     * @param  end    the ending index to get text styles, exclusive.
     * @return the text styles in this text.
     */
    public final TextStyle[] getTextStyles(int begin, int end) {
	return (TextStyle[])runs.getValues(begin, end);
    }

    /**
     * Returns the run array representation of the text styles in this text.
     *
     * @param  begin  the beginning index to get text styles, inclusive.
     * @param  end    the ending index to get text styles, exclusive.
     * @return the run array representation of the text styles in this text.
     */
    public final RunArray getTextStyleArray(int begin, int end) {
	return runs.subarray(begin, end);
    }

    /**
     * Returns the number of the text attachments in this text.
     *
     * @return the number of the text attachments in this text.
     */
    public final int getAttachmentCount() {
	return (attachments == null ? 0 : attachments.size());
    }

    /**
     * Returns all text attachments in this text as a hashtable.
     * An application should not modify the returned array.
     *
     * @return the text attachments in this text, as a hashtable.
     */
    public final Hashtable getAttachments() {
	return attachments;
    }

    /**
     * Returns the text attachment at the specified index.
     *
     * @param  index an index into this text.
     * @return the text attachment if this text contains the text attachment
     *         at the specified index; <code>null</code> otherwise.
     */
    public final TextAttachment getAttachmentAt(int index) {
	return (attachments == null ?
			null :
			(TextAttachment)attachments.get(new Integer(index)));
    }

    /**
     * Sets the component at the specified index of this text to be
     * the specified text attachment.
     *
     * @param index the specified index.
     * @param ta    the specified text attachment.
     */
    public final void setAttachmentAt(int index, TextAttachment ta) {
	if (ta == null)
	    throw new NullPointerException();
	string.setChar(index, ATTACHMENT_CHAR);
	if (attachments == null)
	    attachments = new Hashtable();
	attachments.put(new Integer(index), ta);
    }

    /**
     * Returns the character at the specified index.
     *
     * @param  index an index into this text.
     * @return the character at the specified index.
     */
    public final char charAt(int index) {
	return string.getChar(index);
    }

    /**
     * Sets the component at the specified index of this text to be
     * the specified character.
     *
     * @param index the specified index.
     * @param c     the specified character.
     */
    public final void setCharAt(int index, char c) {
	string.setChar(index, c);
    }

    /**
     * Returns the character at the specified index.
     *
     * @param  index an index into this text.
     * @return the character at the specified index.
     */
    public final char getChar(int index) {
	return string.getChar(index);
    }

    /**
     * Sets the component at the specified index of this text to be
     * the specified character.
     *
     * @param index the specified index.
     * @param c     the specified character.
     */
    public final void setChar(int index, char c) {
	string.setChar(index, c);
    }

    /**
     * Copies characters from this text into the destination character array.
     *
     * @param     srcBegin   index of the first character in this text
     *                       to copy (inclusive).
     * @param     srcEnd     index after the last character in this text
     *                       to copy (exclusive).
     * @param     dst        the destination array.
     * @param     dstBegin   the start offset in the destination array.
     * @exception ArrayIndexOutOfBoundsException If srcBegin or srcEnd is out
     *            of range, or if srcBegin is greater than the srcEnd.
     */
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
	int length = length();
	if ((srcBegin < 0) || (srcBegin >= length)) {
	    throw new ArrayIndexOutOfBoundsException(srcBegin);
	}
	if ((srcEnd < 0) || (srcEnd > length)) {
	    throw new ArrayIndexOutOfBoundsException(srcEnd);
	}
	if (srcBegin < srcEnd) {
	    System.arraycopy(getCharArray(), srcBegin,
			     dst, dstBegin, srcEnd - srcBegin);
	}
    }

    /**
     * Tests if this text has no characters.
     *
     * @return <code>true</code> if this text has no characters;
     *         <code>false</code> otherwise.
     */
    public final boolean isEmpty() {
	return string.isEmpty();
    }

    /**
     * Returns the length of this text.
     *
     * @return the length of this text.
     */
    public final int length() {
	return string.length();
    }

    /**
     * Returns a hashcode for this text.
     */
    public int hashCode() {
	int h = 0;
	int off = 0;
	char val[] = (char[])string.getArray();
	int len = string.length();
	if (len < 16) {
	    for (int i = len; i > 0; i--) {
		h = (h * 37) + (int)val[off++];
	    }
	}
	else {
	    // only sample some characters
	    int skip = len / 8;
	    for (int i = len; i > 0; i -= skip, off += skip) {
		h = (h * 39) + (int)val[off];
	    }
	}
	h ^= runs.hashCode();
	if (attachments != null) {
	    h ^= attachments.size();
	}
	return h;
    }

    /**
     * Compares two objects for equality.
     */
    public boolean equals(Object anObject) {
	if (this == anObject)
	    return true;
	if (anObject == null)
	    return false;
	if (anObject instanceof Text) {
	    Text anotherText = (Text)anObject;
	    int n = length();
	    if (n == anotherText.length()) {
		char v1[] = getCharArray();
		char v2[] = anotherText.getCharArray();
		int i = 0;
		int j = 0;
		while (n-- != 0) {
		    if (v1[i++] != v2[j++]) {
			return false;
		    }
		}
		return runs.equals(anotherText.runs) &&
			(attachments == null ?
				anotherText.attachments == null :
				attachments.equals(anotherText.attachments));
	    }
	}
	return false;
    }

    /**
     * Compares two texts lexicographically. This operation does not
     * compare the styles or the text attachments (visual items).
     * The comparison is based on the Unicode value of each character in
     * the texts.
     *
     * @param  anotherText the <code>Text</code> to be compared.
     * @return the value <code>0</code> if the text argument is equal to
     *         this text; a value less than <code>0</code> if this text
     *         is lexicographically less than the text argument; and a
     *         value greater than <code>0</code> if this text is
     *         lexicographically greater than the text argument.
     */
    public int compareTo(Text anotherText) {
	return compareTo(anotherText.getCharArray(), anotherText.length());
    }

    /**
     * Compares this text with the string argument lexicographically.
     * The comparison is based on the Unicode value of each character in
     * the text and the string.
     *
     * @param  anotherString the <code>String</code> to be compared.
     * @return the value <code>0</code> if the string argument is equal to
     *         this text; a value less than <code>0</code> if this text
     *         is lexicographically less than the string argument; and a
     *         value greater than <code>0</code> if this text is
     *         lexicographically greater than the string argument.
     */
    public int compareTo(String anotherString) {
	char array[] = new char[anotherString.length()];
	anotherString.getChars(0, array.length, array, 0);
	return compareTo(array, array.length);
    }

    /**
     * Compares this text with the character array argument lexicographically.
     * The comparison is based on the Unicode value of each character in
     * the text and the character array.
     *
     * @param  array the character array to be compared.
     * @param  length the length of the character array to be compared.
     * @return the value <code>0</code> if the array argument is equal to
     *         this text; a value less than <code>0</code> if this text
     *         is lexicographically less than the array argument; and a
     *         value greater than <code>0</code> if this text is
     *         lexicographically greater than the array argument.
     */
    protected int compareTo(char array[], int length) {
	int len1 = length();
	int len2 = length;
	int n = Math.min(len1, len2);
	char v1[] = getCharArray();
	char v2[] = array;
	int i = 0;
	int j = 0;

	while (n-- != 0) {
	    char c1 = v1[i++];
	    char c2 = v2[j++];
	    if (c1 != c2) {
		return c1 - c2;
	    }
	}
	return len1 - len2;
    }

    /**
     * Tests if two text regions are equal. The comparison is based on
     * the Unicode value of each character in the texts.
     * <p>
     * If <code>toffset</code> or <code>ooffset</code> is negative, or
     * if <code>toffset</code>+<code>length</code> is greater than the
     * length of this text, or if <code>ooffset</code>+<code>length</code>
     * is greater than the length of the text argument, then this method
     * returns <code>false</code>.
     *
     * @param  toffset the starting offset of the subregion in this text.
     * @param  other   the text argument.
     * @param  ooffset the starting offset of the subregion in the text
     *                 argument.
     * @param  len     the number of characters to compare.
     * @return <code>true</code> if the specified subregion of this text
     *         exactly matches the specified subregion of the text argument;
     *         <code>false</code> otherwise.
     */
    public boolean regionMatches(int toffset, Text other, int ooffset, int len)
    {
	char ta[] = getCharArray();
	char oa[] = other.getCharArray();
	// Note: toffset, ooffset, or len might be near -1>>>1.
	if ((toffset < 0) || (toffset > length() - len) ||
	    (ooffset < 0) || (ooffset > other.length() - len))
	{
	    return false;
	}
	while (len-- > 0) {
	    if (ta[toffset++] != oa[ooffset++]) {
	        return false;
	    }
	}
	return true;
    }

    /**
     * Tests if the text region and the string are equal. The comparison
     * is based on the Unicode value of each character in the text and the
     * string.
     * <p>
     * If <code>toffset</code> or <code>ooffset</code> is negative, or
     * if <code>toffset</code>+<code>length</code> is greater than the
     * length of this text, or if <code>ooffset</code>+<code>length</code>
     * is greater than the length of the string argument, then this method
     * returns <code>false</code>.
     *
     * @param  toffset the starting offset of the subregion in this text.
     * @param  other   the string argument.
     * @param  ooffset the starting offset of the subregion in the string
     *                 argument.
     * @param  len     the number of characters to compare.
     * @return <code>true</code> if the specified subregion of this text
     *         exactly matches the specified subregion of the string argument;
     *         <code>false</code> otherwise.
     */
    public boolean regionMatches(int toffset, String other, int ooffset, int len)
    {
	char ta[] = getCharArray();
	// Note: toffset, ooffset, or len might be near -1>>>1.
	if ((toffset < 0) || (toffset > length() - len) ||
	    (ooffset < 0) || (ooffset > other.length() - len))
	{
	    return false;
	}
	while (len-- > 0) {
	    if (ta[toffset++] != other.charAt(ooffset++)) {
	        return false;
	    }
	}
	return true;
    }

    /**
     * Tests if two text regions are equal. The comparison is based on
     * the Unicode value of each character in the texts.
     * <p>
     * If <code>toffset</code> or <code>ooffset</code> is negative, or
     * if <code>toffset</code>+<code>length</code> is greater than the
     * length of this text, or if <code>ooffset</code>+<code>length</code>
     * is greater than the length of the text argument, then this method
     * returns <code>false</code>.
     *
     * @param  ignoreCase if <code>true</code>, ignore case when comparing
     *                    characters.
     * @param  toffset    the starting offset of the subregion in this text.
     * @param  other      the text argument.
     * @param  ooffset    the starting offset of the subregion in the text
     *                    argument.
     * @param  len        the number of characters to compare.
     * @return <code>true</code> if the specified subregion of this text
     *         matches the specified subregion of the text argument;
     *         <code>false</code> otherwise. Whether the matching is exact
     *         or case insensitive depends on the <code>ignoreCase</code>
     *         argument.
     */
    public boolean regionMatches(boolean ignoreCase, int toffset,
				 Text other, int ooffset, int len)
    {
	char ta[] = getCharArray();
	char oa[] = other.getCharArray();
	// Note: toffset, ooffset, or len might be near -1>>>1.
	if ((toffset < 0) || (toffset > length() - len) ||
	    (ooffset < 0) || (ooffset > other.length() - len))
	{
	    return false;
	}
	while (len-- > 0) {
	    char c1 = ta[toffset++];
	    char c2 = oa[ooffset++];
	    if (c1 == c2)
		continue;
	    if (ignoreCase) {
		// If characters don't match but case may be ignored,
		// try converting both characters to uppercase.
		// If the results match, then the comparison scan should
		// continue.
		char u1 = Character.toUpperCase(c1);
		char u2 = Character.toUpperCase(c2);
		if (u1 == u2)
		    continue;
		// Unfortunately, conversion to uppercase does not work properly
		// for the Georgian alphabet, which has strange rules about case
		// conversion.  So we need to make one last check before
		// exiting.
		if (Character.toLowerCase(u1) == Character.toLowerCase(u2))
		    continue;
	    }
	    return false;
	}
	return true;
    }

    /**
     * Tests if the text region and the string region are equal. The
     * comparison is based on the Unicode value of each character in the
     * texts.
     * <p>
     * If <code>toffset</code> or <code>ooffset</code> is negative, or
     * if <code>toffset</code>+<code>length</code> is greater than the
     * length of this text, or if <code>ooffset</code>+<code>length</code>
     * is greater than the length of the string argument, then this method
     * returns <code>false</code>.
     *
     * @param  ignoreCase if <code>true</code>, ignore case when comparing
     *                    characters.
     * @param  toffset    the starting offset of the subregion in this text.
     * @param  other      the string argument.
     * @param  ooffset    the starting offset of the subregion in the string
     *                    argument.
     * @param  len        the number of characters to compare.
     * @return <code>true</code> if the specified subregion of this text
     *         matches the specified subregion of the string argument;
     *         <code>false</code> otherwise. Whether the matching is exact
     *         or case insensitive depends on the <code>ignoreCase</code>
     *         argument.
     */
    public boolean regionMatches(boolean ignoreCase, int toffset,
				 String other, int ooffset, int len)
    {
	char ta[] = getCharArray();
	// Note: toffset, ooffset, or len might be near -1>>>1.
	if ((toffset < 0) || (toffset > length() - len) ||
	    (ooffset < 0) || (ooffset > other.length() - len))
	{
	    return false;
	}
	while (len-- > 0) {
	    char c1 = ta[toffset++];
	    char c2 = other.charAt(ooffset++);
	    if (c1 == c2)
		continue;
	    if (ignoreCase) {
		// If characters don't match but case may be ignored,
		// try converting both characters to uppercase.
		// If the results match, then the comparison scan should
		// continue.
		char u1 = Character.toUpperCase(c1);
		char u2 = Character.toUpperCase(c2);
		if (u1 == u2)
		    continue;
		// Unfortunately, conversion to uppercase does not work properly
		// for the Georgian alphabet, which has strange rules about case
		// conversion.  So we need to make one last check before
		// exiting.
		if (Character.toLowerCase(u1) == Character.toLowerCase(u2))
		    continue;
	    }
	    return false;
	}
	return true;
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * specified character.
     *
     * @param  ch a character.
     * @return the index of the first occurrence of the character in the
     *         character sequence represented by this object, or
     *         <code>-1</code> if the character does not occur.
     */
    public int indexOf(int ch) {
	return indexOf(ch, 0);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * specified character, starting the search at the specified index.
     *
     * @param  ch        a character.
     * @param  fromIndex the index to start the search from.
     * @return the index of the first occurrence of the character in the
     *         character sequence represented by this object that is greater
     *         than or equal to <code>fromIndex</code>, or <code>-1</code>
     *         if the character does not occur.
     */
    public int indexOf(int ch, int fromIndex) {
	int length = length();
	char v[] = getCharArray();

	if (fromIndex < 0) {
	    fromIndex = 0;
	}
	else if (fromIndex >= length) {
	    return -1;
	}
	for (int i = fromIndex; i < length; i++) {
	    if (v[i] == ch)
		return i;
	}
	return -1;
    }

    /**
     * Returns the index within this text of the last occurrence of the
     * specified character.
     * The Text is searched backwards starting at the last character.
     *
     * @param  ch a character.
     * @return the index of the last occurrence of the character in the
     *         character sequence represented by this object, or
     *         <code>-1</code> if the character does not occur.
     */
    public int lastIndexOf(int ch) {
	return lastIndexOf(ch, length() - 1);
    }

    /**
     * Returns the index within this text of the last occurrence of the
     * specified character, searching backward starting at the specified index.
     *
     * @param  ch        a character.
     * @param  fromIndex the index to start the search from.
     * @return the index of the last occurrence of the character in the
     *         character sequence represented by this object that is less
     *         than or equal to <code>fromIndex</code>, or <code>-1</code>
     *         if the character does not occur before that point.
     */
    public int lastIndexOf(int ch, int fromIndex) {
	int length = length();
	char v[] = getCharArray();

	int i = ((fromIndex >= length) ? length - 1 : fromIndex);
	for (; i >= 0; --i) {
	    if (v[i] == ch)
		return i;
	}
	return -1;
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * substring in the specified text.
     *
     * @param  text the subtext to search for.
     * @return if the text argument occurs as a substring within this
     *         object, then the index of the first character of the first
     *         such substring is returned; if it does not occur as a
     *         substring, <code>-1</code> is returned.
     */
    public int indexOf(Text text) {
	return indexOf(text, 0);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * substring of the specified text, starting at the specified index.
     *
     * @param  text      the subtext to search for.
     * @param  fromIndex the index to start the search from.
     * @return If the text argument occurs as a substring within this
     *         object at a starting index no smaller than
     *         <code>fromIndex</code>, then the index of the first character
     *         of the first such substring is returned. If it does not occur
     *         as a substring starting at <code>fromIndex</code> or beyond,
     *         <code>-1</code> is returned.
     */
    public int indexOf(Text text, int fromIndex) {
	return indexOf(false, text, fromIndex);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * substring in the specified text, ignoring case.
     *
     * @param  text the subtext to search for.
     * @return if the text argument occurs as a substring within this
     *         object, then the index of the first character of the first
     *         such substring is returned; if it does not occur as a
     *         substring, <code>-1</code> is returned.
     */
    public int indexOfIgnoreCase(Text text) {
	return indexOfIgnoreCase(text, 0);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * substring of the specified text, starting at the specified index,
     * ignoring case.
     *
     * @param  text      the subtext to search for.
     * @param  fromIndex the index to start the search from.
     * @return If the text argument occurs as a substring within this
     *         object at a starting index no smaller than
     *         <code>fromIndex</code>, then the index of the first character
     *         of the first such substring is returned. If it does not occur
     *         as a substring starting at <code>fromIndex</code> or beyond,
     *         <code>-1</code> is returned.
     */
    public int indexOfIgnoreCase(Text text, int fromIndex) {
	return indexOf(true, text, fromIndex);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * substring of the specified text, starting at the specified index.
     * If the <code>ignoreCase</code> is true, then ignore case when
     * comparing characters.
     *
     * @param  ignoreCase if <code>true</code>, ignore case when comparing
     *                    characters.
     * @param  text       the subtext to search for.
     * @param  fromIndex  the index to start the search from.
     * @return If the text argument occurs as a substring within this
     *         object at a starting index no smaller than
     *         <code>fromIndex</code>, then the index of the first character
     *         of the first such substring is returned. If it does not occur
     *         as a substring starting at <code>fromIndex</code> or beyond,
     *         <code>-1</code> is returned.
     */
    public int indexOf(boolean ignoreCase, Text text, int fromIndex) {
	return indexOf(ignoreCase, text.getCharArray(), text.length(),
		       fromIndex);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * specified substring.
     *
     * @param  str the substring to search for.
     * @return if the string argument occurs as a substring within this
     *         object, then the index of the first character of the first
     *         such substring is returned; if it does not occur as a
     *         substring, <code>-1</code> is returned.
     */
    public int indexOf(String str) {
	return indexOf(str, 0);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * specified substring, starting at the specified index.
     *
     * @param  str       the substring to search for.
     * @param  fromIndex the index to start the search from.
     * @return If the string argument occurs as a substring within this
     *         object at a starting index no smaller than
     *         <code>fromIndex</code>, then the index of the first character
     *         of the first such substring is returned. If it does not occur
     *         as a substring starting at <code>fromIndex</code> or beyond,
     *         <code>-1</code> is returned.
     */
    public int indexOf(String str, int fromIndex) {
	return indexOf(false, str, fromIndex);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * specified substring, ignoring case.
     *
     * @param  str the substring to search for.
     * @return if the string argument occurs as a substring within this
     *         object, then the index of the first character of the first
     *         such substring is returned; if it does not occur as a
     *         substring, <code>-1</code> is returned.
     */
    public int indexOfIgnoreCase(String str) {
	return indexOfIgnoreCase(str, 0);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * specified substring, starting at the specified index, ignoring case.
     *
     * @param  str       the substring to search for.
     * @param  fromIndex the index to start the search from.
     * @return If the string argument occurs as a substring within this
     *         object at a starting index no smaller than
     *         <code>fromIndex</code>, then the index of the first character
     *         of the first such substring is returned. If it does not occur
     *         as a substring starting at <code>fromIndex</code> or beyond,
     *         <code>-1</code> is returned.
     */
    public int indexOfIgnoreCase(String str, int fromIndex) {
	return indexOf(true, str, fromIndex);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * specified substring, starting at the specified index.
     * If the <code>ignoreCase</code> is true, then ignore case when
     * comparing characters.
     *
     * @param  ignoreCase if <code>true</code>, ignore case when comparing
     *                    characters.
     * @param  str        the substring to search for.
     * @param  fromIndex  the index to start the search from.
     * @return If the string argument occurs as a substring within this
     *         object at a starting index no smaller than
     *         <code>fromIndex</code>, then the index of the first character
     *         of the first such substring is returned. If it does not occur
     *         as a substring starting at <code>fromIndex</code> or beyond,
     *         <code>-1</code> is returned.
     */
    public int indexOf(boolean ignoreCase, String str, int fromIndex) {
	char array[] = new char[str.length()];
	str.getChars(0, array.length, array, 0);
	return indexOf(ignoreCase, array, array.length, fromIndex);
    }

    /**
     * Returns the index within this text of the rightmost occurrence of the
     * substring in the specified text. The rightmost empty string "" is
     * considered to occur at the index value <code>this.length()</code>.
     *
     * @param  text the subtext to search for.
     * @return if the text argument occurs one or more times as a substring
     *         within this object, then the index of the first character of
     *         the last such substring is returned. If it does not occur as
     *         a substring, <code>-1</code> is returned.
     */
    public int lastIndexOf(Text text) {
	return lastIndexOf(text, length() - 1);
    }

    /**
     * Returns the index within this text of the last occurrence of the
     * substring in the specified text.
     * The returned index indicates the start of the subtext, and it
     * must be equal to or less than <code>fromIndex</code>.
     *
     * @param  text      the subtext to search for.
     * @param  fromIndex the index to start the search from.
     * @return If the text argument occurs one or more times as a substring
     *         within this object at a starting index no greater than
     *         <code>fromIndex</code>, then the index of the first character of
     *         the last such substring is returned. If it does not occur as a
     *         substring starting at <code>fromIndex</code> or earlier,
     *         <code>-1</code> is returned.
     */
    public int lastIndexOf(Text text, int fromIndex) {
	return lastIndexOf(false, text, fromIndex);
    }

    /**
     * Returns the index within this text of the rightmost occurrence of the
     * substring in the specified text, ignoring case. The rightmost empty
     * string "" is considered to occur at the index value
     * <code>this.length()</code>.
     *
     * @param  text the subtext to search for.
     * @return if the text argument occurs one or more times as a substring
     *         within this object, then the index of the first character of
     *         the last such substring is returned. If it does not occur as
     *         a substring, <code>-1</code> is returned.
     */
    public int lastIndexOfIgnoreCase(Text text) {
	return lastIndexOfIgnoreCase(text, length() - 1);
    }

    /**
     * Returns the index within this text of the last occurrence of the
     * substring in the specified text, ignoring case.
     * The returned index indicates the start of the subtext, and it
     * must be equal to or less than <code>fromIndex</code>.
     *
     * @param  text      the subtext to search for.
     * @param  fromIndex the index to start the search from.
     * @return If the text argument occurs one or more times as a substring
     *         within this object at a starting index no greater than
     *         <code>fromIndex</code>, then the index of the first character of
     *         the last such substring is returned. If it does not occur as a
     *         substring starting at <code>fromIndex</code> or earlier,
     *         <code>-1</code> is returned.
     */
    public int lastIndexOfIgnoreCase(Text text, int fromIndex) {
	return lastIndexOf(true, text, fromIndex);
    }

    /**
     * Returns the index within this text of the last occurrence of the
     * substring in the specified text.
     * The returned index indicates the start of the subtext, and it
     * must be equal to or less than <code>fromIndex</code>.
     * If the <code>ignoreCase</code> is true, then ignore case when
     * comparing characters.
     *
     * @param  ignoreCase if <code>true</code>, ignore case when comparing
     *                    characters.
     * @param  text       the subtext to search for.
     * @param  fromIndex  the index to start the search from.
     * @return If the text argument occurs one or more times as a substring
     *         within this object at a starting index no greater than
     *         <code>fromIndex</code>, then the index of the first character of
     *         the last such substring is returned. If it does not occur as a
     *         substring starting at <code>fromIndex</code> or earlier,
     *         <code>-1</code> is returned.
     */
    public int lastIndexOf(boolean ignoreCase, Text text, int fromIndex) {
	return lastIndexOf(ignoreCase, text.getCharArray(), text.length(),
			   fromIndex);
    }

    /**
     * Returns the index within this text of the rightmost occurrence of the
     * specified substring. The rightmost empty string "" is considered to
     * occur at the index value <code>this.length()</code>.
     *
     * @param  str the substring to search for.
     * @return if the string argument occurs one or more times as a substring
     *         within this object, then the index of the first character of
     *         the last such substring is returned. If it does not occur as
     *         a substring, <code>-1</code> is returned.
     */
    public int lastIndexOf(String str) {
	return lastIndexOf(str, length() - 1);
    }

    /**
     * Returns the index within this text of the last occurrence of the
     * specified substring.
     * The returned index indicates the start of the substring, and it
     * must be equal to or less than <code>fromIndex</code>.
     *
     * @param  str       the substring to search for.
     * @param  fromIndex the index to start the search from.
     * @return If the string argument occurs one or more times as a substring
     *         within this object at a starting index no greater than
     *         <code>fromIndex</code>, then the index of the first character of
     *         the last such substring is returned. If it does not occur as a
     *         substring starting at <code>fromIndex</code> or earlier,
     *         <code>-1</code> is returned.
     */
    public int lastIndexOf(String str, int fromIndex) {
	return lastIndexOf(false, str, fromIndex);
    }

    /**
     * Returns the index within this text of the rightmost occurrence of the
     * specified substring, ignoring case. The rightmost empty string "" is
     * considered to occur at the index value <code>this.length()</code>.
     *
     * @param  str the substring to search for.
     * @return if the string argument occurs one or more times as a substring
     *         within this object, then the index of the first character of
     *         the last such substring is returned. If it does not occur as
     *         a substring, <code>-1</code> is returned.
     */
    public int lastIndexOfIgnoreCase(String str) {
	return lastIndexOfIgnoreCase(str, length() - 1);
    }

    /**
     * Returns the index within this text of the last occurrence of the
     * specified substring, ignoring case.
     * The returned index indicates the start of the substring, and it
     * must be equal to or less than <code>fromIndex</code>.
     *
     * @param  str       the substring to search for.
     * @param  fromIndex the index to start the search from.
     * @return If the string argument occurs one or more times as a substring
     *         within this object at a starting index no greater than
     *         <code>fromIndex</code>, then the index of the first character of
     *         the last such substring is returned. If it does not occur as a
     *         substring starting at <code>fromIndex</code> or earlier,
     *         <code>-1</code> is returned.
     */
    public int lastIndexOfIgnoreCase(String str, int fromIndex) {
	return lastIndexOf(true, str, fromIndex);
    }

    /**
     * Returns the index within this text of the last occurrence of the
     * specified substring.
     * The returned index indicates the start of the substring, and it
     * must be equal to or less than <code>fromIndex</code>.
     * If the <code>ignoreCase</code> is true, then ignore case when
     * comparing characters.
     *
     * @param  ignoreCase if <code>true</code>, ignore case when comparing
     *                    characters.
     * @param  str        the substring to search for.
     * @param  fromIndex  the index to start the search from.
     * @return If the string argument occurs one or more times as a substring
     *         within this object at a starting index no greater than
     *         <code>fromIndex</code>, then the index of the first character of
     *         the last such substring is returned. If it does not occur as a
     *         substring starting at <code>fromIndex</code> or earlier,
     *         <code>-1</code> is returned.
     */
    public int lastIndexOf(boolean ignoreCase, String str, int fromIndex) {
	char array[] = new char[str.length()];
	str.getChars(0, array.length, array, 0);
	return lastIndexOf(ignoreCase, array, array.length, fromIndex);
    }

    /**
     * Returns the index within this text of the first occurrence of the
     * specified character array whose length is the specified
     * <code>length</code>, starting at the specified index.
     * If the <code>ignoreCase</code> is true, then ignore case when
     * comparing characters.
     *
     * @param  ignoreCase if <code>true</code>, ignore case when comparing
     *                    characters.
     * @param  array      the character array to search for.
     * @param  length     the length of the character array.
     * @param  fromIndex  the index to start the search from.
     * @return If the array argument occurs as a substring within this
     *         object at a starting index no smaller than
     *         <code>fromIndex</code>, then the index of the first character
     *         of the first such substring is returned. If it does not occur
     *         as a substring starting at <code>fromIndex</code> or beyond,
     *         <code>-1</code> is returned.
     */
    protected int indexOf(boolean ignoreCase, char array[], int length,
			  int fromIndex)
    {
	if (fromIndex < 0) {
	    fromIndex = 0;
	}
	else if (fromIndex >= length()) {
	    return -1;
	}
	if (length == 0) {
	    return fromIndex;
	}
	char v1[] = getCharArray();
	char v2[] = array;
	int max = length() - length;
      test:
	for (int i = fromIndex; i <= max ; i++) {
	    int n = length;
	    int j = i;
	    int k = 0;
	    while (n-- > 0) {
		char c1 = v1[j++];
		char c2 = v2[k++];
		if (c1 == c2)
		    continue;
		if (ignoreCase) {
		    char u1 = Character.toUpperCase(c1);
		    char u2 = Character.toUpperCase(c2);
		    if (u1 == u2)
			continue;
		    if (Character.toLowerCase(u1) == Character.toLowerCase(u2))
			continue;
		}
		continue test;
	    }
	    return i;
	}
	return -1;
    }

    /**
     * Returns the index within this text of the last occurrence of
     * the specified character array whose length is the specified
     * <code>length</code>.
     * The returned index indicates the start of the substring, and it
     * must be equal to or less than <code>fromIndex</code>.
     * If the <code>ignoreCase</code> is true, then ignore case when
     * comparing characters.
     *
     * @param  ignoreCase if <code>true</code>, ignore case when comparing
     *                    characters.
     * @param  array      the character array to search for.
     * @param  length     the length of the character array.
     * @param  fromIndex  the index to start the search from.
     * @return If the array argument occurs one or more times as a substring
     *         within this object at a starting index no greater than
     *         <code>fromIndex</code>, then the index of the first character of
     *         the last such substring is returned. If it does not occur as a
     *         substring starting at <code>fromIndex</code> or earlier,
     *         <code>-1</code> is returned.
     */
    protected int lastIndexOf(boolean ignoreCase, char array[], int length,
			      int fromIndex)
    {
	if (fromIndex < 0) {
	    return -1;
	}
	else if (fromIndex > length() - length) {
	    fromIndex = length() - length;
	}
	if (length == 0) {
	    return fromIndex;
	}
	char v1[] = getCharArray();
	char v2[] = array;
      test:
	for (int i = fromIndex; i >= 0; --i) {
	    int n = length;
	    int j = i;
	    int k = 0;
	    while (n-- > 0) {
		char c1 = v1[j++];
		char c2 = v2[k++];
		if (c1 == c2)
		    continue;
		if (ignoreCase) {
		    char u1 = Character.toUpperCase(c1);
		    char u2 = Character.toUpperCase(c2);
		    if (u1 == u2)
			continue;
		    if (Character.toLowerCase(u1) == Character.toLowerCase(u2))
			continue;
		}
		continue test;
	    }
	    return i;
	}
	return -1;
    }

    /**
     * Removes all characters from this text and sets its length to zero.
     */
    public void removeAll() {
	string.removeAll();
	runs.removeAll();
	attachments = null;
    }

    /**
     * Removes the characters in this text from the specified
     * <code>offset</code>. The number of the characters to be removed is
     * specified by the <code>size</code>. Each character in this text
     * with an index greater or equal to <code>offset+size</code> is
     * shifted downward.
     *
     * @param offset the start index of the characters to be removed.
     * @param size   the number of the characters to be removed.
     */
    public void remove(int offset, int size) {
	string.remove(offset, size);
	runs.remove(offset, size);
	if (attachments != null) {
	    int end = offset + size;
	    Hashtable newAttachments = new Hashtable();
	    int len = attachments.size();
	    Enumeration k = attachments.keys();
	    Enumeration e = attachments.elements();
	    for (int i = 0; i < len; i++) {
		int index = ((Integer)k.nextElement()).intValue();
		TextAttachment ta = (TextAttachment)e.nextElement();
		if (index < offset)
		    newAttachments.put(new Integer(index), ta);
		else if (end <= index)
		    newAttachments.put(new Integer(index - size), ta);
	    }
	    if (newAttachments.isEmpty())
		attachments = null;
	    else
		attachments = newAttachments;
	}
    }

    /**
     * Returns a new string that is a substring of this text. The substring
     * begins at the specified index and extends to the end of this text.
     *
     * @param     beginIndex the beginning index, inclusive.
     * @return    the substring.
     */
    public String substring(int beginIndex) {
	return substring(beginIndex, length());
    }

    /**
     * Returns a new string that is a substring of this text. The substring
     * begins at the specified <code>beginIndex</code> and extends to the
     * character at index <code>endIndex-1</code>.
     *
     * @param     beginIndex the beginning index, inclusive.
     * @param     endIndex   the ending index, exclusive.
     * @return    the substring.
     */
    public String substring(int beginIndex, int endIndex) {
	VArray va = string.subarray(beginIndex, endIndex);
	return new String((char[])va.getArray(), 0, va.length());
    }

    /**
     * Returns a new text that is a subtext of this text. The subtext
     * begins at the specified index and extends to the end of this text.
     *
     * @param     beginIndex the beginning index, inclusive.
     * @return    the subtext.
     */
    public Text subtext(int beginIndex) {
	return subtext(beginIndex, length());
    }

    /**
     * Returns a new text that is a subtext of this text. The subtext
     * begins at the specified <code>beginIndex</code> and extends to the
     * character at index <code>endIndex-1</code>.
     *
     * @param     beginIndex the beginning index, inclusive.
     * @param     endIndex   the ending index, exclusive.
     * @return    the subtext.
     */
    public Text subtext(int beginIndex, int endIndex) {
	Hashtable subAttachments = null;
	if (attachments != null) {
	    subAttachments = new Hashtable();
	    int len = attachments.size();
	    Enumeration k = attachments.keys();
	    Enumeration e = attachments.elements();
	    for (int i = 0; i < len; i++) {
		int index = ((Integer)k.nextElement()).intValue();
		TextAttachment ta = (TextAttachment)e.nextElement();
		if (beginIndex <= index && index < endIndex)
		    subAttachments.put(new Integer(index - beginIndex),
				       ta.clone());
	    }
	    if (subAttachments.isEmpty())
		subAttachments = null;
	}
	return new Text(string.subarray(beginIndex, endIndex),
			runs.subarray(beginIndex, endIndex),
			subAttachments);
    }

    /**
     * Appends the character to this text.
     *
     * @param  c a character.
     * @return this text.
     */
    public Text append(char c) {
	string.append(c);
	// If runs isn't empty, duplicates last object; otherwise, adds default.
	runs.append(1, TextStyle.DEFAULT_STYLE);
	return this;
    }

    /**
     * Appends the characters of the <code>string</code> to this text.
     *
     * @param  str a string.
     * @return this text.
     */
    public Text append(String str) {
	return append(str, 0, str.length());
    }

    /**
     * Appends the characters of the <code>string</code> from the specified
     * <code>begin</code> index to the specified <code>endIndex-1</code> index.
     *
     * @param  str   a string.
     * @param  begin the beginning index of the string, inclusive.
     * @param  end   the ending index of the string, exclusive.
     * @return this text.
     */
    public Text append(String str, int begin, int end) {
	string.append(str, begin, end);
	// If runs isn't empty, duplicates last object; otherwise, adds default.
	runs.append(end - begin, TextStyle.DEFAULT_STYLE);
	return this;
    }

    /**
     * Appends the specified text to this text.
     *
     * @param  text a text.
     * @return this text.
     */
    public Text append(Text text) {
	string.append(text.getCharArray(), 0, text.length());
	runs.append(text.runs);
	if (text.attachments != null) {
	    int offset = length() - text.length();
	    if (attachments == null)
		attachments = new Hashtable();
	    int len = text.attachments.size();
	    Enumeration k = text.attachments.keys();
	    Enumeration e = text.attachments.elements();
	    for (int i = 0; i < len; i++) {
		int index = ((Integer)k.nextElement()).intValue();
		TextAttachment ta = (TextAttachment)e.nextElement();
		attachments.put(new Integer(offset + index), ta.clone());
	    }
	}
	return this;
    }

    /**
     * Appends the text attachment to this text.
     *
     * @param  ta a text attachment.
     * @return this text.
     */
    public Text append(TextAttachment ta) {
	if (ta == null)
	    return this;
	if (attachments == null)
	    attachments = new Hashtable();
	attachments.put(new Integer(length()), ta);
	append(ATTACHMENT_CHAR);
	return this;
    }

    /**
     * Inserts the characters of the specified string to this text from
     * the specified <code>offset</code>.
     *
     * @param  offset the start index of the characters to be inserted.
     * @param  str    a string.
     * @return this text.
     */
    public Text insert(int offset, String str) {
	return insert(offset, new Text(str));
    }

    /**
     * Inserts the components of the specified text to this text from
     * the specified <code>offset</code>.
     *
     * @param  offset the start index of the components to be inserted.
     * @param  text   a text.
     * @return this array.
     */
    public Text insert(int offset, Text text) {
	string.insert(offset, text.string);
	runs.insert(offset, text.runs);
	if (attachments != null) {
	    int diff = text.length();
	    Hashtable newAttachments = new Hashtable();
	    int len = attachments.size();
	    Enumeration k = attachments.keys();
	    Enumeration e = attachments.elements();
	    for (int i = 0; i < len; i++) {
		int index = ((Integer)k.nextElement()).intValue();
		TextAttachment ta = (TextAttachment)e.nextElement();
		if (index < offset)
		    newAttachments.put(new Integer(index), ta);
		else
		    newAttachments.put(new Integer(index + diff), ta);
	    }
	    attachments = newAttachments;
	}
	if (text.attachments != null) {
	    if (attachments == null)
		attachments = new Hashtable();
	    int len = text.attachments.size();
	    Enumeration k = text.attachments.keys();
	    Enumeration e = text.attachments.elements();
	    for (int i = 0; i < len; i++) {
		int index = ((Integer)k.nextElement()).intValue();
		TextAttachment ta = (TextAttachment)e.nextElement();
		attachments.put(new Integer(offset + index), ta.clone());
	    }
	}
	return this;
    }

    /**
     * Replaces the components of this text with the components of the
     * specified string.
     *
     * @param  begin  the beginning index to replace, inclusive.
     * @param  end    the ending index to replace, exclusive.
     * @param  str    a replacement string.
     * @return this text.
     */
    public Text replace(int begin, int end, String str) {
	return replace(begin, end, new Text(str));
    }

    /**
     * Replaces the components of this text with the components of the
     * specified text.
     *
     * @param  begin  the beginning index to replace, inclusive.
     * @param  end    the ending index to replace, exclusive.
     * @param  varray a replacement text.
     * @return this text.
     */
    public Text replace(int begin, int end, Text text) {
	string.replace(begin, end, text.string);
	runs.replace(begin, end, text.runs);
	if (attachments != null) {
	    int diff = text.length() - (end - begin);
	    Hashtable newAttachments = new Hashtable();
	    int len = attachments.size();
	    Enumeration k = attachments.keys();
	    Enumeration e = attachments.elements();
	    for (int i = 0; i < len; i++) {
		int index = ((Integer)k.nextElement()).intValue();
		TextAttachment ta = (TextAttachment)e.nextElement();
		if (index < begin)
		    newAttachments.put(new Integer(index), ta);
		else if (end <= index)
		    newAttachments.put(new Integer(index + diff), ta);
	    }
	    if (newAttachments.isEmpty())
		attachments = null;
	    else
		attachments = newAttachments;
	}
	if (text.attachments != null) {
	    if (attachments == null)
		attachments = new Hashtable();
	    int len = text.attachments.size();
	    Enumeration k = text.attachments.keys();
	    Enumeration e = text.attachments.elements();
	    for (int i = 0; i < len; i++) {
		int index = ((Integer)k.nextElement()).intValue();
		TextAttachment ta = (TextAttachment)e.nextElement();
		attachments.put(new Integer(begin + index), ta.clone());
	    }
	}
	return this;
    }

    /**
     * Replaces the style of this text with the specified style.
     *
     * @param  begin  the beginning index to replace, inclusive.
     * @param  end    the ending index to replace, exclusive.
     * @param  style  a replacement style.
     * @return this text.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>, or
     *            the <code>end</code> is out of range.
     */
    public Text replaceStyle(int begin, int end, TextStyle style) {
	if ((begin < 0) || (end > length()) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	runs.replace(begin, end,
		     new RunArray(end - begin, style, TextStyle.class));
	return this;
    }

    /**
     * Modifies the style of this text with the specified text style modifier.
     *
     * @param  begin    the beginning index to modify, inclusive.
     * @param  end      the ending index to modify, exclusive.
     * @param  modifier the text style modifier.
     * @return this text.
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>, or
     *            the <code>end</code> is out of range.
     */
    public Text modifyStyle(int begin, int end, TextStyleModifier modifier) {
	if ((begin < 0) || (end > length()) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	int index = begin;
	while (index < end) {
	    TextStyle textStyle = (TextStyle)runs.get(index);
	    int runEnd = index + runs.getRunLengthAt(index);
	    if (runEnd > end) runEnd = end;
	    TextStyle modStyle = modifier.modify(textStyle);
	    if (modStyle != textStyle) {
		runs.replace(index, runEnd,
			     new RunArray(runEnd - index, modStyle,
					  TextStyle.class));
	    }
	    index = runEnd;
	}
	return this;
    }

    /**
     * Modifies the style of this text to be based on the specified base
     * text style. The base text style is an attribute of the
     * <code>ParagraphStyle</code>.
     * <p>
     * An application should/can not perform this operation.
     *
     * @param  begin     the beginning index to base, inclusive.
     * @param  end       the ending index to base, exclusive.
     * @param  baseStyle the base text style.
     * @return this text.
     * @see    jp.kyasu.graphics.ParagraphStyle
     * @see    jp.kyasu.graphics.ModTextStyle
     * @see    jp.kyasu.graphics.RichText
     * @see    jp.kyasu.graphics.TextStyle#basedOn(jp.kyasu.graphics.TextStyle)
     * @exception ArrayIndexOutOfBoundsException if the <code>begin</code>, or
     *            the <code>end</code> is out of range.
     */
    protected Text baseStyleOn(int begin, int end, TextStyle baseStyle) {
	if ((begin < 0) || (end > length()) || (begin > end)) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	int index = begin;
	while (index < end) {
	    TextStyle textStyle = (TextStyle)runs.get(index);
	    int runEnd = index + runs.getRunLengthAt(index);
	    if (runEnd > end) runEnd = end;
	    TextStyle newStyle = textStyle.basedOn(baseStyle);
	    if (newStyle != textStyle) {
		runs.replace(index, runEnd,
			     new RunArray(runEnd - index, newStyle,
					  TextStyle.class));
	    }
	    index = runEnd;
	}
	return this;
    }

    /**
     * Returns the run length (the number of the constant occurrence)
     * of the style at the specified index.
     *
     * @param  index an index into this text.
     * @return the run length of the style from the specified index.
     */
    public int getRunLengthAt(int index) {
	return runs.getRunLengthAt(index);
    }

    /**
     * Returns the run offset (the starting index of the constant
     * occurrence) of the style at the specified index.
     *
     * @param  index an index into this text.
     * @return the run offset of the style from the specified index.
     */
    public int getRunOffsetAt(int index) {
	return runs.getRunOffsetAt(index);
    }

    /**
     * Returns a clone of this text. Contents of string and attachments are
     * shared.
     *
     * @return a clone of this text. Contents of string and attachments are
     * shared.
     */
    public Text cloneStyle() {
	try {
	    Text text = (Text)super.clone();
	    text.string      = string;
	    text.runs        = (RunArray)runs.clone();
	    text.attachments = attachments;
	    return text;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns a clone of this text.
     *
     * @return a clone of this text.
     */
    public Object clone() {
	try {
	    Text text = (Text)super.clone();
	    text.string = (VArray)string.clone();
	    text.runs   = (RunArray)runs.clone();
	    if (attachments == null) {
		text.attachments = null;
	    }
	    else {
		text.attachments = new Hashtable();
		int len = attachments.size();
		Enumeration k = attachments.keys();
		Enumeration e = attachments.elements();
		for (int i = 0; i < len; i++) {
		    Integer index = (Integer)k.nextElement();
		    TextAttachment ta = (TextAttachment)e.nextElement();
		    text.attachments.put(index, ta.clone());
		}
	    }
	    return text;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns a string representation of this text.
     */
    public String toString() {
	return new String(getCharArray(), 0, length());
    }

    /*
    public static void main(String argv[]) {
	Text text;
	Text rep = new Text("DEF");
	text = (new Text("abc def ghi")).replace(4, 7, rep);
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).replace(4, 7, new Text("DE"));
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).replace(4, 7, new Text("DEFG"));
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).replace(
				4, 7, new Text("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).replace(0, 3, rep);
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).replace(8, 11, rep);
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).replace(3, 3, rep);
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).replace(0, 0, rep);
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).replace(11, 11, rep);
	System.out.println(text.toString());
	System.out.println("--------");
	text = (new Text()).append(rep).append(rep);
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).insert(3, rep);
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).insert(0, rep);
	System.out.println(text.toString());
	text = (new Text("abc def ghi")).insert(11, rep);
	System.out.println(text.toString());
	text = new Text("abc def ghi"); text.remove(0, 3);
	System.out.println(text.toString());
	text = new Text("abc def ghi"); text.remove(4, 3);
	System.out.println(text.toString());
	text = new Text("abc def ghi"); text.remove(8, 3);
	System.out.println(text.toString());
	//System.out.println("--------");
	//text = new Text("abcdefghijklmnop");
	//long current = System.currentTimeMillis();
	//while (text.length() < (128 * 1024)) {
	//    text.append(text);
	//    System.out.println(System.currentTimeMillis() - current);
	//}
	System.exit(0);
    }
    */
}
