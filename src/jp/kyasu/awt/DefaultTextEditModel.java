/*
 * DefaultTextEditModel.java
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
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.ParagraphStyleModifier;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.TextStyleModifier;
import jp.kyasu.graphics.text.TextChange;
import jp.kyasu.util.RunArray;

import java.awt.AWTEventMulticaster;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>DefaultTextEditModel</code> class is a default implementation
 * of the <code>TextEditModel</code> interface.
 *
 * @version 	11 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class DefaultTextEditModel extends DefaultTextModel
	implements TextEditModel
{
    /** The text listener of the text edit model. */
    transient protected TextListener textListener;


    /**
     * Constructs an empty text edit model with the specified rich text style.
     *
     * @param richTextStyle the rich text style.
     */
    public DefaultTextEditModel(RichTextStyle richTextStyle) {
	this(new RichText(richTextStyle));
    }

    /**
     * Constructs a text edit model with the specified rich text.
     *
     * @param richText the rich text.
     */
    public DefaultTextEditModel(RichText richText) {
	super(richText);
	textListener = null;
    }


    /**
     * Constructor for subclasses.
     */
    protected DefaultTextEditModel() {
	super();
	textListener = null;
    }


    /**
     * Sets the rich text of this text model to be the specified rich text.
     *
     * @param richText the rich text.
     */
    public void setRichText(RichText richText) {
	if (richText.getTextStyleConstraint() == null) {
	    TextStyleModifier constraint = null;
	    if (this.richText != null) {
		constraint = this.richText.getTextStyleConstraint();
	    }
	    if (constraint == null) {
		constraint = new ConcreteTextConstraint();
	    }
	    richText.setTextStyleConstraint(constraint);
	}
	super.setRichText(richText);
	//notifyTextListeners(new TextEvent(this, TextEvent.TEXT_VALUE_CHANGED));
    }


    /**
     * Adds the specified text listener to receive text events from
     * this text edit model.
     *
     * @param listener the text listener.
     */
    public void addTextListener(TextListener listener) {
	textListener = AWTEventMulticaster.add(textListener, listener);
    }

    /**
     * Removes the specified text listener so it no longer receives
     * text events from this text edit model.
     *
     * @param listener the text listener.
     */
    public void removeTextListener(TextListener listener) {
	textListener = AWTEventMulticaster.remove(textListener, listener);
    }

    /** Notifies the specified text event to the text listeners. */
    protected void notifyTextListeners(TextEvent event) {
	if (textListener != null) {
	    textListener.textValueChanged(event);
	}
    }

    /**
     * Replaces the specified range of the rich text of this text edit
     * model with the specified replacement text object.
     *
     * @param  begin the beginning text position to replace, inclusive.
     * @param  end   the ending text position to replace, exclusive.
     * @param  rep   a replacement <code>Text</code> object.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public synchronized Undo replace(int begin, int end, Text rep) {
	RichText oldRichText = richText.subtext(begin, end);
	TextChange change = richText.replace(begin, end, rep);
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
	notifyTextListeners(new TextEvent(this, TextEvent.TEXT_VALUE_CHANGED));
	return new ReplaceUndo(begin, begin + rep.length(), oldRichText);
    }

    /*
     * Sets the text style in the specified range of the rich text of this
     * text edit model to be the specified text style.
     *
     * @param  begin     the beginning text position to set, inclusive.
     * @param  end       the ending text position to set, exclusive.
     * @param  textStyle the text style.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public synchronized Undo setTextStyle(int begin, int end,
					  TextStyle textStyle)
    {
	RunArray oldTextStyles =
			richText.getText().getTextStyleArray(begin, end);
	TextChange change = richText.setTextStyle(begin, end, textStyle);
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
	return new TextStyleUndo(begin, end, oldTextStyles);
    }

    /*
     * Modifies the text style in the specified range of the rich text of
     * this text edit model by using the specified text style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the text style modifier.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public synchronized Undo modifyTextStyle(int begin, int end,
					     TextStyleModifier modifier)
    {
	RunArray oldTextStyles =
			richText.getText().getTextStyleArray(begin, end);
	TextChange change = richText.modifyTextStyle(begin, end, modifier);
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
	return new TextStyleUndo(begin, end, oldTextStyles);
    }

    /*
     * Sets the paragraph style in the specified range of the rich text
     * of this text edit model to be the specified paragraph style.
     *
     * @param  begin  the beginning text position to set, inclusive.
     * @param  end    the ending text position to set, exclusive.
     * @param  pStyle the paragraph style.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public synchronized Undo setParagraphStyle(int begin, int end,
					       ParagraphStyle pStyle)
    {
	ParagraphStyle oldParagraphStyles[] =
		(begin == end ?
			richText.getParagraphStylesPerParagraph(begin, end+1) :
			richText.getParagraphStylesPerParagraph(begin, end));
	TextChange change = richText.setParagraphStyle(begin, end, pStyle);
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
	return new ParagraphStyleUndo(begin, end, oldParagraphStyles);
    }

    /*
     * Modifies the paragraph style in the specified range of the rich text of
     * this text edit model by using the specified paragraph style modifier.
     *
     * @param  begin    the beginning text position to modify, inclusive.
     * @param  end      the ending text position to modify, exclusive.
     * @param  modifier the paragraph style modifier.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public synchronized Undo modifyParagraphStyle(int begin, int end,
						  ParagraphStyleModifier modifier)
    {
	ParagraphStyle oldParagraphStyles[] =
		(begin == end ?
			richText.getParagraphStylesPerParagraph(begin, end+1) :
			richText.getParagraphStylesPerParagraph(begin, end));
	TextChange change = richText.modifyParagraphStyle(begin, end, modifier);
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
	return new ParagraphStyleUndo(begin, end, oldParagraphStyles);
    }

    /**
     * Replaces the specified range of the rich text of this text edit
     * model with the specified replacement rich text object.
     *
     * @param  begin the beginning text position to replace, inclusive.
     * @param  end   the ending text position to replace, exclusive.
     * @param  rep   a replacement <code>RichText</code> object.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    protected synchronized Undo replace(int begin, int end, RichText rep) {
	RichText oldRichText = richText.subtext(begin, end);
	TextChange change = richText.replace(begin, end, rep);
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
	notifyTextListeners(new TextEvent(this, TextEvent.TEXT_VALUE_CHANGED));
	return new ReplaceUndo(begin, begin + rep.length(), oldRichText);
    }

    /*
     * Sets the text styles in the specified range of the rich text of this
     * text edit model to be the specified text style.
     *
     * @param  begin      the beginning text position to set, inclusive.
     * @param  end        the ending text position to set, exclusive.
     * @param  textStyles the text styles.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    protected synchronized Undo setTextStyles(int begin, int end,
					      RunArray textStyles)
    {
	RunArray oldTextStyles =
			richText.getText().getTextStyleArray(begin, end);
	TextChange change = richText.setTextStyles(begin, end, textStyles);
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
	return new TextStyleUndo(begin, end, oldTextStyles);
    }

    /*
     * Sets the paragraph styles in the specified range of the rich text
     * of this text edit model to be the specified paragraph style.
     *
     * @param  begin   the beginning text position to set, inclusive.
     * @param  end     the ending text position to set, exclusive.
     * @param  pStyles the paragraph styles.
     * @return the undo object for this operation, or <code>null</code> if
     *         the undo is not supported.
     */
    public synchronized Undo setParagraphStyles(int begin, int end,
						ParagraphStyle pStyles[])
    {
	ParagraphStyle oldParagraphStyles[] =
		(begin == end ?
			richText.getParagraphStylesPerParagraph(begin, end+1) :
			richText.getParagraphStylesPerParagraph(begin, end));
	TextChange change = richText.setParagraphStyles(begin, end, pStyles);
	notifyTextModelListeners(new TextModelEvent(
					this,
					TextModelEvent.TEXT_MODEL_EDITED,
					change));
	return new ParagraphStyleUndo(begin, end, oldParagraphStyles);
    }


    /**
     * Undo for the replace operations.
     */
    public class ReplaceUndo implements Undo {
	int begin;
	int end;
	RichText oldRichText;

	public ReplaceUndo(int begin, int end, RichText oldRichText) {
	    this.begin        = begin;
	    this.end          = end;
	    this.oldRichText  = oldRichText;
	}

	public Undo undo() {
	    return replace(begin, end, oldRichText);
	}
    }

    /**
     * Undo for the text style operations.
     */
    public class TextStyleUndo implements Undo {
	int begin;
	int end;
	RunArray oldTextStyles;

	public TextStyleUndo(int begin, int end, RunArray oldTextStyles) {
	    this.begin         = begin;
	    this.end           = end;
	    this.oldTextStyles = oldTextStyles;
	}

	public Undo undo() {
	    return setTextStyles(begin, end, oldTextStyles);
	}
    }

    /**
     * Undo for the paragraph style operations.
     */
    public class ParagraphStyleUndo implements Undo {
	int begin;
	int end;
	ParagraphStyle oldParagraphStyles[];

	public ParagraphStyleUndo(int begin, int end,
				  ParagraphStyle oldParagraphStyles[])
	{
	    this.begin              = begin;
	    this.end                = end;
	    this.oldParagraphStyles = oldParagraphStyles;
	}

	public Undo undo() {
	    return setParagraphStyles(begin, end, oldParagraphStyles);
	}
    }


    /** Internal constant for serialization */
    static protected final String textListenerK = "textL".intern();

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	jp.kyasu.awt.ListenerSerializer.write(s,
					      textListenerK,
					      textListener);
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws ClassNotFoundException, java.io.IOException
    {
	s.defaultReadObject();
	Object keyOrNull;
	while ((keyOrNull = s.readObject()) != null) {
	    String key = ((String)keyOrNull).intern();
	    if (key == textListenerK)
		addTextListener((TextListener)s.readObject());
	    else // skip value for unrecognized key
		s.readObject();
	}
    }
}


class ConcreteTextConstraint implements TextStyleModifier, java.io.Serializable
{
    public TextStyle modify(TextStyle tStyle) {
	return tStyle.concreteStyle();
    }
}
