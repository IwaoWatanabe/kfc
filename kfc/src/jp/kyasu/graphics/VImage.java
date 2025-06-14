/*
 * VImage.java
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;

/**
 * The <code>VImage</code> class implements a visual image. This class
 * provides the interface of the visual object to the image object.
 *
 * @version 	16 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class VImage extends VObject {
    /** The image. */
    transient protected Image image;

    /** The file name the image is created from. */
    protected String filename;

    /** The url the image is created from. */
    protected URL url;

    /** The byte data the image is created from. */
    protected byte imagedata[];


    /**
     * A dummy component.
     * @see #waitForImage()
     */
    static class DummyComponent extends java.awt.Component {
    }


    /**
     * Constructs a visual image with the specified image.
     *
     * @param image the image.
     */
    public VImage(Image image) {
	this(image, true);
    }

    /**
     * Constructs a visual image with the specified image.
     *
     * @param image the image.
     * @param wait  if true, waits the image to be loaded.
     */
    public VImage(Image image, boolean wait) {
	super(1, 1);
	loadImage(image, wait);
    }

    /**
     * Constructs a visual image from the specified file name.
     *
     * @param filename the file name.
     */
    public VImage(String filename) {
	this(Toolkit.getDefaultToolkit().getImage(filename));
	if (image != null) {
	    this.filename = filename;
	}
    }

    /**
     * Constructs a visual image from the specified url.
     *
     * @param url the url.
     */
    public VImage(URL url) {
	this(Toolkit.getDefaultToolkit().getImage(url), url);
    }

    /**
     * Constructs a visual image with the specified image that is
     * created from the specified url.
     *
     * @param image the image.
     * @param url   the url of the image.
     */
    public VImage(Image image, URL url) {
	this(image);
	if (image != null) {
	    this.url = url;
	}
    }

    /**
     * Constructs a visual image from the specified byte data.
     *
     * @param imagedata the byte data.
     */
    public VImage(byte[] imagedata) {
	this(Toolkit.getDefaultToolkit().createImage(imagedata));
	if (image != null) {
	    this.imagedata = imagedata;
	}
    }


    /**
     * Returns the image in this visual image.
     *
     * @return the image in this visual image.
     */
    public Image getImage() {
	return image;
    }

    /**
     * Returns the file name the image is created from.
     *
     * @return the file name the image is created from.
     */
    public String getFilename() {
	return filename;
    }

    /**
     * Returns the url the image is created from.
     *
     * @return the url the image is created from.
     */
    public URL getURL() {
	return url;
    }

    /**
     * Resizes the visual image to the specified dimension.
     * @see jp.kyasu.graphics.Visualizable#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension d) {
	if (image == null)
	    return;
	if (width == d.width && height == d.height)
	    return;
	image = image.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
	waitForImage();
    }

    /**
     * Checks if the visual image is resizable.
     * @see jp.kyasu.graphics.Visualizable#isResizable()
     */
    public boolean isResizable() {
	return (image != null);
    }

    /**
     * Paints the visual image at the specified location.
     * The subclasses should override this method.
     * @see jp.kyasu.graphics.Visualizable#paint(java.awt.Graphics, java.awt.Point)
     */
    public void paint(Graphics g, Point p) {
	if (image == null)
	    return;
	g.drawImage(image, p.x, p.y, null);
    }

    /**
     * Returns a clone of this visual image.
     */
    public Object clone() {
	VImage vimage = (VImage)super.clone();
	vimage.image     = image;     // share
	vimage.filename  = filename;  // share
	vimage.url       = url;       // share
	vimage.imagedata = imagedata; // share
	return vimage;
    }


    /**
     * Loads the image and sets the width and height of this visual image.
     */
    protected void loadImage(Image image) {
	loadImage(image, true);
    }

    /**
     * Loads the image and sets the width and height of this visual image.
     *
     * @param wait if true, waits the image to be loaded.
     */
    protected void loadImage(Image image, boolean wait) {
	this.image = image;
	if (wait) {
	    waitForImage();
	}
	else {
	    width  = this.image.getWidth(null);
	    height = this.image.getHeight(null);
	}
    }

    /**
     * Waits the image to be loaded.
     */
    protected void waitForImage() {
	MediaTracker tracker = new MediaTracker(new DummyComponent());
	tracker.addImage(image, 0);
	try {
	    tracker.waitForID(0);
	}
	catch (InterruptedException e) {}
	if (tracker.isErrorID(0)) {
	    image = null;
	    width = height = 1;
	}
	else {
	    width  = image.getWidth(null);
	    height = image.getHeight(null);
	}
    }

    /**
     * If true, serializes the image created from the file name
     * as the image created form the bute data.
     */
    static protected final boolean SERIALIZE_FILE_AS_BYTE = true;

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	String savedFilename = filename;
	byte savedImagedata[] = imagedata;
	try {
	    if (SERIALIZE_FILE_AS_BYTE &&
		filename != null && image != null)
	    {
		java.io.File f = new java.io.File(filename);
		if (f.exists() && f.isFile() && f.canRead()) {
		    imagedata = new byte[(int)f.length()];
		    java.io.FileInputStream fs = new java.io.FileInputStream(f);
		    fs.read(imagedata);
		    fs.close();
		    filename = null;
		}
	    }
	    s.defaultWriteObject();
	}
	finally {
	    filename = savedFilename;
	    imagedata = savedImagedata;
	}
    }

    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	s.defaultReadObject();

	int rWidth  = width;
	int rHeight = height;
	if (filename != null) {
	    loadImage(Toolkit.getDefaultToolkit().getImage(filename));
	}
	else if (url != null) {
	    loadImage(Toolkit.getDefaultToolkit().getImage(url));
	}
	else if (imagedata != null) {
	    loadImage(Toolkit.getDefaultToolkit().createImage(imagedata));
	}
	else {
	    image = null;
	    width = height = 1;
	}
	if (image != null && (rWidth != width || rHeight != height)) {
	    setSize(new Dimension(rWidth, rHeight));
	}
    }
}
