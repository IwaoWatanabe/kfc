/*
 * Dialog.java
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

import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.TextStyle;
import jp.kyasu.graphics.Visualizable;
import jp.kyasu.graphics.VImage;
import jp.kyasu.graphics.VRichText;
import jp.kyasu.graphics.VText;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The <code>Dialog</code> class produces a dialog - a window that takes
 * input from the user. The default layout for a dialog is
 * <code>BorderLayout</code>.
 * <p>
 * If the JDK has the <code>sun.awt.windows.WWindowPeer#getFocusPeer()</code>
 * bug (the JDK for Windows 95/NT version 1.1.4 or before), an application
 * that uses the <code>jp.kyasu.awt</code> package should use this class
 * instead of <code>java.awt.Dialog</code>.
 * <p>
 * Because of this bug, if a lightwight component is focus traversable,
 * an application will be hung up.
 *
 * @see 	java.awt.Dialog
 * @see 	jp.kyasu.awt.AWTResources#HAS_FOCUS_BUG
 *
 * @version 	15 May 1999
 * @author 	Kazuki YASUMATSU
 */
public class Dialog extends java.awt.Dialog {

    static protected VImage INFORM_ICON   = null;
    static protected VImage WARN_ICON     = null;
    static protected VImage QUESTION_ICON = null;
    static protected VImage ERROR_ICON    = null;

    /** Returns the information icon. */
    public VImage getInformIcon() {
	if (INFORM_ICON == null) {
	    INFORM_ICON = AWTResources.getIcon(getClass(), "icons/inform.gif");
	}
	return INFORM_ICON;
    }

    /** Returns the warning icon. */
    public VImage getWarnIcon() {
	if (WARN_ICON == null) {
	    WARN_ICON = AWTResources.getIcon(getClass(), "icons/warn.gif");
	}
	return WARN_ICON;
    }

    /** Returns the question icon. */
    public VImage getQuestionIcon() {
	if (QUESTION_ICON == null) {
	    QUESTION_ICON =
			AWTResources.getIcon(getClass(), "icons/question.gif");
	}
	return QUESTION_ICON;
    }

    /** Returns the error icon. */
    public VImage getErrorIcon() {
	if (ERROR_ICON == null) {
	    ERROR_ICON = AWTResources.getIcon(getClass(), "icons/error.gif");
	}
	return ERROR_ICON;
    }


    // ======== Request methods ========

    /** The default columns of the text field for the request methods. */
    static protected final int DEFAULT_COLUMNS = 30;

    /**
     * Prompts a user to input a string with a dialog.
     *
     * @param  message the message string shown in the dialog.
     * @return the inputted string, or an empty string if the dialog is
     *         canceled.
     */
    static public String request(String message) {
	return request(message, "", DEFAULT_COLUMNS);
    }

    /**
     * Prompts a user to input a string with a dialog.
     *
     * @param  message    the message string shown in the dialog.
     * @param  initialStr the initial inputted string.
     * @return the inputted string, or an empty string if the dialog is
     *         canceled.
     */
    static public String request(String message, String initialStr) {
	return request(message, initialStr, DEFAULT_COLUMNS);
    }

    /**
     * Prompts a user to input a string with a dialog.
     *
     * @param  message    the message string shown in the dialog.
     * @param  columns    the columns for the input text field.
     * @return the inputted string, or an empty string if the dialog is
     *         canceled.
     */
    static public String request(String message, int columns) {
	return request(message, "", columns);
    }

    /**
     * Prompts a user to input a string with a dialog.
     *
     * @param  message    the message string shown in the dialog.
     * @param  initialStr the initial inputted string.
     * @param  columns    the columns for the input text field.
     * @return the inputted string, or an empty string if the dialog is
     *         canceled.
     */
    static public String request(String message, String initialStr, int columns)
    {
	return request((java.awt.Frame)null, message, initialStr, columns);
    }

    /**
     * Prompts a user to input a string with a dialog.
     *
     * @param  frame   the owner of the dialog.
     * @param  message the message string shown in the dialog.
     * @return the inputted string, or an empty string if the dialog is
     *         canceled.
     */
    static public String request(java.awt.Frame frame, String message) {
	return request(frame, message, "", DEFAULT_COLUMNS);
    }

    /**
     * Prompts a user to input a string with a dialog.
     *
     * @param  frame      the owner of the dialog.
     * @param  message    the message string shown in the dialog.
     * @param  initialStr the initial inputted string.
     * @return the inputted string, or an empty string if the dialog is
     *         canceled.
     */
    static public String request(java.awt.Frame frame, String message,
				 String initialStr)
    {
	return request(frame, message, initialStr, DEFAULT_COLUMNS);
    }

    /**
     * Prompts a user to input a string with a dialog.
     *
     * @param  frame      the owner of the dialog.
     * @param  message    the message string shown in the dialog.
     * @param  columns    the columns for the input text field.
     * @return the inputted string, or an empty string if the dialog is
     *         canceled.
     */
    static public String request(java.awt.Frame frame, String message,
				 int columns)
    {
	return request(frame, message, "", columns);
    }

    /**
     * Prompts a user to input a string with a dialog.
     *
     * @param  frame      the owner of the dialog.
     * @param  message    the message string shown in the dialog.
     * @param  initialStr the initial inputted string.
     * @param  columns    the columns for the input text field.
     * @return the inputted string, or an empty string if the dialog is
     *         canceled.
     */
    static public String request(java.awt.Frame frame, String message,
				 String initialStr, int columns)
    {
	if (message == null)
	    throw new NullPointerException();
	if (initialStr == null) initialStr = "";
	java.awt.Frame dFrame = (frame == null ? new java.awt.Frame() : frame);
	Dialog dialog = new Dialog(
		dFrame,
		AWTResources.getResourceString("kfc.dialog.requestLabel",
					       "Request"),
		true);
	String answer[] = { initialStr };

	Panel p1 = new Panel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	p1.setLayout(gridbag);

	c.ipadx = c.ipady = 5;
	c.gridx = c.gridy = 0;
	c.gridheight = 2;
	Label label = new Label(dialog.getQuestionIcon());
	gridbag.setConstraints(label, c);
	p1.add(label);

	c.gridx = 1;
	c.gridheight = 1;
	c.anchor = GridBagConstraints.WEST;
	label = new Label(message);
	gridbag.setConstraints(label, c);
	p1.add(label);

	c.gridy = 1;
	TextField field = new TextField(columns);
	field.setText(new String(initialStr));
	field.select(0, initialStr.length());
	field.addActionListener(new DialogActionListener(dialog, answer));
	gridbag.setConstraints(field, c);
	p1.add(field);

	dialog.add(p1, BorderLayout.CENTER);

	Panel p2 = new Panel();
	String okLabel =
	    AWTResources.getResourceString("kfc.dialog.okLabel", "OK");
	String cancelLabel =
	    AWTResources.getResourceString("kfc.dialog.cancelLabel", "Cancel");
	Button ok = new Button(okLabel);
	Button cancel = new Button(cancelLabel);
	ok.addActionListener(new DialogActionListener(dialog, answer, field));
	cancel.addActionListener(new DialogActionListener(dialog, answer, ""));
	p2.add(ok);
	p2.add(cancel);
	dialog.add(p2, BorderLayout.SOUTH);
	dialog.pack();
	dialog.setVisible(true);

	if (frame == null) dFrame.dispose();
	else dialog.dispose();
	return answer[0];
    }


    // ======== Choice methods ========

    /** The normal message type. */
    static public final int MESSAGE  = 0;

    /** The inform message type. */
    static public final int INFORM   = 1;

    /** The warn message type. */
    static public final int WARN     = 2;

    /** The error message type. */
    static public final int ERROR    = 3;

    /** The confirm message type. */
    static public final int CONFIRM  = 4;

    /**
     * Shows the message with a dialog.
     *
     * @param message the message string shown in the dialog.
     */
    static public void message(String message) {
	message((java.awt.Frame)null, message);
    }

    /**
     * Shows the message with a dialog.
     *
     * @param message the visual message shown in the dialog.
     */
    static public void message(Visualizable message) {
	message((java.awt.Frame)null, message);
    }

    /**
     * Shows the message with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param message the message string shown in the dialog.
     */
    static public void message(java.awt.Frame frame, String message) {
	message(frame, createVisualizable(message));
    }

    /**
     * Shows the message with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param label   the label of the dialog.
     * @param message the message string shown in the dialog.
     */
    static public void message(java.awt.Frame frame, String label,
			       String message)
    {
	message(frame, label, createVisualizable(message));
    }

    /**
     * Shows the message with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param message the visual message shown in the dialog.
     */
    static public void message(java.awt.Frame frame, Visualizable message) {
	message(
	    frame,
	    AWTResources.getResourceString("kfc.dialog.informLabel", "Inform"),
	    message);
    }

    /**
     * Shows the message with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param label   the label of the dialog.
     * @param message the visual message shown in the dialog.
     */
    static public void message(java.awt.Frame frame, String label,
			       Visualizable message)
    {
	choice(
	    MESSAGE,
	    frame,
	    label,
	    message,
	    new String[]{
		AWTResources.getResourceString("kfc.dialog.okLabel", "OK")
	    },
	    new Object[]{ null },
	    -1);
    }

    /**
     * Informs a user with a dialog.
     *
     * @param message the message string shown in the dialog.
     */
    static public void inform(String message) {
	inform((java.awt.Frame)null, message);
    }

    /**
     * Informs a user with a dialog.
     *
     * @param message the visual message shown in the dialog.
     */
    static public void inform(Visualizable message) {
	inform((java.awt.Frame)null, message);
    }

    /**
     * Informs a user with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param message the message string shown in the dialog.
     */
    static public void inform(java.awt.Frame frame, String message) {
	inform(frame, createVisualizable(message));
    }

    /**
     * Informs a user with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param message the visual message shown in the dialog.
     */
    static public void inform(java.awt.Frame frame, Visualizable message) {
	choice(
	    INFORM,
	    frame,
	    AWTResources.getResourceString("kfc.dialog.informLabel", "Inform"),
	    message,
	    new String[]{
		AWTResources.getResourceString("kfc.dialog.okLabel", "OK")
	    },
	    new Object[]{ null },
	    -1);
    }

    /**
     * Warns a user with a dialog.
     *
     * @param message the message string shown in the dialog.
     */
    static public void warn(String message) {
	warn((java.awt.Frame)null, message);
    }

    /**
     * Warns a user with a dialog.
     *
     * @param message the visual message shown in the dialog.
     */
    static public void warn(Visualizable message) {
	warn((java.awt.Frame)null, message);
    }

    /**
     * Warns a user with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param message the message string shown in the dialog.
     */
    static public void warn(java.awt.Frame frame, String message) {
	warn(frame, createVisualizable(message));
    }

    /**
     * Warns a user with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param message the visual message shown in the dialog.
     */
    static public void warn(java.awt.Frame frame, Visualizable message) {
	choice(
	    WARN,
	    frame,
	    AWTResources.getResourceString("kfc.dialog.warnLabel", "Warn"),
	    message,
	    new String[]{
		AWTResources.getResourceString("kfc.dialog.okLabel", "OK")
	    },
	    new Object[]{ null },
	    -1);
    }

    /**
     * Errors a user with a dialog.
     *
     * @param message the message string shown in the dialog.
     */
    static public void error(String message) {
	error((java.awt.Frame)null, message);
    }

    /**
     * Errors a user with a dialog.
     *
     * @param message the visual message shown in the dialog.
     */
    static public void error(Visualizable message) {
	error((java.awt.Frame)null, message);
    }

    /**
     * Errors a user with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param message the message string shown in the dialog.
     */
    static public void error(java.awt.Frame frame, String message) {
	error(frame, createVisualizable(message));
    }

    /**
     * Errors a user with a dialog.
     *
     * @param frame   the owner of the dialog.
     * @param message the visual message shown in the dialog.
     */
    static public void error(java.awt.Frame frame, Visualizable message) {
	choice(
	    ERROR,
	    frame,
	    AWTResources.getResourceString("kfc.dialog.errorLabel", "Error"),
	    message,
	    new String[]{
		AWTResources.getResourceString("kfc.dialog.okLabel", "OK")
	    },
	    new Object[]{ null },
	    -1);
    }

    /**
     * Prompts a user to confirm the specified message with a dialog.
     *
     * @param  message the message string shown in the dialog.
     * @return <code>true</code> if confirmed, or <code>false</code> otherwise.
     */
    static public boolean confirm(String message) {
	return confirm((java.awt.Frame)null, message);
    }

    /**
     * Prompts a user to confirm the specified message with a dialog.
     *
     * @param  message the visual message shown in the dialog.
     * @return <code>true</code> if confirmed, or <code>false</code> otherwise.
     */
    static public boolean confirm(Visualizable message) {
	return confirm((java.awt.Frame)null, message);
    }

    /**
     * Prompts a user to confirm the specified message with a dialog.
     *
     * @param  frame   the owner of the dialog.
     * @param  message the message string shown in the dialog.
     * @return <code>true</code> if confirmed, or <code>false</code> otherwise.
     */
    static public boolean confirm(java.awt.Frame frame, String message) {
	return confirm(frame, createVisualizable(message));
    }

    /**
     * Prompts a user to confirm the specified message with a dialog.
     *
     * @param  frame   the owner of the dialog.
     * @param  message the visual message shown in the dialog.
     * @return <code>true</code> if confirmed, or <code>false</code> otherwise.
     */
    static public boolean confirm(java.awt.Frame frame, Visualizable message) {
	Object answer =
	    choice(
		CONFIRM,
		frame,
		AWTResources.getResourceString("kfc.dialog.confirmLabel",
					       "Confirm"),
		message,
		new String[] {
		    AWTResources.getResourceString("kfc.dialog.yesLabel","Yes"),
		    AWTResources.getResourceString("kfc.dialog.noLabel", "No")
		},
		new Object[]{ new Boolean(true), new Boolean(false) },
		-1);
	return ((Boolean)answer).booleanValue();
    }

    /**
     * Prompts a user to select a value from the specified values with a dialog.
     *
     * @param  type         the dialog type.
     * @param  frame        the owner of the dialog.
     * @param  title        the title of the dialog.
     * @param  message      the message string shown in the dialog.
     * @param  labels       the labels associated with the values.
     * @param  values       the values.
     * @param  defaultIndex the default selected index of the values.
     * @return the selected value, or null if the dialog is canceled.
     */
    static public Object choice(int type, java.awt.Frame frame, String title,
				String message,
				String[] labels, Object values[],
				int defaultIndex)
    {
	return choice(type, frame, title, createVisualizable(message),
		      labels, values, defaultIndex);
    }

    /**
     * Prompts a user to select a value from the specified values with a dialog.
     *
     * @param  type         the dialog type.
     * @param  frame        the owner of the dialog.
     * @param  title        the title of the dialog.
     * @param  message      the visual message shown in the dialog.
     * @param  labels       the labels associated with the values.
     * @param  values       the values.
     * @param  defaultIndex the default selected index of the values.
     * @return the selected value, or null if the dialog is canceled.
     */
    static public Object choice(int type, java.awt.Frame frame, String title,
				Visualizable message,
				String[] labels, Object values[],
				int defaultIndex)
    {
	if (message == null || labels == null || values == null)
	    throw new NullPointerException();
	if (title == null) title = "";
	java.awt.Frame dFrame = (frame == null ? new java.awt.Frame() : frame);
	Dialog dialog = new Dialog(dFrame, title, true);
	Object selectedValue[] = { null };

	Panel p1 = new Panel();
	switch (type) {
	case INFORM:
	    p1.add(new Label(dialog.getInformIcon()));
	    break;
	case WARN:
	    p1.add(new Label(dialog.getWarnIcon()));
	    break;
	case ERROR:
	    p1.add(new Label(dialog.getErrorIcon()));
	    break;
	case CONFIRM:
	    p1.add(new Label(dialog.getQuestionIcon()));
	    break;
	}
	Label label = new Label(message);
	p1.add(label);
	dialog.add(p1, BorderLayout.CENTER);

	Panel p2 = new Panel();

	for (int i = 0; i < labels.length; i++) {
	    Button b = new Button(labels[i]);

	    if (i == defaultIndex)
		b.getVButton().setFocused(true);
	    b.addActionListener(
	    	new DialogActionListener(dialog, selectedValue,
					 (i<values.length ? values[i] : null)));
	    p2.add(b);
	}
	dialog.add(p2, BorderLayout.SOUTH);
	dialog.pack();
	dialog.setVisible(true);

	if (frame == null) dFrame.dispose();
	else dialog.dispose();
	return selectedValue[0];
    }


    // ======== Copyright methods ========

    static protected String KFC_Copyright =
"Copyright (c) 1997, 1998, 1999 Kazuki YASUMATSU.  All Rights Reserved.";

    static protected String KFC_Permission =
"Permission to use, copy, modify, and distribute this software and its\n" +
"documentation for any purpose and without fee or royalty is hereby\n" +
"granted, provided that both the above copyright notice and this\n" +
"permission notice appear in all copies of the software and\n" +
"documentation or portions thereof, including modifications, that you\n" +
"make.\n" +
"\n" +
"THIS SOFTWARE IS PROVIDED \"AS IS,\" AND COPYRIGHT HOLDERS MAKE NO\n" +
"REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED. BY WAY OF EXAMPLE,\n" +
"BUT NOT LIMITATION, COPYRIGHT HOLDERS MAKE NO REPRESENTATIONS OR\n" +
"WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR\n" +
"THAT THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY\n" +
"THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.\n" +
"COPYRIGHT HOLDERS WILL BEAR NO LIABILITY FOR ANY USE OF THIS SOFTWARE\n" +
"OR DOCUMENTATION.";

    static protected Visualizable V_KFC_Copyright;

    static {
	jp.kyasu.graphics.TextBuffer buffer =
				new jp.kyasu.graphics.TextBuffer();
	buffer.setTextStyle(new TextStyle("Monospaced", Font.BOLD, 12));
	buffer.append(KFC_Copyright);
	buffer.setTextStyle(new TextStyle("Monospaced", Font.PLAIN, 12));
	buffer.append("\n\n");
	buffer.append(KFC_Permission);
	Visualizable v =
		new jp.kyasu.graphics.VBorderedWrapper(
			new jp.kyasu.graphics.VColoredWrapper(
				new jp.kyasu.graphics.VRichText(buffer),
				Color.black, Color.white),
			new jp.kyasu.graphics.V3DBorder(false));
	buffer = new jp.kyasu.graphics.TextBuffer();
	buffer.setTextStyle(new TextStyle("SansSerif", Font.BOLD, 14));
	buffer.append("Kazuki YASUMATSU's Foundation Classes");
	buffer.append("\n\n");
	buffer.append(v);
	V_KFC_Copyright = new jp.kyasu.graphics.VRichText(
						buffer, ParagraphStyle.CENTER);
    }

    /**
     * Shows the KFC copyright message with a dialog.
     */
    static public void showKFCCopyright() {
	showKFCCopyright(null);
    }

    /**
     * Shows the KFC copyright message with a dialog.
     *
     * @param frame the owner of the dialog.
     */
    static public void showKFCCopyright(java.awt.Frame frame) {
	message(frame, "KFC Copyright", V_KFC_Copyright);
    }


    // ======== Misc. ========

    /** Creates the visual object from the specified message. */
    static protected Visualizable createVisualizable(String message) {
	if (message == null)
	    throw new NullPointerException();
	Text text = new Text(message);
	if (message.indexOf(Text.LINE_SEPARATOR_CHAR) >= 0) {
	    return new VRichText(text, ParagraphStyle.LEFT);
	}
	else {
	    return new VText(text);
	}
    }


    // ======== Dialog methods ========

    /**
     * Constructs an initially invisible Dialog with an empty title.
     *
     * @param parent the owner of the dialog.
     */
    public Dialog(java.awt.Frame parent) {
	this(parent, "", false);
    }

    /**
     * Constructs an initially invisible Dialog with an empty title.
     * A modal Dialog grabs all the input to the parent frame from the user.
     *
     * @param parent the owner of the dialog.
     * @param modal  if true, dialog blocks input to the parent window when
     *               shown.
     */
    public Dialog(java.awt.Frame parent, boolean modal) {
	this(parent, "", modal);
    }

    /**
     * Constructs an initially invisible Dialog with a title.
     *
     * @param parent the owner of the dialog.
     * @param title  the title of the dialog.
     */
    public Dialog(java.awt.Frame parent, String title) {
	this(parent, title, false);
    }

    /**
     * Constructs an initially invisible Dialog with a title.
     * A modal Dialog grabs all the input to the parent frame from the user.
     *
     * @param parent the owner of the dialog.
     * @param title  the title of the dialog.
     * @param modal  if true, dialog blocks input to the parent window when
     *               shown.
     */
    public Dialog(java.awt.Frame parent, String title, boolean modal) {
	super(parent, title, modal);
	setForeground(AWTResources.FOREGROUND_COLOR);
	setBackground(AWTResources.BACKGROUND_COLOR);
    }


    /**
     * Updates this component.
     */
    public void update(Graphics g) {
	Color save = g.getColor();
	g.setColor(getBackground());
	Rectangle r = g.getClipBounds();
	if (r != null) {
	    g.fillRect(r.x, r.y, r.width, r.height);
	}
	else {
	    Dimension d = getSize();
	    g.fillRect(0, 0, d.width, d.height);
	}
	g.setColor(save);
	paint(g);
    }

    /**
     * Returns the child component of this Window which has focus if and
     * only if this Window is active.
     *
     * @return the component with focus, or null if no children have focus
     *         assigned to them.
     * @see    jp.kyasu.awt.AWTResources#HAS_FOCUS_BUG
     */
    public Component getFocusOwner() {
	if (AWTResources.HAS_FOCUS_BUG) {
	    return AWTResources.getFocusOwnerWorkaround(super.getFocusOwner());
	}
	else {
	    return super.getFocusOwner();
	}
    }

    /**
     * Shows the dialog. This will bring the dialog to the front
     * if the dialog is already visible.
     */
    public void show() {
	synchronized (getTreeLock()) {
	    Component c = getFocusRequestComponent(this);
	    if (c != null) {
		c.requestFocus();
	    }
	}
	Point p = getLocation();
	if (p.x == 0 && p.y == 0 && getParent().isShowing()) {
	    p = getParent().getLocationOnScreen();
	    Dimension pd = getParent().getSize();
	    Dimension d = getSize();
	    setLocation(p.x + ((pd.width - d.width) / 2),
			p.y + ((pd.height - d.height) / 2));
	}
	super.show();
    }

    /** Returns the component that requests the focus. */
    protected Component getFocusRequestComponent(Component c) {
	if (c instanceof Container) {
	    Container cont = (Container)c;
	    int count = cont.getComponentCount();
	    for (int i = 0; i < count; i++) {
		c = getFocusRequestComponent(cont.getComponent(i));
		if (c != null) {
		    return c;
		}
	    }
	    return null;
	}
	else {
	    if (c.isEnabled() && c.isFocusTraversable())
		return c;
	    else
		return null;
	}
    }


    /** Executes the examples. */
    public static void main(String arg[]) {
	Dialog.inform("Beware,\nthis is an information");
	Dialog.warn("Beware,\nthis is a warning");
	Dialog.error("Beware,\nthis is an error");

	if (Dialog.confirm("Are you OK?")) {
	    System.out.println("OK");
	}
	else {
	    System.out.println("Not OK");
	}

	System.out.println(
		Dialog.choice(
		    MESSAGE,
		    null,
		    "Choice",
		    "Are you tired?",
		    new String[]{ "absolutely", "sort of", "not really" },
		    new Object[]{ new Boolean(true), new Boolean(false), null },
		    0));

	System.out.println(Dialog.request("Enter your name:", 20));
	System.out.println(Dialog.request("Enter your name:",
					  "Kazuki Yasumatsu"));
	Dialog.showKFCCopyright();
	System.exit(0);
    }
}


/**
 * An ActionListener for the dialog utilities.
 */
class DialogActionListener implements ActionListener {
    Dialog dialog;
    Object valueHolder[];
    Object defaultValue      = null;
    boolean hasDefaultValue  = false;
    TextField field          = null;

    DialogActionListener(Dialog dialog, Object valueHolder[]) {
	this.dialog      = dialog;
	this.valueHolder = valueHolder;
    }

    DialogActionListener(Dialog dialog, Object valueHolder[],
			 Object defaultValue)
    {
	this.dialog       = dialog;
	this.valueHolder  = valueHolder;
	this.defaultValue = defaultValue;
	hasDefaultValue   = true;
    }

    DialogActionListener(Dialog dialog, Object valueHolder[], TextField field)
    {
	this.dialog      = dialog;
	this.valueHolder = valueHolder;
	this.field       = field;
    }

    public void actionPerformed(ActionEvent e) {
	if (hasDefaultValue) {
	    valueHolder[0] = defaultValue;
	}
	else if (field != null) {
	    valueHolder[0] = field.getText();
	}
	else {
	    valueHolder[0] = e.getActionCommand();
	}
	dialog.setVisible(false);
    }
}
