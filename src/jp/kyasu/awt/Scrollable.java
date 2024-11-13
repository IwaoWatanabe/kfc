/*
 * Scrollable.java
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

package jp.kyasu.awt;

import jp.kyasu.awt.event.ScrollListener;

/**
 * The interface for objects which have an vertical/horizaontal adjustable
 * numeric value contained within a bounded range of values.
 *
 * @see 	jp.kyasu.awt.event.ScrollEvent
 * @see 	jp.kyasu.awt.event.ScrollListener
 *
 * @version 	26 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public interface Scrollable {
    /**
     * Gets the vertical minimum value of the scrollable object.
     */
    public int getVMinimum();

    /**
     * Gets the horizontal minimum value of the scrollable object.
     */
    public int getHMinimum();

    /**
     * Gets the vertical maximum value of the scrollable object.
     */
    public int getVMaximum();

    /**
     * Gets the horizontal maximum value of the scrollable object.
     */
    public int getHMaximum();

    /**
     * Gets the vertical unit value increment for the scrollable object.
     */
    public int getVUnitIncrement();

    /**
     * Gets the horizontal unit value increment for the scrollable object.
     */
    public int getHUnitIncrement();

    /**
     * Gets the vertical block value increment for the scrollable object.
     */
    public int getVBlockIncrement();

    /**
     * Gets the horizontal block value increment for the scrollable object.
     */
    public int getHBlockIncrement();

    /**
     * Gets the vertical length of the propertional indicator.
     */
    public int getVVisibleAmount();

    /**
     * Gets the horizontal length of the propertional indicator.
     */
    public int getHVisibleAmount();

    /**
     * Gets the vertical current value of the scrollable object.
     */
    public int getVValue();

    /**
     * Gets the horizontal current value of the scrollable object.
     */
    public int getHValue();

    /**
     * Sets the vertical current value of the scrollable object.
     * @param v the current value.
     */
    public void setVValue(int v);

    /**
     * Sets the horizontal current value of the scrollable object.
     * @param v the current value.
     */
    public void setHValue(int v);

    /**
     * Add a listener to recieve scroll events when the value
     * of the scroll component changes.
     * @param l the listener to recieve events.
     */
    public void addScrollListener(ScrollListener l);

    /**
     * Removes an scroll listener.
     * @param l the listener being removed.
     */
    public void removeScrollListener(ScrollListener l);

}
