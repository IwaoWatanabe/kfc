/*
 * TextStyleModifier.java
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

/**
 * An interface for modifying the text style. The modification of the
 * text style means the creation of the modified version of the text style,
 * because the text style is immutable.
 *
 * @see		jp.kyasu.graphics.TextStyle
 * @see		jp.kyasu.graphics.BasicTSModifier
 *
 * @version 	20 Oct 1997
 * @author 	Kazuki YASUMATSU
 */
public interface TextStyleModifier {

    /**
     * Modifies the given text style, i.e., Creates the modified version
     * of the given text style.
     *
     * @param  tStyle the given text style.
     * @return the modified version of the given text style; or the given
     *         text style, if the modification has no effect on the given
     *         text style.
     *
     * @see    jp.kyasu.graphics.Text#modifyStyle(int, int, jp.kyasu.graphics.TextStyleModifier)
     * @see    jp.kyasu.graphics.TextBuffer#modifyTextStyle(jp.kyasu.graphics.TextStyleModifier)
     * @see    jp.kyasu.graphics.TextStyle#deriveStyle(jp.kyasu.graphics.TextStyleModifier)
     * @see    jp.kyasu.graphics.RichText#modifyTextStyle(int, int, jp.kyasu.graphics.TextStyleModifier)
     */
    public TextStyle modify(TextStyle tStyle);

}
