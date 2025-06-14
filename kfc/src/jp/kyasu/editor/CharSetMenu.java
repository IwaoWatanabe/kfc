/*
 * CharSetMenu.java
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

package jp.kyasu.editor;

/**
 * The <code>CharSetMenu</code> allows a user to select a character set.
 *
 * @version 	20 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class CharSetMenu extends SelectionMenu {

    static public final String CHARSET_LABEL =
	EditorResources.getResourceString("charset");

    static public final String WESTERN_EUROPE_CHARSET_LABEL =
	EditorResources.getResourceString("westernEuropeCharset");
    static public final String EASTERN_EUROPE_CHARSET_LABEL =
	EditorResources.getResourceString("easternEuropeCharset");
    static public final String CYRILLIC_CHARSET_LABEL =
	EditorResources.getResourceString("cyrillicCharset");
    static public final String GREEK_CHARSET_LABEL =
	EditorResources.getResourceString("greekCharset");
    static public final String THAI_CHARSET_LABEL =
	EditorResources.getResourceString("thaiCharset");
    static public final String TURKISH_CHARSET_LABEL =
	EditorResources.getResourceString("turkishCharset");
    static public final String JAPANESE_CHARSET_LABEL =
	EditorResources.getResourceString("japaneseCharset");
    static public final String CHINESE_CHARSET_LABEL =
	EditorResources.getResourceString("chineseCharset");
    static public final String KOREAN_CHARSET_LABEL =
	EditorResources.getResourceString("koreanCharset");
    static public final String BALTIC_CHARSET_LABEL =
	EditorResources.getResourceString("balticCharset");
    static public final String ICELANDIC_CHARSET_LABEL =
	EditorResources.getResourceString("icelandicCharset");
    static public final String OTHER_CHARSET_LABEL =
	EditorResources.getResourceString("otherCharset");

    static protected final String Charsets[][] = {
	// westernEuropeCharset
	{ "8859_1", "MacRoman", "Cp1252", "Cp850" },
	// easternEuropeCharset
	{ "8859_2", "Cp1250", "Cp852" },
	// cyrillicCharset
	{ "8859_5", "Cp1251", "MacCyrillic", "Cp855", "Cp866" },
	// greekCharset
	{ "8859_7", "Cp1253", "MacGreek", "Cp737", "Cp869" },
	// thaiCharset
	{ "Cp874", "MacThai" },
	// turkishCharset
	{ "8859_9", "Cp1254", "MacTurkish", "Cp857" },
	// japaneseCharset
	{ "JISAutoDetect", "EUCJIS", "JIS", "SJIS" },
	// chineseCharset
	{ "Big5", "CNS11643", "GB2312" },
	// koreanCharset
	{ "KSC5601" },
	// balticCharset
	{ "8859_4", "Cp1257", "Cp775" },
	// icelandicCharset
	{ "MacIceland", "Cp861" },
	// otherCharset
	{ "8859_3",
	  "MacCroatian", "MacRomania", "MacUkraine",
	  "Cp860", "Cp863", "Cp865",
	  "MacCentralEurope",
	  "UTF8",
	  "Default"
	}
    };


    /**
     * Constructs a character set menu with the default label and the default
     * read character set.
     */
    public CharSetMenu() {
	this(CHARSET_LABEL);
    }

    /**
     * Constructs a character set menu with the label and the default read
     * character set.
     */
    public CharSetMenu(String label) {
	this(label, EditorResources.DEFAULT_READ_CHARSET);
    }

    /**
     * Constructs a character set menu with the label and initial character
     * set.
     */
    public CharSetMenu(String label, String initialCharset) {
	super(label);
	initializeSubMenus();
	select(initialCharset);
    }


    /**
     * Returns the selected character set.
     */
    public String getSelectedCharcterSet() {
	return getSelectedCommand();
    }


    protected void initializeSubMenus() {
	add(createSubMenu(WESTERN_EUROPE_CHARSET_LABEL, Charsets[0]));
	add(createSubMenu(EASTERN_EUROPE_CHARSET_LABEL, Charsets[1]));
	add(createSubMenu(CYRILLIC_CHARSET_LABEL,       Charsets[2]));
	add(createSubMenu(GREEK_CHARSET_LABEL,          Charsets[3]));
	add(createSubMenu(THAI_CHARSET_LABEL,           Charsets[4]));
	add(createSubMenu(TURKISH_CHARSET_LABEL,        Charsets[5]));
	add(createSubMenu(JAPANESE_CHARSET_LABEL,       Charsets[6]));
	add(createSubMenu(CHINESE_CHARSET_LABEL,        Charsets[7]));
	add(createSubMenu(KOREAN_CHARSET_LABEL,         Charsets[8]));
	add(createSubMenu(BALTIC_CHARSET_LABEL,         Charsets[9]));
	add(createSubMenu(ICELANDIC_CHARSET_LABEL,      Charsets[10]));
	add(createSubMenu(OTHER_CHARSET_LABEL,          Charsets[11]));
    }

    protected SelectionMenu createSubMenu(String label, String charsets[]) {
	SelectionMenu menu = new SelectionMenu(label);
	for (int i = 0; i < charsets.length; i++) {
	    String charset = charsets[i];
	    String sublabel = EditorResources.getResourceString(charset);
	    menu.add(sublabel, charset);
	}
	return menu;
    }


    /** Executes the examples. */
    public static void main(String args[]) {
	CharSetMenu charsetMenu = new CharSetMenu();
	charsetMenu.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent e) {
		System.out.println(e.getActionCommand());
	    }
	});
	java.awt.MenuBar menuBar = new java.awt.MenuBar();
	menuBar.add(charsetMenu);
	jp.kyasu.awt.Frame f = new jp.kyasu.awt.Frame("CharSetMenu");
	f.setMenuBar(menuBar);
	f.setSize(100, 100);
	f.setVisible(true);
    }
}
