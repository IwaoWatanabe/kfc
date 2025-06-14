/*
 * CompositeKeyAction.java
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

package jp.kyasu.awt.text;

/**
 * The <code>CompositeKeyAction</code> object is composed of multiple
 * <code>KeyAction</code> objects and it implements the <code>KeyAction</code>
 * interface.
 *
 * @see		jp.kyasu.awt.text.KeyAction
 * @see		jp.kyasu.awt.text.KeyBinding
 * @see		jp.kyasu.awt.text.Keymap
 * @see		jp.kyasu.awt.text.TextEditController
 *
 * @version 	27 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class CompositeKeyAction implements KeyAction, java.io.Serializable {
    transient protected String actionName;
    transient protected KeyAction[] actions;


    /**
     * Constructs an empty composite key action.
     */
    public CompositeKeyAction() {
	actionName = "";
	actions = new KeyAction[0];
    }


    /**
     * Returns the name of the composite key action object.
     */
    public String getName() {
	return actionName;
    }

    /**
     * Performs the composite key action with the specified typed key
     * character.
     *
     * @param keyChar the typed key character.
     */
    public void perform(char keyChar) {
	for (int i = 0; i < actions.length; i++) {
	    actions[i].perform(keyChar);
	}
    }

    /**
     * Returns the number of key action objects which compose this composite
     * key action.
     */
    public int getActionCount() {
	return actions.length;
    }

    /**
     * Returns the key action object at the specified index.
     *
     * @param index the index into the composite key action.
     */
    public KeyAction getAction(int index) {
	return actions[index];
    }

    /**
     * Appends the specified key action object into this composite key action.
     *
     * @param keyAction the key action object.
     */
    public synchronized void append(KeyAction keyAction) {
	if (keyAction == null)
	    throw new NullPointerException();
	KeyAction newActions[] = new KeyAction[actions.length + 1];
	System.arraycopy(actions, 0, newActions, 0, actions.length);
	newActions[actions.length] = keyAction;
	actions = newActions;
	if (actionName.length() > 0)
	    actionName += "+";
	actionName += keyAction.getName();
    }


    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	for (int i = 0; i < actions.length; i++) {
	    if (actions[i] instanceof java.io.Serializable) {
		s.writeObject(actions[i]);
	    }
	}
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	actionName = "";
	actions = new KeyAction[0];
	Object actionOrNull;
	while ((actionOrNull = s.readObject()) != null) {
	    append((KeyAction)actionOrNull);
	}
    }
}
