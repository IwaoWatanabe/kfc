/*
 * AWTResources.java
 *
 * Copyright (c) 1997, 1998, 1999 Kazuki YASUMATSU.  All Rights Reserved.
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

import jp.kyasu.graphics.VImage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

/**
 * The <code>AWTResources</code> class provides the resources shared in
 * this package.
 *
 * @version 	20 Nov 1999
 * @author 	Kazuki YASUMATSU
 */
public class AWTResources {

    // ======== Resource Handling ========

    static final protected jp.kyasu.util.Resources Resources =
		new jp.kyasu.util.Resources("jp.kyasu.awt.resources.awt");

    /**
     * Returns the resource string indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @return the string value of the resource, or <code>null</code>
     *         if there is no resource with that key.
     */
    static public String getResourceString(String key) {
	return Resources.getResourceString(key);
    }

    /**
     * Returns the resource string indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @param  def a default value.
     * @return the string value of the resource, or the default value
     *         if there is no resource with that key.
     */
    static public String getResourceString(String key, String def) {
	return Resources.getResourceString(key, def);
    }

    /**
     * Returns the resource integer indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @param  def a default integer.
     * @return the integer value of the resource, or the default integer
     *         if there is no resource with that key.
     */
    static public int getResourceInteger(String key, int def) {
	return Resources.getResourceInteger(key, def);
    }

    /**
     * Returns the resource boolean indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @param  def a default boolean.
     * @return the boolean value of the resource, or the default boolean
     *         if there is no resource with that key.
     */
    static public boolean getResourceBoolean(String key, boolean def) {
	return Resources.getResourceBoolean(key, def);
    }

    /**
     * Returns the resource color indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @param  def a default color.
     * @return the color value of the resource, or the default color
     *         if there is no resource with that key.
     */
    static public Color getResourceColor(String key, Color def) {
	return Resources.getResourceColor(key, def);
    }

    /** The default null icon. */
    static protected VImage NULL_ICON = new VImage(new byte[]{
	(byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38, (byte)0x39,
	(byte)0x61, (byte)0x10, (byte)0x00, (byte)0x10, (byte)0x00,
	(byte)0x80, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF,
	(byte)0xFF, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x21,
	(byte)0xF9, (byte)0x04, (byte)0x01, (byte)0x00, (byte)0x00,
	(byte)0x00, (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0x00,
	(byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x10,
	(byte)0x00, (byte)0x00, (byte)0x02, (byte)0x0E, (byte)0x84,
	(byte)0x8F, (byte)0xA9, (byte)0xCB, (byte)0xED, (byte)0x0F,
	(byte)0xA3, (byte)0x9C, (byte)0xB4, (byte)0xDA, (byte)0x8B,
	(byte)0xB3, (byte)0x3E, (byte)0x05, (byte)0x00, (byte)0x3B
    });

    /**
     * Returns the resource icon indicated by the specified class and file.
     *
     * @param  baseClass the base class.
     * @param  file      a file name.
     * @return the icon of the resource, or the default null icon
     *         if there is no resource with that key.
     */         
    static public VImage getIcon(Class baseClass, String file) {
	if (baseClass == null || file == null)
	    return NULL_ICON;

	/*
	java.net.URL url = baseClass.getResource(file);
	if (url == null) {
	    return NULL_ICON;
	}
	return new VImage(url);
	*/

	/* Copy resource into a byte array. This is necessary because
	 * several browsers consider Class.getResource a security risk
	 * because it can be used to load additional classes.
	 * Class.getResourceAsStream just returns raw bytes, which we
	 * can convert to an image.
	 */
	try {
	    java.io.InputStream resource =
				baseClass.getResourceAsStream(file);
	    if (resource == null) {
		return NULL_ICON;
	    }
	    java.io.BufferedInputStream in =
				new java.io.BufferedInputStream(resource);
	    java.io.ByteArrayOutputStream out =
				new java.io.ByteArrayOutputStream(1024);
	    byte[] buffer = new byte[1024];
	    int n;
	    while ((n = in.read(buffer)) > 0) {
		out.write(buffer, 0, n);
	    }
	    in.close();
	    out.flush();
	    buffer = out.toByteArray();
	    if (buffer.length == 0) {
		return NULL_ICON;
	    }
	    return new VImage(buffer);
	}
	catch (java.io.IOException e) {
	    return NULL_ICON;
	}
    }


    // ======== Default Color ========

    /**
     * The default foreground color.
     */
    static public final Color FOREGROUND_COLOR =
			getResourceColor("kfc.foreground", Color.black);

    /**
     * The default background color.
     */
    static public final Color BACKGROUND_COLOR =
			getResourceColor("kfc.background", Color.lightGray);


    // ======== Double Buffer ========

    /**
     * The default double buffer policy.
     */
    static public boolean USE_DOUBLE_BUFFER =
		(System.getProperty("java.version").compareTo("1.2") >= 0);


    /*if[JDK1.2]

    // ======== Default Rendering Hint Map ========

    /**
     * The default rendering hint map.
     */
    /*if[JDK1.2]
    static public final java.util.Map RENDERING_HINTS = new java.util.HashMap();

    /*end[JDK1.2]*/


    // ======== Event Dispatching for Notifying Listeners ========

    /**
     * If true, notifies listeners directly without using the event queue.
     */
    static public boolean IS_DIRECT_NOTIFICATION =
		(System.getProperty("java.version").compareTo("1.1.5") >= 0) ||
		getResourceBoolean("kfc.directNotification", false);


    // ======== JDK Bug Workaround ========

    /** True if the JVM runs on Windows 95/NT. */
    static protected final boolean ON_WINDOWS =
			System.getProperty("os.name").startsWith("Windows");

    /**
     * True if the JDK has the
     * <code>sun.awt.windows.WWindowPeer#getFocusPeer()</code> bug.
     * <p>
     * Because of this bug, if a lightwight component is focus traversable,
     * an application will be hung up.
     * <p>
     * This bug has been fixed from JDK1.1.5.
     */
    static public boolean HAS_FOCUS_BUG =
		ON_WINDOWS &&
		(System.getProperty("java.version").compareTo("1.1.4") <= 0);

    /**
     * True if a pop-up window can receive the events even when the pop-up
     * window is opend within a modal dialog.
     */
    static public boolean CAN_OPEN_POPUP_IN_MODAL_DIALOG = ON_WINDOWS;

    /**
     * True if the JDK has the <code>java.awt.Graphics#copyArea()</code> bug.
     * <p>
     * This bug has arised from JDK1.2.
     */
    static public boolean HAS_COPY_AREA_BUG =
		(System.getProperty("java.version").compareTo("1.2") >= 0);

    /**
     * Checks the component state for the specified component.
     * <p>
     * If <code>HAS_FOCUS_BUG</code> is true, an application should use
     * jp.kyasu.awt.{Dialog,Frame,Window} instead of
     * java.awt.{Dialog,Frame,Window}.
     * <p>
     * If a top frame has a menu bar on windows, an application should
     * wrap a lightweight component in a native component, so that pop-up
     * menu and <code>getLocationOnScreen()</code> work correctly.
     *
     * @exception java.awt.IllegalComponentStateException if the component
     *            state is illegal.
     */
    static public void checkComponentState(Component comp) {
	if (!ON_WINDOWS)
	    return;
	boolean inNativeContainer = false;
	for (Container c = comp.getParent(); c != null; c = c.getParent()) {
	    if (c instanceof java.awt.Window) {
		if (HAS_FOCUS_BUG) {
		    if (!(c instanceof jp.kyasu.awt.Dialog ||
			c instanceof jp.kyasu.awt.Frame ||
			c instanceof jp.kyasu.awt.Window))
		    {
			/* Make applets happy.
			throw new java.awt.IllegalComponentStateException(
				"jp.kyasu.awt.{Dialog,Frame,Window} should be used instead of java.awt.{Dialog,Frame,Window} for the AWT focusing bug.");
			*/
		    }
		}
		if (c instanceof java.awt.Frame) {
		    if (!inNativeContainer &&
			((java.awt.Frame)c).getMenuBar() != null)
		    {
			throw new java.awt.IllegalComponentStateException(
				"A lightweight component should be wrappend in a native component for the AWT menu bar bug.");
		    }
		}
		return;
	    }
	    if (!c.isLightweight()) {
		inNativeContainer = true;
	    }
	}
    }

    static Object getPeer(Component c) {
        // for JRE9
        try { return c.getPeer(); } catch(java.lang.NoSuchMethodError e) {
            try {
                java.lang.reflect.Field pf = c.getClass().getField("peer");
                pf.setAccessible(true);
                return pf.get(c);
            } catch(Exception ignored) { return null; }
        }
    }

    /** The lock for the foucusing bug workaround. */
    static protected final Object FOCUS_LOCK = new Object();

    /** The state for the foucusing bug workaround. */
    static protected boolean IN_REQUEST_FOCUS = false;

    /**
     * The <code>Window#getFocusOwner()</code> workaround.
     *
     * @see jp.kyasu.awt.KComponent#requestFocus()
     * @see jp.kyasu.awt.KContainer#requestFocus()
     */
    static protected Component getFocusOwnerWorkaround(Component focus) {
	synchronized (FOCUS_LOCK) {
	    if (focus == null) {
		return null;
	    }
	    else if (IN_REQUEST_FOCUS) {
		return focus;
	    }
	    else {
		return getNativeComponent(focus);
	    }
	}
    }

    /**
     * Returns the native component for the specified component.
     */
    static protected Component getNativeComponent(Component c) {
	if (!c.isLightweight())
	    return c;
	for (Container p = c.getParent(); p != null; p = p.getParent()) {
	    if (!p.isLightweight())
		return p;
	}
	return null;
    }
}
