/*
 * ScrollEvent.java
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

package jp.kyasu.awt.event;

import jp.kyasu.awt.Scrollable;

/**
 * The Scrollable event that is originated from a <code>Scrollable</code>
 * object to <code>ScrollListener</code>s.
 *
 * @see 	jp.kyasu.awt.Scrollable
 * @see 	jp.kyasu.awt.event.ScrollListener
 *
 * @version 	26 Dec 1997
 * @author 	Kazuki YASUMATSU
 */
public class ScrollEvent extends java.awt.AWTEvent {
    protected Scrollable scrollable;
    protected int orientation;


    /**
     * Marks the first integer id for the range of scroll event ids.
     */
    static public final int SCROLL_FIRST = java.awt.AWTEvent.RESERVED_ID_MAX+1;

    /**
     * Marks the last integer id for the range of scroll event ids.
     */
    static public final int SCROLL_LAST  = SCROLL_FIRST + 1;

    /**
     * The scroll value changed event.
     */
    static public final int SCROLL_VALUE_CHANGED = SCROLL_FIRST;

    /**
     * The scroll size changed event.
     */
    static public final int SCROLL_SIZE_CHANGED  = SCROLL_VALUE_CHANGED + 1;


    /**
     * The horizontal orientation.
     */
    static public final int HORIZONTAL = 0;

    /**
     * The vertical orientation.
     */
    static public final int VERTICAL   = 1;

    /**
     * The horizontal/vertical orientation.
     */
    static public final int BOTH       = 2;


    /**
     * Constructs a ScrollEvent object with the specified Scrollable source,
     * id, and orientation.
     *
     * @param source     the Scrollable object where the event originated.
     * @param id         the event type.
     * @param orientaion the orientation type.
     */
    public ScrollEvent(Scrollable source, int id, int orientation) {
        super(source, id);
	scrollable = source;
        this.orientation = orientation;
    }


    /**
     * Returns the Scrollable object where this event originated.
     */
    public Scrollable getScrollable() {
        return scrollable;
    }

    /**
     * Returns the orientation type in the scrollable event.
     */
    public int getOrientation() {
        return orientation;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case SCROLL_VALUE_CHANGED:
              typeStr = "SCROLL_VALUE_CHANGED";
              break;
          case SCROLL_SIZE_CHANGED:
              typeStr = "SCROLL_SIZE_CHANGED";
          default:
              typeStr = "unknown type";
        }
        return typeStr;
    }
}
