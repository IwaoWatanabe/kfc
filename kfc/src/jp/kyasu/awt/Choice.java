/*
 * Choice.java
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

import jp.kyasu.awt.text.ChoiceListController;
import jp.kyasu.awt.text.TextListView;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.V3DBorder;
import jp.kyasu.graphics.VArrow;
import jp.kyasu.graphics.VBorder;
import jp.kyasu.graphics.VPlainBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.*;

/**
 * The <code>Choice</code> class presents a pop-up menu of choices.
 * The current choice is displayed as the title of the menu.
 *
 * @see		jp.kyasu.awt.List
 *
 * @version 	15 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class Choice extends List {
    protected Button choiceButton;
    transient protected PopupPanel popupPanel;

    protected Object popupLock = new Object();


    /**
     * The maximum number of rows in the popup list.
     */
    static protected final int MAX_POPUP_ROWS = 8;


    class PopupMouse extends MouseAdapter implements java.io.Serializable {
	public void mousePressed(MouseEvent e) {
	    if (!isEnabled())
		return;
	    listView.requestFocus();
	    if (isPopupWindowShowing()) {
		hidePopupWindow();
	    }
	    else {
		//showPopupWindow();
		// delayed show
		ActionEvent ae = new ActionEvent(
					choiceButton,
					ActionEvent.ACTION_PERFORMED +
					    java.awt.AWTEvent.RESERVED_ID_MAX,
					"");
		EventPoster.postEvent(ae);
	    }
	}
    }

    class ShowPopupKey extends KeyAdapter implements java.io.Serializable {
	public void keyPressed(KeyEvent e) {
	    if (!isEnabled())
		return;
	    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
		showPopupWindow();
	    }
	}
    }

    class ShowPopupAction implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    if (!isEnabled())
		return;
	    listView.requestFocus();
	    showPopupWindow();
	}
    }

    class SelectPopupItem implements ItemListener, java.io.Serializable {
	public void itemStateChanged(ItemEvent e) {
	    if (!isEnabled())
		return;
	    if (e.getStateChange() == ItemEvent.SELECTED) {
		hidePopupWindow();
		int index = ((Integer)e.getItem()).intValue();
		listView.requestFocus();
		listController.deselect(index, 0, false, false);
		listController.select(index, 0, true, true);
	    }
	}
    }

    class SelectPopupAction implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    if (!isEnabled())
		return;
	    if (popupMenu == null)
		return;
	    Object src = e.getSource();
	    int index = -1;
	    int count = popupMenu.getItemCount();
	    for (int i = 0; i < count; i++) {
		if (src == popupMenu.getItem(i)) {
		    index = i;
		    break;
		}
	    }
	    /*
	    int index = -1;
	    int count = getItemCount();
	    for (int i = 0; i < count; i++) {
		if (e.getActionCommand().equals(getItem(i))) {
		    index = i;
		    break;
		}
	    }
	    */
	    if (index < 0) {
		return;
	    }
	    listView.requestFocus();
	    listController.deselect(index, 0, false, false);
	    listController.select(index, 0, true, true);
	}
    }


    /**
     * Creates a new choice menu. The menu initially has no items in it.
     * <p>
     * By default, the first item added to the choice menu becomes the
     * selected item, until a different selection is made by the user by
     * calling one of the <code>select</code> methods.
     */
    public Choice() {
	this(RichTextStyle.DEFAULT_LIST_STYLE);
    }

    /**
     * Creates a new choice menu. The menu initially has no items in it.
     * <p>
     * By default, the first item added to the choice menu becomes the
     * selected item, until a different selection is made by the user by
     * calling one of the <code>select</code> methods.
     *
     * @param richTextStyle the rich text style for the choice.
     */
    public Choice(RichTextStyle richTextStyle) {
	super(new DefaultTextListModel(richTextStyle));

	listView = new TextListView(listModel);
	listController = new ChoiceListController(listView);
	listView.setController(listController);
	listController.setMultipleMode(false);
	listController.setPopupMenu(null);

	listController.addItemListener(this);

	listView.addMouseListener(new PopupMouse());
	listView.addKeyListener(new ShowPopupKey());

	rows = 1;

	choiceButton = createChoiceButton();
	popupPanel = null;

	//super.setForeground(listView.getForeground());
	//super.setBackground(listView.getBackground());
	super.setFont(
	  listModel.getTextList().getRichTextStyle().getTextStyle().getFont());
	super.setCursor(listView.getCursor());

	BorderedPanel bp = new BorderedPanel(new V3DBorder(false));
	bp.add(listView, BorderLayout.CENTER);
	bp.add(choiceButton, BorderLayout.EAST);

	setLayout(new BorderLayout());
	add(bp, BorderLayout.CENTER);
    }


    /**
     * Invoked when an item's state has been changed.
     * @see java.awt.event.ItemListener
     */
    public void itemStateChanged(ItemEvent e) {
	if (e.getStateChange() != ItemEvent.SELECTED) {
	    return;
	}
	if (itemListener != null) {
	    if (isDirectNotification()) {
		e = new ItemEvent(this,
				  e.getID(),
				  getSelectedItem(),
				  e.getStateChange());
		itemListener.itemStateChanged(e);
	    }
	    else {
		e = new ItemEvent(this,
				  e.getID() + java.awt.AWTEvent.RESERVED_ID_MAX,
				  getSelectedItem(),
				  e.getStateChange());
		EventPoster.postEvent(e);
	    }
	}
	else {
	    e = new ItemEvent(this,
			      e.getID(),
			      getSelectedItem(),
			      e.getStateChange());
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

    /**
     * Enables or disables this choice.
     */
    public synchronized void setEnabled(boolean b) {
	super.setEnabled(b);
	listView.setEnabled(b);
	choiceButton.setEnabled(b);
    }

    /**
     * Returns the preferred dimensions for a choice.
     */
    public Dimension getPreferredSize() {
	synchronized (getTreeLock()) {
	    int width = listView.getPreferredSize().width;
	    int height = listView.getPreferredHeight(1);
	    width += choiceButton.getPreferredSize().width;
	    Container c = listView.getParent(); // BorderedPanel
	    Insets insets = c.getInsets();
	    width  += (insets.left + insets.right);
	    height += (insets.top + insets.bottom);
	    return new Dimension(width, height);
	}
    }

    /**
     * Returns the minimum dimensions for a choice.
     */
    public Dimension getMinimumSize() {
	return getPreferredSize();
    }

    /**
     * Moves and resizes this choice.
     */
    public synchronized void setBounds(int x, int y, int width, int height) {
	Dimension d = getPreferredSize();
	if (height > d.height) {
	    y += ((height - d.height) / 2);
	    height = d.height;
	}
	super.setBounds(x, y, width, height);
    }


    // ======== java.awt.Choice APIs ========

    /**
     * Returns an array (length 1) containing the currently selected
     * item.  If this choice has no items, returns null.
     */
    public synchronized Object[] getSelectedObjects() {
	Object objs[] = super.getSelectedObjects();
	return (objs.length == 0 ? null : objs);
    }

    /**
     * Adds an item to this Choice.
     * @param item the item to be added
     * @exception NullPointerException If the item's value is equal to null.
     */
    public synchronized void addItem(String item, int index) {
	if (item == null) {
	    throw new NullPointerException("cannot add null item to Choice");
	}
	super.addItem(item, index);
	if (getItemCount() == 1) {
	    select(0);
	}
    }

    /**
     * Inserts the item into this choice at the specified position.
     * @param item  the item to be inserted
     * @param index the position at which the item should be inserted
     * @exception IllegalArgumentException if index is less than 0.
     */
    public synchronized void insert(String item, int index) {
	if (index < 0) {
	    throw new IllegalArgumentException("index less than zero.");
	}
	addItem(item, index);
    }

    // ======== Enhanced APIs ========

    /**
     * Adds a text item to this Choice.
     * @param item the text item to be added
     * @exception NullPointerException If the item's value is equal to null.
     */
    public synchronized void addTextItem(Text item, int index) {
	if (item == null) {
	    throw new NullPointerException("cannot add null item to Choice");
	}
	super.addTextItem(item, index);
	if (getItemCount() == 1) {
	    select(0);
	}
    }

    /**
     * Inserts the text item into this choice at the specified position.
     * @param item  the text item to be inserted
     * @param index the position at which the item should be inserted
     * @exception IllegalArgumentException if index is less than 0.
     */
    public synchronized void insert(Text item, int index) {
	if (index < 0) {
	    throw new IllegalArgumentException("index less than zero.");
	}
	addTextItem(item, index);
    }

    // ======== Protected ========

    /**
     * Creates a button for the choice.
     */
    protected Button createChoiceButton() {
	choiceButton = new Button(new VArrow(VArrow.DOWN));
	choiceButton.getController().setMode(ButtonController.TRIGGER_ON_DOWN);
	choiceButton.getController().setFocusEmphasizeEnabled(false);
	choiceButton.setForeground(Color.black);
	choiceButton.setBackground(Color.lightGray);
	choiceButton.setSize(choiceButton.getPreferredSize().width,
			     listView.getPreferredHeight(1));
	choiceButton.addActionListener(new ShowPopupAction());
	return choiceButton;
    }

    /**
     * Checks if the popup window is showing.
     */
    protected boolean isPopupWindowShowing() {
	return (popupPanel != null && popupPanel.isShowing());
    }

    /**
     * Shows a popup window.
     */
    protected void showPopupWindow() {
	if (!isEnabled())
	    return;
	synchronized (popupLock) {
	    if (isPopupWindowShowing()) {
		hidePopupWindow();
		return;
	    }
	}
	if (isInModalDialog() && !AWTResources.CAN_OPEN_POPUP_IN_MODAL_DIALOG) {
	    choiceButton.getVButton().setState(false);
	    choiceButton.repaintNow();
	    /*
	    Dialog.warn(getFrame(),
	    	AWTResources.getResourceString(
			"kfc.choice.warn",
			"Within modal dialog, pop-up window can not be opened.\nPlease select an item by using UP/DOWN keys."));
	    */
	    getDialogPopupMenu().show(this, 0, getSize().height);
	}
	else {
	    synchronized (popupLock) {
		popupPanel = getPopupPanel();
		int popupHeight = popupPanel.getSize().height;
		popupPanel.setSize(getSize().width, popupHeight);
		int height = getSize().height;
		int top = - getLocationOnScreen().y;
		int bottom =
		    Toolkit.getDefaultToolkit().getScreenSize().height + top;
		if (height + popupHeight > bottom && -popupHeight >= top) {
		    popupPanel.showPopup(this, 0, -popupHeight);
		}
		else {
		    popupPanel.showPopup(this, 0, height);
		}
		choiceButton.removeMouseListener(
				popupPanel.popupWindow.grabMouseListener);
		choiceButton.removeMouseMotionListener(
				popupPanel.popupWindow.grabMouseListener);
		listView.setLineSelectionVisible(false);
	    }
	}
    }

    /**
     * Returns a popup panel.
     */
    protected PopupPanel getPopupPanel() {
	synchronized (popupLock) {
	    if (popupPanel != null) {
		return popupPanel;
	    }
	    List l = new List(listView.getModel(),
			      Math.min(getItemCount(), MAX_POPUP_ROWS),
			      false,
			      ScrollPanel.SCROLLBARS_VERTICAL_ONLY,
			      new VPlainBorder());
	    l.listController.setPopupMenu(null);
	    l.listController.setMovingSelectionEnabled(true);
	    l.setForeground(listView.getForeground());
	    l.setBackground(listView.getBackground());
	    l.setSelectionForeground(listView.getSelectionForeground());
	    l.setSelectionBackground(listView.getSelectionBackground());
	    l.addItemListener(new SelectPopupItem());

	    popupPanel = new PopupPanel();
	    popupPanel.setLayout(new BorderLayout());
	    popupPanel.add(l, BorderLayout.CENTER);
	    popupPanel.setSize(getSize().width, l.getPreferredSize().height);
	    return popupPanel;
	}
    }


    /**
     * Hides a popup window.
     */
    protected void hidePopupWindow() {
	synchronized (popupLock) {
	    if (popupPanel != null) {
		popupPanel.hidePopup();
		listView.setLineSelectionVisible(true);
	    }
	}
    }

    /**
     * Returns true if this choice is in a modal dialog.
     */
    protected boolean isInModalDialog() {
	for (Container c = getParent(); c != null; c = c.getParent()) {
	    if (c instanceof java.awt.Dialog)
		return ((java.awt.Dialog)c).isModal();
	}
	return false;
    }


    transient protected PopupMenu popupMenu = null;

    /**
     * Returns the popup menu if this choice is in a modal dialog.
     */
    protected PopupMenu getDialogPopupMenu() {
	if (popupMenu == null) {
	    popupMenu = new PopupMenu();
	    popupMenu.setFont(getFont());
	    ActionListener l = new SelectPopupAction();
	    int count = getItemCount();
	    for (int i = 0; i < count; i++) {
		String label = getItem(i);
		MenuItem mi = new MenuItem(label);
		mi.setFont(getFont());
		mi.setActionCommand(label);
		mi.addActionListener(l);
		popupMenu.add(mi);
	    }
	    add(popupMenu);
	}
	return popupMenu;
    }
}
