/*
 * ButtonController.java
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

import java.awt.event.*;

/**
 * The <code>ButtonController</code> class controlls the <code>Button</code>
 * component.
 *
 * @see 	jp.kyasu.awt.AbstractButton
 * @see 	jp.kyasu.awt.Button
 * @see 	jp.kyasu.awt.ToggleButton
 * @see 	jp.kyasu.awt.Checkbox
 *
 * @version 	20 Jul 1998
 * @author 	Kazuki YASUMATSU
 */
public class ButtonController
	implements MouseListener, MouseMotionListener, KeyListener,
		   FocusListener, java.io.Serializable
{
    /**
     * The button component.
     */
    protected AbstractButton button;

    /**
     * The controll mode.
     */
    protected int mode;

    /**
     * If true, emphasizes the button when the keyboard focus gained.
     */
    protected boolean focusEmphasizeEnabled;

    /**
     * The saved button state.
     */
    protected boolean savedState;


    /**
     * The action is performed when the mouse button is released.
     */
    static public final int TRIGGER_ON_UP   = 0;

    /**
     * The action is performed when the mouse button is pressed.
     */
    static public final int TRIGGER_ON_DOWN = 1;

    /**
     * The item state is changed when the mouse button is released.
     */
    static public final int TOGGLE_ON_UP    = 2;

    /**
     * The item state is changed when the mouse button is pressed.
     */
    static public final int TOGGLE_ON_DOWN  = 3;


    /**
     * Constructs a controller for the specified button component with the
     * <code>TRIGGER_ON_UP</code> mode.
     * @param button the button component.
     */
    public ButtonController(AbstractButton button) {
	this(button, TRIGGER_ON_UP);
    }

    /**
     * Constructs a controller for the specified button component with the
     * controll mode.
     * @param button the button component.
     * @param mode   the controll mode.
     * @see #TRIGGER_ON_UP
     * @see #TRIGGER_ON_DOWN
     * @see #TOGGLE_ON_UP
     * @see #TOGGLE_ON_DOWN
     */
    public ButtonController(AbstractButton button, int mode) {
	if (button == null)
	    throw new NullPointerException();
	this.button = button;
	setMode(mode);

	focusEmphasizeEnabled = true;

	button.addMouseListener(this);
	button.addMouseMotionListener(this);
	button.addKeyListener(this);
	button.addFocusListener(this);
    }


    /**
     * Returns true if the controller needs to request the keyboard focus.
     */
    public boolean needToRequestFocus() {
	return focusEmphasizeEnabled && !button.getVButton().canActivate();
    }

    /**
     * Returns the control mode.
     * @see #setMode(int)
     * @see #TRIGGER_ON_UP
     * @see #TRIGGER_ON_DOWN
     * @see #TOGGLE_ON_UP
     * @see #TOGGLE_ON_DOWN
     */
    public int getMode() {
	return mode;
    }

    /**
     * Sets the control mode.
     * @see #getMode()
     * @see #TRIGGER_ON_UP
     * @see #TRIGGER_ON_DOWN
     * @see #TOGGLE_ON_UP
     * @see #TOGGLE_ON_DOWN
     */
    public synchronized void setMode(int mode) {
	switch (mode) {
	case TRIGGER_ON_UP:
	case TRIGGER_ON_DOWN:
	case TOGGLE_ON_UP:
	case TOGGLE_ON_DOWN:
	    this.mode = mode;
	    return;
	}
	throw new IllegalArgumentException("improper mode: " + mode);
    }

    /**
     * Returns true, if the button is emphasized when the keyboard focus has
     * been gained; false otherwise.
     * @see #setFocusEmphasizeEnabled(boolean)
     */
    public boolean isFocusEmphasizeEnabled() {
	return focusEmphasizeEnabled;
    }

    /**
     * Enables the button to be emphasized when the keyboard focus has
     * been gained.
     * @see #isFocusEmphasizeEnabled()
     */
    public synchronized void setFocusEmphasizeEnabled(boolean b) {
	focusEmphasizeEnabled = b;
    }


    // ======== Listeners ========

    /**
     * Invoked when the mouse has been clicked on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when the mouse has been pressed on a component.
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(MouseEvent e) {
	if (!button.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	e.consume();

	if (needToRequestFocus()) {
	    button.requestFocus();
	}

	switch (mode) {
	case TRIGGER_ON_UP:
	case TRIGGER_ON_DOWN:
	    button.getVButton().setState(true);
	    button.repaintNow();
	    if (mode == TRIGGER_ON_DOWN && button.contains(e.getX(), e.getY()))
	    {
		button.actionPerformed();
	    }
	    break;

	case TOGGLE_ON_UP:
	case TOGGLE_ON_DOWN:
	    savedState = button.getVButton().getState();
	    button.getVButton().setState(!savedState);
	    button.repaintNow();
	    if (mode == TOGGLE_ON_DOWN && button.contains(e.getX(), e.getY()))
	    {
		button.itemStateChanged(button.getVButton().getState());
	    }
	    break;
	}
    }

    /**
     * Invoked when the mouse has been released on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(MouseEvent e) {
	if (!button.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	switch (mode) {
	case TOGGLE_ON_DOWN:
	    return;

	case TRIGGER_ON_UP:
	case TRIGGER_ON_DOWN:
	    if (button.getVButton().getState()) {
		e.consume();
		button.getVButton().setState(false);
		button.repaintNow();
		if (mode == TRIGGER_ON_UP && button.contains(e.getX(),e.getY()))
		{
		    button.actionPerformed();
		}
	    }
	    break;

	case TOGGLE_ON_UP:
	    if (button.getVButton().getState() != savedState) {
		e.consume();
		if (button.contains(e.getX(), e.getY())) {
		    button.itemStateChanged(button.getVButton().getState());
		}
	    }
	    break;
	}
    }

    /**
     * Invoked when the mouse enters a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseEntered(MouseEvent e) {
	if (!button.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (button.getVButton().canActivate() &&
	    !button.getVButton().isActive())
	{
	    button.getVButton().setActive(true);
	    button.repaintNow();
	}
    }

    /**
     * Invoked when the mouse exits a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseExited(MouseEvent e) {
	if (!button.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	if (button.getVButton().canActivate() &&
	    button.getVButton().isActive())
	{
	    button.getVButton().setActive(false);
	    button.repaintNow();
	}
    }

    /**
     * Invoked when the mouse button is pressed on a component and then dragged.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseDragged(MouseEvent e) {
	if (!button.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	switch (mode) {
	case TOGGLE_ON_DOWN:
	    return;

	case TRIGGER_ON_UP:
	case TRIGGER_ON_DOWN:
	    e.consume();
	    if (button.contains(e.getX(), e.getY())) {
		if (!button.getVButton().getState()) {
		    button.getVButton().setState(true);
		    button.repaintNow();
		}
	    }
	    else {
		if (button.getVButton().getState()) {
		    button.getVButton().setState(false);
		    button.repaintNow();
		}
	    }
	    break;

	case TOGGLE_ON_UP:
	    e.consume();
	    if (button.contains(e.getX(), e.getY())) {
		if (button.getVButton().getState() == savedState) {
		    button.getVButton().setState(!savedState);
		    button.repaintNow();
		}
	    }
	    else {
		if (button.getVButton().getState() != savedState) {
		    button.getVButton().setState(savedState);
		    button.repaintNow();
		}
	    }
	    break;
	}
    }

    /**
     * Invoked when the mouse button has been moved on a component.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseMoved(MouseEvent e) {}

    /**
     * Invoked when a key has been typed.
     * @see java.awt.event.KeyListener
     */
    public void keyTyped(KeyEvent e) {}

    /**
     * Invoked when a key has been pressed.
     * @see java.awt.event.KeyListener
     */
    public void keyPressed(KeyEvent e) {
	if (!button.isEnabled())
	    return;

	if (e.isConsumed())
	    return;

	switch (mode) {
	case TRIGGER_ON_UP:
	case TRIGGER_ON_DOWN:
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		e.consume();
		button.actionPerformed();
	    }
	    break;

	case TOGGLE_ON_UP:
	case TOGGLE_ON_DOWN:
	    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
		e.consume();
		button.getVButton().setState(!button.getVButton().getState());
		button.repaintNow();
		button.itemStateChanged(button.getVButton().getState());
	    }
	    break;
	}
    }

    /**
     * Invoked when a key has been released.
     * @see java.awt.event.KeyListener
     */
    public void keyReleased(KeyEvent e) {}

    /**
     * Invoked when a component gains the keyboard focus.
     * @see java.awt.event.FocusListener
     */
    public void focusGained(FocusEvent e) {
	//synchronized (button) {
	    if (!button.isEnabled())
		return;
	    //if (e.isTemporary()) return;
	    if (!needToRequestFocus())
		return;
	    if (!button.getVButton().isFocused()) {
		button.getVButton().setFocused(true);
		button.repaintNow();
	    }
	//}
    }

    /**
     * Invoked when a component loses the keyboard focus.
     * @see java.awt.event.FocusListener
     */
    public void focusLost(FocusEvent e) {
	//synchronized (button) {
	    if (!button.isEnabled())
		return;
	    //if (e.isTemporary()) return;
	    if (!needToRequestFocus())
		return;
	    if (button.getVButton().isFocused()) {
		button.getVButton().setFocused(false);
		button.repaintNow();
	    }
	//}
    }
}
