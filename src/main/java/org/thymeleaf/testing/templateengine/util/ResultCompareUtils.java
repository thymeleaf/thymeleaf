/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.attoparser.AttoParseException;
import org.attoparser.markup.MarkupAttoParser;
import org.attoparser.markup.html.trace.TracingDetailedHtmlAttoHandler;
import org.attoparser.markup.trace.TraceEvent;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;
import org.thymeleaf.util.Validate;






public class ResultCompareUtils {

    
    private static final AttributeEventComparator ATTRIBUTE_EVENT_COMPARATOR = new AttributeEventComparator();
    private static final MarkupAttoParser PARSER = new MarkupAttoParser();

    
    private static final Set<String> BLOCK_ELEMENTS =
            new HashSet<String>(Arrays.asList(
                    new String[] {
                            "address", "article", "aside", "audio", "blockquote", "canvas", 
                            "dd", "div", "dl", "dt", "fieldset", "figcaption", "figure", "footer", 
                            "form", "h1", "h2", "h3", "h4", "h5", "h6", "header", "hgroup", "hr", 
                            "li", "noscript", "ol", "option", "output", "p", "pre", "section", "table", "tbody",
                            "tfoot", "tr", "td", "th", "ul", "video"
                    }));
    private static final Set<String> BLOCK_CONTAINER_ELEMENTS =
            new HashSet<String>(Arrays.asList(
                    new String[] {
                            "address", "article", "aside", "div", "dl", "fieldset", "footer", 
                            "form", "header", "hgroup",  "noscript", "ol", "section", "table", 
                            "tbody", "tr", "tfoot", "ul"
                    }));
    
    

    
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

                final int[] actualFragmentReportSizes = computeErrorMessageLength(actualTrace, i, actual);
                final int[] expectedFragmentReportSizes = new int[] {20,0};
                
                final String actualFragment =
                        getFragmentSurrounding(
                                actual, actualTraceItem.getLine(), actualTraceItem.getCol(), actualFragmentReportSizes[0], actualFragmentReportSizes[1]);
                final String expectedFragment =
                        getFragmentSurrounding(
                                expected, Integer.MAX_VALUE, Integer.MAX_VALUE, expectedFragmentReportSizes[0], expectedFragmentReportSizes[1]);
               
                final String explanation =
                        createExplanation(actualFragment, actualTraceItem.getLine(), actualTraceItem.getCol(), expectedFragment);
                
                return new ResultComparison(false, explanation);
                
            }
            

            final TraceEvent comparableActualTraceItem = compressWhitespaceIfText(actualTraceItem);
            final TraceEvent comparableExpectedTraceItem = compressWhitespaceIfText(expectedTraceItem);
            
            final boolean itemMatches = 
                    (lenient? comparableActualTraceItem.matchesTypeAndContent(comparableExpectedTraceItem) : actualTraceItem.equals(expectedTraceItem));
            
            if (!itemMatches) {

                final int[] actualFragmentReportSizes = computeErrorMessageLength(actualTrace, i, actual);
                final int[] expectedFragmentReportSizes = computeErrorMessageLength(expectedTrace, i, expected);
                
                final String actualFragment =
                        getFragmentSurrounding(
                                actual, actualTraceItem.getLine(), actualTraceItem.getCol(), actualFragmentReportSizes[0], actualFragmentReportSizes[1]);
                final String expectedFragment =
                        getFragmentSurrounding(
                                expected, expectedTraceItem.getLine(), expectedTraceItem.getCol(), expectedFragmentReportSizes[0], expectedFragmentReportSizes[1]);
               
                final String explanation =
                        createExplanation(actualFragment, actualTraceItem.getLine(), actualTraceItem.getCol(), expectedFragment);
                
                return new ResultComparison(false, explanation);
                
            }
            
        }
        
        
        return new ResultComparison(true, "OK");
        
    }
    
    
    
    

    private static List<TraceEvent> normalizeTrace(final List<TraceEvent> trace) {
        
        String lastOpenElementName = null;
        String lastClosedElementName = null;
        boolean lastIsWhiteSpace = false;
        
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
                    lastIsWhiteSpace = false;
                }
            }
            
            if (newTrace.size() == 2) {
                /*
                 * We will ignore every leading text node before anything not being a DOCTYPE or
                 * an XML DECLARATION (in which cases, leading text will matter).
                 * 
                 * We check newTrace to be of size 2 because we look for: 
                 * [0] = DOCUMENT_START, [1] = TEXT
                 */
                if (!TracingDetailedHtmlAttoHandler.TRACE_TYPE_DOCTYPE.equals(eventType) &&
                    !TracingDetailedHtmlAttoHandler.TRACE_TYPE_XMLDECL.equals(eventType) && 
                    !TracingDetailedHtmlAttoHandler.TRACE_TYPE_DOCUMENT_END.equals(eventType)) {
                    
                    if (lastIsWhiteSpace) {
                        newTrace.remove(newTrace.size() - 1);
                        lastIsWhiteSpace = false;
                    }
                    
                }
            }
            
            if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT.equals(eventType)) {
                // We need to compress all whitespace in order to perform a correct lenient check
                
                final String text = event.getContent()[0];

                // We will not perform the whitespace compression here. Instead, we will
                // do that only in the very moment the text event items are compared, so that
                // every computation regarding error messages is done on the original text.
                newTrace.add(event);
                
                if (isAllWhitespace(text)) {
                    lastIsWhiteSpace = true;
                } else {
                    lastOpenElementName = null;
                    lastClosedElementName = null;
                }
                
                
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_INNERWHITESPACE.equals(eventType)) {
                
                // These events are not relevant for result matching, so we just ignore them 
                // (they represent mere inter-attribute whitespace)
                
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_ATTRIBUTE.equals(eventType)) {
                
                currentAttributeList.add(event);
                
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_CLOSE_ELEMENT_END.equals(eventType) ||
                       TracingDetailedHtmlAttoHandler.TRACE_TYPE_STANDALONE_ELEMENT_END.equals(eventType)) {
                
                /*
                 * We need to store the name of the last element closed in order to later determine whether
                 * any white space events between it and the following element should be ignored.
                 */
                
                lastClosedElementName = event.getContent()[0].toLowerCase();
                lastOpenElementName = null;
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_OPEN_ELEMENT_START.equals(eventType) ||
                       TracingDetailedHtmlAttoHandler.TRACE_TYPE_STANDALONE_ELEMENT_START.equals(eventType)) {

                /*
                 * Whitespace between block elements (e.g. <div>, <p>...) or between block containers
                 * and block elements (e.g. <div><p>, <ul><li>) should not be considered.
                 */
                
                final String elementName = event.getContent()[0].toLowerCase();
                
                if (lastClosedElementName != null && 
                    BLOCK_ELEMENTS.contains(lastClosedElementName) &&
                    BLOCK_ELEMENTS.contains(elementName)) {
                    
                    if (lastIsWhiteSpace) {
                        newTrace.remove(newTrace.size() - 1);
                        lastIsWhiteSpace = false;
                    }

                } else if (lastOpenElementName != null && 
                           BLOCK_CONTAINER_ELEMENTS.contains(lastOpenElementName) &&
                           BLOCK_ELEMENTS.contains(elementName)) {
   
                    if (lastIsWhiteSpace) {
                        newTrace.remove(newTrace.size() - 1);
                        lastIsWhiteSpace = false;
                    }

                } 
                
                if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_STANDALONE_ELEMENT_START.equals(eventType)) {
                    lastOpenElementName = null;
                    lastClosedElementName = event.getContent()[0].toLowerCase();
                } else {
                    lastOpenElementName = event.getContent()[0].toLowerCase();
                    lastClosedElementName = null;
                }
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_CLOSE_ELEMENT_START.equals(eventType)) {

                /*
                 * Whitespace between block elements closing tags and block containers closing tags
                 * (e.g. </p></div>, </li></ul>) should not be considered.
                 */
                
                final String elementName = event.getContent()[0].toLowerCase();
                
                if (lastClosedElementName != null && 
                    BLOCK_ELEMENTS.contains(lastClosedElementName) &&
                    BLOCK_CONTAINER_ELEMENTS.contains(elementName)) {
                    
                    if (lastIsWhiteSpace) {
                        newTrace.remove(newTrace.size() - 1);
                        lastIsWhiteSpace = false;
                    }

                } 
                
                lastOpenElementName = null;
                lastClosedElementName = event.getContent()[0].toLowerCase();
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
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
                lastIsWhiteSpace = false;
                
            } else if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_OPEN_ELEMENT_END.equals(eventType)) {
                
                // If we are closing an opening tag, we should not initialize the "lastOpenElementName" field
                // that we just set at the OPEN_ELEMENT_START
                lastClosedElementName = null;
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            } else {
                
                lastOpenElementName = null;
                lastClosedElementName = null;
                newTrace.add(event);
                lastIsWhiteSpace = false;
                
            }
            
        }
        
        return newTrace;
        
    }
    
    
    
    
    
    private static TraceEvent compressWhitespaceIfText(final TraceEvent event) {
        
        final String eventType = event.getType();
        if (!TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT.equals(eventType)) {
            return event;
        }
        
        final String text = event.getContent()[0]; 
                
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
        
        return new TraceEvent(event.getLine(), event.getCol(), event.getType(), strBuilder.toString());
        
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
    
    
    
    
    private static int[] computeErrorMessageLength(final List<TraceEvent> trace, final int position, final String result) {
        
        TraceEvent eventItem = trace.get(position);
        
        if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_TEXT.equals(eventItem.getType())) {
            
            final Object[] contentArray = eventItem.getContent();
            if (contentArray == null || contentArray.length == 0) {
                return new int[] {20, 80};
            }
            
            // We first at content item number 1, because during trace normalization
            // we might have added there the un-whitespace-stripped original text, which is
            // the correct one to be used for computing this message length.
            final Object contentObj = eventItem.getContent()[0];
            if (contentObj == null || !(contentObj instanceof String)) {
                return new int[] {20, 80};
            }
            
            final String content = (String) contentObj;
            return new int[] {20, content.length() + 20};
            
        }

        if (TracingDetailedHtmlAttoHandler.TRACE_TYPE_ATTRIBUTE.equals(eventItem.getType()) ||
            TracingDetailedHtmlAttoHandler.TRACE_TYPE_OPEN_ELEMENT_START.equals(eventItem.getType()) ||
            TracingDetailedHtmlAttoHandler.TRACE_TYPE_STANDALONE_ELEMENT_START.equals(eventItem.getType())) {
            
            // We will try to output all text from the start of the container tag to the last
            // attribute in the same tag.
            
            final int attributeLine = eventItem.getLine();
            final int attributeCol = eventItem.getCol();

            int i = position;
            eventItem = trace.get(i);
            while (!TracingDetailedHtmlAttoHandler.TRACE_TYPE_OPEN_ELEMENT_START.equals(eventItem.getType()) &&
                    !TracingDetailedHtmlAttoHandler.TRACE_TYPE_STANDALONE_ELEMENT_START.equals(eventItem.getType()) &&
                    i > 0) {
                eventItem = trace.get(--i);
            }
            
            final int elementLine = eventItem.getLine();
            final int elementCol = eventItem.getCol();
            final int beforeDistance = 
                    computeDistance(result, elementLine, elementCol, attributeLine, attributeCol);
            
            int afterDistance = 0;
            int lastAttributeLen = 0;
            eventItem = trace.get(++i);
            
            while (TracingDetailedHtmlAttoHandler.TRACE_TYPE_ATTRIBUTE.equals(eventItem.getType()) && i < trace.size()) {
                final int distance = 
                        computeDistance(result, attributeLine, attributeCol, eventItem.getLine(), eventItem.getCol());
                if (distance > afterDistance) {
                    afterDistance = distance;
                    lastAttributeLen =
                            eventItem.getContent()[0].length() + 
                            (eventItem.getContent()[1] != null? eventItem.getContent()[1].length() : 0) + 
                            (eventItem.getContent()[2] != null? eventItem.getContent()[2].length() : 0); 
                }
                eventItem = trace.get(++i);
            }
            

            return new int[] { (beforeDistance + 20), (afterDistance + lastAttributeLen + 80) };
            
        }
        
        return new int[] {20, 80};
        
    }
    
    
    
    
    private static int computeDistance(
            final String text, 
            final int lineFrom, final int colFrom,
            final int lineTo, final int colTo) {
        
        final int textLen = text.length();
        
        int startPos = 0;
        int endPos = 0;
        
        {
            int line = 1;
            int col = 1;
            for (int i = 0; i < textLen; i++) {
                if (line == lineFrom && col == colFrom) {
                    startPos = i;
                    break;
                }
                final char c = text.charAt(i);
                if (c == '\n') {
                    line++;
                    col = 1;
                    continue;
                }
                col++;
            }
        }

        {
            int line = 1;
            int col = 1;
            for (int i = 0; i < textLen; i++) {
                if (line == lineTo && col == colTo) {
                    endPos = i;
                    break;
                }
                final char c = text.charAt(i);
                if (c == '\n') {
                    line++;
                    col = 1;
                    continue;
                }
                col++;
            }
        }

        return (endPos - startPos);
        
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
