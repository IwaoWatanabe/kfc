/*
 * ToolTip.java
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
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.Visualizable;
import jp.kyasu.graphics.VBorderedWrapper;
import jp.kyasu.graphics.VClipWrapper;
import jp.kyasu.graphics.VPlainBorder;
import jp.kyasu.graphics.VText;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * A <code>ToolTip</code> object is used to display a "Tip" for a component.
 *
 * @see 	jp.kyasu.awt.KComponent#setToolTipText(java.awt.String)
 * @see 	jp.kyasu.awt.KComponent#setToolTipText(jp.kyasu.graphics.Text)
 * @see 	jp.kyasu.awt.KComponent#setToolTipVisual(jp.kyasu.graphics.Visualizable)
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class ToolTip extends PopupPanel implements ActionListener {
    protected String string;
    protected Visualizable visual;
    protected Timer timer;
    protected int x = 0;
    protected int y = 0;
    transient protected Component origin = null;


    /**
     * The delay milliseconds to display a tooltip.
     */
    static public final int TOOLTIP_DELAY =
	AWTResources.getResourceInteger("kfc.tooltip.delay", 500);

    /**
     * The foreground color of the tooltip.
     */
    static public final Color TOOLTIP_FOREGROUND_COLOR =
	AWTResources.getResourceColor("kfc.tooltip.foreground", Color.black);

    /**
     * The background color of the tooltip.
     */
    static public final Color TOOLTIP_BACKGROUND_COLOR =
	AWTResources.getResourceColor("kfc.tooltip.background",
				      new Color(255, 255, 192));


    /**
     * Constructs a new tooltip with the specified string.
     *
     * @param string the string to display of the tooltip.
     */
    public ToolTip(String string) {
	super();
	setForeground(TOOLTIP_FOREGROUND_COLOR);
	setBackground(TOOLTIP_BACKGROUND_COLOR);
	setToolTipText(string);
	timer = new Timer(TOOLTIP_DELAY, this);
	timer.setRepeats(false);
    }

    /**
     * Constructs a new tooltip with the specified text object.
     *
     * @param text the text object to display of the tooltip.
     */
    public ToolTip(Text text) {
	super();
	setForeground(TOOLTIP_FOREGROUND_COLOR);
	setBackground(TOOLTIP_BACKGROUND_COLOR);
	setToolTipText(text);
	timer = new Timer(TOOLTIP_DELAY, this);
	timer.setRepeats(false);
    }

    /**
     * Constructs a new tooltip with the specified visual object.
     *
     * @param visual the visual object to display of the tooltip.
     */
    public ToolTip(Visualizable visual) {
	this(visual, "");
    }

    /**
     * Constructs a new tooltip with the specified visual object and string.
     *
     * @param visual        the visual object to display of the tooltip.
     * @param toolTipString An additional tooltip string.
     */
    public ToolTip(Visualizable visual, String toolTipString) {
	super();
	setForeground(TOOLTIP_FOREGROUND_COLOR);
	setBackground(TOOLTIP_BACKGROUND_COLOR);
	setToolTipVisual(visual, toolTipString);
	timer = new Timer(TOOLTIP_DELAY, this);
	timer.setRepeats(false);
    }


    /**
     * Return the tooltip string.
     */
    public String getToolTipText() {
	return string;
    }

    /**
     * Return the visual object to display of this tooltip.
     */
    public Visualizable getToolTipVisual() {
	return visual;
    }

    /**
     * Sets the string to display of this tooltip.
     *
     * @param string The string to display of this tooltip.
     */
    public void setToolTipText(String string) {
	if (string == null)
	    throw new NullPointerException();
	setToolTipText(new Text(string));
    }

    /**
     * Sets the text object to display of this tooltip.
     *
     * @param text The text object to display of this tooltip.
     */
    public void setToolTipText(Text text) {
	if (text == null)
	    throw new NullPointerException();
	Visualizable v = new VText(text);
	v = new VClipWrapper(v, v.getSize().width + 4,
				v.getSize().height + 2);
	v = new VBorderedWrapper(v, new VPlainBorder(new Insets(1, 1, 1, 1)));
	setToolTipVisual(v, text.toString());
    }

    /**
     * Sets the visual object to display of this tooltip.
     *
     * @param visual The visual object to display of this tooltip.
     */
    public void setToolTipVisual(Visualizable visual) {
	setToolTipVisual(visual, "");
    }

    /**
     * Sets the visual object to display of this tooltip.
     *
     * @param visual      The visual object to display of this tooltip.
     * @param toolTipText An additional tooltip string.
     */
    public synchronized void setToolTipVisual(Visualizable visual,
					      String toolTipText)
    {
	if (visual == null || toolTipText == null)
	    throw new NullPointerException();

	boolean showing = isShowing();
	if (showing) {
	    hidePopup();
	}

	string = toolTipText;
	this.visual = visual;
	setSize(this.visual.getSize());

	if (showing) {
	    showPopup();
	}
    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == timer) {
	    showPopup();
	}
    }

    public void paint(Graphics g) {
	visual.paint(g, new Point(0, 0));
    }

    /**
     * Informs the hint of the location for this pop-up tooltip.
     */
    public void setPopupLocationHint(int x, int y) {
	this.x = x;
	this.y = y;
    }

    /**
     * Shows this pop-up tooltip at the x, y position relative to an
     * origin component after the specified delay milliseconds.
     *
     * @param origin the component which defines the coordinate space.
     * @param x      the x coordinate position to pop-up tooltip.
     * @param y      the y coordinate position to pop-up tooltip.
     * @param delay  the delay milliseconds.
     */
    public void showPopup(Component origin, int x, int y, int delay) {
	if (!origin.isShowing()) {
	    throw new RuntimeException("origin not showing on screen");
	}
	this.origin = origin;
	this.x = x;
	this.y = y;
	timer.setDelay(delay);
	timer.restart();
    }

    /**
     * Hides the pop-up tooltip.
     */
    public void hidePopup() {
	timer.stop();
	super.hidePopup();
    }

    protected void showPopup() {
	if (origin != null && origin.isShowing()) {
	    showPopup(origin, x, y);
	}
    }
}
