/*
 * HTMLEvent.java
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
 * The HTML event that is delivered from <code>HTMLParser</code> to
 * <code>SGMLParserListener</code>s.
 *
 * @see         jp.kyasu.sgml.Element
 * @see         jp.kyasu.sgml.HTMLParser
 * @see         jp.kyasu.sgml.SGMLParserListener
 *
 * @version 	07 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class HTMLEvent extends SGMLEvent {
    /**
     * True if the cdata is nbsp (no break space).
     */
    protected boolean nbsp;


    /**
     * Constructs a html event with the specified html parser (event source),
     * id, element, and attributes. The id must be STARTTAG_PARSED.
     *
     * @param     parser     the html parser (event source).
     * @param     id         the id.
     * @param     element    the element.
     * @param     attributes the attributes.
     * @exception IllegalArgumentException if the id is not STARTTAG_PARSED.
     */
    public HTMLEvent(HTMLParser parser, int id,
		     Element element, Hashtable attributes)
    {
	super(parser, id, element, attributes);
	this.nbsp = false;
    }

    /**
     * Constructs a html event with the specified html parser (event source),
     * id, and element. The id must be ENDTAG_PARSED.
     *
     * @param     parser  the html parser (event source).
     * @param     id      the id.
     * @param     element the element.
     * @exception IllegalArgumentException if the id is not ENDTAG_PARSED.
     */
    public HTMLEvent(HTMLParser parser, int id, Element element) {
	super(parser, id, element);
	this.nbsp = false;
    }

    /**
     * Constructs a html event with the specified html parser (event source),
     * id, and cdata. The id must be CDATA_PARSED.
     *
     * @param     parser the html parser (event source).
     * @param     id     the id.
     * @param     cdata  the cdata.
     * @exception IllegalArgumentException if the id is not CDATA_PARSED.
     */
    public HTMLEvent(HTMLParser parser, int id, String cdata) {
	this(parser, id, cdata, false);
    }

    /**
     * Constructs a html event with the specified html parser (event source),
     * id, cdata, and the flag indicating nbsp (no break space).
     * The id must be CDATA_PARSED.
     *
     * @param     parser the html parser (event source).
     * @param     id     the id.
     * @param     cdata  the cdata.
     * @param     nbsp   true if the cdata is nbsp (non break space).
     * @exception IllegalArgumentException if the id is not CDATA_PARSED.
     */
    public HTMLEvent(HTMLParser parser, int id, String cdata, boolean nbsp) {
	super(parser, id, cdata);
	this.nbsp = nbsp;
    }

    /**
     * Constructs a html event with the specified html parser (event source)
     * and id. The id must be PARSING_FINISHED.
     *
     * @param     parser the html parser (event source).
     * @param     id     the id.
     * @exception IllegalArgumentException if the id is not PARSING_FINISHED.
     */
    public HTMLEvent(HTMLParser parser, int id) {
	super(parser, id);
    }


    /**
     * Checks if the cdata is a nbsp (non break space).
     * This operation is valid when id is CDATA_PARSED.
     *
     * @retrun true if the cdata is nbsp (non break space).
     */
    public boolean isNbsp() {
	return nbsp;
    }
}
