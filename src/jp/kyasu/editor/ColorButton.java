/*
 * ColorButton.java
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

import jp.kyasu.awt.Button;
import jp.kyasu.graphics.VActiveButton;
import jp.kyasu.graphics.VImage;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

/**
 * The <code>ColorButton</code> allows a user to select a color.
 *
 * @version 	11 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class ColorButton extends Button {
    protected ColorChooser colorChooser;


    class ColorAction implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    colorChooser.showPopup(ColorButton.this,
				   0, ColorButton.this.getSize().height);
	}
    }


    /**
     * Constructs a color button with the null default color.
     */
    public ColorButton() {
	this(null);
    }

    /**
     * Constructs a color button with the specified default color.
     */
    public ColorButton(Color defaultColor) {
	super("");
	VImage icon =
	    jp.kyasu.awt.AWTResources.getIcon(getClass(), "icons/color.gif");
	setVButton(new VActiveButton(icon));

	colorChooser = new ColorChooser(defaultColor);
	addActionListener(new ColorAction());
    }


    /**
     * Returns the selected color.
     */
    public Color getColor() {
	return colorChooser.getColor();
    }

    /**
     * Add a listener to recieve item events when the state of an item changes.
     * @see java.awt.ItemSelectable
     */
    public void addItemListener(ItemListener l) {
	colorChooser.addItemListener(l);
    }

    /**
     * Removes an item listener.
     * @see java.awt.ItemSelectable
     */
    public void removeItemListener(ItemListener l) {
	colorChooser.removeItemListener(l);
    }


    /** Executes the examples. */
    public static void main(String args[]) {
	ColorButton button = new ColorButton();
	button.addItemListener(new ItemListener() {
	    public void itemStateChanged(java.awt.event.ItemEvent e) {
		System.out.println(e.getItem());
	    }
	});
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame();
	f.add(button, java.awt.BorderLayout.CENTER);
	f.pack();
	f.setVisible(true);
    }
}
