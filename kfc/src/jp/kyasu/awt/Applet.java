/*
 * Applet.java
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

/**
 * An applet is a small program that is intended not to be run on
 * its own, but rather to be embedded inside another application.
 * <p>
 * The <code>Applet</code> class must be the superclass of any
 * applet that is to be embedded in a Web page or viewed by the Java
 * Applet Viewer. The <code>Applet</code> class provides a standard 
 * interface between applets and their environment.
 *
 * @version 	12 Mar 1998
 * @author 	Kazuki YASUMATSU
 */
public class Applet extends java.applet.Applet {

    /**
     * Constructs an applet.
     */
    public Applet() {
	setForeground(AWTResources.FOREGROUND_COLOR);
	setBackground(AWTResources.BACKGROUND_COLOR);
	AWTResources.IS_DIRECT_NOTIFICATION = true;
	AWTResources.HAS_FOCUS_BUG = false;
    }

    /**
     * Called by the browser or applet viewer to inform this applet
     * that it should start its execution. It is called after the
     * <code>init</code> method and each time the applet is revisited
     * in a Web page.
     * <p>
     * If a subclass of <code>Applet</code> overrides this method, it
     * should call <code>super.start()</code>.
     *
     * @see #stop()
     */
    public void start() {
	Timer.startTimerThread();
    }

    /**
     * Called by the browser or applet viewer to inform this applet
     * that it should stop its execution. It is called when the Web
     * page that contains this applet has been replaced by another page,
     * and also just before the applet is to be destroyed.
     * <p>
     * If a subclass of <code>Applet</code> overrides this method, it
     * should call <code>super.stop()</code>.
     *
     * @see #start()
     */
    public void stop() {
	Timer.stopTimerThread();
    }
}
