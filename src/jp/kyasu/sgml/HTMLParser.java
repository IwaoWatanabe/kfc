/*
 * HTMLParser.java
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

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A <code>HTMLParser</code> parses the HTML 3.2 document
 * (<code>&lt;!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN"&gt;</code>)
 * and delivers the sgml events to the listeners
 * (<code>SGMLParserListener</code>s).
 * <p>
 * For example:
 * <pre>
 *     Reader reader = new BufferedReader(new InputStreamReader(System.in));
 *     HTMLParser parser = new HTMLParser();
 *     parser.addSGMLParserListener(new SGMLParserListener(){});
 *     try {
 *         parser.parse(reader);
 *     }
 *     catch (IOException e) {}
 * </pre>
 *
 * @see         jp.kyasu.sgml.SGMLParserListener
 *
 * @version 	26 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class HTMLParser extends SGMLParser {
    /**
     * HTML 3.2 dtd.
     */
    static protected DTD HTML_DTD = null;

    /**
     * Returns the HTML 3.2 dtd.
     */
    static public synchronized DTD getHtmlDTD() {
	if (HTML_DTD == null) {
	    HTML_DTD = createHTMLDTD();
	}
	return HTML_DTD;
    }


    /**
     * Constructs a html parser with HTML 3.2 dtd.
     */
    public HTMLParser() {
	this(getHtmlDTD());
    }

    /**
     * Constructs a html parser with the specified dtd.
     *
     * @param dtd the specified dtd.
     */
    public HTMLParser(DTD dtd) {
	super(dtd);
    }

    // ---- parsing ----

    /**
     * Parses the attributes with the specified (parsed) element and appends
     * the parsed results to the specified attributes.
     */
    protected void parseElementAttributes(Element elem, Hashtable attributes)
	throws IOException
    {
	String key;
	while ((key = skipAndReadSGMLToken()) != null) {
	    String value = "";
	    if (readAttributeAssignToken()) {
		if ((value = readAttributeValueToken()) == null) {
		    parseError("Illegal attribute format");
		    skipToTagc();
		    return;
		}
	    }
	    /*
	    if (elem.hasAttributeNamed(key)) {
		attributes.put(key.toUpperCase(), value);
	    }
	    else {
		parseError(key + " is not a legal attribute in " + elem.name);
	    }
	    */
	    attributes.put(key.toUpperCase(), value);
	}
	skipSeparatorsAndComments();
	if (peekTagc()) {
	    readChar();
	}
	else {
	    parseError("Tag Close expected");
	    skipToTagc();
	}
    }

    // ---- parsing action ----

    /** Perfroms the action for the entity reference open. */
    protected void ero() throws IOException {
	String ename = readSGMLToken();
	if (ename == null) {
	    writeEro();
	    return;
	}
	Entity entity = dtd.getEntity(ename);
	if (entity != null) {
	    if (peekRefc()) {
		readChar();
	    }
	    if (entity.getName().equalsIgnoreCase("nbsp")) {
		writeCdataAtEnd(false);
		cdataParsed(createCdataEvent(" ", true));
	    }
	    else {
		pushReader(new StringReader(entity.text));
	    }
	}
	/* for pre defined entities
	else if ((entity = (Entity)PreDefined.get(ename)) != null) {
	}
	*/
	else {
	    parseError("Entity " + ename + " does not exist in DTD");
	    writeEro();
	    cdata.append(ename);
	    if (peekRefc()) {
		writeRefc();
		readChar();
	    }
	}
    }

    /** Perfroms the action for the end tag open. */
    protected void etago() throws IOException {
	String name = readSGMLToken();
	if (name == null) {
	    writeEtago();
	    return;
	}
	Element elem = dtd.getElement(name);
	if (elem != null) {
	    parseEndTag(elem);
	}
	else {
	    skipSeparatorsAndComments();
	    if (peekTagc()) {
		readChar();
	    }
	    else {
		skipToTagc();
	    }
	    elem = new Element(name, null, Element.PCDATA);
	    SGMLEvent event = createEndTagEvent(elem);
	    writeCdataAtEnd(true);
	    endTagParsed(event);
	}
    }

    /** Perfroms the action for the start tag open. */
    protected void stago() throws IOException {
	String name = readSGMLToken();
	if (name == null) {
	    writeStago();
	    return;
	}
	Element elem = dtd.getElement(name);
	if (elem != null) {
	    parseStartTag(elem);
	}
	else {
	    elem = new Element(name, null, Element.PCDATA);
	    Hashtable attributes = new Hashtable();
	    parseElementAttributes(elem, attributes);
	    SGMLEvent event = createStartTagEvent(elem, attributes);
	    writeCdataAtEnd(false);
	    startTagParsed(event);
	    if (peekRe()) {
		readChar();
	    }
	}
    }

    // ---- cdata management ----

    /** Flushs the buffer for the cdata and delivers the cdata parsed events. */
    protected void writeCdataAtEnd(boolean atEndTag) throws IOException {
	String str = cdata.toString();
	if (str.length() > 0) {
	    resetCdata();
	}
	/*
	if (contentModelType == Element.MODEL ||
	    contentModelType == Element.EMPTY)
	{
	    return;
	}
	*/
	if (atEndTag) {
	    str = trimLastRe(str);
	}
	if (str.length() > 0) {
	    cdataParsed(createCdataEvent(str));
	}
    }

    // ---- private ----

    /** Creates an event object for the start tag parsed. */
    protected SGMLEvent createStartTagEvent(Element elem, Hashtable attrs) {
	return new HTMLEvent(this, SGMLEvent.STARTTAG_PARSED, elem, attrs);
    }

    /** Creates an event object for the end tag parsed. */
    protected SGMLEvent createEndTagEvent(Element elem) {
	return new HTMLEvent(this, SGMLEvent.ENDTAG_PARSED, elem);
    }

    /** Creates an event object for the cdata parsed. */
    protected SGMLEvent createCdataEvent(String cdata) {
	return new HTMLEvent(this, SGMLEvent.CDATA_PARSED, cdata);
    }

    /** Creates an event object for the cdata parsed. */
    protected SGMLEvent createCdataEvent(String cdata, boolean nbsp) {
	return new HTMLEvent(this, SGMLEvent.CDATA_PARSED, cdata, nbsp);
    }

    /** Creates an event object for the parsing finished. */
    protected SGMLEvent createFinishedEvent() {
	return new HTMLEvent(this, SGMLEvent.PARSING_FINISHED);
    }

    // ---- DTD ----

    /**
     * Creates the HTML 3.2 dtd.
     */
    static protected DTD createHTMLDTD() {
	DTD dtd = new DTD();
	setHTMLElements(dtd);
	setHTMLEntities(dtd);
	if (!dtd.isValidDTD()) {
	    System.err.println("invalid DTD");
	    System.exit(1);
	}
	return dtd;
    }

    /**
     * Sets the elements to the HTML 3.2 dtd.
     */
    static protected void setHTMLElements(DTD dtd) {
	// Following elements are REMOVED because they are always acceptable:
	//     font, phrase, A, FONT, CENTER

	// Parameter Entities
	String heading[] = { "H1", "H2", "H3", "H4", "H5", "H6" };
	String list[] = { "UL", "OL", "DIR", "MENU" };
	String preformatted[] = { "PRE" };

	// Text Markup
	/* REMOVED
	String font[] = { "TT", "I", "B", "U", "STRIKE", "BIG", "SMALL",
			  "SUB", "SUP"};
	String phrase[] = { "EM", "STRONG", "DFN", "CODE", "SAMP", "KBD",
			    "VAR", "CITE" };
	String special[] = { "A", "IMG", "APPLET", "FONT", "BASEFONT", "BR",
			     "SCRIPT", "MAP" };
	*/
	String font[] = {};
	String phrase[] = {};
	String special[] = { "IMG", "APPLET", "BASEFONT", "BR", "SCRIPT",
			     "MAP" };

	String form[] = { "INPUT", "SELECT", "TEXTAREA" };
	String text[] =
	    append(append(append(append("#PCDATA",font),phrase),special),form);
	{
	String s[] = append(font, phrase);
	for (int i = 0; i < s.length; i++) {
	    dtd.addElement(new Element(s[i], null, text, Element.MODEL_PCDATA));
	}
	}
	/* REMOVED
	dtd.addElement(new Element(
			"FONT",
			new String[]{ "SIZE", "COLOR" },
			text, Element.MODEL_PCDATA));
	*/
	dtd.addElement(new Element(
			"BASEFONT", new String[]{ "SIZE" }, Element.EMPTY));
	dtd.addElement(new Element(
			"BR", new String[]{ "CLEAR" }, Element.EMPTY));

	// HTML content models
	/* REMOVED
	String block[] = { "P", "DL", "DIV", "CENTER", "BLOCKQUOTE", "FORM",
			   "ISINDEX", "HR", "TABLE" };
	*/
	String block[] = { "P", "DL", "DIV", "BLOCKQUOTE", "FORM", "ISINDEX",
			   "HR", "TABLE" };
	block = append(append(block, list), preformatted);
	String flow[] = append(text, block);

	// Document Body
	String body_content[] = { "ADDRESS" };
	body_content =
		append(append(append(heading, text), block), body_content);
	dtd.addElement(new Element(
			"BODY",
			new String[]{ "BGCOLOR", "TEXT", "LINK", "VLINK",
				      "ALINK", "BACKGROUND" },
			body_content, Element.MODEL_PCDATA));
	dtd.addElement(new Element(
		"ADDRESS", null, append(text, "P"), Element.MODEL_PCDATA));
	dtd.addElement(new Element(
			"DIV",
			new String[]{ "ALIGN" },
			body_content, Element.MODEL_PCDATA));
	/* REMOVED
	dtd.addElement(new Element(
			"CENTER", null, body_content, Element.MODEL_PCDATA));
	*/

	// The Anchor Element
	/* REMOVED
	dtd.addElement(new Element(
			"A",
			new String[]{ "NAME", "HREF", "REL", "REV", "TITLE" },
			text, Element.MODEL_PCDATA, null,
			new String[]{ "A" }));
	*/

	// Client-side image maps
	dtd.addElement(new Element(
			"MAP",
			new String[]{ "NAME" },
			new String[]{ "AREA" },
			Element.MODEL));
	dtd.addElement(new Element(
			"AREA",
			new String[]{ "SHAPE", "COORDS", "HREF", "NOHREF",
				      "ALT" },
			Element.EMPTY));

	// The LINK Element
	dtd.addElement(new Element(
			"LINK",
			new String[]{ "HREF", "REL", "REV", "TITLE" },
			Element.EMPTY));

	// Images
	dtd.addElement(new Element(
			"IMG",
			new String[]{ "SRC", "ALT", "ALIGN", "HEIGHT", "WIDTH",
				      "BORDER", "HSPACE", "VSPACE", "USEMAP",
				      "ISMAP" },
			Element.EMPTY));

	// Java APPLET tag
	dtd.addElement(new Element(
			"APPLET",
			new String[]{ "CODEBASE", "CODE", "ALT", "NAME",
				      "WIDTH", "HEIGHT", "ALIGN", "HSPACE",
				      "VSPACE" },
			append(text, "PARAM"), Element.MODEL_PCDATA));
	dtd.addElement(new Element(
			"PARAM",
			new String[]{ "NAME", "VALUE" }, Element.EMPTY));

	// Horizontal Rule
	dtd.addElement(new Element(
			"HR",
			new String[]{ "ALIGN", "NOSHADE", "SIZE", "WIDTH" },
			Element.EMPTY));

	// Paragraphs
	dtd.addElement(new Element(
			"P",
			new String[]{ "ALIGN" }, text, Element.MODEL_PCDATA));

	// Paragraphs
	{
	String a[] = { "ALIGN" };
	for (int i = 0; i < heading.length; i++) {
	    dtd.addElement(new Element(
				heading[i], a, text, Element.MODEL_PCDATA));
	}
	}

	// Preformatted Text
	dtd.addElement(new Element(
			"PRE",
			new String[]{ "WIDTH" },
			text, Element.MODEL_PCDATA, null,
			/* REMOVED
			new String[]{ "IMG", "BIG", "SMALL", "SUB", "SUP",
				      "FONT" }
			*/
			new String[]{ "IMG" }
			));

	// Block-like Quotes
	dtd.addElement(new Element(
			"BLOCKQUOTE", null,body_content,Element.MODEL_PCDATA));

	// Lists
	dtd.addElement(new Element(
			"DL",
			new String[]{ "COMPACT" },
			append(new String[]{ "DT", "DD" }, flow),
			Element.MODEL_PCDATA));
	dtd.addElement(new Element("DT", null, text, Element.MODEL_PCDATA));
	dtd.addElement(new Element("DD", null, flow, Element.MODEL_PCDATA));
	dtd.addElement(new Element(
			"OL",
			new String[]{ "TYPE", "START", "COMPACT" },
			append(new String[]{ "LI" }, flow),
			Element.MODEL_PCDATA));
	dtd.addElement(new Element(
			"UL",
			new String[]{ "TYPE", "COMPACT" },
			append(new String[]{ "LI" }, flow),
			Element.MODEL_PCDATA));
	dtd.addElement(new Element(
	    		"LI",
			new String[]{ "TYPE", "VALUE" },
			flow, Element.MODEL_PCDATA));
	{
	String a[] = { "COMPACT" };
	String c[] = append(new String[]{ "LI" }, flow);
	dtd.addElement(new Element(
			"DIR", a, c, Element.MODEL_PCDATA, null, block));
	dtd.addElement(new Element(
			"MENU", a, c, Element.MODEL_PCDATA, null, block));
	}

	// Forms
	dtd.addElement(new Element(
			"FORM",
			new String[]{ "ACTION", "METHOD", "ENCTYPE" },
			body_content, Element.MODEL_PCDATA, null,
			new String[]{ "FORM" }));
	dtd.addElement(new Element(
			"INPUT",
			new String[]{ "TYPE", "NAME", "VALUE", "CHECKED",
				      "SIZE", "MAXLENGTH", "SRC", "ALIGN" },
			Element.EMPTY));
	dtd.addElement(new Element(
			"SELECT",
			new String[]{ "NAME", "SIZE", "MULTIPLE" },
			new String[]{ "OPTION" },
			Element.MODEL_PCDATA));
	dtd.addElement(new Element(
			"OPTION",
			new String[]{ "SELECTED", "VALUE" },
			Element.PCDATA));
	dtd.addElement(new Element(
			"TEXTAREA",
			new String[]{ "NAME", "ROWS", "COLS" },
			Element.PCDATA));

	// Tables
	dtd.addElement(new Element(
			"TABLE",
			new String[]{ "ALIGN", "WIDTH", "BORDER",
				      "CELLSPACING", "CELLPADDING" },
			new String[]{ "CAPTION", "TR" },
			Element.MODEL_PCDATA));
	dtd.addElement(new Element(
			"CAPTION",
			new String[]{ "ALIGN" },
			text, Element.MODEL_PCDATA));
	dtd.addElement(new Element(
			"TR",
			new String[]{ "ALIGN", "VALIGN" },
			append(new String[]{ "TH", "TD" }, flow),
			Element.MODEL_PCDATA));
	{
	String a[] = { "NOWRAP", "ROWSPAN", "COLSPAN", "ALIGN", "VALIGN",
		       "WIDTH", "HEIGHT" };
	dtd.addElement(new Element(
			"TH", a, body_content, Element.MODEL_PCDATA));
	dtd.addElement(new Element(
			"TD", a, body_content, Element.MODEL_PCDATA));
	}

	// Document Head
	String head_misc[] = { "SCRIPT", "STYLE", "META", "LINK" };
	dtd.addElement(new Element(
	    		"HEAD", null,
			new String[]{ "TITLE", "ISINDEX", "BASE" },
			Element.MODEL_PCDATA, head_misc, null));
	dtd.addElement(new Element(
			"TITLE", null, Element.PCDATA, null, head_misc));
	dtd.addElement(new Element(
			"ISINDEX", new String[]{ "PROMPT" }, Element.EMPTY));
	dtd.addElement(new Element(
			"BASE", new String[]{ "HREF" }, Element.EMPTY));
	dtd.addElement(new Element(
			"META",
			new String[]{ "HTTP-EQUIV", "NAME", "CONTENT" },
			Element.EMPTY));
	dtd.addElement(new Element("STYLE", null, Element.CDATA));
	dtd.addElement(new Element("SCRIPT", null, Element.CDATA));

	// Document Structure
	Element docElement =
	    new Element("HTML",
			new String[]{ "VERSION" },
			append(new String[]{ "HEAD", "BODY" }, body_content),
			Element.MODEL_PCDATA);
	dtd.addElement(docElement);
	dtd.setDocElement(docElement);
    }

    /**
     * Sets the entities to the HTML 3.2 dtd.
     */
    static protected void setHTMLEntities(DTD dtd) {
	String entities[] = {
	    "nbsp",	" ",		// no break (required) space
	    "excl",	"!",		// exclamation mark
	    "quot",	"\"",		// quotation mark
	    "num",	"#",		// number sign
	    "dollar",	"$",		// dollar sign
	    "percent",	"%",		// percent sign
	    "amp",	"&",		// ampersand
	    "apos",	"''",		// apostrophe
	    "lpar",	"(",		// left parenthesis
	    "rpar",	")",		// right parenthesis
	    "ast",	"*",		// asterisk
	    "plus",	"+",		// plus sign
	    "comma",	",",		// comma
	    "hyphen",	"-",		// hyphen
	    "shy",	"-",		// soft hyphen
	    "period",	".",		// full stop, period
	    "sol",	"/",		// solidus
	    "colon",	":",		// colon
	    "semi",	";",		// semicolon
	    "lt",	"<",		// less-than sign
	    "equals",	"=",		// equals sign
	    "gt",	">",		// greater than
	    "quest",	"?",		// question mark
	    "commat",	"@",		// commercial at
	    "lsqb",	"[",		// left square bracket
	    "bsol",	"\\",		// backslash
	    "rsqb",	"]",		// right square bracket
	    "lowbar",	"_",		// sollow lineidus
	    "lsquo",	"`",		// single quotation mark
	    "lcub",	"{",		// left curly bracket
	    "verbar",	"|",		// vertical bar
	    "rcub",	"}",		// right curly bracket

	    "iexcl",	"&#161;",	// inverted exclamation mark
	    "cent",	"&#162;",	// cent sign
	    "pound",	"&#163;",	// pound sign
	    "curren",	"&#164;",	// general currency sign
	    "yen",	"&#165;",	// yen sign
	    "brvbar",	"&#166;",	// broken (vertical) bar
	    "sect",	"&#167;",	// section sign
	    "copy",	"&#169;",	// copyright sign
	    "ordf",	"&#170;",	// ordinal indicator, feminine
	    "laquo",	"&#171;",	// angle quotation mark, left
	    "reg",	"&#174;",	// registered sign
	    "deg",	"&#176;",	// degree sign
	    "plusmn",	"&#177;",	// plus-or-minus sign
	    "sup2",	"&#178;",	// superscript two
	    "sup3",	"&#179;",	// superscript three
	    "micro",	"&#181;",	// micro sign
	    "para",	"&#182;",	// pilcrow (paragraph sign)
	    "middot",	"&#183;",	// middle dot
	    "sup1",	"&#185;",	// superscript one
	    "ordm",	"&#186;",	// ordinal indicator, masculine
	    "raquo",	"&#187;",	// angle quotation mark, right
	    "frac14",	"&#188;",	// fraction one-quarter
	    "frac12",	"&#189;",	// fraction one-half
	    "half",	"&#189;",	// fraction one-half
	    "frac34",	"&#190;",	// fraction three-quarters
	    "iquest",	"&#191;",	// inverted question mark
	    "Agrave",	"&#192;",	// capital A, grave accent
	    "Aacute",	"&#193;",	// capital A, acute accent
	    "Acirc",	"&#194;",	// capital A, circumflex accent
	    "Atilde",	"&#195;",	// capital A, tilde
	    "Auml",	"&#196;",	// capital A, dieresis or umlaut mark
	    "Aring",	"&#197;",	// capital A, ring
	    "AElig",	"&#198;",	// capital AE diphthong (ligature)
	    "Ccedil",	"&#199;",	// capital C, cedilla
	    "Egrave",	"&#200;",	// capital E, grave accent
	    "Eacute",	"&#201;",	// capital E, acute accent
	    "Ecirc",	"&#202;",	// capital E, circumflex accent
	    "Euml",	"&#203;",	// capital E, dieresis or umlaut mark
	    "Igrave",	"&#204;",	// capital I, grave accent
	    "Iacute",	"&#205;",	// capital I, acute accent
	    "Icirc",	"&#206;",	// capital I, circumflex accent
	    "Iuml",	"&#207;",	// capital I, dieresis or umlaut mark
	    "ETH",	"&#208;",	// capital Eth, Icelandic
	    "Ntilde",	"&#209;",	// capital N, tilde
	    "Ograve",	"&#210;",	// capital O, grave accent
	    "Oacute",	"&#211;",	// capital O, acute accent
	    "Ocirc",	"&#212;",	// capital O, circumflex accent
	    "Otilde",	"&#213;",	// capital O, tilde
	    "Ouml",	"&#214;",	// capital O, dieresis or umlaut mark
	    "times",	"&#215;",	// multiply sign
	    "Oslash",	"&#216;",	// capital O, slash
	    "Ugrave",	"&#217;",	// capital U, grave accent
	    "Uacute",	"&#218;",	// capital U, acute accent
	    "Ucirc",	"&#219;",	// capital U, circumflex accent
	    "Uuml",	"&#220;",	// capital U, dieresis or umlaut mark
	    "Yacute",	"&#221;",	// capital Y, acute accent
	    "THORN",	"&#222;",	// capital THORN, Icelandic
	    "szlig",	"&#223;",	// small sharp s, German (sz ligature)
	    "agrave",	"&#224;",	// small a, grave accent
	    "aacute",	"&#225;",	// small a, acute accent
	    "acirc",	"&#226;",	// small a, circumflex accent
	    "atilde",	"&#227;",	// small a, tilde
	    "auml",	"&#228;",	// small a, dieresis or umlaut mark
	    "aring",	"&#229;",	// small a, ring
	    "aelig",	"&#230;",	// small ae diphthong (ligature)
	    "ccedil",	"&#231;",	// small c, cedilla
	    "egrave",	"&#232;",	// small e, grave accent
	    "eacute",	"&#233;",	// small e, acute accent
	    "ecirc",	"&#234;",	// small e, circumflex accent
	    "euml",	"&#235;",	// small e, dieresis or umlaut mark
	    "igrave",	"&#236;",	// small i, grave accent
	    "iacute",	"&#237;",	// small i, acute accent
	    "icirc",	"&#238;",	// small i, circumflex accent
	    "iuml",	"&#239;",	// small i, dieresis or umlaut mark
	    "eth",	"&#240;",	// small eth, Icelandic
	    "ntilde",	"&#241;",	// small n, tilde
	    "ograve",	"&#242;",	// small o, grave accent
	    "oacute",	"&#243;",	// small o, acute accent
	    "ocirc",	"&#244;",	// small o, circumflex accent
	    "otilde",	"&#245;",	// small o, tilde
	    "ouml",	"&#246;",	// small o, dieresis or umlaut mark
	    "divide",	"&#247;",	// divide sign
	    "oslash",	"&#248;",	// small o, slash
	    "ugrave",	"&#249;",	// small u, grave accent
	    "uacute",	"&#250;",	// small u, acute accent
	    "ucirc",	"&#251;",	// small u, circumflex accent
	    "uuml",	"&#252;",	// small u, dieresis or umlaut mark
	    "yacute",	"&#253;",	// small y, acute accent
	    "thorn",	"&#254;",	// small thorn, Icelandic
	    "yuml",	"&#255;",	// small y, dieresis or umlaut mark
	    null,	null
	};
	for (int i = 0; entities[i] != null; i += 2) {
	    dtd.addEntity(new Entity(entities[i], entities[i+1]));
	}
    }

    static protected String[] append(String s1, String s2[]) {
	String ss1[] = { s1 };
	return append(ss1, s2);
    }

    static protected String[] append(String s1[], String s2) {
	String ss2[] = { s2 };
	return append(s1, ss2);
    }

    static protected String[] append(String s1[], String s2[]) {
	String s[] = new String[s1.length + s2.length];
	System.arraycopy(s1, 0, s, 0, s1.length);
	System.arraycopy(s2, 0, s, s1.length, s2.length);
	return s;
    }

    /** Converts the HTML document into the ESIS format. */
    public static void main(String args[]) {
	String encoding = "Default";
	if (args.length > 0) {
	    encoding = args[0];
	}
	Reader reader = null;
	try {
	    reader = new BufferedReader(
				new InputStreamReader(System.in, encoding));
	}
	catch (UnsupportedEncodingException e) {
	    System.err.println("UnsupportedEncodingException: " + encoding);
	    System.exit(1);
	}
	HTMLParser parser = new HTMLParser();
	parser.addSGMLParserListener(new ESISFormatter());
	try {
	    parser.parse(reader);
	}
	catch (IOException e) {
	    System.err.println("IOException: " + e.getMessage());
	    System.exit(1);
	}
    }
}

/**
 * ESIS Formatter.
 */
class ESISFormatter implements SGMLParserListener {
    public void startTagParsed(SGMLEvent e) {
	System.out.println("(" + e.getElement().getName());
	Hashtable attrs = e.getAttributes();
	Enumeration keys = attrs.keys();
	Enumeration values = attrs.elements();
	while (keys.hasMoreElements()) {
	    String key = (String)keys.nextElement();
	    String value = (String)values.nextElement();
	    System.out.println("A" + key + " " + value);
	}
    }

    public void endTagParsed(SGMLEvent e) {
	System.out.println(")" + e.getElement().getName());
    }

    public void cdataParsed(SGMLEvent e) {
	System.out.println(e.getCDATA());
    }

    public void parsingFinished(SGMLEvent e) {
    }
}
