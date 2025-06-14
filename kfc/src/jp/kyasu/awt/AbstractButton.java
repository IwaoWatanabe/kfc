/*
 * AbstractButton.java
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

import jp.kyasu.graphics.VAbstractButton;
import jp.kyasu.graphics.VActiveButton;
import jp.kyasu.graphics.VButton;
import jp.kyasu.graphics.VLabel;

import java.awt.Dimension;

/**
 * The <code>AbstractButton</code> class is an abstract base class for all
 * buttons such as <code>Button</code>, <code>ToggleButton</code>, and
 * <code>Checkbox</code>.
 * <p>
 * An AbstractButton is controlled by the <code>ButtonController</code> object.
 *
 * @see 	jp.kyasu.awt.ButtonController
 * @see 	jp.kyasu.awt.Button
 * @see 	jp.kyasu.awt.ToggleButton
 * @see 	jp.kyasu.awt.Checkbox
 *
 * @version 	07 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public abstract class AbstractButton extends Label {
    /**
     * The controller of the button.
     */
    protected ButtonController controller;

    protected String actionCommand;


    /**
     * Constructs a new button with the specified visual button and controll
     * mode.
     *
     * @param vbutton the visual button.
     * @param mode    the controll mode.
     * @see jp.kyasu.awt.ButtonController#TRIGGER_ON_UP
     * @see jp.kyasu.awt.ButtonController#TRIGGER_ON_DOWN
     * @see jp.kyasu.awt.ButtonController#TOGGLE_ON_UP
     * @see jp.kyasu.awt.ButtonController#TOGGLE_ON_DOWN
     */
    protected AbstractButton(VAbstractButton vbutton, int mode) {
	super((VLabel)vbutton, CENTER);
	controller = new ButtonController(this, mode);
	actionCommand = null;
	setVButtonInner(getVButton());
    }


    /**
     * Invoked when the button has been triggered by the controller.
     */
    public abstract void actionPerformed();

    /**
     * Invoked when the button has been toggled by the controller.
     */
    public abstract void itemStateChanged(boolean selected);

    /**
     * Tests if the button can be traversed using Tab or Shift-Tab keyboard
     * focus traversal.
     */
    public boolean isFocusTraversable() {
	if (focusTraversable) {
	    return controller.needToRequestFocus();
	}
	return false;
    }

    // ======== java.awt.Button/Checkbox compatible APIs ========

    /**
     * Sets the command name for the action event fired by this button.
     * By default this action command is set to match the label of the
     * button.
     * @param command A string used to set the button's action command.
     * @see #getActionCommand()
     */
    public synchronized void setActionCommand(String command) {
	actionCommand = command;
    }

    /**
     * Returns the command name of the action event fired by this button.
     * @see #setActionCommand(java.lang.String)
     */
    public String getActionCommand() {
	return (actionCommand == null ? getLabel() : actionCommand);
    }

    /**
     * Gets the label of this button.
     * @return the button's label.
     * @see #setLabel(java.lang.String)
     */
    public String getLabel() {
	return getText();
    }

    /**
     * Sets the button's label to be the specified string.
     * @param label the new label.
     * @see #getLabel()
     */
    public void setLabel(String label) {
	setText(label == null ? "" : label);
    }

    // ======== Enhanced APIs ========

    /**
     * Returns the controller of this button.
     */
    public ButtonController getController() {
	return controller;
    }

    /**
     * Returns the visual button of this button.
     * @return the visual button of this button.
     * @see #setVButton(jp.kyasu.graphics.VAbstractButton)
     */
    public VAbstractButton getVButton() {
	return (VAbstractButton)label;
    }

    /**
     * Sets the visual button of this button to the specified viaul button.
     * @param vbutton the visual button.
     * @see #getVButton()
     */
    public synchronized void setVButton(VAbstractButton vbutton) {
	super.setVLabel(vbutton);
	setVButtonInner(getVButton());
    }


    protected void setVButtonInner(VAbstractButton vbutton) {
	if (vbutton instanceof VButton) {
	    int mode = controller.getMode();
	    if (mode == ButtonController.TRIGGER_ON_UP ||
		mode == ButtonController.TRIGGER_ON_DOWN)
	    {
		((VButton)vbutton).setStyle(VButton.TRIGGER);
	    }
	    else if (mode == ButtonController.TOGGLE_ON_UP ||
		     mode == ButtonController.TOGGLE_ON_DOWN)
	    {
		((VButton)vbutton).setStyle(VButton.TOGGLE);
	    }
	}
	if (vbutton instanceof VActiveButton) {
	    setFocusTraversable(false);
	}
    }
}
