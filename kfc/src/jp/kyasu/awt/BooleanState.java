/*
 * BooleanState.java
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

package jp.kyasu.awt;

/**
 * An interface for the object that has a boolean state.
 *
 * @see 	jp.kyasu.awt.BooleanState
 *
 * @version 	15 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public interface BooleanState {

    /**
     * Returns the boolean state of this object.
     */
    boolean getState();

    /**
     * Sets the state of this object to be the specified boolean state.
     */
    void setState(boolean state);

    /**
     * Helper method for <code>BooleanStateGroup.setSelectedBooleanState</code>.
     *
     * @see jp.kyasu.awt.BooleanStateGroup#setSelectedBooleanState(jp.kyasu.awt.BooleanState)
     */
    void setStateInternal(boolean b);

    /**
     * Determines the group of this boolean state.
     */
    BooleanStateGroup getBooleanStateGroup();
}
