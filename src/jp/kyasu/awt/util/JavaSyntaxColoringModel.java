/*
 * JavaSyntaxColoringModel.java
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

import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;

import java.awt.Font;

/**
 * The <code>JavaSyntaxColoringModel</code> class implements the
 * <code>TextEditModel</code> interface.
 * The <code>JavaSyntaxColoringModel</code> object hilights the syntax of
 * <code>C</code>, <code>C++</code>, and <code>Java</code>.
 *
 * @version 	10 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class JavaSyntaxColoringModel extends FastSyntaxColoringModel {
    /** the syntax coloring language. */
    protected int language = JAVA_LANG;


    /** The syntax coloring of the Java language. */
    static protected final int JAVA_LANG = 0;

    /** The syntax coloring of the C language. */
    static protected final int C_LANG    = 1;

    /** The syntax coloring of the C++ language. */
    static protected final int CPP_LANG  = 2;


    class JavaTokenizerFactory implements LanguageTokenizerFactory {
	public LanguageTokenizer createLanguageTokenizer(Text text,
							 int begin, int end)
	{
	    CLikeLanguageTokenizer tokenizer =
				new CLikeLanguageTokenizer(text, begin, end);
	    switch (language) {
	    case C_LANG:
		tokenizer.setCMode();
		break;
	    case CPP_LANG:
		tokenizer.setCPPMode();
		break;
	    case JAVA_LANG:
	    default:
		tokenizer.setJavaMode();
		break;
	    }
	    return tokenizer;
	}
    }


    /**
     * Constructs an empty model.
     */
    public JavaSyntaxColoringModel() {
	this("");
    }

    /**
     * Constructs a model with the specified string.
     *
     * @param string the string.
     */
    public JavaSyntaxColoringModel(String string) {
	super();
	if (string == null)
	    throw new NullPointerException();
	factory = new JavaTokenizerFactory();
	syntaxColoringEnabled = true;
	setBaseFont(RichTextStyle.DEFAULT_CODE_STYLE.getTextStyle().getFont());
	setRichText(new RichText(string, RichTextStyle.DEFAULT_CODE_STYLE));
    }


    /**
     * Checks if this component colors the syntax of the Java language.
     * @see #setJavaMode()
     */
    public boolean isJavaMode() {
	return language == JAVA_LANG;
    }

    /**
     * Makes this component colors the syntax of the Java language.
     * @see #isJavaMode()
     */
    public void setJavaMode() {
	language = JAVA_LANG;
    }

    /**
     * Checks if this component colors the syntax of the C language.
     * @see #setCMode()
     */
    public boolean isCMode() {
	return language == C_LANG;
    }

    /**
     * Makes this component colors the syntax of the C language.
     * @see #isCMode()
     */
    public void setCMode() {
	language = C_LANG;
    }

    /**
     * Checks if this component colors the syntax of the C++ language.
     * @see #setCPPMode()
     */
    public boolean isCPPMode() {
	return language == CPP_LANG;
    }

    /**
     * Makes this component colors the syntax of the C++ language.
     * @see #isCPPMode()
     */
    public void setCPPMode() {
	language = CPP_LANG;
    }
}
