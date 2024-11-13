/*
 * Keymap.java
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

import java.awt.Event;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The <code>Keymap</code> object maps key characters or codes to action
 * names of the <code>KeyAction</code> object.
 *
 * @see		jp.kyasu.awt.text.CompositeKeyAction
 * @see		jp.kyasu.awt.text.KeyAction
 * @see		jp.kyasu.awt.text.KeyBinding
 * @see		jp.kyasu.awt.text.TextEditController
 *
 * @version 	06 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class Keymap implements Cloneable, java.io.Serializable {
    protected String defaultActionNames[];
    protected String keyCharMap[][];
    protected Hashtable keyCodeMap;

    static protected final int KEY_SHIFT = 4;


    /** Converts a specified string into action names. */
    static protected String[] convertToActionNames(String actions) {
	java.util.StringTokenizer t = new java.util.StringTokenizer(actions);
	int naction = t.countTokens();
	if (naction == 0) {
	    return null;
	}
	/*
	else if (naction == 1) {
	    return new String[]{ actions };
	}
	*/
	else {
	    String actionNames[] = new String[naction];
	    for (int i = 0; i < naction; i++) {
		actionNames[i] = t.nextToken();
	    }
	    return actionNames;
	}
    }


    /**
     * Constructs an empty keymap.
     */
    public Keymap() {
	this((String[])null);
    }

    /**
     * Constructs an empty keymap with the default action name.
     *
     * @param actionName the default action name.
     */
    public Keymap(String actionName) {
	this(convertToActionNames(actionName));
    }

    /**
     * Constructs an empty keymap with the default action names.
     *
     * @param actionNames the default action names.
     */
    public Keymap(String actionNames[]) {
	defaultActionNames = actionNames;
	keyCharMap = new String[256][];
	keyCodeMap = new Hashtable();
    }


    /**
     * Sets the default action to the specified action name.
     *
     * @param actionName the action name.
     */
    public void setDefaultActionName(String actionName) {
	setDefaultActionNames(convertToActionNames(actionName));
    }

    /**
     * Sets the default action to the specified action names.
     *
     * @param actionNames the action names.
     */
    public void setDefaultActionNames(String actionNames[]) {
	defaultActionNames = actionNames;
    }

    /**
     * Returns the default action names.
     */
    public String[] getDefaultActionNames() {
	return defaultActionNames;
    }

    /**
     * Maps the specified key character to the specified action name.
     *
     * @param keyChar    the key character.
     * @param actionName the action name.
     */
    public void setKeyCharMap(char keyChar, String actionName) {
	String actionNames[] = convertToActionNames(actionName);
	if (actionNames == null)
	    return;
	setKeyCharMap(keyChar, actionNames);
    }

    /**
     * Maps the specified key character to the specified action names.
     *
     * @param keyChar     the key character.
     * @param actionNames the action names.
     */
    public void setKeyCharMap(char keyChar, String actionNames[]) {
	if (keyChar < 256) {
	    keyCharMap[keyChar] = actionNames;
	}
    }

    /**
     * Removes the map from the specified key character to action names.
     *
     * @param  keyChar the key character.
     * @return the old mapped action names.
     */
    public String[] removeKeyCharMap(char keyChar) {
	if (keyChar < 256) {
	    String[] old = keyCharMap[keyChar];
	    keyCharMap[keyChar] = null;
	    return old;
	}
	return null;
    }

    /**
     * Maps the specified key code to the specified action name.
     *
     * @param keyCode    the key code.
     * @param actionName the action name.
     */
    public void setKeyCodeMap(int keyCode, String actionName) {
	String actionNames[] = convertToActionNames(actionName);
	if (actionNames == null)
	    return;
	setKeyCodeMap(keyCode, actionNames);
    }

    /**
     * Maps the specified key code to the specified action names.
     *
     * @param keyCode     the key code.
     * @param actionNames the action names.
     */
    public void setKeyCodeMap(int keyCode, String actionNames[]) {
	setKeyCodeMap(keyCode, 0, actionNames);
    }

    /**
     * Maps the specified key code with the META and ALT key modifiers to the
     * specified action name.
     *
     * @param keyCode    the key code.
     * @param actionName the action name.
     */
    public void setMetaAltKeyCodeMap(int keyCode, String actionName) {
	String actionNames[] = convertToActionNames(actionName);
	if (actionNames == null)
	    return;
	setMetaAltKeyCodeMap(keyCode, actionNames);
    }

    /**
     * Maps the specified key code with the META and ALT key modifiers to the
     * specified action names.
     *
     * @param keyCode     the key code.
     * @param actionNames the action names.
     */
    public void setMetaAltKeyCodeMap(int keyCode, String actionNames[]) {
	setKeyCodeMap(keyCode, Event.META_MASK, actionNames);
	setKeyCodeMap(keyCode, Event.ALT_MASK, actionNames);
	setKeyCodeMap(keyCode, Event.META_MASK | Event.ALT_MASK, actionNames);
    }

    /**
     * Maps the specified key code with the specified key modifiers to the
     * specified action name.
     *
     * @param keyCode    the key code.
     * @param modifiers  the key modifiers.
     * @param actionName the action name.
     */
    public void setKeyCodeMap(int keyCode, int modifiers, String actionName) {
	String actionNames[] = convertToActionNames(actionName);
	if (actionNames == null)
	    return;
	setKeyCodeMap(keyCode, modifiers, actionNames);
    }

    /**
     * Maps the specified key code with the specified key modifiers to the
     * specified action names.
     *
     * @param keyCode     the key code.
     * @param modifiers   the key modifiers.
     * @param actionNames the action names.
     */
    public void setKeyCodeMap(int keyCode, int modifiers, String actionNames[])
    {
	keyCodeMap.put(new Integer((keyCode << KEY_SHIFT) + modifiers),
		       actionNames);
    }

    /**
     * Removes the map from the specified key code to action names.
     *
     * @param  keyCode the key code.
     */
    public void removeKeyCodeMap(int keyCode) {
	removeKeyCodeMap(keyCode, 0);
	removeKeyCodeMap(keyCode, Event.META_MASK);
	removeKeyCodeMap(keyCode, Event.ALT_MASK);
	removeKeyCodeMap(keyCode, Event.META_MASK | Event.ALT_MASK);
    }

    /**
     * Removes the map from the specified key code with he specified key
     * modifiers to action names.
     *
     * @param  keyCode   the key code.
     * @param  modifiers the key modifiers.
     * @return the old mapped action names.
     */
    public String[] removeKeyCodeMap(int keyCode, int modifiers) {
	return (String[])keyCodeMap.remove(
			new Integer((keyCode << KEY_SHIFT) + modifiers));
    }

    /**
     * Returns the action names mapped from the specified key character.
     *
     * @param  keyChar the key character.
     * @return the action names if mapped, otherwise <code>null</code>.
     */
    public String[] getKeyCharMap(char keyChar) {
	return (keyChar < 256 ? keyCharMap[keyChar] : null);
    }

    /**
     * Returns the action names mapped from the specified key code.
     *
     * @param  keyCode the key code.
     * @return the action names if mapped, otherwise <code>null</code>.
     */
    public String[] getKeyCodeMap(int keyCode) {
	return getKeyCodeMap(keyCode, 0);
    }

    /**
     * Returns the action names mapped from the specified key code with
     * the specified key modifiers.
     *
     * @param  keyCode   the key code.
     * @param  modifiers the key modifiers.
     * @return the action names if mapped, otherwise <code>null</code>.
     */
    public String[] getKeyCodeMap(int keyCode, int modifiers) {
	return (String[])keyCodeMap.get(
			new Integer((keyCode << KEY_SHIFT) + modifiers));
    }

    /**
     * Returns a clone of this object.
     */
    public Object clone() {
	Keymap km = new Keymap();
	km.setDefaultActionNames(defaultActionNames);
	for (int i = 0; i < 256; i++) {
	    String names[] = keyCharMap[i];
	    if (names == null) {
		km.keyCharMap[i] = null;
		continue;
	    }
	    String newNames[] = new String[names.length];
	    for (int j = 0; j < names.length; j++) {
		newNames[j] = names[j];
	    }
	    km.keyCharMap[i] = newNames;
	}
	int len = keyCodeMap.size();
	Enumeration k = keyCodeMap.keys();
	Enumeration e = keyCodeMap.elements();
	for (int i = 0; i < len; i++) {
	    Integer keyCodeValue = (Integer)k.nextElement();
	    String names[] = (String[])e.nextElement();
	    if (names == null) {
		continue;
	    }
	    String newNames[] = new String[names.length];
	    for (int j = 0; j < names.length; j++) {
		newNames[j] = names[j];
	    }
	    km.keyCodeMap.put(new Integer(keyCodeValue.intValue()),
			      newNames);
	}
	return km;
    }
}
