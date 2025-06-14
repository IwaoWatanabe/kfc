/*
 * TextController.java
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
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextLayout;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.text.TextPositionInfo;

import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * The <code>TextController</code> class is an abstract base class that
 * controlls the <code>TextView</code> object.
 *
 * @see 	jp.kyasu.awt.text.TextView
 *
 * @version 	18 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public abstract class TextController implements
	MouseListener, MouseMotionListener, KeyListener,
	FocusListener, ClipboardOwner, java.io.Serializable
{
    protected boolean clickToFocus;
    protected boolean selectionVisibleAtFocus;
    protected boolean lostClipboardOwnership;


    /**
     * Returns the resource string indicated by the specified key.
     * @see jp.kyasu.awt.AWTResources#getResourceString(java.lang.String, java.lang.String)
     */
    static public String getResourceString(String s, String def) {
	return AWTResources.getResourceString(s, def);
    }


    /** The label name for the edit menu. */
    static public final String L_EDIT
			= getResourceString("kfc.text.editLabel", "Edit");

    /** The label name for the copy operation. */
    static public final String L_COPY
			= getResourceString("kfc.text.copyLabel", "Copy");

    /** The label name for the cut operation. */
    static public final String L_CUT
			= getResourceString("kfc.text.cutLabel", "Cut");

    /** The label name for the paste operation. */
    static public final String L_PASTE
			= getResourceString("kfc.text.pasteLabel", "Paste");

    /** The label name for the find operation. */
    static public final String L_FIND
			= getResourceString("kfc.text.findLabel", "Find");

    /** The label name for the undo operation. */
    static public final String L_UNDO
			= getResourceString("kfc.text.undoLabel", "Undo");

    /** The label name for the print operation. */
    static public final String L_PRINT
			= getResourceString("kfc.text.printLabel", "Print");

    /** The action name for the copy operation. */
    static public final String A_COPY  = "copy";

    /** The action name for the cut operation. */
    static public final String A_CUT   = "cut";

    /** The action name for the paste operation. */
    static public final String A_PASTE = "paste";

    /** The action name for the find operation. */
    static public final String A_FIND  = "find";

    /** The action name for the undo operation. */
    static public final String A_UNDO  = "undo";

    /** The action name for the print operation. */
    static public final String A_PRINT = "print";

    /**
     * If true, the controller requests the focus when the mouse is clicked.
     */
    static protected final boolean CLICK_TO_FOCUS
		= "click".equals(getResourceString("kfc.text.focus", "click"));


    /**
     * Constructs a text controller.
     */
    public TextController() {
	clickToFocus = CLICK_TO_FOCUS;
	selectionVisibleAtFocus = true;

	lostClipboardOwnership = true;
    }


    /**
     * Returns the view of this controller.
     */
    public abstract TextView getView();

    /**
     * Adds this controller to the view.
     */
    protected void addToView() {
	getView().addMouseListener(this);
	getView().addMouseMotionListener(this);
	getView().addKeyListener(this);
	getView().addFocusListener(this);
    }

    /**
     * Removes this controller from the view.
     */
    protected void removeFromView() {
	getView().removeMouseListener(this);
	getView().removeMouseMotionListener(this);
	getView().removeKeyListener(this);
	getView().removeFocusListener(this);
    }

    /**
     * Tests if the controller requests the focus when the mouse is clicked.
     *
     * @return <code>true</code> if the controller requests the focus when
     *         the mouse is clicked, <code>false</code> if the controller
     *         requests the focus when the mouse enters the view.
     * @see #isMouseFocus()
     */
    public boolean isClickToFocus() {
	return clickToFocus;
    }

    /**
     * Tests if the controller requests the focus when the mouse enters
     * the view.
     *
     * @see #isClickToFocus()
     */
    public boolean isMouseFocus() {
	return !clickToFocus;
    }

    /**
     * Makes the controller request the focus when the mouse is clicked.
     *
     * @see #isClickToFocus()
     * @see #setMouseFocus()
     */
    public void setClickToFocus() {
	clickToFocus = true;
    }

    /**
     * Makes the controller request the focus when the mouse enters the view.
     *
     * @see #isMouseFocus()
     * @see #setClickToFocus()
     */
    public void setMouseFocus() {
	clickToFocus = false;
    }

    /**
     * Tests if the selection becomes visible when the view is focused.
     *
     * @see #setSelectionVisibleAtFocus(boolean)
     */
    public boolean isSelectionVisibleAtFocus() {
	return selectionVisibleAtFocus;
    }

    /**
     * Makes the selection become visible when the view is focused.
     *
     * @see #isSelectionVisibleAtFocus()
     */
    public void setSelectionVisibleAtFocus(boolean b) {
	selectionVisibleAtFocus = b;
    }


    /**
     * Invoked when the mouse has been clicked on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseClicked(MouseEvent e) {
	// mousePressed -> mouseReleased -> mouseClicked
    }

    /**
     * Invoked when the mouse has been pressed on a component.
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(MouseEvent e) {
	if (!getView().isEnabled())
	    return;
	if (clickToFocus) {
	    getView().requestFocus();
	}
    }

    /**
     * Invoked when the mouse has been released on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseEntered(MouseEvent e) {
	if (!getView().isEnabled())
	    return;
	if (!clickToFocus) {
	    getView().requestFocus();
	}
    }

    /**
     * Invoked when the mouse exits a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when the mouse button is pressed on a component and then dragged.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseDragged(MouseEvent e) {
	// mousePressed -> mouseDragged* -> mouseReleased
    }

    /**
     * Invoked when the mouse button has been moved on a component.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Invoked when a key has been typed.
     * @see java.awt.event.KeyListener
     */
    public void keyTyped(KeyEvent e) {
	// keyPressed -> keyTyped -> keyReleased
	// This method is NOT invoked by the special keys (CTL, SHIFT, etc.).
    }

    /**
     * Invoked when a key has been pressed.
     * @see java.awt.event.KeyListener
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Invoked when a key has been released.
     * @see java.awt.event.KeyListener
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Invoked when a component gains the keyboard focus.
     * @see java.awt.event.FocusListener
     */
    public void focusGained(FocusEvent e) {
	//synchronized (getView()) {
	    if (!getView().isEnabled())
		return;
	    //if (e.isTemporary()) return;
	    //if (isSelectionVisibleAtFocus()) {
	    if (isSelectionVisibleAtFocus() || !getView().isSelectionVisible())
	    {
		getView().setSelectionVisible(true);
	    }
	//}
    }

    /**
     * Invoked when a component loses the keyboard focus.
     * @see java.awt.event.FocusListener
     */
    public void focusLost(FocusEvent e) {
	//synchronized (getView()) {
	    if (!getView().isEnabled())
		return;
	    //if (e.isTemporary()) return;
	    if (isSelectionVisibleAtFocus()) {
		getView().setSelectionVisible(false);
	    }
	//}
    }

    /**
     * Notifies this object that it is no longer the owner of the contents
     * of the clipboard.
     * @see java.awt.datatransfer.ClipboardOwner
     */
    public synchronized void lostOwnership(Clipboard clipboard,
					   Transferable contents)
    {
	lostClipboardOwnership = true;
    }


    // ================ CutBuffer/Clipboard ================


    /**
     * The system clipboard.
     */
    static private Clipboard SystemClipboard;

    static {
	try {
	    SystemClipboard =
		java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	catch (SecurityException e) {
	    SystemClipboard = new Clipboard("KFC");
	}
    }

    /**
     * The application cut buffer.
     */
    static private Text CutBuffer = null;


    /**
     * Returns the text in the cut buffer.
     */
    static public synchronized Text getCutBufferText() {
	return CutBuffer;
    }

    /**
     * Sets the text to the cut buffer.
     */
    static public synchronized void setCutBuffer(Text text) {
	CutBuffer = text;
    }

    /**
     * Returns the text in the clipboard.
     */
    public Text getClipboardText() {
	return getClipboardText(TextStyle.DEFAULT_STYLE);
    }

    /**
     * Returns the text in the clipboard with the specified text style.
     */
    public Text getClipboardText(TextStyle style) {
	String str = getClipboardString();
	return (str == null ?
			null :
			new Text(Text.getJavaString(str), style));
    }

    /**
     * Sets the text to the clipboard.
     */
    public void setClipboardText(Text text) {
	setClipboardString(Text.getSystemString(text.toString()));
    }

    /**
     * Returns the string in the clipboard.
     */
    public synchronized String getClipboardString() {
	Transferable content = SystemClipboard.getContents(this);
	if (content != null) {
	    try {
		Object obj = content.getTransferData(DataFlavor.stringFlavor);
		return (String)obj;
	    }
	    catch (Exception e) {}
	}
	return null;
    }

    /**
     * Sets the string to the clipboard.
     */
    public synchronized void setClipboardString(String str) {
	StringSelection contents = new StringSelection(str);
	SystemClipboard.setContents(contents, this);
	lostClipboardOwnership = false;
    }
}
