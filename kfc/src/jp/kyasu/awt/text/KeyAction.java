/*
 * KeyAction.java
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

package jp.kyasu.awt.text;

/**
 * The interface for objects which perform an action when a key is typed.
 *
 * @see 	jp.kyasu.awt.text.CompositeKeyAction
 * @see 	jp.kyasu.awt.text.KeyBinding
 * @see 	jp.kyasu.awt.text.Keymap
 * @see 	jp.kyasu.awt.text.TextEditController
 *
 * @version 	01 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public interface KeyAction {
    /**
     * Returns the name of the key action object.
     */
    public String getName();

    /**
     * Performs the key action with the specified typed key character.
     *
     * @param keyChar the typed key character.
     */
    public void perform(char keyChar);
}
