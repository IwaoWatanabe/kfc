/*
 * BooleanStateGroup.java
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
 * The <code>BooleanStateGroup</code> class is used to create a
 * multiple-exclusion scope for a set of <code>BooleanState</code> objects.
 * <p>
 * Exactly one boolean state in a <code>BooleanStateGroup</code> can be
 * in the <code>true</code> state at any given time.
 *
 * @see 	jp.kyasu.awt.BooleanState
 *
 * @version 	15 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class BooleanStateGroup implements java.io.Serializable {
    /**
     * The current boolean state.
     */
    transient protected BooleanState selectedBooleanState;


    /**
     * Constructs a boolean state group.
     */
    public BooleanStateGroup() {
    }


    /**
     * Returns the current boolean state from this group. The current
     * boolean state in this group is currently in the <code>true</code>
     * state, or <code>null</code> if all boolean states in this group are
     * <code>false</code>.
     *
     * @return the boolean state that is currently in the <code>true</code>
     *         state, or <code>null</code>.
     */
    public BooleanState getSelectedBooleanState() {
	return selectedBooleanState;
    }

    /**
     * Sets the currently selected boolean state in this group to be the
     * specified boolean state. This method sets the state of that boolean
     * state to <code>true</code> and sets all other boolean state in this
     * group to be <code>false</code>.
     * <p>
     * If the boolean state argument is <code>null</code> or belongs to a
     * different group, then this method does nothing.
     *
     * @param state the <code>BooleanState</code> to set as the current state.
     */
    public synchronized void setSelectedBooleanState(BooleanState state) {
	if (state != null && state.getBooleanStateGroup() != this) {
	    return;
	}
	BooleanState oldState = selectedBooleanState;
	selectedBooleanState = state;
	if ((oldState != null) && (oldState != state)) {
	    oldState.setState(false);
	}
	if (state != null && oldState != state && !state.getState()) {
	    state.setStateInternal(true);
	}
    }
}
