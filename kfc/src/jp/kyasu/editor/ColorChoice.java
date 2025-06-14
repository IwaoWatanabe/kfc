/*
 * ColorChoice.java
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
import jp.kyasu.awt.ButtonController;
import jp.kyasu.awt.Label;
import jp.kyasu.awt.Panel;
import jp.kyasu.graphics.V3DBorder;
import jp.kyasu.graphics.VArrow;
import jp.kyasu.graphics.VSpace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The <code>ColorChoice</code> class presents a pop-up menu of choices
 * of colors.
 *
 * @version 	12 May 1998
 * @author 	Kazuki YASUMATSU
 */
public class ColorChoice extends Panel {
    protected Label colorLabel;
    protected Button choiceButton;
    protected ColorChooser colorChooser;


    class ColorAction implements ActionListener, java.io.Serializable {
	public void actionPerformed(ActionEvent e) {
	    colorChooser.showPopup(ColorChoice.this,
				   0, ColorChoice.this.getSize().height);
	}
    }

    class ColorItem implements ItemListener, java.io.Serializable {
	public void itemStateChanged(ItemEvent e) {
	    Color color = (Color)e.getItem();
	    if (color != null) {
		colorLabel.setForeground(color);
		colorLabel.setBackground(color);
	    }
	    else {
		colorLabel.setForeground(ColorChoice.this.getForeground());
		colorLabel.setBackground(ColorChoice.this.getForeground());
	    }
	    colorLabel.repaintNow();
	}
    }


    /**
     * Constructs a color choice with the null default color.
     */
    public ColorChoice() {
	this(null, null);
    }

    /**
     * Constructs a color choice with the specified default color.
     */
    public ColorChoice(Color defaultColor) {
	this(defaultColor, null);
    }

    /**
     * Constructs a color choice with the specified default color and
     * foreground color.
     */
    public ColorChoice(Color defaultColor, Color foreground) {
	if (foreground != null)
	    setForeground(foreground);
	colorLabel = new Label(new VSpace(16, 16));
	colorLabel.setFocusTraversable(false);
	if (defaultColor != null) {
	    colorLabel.setForeground(defaultColor);
	    colorLabel.setBackground(defaultColor);
	}
	else {
	    colorLabel.setForeground(getForeground());
	    colorLabel.setBackground(getForeground());
	}

	choiceButton = new Button(new VArrow(VArrow.DOWN));
	//choiceButton.getController().setMode(ButtonController.TRIGGER_ON_DOWN);
	choiceButton.getController().setFocusEmphasizeEnabled(false);
	choiceButton.setFocusTraversable(false);
	choiceButton.setForeground(Color.black);
	choiceButton.setBackground(Color.lightGray);
	choiceButton.setSize(choiceButton.getPreferredSize());
	choiceButton.addActionListener(new ColorAction());

	colorLabel.setSize(16, choiceButton.getSize().height);

	colorChooser = new ColorChooser(defaultColor);
	colorChooser.addItemListener(new ColorItem());

	BorderedPanel bp = new BorderedPanel(new V3DBorder(false));
	bp.add(colorLabel, BorderLayout.CENTER);
	bp.add(choiceButton, BorderLayout.EAST);

	setLayout(new BorderLayout(0, 0));
	add(bp, BorderLayout.CENTER);
    }


    /**
     * Enables or disables this choice.
     */
    public synchronized void setEnabled(boolean b) {
	super.setEnabled(b);
	choiceButton.setEnabled(b);
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
	ColorChoice choice = new ColorChoice();
	choice.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		System.out.println(e.getItem());
	    }
	});
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame();
	f.add(choice, BorderLayout.CENTER);
	f.pack();
	f.setVisible(true);
    }
}
