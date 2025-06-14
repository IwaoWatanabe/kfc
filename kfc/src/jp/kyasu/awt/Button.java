/*
 * Button.java
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
import jp.kyasu.graphics.VLabel;
import jp.kyasu.graphics.VText;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The <code>Button</code> class implements a labeled button. The application
 * can cause some action to happen when the button is pushed.
 * <p>
 * When a button is pressed and released, the button sends an instance of
 * <code>ActionEvent</code> to the button.
 *
 * @see 	jp.kyasu.awt.ButtonController
 *
 * @version 	15 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class Button extends AbstractButton {
    transient protected ActionListener actionListener;


    /**
     * Constructs a button with empty label.
     */
    public Button() {
	this("");
    }

    /**
     * Constructs a button with the specified label.
     * @param label A string label for the button.
     */
    public Button(String label) {
	this(new Text(label == null ? "" : label));
    }

    /**
     * Constructs a button with the specified text.
     * @param text A text label for the button.
     */
    public Button(Text text) {
	this(new VText(text));
    }

    /**
     * Constructs a button with the specified visual object.
     * @param visualizable A visual label for the button.
     */
    public Button(Visualizable visualizable) {
	this((VAbstractButton)new VButton(visualizable, VButton.TRIGGER));
    }

    /**
     * Constructs a button with the specified visual button.
     * @param button A visual button for the button.
     */
    public Button(VAbstractButton button) {
	super(button, ButtonController.TRIGGER_ON_UP);
	actionListener = null;
    }


    // ======== java.awt.Button APIs ========

    /**
     * Adds the specified action listener to receive action events from
     * this button. Action events occur when a user presses or releases
     * the mouse over this button.
     * @param l the action listener.
     */
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
	enableEvents(0); // mark newEventsOnly
    }

    /**
     * Removes the specified action listener so that it no longer
     * receives action events from this button. Action events occur
     * when a user presses or releases the mouse over this button.
     * @param l the action listener.
     */
    public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    /**
     * Invoked when the button has been triggered by the controller.
     */
    public void actionPerformed() {
	ActionEvent ae = new ActionEvent(this,
					 ActionEvent.ACTION_PERFORMED,
					 getActionCommand());
	notifyActionListeners(ae);
    }

    /**
     * Invoked when the button has been toggled by the controller.
     */
    public void itemStateChanged(boolean selected) {
	// do nothing
    }

    /**
     * If true, notifies listeners directly without using the event queue.
     */
    public boolean isDirectNotification() {
	return false;
    }

    /** Notifies the action event to the action listeners. */
    protected void notifyActionListeners(ActionEvent e) {
	if (actionListener != null) {
	    if (isDirectNotification()) {
		actionListener.actionPerformed(e);
	    }
	    else {
		e = new ActionEvent(
				e.getSource(),
				e.getID() + java.awt.AWTEvent.RESERVED_ID_MAX,
				e.getActionCommand(),
				e.getModifiers());
		EventPoster.postEvent(e);
	    }
	}
	else {
	    postOldEvent(e);
	}
    }

    protected void processEvent(java.awt.AWTEvent e) {
	if (actionListener != null && (e instanceof ActionEvent)) {
	    ActionEvent ae = (ActionEvent)e;
	    if (ae.getID() > java.awt.AWTEvent.RESERVED_ID_MAX) {
		ae = new ActionEvent(
				ae.getSource(),
				ae.getID() - java.awt.AWTEvent.RESERVED_ID_MAX,
				ae.getActionCommand(),
				ae.getModifiers());
		actionListener.actionPerformed(ae);
		return;
	    }
	}
	super.processEvent(e);
    }


    /** Internal constant for serialization */
    static protected final String actionListenerK = "actionL".intern();

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
