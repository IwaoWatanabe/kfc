/*
 * TextCharacterIterator.java
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

import java.text.CharacterIterator;

/**
 * The <code>TextCharacterIterator</code> implements the
 * <code>CharacterIterater</code> protocol for an array of characters.
 *
 * @version 	10 Jun 1998
 * @author 	Kazuki YASUMATSU
 */
public class TextCharacterIterator implements CharacterIterator {
    /** the text to be iterated over. */
    protected Text text;

    /** the array of characters to be iterated over. */
    protected char array[];

    /** the beginning index of the text. */
    protected int begin;

    /** the ending index of the text. */
    protected int end;

    /** the position of the text. */
    protected int pos;


    /**
     * Construct an iterator over the specified text object,
     * with an initial index of 0.
     *
     * @param text the text object to be iterated over.
     */
    public TextCharacterIterator(Text text) {
	this(text, 0);
    }

    /**
     * Construct an iterator over the specified text object,
     * with the specified initial index.
     *
     * @param text the text object to be iterated over.
     * @param pos  initial iterator position.
     */
    public TextCharacterIterator(Text text, int pos) {
	this(text, 0, text.length(), pos);
    }

    /**
     * Construct an iterator over the specified range of the specified text
     * object, with the index set at the specified position.
     *
     * @param text the text object to be iterated over.
     * @param begin index of the first character.
     * @param end   index of the character following the last character.
     * @param pos   initial iterator position.
     */
    public TextCharacterIterator(Text text, int begin, int end, int pos) {
	if (text == null)
	    throw new NullPointerException();
	if (begin < 0 || end > text.length() || begin > end)
	    throw new IllegalArgumentException("Invalid subtext range");
	if (pos < begin || pos > end)
	    throw new IllegalArgumentException("Invalid position");

	this.text  = text;
	this.array = text.getCharArray();
	this.begin = begin;
	this.end   = end;
	this.pos   = pos;
    }

    /**
     * Construct an iterator over the specified array of characters,
     * with an initial index of 0.
     *
     * @param array the array of characters to be iterated over.
     */
    public TextCharacterIterator(char array[]) {
	this(array, 0);
    }

    /**
     * Construct an iterator over the specified array of characters,
     * with the specified initial index.
     *
     * @param array the array of characters to be iterated over.
     * @param pos   initial iterator position.
     */
    public TextCharacterIterator(char array[], int pos) {
	this(array, 0, array.length, pos);
    }

    /**
     * Construct an iterator over the specified range of the specified array
     * of characters, with the index set at the specified position.
     *
     * @param array the array of characters to be iterated over.
     * @param begin index of the first character.
     * @param end   index of the character following the last character.
     * @param pos   initial iterator position.
     */
    public TextCharacterIterator(char array[], int begin, int end, int pos) {
	if (array == null)
	    throw new NullPointerException();
	if (begin < 0 || end > array.length || begin > end)
	    throw new IllegalArgumentException("Invalid subtext range");
	if (pos < begin || pos > end)
	    throw new IllegalArgumentException("Invalid position");

	this.text  = null;
	this.array = array;
	this.begin = begin;
	this.end   = end;
	this.pos   = pos;
    }

    /**
     * Returns the text to be iterated over.
     *
     * @return the text to be iterated over.
     */
    public final Text getText() {
	return text;
    }

    /**
     * Set the position to getBeginIndex() and return the character at that
     * position.
     *
     * @return the character at the position to getBeginIndex().
     */
    public final char first() {
	pos = begin;
	return array[pos];
    }

    /**
     * Set the position to getEndIndex() and return the character at that
     * position.
     *
     * @return the character at the position to getEndIndex().
     */
    public final char last() {
	pos = end - 1;
	return array[pos];
    }

    /**
     * Set the position to specified position in the array of characters
     * and return that character.
     *
     * @param p the position.
     * @return the character at the position.
     */
    public final char setIndex(int p) {
	if (p < begin || p >= end)
	    throw new IllegalArgumentException("Invalid index");
	pos = p;
	return array[p];
    }

    /**
     * Returns the character at the current position
     * (as returned by getIndex()).
     *
     * @return the character at the current position or DONE if the current
     *         position is off the end of the array of characters.
     */
    public final char current() {
	if (pos >= begin && pos < end) {
	    return array[pos];
	}
	else {
	    return DONE;
	}
    }

    /**
     * Increment the iterator's index by one and return the character
     * at the new index. If the resulting index is greater or equal
     * to getEndIndex(), the current index is reset to getEndIndex() and
     * a value of DONE is returned.
     *
     * @return the character at the new position or DONE if the current
     *         position is off the end of the array of characters.
     */
    public final char next() {
	if (++pos < end) {
	    return array[pos];
	}
	else {
	    return DONE;
	}
    }

    /**
     * Decrement the iterator's index by one and return the character
     * at the new index. If the resulting index is less than
     * getBeginIndex(), the current index is reset to getBeginIndex()
     * and a value of DONE is returned.
     *
     * @return the character at the new position or DONE if the current
     *         position is off the end of the array of characters.
     */
    public final char previous() {
	if (pos > begin) {
	    return array[--pos];
	}
	else {
	    return DONE;
	}
    }

    /**
     * Return the start index of the array of characters.
     *
     * @return the index at which the array of characters begins.
     */
    public final int getBeginIndex() {
	return begin;
    }

    /**
     * Return the end index of the array of characters. This index is
     * the index of the first character following the end of the
     * array of characters.
     *
     * @return the index at which the array of characters end.
     */
    public final int getEndIndex() {
	return end;
    }

    /**
     * Return the current index.
     *
     * @return the current index.
     */
    public final int getIndex() {
	return pos;
    }

    /**
     * Returns a hashcode for this object.
     */
    public int hashCode() {
	return array.hashCode() ^ pos ^ begin ^ end;
    }

    /**
     * Compares two objects for equality.
     */
    public boolean equals(Object anObject) {
	if (this == anObject)
	    return true;
	if (anObject == null)
	    return false;
	if (anObject instanceof TextCharacterIterator) {
	    TextCharacterIterator iterator = (TextCharacterIterator)anObject;
	    if (pos   == iterator.pos   &&
		begin == iterator.begin &&
		end   == iterator.end)
	    {
		for (int i = begin; i < end; i++) {
		    if (array[i] != iterator.array[i]) {
			return false;
		    }
		}
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns a clone of this object.
     *
     * @return a clone of this object.
     */
    public Object clone() {
    	try {
	    return (TextCharacterIterator)super.clone();
	}
	catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }
}
