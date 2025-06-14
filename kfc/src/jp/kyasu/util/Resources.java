/*
 * Resources.java
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

package jp.kyasu.util;

import java.awt.Color;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The <code>Resources</code> class provides the resources.
 *
 * @version 	10 May 1998
 * @author 	Kazuki YASUMATSU
 */
public class Resources {
    /** The resources. */
    protected ResourceBundle resources;


    /**
     * Constructs a resources with the specified base name.
     *
     * @param baseName the base name of the resource.
     */
    public Resources(String baseName) {
	this(baseName, Locale.getDefault());
    }

    /**
     * Constructs a resources with the specified base name and locale.
     *
     * @param baseName the base name of the resource.
     * @param locale   the locale of the resource.
     */
    public Resources(String baseName, Locale locale) {
	resources = null;
	try {
	    resources = ResourceBundle.getBundle(baseName, locale);
	}
	catch (MissingResourceException mre) {}
	catch (SecurityException se) {}
    }

    /**
     * Returns the resource string indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @return the string value of the resource, or <code>null</code>
     *         if there is no resource with that key.
     */
    public String getResourceString(String key) {
	return getResourceString(key, key);
    }

    /**
     * Returns the resource string indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @param  def a default value.
     * @return the string value of the resource, or the default value
     *         if there is no resource with that key.
     */
    public String getResourceString(String key, String def) {
	String rs = null;
	try { rs = System.getProperty(key); }
	catch (SecurityException e) {}
	if (rs != null) {
	    return rs;
	}
	if (resources == null) {
	    return def;
	}
	try { rs = resources.getString(key); }
	catch (MissingResourceException mre) {}
	if (rs != null) {
	    return rs;
	}
	return def;
    }

    /**
     * Returns the resource integer indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @param  def a default integer.
     * @return the integer value of the resource, or the default integer.
     *         if there is no resource with that key.
     */
    public int getResourceInteger(String key, int def) {
	String rs = getResourceString(key, null);
	Integer integer = null;
	if (rs != null) {
	    try {
		integer = Integer.decode(rs);
	    }
	    catch (NumberFormatException e) {}
	}
	if (integer != null) {
	    return integer.intValue();
	}
	return def;
    }

    /**
     * Returns the resource boolean indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @param  def a default boolean.
     * @return the boolean value of the resource, or the default boolean.
     *         if there is no resource with that key.
     */
    public boolean getResourceBoolean(String key, boolean def) {
	String rs = getResourceString(key, null);
	if (rs != null) {
	    if (def) {
		return !rs.equals("false");
	    }
	    else {
		return rs.equals("true");
	    }
	}
	return def;
    }

    /**
     * Returns the resource color indicated by the specified key.
     *
     * @param  key the name of the resource.
     * @param  def a default color.
     * @return the color value of the resource, or the default color.
     *         if there is no resource with that key.
     */
    public Color getResourceColor(String key, Color def) {
	String rs = getResourceString(key, null);
	Color color = null;
	if (rs != null) {
	    try {
		color = Color.decode(rs);
	    }
	    catch (NumberFormatException e) {}
	}
	if (color != null) {
	    return color;
	}
	return def;
    }
}
