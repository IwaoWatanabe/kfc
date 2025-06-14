/*
 * Element.java
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

/**
 * The <code>Element</code> class represents the SGML element. Refers to
 * <cite>"ISO 8879 -- Standard Generalized Markup Language (SGML)"</cite>.
 *
 * @version 	23 Sep 1997
 * @author 	Kazuki YASUMATSU
 */
public class Element implements java.io.Serializable {
    /**
     * The name of the element.
     */
    protected String name;

    /**
     * The content model type of the element.
     *
     * @see #PCDATA
     * @see #CDATA
     * @see #RCDATA
     * @see #EMPTY
     * @see #MODEL
     * @see #MODEL_PCDATA
     */
    protected int contentModelType;

    /**
     * The names of the sub-elements in the element.
     *
     * @see #MODEL
     * @see #MODEL_PCDATA
     */
    protected Set contentModel;

    /**
     * The names of the attributes of the element.
     */
    protected Set attributes;

    /**
     * The names of the inclusions (elements) of the element.
     */
    protected Set inclusions;

    /**
     * The names of the exclusions (elements) of the element.
     */
    protected Set exclusions;


    /** The content model is #PCDATA. */
    static public final int PCDATA = 0;

    /** The content model is #CDATA. */
    static public final int CDATA  = 1;

    /** The content model is #RCDATA. */
    static public final int RCDATA = 2;

    /** The content model is EMPTY. */
    static public final int EMPTY  = 3;

    /** The content model consists of sub-elements. */
    static public final int MODEL         = 4;

    /** The content model consists of sub-elements and #PCDATA. */
    static public final int MODEL_PCDATA  = 5;


    /**
     * Constructs an element with the specified name, attributes and type.
     * The type should be <code>PCDATA</code>, <code>CDATA</code>,
     * <code>RCDATA</code> or <code>EMPTY</code>.
     *
     * @param name  the specified name.
     * @param attrs the specified attribute names.
     * @param type  the specified type.
     */
    public Element(String name, String attrs[], int type) {
	this(name, attrs, type, null, null);
    }

    /**
     * Constructs an element with the specified name, attributes, type,
     * inclusions and exclusions. The type should be <code>PCDATA</code>,
     * <code>CDATA</code>, <code>RCDATA</code> or <code>EMPTY</code>.
     *
     * @param name  the specified name.
     * @param attrs the specified attribute names.
     * @param type  the specified type.
     * @param incs  the specified names of the inclusions.
     * @param excs  the specified names of the exclusions.
     */
    public Element(String name, String attrs[], int type,
		   String incs[], String excs[])
    {
	if (name == null)
	    throw new NullPointerException();
	setName(name);
	setAttributes(attrs);
	setContentModelType(type);
	setInclusions(incs);
	setExclusions(excs);
    }

    /**
     * Constructs an element with the specified name, attributes and
     * sub-elements.
     *
     * @param name  the specified name.
     * @param attrs the specified attribute names.
     * @param model the specified names of the sub-elements.
     */
    public Element(String name, String attrs[], String model[]) {
	this(name, attrs, model, null, null);
    }

    /**
     * Constructs an element with the specified name, attributes,
     * sub-elements, inclusions and exclusions.
     *
     * @param name  the specified name.
     * @param attrs the specified attribute names.
     * @param model the specified names of the sub-elements.
     * @param incs  the specified names of the inclusions.
     * @param excs  the specified names of the exclusions.
     */
    public Element(String name, String attrs[], String model[],
		   String incs[], String excs[])
    {
	if (name == null || model == null)
	    throw new NullPointerException();
	setName(name);
	setAttributes(attrs);
	setContentModel(model);
	setInclusions(incs);
	setExclusions(excs);
    }

    /**
     * Constructs an element with the specified name, attributes,
     * sub-elements and type. The type should be <code>MODEL</code> or
     * <code>MODEL_PCDATA</code>.
     *
     * @param name  the specified name.
     * @param attrs the specified attribute names.
     * @param model the specified names of the sub-elements.
     * @param type  the specified type.
     */
    public Element(String name, String attrs[], String model[], int type) {
	this(name, attrs, model, type, null, null);
    }

    /**
     * Constructs an element with the specified name, attributes,
     * sub-elements, type, inclusions and exclusions. The type should be
     * <code>MODEL</code> or <code>MODEL_PCDATA</code>.
     *
     * @param name  the specified name.
     * @param attrs the specified attribute names.
     * @param model the specified names of the sub-elements.
     * @param type  the specified type.
     * @param incs  the specified names of the inclusions.
     * @param excs  the specified names of the exclusions.
     */
    public Element(String name, String attrs[], String model[], int type,
		   String incs[], String excs[])
    {
	if (name == null || model == null)
	    throw new NullPointerException();
	setName(name);
	setAttributes(attrs);
	setContentModel(model, type);
	setInclusions(incs);
	setExclusions(excs);
    }


    /**
     * Tests if this element can accept the specified element.
     *
     * @param  element an element.
     * @return <code>true</code> if this element can accept the
     *          specified element; <code>false</code> otherwise.
     */
    public boolean canAccept(Element element) {
	return (contentModel != null && contentModel.contains(element.name));
    }

    /**
     * Returns the name of this element.
     *
     * @return the name of this element.
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the name of this element to be the specified name.
     *
     * @param name the specified name.
     */
    public void setName(String name) {
	if (name == null)
	    throw new NullPointerException();
	this.name = name.toUpperCase();
    }

    /**
     * Returns the names of the attributes of this element.
     *
     * @return the names of the attributes of this element.
     */
    public Set getAttributes() {
	return attributes;
    }

    /**
     * Sets the name of the attributes of this element to be the
     * specified attribute names.
     *
     * @param attrs the specified attribute names.
     */
    public void setAttributes(String attrs[]) {
	attributes = createSet(attrs);
    }

    /**
     * Tests if this element has an attribute by the specified name.
     *
     * @param  name the specified attribute name.
     * @return <code>true</code> if this element has an attribute by
     *          the specified name; <code>false</code> otherwise.
     */
    public boolean hasAttributeNamed(String name) {
	return (attributes != null && attributes.contains(name.toUpperCase()));
    }

    /**
     * Returns the content model type of this element.
     *
     * @return the content model type of this element.
     */
    public int getContentModelType() {
	return contentModelType;
    }

    /**
     * Returns the names of the sub-elements in this element, as a set.
     *
     * @return the names of the sub-elements in this element, as a set
     */
    public Set getContentModel() {
	return contentModel;
    }

    /**
     * Sets the content model type of this element to be the
     * specified type.
     *
     * @param type the specified type.
     * @exception IllegalArgumentException if the specified type is not
     *            a valid.
     */
    public void setContentModelType(int type) {
	switch (type) {
	case PCDATA:
	case CDATA:
	case RCDATA:
	case EMPTY:
	    contentModelType = type;
	    contentModel = null;
	    return;
	}
	throw new IllegalArgumentException("improper type: " + type);
    }

    /**
     * Sets the sub-elements of this element to be the specified
     * sub-elements.
     *
     * @param model the specified names of the sub-elements.
     * @exception IllegalArgumentException if the specified type is not
     *            a valid.
     */
    public void setContentModel(String model[]) {
	setContentModel(model, MODEL);
    }

    /**
     * Sets the sub-elements and the content model type of this element
     * to be the specified sub-elements and the specified type.
     *
     * @param model the specified names of the sub-elements.
     * @param type  the specified type.
     * @exception IllegalArgumentException if the specified type is not
     *            a valid.
     */
    public void setContentModel(String model[], int type) {
	switch (type) {
	case MODEL:
	case MODEL_PCDATA:
	    contentModelType = type;
	    break;
	default:
	    throw new IllegalArgumentException("improper type: " + type);
	}

	contentModel = createSet(model);
	if (contentModel != null && contentModel.contains("#PCDATA")) {
	    contentModel.remove("#PCDATA");
	    contentModelType = MODEL_PCDATA;
	}
	/*
	else {
	    contentModelType = MODEL;
	}
	*/
    }

    /**
     * Returns the names of the inclusions of this element, as a set.
     *
     * @return the names of the inclusions of this element, as a set
     */
    public Set getInclusions() {
	return inclusions;
    }

    /**
     * Sets the inclusions of this element to be the specified inclusions.
     *
     * @param incs the specified names of the inclusions.
     */
    public void setInclusions(String incs[]) {
	inclusions = createSet(incs);
    }

    /**
     * Returns the names of the exclusions of this element, as a set.
     *
     * @return the names of the exclusions of this element, as a set
     */
    public Set getExclusions() {
	return exclusions;
    }

    /**
     * Sets the inclusions of this element to be the specified exclusions.
     *
     * @param excs the specified names of the exclusions.
     */
    public void setExclusions(String excs[]) {
	exclusions = createSet(excs);
    }

    protected Set createSet(String strs[]) {
	if (strs == null || strs.length == 0)
	    return null;
	Set set = new Set();
	for (int i = 0; i < strs.length; i++) {
	    set.add(strs[i].toUpperCase());
	}
	return set;
    }
}
