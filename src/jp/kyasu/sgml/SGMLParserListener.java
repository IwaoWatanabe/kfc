/*
 * SGMLParserListener.java
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

package jp.kyasu.sgml;

import java.io.IOException;

/**
 * The listener interface for receiving sgml events from
 * <code>SGMLParser</code>.
 *
 * @see 	jp.kyasu.sgml.SGMLEvent
 * @see 	jp.kyasu.sgml.SGMLParser
 *
 * @version 	09 Aug 1997
 * @author 	Kazuki YASUMATSU
 */
public interface SGMLParserListener extends java.util.EventListener {
    /**
     * Invoked when a start tag has been parsed.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void startTagParsed(SGMLEvent e) throws IOException;

    /**
     * Invoked when a end tag has been parsed.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void endTagParsed(SGMLEvent e) throws IOException;

    /**
     * Invoked when a cdata has been parsed.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void cdataParsed(SGMLEvent e) throws IOException;

    /**
     * Invoked when a parsing has been finished.
     *
     * @exception java.io.IOException If an I/O error occurs.
     */
    public void parsingFinished(SGMLEvent e) throws IOException;
}
