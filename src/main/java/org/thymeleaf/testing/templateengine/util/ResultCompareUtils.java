/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.testing.templateengine.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.attoparser.AttoParseException;
import org.attoparser.markup.MarkupAttoParser;
import org.attoparser.markup.html.trace.TracingDetailedHtmlAttoHandler;
import org.attoparser.markup.trace.TraceEvent;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.Validate;






public class ResultCompareUtils {

    private static final AttributeEventComparator ATTRIBUTE_EVENT_COMPARATOR = new AttributeEventComparator();
    private static final MarkupAttoParser PARSER = new MarkupAttoParser();
    
    

    
    public static ResultComparison compareResults(final String expected, final String actual, final boolean lenient) {
        
        Validate.notNull(expected, "Expected result cannot be null");
        Validate.notNull(actual, "Actual result cannot be null");

        final TracingDetailedHtmlAttoHandler expectedHandler = new TracingDetailedHtmlAttoHandler();
        final TracingDetailedHtmlAttoHandler actualHandler = new TracingDetailedHtmlAttoHandler();
        
        try {
            PARSER.parse(expected, expectedHandler);
            PARSER.parse(actual, actualHandler);
        } catch (final AttoParseException e) {
            throw new TestEngineExecutionException("Error while trying to compare results", e);
        }
        
        final List<TraceEvent> expectedTrace = 
                (lenient? normalizeTrace(expectedHandler.getTrace()) : expectedHandler.getTrace());
        final List<TraceEvent> actualTrace = 
                (lenient? normalizeTrace(actualHandler.getTrace()) : actualHandler.getTrace());

        final int actualTraceSize = actualTrace.size();
        final int expectedTraceSize = expectedTrace.size();
        
        for (int i = 0; i < actualTraceSize; i++) {
            
            final TraceEvent actualTraceItem = actualTrace.get(i);
            final TraceEvent expectedTraceItem = 
                    (expectedTraceSize > i? expectedTrace.get(i) : null);
            
            if (expectedTraceItem == null) {
                
                final String actualFragment =
                        getFragmentSurrounding(
                                actual, actualTraceItem.getLine(), actualTraceItem.getCol(), 20, computeErrorMessageLength(actualTraceItem));
                final String expectedFragment =
                        getFragmentSurrounding(
                                expected, Integer.MAX_VALUE, Integer.MAX_VALUE, 20, 0);
               
                final String explanation =
                        createExplanation(actualFragment, actualTraceItem.getLine(), actualTraceItem.getCol(), expectedFragment);
                
                return new ResultComparison(false, explanation);
                
            }
            
            
            final boolean itemMatches = 
                    (lenient? actualTraceItem.matchesTypeAndContent(expectedTraceItem) : actualTraceItem.equals(expectedTraceItem));
            
            if (!itemMatches) {
                
                final String actualFragment =
                        getFragmentSurrounding(
                                actual, actualTraceItem.getLine(), actualTraceItem.getCol(), 20, computeErrorMessageLength(actualTraceItem));
                final String expectedFragment =
                        getFragmentSurrounding(
                                expected, expectedTraceItem.getLine(), expectedTraceItem.getCol(), 20, computeErrorMessageLength(expectedTraceItem));
               
                final String explanation =
                        createExplanation(actualFragment, actualTraceItem.getLine(), actualTraceItem.getCol(), expectedFragment);
                
                return new ResultComparison(false, explanation);
                
            }
            
        }
        
        
        return new ResultComparison(true, "OK");
        
    }
    
    
    
    

    private static List<TraceEvent> normalizeTrace(final List<TraceEvent> trace) {
        
        final List<TraceEvent> newTrace = new ArrayList<TraceEvent>();
        final List<TraceEvent> currentAttributeList = new ArrayList<TraceEvent>();
        
        for (final TraceEvent event : trace) {
            
            final String eventType = event.getType();
            
            if (!currentAttributeList.isEmpty()) {
                if (!TracingDetailedHtmlAttoHandler.TRACE_TYPE_ATTRIBUTE.equals(eventType) &&
                        !TracingDetailedHtmlAttoHandler.TRACE_TYPE_INNERWHITESPACE.equals(eventType)) {
                    Collections.sort(currentAttributeList, ATTRIBUTE_EVENT_COMPARATOR);
                    newTrace.addAll(currentAttributeList);
                    currentAttributeList.clear();
                }
            }
            
            if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT.equals(eventType)) {
                // We need to compress all whitespace in order to perform a correct lenient check
                final String text = event.getContent()[0];
                newTrace.add(
                        new TraceEvent(
                                event.getLine(), event.getCol(), 
                                TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT, 
                                compressWhitespace(text)));
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_INNERWHITESPACE.equals(eventType)) {
                // These events are not relevant for result matching, so we just ignore them 
                // (they represent mere inter-attribute whitespace)
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_ATTRIBUTE.equals(eventType)) {
                currentAttributeList.add(event);
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_DOCUMENT_END.equals(eventType)) {
                /*
                 * If the last event before document end is just whitespace text and trace is 
                 * bigger than just two events (document start + one event),
                 * we will just remove it. Whitespace at the end of a document has no influence at all.
                 */
                final TraceEvent lastEvent = 
                        (newTrace.size() > 2? newTrace.get(newTrace.size() - 1) : null);
                if (lastEvent != null && TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT.equals(lastEvent.getType())) {
                    final String text = lastEvent.getContent()[0];
                    if (isAllWhitespace(text)) {
                        newTrace.remove(newTrace.size() - 1);
                    }
                }
                newTrace.add(event);
            } else {
                newTrace.add(event);
            }
            
        }
        
        return newTrace;
        
    }
    
    
    
    
    
    private static String compressWhitespace(final String text) {
        
        final StringBuilder strBuilder = new StringBuilder();
        
        boolean whitespace = false;
        
        final int textLen = text.length();
        for (int i = 0; i < textLen; i++) {
            final char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!whitespace) {
                    strBuilder.append(' ');
                    whitespace = true;
                }
            } else {
                whitespace = false;
                strBuilder.append(c);
            }
        }
        
        return strBuilder.toString();
        
    }
    
    
    
    
    
    private static boolean isAllWhitespace(final String text) {
        final int textLen = text.length();
        for (int i = 0; i < textLen; i++) {
            final char c = text.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }
    
    
    
    
    private static String getFragmentSurrounding(
            final String text, final int line, final int col, final int before, final int after) {
        
        final int textLen = text.length();
        
        int cline = 1;
        int ccol = 1;
        int pos = 0;
        
        while (pos < textLen) {
            if (cline >= line && ccol >= col) {
                break;
            }
            final char c = text.charAt(pos);
            if (c == '\n') {
                cline++;
                ccol = 1;
            } else {
                ccol++;
            }
            pos++;
        }
        
        if (pos >= textLen) {
            pos = textLen - 1;
        }
        
        // pos contains the position in text marking the desired location
        // we should get 50 chars before, 50 chars after
        
        final int startPos = Math.max(0, (pos - before));
        final int endPos = Math.min(textLen, (pos + after));

        return new String(text.substring(startPos, endPos));
        
    }    


    
    
    public static String createExplanation(
            final String actualFragment, final int actualLine, final int actualCol, final String expectedFragment) {
        return "Actual result does not match expected result.\nObtained:\n[" + actualFragment + "]\n" +
               "at line " + actualLine + " col " + actualCol + ", but " +
               "expected:\n[" + expectedFragment + "]";
    }
    
    
    
    
    private static int computeErrorMessageLength(final TraceEvent eventItem) {
        
        if (!TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT.equals(eventItem)) {
            return 80;
        }
        
        final Object[] contentArray = eventItem.getContent();
        if (contentArray == null || contentArray.length == 0) {
            return 80;
        }
        
        final Object contentObj = eventItem.getContent()[0];
        if (contentObj == null || !(contentObj instanceof String)) {
            return 80;
        }
        
        final String content = (String) contentObj;
        return content.length() + 20;
        
    }
    
    
    
    
    
    
    
    private ResultCompareUtils() {
        super();
    }
    
    
    
    
    private static class AttributeEventComparator implements Comparator<TraceEvent> {

        
        AttributeEventComparator() {
            super();
        }
        
        public int compare(final TraceEvent o1, final TraceEvent o2) {
            
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            
            final String name1 = 
                    (o1.getContent().length > 0 ? o1.getContent()[0] : null);
            final String name2 = 
                    (o2.getContent().length > 0 ? o2.getContent()[0] : null);

            if (name1 == null) {
                return -1;
            }
            if (name2 == null) {
                return 1;
            }
            
            return name1.compareTo(name2);
            
        }
        
    }
    

    
    
    public static class ResultComparison {
        
        private final boolean result;
        private final String explanation;
        
        public ResultComparison(final boolean result, final String explanation) {
            super();
            this.result = result;
            this.explanation = explanation;
        }

        public boolean getResult() {
            return this.result;
        }

        public String getExplanation() {
            return this.explanation;
        }
        
    }
    
    
    
}
