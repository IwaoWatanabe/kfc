/*
 * HTMLText.java
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

package jp.kyasu.graphics.html;

import jp.kyasu.graphics.BasicPSModifier;
import jp.kyasu.graphics.ModTextStyle;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.ParagraphStyleModifier;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.TextStyleModifier;
import jp.kyasu.graphics.TextAttachment;
import jp.kyasu.graphics.text.TextChange;

import java.awt.Color;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The <code>HTMLText</code> class implements the editable HTML document.
 * This class is a subclass of the <code>RichText</code> class and it
 * can act as a model for the <code>TextEditView</code> and
 * <code>TextEditController</code>.
 * <p>
 * A <code>HTMLText</code> object is created from a HTML document
 * ('<code>text/html</code>') by a <code>HTMLReader</code> object.
 * <p>
 * For example:
 * <pre>
 *    URL url = null;
 *    try {
 *        url = new URL("http://ring.aist.go.jp/openlab/kyasu/");
 *    }
 *    catch (MalformedURLException e) { return; }
 *    HTMLReader htmlReader = new HTMLReader(new HTMLStyle());
 *    HTMLText htmlText = null;
 *    try {
 *        htmlText = htmlReader.readFrom(url);
 *    }
 *    catch (IOException e) { return; }
 * </pre>
 * <p>
 * A <code>HTMLText</code> is saved as a HTML document
 * ('<code>text/html</code>') by a <code>HTMLWriter</code> object.
 * <p>
 * For example:
 * <pre>
 *    HTMLWriter htmlWriter = new HTMLWriter(htmlText);
 *    try {
 *        htmlWriter.writeTo(new File("index.html"));
 *    }
 *    catch (IOException e) { return; }
 * </pre>
 *
 * @see 	jp.kyasu.graphics.RichText
 * @see 	jp.kyasu.graphics.html.HTMLStyle
 * @see 	jp.kyasu.graphics.html.HTMLReader
 * @see 	jp.kyasu.graphics.html.HTMLWriter
 * @see		jp.kyasu.awt.text.TextEditView
 * @see		jp.kyasu.awt.text.TextEditController
 *
 * @version 	14 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class HTMLText extends RichText {
    /** The html style. */
    protected HTMLStyle htmlStyle;

    /** The url of the html document. */
    protected URL documentURL;

    /** The title of the html document. */
    protected String documentTitle;

    /** The background color of the html document. */
    protected Color backgroundColor;

    /** The text color of the html document. */
    protected Color textColor;

    /** The link color of the html document. */
    protected Color linkColor;


    class HTMLTextConstraint implements TextStyleModifier, java.io.Serializable
    {
	public TextStyle modify(TextStyle tStyle) {
	    if (!(tStyle instanceof ModTextStyle)) {
		tStyle = htmlStyle.getDefaultTextStyle();
	    }
	    return tStyle;
	}
    }


    /**
     * Constrcuts a html document with the specified html style.
     *
     * @param htmlStyle the html style.
     */
    public HTMLText(HTMLStyle htmlStyle) {
	this(new Text(), htmlStyle);
    }

    /**
     * Constrcuts a html document with the specified text and html style.
     *
     * @param text      the text to be laid out.
     * @param htmlStyle the html style.
     */
    public HTMLText(Text text, HTMLStyle htmlStyle) {
	super(text, htmlStyle.getDefaultRichTextStyle());
	init(htmlStyle);
    }

    /**
     * Constrcuts a html document that has the same contents as the
     * specified rich text with the specified html style. The style
     * of the specified rich text should equal to the rich text from
     * the specified html style.
     *
     * @param richText  the rich text.
     * @param htmlStyle the html style.
     */
    public HTMLText(RichText richText, HTMLStyle htmlStyle) {
	super(richText);
	if (htmlStyle == null)
	    throw new NullPointerException();
	if (!richText.getRichTextStyle().equals(
				htmlStyle.getDefaultRichTextStyle()))
	    throw new IllegalArgumentException("the style of the rich text should equal to the rich text style from the html style.");
	init(htmlStyle);
    }


    /** Initializes this html document with the specified html style. */
    protected void init(HTMLStyle htmlStyle) {
	if (htmlStyle == null)
	    throw new NullPointerException();
	this.htmlStyle  = htmlStyle;

	documentURL     = null;
	documentTitle   = "";
	backgroundColor = Color.white; // Color.lightGray;;
	textColor       = Color.black;
	linkColor       = Color.blue;

	setTextStyleConstraint(new HTMLTextConstraint());
    }

    /**
     * Returns the html style of this html document.
     */
    public HTMLStyle getHTMLStyle() {
	return htmlStyle;
    }

    /**
     * Sets the html style of this html document to be the specified style.
     *
     * @return the <code>TextChange</code> object that provides an
     *         information of changes in this html text made by this method.
     */
    public TextChange setHTMLStyle(HTMLStyle htmlStyle) {
	if (htmlStyle == null)
	    throw new NullPointerException();
	TextChange change;
	if (this.htmlStyle != null) {
	    change = modifyParagraphStyle(0, length(),
		new HTMLParagraphModifier(this.htmlStyle,htmlStyle,textColor));
	}
	else {
	    change = new TextChange(TextChange.NO_LAYOUT);
	}
	this.htmlStyle = htmlStyle;
	return change;
    }

    /**
     * Returns the url of this html document.
     */
    public URL getURL() {
	return documentURL;
    }

    /**
     * Sets the url of this html document to be the specified url.
     */
    public void setURL(URL url) {
	if (url == null)
	    throw new NullPointerException();
	documentURL = url;
    }

    /**
     * Returns the title of this html document.
     */
    public String getTitle() {
	return documentTitle;
    }

    /**
     * Sets the title of this html document to be the specified string.
     */
    public void setTitle(String title) {
	if (title == null)
	    throw new NullPointerException();
	documentTitle = title;
    }

    /**
     * Returns the background color of this html document.
     */
    public Color getBackgroundColor() {
	return backgroundColor;
    }

    /**
     * Sets the background color of this html document to be the specified
     * color.
     */
    public void setBackgroundColor(Color color) {
	if (color == null)
	    throw new NullPointerException();
	backgroundColor = color;
    }

    /**
     * Returns the text color of this html document.
     */
    public Color getTextColor() {
	return textColor;
    }

    /**
     * Sets the text color of this html document to be the specified color.
     */
    public void setTextColor(Color color) {
	if (color == null)
	    throw new NullPointerException();
	textColor = color;
    }

    /**
     * Returns the link color of this html document.
     */
    public Color getLinkColor() {
	return linkColor;
    }

    /**
     * Sets the link color of this html document to be the specified color.
     */
    public void setLinkColor(Color color) {
	if (color == null)
	    throw new NullPointerException();
	linkColor = color;
    }

    /**
     * Returns the names of all target anchors (references) in this html
     * document.
     */
    public String[] getAllAnchorNames() {
	Hashtable attachments = text.getAttachments();
	if (attachments == null)
	    return new String[0];
	Vector vector = new Vector();
	Enumeration ke = attachments.keys();
	Enumeration ve = attachments.elements();
	while (ke.hasMoreElements()) {
	    TextAttachment ta = (TextAttachment)ve.nextElement();
	    if (ta.getVisualizable() instanceof VAnchor) {
		vector.addElement(((VAnchor)ta.getVisualizable()).getName());
	    }
	    ke.nextElement();
	}
	String names[] = new String[vector.size()];
	int index = 0;
	for (Enumeration e = vector.elements(); e.hasMoreElements(); ) {
	    names[index++] = (String)e.nextElement();
	}
	return names;
    }

    /**
     * Returns the index of the specified target anchor (reference) in this
     * html document.
     *
     * @param  name the name of the target anchor (reference).
     * @return the index of the target anchor (reference); or <code>-1</code>
     *         if the target anchor (reference) does not exist.
     */
    public int getAnchorIndex(String name) {
	Hashtable attachments = text.getAttachments();
	if (attachments == null)
	    return -1;
	Enumeration ke = attachments.keys();
	Enumeration ve = attachments.elements();
	while (ke.hasMoreElements()) {
	    TextAttachment ta = (TextAttachment)ve.nextElement();
	    if (ta.getVisualizable() instanceof VAnchor) {
		if (name.equals(((VAnchor)ta.getVisualizable()).getName())) {
		    return ((Integer)ke.nextElement()).intValue();
		}
	    }
	    ke.nextElement();
	}
	return -1;
    }
}


class HTMLParagraphModifier implements ParagraphStyleModifier {
    HTMLStyle src;
    HTMLStyle dest;
    Color textColor;

    HTMLParagraphModifier(HTMLStyle src, HTMLStyle dest, Color textColor) {
	this.src       = src;
	this.dest      = dest;
	this.textColor = textColor;
    }

    public ParagraphStyle modify(ParagraphStyle pStyle) {
	BasicPSModifier modifier = new BasicPSModifier();
	modifier.put(BasicPSModifier.ALIGNMENT, pStyle.getAlignment());

	int bqLebel = src.getBqIncrementLevel(pStyle);
	int listLevel = src.getListIncrementLevel(pStyle);
	modifier.put(BasicPSModifier.LEFT_INDENT,
		     dest.getLeftIndentation(bqLebel, listLevel));
	modifier.put(BasicPSModifier.RIGHT_INDENT,
		     dest.getRightIndentation(bqLebel, listLevel));

	String styleName = pStyle.getStyleName();
	if (styleName == null) {
	    pStyle = dest.getDefaultParagraphStyle();
	}
	else if (styleName.equals("LI-UL")) {
	    pStyle = dest.getULIParagraphStyle(listLevel, textColor);
	}
	else if (styleName.equals("LI-OL")) {
	    int index = src.getOLIIndex(pStyle.getHeading());
	    pStyle = dest.getOLIParagraphStyle(listLevel, index, textColor);
	}
	else if (styleName.equals("DT")) {
	    pStyle = dest.getDTParagraphStyle(listLevel);
	}
	else if (styleName.equals("DD")) {
	    pStyle = dest.getDDParagraphStyle(listLevel);
	}
	else {
	    pStyle = dest.getParagraphStyle(styleName);
	    if (pStyle == null) {
		pStyle = dest.getDefaultParagraphStyle();
	    }
	}

	return modifier.modify(pStyle);
    }
}
