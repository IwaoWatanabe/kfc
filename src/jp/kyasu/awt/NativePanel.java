/*
 * NativePanel.java
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

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.LayoutManager;

/**
 * The <code>NativePanel</code> is the simplest native (heavyweight)
 * container class. The <code>update()</code> method simply paints the
 * components. The default layout manager for a panel is
 * <code>BorderLayout</code>.
 *
 * @version 	18 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class NativePanel extends java.awt.Panel {

    public NativePanel() {
	this(new BorderLayout());
    }

    public NativePanel(LayoutManager layout) {
	super(layout);
    }

    /**
     * Updates this panel.
     */
    public void update(Graphics g) {
	paint(g);
    }
}
