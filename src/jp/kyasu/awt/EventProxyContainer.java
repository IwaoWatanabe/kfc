/*
 * EventProxyContainer.java
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
import jp.kyasu.graphics.Visualizable;

import java.awt.Component;
import java.awt.event.*;

/**
 * A <code>EventProxyContainer</code> is a container that delegates events
 * from an event source to the listeners of this container.
 * The event source should be a lightweight component contained by this
 * container.
 *
 * @version 	19 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public abstract class EventProxyContainer extends KContainer
       implements FocusListener, KeyListener, MouseListener, MouseMotionListener
{
    /**
     * Gets the event source component.
     */
    abstract protected Component getEventSource();

    public synchronized void addFocusListener(FocusListener l) {
	super.addFocusListener(l);
	Component c = getEventSource();
	if (c != null) {
	    c.addFocusListener(this);
	}
    }

    public synchronized void addKeyListener(KeyListener l) {
	super.addKeyListener(l);
	Component c = getEventSource();
	if (c != null) {
	    c.addKeyListener(this);
	}
    }

    public synchronized void addMouseListener(MouseListener l) {
	super.addMouseListener(l);
	Component c = getEventSource();
	if (c != null) {
	    c.addMouseListener(this);
	}
    }

    public synchronized void addMouseMotionListener(MouseMotionListener l) {
	super.addMouseMotionListener(l);
	Component c = getEventSource();
	if (c != null) {
	    c.addMouseMotionListener(this);
	}
    }

    public void focusGained(FocusEvent e) { delegateFocusEvent(e); }
    public void focusLost(FocusEvent e)   { delegateFocusEvent(e); }
    protected void delegateFocusEvent(FocusEvent e) {
	processFocusEvent(new FocusEvent(this, e.getID(), e.isTemporary()));
    }

    public void keyTyped(KeyEvent e)    { delegateKeyEvent(e); }
    public void keyPressed(KeyEvent e)  { delegateKeyEvent(e); }
    public void keyReleased(KeyEvent e) { delegateKeyEvent(e); }
    protected void delegateKeyEvent(KeyEvent e) {
	processKeyEvent(new KeyEvent(this, e.getID(), e.getWhen(),
				     e.getModifiers(), e.getKeyCode(),
				     e.getKeyChar()));
    }

    public void mouseClicked(MouseEvent e)  { delegateMouseEvent(e); }
    public void mousePressed(MouseEvent e)  { delegateMouseEvent(e); }
    public void mouseReleased(MouseEvent e) { delegateMouseEvent(e); }
    public void mouseEntered(MouseEvent e)  { delegateMouseEvent(e); }
    public void mouseExited(MouseEvent e)   { delegateMouseEvent(e); }
    public void mouseDragged(MouseEvent e)  { delegateMouseEvent(e); }
    public void mouseMoved(MouseEvent e)    { delegateMouseEvent(e); }
    protected void delegateMouseEvent(MouseEvent e) {
	processMouseEvent(new MouseEvent(this, e.getID(), e.getWhen(),
					 e.getModifiers(), e.getX(), e.getY(),
					 e.getClickCount(),
					 e.isPopupTrigger()));
    }


    /**
     * Return the tooltip string that has been set with
     * <code>setToolTipText()</code>.
     *
     * @return the string of the tool tip.
     */
    public String getToolTipText() {
	Component c = getEventSource();
	if (c != null && c instanceof KComponent) {
	    return ((KComponent)c).getToolTipText();
	}
	else {
	    return null;
	}
    }

    /**
     * Return the tooltip visual object that has been set with
     * <code>setToolTipVisual()</code>.
     *
     * @return the visual object of the tool tip.
     */
    public Visualizable getToolTipVisual() {
	Component c = getEventSource();
	if (c != null && c instanceof KComponent) {
	    return ((KComponent)c).getToolTipVisual();
	}
	else {
	    return null;
	}
    }

    /**
     * Registers the string to display in a ToolTip.
     *
     * @param string The string to display when the cursor lingers over the
     *               component. If string is null, then it turns off tool tip
     *               for this component.
     */
    public void setToolTipText(String string) {
	Component c = getEventSource();
	if (c != null && c instanceof KComponent) {
	    ((KComponent)c).setToolTipText(string);
	}
    }

    /**
     * Registers the text object to display in a ToolTip.
     *
     * @param text The text object to display when the cursor lingers over
     *             the component. If text is null, then it turns off tool
     *             tip for this component.
     */
    public void setToolTipText(Text text) {
	Component c = getEventSource();
	if (c != null && c instanceof KComponent) {
	    ((KComponent)c).setToolTipText(text);
	}
    }

    /**
     * Registers the visual object to display in a ToolTip.
     *
     * @param visual The visual object to display when the cursor lingers
     *               over the component. If visual is null, then it turns
     *               off tool tip for this component.
     */
    public void setToolTipVisual(Visualizable visual) {
	Component c = getEventSource();
	if (c != null && c instanceof KComponent) {
	    ((KComponent)c).setToolTipVisual(visual);
	}
    }
}
