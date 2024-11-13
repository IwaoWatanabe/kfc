/*
 * VAbstractButton.java
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
 * The <code>VAbstractButton</code> class is the abstract base class
 * for the visual buttons that act as the button models. The button
 * has a boolean state that determins whether the button is pressed
 * or not.
 *
 * @see		jp.kyasu.awt.AbstractButton
 *
 * @version 	17 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public abstract class VAbstractButton extends VLabel {
    /** The button state. */
    protected boolean state;


    /**
     * Constructs a button with the specified visual object and the
     * specified states.
     *
     * @param visualizable the visual object.
     * @param enabled      the enabled state.
     * @param focused      the focused state.
     * @param state        the button state.
     */
    protected VAbstractButton(Visualizable visualizable,
			      boolean enabled, boolean focused, boolean state)
    {
	super(visualizable, enabled, focused);
	this.state = state;
    }


    /**
     * Returns the boolean state of the button.
     *
     * @return the boolean state of the button.
     */
    public boolean getState() {
	return state;
    }

    /**
     * Sets the button to the specifed boolean state.
     *
     * @param b the boolean state.
     */
    public void setState(boolean b) {
	state = b;
    }

    /**
     * Checks if the button has the active state and presentation.
     *
     * @return <code>true</code> if the button has the active state;
     *         <code>false</code> otherwise.
     */
    public boolean canActivate() {
	return false;
    }

    /**
     * Checks if the button is active.
     *
     * @return <code>true</code> if the button is active;
     *         <code>false</code> otherwise.
     */
    public boolean isActive() {
	return true;
    }

    /**
     * Activates the button.
     *
     * @param b if true, the button becomes active.
     */
    public void setActive(boolean b) {
	// do nothing
    }

    /**
     * Returns a clone of this button.
     */
    public Object clone() {
	VAbstractButton vbutton = (VAbstractButton)super.clone();
	vbutton.state = state;
	return vbutton;
    }
}
