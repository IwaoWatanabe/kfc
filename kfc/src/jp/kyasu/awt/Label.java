/*
 * Label.java
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
import jp.kyasu.graphics.VLabel;
import jp.kyasu.graphics.VRichText;
import jp.kyasu.graphics.VText;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A <code>Label</code> object is a component for placing text in a
 * container. A label displays a single line of read-only text.
 * The text can be changed by the application, but a user cannot edit it
 * directly.
 * <p>
 * A Label can also display a visual object in a container.
 * <p>
 * For example:
 * <pre>
 *     setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
 *     add(new Label("A Label"));
 *     add(new Label(
 *             new Text(
 *                 "Another Label",
 *                 new TextStyle("Dialog", Font.BOLD, 12, Color.red))));
 *     add(new Label(new VImage("image.gif")));
 * </pre>
 *
 * @see 	jp.kyasu.graphics.Visualizable
 * @see 	jp.kyasu.graphics.VLabel
 *
 * @version 	21 Aug 1998
 * @author 	Kazuki YASUMATSU
 */
public class Label extends KComponent {
    /**
     * The label's alignment.
     */
    protected int alignment;

    /**
     * The visual label of this label.
     */
    protected VLabel label;


    /**
     * Indicates that the label should be left justified.
     */
    static public final int LEFT   = java.awt.Label.LEFT;

    /**
     * Indicates that the label should be centered.
     */
    static public final int CENTER = java.awt.Label.CENTER;

    /**
     * Indicates that the label should be right justified.
     */
    static public final int RIGHT  = java.awt.Label.RIGHT;

    /** Indicates that the label should be top. */
    static public final int NORTH      = 11;

    /** Indicates that the label should be top-right. */
    static public final int NORTHEAST  = 12;

    /** Indicates that the label should be right. */
    static public final int EAST       = RIGHT;

    /** Indicates that the label should be bottom-right. */
    static public final int SOUTHEAST  = 13;

    /** Indicates that the label should be bottom. */
    static public final int SOUTH      = 14;

    /** Indicates that the label should be bottom-left. */
    static public final int SOUTHWEST  = 15;

    /** Indicates that the label should be left. */
    static public final int WEST       = LEFT;

    /** Indicates that the label should be top-left. */
    static public final int NORTHWEST  = 16;


    /**
     * Constructs an empty label.
     */
    public Label() {
	this("");
    }

    /**
     * Constructs a new label with the specified string, left justified.
     *
     * @param str the string that the label presents.
     */
    public Label(String str) {
	this((str == null ? "" : str), LEFT);
    }

    /**
     * Constructs a new label that presents the specified string with the
     * specified alignment.
     *
     * @param str       the string that the label presents.
     * @param alignment the alignment value.
     */
    public Label(String str, int alignment) {
	this(new Text(str == null ? "" : str), alignment);
    }

    /**
     * Constructs a new label with the specified text, left justified.
     *
     * @param text the text that the label presents.
     */
    public Label(Text text) {
	this(text, LEFT);
    }

    /**
     * Constructs a new label that presents the specified string with the
     * specified alignment.
     *
     * @param text      the text that the label presents.
     * @param alignment the alignment value.
     */
    public Label(Text text, int alignment) {
	this(new VText(text), alignment);
    }

    /**
     * Constructs a new label with the specified visual object, left justified.
     *
     * @param visualizable the visual object that the label presents.
     */
    public Label(Visualizable visualizable) {
	this(visualizable, LEFT);
    }

    /**
     * Constructs a new label with the specified visual object with the
     * specified alignment.
     *
     * @param visualizable the visual object that the label presents.
     * @param alignment    the alignment value.
     */
    public Label(Visualizable visualizable, int alignment) {
	this(new VLabel(visualizable), alignment);
    }

    /**
     * Constructs a new label with the specified visual label, left justified.
     *
     * @param label the visual label that the label presents.
     */
    public Label(VLabel label) {
	this(label, LEFT);
    }

    /**
     * Constructs a new label with the specified visual label with the
     * specified alignment.
     *
     * @param label     the visual label that the label presents.
     * @param alignment the alignment value.
     */
    public Label(VLabel label, int alignment) {
	if (label == null)
	    throw new NullPointerException();
	//setDoubleBuffered(true);
	setVLabel(label);
	setAlignment(alignment);
    }


    /**
     * Sets the font of this component.
     */
    public synchronized void setFont(Font f) {
	super.setFont(f);
	Visualizable visualizable = label.getVisualizable();
	if (visualizable instanceof VText) {
	    VText vtext = (VText)visualizable;
	    Text text = vtext.getText();
	    if (!text.isEmpty()) {
		text = (Text)text.clone();
		text.replaceStyle(0, text.length(), new TextStyle(f));
		setVLabel(label.deriveLabel(new VText(text)));
	    }
	}
    }

    /**
     * Gets the preferred size of this component.
     */
    public Dimension getPreferredSize() {
	return label.getSize();
    }

    /**
     * Gets the mininimum size of this component.
     */
    public Dimension getMinimumSize() {
	return label.getSize();
    }

    /**
     * Sets the foreground color of this component.
     */
    public synchronized void setForeground(Color c) {
	super.setForeground(c);
	if (label != null && c != null) {
	    label.setDisabledForeground(c);
	    repaintNow();
	}
    }

    /**
     * Sets the background color of this component.
     */
    public synchronized void setBackground(Color c) {
	super.setBackground(c);
	if (label != null && c != null) {
	    label.setDisabledBackground(c);
	    repaintNow();
	}
    }

    /**
     * Enables or disables this component.
     */
    public synchronized void setEnabled(boolean b) {
	if (isEnabled() == b)
	    return;
	super.setEnabled(b);
	label.setEnabled(b);
	repaintNow();
    }

    /**
     * Paints this label.
     */
    protected void paintOn(Graphics g) {
	Dimension vSize = getSize();
	g.setColor(getButtonBackground());
	Rectangle r = g.getClipBounds();
	if (r != null) {
	    g.fillRect(r.x, r.y, r.width, r.height);
	}
	else {
	    g.fillRect(0, 0, vSize.width, vSize.height);
	}
	g.setColor(getForeground());

	Dimension lSize = label.getSize();
	int x;
	switch (alignment) {
	case CENTER:
	case NORTH:
	case SOUTH:
	    x = (vSize.width - lSize.width) / 2;
	    break;
	case RIGHT:
	//case EAST:
	case NORTHEAST:
	case SOUTHEAST:
	    x = vSize.width - lSize.width;
	    break;
	case LEFT:
	//case WEST:
	case NORTHWEST:
	case SOUTHWEST:
	default:
	    x = 0;
	    break;
	}
	int y;
	switch (alignment) {
	case CENTER:
	case RIGHT:
	case LEFT:
	//case EAST:
	//case WEST:
	default:
	    y = (vSize.height - lSize.height) / 2;
	    break;
	case SOUTH:
	case SOUTHEAST:
	case SOUTHWEST:
	    y = vSize.height - lSize.height;
	    break;
	case NORTH:
	case NORTHEAST:
	case NORTHWEST:
	    y = 0;
	    break;
	}
	label.paint(g, new Point(x, y), this);
    }

    /**
     * Moves and resizes this component.
     */
    public synchronized void setBounds(int x, int y, int width, int height) {
	super.setBounds(x, y, width, height);
	setLabelSize(width, height);
    }

    /**
     * Returns the background color for the button.
     */
    protected Color getButtonBackground() {
	return getBackground();
    }

    /**
     * Sets the size of the visual label to be the specified width and height.
     */
    protected void setLabelSize(int width, int height) {
	/*
	if (label.isResizable()) {
	    label.setSize(new Dimension(width, height));
	}
	*/
    }


    // ======== java.awt.Label APIs ========

    /**
     * Gets the current alignment of this label.
     * @see #setAlignment(int)
     */
    public int getAlignment() {
	return alignment;
    }

    /**
     * Sets the alignment for this label to the specified alignment.
     *
     * @param     alignment the alignment to be set.
     * @exception IllegalArgumentException if an improper value for
     *            <code>alignment</code> is given.
     * @see #getAlignment()
     */
    public synchronized void setAlignment(int alignment) {
	switch (alignment) {
	case LEFT:
	case CENTER:
	case RIGHT:
	//case EAST:
	//case WEST:
	case NORTH:
	case NORTHEAST:
	case NORTHWEST:
	case SOUTH:
	case SOUTHEAST:
	case SOUTHWEST:
	    this.alignment = alignment;
	    repaintNow();
	    return;
	}
	throw new IllegalArgumentException("improper alignment: " + alignment);
    }

    /**
     * Gets the string of this label.
     *
     * @see #setText(java.lang.String)
     */
    public String getText() {
	return getTEXT().toString();
    }

    /**
     * Sets the text for this label to the specified string.
     *
     * @param str the string that this label presents.
     * @see #getText()
     */
    public void setText(String str) {
	setText(new Text(str == null ? "" : str));
    }


    // ======== Enhanced APIs ========

    /**
     * Gets the text object of this label.
     *
     * @return the text object, or empty text object if the visual object
     *         of this label does not contain the text object.
     * @see #setTEXT(jp.kyasu.graphics.Text)
     * @see #setText(jp.kyasu.graphics.Text)
     */
    public synchronized Text getTEXT() {
	Visualizable visualizable = label.getVisualizable();
	if (visualizable instanceof VText) {
	    return ((VText)visualizable).getText();
	}
	else if (visualizable instanceof VRichText) {
	    return ((VRichText)visualizable).getRichText().getText();
	}
	else {
	    return new Text();
	}
    }

    /**
     * Sets the text object for this label to the specified text object.
     *
     * @param text the text object that this label presents.
     * @see #getTEXT()
     */
    public void setTEXT(Text text) {
	setText(text);
    }

    /**
     * Sets the text object for this label to the specified text object.
     *
     * @param text the text object that this label presents.
     * @see #getTEXT()
     */
    public void setText(Text text) {
	setVisualizable(new VText(text));
    }

    /**
     * Gets the visual object of this label.
     *
     * @return the visual object of this label.
     * @see #setVisualizable(jp.kyasu.graphics.Visualizable)
     */
    public Visualizable getVisualizable() {
	return label.getVisualizable();
    }

    /**
     * Sets the visual object for this label to the specified visual object.
     *
     * @param visualizable the visual object that this label presents.
     * @see #getVisualizable()
     */
    public void setVisualizable(Visualizable visualizable) {
	setVLabel(label.deriveLabel(visualizable));
    }

    /**
     * Gets the visual label of this label.
     *
     * @return the visual label of this label.
     * @see #setVLabel(jp.kyasu.graphics.VLabel)
     */
    public VLabel getVLabel() {
	return label;
    }

    /**
     * Sets the visual label for this label to the specified viaul label.
     *
     * @param label the visual label that this label presents.
     * @see #getVLabel()
     */
    public synchronized void setVLabel(VLabel label) {
	if (label == null)
	    throw new NullPointerException();
	this.label = label;
	this.label.setEnabled(isEnabled());
	if (getForeground() != null)
	    this.label.setDisabledForeground(getForeground());
	if (getBackground() != null)
	    this.label.setDisabledBackground(getBackground());
	invalidate();
	repaintNow();
    }
}
