/*
 * TextModelEvent.java
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

package jp.kyasu.awt.event;

import jp.kyasu.awt.TextModel;
import jp.kyasu.graphics.text.TextChange;

/**
 * The TextModel event that is originated from a <code>TextModel</code> to
 * <code>TextModelListener</code>s.
 *
 * @see 	jp.kyasu.awt.TextModel
 * @see 	jp.kyasu.awt.event.TextModelListener
 *
 * @version 	11 Mar 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextModelEvent extends java.util.EventObject {
    /**
     * The id of the event.
     */
    protected int id;

    /**
     * The beginning index to paint, inclusive.
     */
    protected int paintBegin;

    /**
     * The ending index to paint, exclusive.
     */
    protected int paintEnd;

    /**
     * The text change information of the event.
     * This is valid when id is TEXT_MODEL_EDITED.
     */
    protected TextChange textChange;


    /**
     * Marks the first integer id for the range of text model event ids.
     */
    static public final int TEXT_MODEL_FIRST =
					java.awt.AWTEvent.RESERVED_ID_MAX + 1;

    /**
     * Marks the last integer id for the range of text model event ids.
     */
    static public final int TEXT_MODEL_LAST  = TEXT_MODEL_FIRST + 1;

    /**
     * The text model is edited.
     */
    static public final int TEXT_MODEL_EDITED  = TEXT_MODEL_FIRST;

    /**
     * The text model is updated.
     */
    static public final int TEXT_MODEL_UPDATED = TEXT_MODEL_EDITED + 1;


    /**
     * Constructs a text model event with the specified text model (event
     * source) and id. The id must be TEXT_MODEL_UPDATED.
     *
     * @param model the text model (event source).
     * @param id    the id.
     * @exception IllegalArgumentException if the id is not TEXT_MODEL_UPDATED.
     */
    public TextModelEvent(TextModel model, int id) {
	super(model);
	if (id != TEXT_MODEL_UPDATED)
	    throw new IllegalArgumentException("improper id: " + id);
	this.id         = id;
	this.paintBegin = -1;
	this.paintEnd   = -1;
	this.textChange = null;
    }

    /**
     * Constructs a text model event with the specified text model (event
     * source), id, and text change object. The id must be TEXT_MODEL_EDITED.
     *
     * @param model      the text model (event source).
     * @param id         the id.
     * @param textChange the text change infromation.
     * @exception IllegalArgumentException if the id is not TEXT_MODEL_EDITED.
     */
    public TextModelEvent(TextModel model, int id, TextChange textChange) {
	this(model, id, -1, -1, textChange);
    }

    /**
     * Constructs a text model event with the specified text model (event
     * source), id, range to paint, and text change object. The id must be
     * TEXT_MODEL_EDITED.
     *
     * @param model      the text model (event source).
     * @param id         the id.
     * @param paintBegin the beginning index to paint, inclusive.
     * @param paintEnd   the ending index to paint, exclusive.
     * @param textChange the text change infromation.
     * @exception IllegalArgumentException if the id is not TEXT_MODEL_EDITED.
     */
    public TextModelEvent(TextModel model, int id, int paintBegin, int paintEnd,
			  TextChange textChange)
    {
	super(model);
	if (id != TEXT_MODEL_EDITED)
	    throw new IllegalArgumentException("improper id: " + id);
	if (textChange == null)
	    throw new NullPointerException();
	this.id         = id;
	this.paintBegin = paintBegin;
	this.paintEnd   = paintEnd;
	this.textChange = textChange;
    }


    /**
     * Returns the id of this text model event.
     */
    public int getID() {
	return id;
    }

    /**
     * Returns the text model (event source) of this text model event.
     */
    public TextModel getModel() {
	return (TextModel)source;
    }

    /**
     * Returns the beginning index to paint, inclusive.
     */
    public int getPaintBegin() {
	return paintBegin;
    }

    /**
     * Returns the beginning index to paint, exclusive.
     */
    public int getPaintEnd() {
	return paintEnd;
    }

    /**
     * Returns the <code>TextChange</code> object of this text model event.
     * This operation is valid when id is TEXT_MODEL_EDITED.
     */
    public TextChange getTextChange() {
	return textChange;
    }

    public String paramString() {
	String typeStr;
	switch(id) {
	case TEXT_MODEL_EDITED:
	    typeStr = "TEXT_MODEL_EDITED";
	    break;
	case TEXT_MODEL_UPDATED:
	    typeStr = "TEXT_MODEL_UPDATED";
	    break;
	default:
	    typeStr = "unknown type";
	    break;
	}
	return typeStr + ",paintBegin="  + paintBegin
			+ ",paintEnd="   + paintEnd
			+ ",textChange=" + textChange;
    }
}
