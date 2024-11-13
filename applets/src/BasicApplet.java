/*
 * BasicApplet.java
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

package applets; // Make security happy

import jp.kyasu.awt.*;
import jp.kyasu.graphics.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * An example of KFC basic components.
 *
 * @version 	08 Mar 1998
 * @author 	Kazuki YASUMATSU
 */
public class BasicApplet extends Applet {

    public String getAppletInfo() {
	return "An example of KFC basic components";
    }

    public void init() {
	add(createBasicComponents(), BorderLayout.CENTER);
    }


    protected Component createBasicComponents() {
	Component button   = createTitledPanel("Button", createButtons());
	Component toggle   = createTitledPanel("ToggleButton",
							createToggleButtons());
	Component checkbox = createTitledPanel("Checkbox", createCheckboxes());
	Component choice   = createTitledPanel("Choice", createChoices());
	Component list     = createTitledPanel("List", createList());

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();

	Panel p = new Panel();
	p.setLayout(gridbag);

	c.anchor = GridBagConstraints.WEST;
	c.gridx = 0;
	c.gridy = 0;
	gridbag.setConstraints(button, c);
	p.add(button);

	c.gridx = 0;
	c.gridy = 1;
	gridbag.setConstraints(toggle, c);
	p.add(toggle);

	c.gridx = 0;
	c.gridy = 2;
	gridbag.setConstraints(checkbox, c);
	p.add(checkbox);

	c.gridx = 0;
	c.gridy = 3;
	gridbag.setConstraints(choice, c);
	p.add(choice);

	c.anchor = GridBagConstraints.CENTER;
	c.gridx = 1;
	c.gridy = 0;
	c.gridheight = 4;
	c.fill = GridBagConstraints.VERTICAL;
	gridbag.setConstraints(list, c);
	p.add(list);

	Panel pp = new Panel();
	pp.setLayout(new FlowLayout(0));
	pp.add(p);

	return pp;
    }

    protected Component createButtons() {
	Button b1 = new Button("button");
	b1.setToolTipText("button");

	Text text = new Text("button",
			     TextStyle.DEFAULT_STYLE.deriveBoldStyle());
	Button b2 = new Button(text);
	b2.setToolTipText("button");

	Text text2 = new Text(createVImage("mini-penguin.gif")).append(text);
	Button b3 = new Button(text2);
	b3.setToolTipText("button");

	Button b4 = new Button(new VActiveButton(text2));
	b4.setToolTipText("button");

	Panel p = new Panel();
	p.add(b1);
	p.add(b2);
	p.add(b3);
	p.add(b4);
	return p;
    }

    protected Component createToggleButtons() {
	ToggleButton b1 = new ToggleButton("toggle");
	b1.setToolTipText("toggle button");

	BooleanStateGroup g = new BooleanStateGroup();

	TextStyle bold = TextStyle.DEFAULT_STYLE.deriveBoldStyle();
	TextBuffer buffer = new TextBuffer();
	buffer.setTextStyle(bold);
	buffer.append(createVImage("mini-java.gif"));
	buffer.append("Java");
	ToggleButton b2 = new ToggleButton(buffer.toText(), true, g);
	b2.setToolTipText("Java");

	buffer = new TextBuffer();
	buffer.setTextStyle(bold);
	buffer.append(createVImage("mini-penguin.gif"));
	buffer.append("Linux");
	ToggleButton b3 = new ToggleButton(buffer.toText(), false, g);
	b3.setToolTipText("Linux");

	Panel p = new Panel();
	p.add(b1);
	p.add(b2);
	p.add(b3);
	return p;
    }

    protected Component createCheckboxes() {
	Checkbox c1 = new Checkbox("check box");
	c1.setToolTipText("check box");
	CheckboxGroup g = new CheckboxGroup();

	TextStyle bold = TextStyle.DEFAULT_STYLE.deriveBoldStyle();
	TextBuffer buffer = new TextBuffer();
	buffer.setTextStyle(bold);
	buffer.append(createVImage("mini-java.gif"));
	buffer.append("Java");
	Checkbox c2 = new Checkbox(buffer.toText(), true, g);
	c2.setToolTipText("Java");

	buffer = new TextBuffer();
	buffer.setTextStyle(bold);
	buffer.append(createVImage("mini-penguin.gif"));
	buffer.append("Linux");
	Checkbox c3 = new Checkbox(buffer.toText(), false, g);
	c3.setToolTipText("Linux");

	Panel p = new Panel();
	p.add(c1);
	p.add(c2);
	p.add(c3);
	return p;
    }

    protected Component createChoices() {
	Choice choice1 = new Choice();
	choice1.setToolTipText("Color Choice");
	choice1.add(createColorText(Color.black,     "Black"));
	choice1.add(createColorText(Color.red,       "Red"));
	choice1.add(createColorText(Color.pink,      "Pink"));
	choice1.add(createColorText(Color.orange,    "Orange"));
	choice1.add(createColorText(Color.yellow,    "Yellow"));
	choice1.add(createColorText(Color.green,     "Green"));
	choice1.add(createColorText(Color.magenta,   "Magenta"));
	choice1.add(createColorText(Color.cyan,      "Cyan"));
	choice1.add(createColorText(Color.blue,      "Blue"));
	choice1.add(createColorText(Color.darkGray,  "DarkGray"));
	choice1.add(createColorText(Color.gray,      "Gray"));
	choice1.add(createColorText(Color.lightGray, "LightGray"));
	choice1.add(createColorText(Color.white,     "White"));

	TextStyle plain = TextStyle.DEFAULT_STYLE;
	TextStyle bold = TextStyle.DEFAULT_STYLE.deriveBoldStyle();

	Text dir = new Text(createVImage("mini-folder.gif")).append(
			new Text("Directory", bold));
	Text file1 = new Text("\t").append(
		    new Text(createVImage("mini-doc.gif")).append(
			new Text("file 1", plain)));
	Text file2 = new Text("\t").append(
		    new Text(createVImage("mini-doc.gif")).append(
			new Text("file 2", plain)));
	Text file3 = new Text("\t").append(
		    new Text(createVImage("mini-doc.gif")).append(
			new Text("file 3", plain)));
	Text file4 = new Text("\t").append(
		    new Text(createVImage("mini-doc.gif")).append(
			new Text("file 4", plain)));
	Choice choice2 = new Choice();
	choice2.setToolTipText("File Choice");
	choice2.add(dir);
	choice2.add(file1);
	choice2.add(file2);
	choice2.add(file3);
	choice2.add(file4);

	Panel p = new Panel();
	p.add(choice1);
	p.add(choice2);

	return p;
    }

    protected Component createList() {
	List list = new List();

	TextStyle plain = TextStyle.DEFAULT_STYLE;
	TextStyle bold = TextStyle.DEFAULT_STYLE.deriveBoldStyle();

	Text dir = new Text(createVImage("mini-folder.gif")).append(
			new Text("Directory", bold));
	Text file1 = new Text("\t").append(
		    new Text(createVImage("mini-doc.gif")).append(
			new Text("file 1", plain)));
	Text file2 = new Text("\t").append(
		    new Text(createVImage("mini-doc.gif")).append(
			new Text("file 2", plain)));
	Text file3 = new Text("\t").append(
		    new Text(createVImage("mini-doc.gif")).append(
			new Text("file 3", plain)));
	Text file4 = new Text("\t").append(
		    new Text(createVImage("mini-doc.gif")).append(
			new Text("file 4", plain)));

	for (int i = 0; i < 4; i++) {
	    list.addItem(dir);
	    list.addItem(file1);
	    list.addItem(file2);
	    list.addItem(file3);
	    list.addItem(file4);
	}

	return list;
    }

    protected Text createColorText(Color color, String name) {
	TextBuffer buffer = new TextBuffer();
	if (!color.equals(Color.white)) {
	    buffer.append(new VColoredWrapper(new VRectangle(10, 10), color));
	}
	else {
	    buffer.append(new VColoredWrapper(
				new VRectangle(10, 10, VRectangle.OUTLINE),
				Color.black, Color.white));
	}
	buffer.append(name);
	return buffer.toText();
    }

    protected Component createTitledPanel(String title, Component comp) {
	BorderedPanel bp = new BorderedPanel(new VTitledPaneBorder(title));
	bp.add(comp, BorderLayout.CENTER);
	return bp;
    }

    protected VImage createVImage(String name) {
	return new VImage(getImage(getCodeBase(), "applets/images/" + name));
    }
}
