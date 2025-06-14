/*
 * LanguageTokenizerFactory.java
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

/**
 * The <code>LanguageTokenizerFactory</code> defines a factory for
 * <code>LanguageTokenizer</code>.
 *
 * @see 	jp.kyasu.awt.util.LanguageTokenizer
 *
 * @version 	10 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public interface LanguageTokenizerFactory extends java.io.Serializable {
    /**
     * Creates a new <code>LanguageTokenizer</code> to parse a text into
     * into "tokens".
     *
     * @param text  the text to be parsed.
     * @param begin the beginning index for parsing, inclusive.
     * @param end   the ending index for parsing, exclusive.
     * @return a new <code>LanguageTokenizer</code>.
     * @see    jp.kyasu.awt.util.LanguageTokenizer
     * @see    jp.kyasu.awt.util.SyntaxColoringWatcher
     */
    LanguageTokenizer createLanguageTokenizer(Text text, int begin, int end);
}
