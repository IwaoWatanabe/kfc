/*
 * CLikeLanguageTokenizer.java
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
 * The <code>CLikeLanguageTokenizer</code> class parses a text into
 * "tokens". The text should represent a program of <code>C</code>,
 * <code>C++</code>, or <code>Java</code>.
 *
 * @version 	10 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class CLikeLanguageTokenizer extends LanguageTokenizer {
    protected Set keywords;
    protected int keywordBegin;
    protected int keywordEnd;


    /**
     * The keywords of the Java language.
     */
    static public final Set JavaKeywords = new Set();

    /**
     * The keywords of the C language.
     */
    static public final Set CKeywords    = new Set();

    /**
     * The keywords of the C++ language.
     */
    static public final Set CPPKeywords  = new Set();


    /*
     * Initializes the keywords.
     */
    static {
	JavaKeywords.addElement("abstract");
	JavaKeywords.addElement("boolean");
	JavaKeywords.addElement("break");
	JavaKeywords.addElement("byte");
	//JavaKeywords.addElement("byvalue"); // not used
	JavaKeywords.addElement("case");
	//JavaKeywords.addElement("cast"); // not used
	JavaKeywords.addElement("catch");
	JavaKeywords.addElement("char");
	JavaKeywords.addElement("class");
	//JavaKeywords.addElement("const"); // not used
	JavaKeywords.addElement("continue");
	JavaKeywords.addElement("default");
	JavaKeywords.addElement("do");
	JavaKeywords.addElement("double");
	JavaKeywords.addElement("else");
	JavaKeywords.addElement("extends");
	JavaKeywords.addElement("false");
	JavaKeywords.addElement("final");
	JavaKeywords.addElement("finally");
	JavaKeywords.addElement("float");
	JavaKeywords.addElement("for");
	//JavaKeywords.addElement("future"); // not used
	//JavaKeywords.addElement("generic"); // not used
	//JavaKeywords.addElement("goto"); // not used
	JavaKeywords.addElement("if");
	JavaKeywords.addElement("implements");
	JavaKeywords.addElement("import");
	//JavaKeywords.addElement("inner"); // not used
	JavaKeywords.addElement("instanceof");
	JavaKeywords.addElement("int");
	JavaKeywords.addElement("interface");
	JavaKeywords.addElement("long");
	JavaKeywords.addElement("native");
	JavaKeywords.addElement("new");
	JavaKeywords.addElement("null");
	//JavaKeywords.addElement("operator"); // not used
	//JavaKeywords.addElement("outer"); // not used
	JavaKeywords.addElement("package");
	JavaKeywords.addElement("private");
	JavaKeywords.addElement("protected");
	JavaKeywords.addElement("public");
	//JavaKeywords.addElement("rest"); // not used
	JavaKeywords.addElement("return");
	JavaKeywords.addElement("short");
	JavaKeywords.addElement("static");
	JavaKeywords.addElement("super");
	JavaKeywords.addElement("switch");
	JavaKeywords.addElement("synchronized");
	JavaKeywords.addElement("this");
	JavaKeywords.addElement("throw");
	JavaKeywords.addElement("throws");
	JavaKeywords.addElement("transient");
	JavaKeywords.addElement("true");
	JavaKeywords.addElement("try");
	//JavaKeywords.addElement("var"); // not used
	JavaKeywords.addElement("void");
	JavaKeywords.addElement("volatile");
	JavaKeywords.addElement("while");

	CKeywords.addElement("auto");
	CKeywords.addElement("break");
	CKeywords.addElement("case");
	CKeywords.addElement("char");
	CKeywords.addElement("const");
	CKeywords.addElement("continue");
	CKeywords.addElement("default");
	CKeywords.addElement("do");
	CKeywords.addElement("double");
	CKeywords.addElement("else");
	CKeywords.addElement("enum");
	CKeywords.addElement("extern");
	CKeywords.addElement("float");
	CKeywords.addElement("for");
	CKeywords.addElement("goto");
	CKeywords.addElement("if");
	CKeywords.addElement("int");
	CKeywords.addElement("long");
	CKeywords.addElement("register");
	CKeywords.addElement("return");
	CKeywords.addElement("short");
	CKeywords.addElement("signed");
	CKeywords.addElement("sizeof");
	CKeywords.addElement("static");
	CKeywords.addElement("struct");
	CKeywords.addElement("switch");
	CKeywords.addElement("typedef");
	CKeywords.addElement("union");
	CKeywords.addElement("unsigned");
	CKeywords.addElement("void");
	CKeywords.addElement("volatile");
	CKeywords.addElement("while");

	CPPKeywords.addElement("asm");
	CPPKeywords.addElement("auto");
	CPPKeywords.addElement("break");
	CPPKeywords.addElement("case");
	CPPKeywords.addElement("char");
	CPPKeywords.addElement("class");
	CPPKeywords.addElement("const");
	CPPKeywords.addElement("continue");
	CPPKeywords.addElement("default");
	CPPKeywords.addElement("delete");
	CPPKeywords.addElement("do");
	CPPKeywords.addElement("double");
	CPPKeywords.addElement("else");
	CPPKeywords.addElement("enum");
	CPPKeywords.addElement("extern");
	CPPKeywords.addElement("float");
	CPPKeywords.addElement("for");
	CPPKeywords.addElement("friend");
	CPPKeywords.addElement("goto");
	CPPKeywords.addElement("if");
	CPPKeywords.addElement("inline");
	CPPKeywords.addElement("int");
	CPPKeywords.addElement("long");
	CPPKeywords.addElement("new");
	CPPKeywords.addElement("operator");
	CPPKeywords.addElement("overload");
	CPPKeywords.addElement("public");
	CPPKeywords.addElement("register");
	CPPKeywords.addElement("return");
	CPPKeywords.addElement("short");
	CPPKeywords.addElement("signed"); // not used?
	CPPKeywords.addElement("sizeof");
	CPPKeywords.addElement("static");
	CPPKeywords.addElement("struct");
	CPPKeywords.addElement("switch");
	CPPKeywords.addElement("this");
	CPPKeywords.addElement("typedef");
	CPPKeywords.addElement("union");
	CPPKeywords.addElement("unsigned");
	CPPKeywords.addElement("virtual");
	CPPKeywords.addElement("void");
	CPPKeywords.addElement("volatile"); // not used?
	CPPKeywords.addElement("while");
    }


    /**
     * Construct a <code>CLikeLanguageTokenizer</code> with the specified
     * text and range.
     *
     * @param text  the text to be parsed.
     * @param begin the beginning index to parse, inclusive.
     * @param end   the ending index to parse, exclusive.
     */
    public CLikeLanguageTokenizer(Text text, int begin, int end) {
	super(text, begin, end);

	keywords   = JavaKeywords;
	keywordBegin = -1;
	keywordEnd   = -1;
    }


    /**
     * Checks if this tokenizer parses the Java language.
     * @see #setJavaMode()
     */
    public boolean isJavaMode() {
	return keywords == JavaKeywords;
    }

    /**
     * Makes this tokenizer parse the Java language.
     * @see #isJavaMode()
     */
    public void setJavaMode() {
	keywords = JavaKeywords;
    }

    /**
     * Checks if this tokenizer parses the C language.
     * @see #setCMode()
     */
    public boolean isCMode() {
	return keywords == CKeywords;
    }

    /**
     * Makes this tokenizer parse the C language.
     * @see #isCMode()
     */
    public void setCMode() {
	keywords = CKeywords;
    }

    /**
     * Checks if this tokenizer parses the C++ language.
     * @see #setCPPMode()
     */
    public boolean isCPPMode() {
	return keywords == CPPKeywords;
    }

    /**
     * Makes this tokenizer parse the C++ language.
     * @see #isCPPMode()
     */
    public void setCPPMode() {
	keywords = CPPKeywords;
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
		else if (c == '*' && peek() == '/') {
		    read();
		    inMultiLineComment = false;
		    break;
		}
	    }
	    tokenEnd = getPosition();
	    return MULTILINE_COMMENT;
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
		while ((c = read()) != -1) {
		    switch (c) {
		    case '\n':
		    case '"':
			tokenEnd = getPosition();
			return CONSTANT;
		    case '\\':
			if (read() == '\n') {
			    pushBack();
			    tokenEnd = getPosition();
			    return CONSTANT;
			}
			break;
		    }
		}
		tokenEnd = getPosition();
		return CONSTANT;
	    case '\'':
		if (tokenType == OTHER) {
		    pushBack();
		    tokenEnd = getPosition();
		    return OTHER;
		}
		while ((c = read()) != -1) {
		    switch (c) {
		    case '\n':
		    case '\'':
			tokenEnd = getPosition();
			return CONSTANT;
		    case '\\':
			if (read() == '\n') {
			    pushBack();
			    tokenEnd = getPosition();
			    return CONSTANT;
			}
			break;
		    }
		}
		tokenEnd = getPosition();
		return CONSTANT;
	    case '/':
		switch (peek()) {
		case '/':
		    if (isCMode()) {
			break;
		    }
		    if (tokenType == OTHER) {
			pushBack();
			tokenEnd = getPosition();
			return OTHER;
		    }
		    read();
		    while ((c = read()) != -1) {
			if (c == '\n') {
			    break;
			}
		    }
		    tokenEnd = getPosition();
		    return COMMENT;
		case '*':
		    if (tokenType == OTHER) {
			pushBack();
			tokenEnd = getPosition();
			return OTHER;
		    }
		    read();
		    inMultiLineComment = true;
		    while ((c = read()) != -1) {
			if (c == '\n') {
			    break;
			}
			else if (c == '*' && peek() == '/') {
			    read();
			    inMultiLineComment = false;
			    break;
			}
		    }
		    tokenEnd = getPosition();
		    return MULTILINE_COMMENT;
		default:
		    break;
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
		else if (isLetter(c) || c == '_') {
		    int pos = getPosition() - 1;
		    boolean lower = isLowerLetter(c);
		    while ((c = read()) != -1) {
			if (!(isLetterOrDigit(c) || c == '_')) {
			    pushBack();
			    break;
			}
			if (lower) lower = isLowerLetter(c);
		    }
		    if (lower &&
			keywords.contains(getText().substring(pos, getPosition())))
		    {
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
