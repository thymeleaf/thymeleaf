/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.standard.inline;


/**
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 3.0.0
 *
 */
public interface IInlinePreProcessorHandler {


    public void handleText(
            final char[] buffer,
            final int offset, final int len,
            final int line, final int col);


    /*
     * IT WOULD MAKE NO SENSE TO ADD HERE EVENTS FOR CDATA AND/OR COMMENTS. The reason is that these inline
     * handlers are meant to pre-process inlined text during the template parsing phase, and as a result of their
     * processing text events might be broken into several. E.g. a "one  [['two']] three" will actually be broken
     * into two "one " and " three" text events, with a "[# th:text="'two'"/]" element in the middle.
     *
     * This is relatively harmless to pre- and post-processors expecting to act on entire text blocks, because they
     * are single classes and have always the possibility to merge consecutive blocks of text. But CDATA and comment
     * blocks have prefixes and suffixes ("<![CDATA[...]]>", "<!--...-->"), so splitting these events into several
     * would provoke the insertion of prefixes that will actually break the syntax. That is why CDATAs and Comments
     * (inside which inlined output expressions ARE ALLOWED) will always be processed during the processor execution
     * phase.
     */




    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col);

    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized,
            final int line, final int col);


    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col);

    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col);


    public void handleAutoOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col);

    public void handleAutoOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col);


    public void handleCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col);

    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col);


    public void handleAutoCloseElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col);

    public void handleAutoCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col);




    public void handleAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol);


}
