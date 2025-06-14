/*
 * BasicPSModifier.java
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
 * The <code>BasicPSModifier</code> class is a basic implementation of
 * the interface for the paragraph style modifier.
 * <p>
 * An example of the modification is:
 * <pre>
 *     ParagraphStyle pStyle = new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 0)
 *     BasicPSModifier modifier = new BasicPSModifier();
 *     modifier.put(BasicPSModifier.ALIGNMENT,  ParagraphStyle.RIGHT);
 *     modifier.put(BasicPSModifier.LINE_SPACE, 4);
 *     modifier.put(BasicPSModifier.HEADING,    new VRectangle(4, 4));
 *     pStyle = modifier.modify(pStyle);
 *         // pStyle: new ParagraphStyle(ParagraphStyle.RIGHT, 2, 2, 4) with visual heading
 *     modifier.clear();
 *     modifier.put(BasicPSModifier.ALIGNMENT,       ParagraphStyle.LEFT);
 *     modifier.put(BasicPSModifier.LINE_SPACE_DIFF, -2);
 *     modifier.put(BasicPSModifier.HEADING,         BasicPSModifier.NULL);
 *     pStyle = modifier.modify(pStyle);
 *         // pStyle: new ParagraphStyle(ParagraphStyle.LEFT, 2, 2, 2)
 * </pre>
 *
 * @see		jp.kyasu.graphics.ParagraphStyleModifier
 * @see		jp.kyasu.graphics.Modifier
 *
 * @version 	11 Nov 1997
 * @author 	Kazuki YASUMATSU
 */
public class BasicPSModifier extends Modifier implements ParagraphStyleModifier
{
    /**
     * The constant for the attribute "style name". The value of this attribute
     * should be a String.
     */
    static public final String STYLE_NAME           = "style name";

    /**
     * The constant for the attribute "alignment". The value of this attribute
     * should be an alignment integer.
     *
     * @see jp.kyasu.graphics.ParagraphStyle#LEFT
     * @see jp.kyasu.graphics.ParagraphStyle#RIGHT
     * @see jp.kyasu.graphics.ParagraphStyle#CENTER
     */
    static public final String ALIGNMENT            = "alignment";

    /**
     * The constant for the attribute "left indent". The value of this
     * attribute should be an integer.
     */
    static public final String LEFT_INDENT          = "left indent";

    /**
     * The constant for the attribute "left indent". The value of this
     * attribute should be an integer. The modification is done by adding
     * the specified value to the left indent of the paragraph style to
     * be modified.
     */
    static public final String LEFT_INDENT_DIFF     = "left indent_diff";

    /**
     * The constant for the attribute "right indent". The value of this
     * attribute should be an integer.
     */
    static public final String RIGHT_INDENT         = "right indent";

    /**
     * The constant for the attribute "right indent". The value of this
     * attribute should be an integer. The modification is done by adding
     * the specified value to the right indent of the paragraph style to
     * be modified.
     */
    static public final String RIGHT_INDENT_DIFF    = "right indent_diff";

    /**
     * The constant for the attribute "line space". The value of this
     * attribute should be an integer.
     */
    static public final String LINE_SPACE           = "line space";

    /**
     * The constant for the attribute "line space". The value of this
     * attribute should be an integer. The modification is done by adding
     * the specified value to the line space of the paragraph style to
     * be modified.
     */
    static public final String LINE_SPACE_DIFF      = "line space_diff";

    /**
     * The constant for the attribute "paragraph space". The value of this
     * attribute should be an integer.
     */
    static public final String PARAGRAPH_SPACE      = "paragraph space";

    /**
     * The constant for the attribute "paragraph space". The value of this
     * attribute should be an integer. The modification is done by adding
     * the specified value to the paragraph space of the paragraph style to
     * be modified.
     */
    static public final String PARAGRAPH_SPACE_DIFF = "paragraph space_diff";

    /**
     * The constant for the attribute "heading". The value of this
     * attribute should be a visual object (<code>Visualizable</code>).
     *
     * @see jp.kyasu.graphics.Visualizable
     */
    static public final String HEADING              = "heading";

    /**
     * The constant for the attribute "heading space". The value of this
     * attribute should be an integer.
     */
    static public final String HEADING_SPACE        = "heading space";

    /**
     * The constant for the attribute "base style". The value of this
     * attribute should be a TextStyle.
     *
     * @see jp.kyasu.graphics.TextStyle
     */
    static public final String BASE_STYLE           = "base style";


    /**
     * Constructs an empty paragraph style modifier.
     */
    public BasicPSModifier() {
	super();
    }

    /**
     * Modifies the given paragraph style, i.e., Creates the modified version
     * of the given paragraph style.
     *
     * @param  pStyle the given paragraph style.
     * @return the modified version of the given paragraph style;
     *         or the given paragraph style, if the modification has no
     *         effect on the given paragraph style.
     * @see    jp.kyasu.graphics.ParagraphStyleModifier#modify(jp.kyasu.graphics.ParagraphStyle)
     */
    public ParagraphStyle modify(ParagraphStyle pStyle) {
	if (isEmpty()) {
	    return pStyle;
	}

	String styleName     = pStyle.styleName;
	int alignment        = pStyle.alignment;
	int leftIndent       = pStyle.leftIndent;
	int rightIndent      = pStyle.rightIndent;
	int lineSpace        = pStyle.lineSpace;
	int paragraphSpace   = pStyle.paragraphSpace;
	Visualizable heading = pStyle.heading;
	int headingSpace     = pStyle.headingSpace;
	TextStyle baseStyle  = pStyle.baseStyle;

	boolean modified = false;
	Object value;

	if ((value = get(STYLE_NAME)) != null && (value instanceof String)) {
	    String newStyleName = (String)value;
	    if (styleName == null || !styleName.equals(newStyleName)) {
		styleName = newStyleName;
		modified = true;
	    }
	}

	if ((value = get(ALIGNMENT)) != null && (value instanceof Integer)) {
	    int newAlignment = ((Integer)value).intValue();
	    switch (newAlignment) {
	    case ParagraphStyle.LEFT:
	    case ParagraphStyle.CENTER:
	    case ParagraphStyle.RIGHT:
		if (alignment != newAlignment) {
		    alignment = newAlignment;
		    modified = true;
		}
		break;
	    default:
		break;
	    }
	}

	if ((value = get(LEFT_INDENT)) != null && (value instanceof Integer)) {
	    int newLeftIndent = ((Integer)value).intValue();
	    if (newLeftIndent < 0) newLeftIndent = 0;
	    if (leftIndent != newLeftIndent) {
		leftIndent = newLeftIndent;
		modified = true;
	    }
	}
	else if ((value = get(LEFT_INDENT_DIFF)) != null &&
		 (value instanceof Integer))
	{
	    int newLeftIndent = leftIndent + ((Integer)value).intValue();
	    if (newLeftIndent < 0) newLeftIndent = 0;
	    if (leftIndent != newLeftIndent) {
		leftIndent = newLeftIndent;
		modified = true;
	    }
	}

	if ((value = get(RIGHT_INDENT)) != null && (value instanceof Integer)) {
	    int newRightIndent = ((Integer)value).intValue();
	    if (newRightIndent < 0) newRightIndent = 0;
	    if (rightIndent != newRightIndent) {
		rightIndent = newRightIndent;
		modified = true;
	    }
	}
	else if ((value = get(RIGHT_INDENT_DIFF)) != null &&
		 (value instanceof Integer))
	{
	    int newRightIndent = rightIndent + ((Integer)value).intValue();
	    if (newRightIndent < 0) newRightIndent = 0;
	    if (rightIndent != newRightIndent) {
		rightIndent = newRightIndent;
		modified = true;
	    }
	}

	if ((value = get(LINE_SPACE)) != null && (value instanceof Integer)) {
	    int newLineSpace = ((Integer)value).intValue();
	    if (newLineSpace < 0) newLineSpace = 0;
	    if (lineSpace != newLineSpace) {
		lineSpace = newLineSpace;
		modified = true;
	    }
	}
	else if ((value = get(LINE_SPACE_DIFF)) != null &&
		 (value instanceof Integer))
	{
	    int newLineSpace = lineSpace + ((Integer)value).intValue();
	    if (newLineSpace < 0) newLineSpace = 0;
	    if (lineSpace != newLineSpace) {
		lineSpace = newLineSpace;
		modified = true;
	    }
	}

	if ((value = get(PARAGRAPH_SPACE)) != null &&
	    (value instanceof Integer))
	{
	    int newParagraphSpace = ((Integer)value).intValue();
	    if (newParagraphSpace < 0) newParagraphSpace = 0;
	    if (paragraphSpace != newParagraphSpace) {
		paragraphSpace = newParagraphSpace;
		modified = true;
	    }
	}
	else if ((value = get(PARAGRAPH_SPACE_DIFF)) != null &&
		 (value instanceof Integer))
	{
	    int newParagraphSpace = paragraphSpace+((Integer)value).intValue();
	    if (newParagraphSpace < 0) newParagraphSpace = 0;
	    if (paragraphSpace != newParagraphSpace) {
		paragraphSpace = newParagraphSpace;
		modified = true;
	    }
	}

	if ((value = get(HEADING)) != null) {
	    if (NULL.equals(value)) {
		if (heading != null) {
		    heading = null;
		    modified = true;
		}
	    }
	    else if (value instanceof Visualizable) {
		Visualizable newHeading = (Visualizable)value;
		if (heading != newHeading) {
		    heading = newHeading;
		    modified = true;
		}
	    }
	}
	if ((value = get(HEADING_SPACE)) != null && (value instanceof Integer))
	{
	    int newHeadingSpace = ((Integer)value).intValue();
	    if (newHeadingSpace < 0) newHeadingSpace = 0;
	    if (headingSpace != newHeadingSpace) {
		headingSpace = newHeadingSpace;
		modified = true;
	    }
	}

	if ((value = get(BASE_STYLE)) != null && (value instanceof TextStyle)) {
	    TextStyle newBaseStyle = (TextStyle)value;
	    if (baseStyle == null || !baseStyle.equals(newBaseStyle)) {
		baseStyle = newBaseStyle;
		modified = true;
	    }
	}

	if (!modified) {
	    return pStyle;
	}

	if (heading == null) {
	    headingSpace = 0;
	}
	return new ParagraphStyle(styleName,
				  alignment, leftIndent, rightIndent,
				  lineSpace, paragraphSpace, pStyle.tabWidth,
				  heading, headingSpace,
				  baseStyle);
    }
}
