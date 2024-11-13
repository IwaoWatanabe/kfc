/*
 * EditorResources.java
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

package jp.kyasu.editor;

import java.awt.Color;

/**
 * The <code>EditorResources</code> class provides the resources shared in
 * this package.
 *
 * @version 	16 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class EditorResources {

    // ======== Resource Handling ========

    static final protected jp.kyasu.util.Resources Resources =
		new jp.kyasu.util.Resources("jp.kyasu.editor.resources.editor");

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
     * @return the integer value of the resource, or the default integer.
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
     * @return the boolean value of the resource, or the default boolean.
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
     * @return the color value of the resource, or the default color.
     *         if there is no resource with that key.
     */
    static public Color getResourceColor(String key, Color def) {
	return Resources.getResourceColor(key, def);
    }


    // ======== Character Set ========

    /** The default character set for reading. */
    static public String DEFAULT_READ_CHARSET;

    /** The default character set for writing. */
    static public String DEFAULT_WRITE_CHARSET;

    static {
	String fileEncoding = null;
	try { fileEncoding = System.getProperty("file.encoding"); }
	catch (SecurityException e) {}

	// linux and japanese hack.
	if ("EUC_JP".equals(fileEncoding)) fileEncoding = "EUCJIS";

	DEFAULT_READ_CHARSET = getResourceString("defaultReadCharSet", null);
	if (DEFAULT_READ_CHARSET == null) {
	    DEFAULT_READ_CHARSET =
		(fileEncoding != null ? fileEncoding : "Default");
	}

	if (fileEncoding != null) {
	    DEFAULT_WRITE_CHARSET = fileEncoding;
	}
	else {
	    DEFAULT_WRITE_CHARSET = getResourceString("defaultWriteCharSet",
						      null);
	    if (DEFAULT_WRITE_CHARSET == null)
		DEFAULT_WRITE_CHARSET = "Default";
	}
    }
}
