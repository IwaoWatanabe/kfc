/*
 * HTMLStyle.java
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

import jp.kyasu.graphics.ModTextStyle;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.BasicPSModifier;
import jp.kyasu.graphics.BasicTSModifier;
import jp.kyasu.graphics.RichTextStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.TextStyleModifier;
import jp.kyasu.graphics.Visualizable;
import jp.kyasu.graphics.VColoredWrapper;
import jp.kyasu.graphics.VRectangle;
import jp.kyasu.graphics.VText;
import jp.kyasu.util.VArray;

import java.awt.Color;
import java.awt.Font;
import java.util.Hashtable;

/**
 * The <code>HTMLStyle</code> class implements the style for the
 * HTML document (<code>HTMLText</code>).
 * <p>
 * The html style provides text style modifiers associated with the
 * following HTML tags:<br>
 * <tt>B, STRONG, I, CITE, DFN, EM, U, TT, CODE, KBD, SAMP, VAR, BIG and
 * SMALL</tt>.
 * <p>
 * The html style also provides paragraph styles associated with the
 * following HTML tags:<br>
 * <tt>P, H1, H2, H3, H4, H5, H6, LI (LI-UL, LI-OL), DT, DD, ADDRESS and
 * PRE</tt>.
 * <br>
 * <code>LI-UL</code> and <code>LI-OL</code>corresponds to the
 * <code>LI</code> tag in the <code>UL</code> and <code>OL</code>
 * respectively.
 *
 * @see 	jp.kyasu.graphics.html.HTMLText
 * @see 	jp.kyasu.graphics.html.HTMLReader
 * @see 	jp.kyasu.graphics.html.HTMLWriter
 *
 * @version 	24 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class HTMLStyle implements java.io.Serializable {
    /** The base text style. */
    protected TextStyle baseTextStyle;

    /** The base paragraph style. */
    protected ParagraphStyle baseParagraphStyle;

    /** The text style modifiers for HTML tags. */
    protected Hashtable textStyleModifiers;

    /** The paragraph styles for HTML tags. */
    protected Hashtable paragraphStyles;


    /**
     * The default base font name.
     */
    static public final String DEFAULT_BASE_FONT_NAME = "SansSerif";

    /** The heading space for the UL and OL. */
    static protected final int LIST_HEADING_SPACE  = 8;

    /** The indentation of the UL, OL and DL. */
    static protected final int LIST_INDENT         = 50;

    /** The first indentation of BLOCKQUOTE. */
    static protected final int BQ_INDENT           = 35;

    /** The increment of the left indentation of BLOCKQUOTE. */
    static protected final int BQ_INDENT_LEFT_INC  = 30;

    /** The increment of the right indentation of BLOCKQUOTE. */
    static protected final int BQ_INDENT_RIGHT_INC = 5;


    /**
     * The the default font index.
     */
    static public final int DEFAULT_HTML_FONT = 3;

    /** The difference of the point size for the font index 7. */
    static protected final int HTML_FONT_7_DIFF = 8;

    /** The difference of the point size for the font index 6. */
    static protected final int HTML_FONT_6_DIFF = 6;

    /** The difference of the point size for the font index 5. */
    static protected final int HTML_FONT_5_DIFF = 4;

    /** The difference of the point size for the font index 4. */
    static protected final int HTML_FONT_4_DIFF = 2;

    /** The difference of the point size for the font index 3. */
    static protected final int HTML_FONT_3_DIFF = 0;

    /** The difference of the point size for the font index 2. */
    static protected final int HTML_FONT_2_DIFF = -2;

    /** The difference of the point size for the font index 1. */
    static protected final int HTML_FONT_1_DIFF = -4;


    static protected final Hashtable HeadingCache = new Hashtable();


    /**
     * Constructs a html style.
     */
    public HTMLStyle() {
	this(12);
    }

    /**
     * Constructs a html style with the specified base font size.
     *
     * @param baseSize the base font size.
     */
    public HTMLStyle(int baseSize) {
	this(new Font(DEFAULT_BASE_FONT_NAME, Font.PLAIN, baseSize));
    }

    /**
     * Constructs a html style with the specified base font.
     * The style of the base font must be plain.
     *
     * @param baseFont the base font.
     */
    public HTMLStyle(Font baseFont) {
	this(new TextStyle(baseFont));
    }

    /**
     * Constructs a html style with the specified base text style.
     * The style of the font of the base text style must be plain.
     *
     * @param textStyle the base text style.
     */
    public HTMLStyle(TextStyle textStyle) {
	this(textStyle, new ParagraphStyle(ParagraphStyle.LEFT, 4, 4, 0, 0));
    }

    /**
     * Constructs a html style with the specified base text style and
     * paragraph style.
     * The style of the font of the base text style must be plain.
     *
     * @param textStyle      the base text style.
     * @param paragraphStyle the base paragraph style.
     */
    public HTMLStyle(TextStyle textStyle, ParagraphStyle paragraphStyle) {
	if (textStyle == null || paragraphStyle == null)
	    throw new NullPointerException();
	if (textStyle instanceof ModTextStyle)
	    throw new IllegalArgumentException(
				"the text style must not be a ModTextStyle");
	if (!textStyle.getFont().isPlain())
	    throw new IllegalArgumentException(
				"the style of the font must be plain");
	baseTextStyle = textStyle;
	baseParagraphStyle = paragraphStyle.deriveStyle(
					new ModTextStyle(baseTextStyle));
	textStyleModifiers = new Hashtable();
	paragraphStyles = new Hashtable();
	initStyles();
    }


    /**
     * Returns the base text style of this html style.
     */
    public TextStyle getBaseTextStyle() {
	return baseTextStyle;
    }

    /**
     * Returns the base paragraph style of this html style.
     */
    public ParagraphStyle getBaseParagraphStyle() {
	return baseParagraphStyle;
    }

    /**
     * Returns the default text style for the HTML documents.
     */
    public TextStyle getDefaultTextStyle() {
	return getDefaultParagraphStyle().getBaseStyle();
    }

    /**
     * Returns the default paragraph style for the HTML documents.
     */
    public ParagraphStyle getDefaultParagraphStyle() {
	return getParagraphStyle("P");
    }

    /**
     * Returns the default rich text style for the HTML documents.
     */
    public RichTextStyle getDefaultRichTextStyle() {
	return new RichTextStyle(
			RichTextStyle.WORD_WRAP,
			RichTextStyle.JAVA_LINE_SEPARATOR_WITH_BREAK,
			true,
			getDefaultTextStyle(),
			getDefaultParagraphStyle());
    }

    /**
     * Returns the text style modifier associated with the specified HTML tag.
     * The associated HTML tags are
     * B, STRONG, I, CITE, DFN, EM, U, TT, CODE, KBD, SAMP, VAR, BIG and SMALL.
     *
     * @param  name the name of the HTML tag.
     * @return the text style modifier; or <code>null</code> if the
     *         associated text style modifier does not exist.
     */
    public TextStyleModifier getTextStyleModifier(String name) {
	return (TextStyleModifier)textStyleModifiers.get(name.toUpperCase());
    }

    /**
     * Returns the paragraph style associated with the specified HTML tag.
     * The associated HTML tags are
     * P, H1, H2, H3, H4, H5, H6, LI (LI-UL, LI-OL), DT, DD, ADDRESS and PRE.
     *
     * @param  name the name of the HTML tag.
     * @return the paragraph style; or <code>null</code> if the
     *         associated paragraph style does not exist.
     */
    public ParagraphStyle getParagraphStyle(String name) {
	return (ParagraphStyle)paragraphStyles.get(name.toUpperCase());
    }

    /**
     * Returns the difference of the point size of the font from the base
     * font at the specified html font index.
     * <p>
     * The value of the <code>size</code> attribute of the <code>FONT</code>
     * tag is an integer ranging from 1 to 7 with no direct mapping to point
     * sizes.
     *
     * @param  htmlFontIndex the html font index that corresponds to the
     *         value of the <code>size</code> attribute of the
     *         <code>FONT</code> tag.
     * @return the difference of the point size from the base font.
     * @see #getHTMLFontIndex(int)
     */
    public int getFontPointDifference(int htmlFontIndex) {
	htmlFontIndex = Math.min(Math.max(htmlFontIndex, 1), 7);
	switch (htmlFontIndex) {
	case 7: return HTML_FONT_7_DIFF;
	case 6: return HTML_FONT_6_DIFF;
	case 5: return HTML_FONT_5_DIFF;
	case 4: return HTML_FONT_4_DIFF;
	case 2: return HTML_FONT_2_DIFF;
	case 1: return HTML_FONT_1_DIFF;
	default:
	case 3: return HTML_FONT_3_DIFF;
	}
    }

    /**
     * Returns the html font index from the specified difference of the point
     * size of the font from the base font.
     * <p>
     * The value of the <code>size</code> attribute of the <code>FONT</code>
     * tag is an integer ranging from 1 to 7 with no direct mapping to point
     * sizes.
     *
     * @param  pointDiff the difference of the point size from the base font.
     * @return the html font index that corresponds to the value of the
     *         <code>size</code> attribute of the <code>FONT</code> tag.
     * @see #getFontPointDifference(int)
     */
    public int getHTMLFontIndex(int pointDiff) {
	if (pointDiff >= HTML_FONT_7_DIFF) return 7;
	if (pointDiff >= HTML_FONT_6_DIFF) return 6;
	if (pointDiff >= HTML_FONT_5_DIFF) return 5;
	if (pointDiff >= HTML_FONT_4_DIFF) return 4;
	if (pointDiff >= HTML_FONT_3_DIFF) return 3;
	if (pointDiff >= HTML_FONT_2_DIFF) return 2;
	//if (pointDiff >= HTML_FONT_1_DIFF) return 1;
	return 1;
    }

    /**
     * Returns the default height of the HR tag.
     */
    public int getHRSize() {
	return 3;
    }

    /**
     * Returns the increment size for the left margin indentation of
     * the BLOCKQUOTE tag at the specified nesting level.
     *
     * @see #getRightBqIncrementSize(int)
     * @see #getBqIncrementLevel(jp.kyasu.graphics.ParagraphStyle)
     */
    public int getLeftBqIncrementSize(int level) {
	if (level <= 0)
	    return 0;
	else
	    return BQ_INDENT + (BQ_INDENT_LEFT_INC * (level - 1));
    }

    /**
     * Returns the increment size for the right margin indentation of
     * the BLOCKQUOTE tag at the specified nesting level.
     *
     * @see #getLeftBqIncrementSize(int)
     * @see #getBqIncrementLevel(jp.kyasu.graphics.ParagraphStyle)
     */
    public int getRightBqIncrementSize(int level) {
	if (level <= 0)
	    return 0;
	else
	    return BQ_INDENT + (BQ_INDENT_RIGHT_INC * (level - 1));
    }

    /**
     * Returns the nesting level of the BLOCKQUOTE tag from the specified
     * paragraph style.
     *
     * @see #getLeftBqIncrementSize(int)
     * @see #getRightBqIncrementSize(int)
     */
    public int getBqIncrementLevel(ParagraphStyle pStyle) {
	int rinc = pStyle.getRightIndent()
					- baseParagraphStyle.getRightIndent()
					- BQ_INDENT;
	if (rinc < 0)
	    return 0;
	else
	    return (rinc / BQ_INDENT_RIGHT_INC) + 1;
    }

    /**
     * Returns the increment size for the left margin indentation of the
     * lists (UL, OL, DL) at the specified nesting level.
     *
     * @see #getListIncrementLevel(jp.kyasu.graphics.ParagraphStyle)
     */
    public int getListIncrementSize(int level) {
	if (level <= 0)
	    return 0;
	else
	    return (LIST_INDENT * level);
    }

    /**
     * Returns the nesting level of the lists (UL, OL, DL) from the specified
     * paragraph style.
     *
     * @see #getListIncrementSize(int)
     */
    public int getListIncrementLevel(ParagraphStyle pStyle) {
	int bqLevel = getBqIncrementLevel(pStyle);
	int linc = pStyle.getLeftIndent()
				- baseParagraphStyle.getLeftIndent()
				- getLeftBqIncrementSize(bqLevel);
	if (linc < 0)
	    return 0;
	else
	    return (linc / LIST_INDENT);
    }

    /**
     * Returns the left margin indentation with the specified BLOCKQUOTE
     * level and lists (UL, OL, DL) level.
     *
     * @see #getLeftBqIncrementSize(int)
     * @see #getListIncrementSize(int)
     * @see #getRightIndentation(int, int)
     */
    public int getLeftIndentation(int bqLevel, int listLevel) {
	return baseParagraphStyle.getLeftIndent() +
			getLeftBqIncrementSize(bqLevel) +
			getListIncrementSize(listLevel);
    }

    /**
     * Returns the right margin indentation with the specified BLOCKQUOTE
     * level and lists (UL, OL, DL) level.
     *
     * @see #getRightBqIncrementSize(int)
     * @see #getLeftIndentation(int, int)
     */
    public int getRightIndentation(int bqLevel, int listLevel) {
	return baseParagraphStyle.getRightIndent() +
			getRightBqIncrementSize(bqLevel);
    }

    /**
     * Returns the paragraph style for the LI tag in the UL (LI-UL), with
     * the specified nesting level and text color.
     */
    public ParagraphStyle getULIParagraphStyle(int level, Color textColor) {
	ParagraphStyle pStyle = getParagraphStyle("LI-UL");
	TextStyle baseStyle = pStyle.getBaseStyle();
	Visualizable heading = getULIHeading(level, baseStyle, textColor);
	BasicPSModifier modifier = new BasicPSModifier();
	modifier.put(BasicPSModifier.HEADING, heading);
	modifier.put(BasicPSModifier.HEADING_SPACE, LIST_HEADING_SPACE);
	modifier.put(BasicPSModifier.LEFT_INDENT_DIFF,
		     getListIncrementSize(level));
	return pStyle.deriveStyle(modifier);
    }

    /**
     * Returns the paragraph style for the LI tag in the OL (LI-OL), with
     * the specified nesting level, ordered index and text color.
     */
    public ParagraphStyle getOLIParagraphStyle(int level, int index,
					       Color textColor)
    {
	ParagraphStyle pStyle = getParagraphStyle("LI-OL");
	TextStyle baseStyle = pStyle.getBaseStyle();
	Visualizable heading= getOLIHeading(level, index, baseStyle, textColor);
	BasicPSModifier modifier = new BasicPSModifier();
	modifier.put(BasicPSModifier.HEADING, heading);
	modifier.put(BasicPSModifier.HEADING_SPACE, LIST_HEADING_SPACE);
	modifier.put(BasicPSModifier.LEFT_INDENT_DIFF,
		     getListIncrementSize(level));
	return pStyle.deriveStyle(modifier);
    }

    /**
     * Returns the paragraph style for the DT tag with the specified
     * nesting level.
     */
    public ParagraphStyle getDTParagraphStyle(int level) {
	ParagraphStyle pStyle = getParagraphStyle("DT");
	if (level > 0) {
	    BasicPSModifier modifier = new BasicPSModifier();
	    modifier.put(BasicPSModifier.LEFT_INDENT_DIFF,
			 getListIncrementSize(level));
	    pStyle = pStyle.deriveStyle(modifier);
	}
	return pStyle;
    }

    /**
     * Returns the paragraph style for the DD tag with the specified
     * nesting level.
     */
    public ParagraphStyle getDDParagraphStyle(int level) {
	ParagraphStyle pStyle = getParagraphStyle("DD");
	if (level > 0) {
	    BasicPSModifier modifier = new BasicPSModifier();
	    modifier.put(BasicPSModifier.LEFT_INDENT_DIFF,
			 getListIncrementSize(level));
	    pStyle = pStyle.deriveStyle(modifier);
	}
	return pStyle;
    }

    /**
     * Returns the visual heading for the LI tag in the UL (LI-UL), with
     * the specified nesting level, current base text style and text color.
     */
    public Visualizable getULIHeading(int level,
				      TextStyle baseStyle,
				      Color textColor)
    {
	int size = Math.min(Math.max(baseStyle.getFont().getSize() / 3, 4),
			    LIST_INDENT - LIST_HEADING_SPACE);
	int style = (level <= 1 ? VRectangle.PLAIN : VRectangle.OUTLINE);
	Hashtable ulCache = (Hashtable)HeadingCache.get("UL");
	if (ulCache == null) {
	    ulCache = new Hashtable();
	    HeadingCache.put("UL", ulCache);
	}
	VArray key = new VArray(Object.class);
	key.append(new Integer(size));
	key.append(new Integer(style));
	key.append(textColor);
	Visualizable v = (Visualizable)ulCache.get(key);
	if (v == null) {
	    v = new VColoredWrapper(
		    new VRectangle(size, size, style), textColor);
	    ulCache.put(key, v);
	}
	return v;
    }

    /**
     * Returns the visual heading for the LI tag in the OL (LI-OL), with
     * the specified nesting level, ordered index, current base text style
     * and text color.
     *
     * @see #getOLIIndex(jp.kyasu.graphics.Visualizable)
     */
    public Visualizable getOLIHeading(int level,
				      int index,
				      TextStyle baseStyle,
				      Color textColor)
    {
	Hashtable olCache = (Hashtable)HeadingCache.get("OL");
	if (olCache == null) {
	    olCache = new Hashtable();
	    HeadingCache.put("OL", olCache);
	}
	VArray key = new VArray(Object.class);
	key.append(new Integer(index));
	key.append(baseStyle);
	key.append(textColor);
	Visualizable v = (Visualizable)olCache.get(key);
	if (v == null) {
	    v = new VColoredWrapper(
		    new VText(new Text(String.valueOf(index) + ".", baseStyle)),
		    textColor);
	    olCache.put(key, v);
	}
	return v;
    }

    /**
     * Gets the index of the LI tag in the OL (LI-OL) from the specified
     * visual heading.
     *
     * @see #getOLIHeading(int, int, jp.kyasu.graphics.Visualizable, jp.kyasu.graphics.TextStyle, java.awt.Color)
     */
    public int getOLIIndex(Visualizable v) {
	if (v == null || !(v instanceof VColoredWrapper))
	    return 0;
	v = ((VColoredWrapper)v).getVisualizable();
	if (!(v instanceof VText))
	    return 0;
	String s = ((VText)v).getText().toString();
	if (s.length() > 1) {
	    s = s.substring(0, s.length() - 1); // trim last '.'
	}
	try {
	    return Integer.parseInt(s);
	}
	catch (NumberFormatException e) {
	    return 0;
	}
    }


    /**
     * Initializes the text style modifiers and paragraph styles associated
     * with the HTML tags.
     *
     * @see #initTextStyleModifiers
     * @see #initParagraphStyles
     */
    protected void initStyles() {
	initTextStyleModifiers();
	initParagraphStyles();
    }

    /**
     * Initializes the text style modifiers associated with the HTML tags;
     * B, STRONG, I, CITE, DFN, EM, U, TT, CODE, KBD, SAMP, VAR, BIG and SMALL.
     */
    protected void initTextStyleModifiers() {
	BasicTSModifier modifier;

	modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.BOLD, true);
	textStyleModifiers.put("B", modifier);
	textStyleModifiers.put("STRONG", modifier);

	modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.ITALIC, true);
	textStyleModifiers.put("I", modifier);
	textStyleModifiers.put("CITE", modifier);
	textStyleModifiers.put("DFN", modifier);
	textStyleModifiers.put("EM", modifier);

	modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.UNDERLINE, true);
	textStyleModifiers.put("U", modifier);

	modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.NAME, "Monospaced");
	textStyleModifiers.put("TT", modifier);
	textStyleModifiers.put("CODE", modifier);
	textStyleModifiers.put("KBD", modifier);
	textStyleModifiers.put("SAMP", modifier);

	modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.BOLD,   true);
	modifier.put(BasicTSModifier.ITALIC, true);
	textStyleModifiers.put("VAR", modifier);

	modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.SIZE_DIFF, HTML_FONT_4_DIFF);
	textStyleModifiers.put("BIG", modifier);

	modifier = new BasicTSModifier();
	modifier.put(BasicTSModifier.SIZE_DIFF, HTML_FONT_2_DIFF);
	textStyleModifiers.put("SMALL", modifier);
    }

    /**
     * Initializes the paragraph styles associated with the HTML tags;
     * P, H1, H2, H3, H4, H5, H6, LI (LI-UL, LI-OL), DT, DD, ADDRESS and PRE.
     */
    protected void initParagraphStyles() {
	BasicPSModifier pmod = new BasicPSModifier();
	BasicTSModifier mod = new BasicTSModifier();
	ParagraphStyle pStyle;
	ModTextStyle defaultModStyle = new ModTextStyle(baseTextStyle);

	// Paragraph

	pmod.put(BasicPSModifier.STYLE_NAME, "P");
	pmod.put(BasicPSModifier.BASE_STYLE, defaultModStyle);
	pmod.put(BasicPSModifier.PARAGRAPH_SPACE_DIFF, 10);
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("P", pStyle);

	pmod.clear(); mod.clear();

	// Headings

	mod.put(BasicTSModifier.BOLD, true);
	mod.put(BasicTSModifier.SIZE_DIFF, 6);
	pmod.put(BasicPSModifier.BASE_STYLE,
		 new ModTextStyle(mod.modify(baseTextStyle)));
	pmod.put(BasicPSModifier.STYLE_NAME, "H1");
	pmod.put(BasicPSModifier.PARAGRAPH_SPACE_DIFF, 10);
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("H1", pStyle);

	mod.put(BasicTSModifier.SIZE_DIFF, 4);
	pmod.put(BasicPSModifier.BASE_STYLE,
		 new ModTextStyle(mod.modify(baseTextStyle)));
	pmod.put(BasicPSModifier.STYLE_NAME, "H2");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("H2", pStyle);

	mod.put(BasicTSModifier.SIZE_DIFF, 2);
	pmod.put(BasicPSModifier.BASE_STYLE,
		 new ModTextStyle(mod.modify(baseTextStyle)));
	pmod.put(BasicPSModifier.STYLE_NAME, "H3");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("H3", pStyle);

	mod.put(BasicTSModifier.SIZE_DIFF, 0);
	pmod.put(BasicPSModifier.BASE_STYLE,
		 new ModTextStyle(mod.modify(baseTextStyle)));
	pmod.put(BasicPSModifier.STYLE_NAME, "H4");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("H4", pStyle);

	mod.put(BasicTSModifier.SIZE_DIFF, -2);
	pmod.put(BasicPSModifier.BASE_STYLE,
		 new ModTextStyle(mod.modify(baseTextStyle)));
	pmod.put(BasicPSModifier.STYLE_NAME, "H5");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("H5", pStyle);

	mod.put(BasicTSModifier.SIZE_DIFF, -2);
	pmod.put(BasicPSModifier.BASE_STYLE,
		 new ModTextStyle(mod.modify(baseTextStyle)));
	pmod.put(BasicPSModifier.STYLE_NAME, "H6");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("H6", pStyle);

	pmod.clear(); mod.clear();

	// Lists

	pmod.put(BasicPSModifier.BASE_STYLE, defaultModStyle);
	pmod.put(BasicPSModifier.STYLE_NAME, "LI-UL");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("LI-UL", pStyle);

	pmod.put(BasicPSModifier.STYLE_NAME, "LI-OL");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("LI-OL", pStyle);

	pmod.put(BasicPSModifier.STYLE_NAME, "DT");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("DT", pStyle);

	pmod.put(BasicPSModifier.STYLE_NAME, "DD");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("DD", pStyle);

	pmod.clear(); mod.clear();

	// Text Flows

	mod.put(BasicTSModifier.ITALIC, true);
	pmod.put(BasicPSModifier.BASE_STYLE,
		 new ModTextStyle(mod.modify(baseTextStyle)));
	pmod.put(BasicPSModifier.STYLE_NAME, "ADDRESS");
	pmod.put(BasicPSModifier.PARAGRAPH_SPACE_DIFF, 10);
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("ADDRESS", pStyle);

	mod.clear();

	mod.put(BasicTSModifier.NAME, "Monospaced");
	pmod.put(BasicPSModifier.BASE_STYLE,
		 new ModTextStyle(mod.modify(baseTextStyle)));
	pmod.put(BasicPSModifier.STYLE_NAME, "PRE");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("PRE", pStyle);

	mod.clear();

	/*
	pmod.put(BasicPSModifier.BASE_STYLE, defaultModStyle);
	pmod.put(BasicPSModifier.LEFT_INDENT_DIFF, BQ_INDENT);
	pmod.put(BasicPSModifier.RIGHT_INDENT_DIFF, BQ_INDENT);
	pmod.put(BasicPSModifier.STYLE_NAME, "BLOCKQUOTE");
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("BLOCKQUOTE", pStyle);

	pmod.clear(); mod.clear();
	*/

	// Table

	pmod.put(BasicPSModifier.BASE_STYLE, defaultModStyle);
	pmod.put(BasicPSModifier.STYLE_NAME, "TR");
	pmod.put(BasicPSModifier.LEFT_INDENT_DIFF, 20);
	pmod.put(BasicPSModifier.PARAGRAPH_SPACE_DIFF, 2);
	pStyle = baseParagraphStyle.deriveStyle(pmod);
	paragraphStyles.put("TR", pStyle);
    }
}
