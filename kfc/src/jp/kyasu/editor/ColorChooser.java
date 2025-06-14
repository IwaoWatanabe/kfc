/*
 * ColorChooser.java
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

import jp.kyasu.awt.BorderedPanel;
import jp.kyasu.awt.Button;
import jp.kyasu.awt.PopupPanel;
import jp.kyasu.graphics.V3DBorder;
import jp.kyasu.graphics.VActiveButton;
import jp.kyasu.graphics.VSpace;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The <code>ColorChooser</code> allows a user to select a color.
 *
 * @see 	jp.kyasu.awt.PopupPanel#showPopup(java.awt.Component,int,int)
 *
 * @version 	19 May 1998
 * @author 	Kazuki YASUMATSU
 */
public class ColorChooser extends PopupPanel implements ItemSelectable {
    /** The default color. */
    protected Color defaultColor;

    /** The selected color. */
    protected Color selectedColor;

    /** The item listener. */
    transient protected ItemListener itemListener;


    /** The colors to be chosen. */
    static public final Color Colors[] = {
	new Color(0xFFCCCC), new Color(0xFF6666), new Color(0xFF0000),
	new Color(0xCC0000), new Color(0x990000), new Color(0x660000),
	new Color(0x330000), new Color(0xFFFFFF),

	new Color(0xFFFFCC), new Color(0xFFFF99), new Color(0xFFFF00),
	new Color(0xFFCC00), new Color(0x999900), new Color(0x666600),
	new Color(0x333300), new Color(0xCCCCCC),

	new Color(0xCCFFFF), new Color(0x66FFFF), new Color(0x33CCFF),
	new Color(0x3366FF), new Color(0x3333FF), new Color(0x009999),
	new Color(0x000066), new Color(0x999999),

	new Color(0xFFCCFF), new Color(0xFF99FF), new Color(0xCC66CC),
	new Color(0xCC33CC), new Color(0x993399), new Color(0x663366),
	new Color(0x330033), new Color(0x666666),

	new Color(0xFFCC99), new Color(0xFFCC33), new Color(0xFF9900),
	new Color(0xFF6600), new Color(0xCC6600), new Color(0x993300),
	new Color(0x663300), new Color(0x333333),

	new Color(0x99FFCC), new Color(0x66FF99), new Color(0x33FF33),
	new Color(0x33CC00), new Color(0x009900), new Color(0x006600),
	new Color(0x003300), new Color(0x000000)
    };


    /**
     * Constructs a color chooser with the null default color.
     */
    public ColorChooser() {
	this(null);
    }

    /**
     * Constructs a color chooser with the specified default color.
     */
    public ColorChooser(Color defaultColor) {
	this.defaultColor  = defaultColor;
	this.selectedColor = null;
	this.itemListener  = null;
	initializePanel();
    }


    /**
     * Returns the selected color.
     */
    public Color getColor() {
	return selectedColor;
    }

    /**
     * Returns the selected items or null if no items are selected.
     * @see java.awt.ItemSelectable
     */
    public Object[] getSelectedObjects() {
	return new Color[]{ selectedColor };
    }

    /**
     * Add a listener to recieve item events when the state of an item changes.
     * @see java.awt.ItemSelectable
     */
    public void addItemListener(ItemListener l) {
	itemListener = AWTEventMulticaster.add(itemListener, l);
    }

    /**
     * Removes an item listener.
     * @see java.awt.ItemSelectable
     */
    public void removeItemListener(ItemListener l) {
	itemListener = AWTEventMulticaster.remove(itemListener, l);
    }

    /** Notifies the item event to the item listeners. */
    protected void notifyItemListeners() {
	if (itemListener != null) {
	    ItemEvent e = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
					selectedColor, ItemEvent.SELECTED);
	    itemListener.itemStateChanged(e);
	}
    }


    class ColorAction implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    Button b = (Button)e.getSource();
	    selectedColor = b.getBackground();
	    hidePopup();
	    notifyItemListeners();
	}
    }

    class DefaultColorAction implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    selectedColor = defaultColor;
	    hidePopup();
	    notifyItemListeners();
	}
    }


    protected void initializePanel() {
	ActionListener colorAction = new ColorAction();
	ActionListener defaultColorAction = new DefaultColorAction();

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();

	BorderedPanel bp = new BorderedPanel(new V3DBorder(true));
	bp.setLayout(gridbag);

	for (int y = 0; y < 6; y++) {
	    for (int x = 0; x < 8; x++) {
		Button b = makeColorButton(Colors[y * 8 + x]);
		b.setFocusTraversable(false);
		b.addActionListener(colorAction);
		c.gridx = x;
		c.gridy = y;
		gridbag.setConstraints(b, c);
		bp.add(b);
	    }
	}

	Button b = new Button(EditorResources.getResourceString("Default"));
	b.setFocusTraversable(false);
	b.getController().setFocusEmphasizeEnabled(false);
	b.addActionListener(defaultColorAction);
	c.gridwidth = 8;
	c.gridx = 0;
	c.gridy = 7;
	//c.fill = GridBagConstraints.HORIZONTAL;
	gridbag.setConstraints(b, c);
	bp.add(b);

	add(bp, BorderLayout.CENTER);
	setSize(getPreferredSize());
    }

    protected Button makeColorButton(Color c) {
	Button b = new Button(new VActiveButton(new VSpace(12, 12)));
	b.setForeground(c);
	b.setBackground(c);
	return b;
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
