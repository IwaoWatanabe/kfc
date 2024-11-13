/*
 * SelectionMenu.java
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

package jp.kyasu.editor;

import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * A <code>SelectionMenu</code> object is a pull-down menu component that is
 * deployed from a menu bar. It can select one of the
 * <code>CheckboxMenuItem</code>s.
 *
 * @version 	20 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class SelectionMenu extends Menu implements ActionListener, ItemListener
{
    /**
     * Constructs a new menu with an empty label. This menu is not a
     * tear-off menu.
     */
    public SelectionMenu() {
	this("", false);
    }

    /**
     * Constructs a new menu with the specified label. This menu is not
     * a tear-off menu.
     * @param label the menu's label in the menu bar, or in another menu of
     *              which this menu is a submenu.
     */
    public SelectionMenu(String label) {
	this(label, false);
    }

    /**
     * Constructs a new menu with the specified label. If the value of
     * <code>tearOff</code> is <code>true</code>, the menu can be torn off.
     * @param label   the menu's label in the menu bar, or in another menu
     *                of which this menu is a submenu.
     * @param tearOff if <code>true</code>, the menu is a tear-off menu.
     */
    public SelectionMenu(String label, boolean tearOff) {
	super(label, tearOff);
    }


    /**
     * Adds a checkbox item with the specified label to this menu.
     * @param label the text on the checkbox item.
     */
    public void add(String label) {
	add(label, label);
    }

    /**
     * Adds a checkbox item with the specified label and command to this menu.
     * @param label   the text on the checkbox item.
     * @param command the action command on the checkbox item.
     */
    public void add(String label, String command) {
	add(label, command, false);
    }

    /**
     * Adds a checkbox item with the specified label, command, and state
     * to this menu.
     * @param label   the text on the checkbox item.
     * @param command the action command on the checkbox item.
     * @param state   the initial state of the checkbox menu item.
     */
    public void add(String label, String command, boolean state) {
	CheckboxMenuItem mi = new CheckboxMenuItem(label, state);
	mi.setActionCommand(command);
	add(mi);
    }

    /**
     * Adds a separator line, or a hypen, to the menu at the current position.
     */
    public void addSeparator() {
	add(new MenuItem("-"));
    }

    /**
     * Adds the specified menu item to this menu. If the menu item has
     * been part of another menu, remove it from that menu.
     * @param mi the menu item to be added.
     * @return the menu item added.
     * @exception IllegalArgumentException if the item is not a
     *                                     CheckboxMenuItem or SelectionMenu.
     */
    public MenuItem add(MenuItem mi) {
	if (!mi.getLabel().equals("-")) {
	    if (!(mi instanceof CheckboxMenuItem) &&
		!(mi instanceof SelectionMenu))
	    {
		throw new IllegalArgumentException("item must be a CheckboxMenuItem or SelectionMenu");
	    }
	}
	super.add(mi);
	if (mi instanceof CheckboxMenuItem) {
	    ((CheckboxMenuItem)mi).addItemListener(this);
	}
	else if (mi instanceof SelectionMenu) {
	    ((SelectionMenu)mi).addActionListener(this);
	}
	return mi;
    }

    /**
     * Inserts a menu item into this menu at the specified position.
     * @param mi    the menu item to be inserted.
     * @param index the position at which the menu item should be inserted.
     * @exception IllegalArgumentException if the item is not a
     *                                     CheckboxMenuItem or SelectionMenu.
     */
    public void insert(MenuItem mi, int index) {
	if (!mi.getLabel().equals("-")) {
	    if (!(mi instanceof CheckboxMenuItem) &&
		!(mi instanceof SelectionMenu))
	    {
		throw new IllegalArgumentException("item must be a CheckboxMenuItem or SelectionMenu");
	    }
	}
	super.insert(mi, index);
	if (mi instanceof CheckboxMenuItem) {
	    ((CheckboxMenuItem)mi).addItemListener(this);
	}
	else if (mi instanceof SelectionMenu) {
	    ((SelectionMenu)mi).addActionListener(this);
	}
    }

    /**
     * Removes the menu item at the specified index from this menu.
     * @param index the position of the item to be removed.
     */
    public void remove(int index) {
	MenuItem mi = getItem(index);
	super.remove(index);
	if (mi instanceof CheckboxMenuItem) {
	    ((CheckboxMenuItem)mi).removeItemListener(this);
	}
	else if (mi instanceof SelectionMenu) {
	    ((SelectionMenu)mi).removeActionListener(this);
	}
    }

    /**
     * Returns the selected command or null.
     */
    public String getSelectedCommand() {
	int count = getItemCount();
	for (int i = 0; i < count; i++) {
	    MenuItem mi = getItem(i);
	    if (mi instanceof CheckboxMenuItem) {
		if (((CheckboxMenuItem)mi).getState())
		    return mi.getActionCommand();
	    }
	    else if (mi instanceof SelectionMenu) {
		String command = ((SelectionMenu)mi).getSelectedCommand();
		if (command != null)
		    return command;
	    }
	}
	return null;
    }

    /**
     * Selects the checkbox menu item which has the specified command.
     * @param command the command of the checkbox menu item to select.
     */
    public void select(String command) {
	int count = getItemCount();
	for (int i = 0; i < count; i++) {
	    MenuItem mi = getItem(i);
	    if (mi instanceof CheckboxMenuItem) {
		boolean state = (command != null &&
				 command.equals(mi.getActionCommand()));
		((CheckboxMenuItem)mi).setState(state);
	    }
	    else if (mi instanceof SelectionMenu) {
		((SelectionMenu)mi).select(command);
	    }
	}
    }

    /**
     * Invoked when an action occurs.
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(ActionEvent e) {
	Object obj = e.getSource();
	if (!(obj instanceof SelectionMenu))
	    return;
	SelectionMenu menu = (SelectionMenu)obj;
	String command = e.getActionCommand();

	select(command);
	notifyActionListeners(command);
    }

    /**
     * Invoked when an item's state has been changed.
     * @see java.awt.event.ItemListener
     */
    public void itemStateChanged(ItemEvent e) {
	Object obj = e.getSource();
	if (!(obj instanceof CheckboxMenuItem))
	    return;
	CheckboxMenuItem item = (CheckboxMenuItem)obj;
	if (e.getStateChange() != ItemEvent.SELECTED) {
	    if (!item.getState())
		item.setState(true);
	    return;
	}
	String command = item.getActionCommand();

	select(command);
	notifyActionListeners(command);
    }


    /** Notifies the action event to the action listeners. */
    protected void notifyActionListeners(String command) {
	processActionEvent(new ActionEvent(this,
					   ActionEvent.ACTION_PERFORMED,
					   command));
    }


    /** Executes the examples. */
    public static void main(String args[]) {
	final SelectionMenu menu = new SelectionMenu("SelectionMenu");
	menu.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		//System.out.println(e.getActionCommand());
		System.out.println(menu.getSelectedCommand());
	    }
	});
	menu.add("Green", "green");
	menu.add("Red", "red");
	menu.add("Blue", "blue");
	menu.addSeparator();
	SelectionMenu submenu = new SelectionMenu("Another...");
	submenu.add("Black", "black");
	submenu.add("White", "white");
	menu.add(submenu);

	java.awt.MenuBar menuBar = new java.awt.MenuBar();
	menuBar.add(menu);
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame("SelectionMenu");
	f.setMenuBar(menuBar);
	f.setSize(150, 100);
	f.setVisible(true);
    }
}
