/*
 * ClickableTextAction.java
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

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The <code>ClickableTextAction</code> class is used for implementing
 * the clickable (sensible) text. The clickable text action has a command
 * string and action listeners. If the text with a text style containing
 * an action is clicked, the action event that has a command string of
 * the action is delivered to the action listeners associated with the
 * action.
 *
 * @see		jp.kyasu.graphics.TextStyle
 * @see		jp.kyasu.awt.TextComponent#isClickable()
 * @see		jp.kyasu.awt.TextComponent#setClickable(boolean)
 * @see		jp.kyasu.awt.text.BasicTextEditController#isClickable()
 * @see		jp.kyasu.awt.text.BasicTextEditController#setClickable(boolean)
 *
 * @version 	02 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class ClickableTextAction implements java.io.Serializable {
    /** The command string associated with the action. */
    protected String actionCommand;

    /** The action listener associated with the action. */
    transient protected ActionListener actionListener;


    /** Internal constant for serialization */
    static protected final String actionListenerK = "actionL".intern();


    /**
     * Constructs a clickable text action with the specified command string.
     *
     * @param actionCommand the command string.
     */
    public ClickableTextAction(String actionCommand) {
	if (actionCommand == null)
	    throw new NullPointerException();
	this.actionCommand  = actionCommand;
	this.actionListener = null;
    }

    /**
     * Returns the command string for the action event fired by this action.
     */
    public String getActionCommand() {
	return actionCommand;
    }

    /**
     * Checks if this action has action listeners.
     */
    public boolean hasActionListener() {
	return actionListener != null;
    }

    /**
     * Adds the specified action listener to receive action events
     * from this action.
     *
     * @param l the action listener.
     */
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
    }

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this action.
     *
     * @param l the action listener
     */
    public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    /**
     * Performs this action. The action event with the command string of
     * this action is delivered to the action listeners associated with
     * this action.
     */
    public void performClickableAction() {
	performClickableAction(this);
    }

    /**
     * Performs this action with the specified source for the action event.
     * The action event with the command string of this action is delivered
     * to the action listeners associated with this action.
     *
     * @param source the source for the action event.
     */
    public void performClickableAction(Object source) {
	if (actionListener != null) {
	    ActionEvent e = new ActionEvent(source,
					    ActionEvent.ACTION_PERFORMED,
					    actionCommand);
	    actionListener.actionPerformed(e);
	}
    }

    /**
     * Returns a hashcode for this action.
     */
    public int hashCode() {
	int h = actionCommand.hashCode();
	if (actionListener != null) {
	    h ^= actionListener.hashCode();
	}
	return h;
    }

    /**
     * Compares two objects for equality.
     */
    public boolean equals(Object anObject) {
	if (this == anObject)
	    return true;
	if (anObject == null)
	    return false;
	if (anObject instanceof ClickableTextAction) {
	    ClickableTextAction cta = (ClickableTextAction)anObject;
	    return (actionCommand.equals(cta.actionCommand) &&
		    actionListener == cta.actionListener);
	}
	return false;
    }


    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	jp.kyasu.awt.ListenerSerializer.write(s,
					      actionListenerK,
					      actionListener);
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws ClassNotFoundException, java.io.IOException
    {
	s.defaultReadObject();
	Object keyOrNull;
	while ((keyOrNull = s.readObject()) != null) {
	    String key = ((String)keyOrNull).intern();
	    if (key == actionListenerK)
		addActionListener((ActionListener)s.readObject());
	    else // skip value for unrecognized key
		s.readObject();
	}
    }
}
