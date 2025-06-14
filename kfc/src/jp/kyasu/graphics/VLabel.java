/*
 * VLabel.java
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

package jp.kyasu.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;

/**
 * The <code>VLabel</code> class implements the visual label that
 * acts as a label model. The label has a enabled state and a focused
 * state. The label creates different visual presentations according to
 * the enabled state and the focused state. If the label is disabled,
 * the grayed label is displayed. If the label is focused, the label is
 * displayed with a dashed line border.
 *
 * @see		jp.kyasu.awt.Label
 *
 * @version 	12 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class VLabel extends VWrapper {
    /** The enabled state. */
    protected boolean enabled;

    /** The foreground color used when the label is disabled. */
    protected Color disabledForeground;

    /** The background color used when the label is disabled. */
    protected Color disabledBackground;

    /** The disabled visual presentation. */
    transient protected VImage disabledLabel1;

    /** The disabled visual presentation. */
    transient protected VImage disabledLabel2;

    /** The focused state. */
    protected boolean focused;

    /** The dashed line border painted when the label is focused. */
    protected VDashedBorder focusedBorder;


    /**
     * Constructs an empty label.
     */
    public VLabel() {
	this("");
    }

    /**
     * Constructs a label with the specified string.
     *
     * @param str the string.
     */
    public VLabel(String str) {
	this(new Text(str));
    }

    /**
     * Constructs a label with the specified text.
     *
     * @param text the text.
     */
    public VLabel(Text text) {
	this(new VText(text));
    }

    /**
     * Constructs a label with the specified visual object.
     *
     * @param visualizable the visual object.
     */
    public VLabel(Visualizable visualizable) {
	this(visualizable, true, false);
    }

    /**
     * Constructs a label with the specified visual object and the
     * specified states.
     *
     * @param visualizable the visual object.
     * @param enabled      the enabled state.
     * @param focused      the focused state.
     */
    protected VLabel(Visualizable visualizable,
		     boolean enabled, boolean focused)
    {
	super(visualizable);
	this.enabled = enabled;
	disabledForeground = Color.black;
	disabledBackground = Color.lightGray;
	disabledLabel1 = null;
	disabledLabel2 = null;
	this.focused = focused;
	focusedBorder = new VDashedBorder();
    }


    /**
     * Creates a new label by replicating this label with a new visual
     * object associated with it.
     *
     * @param  visualizable the visual object for the new label.
     * @return a new label.
     */
    public VLabel deriveLabel(Visualizable visualizable) {
	if (visualizable == null)
	    throw new NullPointerException();
	return new VLabel(visualizable, enabled, focused);
    }

    /**
     * Sets the visual content of this label to be the specified
     * visual object.
     *
     * @param visualizable the visual object.
     */
    public void setVisualizable(Visualizable visualizable) {
	if (visualizable == null)
	    throw new NullPointerException();
	this.visualizable = visualizable;
	clearDisabledVisualizable();
    }

    /**
     * Checks if this label is focused.
     *
     * @return <code>true</code> if this label is focused;
     *         <code>false</code> otherwise.
     */
    public boolean isFocused() {
	return focused;
    }

    /**
     * Sets the focused state to be the specified boolean.
     *
     * @param b the boolean.
     */
    public void setFocused(boolean b) {
	focused = b;
    }

    /**
     * Checks if this label is enabled.
     *
     * @return <code>true</code> if this label is enabled;
     *         <code>false</code> otherwise.
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Sets the enabled state to be the specified boolean.
     *
     * @param b the boolean.
     */
    public void setEnabled(boolean b) {
	enabled = b;
    }

    /**
     * Returns the foreground color when the label is disabled.
     *
     * @return the foreground color when the label is disabled.
     */
    public Color getDisabledForeground() {
	return disabledForeground;
    }

    /**
     * Sets the foreground color when the label is disabled
     * to be the specified color.
     *
     * @param c the color.
     */
    public void setDisabledForeground(Color c) {
	if (c == null)
	    throw new NullPointerException();
	if (disabledForeground.equals(c))
	    return;
	disabledForeground = c;
	clearDisabledVisualizable();
    }

    /**
     * Returns the background color when the label is disabled.
     *
     * @return the background color when the label is disabled.
     */
    public Color getDisabledBackground() {
	return disabledBackground;
    }

    /**
     * Sets the background color when the label is disabled
     * to be the specified color.
     *
     * @param c the color.
     */
    public void setDisabledBackground(Color c) {
	if (c == null)
	    throw new NullPointerException();
	if (disabledBackground.equals(c))
	    return;
	disabledBackground = c;
	clearDisabledVisualizable();
    }

    /**
     * Clears the cached visual presentation used when the label is disabled.
     */
    public synchronized void clearDisabledVisualizable() {
	disabledLabel1 = null;
	disabledLabel2 = null;
    }

    /**
     * Returns the size of this label.
     * @see jp.kyasu.graphics.Visualizable#getSize()
     */
    public Dimension getSize() {
	return getFocusedSize();
    }

    /**
     * Returns the size of the visual content with the focused (dashed line)
     * border.
     */
    protected Dimension getFocusedSize() {
	Dimension d = visualizable.getSize();
	Insets insets = focusedBorder.getInsets();
	int width  = d.width  + (insets.left + insets.right);
	int height = d.height + (insets.top + insets.bottom);
	return new Dimension(width, height);
    }

    /**
     * Resizes the label to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	setFocusedSize(d);
    }

    /**
     * Resizes the visual content with the focused (dashed line) border
     * to the specified dimension.
     */
    public void setFocusedSize(Dimension d) {
	Dimension size = getFocusedSize();
	if (d.width == size.width && d.height == size.height)
	    return;
	Insets insets = focusedBorder.getInsets();
	int width  = d.width  - (insets.left + insets.right);
	int height = d.height - (insets.top + insets.bottom);
	if (width < 0) width = 0;
	if (height < 0) height = 0;
	visualizable.setSize(new Dimension(width, height));
	clearDisabledVisualizable();
    }

    /**
     * Paints the label at the specified location.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	paint(g, p, null);
    }

    /**
     * Paints the label at the specified location with the component.
     *
     * @param g    the graphics.
     * @param p    the location.
     * @param comp the component used to make the disabled presentation.
     */
    public void paint(Graphics g, Point p, Component comp) {
	paint(g, p, comp, true);
    }

    /**
     * Returns a clone of this label.
     */
    public Object clone() {
	VLabel vlabel = (VLabel)super.clone();
	vlabel.enabled = enabled;
	vlabel.disabledForeground = new Color(disabledForeground.getRGB());
	vlabel.disabledBackground = new Color(disabledBackground.getRGB());
	vlabel.disabledLabel1 = null;
	vlabel.disabledLabel2 = null;
	vlabel.focused = focused;
	vlabel.focusedBorder = focusedBorder; // share
	return vlabel;
    }

    /**
     * Paints the label at the specified location with the component.
     *
     * @param g      the graphics.
     * @param p      the location.
     * @param comp   the component used to make the disabled presentation.
     * @param raised if false, paints the visual content at the sunk location.
     */
    protected void paint(Graphics g, Point p, Component comp, boolean raised) {
	Dimension d = visualizable.getSize();
	Insets insets = focusedBorder.getInsets();
	if (d.width > 0 && d.height > 0) {
	    Point ip = new Point(p.x + insets.left, p.y + insets.top);
	    if (enabled) {
		if (raised)
		    paintEnabled(g, ip);
		else
		    paintEnabled(g, new Point(ip.x + 1, ip.y + 1));
	    }
	    else {
		paintDisabled(g, ip, comp);
	    }
	}
	if (focused) {
	    int width  = d.width  + (insets.left + insets.right);
	    int height = d.height + (insets.top + insets.bottom);
	    if (width > 0 && height > 0) {
		paintFocused(g, p);
	    }
	}
    }

    /**
     * Paints the label at the specified location when the label is enabled.
     */
    protected void paintEnabled(Graphics g, Point p) {
	visualizable.paint(g, p);
    }

    /**
     * Paints the label at the specified location when the label is disabled.
     */
    protected void paintDisabled(Graphics g, Point p) {
	paintDisabled(g, p, null);
    }

    /**
     * Paints the label at the specified location with the component
     * when the label is disabled.
     */
    protected void paintDisabled(Graphics g, Point p, Component comp) {
	if (disabledLabel1 == null || disabledLabel2 == null) {
	    makeDisabledLabel(comp);
	}
	disabledLabel1.paint(g, new Point(p.x + 1, p.y + 1));
	disabledLabel2.paint(g, new Point(p.x, p.y));
    }

    /**
     * Paints the focused (dashed line) border at the specified location
     * when the label is focused.
     */
    protected void paintFocused(Graphics g, Point p) {
	Dimension d = getFocusedSize();
	focusedBorder.paint(g, p.x, p.y, d.width, d.height);
    }

    /**
     * Makes the visual presentation used when the label is disabled.
     */
    protected synchronized void makeDisabledLabel(Component comp) {
	Dimension d = visualizable.getSize();
	java.awt.Frame frame = null;
	if (comp == null) {
	    frame = new java.awt.Frame();
	    frame.setVisible(true);
	    comp = frame;
	}
	Image img = comp.createImage(d.width, d.height);
	Graphics g = img.getGraphics();
	g.setColor(Color.white);
	g.fillRect(0, 0, d.width, d.height);
	g.setColor(disabledForeground);
	visualizable.paint(g, new Point(0, 0));
	Image img1 = Toolkit.getDefaultToolkit().createImage(
		new FilteredImageSource(
			img.getSource(),
			new OpaqueImageFilter(disabledBackground.brighter())));
	Image img2 = Toolkit.getDefaultToolkit().createImage(
		new FilteredImageSource(
			img.getSource(),
			new OpaqueImageFilter(disabledBackground.darker())));
	disabledLabel1 = new VImage(img1, false);
	disabledLabel2 = new VImage(img2, false);
	g.dispose();
	if (frame != null) {
	    frame.setVisible(false);
	    frame.dispose();
	}
    }
}
