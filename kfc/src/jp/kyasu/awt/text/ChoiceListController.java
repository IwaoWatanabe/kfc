/*
 * ChoiceListController.java
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

import java.awt.Adjustable;
import java.awt.event.*;

/**
 * The <code>ChoiceListController</code> class implements a view of a MVC model
 * for the choice. The model of the MVC model is a <code>TextListModel</code>
 * object and the view of the MVC model is a <code>TextListView</code> object.
 *
 * @see		jp.kyasu.awt.Choice
 * @see		jp.kyasu.awt.TextListModel
 * @see		jp.kyasu.awt.text.TextListView
 *
 * @version 	20 Jul 1998
 * @author 	Kazuki YASUMATSU
 */
public class ChoiceListController extends TextListController {

    /**
     * Constructs a choice controller with the specified text list view.
     *
     * @param view the text list view.
     */
    public ChoiceListController(TextListView view) {
	super(view);
	setPopupMenu(null);
	movingSelectionEnabled = true;
	view.setLineSelectionVisible(false);
    }


    /**
     * Invoked when the mouse has been pressed on a component.
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(MouseEvent e) {
	// do nothing
    }

    /**
     * Invoked when the mouse has been released on a component.
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(MouseEvent e) {
	// do nothing
    }

    /**
     * Invoked when the mouse button has been moved on a component.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseMoved(MouseEvent e) {
	// do nothing
    }

    /**
     * Invoked when the mouse button is pressed on a component and then dragged.
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseDragged(MouseEvent e) {
	// do noting
    }

    /**
     * Invoked when a component gains the keyboard focus.
     * @see java.awt.event.FocusListener
     */
    public void focusGained(FocusEvent e) {
	//synchronized (view) {
	    if (!view.isEnabled())
		return;
	    //if (e.isTemporary()) return;
	    view.setLineSelectionVisible(true);
	    view.repaintNow();
	    super.focusGained(e);
	//}
    }

    /**
     * Invoked when a component loses the keyboard focus.
     * @see java.awt.event.FocusListener
     */
    public void focusLost(FocusEvent e) {
	//synchronized (view) {
	    if (!view.isEnabled())
		return;
	    //if (e.isTemporary()) return;
	    view.setLineSelectionVisible(false);
	    view.repaintNow();
	    super.focusLost(e);
	//}
    }
}
