/*
 * DTD.java
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

import jp.kyasu.util.Set;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The <code>DTD</code> class represents the SGML document type definition.
 * Refers to
 * <cite>"ISO 8879 -- Standard Generalized Markup Language (SGML)"</cite>.
 *
 * @see         jp.kyasu.sgml.Element
 * @see         jp.kyasu.sgml.Entity
 *
 * @version 	14 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class DTD implements java.io.Serializable {
    /**
     * The root element of this DTD.
     */
    protected Element docElement;

    /**
     * The elements defined in this DTD.
     */
    protected Hashtable elements;

    /**
     * The entities defined in this DTD.
     */
    protected Hashtable entities;


    /**
     * Constructs an empty DTD.
     */
    public DTD() {
	this(null);
    }

    /**
     * Constructs a DTD with the specified root element.
     *
     * @param docElement the root element of the DTD.
     */
    public DTD(Element docElement) {
	this.docElement = docElement;
	elements = new Hashtable();
	entities = new Hashtable();
	if (docElement != null) {
	    addElement(docElement);
	}
    }


    /**
     * Returns the root element of this DTD.
     *
     * @return the root element of this DTD.
     */
    public Element getDocElement() {
	return docElement;
    }

    /**
     * Sets the root element of this DTD to be the specified element.
     *
     * @param element the specified element.
     */
    public void setDocElement(Element element) {
	if (element == null)
	    throw new NullPointerException();
	docElement = element;
    }

    /**
     * Adds the element to this DTD.
     *
     * @param element an element.
     */
    public void addElement(Element element) {
	elements.put(element.name, element);
    }

    /**
     * Returns the element whose name equals to the specified name in this DTD.
     *
     * @param  name the specified name
     * @return the element whose name equals to the specified name in this DTD;
     *         <code>null</code> if this DTD does not contain the corresponding
     *         element.
     */
    public Element getElement(String name) {
	return (Element)elements.get(name.toUpperCase());
    }

    /**
     * Returns the all elements in this DTD, as a hashtable.
     *
     * @return the all elements in this DTD, as a hashtable.
     */
    public Hashtable getAllElements() {
	return elements;
    }

    /**
     * Tests if this DTD contains the element whose name equals to
     * the specified name.
     *
     * @param  name the specified name.
     * @return <code>true</code> if this DTD contains the element whose
     *         name equals to the specified name; <code>false</code> otherwise.
     */
    public boolean containsElement(String name) {
	return elements.containsKey(name.toUpperCase());
    }

    /**
     * Adds the entity to this DTD.
     *
     * @param entity an entity.
     */
    public void addEntity(Entity entity) {
	entities.put(entity.name, entity);
    }

    /**
     * Returns the entity whose name equals to the specified name in this DTD.
     *
     * @param  name the specified name
     * @return the entity whose name equals to the specified name in this DTD;
     *         <code>null</code> if this DTD does not contain the corresponding
     *         entity.
     */
    public Entity getEntity(String name) {
	return (Entity)entities.get(name);
    }

    /**
     * Returns the all entities in this DTD, as a hashtable.
     *
     * @return the all entities in this DTD, as a hashtable.
     */
    public Hashtable getAllEntities() {
	return entities;
    }

    /**
     * Tests if this DTD contains the entity whose name equals to
     * the specified name.
     *
     * @param  name the specified name.
     * @return <code>true</code> if this DTD contains the entity whose
     *         name equals to the specified name; <code>false</code> otherwise.
     */
    public boolean containsEntity(String name) {
	return entities.containsKey(name);
    }

    /**
     * Checks if this DTD is valid.
     *
     * @return <code>true</code> if this DTD is valid;
     *         <code>false</code> otherwise.
     */
    public boolean isValidDTD() {
	Enumeration e = elements.elements();
	while (e.hasMoreElements()) {
	    Element elem = (Element)e.nextElement();
	    if (!isValidElement(elem)) {
		return false;
	    }
	}
	return (docElement != null && isValidElement(docElement));
    }

    protected boolean isValidElement(Element elem) {
	Set set = elem.getContentModel();
	if (set != null) {
	    for (Enumeration e = set.elements(); e.hasMoreElements(); ) {
		if (!containsElement((String)e.nextElement())) {
		    return false;
		}
	    }
	}
	set = elem.getInclusions();
	if (set != null) {
	    for (Enumeration e = set.elements(); e.hasMoreElements(); ) {
		if (!containsElement((String)e.nextElement())) {
		    return false;
		}
	    }
	}
	set = elem.getExclusions();
	if (set != null) {
	    for (Enumeration e = set.elements(); e.hasMoreElements(); ) {
		if (!containsElement((String)e.nextElement())) {
		    return false;
		}
	    }
	}
	return true;
    }
}
