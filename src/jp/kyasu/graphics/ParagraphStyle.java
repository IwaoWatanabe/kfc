/*
 * ParagraphStyle.java
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

package jp.kyasu.graphics;

/**
 * The <code>ParagraphStyle</code> class implements the paragraph style
 * for the rich text object. The paragraph style has following attributes:
 * <dl>
 * <dt><b>Style name</b><dd>The name of the paragraph style.
 * <dt><b>Alignment</b><dd>The alignment (justification) of the paragraph.
 * <dt><b>Left indentation</b><dd>The left margin indentation of the paragraph.
 * <dt><b>Right indentation</b><dd>The right margin indentation of the
 *     paragraph.
 * <dt><b>Line space</b><dd>The space between lines in the paragraph.
 * <dt><b>Paragraph space</b><dd>The space below the paragraph.
 * <dt><b>Tab width</b><dd>The indentation for the tab character. If the
 *     tab width is not set, the default tab width of the rich text style
 *     is used.
 * <dt><b>Paragraph heading</b><dd>The visual heading of the paragraph.
 * <dt><b>Paragraph heading space</b><dd>The space for the visual heading
 *     from the left indentation.
 * <dt><b>Base text style</b><dd>The base text style of the paragraph. If
 *     the text in the paragraph has a <code>ModTextStyle</code>, the style
 *     of the text is modified from the base text style of the paragraph.
 * </dl>
 * <p>
 * The paragraph style is immutable.
 *
 * @see 	jp.kyasu.graphics.ModTextStyle
 *
 * @version 	11 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class ParagraphStyle implements Cloneable, java.io.Serializable {
    /** The style name. */
    protected String styleName;

    /** The alignment (justification). */
    protected int alignment;

    /** The left indentation. */
    protected int leftIndent;

    /** The right indentation. */
    protected int rightIndent;

    /** The line space. */
    protected int lineSpace;

    /** The paragraph space. */
    protected int paragraphSpace;

    /** The width of tab character. */
    protected int tabWidth;

    /** The visual heading. */
    protected Visualizable heading;

    /** The visual heading space from the left indentation. */
    protected int headingSpace;

    /** The base text style. */
    protected TextStyle baseStyle;


    /**
     * The constant for the left alignment (justification).
     */
    static public final int LEFT   = 0;

    /**
     * The constant for the right alignment (justification).
     */
    static public final int RIGHT  = 1;

    /**
     * The constant for the center alignment (justification).
     */
    static public final int CENTER = 2;

    /**
     * The constant for the tab width.
     */
    static public final int HARD_TAB_LENGTH = 8;

    /**
     * The default paragraph style constant.
     */
    static public final ParagraphStyle DEFAULT_STYLE =
					new ParagraphStyle(LEFT, 2, 2, 0, 0, 0);


    /**
     * Constructs a paragraph style with the specified alignment.
     *
     * @param alignment      the alignment.
     */
    public ParagraphStyle(int alignment) {
	this(alignment, 2, 2);
    }

    /**
     * Constructs a paragraph style with the specified alignment, left
     * indent and right indent.
     *
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     */
    public ParagraphStyle(int alignment, int leftIndent, int rightIndent) {
	this(alignment, leftIndent, rightIndent, 0);
    }

    /**
     * Constructs a paragraph style with the specified alignment, left
     * indent, right indent and line space.
     *
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     * @param lineSpace      the line space.
     */
    public ParagraphStyle(int alignment, int leftIndent, int rightIndent,
			  int lineSpace)
    {
	this(alignment, leftIndent, rightIndent, lineSpace, 0);
    }

    /**
     * Constructs a paragraph style with the specified alignment, left
     * indent, right indent, line space and paragraph space.
     *
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     * @param lineSpace      the line space.
     * @param paragraphSpace the paragraph space.
     */
    public ParagraphStyle(int alignment, int leftIndent, int rightIndent,
			  int lineSpace, int paragraphSpace)
    {
	this(alignment, leftIndent, rightIndent, lineSpace, paragraphSpace, 0);
    }

    /**
     * Constructs a paragraph style with the specified alignment, left
     * indent, right indent, line space, paragraph space and tab width.
     *
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     * @param lineSpace      the line space.
     * @param paragraphSpace the paragraph space.
     * @param tabWidth       the tab width.
     */
    public ParagraphStyle(int alignment, int leftIndent, int rightIndent,
			  int lineSpace, int paragraphSpace, int tabWidth)
    {
	this(null, alignment, leftIndent, rightIndent, lineSpace,
	     paragraphSpace, tabWidth, null, 0, null);
    }

    /**
     * Constructs a paragraph style with the specified alignment, left
     * indent, right indent, line space, paragraph space, tab width,
     * visual heading and heading space.
     *
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     * @param lineSpace      the line space.
     * @param paragraphSpace the paragraph space.
     * @param tabWidth       the tab width.
     * @param heading        the visual heading.
     * @param headingSpace   the heading space.
     */
    public ParagraphStyle(int alignment, int leftIndent, int rightIndent,
			  int lineSpace, int paragraphSpace, int tabWidth,
			  Visualizable heading, int headingSpace)
    {
	this(null, alignment, leftIndent, rightIndent, lineSpace,
	     paragraphSpace, tabWidth, heading, headingSpace, null);
    }

    /**
     * Constructs a paragraph style with the specified alignment, left
     * indent, right indent, line space, paragraph space, tab width and
     * base text style.
     *
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     * @param lineSpace      the line space.
     * @param paragraphSpace the paragraph space.
     * @param tabWidth       the tab width.
     * @param baseStyle      the base text style.
     */
    public ParagraphStyle(int alignment, int leftIndent, int rightIndent,
			  int lineSpace, int paragraphSpace, int tabWidth,
			  TextStyle baseStyle)
    {
	this(null, alignment, leftIndent, rightIndent, lineSpace,
	     paragraphSpace, tabWidth, null, 0, baseStyle);
    }

    /**
     * Constructs a paragraph style with the specified name, alignment,
     * left indent, right indent, line space, paragraph space, tab width,
     * visual heading and heading space.
     *
     * @param styleName      the style name.
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     * @param lineSpace      the line space.
     * @param paragraphSpace the paragraph space.
     * @param tabWidth       the tab width.
     * @param heading        the visual heading.
     * @param headingSpace   the heading space.
     */
    public ParagraphStyle(String styleName,
			  int alignment, int leftIndent, int rightIndent,
			  int lineSpace, int paragraphSpace, int tabWidth,
			  Visualizable heading, int headingSpace)
    {
	this(styleName, alignment, leftIndent, rightIndent, lineSpace,
	     paragraphSpace, tabWidth, heading, headingSpace, null);
    }

    /**
     * Constructs a paragraph style with the specified name, alignment,
     * left indent, right indent, line space, paragraph space, tab width
     * and base text style.
     *
     * @param styleName      the style name.
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     * @param lineSpace      the line space.
     * @param paragraphSpace the paragraph space.
     * @param tabWidth       the tab width.
     * @param baseStyle      the base text style.
     */
    public ParagraphStyle(String styleName,
			  int alignment, int leftIndent, int rightIndent,
			  int lineSpace, int paragraphSpace, int tabWidth,
			  TextStyle baseStyle)
    {
	this(styleName, alignment, leftIndent, rightIndent, lineSpace,
	     paragraphSpace, tabWidth, null, 0, baseStyle);
    }

    /**
     * Constructs a paragraph style with the specified name, alignment,
     * left indent, right indent, line space, paragraph space, tab width,
     * visual heading, heading space and base text style.
     *
     * @param styleName      the style name.
     * @param alignment      the alignment.
     * @param leftIndent     the left indent.
     * @param rightIndent    the right indent.
     * @param lineSpace      the line space.
     * @param paragraphSpace the paragraph space.
     * @param tabWidth       the tab width.
     * @param heading        the visual heading.
     * @param headingSpace   the heading space.
     * @param baseStyle      the base text style.
     */
    public ParagraphStyle(String styleName,
			  int alignment, int leftIndent, int rightIndent,
			  int lineSpace, int paragraphSpace, int tabWidth,
			  Visualizable heading, int headingSpace,
			  TextStyle baseStyle)
    {
	if (leftIndent < 0)
	    throw new IllegalArgumentException("improper leftIndent: " +
						leftIndent);
	if (rightIndent < 0)
	    throw new IllegalArgumentException("improper rightIndent: " +
						rightIndent);
	if (lineSpace < 0)
	    throw new IllegalArgumentException("improper lineSpace: " +
						lineSpace);
	if (paragraphSpace < 0)
	    throw new IllegalArgumentException("improper paragraphSpace: " +
						paragraphSpace);
	if (tabWidth < 0)
	    throw new IllegalArgumentException("improper tabWidth: " +
						tabWidth);
	if (headingSpace < 0)
	    throw new IllegalArgumentException("improper headingSpace: " +
						headingSpace);
	this.styleName      = styleName;
	setAlignment(alignment);
	this.leftIndent     = leftIndent;
	this.rightIndent    = rightIndent;
	this.lineSpace      = lineSpace;
	this.paragraphSpace = paragraphSpace;
	this.tabWidth       = tabWidth;
	this.heading        = heading;
	this.headingSpace   = headingSpace;
	this.baseStyle      = baseStyle;
	if (baseStyle != null) {
	    this.tabWidth = baseStyle.getFontMetrics().charWidth(' ') *
								HARD_TAB_LENGTH;
	}
    }

    /**
     * Returns the name of the paragraph style.
     */
    public final String getStyleName() {
	return styleName;
    }

    /**
     * Returns the alignment (justification) of the paragraph.
     */
    public final int getAlignment() {
	return alignment;
    }

    /**
     * Returns the left margin indentation of the paragraph.
     */
    public final int getLeftIndent() {
	return leftIndent;
    }

    /**
     * Returns the right margin indentation of the paragraph.
     */
    public final int getRightIndent() {
	return rightIndent;
    }

    /**
     * Returns the space between lines in the paragraph.
     */
    public final int getLineSpace() {
	return lineSpace;
    }

    /**
     * Returns the space below the paragraph.
     */
    public final int getParagraphSpace() {
	return paragraphSpace;
    }

    /**
     * Returns the indentation for the tab character.
     */
    public final int getTabWidth() {
	return tabWidth;
    }

    /**
     * Returns the visual heading of the paragraph.
     */
    public final Visualizable getHeading() {
	return heading;
    }

    /**
     * Returns the space for the visual heading from the left indentation.
     */
    public final int getHeadingSpace() {
	return headingSpace;
    }

    /**
     * Returns the base text style of the paragraph.
     */
    public final TextStyle getBaseStyle() {
	return baseStyle;
    }

    /**
     * Checks if this paragraph style has a style name.
     */
    public boolean hasStyleName() {
	return styleName != null;
    }

    /**
     * Checks if this paragraph style has a visual heading.
     */
    public boolean hasHeading() {
	return heading != null;
    }

    /**
     * Checks if this paragraph style has a base text style.
     */
    public boolean hasBaseStyle() {
	return baseStyle != null;
    }

    /**
     * Creates a new style by replicating this style with a new style
     * name associated with it.
     *
     * @param  styleName the style name for the new style.
     * @return a new style.
     */
    public ParagraphStyle deriveStyle(String styleName) {
	ParagraphStyle paragraphStyle = (ParagraphStyle)clone();
	paragraphStyle.styleName = styleName;
	return paragraphStyle;
    }

    /**
     * Creates a new style by replicating this style with a new base
     * text style associated with it.
     *
     * @param  baseStyle the base text style for the new style.
     * @return a new style.
     */
    public ParagraphStyle deriveStyle(TextStyle baseStyle) {
	ParagraphStyle paragraphStyle = (ParagraphStyle)clone();
	paragraphStyle.baseStyle = baseStyle;
	if (baseStyle != null) {
	    paragraphStyle.tabWidth = baseStyle.getFontMetrics().charWidth(' ')
							* HARD_TAB_LENGTH;
	}
	return paragraphStyle;
    }

    /**
     * Creates a new style by replicating this style with a new visual
     * heading and a new heading space associated with it.
     *
     * @param  visualizable the visual heading for the new style.
     * @param  space        the heading space for the new style.
     * @return a new style.
     */
    public ParagraphStyle deriveStyle(Visualizable visualizable, int space) {
	ParagraphStyle paragraphStyle = (ParagraphStyle)clone();
	paragraphStyle.heading = visualizable;
	paragraphStyle.headingSpace = space;
	return paragraphStyle;
    }

    /**
     * Creates a new style by modifying this style by a paragraph style
     * modifier.
     *
     * @param  modifier the paragraph style modifier.
     * @return a new style.
     */
    public ParagraphStyle deriveStyle(ParagraphStyleModifier modifier) {
	if (modifier == null)
	    throw new NullPointerException();
	ParagraphStyle paragraphStyle = modifier.modify(this);
	if (paragraphStyle == this) {
	    paragraphStyle = (ParagraphStyle)clone();
	}
	return paragraphStyle;
    }

    /**
     * Returns a hashcode for this paragraph style.
     */
    public int hashCode() {
	int h = alignment ^ leftIndent ^ rightIndent ^ lineSpace
				^ paragraphSpace ^ tabWidth ^ headingSpace;
	if (styleName != null)
	    h ^= styleName.hashCode();
	if (heading != null)
	    h ^= heading.hashCode();
	if (baseStyle != null)
	    h ^= baseStyle.hashCode();
	return h;
    }

    /**
     * Compares two objects for equality.
     */
    public boolean equals(Object anObject) {
	if (this == anObject)
	    return true;
	if (anObject == null)
	    return false;
	//if (anObject instanceof ParagraphStyle) {
	if (getClass() == anObject.getClass()) {
	    return equalsStyle((ParagraphStyle)anObject);
	}
	return false;
    }

    /**
     * Compares the styles of two paragraph styles for equality.
     */
    protected boolean equalsStyle(ParagraphStyle pStyle) {
	return (alignment      == pStyle.alignment      &&
		leftIndent     == pStyle.leftIndent     &&
		rightIndent    == pStyle.rightIndent    &&
		lineSpace      == pStyle.lineSpace      &&
		paragraphSpace == pStyle.paragraphSpace &&
		tabWidth       == pStyle.tabWidth       &&
		heading        == pStyle.heading        &&
		headingSpace   == pStyle.headingSpace   &&
		(styleName == null ?
			pStyle.styleName == null :
			styleName.equals(pStyle.styleName)) &&
		(baseStyle == null ?
			pStyle.baseStyle == null :
			baseStyle.equals(pStyle.baseStyle)));
    }

    /**
     * Returns a clone of this paragraph style.
     */
    public Object clone() {
	try {
	    ParagraphStyle ps = (ParagraphStyle)super.clone();
	    ps.styleName      = styleName; // share
	    ps.alignment      = alignment;
	    ps.leftIndent     = leftIndent;
	    ps.rightIndent    = rightIndent;
	    ps.lineSpace      = lineSpace;
	    ps.paragraphSpace = paragraphSpace;
	    ps.tabWidth       = tabWidth;
	    ps.heading        = heading; // share
	    ps.headingSpace   = headingSpace;
	    ps.baseStyle      = baseStyle; // share
	    return ps;
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns the string representation of this paragraph style.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	if (styleName != null)
	    buffer.append("styleName=" + styleName + ",");
	buffer.append("alignment=");
	switch (alignment) {
	case RIGHT:         buffer.append("RIGHT"); break;
	case CENTER:        buffer.append("CENTER"); break;
	case LEFT: default: buffer.append("LEFT"); break;
	}
	buffer.append(",leftIndent=" + leftIndent);
	buffer.append(",rightIndent=" + rightIndent);
	buffer.append(",lineSpace=" + lineSpace);
	buffer.append(",paragraphSpace=" + paragraphSpace);
	buffer.append(",tabWidth=" + tabWidth);
	if (baseStyle != null)
	    buffer.append(",baseStyle=" + baseStyle);
	return getClass().getName() + "[" + buffer.toString() + "]";
    }

    /**
     * Sets the alignment of the paragraph style.
     */
    protected void setAlignment(int alignment) {
	switch (alignment) {
	case LEFT:
	case RIGHT:
	case CENTER:
	    this.alignment = alignment;
	    return;
	}
	throw new IllegalArgumentException("improper alignment: " + alignment);
    }
}
