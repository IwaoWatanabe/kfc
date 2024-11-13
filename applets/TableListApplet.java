/*
 * TableListApplet.java
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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;

/**
 *  An example of KFC TableList.
 *
 * @version 	24 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class TableListApplet extends Applet
	implements ActionListener, ItemListener
{

    public String getAppletInfo() {
	return "An example of KFC TableList";
    }

    public void init() {
	add(createTableList(), java.awt.BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
	System.out.println(e.paramString());
    }

    public void itemStateChanged(ItemEvent e) {
	System.out.println(e.paramString());
    }


    protected TableList createTableList() {
	TableList table =
		new TableList(8,
			      new String[]{ "Date", "Sender", "Subject" },
			      new int[]{ 60, 150, 200 });
	table.setSelectionMode(TableList.SHIFT_MULTIPLE_SELECTIONS);

	String mail1[] = { "98/01/01", "Kazuki Yasumatsu", "A Happy New Year!" };
	String mail2[] = { "98/01/02", "kyasu",            "I'm so busy" };
	String mail3[] = { "98/01/03", "Gon Nakayama",     "Gon Goal!" };

	table.setItems(new String[][]{ mail1, mail2, mail3 });

	Text mail4[] = new Text[]{
	    new Text("98/01/04 "),
	    new Text("Kazuki Yasumatsu",
		    new TextStyle("SansSerif", Font.BOLD, 12)),
	    new Text(createVImage("mini-java.gif")).append(
	    	new Text("KFC version 1.0 released ",
		    new TextStyle("SansSerif", Font.ITALIC, 12, Color.red))),
	};
	table.addItem(mail4);

	for (int i = 0; i < 4; i++) {
	    table.addItem(mail1);
	    table.addItem(mail2);
	    table.addItem(mail3);
	    table.addItem(mail4);
	}

	table.addActionListener(this);
	table.addItemListener(this);

	return table;
    }

    protected VImage createVImage(String filename) {
	return new VImage(getImage(getCodeBase(), "applets/images/" + filename));
    }
}
