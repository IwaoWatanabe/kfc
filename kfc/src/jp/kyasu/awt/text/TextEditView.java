/*
 * TextEditView.java
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

package jp.kyasu.awt.text;

import jp.kyasu.awt.AWTResources;
import jp.kyasu.awt.TextModel;
import jp.kyasu.awt.TextEditModel;
import jp.kyasu.awt.event.TextModelEvent;
import jp.kyasu.awt.event.TextModelListener;
import jp.kyasu.awt.event.TextPositionEvent;
import jp.kyasu.awt.event.TextPositionListener;
import jp.kyasu.graphics.ClickableTextAction;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextLayout;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.text.TextChange;
import jp.kyasu.graphics.text.TextLayoutChange;
import jp.kyasu.graphics.text.TextPositionInfo;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

/*if[JDK1.2]
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.awt.font.TextHitInfo;
/*end[JDK1.2]*/

/**
 * The <code>TextEditView</code> class implements a view of a MVC model for
 * the text editing. The model of the MVC model is a <code>TextEditModel</code>
 * object and the controller of the MVC model is a
 * <code>TextEditController</code> object.
 *
 * @see 	jp.kyasu.awt.TextEditModel
 * @see 	jp.kyasu.awt.text.TextEditController
 *
 * @version 	12 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextEditView extends TextView implements TextModelListener {
    protected TextEditModel model;
    protected TextEditController controller;
    protected boolean editable;
    protected Color caretColor;
    protected TextCaret textCaret;
    transient protected TextPositionInfo selectionBegin;
    transient protected TextPositionInfo selectionEnd;

    transient protected Vector textPositionListeners;


    /**
     * The default cursor.
     */
    static public final Cursor DEFAULT_CURSOR =
	Cursor.getPredefinedCursor(AWTResources.getResourceInteger(
					"kfc.text.cursor", Cursor.TEXT_CURSOR));


    /**
     * Constructs a text edit view with the specified text edit model.
     *
     * @param textEditModel the text edit model.
     */
    public TextEditView(TextEditModel textEditModel) {
	super(textEditModel.getRichText().getRichTextStyle().getLineWrap());
	if (textEditModel == null)
	    throw new NullPointerException();

	model = textEditModel;
	model.addTextModelListener(this);
	controller = new TextEditController(this);
	controller.addToView();

	editable = true;

	caretColor = TextCaret.DEFAULT_CARET_COLOR;
	setTextCaret(new TextCaret());

	setCursor(DEFAULT_CURSOR);

	setTextLayout(createTextLayout());

	//enableInputMethods(true);
    }


    /**
     * Returns the <code>RichText</code> object being viewed.
     */
    public RichText getRichText() {
	return model.getRichText();
    }

    /**
     * Returns the model of this view.
     */
    public TextEditModel getModel() {
	return model;
    }

    /**
     * Returns the controller of this view.
     */
    public TextEditController getController() {
	return controller;
    }

    /**
     * Sets the controller of this view.
     */
    public synchronized void setController(TextEditController controller) {
	if (controller == null)
	    throw new NullPointerException();
	if (controller.view != this)
	    throw new IllegalArgumentException("view of controller is not valid");
	this.controller.removeFromView();
	this.controller = controller;
	this.controller.addToView();
    }

    /**
     * Invoked when the text model has been changed.
     * @see jp.kyasu.awt.event.TextModelListener
     */
    public void textModelChanged(TextModelEvent event) {
	switch (event.getID()) {
	case TextModelEvent.TEXT_MODEL_UPDATED:
	    setTextLayout(createTextLayout());
	    controller.lastUndo = null;
	    controller.setCurrentTypeIn(getSelectionBegin());
	    break;
	case TextModelEvent.TEXT_MODEL_EDITED:
	    updateAfterEdited(event);
	    break;
	}
    }

    /**
     * Sets the <code>TextLayout</code> object of this view.
     */
    protected synchronized void setTextLayout(TextLayout layout) {
	if (layout == null)
	    throw new NullPointerException();
	if (isShowing() && needsToRedrawSelection()) {
	    hideSelection();
	}

	selectionBegin = selectionEnd = null;

	super.setTextLayout(layout);

	if (isValid()) {
	    selectionBegin = selectionEnd = layout.getTextPositionAt(0);
	}
    }

    /**
     * Sets the <code>Text</code> object of this view.
     * @param text the <code>Text</code> object.
     */
    public void setText(Text text) {
	if (text == null)
	    throw new NullPointerException();
	setRichText(new RichText(text, model.getRichText().getRichTextStyle()));
    }

    /**
     * Sets the <code>RichText</code> object of this view.
     * @param richText the <code>RichText</code> object.
     */
    public void setRichText(RichText richText) {
	if (richText == null)
	    throw new NullPointerException();
	model.setRichText(richText);
    }

    /**
     * Returns the beginning text position of the selection, inclusive.
     */
    public TextPositionInfo getSelectionBegin() {
	return selectionBegin;
    }

    /**
     * Sets the beginning text position of the selection, inclusive.
     */
    protected void setSelectionBegin(TextPositionInfo posInfo) {
	selectionBegin = posInfo;
    }

    /**
     * Returns the ending text position of the selection, exclusive.
     */
    public TextPositionInfo getSelectionEnd() {
	return selectionEnd;
    }

    /**
     * Sets the ending text position of the selection, exclusive.
     */
    protected void setSelectionEnd(TextPositionInfo posInfo) {
	selectionEnd = posInfo;
    }

    /**
     * Sets the text position of the selection.
     */
    protected void setSelectionBeginEnd(TextPositionInfo posInfo) {
	selectionBegin = selectionEnd = posInfo;
    }

    /**
     * Sets the range of the selection.
     */
    protected void setSelectionBeginEnd(TextPositionInfo begin,
					TextPositionInfo end)
    {
	selectionBegin = begin;
	selectionEnd   = end;
    }

    /**
     * Tests if this view is editable.
     */
    public boolean isEditable() {
	return editable;
    }

    /**
     * Makes this view editable.
     */
    public void setEditable(boolean b) {
	editable = b;
    }

    /**
     * Enables or disables this view.
     */
    public synchronized void setEnabled(boolean b) {
	if (isEnabled() == b)
	    return;
	super.setEnabled(b);
	if (isEnabled()) {
	    startTextCaret();
	}
	else {
	    stopTextCaret();
	}
    }

    /**
     * Returns the caret color.
     * @see #setCaretColor(java.awt.Color)
     */
    public Color getCaretColor() {
	return caretColor;
    }

    /**
     * Sets the caret color.
     * @see #getCaretColor()
     */
    public synchronized void setCaretColor(Color c) {
	if (c == null)
	    return;

	if (caretColor.equals(c)) {
	    return;
	}

	boolean showing =
		isShowing() && selectionShowing && selectionIsCaret();
	if (showing) hideSelection();
	caretColor = c;
	if (showing) showSelection();
    }

    /**
     * Returns the text caret of this view.
     * @see #setTextCaret(jp.kyasu.awt.text.TextCaret)
     */
    public TextCaret getTextCaret() {
	return textCaret;
    }

    /**
     * Sets the text caret of this view.
     * @see #getTextCaret()
     */
    public synchronized void setTextCaret(TextCaret textCaret) {
	if (textCaret == null)
	    throw new NullPointerException();
	boolean showing =
		isShowing() && selectionShowing && selectionIsCaret();
	boolean blinking =
		this.textCaret != null && this.textCaret.isBlinking();
	if (showing) hideSelection();
	if (blinking) stopTextCaret();

	if (this.textCaret != null) this.textCaret.setTarget(null);
	this.textCaret = textCaret;
	this.textCaret.setTarget(this);

	if (blinking) startTextCaret();
	if (showing) showSelection();
    }

    /**
     * Starts the text caret of this view.
     * @see #stopTextCaret()
     */
    protected synchronized void startTextCaret() {
	if (textCaret != null) {
	    boolean showing =
		isShowing() && selectionShowing && selectionIsCaret();
	    if (showing) hideSelection();
	    textCaret.start();
	    if (showing) showSelection();
	}
    }

    /**
     * Stops the text caret of this view.
     * @see #startTextCaret()
     */
    protected synchronized void stopTextCaret() {
	if (textCaret != null) {
	    boolean showing =
		isShowing() && selectionShowing && selectionIsCaret();
	    if (showing) hideSelection();
	    textCaret.stop();
	    if (showing) showSelection();
	}
    }

    /**
     * Returns the selected text.
     */
    public Text getSelectedText() {
	if (selectionBegin == null || selectionEnd == null ||
	    selectionBegin.textIndex == selectionEnd.textIndex)
	{
	    return new Text();
	}
	return model.getRichText().getText().subtext(selectionBegin.textIndex,
						     selectionEnd.textIndex);
    }

    /**
     * Resets the location of the layout text.
     * This method is called from setBounds().
     */
    protected void resetLocationOfText() {
	if (selectionBegin == null || selectionEnd == null) {
	    selectionBegin = selectionEnd = layout.getTextPositionAt(0);
	    offset.x = offset.y = 0;
	}
	else {
	    selectionBegin = layout.getTextPositionAt(selectionBegin.textIndex);
	    selectionEnd = layout.getTextPositionAt(selectionEnd.textIndex);
	    offset.x = getScrollXTo(selectionBegin);
	    offset.y = getScrollYTo(selectionBegin);
	}
    }

    /**
     * Notifies this view that it has been added to a container.
     */
    public void addNotify() {
	/*
	if (textCaret != null) {
	    textCaret.start();
	}
	*/
	super.addNotify();
    }

    /**
     * Notifies this view that it has been removed from its container.
     */
    public void removeNotify() {
	if (textCaret != null) {
	    textCaret.stop();
	}
	super.removeNotify();
    }

    /**
     * Sets the font of this view.
     */
    public void setFont(Font f) {
	super.setFont(f);
	RichText richText = model.getRichText();
	richText.setBaseTextStyle(new TextStyle(f));
	model.setRichText(richText);
    }

    /**
     * Add a listener to recieve text position events when the selection
     * of the text view changes.
     * @param l the listener to recieve events.
     */
    public void addTextPositionListener(TextPositionListener l) {
	if (l == null)
	    return;
	if (textPositionListeners == null)
	    textPositionListeners = new Vector();
	textPositionListeners.addElement(l);
    }

    /**
     * Removes an text position listener.
     * @param l the listener being removed.
     */
    public void removeTextPositionListener(TextPositionListener l) {
	if (textPositionListeners == null)
	    return;
	textPositionListeners.removeElement(l);
	if (textPositionListeners.size() == 0)
	    textPositionListeners = null;
    }

    /**
     * Notifies the text position event to the text position listeners.
     */
    protected void notifyTextPositionListeners() {
	if (textPositionListeners == null)
	    return;
	if (selectionBegin == null || selectionEnd == null)
	    return;
	TextPositionEvent event =
		new TextPositionEvent(this,
				      TextPositionEvent.TEXT_POSITION_CHANGED,
				      selectionBegin, selectionEnd);
	for (Enumeration e = textPositionListeners.elements();
	     e.hasMoreElements();
	     )
	{
	    ((TextPositionListener)e.nextElement()).textPositionChanged(event);
	}
    }

    /**
     * Paints this component.
     */
    public void paint(Graphics g) {
	if (!isShowing())
	    return;

	super.paint(g);

	if (needsToRedrawSelection())
	    showSelection();
    }

    /**
     * Paints this view with the specified range.
     */
    protected void paint(Graphics g,
			 TextPositionInfo begin, TextPositionInfo end)
    {
	if (!selectionVisible || selectionBegin == null || selectionEnd == null)
	{
	    selectionShowing = false;
	    g.setColor(getForeground());
	    layout.draw(g, offset, begin, end);
	    return;
	}

	if (selectionBegin.textIndex == selectionEnd.textIndex) {
	    hideSelection(); // hide caret
	    g.setColor(getForeground());
	    layout.draw(g, offset, begin, end);
	    if (!doubleBuffered) {
		showSelection(); // show caret
	    }
	    return;
	}

	selectionShowing = true;

	if (selectionBegin.textIndex > end.textIndex ||
	    selectionEnd.textIndex < begin.textIndex)
	{
	    g.setColor(getForeground());
	    layout.draw(g, offset, begin, end);
	    return;
	}

	// begin <= selectionEnd && selectionBegin <= end
	TextPositionInfo newBegin;
	if (selectionBegin.textIndex <= begin.textIndex)
	    newBegin = begin;
	else { // begin.textIndex < selectionBegin.textIndex
	    g.setColor(getForeground());
	    layout.draw(g, offset, begin, selectionBegin);
	    newBegin = selectionBegin;
	}
	if (end.textIndex <= selectionEnd.textIndex) {
	    g.setColor(selectionForeground);
	    layout.draw(g, offset, newBegin, end,
			    selectionBackground,
			    selectionBegin.lineIndex < newBegin.lineIndex,
			    (end.textIndex == end.lineBegin ?
			       false : end.lineIndex < selectionEnd.lineIndex));
	    g.setColor(getForeground());
	}
	else {
	    g.setColor(selectionForeground);
	    layout.draw(g, offset, newBegin, selectionEnd,
			    selectionBackground,
			    selectionBegin.lineIndex < newBegin.lineIndex,
			    false);
	    g.setColor(getForeground());
	    layout.draw(g, offset, selectionEnd, end);
	}
    }

    /**
     * Shows the selection.
     */
    protected synchronized void showSelection() {
	if (!isShowing())
	    return;
	if (!selectionVisible || selectionShowing)
	    return;
	if (selectionBegin == null || selectionEnd == null)
	    return;
	/*
	if (!selectionVisible) {
	    return;
	    if (selectionIsCaret())
		return;
	    selectionVisible = true;
	}
	*/
	selectionShowing = !selectionShowing;
	if (selectionBegin.textIndex == selectionEnd.textIndex) {
	    if (textCaret != null) {
		Graphics g = getGraphics();
		if (g != null) {
		    textCaret.showCaret(g,
					offset,
					selectionBegin,
					caretColor);
		    g.dispose();
		}
	    }
	}
	else
	    paintSelection(selectionForeground, selectionBackground);
    }

    /**
     * Hides the selection.
     */
    protected synchronized void hideSelection() {
	if (!isShowing())
	    return;
	if (!selectionVisible || !selectionShowing)
	    return;
	if (selectionBegin == null || selectionEnd == null)
	    return;
	selectionShowing = !selectionShowing;
	if (selectionBegin.textIndex == selectionEnd.textIndex) {
	    if (textCaret != null) {
		Graphics g = getGraphics();
		if (g != null) {
		    textCaret.hideCaret(g,
					offset,
					selectionBegin,
					caretColor);
		    g.dispose();
		}
	    }
	}
	else
	    paintSelection(getForeground(), getBackground());
    }

    /**
     * Paints the selection with the specified colors.
     */
    protected void paintSelection(Color foreColor, Color backColor) {
	 paintSelection(selectionBegin, selectionEnd,
			foreColor, backColor, true);
    }

    /**
     * Paints the selection of the specified range with the specified colors.
     */
    protected void paintSelection(TextPositionInfo selBegin,
				  TextPositionInfo selEnd,
				  Color foreColor, Color backColor)
    {
	 paintSelection(selBegin, selEnd, foreColor, backColor, false);
    }

    /**
     * Paints the selection of the specified range with the specified colors
     * and flag indicating that the specified range is a entire selection.
     */
    protected void paintSelection(TextPositionInfo selBegin,
				  TextPositionInfo selEnd,
				  Color foreColor, Color backColor,
				  boolean fullSelection)
    {
	if (selBegin.textIndex == selEnd.textIndex)
	    return;
	TextPositionInfo begin, end;
	begin = getVisibleBegin();
	end = getVisibleEnd();
	if (selBegin.textIndex > end.textIndex ||
	    selEnd.textIndex < begin.textIndex)
	{
	    return;
	}
	if (begin.textIndex < selBegin.textIndex)
	    begin = selBegin;
	if (selEnd.textIndex < end.textIndex)
	    end = selEnd;

	Graphics g = getPreferredGraphics();
	if (g == null)
	    return;

	g.setColor(foreColor);
	if (fullSelection)
	    layout.draw(g, offset, begin, end, backColor,
			selectionBegin.lineIndex < begin.lineIndex,
			end.lineIndex < selectionEnd.lineIndex);
	else
	    layout.draw(g, offset, begin, end, backColor);

	g.dispose();
	syncGraphics(0,
		     begin.y + offset.y,
		     getSize().width,
		     end.y + end.lineSkip - begin.y);
    }

    /**
     * Updates this view after the text edit model has been edited.
     */
    protected synchronized void updateAfterEdited(TextModelEvent event) {
	TextChange change = event.getTextChange();

	if (isShowing()) {
	    if (needsToRedrawSelection()) {
		hideSelection();
	    }
	    else {
		selectionShowing = false;
	    }
	}

	Dimension oldSize = layout.getSize();
	TextLayoutChange layoutChange = layout.updateLayout(change,
							    selectionBegin,
							    selectionEnd);

	if (layoutChange.isNoRepaint()) {
	    // do nothing
	}
	else if (selectionBegin == null || selectionEnd == null) {
	    setSelectionBeginEnd(getTextPositionAt(0));
	    notifyTextPositionListeners();
	}
	else {
	    int begin = selectionBegin.textIndex;
	    int end = selectionEnd.textIndex;
	    if (change.textReplaced) {
		if (change.begin <= begin) {
		    if (begin < change.end) {
			begin = change.end + change.lengthChanged;
		    }
		    else {
			begin += change.lengthChanged;
		    }
		}
		if (change.begin <= end) {
		    if (end < change.end) {
			end = change.end + change.lengthChanged;
		    }
		    else {
			end += change.lengthChanged;
		    }
		}
	    }
	    if (layoutChange.isFullRepaint()) {
		if (begin == end) {
		    setSelectionBeginEnd(getTextPositionAt(begin));
		}
		else {
		    setSelectionBegin(getTextPositionAt(begin));
		    setSelectionEnd(getTextPositionAt(end));
		}
	    }
	    else if (begin == end) {
		if (begin < layoutChange.paintBegin.textIndex) {
		    if (begin == selectionBegin.textIndex) {
			setSelectionBeginEnd(selectionBegin);
		    }
		    else if (begin == selectionEnd.textIndex) {
			setSelectionBeginEnd(selectionEnd);
		    }
		    else {
			setSelectionBeginEnd(getTextPositionNearby(
					layoutChange.paintBegin, begin));
		    }
		}
		else {
		    setSelectionBeginEnd(
			getTextPositionNearby(layoutChange.paintBegin, begin));
		}
	    }
	    else {
		if (begin < layoutChange.paintBegin.textIndex &&
		    begin == selectionBegin.textIndex)
		{
		    setSelectionBegin(selectionBegin);
		}
		else {
		    setSelectionBegin(
			getTextPositionNearby(layoutChange.paintBegin, begin));
		}
		if (end < layoutChange.paintBegin.textIndex &&
		    end == selectionEnd.textIndex)
		{
		    setSelectionEnd(selectionEnd);
		}
		else {
		    setSelectionEnd(
			getTextPositionNearby(layoutChange.paintBegin, end));
		}
	    }
	    notifyTextPositionListeners();
	}

	if (doubleBuffered || isShowing()) {
	    if (layoutChange.isPartialRepaint()) {
		int paintBegin = event.getPaintBegin();
		int paintEnd = event.getPaintEnd();
		if (paintBegin >= 0 &&
		    paintBegin < layoutChange.paintBegin.textIndex)
		{
		    layoutChange.paintBegin =
			layout.getTextPositionNearby(layoutChange.paintBegin,
						     paintBegin);
		}
		if (paintEnd >= 0 && paintEnd <= model.getRichText().length() &&
		    paintEnd > layoutChange.paintEnd.textIndex)
		{
		    layoutChange.paintEnd =
			layout.getTextPositionNearby(layoutChange.paintEnd,
						     paintEnd);
		}
	    }
	    paintAfterEdited(layoutChange);
	    if (isShowing()) {
		showSelection(); // show caret
	    }
	}
	if (layoutChange.isFullRepaint())
	    layoutResized(-1, -1);
	else
	    layoutResized(oldSize.width, oldSize.height);
    }

    /**
     * Paints this view after the text edit model has been edited.
     */
    protected synchronized void paintAfterEdited(TextLayoutChange change) {
	if (change.isNoRepaint())
	    return;

	Graphics g = getPreferredGraphics();
	if (g == null)
	    return;

	if (change.isFullRepaint()) {
	    _visibleBegin = _visibleEnd = null;
	    Dimension d = getSize();
	    Dimension ld = layout.getSize();
	    if (ld.width > d.width && ld.width + offset.x < d.width)
		offset.x = d.width - ld.width;
	    if (ld.height > d.height && ld.height + offset.y < d.height)
		offset.y = d.height - ld.height;
	    paintOn(g);
	    g.dispose();
	    syncGraphics();
	    return;
	}

	TextPositionInfo paintBegin = change.paintBegin;
	TextPositionInfo paintEnd = change.paintEnd;
	TextPositionInfo vBegin = null;
	TextPositionInfo vEnd = null;
	int newCleanTop = paintEnd.y + offset.y;
	int oldCleanTop = newCleanTop - change.heightChanged;
	int drawWidth = (change.widthChanged < 0 ?
			    layout.getSize().width - change.widthChanged :
			    layout.getSize().width);
	Dimension d = getSize();
	if (change.heightChanged < 0) { // view up
	    TextPositionInfo pBegin = paintEnd;
	    if (oldCleanTop < d.height && newCleanTop >=0) {
		// copyArea() may be fail.
		scrolledUp = true;
		g.copyArea(0, oldCleanTop, d.width, d.height - oldCleanTop,
			   0, change.heightChanged);
		pBegin = layout.getLineBeginPositionOver(
				paintEnd,
				-offset.y+newCleanTop+d.height-oldCleanTop-1);
	    }
	    if (newCleanTop < d.height) {
		vEnd = layout.getLineBeginPositionUnder(
						paintEnd,
						-offset.y + d.height - 1);
		layout.draw(g, offset, pBegin, vEnd,
			    getBackground(),
			    true,
			    (vEnd.textIndex >= getRichText().length()),
			    true,
			    drawWidth);
		int endY = offset.y + vEnd.y + vEnd.lineSkip;
		if (endY < d.height) {
		    g.setColor(getBackground());
		    g.fillRect(0, endY, d.width, d.height - endY);
		    g.setColor(getForeground());
		}
	    }
	}
	else if (change.heightChanged > 0) { // view down
	    if (oldCleanTop >= 0 && newCleanTop < d.height) {
		if (!AWTResources.HAS_COPY_AREA_BUG &&
		    paintEnd.textIndex < getRichText().length())
		{
		    // copyArea() may be fail.
		    scrolledDown = true;
		    g.copyArea(0, oldCleanTop, d.width, d.height - newCleanTop,
			       0, change.heightChanged);
		}
		else {
		    vBegin = layout.getLineBeginPositionOver(paintEnd.y);
		    vEnd = layout.getLineBeginPositionUnder(
						vBegin,
						-offset.y + d.height - 1);
		    layout.draw(g, offset,
				(vBegin.textIndex > paintEnd.textIndex ?
				    vBegin : paintEnd),
				vEnd,
				getBackground(),
				true,
				(vEnd.textIndex >= getRichText().length()),
				true,
				drawWidth);
		    vBegin = vEnd = null;
		}
	    }
	    else if (newCleanTop < d.height) {
		vBegin = layout.getLineBeginPositionOver(-offset.y);
		vEnd = layout.getLineBeginPositionUnder(
						vBegin,
						-offset.y + d.height - 1);
		layout.draw(g, offset,
			    (vBegin.textIndex > paintEnd.textIndex ?
				vBegin : paintEnd),
			    vEnd,
			    getBackground(),
			    true,
			    (vEnd.textIndex >= getRichText().length()),
			    true,
			    drawWidth);
		int endY = offset.y + vEnd.y + vEnd.lineSkip;
		if (endY < d.height) {
		    g.setColor(getBackground());
		    g.fillRect(0, endY, d.width, d.height - endY);
		    g.setColor(getForeground());
		}
	    }
	}
	else { // change.heightChanged == 0
	    // do nothing
	}

	_visibleBegin = vBegin;
	_visibleEnd = vEnd;
	vBegin = getVisibleBegin();
	vEnd = getVisibleEnd();

	if (paintEnd.textIndex < vBegin.textIndex ||
	    vEnd.textIndex < paintBegin.textIndex)
	{
	    g.dispose();
	    if (change.heightChanged != 0) {
		syncGraphics();
	    }
	    return;
	}

	TextPositionInfo drawBegin =
		(vBegin.textIndex > paintBegin.textIndex ? vBegin : paintBegin);
	TextPositionInfo drawEnd =
		(vEnd.textIndex < paintEnd.textIndex ? vEnd : paintEnd);

	layout.draw(g, offset, drawBegin, drawEnd,
		    getBackground(),
		    (vBegin.textIndex > paintBegin.textIndex ?
				true : change.paintFromLineBegin),
		    (vEnd.textIndex < paintEnd.textIndex ?
				true : change.paintToLineEnd),
		    true,
		    drawWidth);

	g.dispose();
	if (change.heightChanged == 0) {
	    syncGraphics(0,
			 drawBegin.y + offset.y,
			 d.width,
			 drawEnd.y + drawEnd.lineSkip - drawBegin.y);
	}
	else {
	    syncGraphics();
	}
    }

    /**
     * Tests if the selection is caret, i.e., null selection.
     */
    public boolean selectionIsCaret() {
	return (selectionBegin != null && selectionEnd != null &&
		selectionBegin.textIndex == selectionEnd.textIndex);
    }

    /**
     * Tests if the selection is needed to be redrawn.
     */
    protected boolean needsToRedrawSelection() {
	//return selectionVisible && selectionIsCaret();
	return selectionIsCaret();
    }

    protected void performClickableTextAction(ClickableTextAction action) {
	if (!action.hasActionListener())
	    return;

	if (isDirectNotification()) {
	    action.performClickableAction();
	}
	else {
	    enableEvents(0); // mark newEventsOnly
	    ClickableTextActionEvent ce =
				new ClickableTextActionEvent(this, action);
	    jp.kyasu.awt.EventPoster.postEvent(ce);
	}
    }

    protected void processEvent(AWTEvent e) {
	if (e instanceof ClickableTextActionEvent) {
	    ClickableTextActionEvent ce = (ClickableTextActionEvent)e;
	    ClickableTextAction action = ce.getClickableTextAction();
	    action.performClickableAction();
	}
	super.processEvent(e);
    }


    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * @exception java.lang.Throwable if an error was occurred.
     */
    protected void finalize() throws Throwable {
	if (textCaret != null) {
	    textCaret.stop();
	}
    }

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();

	if (selectionBegin == null) {
	    s.writeInt(-1);
	}
	else {
	    s.writeInt(selectionBegin.textIndex);
	}
	if (selectionEnd == null) {
	    s.writeInt(-1);
	}
	else {
	    s.writeInt(selectionEnd.textIndex);
	}

	if (textPositionListeners != null) {
	    for (Enumeration e = textPositionListeners.elements();
		 e.hasMoreElements();
		 )
	    {
		TextPositionListener l = (TextPositionListener)e.nextElement();
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

	int begin = s.readInt();
	if (begin < 0) {
	    selectionBegin = null;
	}
	else {
	    selectionBegin = getTextPositionAt(begin);
	}
	int end = s.readInt();
	if (end < 0) {
	    selectionEnd = null;
	}
	else {
	    selectionEnd = getTextPositionAt(end);
	}
	stopTextCaret();

	Object listenerOrNull;
	while ((listenerOrNull = s.readObject()) != null) {
	    addTextPositionListener((TextPositionListener)listenerOrNull);
	}
    }


    /*if[JDK1.2]

    protected InputMethodRequests inputMethodRequestsHandler;

    //
    // Overrides this method to become an active input method client.
    //
    public InputMethodRequests getInputMethodRequests() {
	if (inputMethodRequestsHandler == null) {
	    inputMethodRequestsHandler =
		(InputMethodRequests)new InputMethodRequestsHandler();
	}

	return inputMethodRequestsHandler;
    }

    //
    // An implementation of the InputMethodRequests interface.
    //
    class InputMethodRequestsHandler implements InputMethodRequests {
	public AttributedCharacterIterator cancelLatestCommittedText(
						Attribute[] attributes) {
	    return new AttributedString("").getIterator();
	}

	public AttributedCharacterIterator getCommittedText(int beginIndex,
					int endIndex, Attribute[] attributes) {
	    return new AttributedString("").getIterator();
	}

	public int getCommittedTextLength() {
	    return 0;
	}

	public int getInsertPositionOffset() {
	    return 0;
	}

	public TextHitInfo getLocationOffset(int x, int y) {
	    return TextHitInfo.leading(0);
	}

	public Rectangle getTextLocation(TextHitInfo hitInfo) {
	    Rectangle r;

	    TextPositionInfo pos = getSelectionBegin();
	    if (pos != null) {
		r = new Rectangle(pos.x + offset.x, pos.y + offset.y,
				  1, pos.lineHeight);
	    }
	    else {
		r = new Rectangle();
	    }
	    Point p = getLocationOnScreen();
	    r.translate(p.x, p.y);

	    return r;
	}

	public AttributedCharacterIterator getSelectedText(
						Attribute[] attributes) {
	    return new AttributedString("").getIterator();
	}
    }

    /*end[JDK1.2]*/

}


/**
 * The ClickableTextAction event that is originated from a TextEditView.
 */
class ClickableTextActionEvent extends AWTEvent {
    ClickableTextAction action;

    ClickableTextActionEvent(Object source, ClickableTextAction action) {
	super(source, AWTEvent.RESERVED_ID_MAX + 1);
	this.action = action;
    }

    ClickableTextAction getClickableTextAction() {
	return action;
    }
}
