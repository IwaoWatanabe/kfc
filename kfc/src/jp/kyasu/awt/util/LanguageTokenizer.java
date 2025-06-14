/*
 * LanguageTokenizer.java
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

import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextCharacterIterator;

import java.text.CharacterIterator;

/**
 * The <code>LangTokenizer</code> class is an abstract base class for all
 * objects that parse a text into "tokens". The text should represent a
 * program.
 *
 * @version 	10 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public abstract class LanguageTokenizer extends TextCharacterIterator
	implements java.io.Serializable
{
    /** The beginning index of the token, inclusive. */
    public int tokenBegin;

    /** The ending index of the token, exclusive. */
    public int tokenEnd;

    /** True, if the parsing context is in the multiline constant. */
    public boolean inMultiLineConstant;

    /** True, if the parsing context is in the multiline comment. */
    public boolean inMultiLineComment;


    /**
     * A constant indicating that the end of the array has been read.
     */
    static public final int EOT                = -1;

    /**
     * A constant indicating that a token has been read.
     */
    static public final int OTHER              = 0;

    /**
     * A constant indicating that a keyword token has been read.
     */
    static public final int KEYWORD            = 1;

    /**
     * A constant indicating that a constant token has been read.
     */
    static public final int CONSTANT           = 2;

    /**
     * A constant indicating that a multiline constant token has been read.
     */
    static public final int MULTILINE_CONSTANT = 3;

    /**
     * A constant indicating that a comment token has been read.
     */
    static public final int COMMENT            = 4;

    /**
     * A constant indicating that a multiline comment token has been read.
     */
    static public final int MULTILINE_COMMENT  = 5;


    /**
     * Returns the preferred beginning index to parse after the specified
     * text has been changed.
     * @param text         the text to be parsed.
     * @param tokenType    the token type.
     * @param tokenBegin   the beginning index of the token.
     * @param changeBefore the index before a change has been made.
     */
    static public int getPreferredParseBegin(Text text, int tokenType,
					     int tokenBegin, int changeBefore)
    {
	CharacterIterator iterator;
	switch (tokenType) {
	case MULTILINE_CONSTANT:
	case MULTILINE_COMMENT:
	    iterator = text.getCharacterIterator(tokenBegin, text.length(),
						 changeBefore);
	    for (char c = iterator.current();
		 c != CharacterIterator.DONE;
		 c = iterator.previous())
	    {
		if (c == '\n') {
		    return iterator.getIndex() + 1;
		}
	    }
	    break;
	case OTHER:
	    iterator = text.getCharacterIterator(tokenBegin, text.length(),
						 changeBefore);
	    for (char c = iterator.current();
		 c != CharacterIterator.DONE;
		 c = iterator.previous())
	    {
		if (c == ' ' || c == '\t' || c =='\n') {
		    return iterator.getIndex() + 1;
		}
	    }
	    break;
	default:
	    break;
	}
	return tokenBegin;
    }

    /**
     * Returns the preferred ending index to parse after the specified
     * text has been changed.
     * @param text        the text to be parsed.
     * @param tokenType   the token type.
     * @param tokenEnd    the ending index of the token.
     * @param changeAfter the index after a change has been made.
     */
    static public int getPreferredParseEnd(Text text, int tokenType,
					   int tokenEnd, int changeAfter)
    {
	CharacterIterator iterator = text.getCharacterIterator(changeAfter);
	for (char c = iterator.current();
	     c != CharacterIterator.DONE;
	     c = iterator.next())
	{
	    if (c == '\n') {
		return iterator.getIndex();
	    }
	}
	return text.length();

	/*
	switch (tokenType) {
	case MULTILINE_CONSTANT:
	case MULTILINE_COMMENT:
	case OTHER:
	    CharacterIterator iterator =
			text.getCharacterIterator(0, tokenEnd, changeAfter);
	    for (char c = iterator.current();
		 c != CharacterIterator.DONE;
		 c = iterator.next())
	    {
		if (c == '\n') {
		    return iterator.getIndex();
		}
	    }
	    break;
	default:
	    break;
	}
	return tokenEnd;
	*/
    }


    /**
     * Construct a <code>LanguageTokenizer</code> with the specified
     * text and range.
     *
     * @param text  the text to be parsed.
     * @param begin the beginning index to parse, inclusive.
     * @param end   the ending index to parse, exclusive.
     */
    public LanguageTokenizer(Text text, int begin, int end) {
	super(text, begin, end, begin);

	inMultiLineConstant = false;
	inMultiLineComment  = false;
	tokenBegin = -1;
	tokenEnd   = -1;
    }


    /**
     * Parses the next token from the text of this tokenizer.
     * @return the type of the token.
     * @see #tokenBegin
     * @see #tokenEnd
     * @see #EOT
     * @see #OTHER
     * @see #KEYWORD
     * @see #CONSTANT
     * @see #MULTILINE_CONSTANT
     * @see #COMMENT
     * @see #MULTILINE_COMMENT
     */
    public abstract int nextToken();


    protected final int read() {
	if (pos < end)
	    return (int)array[pos++];
	else
	    return -1;
    }

    protected final int peek() {
	if (pos < end)
	    return (int)array[pos];
	else
	    return -1;
    }

    protected final void pushBack() {
	if (pos > begin)
	    --pos;
    }

    protected final int getPosition() {
	return pos;
    }

    protected final void setPosition(int pos) {
	if (begin <= pos && pos < end)
	    this.pos = pos;
    }

    protected final boolean isLowerLetter(int c) {
	return ('a' <= c && c <= 'z');
    }

    protected final boolean isLetter(int c) {
	return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    protected final boolean isDigit(int c) {
	return ('0' <= c && c <= '9');
    }

    protected final boolean isLetterOrDigit(int c) {
	return isLetter(c) || isDigit(c);
    }
}
