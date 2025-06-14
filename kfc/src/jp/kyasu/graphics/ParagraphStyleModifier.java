/*
 * ParagraphStyleModifier.java
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
 * An interface for modifying the paragraph style. The modification of the
 * paragraph style means the creation of the modified version of the
 * paragraph style, because the paragraph style is immutable.
 *
 * @see 	jp.kyasu.graphics.ParagraphStyle
 * @see 	jp.kyasu.graphics.BasicPSModifier
 *
 * @version 	20 Oct 1997
 * @author 	Kazuki YASUMATSU
 */
public interface ParagraphStyleModifier {

    /**
     * Modifies the given paragraph style, i.e., Creates the modified version
     * of the given paragraph style.
     *
     * @param  pStyle the given paragraph style.
     * @return the modified version of the given paragraph style;
     *         or the given paragraph style, if the modification has no
     *         effect on the given paragraph style.
     *
     * @see    jp.kyasu.graphics.TextBuffer#modifyParagraphStyle(jp.kyasu.graphics.ParagraphStyleModifier)
     * @see    jp.kyasu.graphics.ParagraphStyle#deriveStyle(jp.kyasu.graphics.ParagraphStyleModifier)
     * @see    jp.kyasu.graphics.RichText#modifyParagraphStyle(int, int, jp.kyasu.graphics.ParagraphStyleModifier)
     */
    ParagraphStyle modify(ParagraphStyle pStyle);

}
