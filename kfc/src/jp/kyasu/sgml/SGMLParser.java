/*
 * SGMLParser.java
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

import jp.kyasu.util.Set;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 * A <code>SGMLParser</code> parses the SGML document according to
 * the specified <code>DTD</code> and delivers the sgml events to
 * the listeners (<code>SGMLParserListener</code>s).
 * <p>
 * For example:
 * <pre>
 *     Reader reader = new BufferedReader(new InputStreamReader(System.in));
 *     DTD dtd = new DTD();
 *     // Setups the dtd.
 *     SGMLParser parser = new SGMLParser(dtd);
 *     parser.addSGMLParserListener(new SGMLParserListener(){});
 *     try {
 *         parser.parse(reader);
 *     }
 *     catch (IOException e) {}
 * </pre>
 * Refers to
 * <cite>"ISO 8879 -- Standard Generalized Markup Language (SGML)"</cite>.
 *
 * @see 	jp.kyasu.sgml.DTD
 * @see 	jp.kyasu.sgml.SGMLEvent
 * @see 	jp.kyasu.sgml.SGMLParserListener
 *
 * @version 	14 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class SGMLParser implements java.io.Serializable {
    /** The DTD. */
    protected DTD dtd;

    /** The listeners of the parser. */
    transient protected Vector listeners;


    /** The last char parsed. */
    protected int lastChar;

    /** The reader to be parsed. */
    transient protected Reader reader;

    /** The stack of readers to parse entities. */
    transient protected Stack readerStack;

    /** The buffer for cdata. */
    protected StringBuffer cdata;


    /** The current parsing content model type. */
    protected int contentModelType;

    /** The current parsing element. */
    protected Element element;

    /** The elements that nests the current element. */
    protected Stack elementStack;

    /** The current parsing inclusions. */
    protected Set inclusions;

    /** The current parsing exclusions. */
    protected Set exclusions;


    static protected final int CR  = (int)'\r';
    static protected final int LF  = (int)'\n';
    static protected final int EOF = -1;

    /**
     * Constructs a sgml parser with the specified dtd.
     *
     * @param dtd the specified dtd.
     */
    public SGMLParser(DTD dtd) {
	if (dtd == null)
	    throw new NullPointerException();
	this.dtd = dtd;
	listeners = null;

	lastChar = EOF;
	reader = null;
	readerStack = null;
	cdata = new StringBuffer();

	contentModelType = Element.MODEL_PCDATA;
	element = null;
	elementStack = new Stack();
	inclusions = new Set();
	exclusions = new Set();
    }

    // ---- listener ----

    /**
     * Adds the specified sgml parser listener to receive sgml events
     * from this parser.
     *
     * @param l the sgml parser listener
     */
    public void addSGMLParserListener(SGMLParserListener l) {
	if (l == null)
	    throw new NullPointerException();
	if (listeners == null) {
	    listeners = new Vector();
	}
	listeners.addElement(l);
    }

    /**
     * Removes the specified sgml parser listener so it no longer
     * receives sgml events from this parser.
     *
     * @param l the sgml parser listener
     */
    public void removeSGMLParserListener(SGMLParserListener l) {
	if (listeners == null)
	    return;
	listeners.removeElement(l);
	if (listeners.isEmpty()) {
	    listeners = null;
	}
    }

    /** Delivers the specified start tag parsed event to the listeners. */
    protected void startTagParsed(SGMLEvent event) throws IOException {
	if (listeners == null)
	    return;
	for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
	    SGMLParserListener l = (SGMLParserListener)e.nextElement();
	    l.startTagParsed(event);
	}
    }

    /** Delivers the specified end tag parsed event to the listeners. */
    protected void endTagParsed(SGMLEvent event) throws IOException {
	if (listeners == null)
	    return;
	for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
	    SGMLParserListener l = (SGMLParserListener)e.nextElement();
	    l.endTagParsed(event);
	}
    }

    /** Delivers the specified cdata parsed event to the listeners. */
    protected void cdataParsed(SGMLEvent event) throws IOException {
	if (listeners == null)
	    return;
	for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
	    SGMLParserListener l = (SGMLParserListener)e.nextElement();
	    l.cdataParsed(event);
	}
    }

    /** Delivers the specified parsing finished event to the listeners. */
    protected void parsingFinished(SGMLEvent event) throws IOException {
	if (listeners == null)
	    return;
	for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
	    SGMLParserListener l = (SGMLParserListener)e.nextElement();
	    l.parsingFinished(event);
	}
    }

    // ---- parsing ----

    /**
     * Parses the sgml document contained in the specified reader.
     * The sgml events are delivered to the listeners of this parser.
     *
     * @param     reader the reader that contains the sgml document.
     * @exception IOException If an I/O error occurs.
     */
    public void parse(Reader reader) throws IOException {
	if (reader == null)
	    throw new NullPointerException();
	this.reader = reader;
	readerStack = new Stack();
	startParse();
	endParse();
    }

    /** Starts the parsing. */
    protected void startParse() throws IOException {
	resetCdata();
	contentModelType = Element.MODEL_PCDATA;
	readChar();

	for (;;) {
	    for (;;) {
		readCdata();
		if (atEnd()) {
		    break;
		}
		readAction();
	    }
	    if (readerStack.isEmpty()) {
		break;
	    }
	    popReader();
	}
    }

    /** Ends the parsing and delivers the parsing finished event. */
    protected void endParse() throws IOException {
	while (element != null) {
	    popElement(createEndTagEvent(element));
	}
	parsingFinished(createFinishedEvent());
    }

    /** Parses the start tag with the specified (parsed) element. */
    protected void parseStartTag(Element elem) throws IOException {
	checkNewElementAcceptable(elem);

	Hashtable attributes = new Hashtable();
	parseElementAttributes(elem, attributes);
	SGMLEvent event = createStartTagEvent(elem, attributes);
	pushElement(elem, event);

	if (contentModelType != Element.EMPTY) {
	    if (peekRe()) {
		readChar();
	    }
	}

	switch (contentModelType) {
	case Element.CDATA:
	    parseCdata();
	    return;
	case Element.RCDATA:
	    parseRcdata();
	    return;
	case Element.EMPTY:
	    parseEmpty();
	    return;
	case Element.PCDATA:
	case Element.MODEL:
	case Element.MODEL_PCDATA:
	    return;
	}

	parseError("Illegal content model type");
    }

    /** Parses the end tag with the specified (parsed) element. */
    protected void parseEndTag(Element elem) throws IOException {
	skipSeparatorsAndComments();
	if (peekTagc()) {
	    readChar();
	}
	else {
	    parseError("Tag Close expected");
	    skipToTagc();
	}
	endTagCloseAction(elem);
    }

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
	    if (elem.hasAttributeNamed(key)) {
		attributes.put(key.toUpperCase(), value);
	    }
	    else {
		parseError(key + " is not a legal attribute in " + elem.name);
	    }
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

    /**
     * Perfroms the action for the end tag closing with the specified element.
     */
    protected void endTagCloseAction(Element elem) throws IOException {
	if (element == null) {
	    return;
	}
	if (!element.equals(elem) && !elementStack.contains(elem)) {
	    return;
	}
	while (element != null && !element.equals(elem)) {
	    popElement(createEndTagEvent(element));
	}
	if (!element.equals(elem)) {
	    fatalError("Not happen");
	}
	popElement(createEndTagEvent(elem));
    }

    /** Reports the parsing error that is able to be recovered. */
    protected void parseError(String message) {
	//System.err.println(message);
    }

    /** Reports the fatal error that is not able to be recovered. */
    protected void fatalError(String message) {
	//throw new Exception(message);
    }

    // ---- parsing action ----

    /** Perfroms the action for the character reference open. */
    protected final void cro() throws IOException {
	String num = readSGMLToken();
	if (num == null) {
	    writeCro();
	    return;
	}
	if (peekRefc()) {
	    readChar();
	}
	int n;
	try { n = Integer.parseInt(num); }
	catch (NumberFormatException e) { return; }
	if (n < 256) {
	    try {
		cdata.append(new String(new byte[]{ (byte)n }, "8859_1"));
	    }
	    catch (UnsupportedEncodingException e) {}
	}
    }

    /** Perfroms the action for the entity reference open. */
    //protected final void ero() throws IOException {
    protected void ero() throws IOException {
	String ename = readSGMLToken();
	if (ename == null) {
	    writeEro();
	    return;
	}
	Entity entity = dtd.getEntity(ename);
	if (entity == null) {
	    parseError("Entity " + ename + " does not exist in DTD");
	    writeEro();
	    cdata.append(ename);
	    if (peekRefc()) {
		writeRefc();
		readChar();
	    }
	    return;
	}
	if (peekRefc()) {
	    readChar();
	}
	pushReader(new StringReader(entity.text));
    }

    /** Perfroms the action for the end tag open. */
    //protected final void etago() throws IOException {
    protected void etago() throws IOException {
	String name = readSGMLToken();
	if (name == null) {
	    writeEtago();
	    return;
	}
	Element elem = dtd.getElement(name);
	if (elem == null) {
	    parseError("Element " + name + " does not exist in DTD");
	    /* ignore
	    writeEtago();
	    cdate.append(name);
	    */
	    skipToTagc();
	    return;
	}
	parseEndTag(elem);
    }

    /** Perfroms the action for the markup declaration open. */
    protected final void mdo() throws IOException {
	//parseError("Unexpected Markup Declaration Open");

	//writeMdo();
	if (lastChar == '-') {
	    skipComment();
	}
	skipToTagc();
    }

    /** Perfroms the action for the process instruction open. */
    protected final void pio() throws IOException {
	parseError("Unexpected Process Instruction Open");

	writePio();
	readToTagc();
    }

    /** Perfroms the action for the record end. */
    protected final void re() throws IOException {
	writeRe();
    }

    /** Perfroms the action for the start tag open. */
    //protected final void stago() throws IOException {
    protected void stago() throws IOException {
	String name = readSGMLToken();
	if (name == null) {
	    writeStago();
	    return;
	}
	Element elem = dtd.getElement(name);
	if (elem == null) {
	    parseError("Element " + name + " does not exist in DTD");
	    /* ignore
	    writeStago();
	    cdate.append(name);
	    */
	    skipToTagc();
	    return;
	}
	parseStartTag(elem);
    }

    // ---- parsing model ----

    /** Parses the empty model. */
    protected final void parseEmpty() throws IOException {
	/*
	if (!peekEtago()) {
	    endTagCloseAction(element);
	}
	*/
	endTagCloseAction(element);
    }

    /** Parses the #CDATA content model. */
    protected final void parseCdata() throws IOException {
	for (;;) {
	    for (;;) {
		readCdata();
		if (readActionForCdata()) {
		    break;
		}
		if (atEnd()) {
		    endTagCloseAction(element);
		    return;
		}
	    }
	    String seps = upToSeparators();
	    String name = readSGMLToken();
	    if (name != null && element.name.equals(name.toUpperCase())) {
		break;
	    }
	    writeEtago();
	    cdata.append(seps);
	    if (name != null) {
		cdata.append(name);
	    }
	}
	parseEndTag(element);
    }

    /** Parses the #RCDATA content model. */
    protected final void parseRcdata() throws IOException {
	for (;;) {
	    for (;;) {
		readCdata();
		if (readActionForRcdata()) {
		    break;
		}
		if (atEnd()) {
		    if (readerStack.isEmpty()) {
			endTagCloseAction(element);
			return;
		    }
		    popReader();
		}
	    }
	    String seps = upToSeparators();
	    String name = readSGMLToken();
	    if (name != null && element.name.equals(name.toUpperCase())) {
		break;
	    }
	    writeEtago();
	    cdata.append(seps);
	    if (name != null) {
		cdata.append(name);
	    }
	}
	parseEndTag(element);
    }

    // ---- scanning ----

    /** Tests if the end of the stream is reached. */
    protected final boolean atEnd() {
	return lastChar == EOF;
    }

    /** Tests if the specified character is valid for the head of the token. */
    protected final boolean isFirstTokenish(int c) {
	/*
	return (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
		('0' <= c && c <= '9'));
	*/
	return (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
		('0' <= c && c <= '9') || c == '.' || c == '-');
    }

    /** Tests if the specified character is valid for the token. */
    protected final boolean isTokenish(int c) {
	return (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
		('0' <= c && c <= '9') || c == '.' || c == '-');
    }

    /**
     * Reads the character from the reader and sets the lastChar to be the
     * read character. The parser reads any line-ends (CR, LF, CRLF) as LF.
     */
    protected final int readChar() throws IOException {
	lastChar = reader.read();
	if (lastChar == CR) {
	    reader.mark(1);
	    int ch = reader.read();
	    if (ch != LF && ch != EOF) {
		reader.reset();
	    }
	    lastChar = LF;
	}
	return lastChar;
    }

    /** Peeks the character from the reader. */
    protected final int peekChar() throws IOException {
	reader.mark(1);
	int ch = reader.read();
	if (ch != EOF) {
	    reader.reset();
	}
	if (ch == CR) {
	    ch = LF;
	}
	return ch;
    }

    /** Reads the cdata from the reader. */
    protected final void readCdata() throws IOException {
	while (lastChar != EOF && lastChar != '&' && lastChar != '<' &&
	       lastChar != CR && lastChar != LF)
	{
	    cdata.append((char)lastChar);
	    readChar();
	}
    }

    /** Reads the characters to the occurrence (inclusive) of the tag close. */
    protected final void readToTagc() throws IOException {
	while (lastChar != EOF && lastChar != '>') {
	    cdata.append((char)lastChar);
	    readChar();
	}
	if (lastChar == '>') {
	    writeTagc();
	    readChar();
	}
    }

    /** Reads the sgml token from the reader. */
    protected final String readSGMLToken() throws IOException {
	if (lastChar == EOF)
	    return null;
	if (!isFirstTokenish(lastChar))
	    return null;
	StringBuffer buffer = new StringBuffer();
	buffer.append((char)lastChar);
	readChar();
	while (lastChar != EOF && isTokenish(lastChar)) {
	    buffer.append((char)lastChar);
	    readChar();
	}
	return buffer.toString();
    }

    /** Skips the separators and the comments, and reads the sgml token. */
    protected final String skipAndReadSGMLToken() throws IOException {
	skipSeparatorsAndComments();
	return readSGMLToken();
    }

    /** Reads and performs the action. */
    protected final void readAction() throws IOException {
	switch (lastChar) {
	case EOF:
	    return;
	case '&':
	    if (readChar() == '#') {
		readChar();
		cro();
	    }
	    else {
		ero();
	    }
	    return;
	case '<':
	    if (readChar() == '/') {
		readChar();
		etago();
	    }
	    else if (lastChar == '!') {
		readChar();
		mdo();
	    }
	    else if (lastChar == '?') {
		readChar();
		pio();
	    }
	    else {
		stago();
	    }
	    return;
	case CR:
	case LF:
	    readChar();
	    re();
	    return;
	}
	parseError("Unexpected Character " + lastChar);
    }

    /** Reads and performs the action for the #CDATA content model. */
    protected final boolean readActionForCdata() throws IOException {
	switch (lastChar) {
	case EOF:
	    return false;
	case '&':
	    writeEro();
	    readChar();
	    return false;
	case '<':
	    if (readChar() == '/') {
		readChar();
		return true;
	    }
	    else {
		writeStago();
		return false;
	    }
	case CR:
	case LF:
	    writeRe();
	    readChar();
	    return false;
	}
	parseError("Unexpected Character " + lastChar);
	return false;
    }

    /** Reads and performs the action for the #RCDATA content model. */
    protected final boolean readActionForRcdata() throws IOException {
	switch (lastChar) {
	case EOF:
	    return false;
	case '&':
	    if (readChar() == '#') {
		readChar();
		cro();
	    }
	    else {
		ero();
	    }
	    return false;
	case '<':
	    if (readChar() == '/') {
		readChar();
		return true;
	    }
	    else {
		writeStago();
		return false;
	    }
	case CR:
	case LF:
	    writeRe();
	    readChar();
	    return false;
	}
	parseError("Unexpected Character " + lastChar);
	return false;
    }

    /** Reads the assignment token in the attribute. */
    protected final boolean readAttributeAssignToken() throws IOException {
	skipSeparatorsAndComments();
	if (lastChar == '=') {
	    readChar();
	    return true;
	}
	return false;
    }

    /** Reads the value token in the attribute. */
    protected final String readAttributeValueToken() throws IOException {
	skipSeparatorsAndComments();
	if (lastChar == EOF) {
	    return null;
	}

	StringBuffer buffer = new StringBuffer();
	if (lastChar == '\"' || lastChar == '\'') {
	    int c = lastChar;
	    readChar();
	    //while (lastChar != EOF && lastChar != c && lastChar != '>') {
	    while (lastChar != EOF && lastChar != c) {
		buffer.append((char)lastChar);
		readChar();
	    }
	    if (lastChar == c) {
		readChar();
	    }
	}
	else {
	    while (lastChar != EOF &&
		   !isWhitespace((char)lastChar) &&
		   lastChar != '>')
	    {
		buffer.append((char)lastChar);
		readChar();
	    }
	}
	return buffer.toString();
    }

    /** Tests if the next character is the end tag close. */
    protected final boolean peekEtago() throws IOException {
	return lastChar == '<' && peekChar() == '/';
    }

    /** Tests if the next character is the record end. */
    protected final boolean peekRe() throws IOException {
	return lastChar == CR || lastChar == LF;
    }

    /** Tests if the next character is the reference close. */
    protected final boolean peekRefc() throws IOException {
	return lastChar == ';';
    }

    /** Tests if the next character is the tag close. */
    protected final boolean peekTagc() throws IOException {
	return lastChar == '>';
    }

    /** Tests if the next character is the tag open. */
    protected final boolean peekTago() throws IOException {
	return lastChar == '<';
    }

    /** Skip the sgml comment (&lt;!-- ... --&gt;). */
    protected final void skipComment() throws IOException {
	while (lastChar != EOF) {
	    while (lastChar != '-') {
		if (lastChar == EOF)
		    return;
		readChar();
	    }
	    if (readChar() == '-') {
		if (peekChar() == '>') {
		    readChar();
		    return;
		}
	    }
	}
    }

    /** Skip the separators. */
    protected final void skipSeparators() throws IOException {
	while (lastChar != EOF && isWhitespace((char)lastChar)) {
	    readChar();
	}
    }

    /** Skip the separators and comments. */
    protected final void skipSeparatorsAndComments() throws IOException {
	for (;;) {
	    skipSeparators();
	    if (lastChar != '-')
		return;

	    if (peekChar() != '-') {
		return;
	    }
	    readChar(); // read '-'

	    // skip comment
	    do {
		while (readChar() != '-') {
		    if (lastChar == EOF)
			return;
		}
	    } while (readChar() != '-');
	    readChar();
	}
    }

    /** Skips to the occurrence (inclusive) of the tag close. */
    protected final void skipToTagc() throws IOException {
	while (lastChar != EOF && lastChar != '>') {
	    readChar();
	}
	if (lastChar == '>') {
	    readChar();
	}
    }

    /**
     * Reads the characters to the occurrence (exclusive) of the specified
     * character.
     */
    protected final String upTo(int c) throws IOException {
	StringBuffer buffer = new StringBuffer();
	while (lastChar != EOF && lastChar != c) {
	    buffer.append((char)lastChar);
	    readChar();
	}
	return buffer.toString();
    }

    /**
     * Reads the characters to the occurrence (exclusive) of the non
     * separator character.
     */
    protected final String upToSeparators() throws IOException {
	StringBuffer buffer = new StringBuffer();
	while (lastChar != EOF && isWhitespace((char)lastChar)) {
	    buffer.append((char)lastChar);
	    readChar();
	}
	return buffer.toString();
    }

    // ---- cdata management ----

    /** Resets the buffer for the cdata. */
    protected final void resetCdata() {
	cdata = new StringBuffer();
    }

    /** Trims the last record end of the specified string. */
    protected final String trimLastRe(String str) {
	int len = str.length();
	if (len > 0 && (str.charAt(len-1) == CR || str.charAt(len-1) ==  LF)) {
	    return str.substring(0, len-1);
	}
	else {
	    return str;
	}
    }

    /** Flushs the buffer for the cdata and delivers the cdata parsed events. */
    //protected final void writeCdataAtEnd(boolean atEndTag) throws IOException {
    protected void writeCdataAtEnd(boolean atEndTag) throws IOException {
	String str = cdata.toString();
	if (str.length() > 0) {
	    resetCdata();
	}
	if (contentModelType == Element.MODEL ||
	    contentModelType == Element.EMPTY)
	{
	    return;
	}
	if (atEndTag) {
	    str = trimLastRe(str);
	}
	if (str.length() > 0) {
	    cdataParsed(new SGMLEvent(this, SGMLEvent.CDATA_PARSED, str));
	}
    }

    protected final void writeCro()   { cdata.append("&#"); }
    protected final void writeEro()   { cdata.append('&'); }
    protected final void writeEtago() { cdata.append("</"); }
    protected final void writeMdo()   { cdata.append("<!"); }
    protected final void writePio()   { cdata.append("<?"); }
    protected final void writeRe()    { cdata.append('\n'); }
    protected final void writeRefc()  { cdata.append(';'); }
    protected final void writeStago() { cdata.append('<'); }
    protected final void writeTagc()  { cdata.append('>'); }

    // ---- private ----

    /** Creates an event object for the start tag parsed. */
    protected SGMLEvent createStartTagEvent(Element elem, Hashtable attrs) {
	return new SGMLEvent(this, SGMLEvent.STARTTAG_PARSED, elem, attrs);
    }

    /** Creates an event object for the end tag parsed. */
    protected SGMLEvent createEndTagEvent(Element elem) {
	return new SGMLEvent(this, SGMLEvent.ENDTAG_PARSED, elem);
    }

    /** Creates an event object for the cdata parsed. */
    protected SGMLEvent createCdataEvent(String cdata) {
	return new SGMLEvent(this, SGMLEvent.CDATA_PARSED, cdata);
    }

    /** Creates an event object for the parsing finished. */
    protected SGMLEvent createFinishedEvent() {
	return new SGMLEvent(this, SGMLEvent.PARSING_FINISHED);
    }

    /** Tests if the specified character is ISO-LATIN-1 white space. */
    protected final boolean isWhitespace(char c) {
	//return Character.isWhitespace(c);
	return Character.isSpace(c);
    }

    /** Tests if the current element accepts the specified element. */
    protected final boolean acceptElement(Element elem) {
	return (!exclusions.contains(elem.name) &&
			((element != null && element.canAccept(elem)) ||
			inclusions.contains(elem.name)));
    }

    /**
     * Tests if the current elements on the stack can accept
     * the specified element.
     */
    protected final boolean acceptElementInStack(Element elem) {
	for (Enumeration e = elementStack.elements(); e.hasMoreElements(); ) {
	    Element selem = (Element)e.nextElement();
	    if (selem.canAccept(elem)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Checks whether the specified element is acceptable or not as
     * a new element.
     */
    protected final void checkNewElementAcceptable(Element elem)
	throws IOException
    {
	if (!acceptElement(elem) && acceptElementInStack(elem)) {
	    while (element != null && !element.canAccept(elem)) {
		popElement(createEndTagEvent(element));
	    }
	}
    }

    /** Pushs the reader at the beginning of the entity reference. */
    protected final void pushReader(Reader r) throws IOException {
	if (r == null)
	    return;
	readerStack.push(new Object[]{ reader, new Integer(lastChar) });
	reader = r;
	readChar();
    }

    /** Pops the reader at the ending of the entity reference. */
    protected final void popReader() {
	if (readerStack.isEmpty())
	    return;
	Object ctx[] = (Object[])readerStack.pop();
	reader = (Reader)ctx[0];
	lastChar = ((Integer)ctx[1]).intValue();
    }

    /** Pushs the element and delivers the start tag parsed event. */
    protected final void pushElement(Element elem, SGMLEvent event)
	throws IOException
    {
	writeCdataAtEnd(false);
	startTagParsed(event);
	if (element != null) {
	    elementStack.push(element);
	}
	element = elem;
	contentModelType = element.contentModelType;
	if (element.inclusions != null)
	    inclusions.add(element.inclusions);
	if (element.exclusions != null)
	    exclusions.add(element.exclusions);
    }

    /** Pops the element and delivers the end tag parsed event. */
    protected final Element popElement(SGMLEvent event) throws IOException {
	writeCdataAtEnd(true);
	endTagParsed(event);
	if (element != null) {
	    boolean inc = (element.inclusions != null);
	    boolean exc = (element.exclusions != null);
	    if (inc || exc) {
		if (inc) inclusions = new Set();
		if (exc) exclusions = new Set();
		for (Enumeration e = elementStack.elements();
		     e.hasMoreElements(); )
		{
		    Element selem = (Element)e.nextElement();
		    if (inc && selem.inclusions != null)
			inclusions.add(selem.inclusions);
		    if (exc && selem.exclusions != null)
			exclusions.add(selem.exclusions);
		}
	    }
	}
	if (elementStack.isEmpty()) {
	    element = null;
	    contentModelType = Element.MODEL_PCDATA;
	}
	else {
	    element = (Element)elementStack.pop();
	    contentModelType = element.contentModelType;
	}
	return element;
    }
}
