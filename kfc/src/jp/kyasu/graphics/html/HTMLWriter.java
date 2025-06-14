/*
 * HTMLWriter.java
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

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

/**
 * The <code>HTMLWriter</code> class implements the writer that saves the
 * <code>HTMLText</code> object as a HTML document ('<code>text/html</code>').
 * <p>
 * For example:
 * <pre>
 *    HTMLText htmlText = ...;
 *    HTMLWriter htmlWriter = new HTMLWriter(htmlText);
 *    try {
 *        htmlWriter.writeTo(new File("index.html"));
 *    }
 *    catch (IOException e) { return; }
 * </pre>
 *
 * @see 	jp.kyasu.graphics.html.HTMLStyle
 * @see 	jp.kyasu.graphics.html.HTMLText
 *
 * @version 	08 May 1998
 * @author 	Kazuki YASUMATSU
 */
public class HTMLWriter implements java.io.Serializable {
    /** The html text to be saved. */
    protected HTMLText htmlText;

    /** The html style. */
    protected HTMLStyle htmlStyle;

    /** The base file. */
    transient protected File baseFile;

    /** The target writer. */
    transient protected Writer writer;

    /** The stack for the list (UL, OL and DL) level. */
    protected Stack listStack;

    /** The BLOCKQUOTE level. */
    protected int bqLevel;

    /** The current paragraph style. */
    protected ParagraphStyle currentParagraphStyle;

    /** True if in the PRE. */
    protected boolean inPreFormatted;


    /**
     * The constant for the GENERATOR attribure for the META tag.
     */
    static public final String GENERATOR =
	"Webpad (Java; KFC) [http://ring.aist.go.jp/openlab/kyasu/]";

    /** The constant for the system line separator. */
    static protected final String SYSTEM_LINE_SEPARATOR =
				System.getProperty("line.separator", "\n");


    /**
     * Constructs a html writer with the specified html text.
     *
     * @param htmlText the html text to be saved.
     */
    public HTMLWriter(HTMLText htmlText) {
	if (htmlText == null)
	    throw new NullPointerException();
	this.htmlText = htmlText;
	htmlStyle = htmlText.getHTMLStyle();
    }


    /**
     * Writes the html document as a HTML document ('<code>text/html</code>')
     * into the specified file.
     *
     * @param     file the file to be saved into.
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void writeTo(File file) throws IOException {
	writeTo(file, "Default");
    }

    /**
     * Writes the html document as a HTML document ('<code>text/html</code>')
     * into the specified file with the specified encoding.
     *
     * @param     file         the file to be saved into.
     * @param     encodingName the encoding name for writing.
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void writeTo(File file, String encodingName) throws IOException {
	Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file),
						encodingName));
	writeTo(file, writer);
    }

    /**
     * Writes the html document as a HTML document ('<code>text/html</code>')
     * into the specified writer with the base file.
     *
     * @param     baseFile the base file. the <code>src</code> attribute of
     *                     the <code>IMG</code> tag are genarated as a
     *                     pathname relative to the baseFile.
     * @param     writer   the writer to be saved into.
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void writeTo(File baseFile, Writer writer) throws IOException {
	if (writer == null)
	    throw new NullPointerException();
	if (baseFile == null) {
	    writer.close();
	    throw new NullPointerException();
	}
	this.baseFile = baseFile;
	this.writer = writer;
	listStack = new Stack();
	bqLevel = 0;
	currentParagraphStyle = null;
	inPreFormatted = false;

	try {
	    writeHeader();
	    writeHTML();
	    writeFooter();
	}
	finally {
	    writer.close();
	}
    }


    /** Writes the HTML header. */
    protected void writeHeader() throws IOException {
	writeln("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">");
	writeln("<HTML>");
	writeln("<HEAD>");
	writeln("    <META NAME=GENERATOR CONTENT=\"" + GENERATOR + "\">");
	writeln("    <TITLE>" + htmlText.getTitle() + "</TITLE>");
	writeln("</HEAD>");
	write("<BODY bgcolor=\"");
	writeColor(htmlText.getBackgroundColor());
	write("\" text=\"");
	writeColor(htmlText.getTextColor());
	write("\" link=\"");
	writeColor(htmlText.getLinkColor());
	writeln("\">");
    }

    /** Writes the footer for the HTML. */
    protected void writeFooter() throws IOException {
	writeln("</BODY>");
	writeln("</HTML>");
    }

    /** Writes the HTML body. */
    protected void writeHTML() throws IOException {
	if (htmlText.isEmpty()) {
	    return;
	}
	Text text = htmlText.getText();
	int begin = 0;
	for (;;) {
	    int next = htmlText.nextParagraphBeginIndexOf(begin);
	    int end = (next < 0 ? text.length() : next);
	    currentParagraphStyle = htmlText.getParagraphStyleAt(begin);
	    writeParagraph(begin, end, text, currentParagraphStyle);
	    if (next < 0)
		break;
	    begin = next;
	}
	checkList(null, 0);
	checkBlockquote(null);
    }

    /**
     * Writes the paragraph in the specified range of the specified text
     * as a HTML with the specified paragraph style.
     *
     * @param begin  the beginning index of the paragraph, inclusive.
     * @param end    the ending index of the paragraph, exclusive.
     * @param text   the text.
     * @param pStyle the paragraph style of the paragraph.
     */
    protected void writeParagraph(int begin, int end, Text text,
				  ParagraphStyle pStyle)
	throws IOException
    {
	checkBlockquote(pStyle);

	String styleName = pStyle.getStyleName();
	boolean div = false;
	if (styleName == null || styleName.equals("TR")) {
	    styleName = "P";
	}

	if ((end - begin) == 2) {
	    TextAttachment ta = text.getAttachmentAt(begin);
	    if (ta != null && (ta.getVisualizable() instanceof VHRBorder)) {
		// HR
		styleName = null;
	    }
	}

	if (styleName == null) {
	    checkList(null, htmlStyle.getListIncrementLevel(pStyle));
	}
	else if (styleName.equals("P")  ||
		 styleName.equals("H1") ||
		 styleName.equals("H2") ||
		 styleName.equals("H3") ||
		 styleName.equals("H4") ||
		 styleName.equals("H5") ||
		 styleName.equals("H6"))
	{
	    checkList(null, htmlStyle.getListIncrementLevel(pStyle));
	    write("<" + styleName);
	    switch (pStyle.getAlignment()) {
	    case ParagraphStyle.RIGHT:  write(" align=right"); break;
	    case ParagraphStyle.CENTER: write(" align=center"); break;
	    default:
	    case ParagraphStyle.LEFT: break;
	    }
	    writeln(">");
	}
	else if (styleName.equals("ADDRESS")    ||
		 styleName.equals("PRE"))
	{
	    checkList(null, htmlStyle.getListIncrementLevel(pStyle));
	    switch (pStyle.getAlignment()) {
	    case ParagraphStyle.RIGHT:
		div = true; write("<DIV align=right>"); break;
	    case ParagraphStyle.CENTER:
		div = true; write("<DIV align=center>"); break;
	    default:
	    case ParagraphStyle.LEFT:
		break;
	    }
	    writeln("<" + styleName + ">");
	    if (styleName.equals("PRE")) {
		inPreFormatted = true;
	    }
	}
	else if (styleName.equals("LI-UL")) {
	    checkList("UL", htmlStyle.getListIncrementLevel(pStyle));
	    styleName = null;
	    writeln("<LI>");
	}
	else if (styleName.equals("LI-OL")) {
	    checkList("OL", htmlStyle.getListIncrementLevel(pStyle));
	    styleName = null;
	    writeln("<LI>");
	}
	else if (styleName.equals("DT")) {
	    checkList("DL", htmlStyle.getListIncrementLevel(pStyle) + 1);
	    styleName = null;
	    writeln("<DT>");
	}
	else if (styleName.equals("DD")) {
	    checkList("DL", htmlStyle.getListIncrementLevel(pStyle));
	    styleName = null;
	    writeln("<DD>");
	}
	else { // Unknown
	    checkList(null, htmlStyle.getListIncrementLevel(pStyle));
	    styleName = "BR";
	}

	TextStyle baseStyle = pStyle.getBaseStyle();
	if (baseStyle == null) {
	    baseStyle = htmlStyle.getDefaultTextStyle();
	}
	writeText(begin, end, text, baseStyle);

	if (styleName != null) {
	    writeln("</" + styleName + ">");
	}
	if (div) {
	    writeln("</DIV>");
	}
	inPreFormatted = false;
    }

    /**
     * Checks the change of the level of the BLOCKQUOTE for the specified
     * paragraph style.
     */
    protected void checkBlockquote(ParagraphStyle pStyle) throws IOException {
	int level = (pStyle==null ? 0 : htmlStyle.getBqIncrementLevel(pStyle));
	if (bqLevel == level)
	    return;
	if (bqLevel < level) {
	    for (; bqLevel < level; bqLevel++) {
		writeln("<BLOCKQUOTE>");
	    }
	}
	else { // bqLevel > level
	    for (; bqLevel > level; --bqLevel) {
		writeln("</BLOCKQUOTE>");
	    }
	}
    }

    /**
     * Checks the change of the level of the lists (UL, OL and DL) for
     * the specified list tag name with the current list level.
     */
    protected void checkList(String tag, int level) throws IOException {
	if (listStack.size() < level) {
	    while (listStack.size() < level - 1) {
		listStack.push("UL");
		writeln("<UL>");
	    }
	    if (tag != null) {
		listStack.push(tag);
		writeln("<" + tag + ">");
	    }
	}
	else if (level < listStack.size()) {
	    while (level < listStack.size()) {
		writeln("</" + (String)listStack.pop() + ">");
	    }
	}
	else { // level == listStack.size()
	    if (!listStack.isEmpty()) {
		if (tag != null && !tag.equals((String)listStack.peek())) {
		    writeln("</" + (String)listStack.pop() + ">");
		    listStack.push(tag);
		    writeln("<" + tag + ">");
		}
	    }
	}
    }

    /**
     * Writes the specified range of the specified text as a HTML with
     * the specified base text style.
     *
     * @param begin     the beginning index of the text, inclusive.
     * @param end       the ending index of the text, exclusive.
     * @param text      the text.
     * @param baseStyle the base text style.
     */
    protected void writeText(int begin, int end, Text text, TextStyle baseStyle)
	throws IOException
    {
	boolean firstData = true;
	int index = begin;
	while (index < end) {
	    TextStyle textStyle = text.getTextStyleAt(index);
	    int runEnd = index + text.getRunLengthAt(index);
	    if (runEnd > end) runEnd = end;
	    Vector tags = writeFontTags(baseStyle, textStyle);
	    writeData(index, runEnd, text, firstData);
	    for (Enumeration e = tags.elements(); e.hasMoreElements(); ) {
		write("</" + (String)e.nextElement() + ">");
	    }
	    index = runEnd;
	    firstData = false;
	}
	writeln();
    }

    /**
     * Writes the HTML font tag with a difference of the specified current
     * text style from the specified base text style.
     *
     * @param  baseStyle the base text style.
     * @param  textStyle the current text style.
     * @return a vector of the font tag names written.
     */
    protected Vector writeFontTags(TextStyle baseStyle, TextStyle textStyle)
	throws IOException
    {
	Vector tags = new Vector();
	boolean isA = (textStyle.getClickableTextAction() != null);
	if (isA) {
	    tags.addElement("A");
	    write("<A href=\"");
	    write(textStyle.getClickableTextAction().getActionCommand());
	    write("\">");
	}

	if (!(textStyle instanceof ModTextStyle)) {
	    ExtendedFont exFont = textStyle.getExtendedFont();
	    if (exFont.getName().equalsIgnoreCase("Monospaced")) {
		tags.addElement("TT");
		write("<TT>");
	    }
	    if (exFont.isBold()) {
		tags.addElement("B");
		write("<B>");
	    }
	    if (exFont.isItalic()) {
		tags.addElement("I");
		write("<I>");
	    }
	    if (!isA && exFont.isUnderline()) {
		tags.addElement("U");
		write("<U>");
	    }
	    Color color = exFont.getColor();
	    if (color != null && isA && color.equals(htmlText.getLinkColor())) {
		color = null;
	    }
	    int htmlFontIndex = 0;
	    if (exFont.getSize() != baseStyle.getFont().getSize()) {
		htmlFontIndex =
		    htmlStyle.getHTMLFontIndex(
			exFont.getSize() - baseStyle.getFont().getSize());
	    }
	    if (color != null || htmlFontIndex != 0) {
		tags.addElement("FONT");
		write("<FONT");
		if (color != null) {
		    write(" color=\"");
		    writeColor(color);
		    write("\"");
		}
		if (htmlFontIndex != 0) {
		    write(" size=" + htmlFontIndex);
		}
		write(">");
	    }
	    return tags;
	}

	FontModifier modifier = ((ModTextStyle)textStyle).getFontModifier();
	if (modifier == null) {
	    return tags;
	}
	Object value;
	if ((value = modifier.get(FontModifier.NAME)) != null &&
	    (value instanceof String))
	{
	    String name = (String)value;
	    if (name.equalsIgnoreCase("Monospaced")) {
		tags.addElement("TT");
		write("<TT>");
	    }
	}
	if ((value = modifier.get(FontModifier.BOLD)) != null &&
	    (value instanceof Boolean))
	{
	    boolean isBold = ((Boolean)value).booleanValue();
	    if (isBold) {
		tags.addElement("B");
		write("<B>");
	    }
	}
	if ((value = modifier.get(FontModifier.ITALIC)) != null &&
	    (value instanceof Boolean))
	{
	    boolean isItalic = ((Boolean)value).booleanValue();
	    if (isItalic) {
		tags.addElement("I");
		write("<I>");
	    }
	}
	if ((value = modifier.get(FontModifier.UNDERLINE)) != null &&
	    (value instanceof Boolean))
	{
	    boolean isUnderline = ((Boolean)value).booleanValue();
	    if (isUnderline && !isA) {
		tags.addElement("U");
		write("<U>");
	    }
	}
	Color color = null;
	int htmlFontIndex = 0;
	if ((value = modifier.get(FontModifier.COLOR)) != null &&
	    (value instanceof Color))
	{
	    color = (Color)value;
	    if (isA && color.equals(htmlText.getLinkColor())) {
		color = null;
	    }
	}
	if ((value = modifier.get(FontModifier.SIZE_DIFF)) != null &&
	    (value instanceof Integer))
	{
	    int size = ((Integer)value).intValue();
	    htmlFontIndex = htmlStyle.getHTMLFontIndex(size);
	}
	if (color != null || htmlFontIndex != 0) {
	    tags.addElement("FONT");
	    write("<FONT");
	    if (color != null) {
		write(" color=\"");
		writeColor(color);
		write("\"");
	    }
	    if (htmlFontIndex != 0) {
		write(" size=" + htmlFontIndex);
	    }
	    write(">");
	}
	return tags;
    }

    /**
     * Writes the specified range of the specified text as a HTML data.
     *
     * @param begin     the beginning index of the text, inclusive.
     * @param end       the ending index of the text, exclusive.
     * @param text      the text.
     * @param firstData true if the data written is a first data in the
     *                  paragraph.
     */
    protected void writeData(int begin, int end, Text text, boolean firstData)
	throws IOException
    {
	char lastChar = (firstData ? ' ' : 'a');
	for (int i = begin; i < end; i++) {
	    char c = text.getChar(i);
	    switch (c) {
	    case '<':
		write("&lt;");
		break;
	    case '>':
		write("&gt;");
		break;
	    case '&':
		write("&amp;");
		break;
	    case ' ':
		if (inPreFormatted || lastChar != ' ') {
		    write(' ');
		}
		else {
		    write("&nbsp;");
		    c = 'a'; // lastChar = 'a';
		}
		break;
	    case Text.LINE_SEPARATOR_CHAR:
		break;
	    case Text.LINE_BREAK_CHAR:
		if (inPreFormatted) {
		    writeln();
		}
		else {
		    writeln("<BR>");
		}
		break;
	    case Text.ATTACHMENT_CHAR:
		writeTextAttachment(text.getAttachmentAt(i));
		break;
	    default:
		write(c);
		break;
	    }
	    lastChar = c;
	}
    }

    /**
     * Writes the specified text attachment as a HTML data.
     *
     * @param ta the text attachment.
     */
    protected void writeTextAttachment(TextAttachment ta) throws IOException {
	if (ta == null) {
	    return;
	}
	Visualizable v = ta.getVisualizable();
	if (v instanceof VAnchor) {
	    writeAnchor(ta, (VAnchor)v);
	    return;
	}
	else if (v instanceof VHRBorder) {
	    writeHR(ta, (VHRBorder)v);
	    return;
	}
	else if (v instanceof VImage) {
	    writeIMG(ta, v);
	    return;
	}
	else if (v instanceof VColoredWrapper) {
	    Color color = ((VColoredWrapper)v).getForeground();
	    v = ((VColoredWrapper)v).getVisualizable();
	    if (v instanceof VBorderedWrapper) {
		v = ((VBorderedWrapper)v).getVisualizable();
		if (v instanceof VImage) {
		    writeIMG(ta, v);
		    return;
		}
	    }
	    else if (v instanceof VOval) {
		writeOVAL(ta, color, (VOval)v);
		return;
	    }
	    else if (v instanceof VRectangle) {
		writeRECT(ta, color, (VRectangle)v);
		return;
	    }
	}
	else if (v instanceof VBorderedWrapper) {
	    VBorder border = ((VBorderedWrapper)v).getBorder();
	    v = ((VBorderedWrapper)v).getVisualizable();
	    if (v instanceof VText) {
		writeIMG(ta, v);
		return;
	    }
	    else if (v instanceof VClipWrapper) {
		writeIMG(ta, v);
		return;
	    }
	    else if (v instanceof VRichText) {
		String title = null;
		if (border instanceof VTitledPaneBorder) {
		    Visualizable vtitle= ((VTitledPaneBorder)border).getTitle();
		    if (vtitle instanceof VText) {
			title = ((VText)vtitle).getText().toString();
		    }
		}
		writeTEXT(ta, title, (VRichText)v);
		return;
	    }
	}
    }

    /**
     * Writes the specified text attachment with the specified
     * <code>VAnchor</code> object as a A tag.
     *
     * @param ta     the text attachment.
     * @param anchor the <code>VAnchor</code> object.
     */
    protected void writeAnchor(TextAttachment ta, VAnchor anchor)
	throws IOException
    {
	if (anchor.getName().indexOf('"') < 0)
	    write("<A name=\"" + anchor.getName() + "\"></A>");
	else
	    write("<A name=\'" + anchor.getName() + "\'></A>");
    }

    /**
     * Writes the specified text attachment with the specified
     * <code>VHRBorder</code> object as a HR tag.
     *
     * @param ta the text attachment.
     * @param v  the <code>VHRBorder</code> object.
     */
    protected void writeHR(TextAttachment ta, VHRBorder v) throws IOException {
	write("<HR");
	switch (currentParagraphStyle.getAlignment()) {
	case ParagraphStyle.RIGHT:  write(" align=right"); break;
	case ParagraphStyle.LEFT:   write(" align=left"); break;
	case ParagraphStyle.CENTER:
	default:
	    break;
	}
	if (ta.isVariableWidth()) {
	    write(" width=\"" + (int)(100.0 * ta.getRatioToWidth()) + "%\"");
	}
	else {
	    write(" width=" + v.getSize().width);
	}
	int size = v.getSize().height;
	if (size > htmlStyle.getHRSize()) {
	    write(" size=" + size);
	}
	write(">");
    }

    /**
     * Writes the specified text attachment with the specified
     * <code>Visualizable</code> object as a IMG tag.
     *
     * @param ta the text attachment.
     * @param v  the <code>Visualizable</code> object that should be a
     *           <code>VImage</code> object, <code>V3DBorder</code> object or
     *           <code>VText</code> object.
     */
    protected void writeIMG(TextAttachment ta, Visualizable v)
	throws IOException
    {
	int width  = 0;
	int height = 0;
	if (v instanceof VText) {
	    if (ta.getName() == null)
		return;
	    String alt = ((VText)v).getText().toString();
	    if (alt.equals(" ")) {
		write("<IMG src=\"" + ta.getName() + "\"");
	    }
	    else {
		write("<IMG src=\"" + ta.getName() + "\" alt=\"" + alt + "\"");
	    }
	}
	else if (v instanceof VClipWrapper) {
	    if (ta.getName() == null)
		return;
	    width  = v.getSize().width;
	    height = v.getSize().height;
	    String alt = " ";
	    Visualizable vtext = ((VClipWrapper)v).getVisualizable();
	    if (vtext instanceof VText) {
		alt = ((VText)vtext).getText().toString();
	    }
	    if (alt.equals(" ")) {
		write("<IMG src=\"" + ta.getName() + "\"");
	    }
	    else {
		write("<IMG src=\"" + ta.getName() + "\" alt=\"" + alt + "\"");
	    }
	}
	else if (v instanceof VImage) {
	    VImage vimage = (VImage)v;
	    String filename;
	    URL url;
	    width  = vimage.getSize().width;
	    height = vimage.getSize().height;
	    if ((filename = vimage.getFilename()) != null) {
		write("<IMG src=\"" +
		      getImageSource(baseFile, new File(filename)) + "\"");
	    }
	    else if ((url = vimage.getURL()) != null) {
		write("<IMG src=\"" + url.toExternalForm() + "\"");
	    }
	}
	else {
	    return;
	}
	switch (ta.getAlignment()) {
	case TextAttachment.TOP:    write(" align=top"); break;
	case TextAttachment.MIDDLE: write(" align=middle"); break;
	default:
	case TextAttachment.BOTTOM: /* write(" align=bottom"); */ break;
	}
	if (width  > 0) write(" width="  + width);
	if (height > 0) write(" height=" + height);
	writeln(">");
    }

    /**
     * Writes the specified text attachment with the specified
     * title and <code>VRichText</code> object as a TEXT tag.
     * <p>
     * the TEXT tag is a extension tag and it is not defined in HTML 3.2.
     *
     * @param ta    the text attachment.
     * @param title the title string.
     * @param v     the <code>VRichText</code> object.
     */
    protected void writeTEXT(TextAttachment ta, String title, VRichText v)
	throws IOException
    {
	Text text = v.getRichText().getText();
	writeln();
	write("<TEXT");
	if (title != null) {
	    write(" title=\"" + title + "\"");
	}
	write(" width=" + v.getSize().width);
	writeln(">");
	writeText(0, text.length(), text, htmlStyle.getDefaultTextStyle());
	writeln("</TEXT>");
    }

    /**
     * Writes the specified text attachment with the specified
     * color and <code>VOval</code> object as a OVAL tag.
     * <p>
     * the OVAL tag is a extension tag and it is not defined in HTML 3.2.
     *
     * @param ta the text attachment.
     * @param c  the color.
     * @param v  the <code>VOval</code> object.
     */
    protected void writeOVAL(TextAttachment ta, Color c, VOval v)
	throws IOException
    {
	write("<OVAL");
	write(" width="  + v.getSize().width);
	write(" height=" + v.getSize().height);
	if (c != null) {
	    write(" color=\"");
	    writeColor(c);
	}
	write("\">");
    }

    /**
     * Writes the specified text attachment with the specified
     * color and <code>VRectangle</code> object as a RECT tag.
     * <p>
     * the RECT tag is a extension tag and it is not defined in HTML 3.2.
     *
     * @param ta the text attachment.
     * @param c  the color.
     * @param v  the <code>VRectangle</code> object.
     */
    protected void writeRECT(TextAttachment ta, Color c, VRectangle v)
	throws IOException
    {
	write("<RECT");
	if (ta.isVariableWidth()) {
	    write(" width=\"" + (int)(100.0 * ta.getRatioToWidth()) + "%\"");
	}
	else {
	    write(" width=" + v.getSize().width);
	}
	write(" height=" + v.getSize().height);
	if (c != null) {
	    write(" color=\"");
	    writeColor(c);
	}
	write("\">");
    }

    /** Writes the specified color as a HTML color format (#RRGGBB). */
    protected void writeColor(Color c) throws IOException {
	write('#');
	writeHex(c.getRed());
	writeHex(c.getGreen());
	writeHex(c.getBlue());
    }

    /** Writes the specified integer as a hex value. */
    protected void writeHex(int i) throws IOException {
	char hex[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		       'A', 'B', 'C', 'D', 'E', 'F' };
	write(hex[(i >> 4) & 0xf]);
	write(hex[i & 0xf]);
    }

    /**
     * Returns the image source path relative to the base path from the path
     * of the image file.
     *
     * @param  basepath  the base path.
     * @param  imagepath the pathname of the image file.
     * @return the image source path relative to the base path.
     */
    protected String getImageSource(File basepath, File imagepath) {
	Vector bpath = new Vector();
	Vector lpath = new Vector();
	String name;
	File f = new File(basepath.getAbsolutePath());
	while ((name = f.getParent()) != null) {
	    f = new File(name);
	    bpath.insertElementAt(f.getName(), 0);
	}
	f = new File(imagepath.getAbsolutePath());
	while ((name = f.getParent()) != null) {
	    f = new File(name);
	    lpath.insertElementAt(f.getName(), 0);
	}
	int limit = Math.min(bpath.size(), lpath.size());
	int i;
	for (i = 0; i < limit; i++) {
	    String bname = (String)bpath.elementAt(i);
	    String lname = (String)lpath.elementAt(i);
	    if (!bname.equals(lname)) {
		break;
	    }
	}
	StringBuffer buffer = new StringBuffer();
	for (int j = i; j < bpath.size(); j++) {
	    buffer.append("../");
	}
	for (int j = i; j < lpath.size(); j++) {
	    buffer.append((String)lpath.elementAt(j)).append('/');
	}
	buffer.append(imagepath.getName());
	return buffer.toString();
    }

    // writer accessing

    /** Writes the specified string into the writer. */
    protected void write(String s) throws IOException {
	writer.write(s);
    }

    /** Writes the specified character into the writer. */
    protected void write(int c) throws IOException {
	writer.write(c);
    }

    /** Writes the line separator into the writer. */
    protected void writeln() throws IOException {
	writer.write(SYSTEM_LINE_SEPARATOR);
    }

    /** Writes the specified string with the line separator into the writer. */
    protected void writeln(String s) throws IOException {
	writer.write(s);
	writer.write(SYSTEM_LINE_SEPARATOR);
    }
}
