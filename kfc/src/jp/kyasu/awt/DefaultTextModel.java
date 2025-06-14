/*
 * DefaultTextModel.java
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

import jp.kyasu.awt.event.TextModelEvent;
import jp.kyasu.awt.event.TextModelListener;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;

import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>DefaultTextModel</code> class is a default implementation of
 * the <code>TextModel</code> interface.
 *
 * @version 	27 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class DefaultTextModel implements TextModel, java.io.Serializable {
    /** The rich text of the text model. */
    protected RichText richText;

    /** The text model listeners of the text model. */
    transient protected Vector textModelListeners;


    /**
     * Constructs an empty text model with the specified rich text style.
     *
     * @param richTextStyle the rich text style.
     */
    public DefaultTextModel(RichTextStyle richTextStyle) {
	this(new RichText(richTextStyle));
    }

    /**
     * Constructs a text model with the specified rich text.
     *
     * @param richText the rich text.
     */
    public DefaultTextModel(RichText richText) {
	textModelListeners = null;
	setRichText(richText);
    }


    /**
     * Constructor for subclasses.
     */
    protected DefaultTextModel() {
	textModelListeners = null;
    }


    /**
     * Adds the specified text model listener to receive text model events
     * from this text model.
     *
     * @param listener the text model listener.
     */
    public void addTextModelListener(TextModelListener listener) {
	if (listener == null)
	    return;
	if (textModelListeners == null)
	    textModelListeners = new Vector();
	textModelListeners.addElement(listener);
    }

    /**
     * Removes the specified text model listener so it no longer receives
     * text model events from this text model.
     *
     * @param listener the text model listener.
     */
    public void removeTextModelListener(TextModelListener listener) {
	if (textModelListeners == null)
	    return;
	textModelListeners.removeElement(listener);
	if (textModelListeners.size() == 0)
	    textModelListeners = null;
    }

    /** Notifies the specified text model event to the text model listeners. */
    protected void notifyTextModelListeners(TextModelEvent event) {
	if (textModelListeners == null)
	    return;
	for (Enumeration e = textModelListeners.elements();
	     e.hasMoreElements();
	     )
	{
	    ((TextModelListener)e.nextElement()).textModelChanged(event);
	}
    }

    /**
     * Returns the rich text of this text model.
     *
     * @return the rich text.
     */
    public RichText getRichText() {
	return richText;
    }

    /**
     * Sets the rich text of this text model to be the specified rich text.
     *
     * @param richText the rich text.
     */
    public synchronized void setRichText(RichText richText) {
	if (richText == null)
	    throw new NullPointerException();
	this.richText = richText;
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_UPDATED));
    }


    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	if (textModelListeners != null) {
	    for (Enumeration e = textModelListeners.elements();
		 e.hasMoreElements();
		 )
	    {
		TextModelListener l = (TextModelListener)e.nextElement();
		if (l instanceof java.io.Serializable) {
		    s.writeObject(l);
		}
	    }
	}
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	s.defaultReadObject();
	Object listenerOrNull;
	while ((listenerOrNull = s.readObject()) != null) {
	    addTextModelListener((TextModelListener)listenerOrNull);
	}
    }
}
