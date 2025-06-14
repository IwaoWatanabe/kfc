/*
 * ToolBar.java
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

import jp.kyasu.awt.BorderedPanel;
import jp.kyasu.awt.Button;
import jp.kyasu.awt.Panel;
import jp.kyasu.graphics.VActiveButton;
import jp.kyasu.graphics.VArrow;
import jp.kyasu.graphics.VPaneBorder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A <code>ToolBar</code> object provides a component which is useful for
 * displaying commonly used Actions or controls. It can be showed or hidden
 * by the user.
 *
 * @version 	22 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class ToolBar extends KContainer implements ActionListener {
    protected Component toolBarComponents[][][];
    protected boolean toolBarStates[];
    protected Button toolBarShowButtons[];
    protected Button toolBarHideButtons[];


    static protected final int BAR_SEPARATOR_SIZE = 2;


    /**
     * Constructs a new tool bar with the specified components.
     *
     * @param toolBarComponents the components of the tool bar. the first
     *                          dimension classifies the horizontal bar;
     *                          the second dimension classifies the actions
     *                          with the vertical separator; and the third
     *                          dimension shows the actions.
     */
    public ToolBar(Component toolBarComponents[][][]) {
	this(toolBarComponents, true);
    }

    /**
     * Constructs a new tool bar with the specified components and flag
     * that determins to be showd or hidden initially.
     *
     * @param toolBarComponents the components of the tool bar. the first
     *                          dimension classifies the horizontal bar;
     *                          the second dimension classifies the actions
     *                          with the vertical separator; and the third
     *                          dimension shows the actions.
     * @param showToolBar       If true, shows the actions initially;
     *                          otherwise hides.
     */
    public ToolBar(Component toolBarComponents[][][], boolean showToolBar) {
	if (toolBarComponents == null)
	    throw new NullPointerException();
	if (toolBarComponents.length == 0)
	    throw new IllegalArgumentException("Null component");
	for (int i = 0; i < toolBarComponents.length; i++) {
	    Component comps2[][] = toolBarComponents[i];
	    if (comps2 == null || comps2.length == 0)
		throw new IllegalArgumentException("Null component");
	    for (int j = 0; j < comps2.length; j++) {
		Component comps[] = comps2[j];
		if (comps == null || comps.length == 0)
		    throw new IllegalArgumentException("Null component");
		for (int k = 0; k < comps.length; k++) {
		    if (comps[k] == null)
			throw new IllegalArgumentException("Null component");
		}
	    }
	}
	this.toolBarComponents = toolBarComponents;

	this.toolBarStates = new boolean[toolBarComponents.length];
	for (int i = 0; i < toolBarStates.length; i++) {
	    this.toolBarStates[i] = showToolBar;
	}

	this.toolBarShowButtons =
			createToolBarShowButtons(toolBarComponents.length);
	this.toolBarHideButtons =
			createToolBarHideButtons(toolBarComponents.length);
	initToolBar();
    }


    /**
     * Paints this component.
     */
    public void paint(Graphics g) {
	if (isShowing()) {
	    g.setColor(getBackground());
	    Dimension d = getSize();
	    g.fillRect(0, 0, d.width, d.height);
	    g.setColor(getForeground());
	}
	super.paint(g);
    }


    /**
     * Returns the number of horizontal bars.
     */
    public int getToolBarCount() {
	return toolBarComponents.length;
    }

    /**
     * Returns the horizontal bar at the specified index is showed or hidden.
     *
     * @see #setToolBarState(int, boolean)
     */
    public boolean getToolBarState(int index) {
	if (index < 0 || index >= toolBarComponents.length)
	    throw new ArrayIndexOutOfBoundsException(index);
	return toolBarStates[index];
    }

    /**
     * Sets the horizontal bar at the specified index to be showed or hidden.
     *
     * @see #getToolBarState(int)
     */
    public void setToolBarState(int index, boolean state) {
	if (index < 0 || index >= toolBarComponents.length)
	    throw new ArrayIndexOutOfBoundsException(index);
	if (toolBarStates[index] == state)
	    return;
	toolBarStates[index] = state;
	initToolBar();
    }

    /**
     * Invoked when an action occurs.
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(ActionEvent e) {
	Object obj = e.getSource();
	if (obj == null || !(obj instanceof Button))
	    return;
	Button b = (Button)obj;
	int index;
	index = indexOfButton(toolBarShowButtons, b);
	if (index >= 0) {
	    setToolBarState(index, true);
	    return;
	}
	index = indexOfButton(toolBarHideButtons, b);
	if (index >= 0) {
	    setToolBarState(index, false);
	    return;
	}
    }


    /**
     * Initializes the tool bar.
     */
    protected void initToolBar() {
	boolean valid = isValid();

	removeAll();

	Panel p = new Panel();

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	p.setLayout(gridbag);

	c.gridx = 0;

	int show = 0;
	int showIndices[] = new int[toolBarComponents.length];
	int hide = 0;
	int hideIndices[] = new int[toolBarComponents.length];
	for (int i = 0; i < toolBarComponents.length; i++) {
	    if (toolBarStates[i]) {
		showIndices[show++] = i;
	    }
	    else {
		hideIndices[hide++] = i;
	    }
	    toolBarShowButtons[i].getVButton().setState(false);
	    toolBarShowButtons[i].getVButton().setActive(false);
	    toolBarHideButtons[i].getVButton().setState(false);
	    toolBarHideButtons[i].getVButton().setActive(false);
	}

	if (show > 0) {
	    boolean top = true;
	    int width = getPreferredWidth(toolBarComponents[showIndices[0]]);
	    for (int s = 0; s < show; s++) {
		boolean bottom = false;
		int nextWidth = 0;
		if (s == (show - 1)) { // bottom most
		    if (hide > 0)
			bottom = true;
		}
		else {
		    nextWidth =
			getPreferredWidth(toolBarComponents[showIndices[s+1]]);
		    if (width >= nextWidth)
			bottom = true;
		}

		int i = showIndices[s];
		Component comps[][] = toolBarComponents[i];
		Button b = toolBarHideButtons[i];
		Component panel = createToolBarPanel(b, comps, top, bottom);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(panel, c);
		p.add(panel);

		top = !bottom;
		width = nextWidth;
	    }
	}

	if (hide > 0) {
	    Component comps[] = new Component[hide];
	    for (int h = 0; h < hide; h++) {
		comps[h] = toolBarShowButtons[hideIndices[h]];
	    }

	    Component panel = createPlainPanel(comps);

	    c.anchor = GridBagConstraints.WEST;
	    c.fill = GridBagConstraints.NONE;
	    gridbag.setConstraints(panel, c);
	    p.add(panel);
	}

	setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	add(p);

	if (valid) {
	    Component frame = getFrame();
	    if (frame != null) {
		frame.invalidate();
		frame.validate();
	    }
	    else {
		invalidate();
		validate();
	    }
	}
    }

    protected Button[] createToolBarShowButtons(int length) {
	return createToolBarButtons(length, VArrow.RIGHT, Button.LEFT);
    }

    protected Button[] createToolBarHideButtons(int length) {
	return createToolBarButtons(length, VArrow.DOWN, Button.NORTH);
    }

    protected Button[] createToolBarButtons(int length, int dir, int align) {
	Button buttons[] = new Button[length];
	for (int i = 0; i < length; i++) {
	    VArrow arrow = new VArrow(dir);
	    arrow.setSize(new Dimension(3, 3));
	    Button b = new Button(new VActiveButton(arrow));
	    b.setAlignment(align);
	    b.setActionCommand("");
	    b.addActionListener(this);
	    buttons[i] = b;
	}
	return buttons;
    }

    protected int indexOfButton(Button buttons[], Button b) {
	for (int i = 0; i < buttons.length; i++) {
	    if (b == buttons[i])
		return i;
	}
	return -1;
    }

    protected Component createPlainPanel(Component comps[]) {
	return createBarPanel(comps, null);
    }

    protected Component createRightBarPanel(Component comps[]) {
	return createBarPanel(comps, new Insets(0, 0, 0, BAR_SEPARATOR_SIZE));
    }

    protected Component createBarPanel(Component comps[], Insets insets) {
	Container cont;
	if (insets == null) {
	    cont = new Panel();
	}
	else {
	    cont = new BorderedPanel(new VPaneBorder(insets));
	}
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	cont.setLayout(gridbag);
	for (int i = 0; i < comps.length; i++) {
	    gridbag.setConstraints(comps[i], c);
	    cont.add(comps[i]);
	}
	return cont;
    }

    protected Component createToolBarPanel(Button b, Component comps[][],
					   boolean top, boolean bottom)
    {
	Container cont;
	if (!top && !bottom) {
	    cont = new Panel();
	}
	else {
	    Insets insets = new Insets((top    ? BAR_SEPARATOR_SIZE : 0), 0,
				       (bottom ? BAR_SEPARATOR_SIZE : 0), 0);
	    cont = new BorderedPanel(new VPaneBorder(insets));
	}
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	cont.setLayout(gridbag);

	BorderedPanel bp = new BorderedPanel(new VPaneBorder(
					new Insets(0, BAR_SEPARATOR_SIZE,
						   0, BAR_SEPARATOR_SIZE)));
	bp.setLayout(new BorderLayout());
	bp.add(b, BorderLayout.CENTER);

	c.fill = GridBagConstraints.VERTICAL;
	gridbag.setConstraints(bp, c);
	cont.add(bp);

	c.fill = GridBagConstraints.NONE;
	for (int i = 0; i < comps.length; i++) {
	    Component comp = createRightBarPanel(comps[i]);
	    gridbag.setConstraints(comp, c);
	    cont.add(comp);
	}

	return cont;
    }

    protected int getPreferredWidth(Component comps[][]) {
	int width = 0;
	for (int i = 0; i < comps.length; i++) {
	    for (int j = 0; j < comps[i].length; j++) {
		width += comps[i][j].getPreferredSize().width;
	    }
	}
	return width + (BAR_SEPARATOR_SIZE * comps.length);
    }


    /** Executes the examples. */
    static public void main(String args[]) {
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame("ToolBar");
	f.add(new TextArea(), java.awt.BorderLayout.CENTER);
	Component comps[][][] = {
	    { { new Button(new VActiveButton("one")),
		new Button(new VActiveButton("two")),
		new Button(new VActiveButton("three"))
	      }
	    },
	    { { new Button(new VActiveButton("four")),
		new Button(new VActiveButton("five"))
	      },
	      { new Button(new VActiveButton("six")),
		new Button(new VActiveButton("seven"))
	      }
	    },
	    { { new Button(new VActiveButton("eight")),
		new Button(new VActiveButton("nine"))
	      }
	    },
	};
	f.add(new ToolBar(comps), java.awt.BorderLayout.NORTH);
	f.pack();
	f.setVisible(true);
    }
}
