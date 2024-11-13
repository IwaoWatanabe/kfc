/*
 * VActiveButton.java
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

import java.awt.Graphics;

/**
 * The <code>VActiveButton</code> class implements the visual button that
 * acts as a button model. The active button has an active state. The
 * active button creates different visual presentations according to
 * the active state.
 *
 * @see		jp.kyasu.awt.AbstractButton
 * @see		jp.kyasu.awt.Button
 * @see		jp.kyasu.awt.ToggleButton
 * @see		jp.kyasu.graphics.VButton#TRIGGER
 * @see		jp.kyasu.graphics.VButton#TOGGLE
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class VActiveButton extends VButton {
    /** The active state. */
    protected boolean active;


    /**
     * Constructs a trigger button with the <code>false</code> state.
     */
    public VActiveButton() {
	this(false, TRIGGER);
    }

    /**
     * Constructs a trigger button with the specified state.
     *
     * @param state the state of the button.
     */
    public VActiveButton(boolean state) {
	this(state, TRIGGER);
    }

    /**
     * Constructs a button with the <code>false</code> state,
     * and the specified style.
     *
     * @param style the style of teh button.
     */
    public VActiveButton(int style) {
	this(false, style);
    }

    /**
     * Constructs a button with the specified state and the specified style.
     *
     * @param state the state of teh button.
     * @param style the style of teh button.
     */
    public VActiveButton(boolean state, int style) {
	this("", state, style);
    }

    /**
     * Constructs a trigger button with the <code>false</code> state,
     * and the specified string.
     *
     * @param str the string.
     */
    public VActiveButton(String str) {
	this(str, false, TRIGGER);
    }

    /**
     * Constructs a trigger button with the specified string and the
     * specified state.
     *
     * @param str   the string.
     * @param state the state of the button.
     */
    public VActiveButton(String str, boolean state) {
	this(str, state, TRIGGER);
    }

    /**
     * Constructs a button with the <code>false</code> state,
     * the specified string, and the specified style.
     *
     * @param str   the string.
     * @param style the style of the button.
     */
    public VActiveButton(String str, int style) {
	this(str, false, style);
    }

    /**
     * Constructs a button with the specified string, the specified state,
     * and the specified style.
     *
     * @param str   the string.
     * @param state the state of teh button.
     * @param style the style of teh button.
     */
    public VActiveButton(String str, boolean state, int style) {
	this(new Text(str), state, style);
    }

    /**
     * Constructs a trigger button with the <code>false</code> state,
     * and the specified text.
     *
     * @param text the text.
     */
    public VActiveButton(Text text) {
	this(text, false, TRIGGER);
    }

    /**
     * Constructs a trigger button with the specified text and the
     * specified state.
     *
     * @param text  the text.
     * @param state the state of the button.
     */
    public VActiveButton(Text text, boolean state) {
	this(text, state, TRIGGER);
    }

    /**
     * Constructs a button with the <code>false</code> state,
     * the specified text, and the specified style.
     *
     * @param text  the text.
     * @param style the style of the button.
     */
    public VActiveButton(Text text, int style) {
	this(text, false, style);
    }

    /**
     * Constructs a button with the specified text, the specified state,
     * and the specified style.
     *
     * @param text  the text.
     * @param state the state of teh button.
     * @param style the style of teh button.
     */
    public VActiveButton(Text text, boolean state, int style) {
	this(new VText(text), state, style);
    }

    /**
     * Constructs a trigger button with the <code>false</code> state,
     * and the specified visual object.
     *
     * @param visualizable the visual object.
     */
    public VActiveButton(Visualizable visualizable) {
	this(visualizable, false, TRIGGER);
    }

    /**
     * Constructs a trigger button with the specified visual object
     * and the specified state.
     *
     * @param visualizable the visual object.
     * @param state        the state of the button.
     */
    public VActiveButton(Visualizable visualizable, boolean state) {
	this(visualizable, state, TRIGGER);
    }

    /**
     * Constructs a button with the <code>false</code> state,
     * the specified visual object, and the specified style.
     *
     * @param visualizable the visual object.
     * @param style        the style of the button.
     */
    public VActiveButton(Visualizable visualizable, int style) {
	this(visualizable, false, style);
    }

    /**
     * Constructs a button with the specified visual object,
     * the specified state, and the specified style.
     *
     * @param visualizable the visual object.
     * @param state        the state of teh button.
     * @param style        the style of teh button.
     */
    public VActiveButton(Visualizable visualizable, boolean state, int style) {
	this(visualizable, true, false, state, style);
    }

    /**
     * Constructs a button with the specified visual object,
     * states, and style.
     *
     * @param visualizable the visual object.
     * @param enabled      the enabled state of teh button.
     * @param focused      the focused state of teh button.
     * @param state        the state of teh button.
     * @param style        the style of teh button.
     */
    protected VActiveButton(Visualizable visualizable, boolean enabled,
			    boolean focused, boolean state, int style)
    {
	super(visualizable, enabled, focused, state, style,
	      new V3DButtonBorder(!state, style, true));
	active = false;
    }


    /**
     * Creates a new button by replicating this button with a new visual
     * object associated with it.
     *
     * @param  visualizable the visual object for the new button.
     * @return a new button.
     */
    public VLabel deriveLabel(Visualizable visualizable) {
	if (visualizable == null)
	    throw new NullPointerException();
	VActiveButton vbutton =
	   new VActiveButton(visualizable, enabled, focused, state, getStyle());
	vbutton.setActive(active);
	return (VLabel)vbutton;
    }

    /**
     * Sets the enabled state to be the specified boolean.
     *
     * @param b the boolean.
     */
    public void setEnabled(boolean b) {
	super.setEnabled(b);
	if (!state) {
	    setActive(false);
	}
    }

    /**
     * Checks if the button has the active state and presentation.
     * @see jp.kyasu.graphics.VAbstractButton#canActivate()
     */
    public boolean canActivate() {
	return true;
    }

    /**
     * Checks if the button is active.
     * @see jp.kyasu.graphics.VAbstractButton#isActive()
     */
    public boolean isActive() {
	if (getStyle() == TOGGLE && state) {
	    return true;
	}
	return active;
    }

    /**
     * Activates the button.
     * @see jp.kyasu.graphics.VAbstractButton#setActive(boolean)
     */
    public void setActive(boolean b) {
	active = b;
    }

    /**
     * Returns a clone of this button.
     */
    public Object clone() {
	VActiveButton vbutton = (VActiveButton)super.clone();
	vbutton.active = active;
	return vbutton;
    }


    /**
     * Paints the border of the button with the specified dimension
     * at the specified location.
     */
    protected void paintBorder(Graphics g, int x, int y, int width, int height)
    {
	if (isActive()) {
	    super.paintBorder(g, x, y, width, height);
	}
    }
}
