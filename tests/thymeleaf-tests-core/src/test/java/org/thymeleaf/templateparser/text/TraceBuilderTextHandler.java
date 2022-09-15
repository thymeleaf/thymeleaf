/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.templateparser.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class TraceBuilderTextHandler extends AbstractTextHandler {


    private final List<TextTraceEvent> trace = new ArrayList<TextTraceEvent>(20);




    public TraceBuilderTextHandler() {
        super();
    }




    public List<TextTraceEvent> getTrace() {
        return Collections.unmodifiableList(this.trace);
    }
    


    
    @Override
    public void handleDocumentStart(final long startTimeNanos, final int line, final int col)
            throws TextParseException {
        this.trace.add(new TextTraceEvent.DocumentStartTraceEvent(startTimeNanos, line, col));
    }

    
    
    @Override
    public void handleDocumentEnd(
            final long endTimeNanos, final long totalTimeNanos, final int line, final int col)
            throws TextParseException {
        this.trace.add(new TextTraceEvent.DocumentEndTraceEvent(endTimeNanos, totalTimeNanos, line, col));
    }



    @Override
    public void handleStandaloneElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws TextParseException {
        final String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new TextTraceEvent.StandaloneElementStartTraceEvent(elementName, line, col));
    }



    @Override
    public void handleStandaloneElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final boolean minimized, final int line, final int col)
            throws TextParseException {
        final String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new TextTraceEvent.StandaloneElementEndTraceEvent(elementName, line, col));
    }




    @Override
    public void handleOpenElementStart(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        final String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new TextTraceEvent.OpenElementStartTraceEvent(elementName, line, col));
    }




    @Override
    public void handleOpenElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        final String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new TextTraceEvent.OpenElementEndTraceEvent(elementName, line, col));
    }

    

    
    @Override
    public void handleCloseElementStart(
            final char[] buffer, 
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        final String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new TextTraceEvent.CloseElementStartTraceEvent(elementName, line, col));
    }
    
    

    
    @Override
    public void handleCloseElementEnd(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int line, final int col)
            throws TextParseException {
        final String elementName = new String(buffer, nameOffset, nameLen);
        this.trace.add(new TextTraceEvent.CloseElementEndTraceEvent(elementName, line, col));
    }

    
    
    
    @Override
    public void handleAttribute(
            final char[] buffer,
            final int nameOffset, final int nameLen,
            final int nameLine, final int nameCol,
            final int operatorOffset, final int operatorLen,
            final int operatorLine, final int operatorCol,
            final int valueContentOffset, final int valueContentLen,
            final int valueOuterOffset, final int valueOuterLen,
            final int valueLine, final int valueCol)
            throws TextParseException {

        final String attributeName = new String(buffer, nameOffset, nameLen);
        final String operator = new String(buffer, operatorOffset, operatorLen);
        final String value = new String(buffer, valueOuterOffset, valueOuterLen);

        this.trace.add(new TextTraceEvent.AttributeTraceEvent(
                attributeName, nameLine, nameCol, operator, operatorLine, operatorCol, value, valueLine, valueCol));

    }

    
    
    @Override
    public void handleText(final char[] buffer, final int offset, final int len,
            final int line, final int col)
            throws TextParseException {
        final String content = new String(buffer, offset, len);
        this.trace.add(new TextTraceEvent.TextTextTraceEvent(content, line, col));
    }


    @Override
    public void handleComment(
            final char[] buffer,
            final int contentOffset, final int contentLen,
            final int outerOffset, final int outerLen,
            final int line, final int col)
            throws TextParseException {
        final String content = new String(buffer, contentOffset, contentLen);
        this.trace.add(new TextTraceEvent.CommentTraceEvent(content, line, col));
    }

    
}