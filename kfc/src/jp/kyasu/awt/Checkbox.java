/*
 * Checkbox.java
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

import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.Visualizable;
import jp.kyasu.graphics.VAbstractButton;
import jp.kyasu.graphics.VCheckbox;
import jp.kyasu.graphics.VLabel;
import jp.kyasu.graphics.VText;

import java.awt.Color;

/**
 * The <code>Checkbox</code> class implements a graphical component that can
 * be in either an "on" (<code>true</code>) or "off" (<code>false</code>)
 * state. Clicking on a check box changes its state from "on" to "off," or
 * from "off" to "on."
 *
 * @see		jp.kyasu.awt.ToggleButton
 * @see		jp.kyasu.awt.ButtonController
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class Checkbox extends ToggleButton {

    /**
     * Creates a check box with no label. The state of this check box is set
     * to "off," and it is not part of any check box group.
     */
    public Checkbox() {
	this("", false, null);
    }

    /**
     * Creates a check box with the specified label. The state of this check
     * box is set to "off," and it is not part of any check box group.
     * @param label a string label for this check box.
     */
    public Checkbox(String label) {
	this(label, false, null);
    }

    /**
     * Creates a check box with the specified label. The state of this check
     * box is as specified by the <code>state</code> argument, and it is not
     * part of any check box group.
     * @param label a string label for this check box.
     * @param state the initial state of this check box.
     */
    public Checkbox(String label, boolean state) {
	this(label, state, null);
    }

    /**
     * Creates a check box with the specified label, in the specified check
     * box group, and set to the specified state.
     * @param label a string label for this check box.
     * @param state the initial state of this check box.
     * @param group a check box group for this check box, or <code>null</code>
     *              for no group.
     */
    public Checkbox(String label, boolean state, BooleanStateGroup group) {
	this(new Text(label == null ? "" : label), state, group);
    }

    /**
     * Constructs a check box with the specified label, set to the specified
     * state, and in the specified check box group.
     */
    public Checkbox(String label, BooleanStateGroup group, boolean state) {
	this(new Text(label == null ? "" : label), state, group);
    }

    /**
     * Creates a check box with the specified text. The state of this check
     * box is set to "off," and it is not part of any check box group.
     * @param text a text for this check box.
     */
    public Checkbox(Text text) {
	this(text, false, null);
    }

    /**
     * Creates a check box with the specified text. The state of this check
     * box is as specified by the <code>state</code> argument, and it is not
     * part of any check box group.
     * @param text  a text for this check box.
     * @param state the initial state of this check box.
     */
    public Checkbox(Text text, boolean state) {
	this(text, state, null);
    }

    /**
     * Creates a check box with the specified text, in the specified check
     * box group, and set to the specified state.
     * @param text  a text for this check box.
     * @param state the initial state of this check box.
     * @param group a check box group for this check box, or <code>null</code>
     *              for no group.
     */
    public Checkbox(Text text, boolean state, BooleanStateGroup group) {
	this(new VText(text), state, group);
    }

    /**
     * Creates a check box with the specified visual object. The state of this
     * check box is set to "off," and it is not part of any check box group.
     * @param visualizable a visual object for this check box.
     */
    public Checkbox(Visualizable visualizable) {
	this(visualizable, false, null);
    }

    /**
     * Creates a check box with the specified visual object. The state of this
     * check box is as specified by the <code>state</code> argument, and it is
     * not part of any check box group.
     * @param visualizable a visual object for this check box.
     * @param state        the initial state of this check box.
     */
    public Checkbox(Visualizable visualizable, boolean state) {
	this(visualizable, state, null);
    }

    /**
     * Creates a check box with the specified visual object, in the specified
     * check box group, and set to the specified state.
     * @param visualizable a visual object for this check box.
     * @param state        the initial state of this check box.
     * @param group        a check box group for this check box, or
     *                     <code>null</code> for no group.
     */
    public Checkbox(Visualizable visualizable, boolean state,
		    BooleanStateGroup group)
    {
	this((VAbstractButton)new VCheckbox(visualizable), state, group);
    }

    /**
     * Creates a check box with the specified visual check box, in the
     * specified check box group, and set to the specified state.
     * @param checkbox a visual check box for this check box.
     * @param state    the initial state of this check box.
     * @param group    a check box group for this check box, or
     *                 <code>null</code> for no group.
     */
    public Checkbox(VAbstractButton checkbox, boolean state,
		    BooleanStateGroup group)
    {
	super(checkbox, state, group);
	VCheckbox box = (VCheckbox)label;
	box.setStyle(group != null ? VCheckbox.EXCLUSIVE : VCheckbox.INCLUSIVE);
    }


    /**
     * Gets the background color for the check box.
     */
    protected Color getButtonBackground() {
	return getBackground();
    }


    // ======== java.awt.Choice APIs ========

    /**
     * Determines this check box's group.
     * @return this check box's group, or <code>null</code>
     *         if the check box is not part of a check box group.
     * @see #setCheckboxGroup(jp.kyasu.awt.CheckboxGroup)
     */
    public CheckboxGroup getCheckboxGroup() {
	return (CheckboxGroup)group;
    }

    /**
     * Sets this check box's group to be the specified check box group.
     * If this check box is already in a different check box group,
     * it is first taken out of that group.
     * @param g the new check box group, or <code>null</code> to remove this
     *          check box from any check box group.
     * @see #getCheckboxGroup()
     * @see #setBooleanStateGroup(jp.kyasu.awt.setBooleanStateGroup)
     */
    public void setCheckboxGroup(CheckboxGroup g) {
	setBooleanStateGroup(g);
    }

    /**
     * Sets this check box's group to be the specified group.
     */
    public void setBooleanStateGroup(BooleanStateGroup g) {
	super.setBooleanStateGroup(g);
	VCheckbox box = (VCheckbox)label;
	box.setStyle(group != null ? VCheckbox.EXCLUSIVE : VCheckbox.INCLUSIVE);
    }
}
