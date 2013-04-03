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
import java.util.List;

import junit.framework.ComparisonCompactor;

import org.attoparser.AttoParseException;
import org.attoparser.markup.MarkupAttoParser;
import org.attoparser.markup.html.trace.TracingDetailedHtmlAttoHandler;
import org.attoparser.markup.trace.TraceEvent;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.Validate;






public class ResultCompareUtils {

    
    private static final int CONTEXT_LENGTH = 30;
    
    private static final MarkupAttoParser parser = new MarkupAttoParser();
    
    

    
    public static ResultComparison compareResults(final String expected, final String actual) {
        
        Validate.notNull(expected, "Expected result cannot be null");
        Validate.notNull(actual, "Actual result cannot be null");

        final TracingDetailedHtmlAttoHandler expectedHandler = new TracingDetailedHtmlAttoHandler();
        final TracingDetailedHtmlAttoHandler actualHandler = new TracingDetailedHtmlAttoHandler();
        
        try {
            parser.parse(expected, expectedHandler);
            parser.parse(actual, actualHandler);
        } catch (final AttoParseException e) {
            throw new TestEngineExecutionException("Error while trying to compare results", e);
        }
        
        final List<TraceEvent> expectedTrace = normalizeTrace(expectedHandler.getTrace());
        final List<TraceEvent> actualTrace = normalizeTrace(actualHandler.getTrace());
        
        if (matchTraces(expectedTrace,actualTrace)) {
            return new ResultComparison(true, null);
        }
        
        /*
         * JUnit's comparison reporter will be used
         */
        final ComparisonCompactor compactor =
                new ComparisonCompactor(CONTEXT_LENGTH, expected, actual);
        return new ResultComparison(false, compactor.compact("Result does not match -"));
        
    }
    
    
    
    

    private static List<TraceEvent> normalizeTrace(final List<TraceEvent> trace) {
        
        // We will avoid a whitespace at the end of an open/standalone tag (at the end of its
        // attribute sequence). In order to do so, we will only add whitespace events when
        // the next attribute is found.
        TraceEvent lastWhitespaceEvent = null;
        
        final List<TraceEvent> newTrace = new ArrayList<TraceEvent>();
        
        for (final TraceEvent event : trace) {
            
            final String eventType = event.getType();
            if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT.equals(eventType)) {
                // We need to compress all whitespace in order to perform a correct lenient check
                final String text = event.getContent()[0];
                newTrace.add(
                        new TraceEvent(
                                event.getLine(), event.getCol(), 
                                TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT, 
                                compressWhitespace(text)));
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_INNERWHITESPACE.equals(eventType)) {
                // We need to compress all whitespace in order to perform a correct lenient check
                final String text = event.getContent()[0];
                lastWhitespaceEvent = 
                        new TraceEvent(
                                event.getLine(), event.getCol(), 
                                TracingDetailedHtmlAttoHandler.TRACE_TYPE_INNERWHITESPACE, 
                                compressWhitespace(text));
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_ATTRIBUTE.equals(eventType)) {
                if (lastWhitespaceEvent != null) {
                    newTrace.add(lastWhitespaceEvent);
                    lastWhitespaceEvent = null;
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
    
    
    
    
    private static boolean matchTraces(final List<TraceEvent> trace1, final List<TraceEvent> trace2) {
        
        final int trace1Size = trace1.size();
        final int trace2Size = trace2.size();
        
        if (trace1Size != trace2Size) {
            return false;
        }
        
        for (int i = 0; i < trace1Size; i++) {
            final TraceEvent trace1Item = trace1.get(i);
            final TraceEvent trace2Item = trace2.get(i);
            if (!trace1Item.matchesTypeAndContent(trace2Item)) {
                return false;
            }
        }
        
        return true;
        
    }
    
    
    
    
    
    
    private ResultCompareUtils() {
        super();
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
