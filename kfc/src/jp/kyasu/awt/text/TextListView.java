/*
 * TextListView.java
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
import jp.kyasu.awt.TextListModel;
import jp.kyasu.awt.event.ListModelEvent;
import jp.kyasu.awt.event.ListModelListener;
import jp.kyasu.awt.event.TextListModelEvent;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextLayout;
import jp.kyasu.graphics.TextList;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.text.TextLayoutChange;
import jp.kyasu.graphics.text.TextLineInfo;
import jp.kyasu.graphics.text.TextPositionInfo;
import jp.kyasu.graphics.VDashedBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

/**
 * The <code>TextListView</code> class implements a view of a MVC model for
 * the text list. The model of the MVC model is a <code>TextListModel</code>
 * object and the controller of the MVC model is a
 * <code>TextListController</code> object.
 *
 * @see		jp.kyasu.awt.TextListModel
 * @see		jp.kyasu.awt.text.TextListController
 *
 * @version 	12 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextListView extends TextView implements ListModelListener {
    protected TextListModel model;
    protected TextListController controller;
    protected boolean lineSelectionVisible;
    protected VDashedBorder visibleBorder;
    transient protected TextPositionInfo visiblePosition;


    /**
     * Constructs a text list view with the specified text list model.
     *
     * @param textListModel the text list model.
     */
    public TextListView(TextListModel textListModel) {
	super(RichTextStyle.NO_WRAP);
	if (textListModel == null)
	    throw new NullPointerException();

	model = textListModel;
	model.addListModelListener(this);
	controller = new TextListController(this);
	controller.addToView();

	lineSelectionVisible = true;
	visibleBorder = new VDashedBorder();

	setTextLayout(createTextLayout());
    }


    /**
     * Returns the <code>RichText</code> object being viewed.
     */
    public RichText getRichText() {
	return model.getTextList().getRichText();
    }

    /**
     * Creates a <code>TextLayout</code> object.
     */
    protected TextLayout createTextLayout() {
	return model.getTextList();
    }

    /**
     * Returns the model of this view.
     */
    public TextListModel getModel() {
	return model;
    }

    /**
     * Returns the controller of this view.
     */
    public TextListController getController() {
	return controller;
    }

    /**
     * Sets the controller of this view.
     */
    public void setController(TextListController controller) {
	if (controller == null)
	    throw new NullPointerException();
	if (controller.view != this)
	    throw new IllegalArgumentException("view of controller is not valid");
	this.controller.removeFromView();
	this.controller = controller;
	this.controller.addToView();
    }

    /**
     * Invoked when the list model has been changed.
     * @see jp.kyasu.awt.event.ListModelListener
     */
    public void listModelChanged(ListModelEvent event) {
	switch (event.getID()) {
	case ListModelEvent.LIST_MODEL_SELECTION_CHANGED:
	    if (lineSelectionVisible) {
		if (isShowing()) hideSelection();
		int indices[] = event.getDeselectedIndices();
		int len = indices.length;
		for (int i = 0; i < len; i++) {
		    paintLine(indices[i], false);
		}
		indices = event.getSelectedIndices();
		len = indices.length;
		for (int i = 0; i < len; i++) {
		    paintLine(indices[i], true);
		}
		if (isShowing()) showSelection();
	    }
	    break;
	case ListModelEvent.LIST_MODEL_REPLACED:
	    if (event instanceof TextListModelEvent) {
		TextListModelEvent tevent = (TextListModelEvent)event;
		updateAfterReplaced(tevent);
	    }
	    break;
	}
    }

    /**
     * Sets the <code>TextLayout</code> object of this view.
     */
    protected void setTextLayout(TextLayout layout) {
	if (layout == null)
	    throw new NullPointerException();
	if (!layout.getRichTextStyle().isListSeparator())
	    throw new IllegalArgumentException("improper separator");
	if (!layout.isNoWrap())
	    throw new IllegalArgumentException("improper line wrap");

	visiblePosition = null;

	layout.validate();

	super.setTextLayout(layout);

	if (isValid()) {
	    visiblePosition = this.layout.getTextPositionAtLineBegin(0);
	}
    }

    /**
     * Tests if the line selection is visible.
     */
    public boolean isLineSelectionVisible() {
	return lineSelectionVisible;
    }

    /**
     * Makes the line selection visible.
     */
    public synchronized void setLineSelectionVisible(boolean b) {
	if (lineSelectionVisible == b)
	    return;
	lineSelectionVisible = b;
	if (isShowing()) {
	    repaintNow();
	}
    }

    /**
     * Returns the visible text position.
     */
    protected TextPositionInfo getVisiblePosition() {
	return visiblePosition;
    }

    /**
     * Sets the visible text position.
     */
    protected void setVisiblePosition(TextPositionInfo posInfo) {
	visiblePosition = posInfo;
    }

    /**
     * Returns the visible index.
     */
    protected int getVisibleIndex() {
	return (visiblePosition == null ? -1 : visiblePosition.lineIndex);
    }

    /**
     * Sets the visible index.
     */
    protected void setVisibleIndex(int index) {
	if (index < 0 || index >= model.getItemCount())
	    return;
	if (visiblePosition != null && index == visiblePosition.lineIndex)
	    return;
	visiblePosition = layout.getTextPositionAtLineBegin(index);
    }

    /**
     * Resets the location of the layout text.
     * This method is called from setBounds().
     */
    protected void resetLocationOfText() {
	if (visiblePosition == null) {
	    visiblePosition = getTextPositionAtLineBegin(0);
	    offset.x = offset.y = 0;
	}
	else {
	    visiblePosition =
			getTextPositionAtLineBegin(visiblePosition.lineIndex);
	    offset.x = 0;
	    offset.y = getScrollYTo(visiblePosition);
	}
    }

    /**
     * Returns the preferred size of this view.
     */
    public Dimension getPreferredSize() {
	synchronized (getTreeLock()) {
	    Dimension d = layout.getSize();
	    int width = d.width + getRichText().getRichTextStyle().
					getParagraphStyle().getRightIndent();
	    if (getRichText().isEmpty()) {
		width = Math.max(width, 8); // magic number
	    }
	    int height = Math.min(d.height, getPreferredHeight(10));
	    return new Dimension(width, height);
	}
    }

    /**
     * Notifies this view that it has been added to a container.
     */
    public void addNotify() {
	if (model.getSelectedCount() > 0) {
	    setVisibleIndex(model.getSelectedIndexes()[0]);
	    scrollYTo(getVisiblePosition());
	}
	super.addNotify();
    }

    /**
     * Sets the font of this view.
     */
    public void setFont(Font f) {
	super.setFont(f);
	model.setTextStyle(new TextStyle(f));
    }

    /**
     * Paints this view with the specified range.
     */
    protected void paint(Graphics g,
			 TextPositionInfo begin, TextPositionInfo end)
    {
	selectionShowing = false;
	((TextList)layout).draw(g, offset, begin, end,
			   getForeground(),
			   getBackground(),
			   selectionForeground,
			   selectionBackground,
			   getSize().width,
			   getSelectedForPaint());
	showSelection();
    }

    /**
     * Shows the selection.
     */
    protected synchronized void showSelection() {
	if (!isShowing())
	    return;
	if (!selectionVisible || selectionShowing || visiblePosition == null)
	    return;
	selectionShowing = !selectionShowing;

	toggleVisibleRectangle();
    }

    /**
     * Hides the selection.
     */
    protected synchronized void hideSelection() {
	if (!isShowing())
	    return;
	if (!selectionVisible || !selectionShowing || visiblePosition == null)
	    return;
	selectionShowing = !selectionShowing;

	toggleVisibleRectangle();
    }

    /**
     * Shows or hides the visible rectangle.
     */
    protected void toggleVisibleRectangle() {
	if (visiblePosition == null)
	    return;

	Dimension d = getSize();
	int width = d.width;
	int height = layout.getLineSkipAt(visiblePosition.lineIndex);
	int x = 0;
	int y = offset.y + visiblePosition.y;

	if ((y + height) < 0 || d.height < y)
	    return;

	Graphics g = getGraphics();
	if (g == null)
	    return;

	g.setXORMode(Color.white);
	g.setColor(getForeground());
	visibleBorder.paint(g, x, y, width, height);
	g.dispose();
    }

    /**
     * Paints the line at the specified index with the flag indicating that
     * the line is selected.
     */
    protected void paintLine(int lineIndex, boolean select) {
	if (lineIndex < 0 || lineIndex >= model.getItemCount()) {
	    return;
	}
	TextLineInfo lineInfo = getTextLineAt(lineIndex);
	if (lineInfo == null)
	    return;

	TextPositionInfo begin, end;
	begin = getVisibleBegin();
	end = getVisibleEnd();
	if (lineInfo.lineBegin > end.textIndex ||
	    lineInfo.lineEnd <= begin.textIndex)
	{
	    return;
	}

	Graphics g = getPreferredGraphics();
	if (g == null)
	    return;

	begin = getTextPositionAtLineBegin(lineIndex);
	end = getTextPositionNearby(begin, lineInfo.lineEnd);
	int width = getSize().width;

	((TextList)layout).draw(g, offset, begin, end,
				getForeground(), getBackground(),
				selectionForeground, selectionBackground,
				width,
				((lineSelectionVisible && select) ?
					new int[]{ lineIndex } : null),
				true);
	g.dispose();
	syncGraphics(0,
		     begin.y + offset.y,
		     width,
		     end.y + end.lineSkip - begin.y);
    }

    /**
     * Updates this view after the text list model has been replaced.
     */
    protected synchronized void updateAfterReplaced(TextListModelEvent event) {
	TextLayoutChange layoutChange = event.getTextLayoutChange();
	Dimension oldSize = layout.getSize();
	oldSize = new Dimension(oldSize.width  - layoutChange.widthChanged,
				oldSize.height - layoutChange.heightChanged);

	int visibleIndex = getVisibleIndex();
	if (visibleIndex < 0) {
	    if (isShowing()) {
		hideSelection();
	    }
	    setVisiblePosition(null);
	}
	else if (visibleIndex >= model.getItemCount()) {
	    visibleIndex = Math.max(model.getItemCount() - 1, 0);
	    setVisiblePosition(getTextPositionAtLineBegin(visibleIndex));
	}
	else if (isShowing()) {
	    hideSelection();
	}

	if (doubleBuffered || isShowing()) {
	    paintAfterReplaced(layoutChange);
	    if (isShowing()) {
		showSelection();
	    }
	}
	if (layoutChange.isFullRepaint())
	    layoutResized(-1, -1);
	else
	    layoutResized(oldSize.width, oldSize.height);
    }

    /**
     * Paints this view after the text list model has been replaced.
     */
    protected synchronized void paintAfterReplaced(TextLayoutChange change) {
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
			    layout.getSize().width - change.widthChanged:
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
		((TextList)layout).draw(g, offset, pBegin, vEnd,
					getForeground(),
					getBackground(),
					selectionForeground,
					selectionBackground,
					getSize().width,
					getSelectedForPaint(),
					true);
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
		    paintEnd.textIndex < layout.getRichText().length())
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
		    ((TextList)layout).draw(
					g, offset,
					(vBegin.textIndex > paintEnd.textIndex ?
						vBegin : paintEnd),
					vEnd,
					getForeground(),
					getBackground(),
					selectionForeground,
					selectionBackground,
					getSize().width,
					getSelectedForPaint(),
					true);
		    vBegin = vEnd = null;
		}
	    }
	    else if (newCleanTop < d.height) {
		vBegin = layout.getLineBeginPositionOver(-offset.y);
		vEnd = layout.getLineBeginPositionUnder(
						vBegin,
						-offset.y + d.height - 1);
		((TextList)layout).draw(g, offset,
					(vBegin.textIndex > paintEnd.textIndex ?
						vBegin : paintEnd),
					vEnd,
					getForeground(),
					getBackground(),
					selectionForeground,
					selectionBackground,
					getSize().width,
					getSelectedForPaint(),
					true);
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

	((TextList)layout).draw(g, offset, drawBegin, drawEnd,
				getForeground(),
				getBackground(),
				selectionForeground,
				selectionBackground,
				getSize().width,
				getSelectedForPaint(),
				true);

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
     * Returns the selected indices for the paint operation.
     */
    protected final int[] getSelectedForPaint() {
	return (lineSelectionVisible ? model.getSelectedIndexes() : null);
    }

    /**
     * Tests if the selection is needed to be redrawn.
     */
    protected boolean needsToRedrawSelection() {
	return true;
    }

    /**
     * Returns the line index for the specified text index nearby the specified
     * text position.
     */
    protected int getLineIndexNearby(TextPositionInfo posInfo, int textIndex) {
	return layout.getLineIndexNearby(posInfo, textIndex);
    }

    /**
     * Returns the line index for the specified point nearby the specified
     * text position.
     */
    protected int getLineIndexNearby(TextPositionInfo posInfo, Point point) {
	return layout.getLineIndexNearby(posInfo,
			new Point(point.x - offset.x, point.y - offset.y));
    }

    /**
     * Returns the text position at the beginning of the line.
     */
    protected TextPositionInfo getTextPositionAtLineBegin(int lineIndex) {
	return layout.getTextPositionAtLineBegin(lineIndex);
    }


    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	if (visiblePosition == null) {
	    s.writeInt(-1);
	}
	else {
	    s.writeInt(visiblePosition.lineIndex);
	}
    }

    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	s.defaultReadObject();
	int line = s.readInt();
	if (line < 0) {
	    visiblePosition = null;
	}
	else {
	    visiblePosition = getTextPositionAtLineBegin(line);
	}
    }
}
