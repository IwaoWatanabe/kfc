/*
 * Entity.java
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

package jp.kyasu.sgml;

/**
 * The <code>Entity</code> class represents the SGML entity. Refers to
 * <cite>"ISO 8879 -- Standard Generalized Markup Language (SGML)"</cite>.
 *
 * @version 	14 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class Entity implements java.io.Serializable {
    /**
     * The name of the entity.
     */
    protected String name;

    /**
     * The text (content) of the entity.
     */
    protected String text;


    /**
     * Constructs an empty entity.
     */
    public Entity() {
	this("", "");
    }

    /**
     * Constructs an entity with the specified name and text.
     *
     * @param name the specified name.
     * @param text the specified text.
     */
    public Entity(String name, String text) {
	if (name == null || text == null)
	    throw new NullPointerException();
	setName(name);
	setText(text);
    }


    /**
     * Returns the name of this entity.
     *
     * @return the name of this entity.
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the name of this entity to be the specified name.
     *
     * @param name the specified name.
     */
    public void setName(String name) {
	if (name == null)
	    throw new NullPointerException();
	this.name = name;
    }

    /**
     * Returns the text of this entity.
     *
     * @return the text of this entity.
     */
    public String getText() {
	return text;
    }

    /**
     * Sets the text of this entity to be the specified text.
     *
     * @param text the specified text.
     */
    public void setText(String text) {
	if (name == null)
	    throw new NullPointerException();
	this.text = text;
    }
}
