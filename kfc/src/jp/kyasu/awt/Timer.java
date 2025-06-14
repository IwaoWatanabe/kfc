/*
 * Timer.java
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

import jp.kyasu.util.Comparer;
import jp.kyasu.util.CompareAdapter;
import jp.kyasu.util.VArray;

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The <code>Timer</code> object causes an action to occur at a predefined
 * rate. For example, an animation object can use a Timer as the trigger
 * for drawing its next frame. Each Timer has a list of ActionListeners and
 * a delay (the time between <b>actionPerfomed()</b> calls). When delay
 * milliseconds have passed, a Timer sends the <b>actionPerformed()</b>
 * message to its listeners. This cycle repeats until <b>stop()</b> is
 * called, or halts immediately if the Timer is configured to send its
 * message just once.
 * <p>
 * Using a Timer involves first creating it, then starting it using
 * the <b>start()</b> method.
 * <p>
 * The API of this class is designed based on
 * <code>com.sun.java.swing.Timer</code> in <code>swing 0.5.1</code>,
 * but the implementation of this class is original.
 *
 * @version 	16 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class Timer implements java.io.Serializable {
    /** The initial delay in milliseconds. */
    protected int initialDelay;

    /** The delay in milliseconds. */
    protected int delay;

    /** The action listeners. */
    transient protected ActionListener actionListener;

    /** True if the timer is fired multiple times. */
    protected boolean repeats;

    /** True if the timer is running. */
    protected boolean isRunning;

    /** The wake up time of the timer. */
    protected long wakeUpTime;


    /**
     * The timer queue that ringings timer objects.
     */
    static protected final TimerQueue TQueue = new TimerQueue();

    /**
     * The timer thread that takes timers off the TimerQueue and fires them.
     */
    static protected TimerThread TThread = null;

    static {
	startTimerThread();
    }

    /**
     * Starts the timer thread.
     */
    static protected synchronized void startTimerThread() {
	if (TThread == null) {
	    TThread = new TimerThread("TimerQueue", TQueue);
	    try {
		TThread.setDaemon(true);
		TThread.setPriority(Thread.MAX_PRIORITY);
	    }
	    catch (SecurityException e) {}
	    TThread.start();
	}
    }

    /**
     * Stops the timer thread.
     */
    static protected synchronized void stopTimerThread() {
	if (TThread != null) {
	    TThread.stopFiring();
	    TThread = null;
	}
    }


    /**
     * Constructs a timer that will notify its listeners every delay
     * milliseconds.
     *
     * @param delay    The number of milliseconds between listener
     *                 notification.
     * @param listener An initial listener.
     */
    public Timer(int delay, ActionListener listener) {
	this(delay, delay, listener);
    }

    /**
     * Constructs a timer that will notify its listeners every delay
     * milliseconds.
     *
     * @param initialDelay the initial delay.
     * @param delay        The number of milliseconds between listener
     *                     notification.
     * @param listener     An initial listener.
     */
    public Timer(int initialDelay, int delay, ActionListener listener) {
	setInitialDelay(initialDelay);
	setDelay(delay);
	addActionListener(listener);
	repeats   = true;
	isRunning = false;

	wakeUpTime = 0;
    }


    /**
     * Adds an actionListener to the timer.
     */
    public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
    }

    /**
     * Removes an ActionListener from the timer.
     */
    public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
    }

    /**
     * Returns the timer's initial delay.
     */
    public synchronized int getInitialDelay() {
	return initialDelay;
    }

    /**
     * Sets the timer's initial delay. This will be used for the first
     * "ringing" of the timer only. Subsequent ringings will be spaced
     * using the delay property.
     */
    public synchronized void setInitialDelay(int initialDelay) {
	if (initialDelay <= 0)
	    throw new IllegalArgumentException();
	this.initialDelay = initialDelay;
    }

    /**
     * Returns the timer's delay.
     */
    public synchronized int getDelay() {
	return delay;
    }

    /**
     * Sets the timer's delay, the number of milliseconds between
     * successive <code>actionPerfomed()</code> messages to its listeners.
     */
    public synchronized void setDelay(int delay) {
	if (delay <= 0)
	    throw new IllegalArgumentException();
	this.delay = delay;
    }

    /**
     * Returns <code>true</code> if the timer will send a
     * <code>actionPerformed()</code> message to its listeners multiple
     * times.
     */
    public synchronized boolean repeats() {
	return repeats;
    }

    /**
     * If the specified flag is <code>false</code>, instructs the timer
     * to send <code>actionPerformed()</code> to its listeners only once,
     * and then stop.
     */
    public synchronized void setRepeats(boolean flag) {
	repeats = flag;
    }

    /**
     * Returns <code>true</code> if the timer is running.
     */
    public synchronized boolean isRunning() {
	return isRunning;
    }

    /**
     * Starts the timer, causing it to send <code>actionPerformed()</code>
     * messages to its listeners.
     */
    public synchronized void start() {
	if (!isRunning) {
	    isRunning = true;
	    wakeUpTime = System.currentTimeMillis() + initialDelay;
	    TQueue.addTimer(this);
	}
    }

    /**
     * Stops the timer, causing it to stop sending
     * <code>actionPerformed()</code> messages to its listeners.
     */
    public synchronized void stop() {
	if (isRunning) {
	    isRunning = false;
	    TQueue.removeTimer(this);
	}
    }

    /**
     * Restarts the timer, cancelling any pending firings, and causing
     * it to fire with its initial dely.
     */
    public synchronized void restart() {
	stop();
	start();
    }

    protected void notifyActionListeners() {
	if (actionListener != null) {
	    actionListener.actionPerformed(
		new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Timer"));
	}
    }


    /** Internal constant for serialization */
    static protected final String actionListenerK = "actionL".intern();

    private void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException
    {
	s.defaultWriteObject();
	jp.kyasu.awt.ListenerSerializer.write(s,
					      actionListenerK,
					      actionListener);
	s.writeObject(null);
    }

    private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
    {
	s.defaultReadObject();
	Object keyOrNull;
	while ((keyOrNull = s.readObject()) != null) {
	    String key = ((String)keyOrNull).intern();
	    if (key == actionListenerK)
		addActionListener((ActionListener)s.readObject());
	    else // skip value for unrecognized key
		s.readObject();
	}
	if (isRunning) {
	    restart();
	}
    }


    /*
    public static void main(String args[]) {
	Timer timer1 = new Timer(1000, new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.out.println("Timer(1000)");
	    }
	});
	timer1.start();
	Timer timer2 = new Timer(500, new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.out.println("Timer(500)");
	    }
	});
	timer2.start();
    }
    */
}


/**
 * The TimerQueue is a sorted queue of timers in the order of the wakeUpTime
 * of the timer.
 */
class TimerQueue {
    VArray queue;
    Comparer comparer;


    static class TimerComparer extends CompareAdapter {
	public int compare(Object x, Object y) {
	    return compare(((Timer)x).wakeUpTime, ((Timer)y).wakeUpTime);
	}
    }


    TimerQueue() {
	queue = new VArray(Timer.class);
	comparer = new TimerComparer();
    }


    synchronized void addTimer(Timer timer) {
	queue.append(timer);
	queue.sort(comparer);
	notify();
    }

    synchronized void removeTimer(Timer timer) {
	int index = queue.indexOf(timer);
	if (index >= 0) {
	    queue.remove(index, 1);
	    //queue.sort(comparer);
	}
    }

    synchronized Timer getNextTimer() throws InterruptedException {
	while (queue.isEmpty()) {
	    wait();
	}
	Timer timer = (Timer)queue.get(0);
	queue.remove(0, 1);
	return timer;
    }

    synchronized Timer getNextActiveTimer() throws InterruptedException {
	while (true) {
	    while (queue.isEmpty()) {
		wait();
	    }
	    Timer timer = (Timer)queue.get(0);
	    queue.remove(0, 1);
	    if (!timer.isRunning) {
		continue;
	    }
	    long waitTime = timer.wakeUpTime - System.currentTimeMillis();
	    if (waitTime > 0) {
		wait(waitTime);
	    }
	    if (System.currentTimeMillis() < timer.wakeUpTime) {
		// new timer has been added.
		if (timer.isRunning) {
		    queue.append(timer);
		    queue.sort(comparer);
		}
	    }
	    else if (timer.isRunning) {
		return timer;
	    }
	    else {
		// timer is stopped
	    }
	}
    }
}


/**
 * The TimerThread takes timers off the TimerQueue and fires them.
 */
class TimerThread extends Thread {
    TimerQueue timerQueue;
    boolean doFire;


    TimerThread(String name, TimerQueue timerQueue) {
	super(name);
	this.timerQueue = timerQueue;
	doFire = true;
    }


    void stopFiring() {
	doFire = false;
	interrupt();
    }

    public void run() {
	while (doFire) {
	    try {
		Timer timer = timerQueue.getNextActiveTimer();
		if (timer.isRunning) {
		    if (timer.repeats) {
			timer.wakeUpTime += timer.getDelay();
			timerQueue.addTimer(timer);
		    }
		    else {
			//timer.stop();
		    }
		    fireTimer(timer);
		}
	    }
	    catch (ThreadDeath death) { return; }
	    catch (InterruptedException ie) { return; }
	    catch (Throwable e) {
		System.err.println(
		    "Exception occurred during timer dispatching:");
		e.printStackTrace();
	    }
	}
    }

    void fireTimer(Timer timer) {
	EventPoster.postEvent(new TimerEvent(timer));
	/*
	if (!timer.repeats) {
	    timer.stop();
	}
	timer.notifyActionListeners();
	*/
    }
}


/**
 * The event fired by the TimerQueue.
 */
class TimerEvent extends AWTEvent {
    Timer timer;

    static Component EventTargert = new TimerComponent();

    TimerEvent(Timer timer) {
	super(EventTargert, RESERVED_ID_MAX + 1);
	this.timer = timer;
    }

    Timer getTimer() {
	return timer;
    }
}


/**
 * The component to receive the TimerEvent.
 */
class TimerComponent extends Component {
    TimerComponent() {
	enableEvents(0); // mark newEventsOnly
    }

    protected void processEvent(AWTEvent e) {
	if (e instanceof TimerEvent) {
	    Timer timer = ((TimerEvent)e).getTimer();
	    if (timer.isRunning()) {
		if (!timer.repeats()) {
		    timer.stop();
		}
		timer.notifyActionListeners();
	    }
	}
    }
}
