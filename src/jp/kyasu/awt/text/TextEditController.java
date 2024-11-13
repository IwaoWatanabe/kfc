/*
 * TextEditController.java
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

package jp.kyasu.awt.text;

import jp.kyasu.awt.Dialog;
import jp.kyasu.awt.TextEditModel;
import jp.kyasu.graphics.RichText;
import jp.kyasu.graphics.ParagraphStyle;
import jp.kyasu.graphics.Text;
import jp.kyasu.graphics.text.TextPositionInfo;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.text.BreakIterator;
import java.text.CharacterIterator;

/**
 * The <code>TextEditController</code> class implements a controller
 * of a MVC model for the text editing. The model of the MVC model is a
 * <code>TextEditModel</code> object and the view of the MVC model is a
 * <code>TextEditView</code> object.
 * <p>
 * The <code>BasicTextEditController</code> class (a superclass of this class)
 * implements basic operations for the text editing. The
 * <code>TextEditController</code> class implements full operations for the
 * text editing.
 *
 * @see		jp.kyasu.awt.TextEditModel
 * @see		jp.kyasu.awt.text.TextEditView
 * @see		jp.kyasu.awt.text.BasicTextEditController
 *
 * @version 	15 Dec 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextEditController extends BasicTextEditController {
    protected int softTab;
    protected KeyAction availabelKeyActions[];


    /**
     * The default key map.
     */
    static protected final Keymap DEFAULT_KEYMAP = new Keymap();

    /**
     * Returns the default key map.
     */
    static public Keymap getDefaultKeymap() {
	return (Keymap)DEFAULT_KEYMAP.clone();
    }

    /*
     * Initializes the default key map.
     */
    static {
	DEFAULT_KEYMAP.setDefaultActionName("insert-character");

	DEFAULT_KEYMAP.setKeyCharMap('\001',
	    getResourceString("kfc.text.ctrlAKey", "beginning-of-line"));
	DEFAULT_KEYMAP.setKeyCharMap('\002',
	    getResourceString("kfc.text.ctrlBKey", "backward-character"));
	DEFAULT_KEYMAP.setKeyCharMap('\003',
	    getResourceString("kfc.text.ctrlCKey", "copy-clipboard"));
	DEFAULT_KEYMAP.setKeyCharMap('\004',
	    getResourceString("kfc.text.ctrlDKey", "delete-next-character"));
	DEFAULT_KEYMAP.setKeyCharMap('\005',
	    getResourceString("kfc.text.ctrlEKey", "end-of-line"));
	DEFAULT_KEYMAP.setKeyCharMap('\006',
	    getResourceString("kfc.text.ctrlFKey", "forward-character"));
	DEFAULT_KEYMAP.setKeyCharMap('\007',
	    getResourceString("kfc.text.ctrlGKey", "process-cancel"));
	/*
	DEFAULT_KEYMAP.setKeyCharMap('\b',
	    getResourceString("kfc.text.ctrlHKey", "delete-previous-character"));
	DEFAULT_KEYMAP.setKeyCharMap('\t',
	    getResourceString("kfc.text.ctrlIKey", "tab"));
	DEFAULT_KEYMAP.setKeyCharMap('\n',
	    getResourceString("kfc.text.ctrlJKey", "newline"));
	*/
	DEFAULT_KEYMAP.setKeyCharMap('\013',
	    getResourceString("kfc.text.ctrlKKey", "kill-to-end-of-line"));
	DEFAULT_KEYMAP.setKeyCharMap('\f',
	    getResourceString("kfc.text.ctrlLKey", "redraw-display"));
	/*
	DEFAULT_KEYMAP.setKeyCharMap('\r',
	    getResourceString("kfc.text.ctrlMKey", "newline"));
	*/
	DEFAULT_KEYMAP.setKeyCharMap('\016',
	    getResourceString("kfc.text.ctrlNKey", "next-line"));
	DEFAULT_KEYMAP.setKeyCharMap('\017',
	    getResourceString("kfc.text.ctrlOKey", "newline backward-character"));
	DEFAULT_KEYMAP.setKeyCharMap('\020',
	    getResourceString("kfc.text.ctrlPKey", "previous-line"));
	DEFAULT_KEYMAP.setKeyCharMap('\021',
	    getResourceString("kfc.text.ctrlQKey", "beep"));
	DEFAULT_KEYMAP.setKeyCharMap('\022',
	    getResourceString("kfc.text.ctrlRKey", "beep"));
	DEFAULT_KEYMAP.setKeyCharMap('\023',
	    getResourceString("kfc.text.ctrlSKey", "find-word"));
	DEFAULT_KEYMAP.setKeyCharMap('\024',
	    getResourceString("kfc.text.ctrlTKey", "beep"));
	DEFAULT_KEYMAP.setKeyCharMap('\025',
	    getResourceString("kfc.text.ctrlUKey", "undo"));
	DEFAULT_KEYMAP.setKeyCharMap('\026',
	    getResourceString("kfc.text.ctrlVKey", "paste-clipboard"));
	DEFAULT_KEYMAP.setKeyCharMap('\027',
	    getResourceString("kfc.text.ctrlWKey", "cut-clipboard"));
	DEFAULT_KEYMAP.setKeyCharMap('\030',
	    getResourceString("kfc.text.ctrlXKey", "cut-clipboard"));
	DEFAULT_KEYMAP.setKeyCharMap('\031',
	    getResourceString("kfc.text.ctrlYKey", "paste-clipboard"));
	DEFAULT_KEYMAP.setKeyCharMap('\032',
	    getResourceString("kfc.text.ctrlZKey", "beep"));

	/*
	DEFAULT_KEYMAP.setKeyCharMap('\177',
	    getResourceString("kfc.text.delKey", "delete-next-character"));
	*/

	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_PAGE_UP,
	    getResourceString("kfc.text.pageUpKey", "previous-page"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_PAGE_DOWN,
	    getResourceString("kfc.text.pageDownKey", "next-page"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_END,
	    getResourceString("kfc.text.endKey", "end-of-file"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_HOME,
	    getResourceString("kfc.text.homeKey", "beginning-of-file"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_LEFT,
	    getResourceString("kfc.text.leftKey", "backward-character"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_UP,
	    getResourceString("kfc.text.upKey", "previous-line"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_RIGHT,
	    getResourceString("kfc.text.rightKey", "forward-character"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_DOWN,
	    getResourceString("kfc.text.downKey", "next-line"));

	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_BACK_SPACE,
	    getResourceString("kfc.text.ctrlHKey","delete-previous-character"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_TAB,
	    getResourceString("kfc.text.ctrlIKey", "tab"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_ENTER,
	    getResourceString("kfc.text.ctrlJKey", "newline"));
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_DELETE,
	    getResourceString("kfc.text.delKey", "delete-next-character"));

	/*
	DEFAULT_KEYMAP.setMetaAltKeyCodeMap(KeyEvent.VK_B, "backward-word");
	DEFAULT_KEYMAP.setMetaAltKeyCodeMap(KeyEvent.VK_D, "kill-next-word");
	DEFAULT_KEYMAP.setMetaAltKeyCodeMap(KeyEvent.VK_F, "forward-word");
	DEFAULT_KEYMAP.setMetaAltKeyCodeMap(KeyEvent.VK_H,"kill-previous-word");
	DEFAULT_KEYMAP.setMetaAltKeyCodeMap(KeyEvent.VK_DELETE,
							  "kill-next-word");
	*/

	/*
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_ENTER, Event.SHIFT_MASK,
						"newbreak");
	*/
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_ENTER, Event.SHIFT_MASK,
						"newline-and-indent");
	DEFAULT_KEYMAP.setKeyCodeMap(KeyEvent.VK_TAB, Event.SHIFT_MASK,
						"insert-character");
    }


    /**
     * Constructs a text edit controller with the specified text edit view.
     *
     * @param view the text edit view.
     */
    public TextEditController(TextEditView view) {
	super(view);
	keyBinding = new KeyBinding(getDefaultKeymap());
	keyBinding.setKeyActions(getAvailableKeyActions());
	softTab = 0;
    }

    /**
     * Checks if this controller allows soft tab.
     * @see #clearSoftTab()
     * @see #getSoftTab()
     * @see #setSoftTab(int)
     */
    public boolean isSoftTab() {
	return softTab > 0;
    }

    /**
     * Returns the length of soft tab.
     * @return the length of soft tab. if the length is less than 0, soft tab
     *         is not allowed by this controller.
     * @see #setSoftTab(int)
     * @see #isSoftTab()
     * @see #clearSoftTab()
     */
    public int getSoftTab() {
	return softTab;
    }

    /**
     * Sets the length of soft tab.
     * @param i the length of soft tab. if the length is less than 0, disables
     *          soft tab.
     * @see #getSoftTab()
     * @see #isSoftTab()
     * @see #clearSoftTab()
     */
    public void setSoftTab(int i) {
	softTab = i;
    }

    /**
     * Disables soft tab.
     * @see #isSoftTab()
     * @see #getSoftTab()
     * @see #setSoftTab(int)
     */
    public void clearSoftTab() {
	softTab = 0;
    }


    // ================ KeyActions ================

    /**
     * Returns the available key action objects in this controller.
     */
    public KeyAction[] getAvailableKeyActions() {
	if (availabelKeyActions != null) {
	    return availabelKeyActions;
	}
	return availabelKeyActions = new KeyAction []{
	    new BackwardChar(),
	    new BackwardWord(),
	    new Beep(),
	    new BiginningOfFile(),
	    new BiginningOfLine(),
	    new CopyClipboard(),
	    new CutClipboard(),
	    new DeleteNextChar(),
	    new DeleteNextWord(),
	    new DeletePrevChar(),
	    new DeletePrevWord(),
	    new DeleteSelection(),
	    new DeleteToEndOfLine(),
	    new DeleteToStartOfLine(),
	    new DeselectAll(),
	    new DoNothing(),
	    new EndOfFile(),
	    new EndOfLine(),
	    new FindWord(),
	    new ForwardChar(),
	    new ForwardWord(),
	    new GotoLine(),
	    new InsertChar(),
	    new KillNextChar(),
	    new KillNextWord(),
	    new KillPrevChar(),
	    new KillPrevWord(),
	    new KillSelection(),
	    new KillToEndOfLine(),
	    new KillToStartOfLine(),
	    new NewBreak(),
	    new NewLine(),
	    new NewLineAndIndent(),
	    new NextLine(),
	    new NextPage(),
	    new PasteCutbuffer(),
	    new PasteClipboard(),
	    new PrevLine(),
	    new PrevPage(),
	    new Redraw(),
	    new SelectAll(),
	    new SelectLine(),
	    new SelectWord(),
	    new ShowMatch(),
	    new Tab(),
	    new Undo(),
	    new Unkill(),
	    new ShowDebugInfo()
	};
    }

    class BackwardChar implements KeyAction, java.io.Serializable {
	public String getName()           { return "backward-character"; }
	public void perform(char keyChar) { backward_character(); }
    }
    class BackwardWord implements KeyAction, java.io.Serializable {
	public String getName()           { return "backward-word"; }
	public void perform(char keyChar) { backward_word(); }
    }
    class Beep implements KeyAction, java.io.Serializable {
	public String getName()           { return "beep"; }
	public void perform(char keyChar) { beep(); }
    }
    class BiginningOfFile implements KeyAction, java.io.Serializable {
	public String getName()           { return "beginning-of-file"; }
	public void perform(char keyChar) { beginning_of_file(); }
    }
    class BiginningOfLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "beginning-of-line"; }
	public void perform(char keyChar) { beginning_of_line(); }
    }
    class CopyClipboard implements KeyAction, java.io.Serializable {
	public String getName()           { return "copy-clipboard"; }
	public void perform(char keyChar) { copy_clipboard(); }
    }
    class CutClipboard implements KeyAction, java.io.Serializable {
	public String getName()           { return "cut-clipboard"; }
	public void perform(char keyChar) { cut_clipboard(); }
    }
    class DeleteNextChar implements KeyAction, java.io.Serializable {
	public String getName()           { return "delete-next-character"; }
	public void perform(char keyChar) { delete_next_character(); }
    }
    class DeleteNextWord implements KeyAction, java.io.Serializable {
	public String getName()           { return "delete-next-word"; }
	public void perform(char keyChar) { delete_next_word(); }
    }
    class DeletePrevChar implements KeyAction, java.io.Serializable {
	public String getName() { return "delete-previous-character"; }
	public void perform(char keyChar) { delete_previous_character(); }
    }
    class DeletePrevWord implements KeyAction, java.io.Serializable {
	public String getName()           { return "delete-previous-word"; }
	public void perform(char keyChar) { delete_previous_word(); }
    }
    class DeleteSelection implements KeyAction, java.io.Serializable {
	public String getName()           { return "delete-selection"; }
	public void perform(char keyChar) { delete_selection(); }
    }
    class DeleteToEndOfLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "delete-to-end-of-line"; }
	public void perform(char keyChar) { delete_to_end_of_line(); }
    }
    class DeleteToStartOfLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "delete-to-start-of-line"; }
	public void perform(char keyChar) { delete_to_start_of_line(); }
    }
    class DeselectAll implements KeyAction, java.io.Serializable {
	public String getName()           { return "deselect-all"; }
	public void perform(char keyChar) { deselect_all(); }
    }
    class DoNothing implements KeyAction, java.io.Serializable {
	public String getName()           { return "do-nothing"; }
	public void perform(char keyChar) { do_nothing(); }
    }
    class EndOfFile implements KeyAction, java.io.Serializable {
	public String getName()           { return "end-of-file"; }
	public void perform(char keyChar) { end_of_file(); }
    }
    class EndOfLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "end-of-line"; }
	public void perform(char keyChar) { end_of_line(); }
    }
    class FindWord implements KeyAction, java.io.Serializable {
	public String getName()           { return "find-word"; }
	public void perform(char keyChar) { find_word(); }
    }
    class ForwardChar implements KeyAction, java.io.Serializable {
	public String getName()           { return "forward-character"; }
	public void perform(char keyChar) { forward_character(); }
    }
    class ForwardWord implements KeyAction, java.io.Serializable {
	public String getName()           { return "forward-word"; }
	public void perform(char keyChar) { forward_word(); }
    }
    class GotoLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "goto-line"; }
	public void perform(char keyChar) { goto_line(); }
    }
    class InsertChar implements KeyAction, java.io.Serializable {
	public String getName()           { return "insert-character"; }
	public void perform(char keyChar) { insert_character(keyChar); }
    }
    class KillNextChar implements KeyAction, java.io.Serializable {
	public String getName()           { return "kill-next-character"; }
	public void perform(char keyChar) { kill_next_character(); }
    }
    class KillNextWord implements KeyAction, java.io.Serializable {
	public String getName()           { return "kill-next-word"; }
	public void perform(char keyChar) { kill_next_word(); }
    }
    class KillPrevChar implements KeyAction, java.io.Serializable {
	public String getName()           { return "kill-previous-character"; }
	public void perform(char keyChar) { kill_previous_character(); }
    }
    class KillPrevWord implements KeyAction, java.io.Serializable {
	public String getName()           { return "kill-previous-word"; }
	public void perform(char keyChar) { kill_previous_word(); }
    }
    class KillSelection implements KeyAction, java.io.Serializable {
	public String getName()           { return "kill-selection"; }
	public void perform(char keyChar) { kill_selection(); }
    }
    class KillToEndOfLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "kill-to-end-of-line"; }
	public void perform(char keyChar) { kill_to_end_of_line(); }
    }
    class KillToStartOfLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "kill-to-start-of-line"; }
	public void perform(char keyChar) { kill_to_start_of_line(); }
    }
    class NewBreak implements KeyAction, java.io.Serializable {
	public String getName()           { return "newbreak"; }
	public void perform(char keyChar) { newbreak(); }
    }
    class NewLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "newline"; }
	public void perform(char keyChar) { newline(); }
    }
    class NewLineAndIndent implements KeyAction, java.io.Serializable {
	public String getName()           { return "newline-and-indent"; }
	public void perform(char keyChar) { newline_and_indent(); }
    }
    class NextLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "next-line"; }
	public void perform(char keyChar) { next_line(); }
    }
    class NextPage implements KeyAction, java.io.Serializable {
	public String getName()           { return "next-page"; }
	public void perform(char keyChar) { next_page(); }
    }
    class PasteCutbuffer implements KeyAction, java.io.Serializable {
	public String getName()           { return "paste-cutbuffer"; }
	public void perform(char keyChar) { paste_cutbuffer(); }
    }
    class PasteClipboard implements KeyAction, java.io.Serializable {
	public String getName()           { return "paste-clipboard"; }
	public void perform(char keyChar) { paste_clipboard(); }
    }
    class PrevLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "previous-line"; }
	public void perform(char keyChar) { previous_line(); }
    }
    class PrevPage implements KeyAction, java.io.Serializable {
	public String getName()           { return "previous-page"; }
	public void perform(char keyChar) { previous_page(); }
    }
    class Redraw implements KeyAction, java.io.Serializable {
	public String getName()           { return "redraw-display"; }
	public void perform(char keyChar) { redraw_display(); }
    }
    class SelectAll implements KeyAction, java.io.Serializable {
	public String getName()           { return "select-all"; }
	public void perform(char keyChar) { select_all(); }
    }
    class SelectLine implements KeyAction, java.io.Serializable {
	public String getName()           { return "select-line"; }
	public void perform(char keyChar) { select_line(); }
    }
    class SelectWord implements KeyAction, java.io.Serializable {
	public String getName()           { return "select-word"; }
	public void perform(char keyChar) { select_word(); }
    }
    class ShowMatch implements KeyAction, java.io.Serializable {
	public String getName()           { return "show-match"; }
	public void perform(char keyChar) { show_match(keyChar); }
    }
    class Tab implements KeyAction, java.io.Serializable {
	public String getName()           { return "tab"; }
	public void perform(char keyChar) { tab(); }
    }
    class Undo implements KeyAction, java.io.Serializable {
	public String getName()           { return "undo"; }
	public void perform(char keyChar) { undo(); }
    }
    class Unkill implements KeyAction, java.io.Serializable {
	public String getName()           { return "unkill"; }
	public void perform(char keyChar) { unkill(); }
    }
    class ShowDebugInfo implements KeyAction, java.io.Serializable {
	public String getName()           { return "show-debug-info"; }
	public void perform(char keyChar) { view.layout.printDebugInfo(); }
    }

    /**
     * Moves the insertion cursor one character to the left.
     */
    public void backward_character() {
	if (view.selectionIsCaret()) {
	    TextPositionInfo posInfo = view.getSelectionBegin();
	    if (posInfo.textIndex <= 0) {
		return;
	    }
	    view.hideSelection();
	    setSelectionBeginEnd(view.getTextPositionPrevTo(posInfo));
	}
	else {
	    view.hideSelection();
	    setSelectionBeginEnd(view.getSelectionBegin());
	}
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Moves the insertion cursor to the first non-whitespace character
     * after the first whitespace character to the left or the beginning of
     * the line.  If the insertion cursor is already at the beginning of a
     * word, moves the insertion cursor to the beginning of the previous word.
     */
    public void backward_word() {
	Text text = model.getRichText().getText();
	TextPositionInfo posInfo = view.getSelectionBegin();
	int index = getPrevWordIndex(posInfo);
	if (index < 0) {
	    return;
	}
	view.hideSelection();
	setSelectionBeginEnd(view.getTextPositionNearby(posInfo, index));
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Causes the terminal to beep.
     */
    public void beep() {
	view.getToolkit().beep();
    }

    /**
     * Moves the insertion cursor to the beginning of the text.
     */
    public void beginning_of_file() {
	view.hideSelection();
	setSelectionBeginEnd(view.getTextPositionAt(0));
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Moves the insertion cursor to the beginning of the line.
     */
    public void beginning_of_line() {
	TextPositionInfo posInfo = view.getSelectionBegin();
	int index =
		model.getRichText().paragraphBeginIndexOf(posInfo.textIndex);
	TextPositionInfo paraBegin = view.getTextPositionNearby(posInfo, index);
	view.hideSelection();
	setSelectionBeginEnd(paraBegin);
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the character following the insert cursor.
     * @see #delete_next_character(boolean)
     */
    public void delete_next_character() {
	delete_next_character(false);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the character following the insert cursor.
     * @param kill if true, copies the deleted characters to the clipboard.
     * @see #delete_next_character()
     * @see #kill_next_character()
     */
    public void delete_next_character(boolean kill) {
	if (view.selectionIsCaret()) {
	    TextPositionInfo posInfo = view.getSelectionBegin();
	    if (posInfo.textIndex >= model.getRichText().length()) {
		return;
	    }
	    view.hideSelection();
	    setSelectionEnd(view.getTextPositionNextTo(posInfo));
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
	else {
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the characters following the insertion cursor to
     * the next space, tab or end of line character.
     * @see #delete_next_word(boolean)
     */
    public void delete_next_word() {
	delete_next_word(false);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the characters following the insertion cursor to
     * the next space, tab or end of line character.
     * @param kill if true, copies the deleted characters to the clipboard.
     * @see #delete_next_word()
     * @see #kill_next_word()
     */
    public void delete_next_word(boolean kill) {
	if (view.selectionIsCaret()) {
	    TextPositionInfo posInfo = view.getSelectionBegin();
	    int index = getWordEndIndex(posInfo);
	    if (index < 0) {
		return;
	    }
	    view.hideSelection();
	    setSelectionEnd(view.getTextPositionNearby(posInfo, index));
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
	else {
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the character of text immediately preceding the
     * insertion cursor.
     * @see #delete_previous_character(boolean)
     */
    public void delete_previous_character() {
	delete_previous_character(false);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the character of text immediately preceding the
     * insertion cursor.
     * @param kill if true, copies the deleted characters to the clipboard.
     * @see #delete_previous_character()
     * @see #kill_previous_character()
     */
    public void delete_previous_character(boolean kill) {
	if (!view.selectionIsCaret()) {
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	    return;
	}
	TextPositionInfo posInfo = view.getSelectionBegin();
	if (posInfo.textIndex <= 0) {
	    return;
	}
	Text text = model.getRichText().getText();
	int index = posInfo.textIndex;
	int paraBegin;
	if (softTab <= 0 ||
	    !Character.isWhitespace(text.getChar(index - 1)) ||
	    (paraBegin = model.getRichText().paragraphBeginIndexOf(index))
								== index)
	{
	    view.hideSelection();
	    setSelectionBegin(view.getTextPositionPrevTo(posInfo));
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	    return;
	}

	// soft tab
	int end = posInfo.textIndex;
	int tabLen = ParagraphStyle.HARD_TAB_LENGTH;
	int pos = 0;
	int lastNonWhite = -1;
	int lastSoftTab = paraBegin;
	for (int i = paraBegin; i < end; i++) {
	    char c = text.getChar(i);
	    if (c == '\t') {
		pos = ((i - paraBegin + tabLen) / tabLen) * tabLen;
	    }
	    else {
		pos++;
	    }
	    if (!Character.isWhitespace(c))
		lastNonWhite = i;
	    if ((pos % softTab) == 0) {
		if (i < end - 1)
		    lastSoftTab = i + 1;
	    }
	}
	if (lastSoftTab < lastNonWhite + 1)
	    lastSoftTab = lastNonWhite + 1;
	view.hideSelection();
	setSelectionBegin(view.getTextPositionNearby(posInfo, lastSoftTab));
	if (kill) copy_clipboard();
	replaceSelection(new Text());
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the characters preceding the insertion cursor to
     * the previous space, tab or beginning of line character.
     * @see #delete_previous_word(boolean)
     */
    public void delete_previous_word() {
	delete_previous_word(false);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection;
     * otherwise, deletes the characters preceding the insertion cursor to
     * the previous space, tab or beginning of line character.
     * @param kill if true, copies the deleted characters to the clipboard.
     * @see #delete_previous_word()
     * @see #kill_previous_word()
     */
    public void delete_previous_word(boolean kill) {
	if (view.selectionIsCaret()) {
	    TextPositionInfo posInfo = view.getSelectionBegin();
	    int index = getWordBeginIndex(posInfo);
	    if (index < 0) {
		return;
	    }
	    view.hideSelection();
	    setSelectionBegin(view.getTextPositionNearby(posInfo, index));
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
	else {
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
    }

    /**
     * Deletes the current selection.
     * @see #delete_selection(boolean)
     */
    public void delete_selection() {
	delete_selection(false);
    }

    /**
     * Deletes the current selection.
     * @param kill if true, copies the deleted characters to the clipboard.
     * @see #delete_selection()
     * @see #kill_selection()
     */
    public void delete_selection(boolean kill) {
	if (view.selectionIsCaret()) {
	    return;
	}
	else {
	    view.hideSelection();
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
    }

    /**
     * Deletes the characters following the insertion cursor to the next
     * end of line character.
     * @see #delete_to_end_of_line(boolean)
     */
    public void delete_to_end_of_line() {
	delete_to_end_of_line(false);
    }

    /**
     * Deletes the characters following the insertion cursor to the next
     * end of line character.
     * @param kill if true, copies the deleted characters to the clipboard.
     * @see #delete_to_end_of_line()
     * @see #kill_to_end_of_line()
     */
    public void delete_to_end_of_line(boolean kill) {
	if (view.selectionIsCaret()) {
	    TextPositionInfo posInfo = view.getSelectionBegin();
	    int paraEnd =
		model.getRichText().paragraphEndIndexOf(posInfo.textIndex);
	    if (posInfo.textIndex >= paraEnd) {
		return;
	    }
	    view.hideSelection();
	    setSelectionEnd(view.getTextPositionNearby(posInfo, paraEnd));
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
	else {
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
    }

    /**
     * Deletes the characters preceding the insertion cursor to the previous
     * beginning of line character.
     * @see #delete_to_start_of_line(boolean)
     */
    public void delete_to_start_of_line() {
	delete_to_start_of_line(false);
    }

    /**
     * Deletes the characters preceding the insertion cursor to the previous
     * beginning of line character.
     * @param kill if true, copies the deleted characters to the clipboard.
     * @see #delete_to_start_of_line()
     * @see #kill_to_start_of_line()
     */
    public void delete_to_start_of_line(boolean kill) {
	if (view.selectionIsCaret()) {
	    TextPositionInfo posInfo = view.getSelectionBegin();
	    int index =
		model.getRichText().paragraphBeginIndexOf(posInfo.textIndex);
	    if (posInfo.textIndex <= index) {
		return;
	    }
	    TextPositionInfo paraBegin =
				view.getTextPositionNearby(posInfo, index);
	    view.hideSelection();
	    setSelectionBegin(paraBegin);
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
	else {
	    if (kill) copy_clipboard();
	    replaceSelection(new Text());
	}
    }

    /**
     * Deselects the current selection.
     */
    public void deselect_all() {
	if (view.selectionIsCaret()) {
	    return;
	}
	view.hideSelection();
	setSelectionBeginEnd(view.getSelectionEnd());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Do nothing, but KeyEvent is consumed.
     */
    public void do_nothing() {
    }

    /**
     * Moves the insertion cursor to the end of the text.
     */
    public void end_of_file() {
	view.hideSelection();
	setSelectionBeginEnd(
		view.getTextPositionAt(model.getRichText().length()));
	view.scrollTo(view.getSelectionEnd());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Moves the insertion cursor to the end of the line.
     */
    public void end_of_line() {
	TextPositionInfo posInfo = view.getSelectionBegin();
	int paraEnd =
		model.getRichText().paragraphEndIndexOf(posInfo.textIndex);
	view.hideSelection();
	setSelectionBeginEnd(view.getTextPositionNearby(posInfo, paraEnd));
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Moves the insertion cursor one character to the right.
     */
    public void forward_character() {
	if (view.selectionIsCaret()) {
	    TextPositionInfo posInfo = view.getSelectionBegin();
	    if (posInfo.textIndex >= model.getRichText().length()) {
		return;
	    }
	    view.hideSelection();
	    setSelectionBeginEnd(
		view.getTextPositionNextTo(view.getSelectionEnd()));
	}
	else {
	    view.hideSelection();
	    setSelectionBeginEnd(view.getSelectionEnd());
	}
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Moves the insertion cursor to the first whitespace character or
     * end of line following the next non-whitespace character.  If the
     * insertion cursor is already at the end of a word, moves the
     * insertion cursor to the end of the next word.
     */
    public void forward_word() {
	Text text = model.getRichText().getText();
	TextPositionInfo posInfo = view.getSelectionEnd();
	int index = getNextWordIndex(posInfo);
	if (index < 0) {
	    return;
	}
	view.hideSelection();
	setSelectionBeginEnd(view.getTextPositionNearby(posInfo, index));
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Go to line.
     */
    public void goto_line() {
	String line = Dialog.request(
			view.getFrame(),
			getResourceString("kfc.text.gotoLabel", "Go to line:"),
			10);
	if (line.length() == 0) {
	    return;
	}
	int lineNo;
	try { lineNo = Integer.parseInt(line); }
	catch (NumberFormatException e) {
	    Dialog.warn(
		view.getFrame(),
		getResourceString("kfc.text.numberFormatError",
				  "Number format error"));
	    return;
	}

	view.setSelectionVisible(true);
	goto_line(lineNo);
    }

    /**
     * Go to the specified number of the line.
     */
    public void goto_line(int lineNo) {
	RichText rtext = model.getRichText();
	if (rtext.isEmpty() || lineNo <= 0) {
	    Dialog.warn(
		view.getFrame(),
		getResourceString("kfc.text.noSuchLineError", "No such line"));
	    return;
	}
	int index = 0;
	int lineCount = 1;
	while (--lineNo > 0) {
	    index = rtext.nextParagraphBeginIndexOf(index);
	    if (index < 0) {
		Dialog.warn(
			view.getFrame(),
			getResourceString(
				"kfc.text.noSuchLineError",
				"No such line")
					+ " (1 - " + lineCount + ")");
		return;
	    }
	    ++lineCount;
	}
	TextPositionInfo posInfo = view.getTextPositionAt(index);
	view.hideSelection();
	setSelectionBeginEnd(posInfo);
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts string at the insertion cursor.
     */
    public void insert_string(String str) {
	replaceSelection(new Text(str, typeInStyle));
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Otherwise, kills the character following the insertion cursor and
     * stores the character in the cut buffer.
     * @see #delete_next_character(boolean)
     */
    public void kill_next_character() {
	delete_next_character(true);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Otherwise, kills the characters following the insertion cursor to
     * the next space, tab or end of line character, and stores the
     * characters in the cut buffer.
     * @see #delete_next_word(boolean)
     */
    public void kill_next_word() {
	delete_next_word(true);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Otherwise, kills the character of text immediately preceding the
     * insertion cursor and stores the character in the cut buffer.
     * @see #delete_previous_character(boolean)
     */
    public void kill_previous_character() {
	delete_previous_character(true);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Otherwise, kills the characters preceding the insertion cursor to
     * the next space, tab or beginning of line character, and stores the
     * characters in the cut buffer.
     * @see #delete_previous_word(boolean)
     */
    public void kill_previous_word() {
	delete_previous_word(true);
    }

    /**
     * Kills the currently selected text and stores the text in the cut buffer.
     * @see #delete_selection(boolean)
     */
    public void kill_selection() {
	delete_selection(true);
    }

    /**
     * Kills the characters following the insertion cursor to the next end
     * of line character and stores the characters in the cut buffer.
     * @see #delete_to_end_of_line(boolean)
     */
    public void kill_to_end_of_line() {
	delete_to_end_of_line(true);
    }

    /**
     * Kills the characters preceding the insertion cursor to the next
     * beginning of line character and stores the characters in the cut buffer.
     * @see #delete_to_start_of_line(boolean)
     */
    public void kill_to_start_of_line() {
	delete_to_start_of_line(true);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts a newbreak at the insertion cursor.
     */
    public void newbreak() {
	insert_character(Text.LINE_BREAK_CHAR);
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts a newline at the insertion cursor.
     */
    public void newline() {
	insert_character(
		model.getRichText().getRichTextStyle().getLineEndChar());
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts a newline and then the same number of whitespace characters
     * as at the beginning of the previous line.
     */
    public void newline_and_indent() {
	Text text = model.getRichText().getText();
	TextPositionInfo posInfo = view.getSelectionBegin();
	int bIndex =
		model.getRichText().paragraphBeginIndexOf(posInfo.textIndex);
	int i;
	for (i = bIndex; i < posInfo.textIndex; i++) {
	    if (!Character.isWhitespace(text.getChar(i)))
		break;
	}
	StringBuffer buf = new StringBuffer();
	buf.append(model.getRichText().getRichTextStyle().getLineEndChar());
	buf.append(text.substring(bIndex, i));
	insert_string(buf.toString());
    }

    /**
     * Moves the insertion cursor to the next line.
     */
    public void next_line() {
	TextPositionInfo posInfo = view.getSelectionBegin();
	RichText rtext = model.getRichText();
	int bIndex = rtext.paragraphBeginIndexOf(posInfo.textIndex);
	int eIndex = rtext.nextParagraphBeginIndexOf(posInfo.textIndex);
	if (eIndex < 0) // EOT
	    return;
	int nIndex = rtext.nextParagraphBeginIndexOf(eIndex);
	nIndex = (nIndex < 0 ? rtext.length() : nIndex - 1);
	int nextLineIndex;
	if (posInfo.textIndex == eIndex - 1) // EOP
	    nextLineIndex = nIndex;
	else
	    nextLineIndex = eIndex + (posInfo.textIndex - bIndex);
	if (nextLineIndex > nIndex) nextLineIndex = nIndex;

	view.hideSelection();
	setSelectionBeginEnd(
		view.getTextPositionNearby(posInfo, nextLineIndex));
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Moves the insertion cursor forward one page.
     */
    public void next_page() {
	Dimension viewSize = view.getSize();
	Dimension textSize = view.layout.getSize();
	if (textSize.height <= viewSize.height)
	    return;
	TextPositionInfo posInfo = view.getTextPositionNearby(
					view.getVisibleEnd(),
					new Point(0, viewSize.height - 1));
	int offsetY = Math.min(posInfo.y, textSize.height - viewSize.height);
	view.hideSelection();
	setSelectionBeginEnd(posInfo);
	view.scrollY(-offsetY);
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Moves the insertion cursor to the previous line.
     */
    public void previous_line() {
	TextPositionInfo posInfo = view.getSelectionBegin();
	RichText rtext = model.getRichText();
	int bIndex = rtext.paragraphBeginIndexOf(posInfo.textIndex);
	if (bIndex == 0)
	    return;
	int pIndex = rtext.paragraphBeginIndexOf(bIndex - 1);
	int eIndex = rtext.nextParagraphBeginIndexOf(posInfo.textIndex);
	eIndex = (eIndex < 0 ? rtext.length() : eIndex - 1);
	int prevLineIndex;
	if (posInfo.textIndex == eIndex) // EOP
	    prevLineIndex = bIndex - 1;
	else
	    prevLineIndex = pIndex + (posInfo.textIndex - bIndex);
	if (prevLineIndex > bIndex - 1) prevLineIndex = bIndex - 1;

	view.hideSelection();
	setSelectionBeginEnd(
		view.getTextPositionNearby(posInfo, prevLineIndex));
	view.scrollTo(view.getSelectionBegin());
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Moves the insertion cursor back one page.
     */
    public void previous_page() {
	Dimension viewSize = view.getSize();
	Dimension textSize = view.layout.getSize();
	if (textSize.height <= viewSize.height)
	    return;
	TextPositionInfo posInfo = view.getVisibleBegin();
	int offsetY = Math.max(
			posInfo.y + posInfo.lineSkip - viewSize.height,
			0);
	view.hideSelection();
	setSelectionBeginEnd(posInfo);
	view.scrollY(-offsetY);
	view.showSelection();
	notifyTextPositionListeners();
    }

    /**
     * Redraw the display.
     */
    public void redraw_display() {
	view.repaintNow();
    }

    /**
     * Show match.
     */
    public void show_match(char keyChar) {
	TextPositionInfo selBegin = view.getSelectionBegin();
	TextPositionInfo selEnd = view.getSelectionEnd();
	if (selBegin == null || selEnd == null ||
	    selBegin.textIndex != selEnd.textIndex)
	{
	    return;
	}
	char match;

	/*
	switch (keyChar) {
	case ')': match = '('; break;
	case '}': match = '{'; break;
	case ']': match = '['; break;
	default:
	    return;
	}
	*/
	int bIndex = CLOSE_BRACES.indexOf(keyChar);
	if (bIndex < 0)
	    return;
	match = OPEN_BRACES.charAt(bIndex);

	int count = 0;
	int index = selBegin.textIndex - 2;
	if (index >= 0) {
	    Text text = model.getRichText().getText();
	    CharacterIterator iterator = text.getCharacterIterator(index);
	    index = -1;
	    for (char c = iterator.current();
		 c != CharacterIterator.DONE;
		 c = iterator.previous())
	    {
		if (c == match) {
		    if (count == 0) {
			index = iterator.getIndex();
			break;
		    }
		    --count;
		}
		else if (c == keyChar) {
		    count++;
		}
	    }
	}
	if (index < 0) {
	    view.getToolkit().beep();
	}
	else if (view.isShowing()) {
	    Point p = view.getLocationOfText();
	    select(index, index + 1, true);
	    try { Thread.currentThread().sleep(100); }
	    catch (InterruptedException e) {}
	    view.hideSelection();
	    setSelectionBeginEnd(selBegin, selEnd);
	    view.setLocationOfText(p);
	    view.showSelection();
	}
    }

    /**
     * If the cursor is inside the selection, deletes the entire selection.
     * Inserts the soft tab.
     */
    public void tab() {
	if (softTab <= 0) {
	    insert_character('\t');
	    return;
	}
	Text text = model.getRichText().getText();
	TextPositionInfo posInfo = view.getSelectionBegin();
	int end = posInfo.textIndex;
	int paraBegin = model.getRichText().paragraphBeginIndexOf(end);
	int tabLen = ParagraphStyle.HARD_TAB_LENGTH;
	int pos = 0;
	for (int i = paraBegin; i < end; i++) {
	    if (text.getChar(i) == '\t') {
		pos = ((i - paraBegin + tabLen) / tabLen) * tabLen;
	    }
	    else {
		pos++;
	    }
	}
	int nTab = (((pos + softTab) / softTab) * softTab) - pos;
	StringBuffer buffer = new StringBuffer();
	while (nTab-- > 0) {
	    buffer.append(' ');
	}
	insert_string(buffer.toString());
    }

    /**
     * Restores last killed text to the position of the insertion cursor.
     */
    public void unkill() {
	paste_clipboard();
    }


    // ================ Protected ================

    /**
     * Returns the starting index of the next word.
     */
    protected int getNextWordIndex(TextPositionInfo posInfo) {
	Text text = model.getRichText().getText();
	int index = posInfo.textIndex;
	if (index >= text.length() - 1)
	    return -1;
	BreakIterator boundary= BreakIterator.getWordInstance(view.getLocale());
	boundary.setText(text.getCharacterIterator(index));
	for (int next = boundary.following(index);
	     next != BreakIterator.DONE;
	     next = boundary.next())
	{
	    if (next >= text.length())
		return -1;
	    if (next > index && !Character.isWhitespace(text.getChar(next)))
	    {
		return next;
	    }
	}
	return -1;
    }

    /**
     * Returns the starting index of the previous word.
     */
    protected int getPrevWordIndex(TextPositionInfo posInfo) {
	Text text = model.getRichText().getText();
	int index = posInfo.textIndex;
	if (index >= text.length())
	    index = text.length() - 1;
	if (index <= 0)
	    return -1;
	BreakIterator boundary= BreakIterator.getWordInstance(view.getLocale());
	boundary.setText(text.getCharacterIterator(index));
	boundary.following(index);
	for (int prev = boundary.previous();
	     prev != BreakIterator.DONE;
	     prev = boundary.previous())
	{
	    if (prev < index && !Character.isWhitespace(text.getChar(prev)))
	    {
		return prev;
	    }
	}
	return -1;
    }

    /**
     * Returns the starting index of the current word.
     */
    protected int getWordBeginIndex(TextPositionInfo posInfo) {
	Text text = model.getRichText().getText();
	int index = posInfo.textIndex;
	if (index >= text.length())
	    index = text.length() - 1;
	if (index <= 0)
	    return -1;
	BreakIterator boundary= BreakIterator.getWordInstance(view.getLocale());
	boundary.setText(text.getCharacterIterator(index));
	boundary.following(index);
	int begin;
	while ((begin = boundary.previous()) != BreakIterator.DONE) {
	    if (begin < index)
		return begin;
	}
	return -1;
    }

    /**
     * Returns the ending index of the current word.
     */
    protected int getWordEndIndex(TextPositionInfo posInfo) {
	Text text = model.getRichText().getText();
	int index = posInfo.textIndex;
	if (index >= text.length() - 1)
	    return -1;
	BreakIterator boundary= BreakIterator.getWordInstance(view.getLocale());
	boundary.setText(text.getCharacterIterator(index));
	for (int end = boundary.following(index);
	     end != BreakIterator.DONE;
	     end = boundary.next())
	{
	    if (end > index)
		return end;
	}
	return -1;
    }
}
