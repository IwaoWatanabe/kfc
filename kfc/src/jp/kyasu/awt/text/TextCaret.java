/*
 * TextCaret.java
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

import jp.kyasu.awt.AWTResources;
import jp.kyasu.awt.Timer;
import jp.kyasu.graphics.text.TextPositionInfo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The <code>TextCaret</code> class implements a caret showing in the
 * <code>TextEditView</code> object.
 *
 * @see 	jp.kyasu.awt.text.TextEditView
 *
 * @version 	16 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextCaret implements ActionListener, java.io.Serializable {
    protected int style;
    protected boolean doBlink;
    protected boolean caretShowing;
    protected Timer caretTimer;
    protected Component target;
    transient protected CaretContext context;

    static protected Toolkit DefaultToolkit = Toolkit.getDefaultToolkit();


    /**
     * The caret style constant for the bar caret.
     */
    static public final int BAR_CARET = 0;

    /**
     * The caret style constant for the hat caret.
     */
    static public final int HAT_CARET = 1;

    /**
     * The default caret style.
     */
    static public final int DEFAULT_CARET_STYLE =
	AWTResources.getResourceInteger("kfc.caret.style", BAR_CARET);

    /**
     * The default caret color.
     */
    static public final Color DEFAULT_CARET_COLOR =
	AWTResources.getResourceColor("kfc.caret.color", new Color(0, 0, 128));

    /**
     * The default flag that determines to blink the caret.
     */
    static public final boolean DEFAULT_DO_BLINK =
	AWTResources.getResourceBoolean("kfc.caret.doBlink", true);

    /**
     * The default interval of blinking.
     */
    static public final int BLINK_INTERVAL =
	AWTResources.getResourceInteger("kfc.caret.blinkInterval", 500);


    /** The context of the caret blinking. */
    static class CaretContext {
	Graphics g = null;
	Point offset;
	TextPositionInfo posInfo;
	Color caretColor;

	boolean isOk() {
	    return (g != null);
	}

	void initialize(Graphics g,
			Point offset,
			TextPositionInfo posInfo,
			Color caretColor)
	{
	    if (this.g != null) this.g.dispose();
	    this.g          = g.create();
	    this.offset     = new Point(offset);
	    this.posInfo    = posInfo;
	    this.caretColor = caretColor;
	}

	void dispose() {
	    if (g != null) { g.dispose(); g = null; }
	}
    }


    /**
     * Constructs a text caret with the default style and the default
     * flag determining to do blink.
     */
    public TextCaret() {
	this(DEFAULT_CARET_STYLE, DEFAULT_DO_BLINK);
    }

    /**
     * Constructs a text caret with the specified style and the default
     * flag determining to do blink.
     *
     * @param style the caret style.
     */
    public TextCaret(int style) {
	this(style, DEFAULT_DO_BLINK);
    }

    /**
     * Constructs a text caret with the default style and the specified
     * flag determining to do blink.
     *
     * @param doBlink the flag determining to do blink.
     */
    public TextCaret(boolean doBlink) {
	this(DEFAULT_CARET_STYLE, doBlink);
    }

    /**
     * Constructs a text caret with the specified style and flag determining
     * to do blink.
     *
     * @param style   the caret style.
     * @param doBlink the flag determining to do blink.
     */
    public TextCaret(int style, boolean doBlink) {
	setStyle(style);
	this.doBlink = doBlink;
	caretShowing = false;
	if (doBlink) {
	    caretTimer = new Timer(BLINK_INTERVAL, this);
	}
	else {
	    caretTimer = null;
	}
	context = new CaretContext();
    }


    /**
     * Tests if this caret does blinking.
     */
    public boolean doBlink() {
	return doBlink;
    }

    /**
     * Returns the style of this caret.
     */
    public int getStyle() {
	return style;
    }

    /**
     * Sets the style of this caret.
     *
     * @param style the caret style.
     * @see #BAR_CARET
     * @see #HAT_CARET
     */
    protected void setStyle(int style) {
	switch (style) {
	case BAR_CARET:
	case HAT_CARET:
	    this.style = style;
	    return;
	}
	throw new IllegalArgumentException("improper style: " + style);
    }

    /**
     * Invoked when a timer action occurs.
     */
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == caretTimer) {
	    safeToggleCaret();
	}
    }

    /**
     * Starts the blinking.
     */
    public synchronized void start() {
	if (doBlink) {
	    caretTimer.start();
	}
    }

    /**
     * Stops the blinking.
     */
    public synchronized void stop() {
	if (doBlink) {
	    caretTimer.stop();
	    context.dispose();
	}
    }

    /**
     * Tests if this caret is blinking.
     */
    public synchronized boolean isBlinking() {
	return (doBlink ? caretTimer.isRunning() : false);
    }

    /**
     * Shows this caret with the specified graphics, offset, text position,
     * and color.
     *
     * @param g          the graphics object.
     * @param offset     the offset of the graphics object.
     * @param posInfo    the text position to show the caret.
     * @param caretColor the caret color.
     */
    public synchronized void showCaret(Graphics g,
				       Point offset,
				       TextPositionInfo posInfo,
				       Color caretColor)
    {
	if (!doBlink) {
	    safeToggleCaret(g, offset, posInfo, caretColor);
	}
	else {
	    if (caretShowing) {
		safeToggleCaret();
	    }
	    context.initialize(g, offset, posInfo, caretColor);
	    safeToggleCaret();
	    caretShowing = true;
	}
    }

    /**
     * Hides this caret with the specified graphics, offset, text position,
     * and color.
     *
     * @param g          the graphics object.
     * @param offset     the offset of the graphics object.
     * @param posInfo    the text position to hide the caret.
     * @param caretColor the caret color.
     */
    public synchronized void hideCaret(Graphics g,
				       Point offset,
				       TextPositionInfo posInfo,
				       Color caretColor)
    {
	if (!doBlink) {
	    safeToggleCaret(g, offset, posInfo, caretColor);
	}
	else {
	    if (caretShowing) {
		safeToggleCaret();
		caretShowing = false;
	    }
	    context.dispose();
	}
    }


    /**
     * Returns the target component into whitch the caret shows.
     */
    protected Component getTarget() {
	return target;
    }

    /**
     * Sets the target component into whitch the caret shows.
     */
    protected synchronized void setTarget(Component target) {
	this.target = target;
    }

    /**
     * Shows or hides this caret safely with the caret context.
     */
    protected synchronized void safeToggleCaret() {
	if (context.isOk()) {
	    safeToggleCaret(context.g, context.offset,
			    context.posInfo, context.caretColor);
	}
    }

    /**
     * Shows ot hides this caret safely with the specified graphics, offset,
     * text position, and color.
     */
    protected void safeToggleCaret(Graphics g,
				   Point offset,
				   TextPositionInfo posInfo,
				   Color caretColor)
    {
	if (target == null || !target.isShowing())
	    return;
	if (g != null && offset != null &&
	    posInfo != null && caretColor != null)
	{
	    caretShowing = !caretShowing;
	    toggleCaret(g, offset, posInfo, caretColor);
	}
    }

    /**
     * Shows ot hides this caret with the specified graphics, offset, text
     * position, and color.
     */
    protected void toggleCaret(Graphics g,
			       Point offset,
			       TextPositionInfo posInfo,
			       Color caretColor)
    {
	switch (style) {
	case HAT_CARET:
	    toggleHatCaret(g, offset, posInfo, caretColor);
	    break;
	case BAR_CARET:
	default:
	    toggleBarCaret(g, offset, posInfo, caretColor);
	    break;
	}
    }

    /**
     * Shows ot hides this caret as a bar caret with the specified graphics,
     * offset, text position, and color.
     */
    protected void toggleBarCaret(Graphics g,
				  Point offset,
				  TextPositionInfo posInfo,
				  Color caretColor)
    {
	g.setXORMode(Color.white);
	g.setColor(caretColor);
	g.drawLine(posInfo.x + offset.x,
		   posInfo.y + offset.y,
		   posInfo.x + offset.x,
		   posInfo.y + offset.y + posInfo.lineHeight +
					posInfo.paragraphStyle.getLineSpace());
	g.setPaintMode();

	DefaultToolkit.sync();
    }

    /**
     * Shows ot hides this caret as a hat caret with the specified graphics,
     * offset, text position, and color.
     */
    protected void toggleHatCaret(Graphics g,
				  Point offset,
				  TextPositionInfo posInfo,
				  Color caretColor)
    {
	g.setXORMode(Color.white);
	g.setColor(caretColor);
	int x = posInfo.x + offset.x;
	int y = posInfo.y + offset.y + posInfo.lineHeight - posInfo.baseline;
	g.drawLine(x, y, x, y);
	g.drawLine(x - 1, y + 1, x + 1, y + 1);
	g.drawLine(x - 2, y + 2, x + 2, y + 2);
	g.drawLine(x - 2, y + 3, x + 2, y + 3);
	g.setPaintMode();

	DefaultToolkit.sync();
    }


    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	s.defaultReadObject();
	context = new CaretContext();
    }
}
