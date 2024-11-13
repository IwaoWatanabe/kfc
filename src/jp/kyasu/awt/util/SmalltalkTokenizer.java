/*
 * SmalltalkTokenizer.java
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
import jp.kyasu.util.Set;

/**
 * The <code>SmalltalkTokenizer</code> class parses a text into "tokens".
 * The text should represent a Smalltalk program.
 *
 * @version 	10 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class SmalltalkTokenizer extends LanguageTokenizer {
    protected int keywordBegin;
    protected int keywordEnd;


    /**
     * The keywords of the Smalltalk languages.
     */
    static public final Set Keywords = new Set();

    static {
	Keywords.addElement("true");
	Keywords.addElement("false");
	Keywords.addElement("nil");
	Keywords.addElement("self");
	Keywords.addElement("super");
	Keywords.addElement("thisContext");
    }


    /**
     * Construct a <code>SmalltalkTokenizer</code> with the specified
     * text and range.
     *
     * @param text  the text to be parsed.
     * @param begin the beginning index to parse, inclusive.
     * @param end   the ending index to parse, exclusive.
     */
    public SmalltalkTokenizer(Text text, int begin, int end) {
	super(text, begin, end);

	keywordBegin = -1;
	keywordEnd   = -1;
    }


    /**
     * Parses the next token from the text of this tokenizer.
     * @return the type of the token.
     * @see jp.kyasu.awt.util.LanguageTokenizer#EOT
     * @see jp.kyasu.awt.util.LanguageTokenizer#OTHER
     * @see jp.kyasu.awt.util.LanguageTokenizer#KEYWORD
     * @see jp.kyasu.awt.util.LanguageTokenizer#CONSTANT
     * @see jp.kyasu.awt.util.LanguageTokenizer#MULTILINE_CONSTANT
     * @see jp.kyasu.awt.util.LanguageTokenizer#COMMENT
     * @see jp.kyasu.awt.util.LanguageTokenizer#MULTILINE_COMMENT
     */
    public int nextToken() {
	int tokenType = EOT;
	tokenBegin = getPosition();
	int c;

	if (inMultiLineComment) {
	    if (peek() == -1) {
		tokenEnd = getPosition();
		return EOT;
	    }
	    while ((c = read()) != -1) {
		if (c == '\n') {
		    break;
		}
		else if (c == '"') {
		    inMultiLineComment = false;
		    break;
		}
	    }
	    tokenEnd = getPosition();
	    return MULTILINE_COMMENT;
	}

	if (inMultiLineConstant) {
	    if (peek() == -1) {
		tokenEnd = getPosition();
		return EOT;
	    }
	    while ((c = read()) != -1) {
		if (c == '\n') {
		    break;
		}
		else if (c == '\'') {
		    inMultiLineConstant = false;
		    break;
		}
	    }
	    tokenEnd = getPosition();
	    return MULTILINE_CONSTANT;
	}

	if (keywordBegin >= 0 && keywordEnd >= 0) {
	    tokenBegin = keywordBegin;
	    tokenEnd   = keywordEnd;
	    setPosition(tokenEnd);
	    keywordBegin = -1;
	    keywordEnd   = -1;
	    return KEYWORD;
	}

	for(;;) {
	    switch (c = read()) {
	    case -1:
		tokenEnd = getPosition();
		if (tokenType == OTHER) {
		    return OTHER;
		}
		else {
		    return EOT;
		}
	    case '"':
		if (tokenType == OTHER) {
		    pushBack();
		    tokenEnd = getPosition();
		    return OTHER;
		}
		inMultiLineComment = true;
		while ((c = read()) != -1) {
		    if (c == '\n') {
			break;
		    }
		    else if (c == '"') {
			inMultiLineComment = false;
			break;
		    }
		}
		tokenEnd = getPosition();
		return MULTILINE_COMMENT;
	    case '\'':
		if (tokenType == OTHER) {
		    pushBack();
		    tokenEnd = getPosition();
		    return OTHER;
		}
		inMultiLineConstant = true;
		while ((c = read()) != -1) {
		    if (c == '\n') {
			break;
		    }
		    else if (c == '\'') {
			inMultiLineConstant = false;
			break;
		    }
		}
		tokenEnd = getPosition();
		return MULTILINE_CONSTANT;
	    case '#':
		int p = peek();
		if (p ==  '\'') {
		    if (tokenType == OTHER) {
			pushBack();
			tokenEnd = getPosition();
			return OTHER;
		    }
		    read();
		    inMultiLineConstant = true;
		    while ((c = read()) != -1) {
			if (c == '\n') {
			    break;
			}
			else if (c == '\'') {
			    inMultiLineConstant = false;
			    break;
			}
		    }
		    tokenEnd = getPosition();
		    return MULTILINE_CONSTANT;
		}
		else if (isLetter(p)) {
		    if (tokenType == OTHER) {
			pushBack();
			tokenEnd = getPosition();
			return OTHER;
		    }
		    read();
		    while ((c = read()) != -1) {
			if (!isLetterOrDigit(c)) {
			    pushBack();
			    break;
			}
		    }
		    tokenEnd = getPosition();
		    return CONSTANT;
		}
		break;
	    case '$':
		if (tokenType == OTHER) {
		    pushBack();
		    tokenEnd = getPosition();
		    return OTHER;
		}
		if (read() == '\n') {
		    pushBack();
		}
		tokenEnd = getPosition();
		return CONSTANT;
	    case '^':
	    case '_':
		if (tokenType == OTHER) {
		    pushBack();
		    tokenEnd = getPosition();
		    return OTHER;
		}
		tokenEnd = getPosition();
		return KEYWORD;
	    case ':':
		if (peek() == '=') {
		    if (tokenType == OTHER) {
			pushBack();
			tokenEnd = getPosition();
			return OTHER;
		    }
		    read();
		    tokenEnd = getPosition();
		    return KEYWORD;
		}
		break;
	    default:
		if (isDigit(c)) {
		    if (tokenType == OTHER) {
			pushBack();
			tokenEnd = getPosition();
			return OTHER;
		    }
		    while ((c = read()) != -1) {
			if (!(isLetterOrDigit(c) || c == '.')) {
			    pushBack();
			    break;
			}
		    }
		    tokenEnd = getPosition();
		    return CONSTANT;
		}
		else if (isLetter(c)) {
		    int pos = getPosition() - 1;
		    while ((c = read()) != -1) {
			if (!isLetterOrDigit(c)) {
			    pushBack();
			    break;
			}
		    }
		    String token = getText().substring(pos, getPosition());
		    if (Keywords.contains(token)) {
			if (tokenType == OTHER) {
			    keywordBegin = pos;
			    keywordEnd   = getPosition();
			    setPosition(pos);
			    tokenEnd = getPosition();
			    return OTHER;
			}
			tokenEnd = getPosition();
			return KEYWORD;
		    }
		}
		break;
	    }

	    tokenType = OTHER;
	}
    }
}
