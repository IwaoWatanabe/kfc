/*
 * EventPoster.java
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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;

/**
 * The <code>EventPoster</code> class posts an <code>AWTEvent</code> to
 * the system event queue. If the system event queue cannot be accessed
 * because of the security, then posts the <code>AWTEvent</code> using
 * a new thread to avoid blocking the current thread.
 *
 * @version 	10 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class EventPoster {

    /**
     * The system event queue.
     */
    static protected EventQueue SystemEventQueue;

    static {
	try {
	    SystemEventQueue =
			Toolkit.getDefaultToolkit().getSystemEventQueue();
	}
	catch (SecurityException e) {
	    SystemEventQueue = null;
	}
    }


    /**
     * Posts an event to the system event queue. If the system event
     * queue cannot be accessed because of the security, then posts the
     * event using a new thread to avoid blocking the current thread.
     *
     * @param event an <code>AWTEvent</code> to be posted.
     */
    static public void postEvent(AWTEvent event) {
	if (SystemEventQueue != null) {
	    SystemEventQueue.postEvent(event);
	}
	else {
	    Thread thread = new OneTimeEventDispatchThread(event);
	    thread.start();
	}
    }
}


/**
 * This class dispatches an event and then dies.
 */
class OneTimeEventDispatchThread extends Thread {
    AWTEvent event;

    OneTimeEventDispatchThread(AWTEvent event) {
	super();
	this.event = event;
    }

    public void run() {
	try {
	    Object src = event.getSource();
	    if (src instanceof Component) {
		((Component)src).dispatchEvent(event);
	    }
	}
	catch (ThreadDeath death) {
	    return;
	}
	catch (Throwable e) {
	    System.err.println("Exception occurred during event dispatching:");
	    e.printStackTrace();
	}
    }
}
