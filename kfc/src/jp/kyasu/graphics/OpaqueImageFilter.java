/*
 * OpaqueImageFilter.java
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

import java.awt.Color;
import java.awt.image.*;

/**
 * The <code>OpaqueImageFilter</code> class implements a filter for
 * opaque images. This class is used in conjunction with a
 * FilteredImageSource object to produce opaque versions of existing
 * images.
 *
 * @see java.awt.image.FilteredImageSource
 * @see java.awt.image.ImageFilter
 *
 * @version 	29 Sep 1997
 * @author 	Kazuki YASUMATSU
 */
public class OpaqueImageFilter extends RGBImageFilter {
    /** The RGB color value. */
    protected int colorRGB;


    /**
     * Constructs a opaque image filter with the specified color that is
     * used as a base color for opaque images.
     *
     * @param color the base color.
     */
    public OpaqueImageFilter(Color color) {
	canFilterIndexColorModel = true;
	colorRGB = color.getRGB();
    }


    /**
     * Converts a single input pixel to a base color pixel, if the
     * brightness of the input pixel is less than 128; converts the
     * input pixel to a white pixel, otherwise.
     *
     * @param x   the x position of the pixel.
     * @param y   the y position of the pixel.
     * @param rgb the rgb pixel value.
     * @see java.awt.image.RGBImageFilter#filterRGB(int, int, int)
     */
    public int filterRGB(int x, int y, int rgb) {
	/*
	int brightness = (int)((((rgb & 0xff0000) >> 16) / 3.0) +
				(((rgb & 0xff00) >> 8) / 3.0) +
				((rgb & 0xff) / 3.0));
	*/
	// NTSC conversion
	int brightness = (int)((((rgb & 0xff0000) >> 16) * 0.30) +
				(((rgb & 0xff00) >> 8) * 0.59) +
				((rgb & 0xff) * 0.11));
	//return (brightness < 128 ? (rgb & 0xff000000) | colorRGB : 0xffffff);
	return (brightness < 128 ? colorRGB : 0xffffff);
    }
}
