/*
 * CheckboxGroup.java
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
 * The <code>CheckboxGroup</code> class is used to group together a
 * set of <code>Checkbox</code> buttons.
 * <p>
 * Exactly one check box button in a <code>CheckboxGroup</code> can
 * be in the "on" state at any given time. Pushing any button sets
 * its state to "on" and forces any other button that is in the "on"
 * state into the "off" state.
 *
 * @see		java.awt.CheckboxGroup
 *
 * @version 	15 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class CheckboxGroup extends BooleanStateGroup {

    /**
     * Creates a new instance of <code>CheckboxGroup</code>.
     */
    public CheckboxGroup() {
    }


    /**
     * Gets the current choice from this check box group. The current
     * choice is the check box in this group that is currently in the
     * "on" state, or <code>null</code> if all check boxes in the group
     * are off.
     *
     * @return the check box that is currently in the "on" state, or
     *         <code>null</code>.
     */
    public Checkbox getSelectedCheckbox() {
	return (Checkbox)getSelectedBooleanState();
    }

    /**
     * Sets the currently selected check box in this group to be the
     * specified check box. This method sets the state of that check
     * box to "on" and sets all other check boxes in the group to be off.
     * <p>
     * If the check box argument is <code>null</code> or belongs to a
     * different check box group, then this method does nothing.
     *
     * @param box the <code>Checkbox</code> to set as the current selection.
     */
    public void setSelectedCheckbox(Checkbox box) {
	super.setSelectedBooleanState(box);
    }

    /**
     * @deprecated As of JDK version 1.1.
     */
    public Checkbox getCurrent() {
	return getSelectedCheckbox();
    }

    /**
     * @deprecated As of JDK version 1.1.
     */
    public void setCurrent(Checkbox box) {
	setSelectedCheckbox(box);
    }
}
