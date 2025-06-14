/*
 * ToggleButton.java
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
import jp.kyasu.graphics.VButton;
import jp.kyasu.graphics.VText;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The <code>ToggleButton</code> class implements a labeled button that can
 * be in either an "on" (<code>true</code>) or "off" (<code>false</code>)
 * state. Clicking on a toggle button changes its state from "on" to "off,"
 * or from "off" to "on."
 *
 * @see 	jp.kyasu.awt.Checkbox
 * @see 	jp.kyasu.awt.ButtonController
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class ToggleButton extends AbstractButton
	implements ItemSelectable, BooleanState
{
    protected BooleanStateGroup group;
    transient protected ItemListener itemListener;


    /**
     * The default background color when the toggle button is selected.
     */
    static protected final Color TOGGLE_BACKGROUND =
	AWTResources.getResourceColor("kfc.button.toggleBackground",
				       new Color(224, 224, 224));


    /**
     * Creates a toggle button with empty label. The state of this toggle
     * button is set to "off," and it is not part of any group.
     */
    public ToggleButton() {
	this("", false, null);
    }

    /**
     * Creates a toggle button with the specified label. The state of this
     * toggle button is set to "off," and it is not part of any group.
     * @param label a string label for this toggle button.
     */
    public ToggleButton(String label) {
	this(label, false, null);
    }

    /**
     * Creates a toggle button with the specified label. The state of this
     * toggle button is as specified by the <code>state</code> argument, and
     * it is not part of any group.
     * @param label a string label for this toggle button.
     * @param state the initial state of this toggle button.
     */
    public ToggleButton(String label, boolean state) {
	this(label, state, null);
    }

    /**
     * Creates a toggle button with the specified label, in the specified
     * group, and set to the specified state.
     * @param label a string label for this toggle button.
     * @param state the initial state of this toggle button.
     * @param group a group for this toggle button.
     */
    public ToggleButton(String label, boolean state, BooleanStateGroup group) {
	this(new Text(label == null ? "" : label), state, group);
    }

    /**
     * Creates a toggle button with the specified text. The state of this
     * toggle button is set to "off," and it is not part of any group.
     * @param text a text for this toggle button.
     */
    public ToggleButton(Text text) {
	this(text, false, null);
    }

    /**
     * Creates a toggle button with the specified text. The state of this
     * toggle button is as specified by the <code>state</code> argument, and
     * it is not part of any group.
     * @param text  a text for this toggle button.
     * @param state the initial state of this toggle button.
     */
    public ToggleButton(Text text, boolean state) {
	this(text, state, null);
    }

    /**
     * Creates a toggle button with the specified text, in the specified
     * group, and set to the specified state.
     * @param text  a text label for this toggle button.
     * @param state the initial state of this toggle button.
     * @param group a group for this toggle button.
     */
    public ToggleButton(Text text, boolean state, BooleanStateGroup group) {
	this(new VText(text), state, group);
    }

    /**
     * Creates a toggle button with the specified visual object. The state of
     * this toggle button is set to "off," and it is not part of any group.
     * @param visualizable a visual object for this toggle button.
     */
    public ToggleButton(Visualizable visualizable) {
	this(visualizable, false, null);
    }

    /**
     * Creates a toggle button with the specified visual object. The state of
     * this toggle button is as specified by the <code>state</code> argument,
     * and it is not part of any group.
     * @param visualizable a visual object for this toggle button.
     * @param state        the initial state of this toggle button.
     */
    public ToggleButton(Visualizable visualizable, boolean state) {
	this(visualizable, state, null);
    }

    /**
     * Creates a toggle button with the specified visual object, in the
     * specified group, and set to the specified state.
     * @param visualizable a visual object label for this toggle button.
     * @param state        the initial state of this toggle button.
     * @param group        a group for this toggle button.
     */
    public ToggleButton(Visualizable visualizable, boolean state,
			BooleanStateGroup group)
    {
	this((VAbstractButton)new VButton(visualizable, VButton.TOGGLE),
	     state, group);
    }

    /**
     * Creates a toggle button with the specified visual button. The state of
     * this toggle button is set to "off," and it is not part of any group.
     * @param button a visual button label for this toggle button.
     */
    public ToggleButton(VAbstractButton button) {
	this(button, false, null);
    }

    /**
     * Creates a toggle button with the specified visual button. The state of
     * this toggle button is as specified by the <code>state</code> argument,
     * and it is not part of any group.
     * @param button a visual button label for this toggle button.
     * @param state  the initial state of this toggle button.
     */
    public ToggleButton(VAbstractButton button, boolean state) {
	this(button, state, null);
    }

    /**
     * Creates a toggle button with the specified visual button, in the
     * specified group, and set to the specified state.
     * @param button a visual button label for this toggle button.
     * @param state  the initial state of this toggle button.
     * @param group  a group for this toggle button.
     */
    public ToggleButton(VAbstractButton button, boolean state,
			BooleanStateGroup group)
    {
	super(button, ButtonController.TOGGLE_ON_UP);
	button.setState(state);
	this.group = group;
	if (state && group != null) {
	    group.setSelectedBooleanState(this);
	}
	itemListener = null;
    }


    /**
     * Gets the background color for the button.
     */
    protected Color getButtonBackground() {
	return (getVButton().getState() ? TOGGLE_BACKGROUND : getBackground());
    }

    // ======== java.awt.Choice APIs ========

    /**
     * Determines whether this toggle button/check box is in the "on" or
     * "off" state. The boolean value <code>true</code> indicates the "on"
     * state, and <code>false</code> indicates the "off" state.
     * @return the state of this toggle button/check box, as a boolean value.
     * @see #setState(boolean)
     */
    public boolean getState() {
	return getVButton().getState();
    }

    /**
     * Sets the state of this toggle button/check box to the specified state.
     * The boolean value <code>true</code> indicates the "on" state, and
     * <code>false</code> indicates the "off" state.
     * @param state the boolean state of the toggle button/check box.
     * @see #getState()
     */
    public void setState(boolean state) {
	if (group != null) {
	    if (state) {
		group.setSelectedBooleanState(this);
	    }
	    else if (group.getSelectedBooleanState() == this) {
		state = true;
	    }
	}
	setStateInternal(state);
    }

    /**
     * Helper function for <code>setState</code>.
     */
    public synchronized void setStateInternal(boolean state) {
	if (state == getVButton().getState())
	    return;
	getVButton().setState(state);
	repaintNow();
    }

    /**
     * Returns the an array (length 1) containing the toggle button/check box
     * label or null if the checkbox is not selected.
     */
    public Object[] getSelectedObjects() {
	if (getVButton().getState()) {
	    Object[] items = new Object[1];
	    items[0] = getLabel();
	    return items;
	}
	return null;
    }

    /**
     * Determines this toggle button/check box's group.
     * @return this toggle button/check box's group, or <code>null</code>
     *         if the check box is not part of a group.
     * @see #setBooleanStateGroup(jp.kyasu.awt.BooleanStateGroup)
     */
    public BooleanStateGroup getBooleanStateGroup() {
	return group;
    }

    /**
     * Sets this toggle button/check box's group to be the specified group.
     * If this toggle button/check box is already in a different group,
     * it is first taken out of that group.
     * @param g the new group, or <code>null</code> to remove this
     *          toggle button/check box from any group.
     * @see #getBooleanStateGroup()
     */
    public void setBooleanStateGroup(BooleanStateGroup g) {
	if (group != null) {
	    group.setSelectedBooleanState(null);
	}
	synchronized (this) {
	    group = g;
	}
    }

    /**
     * Adds the specified item listener to receive item events from
     * this toggle button/check box.
     * @param l the item listener.
     */
    public synchronized void addItemListener(ItemListener l) {
	itemListener = AWTEventMulticaster.add(itemListener, l);
	enableEvents(0); // mark newEventsOnly
    }

    /**
     * Removes the specified item listener so that the item listener
     * no longer receives item events from this check box.
     * @param l the item listener.
     */
    public synchronized void removeItemListener(ItemListener l) {
	itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /**
     * Invoked when the button has been triggered by the controller.
     */
    public void actionPerformed() {
	// do nothing
    }

    /**
     * Invoked when the button has been toggled by the controller.
     */
    public void itemStateChanged(boolean selected) {
	setState(selected);
	ItemEvent ie = new ItemEvent(this,
				     ItemEvent.ITEM_STATE_CHANGED,
				     getActionCommand(),
				     (selected ?
					ItemEvent.SELECTED :
					ItemEvent.DESELECTED));
	notifyItemListeners(ie);
    }

    protected void notifyItemListeners(ItemEvent e) {
	if (itemListener != null) {
	    if (isDirectNotification()) {
		itemListener.itemStateChanged(e);
	    }
	    else {
		e = new ItemEvent(
				e.getItemSelectable(),
				e.getID() + java.awt.AWTEvent.RESERVED_ID_MAX,
				e.getItem(),
				e.getStateChange());
		EventPoster.postEvent(e);
	    }
	}
	else {
	    postOldEvent(e);
	}
    }

    protected void processEvent(java.awt.AWTEvent e) {
	if (itemListener != null && (e instanceof ItemEvent)) {
	    ItemEvent ie = (ItemEvent)e;
	    if (ie.getID() > java.awt.AWTEvent.RESERVED_ID_MAX) {
		ie = new ItemEvent(
				ie.getItemSelectable(),
				ie.getID() - java.awt.AWTEvent.RESERVED_ID_MAX,
				ie.getItem(),
				ie.getStateChange());
		itemListener.itemStateChanged(ie);
		return;
	    }
	}
	super.processEvent(e);
    }


    /** Internal constant for serialization */
    static protected final String itemListenerK = "itemL".intern();

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	jp.kyasu.awt.ListenerSerializer.write(s,
					      itemListenerK,
					      itemListener);
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws ClassNotFoundException, java.io.IOException
    {
	s.defaultReadObject();
	Object keyOrNull;
	while ((keyOrNull = s.readObject()) != null) {
	    String key = ((String)keyOrNull).intern();
	    if (key == itemListenerK)
		addItemListener((ItemListener)s.readObject());
	    else // skip value for unrecognized key
		s.readObject();
	}
    }
}
