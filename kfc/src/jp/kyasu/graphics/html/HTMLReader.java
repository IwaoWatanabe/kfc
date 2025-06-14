/*
 * HTMLReader.java
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

import jp.kyasu.graphics.*;
import jp.kyasu.sgml.Element;
import jp.kyasu.sgml.HTMLParser;
import jp.kyasu.sgml.HTMLEvent;
import jp.kyasu.sgml.SGMLEvent;
import jp.kyasu.sgml.SGMLParserListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Stack;

/**
 * The <code>HTMLReader</code> class implements the reader that reads the
 * HTML document ('<code>text/html</code>') and renders it into the
 * <code>HTMLText</code> object or <code>HTMLReaderTarget</code> object.
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
 * This class does not support the following tags:<br>
 * <tt>TABLE, FORM, FRAME, APPLET,</tt>.etc.
 *
 * @see 	jp.kyasu.graphics.html.HTMLStyle
 * @see 	jp.kyasu.graphics.html.HTMLText
 * @see 	jp.kyasu.graphics.html.HTMLReaderTarget
 * @see 	jp.kyasu.graphics.html.DefaultHTMLReaderTarget
 *
 * @version 	22 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class HTMLReader implements SGMLParserListener, java.io.Serializable {
    /** The html style. */
    protected HTMLStyle htmlStyle;

    /** The action listener for the link (<code>A</code>). */
    transient protected ActionListener linkActionListener;


    /** The document url. */
    protected URL documentURL;

    /** The base url defined by the <code>BASE</code> tag. */
    protected URL baseURL;

    /** The text color defined by the <code>BODY</code> tag. */
    protected Color textColor;

    /** The link color defined by the <code>BODY</code> tag. */
    protected Color linkColor;

    /** The text style modifier for the <code>A</code> tag. */
    protected BasicTSModifier linkModifier;


    /** The flag indicating that the reader is interrupted. */
    transient protected boolean interrupted;

    /** The context of the html reader. */
    transient protected HTMLReaderContext context;

    /** The stack for the context of the html reader. */
    transient protected Stack contextStack;

    /** The predefined rendering actions. */
    protected Hashtable renderingActions;


    /** The constant for the line separator character. */
    static protected final char LINE_SEPARATOR = Text.LINE_SEPARATOR_CHAR;

    /** The constant for the line break character. */
    static protected final char LINE_BREAK     = Text.LINE_BREAK_CHAR;

    /** The constant for the line separator string. */
    static protected final String LINE_SEPARATOR_STRING =
			new String(new char[]{ Text.LINE_SEPARATOR_CHAR });

    /** The constant for the line break string. */
    static protected final String LINE_BREAK_STRING =
			new String(new char[]{ Text.LINE_BREAK_CHAR });


    /**
     * The rendering action performed by the <code>SGMLEvent</code>.
     */
    protected interface RenderingAction extends java.io.Serializable {
	/** Performs an action for the start tag. */
	void startTagParsed(SGMLEvent e) throws IOException;

	/** Performs an action for the end tag. */
	void endTagParsed(SGMLEvent e) throws IOException;
    }


    /**
     * Constructs a html reader with the specified html style.
     *
     * @param htmlStyle the html style for the rendering.
     */
    public HTMLReader(HTMLStyle htmlStyle) {
	this(htmlStyle, null);
    }

    /**
     * Constructs a html reader with the specified html style and action
     * listener for the link (<code>A</code>).
     *
     * @param htmlStyle          the html style for the rendering.
     * @param linkActionListener the action listener for the link
     *                           (<code>A</code>). If <code>null</code>,
     *                           the created <code>HTMLText</code> becomes
     *                           non clickable.
     */
    public HTMLReader(HTMLStyle htmlStyle, ActionListener linkActionListener) {
	if (htmlStyle == null)
	    throw new NullPointerException();
	this.htmlStyle          = htmlStyle;
	this.linkActionListener = linkActionListener;

	initRenderingActions();
    }


    /**
     * Reads the HTML document ('<code>text/html</code>') from the specified
     * url and renders it into the <code>HTMLText</code> object.
     *
     * @param     url the url.
     * @return    the rendered <code>HTMLText</code> object.
     * @exception java.io.IOException If an I/O error occurs or the content
     *            type of the url is not '<code>text/html</code>'.
     */
    public HTMLText readFrom(URL url) throws IOException {
	HTMLReaderTarget target = new DefaultHTMLReaderTarget();
	read(url, target);
	return target.getHTMLText();
    }

    /**
     * Reads the HTML document ('<code>text/html</code>') from the specified
     * url with the specified encoding, and renders it into the
     * <code>HTMLText</code> object.
     *
     * @param     url          the url.
     * @param     encodingName the encoding name for reading.
     * @return    the rendered <code>HTMLText</code> object.
     * @exception java.io.IOException If an I/O error occurs or the content
     *            type of the url is not '<code>text/html</code>'.
     */
    public HTMLText readFrom(URL url, String encodingName) throws IOException {
	HTMLReaderTarget target = new DefaultHTMLReaderTarget();
	read(url, encodingName, target);
	return target.getHTMLText();
    }

    /**
     * Reads the HTML document ('<code>text/html</code>') from the specified
     * reader with the url of the HTML document, and renders it into the
     * <code>HTMLText</code> object.
     *
     * @param     documentURL the url of the HTML document.
     * @param     reader      the reader containing the HTML document.
     * @return    the rendered <code>HTMLText</code> object.
     * @exception java.io.IOException If an I/O error occurs.
     */
    public HTMLText readFrom(URL documentURL, Reader reader) throws IOException
    {
	HTMLReaderTarget target = new DefaultHTMLReaderTarget();
	read(documentURL, reader, target);
	return target.getHTMLText();
    }

    /**
     * Reads the HTML document ('<code>text/html</code>') from the specified
     * url and renders it into the <code>HTMLReaderTarget</code> object.
     *
     * @param     url    the url.
     * @param     target the <code>HTMLReaderTarget</code> into which to render.
     * @exception java.io.IOException If an I/O error occurs or the content
     *            type of the url is not '<code>text/html</code>'.
     */
    public void read(URL url, HTMLReaderTarget target) throws IOException {
	read(url, "Default", target);
    }

    /**
     * Reads the HTML document ('<code>text/html</code>') from the specified
     * url with the specified encoding, and renders it into the
     * <code>HTMLReaderTarget</code> object.
     *
     * @param     url          the url.
     * @param     encodingName the encoding name for reading.
     * @param     target       the <code>HTMLReaderTarget</code> into which
     *                         to render.
     * @exception java.io.IOException If an I/O error occurs or the content
     *            type of the url is not '<code>text/html</code>'.
     */
    public void read(URL url, String encodingName, HTMLReaderTarget target)
	throws IOException
    {
	URLConnection conn = url.openConnection();
	if (!conn.getContentType().equals("text/html")) {
	    throw new IOException("content type is not 'text/html'");
	}
	Reader reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream(),
						      encodingName),
				(32 * 1024));
	read(conn.getURL(), reader, target);
    }

    /**
     * Reads the HTML document ('<code>text/html</code>') from the specified
     * reader with the url of the HTML document, and renders it into the
     * <code>HTMLReaderTarget</code> object.
     *
     * @param     documentURL the url of the HTML document.
     * @param     reader      the reader containing the HTML document.
     * @param     target      the <code>HTMLReaderTarget</code> into which
     *                        to render.
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void read(URL documentURL, Reader reader, HTMLReaderTarget target)
	throws IOException
    {
	if (reader == null)
	    throw new NullPointerException();
	if (documentURL == null || target == null) {
	    reader.close();
	    throw new NullPointerException();
	}
	this.documentURL = documentURL;
	baseURL          = null;
	textColor        = Color.black;
	linkColor        = Color.blue;
	linkModifier = new BasicTSModifier();
	linkModifier.put(BasicTSModifier.COLOR, linkColor);
	linkModifier.put(BasicTSModifier.UNDERLINE, true);

	interrupted = false;
	context = new HTMLReaderContext(htmlStyle, target);
	contextStack = new Stack();

	try {
	    HTMLParser parser = new HTMLParser();
	    parser.addSGMLParserListener(this);
	    target.open(documentURL, htmlStyle);
	    target.setTitle("");
	    target.setBackgroundColor(Color.white);
	    target.setTextColor(textColor);
	    target.setLinkColor(linkColor);
	    parser.parse(reader);
	}
	finally {
	    target.close();
	    reader.close();
	}
    }

    /**
     * Interrupts the reading. The reading will be interrupted and then the
     * IOException will be raised to the reading thread.
     */
    public synchronized void interruptReading() {
	interrupted = true;
    }

    /**
     * Invoked when a start tag has been parsed.
     * @see jp.kyasu.sgml.SGMLParserListener#startTagParsed(jp.kyasu.sgml.SGMLEvent)
     */
    public void startTagParsed(SGMLEvent e) throws IOException {
	RenderingAction ra =
	    (RenderingAction)renderingActions.get(e.getElement().getName());
	if (ra != null) {
	    ra.startTagParsed(e);
	}
    }

    /**
     * Invoked when a end tag has been parsed.
     * @see jp.kyasu.sgml.SGMLParserListener#endTagParsed(jp.kyasu.sgml.SGMLEvent)
     */
    public void endTagParsed(SGMLEvent e) throws IOException {
	RenderingAction ra =
	    (RenderingAction)renderingActions.get(e.getElement().getName());
	if (ra != null) {
	    ra.endTagParsed(e);
	}
    }

    /**
     * Invoked when a cdata has been parsed.
     * @see jp.kyasu.sgml.SGMLParserListener#cdataParsed(jp.kyasu.sgml.SGMLEvent)
     */
    public void cdataParsed(SGMLEvent e) throws IOException {
	StringBuffer buffer = new StringBuffer(64);
	String cdata = e.getCDATA();
	int len = cdata.length();
	if ((e instanceof HTMLEvent) && ((HTMLEvent)e).isNbsp()) {
	    append(" ");
	    return;
	}
	if (context.inPreFormatted) {
	    for (int i = 0; i < len; i++) {
		char c = cdata.charAt(i);
		switch (c) {
		case '\r':
		    int j = i + 1;
		    if (j < len && cdata.charAt(j) == '\n') {
			i = j;
		    }
		    buffer.append(LINE_BREAK);
		    break;
		case '\n':
		    buffer.append(LINE_BREAK);
		    break;
		default:
		    buffer.append(c);
		    break;
		}
	    }
	}
	else {
	    boolean needsWhitespace = needsWhitespace();
	    for (int i = 0; i < len; ) {
		char c = cdata.charAt(i);
		if (isWhitespace(c)) {
		    i++;
		    while (i < len && isWhitespace(c = cdata.charAt(i))) {
			i++;
		    }
		    if (needsWhitespace) {
			buffer.append(' ');
		    }
		    else {
			needsWhitespace = true;
		    }
		}
		else {
		    buffer.append(c);
		    needsWhitespace = true;
		    i++;
		}
	    }
	}
	append(buffer.toString());
    }

    /**
     * Invoked when a parsing has been finished.
     * @see jp.kyasu.sgml.SGMLParserListener#parsingFinished(jp.kyasu.sgml.SGMLEvent)
     */
    public void parsingFinished(SGMLEvent e) throws IOException {
    }


    /**
     * A rendering action that ignores contents used for SCRIPT and STYLE.
     */
    class IgnoreAction implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    pushContext();
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    popContext();
	}
    }

    /**
     * A generic font rendering action for B, STRONG, I, CITE, DFN, EM, U,
     * TT, CODE, KBD, SAMP, VAR, BIG, and SMALL.
     */
    class FontAction implements RenderingAction {
	public void startTagParsed(SGMLEvent e) {
	    TextStyleModifier modifier =
		htmlStyle.getTextStyleModifier(e.getElement().getName());
	    if (modifier != null) {
		pushTextStyleStack(modifier);
	    }
	}
	public void endTagParsed(SGMLEvent e) {
	    TextStyleModifier modifier =
		htmlStyle.getTextStyleModifier(e.getElement().getName());
	    if (modifier != null) {
		popTextStyleStack();
	    }
	}
    }

    /**
     * A generic paragraph rendering action for P, H1, H2, H3, H4, H5, H6,
     * and ADDRESS.
     */
    class ParagraphAction implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    ParagraphStyle pStyle = htmlStyle.getParagraphStyle(
						e.getElement().getName());
	    if (pStyle != null) {
	    	pushParagraphStyleStack(pStyle,
					(String)e.getAttributes().get("ALIGN"));
	    }
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    ParagraphStyle pStyle = htmlStyle.getParagraphStyle(
						e.getElement().getName());
	    if (pStyle != null) {
	    	popParagraphStyleStack();
	    }
	}
    }

    /**
     * The FONT rendering action.
     */
    class FONT implements RenderingAction {
	public void startTagParsed(SGMLEvent e) {
	    BasicTSModifier modifier = new BasicTSModifier();
	    Color color = parseColor((String)e.getAttributes().get("COLOR"));
	    if (color != null) {
		modifier.put(BasicTSModifier.COLOR, color);
	    }
	    String str = (String)e.getAttributes().get("SIZE");
	    if (str != null && str.length() > 0) {
		char sign = str.charAt(0);
		if (sign == '+' || sign == '-') {
		    str = str.substring(1, str.length());
		}
		int size;
		try { size = Integer.parseInt(str); }
		catch (NumberFormatException ex) { size = 0; }
		if (size != 0) {
		    switch (sign) {
		    case '+':
			modifier.put(BasicTSModifier.SIZE_DIFF,
				htmlStyle.getFontPointDifference(
					HTMLStyle.DEFAULT_HTML_FONT + size));
			break;
		    case '-':
			modifier.put(BasicTSModifier.SIZE_DIFF,
				htmlStyle.getFontPointDifference(
					HTMLStyle.DEFAULT_HTML_FONT - size));
			break;
		    default:
			modifier.put(BasicTSModifier.SIZE_DIFF,
				htmlStyle.getFontPointDifference(size));
			break;
		    }
		}
	    }
	    pushTextStyleStack(modifier);
	}
	public void endTagParsed(SGMLEvent e) {
	    popTextStyleStack();
	}
    }

    /**
     * The BLOCKQUOTE rendering action.
     */
    class BLOCKQUOTE implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    ParagraphStyle pStyle = htmlStyle.getDefaultParagraphStyle();
	    context.bqStack.push(pStyle); // anything ok
	    pushParagraphStyleStack(pStyle,
				    (String)e.getAttributes().get("ALIGN"));
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    if (!context.bqStack.isEmpty())
		context.bqStack.pop();
	    popParagraphStyleStack();
	}
    }

    /**
     * The PRE rendering action.
     */
    class PRE implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    ParagraphStyle pStyle = htmlStyle.getParagraphStyle(
						e.getElement().getName());
	    if (pStyle != null) {
	    	pushParagraphStyleStack(pStyle);
	    }
	    context.inPreFormatted = true;
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    context.inPreFormatted = false;
	    appendLineSep();
	    ParagraphStyle pStyle = htmlStyle.getParagraphStyle(
						e.getElement().getName());
	    if (pStyle != null) {
	    	popParagraphStyleStack();
	    }
	}
    }

    /**
     * The CENTER rendering action.
     */
    class CENTER implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    BasicPSModifier modifier = new BasicPSModifier();
	    modifier.put(BasicPSModifier.ALIGNMENT, ParagraphStyle.CENTER);
	    context.divStack.push(modifier);
	    pushParagraphStyleStack(getCurrentParagraphStyle());
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    if (!context.divStack.isEmpty())
		context.divStack.pop();
	    popParagraphStyleStack();
	}
    }

    /**
     * The DIV rendering action.
     */
    class DIV implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    BasicPSModifier modifier = new BasicPSModifier();
	    String align = (String)e.getAttributes().get("ALIGN");
	    if (align != null) {
		if (align.equalsIgnoreCase("LEFT")) {
		    modifier.put(BasicPSModifier.ALIGNMENT,
				 ParagraphStyle.LEFT);
		}
		else if (align.equalsIgnoreCase("CENTER")) {
		    modifier.put(BasicPSModifier.ALIGNMENT,
				 ParagraphStyle.CENTER);
		}
		else if (align.equalsIgnoreCase("RIGHT")) {
		    modifier.put(BasicPSModifier.ALIGNMENT,
				 ParagraphStyle.RIGHT);
		}
	    }
	    context.divStack.push(modifier);
	    pushParagraphStyleStack(getCurrentParagraphStyle());
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    if (!context.divStack.isEmpty())
		context.divStack.pop();
	    popParagraphStyleStack();
	}
    }

    /**
     * The A rendering action.
     */
    class A implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    String name = (String)e.getAttributes().get("NAME");
	    if (name != null) {
		append(new TextAttachment(new VAnchor(name)));
	    }
	    String href = (String)e.getAttributes().get("HREF");
	    if (href == null) {
		pushTextStyleStack(new BasicTSModifier());
	    }
	    else {
		if (baseURL != null) {
		    try { href = (new URL(baseURL, href)).toExternalForm(); }
		    catch (MalformedURLException ex) {}
		}
		ClickableTextAction action = new ClickableTextAction(href);
		if (linkActionListener != null) {
		    action.addActionListener(linkActionListener);
		}
		BasicTSModifier modifier= (BasicTSModifier)linkModifier.clone();
		modifier.put(BasicTSModifier.CLICKABLE, action);
		pushTextStyleStack(modifier);
	    }
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    popTextStyleStack();
	}
    }

    /**
     * The BASE rendering action.
     */
    class BASE implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    String href = (String)e.getAttributes().get("HREF");
	    if (href != null) {
		try { baseURL = new URL(href); }
		catch (MalformedURLException ex) {}
	    }
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	}
    }

    /**
     * The BR rendering action.
     */
    class BR implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    append(LINE_BREAK_STRING);
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	}
    }

    /**
     * The HR rendering action.
     */
    class HR implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    String s = (String)e.getAttributes().get("ALIGN");
	    if (s == null) s = "CENTER";
	    pushParagraphStyleStack(htmlStyle.getDefaultParagraphStyle(), s);
	    s = (String)e.getAttributes().get("WIDTH");
	    int width = 100;
	    boolean percent = true;
	    if (s != null) {
		int len = s.length();
		if (len > 0 && s.charAt(len - 1) == '%') {
		    s = s.substring(0, len - 1);
		}
		else {
		    percent = false;
		}
		try { width = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
	    }
	    s = (String)e.getAttributes().get("SIZE");
	    int height = htmlStyle.getHRSize();
	    if (s != null) {
		try { height = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
		height = Math.max(height, htmlStyle.getHRSize());
	    }
	    TextAttachment ta;
	    if (percent) {
		width = Math.min(Math.max(width, 1), 100);
		ta = new TextAttachment(new VHRBorder(0, height));
		ta.setRatioToWidth((float)(width / 100.0));
	    }
	    else {
		ta = new TextAttachment(new VHRBorder(width, height));
	    }
	    append(ta);
	    appendLineSep();
	    popParagraphStyleStack();
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	}
    }

    /**
     * The UL rendering action.
     */
    class UL implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    context.listStack.push(new Integer(-1));
	    pushParagraphStyleStack(htmlStyle.getDefaultParagraphStyle());
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    if (!context.listStack.isEmpty())
		context.listStack.pop();
	    popParagraphStyleStack(context.listStack.isEmpty());
	}
    }

    /**
     * The OL rendering action.
     */
    class OL implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    context.listStack.push(new Integer(0));
	    pushParagraphStyleStack(htmlStyle.getDefaultParagraphStyle());
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    if (!context.listStack.isEmpty())
		context.listStack.pop();
	    popParagraphStyleStack(context.listStack.isEmpty());
	}
    }

    /**
     * The DL rendering action.
     */
    class DL implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    pushParagraphStyleStack(htmlStyle.getDefaultParagraphStyle());
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    popParagraphStyleStack(context.listStack.isEmpty());
	}
    }

    /**
     * The LI rendering action.
     */
    class LI implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    if (context.listStack.isEmpty()) {
		dupParagraphStyleStack();
		return;
	    }
	    int index = ((Integer)context.listStack.peek()).intValue();
	    ParagraphStyle pStyle;
	    if (index < 0) { // UL or DD
		pStyle = htmlStyle.getULIParagraphStyle(
					context.listStack.size(), textColor);
		pushParagraphStyleStack(pStyle);
	    }
	    else { // OL
		context.listStack.pop();
		++index;
		context.listStack.push(new Integer(index));
		pStyle = htmlStyle.getOLIParagraphStyle(
				context.listStack.size(), index, textColor);
		pushParagraphStyleStack(pStyle);
	    }
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    popParagraphStyleStack();
	}
    }

    /**
     * The DT rendering action.
     */
    class DT implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    ParagraphStyle pStyle =
			htmlStyle.getDTParagraphStyle(context.listStack.size());
	    pushParagraphStyleStack(pStyle);
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    popParagraphStyleStack();
	}
    }

    /**
     * The DD rendering action.
     */
    class DD implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    context.listStack.push(new Integer(-2));
	    ParagraphStyle pStyle =
			htmlStyle.getDDParagraphStyle(context.listStack.size());
	    pushParagraphStyleStack(pStyle);
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    if (!context.listStack.isEmpty())
		context.listStack.pop();
	    popParagraphStyleStack();
	}
    }

    /**
     * The TD rendering action.
     */
    class TD implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    //Stack newDivStack = (Stack)context.divStack.clone();
	    pushContext(true); // uses current buffer
	    //context.divStack = newDivStack;

	    pushParagraphStyleStack(htmlStyle.getDefaultParagraphStyle());
	    TextStyle tStyle = getCurrentTextStyle();
	    context.textStyleStack.push(tStyle);
	    setTextStyle(htmlStyle.getDefaultTextStyle());
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    popParagraphStyleStack();
	    popTextStyleStack();
	    append(" ");

	    popContext();
	}
    }

    /**
     * The TR rendering action.
     */
    class TR implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    ParagraphStyle pStyle = htmlStyle.getParagraphStyle("TR");
	    pushParagraphStyleStack(pStyle);
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    popParagraphStyleStack();
	}
    }

    /**
     * The TABLE rendering action.
     */
    class TABLE implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    pushParagraphStyleStack(htmlStyle.getDefaultParagraphStyle());
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    appendLineSep();
	    popParagraphStyleStack(true);
	}
    }

    /**
     * The BODY rendering action.
     */
    class BODY implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    Hashtable attrs = e.getAttributes();
	    Color color;
	    color = parseColor((String)attrs.get("BGCOLOR"));
	    if (color != null) {
		context.target.setBackgroundColor(color);
	    }
	    color = parseColor((String)attrs.get("TEXT"));
	    if (color != null) {
		textColor = color;
		context.target.setTextColor(textColor);
	    }
	    color = parseColor((String)attrs.get("LINK"));
	    if (color != null) {
		linkColor = color;
		context.target.setLinkColor(linkColor);
	    }
	    /*
	    color = parseColor((String)attrs.get("VLINK"));
	    color = parseColor((String)attrs.get("ALINK"));
	    */
	    linkModifier.put(BasicTSModifier.COLOR, linkColor);
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	}
    }

    /**
     * The TITLE rendering action.
     */
    class TITLE implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    pushContext();
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    HTMLReaderTarget titleTarget = popContext().target;
	    context.target.setTitle(titleTarget.getString());
	}
    }

    /**
     * The IMG rendering action.
     */
    class IMG implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    // SRC ALT ALIGN(top|middle|bottom|left|right) HEIGHT WIDTH BORDER
	    // HSPACE VSPACE
	    Hashtable attrs = e.getAttributes();
	    String src = (String)attrs.get("SRC");
	    if (src == null) {
		return;
	    }
	    URL imageURL = (baseURL != null ? baseURL : documentURL);
	    try { imageURL = new URL(imageURL, src); }
	    catch (MalformedURLException ex) { return; }
	    String s = (String)attrs.get("WIDTH");
	    int width = 0;
	    if (s != null) {
		try { width = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
	    }
	    s = (String)attrs.get("HEIGHT");
	    int height = 0;
	    if (s != null) {
		try { height = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
	    }
	    Visualizable v;
	    VImage vimage = null;
	    try { vimage = new VImage(imageURL); }
	    catch (SecurityException se) {}
	    if (vimage == null || vimage.getImage() == null) {
		s = (String)attrs.get("ALT");
		if (s == null || s.length() == 0) s = " ";
		v = new VText(new Text(s,
			      new TextStyle(
				htmlStyle.getBaseTextStyle().
				    getExtendedFont().deriveFont(Color.red))));
		if (width > 0 && height > 0) {
		    v = new VBorderedWrapper(
					new VClipWrapper(v, width, height),
					new V3DBorder(false));
		}
		else {
		    v = new VBorderedWrapper(v, new V3DBorder(false));
		}
	    }
	    else {
		int w = vimage.getSize().width;
		int h = vimage.getSize().height;
		if (width  <= 0) width  = w;
		if (height <= 0) height = h;
		if (width != w || height != h) {
		    vimage.setSize(new java.awt.Dimension(width, height));
		}
		v = vimage;
		if (getCurrentTextStyle().getClickableTextAction() != null) {
		    // in the "A"
		    s = (String)attrs.get("BORDER");
		    int b = 2;
		    if (s != null) {
			try { b = Integer.parseInt(s); }
			catch (NumberFormatException ex) {}
		    }
		    if (b > 0) {
			v = new VColoredWrapper(
				new VBorderedWrapper(
					vimage,
					new VPlainBorder(new Insets(b,b,b,b))),
				linkColor);
		    }
		}
	    }
	    TextAttachment ta = new TextAttachment(src, v,
						   TextAttachment.BOTTOM);
	    String align = (String)attrs.get("ALIGN");
	    if (align != null) {
		if (align.equalsIgnoreCase("TOP")) {
		    ta.setAlignment(TextAttachment.TOP);
		}
		else if (align.equalsIgnoreCase("MIDDLE")) {
		    ta.setAlignment(TextAttachment.MIDDLE);
		}
		else if (align.equalsIgnoreCase("BOTTOM")) {
		    ta.setAlignment(TextAttachment.BOTTOM);
		}
	    }
	    append(ta);
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	}
    }

    /**
     * The TEXT rendering action.
     * The TEXT is a extension tag and it is not defined in HTML 3.2.
     */
    class TEXT implements RenderingAction {
	String title;
	int width;
	public void startTagParsed(SGMLEvent e) throws IOException {
	    title = (String)e.getAttributes().get("TITLE");
	    if (title == null) title = "Text";
	    String s = (String)e.getAttributes().get("WIDTH");
	    width = 300;
	    if (s != null) {
		try { width = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
	    }
	    pushContext();
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	    HTMLReaderTarget textTarget = popContext().target;
	    RichText rtext = textTarget.getRichText();
	    VRichText vtext = new VRichText(rtext);
	    vtext.setSize(new Dimension(width, 0));
	    VTitledPaneBorder border = new VTitledPaneBorder(title);
	    append(new TextAttachment(new VBorderedWrapper(vtext, border)));
	}
    }

    /**
     * The OVAL rendering action.
     * The OVAL is a extension tag and it is not defined in HTML 3.2.
     */
    class OVAL implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    String s = (String)e.getAttributes().get("WIDTH");
	    int width = 10;
	    if (s != null) {
		try { width = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
	    }
	    s = (String)e.getAttributes().get("HEIGHT");
	    int height = 10;
	    if (s != null) {
		try { height = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
	    }
	    Color color = parseColor((String)e.getAttributes().get("COLOR"));
	    if (color == null) color = textColor;
	    VOval oval = new VOval(width, height);
	    append(new TextAttachment(new VColoredWrapper(oval, color)));
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	}
    }

    /**
     * The RECT rendering action.
     * The RECT is a extension tag and it is not defined in HTML 3.2.
     */
    class RECT implements RenderingAction {
	public void startTagParsed(SGMLEvent e) throws IOException {
	    String s = (String)e.getAttributes().get("WIDTH");
	    int width = 10;
	    boolean percent = false;
	    if (s != null) {
		int len = s.length();
		if (len > 0 && s.charAt(len - 1) == '%') {
		    percent = true;
		    s = s.substring(0, len - 1);
		}
		try { width = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
	    }
	    s = (String)e.getAttributes().get("HEIGHT");
	    int height = 10;
	    if (s != null) {
		try { height = Integer.parseInt(s); }
		catch (NumberFormatException ex) {}
	    }
	    Color color = parseColor((String)e.getAttributes().get("COLOR"));
	    if (color == null) color = textColor;
	    TextAttachment ta;
	    if (percent) {
		ta = new TextAttachment(new VColoredWrapper(
				new VRectangle(0, height), color));
		ta.setRatioToWidth((float)(width / 100.0));
	    }
	    else {
		ta = new TextAttachment(new VColoredWrapper(
				new VRectangle(width, height), color));
	    }
	    append(ta);
	}
	public void endTagParsed(SGMLEvent e) throws IOException {
	}
    }


    /** Initializes the rendering actions. */
    protected void initRenderingActions() {
	renderingActions = new Hashtable();

	RenderingAction ignoreAction = new IgnoreAction();
	renderingActions.put("SCRIPT", ignoreAction);
	renderingActions.put("STYLE",  ignoreAction);

	RenderingAction fontAction = new FontAction();
	renderingActions.put("B",      fontAction);
	renderingActions.put("STRONG", fontAction);
	renderingActions.put("I",      fontAction);
	renderingActions.put("CITE",   fontAction);
	renderingActions.put("DFN",    fontAction);
	renderingActions.put("EM",     fontAction);
	renderingActions.put("U",      fontAction);
	renderingActions.put("TT",     fontAction);
	renderingActions.put("CODE",   fontAction);
	renderingActions.put("KBD",    fontAction);
	renderingActions.put("SAMP",   fontAction);
	renderingActions.put("VAR",    fontAction);
	renderingActions.put("BIG",    fontAction);
	renderingActions.put("SMALL",  fontAction);

	RenderingAction paraAction = new ParagraphAction();
	renderingActions.put("P",       paraAction);
	renderingActions.put("H1",      paraAction);
	renderingActions.put("H2",      paraAction);
	renderingActions.put("H3",      paraAction);
	renderingActions.put("H4",      paraAction);
	renderingActions.put("H5",      paraAction);
	renderingActions.put("H6",      paraAction);
	renderingActions.put("ADDRESS", paraAction);

	renderingActions.put("FONT",       new FONT());
	renderingActions.put("BLOCKQUOTE", new BLOCKQUOTE());
	renderingActions.put("PRE",        new PRE());
	renderingActions.put("CENTER",     new CENTER());
	renderingActions.put("DIV",        new DIV());
	renderingActions.put("A",          new A());
	renderingActions.put("BASE",       new BASE());
	renderingActions.put("BR",         new BR());
	renderingActions.put("HR",         new HR());

	RenderingAction ul = new UL();
	renderingActions.put("UL",   ul);
	renderingActions.put("MENU", ul);
	renderingActions.put("DIR",  ul);

	renderingActions.put("OL", new OL());
	renderingActions.put("DL", new DL());
	renderingActions.put("LI", new LI());
	renderingActions.put("DT", new DT());
	renderingActions.put("DD", new DD());

	RenderingAction td = new TD();
	renderingActions.put("TH", td);
	renderingActions.put("TD", td);
	renderingActions.put("TR",    new TR());
	renderingActions.put("TABLE", new TABLE());

	renderingActions.put("BODY",  new BODY());
	renderingActions.put("TITLE", new TITLE());
	renderingActions.put("IMG",   new IMG());

	// TEXT, OVAL, and RECT are extension taga and they are not defined
	// in HTML 3.2.
	renderingActions.put("TEXT", new TEXT());
	renderingActions.put("OVAL", new OVAL());
	renderingActions.put("RECT", new RECT());
    }

    /** Checks if the specified character is white space of the HTML. */
    protected final boolean isWhitespace(char c) {
	//return Character.isWhitespace(c);
	return Character.isSpace(c);
    }

    /** Parses the specified string as a color value (#RRGGBB). */
    protected Color parseColor(String str) {
	if (str == null) {
	    return null;
	}

	try {
	    return Color.decode(str);
	}
	catch (NumberFormatException e) {
	    return null;
	}

	/*
	int len = str.length();
	if (len > 0 && str.charAt(0) == '#') {
	    str = str.substring(1, len);
	    --len;
	}
	if (len != 6) {
	    return null;
	}
	int r = (parseHex(str.charAt(0)) << 4) + parseHex(str.charAt(1));
	int g = (parseHex(str.charAt(2)) << 4) + parseHex(str.charAt(3));
	int b = (parseHex(str.charAt(4)) << 4) + parseHex(str.charAt(5));
	return new Color(r, g, b);
	*/
    }

    /** Parses the specified string as a hex value (0-F). */
    protected int parseHex(char c) {
	if ('0' <= c && c <= '9') {
	    return c - '0';
	}
	else if ('a' <= c && c <= 'f') {
	    return c - 'a' + 10;
	}
	else if ('A' <= c && c <= 'F') {
	    return c - 'A' + 10;
	}
	else {
	    return 0;
	}
    }

    /**
     * Pushes the current text style onto the text style stack, and sets the
     * current text style to be the modified text style by the specified text
     * style modifier.
     */
    protected void pushTextStyleStack(TextStyleModifier modifier) {
	TextStyle tStyle = getCurrentTextStyle();
	context.textStyleStack.push(tStyle);
	modifyTextStyle(modifier);
    }

    /**
     * Pushes the current text style onto the text style stack.
     */
    protected void dupTextStyleStack() {
	TextStyle tStyle = getCurrentTextStyle();
	context.textStyleStack.push(tStyle);
	//setTextStyle(tStyle);
    }

    /**
     * Removes the text style at the top of the text style stack, and sets
     * the current text style to be the removed text style.
     */
    protected void popTextStyleStack() {
	if (context.textStyleStack.isEmpty()) {
	    return;
	}
	setTextStyle((TextStyle)context.textStyleStack.pop());
    }

    /**
     * Pushes the current paragraph style onto the paragraph style stack,
     * and sets the current paragraph style to be the specified paragraph
     * style modified with the current context.
     */
    protected void pushParagraphStyleStack(ParagraphStyle pStyle)
	throws IOException
    {
	pushParagraphStyleStack(pStyle, null);
    }

    /**
     * Pushes the current paragraph style onto the paragraph style stack,
     * and sets the current paragraph style to be the specified paragraph
     * style modified with the current context and the specified alignment.
     */
    protected void pushParagraphStyleStack(ParagraphStyle pStyle, String align)
	throws IOException
    {
	int linc = 0;
	int rinc = 0;
	if (!context.listStack.isEmpty()) {
	    linc += htmlStyle.getListIncrementSize(context.listStack.size());
	}
	if (!context.bqStack.isEmpty()) {
	    linc += htmlStyle.getLeftBqIncrementSize(context.bqStack.size());
	    rinc += htmlStyle.getRightBqIncrementSize(context.bqStack.size());
	}
	if (linc > 0 || rinc > 0) {
	    BasicPSModifier modifier = new BasicPSModifier();
	    modifier.put(
		BasicPSModifier.LEFT_INDENT,
		htmlStyle.getDefaultParagraphStyle().getLeftIndent() + linc);
	    modifier.put(
		BasicPSModifier.RIGHT_INDENT,
		htmlStyle.getDefaultParagraphStyle().getRightIndent() + rinc);
	    pStyle = modifier.modify(pStyle);
	}
	if (!context.divStack.isEmpty()) {
	    BasicPSModifier modifier = (BasicPSModifier)context.divStack.peek();
	    pStyle = modifier.modify(pStyle);
	}
	if (align != null) {
	    if (align.equalsIgnoreCase("CENTER")) {
		BasicPSModifier modifier = new BasicPSModifier();
		modifier.put(BasicPSModifier.ALIGNMENT, ParagraphStyle.CENTER);
		pStyle = pStyle.deriveStyle(modifier);
	    }
	    else if (align.equalsIgnoreCase("RIGHT")) {
		BasicPSModifier modifier = new BasicPSModifier();
		modifier.put(BasicPSModifier.ALIGNMENT, ParagraphStyle.RIGHT);
		pStyle = pStyle.deriveStyle(modifier);
	    }
	}
	context.paragraphStyleStack.push(getCurrentParagraphStyle());
	setParagraphStyle(pStyle);
    }

    /**
     * Pushes the current paragraph style onto the paragraph style stack.
     */
    protected void dupParagraphStyleStack() {
	ParagraphStyle pStyle = getCurrentParagraphStyle();
	context.paragraphStyleStack.push(pStyle);
	//setParagraphStyle(pStyle);
    }

    /**
     * Removes the paragraph style at the top of the paragraph style stack,
     * and sets the current paragraph style to be the removed paragraph style
     * modified with the current context.
     */
    protected void popParagraphStyleStack() throws IOException {
	popParagraphStyleStack(false);
    }

    /**
     * Removes the paragraph style at the top of the paragraph style stack,
     * and sets the current paragraph style to be the removed paragraph style
     * modified with the current context and the specified flag for the
     * vertical space.
     */
    protected void popParagraphStyleStack(boolean withVSpace)
	throws IOException
    {
	ParagraphStyle pStyle;
	if (context.paragraphStyleStack.isEmpty()) {
	    pStyle = htmlStyle.getDefaultParagraphStyle();
	}
	else {
	    pStyle = (ParagraphStyle)context.paragraphStyleStack.pop();
	    String name = pStyle.getStyleName();
	    if (name != null &&
		(name.equals("LI-UL") || name.equals("LI-OL") ||
		name.equals("DT") || name.equals("DD")))
	    {
		// Creates new "P" paragraph style.
		BasicPSModifier modifier = new BasicPSModifier();
		modifier.put(BasicPSModifier.LEFT_INDENT,
			     pStyle.getLeftIndent());
		modifier.put(BasicPSModifier.RIGHT_INDENT,
			     pStyle.getRightIndent());
		pStyle =
		    modifier.modify(htmlStyle.getDefaultParagraphStyle());
	    }
	}
	if (withVSpace) {
	    ParagraphStyle vsstyle = htmlStyle.getDefaultParagraphStyle();
	    BasicPSModifier modifier = new BasicPSModifier();
	    modifier.put(BasicPSModifier.PARAGRAPH_SPACE, 0);
	    setParagraphStyle(vsstyle.deriveStyle(modifier));
	    appendLineSep(true);
	}
	setParagraphStyle(pStyle);
    }

    /**
     * Pushes the current context onto the context style stack, and sets the
     * current context to be a new context.
     */
    protected void pushContext() throws IOException {
	pushContext(false);
    }

    /**
     * Pushes the current context onto the context style stack, and sets the
     * current context to be a new context.
     *
     * @param useCurrentTarget if true, uses current target for the new context.
     */
    protected void pushContext(boolean useCurrentTarget) throws IOException {
	contextStack.push(context);
	if (useCurrentTarget) {
	    context = new HTMLReaderContext(htmlStyle, context.target);
	}
	else {
	    HTMLReaderTarget target = new DefaultHTMLReaderTarget();
	    target.open(documentURL, htmlStyle);
	    context = new HTMLReaderContext(htmlStyle, target);
	}
    }

    /**
     * Removes the context at the top of the context style stack, and sets
     * the current context to be the removed context.
     *
     * @return the previous context.
     */
    protected HTMLReaderContext popContext() throws IOException {
	HTMLReaderContext oldCtx = context;
	if (!contextStack.isEmpty()) {
	    context = (HTMLReaderContext)contextStack.pop();
	    oldCtx.target.close();
	}
	return oldCtx;
    }

    // target accessing

    /** Checks if the white space is needed in the current context. */
    protected boolean needsWhitespace() {
	int len = context.target.getLength();
	if (len == 0)
	    return false;
	char ch = context.target.getChar(len - 1);
	if (ch == Text.ATTACHMENT_CHAR)
	    return false;
	else
	    return !isWhitespace(ch);
    }

    /** Appends the string to the text buffer of the current context. */
    protected void append(String str) throws IOException {
	append(new Text(str, context.currentTextStyle));
    }

    /** Appends the text attachment to the text buffer of the current context.*/
    protected void append(TextAttachment ta) throws IOException {
	append(new Text(ta, context.currentTextStyle));
    }

    /** Appends the text to the text buffer of the current context.*/
    protected void append(Text text) throws IOException {
	if (interrupted) {
	    throw new IOException("interrupted");
	}
	context.target.append(text);
    }

    /**
     * Appends the line separator to the text buffer of the current context
     * according to the current context.
     */
    protected void appendLineSep() throws IOException {
	appendLineSep(false);
    }

    /**
     * Appends the line separator to the text buffer of the current context
     * according to the current context.
     *
     * @param force if true, appends the line separator anyway.
     */
    protected void appendLineSep(boolean force) throws IOException {
	if (!force) {
	    int len = context.target.getLength();
	    if (len == 0)
		return;
	    char ch = context.target.getChar(--len);
	    while (ch == Text.ATTACHMENT_CHAR) {
		TextAttachment ta = context.target.getAttachmentAt(len);
		if (ta == null || !(ta.getVisualizable() instanceof VAnchor))
		    break;
		if (len == 0)
		    return;
		ch = context.target.getChar(--len);
	    }
	    if (ch == LINE_SEPARATOR)
		return;
	}
	append(LINE_SEPARATOR_STRING);
    }

    /** Returns the current text style. */
    protected TextStyle getCurrentTextStyle() {
	return context.currentTextStyle;
    }

    /** Sets the current text style. */
    protected void setTextStyle(TextStyle textStyle) {
	context.currentTextStyle = textStyle;
    }

    /** Modifies the current text style. */
    protected void modifyTextStyle(TextStyleModifier modifier) {
	context.currentTextStyle = modifier.modify(context.currentTextStyle);
    }

    /** Returns the current paragraph style. */
    protected ParagraphStyle getCurrentParagraphStyle() {
	return context.currentParagraphStyle;
    }

    /** Sets the current paragraph style. */
    protected void setParagraphStyle(ParagraphStyle paragraphStyle)
	throws IOException
    {
	if (!context.currentParagraphStyle.equals(paragraphStyle)) {
	    context.currentParagraphStyle = paragraphStyle;
	    context.target.setParagraphStyle(paragraphStyle);
	}
	if (interrupted) {
	    throw new IOException("interrupted");
	}
    }

    /** Modifies the current paragraph style. */
    protected void modifyParagraphStyle(ParagraphStyleModifier modifier)
	throws IOException
    {
	setParagraphStyle(modifier.modify(context.currentParagraphStyle));
    }
}


/**
 * The context for the html reader.
 */
class HTMLReaderContext {
    /** The target. */
    HTMLReaderTarget target;

    /** The current text style. */
    TextStyle currentTextStyle;

    /** The current paragraph style. */
    ParagraphStyle currentParagraphStyle;

    /** The stack for the text style. */
    Stack textStyleStack;

    /** The stack for the paragraph style. */
    Stack paragraphStyleStack;

    /** The stack for the lists (UL, OL and DL). */
    Stack listStack;

    /** The stack for the DIV and CENTER. */
    Stack divStack;

    /** The stack for the BLOCKQUOTE. */
    Stack bqStack;

    /** True if in the PRE. */
    boolean inPreFormatted;


    /** Constructs a context with the specified html style and target. */
    HTMLReaderContext(HTMLStyle htmlStyle, HTMLReaderTarget target) {
	if (htmlStyle == null || target == null)
	    throw new NullPointerException();
	this.target = target;
	currentTextStyle = htmlStyle.getDefaultTextStyle();
	currentParagraphStyle = htmlStyle.getDefaultParagraphStyle();

	textStyleStack = new Stack();
	paragraphStyleStack = new Stack();
	listStack = new Stack();
	divStack = new Stack();
	bqStack = new Stack();
	inPreFormatted = false;
    }
}
