/*
 * KeyBinding.java
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

import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The <code>KeyBinding</code> object maps key characters or codes to
 * <code>KeyAction</code> object according to the <code>Keymap</code> object.
 *
 * @see		jp.kyasu.awt.text.CompositeKeyAction
 * @see		jp.kyasu.awt.text.KeyAction
 * @see		jp.kyasu.awt.text.Keymap
 * @see		jp.kyasu.awt.text.TextEditController
 *
 * @version 	02 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class KeyBinding implements java.io.Serializable {
    protected Keymap keymap;
    transient protected Hashtable actionTable;
    transient protected KeyAction defaultAction;
    transient protected KeyAction keyCharBindings[];
    transient protected Hashtable keyCodeBindings;


    /**
     * Tests if the specified character is printable.
     *
     * @param ch a character.
     * @return <code>true</code> if the character is printable,
     *         <code>false</code> otherwise.
     */
    static public boolean isPrintableCharacter(char ch) {
	if (ch < ' ') return false;
	int type = Character.getType(ch);
	return (type != Character.UNASSIGNED && type != Character.CONTROL);
    }


    /**
     * Constructs an empty key binding.
     */
    public KeyBinding() {
	this(new Keymap());
    }

    /**
     * Constructs a key binding with the specified keymap.
     *
     * @param keymap the keymap.
     */
    public KeyBinding(Keymap keymap) {
	if (keymap == null)
	    throw new NullPointerException();
	this.keymap     = keymap;
	actionTable     = new Hashtable();
	defaultAction   = null;
	keyCharBindings = new KeyAction[256];
	keyCodeBindings = new Hashtable();
    }


    /**
     * Returns the <code>Keymap</code> object of this key binding.
     *
     * @return the <code>Keymap</code> object of this key binding.
     */
    public Keymap getKeymap() {
	return keymap;
    }

    /**
     * Sets the <code>Keymap</code> object of this key binding.
     *
     * @param keymap the <code>Keymap</code> object.
     */
    public void setKeymap(Keymap keymap) {
	if (keymap == null)
	    throw new NullPointerException();
	this.keymap = keymap;
	bindKeymapToAction();
    }

    /**
     * Adds the key action to this key binding.
     *
     * @param keyAction the key action object.
     */
    public void addKeyAction(KeyAction keyAction) {
	if (keyAction == null)
	    throw new NullPointerException();
	actionTable.put(keyAction.getName(), keyAction);
	bindKeymapToAction();
    }

    /**
     * Adds the key actions to this key binding.
     *
     * @param keyActions the key action objects.
     */
    public void addKeyActions(KeyAction keyActions[]) {
	if (keyActions == null)
	    throw new NullPointerException();
	for (int i = 0; i < keyActions.length; i++) {
	    actionTable.put(keyActions[i].getName(), keyActions[i]);
	}
	bindKeymapToAction();
    }

    /**
     * Sets the key actions of this key binding.
     *
     * @param keyActions the key action objects.
     */
    public void setKeyActions(KeyAction keyActions[]) {
	if (keyActions == null)
	    throw new NullPointerException();
	actionTable = new Hashtable();
	addKeyActions(keyActions);
    }

    /**
     * Removes the key action from this key binding.
     *
     * @param keyAction the key action object.
     */
    public void removeKeyAction(KeyAction keyAction) {
	if (keyAction == null)
	    return;
	actionTable.remove(keyAction.getName());
	bindKeymapToAction();
    }

    /**
     * Removes the key action named the specified name from this key binding.
     *
     * @param actionName the name of the key action.
     */
    public void removeKeyActionNamed(String actionName) {
	if (actionName == null)
	    return;
	actionTable.remove(actionName);
	bindKeymapToAction();
    }

    /**
     * Returns the key action object associated with the specified name.
     *
     * @param actionName the name of the key action.
     * @return the key action object, or <code>null</code> if no associated
     *         action exists.
     */
    public KeyAction getKeyAction(String actionName) {
	return (KeyAction)actionTable.get(actionName);
    }

    /**
     * Returns the key action object associated with the specified key event.
     *
     * @param e the key event.
     * @return the key action object, or <code>null</code> if no associated
     *         action exists.
     */
    public KeyAction getKeyAction(KeyEvent e) {
	switch (e.getID()) {
	case KeyEvent.KEY_TYPED:
	    char keyChar = e.getKeyChar();
	    /*
	    if (keyChar == KeyEvent.VK_ENTER      ||
		keyChar == KeyEvent.VK_BACK_SPACE ||
		keyChar == KeyEvent.VK_TAB        ||
		keyChar == KeyEvent.VK_DELETE)
	    {
		return null;
	    }
	    */
	    KeyAction a = getKeyAction(keyChar);
	    if (a != null)
		return a;
	    if (isPrintableCharacter(keyChar))
		return defaultAction;
	    return null;

	case KeyEvent.KEY_PRESSED:
	    return getKeyAction(e.getKeyCode(), e.getModifiers());

	case KeyEvent.KEY_RELEASED:
	default:
	    break;
	}

	return null;
    }

    /**
     * Returns the key action object associated with the specified key
     * character.
     *
     * @param keyChar the key character.
     * @return the key action object, or <code>null</code> if no associated
     *         action exists.
     */
    public KeyAction getKeyAction(char keyChar) {
	if (keyChar < 256) {
	    return keyCharBindings[keyChar];
	}
	return null;
    }

    /**
     * Returns the key action object associated with the specified key code
     * with the specified key modifiers.
     *
     * @param keyCode   the key code.
     * @param modifiers the key modifiers.
     * @return the key action object, or <code>null</code> if no associated
     *         action exists.
     */
    public KeyAction getKeyAction(int keyCode, int modifiers) {
	int keyCodeValue = (keyCode << Keymap.KEY_SHIFT) + modifiers;
	return (KeyAction)keyCodeBindings.get(new Integer(keyCodeValue));
    }

    /**
     * Returns the default key action object which will be invoked when
     * the pressed key is not associated with any actions and the pressed
     * key character is printable.
     */
    public KeyAction getDefaultKeyAction() {
	return defaultAction;
    }


    /**
     * Binds keymap to actions.
     */
    protected void bindKeymapToAction() {
	defaultAction   = null;
	keyCharBindings = new KeyAction[256];
	keyCodeBindings = new Hashtable();

	if (actionTable.isEmpty())
	    return;
	defaultAction = getBindedKeyAction(keymap.getDefaultActionNames());
	for (int i = 0; i < 256; i++) {
	    keyCharBindings[i] = getBindedKeyAction(keymap.keyCharMap[i]);
	}
	int len = keymap.keyCodeMap.size();
	Enumeration k = keymap.keyCodeMap.keys();
	Enumeration e = keymap.keyCodeMap.elements();
	for (int i = 0; i < len; i++) {
	    Integer keyCodeValue = (Integer)k.nextElement();
	    String names[] = (String[])e.nextElement();
	    KeyAction a = getBindedKeyAction(names);
	    if (a != null) {
		keyCodeBindings.put(keyCodeValue, a);
	    }
	}
    }

    protected KeyAction getBindedKeyAction(String names[]) {
	if (names == null || names.length == 0) {
	    return null;
	}
	else if (names.length == 1) {
	    return (KeyAction)actionTable.get(names[0]);
	}
	else { // names.length > 1
	    CompositeKeyAction ca = new CompositeKeyAction();
	    for (int i = 0; i < names.length; i++) {
		KeyAction a = (KeyAction)actionTable.get(names[i]);
		if (a != null) {
		    ca.append(a);
		}
	    }
	    if (ca.getActionCount() == 0) {
		return null;
	    }
	    else if (ca.getActionCount() == 1) {
		return ca.getAction(0);
	    }
	    else { // ca.getActionCount() > 1
		return ca;
	    }
	}
    }


    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();

	int len = actionTable.size();
	Enumeration e = actionTable.elements();
	for (int i = 0; i < len; i++) {
	    KeyAction action = (KeyAction)e.nextElement();
	    if (action instanceof java.io.Serializable) {
		s.writeObject(action);
	    }
	}

	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	s.defaultReadObject();

	actionTable     = new Hashtable();
	defaultAction   = null;
	keyCharBindings = new KeyAction[256];
	keyCodeBindings = new Hashtable();

	Object actionOrNull;
	while ((actionOrNull = s.readObject()) != null) {
	    KeyAction action = (KeyAction)actionOrNull;
	    actionTable.put(action.getName(), action);
	}

	bindKeymapToAction();
    }
}
