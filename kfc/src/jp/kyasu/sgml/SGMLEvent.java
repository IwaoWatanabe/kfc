/*
 * SGMLEvent.java
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

import java.util.Hashtable;

/**
 * The SGML event that is delivered from a <code>SGMLParser</code> to
 * <code>SGMLParserListener</code>s.
 *
 * @see         jp.kyasu.sgml.Element
 * @see         jp.kyasu.sgml.SGMLParser
 * @see         jp.kyasu.sgml.SGMLParserListener
 *
 * @version 	05 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public class SGMLEvent extends java.util.EventObject {
    /**
     * The id of the event.
     */
    protected int id;

    /**
     * The element. This is valid when id is STARTTAG_PARSED or ENDTAG_PARSED.
     */
    protected Element element;

    /**
     * The attributes. This is valid when id is STARTTAG_PARSED.
     */
    protected Hashtable attributes;

    /**
     * The cdata. This is valid when id is CDATA_PARSED.
     */
    protected String cdata;


    /**
     * Marks the first integer id for the range of sgml event ids.
     */
    static public final int SGML_FIRST = java.awt.AWTEvent.RESERVED_ID_MAX + 1;

    /**
     * Marks the last integer id for the range of sgml event ids.
     */
    static public final int SGML_LAST  = SGML_FIRST + 3;

    /**
     * The start tag parsed event type.
     */
    static public final int STARTTAG_PARSED  = SGML_FIRST;

    /**
     * The end tag parsed event type.
     */
    static public final int ENDTAG_PARSED    = STARTTAG_PARSED + 1;

    /**
     * The cdata (#PCDATA, #CDATA, #RCDATA) parsed event type.
     */
    static public final int CDATA_PARSED     = ENDTAG_PARSED   + 1;

    /**
     * The parsing finished event type.
     */
    static public final int PARSING_FINISHED = CDATA_PARSED    + 1;


    /**
     * Constructs a sgml event with the specified sgml parser (event source),
     * id, element, and attributes. The id must be STARTTAG_PARSED.
     *
     * @param     parser     the sgml parser (event source).
     * @param     id         the id.
     * @param     element    the element.
     * @param     attributes the attributes.
     * @exception IllegalArgumentException if the id is not STARTTAG_PARSED.
     */
    public SGMLEvent(SGMLParser parser, int id,
		     Element element, Hashtable attributes)
    {
	super(parser);
	if (element == null || attributes == null)
	    throw new NullPointerException();
	if (id != STARTTAG_PARSED) {
	    throw new IllegalArgumentException("improper id: " + id);
	}
	this.id         = STARTTAG_PARSED;
	this.element    = element;
	this.attributes = attributes;
	this.cdata      = null;
    }

    /**
     * Constructs a sgml event with the specified sgml parser (event source),
     * id, and element. The id must be ENDTAG_PARSED.
     *
     * @param     parser  the sgml parser (event source).
     * @param     id      the id.
     * @param     element the element.
     * @exception IllegalArgumentException if the id is not ENDTAG_PARSED.
     */
    public SGMLEvent(SGMLParser parser, int id, Element element) {
	super(parser);
	if (element == null)
	    throw new NullPointerException();
	if (id != ENDTAG_PARSED) {
	    throw new IllegalArgumentException("improper id: " + id);
	}
	this.id         = ENDTAG_PARSED;
	this.element    = element;
	this.attributes = null;
	this.cdata      = null;
    }

    /**
     * Constructs a sgml event with the specified sgml parser (event source),
     * id, and cdata. The id must be CDATA_PARSED.
     *
     * @param     parser the sgml parser (event source).
     * @param     id     the id.
     * @param     cdata  the cdata.
     * @exception IllegalArgumentException if the id is not CDATA_PARSED.
     */
    public SGMLEvent(SGMLParser parser, int id, String cdata) {
	super(parser);
	if (cdata == null)
	    throw new NullPointerException();
	if (id != CDATA_PARSED) {
	    throw new IllegalArgumentException("improper id: " + id);
	}
	this.id         = CDATA_PARSED;
	this.element    = null;
	this.attributes = null;
	this.cdata      = cdata;
    }

    /**
     * Constructs a sgml event with the specified sgml parser (event source)
     * and id. The id must be PARSING_FINISHED.
     *
     * @param     parser the sgml parser (event source).
     * @param     id     the id.
     * @exception IllegalArgumentException if the id is not PARSING_FINISHED.
     */
    public SGMLEvent(SGMLParser parser, int id) {
	super(parser);
	if (id != PARSING_FINISHED) {
	    throw new IllegalArgumentException("improper id: " + id);
	}
	this.id         = PARSING_FINISHED;
	this.element    = null;
	this.attributes = null;
	this.cdata      = null;
    }


    /**
     * Returns the id of this sgml event.
     *
     * @retrun the id of this sgml event.
     */
    public int getID() {
	return id;
    }

    /**
     * Returns the sgml parser (event source) of this sgml event.
     *
     * @retrun the sgml parser (event source) of this sgml event.
     */
    public SGMLParser getParser() {
	return (SGMLParser)source;
    }

    /**
     * Returns the element of this sgml event. This operation is valid
     * when id is STARTTAG_PARSED or ENDTAG_PARSED.
     *
     * @retrun the element of this sgml event.
     */
    public Element getElement() {
	return element;
    }

    /**
     * Returns the attributes of this sgml event. This operation is valid
     * when id is STARTTAG_PARSED.
     *
     * @retrun the attributes of this sgml event.
     */
    public Hashtable getAttributes() {
	return attributes;
    }

    /**
     * Returns the cdata (#PCDATA, #CDATA, #RCDATA) of this sgml event.
     * This operation is valid when id is CDATA_PARSED.
     *
     * @retrun the cdata of this sgml event.
     */
    public String getCDATA() {
	return cdata;
    }
}
